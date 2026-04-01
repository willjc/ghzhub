<template>
  <view class="container">
    <!-- 步骤1: 加载中 -->
    <view v-if="loading" class="loading-wrap">
      <view class="loading-text">加载中...</view>
    </view>

    <!-- 步骤2: 合同预览 -->
    <view v-else-if="step === 'preview'" class="preview-wrap">
      <view class="preview-header">
        <text class="preview-title">合同预览</text>
        <text class="preview-sub">{{ projectName }}</text>
      </view>
      <scroll-view scroll-y class="preview-content">
        <rich-text :nodes="contractContent"></rich-text>
      </scroll-view>
      <view class="preview-footer">
        <text class="price-info">月租金：¥{{ rentPrice }} | 押金：¥{{ deposit }}</text>
        <button class="btn-primary" :disabled="submitting" @click="handleConfirmSign">
          {{ submitting ? '处理中...' : '确认并签署' }}
        </button>
      </view>
    </view>

    <!-- 步骤3: e签宝实名认证 -->
    <view v-else-if="step === 'auth'" class="step-wrap">
      <view class="step-icon">
        <text>&#x1f510;</text>
      </view>
      <view class="step-title">需要实名认证</view>
      <view class="step-desc">签署电子合同前，请先完成实名认证</view>
      <button class="btn-primary" @click="goAuth">前往认证</button>
    </view>

    <!-- 步骤4: 等待认证 -->
    <view v-else-if="step === 'auth_waiting'" class="step-wrap">
      <view class="step-icon">
        <text>&#x23f3;</text>
      </view>
      <view class="step-title">等待认证完成</view>
      <view class="step-desc">请在外部页面完成实名认证后返回</view>
      <button class="btn-primary" @click="checkAuthAndSign">我已完成认证</button>
      <button class="btn-secondary" @click="goAuth">重新认证</button>
    </view>

    <!-- 步骤5: e签宝签署 -->
    <view v-else-if="step === 'esign'" class="step-wrap">
      <view class="step-icon">
        <text>&#x1f4dd;</text>
      </view>
      <view class="step-title">电子签署</view>
      <view class="step-desc">合同已生成，请前往 e签宝完成电子签署</view>
      <button class="btn-primary" @click="goEsign">前往 e签宝签署</button>
    </view>

    <!-- 步骤6: 等待e签宝签署完成 -->
    <view v-else-if="step === 'esign_waiting'" class="step-wrap">
      <view class="step-icon">
        <text>&#x23f3;</text>
      </view>
      <view class="step-title">等待签署完成</view>
      <view class="step-desc">正在检测签署状态，请稍候...</view>
      <button class="btn-secondary" @click="goBack">返回合同列表</button>
    </view>

    <!-- 步骤7: 完成 -->
    <view v-else-if="step === 'done'" class="step-wrap">
      <view class="step-icon success">
        <text>&#x2713;</text>
      </view>
      <view class="step-title">签署完成</view>
      <view class="step-desc">合同已签署成功</view>
      <button class="btn-primary" @click="goBack">返回合同列表</button>
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
      step: 'preview',  // preview | auth | auth_waiting | esign | esign_waiting | done | error
      userId: null,
      roomId: null,
      projectId: null,
      contractId: null,
      rentMonths: 12,
      // 合同预览数据
      contractContent: '',
      templateId: null,
      projectName: '',
      rentPrice: '0',
      deposit: '0',
      endDate: '',
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
    this.roomId = options.roomId ? parseInt(options.roomId) : null
    this.projectId = options.projectId ? parseInt(options.projectId) : null
    this.contractId = options.contractId ? parseInt(options.contractId) : null
    this.rentMonths = options.rentMonths ? parseInt(options.rentMonths) : 12

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
        // 如果已有 contractId，说明合同已保存，直接走 e签宝流程
        if (this.contractId) {
          await this.callInitSign()
          return
        }
        // 否则先生成合同预览
        const res = await generateContract({
          houseId: this.roomId,
          rentMonths: this.rentMonths
        })
        if (res.code !== 200) throw new Error(res.msg || '生成合同失败')
        this.contractContent = res.data.contractContent || ''
        this.templateId = res.data.templateId
        this.rentPrice = res.data.rentPrice || '0'
        this.deposit = res.data.deposit || '0'
        this.endDate = res.data.endDate || ''
        this.projectName = res.data.projectName || ''
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
      uni.showLoading({ title: '处理中...' })
      try {
        // 1. 保存合同（获取 contractId）
        if (!this.contractId) {
          const saveRes = await signContract({
            houseId: this.roomId,
            projectId: this.projectId,
            templateId: this.templateId,
            contractContent: this.contractContent,
            endDate: this.endDate,
            rentMonths: this.rentMonths
          })
          if (saveRes.code !== 200) throw new Error(saveRes.msg || '保存合同失败')
          this.contractId = saveRes.data.contractId
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
          // 需要实名认证
          this.authUrl = res.data.authUrl
          this.step = 'auth'
        } else {
          // 已认证，获取签署链接
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
          // 认证完成，重新调用 initSign
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
      window.open(this.signUrl, '_blank')
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
        if (count >= 60) { this.stopPolling(); return }
        try {
          const res = await get(`/h5/app/contract/user/${this.userId}`)
          if (res.code === 200 && res.data) {
            const list = Array.isArray(res.data) ? res.data : [res.data]
            const target = list.find(c => c.contractId === this.contractId)
            if (target && target.contractStatus === '2') {
              this.stopPolling()
              this.step = 'done'
            }
          }
        } catch (e) { /* 轮询失败不中断 */ }
      }, 5000)
    },
    stopPolling() {
      if (this.pollTimer) { clearInterval(this.pollTimer); this.pollTimer = null }
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

/* 合同预览 */
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
  flex: 1; padding: 30rpx; background: #fff; margin-top: 20rpx;
  max-height: calc(100vh - 360rpx);
}
.preview-footer {
  padding: 20rpx 30rpx; background: #fff; border-top: 1rpx solid #eee;
}
.price-info { display: block; font-size: 26rpx; color: #e5252b; margin-bottom: 20rpx; text-align: center; }
</style>
