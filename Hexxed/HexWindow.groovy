package Hexxed

import groovy.swing.SwingBuilder
import java.awt.Font
import javax.swing.*


class HexWindow {
		
	def editHex
	def menuBarHex
	def menuFileHex
	def fileHandler
	def fileLoader
	def menuNavigateHex
	def menuNavigateForward
	def frameHex
	def windowHex
	def bitWidthDialog
	
	HexWindow(def x, def y, def handler) 
	{
		fileHandler = handler
		windowHex = new SwingBuilder()
		
		def moveBackwards = {
	
		}
		
		frameHex = windowHex.frame(title:handler.fileName, size:[x, y],
			show:true){
			editHex = editorPane(contentType: "text/plain")
			menuBar() {
				menu(text: "File", mnemonic: 'F') {
					menuItem(text: "Load", mnemonic: 'L',
						actionPerformed: {loadFile()})
					menuItem(text: "Exit", mnemonic: 'x',
						actionPerformed: {dispose()})
			}
			menu(text: "Navigate", mnemonic: 'N') {
				menuItem(text: "Forwards", 
					mnemonic: 'r', actionPerformed: {fileHandler.showLines()})
				menuItem(text: "Backwards", mnemonic: 'B',
					actionPerformed: {
						def curPos = fileHandler.fileChan.position()
						if (fileHandler.eOF) {
							curPos = curPos - 480
							fileHandler.eOF = false
						} else
							curPos = curPos - 960
						fileHandler.fileChan.position(curPos)
						fileHandler.showLines()
					}
				)
			}
			menu(text: "Display", mnemonic: 'D'){
				menuItem(text: "Set Width", mnemonic: 'W',
					actionPerformed: {
						def swingSetWidth = new SwingBuilder()
						bitWidthDialog = swingSetWidth.dialog(
							title: "Set Bit Width",
							visible:true,
							owner: frameHex){
								panel(visible:true){
									radioButton(text: "8 bits", visible:true)
									radioButton(text: "16 bits")
									radioButton(text: "32 bits")
									radioButton(text: "64 bits")
								}
							}
						})
			}
		}
	}
	windowHex.lookAndFeel("system")
	editHex.setFont(new Font("Monospaced", Font.PLAIN, 14))

	}
	
	void loadFile()
	{
		def swingLoadFile = new SwingBuilder()
		def loadDialog = swingLoadFile.fileChooser(
			dialogTitle: "Choose a file to open"
		)
		loadDialog.showOpenDialog()
		fileHandler.setNewFile(loadDialog.getSelectedFile())
	}
}
