package bld.plugin.jpa.service.generator.classes;

import java.lang.reflect.Field;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bld.commons.persistence.reflection.utils.ReflectionUtils;
import bld.commons.yaml.costant.ClassType;
import bld.commons.yaml.model.ModelAnnotation;
import bld.commons.yaml.model.ModelClass;
import bld.commons.yaml.model.ModelClasses;
import bld.commons.yaml.model.ModelGenericType;
import bld.commons.yaml.model.ModelSuperClass;

public class Generator {

	private final static Log logger = LogFactory.getLog(Generator.class);
	
	private static final String SERVICE = "Service";
	private static final String REPOSITORY = "Repository";
	private static final String SERVICE_IMPL = SERVICE+"Impl";

	public static void generateClass(ModelClasses modelClasses,Class<?> classEntity,String path) {
		ModelClass interfaceRepository=new ModelClass();
		ModelClass interfaceService=new ModelClass();
		ModelClass classService=new ModelClass();
		String className=classEntity.getSimpleName();
		String packageName = classEntity.getName().replace("."+className, "");
		String packageService=packageName.substring(0,packageName.lastIndexOf(".")+1)+SERVICE.toLowerCase();
		
		logger.info(packageName);
		logger.info(packageService);
		interfaceRepository.getImports().add(classEntity.getName());
		interfaceService.getImports().add(classEntity.getName());
		classService.getImports().add(classEntity.getName());
	
		interfaceRepository.setName(className+REPOSITORY);
		interfaceRepository.setPackageName(packageName);
		interfaceRepository.setType(ClassType.INTERFACE);
		ModelAnnotation annotationRepository=new ModelAnnotation();
		annotationRepository.setName("Repository");
		interfaceRepository.getAnnotations().add(annotationRepository);
		ModelSuperClass superClassBaseJpaRepository=new ModelSuperClass();
		superClassBaseJpaRepository.setName("BaseJpaRepository");
		ModelGenericType genericTypeEntityClass=new ModelGenericType();
		genericTypeEntityClass.setName(className);
		superClassBaseJpaRepository.getGenericTypes().add(genericTypeEntityClass);
		Set<Field> listField=ReflectionUtils.getListField(classEntity);
		ModelGenericType genericTypeId=new ModelGenericType();
		for(Field field:listField) {
			
			if(field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class)) {
				Class<?>classType=field.getType();
				logger.info("ID name: "+field.getName()+" --- type: "+classType.getSimpleName());
				if(ReflectionUtils.mapPrimitiveToObject.containsKey(classType))
					classType=ReflectionUtils.mapPrimitiveToObject.get(classType);
				genericTypeId.setName(classType.getSimpleName());
				if(!classType.getName().startsWith("java.lang")){
					interfaceRepository.getImports().add(classType.getName());
					interfaceService.getImports().add(classType.getName());
					classService.getImports().add(classType.getName());
				}
				break;
			}
		}
		superClassBaseJpaRepository.getGenericTypes().add(genericTypeId);
		interfaceRepository.getExtendsClass().add(superClassBaseJpaRepository);
		modelClasses.getClasses().add(interfaceRepository);
		
		
		interfaceService.setName(className+SERVICE);
		interfaceService.setType(ClassType.INTERFACE);
		interfaceService.setPackageName(packageService);
		ModelSuperClass superClassJpaService=new ModelSuperClass();
		superClassJpaService.setName("JpaService");
		
		
		superClassJpaService.getGenericTypes().add(genericTypeEntityClass);
		superClassJpaService.getGenericTypes().add(genericTypeId);
		interfaceService.getExtendsClass().add(superClassJpaService);
		
		modelClasses.getClasses().add(interfaceService);
		
		classService.setName(className+SERVICE_IMPL);
		classService.setPackageName(packageService);
		ModelAnnotation repositoryAnnotation=new ModelAnnotation();
		repositoryAnnotation.setName("Repository");
		classService.getAnnotations().add(repositoryAnnotation);
	}
	
}
