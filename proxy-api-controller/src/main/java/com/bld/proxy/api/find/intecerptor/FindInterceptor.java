package com.bld.proxy.api.find.intecerptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.bld.commons.utils.data.CollectionResponse;
import com.bld.commons.utils.data.ObjectResponse;
import com.bld.proxy.api.find.annotations.ApiBeforeRequest;
import com.bld.proxy.api.find.annotations.ApiFind;
import com.bld.proxy.api.find.annotations.ApiMapper;
import com.bld.proxy.api.find.config.ApiQuery;
import com.bld.proxy.api.find.config.DefaultOrderBy;
import com.bld.proxy.api.find.data.ApiMethod;
import com.bld.proxy.api.find.data.ParameterDetails;
import com.bld.proxy.api.find.exception.ApiFindException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import bld.commons.reflection.annotations.ConditionsZones;
import bld.commons.reflection.annotations.DateFilter;
import bld.commons.reflection.annotations.FilterNullValue;
import bld.commons.reflection.annotations.IgnoreMapping;
import bld.commons.reflection.annotations.LikeString;
import bld.commons.reflection.annotations.ListFilter;
import bld.commons.reflection.model.BaseParameter;
import bld.commons.reflection.model.BaseQueryParameter;
import bld.commons.reflection.model.NativeQueryParameter;
import bld.commons.reflection.model.QueryParameter;
import bld.commons.reflection.type.OrderType;
import bld.commons.reflection.utils.ReflectionCommons;
import bld.commons.service.JpaServiceImpl;

@SuppressWarnings("unchecked")
public class FindInterceptor {

	private static final String PAGE_NUMBER = "pageNumber";

	private static final String PAGE_SIZE = "pageSize";

	private static final String ORDER_TYPE = "orderType";

	private static final String SORT_KEY = "sortKey";

	private final static Logger logger = LoggerFactory.getLogger(FindInterceptor.class);

	private final static List<String> PAGINATION_KEYS = Arrays.asList(SORT_KEY, ORDER_TYPE, PAGE_SIZE, PAGE_NUMBER);

	private final static List<Class<? extends Annotation>> REQUEST_ANNOTATIONS = Arrays.asList(RequestParam.class,
			RequestAttribute.class, PathVariable.class);

	private final ApplicationContext context;

	private final Map<String, Object> mapBean;

	private final ObjectMapper objMapper;

	public FindInterceptor(ApplicationContext context, Map<String, Object> mapBean, ObjectMapper objMapper) {
		super();
		this.context = context;
		this.mapBean = mapBean;
		this.objMapper = objMapper;
	}

	public <E, ID> Object find(Object obj, ApiMethod apiMethod) throws Throwable {

		Object response = null;
		try {
			apiMethod.setApiQuery(apiMethod.getMethod().getAnnotation(ApiQuery.class));
			ApiFind apiFind = apiMethod.getMethod().getAnnotation(ApiFind.class);
			if (apiFind == null)
				apiFind = apiMethod.getMethod().getDeclaringClass().getAnnotation(ApiFind.class);
			Class<?> entityClass = apiFind.entity();
			Class<?> idClass = apiFind.id();
			Class<?> outputClass = apiMethod.getMethod().getReturnType();
			this.getParameters(apiMethod.getMethod().getParameters(), apiMethod.getArgs(), apiMethod, RequestBody.class,
					AuthenticationPrincipal.class);
			Assert.isTrue(
					!(apiMethod.getApiQuery() != null && StringUtils.isBlank(apiMethod.getApiQuery().value())
							&& !apiMethod.getApiQuery().jpql()),
					"For native query the field \"value\" can not be blank into ApiQuery");

			if (apiMethod.getApiQuery() == null || apiMethod.getApiQuery().jpql())
				response = this.jpqlQuery(entityClass, idClass, outputClass, apiMethod);
			else {
				Class<?> modelClass = modelClass(apiMethod.getMethod());
				if (modelClass == null)
					modelClass = outputClass;
				response = this.nativeQuery(entityClass, idClass, outputClass, modelClass, apiMethod);
			}
		} catch (Exception e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw e;
		}

		return response;
	}

