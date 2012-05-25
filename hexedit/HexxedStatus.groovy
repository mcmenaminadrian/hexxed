package hexedit

import javax.imageio.IIOException
import javax.swing.event.TableModelEvent
import java.nio.ByteBuffer
import java.awt.event.*
import java.awt.event.InputEvent
import javax.swing.*
import javax.swing.table.*
import java.awt.*

class HexxedStatus {
	
	private HexxedStatus() {}
	
	private static final currentStatus = new HexxedStatus()
	
	static getCurrentStatus() {return currentStatus}
	
	def littleEndian
	def bigEndian
	def bitWidth
	def useBlocks
	def blockSize
	def windowEdit
	def fileOpen = false
	def fileName
	def offset
	def fileChan
	def editMode
	def undoList = []
	def redoList = []
	def usingTempFile = false
	def tempFile
	def holdingFileChan
	def hexxedFile
	def actionListen
	def commandSuccess
	
	def subscribersLittleEndian = []
	def subscribersBigEndian = []
	def subscribersBitWidth = []
	def subscribersUseBlocks = []
	def subscribersBlockSize = []
	def subscribersFileOpen = []
	def subscribersOffset = []
	def subscribersFileName = []
	def subscribersEditMode = []
	def subscribersFileObj = []
	
	def commandMap = ["ESCAPE":"VI_MODE", 'G':"END",
		"K":"UP_LINE", "J":"DOWN_LINE", "1":"ONE", "2":"TWO", "3":"THREE",
		"4":"FOUR", "5":"FIVE", "6":"SIX", "7":"SEVEN", "8":"EIGHT",
		"9":"NINE", "0":"ZERO", "OPEN_BRACKET":"BACK_SCREEN",
		"CLOSE_BRACKET":"NEXT_SCREEN", "ENTER":"DOWN_LINE", "I":"EDIT",
		"U":"UNDO", "X":"DELETE"]
	
	def shiftCommandMap = ["VK_OPEN_BRACKET":"BACK_BLOCK",
		"VK_CLOSE_BRACKET":"NEXT_BLOCK", "VK_SEMICOLON":"COMMAND_MODE"]

	def ctrlCommandMap = ["VK_U":"HALFSCREEN_UP", "VK_D":"HALFSCREEN_DOWN",
		"VK_B":"BACK_SCREEN", "VK_F":"NEXT_SCREEN", "VK_R":"REDO"]
	
	def colonCommandMap = ["W":"WRITE", "Q":"QUIT", "ENTER":"DONE",
		"U":"UNDO"]

	void badCommandString(def string)
	{
		windowEdit.commandTextStatus.append(
			"Bad or not understood command: $string\n")
	}
	
