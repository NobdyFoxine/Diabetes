package com.antigravity.diabetes.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI diabetesOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("糖尿病患者院外管理系统 API")
                        .description("本文档提供系统全部 RESTful API 接口说明，涵盖认证、患者档案、体征数据、预警引擎、随访管理、系统审计等模块。")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("耿可为 - 项目组")
                                .email("gengkewei@example.com")))
                .components(new Components()
                        .addSecuritySchemes("Bearer Token", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("在登录接口获取 Token 后，点击右上角 Authorize 按钮填入")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Token"));
    }
}
