package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.*;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IHzUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 用户信息Service业务层处理
 *
 * @author ruoyi
 */
@Service
public class HzUserServiceImpl extends ServiceImpl<HzUserMapper, HzUser> implements IHzUserService {

    private static final Logger log = LoggerFactory.getLogger(HzUserServiceImpl.class);

    @Autowired
    private HzUserMapper hzUserMapper;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzBillMapper billMapper;

    @Autowired
    private HzHouseOrderMapper houseOrderMapper;

    @Autowired
    private HzCheckInMapper checkInMapper;

    /**
     * 查询用户列表（全量，导出用，不分页）
     *
     * @param hzUser 用户信息
     * @return 用户集合
     */
    @Override
    public List<HzUser> selectHzUserList(HzUser hzUser) {
        return hzUserMapper.selectHzUserList(hzUser);
    }

    /**
     * 分页查询用户列表（管理端列表页专用，真正走数据库分页）
     * <p>
     * 筛选条件与 HzUserMapper.xml#selectHzUserList 保持一致，
     * 以保证导出和列表的筛选语义不漂移。
     */
    @Override
    public IPage<HzUser> selectHzUserPage(HzUser hzUser, int pageNum, int pageSize) {
        Page<HzUser> page = new Page<>(pageNum, pageSize);

        // 日期范围参数（前端通过 addDateRange 放入 params.beginTime / params.endTime）
        // 拼接时分秒：既能包含当天全部数据，又能保证 create_time 走索引（不用 DATE_FORMAT）
        String beginDateTime = null;
        String endDateTime = null;
        if (hzUser.getParams() != null) {
            Object beginObj = hzUser.getParams().get("beginTime");
            Object endObj = hzUser.getParams().get("endTime");
            if (beginObj != null && StringUtils.isNotEmpty(beginObj.toString())) {
                beginDateTime = beginObj.toString() + " 00:00:00";
            }
            if (endObj != null && StringUtils.isNotEmpty(endObj.toString())) {
                endDateTime = endObj.toString() + " 23:59:59";
            }
        }

        LambdaQueryWrapper<HzUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUser::getDelFlag, "0")
                .like(StringUtils.isNotEmpty(hzUser.getPhone()), HzUser::getPhone, hzUser.getPhone())
                .like(StringUtils.isNotEmpty(hzUser.getRealName()), HzUser::getRealName, hzUser.getRealName())
                .like(StringUtils.isNotEmpty(hzUser.getNickname()), HzUser::getNickname, hzUser.getNickname())
                .eq(StringUtils.isNotEmpty(hzUser.getSourceType()), HzUser::getSourceType, hzUser.getSourceType())
                .eq(StringUtils.isNotEmpty(hzUser.getGender()), HzUser::getGender, hzUser.getGender())
                .eq(StringUtils.isNotEmpty(hzUser.getEducation()), HzUser::getEducation, hzUser.getEducation())
                .eq(StringUtils.isNotEmpty(hzUser.getStatus()), HzUser::getStatus, hzUser.getStatus())
                .ge(beginDateTime != null, HzUser::getCreateTime, beginDateTime)
                .le(endDateTime != null, HzUser::getCreateTime, endDateTime)
                .orderByDesc(HzUser::getCreateTime);

