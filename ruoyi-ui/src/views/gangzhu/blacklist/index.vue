<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="姓名" prop="tenantName">
        <el-input
          v-model="queryParams.tenantName"
          placeholder="请输入姓名"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
          <el-option label="生效中" value="1" />
          <el-option label="已解除" value="0" />
        </el-select>
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
          v-hasPermi="['gangzhu:blacklist:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="blacklistList">
      <el-table-column label="ID" align="center" prop="blacklistId" width="70" />
      <el-table-column label="姓名" align="center" prop="tenantName" width="120" show-overflow-tooltip />
      <el-table-column label="手机号" align="center" prop="phone" width="130">
        <template slot-scope="scope">
          <span v-if="scope.row.phone">{{ scope.row.phone }}</span>
          <span v-else style="color: #C0C4CC;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="身份证号" align="center" prop="idCard" width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          <span v-if="scope.row.idCard">{{ scope.row.idCard }}</span>
          <span v-else style="color: #C0C4CC;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="加入原因" align="center" prop="reason" min-width="200" show-overflow-tooltip />
      <el-table-column label="加入时间" align="center" prop="blacklistTime" width="160" />
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '1'" type="danger" size="small">生效中</el-tag>
          <el-tag v-else type="info" size="small">已解除</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="解除时间" align="center" prop="removeTime" width="160">
        <template slot-scope="scope">
          <span v-if="scope.row.removeTime">{{ scope.row.removeTime }}</span>
          <span v-else style="color: #C0C4CC;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="解除原因" align="center" prop="removeReason" min-width="160" show-overflow-tooltip>
        <template slot-scope="scope">
          <span v-if="scope.row.removeReason">{{ scope.row.removeReason }}</span>
          <span v-else style="color: #C0C4CC;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120" fixed="right">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.status === '1'"
            size="mini"
            type="text"
            icon="el-icon-circle-check"
            style="color: #67C23A"
            @click="handleRemove(scope.row)"
            v-hasPermi="['gangzhu:blacklist:remove']"
          >解除</el-button>
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

    <!-- 解除黑名单对话框 -->
    <el-dialog title="解除黑名单" :visible.sync="removeOpen" width="500px" append-to-body>
      <el-form ref="removeForm" :model="removeForm" label-width="90px">
        <el-form-item label="姓名">
          <span>{{ removeForm.tenantName }}</span>
        </el-form-item>
        <el-form-item label="加入原因">
          <span>{{ removeForm.reason }}</span>
        </el-form-item>
        <el-form-item label="解除原因" prop="removeReason" :rules="[{ required: true, message: '请填写解除原因', trigger: 'blur' }]">
          <el-input
            v-model="removeForm.removeReason"
            type="textarea"
            :rows="4"
            placeholder="请输入解除原因"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitRemove">确 定</el-button>
        <el-button @click="removeOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBlacklist, removeBlacklist } from "@/api/gangzhu/blacklist";

export default {
  name: "Blacklist",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      blacklistList: [],
      removeOpen: false,
      removeForm: {
        blacklistId: null,
        tenantName: '',
        reason: '',
        removeReason: ''
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tenantName: null,
        phone: null,
        status: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询黑名单列表 */
    getList() {
      this.loading = true;
      listBlacklist(this.queryParams).then(response => {
        this.blacklistList = response.rows;
        this.total = response.total;
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 解除按钮操作 */
    handleRemove(row) {
      this.removeForm = {
        blacklistId: row.blacklistId,
        tenantName: row.tenantName,
        reason: row.reason,
        removeReason: ''
      };
      this.removeOpen = true;
    },
    /** 提交解除 */
    submitRemove() {
      this.$refs["removeForm"].validate(valid => {
        if (valid) {
          removeBlacklist(this.removeForm.blacklistId, this.removeForm.removeReason).then(response => {
            this.$modal.msgSuccess("解除成功");
            this.removeOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gangzhu/blacklist/export', {
        ...this.queryParams
      }, `blacklist_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
