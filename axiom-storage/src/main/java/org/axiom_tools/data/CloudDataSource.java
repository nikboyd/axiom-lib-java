/**
 * Copyright 2015 Nikolas Boyd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.axiom_tools.data;

import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.axiom_tools.data.CloudDataSource.DatabaseConfiguration;

/**
 * Configures a data source from environment variables.
 * @author nik
 */
@Configuration
@Profile("cloud")
@PropertySource(DatabaseConfiguration)
public class CloudDataSource extends BasicDataSource {

    private static final String Org = "org.";
    private static final String Driver = ".Driver";
    public static final String DatabaseConfiguration = "classpath:db.properties";

    @Value("${cloud.db.driver}")
    private String driverClassName;

    @Value("#{environment['${cloud.db.username}']}")
    private String databaseUsername;

    @Value("#{environment['${cloud.db.password}']}")
    private String databasePassword;

    @Value("#{environment['${cloud.db.host}']}")
    private String databaseHost;

    @Value("#{environment['${cloud.db.name}']}")
    private String databaseName;

    @Bean
    public DataSource dataSource() {
        int pos = Org.length();
        int max = driverClassName.length() - Driver.length();
        String dialect = driverClassName.substring(pos, max);
        String databaseURL = "jdbc:" + dialect + "://" + databaseHost + "/" + databaseName;
        getLogger().info(String.format(CloudDriver, driverClassName));
        getLogger().info(String.format(CloudURL, databaseURL));

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        dataSource.setUrl(databaseURL);
        return dataSource;
    }
    
    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }
    
    static final String CloudURL = "JPA cloud URL: %s";
    static final String CloudDriver = "JPA cloud driver: %s";

}
