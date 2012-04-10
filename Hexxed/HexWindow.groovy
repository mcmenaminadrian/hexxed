package Hexxed

import groovy.swing.SwingBuilder
import java.awt.Font
import javax.swing.*


class HexWindow {
		
	def editHex
	
	HexWindow(def x, def y) {
	def windowHex = new SwingBuilder()
	def frameHex = windowHex.frame(title:'Hexxed', size:[x, y], show:true){
		editHex = editorPane(contentType: "text/plain")
		}
	windowHex.lookAndFeel("system")
	editHex.setFont(new Font("Monospaced", Font.PLAIN, 14))

	}
	
	
}
