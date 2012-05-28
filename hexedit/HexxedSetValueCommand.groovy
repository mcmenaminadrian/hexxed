package hexedit

class HexxedSetValueCommand implements Command {
	
	def row
	def col
	def newValue
	def oldValue
	def position
	def le
	def be
	def bitWidth
	def statusHolder
	
	HexxedSetValueCommand(def r, def c, def value, def statusObj)
	{
		newValue = value
		statusHolder = statusObj
		row = r
		col = c
		oldValue = statusHolder.valueAt(row, col)
		position = statusHolder.offset
		le = statusHolder.littleEndian
		be = statusHolder.bigEndian
		bitWidth = statusHolder.bitWidth
	}
	
	void execute() {
		statusHolder.executeSetValue(this)
		def tempVal = newValue
		newValue = oldValue
		oldValue = tempVal
	}

}