	private <E, ID, O> Object jpqlQuery(Class<E> entityClass, Class<ID> idClass, Class<O> outputClass,
			ApiMethod apiMethod) throws Exception {
		Object response = null;
		apiMethod.setApiMapper(apiMethod.getMethod().getAnnotation(ApiMapper.class));

		if (apiMethod.getApiMapper() == null)
			apiMethod.setApiMapper(apiMethod.getMethod().getDeclaringClass().getAnnotation(ApiMapper.class));
		QueryParameter<E, ID> queryParameter = new QueryParameter<>();
		addBodyParameter(queryParameter, apiMethod);
		queryParameter(queryParameter, apiMethod);
		this.addUserDetails(queryParameter, apiMethod);
		firstStep(queryParameter, apiMethod);
		JpaServiceImpl<E, ID> jpaService = jpaService(entityClass, idClass);
		Class<?> modelClass = modelClass(apiMethod.getMethod());
		if (Number.class.isAssignableFrom(outputClass))
			response = this.countByFilter(queryParameter, jpaService, apiMethod);
		else if (modelClass != null) {

			if (CollectionResponse.class.isAssignableFrom(outputClass))
				response = collectionResponse(queryParameter, jpaService, entityClass, modelClass, apiMethod);
			else if (Collection.class.isAssignableFrom(outputClass)) {
				response = collection(queryParameter, jpaService, entityClass,
						(Class<? extends Collection<?>>) outputClass, modelClass, apiMethod);
			} else if (ObjectResponse.class.isAssignableFrom(outputClass)) {
				if (Number.class.isAssignableFrom(modelClass))
					response = new ObjectResponse<>(this.countByFilter(queryParameter, jpaService, apiMethod));
				else
					response = new ObjectResponse<>(
							this.singleResultByFilter(queryParameter, jpaService, entityClass, modelClass, apiMethod));
			}
		} else
			response = this.singleResultByFilter(queryParameter, jpaService, entityClass, outputClass, apiMethod);

		return response;
	}

	private <ID, E> JpaServiceImpl<E, ID> jpaService(Class<E> entityClass, Class<ID> idClass) {
		return (JpaServiceImpl<E, ID>) mapBean.get(entityClass.getName() + " " + idClass.getName());
//		String[] beanNames = this.context.getBeanNamesForType(JpaServiceImpl.class);
//		for (String beanName : beanNames) {
//			Class<?>[] typeArguments = ResolvableType.forType(this.context.getType(beanName)).as(JpaServiceImpl.class).resolveGenerics();
//			if (typeArguments[0].getName().equals(entityClass.getName()) && idClass.getName().equals(typeArguments[1].getName())) {
//				JpaServiceImpl<E, ID> jpaService = (JpaServiceImpl<E, ID>) this.context.getBean(beanName);
//				return jpaService;
//			}
//
//		}
//		throw new ApiFindException("The " + JpaServiceImpl.class.getName() + "<" + entityClass.getName() + "," + idClass.getName() + "> bean not found");
	}

	private void addBodyParameter(BaseQueryParameter<?, ?> queryParameter, ApiMethod apiMethod) {
		if (apiMethod.getMap().containsKey(RequestBody.class)) {
			Assert.isTrue(apiMethod.getMap().get(RequestBody.class).getValue() instanceof BaseParameter,
					"The body must be \"BaseParameter\" type");
			queryParameter.setBaseParameter((BaseParameter) apiMethod.getMap().get(RequestBody.class).getValue());
		}
	}

	private <E, ID, O, M> Object nativeQuery(Class<E> entityClass, Class<ID> idClass, Class<O> outputClass,
			Class<M> modelClass, ApiMethod apiMethod) throws Exception {
		Object response = null;
		NativeQueryParameter<M, ID> queryParameter = new NativeQueryParameter<>(modelClass);
		this.addBodyParameter(queryParameter, apiMethod);
		this.queryParameter(queryParameter, apiMethod);
		this.addUserDetails(queryParameter, apiMethod);
		this.firstStep(queryParameter, apiMethod);
		JpaServiceImpl<E, ID> jpaService = jpaService(entityClass, idClass);
		if (Number.class.isAssignableFrom(outputClass))
			response = this.countByFilter(queryParameter, jpaService, apiMethod);
		else if (modelClass != null) {
			if (CollectionResponse.class.isAssignableFrom(outputClass))
				response = collectionResponse(queryParameter, jpaService, apiMethod);
			else if (Collection.class.isAssignableFrom(outputClass)) {
				response = collection(queryParameter, jpaService, (Class<? extends Collection<?>>) outputClass,
						apiMethod);
			} else if (ObjectResponse.class.isAssignableFrom(outputClass)) {
				if (Number.class.isAssignableFrom(modelClass))
					response = new ObjectResponse<>(this.countByFilter(queryParameter, jpaService, apiMethod));
				else
					response = new ObjectResponse<>(this.singreResultByFilter(queryParameter, jpaService, apiMethod));
			}
		} else
			response = this.singreResultByFilter(queryParameter, jpaService, apiMethod);
		return response;
	}

