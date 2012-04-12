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
	
	HexWindow(def x, def y, def handler) 
	{
		fileHandler = handler
		windowHex = new SwingBuilder()
		frameHex = windowHex.frame(title:handler.fileName, size:[x, y],
			show:true){
			editHex = editorPane(contentType: "text/plain")
			menuHex = menuBar() {
				menuFileHex = menu(text: "File", mnemonic: 'F') {
					menuItem(text: "Exit", mnemonic: 'x',
						actionPerformed: {dispose()})
					menuItem(text: "Load", mnemonic: 'L',
						actionPerformed: {loadFile()})
				}
			menuNavigateHex = menu(text: "Navigate", mnemonic: 'N') {
				menuNavigateForward = menuItem(text: "Forward", 
					mnemonic: 'r', actionPerformed: {fileHandler.showLines()})
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
