package hexedit

import javax.swing.table.*
import javax.swing.event.TableModelEvent

class HexxedCharTableModel extends AbstractTableModel {

	def hexxedStatus
	def colNames = ["Characters"]
	
	HexxedCharTableModel(def statusObject)
	{
		hexxedStatus = statusObject
		hexxedStatus.subscribeBitWidth(this)
		hexxedStatus.subscribeOffset(this)
		hexxedStatus.subscribeFileOpen(this)
		hexxedStatus.subscribeLittleEndian(this)
		fireTableChanged(new TableModelEvent(this))
	}
	
	int getRowCount()
	{
		if (!hexxedStatus.fileOpen)
			return 0
		else
			return 40
	}
	
	int getColumnCount()
	{
		return 1
	}
	
	def getColumnName(def col)
	{
		if (col >= getColumnCount())
			return null
		else
			return colNames[col]
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
	
	void updateLE(def ignore)
	{
		fireTableChanged(new TableModelEvent(this))
	}
		
	void updateBW(def bitWidth)
	{
		if (bitWidth == 8)
			colNames = ["Characters (UTF8)"]
		else
			colNames = ["Characters (16 bit Unicode)"]
		
		fireTableChanged(new TableModelEvent(this))
	}
	
	void updateFO(def ignore)
	{
		fireTableChanged(new TableModelEvent(this))
	}
}
