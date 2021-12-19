/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.plugin.jpa.service.generator.classes.ClassBuilding.java
 */
package bld.plugin.jpa.service.generator.classes;

import java.lang.reflect.Field;
import java.util.Set;

import javax.persistence.EmbeddedId;
import javax.persistence.Id;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bld.commons.classes.attributes.ClassType;
import bld.commons.classes.attributes.LevelType;
import bld.commons.classes.model.ModelAnnotation;
import bld.commons.classes.model.ModelClass;
import bld.commons.classes.model.ModelClasses;
import bld.commons.classes.model.ModelField;
import bld.commons.classes.model.ModelGenericType;
import bld.commons.classes.model.ModelMethod;
import bld.commons.classes.model.ModelSuperClass;
import bld.commons.reflection.utils.ReflectionUtils;

/**
 * The Class ClassBuilding.
 */
public class ClassBuilding {

	private static final String BASE_JPA_REPOSITORY = "BaseJpaRepository";
	
	private static final String QUERY_BUILDER = "QueryBuilder";

	/** The Constant SPACE. */
	private static final String SPACE="        ";
	
	/** The Constant OVERRIDE. */
	private static final String OVERRIDE = "Override";
	
	private static final ModelAnnotation ANNOTATION_QUERY_BUILDER = getModelAnnotation(QUERY_BUILDER);
	
	/** The Constant NAMED_PARAMETER_JDBC_TEMPLATE. */
	private static final String NAMED_PARAMETER_JDBC_TEMPLATE = "NamedParameterJdbcTemplate";
	
	/** The Constant JDBC_TEMPLATE. */
	private static final String JDBC_TEMPLATE = "jdbcTemplate";
	
	/** The Constant AUTOWIRED. */
	private static final String AUTOWIRED = "Autowired";
	
	/** The Constant TRANSACTIONAL. */
	private static final String TRANSACTIONAL = "Transactional";
	
	/** The Constant ENTITY_MANAGER. */
	private static final String ENTITY_MANAGER = "EntityManager";
	
	/** The Constant logger. */
	private final static Log logger = LogFactory.getLog(ClassBuilding.class);
	
	/** The Constant SERVICE. */
	private static final String SERVICE = "Service";
	
	/** The Constant REPOSITORY. */
	private static final String REPOSITORY = "Repository";
	
	/** The Constant SERVICE_IMPL. */
	private static final String SERVICE_IMPL = SERVICE + "Impl";

	/** The Constant ANNOTATION_REPOSITORY. */
	private static final ModelAnnotation ANNOTATION_REPOSITORY = getModelAnnotation(REPOSITORY);
	
	/** The Constant ANNOTATION_SERVICE. */
	private static final ModelAnnotation ANNOTATION_SERVICE = getModelAnnotation(SERVICE);
	
	/** The Constant ANNOTATION_TRANSACTIONAL. */
	private static final ModelAnnotation ANNOTATION_TRANSACTIONAL = getModelAnnotation(TRANSACTIONAL);
	
	/** The Constant ANNOTATION_AUTOWIRED. */
	private static final ModelAnnotation ANNOTATION_AUTOWIRED = getModelAnnotation(AUTOWIRED);
	
	/** The Constant ANNOTATION_OVERRIDE. */
	private static final ModelAnnotation ANNOTATION_OVERRIDE = new ModelAnnotation(OVERRIDE);

	/** The Constant ENTITY_MANAGER_FIELD. */
	private static final ModelField ENTITY_MANAGER_FIELD = getEntityManagerField();
	
	/** The Constant JDBC_TEMPLATE_FIELD. */
	private static final ModelField JDBC_TEMPLATE_FIELD = getJdbcTemplate();

	/** The Constant ENTITY_MANAGER_METHOD. */
	private static final ModelMethod ENTITY_MANAGER_METHOD = returnMethodService("getEntityManager", ENTITY_MANAGER, SPACE+"return this." + ENTITY_MANAGER_FIELD.getName() + ";");
	
