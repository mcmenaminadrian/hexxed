package hexedit

import java.awt.event.ActionEvent
import javax.swing.AbstractAction



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
	}
	
	void updateCount(def add)
	{
		counting = true
		count = count * 10 + add
	}

	void actionPerformed(ActionEvent e)
	{
		switch (typeAction) {
			case HexxedConstants.VI_MODE:
				resetCount()
				windowHexxed.backward()
				break
			case HexxedConstants.COMMAND_MODE:
				resetCount()
				windowHexxed.forward()
				break
			case HexxedConstants.END:
				if (counting)
					statusHolder.offset = 16 * count
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
				break;
			default:
				resetCount()
		}
	}
}