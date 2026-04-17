<template>
  <view class="page">

    <!-- ── 加载中 ── -->
    <view v-if="loading" class="state-screen">
      <view class="state-spinner">
        <view class="spinner-ring"></view>
      </view>
      <text class="state-label">加载中...</text>
    </view>

    <!-- ── 合同确认预览 ── -->
    <view v-else-if="step === 'preview'" class="preview-layout">

      <!-- 预订倒计时条 -->
      <view class="countdown-bar" v-if="showLockCountdown && !isRenew"
        :class="{ 'countdown-bar-urgent': countdownUrgent, 'countdown-bar-expired': countdownExpired }">
        <view class="countdown-bar-inner">
          <text class="countdown-bar-icon">⏱</text>
          <text class="countdown-bar-text" :class="{ 'countdown-blink': countdownUrgent }">
            {{ countdownExpired ? '预订已失效，请重新选房' : '房源预订剩余 ' + countdown }}
          </text>
        </view>
      </view>

      <!-- 顶部 Banner -->
      <view class="banner">
        <view class="banner-tag">{{ isRenew ? '续租合同' : '电子合同' }}</view>
        <text class="banner-title">{{ isRenew ? '续租签署确认' : '合同签署确认' }}</text>
        <text class="banner-project">{{ projectName }}</text>
      </view>

      <!-- 滚动内容 -->
      <scroll-view scroll-y class="scroll-body">

        <!-- 租户信息 -->
        <view class="card">
          <view class="card-header">
            <view class="card-dot"></view>
            <text class="card-title">租户信息</text>
          </view>
          <view class="row">
            <text class="row-label">姓名</text>
            <text class="row-value">{{ tenantName }}</text>
          </view>
          <view class="row">
            <text class="row-label">手机号</text>
            <text class="row-value">{{ tenantPhone }}</text>
          </view>
          <view class="row">
            <text class="row-label">身份证号</text>
            <text class="row-value">{{ tenantIdCard || '未填写' }}</text>
          </view>
        </view>

        <!-- 房源信息 -->
        <view class="card">
          <view class="card-header">
            <view class="card-dot"></view>
            <text class="card-title">房源信息</text>
          </view>
          <view class="row">
            <text class="row-label">项目名称</text>
            <text class="row-value">{{ projectName }}</text>
          </view>
          <view class="row">
            <text class="row-label">房间号</text>
            <text class="row-value room-no">{{ houseCode || '-' }}</text>
          </view>
        </view>

        <!-- 合同条款 -->
        <view class="card">
          <view class="card-header">
            <view class="card-dot"></view>
            <text class="card-title">合同条款</text>
          </view>
          <view class="row">
            <text class="row-label">月租金</text>
            <text class="row-value price">¥ {{ rentPrice }}</text>
          </view>
          <view class="row">
            <text class="row-label">押金</text>
            <text class="row-value price" v-if="!isRenew">¥ {{ deposit }}</text>
            <text class="row-value renew-free" v-else>续租免押金</text>
          </view>
          <view class="row row-picker">
            <text class="row-label">租期</text>
            <picker
              mode="selector"
              :range="rentMonthsOptions"
              range-key="label"
              :value="rentMonthsIndex"
              @change="onRentMonthsChange"
            >
              <view class="picker-btn">
                <text class="picker-val">{{ rentMonths }} 个月</text>
                <text class="picker-arrow">›</text>
              </view>
            </picker>
          </view>
          <view class="row">
            <text class="row-label">合同期限</text>
            <text class="row-value date-range">{{ startDate }} — {{ endDate }}</text>
          </view>
        </view>

        <!-- 温馨提示 -->
        <view class="notice-box">
          
          <text class="notice-text">点击「确认并签署」后将跳转至 e签宝 平台完成电子合同签署，合同内容以 e签宝 电子合同为准。</text>
        </view>

        <view class="scroll-bottom-gap"></view>
      </scroll-view>

      <!-- 底部签署按钮 -->
      <view class="footer">
        <button
          class="btn-sign"
          :class="{ disabled: submitting || (!isRenew && countdownExpired) }"
          :disabled="submitting || (!isRenew && countdownExpired)"
          @click="handleConfirmSign"
        >
          <text class="btn-sign-icon">✍</text>
          <text>{{ submitting ? '处理中...' : '确认并签署' }}</text>
        </button>
      </view>
    </view>

    <!-- ── 需要实名认证 ── -->
    <view v-else-if="step === 'auth'" class="state-screen">
      <view class="state-circle auth">
        <text class="state-emoji">🔐</text>
      </view>
      <text class="state-title">需要实名认证</text>
      <text class="state-desc">签署电子合同前，请先完成实名认证</text>
      <button class="btn-main" @click="goAuth">前往认证</button>
    </view>

    <!-- ── 等待认证 ── -->
    <view v-else-if="step === 'auth_waiting'" class="state-screen">
      <!-- <view class="state-circle waiting">
        <text class="state-emoji">⏳</text>
      </view> -->
      <text class="state-title">等待认证完成</text>
      <text class="state-desc">请在外部页面完成实名认证后返回</text>
      <button class="btn-main" @click="checkAuthAndSign">我已完成认证</button>
      <button class="btn-ghost" @click="goAuth">重新认证</button>
    </view>

    <!-- ── e签宝签署 ── -->
    <view v-else-if="step === 'esign'" class="state-screen">
      <!-- <view class="state-circle sign">
        <text class="state-emoji">📝</text>
      </view> -->
      <text class="state-title">前往电子签署</text>
      <text class="state-desc">合同已生成，请前往 e签宝 完成电子签署</text>
      <button class="btn-main" @click="goEsign">前往 e签宝 签署</button>
    </view>

    <!-- ── 等待签署完成 ── -->
    <view v-else-if="step === 'esign_waiting'" class="state-screen">
      <view class="waiting-spinner">
        <view class="spinner-ring"></view>
      </view>
      <text class="state-title">正在确认签署结果</text>
      <text class="state-desc">系统正在与 e签宝 同步签署状态，请稍候...{{ pollCountDisplay }}</text>
      <button class="btn-ghost" style="margin-top: 40rpx;" @click="goBack">返回合同列表</button>
    </view>

    <!-- ── 签署完成 ── -->
    <view v-else-if="step === 'done'" class="state-screen">
      <view class="state-circle success">
        <text class="state-emoji">✅</text>
      </view>
      <text class="state-title">签署成功</text>
      <text class="state-desc">{{ isRenew ? '续租合同已签署成功' : '合同已签署成功，正在跳转至押金缴费...' }}</text>
      <button class="btn-main" v-if="!isRenew" @click="goToBill">立即缴纳押金</button>
      <button class="btn-ghost" @click="goBack">返回合同列表</button>
    </view>

    <!-- ── 操作失败 ── -->
    <view v-else-if="step === 'error'" class="state-screen">
      <view class="state-circle error">
        <text class="state-emoji">❌</text>
      </view>
      <text class="state-title">操作失败</text>
      <text class="state-desc">{{ errorMsg }}</text>
      <button class="btn-main" @click="init">重试</button>
      <button class="btn-ghost" @click="goBack">返回</button>
    </view>

  </view>
