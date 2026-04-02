<template>
  <view class="container">
    <!-- 加载中 -->
    <view v-if="loading" class="loading-wrap">
      <view class="loading-text">加载中...</view>
    </view>

    <!-- 合同确认页面（显示摘要+个人信息） -->
    <view v-else-if="step === 'preview'" class="preview-wrap">
      <view class="preview-header">
        <text class="preview-title">合同签署确认</text>
        <text class="preview-sub">{{ projectName }}</text>
      </view>

      <scroll-view scroll-y class="preview-content">
        <!-- 租户信息 -->
        <view class="info-section">
          <view class="section-title">租户信息</view>
          <view class="info-row">
            <text class="info-label">姓名</text>
            <text class="info-value">{{ tenantName }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">手机号</text>
            <text class="info-value">{{ tenantPhone }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">身份证号</text>
            <text class="info-value">{{ tenantIdCard || '未填写' }}</text>
          </view>
        </view>

        <!-- 房源信息 -->
        <view class="info-section">
          <view class="section-title">房源信息</view>
          <view class="info-row">
            <text class="info-label">项目</text>
            <text class="info-value">{{ projectName }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">房间</text>
            <text class="info-value">{{ houseCode }}</text>
          </view>
        </view>

        <!-- 合同条款 -->
        <view class="info-section">
          <view class="section-title">合同条款</view>
          <view class="info-row">
            <text class="info-label">月租金</text>
            <text class="info-value price">¥{{ rentPrice }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">押金</text>
            <text class="info-value price">¥{{ deposit }}</text>
          </view>
          <view class="info-row">
            <text class="info-label">租期</text>
            <text class="info-value">{{ rentMonths }}个月</text>
          </view>
          <view class="info-row">
            <text class="info-label">合同期限</text>
            <text class="info-value">{{ startDate }} 至 {{ endDate }}</text>
          </view>
        </view>

        <!-- 温馨提示 -->
        <view class="info-section tips">
          <text class="tips-text">点击"确认并签署"后，将跳转至 e签宝 平台完成电子合同签署。合同内容将以 e签宝 电子合同为准。</text>
        </view>
      </scroll-view>

      <view class="preview-footer">
        <button class="btn-primary" :disabled="submitting" @click="handleConfirmSign">
          {{ submitting ? '处理中...' : '确认并签署' }}
        </button>
      </view>
    </view>

    <!-- e签宝实名认证 -->
    <view v-else-if="step === 'auth'" class="step-wrap">
      <view class="step-icon">
        <text>&#x1f510;</text>
      </view>
      <view class="step-title">需要实名认证</view>
      <view class="step-desc">签署电子合同前，请先完成实名认证</view>
      <button class="btn-primary" @click="goAuth">前往认证</button>
    </view>

    <!-- 等待认证 -->
    <view v-else-if="step === 'auth_waiting'" class="step-wrap">
      <view class="step-icon">
        <text>&#x23f3;</text>
      </view>
      <view class="step-title">等待认证完成</view>
      <view class="step-desc">请在外部页面完成实名认证后返回</view>
      <button class="btn-primary" @click="checkAuthAndSign">我已完成认证</button>
      <button class="btn-secondary" @click="goAuth">重新认证</button>
    </view>

    <!-- e签宝签署 -->
    <view v-else-if="step === 'esign'" class="step-wrap">
      <view class="step-icon">
        <text>&#x1f4dd;</text>
      </view>
      <view class="step-title">电子签署</view>
      <view class="step-desc">合同已生成，请前往 e签宝完成电子签署</view>
      <button class="btn-primary" @click="goEsign">前往 e签宝签署</button>
    </view>

    <!-- 等待e签宝签署完成 -->
    <view v-else-if="step === 'esign_waiting'" class="step-wrap">
      <view class="step-icon">
        <text>&#x23f3;</text>
      </view>
      <view class="step-title">等待签署完成</view>
      <view class="step-desc">正在检测签署状态，请稍候...</view>
      <button class="btn-primary" @click="manualCheckStatus">手动刷新状态</button>
      <button class="btn-secondary" @click="goBack">返回合同列表</button>
    </view>

    <!-- 完成 -->
    <view v-else-if="step === 'done'" class="step-wrap">
      <view class="step-icon success">
        <text>&#x2713;</text>
      </view>
      <view class="step-title">签署完成</view>
      <view class="step-desc">合同已签署成功，正在跳转至押金缴费...</view>
      <view class="done-actions">
        <button class="btn-primary" @click="goToBill">立即缴纳押金</button>
        <button class="btn-secondary" @click="goBack">返回合同列表</button>
      </view>
    </view>

    <!-- 错误 -->
    <view v-else-if="step === 'error'" class="step-wrap">
      <view class="step-icon error">
        <text>&#x2717;</text>
      </view>
      <view class="step-title">操作失败</view>
      <view class="step-desc">{{ errorMsg }}</view>
      <button class="btn-primary" @click="init">重试</button>
      <button class="btn-secondary" @click="goBack">返回</button>
    </view>
  </view>
</template>

<script>
import { generateContract, signContract } from '@/api/contract'
import { getAuthStatus, initSign } from '@/api/esign'
import { get } from '@/utils/request'

export default {
  data() {
    return {
      loading: true,
      step: 'preview',
      userId: null,
      userInfo: null,
      roomId: null,
      projectId: null,
      contractId: null,
      rentMonths: 12,
      // 合同数据
      templateId: null,
      projectName: '',
      houseCode: '',
      rentPrice: '0',
      deposit: '0',
      startDate: '',
      endDate: '',
      contractContent: '',
      // 租户信息
      tenantName: '',
      tenantPhone: '',
      tenantIdCard: '',
      // e签宝
      authUrl: '',
      signUrl: '',
      errorMsg: '',
      pollTimer: null,
      submitting: false
    }
  },
  onLoad(options) {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo || !userInfo.userId) {
      uni.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => { uni.redirectTo({ url: '/pages/login/index' }) }, 1500)
      return
    }
    this.userId = userInfo.userId
    this.userInfo = userInfo
    this.tenantName = userInfo.realName || userInfo.nickname || ''
    this.tenantPhone = userInfo.phone || ''
    this.tenantIdCard = userInfo.idCard || ''

    this.roomId = options.roomId ? parseInt(options.roomId) : null
    this.projectId = options.projectId ? parseInt(options.projectId) : null
    this.contractId = options.contractId ? parseInt(options.contractId) : null
    this.rentMonths = options.rentMonths ? parseInt(options.rentMonths) : 12

    // 检测是否从e签宝跳回（H5环境）
    // #ifdef H5
    const urlParams = new URLSearchParams(window.location.search)
    if (urlParams.get('esign_done') === '1') {
      // 清除URL参数
      window.history.replaceState({}, '', window.location.pathname + window.location.hash)
      const savedContractId = uni.getStorageSync('esign_contractId')
      if (savedContractId) {
        this.contractId = parseInt(savedContractId)
        this.loading = false
        this.step = 'esign_waiting'
        this.startPolling()
        return
      }
    }
    // #endif

    if (!this.roomId || !this.projectId) {
      this.step = 'error'
      this.errorMsg = '缺少房源或项目信息'
      this.loading = false
      return
    }
    this.init()
  },
  onShow() {
    if (this.step === 'auth_waiting') {
      this.checkAuthAndSign()
    }
  },
  onUnload() {
    this.stopPolling()
  },
  methods: {
    async init() {
      this.loading = true
      try {
        // 如果已有 contractId，直接走 e签宝流程
        if (this.contractId) {
          await this.callInitSign()
          return
        }
        // 获取合同数据（用于显示摘要）
        const res = await generateContract({
          houseId: this.roomId,
          rentMonths: this.rentMonths
        })
        if (res.code !== 200) throw new Error(res.msg || '获取合同信息失败')
        this.contractContent = res.data.contractContent || ''
        this.templateId = res.data.templateId
        this.rentPrice = res.data.rentPrice || '0'
        this.deposit = res.data.deposit || '0'
        this.endDate = res.data.endDate || ''
        this.projectName = res.data.projectName || ''
        this.houseCode = res.data.houseCode || ''
        // 生效日期 = 当前日期 + 3天
        const start = new Date()
        start.setDate(start.getDate() + 3)
        this.startDate = start.toISOString().split('T')[0]
        this.step = 'preview'
      } catch (e) {
        this.step = 'error'
        this.errorMsg = e.message || '初始化失败'
      } finally {
        this.loading = false
      }
    },

    // ========== 点击"确认并签署" ==========
    async handleConfirmSign() {
      if (this.submitting) return
      this.submitting = true
      uni.showLoading({ title: '正在发起签署...' })
      try {
        // 1. 保存合同（获取 contractId）
        if (!this.contractId) {
          const saveRes = await signContract({
            houseId: this.roomId,
            projectId: this.projectId,
            templateId: this.templateId,
            contractContent: this.contractContent,
            endDate: this.endDate,
            rentMonths: this.rentMonths,
            userId: this.userId
          })
          if (saveRes.code !== 200) throw new Error(saveRes.msg || '保存合同失败')
          this.contractId = saveRes.data.contractId
          uni.setStorageSync('esign_contractId', this.contractId)
          uni.setStorageSync('esign_userId', this.userId)
        }

        // 2. 调用 initSign 一体化接口
        await this.callInitSign()
      } catch (e) {
        this.step = 'error'
        this.errorMsg = e.message || '操作失败'
      } finally {
        this.submitting = false
        uni.hideLoading()
      }
    },

    // ========== 调用 initSign 接口 ==========
    async callInitSign() {
      this.loading = true
      try {
        const res = await initSign(this.contractId, this.userId)
        if (res.code !== 200) throw new Error(res.msg || '发起签署失败')

        if (res.data.needAuth) {
          this.authUrl = res.data.authUrl
          this.step = 'auth'
        } else {
          this.signUrl = res.data.signUrl
          this.step = 'esign'
        }
      } catch (e) {
        this.step = 'error'
        this.errorMsg = e.message || 'e签宝初始化失败'
      } finally {
        this.loading = false
      }
    },

    // ========== 实名认证流程 ==========
    goAuth() {
      if (!this.authUrl) return
      this.step = 'auth_waiting'
      // #ifdef H5
      window.open(this.authUrl, '_blank')
      // #endif
      // #ifndef H5
      plus.runtime.openURL(this.authUrl)
      // #endif
    },
    async checkAuthAndSign() {
      uni.showLoading({ title: '查询认证状态...' })
      try {
        const res = await getAuthStatus(this.userId)
        uni.hideLoading()
        if (res.code !== 200) throw new Error(res.msg || '查询失败')
        if (res.data.authenticated) {
          await this.callInitSign()
        } else {
          uni.showToast({ title: '认证未完成，请先完成认证', icon: 'none' })
          this.step = 'auth_waiting'
        }
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '查询失败', icon: 'none' })
      }
    },

    // ========== e签宝签署 ==========
    goEsign() {
      if (!this.signUrl) return
      this.step = 'esign_waiting'
      // #ifdef H5
      window.location.href = this.signUrl
      // #endif
      // #ifndef H5
      plus.runtime.openURL(this.signUrl)
      // #endif
      this.startPolling()
    },

    // ========== 轮询签署状态 ==========
    startPolling() {
      this.stopPolling()
      let count = 0
      this.pollTimer = setInterval(async () => {
        count++
        if (count >= 360) { this.stopPolling(); return } // 30分钟超时
        try {
          const res = await get(`/h5/app/contract/user/${this.userId}`)
          if (res.code === 200 && res.data) {
            const list = Array.isArray(res.data) ? res.data : [res.data]
            const target = list.find(c => c.contractId === this.contractId)
            if (target && target.contractStatus === '2') {
              this.stopPolling()
              this.step = 'done'
              // 3秒后自动跳转到账单页
              setTimeout(() => { this.goToBill() }, 3000)
            }
          }
        } catch (e) { /* ignore */ }
      }, 5000)
    },
    stopPolling() {
      if (this.pollTimer) { clearInterval(this.pollTimer); this.pollTimer = null }
    },

    // ========== 手动刷新状态 ==========
    async manualCheckStatus() {
      uni.showLoading({ title: '查询签署状态...' })
      try {
        const res = await get(`/h5/app/contract/user/${this.userId}`)
        uni.hideLoading()
        if (res.code === 200 && res.data) {
          const list = Array.isArray(res.data) ? res.data : [res.data]
          const target = list.find(c => c.contractId === this.contractId)
          if (target && target.contractStatus === '2') {
            this.stopPolling()
            this.step = 'done'
            setTimeout(() => { this.goToBill() }, 3000)
          } else {
            uni.showToast({ title: '签署尚未完成，请稍候', icon: 'none' })
          }
        }
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: '查询失败', icon: 'none' })
      }
    },

    // ========== 跳转账单缴费 ==========
    goToBill() {
      this.stopPolling()
      uni.removeStorageSync('esign_contractId')
      uni.removeStorageSync('esign_userId')
      uni.redirectTo({ url: '/pages/affairs/contract?esign_done=1' })
    },

    goBack() {
      this.stopPolling()
      uni.navigateBack()
    }
  }
}
</script>

