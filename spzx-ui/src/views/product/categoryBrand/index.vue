<template>
  <div class="app-container">

    <el-form ref="queryRef" :inline="true" label-width="68px">
      <el-form-item label="分类">
        <el-cascader
            :props="categoryProps"
            style="width: 100%"
        />
      </el-form-item>

      <el-form-item label="品牌">
        <el-select
            class="m-2"
            placeholder="选择品牌"
            size="small"
            style="width: 100%"
        >
          <el-option
              v-for="item in brandList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search">搜索</el-button>
        <el-button icon="Refresh" id="reset-all">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
        >删除
        </el-button>
      </el-col>
      <right-toolbar></right-toolbar>
    </el-row>

    <!-- 数据展示表格 -->
    <el-table v-loading="loading" :data="categoryBrandList">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="分类名称" prop="categoryName"/>
      <el-table-column label="品牌名称" prop="brandName"/>
      <el-table-column prop="logo" label="品牌图标" #default="scope">
        <img :src="scope.row.logo" width="50"/>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit">修改</el-button>
          <el-button link type="primary" icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页条组件 -->
    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

  </div>
</template>

<script setup name="CategoryBrand">
import {listCategoryBrand} from "@/api/product/categoryBrand";

//定义隐藏搜索控制模型
const showSearch = ref(true);

const loading = ref(true)

const categoryBrandList = ref([])

const total = ref(0)

const data = reactive({
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    brandId: null,
    categoryId: null
  }
})

const queryParams = toRefs(data)

function getList() {
  loading.value = true
  listCategoryBrand(data).then(response => {
    categoryBrandList.value = response.rows
    total.value = response.total
    loading.value = false
  });
}

getList()
</script>