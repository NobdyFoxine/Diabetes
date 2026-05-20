package com.antigravity.diabetes.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class BizHealthArticle {
    private Long id;
    private String title;
    private String category;
    private String summary;
    private String content;
    private String tags;
    private LocalDateTime createTime;
}
