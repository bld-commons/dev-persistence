/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.type.DriverClassPageble.java
 */
package bld.commons.reflection.type;

/**
 * The Enum DriverClassPageble.
 */
public enum DriverClassPageble {

	
	/** The org postgresql driver. */
	org_postgresql_Driver(" OFFSET :queryStartRow LIMIT :queryPageSize\n "),
	
	/** The com mysql cj jdbc driver. */
	com_mysql_cj_jdbc_Driver(" LIMIT :queryStartRow,:queryPageSize \n"),
	
	/** The com mysql jdbc driver. */
	com_mysql_jdbc_Driver(" LIMIT :queryStartRow,:queryPageSize \n"),
	
	/** The oracle jdbc oracle driver. */
	oracle_jdbc_OracleDriver(" OFFSET :queryStartRow ROWS FETCH NEXT :queryPageSize ROWS ONLY \n"),
	;
	
	
	/** The pageable. */
	private String pageable;
	

	/**
	 * Instantiates a new driver class pageble.
	 *
	 * @param pageable the pageable
	 */
	private DriverClassPageble(String pageable) {
		this.pageable = pageable;
	}


	/**
	 * Gets the pageable.
	 *
	 * @return the pageable
	 */
	public String getPageable() {
		return pageable;
	}


	
	
	
}
