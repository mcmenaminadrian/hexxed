package hexedit

class HexxedFile {
	
	def hexxedStatus
	def fileChan
	def randomFile

	HexxedFile(def statusObject)
	{
		hexxedStatus = statusObject
		hexxedStatus.subscribeFileOpen(this)
	}
	
	void updateFO(def fileStatus)
	{
		if (fileStatus)
			getNewFile(hexxedStatus.fileName)
		else {
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
				fileChan.position(hexxedStatus)
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
