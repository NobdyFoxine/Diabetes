package com.antigravity.diabetes.mapper;

import com.antigravity.diabetes.entity.BizMessage;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface MessageMapper {

    @Insert("INSERT INTO biz_message (sender_id, receiver_id, content, create_time) " +
            "VALUES (#{senderId}, #{receiverId}, #{content}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BizMessage msg);

    @Select("SELECT * FROM biz_message WHERE (sender_id = #{userId1} AND receiver_id = #{userId2}) " +
            "OR (sender_id = #{userId2} AND receiver_id = #{userId1}) ORDER BY create_time ASC LIMIT 50")
    List<BizMessage> selectConversation(Long userId1, Long userId2);
}
