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

  // 其他配置
  timeout: 30000, // 请求超时时间（毫秒）

  // 郑好办配置
  zhbModuleId: '413780', // 郑好办应用ID（正式）
}

// 生产环境配置
const production = {
  baseUrl: 'http://ghzapi.dayushaiwang.com',
  uploadUrl: 'http://ghzapi.dayushaiwang.com',
  staticUrl: 'http://ghzapi.dayushaiwang.com',
  timeout: 30000,

  // 郑好办配置
  zhbModuleId: '413780', // 郑好办应用ID��正式）
}

// 测试环境配置（本地开发，后端8090端口）
const test = {
  baseUrl: 'http://192.168.31.146:8090',
  uploadUrl: 'http://192.168.31.146:8090',
  staticUrl: 'http://192.168.31.146:8090',
  timeout: 30000,

  // 郑好办配置
  zhbModuleId: '413780', // 郑好办应用ID（正式）
}

// 根据环境变量选择配置
// UniApp 没有 process.env.NODE_ENV，需要手动切换
const env = 'test' // 手动切换：development | test | production

const config = {
  development,
  production,
  test
}

export default config[env]
