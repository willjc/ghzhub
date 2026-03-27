<template>
  <view class="result-wrap">
    <view v-if="checking" class="status-icon">
      <text class="icon-loading">⏳</text>
      <text class="tip">正在确认支付结果...</text>
    </view>
    <view v-else-if="paid" class="status-icon success">
      <text class="icon-ok">✓</text>
      <text class="tip">支付成功！</text>
      <text class="sub-tip">正在跳转...</text>
    </view>
    <view v-else class="status-icon fail">
      <text class="icon-fail">✗</text>
      <text class="tip">支付未完成</text>
      <button class="btn-retry" @click="goBack">返回重试</button>
    </view>
  </view>
</template>

<script>
import { queryPayResult } from '@/api/pay'

export default {
  data() {
    return {
      billNo: '',
      checking: true,
      paid: false
    }
  },
  onLoad(options) {
    this.billNo = options.billNo || ''
    if (this.billNo) {
      this.poll()
    } else {
      this.checking = false
    }
  },
  methods: {
    async poll() {
      for (let i = 0; i < 8; i++) {
        await new Promise(r => setTimeout(r, 2000))
        try {
          const res = await queryPayResult(this.billNo)
          if (res.code === 200 && res.data.paid) {
            this.checking = false
            this.paid = true
            setTimeout(() => {
              uni.redirectTo({ url: '/pages/upload/index' })
            }, 1500)
            return
          }
        } catch (e) { /* ignore single query failure */ }
      }
      this.checking = false
      this.paid = false
    },
    goBack() {
      uni.navigateBack()
    }
  }
}
</script>

<style>
.result-wrap { display: flex; flex-direction: column; align-items: center; justify-content: center; min-height: 60vh; }
.status-icon { display: flex; flex-direction: column; align-items: center; gap: 20rpx; }
.icon-loading, .icon-ok, .icon-fail { font-size: 80rpx; }
.tip { font-size: 32rpx; color: #333; }
.sub-tip { font-size: 26rpx; color: #999; }
.btn-retry { margin-top: 40rpx; background: #4A90E2; color: #fff; border-radius: 50rpx; padding: 20rpx 60rpx; font-size: 28rpx; }
</style>
