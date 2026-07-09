<template>
  <div class="app-container">
    <el-card>
      <div slot="header">
        <span>更换手机号</span>
        <span style="margin-left: 10px; color: #999; font-size: 12px;">
          用于用户更换手机号后，将旧账号数据迁移到新手机号
        </span>
      </div>
      <el-form :model="form" :rules="rules" ref="form" label-width="120px">
        <el-form-item label="原手机号" prop="oldPhone">
          <el-input v-model="form.oldPhone" placeholder="请输入用户原手机号" maxlength="11" style="width: 300px" />
        </el-form-item>
        <el-form-item label="新手机号" prop="newPhone">
          <el-input v-model="form.newPhone" placeholder="请输入用户新手机号" maxlength="11" style="width: 300px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handlePreview" :loading="previewLoading">查询预览</el-button>
          <el-button icon="el-icon-refresh" @click="resetForm">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 预览结果 -->
    <el-card v-if="previewData" style="margin-top: 16px">
      <div slot="header">
        <span>预览结果</span>
      </div>

      <!-- 旧账号信息 -->
      <div v-if="previewData.oldAccount">
        <h4 style="margin: 0 0 12px;">原账号信息（保留）</h4>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="用户ID">{{ previewData.oldAccount.userId }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ previewData.oldAccount.realName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ previewData.oldAccount.phone }}</el-descriptions-item>
          <el-descriptions-item label="身份证">{{ previewData.oldAccount.idCard }}</el-descriptions-item>
          <el-descriptions-item label="合同数">{{ previewData.oldAccount.contractCount }}</el-descriptions-item>
          <el-descriptions-item label="账单数">{{ previewData.oldAccount.billCount }}</el-descriptions-item>
          <el-descriptions-item label="微信绑定">{{ previewData.oldAccount.hasWechat ? '是' : '否' }}</el-descriptions-item>
          <el-descriptions-item label="e签宝">{{ previewData.oldAccount.hasEsign ? '是' : '否' }}</el-descriptions-item>
          <el-descriptions-item label="实名认证">
            <el-tag size="mini" :type="previewData.oldAccount.authStatus === '2' ? 'success' : 'info'">
              {{ previewData.oldAccount.authStatus === '2' ? '已认证' : '未认证' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 新账号信息 -->
      <div v-if="previewData.newAccount" style="margin-top: 20px;">
        <h4 style="margin: 0 0 12px;">新账号信息（将被删除合并）</h4>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="用户ID">{{ previewData.newAccount.userId }}</el-descriptions-item>
          <el-descriptions-item label="姓名">{{ previewData.newAccount.realName }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ previewData.newAccount.phone }}</el-descriptions-item>
          <el-descriptions-item label="身份证">{{ previewData.newAccount.idCard }}</el-descriptions-item>
          <el-descriptions-item label="合同数">{{ previewData.newAccount.contractCount }}</el-descriptions-item>
          <el-descriptions-item label="账单数">{{ previewData.newAccount.billCount }}</el-descriptions-item>
          <el-descriptions-item label="微信绑定">{{ previewData.newAccount.hasWechat ? '是' : '否' }}</el-descriptions-item>
          <el-descriptions-item label="e签宝">{{ previewData.newAccount.hasEsign ? '是' : '否' }}</el-descriptions-item>
          <el-descriptions-item label="实名认证">
            <el-tag size="mini" :type="previewData.newAccount.authStatus === '2' ? 'success' : 'info'">
              {{ previewData.newAccount.authStatus === '2' ? '已认证' : '未认证' }}
            </el-tag>
          </el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 操作提示 -->
      <el-alert
        :title="previewData.message"
        :type="previewData.canProceed ? 'info' : 'error'"
        style="margin-top: 20px"
        :closable="false"
        show-icon
      />

      <!-- 执行步骤 -->
      <div v-if="previewData.steps" style="margin-top: 16px;">
        <h4 style="margin: 0 0 12px;">执行步骤</h4>
        <div v-for="(step, index) in previewData.steps" :key="index" style="margin: 8px 0; font-size: 14px;">
          <el-tag size="small" type="info" style="margin-right: 8px;">{{ index + 1 }}</el-tag>
          {{ step }}
        </div>
      </div>

      <!-- 确认按钮 -->
      <div v-if="previewData.canProceed" style="margin-top: 24px; text-align: center;">
        <el-button type="danger" icon="el-icon-check" @click="handleExecute" :loading="executeLoading">
          确认更换手机号
        </el-button>
      </div>
    </el-card>

    <!-- 执行结果 -->
    <el-card v-if="executeData" style="margin-top: 16px">
      <div slot="header">
        <span>执行结果</span>
      </div>
      <el-alert title="更换成功" type="success" :closable="false" show-icon style="margin-bottom: 16px;" />
      <div style="background: #f5f7fa; padding: 16px; border-radius: 4px;">
        <div v-for="(log, index) in executeData.log" :key="index" style="margin: 6px 0; font-size: 14px; line-height: 1.8;">
          <i class="el-icon-success" style="color: #67c23a; margin-right: 6px;"></i>
          {{ log }}
        </div>
      </div>
      <div style="margin-top: 16px; text-align: center;">
        <el-button type="primary" @click="resetAll">完成</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import { previewChange, executeChange } from '@/api/gangzhu/phoneChange'

export default {
  name: 'PhoneChange',
  data() {
    const validatePhone = (rule, value, callback) => {
      if (!value) {
        callback(new Error('请输入手机号'))
      } else if (!/^1[3-9]\d{9}$/.test(value)) {
        callback(new Error('请输入正确的手机号'))
      } else {
        callback()
      }
    }
    return {
      form: {
        oldPhone: '',
        newPhone: ''
      },
      rules: {
        oldPhone: [{ required: true, validator: validatePhone, trigger: 'blur' }],
        newPhone: [{ required: true, validator: validatePhone, trigger: 'blur' }]
      },
      previewLoading: false,
      executeLoading: false,
      previewData: null,
      executeData: null
    }
  },
  methods: {
    handlePreview() {
      this.$refs.form.validate(valid => {
        if (!valid) return
        if (this.form.oldPhone === this.form.newPhone) {
          this.$message.warning('原手机号和新手机号不能相同')
          return
        }
        this.previewLoading = true
        this.previewData = null
        this.executeData = null
        previewChange(this.form.oldPhone, this.form.newPhone).then(res => {
          this.previewData = res.data
        }).catch(() => {
          // 请求失败
        }).finally(() => {
          this.previewLoading = false
        })
      })
    },
    handleExecute() {
      this.$confirm('确认要执行更换手机号操作吗？此操作不可逆！', '警告', {
        confirmButtonText: '确认更换',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.executeLoading = true
        executeChange({ oldPhone: this.form.oldPhone, newPhone: this.form.newPhone }).then(res => {
          this.executeData = res.data
          this.$message.success(res.data.message || '更换成功')
        }).catch(() => {
          // 请求失败
        }).finally(() => {
          this.executeLoading = false
        })
      }).catch(() => {})
    },
    resetForm() {
      this.$refs.form.resetFields()
    },
    resetAll() {
      this.$refs.form.resetFields()
      this.previewData = null
      this.executeData = null
    }
  }
}
</script>
