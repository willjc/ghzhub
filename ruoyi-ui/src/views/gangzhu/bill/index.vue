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
      <el-form-item label="是否逾期" prop="isOverdue">
        <el-select v-model="queryParams.isOverdue" placeholder="请选择" clearable>
          <el-option label="否" value="0" />
          <el-option label="是" value="1" />
        </el-select>
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['gangzhu:bill:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['gangzhu:bill:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gangzhu:bill:remove']"
        >删除</el-button>
      </el-col>
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

    <el-table v-loading="loading" :data="billList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:bill:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:bill:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:bill:remove']"
          >删除</el-button>
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

    <!-- 添加或修改账单对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="账单类型" prop="billType">
              <el-select v-model="form.billType" placeholder="请选择账单类型">
                <el-option label="押金" value="1" />
                <el-option label="租金" value="2" />
                <el-option label="水费" value="3" />
                <el-option label="电费" value="4" />
                <el-option label="燃气费" value="5" />
                <el-option label="物业费" value="6" />
                <el-option label="其他" value="7" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账单金额(元)" prop="billAmount">
              <el-input-number v-model="form.billAmount" controls-position="right" :min="0" :precision="2" :step="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="账期开始" prop="periodStart">
              <el-date-picker
                v-model="form.periodStart"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="账期结束" prop="periodEnd">
              <el-date-picker
                v-model="form.periodEnd"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="应付日期" prop="dueDate">
          <el-date-picker
            v-model="form.dueDate"
            type="date"
            placeholder="选择日期"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item label="账单状态" prop="billStatus">
          <el-select v-model="form.billStatus" placeholder="请选择账单状态">
            <el-option label="待支付" value="0" />
            <el-option label="已支付" value="1" />
            <el-option label="部分支付" value="2" />
            <el-option label="已逾期" value="3" />
            <el-option label="已关闭" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

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
import { listBill, getBill, addBill, updateBill, delBill } from "@/api/gangzhu/bill";

export default {
  name: "Bill",
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
      open: false,
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
        isOverdue: null,
      },
      detailData: {},
      form: {},
      rules: {
        billType: [
          { required: true, message: "账单类型不能为空", trigger: "change" }
        ],
        billAmount: [
          { required: true, message: "账单金额不能为空", trigger: "blur" }
        ],
        periodStart: [
          { required: true, message: "账期开始日期不能为空", trigger: "change" }
        ],
        periodEnd: [
          { required: true, message: "账期结束日期不能为空", trigger: "change" }
        ],
        dueDate: [
          { required: true, message: "应付日期不能为空", trigger: "change" }
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
      // 处理日期范围参数
      this.queryParams.params = {};
      if (this.billDateRange && this.billDateRange.length === 2) {
        this.queryParams.params["beginBillDate"] = this.billDateRange[0];
        this.queryParams.params["endBillDate"] = this.billDateRange[1];
      }
      if (this.dueDateRange && this.dueDateRange.length === 2) {
        this.queryParams.params["beginDueDate"] = this.dueDateRange[0];
        this.queryParams.params["endDueDate"] = this.dueDateRange[1];
      }
      listBill(this.queryParams).then(response => {
        this.billList = response.rows;
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
        billId: null,
        billType: null,
        billAmount: null,
        periodStart: null,
        periodEnd: null,
        dueDate: null,
        billStatus: "0",
        remark: null
      };
      this.resetForm("form");
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
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.billId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加账单";
    },
    handleUpdate(row) {
      this.reset();
      const billId = row.billId || this.ids
      getBill(billId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改账单";
      });
    },
    handleDetail(row) {
      const billId = row.billId;
      getBill(billId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.billId != null) {
            updateBill(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addBill(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const billIds = row.billId || this.ids;
      this.$modal.confirm('是否确认删除账单编号为"' + billIds + '"的数据项？').then(function() {
        return delBill(billIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('system/bill/export', {
        ...this.queryParams
      }, `bill_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
