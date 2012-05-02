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
	{ }
}
