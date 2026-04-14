<template>
  <div class="login-container">
    <!-- 左侧品牌展示区 -->
    <div class="login-brand">
      <div class="brand-content">
        <div class="brand-decoration">
          <div class="circle circle-1"></div>
          <div class="circle circle-2"></div>
          <div class="circle circle-3"></div>
        </div>
        <h1 class="brand-title">{{title}}</h1>
        <p class="brand-desc">智慧住房租赁管理平台</p>
      </div>
    </div>
    <!-- 右侧登录区 -->
    <div class="login-panel">
      <div class="login-form-wrapper">
        <h3 class="login-title">欢迎登录</h3>
        <p class="login-subtitle">请输入您的账号信息</p>
        <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <div style="float: right;" v-if="register">
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login"
import Cookies from "js-cookie"
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      title: process.env.VUE_APP_TITLE,
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaEnabled: true,
      // 注册开关
      register: false,
      redirect: undefined
    }
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect
      },
      immediate: true
    }
  },
  created() {
    this.getCode()
    this.getCookie()
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.loginForm.uuid = res.uuid
        }
      })
    },
    getCookie() {
      const username = Cookies.get("username")
      const password = Cookies.get("password")
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      }
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 })
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 })
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 })
          } else {
            Cookies.remove("username")
            Cookies.remove("password")
            Cookies.remove('rememberMe')
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
// ===== 整体布局 =====
.login-container {
  display: flex;
  height: 100%;
  min-height: 100vh;
}

// ===== 左侧品牌区 =====
.login-brand {
  flex: 0 0 55%;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.brand-content {
  text-align: center;
  z-index: 2;
  position: relative;
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  color: #ffffff;
  margin-bottom: 16px;
  letter-spacing: 4px;
}

.brand-desc {
  font-size: 18px;
  color: rgba(255, 255, 255, 0.7);
  letter-spacing: 2px;
}

// CSS装饰圆形光晕
.brand-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;

  .circle {
    position: absolute;
    border-radius: 50%;
    opacity: 0.08;
    background: #ffffff;
  }

  .circle-1 {
    width: 400px;
    height: 400px;
    top: -100px;
    right: -100px;
  }

  .circle-2 {
    width: 300px;
    height: 300px;
    bottom: -80px;
    left: -60px;
  }

  .circle-3 {
    width: 200px;
    height: 200px;
    top: 50%;
    left: 60%;
    opacity: 0.05;
  }
}

// ===== 右侧登录区 =====
.login-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #ffffff;
  padding: 40px;
}

.login-form-wrapper {
  width: 100%;
  max-width: 380px;
}

.login-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.login-subtitle {
  font-size: 14px;
  color: #909399;
  margin-bottom: 36px;
}

// ===== 登录表单 =====
.login-form {
  background: transparent;
  padding: 0;
  width: 100%;

  .el-input {
    height: 44px;

    input {
      height: 44px;
      border-radius: 8px;
      font-size: 14px;

      &:focus {
        border-color: #1890ff;
        box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.15);
      }
    }
  }

  .el-form-item {
    margin-bottom: 24px;
  }

  .input-icon {
    height: 44px;
    width: 16px;
    margin-left: 4px;
  }

  .el-button--primary {
    height: 46px;
    border-radius: 8px;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 4px;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    border: none;
    transition: all 0.3s ease;

    &:hover, &:focus {
      background: linear-gradient(135deg, #40a9ff 0%, #1890ff 100%);
      box-shadow: 0 4px 12px rgba(24, 144, 255, 0.4);
      transform: translateY(-1px);
    }

    &:active {
      transform: translateY(0);
    }
  }

  // 记住密码复选框
  .el-checkbox {
    color: #606266;
  }
}

// ===== 验证码 =====
.login-code {
  width: 33%;
  height: 44px;
  float: right;

  img {
    cursor: pointer;
    vertical-align: middle;
    border-radius: 6px;
    border: 1px solid #e4e7ed;
    transition: border-color 0.3s;

    &:hover {
      border-color: #1890ff;
    }
  }
}

.login-code-img {
  height: 44px;
}

// ===== 底部版权 =====
.el-login-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}

// ===== 响应式 =====
@media (max-width: 992px) {
  .login-brand {
    flex: 0 0 45%;
  }
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
  }

  .login-brand {
    display: none;
  }

  .login-panel {
    min-height: 100vh;
    padding: 20px;
  }
}
</style>
