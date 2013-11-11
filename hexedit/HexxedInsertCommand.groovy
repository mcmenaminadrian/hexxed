package hexedit

class HexxedInsertCommand implements Command {

	def count
	def insertPosition
	def statusHolder
	def bitWidth
	def done
	
	HexxedInsertCommand(def cnt, def statusObj)
	{
		count = cnt
		statusHolder = statusObj
		bitWidth = statusHolder.bitWidth
		insertPosition = statusHolder.offset
		def colPosition = statusHolder.windowEdit.tableHex.getSelectedColumn()
		if (colPosition > 0) {
			def rowPosition = statusHolder.windowEdit.tableHex.getSelectedRow()
			insertPosition += 
				(--colPosition * (bitWidth / 8)) + rowPosition * 16 as Long
		}
		done = false // inserting
		
	}
	
	def clone()
	{
		def returnedObject =
			new HexxedInsertCommand(this.count, this.statusHolder)
		returnedObject.bitWidth = this.bitWidth
		returnedObject.done = !(this.done)
		returnedObject.insertPosition = this.insertPosition
		return returnedObject
	}
	
	void execute()
	{
		statusHolder.executeInsert(this)
	}

}
