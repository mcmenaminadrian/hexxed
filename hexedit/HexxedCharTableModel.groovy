package hexedit

import javax.swing.table.*
import javax.swing.event.TableModelEvent

class HexxedCharTableModel extends AbstractTableModel {

	def hexxedStatus
	
	HexxedCharTableModel(def statusObject)
	{
		hexxedStatus = statusObject
		hexxedStatus.subscribeBitWidth(this)
		hexxedStatus.subscribeOffset(this)
		hexxedStatus.subscribeFileOpen(this)
	}
	
	int getRowCount()
	{
		if (!hexxedStatus.fileOpen)
			return 0
		else
			return 17
	}
	
	int getColumnCount()
	{
		return 1
	}
	
	def getColumnName(def col)
	{
		return "Characters"
	}
	
	def getValueAt(int row, int col)
	{
		if (col >= getColumnCount() || row >= getRowCount())
			return null
		return hexxedStatus.stringAt(row)
	}
	
	void updateOff(def ignore)
	{
		fireTableChanged(new TableModelEvent(this))
	}
	
	void updateBW(def ignore)
	{
		fireTableChanged(new TableModelEvent(this))
	}
	
	void updateFO(def ignore)
	{
		fireTableChanged(new TableModelEvent(this))
	}
}
