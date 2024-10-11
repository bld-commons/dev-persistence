/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class com.bld.processor.data.PathPackage.java
 */
package com.bld.processor.data;

/**
 * The Class PathPackage.
 */
public class PathPackage {
	
	

	/** The repository package. */
	private String repositoryPackage;


	/** The service package. */
	private String servicePackage;
	

	/**
	 * Instantiates a new path package.
	 *
	 * @param repositoryPackage the repository package
	 * @param servicePackage the service package
	 */
	public PathPackage(String repositoryPackage, String servicePackage) {
		super();
		this.repositoryPackage = repositoryPackage;
		this.servicePackage = servicePackage;
//		this.source = baseDir+OutputDirectoryType.src_main_java.getValue();
//		this.target=baseDir+OutputDirectoryType.target_generated_source_annotations.getValue();
//		this.baseDir=baseDir;
	}

	/**
	 * Gets the repository package.
	 *
	 * @return the repository package
	 */
	public String getRepositoryPackage() {
		return repositoryPackage;
	}


	/**
	 * Gets the service package.
	 *
	 * @return the service package
	 */
	public String getServicePackage() {
		return servicePackage;
	}
	

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return "PathPackage [repositoryPackage=" + repositoryPackage + ", servicePackage=" + servicePackage + "]";
	}

	
	
	
}
