package com.synaptix.mybatis.component.cache;

import com.synaptix.mybatis.session.handler.INlsColumnHandler;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.ibatis.cache.Cache;

import java.util.concurrent.locks.ReadWriteLock;

public class NlsColumnCache implements Cache {

    private final INlsColumnHandler nlsColumnHandler;

    private final Cache delegate;

    public NlsColumnCache(INlsColumnHandler nlsColumnHandler, Cache delegate) {
        super();

        this.nlsColumnHandler = nlsColumnHandler;
        this.delegate = delegate;
    }

    private Object buildKey(Object key) {
        return new MyKey(key, nlsColumnHandler.getContext());
    }

    @Override
    public String getId() {
        return delegate.getId();
    }

    @Override
    public void putObject(Object key, Object value) {
        delegate.putObject(buildKey(key), value);
    }

    @Override
    public Object getObject(Object key) {
        return delegate.getObject(buildKey(key));
    }

    @Override
    public Object removeObject(Object key) {
        return delegate.removeObject(buildKey(key));
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public int getSize() {
        return delegate.getSize();
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return delegate.getReadWriteLock();
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return delegate.equals(obj);
    }

    private class MyKey {

        final Object key;

        final Object context;

        MyKey(Object key, Object context) {
            super();
            this.key = key;
            this.context = context;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            MyKey myKey = (MyKey) o;
            return new EqualsBuilder().append(key, myKey.key).append(context, myKey.context).isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37).append(key).append(context).toHashCode();
        }

        @Override
        public String toString() {
            return new ToStringBuilder(this).append("key", key).append("context", context).toString();
        }
    }
}
