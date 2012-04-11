package Hexxed

import groovy.swing.SwingBuilder
import java.awt.Font
import javax.swing.*


class HexWindow {
		
	def editHex
	def menuBarHex
	def menuFileHex
	def fileHandler
	def menuNavigateHex
	def menuNavigateForward
	
	HexWindow(def x, def y, def handler) {
	fileHandler = handler
	def windowHex = new SwingBuilder()
	def frameHex = windowHex.frame(title:'Hexxed', size:[x, y], show:true){
		editHex = editorPane(contentType: "text/plain")
		menuHex = menuBar() {
			menuFileHex = menu(text: "File", mnemonic: 'F') {
				menuItem(text: "Exit", mnemonic: 'x',
					actionPerformed: {dispose()})
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
	
	
}
