package bld.commons.workspace;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import bld.commons.annotations.WorkspaceClasses;
import bld.commons.reflection.model.BasicModel;
import bld.commons.reflection.model.CollectionResponse;
import bld.commons.reflection.model.QueryFilter;
import bld.commons.service.JpaService;
import bld.commons.workspace.mapper.MapperModel;

@Component
@Transactional(rollbackFor = Exception.class)
@SuppressWarnings("unchecked")
public class WorkModelImpl implements WorkModel {

	@Autowired
	private ApplicationContext context;
	
	
	@Override
	public <T,ID,M extends BasicModel<ID>> CollectionResponse<M> findByFilter(QueryFilter<T, ID>queryFilter) throws Exception {
		CollectionResponse<M> collectionResponse = new CollectionResponse<>();
		if(!queryFilter.getFilterParameter().getClass().isAnnotationPresent(WorkspaceClasses.class))
			throw new Exception("");
		WorkspaceClasses workspaceClasses=queryFilter.getFilterParameter().getClass().getAnnotation(WorkspaceClasses.class);
		JpaService<T,ID> service=(JpaService<T, ID>) this.context.getBean(workspaceClasses.service());
		List<T> list = service.findByFilter(queryFilter);
		Long resultNumber = service.countByFilter(queryFilter);
		MapperModel<T,M> mapperModel=(MapperModel<T,M>)this.context.getBean(workspaceClasses.mapper());
		List<M> listModel = new ArrayList<M>();
		
		for(T entity:list) 
			listModel.add(mapperModel.convertToModel(entity));
		collectionResponse.setData(listModel);
		collectionResponse.setResultNumber(resultNumber != null ? resultNumber : Long.valueOf(0));
		if(queryFilter.getPageable()!=null) {
			collectionResponse.setPageNumber(queryFilter.getPageable().getPageNumber());
			collectionResponse.setPageSize(queryFilter.getPageable().getPageSize());
			
		}
		
		return collectionResponse;
	}

	
	
}
