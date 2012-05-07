package hexedit

import javax.swing.table.*
import javax.swing.event.TableModelEvent

class HexxedTableModel extends AbstractTableModel {
	
	def hexxedStatus
	def hexxedFile
	def colNames = ["Hex"]
	
	HexxedTableModel(def statusObject)
	{
		hexxedStatus = statusObject
		hexxedStatus.subscribeOffset(this)
		hexxedStatus.subscribeBitWidth(this)
		hexxedStatus.subscribeBlockSize(this)
		hexxedStatus.subscribeFileOpen(this)
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
		def width = hexxedStatus.bitWidth
		if (width == 8)
			return 17
		else if (width == 16)
			return 9
		else if (width == 32)
			return 5
		else
			return 3
	}

	
	def updateColumnNames(def bitWidth)
	{
		def skip = 1
		colNames = ["Address"]
		if (bitWidth == 16)
			skip = 2
		else if (bitWidth == 32)
			skip = 4
		else if (bitWidth == 64)
			skip = 8
		(0 .. 15).step(skip) { i ->
			colNames << String.format("%02X", i)
		}
	}
		
	def getValueAt(int row, int col)
	{
		if (row >= getRowCount() || col >= getColumnCount())
			return null
		return hexxedStatus.valueAt(row, col)
	}
	
	void updateBW(def bitWidth)
	{
		updateColumnNames(bitWidth)
		fireTableChanged(new TableModelEvent(this))
	}
	
	def getColumnName(def col)
	{
		if (col >= getColumnCount())
			return null
		else
			return colNames[col]
	}
	
	void updateOff(def ignore)
	{
		fireTableChanged(new TableModelEvent(this))
	}
	
	void updateBS(def ignore)
	{
		fireTableChanged(new TableModelEvent(this))
	}
	
	void updateFO(def ignore)
	{
		updateColumnNames(hexxedStatus.bitWidth)
		fireTableChanged(new TableModelEvent(this))
	}
	
}