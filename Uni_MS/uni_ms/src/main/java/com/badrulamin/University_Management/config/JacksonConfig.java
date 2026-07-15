package com.badrulamin.University_Management.config;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.core.JsonGenerator;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class JacksonConfig {

    @Bean
    public Module hibernateLazyProxyModule() {
        SimpleModule module = new SimpleModule("HibernateLazyProxyModule");
        module.addSerializer(HibernateProxy.class, new HibernateProxySerializer());
        return module;
    }

    public static class HibernateProxySerializer extends JsonSerializer<HibernateProxy> {
        @Override
        public void serialize(HibernateProxy proxy, JsonGenerator generator, SerializerProvider provider) throws IOException {
            Object impl = proxy.getHibernateLazyInitializer().getImplementation();
            generator.writeObject(impl);
        }
    }
}
