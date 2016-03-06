package com.synaptix.mybatis.simple.handler;

import com.synaptix.entity.IId;
import com.synaptix.entity.factory.IdFactory;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeException;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@MappedTypes({ IId.class, IdFactory.IdString.class })
public class IdTypeHandler extends BaseTypeHandler<Object> {

    @Override
    public void setParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (parameter == null) {
            try {
                ps.setNull(i, JdbcType.VARCHAR.TYPE_CODE);
            } catch (SQLException e) {
                throw new TypeException("Error setting null for parameter #" + i + " with JdbcType " + jdbcType + " . "
                        + "Try setting a different JdbcType for this parameter or a different jdbcTypeForNull configuration property. " + "Cause: " + e, e);
            }
        } else {
            setNonNullParameter(ps, i, parameter, jdbcType);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Object parameter, JdbcType jdbcType) throws SQLException {
        if (parameter instanceof IdFactory.IdString) {
            ps.setString(i, ((IdFactory.IdString) parameter).getId());
        } else if (parameter instanceof String) {
            ps.setString(i, (String) parameter);
        } else {
            throw new IllegalArgumentException("Not IId for parameter=" + parameter);
        }
    }

    @Override
    public IId getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String bs = rs.getString(columnName);
        return parse(bs);
    }

    @Override
    public IId getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String bs = cs.getString(columnIndex);
        return parse(bs);
    }

    @Override
    public IId getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String bs = rs.getString(columnIndex);
        return parse(bs);
    }

    private IId parse(String bs) throws SQLException {
        return bs != null ? new IdFactory.IdString(bs) : null;
    }
}
