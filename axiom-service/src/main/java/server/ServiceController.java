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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import static server.ServiceController.ConfigurationFile;
import static server.ServiceController.FacadePackage;
import static server.ServiceController.StoragePackage;

/**
 * Launches embedded Tomcat hosting CXF servlet.
 * @author nik
 */
@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = { FacadePackage, StoragePackage })
@ImportResource({ ConfigurationFile })
public class ServiceController extends WebMvcConfigurerAdapter {

    public static final String Empty = "";
    public static final String ApiPath = "/api/*";
    public static final String FacadePackage = "org.axiom_tools.services";
    public static final String StoragePackage = "org.axiom_tools.storage";
    public static final String ConfigurationFile = "classpath:hosted-service.xml";

    @Autowired
    private ApplicationContext context;

    @Value("${server.port:9001}")
    int serverPort;

    @Value("${server.address:}")
    String serverAddress;

    @Value("${spring.profiles.active:}")
    String springProfiles;

    public static void main(String[] args) {
        System.out.println("starting service");
        SpringApplication.run(ServiceController.class, args);
    }

    static final String StartMessage = "hosting service at %s:%d with profiles '%s'";
    @Bean public TomcatServletWebServerFactory containerFactory() {
        getLogger().info(String.format(StartMessage, serverAddress, serverPort, springProfiles));
        return new TomcatServletWebServerFactory(Empty, serverPort);
    }

    @Bean
    public ServletRegistrationBean servletRegistration() {
        ServletRegistrationBean result = new ServletRegistrationBean(new CXFServlet(), ApiPath);
        result.setLoadOnStartup(1);
        return result;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/docs/").setViewName("forward:/docs/index.html");
        registry.addViewController("/docs/ui/").setViewName("forward:/docs/ui/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/docs/**")
                .addResourceLocations("classpath:/resources/docs/");
    }

    private Logger getLogger() {
        return LoggerFactory.getLogger(getClass());
    }

} // ServiceController
