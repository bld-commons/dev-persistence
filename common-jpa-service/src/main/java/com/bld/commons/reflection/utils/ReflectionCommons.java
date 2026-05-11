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
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
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
import com.bld.commons.reflection.annotations.ConditionTrigger;
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

	/** Cache: fields per class (including superclasses up to Object). */
	private static final ClassValue<Set<Field>> CACHE_FIELDS = new ClassValue<>() {
		@Override
		protected Set<Field> computeValue(Class<?> type) {
			Set<Field> listField = new HashSet<>();
			Class<?> c = type;
			do {
				for (Field field : c.getDeclaredFields())
					listField.add(field);
				c = c.getSuperclass();
			} while (c != null && c != Object.class);
			return listField;
		}
	};

	/** Cache: fields per class filtered by annotation type. */
	private static final ClassValue<ConcurrentHashMap<Class<? extends Annotation>, Set<Field>>> CACHE_FIELDS_BY_ANNOTATION = new ClassValue<>() {
		@Override
		protected ConcurrentHashMap<Class<? extends Annotation>, Set<Field>> computeValue(Class<?> type) {
			return new ConcurrentHashMap<>();
		}
	};

	/** Cache: map name->field per class. */
	private static final ClassValue<Map<String, Field>> CACHE_MAP_FIELDS = new ClassValue<>() {
		@Override
		protected Map<String, Field> computeValue(Class<?> type) {
			Map<String, Field> mapField = new HashMap<>();
			Class<?> c = type;
			do {
				for (Field field : c.getDeclaredFields())
					if (!mapField.containsKey(field.getName()))
						mapField.put(field.getName(), field);
				c = c.getSuperclass();
			} while (c != null && c != Object.class);
			return mapField;
		}
	};

	/** Cache: map name->methods per class. */
	private static final ClassValue<Map<String, LinkedHashSet<Method>>> CACHE_MAP_METHODS = new ClassValue<>() {
		@Override
		protected Map<String, LinkedHashSet<Method>> computeValue(Class<?> type) {
			Map<String, LinkedHashSet<Method>> mapMethod = new HashMap<>();
			Class<?> c = type;
			do {
				for (Method method : c.getMethods()) {
					if (!mapMethod.containsKey(method.getName()))
						mapMethod.put(method.getName(), new LinkedHashSet<>());
					mapMethod.get(method.getName()).add(method);
				}
				c = c.getSuperclass();
			} while (c != null && c != Object.class);
			return mapMethod;
		}
	};

	/** Cache: unique methods per class (override-aware). */
	private static final ClassValue<Set<Method>> CACHE_METHODS = new ClassValue<>() {
		@Override
		protected Set<Method> computeValue(Class<?> type) {
			Set<Method> methods = new HashSet<>();
			Set<MethodOverride> methodsOverride = new HashSet<>();
			Class<?> c = type;
			do {
				for (Method method : c.getMethods()) {
					MethodOverride methodOverride = new MethodOverride(method.getName(), method.getParameterTypes());
					if (!methodsOverride.contains(methodOverride)) {
						methods.add(method);
						methodsOverride.add(methodOverride);
					}
				}
				c = c.getSuperclass();
			} while (c != null && c != Object.class);
			return methods;
		}
	};

	/** Cached BeanUtilsBean — enum-aware ConvertUtilsBean, registered once. */
	private static final BeanUtilsBean BEAN_UTILS = createBeanUtils();

	/** Cache: original column name -> camelCase field name. */
	private static final ConcurrentHashMap<String, String> CACHE_CAMEL_CASE = new ConcurrentHashMap<>();

	private static BeanUtilsBean createBeanUtils() {
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
		return beanUtils;
	}

	/** Per-field precomputed metadata for dataToMap hot path. */
	private static final class FieldMeta {
		final Method getter;
		final String fieldName;
		final IgnoreMapping ignore;
		final DateFilter date;
		final LikeString like;
		final TupleComparison tuple;
		final boolean conditionTrigger;
		final boolean filterNullValue;
		final ConditionsZones zones;
		final BindableType<?> bindableType;

		FieldMeta(Field field, Method getter) {
			this.getter = getter;
			this.fieldName = field.getName();
			this.ignore = pickAnnotation(IgnoreMapping.class, getter, field);
			this.date = pickAnnotation(DateFilter.class, getter, field);
			this.like = pickAnnotation(LikeString.class, getter, field);
			this.tuple = field.getAnnotation(TupleComparison.class);
			this.conditionTrigger = field.isAnnotationPresent(ConditionTrigger.class);
			FilterNullValue fnvField = field.getAnnotation(FilterNullValue.class);
			FilterNullValue fnvMethod = getter != null ? getter.getAnnotation(FilterNullValue.class) : null;
			this.filterNullValue = (fnvField != null && fnvField.value()) || (fnvMethod != null && fnvMethod.value());
			this.zones = pickAnnotation(ConditionsZones.class, getter, field);
			this.bindableType = mapType.get(field.getType());
		}
	}

	/** Per-field precomputed metadata for mapResultSet hot path. */
	private static final class ResultFieldMeta {
		final String fieldName;
		final String mappedKey;
		final boolean isResultMapping;
		final Class<?> fieldType;

		ResultFieldMeta(Field field) {
			this.fieldName = field.getName();
			this.fieldType = field.getType();
			this.isResultMapping = field.isAnnotationPresent(ResultMapping.class);
			FieldMapping fm = field.getAnnotation(FieldMapping.class);
			this.mappedKey = fm != null ? fm.value() : field.getName();
		}
	}

	private static <A extends Annotation> A pickAnnotation(Class<A> annClass, Method m, Field f) {
		if (m != null && m.isAnnotationPresent(annClass))
			return m.getAnnotation(annClass);
		return f.getAnnotation(annClass);
	}

	/** Cache: per-class precomputed FieldMeta list (only fields with a getter). */
	private static final ClassValue<List<FieldMeta>> CACHE_FIELD_META = new ClassValue<>() {
		@Override
		protected List<FieldMeta> computeValue(Class<?> type) {
			Set<Field> fs = fields(type);
			Map<String, LinkedHashSet<Method>> mapMethod = mapMethods(type);
			List<FieldMeta> list = new ArrayList<>(fs.size());
			for (Field field : fs) {
				Method getter = getMethod(mapMethod, field, GetSetType.get);
				if (getter != null)
					list.add(new FieldMeta(field, getter));
			}
			return list;
		}
	};

	/** Cache: per-class precomputed ResultFieldMeta list (skips @IgnoreResultSet). */
	private static final ClassValue<List<ResultFieldMeta>> CACHE_RESULT_META = new ClassValue<>() {
		@Override
		protected List<ResultFieldMeta> computeValue(Class<?> type) {
			Set<Field> fs = fields(type);
			List<ResultFieldMeta> list = new ArrayList<>(fs.size());
			for (Field f : fs)
				if (!f.isAnnotationPresent(IgnoreResultSet.class))
					list.add(new ResultFieldMeta(f));
			return list;
		}
	};

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
		map.put(Instant.class, StandardBasicTypes.INSTANT);
		map.put(LocalDate.class, StandardBasicTypes.LOCAL_DATE);
		map.put(LocalDateTime.class, StandardBasicTypes.LOCAL_DATE_TIME);
		map.put(OffsetDateTime.class, StandardBasicTypes.OFFSET_DATE_TIME);
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
			for (FieldMeta meta : CACHE_FIELD_META.get(obj.getClass())) {
				if (meta.ignore != null && meta.ignore.value())
					continue;
				try {
					Object value = meta.getter.invoke(obj);
					if (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value))
						value = null;
					if (value != null && value instanceof String && StringUtils.isBlank((String) value))
						value = null;
					if (value != null) {
						value = value(value, meta.date, meta.like);
						if (meta.tuple != null) {
							TupleParameter tupleParameter = this.getTupleParameter(meta.tuple, value);
							queryParameter.addParameter(meta.fieldName, tupleParameter);
						} else if (value instanceof Boolean && (Boolean) value && meta.conditionTrigger)
							queryParameter.addNullable(meta.fieldName);
						else if (value.getClass().isArray()) {
							Object[] array = (Object[]) value;
							queryParameter.addParameter(meta.fieldName, Arrays.asList(array));
						} else
							queryParameter.addParameter(meta.fieldName, value);
					} else if (meta.filterNullValue)
						queryParameter.addParameter(meta.fieldName, initTypedParameterValue(meta.bindableType, value));
				} catch (Exception e) {
					logger.warn("Error converting data to map");
				}
			}
		}
		return queryParameter;
	}

	private TupleParameter getTupleParameter(TupleComparison tupleComparison, Object value) {
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

	public static Object value(Object value, DateFilter dateFilter, LikeString likeString) {
		if (dateFilter != null) {
			if (value instanceof Calendar)
				value = DateUtils.sumDate((Calendar) ((Calendar) value).clone(), dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
			else if (value instanceof Date)
				value = DateUtils.sumDate((Date) ((Date) value).clone(), dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
			else if (value instanceof Timestamp)
				value = DateUtils.sumDate((Timestamp) ((Timestamp) value).clone(), dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
			else if (value instanceof Instant) {
				Date tempDate = Date.from((Instant) value);
				tempDate = DateUtils.sumDate(tempDate, dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
				value = tempDate.toInstant();
			} else if (value instanceof LocalDate) {
				Date tempDate = Date.from(((LocalDate) value).atStartOfDay(ZoneId.systemDefault()).toInstant());
				tempDate = DateUtils.sumDate(tempDate, dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
				value = tempDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			} else if (value instanceof LocalDateTime) {
				Date tempDate = Date.from(((LocalDateTime) value).atZone(ZoneId.systemDefault()).toInstant());
				tempDate = DateUtils.sumDate(tempDate, dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
				value = tempDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			} else if (value instanceof OffsetDateTime) {
				OffsetDateTime odt = (OffsetDateTime) value;
				Date tempDate = Date.from(odt.toInstant());
				tempDate = DateUtils.sumDate(tempDate, dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(), dateFilter.addMinute(), dateFilter.addSecond());
				value = tempDate.toInstant().atOffset(odt.getOffset());
			}

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
			for (FieldMeta meta : CACHE_FIELD_META.get(obj.getClass())) {
				if (meta.ignore != null && meta.ignore.value())
					continue;
				try {
					Object value = meta.getter.invoke(obj);
					ConditionsZones conditionsZones = meta.zones;
					if (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value))
						value = null;
					if (value != null && value instanceof String && StringUtils.isBlank((String) value))
						value = null;
					if (value != null) {
						value = value(value, meta.date, meta.like);
						if (meta.tuple != null) {
							TupleParameter tupleParameter = this.getTupleParameter(meta.tuple, value);
							queryParameter.addParameter(meta.fieldName, tupleParameter, conditionsZones);
						} else if (value instanceof Boolean && (Boolean) value && meta.conditionTrigger)
							queryParameter.addNullable(meta.fieldName, conditionsZones);
						else if (value.getClass().isArray()) {
							Object[] array = (Object[]) value;
							queryParameter.addParameter(meta.fieldName, Arrays.asList(array), conditionsZones);
						} else
							queryParameter.addParameter(meta.fieldName, value, conditionsZones);
					} else if (meta.filterNullValue)
						queryParameter.addParameter(meta.fieldName, initTypedParameterValue(meta.bindableType, value), conditionsZones);
					else if (conditionsZones != null)
						queryParameter.addEmptyZones(conditionsZones);
				} catch (Exception e) {
					logger.warn("Error converting data to map");
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
		Map<String, Object> mapRow = new HashMap<>(mapResult.size() * 2);
		for (Map.Entry<String, Object> entry : mapResult.entrySet()) {
			String fieldName = CACHE_CAMEL_CASE.computeIfAbsent(entry.getKey(), k -> CamelCaseUtils.camelCase(k, true));
			mapRow.put(fieldName, entry.getValue());
		}
		try {
			return mapResultSet(classT, mapRow, BEAN_UTILS);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
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
		boolean isEmpty = true;
		for (ResultFieldMeta meta : CACHE_RESULT_META.get(classT)) {
			if (meta.isResultMapping) {
				Object value = mapResultSet(meta.fieldType, mapRow, beanUtils);
				if (value != null) {
					isEmpty = false;
					beanUtils.setProperty(t, meta.fieldName, value);
				}
			} else if (mapRow.containsKey(meta.mappedKey)) {
				Object value = mapRow.get(meta.mappedKey);
				if (value != null) {
					isEmpty = false;
					beanUtils.setProperty(t, meta.fieldName, value);
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
		for (Field f : fields(obj.getClass())) {
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
		Type generic = clazz.getGenericSuperclass();
		ParameterizedType parameterizedType = generic instanceof ParameterizedType
				? (ParameterizedType) generic
				: (ParameterizedType) clazz.getSuperclass().getGenericSuperclass();
		return (Class<T>) parameterizedType.getActualTypeArguments()[i];
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
		return join.trim().replaceAll(" +", " ");
	}

	/**
	 * Fields.
	 *
	 * @param classApp the class app
	 * @return the sets the
	 */
	public static Set<Field> fields(Class<?> classApp) {
		return CACHE_FIELDS.get(classApp);
	}

	/**
	 * Fields.
	 *
	 * @param classApp   the class app
	 * @param annotation the annotation
	 * @return the sets the
	 */
	public static Set<Field> fields(Class<?> classApp, Class<? extends Annotation> annotation) {
		return CACHE_FIELDS_BY_ANNOTATION.get(classApp).computeIfAbsent(annotation, ann -> {
			Set<Field> listField = new HashSet<>();
			for (Field field : fields(classApp))
				if (field.isAnnotationPresent(ann))
					listField.add(field);
			return listField;
		});
	}

	/**
	 * Map fields.
	 *
	 * @param classApp the class app
	 * @return the map
	 */
	public static Map<String, Field> mapFields(Class<?> classApp) {
		return CACHE_MAP_FIELDS.get(classApp);
	}

	/**
	 * Map methods.
	 *
	 * @param classApp the class app
	 * @return the map
	 */
	public static Map<String, LinkedHashSet<Method>> mapMethods(Class<?> classApp) {
		return CACHE_MAP_METHODS.get(classApp);
	}

	/**
	 * Methods.
	 *
	 * @param classApp the class app
	 * @return the sets the
	 */
	public static Set<Method> methods(Class<?> classApp) {
		return CACHE_METHODS.get(classApp);
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
