package com.talanlabs.entity.factory;

import com.google.common.base.Objects;
import com.talanlabs.entity.IId;

import java.util.UUID;

public class IdFactory {

    private static IdFactory instance;

    public IdFactory() {
        super();
    }

    public static synchronized IdFactory getInstance() {
        if (instance == null) {
            instance = new IdFactory();
        }
        return instance;
    }

    public static synchronized void setInstance(IdFactory instance) {
        IdFactory.instance = instance;
    }

    public IId newId() {
        return new IdString(UUID.randomUUID().toString());
    }

    public static class IdString implements IId {

        private String id;

        public IdString(String id) {
            super();
            this.id = id;
        }

        public static IId from(String id) {
            return new IdString(id);
        }

        public String getId() {
            return id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            IdString idString = (IdString) o;
            return Objects.equal(id, idString.id);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(id);
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
