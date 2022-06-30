/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.plugin.jpa.service.generator.classes.ClassBuilding.java
 */
package com.bld.processor.build;

import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.tools.Diagnostic.Kind;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.bld.processor.data.ClassField;
import com.bld.processor.data.QueryDetail;
import com.bld.processor.exception.ProcessorJpaServiceException;

import bld.commons.classes.attributes.ClassType;
import bld.commons.classes.attributes.LevelType;
import bld.commons.classes.attributes.PrimitiveType;
import bld.commons.classes.model.ModelAnnotation;
import bld.commons.classes.model.ModelClass;
import bld.commons.classes.model.ModelClasses;
import bld.commons.classes.model.ModelField;
import bld.commons.classes.model.ModelGenericType;
import bld.commons.classes.model.ModelMethod;
import bld.commons.classes.model.ModelSuperClass;
import bld.commons.processor.ConditionType;
import bld.commons.processor.annotations.ConditionBuilder;
import bld.commons.processor.annotations.CustomConditionBuilder;
import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.reflection.utils.ReflectionCommons;
import bld.commons.service.BaseJpaService;

/**
 * The Class ClassBuilding.
 */
public class ClassBuilding {

	/** The Constant JOINS. */
	private static final String JOINS = "joins";

	/** The Constant CONDITIONS. */
	private static final String CONDITIONS = "conditions";

	/** The Constant SPACE. */
	private static final String SPACE = "        ";

	/** The Constant OVERRIDE. */
	private static final String OVERRIDE = "Override";

	/** The Constant FROM_BY_FILTER. */
	private static final String FROM_BY_FILTER = "FROM_BY_FILTER";

	/** The Constant COUNT_BY_FILTER. */
	private static final String COUNT_BY_FILTER = "COUNT_BY_FILTER";

	/** The Constant SELECT_BY_FILTER. */
	private static final String SELECT_BY_FILTER = "SELECT_BY_FILTER";

	/** The Constant DELETE_BY_FILTER. */
	private static final String DELETE_BY_FILTER = "DELETE_BY_FILTER";

	/** The Constant STRING. */
	private static final String STRING = "String";

	/** The Constant COMPONENT. */
	private static final String COMPONENT = "Component";

	/** The Constant QUERY_JPQL. */
	private static final String QUERY_JPQL = "QueryJpql";

	/** The Constant QUERY_JPQL_IMPL. */
	private static final String QUERY_JPQL_IMPL = QUERY_JPQL + "Impl";

	/** The Constant ANNOTATION_COMPONENT. */
	private static final ModelAnnotation ANNOTATION_COMPONENT = getModelAnnotation(COMPONENT);

	/** The Constant ANNOTATION_OVERRIDE. */
	private static final ModelAnnotation ANNOTATION_OVERRIDE = new ModelAnnotation(OVERRIDE);

	/** The Constant SELECT_BY_FILTER_METHOD. */
	private static final ModelMethod SELECT_BY_FILTER_METHOD = returnMethodService("selectByFilter", STRING, SPACE + "return " + SELECT_BY_FILTER + ";", LevelType.PUBLIC);

	/** The Constant COUNT_BY_FILTER_METHOD. */
	private static final ModelMethod COUNT_BY_FILTER_METHOD = returnMethodService("countByFilter", STRING, SPACE + "return " + COUNT_BY_FILTER + ";", LevelType.PUBLIC);

	/** The Constant DELETE_BY_FILTER_METHOD. */
	private static final ModelMethod DELETE_BY_FILTER_METHOD = returnMethodService("deleteByFilter", STRING, SPACE + "return " + DELETE_BY_FILTER + ";", LevelType.PUBLIC);

	/** The Constant MAP_CONDITIONS_FIELD. */
	private static final ModelField MAP_CONDITIONS_FIELD = getFieldMapConditions("MAP_CONDITIONS", "getMapConditions()");

	/** The Constant MAP_NATIVE_CONDITIONS_FIELD. */
	private static final ModelField MAP_NATIVE_CONDITIONS_FIELD = getFieldMapConditions("MAP_NATIVE_CONDITIONS", "getMapNativeConditions()");

	/** The Constant MAP_CONDITIONS_METHOD. */
	private static final ModelMethod MAP_CONDITIONS_METHOD = getConditions("mapConditions", "return MAP_CONDITIONS;");

	/** The Constant MAP_DELETE_CONDITIONS_METHOD. */
	private static final ModelMethod MAP_DELETE_CONDITIONS_METHOD = getConditions("mapDeleteConditions", "return MAP_DELETE_CONDITIONS;");

	/** The Constant MAP_NATIVE_CONDITIONS_METHOD. */
	private static final ModelMethod MAP_NATIVE_CONDITIONS_METHOD = getConditions("mapNativeConditions", "return MAP_NATIVE_CONDITIONS;");

	/** The Constant MAP_DELETE_CONDITIONS_FIELD. */
	private static final ModelField MAP_DELETE_CONDITIONS_FIELD = getFieldMapConditions("MAP_DELETE_CONDITIONS", "getMapDeleteConditions()");

	/** The map class field. */
	private static Map<String, ClassField> mapClassField = new HashMap<>();

