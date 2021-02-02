package bld.plugin.jpa.service.generator.classes;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bld.commons.yaml.costant.ClassType;
import bld.commons.yaml.model.ModelAnnotation;
import bld.commons.yaml.model.ModelClass;
import bld.commons.yaml.model.ModelGenericType;
import bld.commons.yaml.model.ModelSuperClass;

public class Generator {

	private final static Log logger = LogFactory.getLog(Generator.class);
	
	private static final String SERVICE = "Service";
	private static final String REPOSITORY = "Repository";
	private static final String SERVICE_IMPL = SERVICE+"Impl";

	public static void generateClass(Class<?> classEntity,String path) {
		ModelClass interfaceRepository=new ModelClass();
		ModelClass interfaceService=new ModelClass();
		ModelClass classService=new ModelClass();
		String className=classEntity.getSimpleName();
		String packageName = classEntity.getPackageName();
		String packageService=classEntity.getPackageName().substring(0,classEntity.getPackageName().lastIndexOf("."))+SERVICE.toLowerCase();
		
		logger.info(packageName);
		logger.info(packageService);
		interfaceRepository.getImports().add(classEntity.getName());
		
		interfaceRepository.setName(className+REPOSITORY);
		interfaceRepository.setPackageName(packageName);
		interfaceRepository.setType(ClassType.INTERFACE);
		ModelAnnotation annotationRepository=new ModelAnnotation();
		annotationRepository.setName("Repository");
		interfaceRepository.getAnnotations().add(annotationRepository);
		ModelSuperClass superClassBaseJpaRepository=new ModelSuperClass();
		ModelGenericType genericTypeEntityClass=new ModelGenericType();
		
		superClassBaseJpaRepository.setGenericTypes(null);
		interfaceRepository.setExtendsClass(null);
		
		
		interfaceService.setName(className+SERVICE);
		interfaceService.setType(ClassType.INTERFACE);
		interfaceService.setPackageName(packageService);
		
		classService.setName(className+SERVICE_IMPL);
		classService.setPackageName(packageService);
		ModelAnnotation repositoryAnnotation=new ModelAnnotation();
		repositoryAnnotation.setName("Repository");
		classService.getAnnotations().add(repositoryAnnotation);
	}
	
}
