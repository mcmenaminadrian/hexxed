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
			if (actionString.isAllWhitespace() || actionString.size() == 0) {
				statusHolder.writeFile(null)
			}
			statusHolder.writeFile(actionString)
			return
		}
		
		statusHolder.badCommandString(actionString)
		statusHolder.cleanCommandLine()
	
	}
}