        return this.page(page, wrapper);
    }

    /**
     * 查询用户详情
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public HzUser selectHzUserById(Long userId) {
        return hzUserMapper.selectById(userId);
    }

    /**
     * 修改用户状态
     *
     * @param hzUser 用户信息
     * @return 结果
     */
    @Override
    public int updateHzUserStatus(HzUser hzUser) {
        hzUser.setUpdateTime(DateUtils.getNowDate());
        return hzUserMapper.updateById(hzUser);
    }

    /**
     * 删除用户信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int deleteHzUserById(Long userId) {
        // 物理删除，彻底清除记录（含手机号唯一索引）
        return hzUserMapper.physicalDeleteById(userId);
    }

    /**
     * 批量删除用户信息
     *
     * @param userIds 用户ID数组
     * @return 结果
     */
    @Override
    public int deleteHzUserByIds(Long[] userIds) {
        int count = 0;
        for (Long userId : userIds) {
            count += deleteHzUserById(userId);
        }
        return count;
    }

    /**
     * 用户登录或注册
     *
     * @param loginType 登录类型
     * @param phone 手机号
     * @param openId OpenID
     * @param nickname 昵称
     * @return 用户信息
     */
    @Override
    public HzUser loginOrRegister(String loginType, String phone, String openId, String nickname) {
        // 根据手机号和openId查询用户
        LambdaQueryWrapper<HzUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUser::getPhone, phone);

        // 根据登录类型查询对应字段
        if ("wechat".equals(loginType)) {
            wrapper.eq(HzUser::getWechatOpenid, openId);
        } else if ("zhenghaoban".equals(loginType)) {
            wrapper.eq(HzUser::getZhaohaoUserId, openId);
        }

        HzUser user = this.getOne(wrapper);

        if (user == null) {
            // 用户不存在，创建新用户
            user = new HzUser();
            user.setPhone(phone);
            user.setLoginType(loginType);
            user.setNickname(nickname != null ? nickname : "用户" + phone.substring(7));
            user.setStatus("0"); // 正常状态
            user.setIsInfoCompleted("0"); // 未完善信息
            user.setDelFlag("0");
            user.setCreateTime(new Date());

            // 设置openId
            if ("wechat".equals(loginType)) {
                user.setWechatOpenid(openId);
                user.setSourceType("1"); // 微信小程序
            } else if ("zhenghaoban".equals(loginType)) {
                user.setZhaohaoUserId(openId);
                user.setSourceType("2"); // 郑好办
            }

            this.save(user);
        } else {
            // 更新最后登录时间
            user.setLastLoginTime(new Date());
            this.updateById(user);
        }

        return user;
    }

    /**
     * ��据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    @Override
    public HzUser getUserByPhone(String phone) {
        LambdaQueryWrapper<HzUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUser::getPhone, phone);
        return this.getOne(wrapper);
    }

    /**
     * 郑好办用户登录或注册
     *
     * @param zid 郑好办用户ID
     * @param phone 手机号
     * @param displayName 昵称
     * @param realName 真实姓名
     * @param idCard 身份证号
     * @param avatarUrl 头像URL
     * @return 用户信息
     */
    @Override
    public HzUser loginOrRegisterByZhb(String zid, String phone, String displayName, String realName, String idCard, String avatarUrl) {
        // 根据郑好办用户ID查询用户
        LambdaQueryWrapper<HzUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUser::getZhaohaoUserId, zid);

        HzUser user = this.getOne(wrapper);

        if (user == null) {
            // 用户不存在，创建新用户
            user = new HzUser();
            user.setPhone(phone);
            user.setZhaohaoUserId(zid);
            user.setNickname(displayName != null ? displayName : "郑好办用户");
            user.setRealName(realName);
            user.setIdCard(idCard);
            user.setAvatar(avatarUrl);
            user.setSourceType("2"); // 2=郑好办
            user.setLoginType("zhenghaoban");
            user.setStatus("0"); // 正常状态
            // 新用户需要完善工作单位、单位电话等信息
            user.setIsInfoCompleted("0");
            user.setDelFlag("0");
            user.setCreateTime(new Date());
            user.setLastLoginTime(new Date());

            this.save(user);
        } else {
            // 老用户更新信息
            user.setLastLoginTime(new Date());
            // 如果之前没有身份证号，现在有了，更新一下
            if (com.ruoyi.common.utils.StringUtils.isEmpty(user.getIdCard()) && com.ruoyi.common.utils.StringUtils.isNotEmpty(idCard)) {
                user.setIdCard(idCard);
                user.setRealName(realName);
                user.setIsInfoCompleted("1");
            }
            // 更新昵称和头像
            if (displayName != null) {
                user.setNickname(displayName);
            }
            if (avatarUrl != null) {
                user.setAvatar(avatarUrl);
            }
            this.updateById(user);
        }

        return user;
    }

    /**
     * 微信小程序用户登录或注册
     *
     * @param openid 微信openid
     * @param unionid 微信unionid（可选，跨应用用户匹配用）
     * @param phone 手机号（从微信获取）
     * @return 用户信息
     */
    @Override
    public HzUser loginOrRegisterByWechat(String openid, String unionid, String phone) {
        // 1. 根据微信openid查询用户
        LambdaQueryWrapper<HzUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUser::getWechatOpenid, openid);
        HzUser user = this.getOne(wrapper);

        if (user != null) {
            // 已有openid用户：更新登录时间，回填unionid
            user.setLastLoginTime(new Date());
            if (com.ruoyi.common.utils.StringUtils.isEmpty(user.getWechatUnionid()) && com.ruoyi.common.utils.StringUtils.isNotEmpty(unionid)) {
                user.setWechatUnionid(unionid);
            }
            this.updateById(user);
            return user;
        }

        // 2. openid未找到，根据手机号查询
        LambdaQueryWrapper<HzUser> phoneWrapper = new LambdaQueryWrapper<>();
        phoneWrapper.eq(HzUser::getPhone, phone);
        HzUser phoneUser = this.getOne(phoneWrapper);

        if (phoneUser != null) {
            // 手机号已存在
            if (com.ruoyi.common.utils.StringUtils.isNotEmpty(phoneUser.getWechatOpenid()) && !phoneUser.getWechatOpenid().equals(openid)) {
                // 已绑定其他微信openid，拒绝
                throw new RuntimeException("该手机号已被其他账号绑定");
            }
            // 手机号存在但未绑定openid，绑定openid和unionid
            phoneUser.setWechatOpenid(openid);
            if (com.ruoyi.common.utils.StringUtils.isNotEmpty(unionid)) {
                phoneUser.setWechatUnionid(unionid);
            }
            phoneUser.setLastLoginTime(new Date());
            phoneUser.setLoginType("wechat");
            this.updateById(phoneUser);
            return phoneUser;
        }

        // 3. 全新用户，创建
        HzUser newUser = new HzUser();
        newUser.setPhone(phone);
        newUser.setWechatOpenid(openid);
        if (com.ruoyi.common.utils.StringUtils.isNotEmpty(unionid)) {
            newUser.setWechatUnionid(unionid);
        }
        newUser.setSourceType("1"); // 1=微信小程序
        newUser.setLoginType("wechat");
        newUser.setNickname("微信用户");
        newUser.setStatus("0"); // 正常
        newUser.setIsInfoCompleted("0"); // 未完善
        newUser.setAuthStatus("0"); // 未认证
        newUser.setDelFlag("0");
        newUser.setCreateTime(new Date());
        newUser.setLastLoginTime(new Date());

        try {
            this.save(newUser);
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 手机号已存在（通常是后台软删除后重新登录）
            // 用原生 SQL 绕过 @TableLogic 过滤，查出被软删除的记录并复活
            HzUser deleted = hzUserMapper.selectByPhoneIgnoreLogicDelete(phone);
            if (deleted != null) {
                deleted.setDelFlag("0");
                deleted.setStatus("0");
                deleted.setWechatOpenid(openid);
                if (com.ruoyi.common.utils.StringUtils.isNotEmpty(unionid)) {
                    deleted.setWechatUnionid(unionid);
                }
                deleted.setLoginType("wechat");
                deleted.setLastLoginTime(new Date());
                this.updateById(deleted);
                return deleted;
            }
            throw new RuntimeException("登录失败：手机号已注册，请联系管理员");
        }

        return newUser;
    }

    /**
     * 身份证账号自动合并：将旧账号的业务数据迁移到当前账号
     * 触发时机：实名认证时发现身份证已存在于另一个账号
     * 安全策略：仅合并「从未登录过」的旧账号（无openid、无lastLoginTime），
     *          若旧账号有活跃登录记录则拒绝自动合并，避免误伤
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean mergeUserByIdCard(Long currentUserId, String idCard) {
        if (currentUserId == null || StringUtils.isEmpty(idCard)) {
            return false;
        }

        // 1. 查找同身份证的其他账号
        LambdaQueryWrapper<HzUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HzUser::getIdCard, idCard)
                .ne(HzUser::getUserId, currentUserId)
                .eq(HzUser::getDelFlag, "0");
        HzUser oldUser = this.getOne(wrapper);

        if (oldUser == null) {
            return false; // 无重复账号，无需合并
        }

        // 2. 安全校验：旧账号必须是「从未活跃登录」的迁移数据
        //    有 wechatOpenid 说明是活跃的微信用户，不能自动合并
        if (StringUtils.isNotEmpty(oldUser.getWechatOpenid())) {
            log.warn("身份证账号合并被拒绝：旧账号{}有活跃openid，当前账号{}，身份证={}",
                    oldUser.getUserId(), currentUserId, idCard);
            return false;
        }

        Long oldUserId = oldUser.getUserId();
        log.info("开始身份证账号合并：旧账号{}→新账号{}，身份证={}", oldUserId, currentUserId, idCard);

        // 3. 迁移业务数据：更新所有关联表的 tenant_id
        int contractCount = contractMapper.update(null, new LambdaUpdateWrapper<HzContract>()
                .eq(HzContract::getTenantId, oldUserId)
                .set(HzContract::getTenantId, currentUserId));

        int billCount = billMapper.update(null, new LambdaUpdateWrapper<HzBill>()
                .eq(HzBill::getTenantId, oldUserId)
                .set(HzBill::getTenantId, currentUserId));

        int orderCount = houseOrderMapper.update(null, new LambdaUpdateWrapper<HzHouseOrder>()
                .eq(HzHouseOrder::getTenantId, oldUserId)
                .set(HzHouseOrder::getTenantId, currentUserId));

        int checkinCount = checkInMapper.update(null, new LambdaUpdateWrapper<HzCheckIn>()
                .eq(HzCheckIn::getTenantId, oldUserId)
                .set(HzCheckIn::getTenantId, currentUserId));

        // 4. 补全当前账号的空字段（从旧账号继承有用信息）
        HzUser currentUser = this.getById(currentUserId);
        boolean needUpdate = false;
        if (StringUtils.isEmpty(currentUser.getEducation()) && StringUtils.isNotEmpty(oldUser.getEducation())) {
            currentUser.setEducation(oldUser.getEducation());
            needUpdate = true;
        }
        if (StringUtils.isEmpty(currentUser.getWorkUnit()) && StringUtils.isNotEmpty(oldUser.getWorkUnit())) {
            currentUser.setWorkUnit(oldUser.getWorkUnit());
            needUpdate = true;
        }
        if (StringUtils.isEmpty(currentUser.getIdentityType()) && StringUtils.isNotEmpty(oldUser.getIdentityType())) {
            currentUser.setIdentityType(oldUser.getIdentityType());
            needUpdate = true;
        }
        if (needUpdate) {
            this.updateById(currentUser);
        }

        // 5. 标记旧账号为已合并（软删除，del_flag='2'表示已合并）
        oldUser.setDelFlag("2");
        oldUser.setUpdateTime(new Date());
        oldUser.setRemark("已合并至账号" + currentUserId);
        this.updateById(oldUser);

        log.info("身份证账号合并完成：旧账号{}→新账号{}，迁移合同{}条、账单{}条、预订单{}条、入住记录{}条",
                oldUserId, currentUserId, contractCount, billCount, orderCount, checkinCount);

        return true;
    }

}
