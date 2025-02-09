package com.ajie.enterprisewechat.config.ebean;

import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Properties;

@Configuration
public class MigrationRunnerConfig {
    @Value("${spring.datasource.driverClassName}")
    private String driver;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @PostConstruct
    public void init(){
        DataSourceConfig dataSourceConfig =new DataSourceConfig();
        dataSourceConfig.setDriver(driver);
        dataSourceConfig.setPlatform("mysql");
        dataSourceConfig.setUrl(url);
        dataSourceConfig.setUsername(username);
        dataSourceConfig.setPassword(password);
        DatabaseConfig databaseConfig = new DatabaseConfig();
        databaseConfig.setDataSourceConfig(dataSourceConfig);
        Database database= DatabaseFactory.create(databaseConfig);
        MigrationConfig config  =new MigrationConfig();
        config.setDbUsername(username);
        config.setDbPassword(password);
        config.setDbUrl(url);
        Properties properties = new Properties();
        properties.put("ebean.migration.run",true);
        properties.put("ebean.ddl.generate",true);
        config.load(properties);
        MigrationRunner runner  =new MigrationRunner(config);
        runner.run();
    }

}
