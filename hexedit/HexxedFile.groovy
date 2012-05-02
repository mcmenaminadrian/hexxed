package hexedit

class HexxedFile {
	
	def hexxedStatus
	def fileChan
	def randomFile
	def tableModel

	HexxedFile(def statusObject)
	{
		hexxedStatus = statusObject
		hexxedStatus.subscribeFileOpen(this)
		hexxedStatus.subscribeFileName(this)
		fileChan = null
		randomFile = null
	}
	
	void updateFN()
	{
		getNewFile(hexxedStatus.fileName)
	}
	
	void updateFO(def fileOpen)
	{
		if (!fileOpen) {
			if (fileChan)
				fileChan.close()
			if (randomFile)
				randomFile.close()
			fileChan = null
			randomFile = null
		}
	}
	
	void getNewFile(def fileToGet)
	{
		if (hexxedStatus.fileOpen)
			hexxedStatus.setFileOpen(false)
		
		if (fileToGet) {
			try {
				randomFile = new RandomAccessFile(fileToGet, "rw")
				fileChan = randomFile.getChannel()
				fileChan.position(hexxedStatus.offset)
				hexxedStatus.fileName = fileToGet
				hexxedStatus.setFileOpen(true)
			}
			catch(e)
			{
				println "Unable to open $fileToGet, exception $e"
			}
		}	
	}
	
	void finalize()
	{
		super.finalize()
		if (hexxedStatus.fileOpen) {
			fileChan.close()
			randomFile.close()
			hexxedStatus.setFileOpen(false)
		}
	}
}
