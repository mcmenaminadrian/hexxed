package Hexxed

import java.nio.ByteBuffer

class HexDisplay {
	
	def displayEngine
	def fileChannel
	def ALLOCATE = 0x10
	def hexFileHandler
	
	HexDisplay(def engine, def channel, def hexHandler)
	{
		displayEngine = engine
		fileChannel = channel
		hexFileHandler = hexHandler
	}
	
	String getLine()
	{
		return getLine(true)
	}
	
	String getLine(def advance)
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
			Long x = 0xFF & it
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
			outChar = outChar * 256 + it
			if (byteCnt > 1) {
				i++
				if (i > 1) {
					if (outChar < 32 || outChar == 127)
						outChar = ' '
					lineOut = lineOut + outChar
					i = 0
					outChar = '\0'
				}
			}
			else {
				if (outChar< 32 || outChar == 127)
					outChar = ' '
				lineOut = lineOut + outChar
				outChar = '\0'
			}
		}
		
		def bytes = ByteBuffer.allocate(ALLOCATE)
		def bytesRet = fileChannel.read(bytes, position)
		if (bytesRet == -1) {
			hexFileHandler.eOF = true
			return null
		}
		(bytes.array()).eachByte(displayHexLine)
		lineOut = lineOut + "\t"
		(bytes.array()).eachByte(displayCharLine)
		if (advance) {
			fileChannel.position(position + 0x10)
		}
		return lineOut + "\r"
	}

}