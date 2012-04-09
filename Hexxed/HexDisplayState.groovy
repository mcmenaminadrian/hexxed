package Hexxed

class HexDisplayState {
	
	def littleEndian
	def bigEndian
	def bits
	def blockSize
	def useBlock

	HexDisplayState(def le, def be, def width, def bs, def blocks)
	{
		littleEndian = le
		bigEndian = be
		bits = width
		blockSize = bs
		useBlock = blocks
	}
}
