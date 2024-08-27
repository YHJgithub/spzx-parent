<template>
  <div class="app-container">

    <!-- 搜索表单 -->
    <el-form ref="queryRef" :inline="true" label-width="68px">
      <el-form-item label="品牌名称" prop="name">
        <el-input
            placeholder="请输入品牌名称"
            clearable
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search">搜索</el-button>
        <el-button icon="Refresh">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 功能按钮栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
        >删除</el-button>
      </el-col>
    </el-row>

    <!-- 数据展示表格 -->
    <el-table  v-loading="loading" :data="brandList">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="品牌名称" prop="name" width="200"/>
      <el-table-column label="品牌图标" prop="logo" #default="scope">
        <img :src="scope.row.logo" width="50" />
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit">修改</el-button>
          <el-button link type="primary" icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页条组件 -->
    <!--    <pagination-->
    <!--        v-show="total > 0"-->
    <!--        :total="total"-->
    <!--    />-->
    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />
  </div>
</template>

<script setup name="Brand">
//引入api接口
import { listBrand } from "@/api/product/brand";

//定义分页列表数据模型
const brandList = ref([]);
//定义列表总记录数模型
const total = ref(0);
//加载数据时显示的动效控制模型
const loading = ref(true);

//Vue 3 中的两种响应式数据绑定方式：reactive 和 ref
//ref定义：基本数据类型，适用于简单的响应式数据
//reactive定义：对象（或数组）数据类型，则适用于复杂对象或数组的响应式数据
const data = reactive({
  //定义搜索模型
  queryParams: {
    pageNum: 1,
    pageSize: 10
  }
});
//使用 Vue.js 的 Composition API 中的 toRefs 函数将 data 对象的属性转换为响应式引用。queryParams 是一个响应式引用，它指向 data 对象中的 queryParams 属性。这样，当 queryParams 的值发生变化时，任何依赖于它的组件都将自动更新。
const { queryParams } = toRefs(data);

/** 查询品牌列表 */
function getList() {
  loading.value = true;
  listBrand(queryParams.value).then(response => {
    brandList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

//执行查询品牌列表
getList()
</script>