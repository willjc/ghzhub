<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="批次名称" prop="batchName">
        <el-input
          v-model="queryParams.batchName"
          placeholder="请输入批次名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="企业名称" prop="enterpriseName">
        <el-input
          v-model="queryParams.enterpriseName"
          placeholder="请输入企业名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系方式" prop="contactPhone">
        <el-input
          v-model="queryParams.contactPhone"
          placeholder="请输入联系方式"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账单状态" prop="billStatus">
        <el-select v-model="queryParams.billStatus" placeholder="请选择" clearable>
          <el-option label="待审核" value="0" />
          <el-option label="已审核" value="1" />
          <el-option label="已支付" value="2" />
          <el-option label="已关闭" value="3" />
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
          type="success"
          plain
          icon="el-icon-check"
          size="mini"
          :disabled="single"
          @click="handleApprove"
          v-hasPermi="['system:enterpriseBill:approve']"
        >审核通过</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-close"
          size="mini"
          :disabled="single"
          @click="handleReject"
          v-hasPermi="['system:enterpriseBill:approve']"
        >审核拒绝</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:enterpriseBill:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="billList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="账单编号" align="center" prop="billNo" width="150" show-overflow-tooltip />
      <el-table-column label="批次名称" align="center" prop="batchName" min-width="140" show-overflow-tooltip />
      <el-table-column label="企业名称" align="center" prop="enterpriseName" min-width="140" show-overflow-tooltip />
      <el-table-column label="联系人" align="center" prop="contactPerson" width="100" />
      <el-table-column label="联系方式" align="center" prop="contactPhone" width="130" />
      <el-table-column label="房源数量" align="center" prop="houseCount" width="90">
        <template slot-scope="scope">
          <span>{{ scope.row.houseCount }} 套</span>
        </template>
      </el-table-column>
      <el-table-column label="计算月数" align="center" prop="months" width="90">
        <template slot-scope="scope">
          <span>{{ scope.row.months }} 个月</span>
        </template>
      </el-table-column>
      <el-table-column label="计算总价" align="center" prop="totalAmount" width="110">
        <template slot-scope="scope">
          <span>¥{{ scope.row.totalAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="优惠金额" align="center" prop="discountAmount" width="110">
        <template slot-scope="scope">
          <span style="color: #e6a23c">-¥{{ scope.row.discountAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="优惠后总价" align="center" prop="finalAmount" width="120">
        <template slot-scope="scope">
          <span style="color: #f56c6c; font-weight: bold">¥{{ scope.row.finalAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="账单状态" align="center" prop="billStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.billStatus === '0'" type="info">待审核</el-tag>
          <el-tag v-else-if="scope.row.billStatus === '1'" type="warning">已审核</el-tag>
          <el-tag v-else-if="scope.row.billStatus === '2'" type="success">已支付</el-tag>
          <el-tag v-else-if="scope.row.billStatus === '3'" type="info">已关闭</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            v-if="scope.row.billStatus === '0'"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleApproveItem(scope.row)"
            v-hasPermi="['system:enterpriseBill:approve']"
          >审核</el-button>
          <el-button
            v-if="scope.row.contractFile"
            size="mini"
            type="text"
            icon="el-icon-download"
            @click="handleDownloadContract(scope.row)"
          >合同</el-button>
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

    <!-- 详情对话框 -->
    <el-dialog title="企业账单详情" :visible.sync="viewOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="账单编号">{{ viewData.billNo }}</el-descriptions-item>
        <el-descriptions-item label="批次名称">{{ viewData.batchName }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ viewData.enterpriseName }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ viewData.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ viewData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="房源数量">{{ viewData.houseCount }} 套</el-descriptions-item>
        <el-descriptions-item label="入驻日期">{{ parseTime(viewData.checkInDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="截止日期">{{ parseTime(viewData.checkOutDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="计算月数">{{ viewData.months }} 个月</el-descriptions-item>
        <el-descriptions-item label="单价合计">¥{{ viewData.totalRent }}/月</el-descriptions-item>
        <el-descriptions-item label="计算总价">¥{{ viewData.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">¥{{ viewData.discountAmount }}</el-descriptions-item>
        <el-descriptions-item label="优惠后总价">
          <span style="color: #f56c6c; font-weight: bold; font-size: 16px">¥{{ viewData.finalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="账单状态">
          <el-tag v-if="viewData.billStatus === '0'" type="info">待审核</el-tag>
          <el-tag v-else-if="viewData.billStatus === '1'" type="warning">已审核</el-tag>
          <el-tag v-else-if="viewData.billStatus === '2'" type="success">已支付</el-tag>
          <el-tag v-else-if="viewData.billStatus === '3'" type="info">已关闭</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="viewData.approveStatus === '0'" type="info">待审核</el-tag>
          <el-tag v-else-if="viewData.approveStatus === '1'" type="success">已通过</el-tag>
          <el-tag v-else-if="viewData.approveStatus === '2'" type="danger">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="合同文件" :span="2">
          <el-link v-if="viewData.contractFile" :href="getContractUrl(viewData.contractFile)" target="_blank" type="primary">
            <i class="el-icon-document"></i> 查看合同
          </el-link>
          <span v-else style="color: #909399">未上传</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ viewData.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(viewData.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '无' }}</el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listEnterpriseBill, getEnterpriseBill, approveEnterpriseBill } from "@/api/gangzhu/enterprise";

export default {
  name: "EnterpriseBill",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      billList: [],
      title: "",
      viewOpen: false,
      viewData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        batchName: null,
        enterpriseName: null,
        contactPhone: null,
        billStatus: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询企业账单列表 */
    getList() {
      this.loading = true;
      listEnterpriseBill(this.queryParams).then(response => {
        this.billList = response.rows || response.data || [];
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.billId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 详情按钮操作 */
    handleView(row) {
      getEnterpriseBill(row.billId).then(response => {
        this.viewData = response.data;
        // 计算单价合计
        if (this.viewData.houseCount && this.viewData.totalAmount && this.viewData.months) {
          this.viewData.totalRent = (parseFloat(this.viewData.totalAmount) / this.viewData.months).toFixed(2);
        }
        this.viewOpen = true;
      });
    },
    /** 审核通过 */
    handleApprove() {
      const billIds = this.ids;
      this.$modal.confirm('是否确认审核通过选中的账单？').then(() => {
        return approveEnterpriseBill(billIds[0], { approved: true });
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("审核成功");
      }).catch(() => {});
    },
    /** 审核拒绝 */
    handleReject() {
      const billIds = this.ids;
      this.$modal.confirm('是否确认审核拒绝选中的账单？').then(() => {
        return approveEnterpriseBill(billIds[0], { approved: false });
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("已拒绝");
      }).catch(() => {});
    },
    /** 单个审核 */
    handleApproveItem(row) {
      this.$modal.confirm('是否确认审核通过该账单？').then(() => {
        return approveEnterpriseBill(row.billId, { approved: true });
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("审核成功");
      }).catch(() => {});
    },
    /** 下载合同 */
    handleDownloadContract(row) {
      const url = this.getContractUrl(row.contractFile);
      window.open(url, '_blank');
    },
    /** 获取合同URL */
    getContractUrl(contractFile) {
      if (!contractFile) return '';
      if (contractFile.startsWith('http://') || contractFile.startsWith('https://')) {
        return contractFile;
      }
      return process.env.VUE_APP_BASE_API + contractFile;
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/enterpriseBill/export', {
        ...this.queryParams
      }, `企业账单_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
</style>