	/**
	 * Generate query class.
	 *
	 * @param modelClasses     the model classes
	 * @param type             the type
	 * @param servicePackage   the service package
	 * @param processingEnv    the processing env
	 * @param queryBuilder     the query builder
	 * @param annotationMirror the annotation mirror
	 * @param typeService      the type service
	 * @throws Exception the exception
	 */
	public static void generateQueryClass(ModelClasses modelClasses, TypeElement type, String servicePackage, ProcessingEnvironment processingEnv, QueryBuilder queryBuilder, AnnotationMirror annotationMirror, TypeElement typeService)
			throws Exception {
		ModelClass classQueryJpql = new ModelClass();
		ModelClass interfaceQueryJpql = new ModelClass();
		interfaceQueryJpql.setType(ClassType.INTERFACE);
		String classEntity = type.getQualifiedName().toString().substring(type.getQualifiedName().toString().lastIndexOf(".") + 1);
		String fieldEntity = Character.toLowerCase(classEntity.charAt(0)) + classEntity.substring(1);
		classQueryJpql.setName(classEntity + QUERY_JPQL_IMPL);
		interfaceQueryJpql.setName(classEntity + QUERY_JPQL);
		classQueryJpql.setPackageName(servicePackage);
		interfaceQueryJpql.setPackageName(servicePackage);
		classQueryJpql.addAnnotations(ANNOTATION_COMPONENT);
		TypeElement typeElement = type;

		List<String> mapBaseConditions = new ArrayList<>();
		List<String> mapNativeConditions = new ArrayList<>();
		List<String> mapConditions = new ArrayList<>();
		List<String> mapDeleteConditions = new ArrayList<>();
		LinkedHashSet<String> mapOneToMany = new LinkedHashSet<>();
		classQueryJpql.getImports().add("java.util.HashMap");
		classQueryJpql.getImports().add("bld.commons.service.BaseJpaService");
		classQueryJpql.getImports().add(type.getQualifiedName().toString());
		mapBaseConditions.add(SPACE + "Map<String,String> map=new HashMap<>();");
		mapDeleteConditions.add(SPACE + "Map<String,String> map=getMapBaseConditions();");
		mapConditions.add(SPACE + "Map<String,String> map=getMapBaseConditions();");
		mapNativeConditions.add(SPACE + "Map<String,String> map=new HashMap<>();");

		Map<String, QueryDetail> mapAlias = new HashMap<>();
		mapAlias.put(fieldEntity, new QueryDetail(fieldEntity, fieldEntity, mapClassField.get(type.asType().toString())));
		Set<String> aliases = new HashSet<>();
		aliases.add(fieldEntity);
		String selectByFilter = "\"select distinct " + fieldEntity + "\"";

		String deleteByFilter = "\"delete from " + classEntity + " " + fieldEntity + " where 1=1 \"";

		String countByFilter = "\"select distinct count(" + fieldEntity + ")\"";
		String fromByFilter = " From " + classEntity + " " + fieldEntity + " \"";
		Set<Element> elements = mapClassField.get(type.getQualifiedName().toString()).getElements();
		Set<String> manyProps = new HashSet<>();
		Set<String> keyConditions = new TreeSet<>();
		for (Element element : elements) {

			ClassField classField = mapClassField.get(element.asType().toString());
			String fieldName = element.getSimpleName().toString();
			if (element.getAnnotation(EmbeddedId.class) != null || element.getAnnotation(Id.class) != null) {
				if (element.getAnnotation(EmbeddedId.class) != null) {
					TypeElement typeElementId = (TypeElement) processingEnv.getTypeUtils().asElement(element.asType());
					Set<Element> fieldElementsId = new HashSet<>();
					do {
						for (Element elementId : typeElementId.getEnclosedElements()) {
							if (ElementKind.FIELD.equals(elementId.getKind()) && !fieldElementsId.contains(elementId) && elementId.getAnnotation(Column.class) != null) {
								fieldElementsId.add(elementId);
								mapBaseConditions
										.add(SPACE + "map.put(" + elementId.getSimpleName().toString() + ", \" and " + fieldEntity + ".id." + elementId.getSimpleName().toString() + " in (:" + elementId.getSimpleName().toString() + ") \");");
								keyConditions.add(elementId.getSimpleName().toString());
							}
						}
						TypeMirror superClassIdTypeMirror = typeElementId.getSuperclass();
						typeElementId = (TypeElement) processingEnv.getTypeUtils().asElement(superClassIdTypeMirror);
					} while (!Object.class.getName().equals(typeElementId.getQualifiedName().toString()));

				} else {
					mapBaseConditions.add(SPACE + "map.put(" + element.getSimpleName().toString() + ", \" and " + fieldEntity + "." + element.getSimpleName().toString() + " in (:" + element.getSimpleName().toString() + ") \");");
					mapBaseConditions.add(SPACE + "map.put(id, \" and " + fieldEntity + "." + element.getSimpleName().toString() + " in (:id) \");");
					keyConditions.add(element.getSimpleName().toString());
					keyConditions.add("id");
				}

			} else if (element.getAnnotation(Column.class) != null) {

				Class<?> classFieldElement = PrimitiveType.getClass(element.asType().toString());
				if (classFieldElement == null)
					classFieldElement = Class.forName(element.asType().toString());
				if (Calendar.class.isAssignableFrom(classFieldElement) || Date.class.isAssignableFrom(classFieldElement) || Timestamp.class.isAssignableFrom(classFieldElement)) {
					mapBaseConditions.add(SPACE + "map.put(" + fieldName + "From, \" and :" + fieldName + "From<=" + fieldEntity + "." + fieldName + " \");");
					mapBaseConditions.add(SPACE + "map.put(" + fieldName + "To, \" and " + fieldEntity + "." + fieldName + "<=:" + fieldName + "To \");");
					mapBaseConditions.add(SPACE + "map.put(" + fieldName + ", \" and " + fieldEntity + "." + fieldName + "=:" + fieldName + " \");");
					keyConditions.add(fieldName + "From");
					keyConditions.add(fieldName + "To");
				} else if (String.class.isAssignableFrom(classFieldElement)) {
					mapBaseConditions.add(SPACE + "map.put(" + fieldName + ", \" and upper(" + fieldEntity + "." + fieldName + ") like :" + fieldName + " \");");
				} else if (Boolean.class.isAssignableFrom(classFieldElement)) {
					mapBaseConditions.add(SPACE + "map.put(" + fieldName + ", \" and " + fieldEntity + "." + fieldName + "= :" + fieldName + " \");");
				} else {
					mapBaseConditions.add(SPACE + "map.put(" + fieldName + ", \" and " + fieldEntity + "." + fieldName + " in (:" + fieldName + ") \");");
				}
				keyConditions.add(fieldName);

			} else if (element.getAnnotation(ManyToOne.class) != null) {
				JoinColumn joinColumn = element.getAnnotation(JoinColumn.class);
				fromByFilter = fromManyAndOneToOne(fieldEntity, mapAlias, aliases, fromByFilter, classField, fieldName, joinColumn.nullable());
				Set<Element> listFieldReference = classField.getElements();
				for (Element fieldReference : listFieldReference) {
					if (fieldReference.getAnnotation(Id.class) != null || fieldReference.getAnnotation(EmbeddedId.class) != null) {
						mapConditions
								.add(SPACE + "map.put(" + fieldReference.getSimpleName().toString() + ", \" and " + fieldName + "." + fieldReference.getSimpleName().toString() + " in (:" + fieldReference.getSimpleName().toString() + ") \");");
						mapDeleteConditions.add(SPACE + "map.put(" + fieldReference.getSimpleName().toString() + ", \" and " + fieldEntity + "." + fieldName + "." + fieldReference.getSimpleName().toString() + " in (:"
								+ fieldReference.getSimpleName().toString() + ") \");");
						manyProps.add(fieldReference.getSimpleName().toString());
						keyConditions.add(fieldReference.getSimpleName().toString());
						break;
					}
				}
			} else if (element.getAnnotation(OneToOne.class) != null) {
				fromByFilter = fromManyAndOneToOne(fieldEntity, mapAlias, aliases, fromByFilter, classField, fieldName, nullableOneToOne(element));
			} else if (element.getAnnotation(OneToMany.class) != null || element.getAnnotation(ManyToMany.class) != null) {
				DeclaredType dclt = (DeclaredType) element.asType();
				ClassField classFieldRefernce = mapClassField.get(dclt.getTypeArguments().get(0).toString());
				Map<String, Element> mapField = classFieldRefernce.getMapElement();
				Element fieldReference = null;
				Set<Element> listReferenceField = classFieldRefernce.getElements();
				if (element.getAnnotation(OneToMany.class) != null) {
					OneToMany oneToMany = element.getAnnotation(OneToMany.class);
					fieldReference = mapField.get(oneToMany.mappedBy());
					for (Element fieldOneToMany : listReferenceField) {
						if (fieldOneToMany.getAnnotation(Id.class) != null && fieldReference.getAnnotation(JoinColumn.class) != null) {
							JoinColumn joinColumn = fieldReference.getAnnotation(JoinColumn.class);
							String keyProps = fieldOneToMany.getSimpleName().toString();
							if (manyProps.contains(keyProps))
								keyProps = fieldName + Character.toUpperCase(fieldOneToMany.getSimpleName().toString().charAt(0)) + fieldOneToMany.getSimpleName().toString().substring(1);
							manyProps.add(keyProps);
							mapOneToMany.add(SPACE + "addJoinOneToMany(" + keyProps + ", \" left join fetch " + fieldEntity + "." + fieldName + " " + fieldName + " \");");
							mapConditions.add(SPACE + "map.put(" + keyProps + ", \" and " + fieldName + "." + fieldOneToMany.getSimpleName().toString() + " in (:" + keyProps + ") \");");
							keyConditions.add(keyProps);
							QueryDetail queryDetail = new QueryDetail(fieldName, fieldName, joinColumn.nullable(), true, classFieldRefernce);
							mapAlias.put(fieldEntity + "." + fieldName, queryDetail);
							aliases.add(fieldName);
						}
					}

				} else {
					for (Element fieldManyToMany : listReferenceField) {
						if (fieldManyToMany.getAnnotation(Id.class) != null) {
							String keyProps = fieldManyToMany.getSimpleName().toString();
							if (manyProps.contains(keyProps))
								keyProps = fieldName + Character.toUpperCase(fieldManyToMany.getSimpleName().toString().charAt(0)) + fieldManyToMany.getSimpleName().toString().substring(1);
							manyProps.add(keyProps);
							mapOneToMany.add(SPACE + "addJoinOneToMany(" + keyProps + ", \" left join fetch " + fieldEntity + "." + fieldName + " " + fieldName + " \");");
							mapConditions.add(SPACE + "map.put(" + keyProps + ", \" and " + fieldName + "." + fieldManyToMany.getSimpleName().toString() + " in (:" + keyProps + ") \");");
							keyConditions.add(keyProps);
							QueryDetail queryDetail = new QueryDetail(fieldName, fieldName, false, true, classFieldRefernce);
							mapAlias.put(fieldEntity + "." + fieldName, queryDetail);
							aliases.add(fieldName);
						}
					}
				}

			}

		}

		for (String joinPath : queryBuilder.joins()) {
			LinkedHashSet<String> manies = new LinkedHashSet<>();
			fromByFilter = buildJoin(type, processingEnv, mapConditions, mapOneToMany, mapAlias, aliases, fromByFilter, manyProps, joinPath, null, typeService, annotationMirror, JOINS, manies,keyConditions);
		}

		for (ConditionBuilder condition : queryBuilder.conditions()) {
			String joinPath = condition.field().substring(0, condition.field().lastIndexOf("."));
			String field = condition.field().substring(condition.field().lastIndexOf(".") + 1);
			LinkedHashSet<String> manies = new LinkedHashSet<>();
			fromByFilter = buildJoin(type, processingEnv, mapConditions, mapOneToMany, mapAlias, aliases, fromByFilter, manyProps, joinPath, condition.parameter(), typeService, annotationMirror, CONDITIONS, manies,keyConditions);
			QueryDetail queryDetail = mapAlias.get(joinPath);
			if (queryDetail == null) {
				String errorMessage = "The field path \"" + condition.field() + "\" is not valid";
				AnnotationValue annotationValue = getAnnotationValue(annotationMirror, CONDITIONS);
				processingEnv.getMessager().printMessage(Kind.ERROR, errorMessage, typeService, annotationMirror, annotationValue);
				throw new ProcessorJpaServiceException(errorMessage);
			}

			ClassField classField = queryDetail.getClassField();

			Element fieldElement = classField.getMapElement().get(field);
			if (fieldElement == null) {
				AnnotationValue annotationValue = getAnnotationValue(annotationMirror, CONDITIONS);
				String errorMessage = "The \"" + field + "\" field does not exist";
				processingEnv.getMessager().printMessage(Kind.ERROR, errorMessage, typeService, annotationMirror, annotationValue);
				throw new ProcessorJpaServiceException(errorMessage);
			}

			Class<?> classFieldElement = PrimitiveType.getClass(fieldElement.asType().toString());
			if (classFieldElement == null)
				classFieldElement = Class.forName(fieldElement.asType().toString());

			if (String.class.isAssignableFrom(classFieldElement)) {
				mapConditions.add(SPACE + "map.put(" + condition.parameter() + ", \" and (" + condition.upperLower().getFunction() + "(" + queryDetail.getAlias() + "." + field + ") "
						+ condition.operation().getValue().replace(BaseJpaService.KEY_PROPERTY, ":" + condition.parameter()) + (condition.nullable() ? " or " + queryDetail.getAlias() + "." + field + " is null " : "") + ")\");");
				mapDeleteConditions.add(SPACE + "map.put(" + condition.parameter() + ", \" and (" + condition.upperLower().getFunction() + "(" + condition.field() + ") "
						+ condition.operation().getValue().replace(BaseJpaService.KEY_PROPERTY, ":" + condition.parameter()) + (condition.nullable() ? " or " + condition.field() + " is null " : "") + ") \");");
			} else {
				mapConditions.add(SPACE + "map.put(" + condition.parameter() + ", \" and (" + queryDetail.getAlias() + "." + field + " " + condition.operation().getValue().replace(BaseJpaService.KEY_PROPERTY, ":" + condition.parameter())
						+ (condition.nullable() ? " or " + queryDetail.getAlias() + "." + field + " is null " : "") + ")\");");
				mapDeleteConditions.add(SPACE + "map.put(" + condition.parameter() + ", \" and (" + condition.field() + " " + condition.operation().getValue().replace(BaseJpaService.KEY_PROPERTY, ":" + condition.parameter())
						+ (condition.nullable() ? " or " + condition.field() + " is null " : "") + ")\");");
			}
			keyConditions.add(condition.parameter());
			if (queryDetail.isMany()) {
				// manies.add("\"" + (queryDetail.isNullable() ? " left" : "") + " join fetch "
				// + queryDetail.getAlias() + "." + field + " " + getAlias(aliases, field) + "
				// \"");
				mapOneToMany.add(SPACE + "addJoinOneToMany(" + condition.parameter() + ", " + printManies(manies) + " );");
			}

		}

		for (CustomConditionBuilder customCondition : queryBuilder.customConditions())
			writeCustomCondition(mapConditions, mapDeleteConditions, customCondition,keyConditions);
		for (CustomConditionBuilder customCondition : queryBuilder.customNativeConditions()) {
			mapNativeConditions.add(SPACE + "map.put(" + customCondition.parameter() + ", \" " + customCondition.condition().trim() + " \");");
			keyConditions.add(customCondition.parameter());
		}
			

		TypeMirror superClassTypeMirror = typeElement.getSuperclass();
		typeElement = (TypeElement) processingEnv.getTypeUtils().asElement(superClassTypeMirror);

		fromByFilter += "\n" + SPACE + "+\" \"+BaseJpaService.ONE_TO_MANY+\" where 1=1 ";
		ModelField fromByFilterField = finalStaticField(FROM_BY_FILTER, STRING, fromByFilter, true);
		ModelField countByFilterField = finalStaticField(COUNT_BY_FILTER, STRING, countByFilter + "+" + FROM_BY_FILTER, false);
		ModelField selectByFilterField = finalStaticField(SELECT_BY_FILTER, STRING, selectByFilter + "+" + FROM_BY_FILTER, false);
		ModelField deletetByFilterField = finalStaticField(DELETE_BY_FILTER, STRING, deleteByFilter, false);

		for (String keyCondition : keyConditions)
			interfaceQueryJpql.addFields(finalStaticField(keyCondition, STRING, keyCondition, true, LevelType.PUBLIC));

		classQueryJpql.addExtendsClass(getSuperClassQueryJpql(classEntity));
		classQueryJpql.addInterface(getInterfaceQueryJpql(classEntity));

		classQueryJpql.getFields().add(MAP_CONDITIONS_FIELD);
		classQueryJpql.getFields().add(MAP_DELETE_CONDITIONS_FIELD);
		classQueryJpql.getFields().add(MAP_NATIVE_CONDITIONS_FIELD);
		classQueryJpql.getFields().add(fromByFilterField);
		classQueryJpql.getFields().add(countByFilterField);
		classQueryJpql.getFields().add(selectByFilterField);
		classQueryJpql.getFields().add(deletetByFilterField);

		classQueryJpql.getMethods().add(SELECT_BY_FILTER_METHOD);
		classQueryJpql.getMethods().add(COUNT_BY_FILTER_METHOD);
		classQueryJpql.getMethods().add(DELETE_BY_FILTER_METHOD);

		mapConditions.add(SPACE + "return map;");
		mapBaseConditions.add(SPACE + "return map;");
		mapDeleteConditions.add(SPACE + "return map;");
		mapNativeConditions.add(SPACE + "return map;");
		ModelMethod staticMapConditions = getMapConditions("getMapConditions", mapConditions);
		ModelMethod staticMapBaseConditions = getMapConditions("getMapBaseConditions", mapBaseConditions);
		ModelMethod staticMapDeleteConditions = getMapConditions("getMapDeleteConditions", mapDeleteConditions);
		ModelMethod staticMapNativeConditions = getMapConditions("getMapNativeConditions", mapNativeConditions);

		classQueryJpql.getMethods().add(staticMapConditions);
		classQueryJpql.getMethods().add(staticMapBaseConditions);
		classQueryJpql.getMethods().add(staticMapDeleteConditions);
		classQueryJpql.getMethods().add(staticMapNativeConditions);

		ModelMethod mapConditionsMethod = MAP_CONDITIONS_METHOD;
		ModelMethod mapDeleteConditionsMethod = MAP_DELETE_CONDITIONS_METHOD;
		ModelMethod mapNativeConditionsMethod = MAP_NATIVE_CONDITIONS_METHOD;
		classQueryJpql.getMethods().add(mapConditionsMethod);
		classQueryJpql.getMethods().add(mapDeleteConditionsMethod);
		classQueryJpql.getMethods().add(mapNativeConditionsMethod);

		ModelMethod mapOneToManyMethod = new ModelMethod("mapOneToMany", "void");
		mapOneToManyMethod.setLevelType(LevelType.PUBLIC);
		mapOneToManyMethod.getAnnotations().add(ANNOTATION_OVERRIDE);
		mapOneToManyMethod.setCommands(mapOneToMany);

		classQueryJpql.getMethods().add(mapOneToManyMethod);

		modelClasses.getClasses().add(interfaceQueryJpql);
		modelClasses.getClasses().add(classQueryJpql);
	}

