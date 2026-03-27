<template>
  <view class="container">
    <view v-if="loading" class="loading-wrap">
      <view class="loading-text">加载中...</view>
    </view>

    <view v-else-if="step === 'auth'" class="step-wrap">
      <view class="step-icon">🔐</view>
      <view class="step-title">需要实名认证</view>
      <view class="step-desc">签署合同前，请先完成实名认证</view>
      <button class="btn-primary" @click="goAuth">前往认证</button>
    </view>

    <view v-else-if="step === 'auth_waiting'" class="step-wrap">
      <view class="step-icon">⏳</view>
      <view class="step-title">等待认证完成</view>
      <view class="step-desc">请在外部页面完成实名认证后返回</view>
      <button class="btn-primary" @click="checkAuthStatus">我已完成认证</button>
      <button class="btn-secondary" @click="goAuth">重新认证</button>
    </view>

    <view v-else-if="step === 'sign'" class="step-wrap">
      <view class="step-icon">📝</view>
      <view class="step-title">待签署合同</view>
      <view class="step-desc">请点击下方按钮进入签署页面</view>
      <button class="btn-primary" @click="goSign">前往签署</button>
    </view>

    <view v-else-if="step === 'sign_waiting'" class="step-wrap">
      <view class="step-icon">⏳</view>
      <view class="step-title">等待签署完成</view>
      <view class="step-desc">请在签署页面完成签署后返回</view>
      <button class="btn-primary" @click="goBack">返回合同列表</button>
    </view>

    <view v-else-if="step === 'done'" class="step-wrap">
      <view class="step-icon success">✓</view>
      <view class="step-title">签署完成</view>
      <view class="step-desc">合同已签署成功，请缴纳押金</view>
      <button class="btn-primary" @click="goBack">返回合同列表</button>
    </view>

    <view v-else-if="step === 'error'" class="step-wrap">
      <view class="step-icon error">✗</view>
      <view class="step-title">操作失败</view>
      <view class="step-desc">{{ errorMsg }}</view>
      <button class="btn-primary" @click="init">重试</button>
      <button class="btn-secondary" @click="goBack">返回</button>
    </view>
  </view>
</template>

<script>
import { getAuthUrl, getAuthStatus, getSignUrl } from '@/api/esign'

export default {
  data() {
    return {
      loading: true,
      step: 'auth',  // auth | auth_waiting | sign | sign_waiting | done | error
      contractId: null,
      tenantId: null,
      authUrl: '',
      signUrl: '',
      errorMsg: '',
      justReturned: false  // track onShow returns
    }
  },
  onLoad(options) {
    const userInfo = uni.getStorageSync('userInfo')
    if (!userInfo || !userInfo.userId) {
      uni.showToast({ title: '请先登录', icon: 'none' })
      setTimeout(() => {
        uni.redirectTo({ url: '/pages/login/index' })
      }, 1500)
      return
    }
    this.tenantId = userInfo.userId
    this.contractId = options.contractId ? parseInt(options.contractId) : null
    if (!this.contractId) {
      this.step = 'error'
      this.errorMsg = '合同参数缺失'
      this.loading = false
      return
    }
    this.init()
  },
  onShow() {
    // When user returns from authUrl or signUrl external page
    if (!this.justReturned) return
    this.justReturned = false
    if (this.step === 'auth_waiting') {
      this.checkAuthStatus()
    }
  },
  methods: {
    async init() {
      this.loading = true
      try {
        const res = await getAuthUrl(this.tenantId)
        if (res.code !== 200) throw new Error(res.msg || '获取认证信息失败')
        if (res.data.needAuth) {
          this.authUrl = res.data.authUrl
          this.step = 'auth'
        } else {
          // Already certified, get sign URL
          await this.fetchSignUrl()
        }
      } catch (e) {
        this.step = 'error'
        this.errorMsg = e.message || '初始化失败'
      } finally {
        this.loading = false
      }
    },
    goAuth() {
      if (!this.authUrl) return
      this.justReturned = true
      this.step = 'auth_waiting'
      // Open auth URL in browser
      // #ifdef H5
      window.open(this.authUrl, '_blank')
      // #endif
      // #ifndef H5
      plus.runtime.openURL(this.authUrl)
      // #endif
    },
    async checkAuthStatus() {
      uni.showLoading({ title: '查询认证状态...' })
      try {
        const res = await getAuthStatus(this.tenantId)
        uni.hideLoading()
        if (res.code !== 200) throw new Error(res.msg || '查询失败')
        if (res.data.certified) {
          await this.fetchSignUrl()
        } else {
          uni.showToast({ title: '认证未完成，请先完成认证', icon: 'none' })
          this.step = 'auth_waiting'
        }
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: e.message || '查询失败', icon: 'none' })
      }
    },
    async fetchSignUrl() {
      try {
        const res = await getSignUrl(this.contractId, this.tenantId)
        if (res.code !== 200) throw new Error(res.msg || '获取签署链接失败')
        this.signUrl = res.data.signUrl
        this.step = 'sign'
      } catch (e) {
        this.step = 'error'
        this.errorMsg = e.message || '获取签署链接失败'
      }
    },
    goSign() {
      if (!this.signUrl) return
      this.justReturned = true
      this.step = 'sign_waiting'
      // #ifdef H5
      window.open(this.signUrl, '_blank')
      // #endif
      // #ifndef H5
      plus.runtime.openURL(this.signUrl)
      // #endif
    },
    goBack() {
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
  align-items: center;
  justify-content: center;
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
.step-icon {
  font-size: 100rpx;
  margin-bottom: 32rpx;
}
.step-icon.success { color: #07c160; }
.step-icon.error { color: #e5252b; }
.step-title {
  font-size: 36rpx;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 16rpx;
}
.step-desc {
  font-size: 28rpx;
  color: #666;
  text-align: center;
  margin-bottom: 48rpx;
  line-height: 1.6;
}
.btn-primary {
  width: 100%;
  height: 90rpx;
  background: #4A90E2;
  color: #fff;
  border-radius: 50rpx;
  font-size: 32rpx;
  border: none;
  margin-bottom: 24rpx;
}
.btn-secondary {
  width: 100%;
  height: 90rpx;
  background: #fff;
  color: #4A90E2;
  border: 2rpx solid #4A90E2;
  border-radius: 50rpx;
  font-size: 32rpx;
}
.loading-text {
  font-size: 32rpx;
  color: #666;
}
</style>
