package com.eden.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

/**
 * 文章实体类，映射到数据库的posts表
 */
@Entity(name = "PostModel") // 确保实体名称唯一
@Table(name = "posts")
public class Post {

    // 主键，自增
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 用于指定主键字段的生成策略
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    // 文章标题，不能为空
    @Column(nullable = false, name = "title", columnDefinition = "VARCHAR(255) COMMENT '記事タイトル'")
    private String title;

    // 文章内容，不能为空
    @Column(nullable = false, columnDefinition = "TEXT COMMENT '記事内容'")
    private String content;

    // 作者ID，外键引用users表的id
    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false, columnDefinition = "INT COMMENT '著者ID、外部キー'")
    private User author;

    // 创建时间，自动生成
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '作成日時'")
    private LocalDateTime createdAt = LocalDateTime.now();

    // 更新时间，自动更新
    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時'")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // 用于显示格式化的创建时间
    private transient String formattedCreatedAt;

    public String getFormattedCreatedAt() {
        return formattedCreatedAt;
    }

    public void setFormattedCreatedAt(String formattedCreatedAt) {
        this.formattedCreatedAt = formattedCreatedAt;
    }

    // Getters和Setters省略...

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
