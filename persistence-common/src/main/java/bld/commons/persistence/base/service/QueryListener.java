package bld.commons.persistence.base.service;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class QueryListener {

//	private final static Log logger = LogFactory.getLog(QueryListener.class);
//	
//	//@AfterReturning(pointcut="execution(* com.bld.pmg..*.core.persistence.service..*(..))")
//	@Before("execution(* com.bld.pmg..*.core.persistence.service..*(..))")
//	public void listenerAllFunctionService() {
//		logger.info("Catturata l'esecuzione di un metodo all'interno di core persistence service");
//	}
//	
//	
//	@AfterReturning(pointcut="execution(* com.bld.pmg..*.core.persistence.service..*.save*(..))")
//	public void saveListener() {
//		Object authentication=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		logger.info("Catturata il save");
//		logger.info(authentication);
//	}
//	
//	@AfterReturning(pointcut="execution(* com.bld.pmg..*.core.persistence.service..*.update*(..))", returning = "object")
//	public void updateListener(Object object) {
//		Object authentication=SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//		logger.info(authentication);
//		logger.info("Object type: "+object.getClass().getSimpleName());
//		logger.info("Catturata l'update");
//	}
//	
//	@AfterReturning(pointcut="execution(* com.bld.pmg..*.core.persistence.service..*.delete*(..))")
//	public void deleteListener() {
//		logger.info("Catturata la delete");
//	}
}
