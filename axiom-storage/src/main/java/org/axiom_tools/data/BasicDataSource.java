/**
 * Copyright 2015,2016 Nikolas Boyd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.axiom_tools.data;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * Abstract data source.
 *
 * @author nik
 */
public abstract class BasicDataSource {

    public static final String Comma = ",";
    public static final String HibernateDialect = "hibernate.dialect";
    public static final String HibernateCodeDDL = "hibernate.hbm2ddl.auto";

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyReplacer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    @Value("${${db.type}.db.dialect}")
    private String databaseDialect;

    @Value("${${db.type}.db.code.ddl}")
    private String codeGeneration;

    @Value("${db.model.packages}")
    private String modelPackages;

    public String[] modelPackages() {
        return modelPackages.split(Comma);
    }

    public Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty(HibernateDialect, databaseDialect);
        if (!codeGeneration.isEmpty()) {
            properties.setProperty(HibernateCodeDDL, codeGeneration);
        }
        return properties;
    }

}
