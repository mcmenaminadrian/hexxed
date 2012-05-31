package hexedit

class HexxedDeleteCommand implements Command {
	
	def count
	def statusHolder
	def le
	def be
	def bitWidth
	def position
	def oldValues = []
	
	HexxedDeleteCommand(def cnt, def statusObject)
	{
		count = cnt
		statusHolder = statusObject
		le = statusHolder.littleEndian
		be = statusHolder.bigEndian
		bitWidth = statusHolder.bitWidth
		position = statusHolder.offset
		def selectedCol = statusHolder.windowEdit.tableHex.getSelectedColumn()
		if (selectedCol < 1)
			return
		def selectedRow = statusHolder.windowEdit.tableHex.getSelectedRow()
		position += 
			(selectedRow * 16 + (--selectedCol) * (bitWidth / 8)) as Integer
	}
	
	def clone()
	{
		def returnedObject = new HexxedDeleteCommand(this.count,
			this.statusHolder)
		returnedObject.le = this.le
		returnedObject.be = this.be
		returnedObject.bitWidth = this.bitWidth
		returnedObject.position = this.position
		return returnedObject
	}
	
	void execute()
	{
		statusHolder.executeDelete(this)
	}

}
