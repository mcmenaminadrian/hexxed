package hexedit

class UndoRecord {
	def bitSize
	def endianness
	def oldValue
	def address = startByte
	
	UndoRecord(def sizeBit, def littleEndian, def oldVal, def startByte)
	{
		bitSize = sizeBit
		if (littleEndian)
			endianness = HexxedConstants.LITTLE_ENDIAN
		else
			endianness = HexxedConstants.BIG_ENDIAN
		oldValue = oldVal
		address = startByte
	}
}