	/**
	 * From many and one to one.
	 *
	 * @param fieldEntity  the field entity
	 * @param mapAlias     the map alias
	 * @param aliases      the aliases
	 * @param fromByFilter the from by filter
	 * @param classField   the class field
	 * @param fieldName    the field name
	 * @param nullable     the nullable
	 * @return the string
	 */
	private static String fromManyAndOneToOne(String fieldEntity, Map<String, QueryDetail> mapAlias, Set<String> aliases, String fromByFilter, ClassField classField, String fieldName, boolean nullable) {
		String alias = getAlias(aliases, fieldName);
		fromByFilter += "\n" + SPACE + "+\"" + (nullable ? " left" : "") + " join fetch " + fieldEntity + "." + fieldName + " " + alias + " \"";
		QueryDetail queryDetail = new QueryDetail(alias, fieldName, nullable, classField);
		mapAlias.put(fieldEntity + "." + fieldName, queryDetail);
		return fromByFilter;
	}

	/**
	 * Gets the annotation value.
	 *
	 * @param annotationMirror the annotation mirror
	 * @param element          the element
	 * @return the annotation value
	 */
	private static AnnotationValue getAnnotationValue(AnnotationMirror annotationMirror, String element) {
		for (Entry<? extends ExecutableElement, ? extends AnnotationValue> entryAnnotation : annotationMirror.getElementValues().entrySet()) {
			if (entryAnnotation.getKey().getSimpleName().toString().equals(element))
				return entryAnnotation.getValue();
		}
		return null;
	}

