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
package server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Launches embedded Tomcat hosting CXF servlet.
 * @author nik
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan({ ServiceController.FacadePackage })
@ImportResource({ ServiceController.ConfigurationFile })
public class ServiceController {

    private static final Logger Log = LoggerFactory.getLogger(ServiceController.class);
    private static final String Empty = "";
    
    public static final int DefaultPort = 9001;
    public static final String ApiPath = "/api/*";
    public static final String ConfigurationFile = "classpath:hosted-service.xml";
    public static final String FacadePackage = "org.axiom_tools.services";
    
    public static void main(String[] args) {
        Log.info("starting service");
        SpringApplication.run(ServiceController.class, args);
    }
    
    @Bean
    public EmbeddedServletContainerFactory containerFactory() {
        return new TomcatEmbeddedServletContainerFactory(Empty, DefaultPort);
    }
    
    @Bean
    public ServletRegistrationBean servletRegistration() {
        Log.info("hosting CustomerService on port " + DefaultPort);
        ServletRegistrationBean result = new ServletRegistrationBean(new CXFServlet(), ApiPath);
        result.setLoadOnStartup(1);
        return result;
    }
    
} // ServiceController
