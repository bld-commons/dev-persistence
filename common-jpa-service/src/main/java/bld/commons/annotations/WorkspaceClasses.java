/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.annotations.WorkspaceClasses.java
 */
package bld.commons.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import bld.commons.service.JpaService;
import bld.commons.workspace.mapper.MapperModel;

/**
 * The Interface WorkspaceClasses.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface WorkspaceClasses {

	/**
	 * Service.
	 *
	 * @return the class<? extends jpa service<?,?>>
	 */
	public Class<? extends JpaService<?,?>> service();
	
	/**
	 * Mapper.
	 *
	 * @return the class<? extends mapper model<?,?>>
	 */
	public Class<? extends MapperModel<?,?>> mapper();
	
}
