package com.bld.processor.data;

public class PathPackage {
	
	

	private String repositoryPackage;


	private String servicePackage;
	

	public PathPackage(String repositoryPackage, String servicePackage) {
		super();
		this.repositoryPackage = repositoryPackage;
		this.servicePackage = servicePackage;
//		this.source = baseDir+OutputDirectoryType.src_main_java.getValue();
//		this.target=baseDir+OutputDirectoryType.target_generated_source_annotations.getValue();
//		this.baseDir=baseDir;
	}

	public String getRepositoryPackage() {
		return repositoryPackage;
	}


	public String getServicePackage() {
		return servicePackage;
	}
	

	@Override
	public String toString() {
		return "PathPackage [repositoryPackage=" + repositoryPackage + ", servicePackage=" + servicePackage + "]";
	}

	
	
	
}
