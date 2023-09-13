package com.teample.packages.profile.repository;

import com.teample.packages.member.domain.GenderType;
import com.teample.packages.member.domain.Member;
import com.teample.packages.profile.domain.Profile;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Slf4j
public class JdbcProfileRepository implements ProfileRepository {

    private final DataSource dataSource;

    public JdbcProfileRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    @Override
    public Profile save(Profile profile) {
        String sql = "insert into profile(authorId, tags, introduction) values (?, ?, ?)";


        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setLong(1, profile.getAuthorId());
            pstmt.setString(2, profile.getTags());
            pstmt.setString(3, profile.getIntroduction());

            pstmt.executeUpdate();

            rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                profile.setId(rs.getLong(1));
            } else {
                throw new SQLException("id 조회 실패");
            }
            return profile;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }


    }

    @Override
    public Optional<Profile> findById(Long id) {
        String sql = "select * from profile left outer join member on profile.authorId = member.id where profile.id = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, id);
            rs = pstmt.executeQuery();

            if(rs.next()) {
                Profile profile = new Profile();

                profile.setId(rs.getLong("id"));
                profile.setAuthorId(rs.getLong("authorId"));
                profile.setAuthorName(rs.getString("name"));
                profile.setGender(GenderType.valueOf(rs.getString("gender")));
                profile.setFields(getFieldsList(rs));
                profile.setTags(rs.getString("tags"));
                profile.setIntroduction(rs.getString("introduction"));

                return Optional.of(profile);
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
    public List<Profile> findAllByAuthorId(Long authorId) {
        String sql = "select * from profile left outer join member on profile.authorId = member.id where authorId = ?";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, authorId);
            rs = pstmt.executeQuery();


            List<Profile> profiles = new ArrayList<Profile>();


            while(rs.next()) {
                Profile profile = new Profile();

                profile.setId(rs.getLong("id"));
                profile.setAuthorId(rs.getLong("authorId"));
                profile.setAuthorName(rs.getString("name"));
                profile.setGender(GenderType.valueOf(rs.getString("gender")));
                profile.setFields(getFieldsList(rs));
                profile.setTags(rs.getString("tags"));
                profile.setIntroduction(rs.getString("introduction"));

                profiles.add(profile);
            }

            return profiles;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public List<Profile> findAll() {
        String sql = "select * from profile left outer join member on profile.authorId = member.id ";

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();


            List<Profile> profiles = new ArrayList<Profile>();


            while(rs.next()) {
                Profile profile = new Profile();

                profile.setId(rs.getLong("id"));
                profile.setAuthorId(rs.getLong("authorId"));
                profile.setAuthorName(rs.getString("name"));
                profile.setGender(GenderType.valueOf(rs.getString("gender")));
                profile.setFields(getFieldsList(rs));
                profile.setTags(rs.getString("tags"));
                profile.setIntroduction(rs.getString("introduction"));

                profiles.add(profile);
            }

            return profiles;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            close(conn, pstmt, rs);
        }
    }

    @Override
    public void update(Long updateProfileId, Profile updateParam) {
        Optional<Profile> optionalProfile = findById(updateProfileId);

        if (optionalProfile.isPresent()) {
            Profile findProfile = optionalProfile.get();

            findProfile.setTags(updateParam.getTags());
            findProfile.setIntroduction(updateParam.getIntroduction());

            // 업데이트된 정보를 데이터베이스에 반영
            String sql = "update profile set tags = ?, introduction = ? where profile.id = ?";
            Connection conn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;

            try {
                conn = getConnection();
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, findProfile.getTags());
                pstmt.setString(2, findProfile.getIntroduction());
                pstmt.setLong(3, updateProfileId);
                pstmt.executeUpdate();
            } catch (Exception e) {
                throw new IllegalStateException(e);
            } finally {
                close(conn, pstmt, rs);
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Long findAuthorIdByProfileId(Long profileId) {
        Optional<Profile> optionalProfile = findById(profileId);


        if (optionalProfile.isPresent()) {

            Profile findProfile = optionalProfile.get();
            return findProfile.getAuthorId();

        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void delete(Long profileId) {
        String sql = "DELETE FROM profile WHERE id = ?";


        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setLong(1, profileId);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException();
        } finally {
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


    private static List<String> getFieldsList(ResultSet rs) throws SQLException {
        String fieldsStr = rs.getString("fields");

        List<String> fields = new ArrayList<>();

        // 문자열에서 '['와 ']'를 제거하고 쉼표로 분리
        String[] parts = fieldsStr.substring(1, fieldsStr.length() - 1).split(", ");

        // 배열을 리스트로 변환
        for (String part : parts) {
            fields.add(part);
        }
        return fields;
    }
}
