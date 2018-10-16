package com.example.blob;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.dbunit.ext.postgresql.PostgresqlDataTypeFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DbUnitConfig {

    @Bean(name="dbUnitDataTypeFactory")
    public DefaultDataTypeFactory datatypeFactory() {
        return new PostgresqlDataTypeFactory();
    }

    @Bean(name="dbUnitDatabaseConfig")
    public DatabaseConfigBean databaseConfigBean() {
        DatabaseConfigBean bean = new DatabaseConfigBean();
        bean.setDatatypeFactory(datatypeFactory());
        return bean;
    }

    @Bean(name="dbUnitDatabaseConnection")
    public DatabaseDataSourceConnectionFactoryBean databaseDataSourceConnectionFactoryBean(DataSource ds) {
        DatabaseDataSourceConnectionFactoryBean bean = new DatabaseDataSourceConnectionFactoryBean();
        bean.setDatabaseConfig(databaseConfigBean());
        bean.setDataSource(ds);
        return bean;
    }
}
