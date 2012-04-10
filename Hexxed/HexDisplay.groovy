package Hexxed

import java.nio.ByteBuffer

class HexDisplay {
	
	def displayEngine
	def fileChannel
	def ALLOCATE = 0x10
	
	HexDisplay(def engine, def channel)
	{
		displayEngine = engine
		fileChannel = channel
	}
	
	String showLine()
	{
		return showLine(true)
	}
	
	String showLine(def advance)
	{
		def lineOut
		def position = fileChannel.position()
		if (displayEngine.useBlock) {
			def decBlock = (position / displayEngine.blockSize) as Integer
			def blockCnt = String.format("%08X", decBlock)
			def offCnt = String.format("%04X", position % displayEngine.blockSize)
			lineOut = "$blockCnt:$offCnt\t"
		} else
			lineout = String.format("%08X\t", position)
		def byteCnt = (displayEngine.bits / 8) as Integer
		def fString = "%0${byteCnt * 2}X"
		def i = 0
		BigInteger numb = 0
		
		def displayHexLine = {
			Long x = 0x00000000000000FF & it
			numb = numb * 256 + x
			i++
			if (i == byteCnt) {
				i = 0
				def hexStr = String.format(fString, numb)
				lineOut = lineOut + " $hexStr"
				numb = 0
			}
		}
		
		char outChar ='\0'
		i = 0
		def displayCharLine = {
			Long x = 0x00000000000000FF & it
			outChar = outChar * 256 + it
			if (byteCnt > 1) {
				i++
				if (i > 1) {
					lineOut = lineOut + outChar
					i = 0
					outChar = '\0'
				}
			}
			else {
				lineOut = lineOut + outChar
				outChar = '\0'
			}
		}
		
		def bytes = ByteBuffer.allocate(ALLOCATE)
		fileChannel.read(bytes, position)
		(bytes.array()).eachByte(displayHexLine)
		lineOut = lineOut + "\t"
		(bytes.array()).eachByte(displayCharLine)
		fileChannel.position(position + 0x10)
		return lineOut + "\r"
	}

}