package com.bld.proxy.api.find.intecerptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.core.ResolvableType;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
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
import com.bld.proxy.api.find.data.ParameterDetails;

import bld.commons.controller.mapper.ModelMapper;
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
import bld.commons.service.JpaService;

@Component
@Scope("prototype")
@SuppressWarnings("unchecked")
class FindInterceptor {

	private static final String PAGE_NUMBER = "pageNumber";

	private static final String PAGE_SIZE = "pageSize";

	private static final String ORDER_TYPE = "orderType";

	private static final String SORT_KEY = "sortKey";

	private final static Logger logger = LoggerFactory.getLogger(FindInterceptor.class);

	private final static List<String> PAGINATION_KEYS = Arrays.asList(SORT_KEY, ORDER_TYPE, PAGE_SIZE, PAGE_NUMBER);

	private final static List<Class<? extends Annotation>> REQUEST_ANNOTATIONS = Arrays.asList(RequestParam.class, RequestAttribute.class, PathVariable.class);

	private Method method;

	private Object[] args;

	private Map<Class<? extends Annotation>, ParameterDetails> map;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	private ReflectionCommons reflectionCommons;

	private ApiQuery apiQuery;

	private ApiMapper apiMapper;

	public <E, ID> Object find(Object obj, Method method, Object[] args) throws Throwable {
		Object response = null;
		this.apiQuery = method.getAnnotation(ApiQuery.class);
		ApiFind apiFind = method.getAnnotation(ApiFind.class);
		if (apiFind == null)
			apiFind = method.getDeclaringClass().getAnnotation(ApiFind.class);
		this.method = method;
		this.args = args;
		Class<?> entityClass = apiFind.entity();
		Class<?> idClass = apiFind.id();
		Class<?> outputClass = method.getReturnType();
		this.getParameters(this.method.getParameters(), args, RequestBody.class, AuthenticationPrincipal.class);
		Assert.isTrue(!(this.apiQuery != null && StringUtils.isBlank(this.apiQuery.value()) && !this.apiQuery.jpql()), "For native query the field \"value\" can not be blank into ApiQuery");

		if (this.apiQuery == null || this.apiQuery.jpql())
			response = this.jpqlQuery(entityClass, idClass, outputClass);
		else {
			Class<?> modelClass = modelClass(method);
			if (modelClass == null)
				modelClass = outputClass;
			response = this.nativeQuery(entityClass, idClass, outputClass, modelClass);
		}

		return response;
	}

	private <E, ID, O> Object jpqlQuery(Class<E> entityClass, Class<ID> idClass, Class<O> outputClass) throws Exception {
		Object response = null;
		this.apiMapper = method.getAnnotation(ApiMapper.class);
		QueryParameter<E, ID> queryParameter = new QueryParameter<>();
		addBodyParameter(queryParameter);
		queryParameter(queryParameter);
		this.addUserDetails(queryParameter);
		firstStep(queryParameter);
		JpaService<E, ID> jpaService = jpaService(entityClass, idClass);
		Class<?> modelClass = modelClass(method);
		if (Number.class.isAssignableFrom(outputClass))
			response = this.countByFilter(queryParameter, jpaService);
		else if (modelClass != null) {

			if (CollectionResponse.class.isAssignableFrom(outputClass))
				response = collectionResponse(queryParameter, jpaService, entityClass, modelClass);
			else if (Collection.class.isAssignableFrom(outputClass)) {
				response = collection(queryParameter, jpaService, entityClass, (Class<? extends Collection<?>>) outputClass, modelClass);
			} else if (ObjectResponse.class.isAssignableFrom(outputClass)) {
				if (Number.class.isAssignableFrom(modelClass))
					response = new ObjectResponse<>(this.countByFilter(queryParameter, jpaService));
				else
					response = new ObjectResponse<>(this.singreResultByFilter(queryParameter, jpaService, entityClass, modelClass));
			}
		} else
			response = this.singreResultByFilter(queryParameter, jpaService, entityClass, outputClass);

		return response;
	}

