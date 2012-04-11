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
	
	HexFileHandler(def le, def be, def bits, def offset,
		def bSize, def blocks, def name)
	{
		displayEngine = new HexDisplayState(le, be, bits, bSize, blocks)
		fileName = name
		fileOffset = offset
		blockSize = bSize
		
		try {
			randomFile = new RandomAccessFile(name, "rw") 
			fileChan = randomFile.getChannel()
			fileChan.position(fileOffset)
			open = true
		}
		catch(e) {
			println "Unable to open file $fileName, exception $e"
		}
		
		editFile = new HexWindow(640, 480, this)
		displayLines = new HexDisplay(displayEngine, fileChan)
		showLines()
	}
	
	void finalize()
	{
		super.finalize()
		if (open) {
			fileChan.close()
			randomFile.close()
		}
	}
	
	void showLines()
	{		
		def displayStr = ''
		for (i in 1 .. 30) {
			displayStr = displayStr + displayLines.showLine()
		}
		editFile.editHex.setText(displayStr)
	}
	
}

def hexCli = new CliBuilder
	(usage: 'hexxed [options] <file to edit>')
	
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
	
	def hexParse = hexCli.parse(args)
	if (hexParse.u || args.size() == 0) {
		hexCli.usage()
	} else {
		def bits = 8
		def le = false
		def be = false
		def bs = 512
		def blocks = false
		def offset = 0
		
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
			bits = Integer.parseInt(hexParse.w)
			bits = bits & 0xF8
			if (bits == 0)
				bits = 8
			else if (bits > 64)
				bits = 64
		}
		
		if (hexParse.o)
			offset = Integer.parseInt(hexParse.o)
		
		def hexFileHandler = new HexFileHandler(le, be, bits, offset, bs,
			blocks, args[args.size() - 1])	
	}