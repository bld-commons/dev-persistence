package bld.plugin.jpa.service.generator.classes;

import java.lang.reflect.Field;
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

import bld.commons.persistence.reflection.utils.ReflectionUtils;
import bld.commons.yaml.costant.ClassType;
import bld.commons.yaml.costant.LevelType;
import bld.commons.yaml.model.ModelAnnotation;
import bld.commons.yaml.model.ModelClass;
import bld.commons.yaml.model.ModelClasses;
import bld.commons.yaml.model.ModelField;
import bld.commons.yaml.model.ModelGenericType;
import bld.commons.yaml.model.ModelMethod;
import bld.commons.yaml.model.ModelSuperClass;

public class Generator {

	private static final String SPACE="        ";
	private static final String OVERRIDE = "Override";
	private static final String FROM_BY_FILTER = "FROM_BY_FILTER";
	private static final String COUNT_BY_FILTER = "COUNT_BY_FILTER";
	private static final String SELECT_BY_FILTER = "SELECT_BY_FILTER";
	private static final String STRING = "String";
	private static final String NAMED_PARAMETER_JDBC_TEMPLATE = "NamedParameterJdbcTemplate";
	private static final String JDBC_TEMPLATE = "jdbcTemplate";
	private static final String AUTOWIRED = "Autowired";
	private static final String TRANSACTIONAL = "Transactional";
	private static final String ENTITY_MANAGER = "EntityManager";
	private final static Log logger = LogFactory.getLog(Generator.class);
	private static final String SERVICE = "Service";
	private static final String REPOSITORY = "Repository";
	private static final String SERVICE_IMPL = SERVICE + "Impl";

	private static final ModelAnnotation ANNOTATION_REPOSITORY = getModelAnnotation(REPOSITORY);
	private static final ModelAnnotation ANNOTATION_SERVICE = getModelAnnotation(SERVICE);
	private static final ModelAnnotation ANNOTATION_TRANSACTIONAL = getModelAnnotation(TRANSACTIONAL);
	private static final ModelAnnotation ANNOTATION_AUTOWIRED = getModelAnnotation(AUTOWIRED);
	private static final ModelAnnotation ANNOTATION_OVERRIDE = new ModelAnnotation(OVERRIDE);

	private static final ModelField ENTITY_MANAGER_FIELD = getEntityManagerField();
	private static final ModelField JDBC_TEMPLATE_FIELD = getJdbcTemplate();

	private static final ModelMethod ENTITY_MANAGER_METHOD = returnMethodService("getEntityManager", ENTITY_MANAGER, SPACE+"return " + ENTITY_MANAGER_FIELD.getName() + ";");
	private static final ModelMethod JDBC_TEMPLATE_METHOD = returnMethodService("getJdbcTemplate", NAMED_PARAMETER_JDBC_TEMPLATE, SPACE+"return " + JDBC_TEMPLATE_FIELD.getName() + ";");
	private static final ModelMethod SELECT_BY_FILTER_METHOD=returnMethodService("selectByFilter", STRING, SPACE+"return "+SELECT_BY_FILTER+";");
	private static final ModelMethod COUNT_BY_FILTER_METHOD=returnMethodService("countByFilter", STRING, SPACE+"return "+COUNT_BY_FILTER+";");
	private static final ModelField MAP_CONDITIONS_FIELD=getFieldMapConditions();
	

