package com.ruoyi.gangzhu.policy.domain;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 政策文件对象 hz_policy
 *
 * @author ruoyi
 */
@TableName("hz_policy")
public class HzPolicy extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 政策ID */
    @TableId(value = "policy_id", type = IdType.AUTO)
    private Long policyId;

    /** 政策标题 */
    private String policyTitle;

    /** 封面图片 */
    private String coverImage;

    /** 政策文号 */
    private String policyNo;

    /** 政策类型 */
    private String policyType;

    /** 政策内容（富文本） */
    private String policyContent;

    /** 附件路径（JSON格式） */
    private String policyFile;

    /** 发布部门 */
    private String publishDept;

    /** 发布日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date publishDate;

    /** 生效日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date effectiveDate;

    /** 浏览次数 */
    private Integer viewCount;

    /** 状态（0正常 1停用） */
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    @TableLogic
    private String delFlag;

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

    public String getPolicyTitle() {
        return policyTitle;
    }

    public void setPolicyTitle(String policyTitle) {
        this.policyTitle = policyTitle;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getPolicyType() {
        return policyType;
    }

    public void setPolicyType(String policyType) {
        this.policyType = policyType;
    }

    public String getPolicyContent() {
        return policyContent;
    }

    public void setPolicyContent(String policyContent) {
        this.policyContent = policyContent;
    }

    public String getPolicyFile() {
        return policyFile;
    }

    public void setPolicyFile(String policyFile) {
        this.policyFile = policyFile;
    }

    public String getPublishDept() {
        return publishDept;
    }

    public void setPublishDept(String publishDept) {
        this.publishDept = publishDept;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return "HzPolicy{" +
                "policyId=" + policyId +
                ", policyTitle='" + policyTitle + '\'' +
                ", coverImage='" + coverImage + '\'' +
                ", policyNo='" + policyNo + '\'' +
                ", policyType='" + policyType + '\'' +
                ", policyContent='" + policyContent + '\'' +
                ", policyFile='" + policyFile + '\'' +
                ", publishDept='" + publishDept + '\'' +
                ", publishDate=" + publishDate +
                ", effectiveDate=" + effectiveDate +
                ", viewCount=" + viewCount +
                ", status='" + status + '\'' +
                ", delFlag='" + delFlag + '\'' +
                '}';
    }
}
