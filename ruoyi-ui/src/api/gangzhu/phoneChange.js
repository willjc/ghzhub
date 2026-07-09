import request from '@/utils/request'

// 预览更换手机号
export function previewChange(oldPhone, newPhone) {
  return request({
    url: '/system/phoneChange/preview',
    method: 'get',
    params: { oldPhone, newPhone }
  })
}

// 执行更换手机号
export function executeChange(data) {
  return request({
    url: '/system/phoneChange/execute',
    method: 'post',
    data: data
  })
}
