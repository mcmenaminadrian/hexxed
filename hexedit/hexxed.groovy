package hexedit

import java.nio.channels.FileChannel
import java.nio.ByteOrder
import java.awt.*


class HexxedStart
{

	HexxedStart(littleEndian, bigEndian, bitWidth, offsetInFile, blockSize,
	useBlocks, fileToEdit, x, y)
	{
		def hexxedStatus		//singleton - controller
		def hexxedWindow		//View
		def hexxedFile
	
		hexxedStatus = HexxedStatus.currentStatus
		if (littleEndian == false && bigEndian == false) {
			hexxedStatus.littleEndian = (ByteOrder.nativeOrder()
				== ByteOrder.LITTLE_ENDIAN) ? true : false
			hexxedStatus.bigEndian = !hexxedStatus.littleEndian
		} else {
			hexxedStatus.setLittleEndian(littleEndian)
			hexxedStatus.setBigEndian(bigEndian)
		}
		hexxedStatus.setBitWidth(bitWidth)
		hexxedStatus.setUseBlocks(useBlocks)
		hexxedStatus.setBlockSize(blockSize)
		hexxedStatus.setOffset(offsetInFile)
		
		hexxedWindow = new HexxedWindow(x, y, hexxedStatus)
		hexxedStatus.setWindowEdit(hexxedWindow)
		
		hexxedFile = new HexxedFile(hexxedStatus)
		if (fileToEdit)
			hexxedFile.getNewFile(fileToEdit)
		hexxedStatus.setHexxedFile(hexxedFile)
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
	hexCli.x(longOpt: 'x', args: 1, 'width of window (default 640 pixels)')
	hexCli.y(longOpt: 'y', args: 1, 'height of window (default 480 pixels)')
	
	def hexParse = hexCli.parse(args)
	if (hexParse.u) {
		hexCli.usage()
	} else {
		def bits = 8
		def le = false
		def be = false
		def bs = 512L
		def blocks = false
		def offset = 0L
		def fileToEdit
		def xw = 640
		def yh = 480
		
		if (hexParse.f)
			fileToEdit = hexParse.f
		
		if (hexParse.le)
			le = true
		else if (hexParse.be)
			be = true
			
		if (hexParse.b) {
			blocks = true
			if (hexParse.s)
				bs = Long.parseLong(hexParse.s)
		}
		
		if (hexParse.w){
			def numb = Integer.parseInt(hexParse.w)
			numb = numb >> 3
			bits = 8 
			for (i in 1 .. 3) {
				numb = numb >> 1
				if (numb & 1) {
					if (i == 1)
						bits = 16
					else if (i == 2)
						bits = 32
					else
						bits = 64
					break;
				}
			}
		}
		
		if (hexParse.o)
			offset = Long.parseLong(hexParse.o)

		if (hexParse.x)
			xw = Integer.parseInt(hexParse.x)
		if (hexParse.y)
			yh = Integer.parseInt(hexParse.y)
		
		def hexFileHandler = new HexxedStart(le, be, bits, offset, bs,
			blocks, fileToEdit, xw, yh)	
	}