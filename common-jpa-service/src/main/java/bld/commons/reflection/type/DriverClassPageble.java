package bld.commons.reflection.type;

public enum DriverClassPageble {

	
	org_postgresql_Driver(" OFFSET :bldStartRow LIMIT :bldPageSize\n "),
	com_mysql_cj_jdbc_Driver(" LIMIT :bldStartRow,:bldPageSize \n"),
	com_mysql_jdbc_Driver(" LIMIT :bldStartRow,:bldPageSize \n"),
	oracle_jdbc_OracleDriver(" OFFSET :bldStartRow ROWS FETCH NEXT :bldPageSize ROWS ONLY \n"),
	;
	
	
	private String pageable;
	

	private DriverClassPageble(String pageable) {
		this.pageable = pageable;
	}


	public String getPageable() {
		return pageable;
	}


	
	
	
}
