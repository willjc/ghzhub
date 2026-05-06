<template>
  <view class="page">
    <view class="header">
      <view class="title">正在校验申请资格</view>
      <view class="subtitle">我们正在核对您的婚姻、社保及房产信息</view>
    </view>

    <view class="progress-bar">
      <view class="progress-fill" :style="{ width: progressPercent + '%' }"></view>
    </view>
    <view class="progress-text">{{ progressPercent }}%</view>

    <view class="check-list">
      <view
        v-for="item in items"
        :key="item.code"
        class="check-item"
        :class="['state-' + item.status]"
      >
        <view class="icon-col">
          <view v-if="item.status === 'pending'" class="dot-loading"></view>
          <text v-else-if="item.status === 'passed'" class="icon icon-ok">✓</text>
          <text v-else-if="item.status === 'failed'" class="icon icon-fail">✕</text>
          <text v-else-if="item.status === 'skipped'" class="icon icon-skip">—</text>
          <text v-else class="icon icon-err">!</text>
        </view>
        <view class="body-col">
          <text class="label">{{ item.label }}</text>
          <text class="message">{{ item.message || itemHint(item) }}</text>
        </view>
      </view>
    </view>

    <view class="footer-tip" v-if="!finished">请不要关闭本页，校验通常需要 3-10 秒</view>
    <view class="footer-tip" v-else-if="resultPassed">资格校验已通过，即将为您继续办理</view>
    <view class="footer-tip error" v-else>资格校验未通过</view>
  </view>
</template>

<script>
import { runQualificationCheck, getCurrentUserId } from '@/api/qualification'

const INIT_ITEMS = [
  { code: 'marriage',     label: '婚姻信息核验',   status: 'pending', message: '' },
  { code: 'social',       label: '社保缴纳记录',   status: 'pending', message: '' },
  { code: 'selfEstate',   label: '本人不动产登记', status: 'pending', message: '' },
  { code: 'selfHousing',  label: '本人公租房记录', status: 'pending', message: '' },
  { code: 'spouseEstate', label: '配偶不动产登记', status: 'pending', message: '' },
  { code: 'spouseHousing',label: '配偶公租房记录', status: 'pending', message: '' }
]

export default {
  data() {
    return {
      items: JSON.parse(JSON.stringify(INIT_ITEMS)),
      progressPercent: 0,
      progressTimer: null,
      finished: false,
      resultPassed: false,
      failReasons: [],
      redirect: '',
      redirectParams: null
    }
  },
  onLoad(options) {
    if (options && options.redirect) {
      this.redirect = decodeURIComponent(options.redirect)
    }
    if (options && options.redirectParams) {
      try {
        this.redirectParams = JSON.parse(decodeURIComponent(options.redirectParams))
      } catch (e) {
        this.redirectParams = null
      }
    }
    this.startCheck()
  },
  onUnload() {
    if (this.progressTimer) {
      clearInterval(this.progressTimer)
      this.progressTimer = null
    }
  },
  methods: {
    itemHint(item) {
      if (item.status === 'pending') return '校验中...'
      return ''
    },
    startCheck() {
      const userId = getCurrentUserId()
      if (!userId) {
        uni.showToast({ title: '请先登录', icon: 'none' })
        setTimeout(() => uni.navigateBack(), 800)
        return
      }
      this.startVirtualProgress()
      runQualificationCheck(userId)
        .then((res) => {
          const data = (res && res.data) || {}
          this.applyServerResult(data)
        })
        .catch((err) => {
          this.handleCheckError(err)
        })
    },
    // 虚拟进度：90% 前缓慢爬升，真实结果回来后再拉到 100%
    startVirtualProgress() {
      this.progressTimer = setInterval(() => {
        if (this.progressPercent < 88) {
          const step = this.progressPercent < 50 ? 4 : 2
          this.progressPercent = Math.min(88, this.progressPercent + step)
        }
      }, 220)
    },
    applyServerResult(data) {
      if (this.progressTimer) {
        clearInterval(this.progressTimer)
        this.progressTimer = null
      }
      this.progressPercent = 100

      // 服务端返回的 items 按 code 匹配
      const map = {}
      ;(data.items || []).forEach((it) => { map[it.code] = it })
      this.items = this.items.map((it) => {
        const srv = map[it.code]
        if (!srv) {
          // 服务端没返回（例如未婚就不会有 spouseEstate/spouseHousing），标记为跳过
          return { ...it, status: 'skipped', message: '本次无需核验' }
        }
        return {
          code: it.code,
          label: it.label,
          status: srv.status || 'error',
          message: srv.message || ''
        }
      })

      this.finished = true
      this.resultPassed = !!data.passed
      this.failReasons = data.failReasons || []

      setTimeout(() => {
        if (this.resultPassed) {
          this.goRedirect()
        } else {
          this.goFail()
        }
      }, 900)
    },
    handleCheckError(err) {
      if (this.progressTimer) {
        clearInterval(this.progressTimer)
        this.progressTimer = null
      }
      this.progressPercent = 100
      this.finished = true
      this.resultPassed = false
      this.failReasons = [(err && err.msg) || '校验失败，请稍后再试']
      // 所有 pending 项标记为错误
      this.items = this.items.map((it) => {
        if (it.status === 'pending') {
          return { ...it, status: 'error', message: '未完成' }
        }
        return it
      })
      setTimeout(() => this.goFail(), 900)
    },
    goRedirect() {
      if (!this.redirect) {
        // 没指定目标则退回上一页，由原页面自己继续
        uni.navigateBack()
        return
      }
      let url = this.redirect
      if (this.redirectParams) {
        const qs = Object.keys(this.redirectParams)
          .map(k => `${encodeURIComponent(k)}=${encodeURIComponent(this.redirectParams[k])}`)
          .join('&')
        url += (url.indexOf('?') >= 0 ? '&' : '?') + qs
      }
      uni.redirectTo({ url })
    },
    goFail() {
      const reasons = encodeURIComponent(JSON.stringify(this.failReasons))
      const itemsPayload = encodeURIComponent(JSON.stringify(this.items))
      uni.redirectTo({
        url: `/subpkg/qualification/fail?reasons=${reasons}&items=${itemsPayload}`
      })
    }
  }
}
</script>

