package hexedit

import java.awt.event.ActionEvent
import javax.swing.AbstractAction



class HexxedViAction extends AbstractAction {

	def windowHexxed
	def statusHolder
	def typeAction

	HexxedViAction(def wHexxed, def statusObj, def type)
	{
		windowHexxed = wHexxed
		statusHolder = statusObj
		typeAction = type
	}

	void actionPerformed(ActionEvent e)
	{
		switch (typeAction) {
			case HexxedConstants.VI_MODE:
				windowHexxed.backward()
				break
			case HexxedConstants.COMMAND_MODE:
				windowHexxed.forward()
				break
			case HexxedConstants.DOWN_LINE:
				statusHolder.offset += 16
				break
			case HexxedConstants.UP_LINE:
				statusHolder.offset -= 16
				break
		}
	}
}