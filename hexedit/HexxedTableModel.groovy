package hexedit

import javax.swing.table.*

class HexxedTableModel extends AbstractTableModel {
	
	def hexxedStatus
	def hexxedFile
	
	HexxedTableModel(def statusObject, def fileToModel)
	{
		hexxedStatus = statusObject
		hexxedFile = fileToModel
		hexxedStatus.subscribeOffset(this)
		hexxedStatus.subscribeBitWidth(this)
		hexxedStatus.subscribeBlockSize(this)
	}
	
	int getRowCount() { return 16}
	
	int getColumnCount()
	{
		def width = hexxedStatus.bitWidth
		if width == 8
			return 17
		else if width == 16
			return 9
		else if width == 8
			return 5
		else
			return 3
	}
	
	def getValueAt(def row, def col)
	{
		if (row > getRowCount() || col > getColCount())
			return null
		return hexxedStatus.valueAt(row, col)
	}
}