	/**
	 * Write custom condition.
	 *
	 * @param conditions       the conditions
	 * @param deleteConditions the delete conditions
	 * @param customCondition  the custom condition
	 * @param keyConditions the key conditions
	 */
	private static void writeCustomCondition(List<String> conditions, List<String> deleteConditions, CustomConditionBuilder customCondition, Set<String> keyConditions) {
		String condition = SPACE + "map.put(" + customCondition.parameter() + ", \" " + customCondition.condition() + "\");";
		keyConditions.add(customCondition.parameter());
		if (ConditionType.SELECT.equals(customCondition.type()))
			conditions.add(condition);
		else
			deleteConditions.add(condition);
	}

	/**
	 * Builds the join.
	 *
	 * @param type              the type
	 * @param processingEnv     the processing env
	 * @param mapConditions     the map conditions
	 * @param mapOneToMany      the map one to many
	 * @param mapAlias          the map alias
	 * @param aliases           the aliases
	 * @param fromByFilter      the from by filter
	 * @param manyProps         the many props
	 * @param joinPath          the join path
	 * @param parameter         the parameter
	 * @param typeService       the type service
	 * @param annotationMirror  the annotation mirror
	 * @param elementAnnotation the element annotation
	 * @param manies            the manies
	 * @param keyConditions the key conditions
	 * @return the string
	 */
	private static String buildJoin(TypeElement type, ProcessingEnvironment processingEnv, List<String> mapConditions, LinkedHashSet<String> mapOneToMany, Map<String, QueryDetail> mapAlias, Set<String> aliases, String fromByFilter,
			Set<String> manyProps, String joinPath, String parameter, TypeElement typeService, AnnotationMirror annotationMirror, String elementAnnotation, LinkedHashSet<String> manies, Set<String> keyConditions) {
		List<String> joins = new ArrayList<>(Arrays.asList(joinPath.split("\\.")));
		QueryDetail queryDetail = null;
		String alias = null;
		String key = joins.get(0);
		String classJoin = null;

		if (type.getAnnotation(OneToMany.class) != null || type.getAnnotation(ManyToMany.class) != null) {
			DeclaredType dclt = (DeclaredType) type.asType();
			classJoin = dclt.getTypeArguments().get(0).toString();
		} else
			classJoin = type.getQualifiedName().toString();

		if (CollectionUtils.isNotEmpty(joins))
			joins.remove(0);
		for (String join : joins) {
			ClassField classFieldJoin = mapClassField.get(classJoin);
			Element elementJoin = classFieldJoin.getMapElement().get(join);
			if (elementJoin == null) {
				String errorMessage = "Not exist element with name \"" + key + "." + join + "\"";
				AnnotationValue annotationValue = getAnnotationValue(annotationMirror, elementAnnotation);
				processingEnv.getMessager().printMessage(Kind.ERROR, errorMessage, typeService, annotationMirror, annotationValue);
				throw new ProcessorJpaServiceException(errorMessage);
			}
			classJoin = elementJoin.asType().toString();
			boolean nullable = queryDetail != null ? queryDetail.isNullable() : false;
			boolean many = queryDetail != null ? queryDetail.isMany() : false;
			key += "." + join;
			if (mapAlias.containsKey(key)) {
				QueryDetail appQueryDetail = mapAlias.get(key);
				if (appQueryDetail != null && appQueryDetail.isMany()) {
					String className = queryDetail != null ? queryDetail.getAlias() : key;
					manies.add("\" left join fetch " + className + " " + appQueryDetail.getAlias() + " \"");
				}
				queryDetail = appQueryDetail;
			} else {
				alias = getAlias(aliases, join);
				if (elementJoin.getAnnotation(OneToMany.class) != null) {
					DeclaredType dclt = (DeclaredType) elementJoin.asType();
					classJoin = dclt.getTypeArguments().get(0).toString();
					ClassField classFieldRefernce = mapClassField.get(classJoin);
					Map<String, Element> mapField = classFieldRefernce.getMapElement();
					Element fieldReference = null;
					Set<Element> listReferenceField = classFieldRefernce.getElements();
					OneToMany oneToMany = elementJoin.getAnnotation(OneToMany.class);
					fieldReference = mapField.get(oneToMany.mappedBy());
					for (Element fieldOneToMany : listReferenceField) {
						if (fieldOneToMany.getAnnotation(Id.class) != null && fieldReference.getAnnotation(JoinColumn.class) != null) {
							JoinColumn joinColumn = fieldReference.getAnnotation(JoinColumn.class);
							String keyProps = fieldOneToMany.getSimpleName().toString();
							if (manyProps.contains(keyProps))
								keyProps = alias + Character.toUpperCase(fieldOneToMany.getSimpleName().toString().charAt(0)) + fieldOneToMany.getSimpleName().toString().substring(1);
							manies.add(" left join fetch " + queryDetail.getAlias() + "." + join + " " + alias + " \"");
							mapOneToMany.add(SPACE + "addJoinOneToMany(" + keyProps + ", " + printManies(manies) + " );");
							mapConditions.add(SPACE + "map.put(" + keyProps + ", \" and " + alias + "." + fieldOneToMany.getSimpleName().toString() + " in (:" + keyProps + ") \");");
							keyConditions.add(keyProps);
							queryDetail = new QueryDetail(alias, key, joinColumn.nullable() || nullable, true, classFieldRefernce);
							mapAlias.put(key, queryDetail);
						}
					}

				} else if (elementJoin.getAnnotation(ManyToMany.class) != null) {
					DeclaredType dclt = (DeclaredType) elementJoin.asType();
					classJoin = dclt.getTypeArguments().get(0).toString();
					ClassField classFieldRefernce = mapClassField.get(classJoin);
					Set<Element> listReferenceField = classFieldRefernce.getElements();
					for (Element fieldManyToMany : listReferenceField) {
						if (fieldManyToMany.getAnnotation(Id.class) != null) {
							String keyProps = fieldManyToMany.getSimpleName().toString();
							if (manyProps.contains(keyProps))
								keyProps = alias + Character.toUpperCase(fieldManyToMany.getSimpleName().toString().charAt(0)) + fieldManyToMany.getSimpleName().toString().substring(1);
							manyProps.add(keyProps);
							manies.add(" left join fetch " + queryDetail.getAlias() + "." + join + " " + alias + " \"");
							mapOneToMany.add(SPACE + "addJoinOneToMany(" + keyProps + ", " + printManies(manies) + " );");
							mapConditions.add(SPACE + "map.put(" + keyProps + ", \" and " + alias + "." + fieldManyToMany.getSimpleName().toString() + " in (:" + keyProps + ") \");");
							keyConditions.add(keyProps);
							queryDetail = new QueryDetail(alias, key, nullable, true, classFieldRefernce);
							mapAlias.put(key, queryDetail);
						}
					}
				} else if (elementJoin.getAnnotation(ManyToOne.class) != null) {
					nullable = nullable || elementJoin.getAnnotation(JoinColumn.class).nullable();
					if (!many)
						fromByFilter += "\n" + SPACE + "+\"" + (nullable ? " left" : "") + " join fetch " + queryDetail.getAlias() + "." + join + " " + alias + " \"";
					else {
						ClassField classField = mapClassField.get(elementJoin.asType().toString());
						Set<Element> listFieldReference = classField.getElements();
						for (Element fieldReference : listFieldReference) {
							if (fieldReference.getAnnotation(Id.class) != null || fieldReference.getAnnotation(EmbeddedId.class) != null) {
								mapConditions.add(SPACE + "map.put(" + fieldReference.getSimpleName().toString() + ", \" and " + alias + "." + fieldReference.getSimpleName().toString() + " in (:" + fieldReference.getSimpleName().toString() + ") \");");
								manies.add("\" left join fetch " + queryDetail.getAlias() + "." + join + " " + alias + " \"");
								mapOneToMany.add(SPACE + "addJoinOneToMany(" + fieldReference.getSimpleName().toString() + ", " + printManies(manies) + " );");
								manyProps.add(fieldReference.getSimpleName().toString());
								keyConditions.add(fieldReference.getSimpleName().toString());
								break;
							}
						}

					}
					queryDetail = new QueryDetail(alias, key, nullable, many, mapClassField.get(elementJoin.asType().toString()));
					mapAlias.put(key, queryDetail);
				} else if (elementJoin.getAnnotation(OneToOne.class) != null) {
					nullable = nullable || nullableOneToOne(elementJoin);
					if (!many)
						fromByFilter += "\n" + SPACE + "+\"" + (nullable ? " left" : "") + " join fetch " + queryDetail.getAlias() + "." + join + " " + alias + " \"";
					else {
						ClassField classField = mapClassField.get(elementJoin.asType().toString());
						Set<Element> listFieldReference = classField.getElements();
						for (Element fieldReference : listFieldReference) {
							if (fieldReference.getAnnotation(Id.class) != null || fieldReference.getAnnotation(EmbeddedId.class) != null) {
								mapConditions.add(
										SPACE + "map.put(" + fieldReference.getSimpleName().toString() + ", \" and " + alias + "." + fieldReference.getSimpleName().toString() + " in (:" + fieldReference.getSimpleName().toString() + ") \");");
								manies.add("\"" + (nullable ? " left" : "") + " join fetch " + queryDetail.getAlias() + "." + join + " " + alias + " \"");
								mapOneToMany.add(SPACE + "addJoinOneToMany(" + fieldReference.getSimpleName().toString() + ", " + printManies(manies) + " );");
								manyProps.add(fieldReference.getSimpleName().toString());
								keyConditions.add(fieldReference.getSimpleName().toString());
								break;
							}
						}

					}
					queryDetail = new QueryDetail(alias, key, nullable, many, mapClassField.get(elementJoin.asType().toString()));
					mapAlias.put(key, queryDetail);
				}

			}

		}
		if (CollectionUtils.isNotEmpty(manies) && StringUtils.isNotEmpty(parameter)) {
			mapOneToMany.add(SPACE + "addJoinOneToMany(" + parameter + ", " + printManies(manies) + " );");
			keyConditions.add(parameter);
		}
			

		return fromByFilter;
	}

