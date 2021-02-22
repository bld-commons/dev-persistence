/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.QueryListener.java
 */
package bld.commons.persistence.base.service;

import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;



/**
 * The listener interface for receiving query events. The class that is
 * interested in processing a query event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addQueryListener<code> method. When the query event occurs,
 * that object's appropriate method is invoked.
 *
 * @see QueryEvent
 */
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
