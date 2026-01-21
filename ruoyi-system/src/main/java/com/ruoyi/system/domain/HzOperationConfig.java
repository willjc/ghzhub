package com.ruoyi.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 运营配置对象 hz_operation_config
 *
 * @author ruoyi
 * @date 2026-01-12
 */
@TableName("hz_operation_config")
public class HzOperationConfig extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 配置ID */
    @TableId(type = IdType.AUTO)
    private Long configId;

    /** 配置类型（banner=轮播图 ad=广告位 notice=通知公告 icon=功能图标） */
    private String configType;

    /** 标题/名称 */
    private String title;

    /** 图片地址（相对路径） */
    private String imageUrl;

    /** 文字描述/内容 */
    private String content;

    /** 跳转链接 */
    private String linkUrl;

    /** 链接类型（page=页面路径 url=外部链接 none=无链接） */
    private String linkType;

    /** 排序号（数字越小越靠前） */
    private Integer sortOrder;

    /** 状态（0=启用 1=禁用） */
    private String status;

    public Long getConfigId() {
        return configId;
    }

    public void setConfigId(Long configId) {
        this.configId = configId;
    }

    public String getConfigType() {
        return configType;
    }

    public void setConfigType(String configType) {
        this.configType = configType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
