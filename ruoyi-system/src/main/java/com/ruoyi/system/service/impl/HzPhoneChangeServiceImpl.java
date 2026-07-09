package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzContractMapper;
import com.ruoyi.system.mapper.HzUserMapper;
import com.ruoyi.system.service.IHzPhoneChangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 更换手机号Service实现
 *
 * @author ruoyi
 */
@Service
public class HzPhoneChangeServiceImpl implements IHzPhoneChangeService {

    @Autowired
    private HzUserMapper userMapper;

    @Autowired
    private HzContractMapper contractMapper;

    @Autowired
    private HzBillMapper billMapper;

    @Override
    public Map<String, Object> previewChange(String oldPhone, String newPhone) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> steps = new ArrayList<>();

        // 校验
        if (oldPhone.equals(newPhone)) {
            result.put("canProceed", false);
            result.put("message", "原手机号和新手机号不能相同");
            return result;
        }

        // 1. 查找旧账号
        HzUser oldUser = userMapper.selectOne(new LambdaQueryWrapper<HzUser>()
                .eq(HzUser::getPhone, oldPhone));
        if (oldUser == null) {
            result.put("canProceed", false);
            result.put("message", "未找到手机号 " + oldPhone + " 对应的用户");
            return result;
        }

        // 2. 统计旧账号业务数据
        long oldContracts = contractMapper.selectCount(new LambdaQueryWrapper<HzContract>()
                .eq(HzContract::getTenantId, oldUser.getUserId())
                .eq(HzContract::getDelFlag, "0"));
        long oldBills = billMapper.selectCount(new LambdaQueryWrapper<HzBill>()
                .eq(HzBill::getTenantId, oldUser.getUserId())
                .eq(HzBill::getDelFlag, "0"));

        Map<String, Object> oldAccount = new LinkedHashMap<>();
        oldAccount.put("userId", oldUser.getUserId());
        oldAccount.put("phone", oldUser.getPhone());
        oldAccount.put("realName", oldUser.getRealName());
        oldAccount.put("idCard", oldUser.getIdCard());
        oldAccount.put("hasWechat", StringUtils.isNotEmpty(oldUser.getWechatOpenid()));
        oldAccount.put("hasEsign", StringUtils.isNotEmpty(oldUser.getEsignPsnId()));
        oldAccount.put("authStatus", oldUser.getAuthStatus());
        oldAccount.put("contractCount", oldContracts);
        oldAccount.put("billCount", oldBills);
        result.put("oldAccount", oldAccount);

        // 3. 查找新账号
        HzUser newUser = userMapper.selectOne(new LambdaQueryWrapper<HzUser>()
                .eq(HzUser::getPhone, newPhone));

