public class HuffmanNode implements Comparable<HuffmanNode> {
	
	private Character inChar;
	private int frequency;
	private HuffmanNode left;
	private HuffmanNode right;
	
	public Character getInChar() {
		return inChar;
	}

	public void setInChar(Character inChar) {
		this.inChar = inChar;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public HuffmanNode getLeft() {
		return left;
	}

	public void setLeft(HuffmanNode left) {
		this.left = left;
	}

	public HuffmanNode getRight() {
		return right;
	}

	public void setRight(HuffmanNode right) {
		this.right = right;
	}
	
	// so nodes can be sorted
	@Override
	public int compareTo(HuffmanNode node) {
		return getFrequency() - node.getFrequency();
	}
	
	// contructor to set up node
	public HuffmanNode(Character inChar, int frequency, HuffmanNode left, HuffmanNode right) {
		this.inChar = inChar;
		this.frequency = frequency;
		this.left = left;
		this.right = right;
	}

}
