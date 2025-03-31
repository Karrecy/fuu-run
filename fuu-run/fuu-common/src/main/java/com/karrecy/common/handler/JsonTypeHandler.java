package com.karrecy.common.handler;

import com.baomidou.mybatisplus.extension.handlers.AbstractJsonTypeHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JsonTypeHandler<T> extends BaseTypeHandler<T> {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private final Class<T> type;

    public JsonTypeHandler(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, T parameter, JdbcType jdbcType) throws SQLException {
        // 将对象转换为 JSON 字符串，并设置到 PreparedStatement
        try {
            ps.setString(i, OBJECT_MAPPER.writeValueAsString(parameter));
        } catch (Exception e) {
            throw new SQLException("Error converting object to JSON", e);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从 ResultSet 中获取字符串，并解析为对象
        String json = rs.getString(columnName);
        try {
            return json == null ? null : OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new SQLException("Error converting JSON to object", e);
        }
    }

    @Override
    public T getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 从 ResultSet 中获取字符串，并解析为对象
        String json = rs.getString(columnIndex);
        try {
            return json == null ? null : OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new SQLException("Error converting JSON to object", e);
        }
    }

    @Override
    public T getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 从 CallableStatement 中获取字符串，并解析为对象
        String json = cs.getString(columnIndex);
        try {
            return json == null ? null : OBJECT_MAPPER.readValue(json, type);
        } catch (Exception e) {
            throw new SQLException("Error converting JSON to object", e);
        }
    }
}
