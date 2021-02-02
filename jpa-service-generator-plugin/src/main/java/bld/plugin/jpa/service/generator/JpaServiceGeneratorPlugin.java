package bld.plugin.jpa.service.generator;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import bld.commons.yaml.utils.ClassGeneratorUtils;

@Mojo(name = "jpa-service-generator", defaultPhase = LifecyclePhase.INSTALL, requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM, requiresDependencyCollection = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class JpaServiceGeneratorPlugin extends AbstractMojo {

	@Parameter(property = "project", readonly = true)
	private MavenProject project;

	@Parameter(required = true)
	private String persistencePackage;
//	
	@Parameter(defaultValue = "${project.basedir}/target/")
	private String outputDirectory;
//	
//	@Parameter(required = true)
//	private String outputDirectory;

	@Parameter(defaultValue = "${project.basedir}/target/jap-service.xml")
	private String saveXml;

	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			if(!persistencePackage.endsWith("."))
				persistencePackage=persistencePackage+".";
			List<Class<?>>entities=new ArrayList<>();
			
			String outputDirectory = this.project.getBuild().getOutputDirectory();
			List<String> runtimeClasspathElements = this.project.getRuntimeClasspathElements();

			List<File> files = ClassGeneratorUtils.getFiles(outputDirectory + "/bld/persistence/core/domain","class");
			URL[] urls = new URL[runtimeClasspathElements.size()];

			for (int i = 0; i < runtimeClasspathElements.size(); i++)
				urls[i] = new File(runtimeClasspathElements.get(i)).toURI().toURL();

			 URLClassLoader urlClassLoader = new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
			
			for (File file : files) {
				String nameClass=persistencePackage.replace("/", ".")+ file.getName().replace(".class", "");
				Class<?>entityClass=urlClassLoader.loadClass(nameClass);
				if(entityClass.isAnnotationPresent(Entity.class)) {
					getLog().info(nameClass);
					entities.add(entityClass);
				}
					
			} 
				

			getLog().info("size entities: " + entities.size());
			


			getLog().info("----------------End reflection--------------------");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
