package hexedit

import javax.imageio.IIOException
import java.nio.ByteBuffer

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
	def usingTempFile = false
	def tempFile
	
	def subscribersLittleEndian = []
	def subscribersBigEndian = []
	def subscribersBitWidth = []
	def subscribersUseBlocks = []
	def subscribersBlockSize = []
	def subscribersFileOpen = []
	def subscribersOffset = []
	def subscribersFileName = []
	def subscribersEditMode = []
	
	
	def storeUndo(def val, def row, def col)
	{
		def address = offset + row * 16 + (col - 1)
		def undoRecord = new UndoRecord(bitWidth, littleEndian, val, address)
		undoList << undoRecord
	}
	
	boolean setValueAt(def value, def row, def col)
	{
		//is value a valid hex number?
		def nibbles = 2
		if (bitWidth == 16)
			nibbles = 4
		else if (bitWidth == 32)
			nibbles = 8
		else if (bitWidth == 64)
			nibbles = 16
		
		boolean isHex = value.matches("[0-9A-Fa-f]{$nibbles}")
		if (!isHex) {
			println "Edits must be Hex and match bit width"
			undoList.pop()
			return false
		}
		//create a temporary file if we have not done so already
		//this stores a copy of the unchanged file if we don't save changes
		if (usingTempFile == false) {
			 def tempFileObj = File.createTempFile(fileChan.toString(), null)
			 def outStream = new FileOutputStream(tempFileObj)
			 def tempFileChan = outStream.getChannel()
			 fileChan.transferTo(0, fileChan.size(), tempFileChan)
			 tempFile = tempFileObj.getPath()
			 outStream.close()
			 println "Temporary file written to $tempFile"
			 usingTempFile = true
		}
		
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
		def address = offset + row * 16 + (col - 1)
		fileChan.write(bytes, address)
		return true
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
		fileOpen = fo
		usingTempFile = false
		notifyFO(subscribersFileOpen)
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
	
}
