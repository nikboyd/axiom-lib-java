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
package org.axiom_tools.storage;

import javax.persistence.EntityManagerFactory;
import org.axiom_tools.data.CloudDataSource;
import org.axiom_tools.data.DirectDataSource;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import org.axiom_tools.domain.Contact;
import org.axiom_tools.domain.EmailAddress;
import org.axiom_tools.domain.MailAddress;
import org.axiom_tools.domain.Person;
import org.axiom_tools.domain.PhoneNumber;

import static org.axiom_tools.storage.PersistenceContext.StoragePackage;

/**
 * Configures the persistence mechanisms.
 * @author nik
 */
@Configuration
@EnableTransactionManagement
@Import({ CloudDataSource.class, DirectDataSource.class })
@EnableJpaRepositories(basePackages = { StoragePackage })
public class PersistenceContext {

    public static final String StoragePackage = "org.axiom_tools.storage";
    
    @Autowired
    DirectDataSource directDataSource;
    
    @Autowired
    CloudDataSource cloudDataSource;

    @Profile("default")
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean defaultEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setDataSource(directDataSource.dataSource());
        em.setPackagesToScan(directDataSource.modelPackages());
        em.setJpaProperties(directDataSource.additionalProperties());
        return em;
    }

    @Profile("cloud")
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean cloudEntityManagerFactory() {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        em.setDataSource(cloudDataSource.dataSource());
        em.setPackagesToScan(cloudDataSource.modelPackages());
        em.setJpaProperties(cloudDataSource.additionalProperties());
        return em;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public JpaRepositoryFactory repositoryFactory(EntityManagerFactory emf) {
        return new JpaRepositoryFactory(emf.createEntityManager());
    }

    @Bean
    public AddressStorage addressStorage(JpaRepositoryFactory rf) {
        return rf.getRepository(AddressStorage.class);
    }

    @Bean
    public EmailStorage emailStorage(JpaRepositoryFactory rf) {
        return rf.getRepository(EmailStorage.class);
    }

    @Bean
    public PhoneStorage phoneStorage(JpaRepositoryFactory rf) {
        return rf.getRepository(PhoneStorage.class);
    }

    @Bean
    public StorageMechanism.Registry storageRegistry(
            StorageMechanism<Person, PersonStorage> personStorage,
            StorageMechanism<Contact, ContactStorage> contactStorage,
            StorageMechanism<PhoneNumber, PhoneStorage> phoneStorage,
            StorageMechanism<EmailAddress, EmailStorage> emailStorage,
            StorageMechanism<MailAddress, AddressStorage> addressStorage) {
        return StorageMechanism.Registry.with(
                phoneStorage, emailStorage, addressStorage, contactStorage, personStorage);
    }

    @Bean
    public StorageMechanism<Person, PersonStorage> personStorageMechanism(PersonStorage store) {
        return new StorageMechanism(store, PersonStorage.class, Person.class);
    }

    @Bean
    public StorageMechanism<Contact, ContactStorage> contactStorageMechanism(ContactStorage store) {
        return new StorageMechanism(store, ContactStorage.class, Contact.class);
    }

    @Bean
    public StorageMechanism<MailAddress, AddressStorage> addressStorageMechanism(AddressStorage store) {
        return new StorageMechanism(store, AddressStorage.class, MailAddress.class);
    }

    @Bean
    public StorageMechanism<EmailAddress, EmailStorage> emailStorageMechanism(EmailStorage store) {
        return new StorageMechanism(store, EmailStorage.class, EmailAddress.class);
    }

    @Bean
    public StorageMechanism<PhoneNumber, PhoneStorage> phoneStorageMechanism(PhoneStorage store) {
        return new StorageMechanism(store, PhoneStorage.class, PhoneNumber.class);
    }

}
