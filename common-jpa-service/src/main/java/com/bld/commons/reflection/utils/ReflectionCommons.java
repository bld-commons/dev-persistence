/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.utils.ReflectionCommons.java
 */
package com.bld.commons.reflection.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.query.BindableType;
import org.hibernate.query.TypedParameterValue;
import org.hibernate.type.StandardBasicTypes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.bld.commons.reflection.annotations.ConditionsZones;
import com.bld.commons.reflection.annotations.DateFilter;
import com.bld.commons.reflection.annotations.FieldMapping;
import com.bld.commons.reflection.annotations.FilterNullValue;
import com.bld.commons.reflection.annotations.IgnoreMapping;
import com.bld.commons.reflection.annotations.IgnoreResultSet;
import com.bld.commons.reflection.annotations.LikeString;
import com.bld.commons.reflection.annotations.ListFilter;
import com.bld.commons.reflection.annotations.ResultMapping;
import com.bld.commons.reflection.annotations.TupleComparison;
import com.bld.commons.reflection.model.BaseParameter;
import com.bld.commons.reflection.model.NativeQueryParameter;
import com.bld.commons.reflection.model.QueryParameter;
import com.bld.commons.reflection.model.TupleParameter;
import com.bld.commons.reflection.type.GetSetType;
import com.bld.commons.utils.CamelCaseUtils;
import com.bld.commons.utils.DateUtils;

/**
 * The Class ReflectionCommons.
 */
@Component
@SuppressWarnings("unchecked")
public class ReflectionCommons {

	/** The Constant PK. */
	public static final String PK = "PK";

	/** The context. */
	@Autowired
	private ApplicationContext applicationContext;

	/** The logger. */
	private final static Log logger = LogFactory.getLog(ReflectionCommons.class);

	/** The Constant SERVICE_IMPL. */
	public static final String SERVICE_IMPL = "ServiceImpl";

	/** The Constant UPDATE. */
	public static final String UPDATE = "update";

	/** The Constant SAVE. */
	public static final String SAVE = "save";

	/** The Constant mapPrimitiveToObject. */
	public final static Map<Class<?>, Class<?>> mapPrimitiveToObject = mapFromPrimitiveToObject();

	/** The Constant mapType. */
	public final static Map<Class<?>, BindableType<?>> mapType = getMapType();

	/** The Constant pattern. */
	private final static Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");

