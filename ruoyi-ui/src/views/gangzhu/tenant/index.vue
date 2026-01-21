<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="租户姓名" prop="tenantName">
        <el-input
          v-model="queryParams.tenantName"
          placeholder="请输入租户姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCard">
        <el-input
          v-model="queryParams.idCard"
          placeholder="请输入身份证号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:tenant:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="tenantList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="租户姓名" align="center" prop="tenantName" width="120" />
      <el-table-column label="身份证号" align="center" prop="idCard" width="180" />
      <el-table-column label="手机号" align="center" prop="phone" width="120" />
      <el-table-column label="性别" align="center" prop="gender" width="80">
        <template slot-scope="scope">
          <span v-if="scope.row.gender === '1'">男</span>
          <span v-else-if="scope.row.gender === '2'">女</span>
        </template>
      </el-table-column>
      <el-table-column label="工作单位" align="center" prop="workUnit" show-overflow-tooltip />
      <el-table-column label="职务" align="center" prop="position" width="120" />
      <el-table-column label="月收入(元)" align="center" prop="monthlyIncome" width="120" />
      <el-table-column label="租户类型" align="center" prop="tenantType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.tenantType === '1'" type="success">个人</el-tag>
          <el-tag v-else-if="scope.row.tenantType === '2'" type="primary">家庭</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['gangzhu:tenant:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:tenant:edit']"
          >修改</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 租户详情对话框 -->
    <el-dialog title="租户详情" :visible.sync="viewOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="租户姓名">{{ tenant.tenantName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ tenant.idCard }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ tenant.phone }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          <span v-if="tenant.gender === '1'">男</span>
          <span v-else-if="tenant.gender === '2'">女</span>
        </el-descriptions-item>
        <el-descriptions-item label="出生日期">{{ tenant.birthDate }}</el-descriptions-item>
        <el-descriptions-item label="民族">{{ tenant.nation }}</el-descriptions-item>
        <el-descriptions-item label="学历">{{ tenant.education }}</el-descriptions-item>
        <el-descriptions-item label="婚姻状况">{{ tenant.maritalStatus }}</el-descriptions-item>
        <el-descriptions-item label="户籍地址" :span="2">{{ tenant.householdAddress }}</el-descriptions-item>
        <el-descriptions-item label="现住址" :span="2">{{ tenant.currentAddress }}</el-descriptions-item>
        <el-descriptions-item label="工作单位">{{ tenant.workUnit }}</el-descriptions-item>
        <el-descriptions-item label="单位性质">{{ tenant.workUnitType }}</el-descriptions-item>
        <el-descriptions-item label="职务">{{ tenant.position }}</el-descriptions-item>
        <el-descriptions-item label="月收入">{{ tenant.monthlyIncome }}元</el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ tenant.emergencyContact }}</el-descriptions-item>
        <el-descriptions-item label="紧急联系电话">{{ tenant.emergencyPhone }}</el-descriptions-item>
        <el-descriptions-item label="租户类型">
          <el-tag v-if="tenant.tenantType === '1'" type="success">个人</el-tag>
          <el-tag v-else-if="tenant.tenantType === '2'" type="primary">家庭</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="tenant.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 修改租户对话框 -->
    <el-dialog title="修改租户" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="租户姓名" prop="tenantName">
              <el-input v-model="form.tenantName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard">
              <el-input v-model="form.idCard" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
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
import { listTenant, getTenant, updateTenant } from "@/api/gangzhu/tenant";

export default {
  name: "Tenant",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      tenantList: [],
      title: "",
      open: false,
      viewOpen: false,
      tenant: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tenantName: null,
        idCard: null,
        phone: null,
        status: null,
      },
      form: {},
      rules: {
        phone: [
          { required: true, message: "手机号不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listTenant(this.queryParams).then(response => {
        this.tenantList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        tenantId: null,
        tenantName: null,
        idCard: null,
        phone: null,
        status: "0",
        remark: null
      };
      this.resetForm("form");
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.tenantId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleView(row) {
      this.viewOpen = true;
      const tenantId = row.tenantId;
      getTenant(tenantId).then(response => {
        this.tenant = response.data;
      });
    },
    handleUpdate(row) {
      this.reset();
      const tenantId = row.tenantId;
      getTenant(tenantId).then(response => {
        this.form = response.data;
        this.open = true;
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          updateTenant(this.form).then(response => {
            this.$modal.msgSuccess("修改成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    handleExport() {
      this.download('system/tenant/export', {
        ...this.queryParams
      }, `tenant_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
