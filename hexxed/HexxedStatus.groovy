package hexxed

class HexxedStatus {
	
	private HexxedStatus() {}
	private static final currentStatus = new HexxedStatus()
	static getCurrentStatus() { return currentStatus}
	def littleEndian
	def bigEndian
	def bitWidth
	def useBlocks
	def blockSize

}
