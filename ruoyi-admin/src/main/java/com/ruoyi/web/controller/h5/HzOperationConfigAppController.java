package com.ruoyi.web.controller.h5;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.HzOperationConfig;
import com.ruoyi.system.domain.SysNotice;
import com.ruoyi.system.service.IHzOperationConfigService;
import com.ruoyi.system.service.ISysNoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * H5运营配置Controller
 *
 * @author ruoyi
 */
@RestController
@RequestMapping("/h5/config")
public class HzOperationConfigAppController extends BaseController {

    @Autowired
    private IHzOperationConfigService configService;

    @Autowired
    private ISysNoticeService noticeService;

    /**
     * 获取轮播图列表
     */
    @GetMapping("/banners")
    public AjaxResult getBanners() {
        HzOperationConfig query = new HzOperationConfig();
        query.setConfigType("banner");
        query.setStatus("0"); // 只查询启用状态

        List<HzOperationConfig> list = configService.selectConfigList(query);
        return success(list);
    }

    /**
     * 获取通知公告列表
     */
    @GetMapping("/notices")
    public AjaxResult getNotices() {
        HzOperationConfig query = new HzOperationConfig();
        query.setConfigType("notice");
        query.setStatus("0"); // 只查询启用状态

        List<HzOperationConfig> list = configService.selectConfigList(query);
        return success(list);
    }

    /**
     * 获取最新通知内容（标题为"最新通知"的公告）
     */
    @GetMapping("/latestNotice")
    public AjaxResult getLatestNotice() {
        SysNotice query = new SysNotice();
        query.setNoticeTitle("最新通知");
        query.setStatus("0"); // 正常状态

        List<SysNotice> list = noticeService.selectNoticeList(query);
        if (list != null && !list.isEmpty()) {
            // 返回第一条记录的内容
            return success(list.get(0).getNoticeContent());
        }
        return success("");
    }

    /**
     * 获取功能图标列表
     */
    @GetMapping("/icons")
    public AjaxResult getIcons() {
        HzOperationConfig query = new HzOperationConfig();
        query.setConfigType("icon");
        query.setStatus("0"); // 只查询启用状态

        List<HzOperationConfig> list = configService.selectConfigList(query);
        return success(list);
    }
}
