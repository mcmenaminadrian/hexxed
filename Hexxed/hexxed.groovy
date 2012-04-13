package Hexxed

import java.nio.channels.FileChannel

class HexFileHandler {

	def fileName
	def fileHandle
	def fileOffset
	def displayEngine
	def blockSize
	def fileChan
	def randomFile
	def editFile
	def displayLines
	def open = false
	def eOF = false
	
	HexFileHandler(def le, def be, def bits, def offset,
		def bSize, def blocks, def name)
	{
		displayEngine = new HexDisplayState(le, be, bits, bSize, blocks)
		fileName = name
		fileOffset = offset
		blockSize = bSize

		editFile = new HexWindow(640, 480, this)
		setNewFile(name)
	}
	
	void finalize()
	{
		super.finalize()
		if (open) {
			fileChan.close()
			randomFile.close()
			open = false
		}
	}
	
	void showLines()
	{		
		def displayStr = ''
		for (i in 1 .. 30) {
			def nextLine = displayLines.getLine()
			if (!nextLine)
				break
			displayStr = displayStr + nextLine
		}
		editFile.editHex.setText(displayStr)
	}
	
	void setNewFile(def fileInName)
	{
		if (open) {
			fileChan.close()
			randomFile.close()
			open = false
		}
		if (fileInName) {
			try {
				randomFile = new RandomAccessFile(fileInName, "rw")
				fileChan = randomFile.getChannel()
				fileChan.position(fileOffset)
				open = true
				displayLines = new HexDisplay(displayEngine, fileChan, this)
				showLines()
			}
			catch(e) {
				println "Unable to open file $fileInName, exception $e"
			}
		}
		fileName = fileInName
		editFile.frameHex.title = fileName
	}
	
}

def hexCli = new CliBuilder
	(usage: 'hexxed [options]')
	
	hexCli.o(longOpt: 'offset', args: 1,
		'offset in file - default 0')
	hexCli.b(longOpt: 'block',
		'use block:offset address output - default is linear address')
	hexCli.s(longOpt: 'blocksize', args: 1,
		'size of block if block:offset addressing used - default is 0x200')
	hexCli.w(longOpt: 'width', args: 1,
		'width (in bits, 8 - 64 bits) of output data - default is 8 bits')
	hexCli.le(longOpt: 'littleendian',
		'interpret data as little endian - default is cpu endianness')
	hexCli.be(longOpt: 'bigendian',
		'interpret data as big endian - default is cpu endianness');
	hexCli.u(longOpt: 'usage', 'show this information')
	hexCli.f(longOpt: 'file', args: 1, 'file to edit')
	
	def hexParse = hexCli.parse(args)
	if (hexParse.u) {
		hexCli.usage()
	} else {
		def bits = 8
		def le = false
		def be = false
		def bs = 512
		def blocks = false
		def offset = 0
		def fileToEdit
		
		if (hexParse.f)
			fileToEdit = hexParse.f
		
		if (hexParse.le)
			le = true
		else if (hexParse.be)
			be = true
			
		if (hexParse.b) {
			blocks = true
			if (hexParse.s)
				bs = Integer.parseInt(hexParse.s)
		}
		
		if (hexParse.w){
			def numb = Integer.parseInt(hexParse.w)
			numb = numb >> 4
			bits = 8 
			for (i in 1 .. 3) {
				numb = numb >> 1
				if (numb & 1) {
					bits = bits * (2 ** i)
					break;
				}
			}
		}
		
		if (hexParse.o)
			offset = Integer.parseInt(hexParse.o)
		
		def hexFileHandler = new HexFileHandler(le, be, bits, offset, bs,
			blocks, fileToEdit)	
	}