	/**
	 * Gets the alias.
	 *
	 * @param aliases the aliases
	 * @param join    the join
	 * @return the alias
	 */
	private static String getAlias(Set<String> aliases, String join) {
		String alias;
		alias = join;
		int i = 0;
		do {
			if (aliases.contains(alias))
				alias = join + "_" + (++i);
		} while (aliases.contains(alias));
		aliases.add(alias);
		return alias;
	}

	/**
	 * Nullable one to one.
	 *
	 * @param element the element
	 * @return true, if successful
	 */
	private static boolean nullableOneToOne(Element element) {
		boolean nullable = true;
		if (element.getAnnotation(JoinColumn.class) != null) {
			JoinColumn joinColumn = element.getAnnotation(JoinColumn.class);
			nullable = joinColumn.nullable();
		} else {
			if (element.getAnnotation(NotNull.class) != null)
				nullable = false;
		}
		return nullable;
	}

	/**
	 * Prints the manies.
	 *
	 * @param manies the manies
	 * @return the string
	 */
	private static String printManies(LinkedHashSet<String> manies) {
		String joinMany = "";
		for (String many : manies)
			joinMany += "," + many;
		return joinMany.substring(1);
	}

	/**
	 * Gets the interface query jpql.
	 *
	 * @param classEntity the class entity
	 * @return the interface query jpql
	 */
	private static ModelSuperClass getInterfaceQueryJpql(String classEntity) {
		ModelSuperClass superClassQueryJpql = new ModelSuperClass();
		superClassQueryJpql.setName(classEntity + QUERY_JPQL);
		return superClassQueryJpql;
	}

