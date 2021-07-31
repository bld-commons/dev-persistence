package bld.commons.reflection.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class StaticApplicationContext  implements ApplicationContextAware{
	
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext ac) throws BeansException {
		applicationContext=ac;
	}
	
	public static <T> T getBean(Class<T>classBean) {
		return applicationContext.getBean(classBean);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getBean(String className,Class<T>classBean) {
		return (T)applicationContext.getBean(className);
	}
	
}