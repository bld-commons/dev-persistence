/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.plugin.jpa.service.generator.classes.ClassBuilding.java
 */
package bld.plugin.jpa.service.generator.classes;

import java.lang.reflect.Field;
import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

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

	/** The Constant SPACE. */
	private static final String SPACE="        ";
	
	/** The Constant OVERRIDE. */
	private static final String OVERRIDE = "Override";
	
	/** The Constant FROM_BY_FILTER. */
	private static final String FROM_BY_FILTER = "FROM_BY_FILTER";
	
	/** The Constant COUNT_BY_FILTER. */
	private static final String COUNT_BY_FILTER = "COUNT_BY_FILTER";
	
	/** The Constant SELECT_BY_FILTER. */
	private static final String SELECT_BY_FILTER = "SELECT_BY_FILTER";
	
	private static final String DELETE_BY_FILTER= "DELETE_BY_FILTER";
	
	/** The Constant STRING. */
	private static final String STRING = "String";
	
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
	
	/** The Constant SELECT_BY_FILTER_METHOD. */
	private static final ModelMethod SELECT_BY_FILTER_METHOD=returnMethodService("selectByFilter", STRING, SPACE+"return "+SELECT_BY_FILTER+";");
	
	/** The Constant COUNT_BY_FILTER_METHOD. */
	private static final ModelMethod COUNT_BY_FILTER_METHOD=returnMethodService("countByFilter", STRING, SPACE+"return "+COUNT_BY_FILTER+";");
	
	private static final ModelMethod DELETE_BY_FILTER_METHOD=returnMethodService("deleteByFilter", STRING, SPACE+"return "+DELETE_BY_FILTER+";");
	
	/** The Constant MAP_CONDITIONS_FIELD. */
	private static final ModelField MAP_CONDITIONS_FIELD=getFieldMapConditions("MAP_CONDITIONS","getMapConditions()");
	
	private static final ModelMethod MAP_CONDITIONS_METHOD=getConditions("mapConditions","return MAP_CONDITIONS;");
	
	private static final ModelMethod MAP_DELETE_CONDITIONS_METHOD=getConditions("mapDeleteConditions","return MAP_DELETE_CONDITIONS;");
	
	private static final ModelField MAP_DELETE_CONDITIONS_FIELD=getFieldMapConditions("MAP_DELETE_CONDITIONS","getMapDeleteConditions()");
	

	/**
	 * Generate class.
	 *
	 * @param modelClasses the model classes
	 * @param classEntity  the class entity
	 * @param path         the path
	 * @throws Exception the exception
	 */
	public static void generateClass(ModelClasses modelClasses, Class<?> classEntity, String path) throws Exception {
		ModelClass interfaceRepository = new ModelClass();
		ModelClass interfaceService = new ModelClass();
		ModelClass classService = new ModelClass();
		String className = classEntity.getSimpleName();
		String packageName = classEntity.getName().replace("." + className, "");
		String packageService = packageName.substring(0, packageName.lastIndexOf(".") + 1) + SERVICE.toLowerCase();

		logger.info("Class building: "+classEntity.getName()+REPOSITORY);
		logger.info("Class building: "+packageService+"."+className+SERVICE);
		logger.info("Class building: "+packageService+"."+className+SERVICE_IMPL);

		interfaceRepository.getImports().add(classEntity.getName());
		interfaceService.getImports().add(classEntity.getName());
		classService.getImports().add(classEntity.getName());
		classService.getImports().add("java.util.HashMap");
		classService.getImports().add(classEntity.getName() + REPOSITORY);

		interfaceRepository.setName(className + REPOSITORY);
		interfaceRepository.setPackageName(packageName);
		interfaceRepository.setType(ClassType.INTERFACE);
		interfaceRepository.getAnnotations().add(ANNOTATION_REPOSITORY);

		ModelGenericType genericTypeEntityClass = new ModelGenericType();
		genericTypeEntityClass.setName(className);

		Set<Field> listField = ReflectionUtils.getListField(classEntity);
		ModelGenericType genericTypeId = new ModelGenericType();
		String fieldEntity = Character.toLowerCase(classEntity.getSimpleName().charAt(0))
				+ classEntity.getSimpleName().substring(1);

		String selectByFilter = "\"select distinct " + fieldEntity + "\"";

		String deleteByFilter = "\"delete from " + classEntity.getSimpleName()+ " " + fieldEntity + " where 1=1 \"";
		
		String countByFilter = "\"select distinct count(" + fieldEntity + ")\"";

		String fromByFilter = " From " + classEntity.getSimpleName() + " " + fieldEntity;

		List<String> mapBaseConditions = new ArrayList<>();
		List<String> mapConditions = new ArrayList<>();
		List<String> mapDeleteConditions = new ArrayList<>();
		List<String> mapOneToMany=new ArrayList<>();
		mapBaseConditions.add(SPACE+"Map<String,String> map=new HashMap<>();");
		mapDeleteConditions.add(SPACE+"Map<String,String> map=getMapBaseConditions();");
		mapConditions.add(SPACE+"Map<String,String> map=getMapBaseConditions();");
		
		boolean checkOneToMany=false;
		
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
				if(field.isAnnotationPresent(EmbeddedId.class)) {
					Set<Field>listFieldPk=ReflectionUtils.getListField(field.getType());
					for(Field fieldPk:listFieldPk) 
						if(fieldPk.isAnnotationPresent(Column.class))
							mapBaseConditions.add(SPACE+"map.put(\"" + fieldPk.getName()+"\", \" and "+fieldEntity+".id."+fieldPk.getName()+" in (:"+fieldPk.getName()+") \");");
				}else {
					mapBaseConditions.add(SPACE+"map.put(\"" + field.getName()+"\", \" and "+fieldEntity+"."+field.getName()+" in (:"+field.getName()+") \");");
					mapBaseConditions.add(SPACE+"map.put(\"id\", \" and "+fieldEntity+"."+field.getName()+" in (:id) \");");
				}
				
			}else if (field.isAnnotationPresent(Column.class)) {
				if(Calendar.class.isAssignableFrom(field.getType()) || Date.class.isAssignableFrom(field.getType()) || Timestamp.class.isAssignableFrom(field.getType())) {
					mapBaseConditions.add(SPACE+"map.put(\"" + field.getName()+"From\", \" and :"+field.getName()+"From<="+fieldEntity+"."+field.getName()+" \");");
					mapBaseConditions.add(SPACE+"map.put(\"" + field.getName()+"To\", \" and "+fieldEntity+"."+field.getName()+"<=:"+field.getName()+"To \");");
					mapBaseConditions.add(SPACE+"map.put(\"" + field.getName()+"\", \" and "+fieldEntity+"."+field.getName()+"=:"+field.getName()+" \");");
				}else if(String.class.isAssignableFrom(field.getType())){
					mapBaseConditions.add(SPACE+"map.put(\"" + field.getName()+"\", \" and <upper>("+fieldEntity+"."+field.getName()+") like :"+field.getName()+" \");");
				}else if(Boolean.class.isAssignableFrom(field.getType())){
					mapBaseConditions.add(SPACE+"map.put(\"" + field.getName()+"\", \" and "+fieldEntity+"."+field.getName()+"= :"+field.getName()+" \");");
				}else {
					mapBaseConditions.add(SPACE+"map.put(\"" + field.getName()+"\", \" and "+fieldEntity+"."+field.getName()+" in (:"+field.getName()+") \");");
				}
				
			}else if (field.isAnnotationPresent(ManyToOne.class)) {
				JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
				fromByFilter += (joinColumn.nullable()?" left":"")+" join fetch " + fieldEntity + "." + field.getName() + " " + field.getName();
				Set<Field>listFieldReference=ReflectionUtils.getListField(field.getType());
				for (Field fieldReference : listFieldReference) {
					if(fieldReference.isAnnotationPresent(Id.class) || fieldReference.isAnnotationPresent(EmbeddedId.class)) {
						mapConditions.add(SPACE+"map.put(\"" + fieldReference.getName()+"\", \" and "+field.getName()+"."+fieldReference.getName()+" in (:"+fieldReference.getName()+") \");");
						mapDeleteConditions.add(SPACE+"map.put(\"" + fieldReference.getName()+"\", \" and "+fieldEntity+"."+field.getName()+"."+fieldReference.getName()+" in (:"+fieldReference.getName()+") \");");
						break;
					}
				}
			}else if (field.isAnnotationPresent(OneToMany.class)) {
				checkOneToMany=true;
				OneToMany oneToMany=field.getAnnotation(OneToMany.class);
				Class<?>classReferenceField=ReflectionUtils.getGenericTypeField(field);
				Map<String,Field>mapField=ReflectionUtils.getMapField(classReferenceField);
				Field fieldReference = mapField.get(oneToMany.mappedBy());
				
				
				
				
				Set<Field>listReferenceField=ReflectionUtils.getListField(classReferenceField);
				for(Field fieldOneToMany:listReferenceField) {
					if(fieldOneToMany.isAnnotationPresent(Id.class)  && fieldReference.isAnnotationPresent(JoinColumn.class)) {
						JoinColumn joinColumn=fieldReference.getAnnotation(JoinColumn.class);
						mapOneToMany.add(SPACE+"addJoinOneToMany(\""+fieldOneToMany.getName()+"\", \" "+(joinColumn.nullable()?"left":"")+" join fetch "+fieldEntity+"."+field.getName()+" "+field.getName()+" \");");
						mapConditions.add(SPACE+"map.put(\"" + fieldOneToMany.getName()+"\", \" and "+field.getName()+"."+fieldOneToMany.getName()+" in (:"+field.getName()+") \");");
					}
				}
				
				
			}

		}
		fromByFilter+=(checkOneToMany?" \"+ONE_TO_MANY+\" where 1=1 ":" where 1=1 ");
		ModelField fromByFilterField = finalStaticField(FROM_BY_FILTER, STRING, fromByFilter,true);
		ModelField countByFilterField = finalStaticField(COUNT_BY_FILTER, STRING, countByFilter + "+" + FROM_BY_FILTER,false);
		ModelField selectByFilterField = finalStaticField(SELECT_BY_FILTER, STRING,	selectByFilter + "+" + FROM_BY_FILTER,false);
		ModelField deletetByFilterField = finalStaticField(DELETE_BY_FILTER, STRING,	deleteByFilter,false);

		ModelSuperClass superClassBaseJpaRepository = getPersistenceGenericType(genericTypeEntityClass, genericTypeId,
				BASE_JPA_REPOSITORY);

		interfaceRepository.getExtendsClass().add(superClassBaseJpaRepository);
		modelClasses.getClasses().add(interfaceRepository);

		interfaceService.setName(className + SERVICE);
		interfaceService.setType(ClassType.INTERFACE);
		interfaceService.setPackageName(packageService);
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
		classService.setPackageName(packageService);
		classService.getAnnotations().add(ANNOTATION_SERVICE);
		classService.getAnnotations().add(ANNOTATION_TRANSACTIONAL);
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
		classService.getFields().add(MAP_CONDITIONS_FIELD);
		classService.getFields().add(MAP_DELETE_CONDITIONS_FIELD);
		
		classService.getFields().add(fromByFilterField);
		classService.getFields().add(selectByFilterField);
		classService.getFields().add(countByFilterField);
		classService.getFields().add(deletetByFilterField);

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
		classService.getMethods().add(SELECT_BY_FILTER_METHOD);
		classService.getMethods().add(COUNT_BY_FILTER_METHOD);
		classService.getMethods().add(DELETE_BY_FILTER_METHOD);
		
		mapConditions.add(SPACE+"return map;");
		mapBaseConditions.add(SPACE+"return map;");
		mapDeleteConditions.add(SPACE+"return map;");
		ModelMethod staticMapConditions=getMapConditions("getMapConditions",mapConditions);
		ModelMethod staticMapBaseConditions=getMapConditions("getMapBaseConditions",mapBaseConditions);
		ModelMethod staticMapDeleteConditions=getMapConditions("getMapDeleteConditions",mapDeleteConditions);
		
		classService.getMethods().add(staticMapConditions);
		classService.getMethods().add(staticMapBaseConditions);
		classService.getMethods().add(staticMapDeleteConditions);
		
		
		ModelMethod mapConditionsMethod = MAP_CONDITIONS_METHOD;
		ModelMethod mapDeleteConditionsMethod = MAP_DELETE_CONDITIONS_METHOD;
		classService.getMethods().add(mapConditionsMethod);
		classService.getMethods().add(mapDeleteConditionsMethod);
		
		ModelMethod mapOneToManyMethod=new ModelMethod("mapOneToMany", "void");
		mapOneToManyMethod.setLevelType(LevelType.PROTECTED);
		mapOneToManyMethod.getAnnotations().add(ANNOTATION_OVERRIDE);
		mapOneToManyMethod.setCommands(mapOneToMany);
		
		classService.getMethods().add(mapOneToManyMethod);
		
		modelClasses.getClasses().add(classService);
		

	}



	private static ModelMethod getConditions(String name,String command) {
		ModelMethod mapConditionsMethod=new ModelMethod();
		mapConditionsMethod.setName(name);
		mapConditionsMethod.setType("Map");
		mapConditionsMethod.getGenericTypes().add(new ModelGenericType("String"));
		mapConditionsMethod.getGenericTypes().add(new ModelGenericType("String"));
		mapConditionsMethod.setLevelType(LevelType.PROTECTED);
		mapConditionsMethod.getAnnotations().add(ANNOTATION_OVERRIDE);
		
		mapConditionsMethod.getCommands().add(SPACE+command);
		return mapConditionsMethod;
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
	
	

	/**
	 * Final static field.
	 *
	 * @param name       the name
	 * @param type       the type
	 * @param value      the value
	 * @param showQuotes the show quotes
	 * @return the model field
	 */
	private static ModelField finalStaticField(String name, String type, Object value,boolean showQuotes) {
		ModelField modelField = new ModelField();
		modelField.setType(type);
		modelField.setFieldStatic(true);
		modelField.setFieldFinal(true);
		modelField.setName(name);
		modelField.setValue(value);
		modelField.setGetterSetter(false);
		modelField.setShowQuotes(showQuotes);
		return modelField;
	}
	
	/**
	 * Gets the field map conditions.
	 *
	 * @return the field map conditions
	 */
	private static ModelField getFieldMapConditions(String name,String value) {
		ModelField mapConditions=finalStaticField(name, "Map", value, false);
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		return mapConditions;
	}
	
	
	/**
	 * Gets the map conditions.
	 * @param commands 
	 *
	 * @return the map conditions
	 */
	private static ModelMethod getMapConditions(String name,List<String> commands) {
		ModelMethod mapConditions=new ModelMethod(name, "Map");
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		mapConditions.setStaticMethod(true);
		mapConditions.setLevelType(LevelType.PRIVATE);
		mapConditions.setCommands(commands);
		return mapConditions;
	}

	
	
}
