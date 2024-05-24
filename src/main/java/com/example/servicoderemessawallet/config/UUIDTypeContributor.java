package com.example.servicoderemessawallet.config;

import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarbinaryTypeDescriptor;

import java.util.UUID;

public class UUIDTypeContributor implements TypeContributor {

    @Override
    public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {
        typeContributions.contributeType(new UUIDType());
    }

    private static class UUIDType extends AbstractSingleColumnStandardBasicType<UUID> {

        public UUIDType() {
            super(VarbinaryTypeDescriptor.INSTANCE, UUIDTypeDescriptor.INSTANCE);
        }

        @Override
        public String getName() {
            return "uuid"; // Nome do tipo registrado para o Hibernate
        }
    }
}
