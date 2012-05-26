package hexedit

import java.awt.event.ActionEvent
import java.awt.event.ActionListener


class HexxedWriteFileAdapter implements ActionListener {

	def statusHolder
	
	HexxedWriteFileAdapter(def statusObj)
	{
		super()
		statusHolder = statusObj
	}
	
	void actionPerformed(ActionEvent e)
	{
		def actionString = e.getActionCommand()
		statusHolder.processActionString(actionString)
	}
}
