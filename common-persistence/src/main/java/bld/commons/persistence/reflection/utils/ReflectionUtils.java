/**************************************************************************
 * 
 * Copyright 2018 (C) DXC Technology
 * 
 * Author      : DXC Technology
 * Project Name: pmg-common
 * Package     : com.bld.pmg.utils
 * File Name   : ReflectionUtils.java
 *
 ***************************************************************************/
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
import java.util.List;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import bld.commons.persistence.reflection.annotations.ExcludeFromMap;
import bld.commons.persistence.reflection.annotations.LikeString;
import bld.commons.persistence.reflection.annotations.ListFilter;
import bld.commons.persistence.reflection.annotations.ToCalendar;
import bld.commons.persistence.reflection.model.QueryFilter;

/**
 * The Class ReflectionUtils.
 */
@Component
@SuppressWarnings("unchecked")
public class ReflectionUtils {

	/** The Constant PK. */
	public static final String PK = "PK";

	/** The base package persistence. */
	@Value("#{'${com.bld.common.base.package.persistence}'.split(',')}")
	private List<String> basePackagePersistence;

	/** The base package project. */
	@Value("${com.bld.common.base.package.project:com.bld}")
	private String basePackageProject;

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
	public void saveGeneric(Object valore, Class<?> classCampoDestinatario)
			throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String nomeClasse = classCampoDestinatario.getSimpleName();
		String nomeClasseServiceImpl = getBeanName(nomeClasse, SERVICE_IMPL);
		Object oggettoServiceImpl = applicationContext.getBean(nomeClasseServiceImpl);
		Method metodo = oggettoServiceImpl.getClass().getMethod(SAVE, valore.getClass());
		metodo.invoke(oggettoServiceImpl, valore);

	}

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
	 * @param obj         the obj
	 * @param queryFilter the query filter
	 * @return the map
	 */
	public <T, ID> QueryFilter<T, ID> dataToMap(Object obj, QueryFilter<T, ID> queryFilter) {

		Map<String, Object> mapParameters = new HashMap<String, Object>();
		Set<String> checkNullable = new HashSet<>();

		Set<Field> campi = ReflectionUtils.getListField(obj.getClass());

		for (Field f : campi) {
			if (!f.isAnnotationPresent(ExcludeFromMap.class)) {
				try {
					Object value = BeanUtils.getProperty(obj, f.getName());
					if (value != null && value instanceof String && StringUtils.isBlank((String) value))
						value = null;
					if (value != null) {
						if (value instanceof Date && f.isAnnotationPresent(ToCalendar.class))
							value = DateUtils.dateToCalendar((Date) value);
						else if (value instanceof String && f.isAnnotationPresent(LikeString.class)) {
							LikeString likeString = f.getAnnotation(LikeString.class);
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
						if (value instanceof String && f.isAnnotationPresent(ListFilter.class))
							checkNullable.add(value.toString());
						else
							mapParameters.put(f.getName(), value);
					}
				} catch (Exception e) {
					logger.warn("Errore durante la conversione dei dati in mappa");
				}
			}
		}
		queryFilter.setMapParameters(mapParameters);
		queryFilter.setCheckNullable(checkNullable);
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
	 * Gets the t class.
	 *
	 * @param <T>    the generic type
	 * @param entity the entity
	 * @return the t class
	 */
	public static <T> Class<T> getTClass(Object entity) {
		return getTClass(entity, 0);
	}

	/**
	 * Gets the t class.
	 *
	 * @param <T>    the generic type
	 * @param entity the entity
	 * @param i      the i
	 * @return the t class
	 */
	public static <T> Class<T> getTClass(Object entity, int i) {
		ParameterizedType parameterizedType = null;
		try {
			parameterizedType = (ParameterizedType) entity.getClass().getGenericSuperclass();
		} catch (Exception e) {
			parameterizedType = (ParameterizedType) entity.getClass().getSuperclass().getGenericSuperclass();
		}
		Class<T> classT = (Class<T>) parameterizedType.getActualTypeArguments()[i];
		return classT;
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

}
