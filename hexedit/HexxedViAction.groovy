package hexedit

import java.awt.event.ActionEvent
import javax.swing.AbstractAction
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
	
	void clearTextArea()
	{
		windowHexxed.commandTextLine.setText(null)
	}
	
	
	void resetCount()
	{
		count = 0
		counting = false
		clearTextArea()
	}
	
	void updateCount(def add)
	{
		counting = true
		count = count * 10 + add
		windowHexxed.commandTextLine.setText(count as String)
	}
	
	void returnToViMode()
	{
		statusHolder.setEditMode(false)
		//kill edit mode binding
		windowHexxed.tableHex.getInputMap().put(
			KeyStroke.getKeyStroke("ESCAPE"), null)
		windowHexxed.tableHex.getActionMap().put("RETURN_VI_MODE", null)
		//add back old bindings
		windowHexxed.commandMap.each() { k, v ->
			windowHexxed.tableHex.getInputMap().put(KeyStroke.getKeyStroke(k),
				"$v")
		}
		windowHexxed.shiftCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k", Event.SHIFT_MASK)
			windowHexxed.tableHex.getInputMap().put(key, "$v")
		}
		windowHexxed.ctrlCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k", Event.CTRL_MASK)
			windowHexxed.tableHex.getInputMap().put(key, "$v")
		}
	}
	
	void setupEditMode()
	{
		resetCount()
		//remove old key bindings
		windowHexxed.commandMap.each() { k, v ->
			windowHexxed.tableHex.getInputMap().put(KeyStroke.getKeyStroke(k),
				null)
		}
		windowHexxed.shiftCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k", Event.SHIFT_MASK)
			windowHexxed.tableHex.getInputMap().put(key, null)
		}
		windowHexxed.ctrlCommandMap.each() { k, v ->
			def key = KeyStroke.getKeyStroke(KeyEvent."$k", Event.CTRL_MASK)
			windowHexxed.tableHex.getInputMap().put(key, null)
		}
		//add ESCAPE (vi mode) binding
		windowHexxed.tableHex.getInputMap().put(
			KeyStroke.getKeyStroke("ESCAPE"), "RETURN_VI_MODE")
		windowHexxed.tableHex.getActionMap().put("RETURN_VI_MODE",
			new HexxedViAction(windowHexxed, statusHolder,
				HexxedConstants.RETURN_VI_MODE))
		statusHolder.setEditMode(true)
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
				returnToViMode()
				break
			default:
				resetCount()
		}
	}
}