package hexedit

import groovy.swing.SwingBuilder
import javax.swing.*
import javax.swing.table.*
import java.awt.*

class HexxedWindow {

	def swingWindow
	def swingBuilder
	def statusHolder
	def bitsButtons = []
	def tableHex = null
	def tableChar
	def tableHexHeader
	
	
	HexxedWindow(def x, def y, def controller)
	{
		statusHolder = controller
		//create basic window
		//then create menus
		swingBuilder = new SwingBuilder()
		swingWindow = swingBuilder.frame(title: "HEXXED", size: [x, y],
			show: true) {
			gridBagLayout()
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
			scrollPane() {
				tableHex = table() {visible:true}
			}
			scrollPane() {
				tableChar = table() {visble: true}
			}
			tableHex.setModel(new HexxedTableModel(statusHolder))
			tableChar.setModel(new HexxedCharTableModel(statusHolder))
		}
		statusHolder.subscribeFileOpen(this)
		statusHolder.subscribeBitWidth(this)
	}
	
	void updateFO(def fileStatus)
	{
		if (fileStatus)
			swingWindow.title = statusHolder.fileName
		else
			swingWindow.title = "HEXXED"
	}
	
	void updateBW(def bitWidth)
	{
		def tableModel = tableHex.getModel()
		tableHex.createDefaultColumnsFromModel()
		tableModel.colNames.eachWithIndex { name, i ->
			tableHex.getColumnModel().getColumn(i).setHeaderValue(name)
		}
	}
	
	def chooseWidth()
	{
		def bitWidthDialog = swingBuilder.frame(
			title: "Set Bit Width",
			size: [450, 190]
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
		statusHolder.changeFileName()
	}
	
	def forward()
	{
		statusHolder.offset += 256
	}
	
	def backward()
	{
		def pos = statusHolder.offset
		if (pos >= 256)
			statusHolder.offset -= 256
		else
			statusHolder.offset = 0 
	}
	
	def nextBlock()
	{}
	
	def previousBlock()
	{}
	
	def helpMenu()
	{}
	
	def displayGPL()
	{
		def name = "Hexxed"
		def version = "0.1"
		def copyright = "copyright (c) Adrian McMenamin, 2012"
		def message =
		name +  " v" + version + "\n" + "\n" + copyright +
		"\n\nThis program is free software, You can redistribute" +
		"\nit and/or modify it under the terms of the GNU" +
		"\nGeneral Public License (GPL) as published by the " +
		"\nFree Software Foundation; either version 3 of the" +
		"\nlicence, or (at your option) any later version.\n";
		
		def gPLDialog = swingBuilder.frame(title: "Licence terms",
			size: [320, 200]) {
			scrollPane(constraints: BorderLayout.CENTER){
				textArea(text: message)}
		}
		gPLDialog.pack()
		gPLDialog.show()
	}
}
