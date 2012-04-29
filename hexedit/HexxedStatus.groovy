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
	
	def subscribersLittleEndian = []
	def subscribersBigEndian = []
	def subscribersBitWidth = []
	def subscribersUseBlocks = []
	def subscribersBlockSize = []
	
	
	
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
	
	void notifyBE(def list)
	{
		list.each{$it.updateBE(bigEndian)}
	}
	
	void notifyLE(def list)
	{
		list.each{$it.updateLE(littleEndian)}
	}
		
	void notifyBW(def list)
	{
		list.each{$it.updateBW(bitWidth)}
	}
	
	void notifyUB(def list)
	{
		list.each{$it.updateUB(useBlocks)}
	}
	
	void notifyBS(def list)
	{
		list.each{$it.updateBS(blockSize)}
	}
	
	void subscribeBigEndian(def subscriber)
	{
		//cannot subscribe twice
		subscribersBigEndian -= subscriber
		subscribersBigEndian << subscriber
	}
	
	void unsubscribeBigEndian(def subscriber)
	{
		subscribersBigEndian -= subscriber
	}
	
	void subscribeLittleEndian(def subscriber)
	{
		//cannot subscribe twice
		subscribersLittleEndian -= subscriber
		subscribersLittleEndian << subscriber
	}
	
	void unsubscribeLittleEndian(def subscriber)
	{
		subscribersLittleEndian -= subscriber
	}
	
	void subscribeBitWidth(def subscriber)
	{
		//cannot subscribe twice
		subscribersBitWidth -= subscriber
		subscribersBitWidth << subscriber
	}
	
	void unsubscribeBitWidth(def subscriber)
	{
		subscribersBitWidth -= subscriber
	}
	void subscribeUseBlocks(def subscriber)
	{
		//cannot subscribe twice
		subscribersUseBlocks -= subscriber
		subscribersUseBlocks << subscriber
	}
	
	void unsubscribeUseBlocks(def subscriber)
	{
		subscribersUseBlocks -= subscriber
	}

	void subscribeBlockSize(def subscriber)
	{
		//cannot subscribe twice
		subscribersBlockSize -= subscriber
		subscribersBlockSize << subscriber
	}
	
	void unsubscribeBlockSize(def subscriber)
	{
		subscribersBlockSize -= subscriber
	}
	
}
