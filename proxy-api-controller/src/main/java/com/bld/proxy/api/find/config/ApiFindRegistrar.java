package com.bld.proxy.api.find.config;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.bld.proxy.api.find.annotations.ApiFindController;
import com.bld.proxy.api.find.config.annotation.EnableProxyApiController;

public class ApiFindRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {

	private static final String PROXY_CONFIG = "proxyConfig";

	private static final String NEW_PROXY_INSTANCE = "newProxyInstance";

	private Environment env;

	private ResourceLoader resourceLoader;

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		ClassPathScanningCandidateComponentProvider scanner = getScanner();
		LinkedHashSet<BeanDefinition> candidateComponents = new LinkedHashSet<>();
		scanner.setResourceLoader(this.resourceLoader);
		scanner.addIncludeFilter(new AnnotationTypeFilter(ApiFindController.class));
		Set<String> baseProjects=getBasePackages(metadata);
		for(String baseProject:baseProjects) {
			candidateComponents.addAll(scanner.findCandidateComponents(baseProject));
			try {
				for (BeanDefinition candidate : candidateComponents) {
					String beanClassName = candidate.getBeanClassName();
					Class<?> classApiController = Class.forName(beanClassName);
//					
//					registration(registry, classApiController);

					BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(classApiController);
					builder.setFactoryMethodOnBean(NEW_PROXY_INSTANCE, PROXY_CONFIG);
					//builder.setFactoryMethod("newProxyInstance"); // Factory method from DynamicInterfaceFactory
					builder.addConstructorArgValue(classApiController);

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
		Map<String, Object> attributes = importingClassMetadata
				.getAnnotationAttributes(EnableProxyApiController.class.getCanonicalName());

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

//	private <T> void registration(BeanDefinitionRegistry registry, Class<T> classApiController) {
//		Supplier<T> supplier = () -> ProxyConfig.newProxyInstance(classApiController);
//		BeanDefinition dynamicInterfaceBeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(classApiController, supplier).getBeanDefinition();
//		registry.registerBeanDefinition(classApiController.getSimpleName(), dynamicInterfaceBeanDefinition);
//	}
}
