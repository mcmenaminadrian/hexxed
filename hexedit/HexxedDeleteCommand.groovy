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
	}
	
	void execute()
	{
		statusHolder.executeDelete(this)
	}

}
