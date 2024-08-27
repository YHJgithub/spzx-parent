import request from '@/utils/request'

// 查询商品单位列表
export function listProductUnit(query) {
    return request({
        url: '/product/productUnit/list',
        method: 'get',
        params: query
    })
}