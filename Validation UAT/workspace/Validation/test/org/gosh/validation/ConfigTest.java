package org.gosh.validation;

import javax.sql.DataSource;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.test.AbstractDependencyInjectionSpringContextTests;

public class ConfigTest extends AbstractDependencyInjectionSpringContextTests {
    protected String[] getConfigLocations() {
        return new String[] { "classpath:config.xml" };
    }
    
    public void test() throws Exception {
    	new SimpleJdbcTemplate(reDataSource).queryForInt("select count(*) from APPEAL");
    	new SimpleJdbcTemplate((DataSource)getApplicationContext().getBean("legacyDataSource")).queryForInt("select count(*) from SupplierBatchNumberLog");
    	new SimpleJdbcTemplate((DataSource)getApplicationContext().getBean("webDonationsLegacyDataSource")).queryForInt("select count(*) from reasondata");
    }
    
    private DataSource reDataSource;
    
    public void setReDataSource(DataSource reDataSource) {
		this.reDataSource = reDataSource;
	}
}
