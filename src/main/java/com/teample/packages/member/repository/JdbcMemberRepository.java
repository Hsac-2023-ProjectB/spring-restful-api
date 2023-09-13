package com.teample.packages.member.repository;

import com.teample.packages.member.domain.GenderType;
import com.teample.packages.member.domain.Member;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcMemberRepository implements MemberRepository {
    private final DataSource dataSource;


    public JdbcMemberRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


//    DB 접근 로직

    @Override
    public Member save(Member member) {

        String sql = "insert into member(name, loginId, password, email, birthDate, gender, fields) values (?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, member.getName());
            pstmt.setString(2, member.getLoginId());
            pstmt.setString(3, member.getPassword());
            pstmt.setString(4, member.getEmail());
            pstmt.setDate(5, member.getBirthDate());
            pstmt.setString(6, member.getGender().name());
            pstmt.setString(7, member.getFields().toString());

            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                member.setId(rs.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }
            return member;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public Optional<Member> findById(Long id) {
        String sql = "select * from member where id = ?";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();

            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                member.setLoginId(rs.getString("loginId"));
                member.setPassword(rs.getString("password"));
                member.setEmail(rs.getString("email"));
                member.setBirthDate(rs.getDate("birthDate"));
                member.setGender(GenderType.valueOf(rs.getString("gender")));
                member.setFields(getFieldsList(rs));
                return Optional.of(member);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public void update(Long id, Member updateParam) {
        String sql = "update member set name = ?, password = ?, email = ?, birthDate = ?, gender = ?, fields = ? where id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;


        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);

            pstmt.setString(1, updateParam.getName());
            pstmt.setString(2, updateParam.getPassword());
            pstmt.setString(3, updateParam.getEmail());
            pstmt.setDate(4, updateParam.getBirthDate());
            pstmt.setString(5, updateParam.getGender().name());
            pstmt.setString(6, updateParam.getFields().toString());
            pstmt.setLong(7, id);


            pstmt.executeUpdate();
        }catch (Exception e) {
            throw new IllegalStateException(e);
        }finally {
            close(conn, pstmt, rs);
        }

    }

    @Override
    public Optional<Member> findByLoginId(String loginId) {
        String sql = "select * from member where loginId = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, loginId);
            rs = pstmt.executeQuery();
            if(rs.next()) {
                Member member = new Member();
                member.setId(rs.getLong("id"));
                member.setName(rs.getString("name"));
                member.setLoginId(rs.getString("loginId"));
                member.setPassword(rs.getString("password"));
                member.setEmail(rs.getString("email"));
                member.setBirthDate(rs.getDate("birthDate"));
                member.setGender(GenderType.valueOf(rs.getString("gender")));
                member.setFields(getFieldsList(rs));
                return Optional.of(member);
            }
            return Optional.empty();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    private static List<String> getFieldsList(ResultSet rs) throws SQLException {
        String fieldsStr = rs.getString("fields");
        ;
        List<String> fields = new ArrayList<>();

        // 문자열에서 '['와 ']'를 제거하고 쉼표로 분리
        String[] parts = fieldsStr.substring(1, fieldsStr.length() - 1).split(", ");

        // 배열을 리스트로 변환
        for (String part : parts) {
            fields.add(part);
        }
        return fields;
    }

    @Override
    public void delete(Long memberId) {
        String sql = "delete from member where id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1,  memberId);

            pstmt.executeUpdate();
        }catch (Exception e) {
            throw new IllegalStateException(e);
        }finally {
            close(conn, pstmt, rs);
        }

    }


    private Connection getConnection() {
        return DataSourceUtils.getConnection(dataSource);
    }
    private void close(Connection conn, PreparedStatement pstmt, ResultSet rs){
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (pstmt != null) {
                pstmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                close(conn);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void close(Connection conn) throws SQLException {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }



}
