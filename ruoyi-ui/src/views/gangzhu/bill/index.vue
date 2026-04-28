<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="账单编号" prop="billNo">
        <el-input
          v-model="queryParams.billNo"
          placeholder="请输入账单编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="租户姓名" prop="tenantName">
        <el-input
          v-model="queryParams.tenantName"
          placeholder="请输入租户姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账单类型" prop="billType">
        <el-select v-model="queryParams.billType" placeholder="请选择账单类型" clearable>
          <el-option label="押金" value="1" />
          <el-option label="租金" value="2" />
          <el-option label="水费" value="3" />
          <el-option label="电费" value="4" />
          <el-option label="燃气费" value="5" />
          <el-option label="物业费" value="6" />
          <el-option label="其他" value="7" />
        </el-select>
      </el-form-item>
      <el-form-item label="账单状态" prop="billStatus">
        <el-select v-model="queryParams.billStatus" placeholder="请选择账单状态" clearable>
          <el-option label="待支付" value="0" />
          <el-option label="已支付" value="1" />
          <el-option label="部分支付" value="2" />
          <el-option label="已逾期" value="3" />
          <el-option label="已关闭" value="4" />
        </el-select>
      </el-form-item>
      <el-form-item label="合同编号" prop="contractNo">
        <el-input
          v-model="queryParams.contractNo"
          placeholder="请输入合同编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="房源编号" prop="houseCode">
        <el-input
          v-model="queryParams.houseCode"
          placeholder="请输入房源编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="账单日期" prop="billDateRange">
        <el-date-picker
          v-model="billDateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
        />
      </el-form-item>
      <el-form-item label="应付日期" prop="dueDateRange">
        <el-date-picker
          v-model="dueDateRange"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
        />
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
          v-hasPermi="['gangzhu:bill:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="billList">
      <el-table-column label="账单编号" align="center" prop="billNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="租户姓名" align="center" prop="tenantName" width="120" show-overflow-tooltip />
      <el-table-column label="所属合同" align="center" prop="contractNo" width="160" show-overflow-tooltip />
      <el-table-column label="账单类型" align="center" prop="billType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.billType === '1'" type="warning">押金</el-tag>
          <el-tag v-else-if="scope.row.billType === '2'" type="primary">租金</el-tag>
          <el-tag v-else-if="scope.row.billType === '3'" type="info">水费</el-tag>
          <el-tag v-else-if="scope.row.billType === '4'" type="info">电费</el-tag>
          <el-tag v-else-if="scope.row.billType === '5'" type="info">燃气费</el-tag>
          <el-tag v-else-if="scope.row.billType === '6'" type="info">物业费</el-tag>
          <el-tag v-else>其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="账单金额(元)" align="center" prop="billAmount" width="120" />
      <el-table-column label="已支付(元)" align="center" prop="paidAmount" width="120" />
      <el-table-column label="未支付(元)" align="center" prop="unpaidAmount" width="120" />
      <el-table-column label="账期" align="center" prop="billPeriod" width="120" />
      <el-table-column label="应付日期" align="center" prop="dueDate" width="120" />
      <el-table-column label="账单状态" align="center" prop="billStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.billStatus === '0'" type="warning">待支付</el-tag>
          <el-tag v-else-if="scope.row.billStatus === '1'" type="success">已支付</el-tag>
          <el-tag v-else-if="scope.row.billStatus === '2'" type="info">部分支付</el-tag>
          <el-tag v-else-if="scope.row.billStatus === '3'" type="danger">已逾期</el-tag>
          <el-tag v-else type="info">已关闭</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="逾期天数" align="center" prop="overdueDays" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.overdueDays > 0" style="color: red;">{{ scope.row.overdueDays }}天</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="100">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:bill:query']"
          >详情</el-button>
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

    <!-- 账单详情对话框 -->
    <el-dialog title="账单详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="账单编号">{{ detailData.billNo }}</el-descriptions-item>
        <el-descriptions-item label="账单类型">
          <el-tag v-if="detailData.billType === '1'" type="warning">押金</el-tag>
          <el-tag v-else-if="detailData.billType === '2'" type="primary">租金</el-tag>
          <el-tag v-else-if="detailData.billType === '3'" type="info">水费</el-tag>
          <el-tag v-else-if="detailData.billType === '4'" type="info">电费</el-tag>
          <el-tag v-else-if="detailData.billType === '5'" type="info">燃气费</el-tag>
          <el-tag v-else-if="detailData.billType === '6'" type="info">物业费</el-tag>
          <el-tag v-else>其他</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="租户姓名">{{ detailData.tenantName }}</el-descriptions-item>
        <el-descriptions-item label="所属合同">{{ detailData.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="房源编号">{{ detailData.houseCode }}</el-descriptions-item>
        <el-descriptions-item label="账期">{{ detailData.billPeriod }}</el-descriptions-item>
        <el-descriptions-item label="账单金额">{{ detailData.billAmount }} 元</el-descriptions-item>
        <el-descriptions-item label="已支付金额">{{ detailData.paidAmount }} 元</el-descriptions-item>
        <el-descriptions-item label="未支付金额">{{ detailData.unpaidAmount }} 元</el-descriptions-item>
        <el-descriptions-item label="滞纳金">{{ detailData.lateFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="账单日期">{{ detailData.billDate }}</el-descriptions-item>
        <el-descriptions-item label="应付日期">{{ detailData.dueDate }}</el-descriptions-item>
        <el-descriptions-item label="账单状态">
          <el-tag v-if="detailData.billStatus === '0'" type="warning">待支付</el-tag>
          <el-tag v-else-if="detailData.billStatus === '1'" type="success">已支付</el-tag>
          <el-tag v-else-if="detailData.billStatus === '2'" type="info">部分支付</el-tag>
          <el-tag v-else-if="detailData.billStatus === '3'" type="danger">已逾期</el-tag>
          <el-tag v-else type="info">已关闭</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="逾期天数">
          <span v-if="detailData.overdueDays > 0" style="color: red;">{{ detailData.overdueDays }} 天</span>
          <span v-else>未逾期</span>
        </el-descriptions-item>
        <el-descriptions-item label="支付时间">{{ detailData.payTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ detailData.payMethod || '-' }}</el-descriptions-item>
        <el-descriptions-item label="交易流水号" :span="2">{{ detailData.transactionNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detailData.createBy }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBill, getBill } from "@/api/gangzhu/bill";

export default {
  name: "Bill",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      billList: [],
      detailOpen: false,
      billDateRange: [],
      dueDateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        billNo: null,
        tenantName: null,
        billType: null,
        billStatus: null,
        contractNo: null,
        houseCode: null,
      },
      detailData: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      // 处理日期范围参数和合同编号/房源编号参数（后端通过 params 读取）
      this.queryParams.params = {};
      if (this.billDateRange && this.billDateRange.length === 2) {
        this.queryParams.params["beginBillDate"] = this.billDateRange[0];
        this.queryParams.params["endBillDate"] = this.billDateRange[1];
      }
      if (this.dueDateRange && this.dueDateRange.length === 2) {
        this.queryParams.params["beginDueDate"] = this.dueDateRange[0];
        this.queryParams.params["endDueDate"] = this.dueDateRange[1];
      }
      if (this.queryParams.contractNo) {
        this.queryParams.params["contractNo"] = this.queryParams.contractNo;
      }
      if (this.queryParams.houseCode) {
        this.queryParams.params["houseCode"] = this.queryParams.houseCode;
      }
      listBill(this.queryParams).then(response => {
        this.billList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.billDateRange = [];
      this.dueDateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleDetail(row) {
      const billId = row.billId;
      getBill(billId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    handleExport() {
      this.download('system/bill/export', {
        ...this.queryParams
      }, `bill_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
