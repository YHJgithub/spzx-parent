<template>
  <div class="app-container">

    <!-- 搜索表单 -->
    <el-form ref="queryRef" :inline="true" :model="queryParams" v-show="showSearch" label-width="68px">
      <el-form-item label="品牌名称" prop="name">
        <el-input
            placeholder="请输入品牌名称"
            clearable
            v-model="queryParams.name"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>

    </el-form>

    <!-- 功能按钮栏 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['product:brand:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['product:brand:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['product:brand:remove']"
        >删除
        </el-button>
      </el-col>
      <!-- 功能按钮栏 -->
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>

    </el-row>

    <!-- 数据展示表格 -->
    <el-table :data="brandList" v-loading="loading" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="品牌名称" prop="name" width="200"/>
      <el-table-column label="品牌图标" prop="logo" #default="scope">
        <img :src="scope.row.logo" width="50"/>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)"
                     v-hasPermi="['product:brand:edit']">修改
          </el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)"
                     v-hasPermi="['product:brand:remove']">删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页条组件 -->
    <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 新增或修改分类品牌对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="brandRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="品牌名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入品牌名称"/>
        </el-form-item>
        <el-form-item label="品牌图标" prop="logo">
          <el-upload
              class="avatar-uploader"
              :action="imgUpload.url"
              :headers="imgUpload.headers"
              :show-file-list="false"
              :on-success="handleAvatarSuccess"
              :before-upload="beforeUpload"
          >
            <img v-if="form.logo" :src="form.logo" class="avatar"/>
            <el-icon v-else class="avatar-uploader-icon">
              <Plus/>
            </el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>

  </div>
</template>

<script setup name="Brand">
//===============导入区域=================
import {listBrand, addBrand, getBrand, updateBrand, deleteById} from "@/api/product/brand.js"
import {getToken} from "@/utils/auth.js";
import {ElMessage} from "element-plus";

const {proxy} = getCurrentInstance();


//================变量区域================

//定义分页列表数据模型
const brandList = ref([]);
//定义列表总记录数模型
const total = ref(2);
//加载数据效果
const loading = ref(false)
//新增与修改弹出层标题模型
const title = ref('')
//新增与修改弹出层控制模型
const open = ref(false)

const ids = ref([])
const multiple = ref(true)
const single = ref(true)

const showSearch = ref(true)

const date = reactive({
  queryParams: {
    name: null,
    pageNum: 1,
    pageSize: 5
  },
  form: {
    id: null,
    name: null,
    logo: null
  },
  imgUpload: {
    // 设置上传的请求头部
    headers: {
      Authorization: "Bearer " + getToken()
    },
    // 图片上传的方法地址:
    url: import.meta.env.VITE_APP_BASE_API + "/file/upload"
  },
  rules: {
    name: [{required: true, message: "品牌名称不能为空", trigger: "blur"}],
    logo: [{required: true, message: "品牌LOGO不能为空", trigger: "blur"}],
  }
})

const {queryParams, form, imgUpload, rules} = toRefs(date)


//==================事件处理区===================
//分页列表
function getList() {
  loading.value = true
  listBrand(queryParams.value).then(result => {
    brandList.value = result.rows
    total.value = result.total
    loading.value = false
  })
}

//搜索
function handleQuery() {
  getList()
}

//列表重置
function resetQuery() {
  /*queryParams.value.name = null
  queryParams.value.pageNum = 1
  queryParams.value.pageSize = 5*/
  proxy.resetForm("queryRef")
  getList()
}

getList()

//新增按钮操作
function handleAdd() {
  reset()
  open.value = true
  title.value = '添加品牌'
}

//修改按钮操作
function handleUpdate(row) {
  const _id = row.id || ids.value
  getBrand(_id).then(result => {
    form.value = result.data
    open.value = true
    title.value = '修改品牌'
  })
}

// 表单重置
function reset() {
  /*form.value.id = null
  form.value.name = null
  form.value.logo = null*/
  proxy.resetForm("brandRef")
}

//上传
function handleAvatarSuccess(result, uploadFile) {
  form.value.logo = result.data.url
  proxy.$refs['brandRef'].clearValidate('logo')
}

const beforeUpload = (rawFile) => {
  if (rawFile.type !== 'image/jpeg') {
    ElMessage.error('图片必须是JPG格式!')
    return false
  } else if (rawFile.size / 1024 / 1024 > 1) {
    ElMessage.error('图片不能大于 1MB!')
    return false
  }
  return true
}

//提交按钮
function submitForm() {
  proxy.$refs["brandRef"].validate(valid => {
    if (valid) {
      if (form.value.id) {
        updateBrand(form.value).then(result => {
          ElMessage.success('修改成功')
          open.value = false
          getList()
        })
      } else {
        addBrand(form.value).then(result => {
          ElMessage.success('新增成功')
          open.value = false
          getList()
        })
      }
    }
  })
}

//取消按钮
function cancel() {
  reset()
  open.value = false
}

//删除按钮
function handleDelete(row) {
  const _ids = row.id || ids.value
  proxy.$modal.confirm("您是否确认删除此条信息")
      .then(() => deleteById(_ids))
      .then(() => {
        getList()
        proxy.$modal.msgSuccess("删除成功")
      })
      .catch(() => {
        getList()
        proxy.$modal.msgWarning("删除失败")
      })
}

function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.id)
  multiple.value = !selection.length
  single.value = selection.length != 1
}

</script>

<style scoped>
.avatar-uploader .avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  text-align: center;
}
</style>