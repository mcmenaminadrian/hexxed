package Hexxed

import groovy.swing.SwingBuilder
import java.awt.Font
import java.awt.FlowLayout
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
						def bitWidthDialog = dialog(
							title: "Set Bit Width",
							size: [450, 190],
						) { 
							panel(){
								def widthButtonGroup = buttonGroup()
								def bitSize = 4 
								for (i in 1 .. 4) {
									bitSize = bitSize * 2
									def strBitSz = "$bitSize bits"
									def curButton = radioButton(text: strBitSz,
										buttonGroup: widthButtonGroup,
										label: bitSize,
										)
									curButton.actionPerformed = {
											fileHandler.displayEngine.bits = (curButton.label).toInteger()
											def curPos = fileHandler.fileChan.position()
												if (fileHandler.eOF) {
													curPos = curPos - 480
													fileHandler.eOF = false
												} else
													curPos = curPos - 960
												if (curPos < 0)
													curPos = 0
												fileHandler.fileChan.position(curPos)
												fileHandler.showLines()
											}
									if (i == handler.displayEngine.bits)
									curButton.selected = true
									}
	
								}
						}
						bitWidthDialog.pack()
						bitWidthDialog.show()
					}
				)
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
