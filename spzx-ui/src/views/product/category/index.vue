<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Upload"
        >导入
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
        >导出
        </el-button>
      </el-col>
      <right-toolbar></right-toolbar>
    </el-row>

    <el-table
        v-loading="loading"
        :data="categoryList"
        style="width: 100%"
        row-key="id"
        border
        lazy
        :load="fetchData"
        :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
    >
      <el-table-column prop="name" label="分类名称"/>
      <el-table-column prop="imageUrl" label="图标" #default="scope">
        <img :src="scope.row.imageUrl" width="50"/>
      </el-table-column>
      <el-table-column prop="orderNum" label="排序"/>
      <el-table-column prop="status" label="状态" #default="scope">
        {{ scope.row.status == 1 ? '正常' : '停用' }}
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间"/>
    </el-table>
  </div>
</template>

<script setup name="Category">

import {getTreeSelect} from '@/api/product/category'

const categoryList = ref([])
const loading = ref(true)

/** 查询商品分类列表 */
function getList(id) {
  loading.value = true
  getTreeSelect(id).then(response => {
    categoryList.value = response.data
    loading.value = false
  });
}

// 数据列表
const fetchData = async (row, treeNode, resolve) => {
  const {data} = await getTreeSelect(row.id)
  resolve(data)
}

getList(0)
</script>