	void removeOldBindings()
	{
		commandMap.each() { k, v ->
			windowEdit.tableHex.getInputMap().put(KeyStroke.getKeyStroke(k),
				null)
		}
		shiftCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.SHIFT_DOWN_MASK)
			windowEdit.tableHex.getInputMap().put(key, null)
		}
		ctrlCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.CTRL_DOWN_MASK)
			windowEdit.tableHex.getInputMap().put(key, null)
		}
	}
	
	void setupBindings()
	{
		def key
		
		commandMap.each() { k, v ->
			windowEdit.tableHex.getInputMap().put(
				KeyStroke.getKeyStroke(k), "$v")
			windowEdit.tableHex.getActionMap().put("$v",
				new HexxedViAction(windowEdit, this, HexxedConstants."$v"))
		}
		
		shiftCommandMap.each { k, v ->
			key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.SHIFT_DOWN_MASK)
			windowEdit.tableHex.getInputMap().put(key, "$v")
			windowEdit.tableHex.getActionMap().put("$v",
				new HexxedViAction(windowEdit, this, HexxedConstants."$v"))
		}
		
		ctrlCommandMap.each { k, v ->
			key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.CTRL_DOWN_MASK)
			windowEdit.tableHex.getInputMap().put(key, "$v")
			windowEdit.tableHex.getActionMap().put("$v",
				new HexxedViAction(windowEdit, this, HexxedConstants."$v"))
		}
	}
	
	void wipeBindings()
	{
		def key
		
		commandMap.each() { k, v ->
			windowEdit.tableHex.getInputMap().put(
				KeyStroke.getKeyStroke(k), null)
			windowEdit.tableHex.getActionMap().put("$v", null)
		}
		
		shiftCommandMap.each { k, v ->
			key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.SHIFT_DOWN_MASK)
			windowEdit.tableHex.getInputMap().put(key, null)
			windowEdit.tableHex.getActionMap().put("$v", null)
		}
		
		ctrlCommandMap.each { k, v ->
			key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.CTRL_DOWN_MASK)
			windowEdit.tableHex.getInputMap().put(key, null)
			windowEdit.tableHex.getActionMap().put("$v", null)
		}
	}
	
	void addOldBindings()
	{
		commandMap.each() { k, v ->
			windowEdit.tableHex.getInputMap().put(KeyStroke.getKeyStroke(k),
				"$v")
		}
		shiftCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.SHIFT_DOWN_MASK)
			windowEdit.tableHex.getInputMap().put(key, "$v")
		}
		ctrlCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.CTRL_DOWN_MASK)
			windowEdit.tableHex.getInputMap().put(key, "$v")
		}
	}
	
	void returnToViModeFromEdit()
	{
		setEditMode(false)
		//kill edit mode binding
		windowEdit.tableHex.getInputMap().put(
			KeyStroke.getKeyStroke("ESCAPE"), null)
		windowEdit.tableHex.getActionMap().put("RETURN_VI_MODE", null)
		addOldBindings()
	}
	
	void returnToViModeFromCommand()
	{
		colonCommandMap.each{k, v ->
			windowEdit.tableHex.getInputMap().put(
				KeyStroke.getKeyStroke("$k"), null)
			windowEdit.tableHex.getActionMap().put("$v", null)
		}
		setupBindings()
	}
	
	void setupEditMode()
	{
		removeOldBindings()
		//add ESCAPE (vi mode) binding
		windowEdit.tableHex.getInputMap().put(
			KeyStroke.getKeyStroke("ESCAPE"), "RETURN_VI_MODE")
		windowEdit.tableHex.getActionMap().put("RETURN_VI_MODE",
			new HexxedViAction(windowEdit, this,
				HexxedConstants.RETURN_VI_MODE))
		setEditMode(true)
	}
	
	void setupCommandMode()
	{
		wipeBindings()
		colonCommandMap.each { k, v ->
			windowEdit.tableHex.getInputMap().put(
				KeyStroke.getKeyStroke("$k"), "$v")
			windowEdit.tableHex.getActionMap().put("$v",
				new HexxedViAction(windowEdit, this,
					HexxedConstants."$v"))
		}
	}
	
	void processEnter()
	{
		def actionString = windowEdit.commandTextLine.getText()
		if (actionString.size() == 0) {
			cleanCommandLine()
			return
		}
		
		if (actionString[0] != ':') {
			badCommandString(actionString)
			cleanCommandLine()
			return
		}
		
		if (actionString[1] == 'w') {
			actionString = actionString.minus(":w")
			if (actionString.isAllWhitespace() || actionString.size() == 0)
				writeFile(null)
			else
				writeFile(actionString)
			return
		} else if (actionString[1] == 'q') {
			actionString = actionString.minus(":q")
			quitFile(actionString)
			return
		}
		
		badCommandString(actionString)
		cleanCommandLine()
	}
	
	void cleanCommandLine()
	{
		windowEdit.commandTextLine.removeActionListener(actionListen)
		windowEdit.commandTextLine.setEditable(false)
		windowEdit.commandTextLine.setText("")
		returnToViModeFromCommand()
	}
	
	void setupWriteFile(def actObject)
	{
		windowEdit.commandTextLine.setText(":w $fileName")
		actionListen = new HexxedWriteFileAdapter(this)
		windowEdit.commandTextLine.addActionListener(actionListen)
		windowEdit.commandTextLine.setEditable(true)
	}
	
	void setupQuit(def actObject)
	{
		windowEdit.commandTextLine.setText(":q")
		actionListen = new HexxedWriteFileAdapter(this)
		windowEdit.commandTextLine.addActionListener(actionListen)
		windowEdit.commandTextLine.setEditable(true)
	}
	
	void quitFile(def commandString)
	{
		//TODO: handle non null strings
		if (!fileOpen) {
			cleanCommandLine()
			return
		}
		fileChan.close()
		setFileName(null)
		setFileOpen(false)
		cleanCommandLine()
	}
		
	void writeFile(def filePath)
	{
		if (!usingTempFile)
			return //nothing to save
		def backupFile
		def backChannel
		if (filePath)
			fileName = filePath.trim()
		//backup file
		try {
			backupFile = File.createTempFile("~~bCKUP", null)
			def backStream = new RandomAccessFile(backupFile, "rw")
			backChannel = backStream.getChannel()
			fileChan.transferTo(0, fileChan.size(), backChannel)
			backChannel.close()
		}
		catch (e) {
			windowEdit.commandTextStatus.append("Exception $e\n")
			windowEdit.commandTextStatus.append(
				"Could not backup edit")
			cleanCommandLine()
			return
		}
		
		try {
			hexxedFile = new RandomAccessFile(fileName, "rw")
			hexxedFile.getChannel().truncate(0)
			fileChan.transferTo(0, fileChan.size(), hexxedFile.getChannel())
			fileChan.close()
			fileChan = hexxedFile.getChannel()
			backupFile.delete()
		}
		catch (e)
		{
			windowEdit.commandTextStatus.append("Exception $e\n")
			windowEdit.commandTextStatus.append(
				"Could not save edit: backup at ${backupFile.getPath()}")
			cleanCommandLine()
			return
		}

		usingTempFile = false
		cleanCommandLine()
	}
	
	boolean setValueAt(def value, def row, def col)
	{
		def commandSet = new HexxedSetValueCommand(row, col, value, this)
		undoList << commandSet
		commandSet.execute()
		return commandSuccess
	}
	
	def deleteHex(def count)
	{
		def commandDelete = new HexxedDeleteCommand(count, this)
		undoList << commandDelete
		commandDelete.execute()
	}
	
	void resetTableToMatchCommand(def commandObj)
	{
		def oldBitWidth = bitWidth
		def oldLE = littleEndian
		def oldBE = bigEndian
		setBitWidth(commandObj.bitWidth)
		setLittleEndian(commandObj.le)
		setBigEndian(commandObj.be)
		commandObj.bitWidth = oldBitWidth
		commandObj.le = oldLE
		commandObj.be = oldBE
	}
	
	def createTempFile()
	{
		if (usingTempFile == false) {
			//store a copy of the pristine file
			def tempFileObj = File.createTempFile(fileChan.toString(), null)
			def outStream = new RandomAccessFile(tempFileObj, "rw")
			holdingFileChan = outStream.getChannel()
			fileChan.transferTo(0, fileChan.size(), holdingFileChan)
			tempFile = tempFileObj.getPath()
			//close the original so we edit only the copy
			fileChan.close()
			fileChan = holdingFileChan
			windowEdit.commandTextStatus.append(
				"Temporary file written to $tempFile\n")
			usingTempFile = true
		}
	}
	
	void executeDelete(def commandObj)
	{
		def reverseRequired = false
		def tableModel = windowEdit.tableHex.getModel()
		def charTableModel = windowEdit.tableChar.getModel()
		if (commandObj.bitWidth != bitWidth || commandObj.le != littleEndian) {
			resetTableToMatchCommand(commandObj)
			tableModel.fireTableChanged(new TableModelEvent(tableModel))
			charTableModel.fireTableChanged(
				new TableModelEvent(charTableModel))
			reverseRequired = true
		}
		
		def count = commandObj.count * (bitWidth / 8) as Integer
		offset = commandObj.position
		tableModel.fireTableChanged(new TableModelEvent(tableModel))
		charTableModel.fireTableChanged(new TableModelEvent(charTableModel))
		def oldSize = fileChan.size()
		createTempFile()
		
		if (commandObj.oldValues.size() > 0)
		{	//undo
			def buf = ByteBuffer.allocate((oldSize - commandObj.position)
				 as Integer)
			fileChan.read(buf, commandObj.position as Integer)
			commandObj.oldValues.eachWithIndex(){v, i->
				def row = i / (16 / (bitWidth / 8) as Integer) as Integer
				def col = i % (16 / (bitWidth / 8) as Integer) as Integer
				col++ //not try to over-write address
				def valueCommand = new HexxedSetValueCommand(row, col, v, this)
				executeSetValue(valueCommand)
			}
			buf.position(0)
			fileChan.write(buf, (commandObj.position + count) as Integer)
			commandObj.oldValues.clear() // so we look like a redo now
		} else {
			//delete
			def buf
			def allocSize = oldSize - (commandObj.position + count)
			if (allocSize > 0) {
				buf = ByteBuffer.allocate(allocSize as Integer)
				fileChan.read(buf, (commandObj.position + count) as Integer)
			}
			for (i in 0..commandObj.count - 1) {		
				def row = i / ((16 / (bitWidth / 8)) as Integer) as Integer
				def col = i % ((16 / (bitWidth / 8)) as Integer) as Integer
				col++ //col 0 is address
				commandObj.oldValues << valueAt(row, col)
			}
			if (allocSize > 0) {
				buf.position(0)
				fileChan.write(buf, commandObj.position as Integer)
				fileChan.truncate(oldSize - count as Integer)
			} else
				fileChan.truncate(0)
		}
		tableModel.fireTableChanged(new TableModelEvent(tableModel))
		charTableModel.fireTableChanged(new TableModelEvent(charTableModel))
	}
	
	void executeSetValue(def commandObj)
	{
		def reverseRequired = false
		def value = commandObj.newValue
		def row = commandObj.row
		def col = commandObj.col
		def tableModel = windowEdit.tableHex.getModel()
		def charTableModel = windowEdit.tableChar.getModel()
		if (commandObj.bitWidth != bitWidth || commandObj.le != littleEndian) {
			resetTableToMatchCommand(commandObj)
			tableModel.fireTableChanged(new TableModelEvent(tableModel))
			charTableModel.fireTableChanged(
				new TableModelEvent(charTableModel))
			reverseRequired = true
		}
		
		if (offset != commandObj.position)
			setOffset(commandObj.position)
		
		//is value a valid hex number?
		def nibbles
		if (bitWidth == 8)
			nibbles = 2
		else if (bitWidth == 16)
			nibbles = 4
		else if (bitWidth == 32)
			nibbles = 8
		else
			nibbles = 16
		
		boolean isHex = value.matches("[0-9A-Fa-f]{$nibbles}")
		if (!isHex) {
			windowEdit.commandTextStatus.append(
				"Failed: Edits must be hex format and match bit width\n")
			if (undoList.size())
				undoList.pop()
			commandSuccess = false
			if (reverseRequired)
				resetTableToMatchCommand(commandObj)
			return
		}
		createTempFile()
		
		def bytes = ByteBuffer.allocate((nibbles / 2) as Integer)
		def j = 0
		def listVal = value.toList()
		if (littleEndian) {
			def i = listVal.size() - 1
			while (i >= 0) {
				byte le = Integer.parseInt(listVal[i], 16) +
					16 * Integer.parseInt(listVal[i - 1], 16)
				i -= 2
				bytes.put(j++, le)
			}
		} else {
			def i = 0
			while (i <= listVal.size() - 1) {
				byte be = Integer.parseInt(listVal[i + 1], 16) +
					16 * Integer.parseInt(listVal[i], 16)
				i += 2
				bytes.put(j++, be)
			}
		}
		def address = offset + row * 16 + (col - 1) * (bitWidth / 8) as Integer
		fileChan.write(bytes, address)
		if (reverseRequired)
			resetTableToMatchCommand(commandObj)
		tableModel.fireTableChanged(new TableModelEvent(tableModel))
		charTableModel.fireTableChanged(new TableModelEvent(charTableModel))
		commandSuccess = true
		return
	}
	
	def valueAt(def row, def col)
	{
		if (!fileChan)
			return null
		def position = offset + row * 16
		if (col == 0) {
			//return address
			if (useBlocks) {
				def decBlock = (position / blockSize) as Integer
				def blockCnt = String.format("%08X", decBlock)
				def offCnt = String.format("%04X", position % blockSize)
				return "$blockCnt:$offCnt"
			} else
				return String.format("%08X", position)
		}
		
		def byteCount = 1
		if (bitWidth == 8)
			position += col - 1
		else if (bitWidth == 16) {
			byteCount = 2 
			position += (col - 1) * 2
		}
		else if (bitWidth == 32) {
			byteCount = 4
			position += (col - 1) * 4
		}
		else {
			byteCount = 8 
			position += (col - 1) * 8
		}
		
		def bytes = ByteBuffer.allocate(byteCount)
		def bytesRet = fileChan.read(bytes, position as Integer)
		if (bytesRet == 0)
			throw new IOException("EOF")
		
		def littleEndianNumb = []
		def outStr = ""
		def i = 0
		BigInteger numb = 0

		def displayHex = {
			if (bitWidth == 8 || bigEndian) {
				Long x = 0xFF & it
				numb = x + numb * 256
				if (++i == byteCount)
					outStr = String.format("%0${byteCount * 2}X", numb)
			} else {
				Long x = 0xFF & it
				littleEndianNumb << x
				if (++i == byteCount) {
					def littleEndianList = littleEndianNumb.reverse()
					littleEndianList.each() {
						numb = numb * 256 + it
					}
					outStr = String.format("%0${byteCount * 2}X", numb)
				}
			}
		}
		
		bytes.array().eachByte(displayHex)
		return outStr
	}

	def stringAt(def row)
	{
		if (!fileChan)
			return null
		
		def position = offset + row * 16	
		if (position > fileChan.size())
			throw new IOException("EOF")
		def byteCnt = (bitWidth == 8) ? 1 : 2
		def lineOut = ""
		char outChar = '\0'
		def i = 0
		
		def displayCharLine = {
			if (byteCnt == 1) {
				outChar = it
				if (outChar < 32 || outChar == 127)
					outChar = ' '
				lineOut = lineOut + outChar
			} else if (littleEndian) {
				if (i++ == 0) {
					outChar = it
				} else {
					outChar += it * 256
					if (outChar < 32 || outChar == 127)
						outChar = ' '
					lineOut =lineOut + outChar
					i = 0
				}		
			} else {
				if (i++ == 0) {
					outChar = it * 256
				} else {
					outChar += it
					if (outChar < 32 || outChar == 127)
						outChar = ' '
					lineOut = lineOut + outChar
					i = 0
				}
			}
		}
		
		def bytes = ByteBuffer.allocate(16)
		def bytesRet = fileChan.read(bytes, position as Integer)
		if (bytesRet == 0)
			return
		
		bytes.array().eachByte(displayCharLine)
		return lineOut
	}
	
	void setHexxedFile(def file)
	{
		hexxedFile = file
		notifyFileObj(subscribersFileObj)
	}
	
	void setEditMode(def mode)
	{
		editMode = mode
		notifyEditMode(subscribersEditMode)
	}
	
	void setOffset(def off)
	{
		if (fileChan && off >= fileChan.size())
			off = ((fileChan.size() - 1) & 0xFFFFFFFFFFFFFFF0) as Integer
		if (off < 0)
			off = 0
		offset = off
		notifyOffset(subscribersOffset)
	}
	
	void setFileOpen(def fo)
	{
		wipeBindings()
		fileOpen = fo
		usingTempFile = false
		notifyFO(subscribersFileOpen)
		setupBindings()
	}
	
	void setLittleEndian(def le)
	{
		littleEndian = le
		notifyLE(subscribersLittleEndian)
	}
	
	void setBigEndian(def be)
	{
		bigEndian = be
		notifyBE(subscribersBigEndian)
	}
	
	void setBitWidth(def bw)
	{
		bitWidth = bw
		notifyBW(subscribersBitWidth)
	}
	
	void setUseBlocks(def ub)
	{
		useBlocks = ub
		notifyUB(subscribersUseBlocks)
	}
	
	void setBlockSize(def bs)
	{
		blockSize = bs
		if (useBlocks)
			notifyBS(subscribersBlockSize)
	}
	
	void setWindowEdit(def we)
	{
		windowEdit = we
	}
	
	void changeFileName()
	{
		notifyFN(subscribersFileName)
	}
	
	void notifyFileObj(def listSubs)
	{
		listSubs.each{it.updateFileObj(hexxedFile)}
	}
	
	void notifyEditMode(def listSubs)
	{
		listSubs.each {it.updateEM(editMode)}
	}
	
	void notifyFN(def listSubs)
	{
		listSubs.each{it.updateFN()}
	}
	
	void notifyBE(def listSubs)
	{
		listSubs.each{it.updateBE(bigEndian)}
	}
	
	void notifyLE(def listSubs)
	{
		listSubs.each{it.updateLE(littleEndian)}
	}
		
	void notifyBW(def listSubs)
	{
		listSubs.each{it.updateBW(bitWidth)}
	}
	
	void notifyUB(def listSubs)
	{
		listSubs.each{it.updateUB(useBlocks)}
	}
	
	void notifyBS(def listSubs)
	{
		listSubs.each{it.updateBS(blockSize)}
	}
	
	void notifyFO(def listSubs)
	{
		listSubs.each{it.updateFO(fileOpen)}
	}
	
	void notifyOffset(def listSubs)
	{
		listSubs.each{it.updateOff(offset)}
	}
	
	void subscribeEditMode(def subscriber)
	{
		subscribersEditMode -= subscriber
		subscribersEditMode << subscriber
	}
	
	void unsubscribeEditMode(def subscriber)
	{
		subscribersEditMode -= subscriber
	}
	
	void subscribeFileName(def subscriber)
	{
		subscribersFileName -= subscriber
		subscribersFileName << subscriber
	}
	
	void unsubscribeFileName(def subscriber)
	{
		subscribersFileName -= subscriber
	}
	
	void subscribeBigEndian(def subscriber)
	{
		subscribersBigEndian -= subscriber
		subscribersBigEndian << subscriber
	}
	
	void unsubscribeBigEndian(def subscriber)
	{
		subscribersBigEndian -= subscriber
	}
	
	void subscribeLittleEndian(def subscriber)
	{
		subscribersLittleEndian -= subscriber
		subscribersLittleEndian << subscriber
	}
	
	void unsubscribeLittleEndian(def subscriber)
	{
		subscribersLittleEndian -= subscriber
	}
	
	void subscribeBitWidth(def subscriber)
	{
		subscribersBitWidth -= subscriber
		subscribersBitWidth << subscriber
	}
	
	void unsubscribeBitWidth(def subscriber)
	{
		subscribersBitWidth -= subscriber
	}
	
	void subscribeUseBlocks(def subscriber)
	{
		subscribersUseBlocks -= subscriber
		subscribersUseBlocks << subscriber
	}
	
	void unsubscribeUseBlocks(def subscriber)
	{
		subscribersUseBlocks -= subscriber
	}

	void subscribeBlockSize(def subscriber)
	{
		subscribersBlockSize -= subscriber
		subscribersBlockSize << subscriber
	}
	
	void unsubscribeBlockSize(def subscriber)
	{
		subscribersBlockSize -= subscriber
	}
	
	void subscribeFileOpen(def subscriber)
	{
		subscribersFileOpen -= subscriber
		subscribersFileOpen << subscriber
	}
	
	void unsubscribeFileOpen(def subscriber)
	{
		subscribersFileOpen -= subscriber
	}
	
	void subscribeOffset(def subscriber)
	{
		subscribersOffset -= subscriber
		subscribersOffset << subscriber
	}
	
	void unsubscribeOffset(def subscriber)
	{
		subscribersOffset -= subscriber
	}
	
	void subscribeFileObj(def subscriber)
	{
		subscribersFileObj -=subscriber
		subscribersFilObj << subscriber
	}
	
	void unsubscribeFileObj(def subscriber)
	{
		susbcribersFileObj -= subscriber
	}
	
}
