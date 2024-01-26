package org.example.Model;

import org.example.Dto.UserDto;
import org.example.Util.SqlUtil;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserModel {
    public static boolean existsUser(String username) throws SQLException {
        String query = "SELECT userName FROM user WHERE userName = ?";
        ResultSet rs = SqlUtil.execute(query,username);
        return rs.next();
    }

    public static UserDto userDetails(String username) throws SQLException {
        String query = "SELECT * FROM user WHERE userName = ?";
        ResultSet rs = SqlUtil.execute(query,username);
        if (rs.next()){
            return new UserDto(rs.getString(1),rs.getString(2),rs.getBinaryStream(3));
        } else {
            return null;
        }
    }

    public static boolean saveUser(UserDto userDto) throws SQLException {
        String query = "INSERT INTO user VALUES(?,?,?)";
        return SqlUtil.execute(query,userDto.getUsername(),userDto.getPassword(),userDto.getProfilePic());
    }
}