	/** The Constant JDBC_TEMPLATE_METHOD. */
	private static final ModelMethod JDBC_TEMPLATE_METHOD = returnMethodService("getJdbcTemplate", NAMED_PARAMETER_JDBC_TEMPLATE, SPACE+"return this." + JDBC_TEMPLATE_FIELD.getName() + ";");
	
	

	
	

	/**
	 * Generate class.
	 *
	 * @param modelClasses the model classes
	 * @param classEntity  the class entity
	 * @param path         the path
	 * @throws Exception the exception
	 */
	public static void generateClass(ModelClasses modelClasses, Class<?> classEntity, String path,String servicePackage,String repositoryPackage) throws Exception {
		ModelClass interfaceRepository = new ModelClass();
		ModelClass interfaceService = new ModelClass();
		ModelClass classService = new ModelClass();
		String className = classEntity.getSimpleName();
		if(StringUtils.isEmpty(repositoryPackage))
			repositoryPackage = classEntity.getName().replace("." + className, "");
	
		//String packageService = packageName.substring(0, packageName.lastIndexOf(".") + 1) + SERVICE.toLowerCase();

		logger.info("Class building: "+classEntity.getName()+REPOSITORY);
		logger.info("Class building: "+servicePackage+"."+className+SERVICE);
		logger.info("Class building: "+servicePackage+"."+className+SERVICE_IMPL);

		interfaceRepository.getImports().add(classEntity.getName());
		interfaceService.getImports().add(classEntity.getName());
		classService.getImports().add(classEntity.getName());
		//classService.getImports().add("java.util.HashMap");
		classService.getImports().add(repositoryPackage+"."+className + REPOSITORY);

		interfaceRepository.setName(className + REPOSITORY);
		interfaceRepository.setPackageName(repositoryPackage);
		interfaceRepository.setType(ClassType.INTERFACE);
		interfaceRepository.getAnnotations().add(ANNOTATION_REPOSITORY);

		ModelGenericType genericTypeEntityClass = new ModelGenericType();
		genericTypeEntityClass.setName(className);

		Set<Field> listField = ReflectionUtils.getListField(classEntity);
		ModelGenericType genericTypeId = new ModelGenericType();
		
		for (Field field : listField) {

			if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class)) {
				Class<?> classType = field.getType();
				if (ReflectionUtils.mapPrimitiveToObject.containsKey(classType))
					classType = ReflectionUtils.mapPrimitiveToObject.get(classType);
				genericTypeId.setName(classType.getSimpleName());
				if (!classType.getName().startsWith("java.lang")) {
					interfaceRepository.getImports().add(classType.getName());
					interfaceService.getImports().add(classType.getName());
					classService.getImports().add(classType.getName());
				}
			}

		}

		ModelSuperClass superClassBaseJpaRepository = getPersistenceGenericType(genericTypeEntityClass, genericTypeId,
				BASE_JPA_REPOSITORY);

		interfaceRepository.getExtendsClass().add(superClassBaseJpaRepository);
		modelClasses.getClasses().add(interfaceRepository);

		interfaceService.setName(className + SERVICE);
		interfaceService.setType(ClassType.INTERFACE);
		interfaceService.setPackageName(servicePackage);
		ModelSuperClass superClassJpaService = getPersistenceGenericType(genericTypeEntityClass, genericTypeId,
				"JpaService");
		interfaceService.getExtendsClass().add(superClassJpaService);

		modelClasses.getClasses().add(interfaceService);
		ModelSuperClass interfaceServiceSuperClass = new ModelSuperClass();
		interfaceServiceSuperClass.setName(interfaceService.getName());
		classService.getImplementsClass().add(interfaceServiceSuperClass);

		classService.getExtendsClass()
				.add(getPersistenceGenericType(genericTypeEntityClass, genericTypeId, "JpaServiceImpl"));
		classService.setName(className + SERVICE_IMPL);
		classService.setPackageName(servicePackage);
		classService.getAnnotations().add(ANNOTATION_SERVICE);
		classService.getAnnotations().add(ANNOTATION_TRANSACTIONAL);
		classService.getAnnotations().add(ANNOTATION_QUERY_BUILDER);
		ModelField repositoryField = new ModelField();
		String typeRepoField = className + REPOSITORY;
		String repoField = Character.toLowerCase(typeRepoField.charAt(0)) + typeRepoField.substring(1);
		repositoryField.setType(typeRepoField);
		repositoryField.setName(repoField);
		repositoryField.setGetterSetter(false);
		repositoryField.getAnnotations().add(ANNOTATION_AUTOWIRED);
		classService.getFields().add(repositoryField);

		classService.getFields().add(ENTITY_MANAGER_FIELD);
		classService.getFields().add(JDBC_TEMPLATE_FIELD);

		ModelMethod jpaRepositoryMethod = new ModelMethod();
		jpaRepositoryMethod.setName("getJpaRepository");
		jpaRepositoryMethod.setLevelType(LevelType.PROTECTED);
		jpaRepositoryMethod.getAnnotations().add(ANNOTATION_OVERRIDE);
		jpaRepositoryMethod.setType("JpaRepository");
		jpaRepositoryMethod.getGenericTypes().add(genericTypeEntityClass);
		jpaRepositoryMethod.getGenericTypes().add(genericTypeId);
		jpaRepositoryMethod.getCommands().add(SPACE+"return this." + repoField + ";");
		classService.getMethods().add(jpaRepositoryMethod);
		classService.getMethods().add(ENTITY_MANAGER_METHOD);
		classService.getMethods().add(JDBC_TEMPLATE_METHOD);
		
		modelClasses.getClasses().add(classService);
		

	}




	

	/**
	 * Gets the persistence generic type.
	 *
	 * @param genericTypeEntityClass the generic type entity class
	 * @param genericTypeId          the generic type id
	 * @param name                   the name
	 * @return the persistence generic type
	 */
	private static ModelSuperClass getPersistenceGenericType(ModelGenericType genericTypeEntityClass,
			ModelGenericType genericTypeId, String name) {
		ModelSuperClass modelSuperClass = new ModelSuperClass();
		modelSuperClass.setName(name);
		modelSuperClass.getGenericTypes().add(genericTypeEntityClass);
		modelSuperClass.getGenericTypes().add(genericTypeId);
		return modelSuperClass;
	}

	/**
	 * Gets the model annotation.
	 *
	 * @param annotationName the annotation name
	 * @return the model annotation
	 */
	private static ModelAnnotation getModelAnnotation(String annotationName) {
		ModelAnnotation modelAnnotation = new ModelAnnotation();
		modelAnnotation.setName(annotationName);
		return modelAnnotation;
	}

	/**
	 * Gets the entity manager field.
	 *
	 * @return the entity manager field
	 */
	private static ModelField getEntityManagerField() {
		ModelField entityManagerField = new ModelField();
		entityManagerField.setGetterSetter(false);
		entityManagerField.setType(ENTITY_MANAGER);
		entityManagerField.setName(Character.toLowerCase(ENTITY_MANAGER.charAt(0)) + ENTITY_MANAGER.substring(1));
		ModelAnnotation persistenceContextAnnotation = new ModelAnnotation();
		persistenceContextAnnotation.setName("PersistenceContext");
		entityManagerField.getAnnotations().add(persistenceContextAnnotation);
		return entityManagerField;
	}

	/**
	 * Gets the jdbc template.
	 *
	 * @return the jdbc template
	 */
	private static ModelField getJdbcTemplate() {
		ModelField entityManagerField = new ModelField();
		entityManagerField.setGetterSetter(false);
		entityManagerField.setType(NAMED_PARAMETER_JDBC_TEMPLATE);
		entityManagerField.setName(JDBC_TEMPLATE);
		entityManagerField.getAnnotations().add(ANNOTATION_AUTOWIRED);
		return entityManagerField;
	}


	
	
	/**
	 * Return method service.
	 *
	 * @param name    the name
	 * @param type    the type
	 * @param command the command
	 * @return the model method
	 */
	private static ModelMethod returnMethodService(String name,String type,String command) {
		ModelMethod modelMethod = new ModelMethod(name,type);
		modelMethod.getAnnotations().add(ANNOTATION_OVERRIDE);
		modelMethod.setLevelType(LevelType.PROTECTED);
		modelMethod.getCommands().add(command);
		return modelMethod;
	}
	
	


	
}
