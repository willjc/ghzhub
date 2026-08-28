<template>
  <view class="page">
    <view class="card">
      <view class="icon-wrap">
        <text class="icon">✕</text>
      </view>
      <view class="title">资格校验未通过</view>
      <view class="subtitle">以下信息与申请要求不符，请核对后重新校验</view>

      <view class="reason-list" v-if="reasons.length">
        <view class="reason-item" v-for="(r, idx) in reasons" :key="idx">
          <text class="dot">•</text>
          <text class="reason-text">{{ formatReason(r) }}</text>
        </view>
      </view>

      <view class="detail-title">核验明细</view>
      <view class="detail-list">
        <view
          v-for="it in items"
          :key="it.code"
          class="detail-item"
          :class="['state-' + it.status]"
        >
          <text class="detail-label">{{ it.label }}</text>
          <text class="detail-status">{{ statusText(it.status) }}</text>
        </view>
      </view>

      <view v-if="educationFailed" class="education-notice">
        因数据端口原因，请上传证明材料。提交后将由工作人员人工审核，审核将在3个工作日内完成。
      </view>

      <view class="tips">
        如信息有误（如政务数据尚未更新），可稍后重新校验；若仍不通过请联系工作人员。
      </view>
    </view>

    <view class="footer">
      <view class="btn-secondary" @click="goHome">返回首页</view>
      <view class="btn-primary" @click="recheck">重新校验</view>
      <view v-if="canAppeal" class="btn-primary btn-appeal" @click="goAppeal">
        {{ educationFailed ? '上传学历证明' : '发起申诉' }}
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      reasons: [],
      items: []
    }
  },
  onLoad(options) {
    if (options && options.reasons) {
      try { this.reasons = JSON.parse(decodeURIComponent(options.reasons)) } catch (e) { this.reasons = [] }
    }
    if (options && options.items) {
      try { this.items = JSON.parse(decodeURIComponent(options.items)) } catch (e) { this.items = [] }
    }
  },
  computed: {
    educationFailed() {
      return this.items.some(i => i.code === 'education' && i.status === 'failed')
    },
    // 仅当学历或社保 failed 时才允许申诉
    canAppeal() {
      return this.items.some(i =>
        (i.code === 'education' || i.code === 'social') && i.status === 'failed'
      )
    }
  },
  methods: {
    formatReason(reason) {
      if (this.educationFailed && reason && reason.indexOf('学历待人工审核') !== -1) {
        return '因数据端口原因，学历信息需上传证明材料进行人工审核'
      }
      return reason
    },
    statusText(s) {
      switch (s) {
        case 'passed': return '通过'
        case 'failed': return '未通过'
        case 'skipped': return '无需核验'
        case 'error': return '未完成'
        default: return '—'
      }
    },
    recheck() {
      // 手动重新校验：回 check 页重新跑一次
      uni.redirectTo({ url: '/subpkg/qualification/check' })
    },
    goAppeal() {
      uni.navigateTo({ url: '/subpkg/affairs/appeal-submit' })
    },
    goHome() {
      uni.switchTab({ url: '/pages/index/index' })
    }
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f6fc;
  padding: 40rpx 30rpx 180rpx;
  box-sizing: border-box;
}
.card {
  background: #fff;
  border-radius: 24rpx;
  padding: 60rpx 40rpx 40rpx;
  display: flex;
  flex-direction: column;
  align-items: stretch;
}
.icon-wrap {
  width: 120rpx;
  height: 120rpx;
  border-radius: 50%;
  background: #fff0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 32rpx;
}
.icon {
  font-size: 68rpx;
  color: #e74c3c;
  font-weight: 700;
}
.title {
  font-size: 36rpx;
  font-weight: 600;
  color: #1a1a1a;
  text-align: center;
  margin-bottom: 12rpx;
}
.subtitle {
  font-size: 26rpx;
  color: #888;
  text-align: center;
  margin-bottom: 40rpx;
}
.reason-list {
  background: #fff8f7;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 40rpx;
}
.reason-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12rpx;
}
.reason-item:last-child { margin-bottom: 0; }
.dot { color: #e74c3c; margin-right: 12rpx; }
.reason-text {
  flex: 1;
  font-size: 26rpx;
  color: #c0392b;
  line-height: 38rpx;
}
.detail-title {
  font-size: 28rpx;
  color: #1a1a1a;
  font-weight: 600;
  margin-bottom: 16rpx;
}
.detail-list {
  border-top: 1rpx solid #f0f0f0;
}
.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24rpx 0;
  border-bottom: 1rpx solid #f0f0f0;
}
.detail-label {
  font-size: 26rpx;
  color: #333;
}
.detail-status {
  font-size: 24rpx;
  color: #888;
}
.state-passed .detail-status { color: #0f9d58; }
.state-failed .detail-status { color: #e74c3c; }
.state-error .detail-status { color: #e59700; }
.state-skipped .detail-status { color: #bbb; }
.education-notice {
  margin-top: 32rpx;
  padding: 24rpx;
  border-radius: 16rpx;
  background: #fff7e8;
  color: #b85c00;
  font-size: 26rpx;
  line-height: 40rpx;
}
.tips {
  margin-top: 32rpx;
  font-size: 22rpx;
  color: #999;
  line-height: 36rpx;
}
.footer {
  position: fixed;
  left: 0;
  right: 0;
  bottom: 0;
  padding: 20rpx 30rpx calc(20rpx + env(safe-area-inset-bottom));
  background: #fff;
  display: flex;
  box-shadow: 0 -2rpx 10rpx rgba(0,0,0,0.04);
}
.btn-secondary, .btn-primary {
  flex: 1;
  height: 88rpx;
  border-radius: 16rpx;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 30rpx;
}
.btn-secondary {
  background: #f0f2f7;
  color: #333;
  margin-right: 20rpx;
}
.btn-primary {
  background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
  color: #fff;
  font-weight: 500;
}
.btn-primary + .btn-primary {
  margin-left: 20rpx;
}
.btn-appeal {
  background: linear-gradient(270deg, #ffb04f 0%, #ff7a18 100%);
}
</style>
