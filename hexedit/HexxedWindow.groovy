package hexedit

import groovy.swing.SwingBuilder
import javax.swing.*
import javax.swing.table.*
import java.awt.*
import java.awt.event.*

class HexxedWindow {

	def swingWindow
	def swingBuilder
	def statusHolder
	def bitsButtons = []
	def tableHex = null
	def tableChar
	def tableHexHeader
	def menuNextBlock
	def menuPrevBlock
	def menuUseBlock
	def setBlockSizeMenu
	def hexxedKeyListener
	def leMenu
	def beMenu
	def tablePanel
	def commandMap = [:]
	
	HexxedWindow(def x, def y, def controller)
	{
		statusHolder = controller
		//create basic window
		//then create menus
		swingBuilder = new SwingBuilder()
		swingWindow = swingBuilder.frame(title: "HEXXED", size: [x, y],
			show: true) {
			gridBagLayout()

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
					separator()
					setBlockSizeMenu = menuItem(text: "Set block size",
					mnemonic: 'z', actionPerformed: { chooseBlockSize() })
					separator()
					leMenu = radioButtonMenuItem(text: "Little Endian")
					beMenu = radioButtonMenuItem(text: "Big Endian")
				}
				menu(text: "Navigate", mnemonic: 'N'){
					menuItem(text: "Forwards", mnemonic: 'F',
						actionPerformed: { forward() })
					menuItem(text: "Backwards", mnemonic: 'B',
						actionPerformed: { backward() })
					separator(){}
					menuUseBlock = checkBoxMenuItem(text: "Use block",
						mnemonic: 'U',
						actionPerformed: { toggleBlockUse() })
					menuNextBlock = menuItem(text: "Next block", mnemonic: 'x',
						actionPerformed: { nextBlock() })
					menuPrevBlock = menuItem(text: "Previous block", mnemonic: 'P',
						actionPerformed: { previousBlock() })
				}
				menu(text: "About", mnemonic: 'A'){
					menuItem(text: "Help", mnemonic: 'H',
						actionPerformed: { helpMenu()})
					menuItem(text: "Licence", mnemonic: 'L',
						actionPerformed: { displayGPL() })
				}
			}
			
			tablePanel = scrollPane(constraints:gbc(gridx:0, gridy:0, 
				gridwidth:20, gridheight:20,
				fill:GridBagConstraints.BOTH,
				anchor:GridBagConstraints.FIRST_LINE_START,
				weightx:0.8, weighty:0.7)) {
				tableHex = table() {visible:true}
			}
			scrollPane(constraints:gbc(gridx:GridBagConstraints.RELATIVE,
				gridy:0, gridwidth:5, gridheight:20,
				fill:GridBagConstraints.BOTH,
				weightx:0.2, weighty:0.7)) {
				tableChar = table() {visble: true}
			}
			tableHex.setModel(new HexxedTableModel(statusHolder))
			tableChar.setModel(new HexxedCharTableModel(statusHolder))
			
			def endianGroup = buttonGroup(id: "Endianness")
			endianGroup.add(leMenu)
			endianGroup.add(beMenu)
			if (statusHolder.littleEndian) {
				leMenu.selected = true
			} else {
				beMenu.selected = true
			}
			leMenu.actionPerformed = {toggleEndian()}
			beMenu.actionPerformed = {toggleEndian()}
			
		}
			
		swingBuilder.lookAndFeel("system")
		tableHex.setFont(new Font("Monospaced", Font.PLAIN, 12))
		if (statusHolder.useBlocks == false) {
			menuNextBlock.setEnabled(false)
			menuPrevBlock.setEnabled(false)
			setBlockSizeMenu.setEnabled(false)
			menuUseBlock.setSelected(false)
		} else {
			menuUseBlock.setSelected(true)
		}
		
		statusHolder.subscribeFileOpen(this)
		statusHolder.subscribeBitWidth(this)
		statusHolder.subscribeUseBlocks(this)
		
		
		commandMap = ['ESC':"VI_MODE", ';':"COMMAND_MODE", 'G':"END"]
		
		commandMap.each() { k, v ->
			tableHex.getInputMap().put(KeyStroke.getKeyStroke(k), "$v")
			tableHex.getActionMap().put("$v", new HexxedViAction(this, HexxedConstants."$v"))
		}

	}
		
	void setAddressColour(Color color)
	{
		def columnRenderer = new HexxedAddressColumn()
		tableHex.getColumnModel().getColumn(0).setCellRenderer(columnRenderer)
		columnRenderer.setOpaque(true)
		columnRenderer.setBackground(color)
	}
	
	void setColumnWidths()
	{
		def tableModel = tableHex.getModel()
		def colCount = tableModel.getColumnCount()
		tableHex.getColumnModel().getColumn(0).setPreferredWidth(100)
		for (i in 1 .. (colCount - 1)) {
			tableHex.getColumnModel().getColumn(i).setPreferredWidth(20)
		}
	}
	
	void setColumnNames()
	{
		def tableModel = tableHex.getModel()
		tableHex.createDefaultColumnsFromModel()
		tableModel.colNames.eachWithIndex { name, i ->
			tableHex.getColumnModel().getColumn(i).setHeaderValue(name)
		}
		def tableCharModel = tableChar.getModel()
		tableChar.createDefaultColumnsFromModel()
		tableCharModel.colNames.eachWithIndex {name, i ->
			tableChar.getColumnModel().getColumn(i).setHeaderValue(name)
		}
	}

	void updateUB(def useBlocks)
	{
		menuNextBlock.setEnabled(useBlocks)
		menuPrevBlock.setEnabled(useBlocks)
		setBlockSizeMenu.setEnabled(useBlocks)
	}
	
	void updateFO(def fileStatus)
	{
		if (fileStatus)
			swingWindow.title = statusHolder.fileName
		else
			swingWindow.title = "HEXXED"
		setColumnNames()
		setColumnWidths()
		setAddressColour(Color.YELLOW)
	}
	
	void updateBW(def bitWidth)
	{
		setColumnNames()
		setColumnWidths()
		setAddressColour(Color.YELLOW)
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
	
	def chooseBlockSize()
	{
		
		def blockSizeDialog = swingBuilder.optionPane()
		def retVal = blockSizeDialog.showInputDialog(null,
			"", "Set Block Size", JOptionPane.INFORMATION_MESSAGE,
			null, null, statusHolder.blockSize)

		def numb = retVal as Integer
		if (numb <= 16) {
			statusHolder.setUseBlocks(false)
			statusHolder.setBlockSize(0)
		} else {
			statusHolder.setBlockSize(numb)
		}
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
	
	def toggleBlockUse(def menuBlockItem)
	{
		if (statusHolder.useBlocks == false) {
			statusHolder.setUseBlocks(true)
			chooseBlockSize()
		} else {
			statusHolder.setUseBlocks(false)
		}
	}
	
	def toggleEndian()
	{
		def toggle = statusHolder.littleEndian
		statusHolder.setLittleEndian(!toggle)
		statusHolder.setBigEndian(toggle)
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
	{
		def position = statusHolder.offset
		position += statusHolder.blockSize
		def multi = (position / statusHolder.blockSize) as Integer
		statusHolder.setOffset(multi * statusHolder.blockSize) 	
	}
	
	def previousBlock()
	{
		def position = statusHolder.offset
		position -= statusHolder.blockSize
		if (position < 0)
			position = 0
		def multi = (position / statusHolder.blockSize) as Integer
		statusHolder.setOffset(multi * statusHolder.blockSize)
	}
	
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
