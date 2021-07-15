package bld.commons.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import bld.commons.service.JpaService;
import bld.commons.workspace.mapper.MapperModel;

@Retention(RUNTIME)
@Target(TYPE)
public @interface WorkspaceClasses {

	public Class<? extends JpaService<?,?>> service();
	
	public Class<? extends MapperModel<?,?>> mapper();
	
}
