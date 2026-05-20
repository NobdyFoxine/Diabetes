package com.antigravity.diabetes.controller;

import com.antigravity.diabetes.vo.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/ai")
public class LlmController {

    @Value("${llm.api.url}")
    private String apiUrl;

    @Value("${llm.api.key}")
    private String apiKey;

    @Value("${llm.api.models}")
    private String modelsConfig;

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping("/chat")
    public CommonResult<Map<String, String>> chat(@RequestBody Map<String, Object> body) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> messages = (List<Map<String, String>>) body.get("messages");

            // 构建带系统提示词的消息列表
            List<Map<String, Object>> apiMessages = new ArrayList<>();
            Map<String, Object> sysMsg = new HashMap<>();
            sysMsg.put("role", "system");
            sysMsg.put("content", "你是糖尿病患者院外管理系统的AI健康助手。请用专业但易懂的中文回答用户关于糖尿病管理的问题，" +
                "包括血糖监测、饮食营养、运动康复、用药指导、并发症预防、血压管理等方面。" +
                "回答应简洁、结构化，不超过300字。如遇紧急医疗问题，提醒用户立即联系医生。");
            apiMessages.add(sysMsg);

            for (Map<String, String> m : messages) {
                Map<String, Object> msg = new HashMap<>();
                String role = m.get("role");
                if ("bot".equals(role) || "assistant".equals(role)) role = "assistant";
                else if ("user".equals(role)) role = "user";
                else role = "user";
                msg.put("role", role);
                msg.put("content", m.get("content"));
                apiMessages.add(msg);
            }

            // 从配置中解析模型列表，依次尝试每个模型
            String[] models = modelsConfig.split(",");
            for (String modelName : models) {
                modelName = modelName.trim();
                log.info("【AI】尝试调用模型: {}", modelName);
                String result = callModel(modelName, apiMessages);
                if (result != null) {
                    log.info("【AI】模型 {} 调用成功", modelName);
                    Map<String, String> data = new HashMap<>();
                    data.put("reply", result);
                    return CommonResult.success(data);
                }
                log.warn("【AI】模型 {} 调用失败，尝试下一个...", modelName);
            }

            // 所有模型都失败，等待15秒后用第一个模型重试一次（等限速解除）
            log.warn("【AI】所有模型首轮失败，等待15秒后重试主模型...");
            try { Thread.sleep(15000); } catch (InterruptedException ignored) {}
            String retryResult = callModel(models[0].trim(), apiMessages);
            if (retryResult != null) {
                Map<String, String> data = new HashMap<>();
                data.put("reply", retryResult);
                return CommonResult.success(data);
            }

            return CommonResult.error("AI 服务暂时繁忙，请稍后再试");
        } catch (Exception e) {
            log.error("【AI】服务调用异常: {}", e.getMessage());
            return CommonResult.error("AI 服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 调用指定模型，成功返回回复内容，失败返回 null
     */
    private String callModel(String modelName, List<Map<String, Object>> apiMessages) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("messages", apiMessages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 4096);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);
            headers.set("HTTP-Referer", "http://localhost:5173");
            headers.set("X-Title", "DiabetesHealthSystem");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                apiUrl + "/chat/completions", HttpMethod.POST, request,
                new ParameterizedTypeReference<Map<String, Object>>() {});

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            return null;
        } catch (Exception e) {
            log.warn("【AI】模型 {} 异常: {}", modelName, e.getMessage());
            return null;
        }
    }
}
