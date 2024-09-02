<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="关键字" prop="username">
        <el-input
            v-model="queryParams.username"
            placeholder="用户名称、姓名、手机号"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>

      <el-form-item label="创建时间" style="width: 308px;">
        <el-date-picker
            v-model="dateRange"
            value-format="YYYY-MM-DD"
            type="daterange"
            range-separator="-"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>


      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['user:userInfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userInfoList">
      <el-table-column label="用户名" align="center" prop="username" />
      <el-table-column label="昵称" align="center" prop="nickName" />
      <el-table-column label="头像" prop="avatar" #default="scope">
        <img :src="scope.row.avatar" width="50"/>
      </el-table-column>
      <el-table-column prop="sex" label="性别" #default="scope">
        {{ scope.row.sex == 0 ? '女' : '男' }}
      </el-table-column>
      <el-table-column label="电话号码" align="center" prop="phone" />
      <el-table-column label="最后一次登录时间" align="center" prop="lastLoginTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.lastLoginTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" #default="scope">
        {{ scope.row.status == 0 ? '禁止' : '正常' }}
      </el-table-column>

      <el-table-column label="创建时间" align="center" prop="createTime"  width="160">
        <template #default="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleShow(scope.row)" v-hasPermi="['user:userInfo:edit']">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />



    <!-- 添加或修改会员对话框 -->
    <el-dialog :title="title" v-model="open" width="50%" append-to-body>
      <el-form ref="userInfoRef" :model="form" :rules="rules" label-width="120px">
        <el-divider />
        <span style="margin-bottom: 5px;">基本信息</span>
        <el-row>
          <el-col :span="12">
            <el-form-item label="头像">
              <img :src="form.avatar" width="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="用户名">
              {{ form.username }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="昵称">
              {{ form.nickName }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别">
              {{ form.sex == 1 ? '女' : '男' }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="电话号码">
              {{ form.phone }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="备注">
              {{ form.memo }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最后一次登录ip">
              {{ form.lastLoginIp }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最后一次登录时间">
              {{ form.lastLoginTime }}
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="状态">
              {{ form.status == 1 ? '正常' : '停用' }}
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间">
              {{ form.createTime }}
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider />
        <span style="margin-bottom: 5px;">地址信息</span>
        <el-table :data="userAddressList" style="width: 100%">
          <el-table-column prop="tagName" label="地址标签" width="80" />
          <el-table-column
              prop="isDefault"
              label="是否默认"
              #default="scope"
              width="100"
          >
            {{ scope.row.isDefault == 1 ? '是' : '否' }}
          </el-table-column>
          <el-table-column prop="isDefault" label="详细详细" #default="scope">
            {{ scope.row.name }} | {{ scope.row.phone }} |
            {{ scope.row.fullAddress }}
          </el-table-column>
          <el-table-column label="创建时间" align="center" prop="createTime"  width="160">
            <template #default="scope">
              <span>{{ parseTime(scope.row.createTime) }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>


  </div>
</template>

<script setup name="UserInfo">
import { listUserInfo, getUserAddress } from "@/api/user/userInfo";

const { proxy } = getCurrentInstance();

const userInfoList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);

const total = ref(0);
const title = ref("");

const dateRange = ref([])

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    username: null,

  },
  rules: {
    createTime: [
      {required: true, message: "创建时间不能为空", trigger: "blur"}
    ],
    updateTime: [
      {required: true, message: "更新时间不能为空", trigger: "blur"}
    ],

  }
});

const {queryParams, rules, form} = toRefs(data);

/** 查询会员列表 */
function getList() {
  loading.value = true;
  listUserInfo(proxy.addDateRange(queryParams.value, dateRange.value)).then(response => {
    userInfoList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}


/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  dateRange.value = [];
  proxy.resetForm("queryRef");
  handleQuery();
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('user/userInfo/export', {
    ...queryParams.value
  }, `userInfo_${new Date().getTime()}.xlsx`)
}

getList();


/** 显示操作 */
const userAddressList = ref([])

function handleShow(row) {
  form.value = row; //偷懒做法，直接用row数据进行回显
  open.value = true;
  title.value = "查看会员";

  getUserAddress(row.id).then(response => {
    userAddressList.value = response.data
  })
}


// 取消按钮
function cancel() {
  open.value = false;
  form.value = {}
}

</script>
