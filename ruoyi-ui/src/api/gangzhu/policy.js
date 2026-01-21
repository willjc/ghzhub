import request from '@/utils/request'

// 查询政策文件列表
export function listPolicy(query) {
  return request({
    url: '/gangzhu/policy/list',
    method: 'get',
    params: query
  })
}

// 查询政策文件详细
export function getPolicy(policyId) {
  return request({
    url: '/gangzhu/policy/' + policyId,
    method: 'get'
  })
}

// 新增政策文件
export function addPolicy(data) {
  return request({
    url: '/gangzhu/policy',
    method: 'post',
    data: data
  })
}

// 修改政策文件
export function updatePolicy(data) {
  return request({
    url: '/gangzhu/policy',
    method: 'put',
    data: data
  })
}

// 删除政策文件
export function delPolicy(policyId) {
  return request({
    url: '/gangzhu/policy/' + policyId,
    method: 'delete'
  })
}

// 增加浏览次数
export function increaseViewCount(policyId) {
  return request({
    url: '/gangzhu/policy/view/' + policyId,
    method: 'post'
  })
}
