package com.bld.proxy.api.find.config;

import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.bld.proxy.api.find.intecerptor.ApiFindInterceptor;
import com.bld.proxy.api.find.intecerptor.FindInterceptor;

@Component
public class ProxyConfig {

//	@Autowired
//	private ApplicationContext applicationContext;

	private final static Logger logger = LoggerFactory.getLogger(ProxyConfig.class);

	@SuppressWarnings("unchecked")
	public <T> T newProxyInstance(Class<T> clazz,ApplicationContext applicationContext,Map<String, Object> mapBean) {
		//Map<String, Object> mapBean = new HashMap<>();
//
//		
//		ApiFind apiFind = clazz.getAnnotation(ApiFind.class);
//		ApiMapper apiMapper = clazz.getAnnotation(ApiMapper.class);
//
//		addBean(mapBean, apiFind, apiMapper,applicationContext);
//
//		Set<Method> methods = ReflectionCommons.methods(clazz);
//		for (Method method : methods) {
//			apiFind = method.getAnnotation(ApiFind.class);
//			apiMapper = method.getAnnotation(ApiMapper.class);
//			addBean(mapBean, apiFind, apiMapper,applicationContext);
//		}

		T t = (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, new ApiFindInterceptor(new FindInterceptor(applicationContext, mapBean)));
		return t;
	}

//	private void addBean(Map<String, Object> mapBean, ApiFind apiFind, ApiMapper apiMapper,ApplicationContext applicationContext) {
//		if (apiFind != null) {
//			String[] beanNames = applicationContext.getBeanNamesForType(JpaService.class);
//			for (String beanName : beanNames) {
//				Class<?>[] typeArguments = ResolvableType.forType(applicationContext.getType(beanName)).as(JpaService.class).resolveGenerics();
//				if (typeArguments[0].getName().equals(apiFind.entity().getName()) && apiFind.id().getName().equals(typeArguments[1].getName())) {
//					JpaService<?, ?> jpaService = (JpaService<?, ?>) applicationContext.getBean(beanName);
//					mapBean.put(beanName, jpaService);
//				}
//
//			}
//		}
//
//		if (apiMapper != null) {
//			for (String beanName : applicationContext.getBeanDefinitionNames()) {
//				try {
//					logger.info("Bean Name: "+beanName);
//					Object mapper=applicationContext.getBean(beanName);
//					logger.info(beanName+" not null= "+(mapper!=null));
//					Class<?>mapperClass=mapper.getClass();
//					do {
//						if(mapperClass.getName().equals(apiMapper.value().getName())) {
//							logger.info("class "+beanName+" set in map");
//							mapBean.put(apiMapper.value().getName(), mapper);
//							break;
//						}
//						mapperClass=mapperClass.getSuperclass();
//							
//					}while(mapperClass!=null && !Object.class.getName().equals(mapperClass.getName()));
//					
//					Class<?>[] interfaces = mapperClass.getInterfaces();
//					for(Class<?> intrf:interfaces) {
//						if(intrf.getClass().getName().equals(apiMapper.value().getName())) {
//							logger.info("interface "+beanName+" set in map");
//							mapBean.put(apiMapper.value().getName(), mapper);
//							break;
//						}
//							
//					}
//					
////					if(apiMapper.value().isAssignableFrom(mapper.getClass())) {
////						logger.info("Mapper found: "+apiMapper.value().getName());
////						mapBean.put(apiMapper.value().getName(), mapper);
////						break;
////					}
//				} catch (BeansException e) {
//					logger.warn("ignore bean");
//				}
//					
//			}
//			
//		}
//	}
	
	
	

}
