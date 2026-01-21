/**
 * UniApp 统一配置文件
 * 用于管理不同环境的API地址和其他配置
 */

// 开发环境配置
const development = {
  // API 基础地址（后端服务地址）
  baseUrl: 'http://localhost:8090',

  // 上传文件地址（通常与 baseUrl 相同）
  uploadUrl: 'http://localhost:8090',

  // 静态资源地址（用于访问已上传的文件）
  staticUrl: 'http://localhost:8090',

  // 其他配置
  timeout: 30000, // 请求超时时间（毫秒）
}

// 生产环境配置
const production = {
  baseUrl: 'https://api.yourdomain.com',
  uploadUrl: 'https://api.yourdomain.com',
  staticUrl: 'https://api.yourdomain.com',
  timeout: 30000,
}

// 测试环境配置
const test = {
  baseUrl: 'http://test-api.yourdomain.com',
  uploadUrl: 'http://test-api.yourdomain.com',
  staticUrl: 'http://test-api.yourdomain.com',
  timeout: 30000,
}

// 根据环境变量选择配置
// UniApp 没有 process.env.NODE_ENV，需要手动切换
const env = 'development' // 手动切换：development | test | production

const config = {
  development,
  production,
  test
}

export default config[env]
