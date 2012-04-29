package hexedit
class HexxedStatus {
	
	
	private HexxedStatus() {}
	
	private static final currentStatus = new HexxedStatus()
	
	static getCurrentStatus() { return currentStatus}
	
	def littleEndian
	def bigEndian
	def bitWidth
	def useBlocks
	def blockSize
	def windowEdit
	def fileOpen = false
	def fileName
	def offset
	
	def subscribersLittleEndian = []
	def subscribersBigEndian = []
	def subscribersBitWidth = []
	def subscribersUseBlocks = []
	def subscribersBlockSize = []
	def subscribersFileOpen = []
	def subscribersOffset = []
	
	void setOffset(def off)
	{
		offset = off
		notifyOffset(subscribersOffset)
	}
	
	void setFileOpen(def fo)
	{
		fileOpen = fo
		notifyFO(subscribersFileOpen)
	}
	
	void setLittleEndian(def le)
	{
		littleEndian = le
		notifyLE(subscribersLittleEndian)
	}
	
	void setBigEndian(def be)
	{
		bigEndian = be
		notifyBE(subscribersBigEndian)
	}
	
	void setBitWidth(def bw)
	{
		bitWidth = bw
		notifyBW(subscribersBitWidth)
	}
	
	void setUseBlocks(def ub)
	{
		useBlocks = ub
		notifyUB(subscribersUseBlocks)
	}
	
	void setBlockSize(def bs)
	{
		blockSize = bs
		notifyBS(subscribersBlockSize)
	}
	
	void setWindowEdit(def we)
	{
		windowEdit = we
	}
	
	void notifyBE(def listSubs)
	{
		listSubs.each{it.updateBE(bigEndian)}
	}
	
	void notifyLE(def listSubs)
	{
		listSubs.each{it.updateLE(littleEndian)}
	}
		
	void notifyBW(def listSubs)
	{
		listSubs.each{it.updateBW(bitWidth)}
	}
	
	void notifyUB(def listSubs)
	{
		listSubs.each{it.updateUB(useBlocks)}
	}
	
	void notifyBS(def listSubs)
	{
		listSubs.each{it.updateBS(blockSize)}
	}
	
	void notifyFO(def listSubs)
	{
		listSubs.each{it.updateFO(fileOpen)}
	}
	
	void notifyOffset(def listSubs)
	{
		listSubs.each{it.updateOff(offset)}
	}
	
	void subscribeBigEndian(def subscriber)
	{
		subscribersBigEndian -= subscriber
		subscribersBigEndian << subscriber
	}
	
	void unsubscribeBigEndian(def subscriber)
	{
		subscribersBigEndian -= subscriber
	}
	
	void subscribeLittleEndian(def subscriber)
	{
		subscribersLittleEndian -= subscriber
		subscribersLittleEndian << subscriber
	}
	
	void unsubscribeLittleEndian(def subscriber)
	{
		subscribersLittleEndian -= subscriber
	}
	
	void subscribeBitWidth(def subscriber)
	{
		subscribersBitWidth -= subscriber
		subscribersBitWidth << subscriber
	}
	
	void unsubscribeBitWidth(def subscriber)
	{
		subscribersBitWidth -= subscriber
	}
	
	void subscribeUseBlocks(def subscriber)
	{
		subscribersUseBlocks -= subscriber
		subscribersUseBlocks << subscriber
	}
	
	void unsubscribeUseBlocks(def subscriber)
	{
		subscribersUseBlocks -= subscriber
	}

	void subscribeBlockSize(def subscriber)
	{
		subscribersBlockSize -= subscriber
		subscribersBlockSize << subscriber
	}
	
	void unsubscribeBlockSize(def subscriber)
	{
		subscribersBlockSize -= subscriber
	}
	
	void subscribeFileOpen(def subscriber)
	{
		subscribersFileOpen -= subscriber
		subscribersFileOpen << subscriber
	}
	
	void unsubscribeFileOpen(def subscriber)
	{
		subscribersFileOpen -= subscriber
	}
	
	void subscribeOffset(def subscriber)
	{
		subscribersOffset -= subscriber
		subscribersOffset << subscribe
	}
	
	void unsubscribeOffset(def subscriber)
	{
		subscribersOffset -= subscriber
	}
	
}
