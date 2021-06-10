/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.plugin.jpa.service.generator.JpaServiceGeneratorPlugin.java
 */
package bld.plugin.jpa.service.generator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

import javax.persistence.Entity;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import bld.commons.classes.generator.ClassesGenerator;
import bld.commons.classes.generator.config.ConfigurationClassGenerator;
import bld.commons.classes.generator.impl.ClassesGeneratorImpl;
import bld.commons.classes.generator.utils.ClassGeneratorUtils;
import bld.commons.classes.model.ModelClasses;
import bld.plugin.jpa.service.generator.classes.ClassBuilding;
import bld.plugin.jpa.service.property.OutputDirectoryType;

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
	@Parameter(defaultValue = "TARGET")
	private OutputDirectoryType outputDirectory;

	/** The resource template directory. */
	@Parameter(defaultValue = "/template")
	private String resourceTemplateDirectory;

	@Parameter
	private List<String> buildPackages;

	/**
	 * Execute.
	 *
	 * @throws MojoExecutionException the mojo execution exception
	 * @throws MojoFailureException   the mojo failure exception
	 */
	public void execute() throws MojoExecutionException, MojoFailureException {
		try {

			String outputDirectory = this.project.getBasedir() + "/" + this.outputDirectory.getValue();
			String classesDirectory = this.project.getBuild().getOutputDirectory();

			project.addCompileSourceRoot(new File(this.project.getBasedir() + TARGET_GENERATED_SOURCES_CLASSES).getAbsolutePath());

			String slash = "/";
			String shell = "bash";
			if (System.getProperty("os.name").toLowerCase().contains("win")) {
				slash = "\\";
				shell = "cmd.exe";
			}

			if (!persistencePackage.endsWith("."))
				persistencePackage = persistencePackage + ".";
			ModelClasses modelClasses = new ModelClasses();

			File dir = new File(classesDirectory);
			if (!dir.exists())
				dir.mkdirs();
			String importJar = "";
			for (Artifact artifact : this.project.getArtifacts())
				importJar += ":" + artifact.getFile().getPath();

			String packages = this.project.getCompileSourceRoots().get(0) + slash + persistencePackage.replace(".", slash) + "*.java";
			String target=this.project.getBuild().getOutputDirectory()+"/";
			getLog().info(target);
			if (CollectionUtils.isNotEmpty(buildPackages)) {
				for (String buildPackage : this.buildPackages) {
					packages += " " + this.project.getCompileSourceRoots().get(0) + slash + buildPackage.replace(".", slash) + "/*.java";
				}
			}

			String command = "javac -cp ." + importJar + " -d " + classesDirectory + " " + packages;
			getLog().info(command);

			ProcessBuilder processBuilder = new ProcessBuilder(new String[] { shell, "-c", command });
			processBuilder.redirectOutput(new File(this.project.getBuild().getOutputDirectory() + "/out.log"));
			File errorFile = new File(this.project.getBuild().getOutputDirectory() + "/out-error.log");
			processBuilder.redirectError(errorFile);
			processBuilder.start().waitFor();
			readLog(errorFile);
			List<String> runtimeClasspathElements = this.project.getRuntimeClasspathElements();

			URL[] urls = new URL[runtimeClasspathElements.size()];
			for (int i = 0; i < runtimeClasspathElements.size(); i++)
				urls[i] = new File(runtimeClasspathElements.get(i)).toURI().toURL();

			URLClassLoader urlClassLoader = new URLClassLoader(urls, Thread.currentThread().getContextClassLoader());
			List<File> files = ClassGeneratorUtils.getFiles(classesDirectory + slash + persistencePackage.replace(".", slash), "class");
			for (File file : files) {
				
				String nameClass = file.getPath().replace(target, "").replace("/", ".").replace(".class", "");
				Class<?> entityClass = urlClassLoader.loadClass(nameClass);
				if (entityClass.isAnnotationPresent(Entity.class))
					ClassBuilding.generateClass(modelClasses, entityClass, classesDirectory);
			}

			getLog().info("Entities size: " + modelClasses.getClasses().size());

			ClassesGenerator generatorClass = new ClassesGeneratorImpl(ConfigurationClassGenerator.configClassGenerator(resourceTemplateDirectory));
			generatorClass.writeClass(modelClasses, outputDirectory);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void readLog(File file) {
		try {
			FileInputStream fstream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			while ((strLine = br.readLine()) != null) 
				System.out.println(strLine);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
