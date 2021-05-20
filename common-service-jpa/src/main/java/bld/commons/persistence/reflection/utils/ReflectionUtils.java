/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.utils.ReflectionUtils.java
 */
package bld.commons.persistence.reflection.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.beanutils.converters.CalendarConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import bld.commons.persistence.reflection.annotations.DateFilter;
import bld.commons.persistence.reflection.annotations.IgnoreMapping;
import bld.commons.persistence.reflection.annotations.LikeString;
import bld.commons.persistence.reflection.annotations.ListFilter;
import bld.commons.persistence.reflection.model.FilterParameter;
import bld.commons.persistence.reflection.model.QueryFilter;

/**
 * The Class ReflectionUtils.
 */
@Component
@SuppressWarnings("unchecked")
public class ReflectionUtils {

	/** The Constant PK. */
	public static final String PK = "PK";

	/** The context. */
	@Autowired
	private ApplicationContext applicationContext;

	/** The logger. */
	private final static Log logger = LogFactory.getLog(ReflectionUtils.class);

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
			Set<Field> fields = ReflectionUtils.getListField(obj.getClass());

			for (Field field : fields) {
				if (!field.isAnnotationPresent(IgnoreMapping.class)) {
					try {
						Object value = PropertyUtils.getProperty(obj, field.getName());
						if (value != null && value instanceof String && StringUtils.isBlank((String) value))
							value = null;
						if (value != null) {
							if (value instanceof Date && field.isAnnotationPresent(DateFilter.class)) {
								DateFilter dateFilter = field.getAnnotation(DateFilter.class);
								switch (dateFilter.dateType()) {
								case CALENDAR:
									Calendar calendar = DateUtils.dateToCalendar((Date) value);
									value = DateUtils.sumDate(calendar, dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
									break;
								case DATE:
									value = DateUtils.sumDate((Date) value, dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
									break;
								case TIMESTAMP:
									Date date = DateUtils.sumDate((Date) value, dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
									value = DateUtils.dateToTimestamp(date);
									break;
								default:
									break;

								}

							} else if (value instanceof String && field.isAnnotationPresent(LikeString.class)) {
								LikeString likeString = field.getAnnotation(LikeString.class);
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
								case EQUAL:
									break;
								default:
									value = "%" + value + "%";
									break;
								}
								if (likeString.ignoreCase())
									value = ((String) value).toUpperCase();
							}
							if (value instanceof String && field.isAnnotationPresent(ListFilter.class))
								checkNullable.add(value.toString());
							else
								mapParameters.put(field.getName(), value);
						}
					} catch (Exception e) {
						logger.warn("Errore durante la conversione dei dati in mappa");
					}
				}
			}
			queryFilter.getMapParameters().putAll(mapParameters);
			queryFilter.getCheckNullable().addAll(checkNullable);

		}
		return queryFilter;
	}

	/**
	 * Reflection.
	 *
	 * @param <T>       the generic type
	 * @param t         the t
	 * @param mapResult the map result
	 * @throws Exception the exception
	 */
	public <T> void reflection(T t, Map<String, Object> mapResult) throws Exception {
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
		BeanUtils.copyProperties(t, mapResultApp);

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

}
