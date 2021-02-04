package bld.plugin.jpa.service.generator;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import javax.persistence.Entity;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import bld.commons.generator.classes.GeneratorClass;
import bld.commons.generator.classes.impl.GeneratorClassImpl;
import bld.commons.generator.config.ConfigurationClassGenerator;
import bld.commons.yaml.model.ModelClasses;
import bld.commons.yaml.utils.ClassGeneratorUtils;
import bld.plugin.jpa.service.generator.classes.Generator;

@Mojo(name = "jpa-service-generator", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresDependencyCollection = ResolutionScope.COMPILE)
@SuppressWarnings("resource")
public class JpaServiceGeneratorPlugin extends AbstractMojo {

	@Parameter(property = "project", readonly = true)
	private MavenProject project;

	@Parameter(required = true)
	private String persistencePackage;
//	
	@Parameter(defaultValue = "${project.basedir}/target/generated-sources/classes")
	private String outputDirectory;
	
	
	@Parameter(defaultValue = "/template")
	private String resourceTemplateDirectory;

	
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			project.addCompileSourceRoot(new File(outputDirectory).getAbsolutePath());
			
			if(!persistencePackage.endsWith("."))
				persistencePackage=persistencePackage+".";
			ModelClasses modelClasses=new ModelClasses();
			String classesDirectory = this.project.getBuild().getOutputDirectory();
			File dir=new File(this.project.getBuild().getOutputDirectory());
			if(!dir.exists())
				dir.mkdirs();
			String importJar="";
			for(Artifact artifact:this.project.getArtifacts()) 
				importJar+=":"+artifact.getFile().getPath();
			String command="javac -cp ."+importJar+" -d "+classesDirectory +" "+this.project.getBasedir() + "/src/main/java/com/bld/persistence/core/domain/*.java";
			getLog().info(command);
			

			ProcessBuilder processBuilder=new ProcessBuilder(new String[]{"bash","-c",command});
			processBuilder.redirectOutput(new File(this.project.getBuild().getOutputDirectory()+"/out.txt"));
			processBuilder.redirectError(new File("out-error.txt"));
			processBuilder.start().waitFor();
			
			
			List<String> runtimeClasspathElements = this.project.getRuntimeClasspathElements();
			
			URL[] urls = new URL[runtimeClasspathElements.size()];
			for (int i = 0; i < runtimeClasspathElements.size(); i++) 
				urls[i] = new File(runtimeClasspathElements.get(i)).toURI().toURL();

			

			URLClassLoader urlClassLoader = new URLClassLoader(urls,Thread.currentThread().getContextClassLoader());
			List<File> files = ClassGeneratorUtils.getFiles(classesDirectory + "/com/bld/persistence/core/domain","class");
			for (File file : files) {
				String nameClass=persistencePackage.replace("/", ".")+ file.getName().replace(".class", "");
				Class<?>entityClass=urlClassLoader.loadClass(nameClass);
				if(entityClass.isAnnotationPresent(Entity.class)) {
					Generator.generateClass(modelClasses, entityClass, classesDirectory);
				}
					
			} 
				

			getLog().info("size entities: " + modelClasses.getClasses().size());
			
			
			GeneratorClass generatorClass=new GeneratorClassImpl(ConfigurationClassGenerator.configClassGenerator(resourceTemplateDirectory));
			generatorClass.writeClass(modelClasses, outputDirectory);
			


			getLog().info("----------------End reflection--------------------");
			this.project.addLifecyclePhase("install");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
