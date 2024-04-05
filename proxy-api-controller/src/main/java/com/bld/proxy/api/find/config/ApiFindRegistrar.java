package com.bld.proxy.api.find.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.bld.proxy.api.find.annotations.ApiFind;
import com.bld.proxy.api.find.annotations.ApiFindController;
import com.bld.proxy.api.find.annotations.ApiMapper;
import com.bld.proxy.api.find.config.annotation.EnableProxyApiController;

import bld.commons.reflection.utils.ReflectionCommons;
import bld.commons.service.JpaService;
import bld.commons.service.JpaServiceImpl;

@Configuration
public class ApiFindRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware, ApplicationContextAware {

	private static final String PROXY_CONFIG = "proxyConfig";

	private static final String NEW_PROXY_INSTANCE = "newProxyInstance";

	private Environment env;

	private ResourceLoader resourceLoader;

	private final static Logger logger = LoggerFactory.getLogger(ApiFindRegistrar.class);

	private ApplicationContext applicationContext;

	private static AnnotationMetadata metadata;

	private static BeanDefinitionRegistry registry;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		ApiFindRegistrar.metadata = metadata;
		ApiFindRegistrar.registry = registry;
	}

	private void addBeanDefinitions() {
		ClassPathScanningCandidateComponentProvider scanner = getScanner();
		LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
		scanner.setResourceLoader(this.resourceLoader);
		scanner.addIncludeFilter(new AnnotationTypeFilter(ApiFindController.class));

//		try {
//			Class<?> clazz=Class.forName("com.dxc.mase.territorio.core.domain.TrNazione");
//			methodInvoke(clazz);
//		}catch(Exception e) {
//			logger.error(ExceptionUtils.getStackTrace(e));
//		}

		Set<String> baseProjects = getBasePackages(metadata);

		for (String baseProject : baseProjects) {
			logger.debug("package: " + baseProject);
			candidateComponents.addAll(scanner.findCandidateComponents(baseProject));
			try {
				for (BeanDefinition candidate : candidateComponents) {
					Map<String, Object> mapBean = new HashMap<>();
					String beanClassName = candidate.getBeanClassName();
					Class<?> classApiController = Class.forName(beanClassName);

					ApiFind apiFind = classApiController.getAnnotation(ApiFind.class);
					ApiMapper apiMapper = classApiController.getAnnotation(ApiMapper.class);
//
					addBean(mapBean, apiFind, apiMapper);
//
//					Set<Method> methods = ReflectionCommons.methods(classApiController);
//					for (Method method : methods) {
//						apiFind = method.getAnnotation(ApiFind.class);
//						apiMapper = method.getAnnotation(ApiMapper.class);
//						addBean(mapBean, apiFind, apiMapper);
//					}

					BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(classApiController);
					builder.setFactoryMethodOnBean(NEW_PROXY_INSTANCE, PROXY_CONFIG);
					// builder.setFactoryMethod("newProxyInstance"); // Factory method from
					// DynamicInterfaceFactory
					builder.addConstructorArgValue(classApiController);
					builder.addConstructorArgValue(applicationContext);
					builder.addConstructorArgValue(mapBean);
					// Register the bean definition with a name
					BeanDefinition beanDefinition = builder.getBeanDefinition();
					registry.registerBeanDefinition(classApiController.getName(), beanDefinition);

//					BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(beanClassName);
					//
//					// Add any custom configuration for the bean definition if needed
					//
//					registry.registerBeanDefinition(beanClassName, builder.getBeanDefinition());
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

//	private <T> void methodInvoke(Class<T> clazz) throws Exception{
//		Object mapper=this.applicationContext.getBean("nazioneMapperImpl");
//		for(Method method:mapper.getClass().getMethods()) {
//			if(method.getName().equals("convertToNazioneModel"))
//				method.invoke(mapper, (T)null);
//		}
//		
//		
//	}

	protected ClassPathScanningCandidateComponentProvider getScanner() {
		return new ClassPathScanningCandidateComponentProvider(false, this.env) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				boolean isCandidate = false;
				if (beanDefinition.getMetadata().isIndependent()) {
					if (!beanDefinition.getMetadata().isAnnotation()) {
						isCandidate = true;
					}
				}
				return isCandidate;
			}
		};
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;

	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;

	}

	protected Set<String> getBasePackages(AnnotationMetadata importingClassMetadata) {
		Map<String, Object> attributes = importingClassMetadata.getAnnotationAttributes(EnableProxyApiController.class.getCanonicalName());

		Set<String> basePackages = new HashSet<>();
		for (String pkg : (String[]) attributes.get("value")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (String pkg : (String[]) attributes.get("basePackages")) {
			if (StringUtils.hasText(pkg)) {
				basePackages.add(pkg);
			}
		}
		for (Class<?> clazz : (Class[]) attributes.get("basePackageClasses")) {
			basePackages.add(ClassUtils.getPackageName(clazz));
		}

		if (basePackages.isEmpty()) {
			basePackages.add(ClassUtils.getPackageName(importingClassMetadata.getClassName()));
		}
		return basePackages;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
		addBeanDefinitions();

	}

	private void addBean(Map<String, Object> mapBean, ApiFind apiFind, ApiMapper apiMapper) {
		if (apiFind != null) {
			apiFind(mapBean, apiFind);
		}

		if (apiMapper != null) {
//			String[] beanNames = applicationContext.getBeanNamesForType(apiMapper.value());
//			Object  mapper=applicationContext.getBeanProvider(ResolvableType.forType(apiMapper.value()));
//			for (String beanName : beanNames) {
//				mapBean.put(apiMapper.value().getName(), this.applicationContext.getBean(beanName));
//			}

			apiMapper(mapBean, apiMapper);

		}
	}

	private void apiMapper(Map<String, Object> mapBean, ApiMapper apiMapper) {
		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			try {
				// logger.info("Bean Name: "+beanName);
				Object mapper = applicationContext.getBean(beanName);
				// logger.info(beanName+" not null= "+(mapper!=null));
				Class<?> mapperClass = mapper.getClass();
				do {
					if (mapperClass.getName().equals(apiMapper.value().getName())) {
						logger.info("class " + beanName + " set in map");
						mapBean.put(apiMapper.value().getName(), mapper);
						return;
					}
					mapperClass = mapperClass.getSuperclass();

				} while (mapperClass != null && !Object.class.getName().equals(mapperClass.getName()));

				Class<?>[] interfaces = mapperClass.getInterfaces();
				for (Class<?> intrf : interfaces) {
					if (intrf.getClass().getName().equals(apiMapper.value().getName())) {
						logger.info("interface " + beanName + " set in map");
						mapBean.put(apiMapper.value().getName(), mapper);
						return;
					}

				}

			} catch (BeansException e) {
				logger.warn("ignore bean");
			}

		}
	}

	private void apiFind(Map<String, Object> mapBean, ApiFind apiFind) {
		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			try {
				Object service = applicationContext.getBean(beanName);
				Class<?> serviceClass = service.getClass();
				do {
					if (serviceClass.getName().equals(JpaServiceImpl.class.getName())) {
						Class<?>entity=ReflectionCommons.getGenericTypeClass(service.getClass(), 0);
						Class<?>id=ReflectionCommons.getGenericTypeClass(service.getClass(), 1);
						if(entity.getName().equals(apiFind.entity().getName()) && apiFind.id().getName().equals(id.getName())) {
							logger.info("class " + beanName + " set in map");
							mapBean.put(apiFind.entity().getName() + " " + apiFind.id().getName(), service);
							return;
						}
					}
					serviceClass = serviceClass.getSuperclass();

				} while (serviceClass != null && !Object.class.getName().equals(serviceClass.getName()));
				
//				Class<?>[] typeArguments = ResolvableType.forType(applicationContext.getType(beanName)).as(JpaService.class).resolveGenerics();
//				if (typeArguments[0].getName().equals(apiFind.entity().getName()) && apiFind.id().getName().equals(typeArguments[1].getName())) {
//					JpaService<?, ?> jpaService = (JpaService<?, ?>) applicationContext.getBean(beanName);
//					mapBean.put(apiFind.entity().getName() + " " + apiFind.id().getName(), jpaService);
//					return;
//				}
			} catch (Exception e) {
				logger.info("ignore");
			}

		}

//		String[] beanNames = applicationContext.getBeanNamesForType(JpaService.class);
//		for (String beanName : beanNames) {
//			Class<?>[] typeArguments = ResolvableType.forType(applicationContext.getType(beanName)).as(JpaService.class).resolveGenerics();
//			if (typeArguments[0].getName().equals(apiFind.entity().getName()) && apiFind.id().getName().equals(typeArguments[1].getName())) {
//				JpaService<?, ?> jpaService = (JpaService<?, ?>) applicationContext.getBean(beanName);
//				mapBean.put(apiFind.entity().getName()+" "+apiFind.id().getName(), jpaService);
//				return;
//			}
//
//		}
	}

//	private <T> void registration(BeanDefinitionRegistry registry, Class<T> classApiController) {
//		Supplier<T> supplier = () -> ProxyConfig.newProxyInstance(classApiController);
//		BeanDefinition dynamicInterfaceBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(classApiController, supplier).getBeanDefinition();
//		registry.registerBeanDefinition(classApiController.getSimpleName(), dynamicInterfaceBeanDefinition);
//	}
}