	public static void generateClass(ModelClasses modelClasses, Class<?> classEntity, String path) throws Exception {
		ModelClass interfaceRepository = new ModelClass();
		ModelClass interfaceService = new ModelClass();
		ModelClass classService = new ModelClass();
		String className = classEntity.getSimpleName();
		String packageName = classEntity.getName().replace("." + className, "");
		String packageService = packageName.substring(0, packageName.lastIndexOf(".") + 1) + SERVICE.toLowerCase();

		logger.info(packageName);
		logger.info(packageService);

		interfaceRepository.getImports().add(classEntity.getName());
		interfaceService.getImports().add(classEntity.getName());
		classService.getImports().add(classEntity.getName());
		classService.getImports().add("java.util.HashMap");
		classService.getImports().add(classEntity.getName() + REPOSITORY);
		classService.setShowCostruct(false);

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

		String countByFilter = "\"select distinct count(" + fieldEntity + ")\"";

		String fromByFilter = " From " + classEntity.getSimpleName() + " " + fieldEntity;

		List<String> mapConditions = new ArrayList<>();
		List<String> mapOneToMany=new ArrayList<>();
		mapConditions.add(SPACE+"Map<String,String> map=new HashMap<>();");

		for (Field field : listField) {

			if (field.isAnnotationPresent(Id.class) || field.isAnnotationPresent(EmbeddedId.class)) {
				Class<?> classType = field.getType();
				logger.info("ID name: " + field.getName() + " --- type: " + classType.getSimpleName());
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
							mapConditions.add(SPACE+"map.put(\"" + fieldPk.getName()+"\", \" and "+fieldEntity+".id."+fieldPk.getName()+" in (:"+fieldPk.getName()+") \");");
				}else {
					mapConditions.add(SPACE+"map.put(\"" + field.getName()+"\", \" and "+fieldEntity+"."+field.getName()+" in (:"+field.getName()+") \");");
					mapConditions.add(SPACE+"map.put(\"id\", \" and "+fieldEntity+"."+field.getName()+" in (:"+field.getName()+") \");");
				}
				
			}else if (field.isAnnotationPresent(Column.class)) {
				if(Calendar.class.isAssignableFrom(field.getType()) || Date.class.isAssignableFrom(field.getType())) {
					mapConditions.add(SPACE+"map.put(\"" + field.getName()+"Before\", \" and "+fieldEntity+"."+field.getName()+">= :"+field.getName()+"Before \");");
					mapConditions.add(SPACE+"map.put(\"" + field.getName()+"After\", \" and "+fieldEntity+"."+field.getName()+"<= :"+field.getName()+"After \");");
				}else if(String.class.isAssignableFrom(field.getType())){
					mapConditions.add(SPACE+"map.put(\"" + field.getName()+"\", \" and "+fieldEntity+"."+field.getName()+" like :"+field.getName()+" \");");
				}else if(Boolean.class.isAssignableFrom(field.getType())){
					mapConditions.add(SPACE+"map.put(\"" + field.getName()+"\", \" and "+fieldEntity+"."+field.getName()+"= :"+field.getName()+" \");");
				}else {
					mapConditions.add(SPACE+"map.put(\"" + field.getName()+"\", \" and "+fieldEntity+"."+field.getName()+" in (:"+field.getName()+") \");");
				}
				
			}else if (field.isAnnotationPresent(ManyToOne.class)) {
				JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
				fromByFilter += (joinColumn.nullable()?" left":"")+" join fetch " + fieldEntity + "." + field.getName() + " " + field.getName();
				Set<Field>listFieldReference=ReflectionUtils.getListField(field.getType());
				for (Field fieldReference : listFieldReference) {
					if(fieldReference.isAnnotationPresent(Id.class) || fieldReference.isAnnotationPresent(EmbeddedId.class)) {
						mapConditions.add(SPACE+"map.put(\"" + fieldReference.getName()+"\", \" and "+field.getName()+"."+fieldReference.getName()+" in (:"+fieldReference.getName()+") \");");
						break;
					}
				}
			}else if (field.isAnnotationPresent(OneToMany.class)) {
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
		fromByFilter+=" where 1=1 ";
		ModelField fromByFilterField = finalStaticField(FROM_BY_FILTER, STRING, fromByFilter,true);
		ModelField countByFilterField = finalStaticField(COUNT_BY_FILTER, STRING, countByFilter + "+" + FROM_BY_FILTER,false);
		ModelField selectByFilterField = finalStaticField(SELECT_BY_FILTER, STRING,
				selectByFilter + "+" + FROM_BY_FILTER,false);

		ModelSuperClass superClassBaseJpaRepository = getPersistenceGenericType(genericTypeEntityClass, genericTypeId,
				"BaseJpaRepository");

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
		
		
		classService.getFields().add(fromByFilterField);
		classService.getFields().add(selectByFilterField);
		classService.getFields().add(countByFilterField);

		ModelMethod jpaRepositoryMethod = new ModelMethod();
		jpaRepositoryMethod.setName("getJpaRepository");
		jpaRepositoryMethod.setLevelType(LevelType.PROTECTED);
		jpaRepositoryMethod.getAnnotations().add(ANNOTATION_OVERRIDE);
		jpaRepositoryMethod.setType("JpaRepository");
		jpaRepositoryMethod.getGenericTypes().add(genericTypeEntityClass);
		jpaRepositoryMethod.getGenericTypes().add(genericTypeId);
		jpaRepositoryMethod.getCommands().add(SPACE+"return " + repoField + ";");
		classService.getMethods().add(jpaRepositoryMethod);
		classService.getMethods().add(ENTITY_MANAGER_METHOD);
		classService.getMethods().add(JDBC_TEMPLATE_METHOD);
		classService.getMethods().add(SELECT_BY_FILTER_METHOD);
		classService.getMethods().add(COUNT_BY_FILTER_METHOD);
		
		ModelMethod staticMapConditions=getMapConditions();
		mapConditions.add(SPACE+"return map;");
		staticMapConditions.setCommands(mapConditions);
		classService.getMethods().add(staticMapConditions);
		
		
		
		ModelMethod mapConditionsMethod=new ModelMethod();
		mapConditionsMethod.setName("mapConditions");
		mapConditionsMethod.setType("Map");
		mapConditionsMethod.getGenericTypes().add(new ModelGenericType("String"));
		mapConditionsMethod.getGenericTypes().add(new ModelGenericType("String"));
		mapConditionsMethod.setLevelType(LevelType.PROTECTED);
		mapConditionsMethod.getAnnotations().add(ANNOTATION_OVERRIDE);
		
		mapConditionsMethod.getCommands().add(SPACE+"return MAP_CONDITIONS;");
		
		classService.getMethods().add(mapConditionsMethod);
		
		
		ModelMethod mapOneToManyMethod=new ModelMethod("mapOneToMany", "void");
		mapOneToManyMethod.setLevelType(LevelType.PROTECTED);
		mapOneToManyMethod.getAnnotations().add(ANNOTATION_OVERRIDE);
		mapOneToManyMethod.setCommands(mapOneToMany);
		
		classService.getMethods().add(mapOneToManyMethod);
		
		modelClasses.getClasses().add(classService);
		

	}

	

	private static ModelSuperClass getPersistenceGenericType(ModelGenericType genericTypeEntityClass,
			ModelGenericType genericTypeId, String name) {
		ModelSuperClass modelSuperClass = new ModelSuperClass();
		modelSuperClass.setName(name);
		modelSuperClass.getGenericTypes().add(genericTypeEntityClass);
		modelSuperClass.getGenericTypes().add(genericTypeId);
		return modelSuperClass;
	}

	private static ModelAnnotation getModelAnnotation(String annotationName) {
		ModelAnnotation modelAnnotation = new ModelAnnotation();
		modelAnnotation.setName(annotationName);
		return modelAnnotation;
	}

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

	private static ModelField getJdbcTemplate() {
		ModelField entityManagerField = new ModelField();
		entityManagerField.setGetterSetter(false);
		entityManagerField.setType(NAMED_PARAMETER_JDBC_TEMPLATE);
		entityManagerField.setName(JDBC_TEMPLATE);
		entityManagerField.getAnnotations().add(ANNOTATION_AUTOWIRED);
		return entityManagerField;
	}


	
	
	private static ModelMethod returnMethodService(String name,String type,String command) {
		ModelMethod modelMethod = new ModelMethod(name,type);
		modelMethod.getAnnotations().add(ANNOTATION_OVERRIDE);
		modelMethod.setLevelType(LevelType.PROTECTED);
		modelMethod.getCommands().add(command);
		return modelMethod;
	}
	
	

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
	
	private static ModelField getFieldMapConditions() {
		ModelField mapConditions=finalStaticField("MAP_CONDITIONS", "Map", "getMapConditions()", false);
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		return mapConditions;
	}
	
	
	private static ModelMethod getMapConditions() {
		ModelMethod mapConditions=new ModelMethod("getMapConditions", "Map");
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		mapConditions.setStaticMethod(true);
		mapConditions.setLevelType(LevelType.PRIVATE);
		return mapConditions;
	}

	
	
}