	private <ID, E> JpaService<E, ID> jpaService(Class<E> entityClass, Class<ID> idClass) {
		ResolvableType resolvableType = ResolvableType.forClassWithGenerics(JpaService.class, entityClass, idClass);
		JpaService<E, ID> jpaService = (JpaService<E, ID>) this.applicationContext.getBeanProvider(resolvableType).getObject();
		return jpaService;
	}

	private void addBodyParameter(BaseQueryParameter<?, ?> queryParameter) {
		if (map.containsKey(RequestBody.class)) {
			Assert.isTrue(map.get(RequestBody.class).getValue() instanceof BaseParameter, "The body must be \"BaseParameter\" type");
			queryParameter.setBaseParameter((BaseParameter) map.get(RequestBody.class).getValue());
		}
	}

	private <E, ID, O, M> Object nativeQuery(Class<E> entityClass, Class<ID> idClass, Class<O> outputClass, Class<M> modelClass) throws Exception {
		Object response = null;
		NativeQueryParameter<M, ID> queryParameter = new NativeQueryParameter<>(modelClass);
		this.addBodyParameter(queryParameter);
		this.queryParameter(queryParameter);
		this.addUserDetails(queryParameter);
		this.firstStep(queryParameter);
		JpaService<E, ID> jpaService = jpaService(entityClass, idClass);
		if (Number.class.isAssignableFrom(outputClass))
			response = this.countByFilter(queryParameter, jpaService);
		else if (modelClass != null) {
			if (CollectionResponse.class.isAssignableFrom(outputClass))
				response = collectionResponse(queryParameter, jpaService);
			else if (Collection.class.isAssignableFrom(outputClass)) {
				response = collection(queryParameter, jpaService, (Class<? extends Collection<?>>) outputClass);
			} else if (ObjectResponse.class.isAssignableFrom(outputClass)) {
				if (Number.class.isAssignableFrom(modelClass))
					response = new ObjectResponse<>(this.countByFilter(queryParameter, jpaService));
				else
					response = new ObjectResponse<>(this.singreResultByFilter(queryParameter, jpaService));
			}
		} else
			response = this.singreResultByFilter(queryParameter, jpaService);
		return response;
	}

	private void getParameters(Parameter[] parameters, Object[] args, Class<? extends Annotation>... annotations) {
		this.map = new HashMap<>();
		if (ArrayUtils.isNotEmpty(parameters))
			for (int i = 0; i < parameters.length; i++)
				for (Class<? extends Annotation> annotation : annotations)
					if (parameters[i].isAnnotationPresent(annotation) && ignoreMapping(parameters[i]))
						this.map.put(annotation, new ParameterDetails(parameters[i], args[i], i));

	}

	private Class<?> modelClass(Method method) throws ClassNotFoundException {
		String genericReturnType = method.getGenericReturnType().getTypeName();
		if (!(genericReturnType.contains("<") && genericReturnType.contains(">")))
			return null;
		String genericType = genericReturnType.substring(genericReturnType.indexOf("<") + 1, genericReturnType.lastIndexOf(">"));
		if (genericType.contains("<"))
			genericType = genericType.substring(0, genericType.indexOf("<"));
		return Class.forName(genericType);
	}

	private boolean ignoreMapping(Parameter parameter) {
		return !parameter.isAnnotationPresent(IgnoreMapping.class) || parameter.isAnnotationPresent(IgnoreMapping.class) && !parameter.getAnnotation(IgnoreMapping.class).value();
	}

	private void addUserDetails(NativeQueryParameter<?, ?> queryParameter) {
		ParameterDetails userDetails = map.get(AuthenticationPrincipal.class);
		if (userDetails != null) {
			UserDetails user = ((UserDetails) userDetails.getValue());
			String key = "username";
			String username = user.getUsername();
			if (userDetails.getParameter().isAnnotationPresent(Param.class))
				key = userDetails.getParameter().getAnnotation(Param.class).value();
			if (userDetails.getParameter().isAnnotationPresent(LikeString.class))
				username = (String) this.reflectionCommons.getValue(username, null, userDetails.getParameter().getAnnotation(LikeString.class));
			ConditionsZones conditionsZones = userDetails.getParameter().getAnnotation(ConditionsZones.class);
			queryParameter.addParameter(key, username, conditionsZones);

		}

	}

