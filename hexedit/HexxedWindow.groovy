package hexedit

import groovy.swing.SwingBuilder
import javax.swing.*

class HexxedWindow {

	def swingWindow
	def swingBuilder
	def statusHolder
	
	HexxedWindow(def x, def y, def controller)
	{
		statusHolder = controller
		//create basic window
		//then create menus
		swingBuilder = new SwingBuilder()
		swingWindow = swingBuilder.frame(title: "HEXXED", size: [x, y],
			show: true) {
			//menu
			menuBar() {
				menu(text: "File", mnemonic: 'F'){
					menuItem(text: "Load", mnemonic: 'L',
						actionPerformed: { loadFile() })
					menuItem(text: "Quit", mnemonic: 'Q',
						actionPerformed: { dispose() })
				}
				menu(text: "Display", mnemonic: 'D'){
					menuItem(text: "Set width", mnemonic: 'w',
						actionPerformed: { setWidth() })
				}
				menu(text: "Navigate", mnemonic: 'N'){
					menuItem(text: "Forwards", mnemonic: 'F',
						actionPerformed: { forward() })
					menuItem(text: "Backwards", mnemonic: 'B',
						actionPerformed: { backward() })
					menuItem(text: "Next block", mnemonic: 'x',
						actionPerformed: { nextBlock() })
					menuItem(text: "Previous block", mnemonic: 'P',
						actionPerformed: { previousBlock() })
				}
				menu(text: "About", mnemonic: 'A'){
					menuItem(text: "Help", mnemonic: 'H',
						actionPerformed: { helpMenu()})
					menuItem(text: "Licence", mnemonic: 'L',
						actionPerformed: { displayGPL() })
				}
			}
		}
	}
	
	
	def setWidth()
	{}
	
	def loadFile()
	{
		def loadDialog = swingBuilder.fileChooser(
			dialogTitle: "Choose a file to open"
		)
		loadDialog.showOpenDialog()
		//fileHandler.setNewFile(loadDialog.getSelectedFile())
		}
	
	def forward()
	{}
	
	def backward()
	{}
	
	def nextBlock()
	{}
	
	def previousBlock()
	{}
	
	def helpMenu()
	{}
	
	def displayGPL()
	{}
}
