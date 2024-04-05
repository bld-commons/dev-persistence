package com.bld.proxy.api.find.intecerptor;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.ResolvableType;

//@Component
public class StaticApplicationContext implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (applicationContext != null)
			context = applicationContext;
	}

	public static <T> T bean(Class<T> clazz) {
		return (T) StaticApplicationContext.context.getBean(clazz);
	}

	public static Object bean(String beanName) {
		return StaticApplicationContext.context.getBean(beanName);
	}

	public static Object beanProvider(ResolvableType resolvableType) {
		return StaticApplicationContext.context.getBeanProvider(resolvableType);
	}

	@SuppressWarnings("unchecked")
	public static <T> T beanProvider(Class<T> clazz) {
		ResolvableType resolvableType = ResolvableType.forType(clazz);
		return (T) StaticApplicationContext.beanProvider(resolvableType);
	}

	public static String[] beanNamesForType(Class<?> clazz) {
		ResolvableType resolvableType = ResolvableType.forType(clazz);
		return StaticApplicationContext.context.getBeanNamesForType(resolvableType);
	}

	public static Class<?> type(String beanName) {
		return StaticApplicationContext.context.getType(beanName);
	}

}