<style scoped>
.container {
  min-height: 100vh;
  background: #f5f6fc;
  display: flex;
  flex-direction: column;
}
.loading-wrap, .step-wrap {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80rpx 60rpx;
  background: #fff;
  border-radius: 24rpx;
  margin: 40rpx;
  width: calc(100% - 80rpx);
  box-sizing: border-box;
}
.step-icon { font-size: 100rpx; margin-bottom: 32rpx; }
.step-icon.success { color: #07c160; }
.step-icon.error { color: #e5252b; }
.step-title { font-size: 36rpx; font-weight: 600; color: #1a1a1a; margin-bottom: 16rpx; }
.step-desc { font-size: 28rpx; color: #666; text-align: center; margin-bottom: 48rpx; line-height: 1.6; }
.btn-primary {
  width: 100%; height: 90rpx; background: #4A90E2; color: #fff;
  border-radius: 50rpx; font-size: 32rpx; border: none; margin-bottom: 24rpx;
}
.btn-primary[disabled] { opacity: 0.6; }
.btn-secondary {
  width: 100%; height: 90rpx; background: #fff; color: #4A90E2;
  border: 2rpx solid #4A90E2; border-radius: 50rpx; font-size: 32rpx;
}
.loading-text { font-size: 32rpx; color: #666; }
.done-actions { width: 100%; }

/* 合同确认页 */
.preview-wrap {
  flex: 1; display: flex; flex-direction: column; min-height: 100vh;
}
.preview-header {
  padding: 30rpx; background: #fff; border-bottom: 1rpx solid #eee;
  display: flex; flex-direction: column; align-items: center;
}
.preview-title { font-size: 36rpx; font-weight: 600; color: #1a1a1a; }
.preview-sub { font-size: 26rpx; color: #999; margin-top: 8rpx; }
.preview-content {
  flex: 1; padding: 20rpx 30rpx; background: #f5f6fc;
  max-height: calc(100vh - 260rpx);
}

/* 信息卡片 */
.info-section {
  background: #fff; border-radius: 16rpx; padding: 24rpx 30rpx;
  margin-bottom: 20rpx;
}
.section-title {
  font-size: 30rpx; font-weight: 600; color: #1a1a1a;
  padding-bottom: 16rpx; border-bottom: 1rpx solid #f0f0f0;
  margin-bottom: 12rpx;
}
.info-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 10rpx 0;
}
.info-label { font-size: 26rpx; color: #999; }
.info-value { font-size: 26rpx; color: #333; font-weight: 500; }
.info-value.price { color: #e5252b; font-weight: 600; }

/* 温馨提示 */
.tips { background: #fff8e6; }
.tips-text { font-size: 24rpx; color: #b8860b; line-height: 1.6; }

.preview-footer {
  padding: 20rpx 30rpx; background: #fff; border-top: 1rpx solid #eee;
}
</style>
