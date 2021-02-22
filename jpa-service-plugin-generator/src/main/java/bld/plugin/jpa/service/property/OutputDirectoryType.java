/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.plugin.jpa.service.property.OutputDirectoryType.java
 */
package bld.plugin.jpa.service.property;

/**
 * The Enum OutputDirectoryType.
 */
public enum OutputDirectoryType {

	/** The src. */
	SRC("src/main/java"),
	
	/** The target. */
	TARGET("target/generated-sources/classes");
	
	
	/** The value. */
	private String value;
	

	/**
	 * Instantiates a new output directory type.
	 *
	 * @param value the value
	 */
	private OutputDirectoryType(String value) {
		this.value=value;
	}


	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	
	
}
