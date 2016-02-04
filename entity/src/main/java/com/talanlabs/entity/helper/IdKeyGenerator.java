package com.talanlabs.entity.helper;

import com.talanlabs.component.IComponent;
import com.talanlabs.entity.factory.IdFactory;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

import java.sql.Statement;

public class IdKeyGenerator implements KeyGenerator {

    private final String idPropertyName;

    public IdKeyGenerator(Configuration configuration, String id, Class<? extends IComponent> componentClass, String idPropertyName) {
        super();

        this.idPropertyName = idPropertyName;
    }

    @Override
    public void processBefore(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        if (parameter instanceof IComponent) {
            IComponent component = (IComponent) parameter;
            component.straightSetProperty(idPropertyName, IdFactory.getInstance().newId());
        }
    }

    @Override
    public void processAfter(Executor executor, MappedStatement ms, Statement stmt, Object parameter) {
        // Nothing
    }
}
