package org.example.Model;

import org.example.Dto.ChatDto;
import org.example.Util.SqlUtil;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ChatModel {
    public boolean saveChat(ChatDto dto) throws SQLException{
        String query = "insert into chat values(?,?,?,?,?)";
        return SqlUtil.execute(query,dto.getUserName(),dto.getId(),dto.getMassege(),dto.getImage(),dto.getTime());
    }
    public ArrayList<ChatDto> getChat(String userName) throws SQLException {
        String query="select * from chat where userName=? ORDER BY time";
        ResultSet resultSet = SqlUtil.execute(query,userName);
        ArrayList<ChatDto> chatDtos = new ArrayList<>();
        while (resultSet.next()){
            chatDtos.add(new ChatDto(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3),resultSet.getBinaryStream(4),resultSet.getString(5)));
        }
        return chatDtos;
    }
}
