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
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import static org.axiom_tools.data.DirectDataSource.DatabaseConfiguration;

/**
 * Configures a data source from a properties file.
 * @author nik
 */
@Configuration
@PropertySource(DatabaseConfiguration)
public class DirectDataSource extends BasicDataSource {

    public static final String DatabaseConfiguration = "classpath:db.properties";

    @Value("${${db.type}.db.driver}")
    private String driverClassName;

    @Value("${${db.type}.db.username}")
    private String databaseUsername;

    @Value("${${db.type}.db.password}")
    private String databasePassword;

    @Value("${${db.type}.db.url}")
    private String databaseURL;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        dataSource.setUrl(databaseURL);
        return dataSource;
    }

}