<style scoped>
.page {
  min-height: 100vh;
  background: #f5f6fc;
  padding: 60rpx 40rpx;
  box-sizing: border-box;
}
.header {
  margin-bottom: 40rpx;
}
.title {
  font-size: 40rpx;
  font-weight: 600;
  color: #1a1a1a;
  margin-bottom: 16rpx;
}
.subtitle {
  font-size: 26rpx;
  color: #888;
}
.progress-bar {
  width: 100%;
  height: 12rpx;
  background: #e6eaf2;
  border-radius: 6rpx;
  overflow: hidden;
  margin-top: 20rpx;
}
.progress-fill {
  height: 100%;
  background: linear-gradient(270deg, #4fc7ff 0%, #0f73ff 100%);
  transition: width 0.3s ease;
}
.progress-text {
  margin-top: 12rpx;
  font-size: 24rpx;
  color: #0f73ff;
  text-align: right;
}
.check-list {
  margin-top: 48rpx;
  background: #fff;
  border-radius: 20rpx;
  padding: 20rpx 24rpx;
}
.check-item {
  display: flex;
  align-items: center;
  padding: 24rpx 8rpx;
  border-bottom: 1rpx solid #f0f0f0;
}
.check-item:last-child {
  border-bottom: none;
}
.icon-col {
  width: 60rpx;
  display: flex;
  justify-content: center;
}
.body-col {
  flex: 1;
  display: flex;
  flex-direction: column;
}
.label {
  font-size: 28rpx;
  color: #222;
}
.message {
  margin-top: 6rpx;
  font-size: 22rpx;
  color: #999;
}
.state-failed .message,
.state-error .message { color: #e74c3c; }
.state-passed .message { color: #0f9d58; }
.icon {
  font-size: 32rpx;
  font-weight: 700;
}
.icon-ok { color: #0f9d58; }
.icon-fail { color: #e74c3c; }
.icon-skip { color: #bbb; }
.icon-err { color: #e59700; }
.dot-loading {
  width: 24rpx;
  height: 24rpx;
  border-radius: 50%;
  border: 3rpx solid #0f73ff;
  border-top-color: transparent;
  animation: spin 0.8s linear infinite;
}
@keyframes spin {
  to { transform: rotate(360deg); }
}
.footer-tip {
  margin-top: 36rpx;
  text-align: center;
  font-size: 24rpx;
  color: #888;
}
.footer-tip.error { color: #e74c3c; }
</style>
