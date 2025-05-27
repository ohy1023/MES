package com.ohyungsang.mes.config.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.nio.ByteBuffer; // UUID <-> byte[] 변환에 핵편함
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

// 이 TypeHandler가 어떤 JDBC 타입(BINARY)과 Java 타입(UUID)을 다룰 건지 명시해줘
@MappedJdbcTypes(JdbcType.BINARY)
@MappedTypes(UUID.class)
public class UuidBinaryTypeHandler extends BaseTypeHandler<UUID> {

    // UUID 객체를 받아서 PreparedStatement에 BINARY 형태로 설정할 때 호출돼
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, UUID parameter, JdbcType jdbcType) throws SQLException {
        // UUID를 16바이트 BINARY 형태로 변환
        // Java의 UUID는 내부적으로 long 2개로 표현되고, 이걸 ByteBuffer로 합치면 16바이트 됨.
        // 네트워크 바이트 순서 (빅 엔디안)는 RFC에서 정한 기준이라 보통 ByteBuffer 기본값(빅 엔디안) 쓰면 돼.
        byte[] uuidBytes = new byte[16];
        ByteBuffer bb = ByteBuffer.wrap(uuidBytes);
        bb.putLong(parameter.getMostSignificantBits()); // UUID 앞쪽 8바이트
        bb.putLong(parameter.getLeastSignificantBits()); // UUID 뒤쪽 8바이트

        ps.setBytes(i, uuidBytes); // PreparedStatement에 BINARY(byte[]) 형태로 설정
    }

    // ResultSet에서 컬럼 이름으로 BINARY 데이터를 읽어서 UUID 객체로 변환할 때 호출돼 (null 아닐 경우)
    @Override
    public UUID getNullableResult(ResultSet rs, String columnName) throws SQLException {
        byte[] bytes = rs.getBytes(columnName);
        return fromBytes(bytes); // byte[] -> UUID 변환 로직 호출
    }

    // ResultSet에서 컬럼 인덱스로 BINARY 데이터를 읽어서 UUID 객체로 변환할 때 호출돼 (null 아닐 경우)
    @Override
    public UUID getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        byte[] bytes = rs.getBytes(columnIndex);
        return fromBytes(bytes); // byte[] -> UUID 변환 로직 호출
    }

    // CallableStatement에서 BINARY 데이터를 읽어서 UUID 객체로 변환할 때 호출돼 (null 아닐 경우)
    @Override
    public UUID getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        byte[] bytes = cs.getBytes(columnIndex);
        return fromBytes(bytes); // byte[] -> UUID 변환 로직 호출
    }

    // byte[] -> UUID 실제 변환 로직
    private UUID fromBytes(byte[] bytes) {
        if (bytes == null || bytes.length != 16) {
            // null이거나 16바이트가 아니면 유효한 UUID BINARY가 아님
            return null;
        }
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        long firstLong = bb.getLong(); // 앞쪽 8바이트 -> Most Significant Bits
        long secondLong = bb.getLong(); // 뒤쪽 8바이트 -> Least Significant Bits
        return new UUID(firstLong, secondLong); // long 2개로 UUID 객체 생성
    }
}
