package org.gosh.validation.functests;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

public class TestDataSourceFactory {
	public static SQLServerDataSource getDataSource() {
		SQLServerDataSource dataSource = new SQLServerDataSource();
		dataSource.setDatabaseName("RE7");
		dataSource.setServerName("goshcc-retest1");
		dataSource.setUser("reopen7");
		dataSource.setPassword("iomega");
		dataSource.setPortNumber(1433);
		dataSource.setSelectMethod("CURSOR");
		return dataSource;
	}
	
	public static SQLServerDataSource getCIIADataSource() {
		SQLServerDataSource dataSource = new SQLServerDataSource();
		dataSource.setDatabaseName("CIIA");
		dataSource.setServerName("goshcc-legacy");
		dataSource.setUser("jcaps");
		dataSource.setPassword("aztecs");
		dataSource.setPortNumber(1433);
		dataSource.setSelectMethod("CURSOR");
		return dataSource;
	}
	
	public static SQLServerDataSource getLegacyDataSource() {
		SQLServerDataSource dataSource = new SQLServerDataSource();
		dataSource.setDatabaseName("Website");
		dataSource.setServerName("goshcc-legacy");
		dataSource.setUser("ICAN");
		dataSource.setPassword("tn3vni");
		dataSource.setPortNumber(1433);
		dataSource.setSelectMethod("CURSOR");
		return dataSource;
	}
}
