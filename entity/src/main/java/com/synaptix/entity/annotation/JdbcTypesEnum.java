package com.synaptix.entity.annotation;

import com.synaptix.component.factory.PrimitiveHelper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public enum JdbcTypesEnum {

    DOUBLE {
        @Override
        public boolean matches(Class<?> c) {
            if (Double.class.isAssignableFrom(c)) {
                return true;
            }
            return false;
        }
    },
    REAL {
        @Override
        public boolean matches(Class<?> c) {
            if (Float.class.isAssignableFrom(c)) {
                return true;
            }
            return false;
        }
    },
    INTEGER {
        @Override
        public boolean matches(Class<?> c) {
            if (Integer.class.isAssignableFrom(c)) {
                return true;
            }
            return false;
        }
    },
    NUMERIC {
        @Override
        public boolean matches(Class<?> c) {
            if (Number.class.isAssignableFrom(c)) {
                return true;
            }
            return false;
        }
    },
    DATE {
        @Override
        public boolean matches(Class<?> c) {
            if (Date.class.isAssignableFrom(c) || LocalDateTime.class.isAssignableFrom(c) || LocalTime.class.isAssignableFrom(c) || LocalDate.class.isAssignableFrom(c)) {
                return true;
            }
            return false;
        }
    },
    TIMESTAMP {
        @Override
        public boolean matches(Class<?> c) {
            if (Date.class.isAssignableFrom(c)) {
                return true;
            }
            return false;
        }
    },
    CHAR {
        @Override
        public boolean matches(Class<?> c) {
            if (Character.class.isAssignableFrom(c) || char.class.isAssignableFrom(c)) {
                return true;
            }
            return false;
        }
    },
    BOOLEAN {
        @Override
        public boolean matches(Class<?> c) {
            if (Boolean.class.isAssignableFrom(c) || boolean.class.isAssignableFrom(c)) {
                return true;
            }
            return false;
        }
    },
    BLOB {
        @Override
        public boolean matches(Class<?> c) {
            if (c.isArray() && c.getComponentType().equals(Byte.TYPE)) {
                return true;
            }
            return false;

        }
    },
    VARCHAR {
        @Override
        public boolean matches(Class<?> c) {
            if (String.class.isAssignableFrom(c) || Enum.class.isAssignableFrom(c)) {
                return true;
            }
            return false;
        }
    },
    NONE {
        @Override
        public boolean matches(Class<?> c) {
            return true;
        }
    };

    /**
     * Determine which JDBC type a Java type corresponds to.
     */
    public static JdbcTypesEnum which(Class<?> c) {
        Class<?> c2 = PrimitiveHelper.determineClass(c);
        for (JdbcTypesEnum type : JdbcTypesEnum.values()) {
            if (type.matches(c2)) {
                return type;
            }
        }
        throw new RuntimeException("Java type does not match to JDBC type");
    }

    /**
     * Returns {@code true} if the JdbcTypeEnum matches the class.
     */
    public abstract boolean matches(Class<?> c);
}
