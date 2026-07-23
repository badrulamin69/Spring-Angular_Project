package com.badrulamin.University_Management.config;

import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.stereotype.Component;

import java.beans.PropertyDescriptor;
import java.util.Set;

@Component
public class EntityUpdateUtil {

    private static final Set<String> SKIP_PROPS = Set.of(
            "id", "version", "createdAt", "updatedAt",
            "createdBy", "updatedBy", "deleted", "deletedAt", "deletedBy"
    );

    public void merge(Object source, Object target) {
        try {
            PropertyDescriptor[] targetPds = java.beans.Introspector.getBeanInfo(target.getClass()).getPropertyDescriptors();
            PropertyAccessor targetAccessor = PropertyAccessorFactory.forBeanPropertyAccess(target);

            for (PropertyDescriptor pd : targetPds) {
                if (SKIP_PROPS.contains(pd.getName())) continue;
                if (pd.getReadMethod() == null || pd.getWriteMethod() == null) continue;

                try {
                    PropertyAccessor sourceAccessor = PropertyAccessorFactory.forBeanPropertyAccess(source);
                    if (!sourceAccessor.isReadableProperty(pd.getName())) continue;

                    Object value = sourceAccessor.getPropertyValue(pd.getName());
                    if (value != null) {
                        targetAccessor.setPropertyValue(pd.getName(), value);
                    }
                } catch (Exception ignored) {
                }
            }
        } catch (java.beans.IntrospectionException ignored) {
        }
    }
}