	private void addUserDetails(QueryParameter<?, ?> queryParameter) {
		ParameterDetails userDetails = map.get(AuthenticationPrincipal.class);
		if (userDetails != null) {
			UserDetails user = ((UserDetails) userDetails.getValue());
			String key = "username";
			String username = user.getUsername();
			if (userDetails.getParameter().isAnnotationPresent(Param.class))
				key = userDetails.getParameter().getAnnotation(Param.class).value();
			if (userDetails.getParameter().isAnnotationPresent(LikeString.class))
				username = (String) this.reflectionCommons.getValue(username, null, userDetails.getParameter().getAnnotation(LikeString.class));
			queryParameter.addParameter(key, username);

		}

	}

	private void firstStep(BaseQueryParameter<?, ?> queryParameter) throws Exception {
		if (method.isAnnotationPresent(ApiBeforeRequest.class)) {
			ApiBeforeRequest beforeRequest = method.getAnnotation(ApiBeforeRequest.class);
			Object bean = this.applicationContext.getBean(beforeRequest.bean());
			Class<?>[] paramaterClasses = new Class<?>[args.length + 1];
			Object[] parameters = new Object[args.length + 1];
			int i = 0;
			for (Object arg : args) {
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

	private void queryParameter(NativeQueryParameter<?, ?> queryParameter) {
		if (ArrayUtils.isNotEmpty(this.args)) {
			Map<String, Object> mapPagination = new HashMap<>();
			for (int i = 0; i < args.length; i++) {
				Parameter parameter = method.getParameters()[i];
				if (this.isRequestAnnotationPresent(parameter) && ignoreMapping(parameter)) {
					String name = parameter.getName();
					Object value = args[i];
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
								value = this.reflectionCommons.getValue(value, dateFilter, likeString);

								if (value instanceof Boolean && (Boolean) value && parameter.isAnnotationPresent(ListFilter.class))
									queryParameter.addNullable(name, conditionsZones);
								else if (value.getClass().isArray()) {
									Object[] array = (Object[]) value;
									queryParameter.addParameter(name, Arrays.asList(array), conditionsZones);
								} else
									queryParameter.addParameter(name, value, conditionsZones);
							} else if (parameter.isAnnotationPresent(FilterNullValue.class) && parameter.getAnnotation(FilterNullValue.class).value())
								queryParameter.addParameter(name, ReflectionCommons.initTypedParameterValue(ReflectionCommons.mapType.get(parameter.getType()), value), conditionsZones);
							else if (conditionsZones != null)
								queryParameter.addEmptyZones(conditionsZones);
						} catch (Exception e) {
							logger.warn("Error converting data to map");
						}
					} else {
						mapPagination.put(name, value);
					}
				}
				queryParameter.addOrderBy((String) mapPagination.get(SORT_KEY), (OrderType) mapPagination.get(ORDER_TYPE));
				queryParameter.setPageable((Integer) mapPagination.get(PAGE_NUMBER), (Integer) mapPagination.get(PAGE_SIZE));
			}
		}
	}

