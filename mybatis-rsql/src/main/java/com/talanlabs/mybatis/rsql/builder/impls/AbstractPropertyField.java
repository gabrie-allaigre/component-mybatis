package com.talanlabs.mybatis.rsql.builder.impls;

import com.talanlabs.mybatis.rsql.builder.RsqlBuilder;
import com.talanlabs.mybatis.rsql.builder.impls.properties.*;

public abstract class AbstractPropertyField<Builder> extends AbstractField<Builder> {

    AbstractPropertyField(RsqlBuilder rsqlBuilder, StringBuilder context) {
        super(rsqlBuilder, context);
    }

    /**
     * String field
     *
     * @param field a name
     */
    public StringProperty<Builder> string(String field) {
        return new StringProperty<>(field, this);
    }

    /**
     * String field
     *
     * @param field a name
     */
    public BooleanProperty<Builder> bool(String field) {
        return new BooleanProperty<Builder>(field, this);
    }

    /**
     * Short field
     *
     * @param field a name
     */
    public ShortProperty<Builder> shortNum(String field) {
        return new ShortProperty<Builder>(field, this);
    }

    /**
     * Int field
     *
     * @param field a name
     */
    public IntegerProperty<Builder> intNum(String field) {
        return new IntegerProperty<Builder>(field, this);
    }

    /**
     * Float field
     *
     * @param field a name
     */
    public FloatProperty<Builder> floatNum(String field) {
        return new FloatProperty<Builder>(field, this);
    }

    /**
     * Double field
     *
     * @param field a name
     */
    public DoubleProperty<Builder> doubleNum(String field) {
        return new DoubleProperty<Builder>(field, this);
    }

    /**
     * Long field
     *
     * @param field a name
     */
    public LongProperty<Builder> longNum(String field) {
        return new LongProperty<Builder>(field, this);
    }

    /**
     * Enum field
     *
     * @param field a name
     */
    public <E extends Enum<E>> EnumProperty<Builder, E> enumeration(String field) {
        return new EnumProperty<>(field, this);
    }
}
