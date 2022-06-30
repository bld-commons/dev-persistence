/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.utils.ReflectionCommons.java
 */
package bld.commons.reflection.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.CalendarConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import bld.commons.reflection.annotations.DateFilter;
import bld.commons.reflection.annotations.FilterNullValue;
import bld.commons.reflection.annotations.IgnoreMapping;
import bld.commons.reflection.annotations.LikeString;
import bld.commons.reflection.annotations.ListFilter;
import bld.commons.reflection.model.FilterParameter;
import bld.commons.reflection.model.QueryFilter;
import bld.commons.reflection.type.GetSetType;

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
	 * @param <T>         the generic type
	 * @param <ID>        the generic type
	 * @param queryFilter the query filter
	 * @return the query filter
	 */
	public <T, ID> QueryFilter<T, ID> dataToMap(QueryFilter<T, ID> queryFilter) {

		Map<String, Object> mapParameters = new HashMap<String, Object>();
		Set<String> checkNullable = new HashSet<>();

		FilterParameter obj = queryFilter.getFilterParameter();

		if (obj != null) {
			Set<Field> fields = ReflectionCommons.getListField(obj.getClass());
			Map<String, LinkedHashSet<Method>> mapMethod = ReflectionCommons.getMapMethod(obj.getClass());
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
								DateFilter dateFilter = method.isAnnotationPresent(DateFilter.class) ? method.getAnnotation(DateFilter.class) : field.getAnnotation(DateFilter.class);
								LikeString likeString = method.isAnnotationPresent(LikeString.class) ? method.getAnnotation(LikeString.class) : field.getAnnotation(LikeString.class);
								if (dateFilter != null) {
									if (value instanceof Calendar)
										value = DateUtils.sumDate((Calendar) value, dateFilter.addYear(), dateFilter.addMonth(),dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
									else if (value instanceof Date)
										value = DateUtils.sumDate((Date) value, dateFilter.addYear(), dateFilter.addMonth(),dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
									else if (value instanceof Timestamp)
										value = DateUtils.sumDate((Timestamp) value, dateFilter.addYear(), dateFilter.addMonth(),dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());

								}else if(likeString != null && value instanceof String) {
										switch (likeString.likeType()) {
										case LEFT:
											value = "%" + value;
											break;
										case LEFT_RIGHT:
											value = "%" + value + "%";
											break;
										case RIGHT:
											value = value + "%";
											break;
										case NONE:
											break;
										default:
											value = "%" + value + "%";
											break;
										}
										switch(likeString.upperLowerType()) {
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
									
								if (value instanceof Boolean && (Boolean)value && field.isAnnotationPresent(ListFilter.class))
									checkNullable.add(field.getName());
								else if (value.getClass().isArray()) {
									Object[] array = (Object[]) value;
									mapParameters.put(field.getName(), Arrays.asList(array));
								} else
									mapParameters.put(field.getName(), value);
							} else if (field.isAnnotationPresent(FilterNullValue.class) && field.getAnnotation(FilterNullValue.class).value() || method.isAnnotationPresent(FilterNullValue.class) && method.getAnnotation(FilterNullValue.class).value())
								mapParameters.put(field.getName(), null);
						} catch (Exception e) {
							logger.warn("Errore durante la conversione dei dati in mappa");
						}
					}
				}
			}
			queryFilter.getMapParameters().putAll(mapParameters);
			queryFilter.getNullables().addAll(checkNullable);

		}
		return queryFilter;
	}

	/**
	 * Reflection.
	 *
	 * @param <T>       the generic type
	 * @param t         the t
	 * @param mapResult the map result
	 */
	public <T> void reflection(T t, Map<String, Object> mapResult) {
		Map<String, Object> mapResultApp = new HashMap<>();
		for (String keyResult : mapResult.keySet()) {
			String nameField = CamelCaseUtils.camelCase(keyResult, true);
			mapResultApp.put(nameField, mapResult.get(keyResult));
		}
		BeanUtilsBean beanUtilsBean = BeanUtilsBean.getInstance();
		Converter converter = new DateConverter();
		beanUtilsBean.getConvertUtils().register(converter, Date.class);
		converter = new CalendarConverter(null);
		beanUtilsBean.getConvertUtils().register(converter, Calendar.class);
		try {
			beanUtilsBean.copyProperties(t, mapResultApp);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}

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
		return getGenericTypeClass(entity, 0);
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
		ParameterizedType parameterizedType = null;
		try {
			parameterizedType = (ParameterizedType) entity.getClass().getGenericSuperclass();
		} catch (Exception e) {
			parameterizedType = (ParameterizedType) entity.getClass().getSuperclass().getGenericSuperclass();
		}
		Class<T> clazz = (Class<T>) parameterizedType.getActualTypeArguments()[i];
		return clazz;
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
	 * Gets the list field.
	 *
	 * @param classApp the class app
	 * @return the list field
	 */
	public static Set<Field> getListField(Class<?> classApp) {
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
	 * Gets the map field.
	 *
	 * @param classApp the class app
	 * @return the map field
	 */
	public static Map<String, Field> getMapField(Class<?> classApp) {
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
	 * Gets the map method.
	 *
	 * @param classApp the class app
	 * @return the map method
	 */
	public static Map<String, LinkedHashSet<Method>> getMapMethod(Class<?> classApp) {
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
	 * Gets the method.
	 *
	 * @param mapMethod the map method
	 * @param methodName the method name
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
	 * @param mapMethod the map method
	 * @param field the field
	 * @param getSetType the get set type
	 * @param classParameter the class parameter
	 * @return the method
	 */
	public static Method getMethod(Map<String, LinkedHashSet<Method>> mapMethod, Field field, GetSetType getSetType, Class<?>... classParameter) {
		String methodName = getSetType.name() + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
		return ReflectionCommons.getMethod(mapMethod, methodName, classParameter);

	}

}