	private void queryParameter(QueryParameter<?, ?> queryParameter) throws Exception {
		if (ArrayUtils.isNotEmpty(this.args)) {
			Map<String, Object> mapPagination = new HashMap<>();
			for (int i = 0; i < args.length; i++) {
				Parameter parameter = method.getParameters()[i];
				if (this.isRequestAnnotationPresent(parameter) && ignoreMapping(parameter)) {
					String name = parameter.getName();
					Object value = args[i];
					if (!PAGINATION_KEYS.contains(name)) {
						if (value instanceof Collection && CollectionUtils.isEmpty((Collection<?>) value))
							value = null;
						if (value != null && value instanceof String && StringUtils.isBlank((String) value))
							value = null;
						if (value != null) {
							DateFilter dateFilter = parameter.getAnnotation(DateFilter.class);
							LikeString likeString = parameter.getAnnotation(LikeString.class);
							value = this.reflectionCommons.getValue(value, dateFilter, likeString);

							if (value instanceof Boolean && (Boolean) value && parameter.isAnnotationPresent(ListFilter.class))
								queryParameter.addNullable(name);
							else if (value.getClass().isArray()) {
								Object[] array = (Object[]) value;
								queryParameter.addParameter(name, Arrays.asList(array));
							} else
								queryParameter.addParameter(name, value);
						} else if (parameter.isAnnotationPresent(FilterNullValue.class) && parameter.getAnnotation(FilterNullValue.class).value())
							queryParameter.addParameter(name, ReflectionCommons.initTypedParameterValue(ReflectionCommons.mapType.get(parameter.getType()), value));
					} else
						mapPagination.put(name, value);
				}
				queryParameter.addOrderBy((String) mapPagination.get(SORT_KEY), (OrderType) mapPagination.get(ORDER_TYPE));
				queryParameter.setPageable((Integer) mapPagination.get(PAGE_NUMBER), (Integer) mapPagination.get(PAGE_SIZE));
			}
		}

	}

	private <ID, E, M> CollectionResponse<M> collectionResponse(QueryParameter<E, ID> queryParameter, JpaService<E, ID> jpaService, Class<E> entityClass, Class<M> modelClass) throws Exception {
		CollectionResponse<M> response = new CollectionResponse<>();
		Long totalCount = 0L;
		if (this.apiQuery == null || StringUtils.isBlank(this.apiQuery.value()))
			totalCount = this.countByFilter(queryParameter, jpaService);
		response.setTotalCount(totalCount);
		if (totalCount > 0 || (this.apiQuery != null && StringUtils.isNotBlank(this.apiQuery.value()))) {
			this.defaultOrderBy(queryParameter);
			List<M> list = this.findByFilter(queryParameter, jpaService, entityClass, modelClass);
			response.setData(list);
			if (queryParameter.getPageable() != null) {
				response.setPageNumber(queryParameter.getPageable().getPageNumber());
				response.setPageSize(queryParameter.getPageable().getPageSize());

			}
		}
		return response;
	}

	private <ID, E, K> CollectionResponse<K> collectionResponse(NativeQueryParameter<K, ID> queryParameter, JpaService<E, ID> jpaService) throws Exception {
		CollectionResponse<K> response = new CollectionResponse<>();
		this.defaultOrderBy(queryParameter);
		List<K> list = jpaService.findByFilter(queryParameter, this.apiQuery.value());
		response.setData(list);
		if (queryParameter.getPageable() != null) {
			response.setPageNumber(queryParameter.getPageable().getPageNumber());
			response.setPageSize(queryParameter.getPageable().getPageSize());
		}

		return response;
	}

	private <K, L extends Collection<?>, E, ID> L collection(NativeQueryParameter<K, ID> queryParameter, JpaService<E, ID> jpaService, Class<L> collectionClass) throws Exception {
		this.defaultOrderBy(queryParameter);
		List<K> list = jpaService.findByFilter(queryParameter, this.apiQuery.value());
		L l = null;
		if (List.class.equals(collectionClass))
			l = (L) new ArrayList<>(list);
		else if (Set.class.equals(collectionClass))
			l = (L) new HashSet<>(list);
		else
			l = collectionClass.getDeclaredConstructor(Collection.class).newInstance(list);
		return l;
	}

	private <E, K, ID> K singreResultByFilter(NativeQueryParameter<K, ID> queryParameter, JpaService<E, ID> jpaService) throws Exception {
		return jpaService.singleResultByFilter(queryParameter, this.apiQuery.value());
	}

