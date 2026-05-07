package com.roboshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiObjectFactoryBean;

import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * DataSource configuration for Tomcat deployment.
 *
 * DR NOTE - Active/Passive Strategy:
 * - Primary: JNDI DataSource configured in Tomcat's context.xml pointing to primary MySQL
 * - Standby: Separate Tomcat instance with JNDI pointing to MySQL read-replica
 * - Failover: DNS switch (Route 53 health check) to standby Tomcat
 * - Limitation: In-memory HTTP sessions are lost on failover (sticky sessions required)
 *
 * VERTICAL SCALING NOTE:
 * - This monolith runs as a single WAR. To handle more cart traffic,
 *   you must scale the ENTIRE Tomcat server (CPU, memory) even though
 *   only the cart module needs more resources. This is a key limitation
 *   compared to microservices where you'd scale only the cart service.
 */
@Configuration
public class DataSourceConfig {

    @Profile("tomcat")
    @Bean
    public DataSource jndiDataSource() throws NamingException {
        JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
        bean.setJndiName("java:comp/env/jdbc/roboshop");
        bean.setProxyInterface(DataSource.class);
        bean.setLookupOnStartup(true);
        bean.afterPropertiesSet();
        return (DataSource) bean.getObject();
    }
}
