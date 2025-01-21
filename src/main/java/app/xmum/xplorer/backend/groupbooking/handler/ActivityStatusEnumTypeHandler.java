package app.xmum.xplorer.backend.groupbooking.handler;

import app.xmum.xplorer.backend.groupbooking.enums.ActivityStatusEnum;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityStatusEnumTypeHandler extends BaseTypeHandler<ActivityStatusEnum> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, ActivityStatusEnum parameter, JdbcType jdbcType) throws SQLException {
        // 将枚举的 code 值插入数据库
        ps.setInt(i, parameter.getCode());
    }

    @Override
    public ActivityStatusEnum getNullableResult(ResultSet rs, String columnName) throws SQLException {
        // 从数据库读取 code 值并转换为枚举
        int code = rs.getInt(columnName);
        return ActivityStatusEnum.getByCode(code);
    }

    @Override
    public ActivityStatusEnum getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        // 从数据库读取 code 值并转换为枚举
        int code = rs.getInt(columnIndex);
        return ActivityStatusEnum.getByCode(code);
    }

    @Override
    public ActivityStatusEnum getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        // 从数据库读取 code 值并转换为枚举
        int code = cs.getInt(columnIndex);
        return ActivityStatusEnum.getByCode(code);
    }
}