import request from '@/utils/request'

// 查询品牌列表
export function listBrand(query) {
    return request({
        url: '/product/brand/list',
        method: 'get',
        params: query
    })
}