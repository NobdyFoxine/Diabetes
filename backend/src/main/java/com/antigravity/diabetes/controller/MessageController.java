package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.entity.BizMessage;
import com.antigravity.diabetes.mapper.MessageMapper;
import com.antigravity.diabetes.vo.CommonResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/message")
@Tag(name = "在线问询", description = "医患之间的在线消息问询")
public class MessageController {

    @Autowired
    private MessageMapper messageMapper;

    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "向指定用户发送一条问询消息")
    public CommonResult<?> send(@RequestBody BizMessage msg, HttpServletRequest request) {
        Long senderId = (Long) request.getAttribute("userId");
        msg.setSenderId(senderId);
        msg.setCreateTime(LocalDateTime.now());
        messageMapper.insert(msg);
        return CommonResult.success("消息已发送");
    }

    @GetMapping("/chat/{otherUserId}")
    @Operation(summary = "获取聊天记录", description = "获取与指定用户之间的双向消息记录")
    public CommonResult<List<BizMessage>> getConversation(@PathVariable Long otherUserId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        return CommonResult.success(messageMapper.selectConversation(userId, otherUserId));
    }
}
