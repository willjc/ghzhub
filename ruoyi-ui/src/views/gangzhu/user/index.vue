<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="姓名" prop="realName">
        <el-input
          v-model="queryParams.realName"
          placeholder="请输入姓名"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="来源类型" prop="sourceType">
        <el-select v-model="queryParams.sourceType" placeholder="请选择来源类型" clearable style="width: 150px">
          <el-option
            v-for="dict in dict.type.hz_user_source_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="注册时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:user:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="userList">
      <el-table-column label="用户ID" align="center" prop="userId" width="80" />
      <el-table-column label="手机号（登录账号）" align="center" prop="phone" min-width="130" show-overflow-tooltip />
      <el-table-column label="联系电话" align="center" prop="contactPhone" min-width="120" show-overflow-tooltip />
      <el-table-column label="昵称" align="center" prop="nickname" width="120" show-overflow-tooltip />
      <el-table-column label="真实姓名" align="center" prop="realName" width="100" />
      <el-table-column label="性别" align="center" prop="gender" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_user_sex" :value="scope.row.gender"/>
        </template>
      </el-table-column>
      <el-table-column label="来源类型" align="center" prop="sourceType" width="110">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hz_user_source_type" :value="scope.row.sourceType"/>
        </template>
      </el-table-column>
      <el-table-column label="第三方ID" align="center" min-width="150" show-overflow-tooltip>
        <template slot-scope="scope">
          <span v-if="scope.row.sourceType === '1'">{{ scope.row.wechatOpenid }}</span>
          <span v-else-if="scope.row.sourceType === '2'">{{ scope.row.zhaohaoUserId }}</span>
          <span v-else style="color: #909399">-</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="注册时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最后登录" align="center" prop="lastLoginTime" width="160">
        <template slot-scope="scope">
          <span v-if="scope.row.lastLoginTime">{{ parseTime(scope.row.lastLoginTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          <span v-else style="color: #909399">未登录</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['gangzhu:user:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:user:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:user:remove']"
            style="color: #F56C6C"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 用户详情对话框 -->
    <el-dialog title="用户详情" :visible.sync="open" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户ID">{{ userData.userId }}</el-descriptions-item>
        <el-descriptions-item label="手机号（登录账号）">{{ userData.phone }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">
          {{ userData.contactPhone || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="昵称">{{ userData.nickname }}</el-descriptions-item>
        <el-descriptions-item label="真实姓名">{{ userData.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          <dict-tag :options="dict.type.sys_user_sex" :value="userData.gender"/>
        </el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ userData.idCard }}</el-descriptions-item>
        <el-descriptions-item label="学历">
          <dict-tag :options="dict.type.hz_education_type" :value="userData.education" v-if="userData.education"/>
          <span v-else style="color: #909399">未填写</span>
        </el-descriptions-item>
        <el-descriptions-item label="身份类型">
          <dict-tag :options="dict.type.hz_identity_type" :value="userData.identityType" v-if="userData.identityType"/>
          <span v-else style="color: #909399">未填写</span>
        </el-descriptions-item>
        <el-descriptions-item label="工作单位">
          {{ userData.workUnit || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="单位联系方式">
          {{ userData.unitContact || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="配偶姓名">
          {{ userData.spouseName || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="工作证明附件" :span="2">
          <template v-if="userData.workProofAttachment">
            <el-image
              :src="getImageUrl(userData.workProofAttachment)"
              :preview-src-list="[getImageUrl(userData.workProofAttachment)]"
              style="width: 100px; height: 100px; cursor: pointer"
              fit="cover"
            />
            <el-button
              size="mini"
              type="text"
              icon="el-icon-download"
              @click="downloadAttachment(userData.workProofAttachment)"
              style="margin-left: 10px"
            >下载</el-button>
          </template>
          <span v-else style="color: #909399">未上传</span>
        </el-descriptions-item>
        <el-descriptions-item label="来源类型">
          <dict-tag :options="dict.type.hz_user_source_type" :value="userData.sourceType"/>
        </el-descriptions-item>
        <el-descriptions-item label="微信OpenID" v-if="userData.sourceType === '1'">
          {{ userData.wechatOpenid }}
        </el-descriptions-item>
        <el-descriptions-item label="微信UnionID" v-if="userData.sourceType === '1'">
          {{ userData.wechatUnionid }}
        </el-descriptions-item>
        <el-descriptions-item label="郑好办用户ID" v-if="userData.sourceType === '2'">
          {{ userData.zhaohaoUserId }}
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="userData.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">停用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="实名认证状态">
          <el-tag v-if="userData.authStatus === '2'" type="success" size="small">已实名认证</el-tag>
          <el-tag v-else-if="userData.authStatus === '1'" type="warning" size="small">已填写资料</el-tag>
          <el-tag v-else type="info" size="small">未认证</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="认证时间">
          <span v-if="userData.authTime">{{ parseTime(userData.authTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          <span v-else style="color: #909399">-</span>
        </el-descriptions-item>
        <el-descriptions-item label="头像" :span="2">
          <el-image
            v-if="userData.avatar"
            :src="getImageUrl(userData.avatar)"
            style="width: 100px; height: 100px"
            fit="cover"
          />
          <span v-else style="color: #909399">无头像</span>
        </el-descriptions-item>
        <el-descriptions-item label="注册时间">
          {{ parseTime(userData.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
        </el-descriptions-item>
        <el-descriptions-item label="最后登录时间">
          <span v-if="userData.lastLoginTime">{{ parseTime(userData.lastLoginTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          <span v-else style="color: #909399">未登录</span>
        </el-descriptions-item>
        <el-descriptions-item label="最后登录IP" :span="2">
          {{ userData.lastLoginIp || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ userData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="open = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 编辑用户对话框 -->
    <el-dialog title="修改用户信息" :visible.sync="openEdit" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="11" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" maxlength="18" />
        </el-form-item>
        <el-form-item label="身份类型">
          <el-select v-model="form.identityType" placeholder="请选择身份类型" clearable style="width: 100%">
            <el-option
              v-for="dict in dict.type.hz_identity_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="工作单位">
          <el-input v-model="form.workUnit" placeholder="请输入工作单位" />
        </el-form-item>
        <el-form-item label="单位联系方式">
          <el-input v-model="form.unitContact" placeholder="请输入单位联系方式" />
        </el-form-item>
        <el-form-item label="配偶姓名">
          <el-input v-model="form.spouseName" placeholder="请输入配偶姓名" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUser, getUser, updateUser, changeUserStatus, delUser } from "@/api/gangzhu/user";

export default {
  name: "HzUser",
  dicts: ['sys_user_sex', 'hz_user_source_type', 'hz_education_type', 'hz_identity_type'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      userList: [],
      open: false,
      openEdit: false,
      userData: {},
      form: {},
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        phone: null,
        realName: null,
        sourceType: null,
        status: null
      },
      rules: {
        realName: [
          { required: true, message: "真实姓名不能为空", trigger: "blur" }
        ],
        idCard: [
          { required: true, message: "身份证号不能为空", trigger: "blur" },
          { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/, message: "身份证号格式不正确", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询用户列表 */
    getList() {
      this.loading = true;
      listUser(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.userList = response.rows || response.data || [];
        this.total = response.total || 0;
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/gangzhu/user/export', {
        ...this.queryParams
      }, `user_${new Date().getTime()}.xlsx`)
    },
    /** 详情按钮操作 */
    handleView(row) {
      getUser(row.userId).then(response => {
        this.userData = response.data;
        this.open = true;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      getUser(row.userId).then(response => {
        this.form = response.data;
        this.openEdit = true;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.openEdit = false;
      this.resetForm();
    },
    /** 表单重置 */
    resetForm() {
      this.form = {
        userId: null,
        realName: null,
        idCard: null,
        identityType: null,
        workUnit: null,
        unitContact: null,
        spouseName: null
      };
      this.resetForm("form");
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateUser(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.openEdit = false;
            this.getList();
          });
        }
      });
    },
    /** 状态修改 */
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "停用";
      this.$modal.confirm('确认要"' + text + '""' + row.realName + '"用户吗？').then(() => {
        return changeUserStatus(row.userId, row.status);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(() => {
        row.status = row.status === "0" ? "1" : "0";
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$modal.confirm('是否确认删除用户"' + row.realName + '"？').then(() => {
        return delUser(row.userId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 获取图片完整URL */
    getImageUrl(url) {
      if (!url) return '';
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }
      const baseUrl = process.env.VUE_APP_BASE_API;
      if (url.indexOf(baseUrl) !== -1) {
        return url;
      }
      return baseUrl + (url.startsWith('/') ? url : '/' + url);
    },
    /** 下载附件 */
    downloadAttachment(url) {
      const fullUrl = this.getImageUrl(url);
      window.open(fullUrl, '_blank');
    }
  }
};
</script>