</template>

<script>
import { generateContract, signContract, renewContract } from '@/api/contract'
import { getAuthStatus, initSign, checkSign } from '@/api/esign'

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
      orderNo: null,
      rentMonths: 12,
      rentMonthsOptions: Array.from({ length: 24 }, (_, i) => ({ label: `${i + 1} 个月`, value: i + 1 })),
      rentMonthsIndex: 11,
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
      pollCount: 0,
      submitting: false,
      fromEsignWebview: false,  // 标记是否跳转去了 esign-webview，用于 onShow 检测返回
      // 续租模式
      isRenew: false,
      oldContractId: null,
      // 预订倒计时
      lockExpireTime: null,
      countdown: '10:00',
      countdownSeconds: 0,
      countdownTimer: null
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
    this.orderNo = options.orderNo || null
    this.rentMonths = options.rentMonths ? parseInt(options.rentMonths) : 12
    this.rentMonthsIndex = this.rentMonths - 1
    if (options.houseCode) {
      this.houseCode = decodeURIComponent(options.houseCode)
    }
    // 接收续租参数
    if (options.isRenew === 'true' || options.isRenew === true) {
      this.isRenew = true
    }
    if (options.oldContractId) {
      this.oldContractId = parseInt(options.oldContractId)
    }
    // 接收预订锁定过期时间（仅从选房流程跳转时有值）
    if (options.lockExpireTime && this.orderNo) {
      this.lockExpireTime = decodeURIComponent(options.lockExpireTime)
      this.startLockCountdown()
    }

    // 检测是否从e签宝跳回（H5环境）
    // #ifdef H5
    const urlParams = new URLSearchParams(window.location.search)
    if (urlParams.get('esign_done') === '1') {
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

    if (!this.contractId && (!this.roomId || !this.projectId)) {
      this.step = 'error'
      this.errorMsg = '缺少房源或项目信息'
      this.loading = false
      return
    }
    this.init()
  },
  onShow() {
    // #ifdef MP-WEIXIN
    // 从 esign-webview（认证页/签署页）返回时检查状态
    if (this.fromEsignWebview) {
      this.fromEsignWebview = false
      if (this.step === 'auth_waiting') {
        this.checkAuthAndSign()
      } else if (this.step === 'esign_waiting') {
        this.pollAfterReturn()  // 带延迟重试，等 e签宝 处理完
      }
      return
    }
    // #endif
    // H5 端：从实名认证外部页面返回后自动检查
    if (this.step === 'auth_waiting') {
      this.checkAuthAndSign()
    }
  },
  onUnload() {
    this.stopPolling()
    this.clearLockCountdown()
  },
  computed: {
    pollCountDisplay() {
      if (this.pollCount === 0) return ''
      return `（已等待 ${this.pollCount * 5} 秒）`
    },
    // 是否显示预订倒计时（仅选房流程 + 有 lockExpireTime 时显示）
    showLockCountdown() {
      return !!(this.lockExpireTime && this.orderNo)
    },
    // 剩余不足2分钟 → 紧急状态
    countdownUrgent() {
      return this.countdownSeconds > 0 && this.countdownSeconds < 120
    },
    // 倒计时已结束
    countdownExpired() {
      return this.showLockCountdown && this.countdownSeconds <= 0
    }
  },
  methods: {
    onRentMonthsChange(e) {
      this.rentMonthsIndex = e.detail.value
      const m = this.rentMonthsOptions[this.rentMonthsIndex].value
      this.rentMonths = m
      const start = new Date()
      start.setDate(start.getDate() + 3)
      this.startDate = start.toISOString().split('T')[0]
      const end = new Date(start)
      end.setMonth(end.getMonth() + m)
      this.endDate = end.toISOString().split('T')[0]
    },

    async init() {
      this.loading = true
      try {
        if (this.contractId) {
          await this.callInitSign()
          return
        }
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
        // URL 参数优先；API 返回作为备用
        if (!this.houseCode) {
          this.houseCode = res.data.houseCode || res.data.roomNo || res.data.roomNumber || res.data.houseNo || ''
        }
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

    async handleConfirmSign() {
      if (this.submitting) return
      this.submitting = true
      uni.showLoading({ title: '正在发起签署...' })
      try {
        if (!this.contractId) {
          let saveRes
          if (this.isRenew && this.oldContractId) {
            // 续租模式
            saveRes = await renewContract({
              oldContractId: this.oldContractId,
              houseId: this.roomId,
              projectId: this.projectId,
              templateId: this.templateId,
              contractContent: this.contractContent,
              endDate: this.endDate,
              rentMonths: this.rentMonths,
              userId: this.userId
            })
          } else {
            // 新签模式
            saveRes = await signContract({
              houseId: this.roomId,
              projectId: this.projectId,
              templateId: this.templateId,
              contractContent: this.contractContent,
              endDate: this.endDate,
              rentMonths: this.rentMonths,
              userId: this.userId,
              orderNo: this.orderNo
            })
          }
          if (saveRes.code !== 200) throw new Error(saveRes.msg || '保存合同失败')
          this.contractId = saveRes.data.contractId
          uni.setStorageSync('esign_contractId', this.contractId)
          uni.setStorageSync('esign_userId', this.userId)
        }
        await this.callInitSign()
      } catch (e) {
        this.step = 'error'
        this.errorMsg = e.message || '操作失败'
      } finally {
        this.submitting = false
        uni.hideLoading()
      }
    },

    async callInitSign() {
      this.loading = true
      try {
        let platform = 'h5'
        // #ifdef MP-WEIXIN
        platform = 'mp'
        // #endif
        const res = await initSign(this.contractId, this.userId, platform)
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

    goAuth() {
      if (!this.authUrl) return
      this.fromEsignWebview = true
      this.step = 'auth_waiting'
      // #ifdef MP-WEIXIN
      uni.navigateTo({
        url: '/pages/auth/esign-webview?url=' + encodeURIComponent(this.authUrl)
      })
      // #endif
      // #ifdef H5
      window.open(this.authUrl, '_blank')
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

    goEsign() {
      if (!this.signUrl) return
      this.step = 'esign_waiting'
      // #ifdef MP-WEIXIN
      this.fromEsignWebview = true
      uni.navigateTo({
        url: '/pages/auth/esign-webview?url=' + encodeURIComponent(this.signUrl)
      })
      // #endif
      // #ifdef H5
      window.location.href = this.signUrl
      this.startPolling()
      // #endif
    },

    startPolling() {
      this.stopPolling()
      this.pollCount = 0
      this.pollTimer = setInterval(async () => {
        this.pollCount++
        if (this.pollCount >= 72) { this.stopPolling(); return } // 最多等 6 分钟
        try {
          const res = await checkSign(this.contractId)
          if (res.code === 200 && res.data && res.data.signed) {
            this.stopPolling()
            this.step = 'done'
            setTimeout(() => { this.goToBill() }, 2000)
          }
        } catch (e) { /* ignore */ }
      }, 5000)
    },

    stopPolling() {
      if (this.pollTimer) { clearInterval(this.pollTimer); this.pollTimer = null }
    },

    // ========== 从 esign-webview 返回后：延迟重试直查 e签宝 ==========
    pollAfterReturn() {
      let attempts = 0
      const maxAttempts = 5
      const tryCheck = async () => {
        attempts++
        uni.showLoading({ title: `确认签署结果(${attempts}/${maxAttempts})...` })
        try {
          const res = await checkSign(this.contractId)
          uni.hideLoading()
          if (res.code === 200 && res.data && res.data.signed) {
            this.stopPolling()
            this.step = 'done'
            setTimeout(() => { this.goToBill() }, 2000)
            return
          }
        } catch (e) {
          uni.hideLoading()
        }
        if (attempts < maxAttempts) {
          setTimeout(tryCheck, 3000)
        } else {
          uni.showToast({ title: '请点击"刷新签署状态"按钮重试', icon: 'none', duration: 3000 })
        }
      }
      setTimeout(tryCheck, 2000) // 先等 2 秒让 e签宝 处理
    },

    // ========== 手动刷新按钮：直查 e签宝 ==========
    async manualCheckStatus() {
      if (!this.contractId) {
        uni.showToast({ title: '合同信息丢失，请返回重试', icon: 'none' })
        return
      }
      uni.showLoading({ title: '查询签署状态...' })
      try {
        const res = await checkSign(this.contractId)
        uni.hideLoading()
        if (res.code === 200 && res.data && res.data.signed) {
          this.stopPolling()
          this.step = 'done'
          setTimeout(() => { this.goToBill() }, 2000)
        } else {
          uni.showToast({ title: '签署尚未完成，请稍候再试', icon: 'none' })
        }
      } catch (e) {
        uni.hideLoading()
        uni.showToast({ title: '查询失败', icon: 'none' })
      }
    },

    goToBill() {
      this.stopPolling()
      uni.removeStorageSync('esign_contractId')
      uni.removeStorageSync('esign_userId')
      uni.redirectTo({ url: '/subpkg/affairs/contract?esign_done=1' })
    },

    goBack() {
      this.stopPolling()
      this.clearLockCountdown()
      uni.redirectTo({ url: '/subpkg/affairs/contract' })
    },

    // ========== 预订倒计时 ==========
    startLockCountdown() {
      this.clearLockCountdown()
      const expireTs = new Date(Number(this.lockExpireTime)).getTime()
      if (isNaN(expireTs)) return
      const remaining = Math.floor((expireTs - Date.now()) / 1000)
      this.countdownSeconds = Math.max(0, remaining)
      this.formatCountdown()
      if (this.countdownSeconds <= 0) {
        this.onLockExpired()
        return
      }
      this.countdownTimer = setInterval(() => {
        this.countdownSeconds--
        this.formatCountdown()
        if (this.countdownSeconds <= 0) {
          this.clearLockCountdown()
          this.onLockExpired()
        }
      }, 1000)
    },

    formatCountdown() {
      const s = Math.max(0, this.countdownSeconds)
      const min = String(Math.floor(s / 60)).padStart(2, '0')
      const sec = String(s % 60).padStart(2, '0')
      this.countdown = `${min}:${sec}`
    },

    clearLockCountdown() {
      if (this.countdownTimer) {
        clearInterval(this.countdownTimer)
        this.countdownTimer = null
      }
    },

    onLockExpired() {
      uni.showModal({
        title: '预订已失效',
        content: '房源预订时间已到期，请重新选房',
        showCancel: false,
        confirmText: '返回选房',
        success: () => {
          uni.navigateBack()
        }
      })
    }
  }
}
</script>

<style scoped>
/* ─── 全局 ─── */
.page {
  min-height: 100vh;
  background: #F0F3F9;
  display: flex;
  flex-direction: column;
  font-family: "PingFang SC", "苹方-简", sans-serif;
}

/* ─── 加载 / 状态页面 ─── */
.state-screen {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 60rpx;
  min-height: 100vh;
}

/* 加载圈 */
.state-spinner {
  width: 100rpx;
  height: 100rpx;
  margin-bottom: 32rpx;
}
/* 签署等待圈（更大） */
.waiting-spinner {
  width: 140rpx;
  height: 140rpx;
  margin-bottom: 40rpx;
}
.waiting-spinner .spinner-ring {
  width: 140rpx;
  height: 140rpx;
  border-width: 8rpx;
}
.spinner-ring {
  width: 100rpx;
  height: 100rpx;
  border: 6rpx solid #E0E6F0;
  border-top-color: #2B6CB0;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

/* 状态圆圈 */
.state-circle {
  width: 140rpx;
  height: 140rpx;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 36rpx;
}
.state-circle.auth    { background: #EBF4FF; }
.state-circle.waiting { background: #FFF7E6; }
.state-circle.sign    { background: #EBF4FF; }
.state-circle.success { background: #E6F9F0; }
.state-circle.error   { background: #FDECEA; }
.state-emoji { font-size: 60rpx; }

.state-label { font-size: 30rpx; color: #8A96A8; }
.state-title {
  font-size: 38rpx;
  font-weight: 700;
  color: #1A2340;
  margin-bottom: 16rpx;
  text-align: center;
}
.state-desc {
  font-size: 28rpx;
  color: #7A8599;
  text-align: center;
  line-height: 1.7;
  margin-bottom: 56rpx;
  padding: 0 20rpx;
}

/* 按钮 */
.btn-main {
  width: 100%;
  height: 96rpx;
  background: linear-gradient(135deg, #2563EB, #1E40AF);
  color: #fff;
  border-radius: 16rpx;
  font-size: 32rpx;
  font-weight: 600;
  border: none;
  margin-bottom: 24rpx;
  box-shadow: 0 8rpx 24rpx rgba(37, 99, 235, 0.30);
}
.btn-ghost {
  width: 100%;
  height: 96rpx;
  background: #fff;
  color: #2563EB;
  border: 2rpx solid #BFDBFE;
  border-radius: 16rpx;
  font-size: 32rpx;
  font-weight: 500;
}

/* ─── 预览布局 ─── */
.preview-layout {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

/* Banner 头部 */
.banner {
  background: linear-gradient(145deg, #1E3A8A 0%, #2563EB 60%, #3B82F6 100%);
  padding: 48rpx 40rpx 52rpx;
  display: flex;
  flex-direction: column;
}
.banner-tag {
  align-self: flex-start;
  background: rgba(255, 255, 255, 0.18);
  color: #BFDBFE;
  font-size: 22rpx;
  font-weight: 500;
  letter-spacing: 2rpx;
  padding: 6rpx 18rpx;
  border-radius: 50rpx;
  border: 1rpx solid rgba(255, 255, 255, 0.25);
  margin-bottom: 20rpx;
}
.banner-title {
  font-size: 42rpx;
  font-weight: 700;
  color: #fff;
  margin-bottom: 10rpx;
}
.banner-project {
  font-size: 26rpx;
  color: rgba(255, 255, 255, 0.70);
  line-height: 1.5;
}

/* 滚动内容 */
.scroll-body {
  flex: 1;
  padding: 28rpx 28rpx 0;
  max-height: calc(100vh - 280rpx);
  box-sizing: border-box;
  width: 100%;
}
.scroll-bottom-gap { height: 32rpx; }

/* 信息卡片 */
.card {
  background: #fff;
  border-radius: 20rpx;
  padding: 32rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 12rpx rgba(30, 58, 138, 0.06);
  box-sizing: border-box;
  width: 100%;
}
.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 24rpx;
  padding-bottom: 20rpx;
  border-bottom: 1rpx solid #F0F3FA;
}
.card-dot {
  width: 8rpx;
  height: 32rpx;
  background: linear-gradient(180deg, #2563EB, #3B82F6);
  border-radius: 4rpx;
  margin-right: 16rpx;
}
.card-title {
  font-size: 30rpx;
  font-weight: 700;
  color: #1A2340;
}

/* 信息行 */
.row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18rpx 0;
  border-bottom: 1rpx solid #F7F8FC;
}
.row:last-child { border-bottom: none; }

.row-label {
  font-size: 28rpx;
  color: #8A96A8;
  flex-shrink: 0;
  width: 160rpx;
}
.row-value {
  font-size: 28rpx;
  color: #1A2340;
  font-weight: 500;
  flex: 1;
  text-align: right;
  padding-right: 0;
}
.row-value.price {
  color: #DC2626;
  font-weight: 700;
  font-size: 30rpx;
}
.row-value.room-no {
  color: #2563EB;
  font-weight: 700;
}
.row-value.renew-free {
  color: #16A34A;
  font-weight: 600;
  font-size: 28rpx;
}
.row-value.date-range {
  font-size: 26rpx;
  color: #4A5568;
}

/* 租期选择器行 */
.row-picker {
  align-items: center;
}
.picker-btn {
  display: flex;
  align-items: center;
  gap: 6rpx;
  background: #F0F5FF;
  border: 1rpx solid #BFDBFE;
  border-radius: 12rpx;
  padding: 10rpx 20rpx 10rpx 24rpx;
}
.picker-val {
  font-size: 28rpx;
  color: #2563EB;
  font-weight: 600;
}
.picker-arrow {
  font-size: 32rpx;
  color: #60A5FA;
  font-weight: 300;
  margin-top: -2rpx;
}

/* 提示框 */
.notice-box {
  background: #FFFBEB;
  border: 1rpx solid #FCD34D;
  border-radius: 16rpx;
  padding: 24rpx 28rpx;
  display: flex;
  align-items: flex-start;
  gap: 16rpx;
  margin-bottom: 0;
  box-sizing: border-box;
  width: 100%;
}
.notice-icon { font-size: 32rpx; flex-shrink: 0; margin-top: 2rpx; }
.notice-text {
  font-size: 26rpx;
  color: #92400E;
  line-height: 1.7;
  flex: 1;
}

/* 底部按钮区 */
.footer {
  padding: 24rpx 28rpx;
  padding-bottom: calc(24rpx + env(safe-area-inset-bottom));
  background: #fff;
  border-top: 1rpx solid #EEF1F8;
  box-shadow: 0 -4rpx 20rpx rgba(30, 58, 138, 0.06);
}
.btn-sign {
  width: 100%;
  height: 100rpx;
  background: linear-gradient(135deg, #2563EB, #1E40AF);
  color: #fff;
  border-radius: 20rpx;
  font-size: 34rpx;
  font-weight: 700;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
  box-shadow: 0 10rpx 28rpx rgba(37, 99, 235, 0.35);
  letter-spacing: 2rpx;
}
.btn-sign.disabled { opacity: 0.55; }
.btn-sign-icon { font-size: 34rpx; }

/* ─── 预订倒计时条 ─── */
.countdown-bar {
  background: linear-gradient(135deg, #FF6B00, #FF9500);
  padding: 16rpx 28rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}
.countdown-bar-urgent {
  background: linear-gradient(135deg, #DC2626, #EF4444);
}
.countdown-bar-expired {
  background: #991B1B;
}
.countdown-bar-inner {
  display: flex;
  align-items: center;
  gap: 10rpx;
}
.countdown-bar-icon {
  font-size: 28rpx;
}
.countdown-bar-text {
  color: #fff;
  font-size: 28rpx;
  font-weight: 600;
  letter-spacing: 1rpx;
}
@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.4; }
}
.countdown-blink {
  animation: blink 1s ease-in-out infinite;
}
</style>
