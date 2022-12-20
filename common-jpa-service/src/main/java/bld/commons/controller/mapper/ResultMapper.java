package bld.commons.controller.mapper;

import java.util.Map;

public interface ResultMapper<T> {

	public T mapToData(Map<String,Object> map);
}
