package bld.commons.reflection.annotations.deserialize.data;

public class MaxConsecutiveSpaceProps {
	private int consecutive;

	private boolean trim;
	
	private boolean removeEndline;
	
	private boolean removeAllSpaceType;



	public MaxConsecutiveSpaceProps(int consecutive, boolean trim, boolean removeEndline, boolean removeAllSpaceType) {
		super();
		this.consecutive = consecutive;
		this.trim = trim;
		this.removeEndline = removeEndline;
		this.removeAllSpaceType = removeAllSpaceType;
	}

	public int getConsecutive() {
		return consecutive;
	}

	public boolean isTrim() {
		return trim;
	}

	public boolean isRemoveEndline() {
		return removeEndline;
	}

	public boolean isRemoveAllSpaceType() {
		return removeAllSpaceType;
	}

	
}
