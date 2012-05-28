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
	
	def clone()
	{
		def returnedObject = new HexxedSetValueCommand(this.row, this.col,
			this.newValue, this.statusHolder)
		returnedObject.oldValue = this.oldValue
		returnedObject.position = this.position
		returnedObject.le = this.le
		returnedObject.be = this.be
		returnedObject.bitWidth = this.bitWidth
		return returnedObject
	}
	
	void execute() {
		statusHolder.executeSetValue(this)
		def tempVal = newValue
		newValue = oldValue
		oldValue = tempVal
	}

}
