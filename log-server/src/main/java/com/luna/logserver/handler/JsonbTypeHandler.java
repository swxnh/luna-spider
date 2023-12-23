package com.luna.logserver.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;
import org.postgresql.util.PGobject;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author 文轩
 */
@MappedTypes({Object.class})
public class JsonbTypeHandler extends BaseTypeHandler<Object> {



    private final PGobject pGobject = new PGobject();

    private final ObjectMapper objectMapper = new ObjectMapper();



    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, Object o, JdbcType jdbcType) throws SQLException {

        pGobject.setType("jsonb");

        try {
            pGobject.setValue(objectMapper.writeValueAsString(o));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        preparedStatement.setObject(i,  pGobject);

    }



    @Override
    public Object getNullableResult(ResultSet resultSet, String s) throws SQLException {

        return resultSet.getString(s);

    }



    @Override
    public Object getNullableResult(ResultSet resultSet, int i) throws SQLException {

        return resultSet.getString(i);

    }



    @Override
    public Object getNullableResult(CallableStatement callableStatement, int i) throws SQLException {

        return callableStatement.getString(i);

    }

}