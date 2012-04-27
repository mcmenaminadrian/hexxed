package hexxed

import java.nio.channels.FileChannel


class HexxedStart
{

	HexxedStart(littleEndian, bigEndian, bitWidth, offsetInFile, blockSize,
	useBlocks, fileToEdit)
	{
		def hexxedStatus		//Model
		def hexxedWindow		//View
		def hexxedFile			//Container
	
		hexxedStatus = HexxedStatus.currentStatus
		hexxedStatus.littleEndian = littleEndian
		hexxedStatus.bigEndian = bigEndian
		hexxedStatus.bitWidth = bitWidth
		hexxedStatus.useBlocks = useBlocks
		hexxedStatus.blockSize = blockSize
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
			offset = Integer.parseInt(hexParse.o)
		
		def hexFileHandler = new HexxedStart(le, be, bits, offset, bs,
			blocks, fileToEdit)	
	}