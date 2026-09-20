<template>
  <div class="app-container">
    <el-form ref="queryForm" :model="queryParams" size="small" :inline="true" v-show="showSearch" label-width="110px">
      <el-form-item label="租户姓名" prop="realName">
        <el-input v-model="queryParams.realName" placeholder="请输入租户姓名" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="身份证号" prop="idCard">
        <el-input v-model="queryParams.idCard" placeholder="请输入身份证号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="学历" prop="education">
        <el-select v-model="queryParams.education" placeholder="请选择学历" clearable>
          <el-option v-for="item in dict.type.hz_education_type" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="职业（身份类型）" prop="identityType">
        <el-select v-model="queryParams.identityType" placeholder="请选择职业" clearable>
          <el-option v-for="item in dict.type.hz_identity_type" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="是否配租" prop="allocated">
        <el-select v-model="queryParams.allocated" placeholder="请选择" clearable>
          <el-option label="已配租" value="1" />
          <el-option label="未配租" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="申请时间">
        <el-date-picker
          v-model="applyTimeRange"
          type="daterange"
          value-format="yyyy-MM-dd"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 240px"
        />
      </el-form-item>
      <el-form-item label="用户状态" prop="status">
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
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="tenantList">
      <el-table-column label="租户姓名" align="center" prop="realName" width="110" />
      <el-table-column label="身份证号" align="center" prop="idCard" width="180" />
      <el-table-column label="手机号" align="center" prop="phone" width="120" />
      <el-table-column label="性别" align="center" prop="gender" width="70">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hz_user_sex" :value="scope.row.gender" />
        </template>
      </el-table-column>
      <el-table-column label="学历" align="center" prop="education" width="100">
        <template slot-scope="scope">
          <dict-tag v-if="scope.row.education" :options="dict.type.hz_education_type" :value="scope.row.education" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="职业（身份类型）" align="center" prop="identityType" width="140">
        <template slot-scope="scope">
          <dict-tag v-if="scope.row.identityType" :options="dict.type.hz_identity_type" :value="scope.row.identityType" />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="工作单位" align="center" prop="workUnit" min-width="150" show-overflow-tooltip />
      <el-table-column label="是否配租" align="center" prop="allocated" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.allocated === '1'" type="success" size="small">已配租</el-tag>
          <el-tag v-else type="info" size="small">未配租</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="applyTime" width="160">
        <template slot-scope="scope">
          <span>{{ scope.row.applyTime || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240" fixed="right">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['gangzhu:tenant:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['gangzhu:tenant:edit']">修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-circle-close"
            style="color: #E6A23C"
            @click="handleAddBlacklist(scope.row)"
            v-hasPermi="['gangzhu:blacklist:add']"
          >资格退出</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <el-dialog title="租户详情" :visible.sync="viewOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="租户姓名">{{ tenant.realName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ tenant.idCard || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ tenant.phone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ tenant.contactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="性别">
          <dict-tag :options="dict.type.hz_user_sex" :value="tenant.gender" />
        </el-descriptions-item>
        <el-descriptions-item label="学历">
          <dict-tag v-if="tenant.education" :options="dict.type.hz_education_type" :value="tenant.education" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="职业（身份类型）">
          <dict-tag v-if="tenant.identityType" :options="dict.type.hz_identity_type" :value="tenant.identityType" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="工作单位">{{ tenant.workUnit || '-' }}</el-descriptions-item>
        <el-descriptions-item label="单位性质">
          <dict-tag v-if="tenant.unitNature" :options="dict.type.hz_unit_nature" :value="tenant.unitNature" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="婚姻状态">
          <dict-tag v-if="tenant.marriageStatus" :options="dict.type.hz_marriage_status" :value="tenant.marriageStatus" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="是否配租">{{ tenant.allocated === '1' ? '已配租' : '未配租' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ tenant.applyTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请类型">
          <span v-if="tenant.applyType === '1'">人才公寓</span>
          <span v-else-if="tenant.applyType === '2'">保障性租赁住房</span>
          <span v-else-if="tenant.applyType === '3'">市场化租赁</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="资格结果">
          <span v-if="tenant.qualificationResult === '1'">通过</span>
          <span v-else-if="tenant.qualificationResult === '0'">未通过</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="用户状态">{{ tenant.status === '0' ? '正常' : '停用' }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ tenant.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="修改租户" :visible.sync="editOpen" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="90px">
        <el-form-item label="租户姓名">
          <el-input v-model="form.realName" disabled />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="form.idCard" disabled />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" />
        </el-form-item>
        <el-form-item label="用户状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="editOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="资格退出/加入黑名单" :visible.sync="blacklistOpen" width="500px" append-to-body>
      <el-form ref="blacklistForm" :model="blacklistForm" label-width="90px">
        <el-form-item label="用户">
          <span>{{ blacklistForm.tenantName }}{{ blacklistForm.idCard ? '（' + blacklistForm.idCard + '）' : '' }}</span>
        </el-form-item>
        <el-form-item label="退出原因" prop="reason" :rules="[{ required: true, message: '请填写退出原因', trigger: 'blur' }]">
          <el-input v-model="blacklistForm.reason" type="textarea" :rows="4" placeholder="请输入资格退出原因" />
        </el-form-item>
        <div style="color: #E6A23C; font-size: 12px; padding-left: 90px;">加入黑名单后，该用户将无法登录小程序</div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBlacklist">确 定</el-button>
        <el-button @click="blacklistOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTenant, getTenant, updateTenant } from "@/api/gangzhu/tenant";
import { addBlacklist } from "@/api/gangzhu/blacklist";

export default {
  name: "Tenant",
  dicts: ['hz_user_sex', 'hz_education_type', 'hz_identity_type', 'hz_unit_nature', 'hz_marriage_status'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      tenantList: [],
      applyTimeRange: [],
      viewOpen: false,
      editOpen: false,
      blacklistOpen: false,
      tenant: {},
      form: {},
      blacklistForm: {
        tenantId: null,
        tenantName: '',
        idCard: '',
        reason: ''
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        realName: null,
        idCard: null,
        phone: null,
        education: null,
        identityType: null,
        allocated: null,
        status: null
      },
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
      listTenant(this.addDateRange(this.queryParams, this.applyTimeRange, 'ApplyTime')).then(response => {
        this.tenantList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.applyTimeRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleView(row) {
      getTenant(row.userId).then(response => {
        this.tenant = response.data;
        this.viewOpen = true;
      });
    },
    handleUpdate(row) {
      getTenant(row.userId).then(response => {
        this.form = response.data;
        this.editOpen = true;
      });
    },
    submitForm() {
      this.$refs.form.validate(valid => {
        if (valid) {
          updateTenant({
            userId: this.form.userId,
            phone: this.form.phone,
            status: this.form.status,
            remark: this.form.remark
          }).then(() => {
            this.$modal.msgSuccess("修改成功");
            this.editOpen = false;
            this.getList();
          });
        }
      });
    },
    handleAddBlacklist(row) {
      this.blacklistForm = {
        tenantId: row.userId,
        tenantName: row.realName || '',
        idCard: row.idCard || '',
        reason: ''
      };
      this.blacklistOpen = true;
    },
    submitBlacklist() {
      this.$refs.blacklistForm.validate(valid => {
        if (valid) {
          this.$modal.confirm('确认将用户"' + this.blacklistForm.tenantName + '"加入黑名单？加入后该用户将无法登录小程序。').then(() => {
            return addBlacklist({
              tenantId: this.blacklistForm.tenantId,
              reason: this.blacklistForm.reason
            });
          }).then(() => {
            this.$modal.msgSuccess("资格退出成功");
            this.blacklistOpen = false;
            this.getList();
          }).catch(() => {});
        }
      });
    },
    handleExport() {
      this.download('system/tenant/export', {
        ...this.addDateRange(this.queryParams, this.applyTimeRange, 'ApplyTime')
      }, `tenant_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>
