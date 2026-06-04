package com.ruoyi.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.system.domain.HzUser;

import java.util.List;

/**
 * 用户信息Service接口
 *
 * @author ruoyi
 */
public interface IHzUserService extends IService<HzUser> {

    /**
     * 查询用户列表（全量，导出用）
     *
     * @param hzUser 用户信息
     * @return 用户集合
     */
    List<HzUser> selectHzUserList(HzUser hzUser);

    /**
     * 分页查询用户列表（管理端列表页专用，真正走数据库分页）
     *
     * @param hzUser   查询条件
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    IPage<HzUser> selectHzUserPage(HzUser hzUser, int pageNum, int pageSize);

    /**
     * 查询用户详情
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    HzUser selectHzUserById(Long userId);

    /**
     * 修改用户状态
     *
     * @param hzUser 用户信息
     * @return 结果
     */
    int updateHzUserStatus(HzUser hzUser);

    /**
     * 删除用户信息
     *
     * @param userId 用户ID
     * @return 结果
     */
    int deleteHzUserById(Long userId);

    /**
     * 批量删除用户信息
     *
     * @param userIds 用户ID数组
     * @return 结果
     */
    int deleteHzUserByIds(Long[] userIds);

    /**
     * 用户登录或注册
     *
     * @param loginType 登录类型
     * @param phone 手机号
     * @param openId OpenID
     * @param nickname 昵称
     * @return 用户信息
     */
    HzUser loginOrRegister(String loginType, String phone, String openId, String nickname);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    HzUser getUserByPhone(String phone);

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
    HzUser loginOrRegisterByZhb(String zid, String phone, String displayName, String realName, String idCard, String avatarUrl);

    /**
     * 微信小程序用户登录或注册
     *
     * @param openid 微信openid
     * @param unionid 微信unionid（可选，跨应用用户匹配用）
     * @param phone 手机号（从微信获取）
     * @return 用户信息
     */
    HzUser loginOrRegisterByWechat(String openid, String unionid, String phone);

    /**
     * 身份证账号自动合并：当用户实名认证时发现身份证已存在于其他账号，
     * 自动将旧账号的业务数据迁移到当前账号
     *
     * @param currentUserId 当前登录的用户ID
     * @param idCard 身份证号
     * @return true=发生了合并, false=无需合并
     */
    boolean mergeUserByIdCard(Long currentUserId, String idCard);

}