	private void getParameters(Parameter[] parameters, Object[] args, ApiMethod apiMethod,
			Class<? extends Annotation>... annotations) {
		if (ArrayUtils.isNotEmpty(parameters))
			for (int i = 0; i < parameters.length; i++)
				for (Class<? extends Annotation> annotation : annotations)
					if (parameters[i].isAnnotationPresent(annotation) && ignoreMapping(parameters[i]))
						apiMethod.getMap().put(annotation, new ParameterDetails(parameters[i], args[i], i));

	}

	private Class<?> modelClass(Method method) throws ClassNotFoundException {
		String genericReturnType = method.getGenericReturnType().getTypeName();
		if (!(genericReturnType.contains("<") && genericReturnType.contains(">")))
			return null;
		String genericType = genericReturnType.substring(genericReturnType.indexOf("<") + 1,
				genericReturnType.lastIndexOf(">"));
		if (genericType.contains("<"))
			genericType = genericType.substring(0, genericType.indexOf("<"));
		return Class.forName(genericType);
	}

	private boolean ignoreMapping(Parameter parameter) {
		return !parameter.isAnnotationPresent(IgnoreMapping.class) || parameter.isAnnotationPresent(IgnoreMapping.class)
				&& !parameter.getAnnotation(IgnoreMapping.class).value();
	}

	private void addUserDetails(NativeQueryParameter<?, ?> queryParameter, ApiMethod apiMethod) {
		ParameterDetails userDetails = apiMethod.getMap().get(AuthenticationPrincipal.class);
		if (userDetails != null) {
			UserDetails user = ((UserDetails) userDetails.getValue());
			String key = "username";
			String username = user.getUsername();
			if (userDetails.getParameter().isAnnotationPresent(Param.class))
				key = userDetails.getParameter().getAnnotation(Param.class).value();
			if (userDetails.getParameter().isAnnotationPresent(LikeString.class))
				username = (String) ReflectionCommons.value(username, null,
						userDetails.getParameter().getAnnotation(LikeString.class));
			ConditionsZones conditionsZones = userDetails.getParameter().getAnnotation(ConditionsZones.class);
			queryParameter.addParameter(key, username, conditionsZones);

		}

	}

	private void addUserDetails(QueryParameter<?, ?> queryParameter, ApiMethod apiMethod) {
		ParameterDetails userDetails = apiMethod.getMap().get(AuthenticationPrincipal.class);
		if (userDetails != null) {
			UserDetails user = ((UserDetails) userDetails.getValue());
			String key = "username";
			String username = user.getUsername();
			if (userDetails.getParameter().isAnnotationPresent(Param.class))
				key = userDetails.getParameter().getAnnotation(Param.class).value();
			if (userDetails.getParameter().isAnnotationPresent(LikeString.class))
				username = (String) ReflectionCommons.value(username, null,
						userDetails.getParameter().getAnnotation(LikeString.class));
			queryParameter.addParameter(key, username);

		}

	}

	private void firstStep(BaseQueryParameter<?, ?> queryParameter, ApiMethod apiMethod) throws Exception {
		if (apiMethod.getMethod().isAnnotationPresent(ApiBeforeRequest.class)) {
			ApiBeforeRequest beforeRequest = apiMethod.getMethod().getAnnotation(ApiBeforeRequest.class);
			Object bean = this.context.getBean(beforeRequest.bean());
			Class<?>[] paramaterClasses = new Class<?>[apiMethod.getArgs().length + 1];
			Object[] parameters = new Object[apiMethod.getArgs().length + 1];
			int i = 0;
			for (Object arg : apiMethod.getArgs()) {
				paramaterClasses[i] = arg.getClass();
				parameters[i] = arg;
				i++;
			}

			parameters[i] = queryParameter;
			paramaterClasses[i] = queryParameter.getClass();
			Method m = beforeRequest.bean().getMethod(beforeRequest.method(), paramaterClasses);
			m.invoke(bean, parameters);
		}
	}

