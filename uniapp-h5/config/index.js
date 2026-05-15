/**
 * UniApp 统一配置文件
 * 用于管理不同环境的API地址和其他配置
 */

// 开发环境配置
const development = {
  // API 基础地址（后端服务地址）
  baseUrl: 'http://ghzapi.dayushaiwang.com',

  // 上传文件地址（通常与 baseUrl 相同）
  uploadUrl: 'http://ghzapi.dayushaiwang.com',

  // 静态资源地址（用于访问已上传的文件）
  staticUrl: 'http://ghzapi.dayushaiwang.com',

  // H5 前端部署域名（用于小程序 web-view 加载自带 HTML，如 /static/vr-viewer.html）
  // 开发环境留空即可，H5 下会自动走 window.location.origin
  h5FrontendUrl: '',

  // 其他配置
  timeout: 30000, // 请求超时时间（毫秒）

  // 郑好办配置
  zhbModuleId: '413780', // 郑好办应用ID（正式）
}

// 生产环境配置
const production = {
  baseUrl: 'https://api.caigon.cn',
  uploadUrl: 'https://api.caigon.cn',
  staticUrl: 'https://api.caigon.cn',
  // H5 前端部署域名（小程序 web-view 需要完整 https URL，域名必须在业务域名白名单里）
  h5FrontendUrl: 'https://app.caigon.cn',
  timeout: 30000,

  // 郑好办配置
  zhbModuleId: '413780', // 郑好办应用ID（正式）
}

// 测试环境配置（本地开发，后端8090端口）
const test = {
  baseUrl: 'http://192.168.0.115:8090',
  uploadUrl: 'http://192.168.0.115:8090',
  staticUrl: 'http://192.168.0.115:8090',
  h5FrontendUrl: '',
  timeout: 30000,

  // 郑好办配置
  zhbModuleId: '413780', // 郑好办应用ID（正式）
}

// 根据环境变量选择配置
// UniApp 没有 process.env.NODE_ENV，需要手动切换
const env = 'production' // 手动切换：development | test | production

const config = {
  development,
  production,
  test
}

export default config[env]
