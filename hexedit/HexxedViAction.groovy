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
				counting = true
				count = count * 10 + 1
				break
			case HexxedConstants.TWO:
				counting = true
				count = count * 10 + 2
				break
			case HexxedConstants.THREE:
				counting = true
				count = count * 10 + 3
				break
			case HexxedConstants.FOUR:
				counting = true
				count = count * 10 + 4
				break
			case HexxedConstants.FIVE:
				counting = true
				count = count * 10 + 5
				break
			case HexxedConstants.SIX:
				counting = true
				count = count * 10 + 6
				break
			case HexxedConstants.SEVEN:
				counting = true
				count = count * 10 + 7
				break
			case HexxedConstants.EIGHT:
				counting = true
				count = count * 10 + 8
				break
			case HexxedConstants.NINE:
				counting = true
				count = count * 10 + 9
				break
			case HexxedConstants.ZERO:
				counting = true
				count = count * 10
				break;
			default:
				counting = false
				count = 0
		}
	}
}