	/**
	 * Save generic.
	 *
	 * @param valore                 the valore
	 * @param classCampoDestinatario the class campo destinatario
	 * @throws NoSuchMethodException     the no such method exception
	 * @throws IllegalAccessException    the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 */
	public void saveGeneric(Object valore, Class<?> classCampoDestinatario) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String nomeClasse = classCampoDestinatario.getSimpleName();
		String nomeClasseServiceImpl = getBeanName(nomeClasse, SERVICE_IMPL);
		Object oggettoServiceImpl = applicationContext.getBean(nomeClasseServiceImpl);
		Method metodo = oggettoServiceImpl.getClass().getMethod(SAVE, valore.getClass());
		metodo.invoke(oggettoServiceImpl, valore);

	}

	/**
	 * Gets the map type.
	 *
	 * @return the map type
	 */
	private static Map<Class<?>, BindableType<?>> getMapType() {
		Map<Class<?>, BindableType<?>> map = new HashMap<>();
		map.put(Boolean.class, StandardBasicTypes.BOOLEAN);
		map.put(String.class, StandardBasicTypes.STRING);
		map.put(Long.class, StandardBasicTypes.LONG);
		map.put(BigInteger.class, StandardBasicTypes.BIG_INTEGER);
		map.put(Integer.class, StandardBasicTypes.INTEGER);
		map.put(Short.class, StandardBasicTypes.SHORT);
		map.put(BigDecimal.class, StandardBasicTypes.BIG_DECIMAL);
		map.put(Double.class, StandardBasicTypes.DOUBLE);
		map.put(Float.class, StandardBasicTypes.FLOAT);
		map.put(Byte.class, StandardBasicTypes.BYTE);
		map.put(Character.class, StandardBasicTypes.CHARACTER);
		map.put(Date.class, StandardBasicTypes.DATE);
		map.put(Calendar.class, StandardBasicTypes.CALENDAR);
		map.put(Locale.class, StandardBasicTypes.LOCALE);
		map.put(TimeZone.class, StandardBasicTypes.TIMEZONE);
		map.put(Clob.class, StandardBasicTypes.CLOB);
		map.put(Blob.class, StandardBasicTypes.BLOB);
		return map;
	}

	/**
	 * Map from primitive to object.
	 *
	 * @return the map
	 */
	private static Map<Class<?>, Class<?>> mapFromPrimitiveToObject() {
		Map<Class<?>, Class<?>> map = new HashMap<>();
		map.put(int.class, Integer.class);
		map.put(byte.class, Byte.class);
		map.put(char.class, Character.class);
		map.put(boolean.class, Boolean.class);
		map.put(double.class, Double.class);
		map.put(float.class, Float.class);
		map.put(long.class, Long.class);
		map.put(short.class, Short.class);
		map.put(void.class, Void.class);
		return map;
	}

	/**
	 * Data to map.
	 *
	 * @param <T>            the generic type
	 * @param <ID>           the generic type
	 * @param queryParameter the query parameter
	 * @return the query parameter
	 */
	public <T, ID> QueryParameter<T, ID> dataToMap(QueryParameter<T, ID> queryParameter) {

		BaseParameter obj = queryParameter.getBaseParameter();

		if (obj != null) {
			Set<Field> fields = ReflectionCommons.fields(obj.getClass());
			Map<String, LinkedHashSet<Method>> mapMethod = ReflectionCommons.mapMethods(obj.getClass());
			for (Field field : fields) {
				Method method = ReflectionCommons.getMethod(mapMethod, field, GetSetType.get);
				if (method != null) {
					IgnoreMapping ignoreMapping = method.isAnnotationPresent(IgnoreMapping.class) ? method.getAnnotation(IgnoreMapping.class) : field.getAnnotation(IgnoreMapping.class);
					if (ignoreMapping == null || !ignoreMapping.value()) {
						try {
							Object value = PropertyUtils.getProperty(obj, field.getName());
							if (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value))
								value = null;
							if (value != null && value instanceof String && StringUtils.isBlank((String) value))
								value = null;
							if (value != null) {
								value = getValue(field, method, value);
								if (field.isAnnotationPresent(TupleComparison.class)) {
									TupleParameter tupleParameter=this.getTupleParameter(field, value);
									queryParameter.addParameter(field.getName(), tupleParameter);
								} else if (value instanceof Boolean && (Boolean) value && field.isAnnotationPresent(ListFilter.class))
									queryParameter.addNullable(field.getName());
								else if (value.getClass().isArray()) {
									Object[] array = (Object[]) value;
									queryParameter.addParameter(field.getName(), Arrays.asList(array));
								} else
									queryParameter.addParameter(field.getName(), value);
							} else if (field.isAnnotationPresent(FilterNullValue.class) && field.getAnnotation(FilterNullValue.class).value() || method.isAnnotationPresent(FilterNullValue.class) && method.getAnnotation(FilterNullValue.class).value())
								queryParameter.addParameter(field.getName(), initTypedParameterValue(mapType.get(field.getType()), value));
						} catch (Exception e) {
							logger.warn("Error converting data to map");
						}
					}
				}
			}

		}
		return queryParameter;
	}

	private <T, ID> TupleParameter getTupleParameter(Field field, Object value) {
		TupleComparison tupleComparison = field.getAnnotation(TupleComparison.class);
		TupleParameter tuple = new TupleParameter(tupleComparison.value());
		if (value instanceof Collection)
			tuple.setObjects((Collection<Object>) value);
		else
			tuple.setObjects(value);
		return tuple;
	}

	/**
	 * Inits the typed parameter value.
	 *
	 * @param <J>          the generic type
	 * @param bindableType the bindable type
	 * @param value        the value
	 * @return the typed parameter value
	 */
	public static <J> TypedParameterValue<J> initTypedParameterValue(BindableType<J> bindableType, Object value) {
		return new TypedParameterValue<J>(bindableType, (J) value);
	}

	/**
	 * Gets the value.
	 *
	 * @param field  the field
	 * @param method the method
	 * @param value  the value
	 * @return the value
	 */
	private Object getValue(Field field, Method method, Object value) {
		DateFilter dateFilter = method.isAnnotationPresent(DateFilter.class) ? method.getAnnotation(DateFilter.class) : field.getAnnotation(DateFilter.class);
		LikeString likeString = method.isAnnotationPresent(LikeString.class) ? method.getAnnotation(LikeString.class) : field.getAnnotation(LikeString.class);
		return value(value, dateFilter, likeString);
	}

	public static Object value(Object value, DateFilter dateFilter, LikeString likeString) {
		if (dateFilter != null) {
			if (value instanceof Calendar)
				value = DateUtils.sumDate((Calendar) ((Calendar) value).clone(), dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
			else if (value instanceof Date)
				value = DateUtils.sumDate((Date) ((Date) value).clone(), dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
			else if (value instanceof Timestamp)
				value = DateUtils.sumDate((Timestamp) ((Timestamp) value).clone(), dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());

		} else if (likeString != null && value instanceof String) {
			switch (likeString.likeType()) {
			case LEFT_RIGHT:
				value = "%" + value + "%";
				break;
			case NONE:
				break;
			case LEFT:
				value = "%" + value;
				break;
			case RIGHT:
				value = value + "%";
				break;
			default:
				value = "%" + value + "%";
				break;
			}
			switch (likeString.upperLowerType()) {
			case LOWER:
				value = ((String) value).toLowerCase();
				break;
			case UPPER:
				value = ((String) value).toUpperCase();
				break;
			case NONE:
			default:
				break;

			}
		}
		return value;
	}

	/**
	 * Data to map.
	 *
	 * @param <T>            the generic type
	 * @param <ID>           the generic type
	 * @param queryParameter the query parameter
	 * @return the native query parameter
	 */
	public <T, ID> NativeQueryParameter<T, ID> dataToMap(NativeQueryParameter<T, ID> queryParameter) {

		BaseParameter obj = queryParameter.getBaseParameter();

		if (obj != null) {
			Set<Field> fields = ReflectionCommons.fields(obj.getClass());
			Map<String, LinkedHashSet<Method>> mapMethod = ReflectionCommons.mapMethods(obj.getClass());
			for (Field field : fields) {
				Method method = ReflectionCommons.getMethod(mapMethod, field, GetSetType.get);
				if (method != null) {
					IgnoreMapping ignoreMapping = method.isAnnotationPresent(IgnoreMapping.class) ? method.getAnnotation(IgnoreMapping.class) : field.getAnnotation(IgnoreMapping.class);
					if (ignoreMapping == null || !ignoreMapping.value()) {
						try {
							Object value = PropertyUtils.getProperty(obj, field.getName());
							ConditionsZones conditionsZones = method.isAnnotationPresent(ConditionsZones.class) ? method.getAnnotation(ConditionsZones.class) : field.getAnnotation(ConditionsZones.class);
							if (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value))
								value = null;
							if (value != null && value instanceof String && StringUtils.isBlank((String) value))
								value = null;
							if (value != null) {
								value = getValue(field, method, value);

								if (field.isAnnotationPresent(TupleComparison.class)) {
									TupleParameter tupleParameter=this.getTupleParameter(field, value);
									queryParameter.addParameter(field.getName(), tupleParameter, conditionsZones);
								} else if (value instanceof Boolean && (Boolean) value && field.isAnnotationPresent(ListFilter.class))
									queryParameter.addNullable(field.getName(), conditionsZones);
								else if (value.getClass().isArray()) {
									Object[] array = (Object[]) value;
									queryParameter.addParameter(field.getName(), Arrays.asList(array), conditionsZones);
								} else
									queryParameter.addParameter(field.getName(), value, conditionsZones);
							} else if (field.isAnnotationPresent(FilterNullValue.class) && field.getAnnotation(FilterNullValue.class).value() || method.isAnnotationPresent(FilterNullValue.class) && method.getAnnotation(FilterNullValue.class).value())
								queryParameter.addParameter(field.getName(), initTypedParameterValue(mapType.get(field.getType()), value), conditionsZones);
							else if (conditionsZones != null)
								queryParameter.addEmptyZones(conditionsZones);
						} catch (Exception e) {
							logger.warn("Error converting data to map");
						}
					}
				}
			}

		}
		return queryParameter;
	}

	/**
	 * Reflection.
	 *
	 * @param <T>       the generic type
	 * @param classT    the class T
	 * @param mapResult the map result
	 * @return the t
	 */
	public <T> T reflection(Class<T> classT, Map<String, Object> mapResult) {
		Map<String, Object> mapRow = new HashMap<>();
		BeanUtilsBean beanUtils = new BeanUtilsBean(new ConvertUtilsBean() {
			@Override
			public Object convert(String value, @SuppressWarnings("rawtypes") Class clazz) {
				if (clazz.isEnum()) {
					return Enum.valueOf(clazz, value);
				} else {
					return super.convert(value, clazz);
				}
			}
		});
		beanUtils.getConvertUtils().register(false, false, 0);
		for (String keyResult : mapResult.keySet()) {
			String fieldName = CamelCaseUtils.camelCase(keyResult, true);
			mapRow.put(fieldName, mapResult.get(keyResult));
		}
		T t = null;
		try {
			t = mapResultSet(classT, mapRow, beanUtils);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return t;
	}

	/**
	 * Map result set.
	 *
	 * @param <T>       the generic type
	 * @param classT    the class T
	 * @param mapRow    the map row
	 * @param beanUtils the bean utils
	 * @return the t
	 * @throws InstantiationException    the instantiation exception
	 * @throws IllegalAccessException    the illegal access exception
	 * @throws IllegalArgumentException  the illegal argument exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException     the no such method exception
	 * @throws SecurityException         the security exception
	 */
	private <T> T mapResultSet(Class<T> classT, Map<String, Object> mapRow, BeanUtilsBean beanUtils)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		T t = classT.getConstructor().newInstance();
		Set<Field> fields = fields(classT);
		boolean isEmpty = true;
		for (Field field : fields) {
			if (!field.isAnnotationPresent(IgnoreResultSet.class)) {
				Object value = null;
				if (field.isAnnotationPresent(ResultMapping.class)) {
					value = mapResultSet(field.getType(), mapRow, beanUtils);
					if (value != null) {
						isEmpty = false;
						beanUtils.setProperty(t, field.getName(), value);
					}
				} else {
					String key = field.getName();
					if (field.isAnnotationPresent(FieldMapping.class))
						key = field.getAnnotation(FieldMapping.class).value();
					if (mapRow.containsKey(key)) {
						value = mapRow.get(key);
						if (value != null) {
							isEmpty = false;
							beanUtils.setProperty(t, field.getName(), value);
						}

					}

				}
			}
		}
		if (isEmpty)
			return null;
		return t;
	}

	/**
	 * Gets the bean name.
	 *
	 * @param nomeClasse  the nome classe
	 * @param rightConcat the right concat
	 * @return the bean name
	 */
	public static String getBeanName(String nomeClasse, String rightConcat) {
		nomeClasse = Character.toLowerCase(nomeClasse.charAt(0)) + nomeClasse.substring(1);
		rightConcat = rightConcat != null ? rightConcat : "";
		return nomeClasse + rightConcat;
	}

	/**
	 * Check empty.
	 *
	 * @param obj the obj
	 * @return the object
	 */
	public static Object checkEmpty(Object obj) {
		Set<Field> campi = new HashSet<Field>(Arrays.asList(obj.getClass().getDeclaredFields()));

		for (Field f : campi) {
			try {
				Object value = PropertyUtils.getProperty(obj, f.getName());
				if (value != null) {
					return obj;
				}
			} catch (Exception e) {
				logger.warn("-XXX- errore durante la lettura del campo -XXX");
				logger.warn(ExceptionUtils.getStackTrace(e));
			}
		}

		return null;
	}

	/**
	 * Gets the generic type class.
	 *
	 * @param <T>    the generic type
	 * @param entity the entity
	 * @return the generic type class
	 */
	public static <T> Class<T> getGenericTypeClass(Object entity) {
		return getGenericTypeClass(entity.getClass(), 0);
	}

	/**
	 * Gets the generic type class.
	 *
	 * @param <T>   the generic type
	 * @param clazz the clazz
	 * @return the generic type class
	 */
	public static <T> Class<T> getGenericTypeClass(Class<?> clazz) {
		return getGenericTypeClass(clazz, 0);
	}

	/**
	 * Gets the generic type class.
	 *
	 * @param <T>   the generic type
	 * @param clazz the clazz
	 * @param i     the i
	 * @return the generic type class
	 */
	public static <T> Class<T> getGenericTypeClass(Class<?> clazz, int i) {
		ParameterizedType parameterizedType = null;
		try {
			parameterizedType = (ParameterizedType) clazz.getGenericSuperclass();
		} catch (Exception e) {
			parameterizedType = (ParameterizedType) clazz.getSuperclass().getGenericSuperclass();
		}
		Class<T> clazzType = (Class<T>) parameterizedType.getActualTypeArguments()[i];
		return clazzType;
	}

	/**
	 * Gets the generic type class.
	 *
	 * @param <T>    the generic type
	 * @param entity the entity
	 * @param i      the i
	 * @return the generic type class
	 */
	public static <T> Class<T> getGenericTypeClass(Object entity, int i) {
		return getGenericTypeClass(entity.getClass(), i);
	}

	/**
	 * Gets the generic type field.
	 *
	 * @param <T>   the generic type
	 * @param field the field
	 * @return the generic type field
	 */
	public static <T> Class<T> getGenericTypeField(Field field) {
		return getGenericTypeField(field, 0);
	}

	/**
	 * Gets the generic type field.
	 *
	 * @param <T>   the generic type
	 * @param field the field
	 * @param i     the i
	 * @return the generic type field
	 */
	public static <T> Class<T> getGenericTypeField(Field field, int i) {
		ParameterizedType parameterizedType = null;
		parameterizedType = (ParameterizedType) field.getGenericType();
		Class<T> clazz = (Class<T>) parameterizedType.getActualTypeArguments()[i];
		return clazz;
	}

	/**
	 * Removes the extra space.
	 *
	 * @param join the join
	 * @return the string
	 */
	public static String removeExtraSpace(String join) {
		join = join.trim();
		if (join.contains("  "))
			join = removeExtraSpace(join.replace("  ", " "));
		return join;
	}

	/**
	 * Fields.
	 *
	 * @param classApp the class app
	 * @return the sets the
	 */
	public static Set<Field> fields(Class<?> classApp) {
		Set<Field> listField = new HashSet<>();
		do {
			for (Field field : classApp.getDeclaredFields())
				if (!listField.contains(field))
					listField.add(field);
			classApp = classApp.getSuperclass();
		} while (classApp != null && !classApp.getName().equals(Object.class.getName()));
		return listField;
	}

	/**
	 * Fields.
	 *
	 * @param classApp   the class app
	 * @param annotation the annotation
	 * @return the sets the
	 */
	public static Set<Field> fields(Class<?> classApp, Class<? extends Annotation> annotation) {
		Set<Field> listField = new HashSet<>();
		Set<Field> skipField = new HashSet<>();
		do {
			for (Field field : classApp.getDeclaredFields()) {
				if (field.isAnnotationPresent(annotation) && !skipField.contains(field))
					listField.add(field);
				skipField.add(field);
			}
			classApp = classApp.getSuperclass();
		} while (classApp != null && !classApp.getName().equals(Object.class.getName()));
		return listField;
	}

	/**
	 * Map fields.
	 *
	 * @param classApp the class app
	 * @return the map
	 */
	public static Map<String, Field> mapFields(Class<?> classApp) {
		Map<String, Field> mapField = new HashMap<>();
		do {
			for (Field field : classApp.getDeclaredFields())
				if (!mapField.containsKey(field.getName()))
					mapField.put(field.getName(), field);
			classApp = classApp.getSuperclass();
		} while (classApp != null && !classApp.getName().equals(Object.class.getName()));
		return mapField;
	}

	/**
	 * Map methods.
	 *
	 * @param classApp the class app
	 * @return the map
	 */
	public static Map<String, LinkedHashSet<Method>> mapMethods(Class<?> classApp) {
		Map<String, LinkedHashSet<Method>> mapMethod = new HashMap<>();
		do {
			for (Method method : classApp.getMethods()) {
				if (!mapMethod.containsKey(method.getName()))
					mapMethod.put(method.getName(), new LinkedHashSet<>());
				mapMethod.get(method.getName()).add(method);
			}
			classApp = classApp.getSuperclass();
		} while (classApp != null && !classApp.getName().equals(Object.class.getName()));
		return mapMethod;
	}

	/**
	 * Methods.
	 *
	 * @param classApp the class app
	 * @return the sets the
	 */
	public static Set<Method> methods(Class<?> classApp) {
		Set<Method> methods = new HashSet<>();
		Set<MethodOverride> methodsOverride = new HashSet<>();
		do {
			for (Method method : classApp.getMethods()) {
				MethodOverride methodOverride = new MethodOverride(method.getName(), method.getParameterTypes());
				if (!methodsOverride.contains(methodOverride)) {
					methods.add(method);
					methodsOverride.add(methodOverride);
				}

			}

			classApp = classApp.getSuperclass();
		} while (classApp != null && !classApp.getName().equals(Object.class.getName()));
		return methods;
	}

	/**
	 * Gets the method.
	 *
	 * @param mapMethod      the map method
	 * @param methodName     the method name
	 * @param classParameter the class parameter
	 * @return the method
	 */
	public static Method getMethod(Map<String, LinkedHashSet<Method>> mapMethod, String methodName, Class<?>... classParameter) {
		Set<Method> methods = mapMethod.get(methodName);
		if (CollectionUtils.isNotEmpty(methods)) {
			for (Method method : methods) {
				Class<?>[] parameterTypes = method.getParameterTypes();
				if (ArrayUtils.isEmpty(parameterTypes) && ArrayUtils.isEmpty(classParameter))
					return method;
				boolean check = true;
				if (ArrayUtils.isNotEmpty(parameterTypes) && ArrayUtils.isNotEmpty(classParameter) && parameterTypes.length == classParameter.length) {
					for (int i = 0; i < parameterTypes.length; i++)
						if (!parameterTypes[i].isAssignableFrom(classParameter[i])) {
							check = false;
							break;
						}
				} else
					continue;
				if (check)
					return method;
			}
		}
		return null;
	}

	/**
	 * Gets the method.
	 *
	 * @param mapMethod      the map method
	 * @param field          the field
	 * @param getSetType     the get set type
	 * @param classParameter the class parameter
	 * @return the method
	 */
	public static Method getMethod(Map<String, LinkedHashSet<Method>> mapMethod, Field field, GetSetType getSetType, Class<?>... classParameter) {
		String methodName = getSetType.name() + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
		return ReflectionCommons.getMethod(mapMethod, methodName, classParameter);

	}

	/**
	 * Extract variables.
	 *
	 * @param input the input
	 * @return the sets the
	 */
	public static Set<String> extractVariables(String input) {
		Matcher matcher = pattern.matcher(input);
		Set<String> variables = new HashSet<>();
		while (matcher.find())
			variables.add(matcher.group(1));
		return variables;
	}

}
