package hexedit

import java.awt.event.ActionEvent
import javax.swing.AbstractAction



class HexxedBackAction extends AbstractAction {

	def windowHexxed
	def typeAction

	HexxedBackAction(def wHexxed, def type)
	{
		windowHexxed = wHexxed
		typeAction = type
	}

	void actionPerformed(ActionEvent e)
	{
		switch (typeAction) {
			case HexxedConstants.BACKWARDS:
				windowHexxed.backward()
				break
			case HexxedConstants.FORWARDS:
				windowHexxed.forward()
				break
		}
	}
}