        if (newUser != null) {
            // 新账号存在，需要合并
            long newContracts = contractMapper.selectCount(new LambdaQueryWrapper<HzContract>()
                    .eq(HzContract::getTenantId, newUser.getUserId())
                    .eq(HzContract::getDelFlag, "0"));
            long newBills = billMapper.selectCount(new LambdaQueryWrapper<HzBill>()
                    .eq(HzBill::getTenantId, newUser.getUserId())
                    .eq(HzBill::getDelFlag, "0"));

            Map<String, Object> newAccount = new LinkedHashMap<>();
            newAccount.put("userId", newUser.getUserId());
            newAccount.put("phone", newUser.getPhone());
            newAccount.put("realName", newUser.getRealName());
            newAccount.put("idCard", newUser.getIdCard());
            newAccount.put("hasWechat", StringUtils.isNotEmpty(newUser.getWechatOpenid()));
            newAccount.put("hasEsign", StringUtils.isNotEmpty(newUser.getEsignPsnId()));
            newAccount.put("authStatus", newUser.getAuthStatus());
            newAccount.put("contractCount", newContracts);
            newAccount.put("billCount", newBills);
            result.put("newAccount", newAccount);

            if (newContracts > 0 || newBills > 0) {
                result.put("canProceed", false);
                result.put("action", "blocked");
                result.put("message", "新手机号账号存在业务数据（合同" + newContracts + "份，账单" + newBills + "条），无法自动合并，需人工处理");
            } else {
                result.put("canProceed", true);
                result.put("action", "merge");
                result.put("message", "将执行账号合并：迁移微信/e签宝/认证数据 → 物理删除新账号 → 更新旧账号手机号");
                steps.add("将新账号的微信openid、e签宝ID、实名认证状态迁移到旧账号（仅在旧账号缺失时填充）");
                steps.add("物理删除新账号（user_id=" + newUser.getUserId() + "），释放手机号");
                steps.add("更新旧账号手机号：" + oldPhone + " → " + newPhone);
                result.put("steps", steps);
            }
        } else {
            result.put("canProceed", true);
            result.put("action", "update");
            result.put("message", "新手机号尚未注册，直接更新旧账号手机号即可");
            steps.add("更新旧账号手机号：" + oldPhone + " → " + newPhone);
            result.put("steps", steps);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> executeChange(String oldPhone, String newPhone) {
        Map<String, Object> result = new LinkedHashMap<>();
        List<String> log = new ArrayList<>();

        // 校验
        if (oldPhone.equals(newPhone)) {
            throw new RuntimeException("原手机号和新手机号不能相同");
        }

        // 1. 查找旧账号
        HzUser oldUser = userMapper.selectOne(new LambdaQueryWrapper<HzUser>()
                .eq(HzUser::getPhone, oldPhone));
        if (oldUser == null) {
            throw new RuntimeException("未找到手机号 " + oldPhone + " 对应的用户");
        }
        log.add("找到旧账号：user_id=" + oldUser.getUserId() + "，" + oldUser.getRealName() + "，" + oldUser.getPhone());

        // 2. 查找新账号
        HzUser newUser = userMapper.selectOne(new LambdaQueryWrapper<HzUser>()
                .eq(HzUser::getPhone, newPhone));

        if (newUser != null) {
            // 检查新账号业务数据
            long newContracts = contractMapper.selectCount(new LambdaQueryWrapper<HzContract>()
                    .eq(HzContract::getTenantId, newUser.getUserId())
                    .eq(HzContract::getDelFlag, "0"));
            long newBills = billMapper.selectCount(new LambdaQueryWrapper<HzBill>()
                    .eq(HzBill::getTenantId, newUser.getUserId())
                    .eq(HzBill::getDelFlag, "0"));

            if (newContracts > 0 || newBills > 0) {
                throw new RuntimeException("新手机号账号存在业务数据（合同" + newContracts + "份，账单" + newBills + "条），无法自动合并");
            }

            log.add("找到新账号：user_id=" + newUser.getUserId() + "，" + newUser.getRealName() + "，" + newUser.getPhone() + "（无业务数据，可安全删除）");

            // 3. 迁移微信/e签宝/认证数据（仅在旧账号缺失时填充）
            if (StringUtils.isEmpty(oldUser.getWechatOpenid()) && StringUtils.isNotEmpty(newUser.getWechatOpenid())) {
                oldUser.setWechatOpenid(newUser.getWechatOpenid());
                log.add("迁移微信openid：" + newUser.getWechatOpenid());
            }
            if (StringUtils.isEmpty(oldUser.getWechatUnionid()) && StringUtils.isNotEmpty(newUser.getWechatUnionid())) {
                oldUser.setWechatUnionid(newUser.getWechatUnionid());
                log.add("迁移微信unionid");
            }
            if (StringUtils.isEmpty(oldUser.getZhaohaoUserId()) && StringUtils.isNotEmpty(newUser.getZhaohaoUserId())) {
                oldUser.setZhaohaoUserId(newUser.getZhaohaoUserId());
                log.add("迁移郑好办用户ID：" + newUser.getZhaohaoUserId());
            }
            if (StringUtils.isEmpty(oldUser.getEsignPsnId()) && StringUtils.isNotEmpty(newUser.getEsignPsnId())) {
                oldUser.setEsignPsnId(newUser.getEsignPsnId());
                log.add("迁移e签宝ID：" + newUser.getEsignPsnId());
            }
            if (!"2".equals(oldUser.getAuthStatus()) && "2".equals(newUser.getAuthStatus())) {
                oldUser.setAuthStatus(newUser.getAuthStatus());
                oldUser.setAuthTime(newUser.getAuthTime());
                log.add("迁移实名认证状态：已认证");
            }

            // 4. 物理删除新账号（释放手机号）
            userMapper.physicalDeleteById(newUser.getUserId());
            log.add("物理删除新账号（user_id=" + newUser.getUserId() + "）");
        } else {
            log.add("新手机号 " + newPhone + " 未注册，无需合并");
        }

        // 5. 更新旧账号手机号
        oldUser.setPhone(newPhone);
        oldUser.setUpdateTime(new Date());
        userMapper.updateById(oldUser);
        log.add("更新旧账号手机号：" + oldPhone + " → " + newPhone);

        result.put("success", true);
        result.put("log", log);
        result.put("oldUserId", oldUser.getUserId());
        result.put("newPhone", newPhone);
        result.put("message", "更换成功！用户 " + oldUser.getRealName() + " 的手机号已从 " + oldPhone + " 更换为 " + newPhone);

        return result;
    }
}
