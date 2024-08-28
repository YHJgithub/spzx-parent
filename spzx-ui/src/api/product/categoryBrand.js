import request from '@/utils/request'

// 查询分类品牌列表
export function listCategoryBrand(query) {
    return request({
        url: '/product/categoryBrand/list',
        method: 'get',
        params: query
    })
}