	/**
	 * Gets the super class query jpql.
	 *
	 * @param classEntity the class entity
	 * @return the super class query jpql
	 */
	private static ModelSuperClass getSuperClassQueryJpql(String classEntity) {
		ModelGenericType genericTypeEntityClass = new ModelGenericType();
		genericTypeEntityClass.setName(classEntity);
		ModelSuperClass superClassQueryJpql = new ModelSuperClass();
		superClassQueryJpql.setName(QUERY_JPQL);
		superClassQueryJpql.addGenericTypes(genericTypeEntityClass);
		return superClassQueryJpql;
	}

	/**
	 * Generate jpa class.
	 *
	 * @param modelClasses the model classes
	 * @param type         the type
	 * @throws Exception the exception
	 */
	public static void generateJpaClass(ModelClasses modelClasses, TypeElement type) throws Exception {
		TypeElement typeElement = type;
		ClassField classField = new ClassField(type.getQualifiedName().toString());
		mapClassField.put(classField.getClassName(), classField);
		do {
			for (Element element : typeElement.getEnclosedElements()) {
				if (ElementKind.FIELD.equals(element.getKind()) && !classField.getElements().contains(element) && element.getAnnotation(Transient.class) == null) {
					classField.getElements().add(element);
					classField.getMapElement().put(element.getSimpleName().toString(), element);
					if (element.getAnnotation(EmbeddedId.class) != null || element.getAnnotation(Id.class) != null) {
						Class<?> classTypeField = null;
						if (element.getAnnotation(EmbeddedId.class) == null) {
							classTypeField = Class.forName(element.asType().toString());
							if (ReflectionCommons.mapPrimitiveToObject.containsKey(classTypeField))
								classTypeField = ReflectionCommons.mapPrimitiveToObject.get(classTypeField);

						}
					}
				}
			}
			TypeMirror superClassTypeMirror = typeElement.getSuperclass();
			typeElement = (TypeElement) ((DeclaredType) superClassTypeMirror).asElement();
		} while (!Object.class.getName().equals(typeElement.getQualifiedName().toString()));

	}

