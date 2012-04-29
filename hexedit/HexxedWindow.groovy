package hexedit

import groovy.swing.SwingBuilder
import javax.swing.*
import java.awt.FlowLayout

class HexxedWindow {

	def swingWindow
	def swingBuilder
	def statusHolder
	def bitsButtons = []
	
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
						actionPerformed: { chooseWidth() })
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
		statusHolder.subscribeFileOpen(this)
	}
	
	void updateFO(status)
	{
		if (status)
			swingWindow.title = statusHolder.fileName
		else
			swingWindow.title = "HEXXED"
	}
	
	def chooseWidth()
	{
		def bitWidthDialog = swingBuilder.frame(
			title: "Set Bit Width",
			size: [450, 190],
		){
			panel(layout: new FlowLayout(FlowLayout.CENTER)){
				def butGrp = buttonGroup(id: "widthButtonGroup")
				bitsButtons = []
				bitsButtons << radioButton(text: "8 bits") 
				bitsButtons << radioButton(text: "16 bits")
				bitsButtons << radioButton(text: "32 bits")
				bitsButtons << radioButton(text: "64 bits")
				bitsButtons.each {
					butGrp.add(it)
					it.actionPerformed = { setBitWidth() }
				}
				if (statusHolder.bitWidth == 8)
					bitsButtons[0].selected = true
				else if (statusHolder.bitWidth == 16)
					bitsButtons[1].selected = true
				else if (statusHolder.bitWidth == 32)
					bitsButtons[2].selected = true
				else
					bitsButtons[3].selected = true
			}
		}

		bitWidthDialog.pack()
		bitWidthDialog.show()
		
	}
	
	void setBitWidth()
	{
		def i = 4
		bitsButtons.each {
			i = i * 2
			if (it.isSelected()) {
				statusHolder.setBitWidth(i)
			}
		}	
	}
	
	def loadFile()
	{
		def loadDialog = swingBuilder.fileChooser(
			dialogTitle: "Choose a file to open"
		)
		loadDialog.showOpenDialog()
		statusHolder.fileName = loadDialog.getSelectedFile()
		statusHolder.notifyFO(true)
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
