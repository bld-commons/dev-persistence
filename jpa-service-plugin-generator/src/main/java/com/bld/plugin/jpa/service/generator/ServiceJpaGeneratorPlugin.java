/*
 *@author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.plugin.jpa.service.generator.ServiceJpaGeneratorPlugin.java 
 */
package com.bld.plugin.jpa.service.generator;

import java.io.File;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import com.bld.commons.classes.generator.ClassesGenerator;
import com.bld.commons.classes.generator.config.ConfigurationClassGenerator;
import com.bld.commons.classes.generator.impl.ClassesGeneratorImpl;
import com.bld.commons.classes.generator.utils.ClassGeneratorUtils;
import com.bld.commons.classes.model.EntityModel;
import com.bld.commons.classes.model.ModelClasses;
import com.bld.commons.classes.type.OutputDirectoryType;
import com.bld.plugin.jpa.service.generator.classes.ClassBuilding;

/**
 * The Class JpaServiceGeneratorPlugin.
 */
@Mojo(name = "jpa-service-generator", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE, requiresDependencyCollection = ResolutionScope.COMPILE)
@SuppressWarnings("resource")
public class ServiceJpaGeneratorPlugin extends AbstractMojo {

	/** The Constant TARGET_GENERATED_SOURCES_CLASSES. */
	private static final String TARGET_GENERATED_SOURCES_CLASSES = "/target/generated-sources/classes";

	/** The project. */
	@Parameter(property = "project", readonly = true)
	private MavenProject project;

	/** The persistence package. */
	@Parameter(required = true)
	private String persistencePackage;

	/** The output directory. */
	@Parameter(defaultValue = "src_main_java")
	private OutputDirectoryType outputDirectory;

	/** The service package. */
	@Parameter(required = true)
	private String servicePackage;

	/** The base package. */
	@Parameter(required = true)
	private String basePackage;

	/** The repository package. */
	@Parameter(required = false)
	private String repositoryPackage;

	/** The resource template directory. */
	@Parameter(defaultValue = "/template")
	private String resourceTemplateDirectory;

//	@Parameter
//	private List<String> buildPackages;

	/**
	 * Execute.
	 *
	 * @throws MojoExecutionException the mojo execution exception
	 * @throws MojoFailureException   the mojo failure exception
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {
			String slash = "/";
//			String shell = "bash";
//			if (System.getProperty("os.name").toLowerCase().contains("win")) {
//				slash = "\\";
//				shell = "cmd.exe";
//			}
			
			
			String outputDirectory = this.project.getBasedir() + this.outputDirectory.getValue();
			String classesDirectory = this.project.getBuild().getOutputDirectory();

			project.addCompileSourceRoot(new File(this.project.getBasedir() + TARGET_GENERATED_SOURCES_CLASSES).getAbsolutePath());
			Set<EntityModel>entitiesModel=ClassGeneratorUtils.entitiesModel(outputDirectory,persistencePackage,slash);
			
			ModelClasses modelClasses = new ModelClasses();
			
//			Set<String>buildPackages=ClassGeneratorUtils.buildPackage(outputDirectory,persistencePackage.replace(".", slash), "^import "+basePackage+".*;$", slash,null,entities);
//
//
//			if (!persistencePackage.endsWith("."))
//				persistencePackage = persistencePackage + ".";
//			ModelClasses modelClasses = new ModelClasses();
//
//			File dir = new File(classesDirectory);
//			if (!dir.exists())
//				dir.mkdirs();
//			String importJar = "";
//			for (Artifact artifact : this.project.getArtifacts())
//				importJar += ":" + artifact.getFile().getPath();
//
//			String compileSoruceRoot = this.project.getCompileSourceRoots().get(0);
//			String persistenceDirectory = compileSoruceRoot + slash + persistencePackage.replace(".", slash);
//
//			String packages = persistenceDirectory + "*.java";
//			//String target = this.project.getBuild().getOutputDirectory() + slash;
//			if (CollectionUtils.isNotEmpty(buildPackages)) {
//				for (String buildPackage : buildPackages) {
//					packages += " " + compileSoruceRoot + slash + buildPackage.replace(".", slash) +slash+ "*.java";
//				}
//			}
//
//			String command = "javac -cp ." + importJar + " -d " + classesDirectory + " " + packages;
//			getLog().debug(command);
//
//			ProcessBuilder processBuilder = new ProcessBuilder(new String[] { shell, "-c", command });
//			processBuilder.redirectOutput(new File(this.project.getBuild().getOutputDirectory() +slash+ "out.log"));
//			File errorFile = new File(this.project.getBuild().getOutputDirectory() +slash+ "out-error.log");
//			processBuilder.redirectError(errorFile);
//			processBuilder.start().waitFor();
//			readLog(errorFile);
//			List<String> runtimeClasspathElements = this.project.getRuntimeClasspathElements();
//
//			URL[] urls = new URL[runtimeClasspathElements.size()];
//			for (int i = 0; i < runtimeClasspathElements.size(); i++)
//				urls[i] = new File(runtimeClasspathElements.get(i)).toURI().toURL();
//
//			URLClassLoader urlClassLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
//			List<File> files = ClassGeneratorUtils.getFiles(classesDirectory + slash + persistencePackage.replace(".", slash), "class");
//			for (File file : files) {
//
//				String nameClass = file.getPath().replace(target, "").replace(slash, ".").replace(".class", "");
//				Class<?> entityClass = urlClassLoader.loadClass(nameClass);
//				try {
//					if (entityClass.isAnnotationPresent(Entity.class))
//						ClassBuilding.generateClass(modelClasses, entityClass, classesDirectory, servicePackage, repositoryPackage);
//				} catch (ArrayStoreException e) {
//					getLog().error(ExceptionUtils.getStackTrace(e));
//					getLog().error("TypeNotPresentExceptionProxy to: " + entityClass.getName());
//				}
//
//			}
			
//			for (String entity:entities) {
//
//				//String nameClass = file.getPath().replace(target, "").replace(slash, ".").replace(".class", "");
//				Class<?> entityClass = urlClassLoader.loadClass(entity);
//				try {
//						ClassBuilding.generateClass(modelClasses, entityClass, classesDirectory, servicePackage, repositoryPackage);
//				} catch (ArrayStoreException e) {
//					getLog().error(ExceptionUtils.getStackTrace(e));
//					getLog().error("TypeNotPresentExceptionProxy to: " + entityClass.getName());
//				}
//
//			}
//			
			for (EntityModel entityModel:entitiesModel) {

				//String nameClass = file.getPath().replace(target, "").replace(slash, ".").replace(".class", "");
				
				try {
						ClassBuilding.generateClass(modelClasses, entityModel, classesDirectory, servicePackage, repositoryPackage);
				} catch (ArrayStoreException e) {
					getLog().error(ExceptionUtils.getStackTrace(e));
					getLog().error("TypeNotPresentExceptionProxy to: " + entityModel.getClassName());
				}

			}


			getLog().debug("Entities size: " + modelClasses.getClasses().size());

			ClassesGenerator generatorClass = new ClassesGeneratorImpl(ConfigurationClassGenerator.configClassGenerator(resourceTemplateDirectory));
			generatorClass.writeClass(modelClasses, outputDirectory);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	/**
//	 * Read log.
//	 *
//	 * @param file the file
//	 */
//	private void readLog(File file) {
//		try {
//			FileInputStream fstream = new FileInputStream(file);
//			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
//			String strLine;
//			while ((strLine = br.readLine()) != null)
//				System.out.println(strLine);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}


}