	private boolean isRequestAnnotationPresent(Parameter parameter) {
		for (Class<? extends Annotation> annotation : REQUEST_ANNOTATIONS)
			if (parameter.isAnnotationPresent(annotation))
				return true;
		return false;
	}

	private void queryParameter(NativeQueryParameter<?, ?> queryParameter, ApiMethod apiMethod) {
		if (ArrayUtils.isNotEmpty(apiMethod.getArgs())) {
			Map<String, Object> mapPagination = new HashMap<>();
			for (int i = 0; i < apiMethod.getArgs().length; i++) {
				Parameter parameter = apiMethod.getMethod().getParameters()[i];
				if (this.isRequestAnnotationPresent(parameter) && ignoreMapping(parameter)) {
					String name = parameter.getName();
					Object value = apiMethod.getArgs()[i];
					if (!PAGINATION_KEYS.contains(name)) {
						try {
							ConditionsZones conditionsZones = parameter.getAnnotation(ConditionsZones.class);
							if (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value))
								value = null;
							if (value != null && value instanceof String && StringUtils.isBlank((String) value))
								value = null;
							if (value != null) {
								DateFilter dateFilter = parameter.getAnnotation(DateFilter.class);
								LikeString likeString = parameter.getAnnotation(LikeString.class);
								value = ReflectionCommons.value(value, dateFilter, likeString);

								if (value instanceof Boolean && (Boolean) value
										&& parameter.isAnnotationPresent(ListFilter.class))
									queryParameter.addNullable(name, conditionsZones);
								else if (value.getClass().isArray()) {
									Object[] array = (Object[]) value;
									queryParameter.addParameter(name, Arrays.asList(array), conditionsZones);
								} else
									queryParameter.addParameter(name, value, conditionsZones);
							} else if (parameter.isAnnotationPresent(FilterNullValue.class)
									&& parameter.getAnnotation(FilterNullValue.class).value())
								queryParameter.addParameter(name,
										ReflectionCommons.initTypedParameterValue(
												ReflectionCommons.mapType.get(parameter.getType()), value),
										conditionsZones);
							else if (conditionsZones != null)
								queryParameter.addEmptyZones(conditionsZones);
						} catch (Exception e) {
							logger.warn("Error converting data to map");
						}
					} else {
						mapPagination.put(name, value);
					}
				}
				queryParameter.addOrderBy((String) mapPagination.get(SORT_KEY),
						(OrderType) mapPagination.get(ORDER_TYPE));
				queryParameter.setPageable((Integer) mapPagination.get(PAGE_NUMBER),
						(Integer) mapPagination.get(PAGE_SIZE));
			}
		}
	}

	private void queryParameter(QueryParameter<?, ?> queryParameter, ApiMethod apiMethod) throws Exception {
		if (ArrayUtils.isNotEmpty(apiMethod.getArgs())) {
			Map<String, Object> mapPagination = new HashMap<>();
			for (int i = 0; i < apiMethod.getArgs().length; i++) {
				Parameter parameter = apiMethod.getMethod().getParameters()[i];
				if (this.isRequestAnnotationPresent(parameter) && ignoreMapping(parameter)) {
					String name = parameter.getName();
					Object value = apiMethod.getArgs()[i];
					if (!PAGINATION_KEYS.contains(name)) {
						if (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value))
							value = null;
						if (value != null && value instanceof String && StringUtils.isBlank((String) value))
							value = null;
						if (value != null) {
							DateFilter dateFilter = parameter.getAnnotation(DateFilter.class);
							LikeString likeString = parameter.getAnnotation(LikeString.class);
							value = ReflectionCommons.value(value, dateFilter, likeString);

							if (value instanceof Boolean && (Boolean) value
									&& parameter.isAnnotationPresent(ListFilter.class))
								queryParameter.addNullable(name);
							else if (value.getClass().isArray()) {
								Object[] array = (Object[]) value;
								queryParameter.addParameter(name, Arrays.asList(array));
							} else
								queryParameter.addParameter(name, value);
						} else if (parameter.isAnnotationPresent(FilterNullValue.class)
								&& parameter.getAnnotation(FilterNullValue.class).value())
							queryParameter.addParameter(name, ReflectionCommons.initTypedParameterValue(
									ReflectionCommons.mapType.get(parameter.getType()), value));
					} else
						mapPagination.put(name, value);
				}
				queryParameter.addOrderBy((String) mapPagination.get(SORT_KEY),
						(OrderType) mapPagination.get(ORDER_TYPE));
				queryParameter.setPageable((Integer) mapPagination.get(PAGE_NUMBER),
						(Integer) mapPagination.get(PAGE_SIZE));
			}
		}

	}

	private <ID, E, M> CollectionResponse<M> collectionResponse(QueryParameter<E, ID> queryParameter,
			JpaServiceImpl<E, ID> jpaService, Class<E> entityClass, Class<M> modelClass, ApiMethod apiMethod)
			throws Exception {
		CollectionResponse<M> response = new CollectionResponse<>();
		Long totalCount = 0L;
		if (apiMethod.getApiQuery() == null || StringUtils.isBlank(apiMethod.getApiQuery().value()))
			totalCount = this.countByFilter(queryParameter, jpaService, apiMethod);
		response.setTotalCount(totalCount);
		if (totalCount > 0
				|| (apiMethod.getApiQuery() != null && StringUtils.isNotBlank(apiMethod.getApiQuery().value()))) {
			this.defaultOrderBy(queryParameter, apiMethod);
			List<M> list = this.findByFilter(queryParameter, jpaService, entityClass, modelClass, apiMethod);
			response.setData(list);
			if (queryParameter.getPageable() != null) {
				response.setPageNumber(queryParameter.getPageable().getPageNumber());
				response.setPageSize(queryParameter.getPageable().getPageSize());

			}
		}
		return response;
	}

	private <ID, E, K> CollectionResponse<K> collectionResponse(NativeQueryParameter<K, ID> queryParameter,
			JpaServiceImpl<E, ID> jpaService, ApiMethod apiMethod) throws Exception {
		CollectionResponse<K> response = new CollectionResponse<>();
		this.defaultOrderBy(queryParameter, apiMethod);
		List<K> list = jpaService.findByFilter(queryParameter, apiMethod.getApiQuery().value());
		response.setData(list);
		if (queryParameter.getPageable() != null) {
			response.setPageNumber(queryParameter.getPageable().getPageNumber());
			response.setPageSize(queryParameter.getPageable().getPageSize());
		}

		return response;
	}

	private <K, L extends Collection<?>, E, ID> L collection(NativeQueryParameter<K, ID> queryParameter,
			JpaServiceImpl<E, ID> jpaService, Class<L> collectionClass, ApiMethod apiMethod) throws Exception {
		this.defaultOrderBy(queryParameter, apiMethod);
		List<K> list = jpaService.findByFilter(queryParameter, apiMethod.getApiQuery().value());
		L l = null;
		if (List.class.equals(collectionClass))
			l = (L) new ArrayList<>(list);
		else if (Set.class.equals(collectionClass))
			l = (L) new HashSet<>(list);
		else
			l = collectionClass.getDeclaredConstructor(Collection.class).newInstance(list);
		return l;
	}

	private <E, K, ID> K singreResultByFilter(NativeQueryParameter<K, ID> queryParameter,
			JpaServiceImpl<E, ID> jpaService, ApiMethod apiMethod) throws Exception {
		return jpaService.singleResultByFilter(queryParameter, apiMethod.getApiQuery().value());
	}

	private void defaultOrderBy(BaseQueryParameter<?, ?> queryParameter, ApiMethod apiMethod) {
		if (CollectionUtils.isEmpty(queryParameter.getListOrderBy()) && apiMethod.getApiQuery() != null)
			for (DefaultOrderBy orderBy : apiMethod.getApiQuery().orderBy())
				queryParameter.addOrderBy(orderBy.value(), orderBy.orderType());
	}

	private <E, ID> Long countByFilter(QueryParameter<E, ID> queryParameter, JpaServiceImpl<E, ID> jpaService,
			ApiMethod apiMethod) {
		Long totalCount = 0L;
		if (apiMethod.getApiQuery() == null || StringUtils.isBlank(apiMethod.getApiQuery().value()))
			totalCount = jpaService.countByFilter(queryParameter);
		else
			totalCount = jpaService.countByFilter(queryParameter, apiMethod.getApiQuery().value());
		return totalCount;
	}

	private <K, E, ID> Long countByFilter(NativeQueryParameter<K, ID> queryParameter, JpaServiceImpl<E, ID> jpaService,
			ApiMethod apiMethod) {
		Long totalCount = 0L;
		totalCount = jpaService.countByFilter(queryParameter, apiMethod.getApiQuery().value());
		return totalCount;
	}

	private <M, L extends Collection<?>, E, ID> L collection(QueryParameter<E, ID> queryParameter,
			JpaServiceImpl<E, ID> jpaService, Class<E> entityClass, Class<L> collectionClass, Class<M> modelClass,
			ApiMethod apiMethod) throws Exception {
		this.defaultOrderBy(queryParameter, apiMethod);
		List<?> list = this.findByFilter(queryParameter, jpaService, entityClass, modelClass, apiMethod);
		L l = null;
		if (List.class.equals(collectionClass))
			l = (L) new ArrayList<>(list);
		else if (Set.class.equals(collectionClass))
			l = (L) new HashSet<>(list);
		else
			l = collectionClass.getDeclaredConstructor(Collection.class).newInstance(list);
		return l;
	}

	private <M, E, ID> List<M> findByFilter(QueryParameter<E, ID> queryParameter, JpaServiceImpl<E, ID> jpaService,
			Class<E> entityClass, Class<M> modelClass, ApiMethod apiMethod) throws Exception {
		List<E> entities = null;
		if (apiMethod.getApiQuery() == null || StringUtils.isBlank(apiMethod.getApiQuery().value()))
			entities = jpaService.findByFilter(queryParameter);
		else
			entities = jpaService.findByFilter(queryParameter, apiMethod.getApiQuery().value());
		List<M> models = new ArrayList<>();
		if (apiMethod.getApiMapper() == null)
			throw new ApiFindException("The class to convert the entity to output is not declared");
		try {
//			for (String beanName : this.applicationContext.getBeanDefinitionNames()) {
//				Class<?> beanType = this.applicationContext.getType(beanName);
//				logger.info(beanName + " " + beanType);
//			}
//
//			ResolvableType resolvableType = ResolvableType.forType(apiMethod.getApiMapper().value());
//			logger.info(resolvableType.toString());
//			String[] beanNames = this.context.getBeanNamesForType(apiMethod.getApiMapper().value());
//			Object mapper = this.context.getBean(beanNames[0]);

//			if (!mapBean.containsKey(apiMethod.getApiMapper().value().getName()))
//				throw new ApiFindException(apiMethod.getApiMapper().value().getName() + " bean is not found");
//			else if (mapBean.containsKey(apiMethod.getApiMapper().value().getName()) && mapBean.get(apiMethod.getApiMapper().value().getName()).size() > 1)
//				throw new ApiFindException("Found more beans for \"" + apiMethod.getApiMapper().value().getName() + "\"");
//			String beanName = mapBean.get(apiMethod.getApiMapper().value().getName()).get(0);

//			String[] beanNames = this.context.getBeanNamesForType(apiMethod.getApiMapper().value());
//			logger.info("Bean names");
//			for(String beanName:beanNames)
//				logger.info(beanName);

//			Object mapper = this.context.getBean(beanName);
			Object mapper = mapBean.get(apiMethod.getApiMapper().value().getName());
			// Object mapper= this.context.getBean(apiMethod.getApiMapper().value());
			if (mapper != null)
				logger.info("mapper is not null");
			Method mapperMethod = methodMapper(entityClass, modelClass, mapper.getClass(),
					apiMethod.getApiMapper().method());

			Class<?>[] types = outputGenericType(mapperMethod);

			for (E entity : entities) {
				String json = this.objMapper.writeValueAsString(mapperMethod.invoke(mapper, entity));
				if (ArrayUtils.isNotEmpty(types)) {
					JavaType type = objMapper.getTypeFactory().constructParametricType(modelClass, types);
					models.add(this.objMapper.readValue(json, type));
				} else {
					models.add(this.objMapper.readValue(json, modelClass));
				}

			}

		} catch (Throwable e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new ApiFindException("Method mapper is not found", e);
		}

		return models;
	}

	private Class<?>[] outputGenericType(Method method) throws Exception {
		Type returnType = method.getGenericReturnType();
		Class<?>[] types = null;
		int i = 0;
		if (returnType instanceof ParameterizedType) {
			ParameterizedType type = (ParameterizedType) returnType;
			Type[] typeArguments = type.getActualTypeArguments();

			if (ArrayUtils.isNotEmpty(typeArguments)) {
				types = new Class[typeArguments.length];
				for (Type typeArgument : typeArguments) {
					logger.info(typeArgument.getTypeName());
					types[i++] = Class.forName(typeArgument.getTypeName());
				}

			}

		}
		return types;
	}