	/**
	 * Gets the conditions.
	 *
	 * @param name    the name
	 * @param command the command
	 * @return the conditions
	 */
	private static ModelMethod getConditions(String name, String command) {
		ModelMethod mapConditionsMethod = new ModelMethod();
		mapConditionsMethod.setName(name);
		mapConditionsMethod.setType("Map");
		mapConditionsMethod.getGenericTypes().add(new ModelGenericType("String"));
		mapConditionsMethod.getGenericTypes().add(new ModelGenericType("String"));
		mapConditionsMethod.setLevelType(LevelType.PUBLIC);
		mapConditionsMethod.getAnnotations().add(ANNOTATION_OVERRIDE);

		mapConditionsMethod.getCommands().add(SPACE + command);
		return mapConditionsMethod;
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
	 * Return method service.
	 *
	 * @param name      the name
	 * @param type      the type
	 * @param command   the command
	 * @param levelType the level type
	 * @return the model method
	 */
	private static ModelMethod returnMethodService(String name, String type, String command, LevelType levelType) {
		ModelMethod modelMethod = new ModelMethod(name, type);
		modelMethod.getAnnotations().add(ANNOTATION_OVERRIDE);
		modelMethod.setLevelType(levelType);
		modelMethod.getCommands().add(command);
		return modelMethod;
	}

	/**
	 * Final static field.
	 *
	 * @param name the name
	 * @param type the type
	 * @param value the value
	 * @param showQuotes the show quotes
	 * @return the model field
	 */
	private static ModelField finalStaticField(String name, String type, Object value, boolean showQuotes) {
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
	 * Final static field.
	 *
	 * @param name       the name
	 * @param type       the type
	 * @param value      the value
	 * @param showQuotes the show quotes
	 * @param levelType the level type
	 * @return the model field
	 */
	private static ModelField finalStaticField(String name, String type, Object value, boolean showQuotes, LevelType levelType) {
		ModelField modelField = finalStaticField(name, type, value, showQuotes);
		modelField.setLevelType(levelType);
		return modelField;
	}

	/**
	 * Gets the field map conditions.
	 *
	 * @param name  the name
	 * @param value the value
	 * @return the field map conditions
	 */
	private static ModelField getFieldMapConditions(String name, String value) {
		ModelField mapConditions = finalStaticField(name, "Map", value, false);
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		return mapConditions;
	}

	/**
	 * Gets the map conditions.
	 *
	 * @param name     the name
	 * @param commands the commands
	 * @return the map conditions
	 */
	private static ModelMethod getMapConditions(String name, List<String> commands) {
		ModelMethod mapConditions = new ModelMethod(name, "Map");
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		mapConditions.getGenericTypes().add(new ModelGenericType("String"));
		mapConditions.setStaticMethod(true);
		mapConditions.setLevelType(LevelType.PRIVATE);
		mapConditions.setCommands(commands);
		return mapConditions;
	}

}
