package com.synaptix.mybatis.test.it;

import com.synaptix.component.IComponent;
import com.synaptix.mybatis.component.session.handler.INlsColumnHandler;

import java.util.HashMap;
import java.util.Map;

public class DefaultNlsColumnHandler implements INlsColumnHandler {

    private String languageCode;

    public DefaultNlsColumnHandler() {
        super();

        this.languageCode = "fra";
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    @Override
    public Object getContext() {
        return languageCode;
    }

    @Override
    public Map<String, Object> getAdditionalParameter(Class<? extends IComponent> componentClass, String propertyName) {
        Map<String, Object> map = new HashMap<>();
        map.put("languageCode", languageCode);
        return map;
    }

    @Override
    public String getSelectNlsColumnId(Class<? extends IComponent> componentClass, String propertyName) {
        return "com.synaptix.mybatis.test.it.mapper.NlsMapper.selectNlsColumn";
    }

    @Override
    public String getMergeNlsColumnId(Class<? extends IComponent> componentClass, String propertyName) {
        return null;
    }

    @Override
    public String getDeleteNlsColumnId(Class<? extends IComponent> componentClass, String propertyName) {
        return null;
    }

    @Override
    public boolean isUpdateDefaultNlsColumn(Class<? extends IComponent> componentClass, String propertyName) {
        return false;
    }

}
