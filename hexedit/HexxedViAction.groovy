package hexedit

import java.awt.event.ActionEvent
import javax.swing.AbstractAction



class HexxedViAction extends AbstractAction {

	def windowHexxed
	def typeAction

	HexxedViAction(def wHexxed, def type)
	{
		windowHexxed = wHexxed
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
		}
	}
}