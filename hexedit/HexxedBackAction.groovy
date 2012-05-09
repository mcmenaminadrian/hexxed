package hexedit

import java.awt.event.ActionEvent
import javax.swing.AbstractAction


class HexxedBackAction extends AbstractAction {

	def windowHexxed
	
	HexxedBackAction(def wHexxed)
	{
		windowHexxed = wHexxed
	}
	
	void actionPerformed(ActionEvent e)
	{
		windowHexxed.backward()
	}
}