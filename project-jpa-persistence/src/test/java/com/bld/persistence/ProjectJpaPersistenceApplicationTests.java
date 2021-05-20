package com.bld.persistence;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.bld.persistence.core.domain.ConfiguraMenu;
import com.bld.persistence.core.service.ConfiguraMenuService;
import com.bld.persistence.filter.ConfiguraMenuFilter;

import bld.commons.config.annotation.EnableJpaService;
import bld.commons.reflection.model.QueryFilter;

@SpringBootTest
@EnableTransactionManagement
@EnableJpaService
class ProjectJpaPersistenceApplicationTests {
	
	@Autowired
	private ConfiguraMenuService confMenuService;

	@Test
	void searchByFilter() {
		try {
			ConfiguraMenuFilter filter=new ConfiguraMenuFilter(Integer.valueOf(10),null,null);
			filter.setSortKey("configuraMenu.quantita");
			filter.setSortOrder("asc");
			filter.setPageNumber(0);
			filter.setPageSize(50);
			QueryFilter<ConfiguraMenu, Long>qf=new QueryFilter<>(filter);
			List<ConfiguraMenu> listConfMenu = this.confMenuService.findByFilter(qf);
			
			System.out.println(listConfMenu.size());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	
	}
	

}
