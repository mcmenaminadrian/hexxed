package hexedit



import java.awt.event.ActionEvent
import javax.swing.*
import java.awt.*
import java.awt.event.*

class HexxedViAction extends AbstractAction {

	def windowHexxed
	def statusHolder
	def typeAction
	def static count = 0
	def static counting = false
	

	HexxedViAction(def wHexxed, def statusObj, def type)
	{
		windowHexxed = wHexxed
		statusHolder = statusObj
		typeAction = type
	}
	
	void resetCount()
	{
		count = 0
		counting = false
		windowHexxed.commandTextLine.setText(null)
	}
	
	void updateCount(def add)
	{
		counting = true
		count = count * 10 + add
		windowHexxed.commandTextLine.setText(count as String)
	}
	
	void removeOldBindings()
	{
		resetCount()
		//remove old key bindings
		windowHexxed.commandMap.each() { k, v ->
			windowHexxed.tableHex.getInputMap().put(KeyStroke.getKeyStroke(k),
				null)
		}
		windowHexxed.shiftCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.SHIFT_DOWN_MASK)
			windowHexxed.tableHex.getInputMap().put(key, null)
		}
		windowHexxed.ctrlCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.CTRL_DOWN_MASK)
			windowHexxed.tableHex.getInputMap().put(key, null)
		}
	}
	
	void addOldBindings()
	{
		windowHexxed.commandMap.each() { k, v ->
			windowHexxed.tableHex.getInputMap().put(KeyStroke.getKeyStroke(k),
				"$v")
		}
		windowHexxed.shiftCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.SHIFT_DOWN_MASK)
			windowHexxed.tableHex.getInputMap().put(key, "$v")
		}
		windowHexxed.ctrlCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k",
				KeyEvent.CTRL_DOWN_MASK)
			windowHexxed.tableHex.getInputMap().put(key, "$v")
		}
	}
	
	void returnToViModeFromEdit()
	{
		statusHolder.setEditMode(false)
		//kill edit mode binding
		windowHexxed.tableHex.getInputMap().put(
			KeyStroke.getKeyStroke("ESCAPE"), null)
		windowHexxed.tableHex.getActionMap().put("RETURN_VI_MODE", null)
		addOldBindings()
	}
	
	void returnToViModeFromCommand()
	{
		windowHexxed.colonCommandMap.each{k, v ->
			windowHexxed.tableHex.getInputMap().put(
				KeyStroke.getKeyStroke("$k"), null)
			windowHexxed.tableHex.getActionMap().put("$v", null)
		}
		addOldBindings()
	}
	
	void setupEditMode()
	{
		removeOldBindings()
		//add ESCAPE (vi mode) binding
		windowHexxed.tableHex.getInputMap().put(
			KeyStroke.getKeyStroke("ESCAPE"), "RETURN_VI_MODE")
		windowHexxed.tableHex.getActionMap().put("RETURN_VI_MODE",
			new HexxedViAction(windowHexxed, statusHolder,
				HexxedConstants.RETURN_VI_MODE))
		statusHolder.setEditMode(true)
	}
	
	void setupCommandMode()
	{
		removeOldBindings()
		windowHexxed.colonCommandMap.each { k, v ->
			windowHexxed.tableHex.getInputMap().put(
				KeyStroke.getKeyStroke("$k"), "$v")
			windowHexxed.tableHex.getActionMap().put("$v",
				new HexxedViAction(windowHexxed, statusHolder,
					HexxedConstants."$v"))
		}
	}
	
	void processEnter()
	{
		def actionString = windowHexxed.commandTextLine.getText()
		if (actionString.size() == 0) {
			statusHolder.cleanCommandLine()
			return
		}
		
		if (actionString[0] != ':') {
			statusHolder.badCommandString(actionString)
			statusHolder.cleanCommandLine()
			return
		}
		
		if (actionString[1] == 'w') {
			actionString = actionString.minus(":w")
			if (actionString.isAllWhitespace() || actionString.size() == 0)
				statusHolder.writeFile(null)
			else
				statusHolder.writeFile(actionString)
			return
		} else if (actionString[1] == 'q') {
			actionString = actionString.minus(":q")
			statusHolder.quitFile(actionString)
			return
		}
		
		statusHolder.badCommandString(actionString)
		statusHolder.cleanCommandLine()
	}

	void actionPerformed(ActionEvent e)
	{
		switch (typeAction) {
			case HexxedConstants.VI_MODE:
				// In vi mode already so just beep
				System.out.print("\0007")
				System.out.flush()
				resetCount()
				break
			case HexxedConstants.COMMAND_MODE:
				setupCommandMode()
				break
			case HexxedConstants.WRITE:
				statusHolder.setupWriteFile(this)
				break
			case HexxedConstants.QUIT:
				statusHolder.setupQuit(this)
				break
			case HexxedConstants.DONE:
				processEnter()
				break
			case HexxedConstants.DELETE:
				def x = 1
				if (counting)
					x = count
				statusHolder.deleteHex(x)
				resetCount()
				break
			case HexxedConstants.UNDO:
				def x = 1
				if (counting)
					x = count
				for (i in 1..x) {
					if (statusHolder.undoList.size == 0)
						break
					def command = statusHolder.undoList.pop()
					command.execute()
					statusHolder.redoList << command
				}
				resetCount()
				break
			case HexxedConstants.REDO:
				def x = 1
				if (counting)
					x = count
				for (i in 1..x) {
					if (statusHolder.redoList.size == 0)
						break
					def command = statusHolder.redoList.pop()
					command.execute()
					statusHolder.undoList << command
				}
				resetCount()
				break
			case HexxedConstants.END:
				if (counting)
					statusHolder.offset = 16 * (count - 1)
				else
					if (statusHolder.fileChan)
						statusHolder.offset =
							statusHolder.fileChan.size() & 0xFFFFFFFFFFFFFFF0
				resetCount()
				break
			case HexxedConstants.DOWN_LINE:
				if (counting)
					statusHolder.offset += 16 * count
				else
					statusHolder.offset += 16
				resetCount()
				break
			case HexxedConstants.UP_LINE:
				if (counting)
					statusHolder.offset -= 16 * count
				else
					statusHolder.offset -= 16
				resetCount()
				break
			case HexxedConstants.BACK_BLOCK:
			case HexxedConstants.NEXT_BLOCK:
				if (statusHolder.useBlocks) {
					def noblocks = 1
					def position = statusHolder.offset
					if (counting)
						noblocks = count
					if (typeAction == HexxedConstants.BACK_BLOCK)
						position -= statusHolder.blockSize * noblocks
					else
						position += statusHolder.blockSize * noblocks
					def multi = (position / statusHolder.blockSize) as Integer
					statusHolder.setOffset(multi * statusHolder.blockSize)
				}
				resetCount()
				break
			case HexxedConstants.BACK_SCREEN:
			case HexxedConstants.NEXT_SCREEN:
				def noscreens = 1
				def position = statusHolder.offset
				if (counting)
					noscreens = count
				if (typeAction == HexxedConstants.BACK_SCREEN)
					position -= HexxedConstants.ROWMAX * noscreens * 16
				else
					position += HexxedConstants.ROWMAX * noscreens * 16
				statusHolder.setOffset(position)
				resetCount()
				break
			case HexxedConstants.HALFSCREEN_UP:
			case HexxedConstants.HALFSCREEN_DOWN:
				def add = HexxedConstants.ROWMAX / 2 as Integer
				if (typeAction == HexxedConstants.HALFSCREEN_UP)
					add = add * -1
				statusHolder.offset += add * 16
				resetCount()
				break
			case HexxedConstants.ONE:
				updateCount(1)
				break
			case HexxedConstants.TWO:
				updateCount(2)
				break
			case HexxedConstants.THREE:
				updateCount(3)
				break
			case HexxedConstants.FOUR:
				updateCount(4)
				break
			case HexxedConstants.FIVE:
				updateCount(5)
				break
			case HexxedConstants.SIX:
				updateCount(6)
				break
			case HexxedConstants.SEVEN:
				updateCount(7)
				break
			case HexxedConstants.EIGHT:
				updateCount(8)
				break
			case HexxedConstants.NINE:
				updateCount(9)
				break
			case HexxedConstants.ZERO:
				updateCount(0)
				break
			case HexxedConstants.EDIT:
				setupEditMode()
				break
			case HexxedConstants.RETURN_VI_MODE:
				returnToViModeFromEdit()
				break
			default:
				resetCount()
		}
	}
}