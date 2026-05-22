<template>
  <div class="app-container">
    <!-- 搜索 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="备案编号" prop="filingNo">
        <el-input v-model="queryParams.filingNo" placeholder="备案编号" clearable
          style="width:180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="签约人" prop="signName">
        <el-input v-model="queryParams.signName" placeholder="签约人姓名" clearable
          style="width:140px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="审批状态" prop="approveStatus">
        <el-select v-model="queryParams.approveStatus" placeholder="审批状态" clearable style="width:130px">
          <el-option v-for="dict in dict.type.hz_filing_approve_status"
            :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple"
          @click="handleDelete" v-hasPermi="['gangzhu:contractFiling:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini"
          @click="handleExport" v-hasPermi="['gangzhu:contractFiling:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="备案编号" align="center" prop="filingNo" width="180" />
      <el-table-column label="签约人" align="center" prop="signName" width="100" />
      <el-table-column label="签约单位" align="center" prop="signUnit" min-width="160" show-overflow-tooltip />
      <el-table-column label="签订日期" align="center" prop="signDate" width="110">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.signDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审批状态" align="center" prop="approveStatus" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hz_filing_approve_status" :value="scope.row.approveStatus" />
        </template>
      </el-table-column>
      <el-table-column label="审批人" align="center" prop="approveBy" width="100" />
      <el-table-column label="审批时间" align="center" prop="approveTime" width="155">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.approveTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="提交时间" align="center" prop="createTime" width="155">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view"
            @click="handleView(scope.row)" v-hasPermi="['gangzhu:contractFiling:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-check"
            v-if="scope.row.approveStatus === '0'"
            @click="handleApprove(scope.row)" v-hasPermi="['gangzhu:contractFiling:approve']">审批</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color:#F56C6C"
            @click="handleDelete(scope.row)" v-hasPermi="['gangzhu:contractFiling:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total"
      :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情 -->
    <el-dialog title="合同备案详情" :visible.sync="detailOpen" width="720px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="备案编号">{{ detail.filingNo }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <dict-tag :options="dict.type.hz_filing_approve_status" :value="detail.approveStatus" />
        </el-descriptions-item>
        <el-descriptions-item label="签约人">{{ detail.signName }}</el-descriptions-item>
        <el-descriptions-item label="签约单位">{{ detail.signUnit }}</el-descriptions-item>
        <el-descriptions-item label="签订日期" :span="2">{{ parseTime(detail.signDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="合同附件" :span="2">
          <div v-if="detail.contractFiles">
            <el-link v-for="(f,i) in (detail.contractFiles||'').split(',').filter(x=>x)" :key="i"
              type="primary" :href="f" target="_blank" style="margin-right:12px">附件{{ i+1 }}</el-link>
          </div>
          <span v-else>—</span>
        </el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detail.approveBy || '—' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ parseTime(detail.approveTime) || '—' }}</el-descriptions-item>
        <el-descriptions-item label="审批备注" :span="2">{{ detail.approveRemark || '—' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间" :span="2">{{ parseTime(detail.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 审批 -->
    <el-dialog title="备案审批" :visible.sync="approveOpen" width="500px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" :rules="approveRules" label-width="100px">
        <el-form-item label="备案编号">
          <span>{{ approveForm.filingNo }}</span>
        </el-form-item>
        <el-form-item label="签约人">
          <span>{{ approveForm.signName }}</span>
        </el-form-item>
        <el-form-item label="审批结果" prop="approveStatus">
          <el-radio-group v-model="approveForm.approveStatus">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批备注" prop="approveRemark">
          <el-input v-model="approveForm.approveRemark" type="textarea" :rows="3"
            :placeholder="approveForm.approveStatus === '2' ? '请填写驳回原因（必填）' : '可选'" maxlength="500" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApprove">确 定</el-button>
        <el-button @click="approveOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listContractFiling, getContractFiling, approveContractFiling, delContractFiling } from "@/api/gangzhu/contractFiling";

export default {
  name: "ContractFiling",
  dicts: ["hz_filing_approve_status"],
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      dataList: [],
      detailOpen: false,
      detail: {},
      approveOpen: false,
      approveForm: {},
      approveRules: {
        approveStatus: [{ required: true, message: "请选择审批结果", trigger: "change" }],
        approveRemark: [{
          validator: (rule, value, cb) => {
            if (this.approveForm.approveStatus === "2" && !value) {
              cb(new Error("驳回必须填写原因"));
            } else { cb(); }
          }, trigger: "blur"
        }]
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        filingNo: null,
        signName: null,
        approveStatus: null
      }
    };
  },
  created() { this.getList(); },
  methods: {
    getList() {
      this.loading = true;
      listContractFiling(this.queryParams).then(res => {
        this.dataList = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(s) {
      this.ids = s.map(it => it.filingId);
      this.multiple = !s.length;
    },
    handleView(row) {
      getContractFiling(row.filingId).then(res => {
        this.detail = res.data || {};
        this.detailOpen = true;
      });
    },
    handleApprove(row) {
      this.approveForm = {
        filingId: row.filingId,
        filingNo: row.filingNo,
        signName: row.signName,
        approveStatus: "1",
        approveRemark: ""
      };
      this.approveOpen = true;
    },
    submitApprove() {
      this.$refs["approveForm"].validate(valid => {
        if (!valid) return;
        approveContractFiling({
          filingId: this.approveForm.filingId,
          approveStatus: this.approveForm.approveStatus,
          approveRemark: this.approveForm.approveRemark
        }).then(() => {
          this.$modal.msgSuccess("审批完成");
          this.approveOpen = false;
          this.getList();
        });
      });
    },
    handleDelete(row) {
      const ids = row.filingId || this.ids;
      this.$modal.confirm('是否确认删除选中的合同备案？').then(() =>
        delContractFiling(ids)
      ).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('gangzhu/contractFiling/export', { ...this.queryParams },
        `contract_filing_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>
