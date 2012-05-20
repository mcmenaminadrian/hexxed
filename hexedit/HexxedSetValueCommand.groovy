package hexedit

public interface Command {
	void execute();
 }

class HexxedSetValueCommand implements Command {
	
	def row
	def col
	def newValue
	def oldValue
	def statusHolder
	
	HexxedSetValueCommand(def r, def c, def value, def statusObj)
	{
		newValue = value
		statusHolder = statusObj
		row = r
		col = c
		oldValue = statusHolder.valueAt(row, col)
	}
	
	void execute() {
		statusHolder.executeSetValue(this)
	}
	

}
