package com.ruoyi.web.controller.h5;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.ServletUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.ip.IpUtils;
import com.ruoyi.system.domain.HzCommitment;
import com.ruoyi.system.domain.HzCommitmentTemplate;
import com.ruoyi.system.domain.HzProject;
import com.ruoyi.system.service.IHzCommitmentService;
import com.ruoyi.system.service.IHzCommitmentTemplateService;
import com.ruoyi.system.service.IHzProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * H5承诺书Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/app/commitment")
public class HzCommitmentAppController extends BaseController {

    @Autowired
    private IHzCommitmentTemplateService commitmentTemplateService;

    @Autowired
    private IHzCommitmentService commitmentService;

    @Autowired
    private IHzProjectService projectService;

    /**
     * 根据项目ID获取承诺书内容
     */
    @Anonymous
    @GetMapping("/content")
    public AjaxResult getCommitmentContent(@RequestParam("projectId") Long projectId) {
        // 1. 查询项目信息
        HzProject project = projectService.selectProjectById(projectId);
        if (project == null) {
            return error("项目不存在");
        }

        // 2. 根据项目类型获取对应的承诺书模板
        String commitmentType = project.getProjectType();
        HzCommitmentTemplate template = commitmentTemplateService.selectDefaultTemplateByType(commitmentType);

        if (template == null) {
            return error("未找到对应的承诺书模板");
        }

        // 3. 返回承诺书内容
        Map<String, Object> result = new HashMap<>();
        result.put("templateId", template.getTemplateId());
        result.put("commitmentType", template.getCommitmentType());
        result.put("commitmentContent", template.getTemplateContent());
        result.put("templateName", template.getTemplateName());
        result.put("projectName", project.getProjectName());

        return success(result);
    }

    /**
     * 根据模板编码获取承诺书内容（适用于无项目场景，如代购补贴）
     */
    @Anonymous
    @GetMapping("/templateByCode")
    public AjaxResult getTemplateByCode(@RequestParam("code") String code) {
        HzCommitmentTemplate template = commitmentTemplateService.selectTemplateByCode(code);
        if (template == null) {
            return error("未找到承诺书模板：" + code);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("templateId", template.getTemplateId());
        result.put("commitmentType", template.getCommitmentType());
        result.put("commitmentContent", template.getTemplateContent());
        result.put("templateName", template.getTemplateName());
        result.put("templateCode", template.getTemplateCode());
        return success(result);
    }

    /**
     * 提交签署的承诺书
     */
    @PostMapping("/sign")
    public AjaxResult signCommitment(@RequestBody Map<String, Object> params) {
        try {
            // 1. 获取参数（projectId 在代购补贴等无项目场景下可为空）
            Long projectId = params.get("projectId") != null && !"".equals(params.get("projectId").toString())
                    ? Long.valueOf(params.get("projectId").toString()) : null;
            Long tenantId = SecurityUtils.getHzUserId();
            String commitmentType = params.get("commitmentType").toString();
            String commitmentContent = params.get("commitmentContent").toString();
            String signatureData = params.get("signatureData").toString();

            // 2. 参数校验
            if (commitmentContent == null || commitmentContent.isEmpty()) {
                return error("承诺书内容不能为空");
            }
            if (signatureData == null || signatureData.isEmpty()) {
                return error("签名不能为空");
            }

            // 3. 创建承诺书记录
            HzCommitment commitment = new HzCommitment();
            commitment.setTenantId(tenantId);
            commitment.setProjectId(projectId);
            commitment.setCommitmentType(commitmentType);

            // 格式化日期为 MySQL datetime 格式和显示格式
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String signTime = sdf.format(new Date());

            // 格式化承诺日期为 yyyy年MM月dd日 格式用于显示
            SimpleDateFormat displaySdf = new SimpleDateFormat("yyyy年MM月dd日");
            String displayDate = displaySdf.format(new Date());

            // 只替换"承诺日期："后面的占位符，保留"承诺人签字："后面的占位符
            String processedContent = commitmentContent.replace("<strong>承诺日期：</strong>______________",
                                                                "<strong>承诺日期：</strong>" + displayDate);

            commitment.setCommitmentContent(processedContent);
            commitment.setSignatureImage(signatureData);
            commitment.setSignTime(signTime);
            commitment.setIpAddress(IpUtils.getIpAddr());
            commitment.setDeviceInfo(ServletUtils.getRequest().getHeader("User-Agent"));
            commitment.setStatus("0"); // 有效

            // 4. 保存到数据库
            int result = commitmentService.insertCommitment(commitment);

            if (result > 0) {
                Map<String, Object> data = new HashMap<>();
                data.put("commitmentId", commitment.getCommitmentId());
                data.put("message", "承诺书签署成功");
                return success(data);
            } else {
                return error("承诺书签署失败");
            }
        } catch (Exception e) {
            logger.error("签署承诺书失败", e);
            return error("签署承诺书失败：" + e.getMessage());
        }
    }

    /**
     * 获取小程序首次打开的友情提醒公告（模板编码固定为 gonggao）
     * - 免登录接口（通过 @Anonymous 单独放行）
     * - 未配置或已下架时返回 empty=true，前端不弹窗
     */
    @Anonymous
    @GetMapping("/notice")
    public AjaxResult getNotice() {
        HzCommitmentTemplate template = commitmentTemplateService.selectTemplateByCode("gonggao");
        Map<String, Object> result = new HashMap<>();
        if (template == null) {
            result.put("empty", true);
            return success(result);
        }
        result.put("empty", false);
        result.put("templateId", template.getTemplateId());
        result.put("templateName", template.getTemplateName());
        result.put("templateCode", template.getTemplateCode());
        result.put("content", template.getTemplateContent());
        result.put("version", template.getVersion());
        return success(result);
    }

    /**
     * 查询用户是否已签署过某类型承诺书
     */
    @GetMapping("/checkSigned")
    public AjaxResult checkSigned(@RequestParam("tenantId") Long tenantId,
                                   @RequestParam("commitmentType") String commitmentType) {
        tenantId = SecurityUtils.getHzUserId();
        HzCommitment commitment = commitmentService.selectCommitmentByTenantIdAndType(tenantId, commitmentType);

        Map<String, Object> result = new HashMap<>();
        result.put("signed", commitment != null);
        if (commitment != null) {
            result.put("signTime", commitment.getSignTime());
        }

        return success(result);
    }
}
