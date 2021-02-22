package bld.plugin.jpa.service.property;

public enum OutputDirectoryType {

	SRC("src/main/java"),
	TARGET("target/generated-sources/classes");
	
	
	private String value;
	

	private OutputDirectoryType(String value) {
		this.value=value;
	}


	public String getValue() {
		return value;
	}
	
	
	
}
