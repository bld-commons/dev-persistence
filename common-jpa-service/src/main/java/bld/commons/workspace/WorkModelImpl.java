package bld.commons.workspace;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import bld.commons.annotations.WorkspaceClasses;
import bld.commons.reflection.model.BaseModel;
import bld.commons.reflection.model.CollectionResponse;
import bld.commons.reflection.model.ObjectResponse;
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
	public <T,ID,M extends BaseModel<ID>> CollectionResponse<M> findByFilter(QueryFilter<T, ID>queryFilter) throws Exception {
		CollectionResponse<M> response = new CollectionResponse<>();
		if(!queryFilter.getFilterParameter().getClass().isAnnotationPresent(WorkspaceClasses.class))
			throw new Exception("The \"WorkspaceClasses\" annotation is missing in \"FilterParameter\" class");
		WorkspaceClasses workspaceClasses=queryFilter.getFilterParameter().getClass().getAnnotation(WorkspaceClasses.class);
		JpaService<T,ID> service=(JpaService<T, ID>) this.context.getBean(workspaceClasses.service());
		List<T> list = service.findByFilter(queryFilter);
		Long totalCount = service.countByFilter(queryFilter);
		MapperModel<T,M> mapperModel=(MapperModel<T,M>)this.context.getBean(workspaceClasses.mapper());
		List<M> listModel = new ArrayList<M>();
		
		for(T entity:list) {
			M model=mapperModel.convertToModel(entity);
			listModel.add(model);
			
		}
			
		response.setData(listModel);
		response.setTotalCount(totalCount != null ? totalCount : Long.valueOf(0));
		if(queryFilter.getPageable()!=null) {
			response.setPageNumber(queryFilter.getPageable().getPageNumber());
			response.setPageSize(queryFilter.getPageable().getPageSize());
			
		}
		
		return response;
	}

	@Override
	public <T,ID,M extends BaseModel<ID>> ObjectResponse<M> findSingleResultByFilter(QueryFilter<T, ID>queryFilter) throws Exception {
		ObjectResponse<M> response = new ObjectResponse<>();
		if(!queryFilter.getFilterParameter().getClass().isAnnotationPresent(WorkspaceClasses.class))
			throw new Exception("The \"WorkspaceClasses\" annotation is missing in \"FilterParameter\" class");
		WorkspaceClasses workspaceClasses=queryFilter.getFilterParameter().getClass().getAnnotation(WorkspaceClasses.class);
		JpaService<T,ID> service=(JpaService<T, ID>) this.context.getBean(workspaceClasses.service());
		T entity = service.findSingleResultByFilter(queryFilter);
		MapperModel<T,M> mapperModel=(MapperModel<T,M>)this.context.getBean(workspaceClasses.mapper());
		response.setData(mapperModel.convertToModel(entity));
		
		return response;
	}
}