	private void defaultOrderBy(BaseQueryParameter<?, ?> queryParameter) {
		if (CollectionUtils.isEmpty(queryParameter.getListOrderBy()) && this.apiQuery != null)
			for (DefaultOrderBy orderBy : this.apiQuery.orderBy())
				queryParameter.addOrderBy(orderBy.value(), orderBy.orderType());
	}

	private <E, ID> Long countByFilter(QueryParameter<E, ID> queryParameter, JpaService<E, ID> jpaService) {
		Long totalCount = 0L;
		if (this.apiQuery == null || StringUtils.isBlank(this.apiQuery.value()))
			totalCount = jpaService.countByFilter(queryParameter);
		else
			totalCount = jpaService.countByFilter(queryParameter, this.apiQuery.value());
		return totalCount;
	}

	private <K, E, ID> Long countByFilter(NativeQueryParameter<K, ID> queryParameter, JpaService<E, ID> jpaService) {
		Long totalCount = 0L;
		totalCount = jpaService.countByFilter(queryParameter, this.apiQuery.value());
		return totalCount;
	}

	private <M, L extends Collection<?>, E, ID> L collection(QueryParameter<E, ID> queryParameter, JpaService<E, ID> jpaService, Class<E> entityClass, Class<L> collectionClass, Class<M> modelClass) throws Exception {
		this.defaultOrderBy(queryParameter);
		List<?> list = this.findByFilter(queryParameter, jpaService, entityClass, modelClass);
		L l = null;
		if (List.class.equals(collectionClass))
			l = (L) new ArrayList<>(list);
		else if (Set.class.equals(collectionClass))
			l = (L) new HashSet<>(list);
		else
			l = collectionClass.getDeclaredConstructor(Collection.class).newInstance(list);
		return l;
	}

	private <M, E, ID> List<M> findByFilter(QueryParameter<E, ID> queryParameter, JpaService<E, ID> jpaService, Class<E> entityClass, Class<M> modelClass) throws Exception {
		List<E> entities = null;
		if (this.apiQuery == null || StringUtils.isBlank(this.apiQuery.value()))
			entities = jpaService.findByFilter(queryParameter);
		else
			entities = jpaService.findByFilter(queryParameter, this.apiQuery.value());
		List<M> models = new ArrayList<>();
		if (this.apiMapper == null) {
			ModelMapper<E, M> mapper = modelMapper(entityClass, modelClass);
			for (E entity : entities)
				models.add(mapper.convertToModel(entity));
		} else {
			Object mapper = this.applicationContext.getBean(this.apiMapper.bean());
			Method mapperMethod = this.apiMapper.bean().getMethod(this.apiMapper.method(), entityClass);
			for (E entity : entities)
				models.add((M) mapperMethod.invoke(mapper, entity));
		}

		return models;
	}

	private <E, M> ModelMapper<E, M> modelMapper(Class<E> entityClass, Class<M> modelClass) {
		ResolvableType resolvableType = ResolvableType.forClassWithGenerics(ModelMapper.class, entityClass, modelClass);
		ModelMapper<E, M> mapper = (ModelMapper<E, M>) this.applicationContext.getBeanProvider(resolvableType).getObject();
		return mapper;
	}

	private <E, M, ID> M singreResultByFilter(QueryParameter<E, ID> queryParameter, JpaService<E, ID> jpaService, Class<E> entityClass, Class<M> modelClass) throws Exception {
		E entity = null;
		if (this.apiQuery == null || StringUtils.isBlank(this.apiQuery.value()))
			entity = jpaService.singleResultByFilter(queryParameter);
		else
			entity = jpaService.singleResultByFilter(queryParameter, this.apiQuery.value());
		M model = null;
		if (entity != null) {
			if (this.apiMapper == null) {
				ModelMapper<E, M> mapper = modelMapper(entityClass, modelClass);
				model = mapper.convertToModel(entity);
			} else {
				Object mapper = this.applicationContext.getBean(this.apiMapper.bean());
				Method mapperMethod = this.apiMapper.bean().getMethod(this.apiMapper.method(), entityClass);
				model = (M) mapperMethod.invoke(mapper, entity);
			}
		}

		return model;
	}

}
