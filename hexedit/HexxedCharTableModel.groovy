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
	{
		def max = hexxedStatus.fileChan.size() - 1
		if (max >= hexxedStatus.offset + HexxedConstants.ROWMAX * 16)
			return HexxedConstants.ROWMAX
		else
			return Math.ceil(((max - hexxedStatus.offset)/16)) as Long
		}
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
		def val
		if (col >= getColumnCount() || row >= getRowCount())
			return null
		try {
			val = hexxedStatus.stringAt(row)
		}
		catch (IOException e) {
			if (e.getMessage() != "EOF")
				throw e
		}
		return val
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
