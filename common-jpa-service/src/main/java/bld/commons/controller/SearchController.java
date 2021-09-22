package bld.commons.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import bld.commons.reflection.model.BaseModel;
import bld.commons.reflection.model.CollectionResponse;
import bld.commons.reflection.model.FilterParameter;
import bld.commons.reflection.model.ObjectResponse;
import bld.commons.reflection.model.QueryFilter;
import bld.commons.workspace.WorkModel;

public class SearchController<ID,T extends BaseModel<ID>, F extends FilterParameter> {

	
	@Autowired
	private WorkModel workModel;
	
	
	@PostMapping(path = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Valid
	public CollectionResponse<T> findByFilter(@RequestBody F filter) throws Exception {
		QueryFilter<?, ID> queryFilter = new QueryFilter<>(filter);
		return this.workModel.findByFilter(queryFilter);
	}
	
	
	
	@PostMapping(path="/search/single-result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Valid
	public ObjectResponse<T> singleResultFindByFilter(@RequestBody F filter) throws Exception{
		QueryFilter<?, ID>query=new QueryFilter<>(filter);
		return this.workModel.findSingleResultByFilter(query);
	}
	
	
}