//	private <E, M> ModelMapper<E, M> modelMapper(Class<E> entityClass, Class<M> modelClass) {
//		ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ModelMapper.class, entityClass, modelClass);
//		ModelMapper<E, M> mapper = (ModelMapper<E, M>) this.applicationContext.getBeanProvider(resolvableType).getObject();
//		return mapper;
//	}

	private <E, M, ID> M singleResultByFilter(QueryParameter<E, ID> queryParameter, JpaServiceImpl<E, ID> jpaService,
			Class<E> entityClass, Class<M> modelClass, ApiMethod apiMethod) throws Exception {
		E entity = null;
		if (apiMethod.getApiQuery() == null || StringUtils.isBlank(apiMethod.getApiQuery().value()))
			entity = jpaService.singleResultByFilter(queryParameter);
		else
			entity = jpaService.singleResultByFilter(queryParameter, apiMethod.getApiQuery().value());
		M model = null;
		if (entity != null) {
			if (apiMethod.getApiMapper() == null)
				throw new ApiFindException("The class to convert the entity to output is not declared");
			try {
				Object mapper = mapBean.get(apiMethod.getApiMapper().value().getName());
				Method mapperMethod = methodMapper(entityClass, modelClass, mapper.getClass(),
						apiMethod.getApiMapper().method());
				Class<?>[] types = outputGenericType(mapperMethod);
				String json = this.objMapper.writeValueAsString(mapperMethod.invoke(mapper, entity));
				if (ArrayUtils.isNotEmpty(types)) {
					JavaType type = objMapper.getTypeFactory().constructParametricType(modelClass, types);
					model = this.objMapper.readValue(json, type);
				} else
					model = this.objMapper.readValue(json, modelClass);
			} catch (NoSuchMethodException e) {
				logger.error("Method mapper is not found");
				throw new ApiFindException("Method mapper is not found", e);
			}

		}

		return model;
	}

	private <P, O> Method methodMapper(Class<P> parameterClass, Class<O> outputClass, Class<?> mapperClass,
			String methodName) throws Exception {

		Method method = null;
		try {
			method = StringUtils.isBlank(methodName) ? findMethod(mapperClass, parameterClass, outputClass)
					: mapperClass.getMethod(methodName, parameterClass);
		} catch (NoSuchMethodException e) {
			logger.error("Method mapper is not found");
			throw new ApiFindException("Method mapper is not found", e);
		}
		return method;
	}

	private <M, P, O> Method findMethod(Class<M> mapperClass, Class<P> parameterClass, Class<O> outputClass)
			throws Exception {
		Method methodFound = null;
		Method methodAssignInputFound = null;
		int countMethodFound = 0;
		int countAssignMethodFound = 0;
		Set<Method> methods = ReflectionCommons.methods(mapperClass);
		for (Method method : methods) {
			if (method.getParameterCount() == 1 && method.getReturnType().getName().equals(outputClass.getName())) {
				if (method.getParameterTypes()[0].getName().equals(parameterClass.getName())) {
					methodFound = method;
					countMethodFound++;
				} else if (Class.forName(method.getParameterTypes()[0].getName())
						.isAssignableFrom(Class.forName(parameterClass.getName()))) {
					methodAssignInputFound = method;
					countAssignMethodFound++;
				}
			}

		}
		if (countMethodFound == 1)
			return methodFound;
		if (countAssignMethodFound == 1)
			return methodAssignInputFound;
		if (countMethodFound > 1 || countAssignMethodFound > 1)
			throw new ApiFindException(
					"More compatible methods were found in the mapping class, use @ApiMethodMapper or @ApiMapper to select the method name");
		throw new ApiFindException("Method mapper is not found");

	}

}
