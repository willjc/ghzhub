<template>
  <div class="app-container">
    <!-- 搜索 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="订单号" prop="orderNo">
        <el-input v-model="queryParams.orderNo" placeholder="订单号" clearable
          style="width:180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="申请人" prop="applicantName">
        <el-input v-model="queryParams.applicantName" placeholder="申请人" clearable
          style="width:140px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号" prop="applicantPhone">
        <el-input v-model="queryParams.applicantPhone" placeholder="手机号" clearable
          style="width:140px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width:120px">
          <el-option v-for="dict in dict.type.hz_service_order_status"
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
          @click="handleDelete" v-hasPermi="['gangzhu:moveOrder:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini"
          @click="handleExport" v-hasPermi="['gangzhu:moveOrder:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="订单号" align="center" prop="orderNo" width="200" />
      <el-table-column label="申请人" align="center" prop="applicantName" width="100" />
      <el-table-column label="手机号" align="center" prop="applicantPhone" width="120" />
      <el-table-column label="起运地址" align="center" prop="fromAddress" min-width="160" show-overflow-tooltip />
      <el-table-column label="目的地址" align="center" prop="toAddress" min-width="160" show-overflow-tooltip />
      <el-table-column label="拆装家具" align="center" prop="needDisassembly" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.needDisassembly === '1'" type="warning">需要</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="期望时间" align="center" prop="expectTime" width="155">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.expectTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hz_service_order_status" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="服务公司" align="center" prop="companyName" min-width="140" show-overflow-tooltip />
      <el-table-column label="评分" align="center" prop="rateScore" width="80">
        <template slot-scope="scope">
          <el-rate v-if="scope.row.rateScore" :value="scope.row.rateScore" disabled />
          <span v-else>—</span>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="createTime" width="155">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view"
            @click="handleView(scope.row)" v-hasPermi="['gangzhu:moveOrder:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-user"
            v-if="['0','1'].indexOf(scope.row.status) >= 0"
            @click="handleAssign(scope.row)" v-hasPermi="['gangzhu:moveOrder:assign']">分配</el-button>
          <el-button size="mini" type="text" icon="el-icon-check"
            v-if="['1','2'].indexOf(scope.row.status) >= 0"
            @click="handleFinish(scope.row)" v-hasPermi="['gangzhu:moveOrder:finish']">完成</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color:#F56C6C"
            @click="handleDelete(scope.row)" v-hasPermi="['gangzhu:moveOrder:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total"
      :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 详情 -->
    <el-dialog title="搬家订单详情" :visible.sync="detailOpen" width="720px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="订单号">{{ detail.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="dict.type.hz_service_order_status" :value="detail.status" />
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detail.applicantName }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detail.applicantPhone }}</el-descriptions-item>
        <el-descriptions-item label="起运地址" :span="2">{{ detail.fromAddress }}</el-descriptions-item>
        <el-descriptions-item label="目的地址" :span="2">{{ detail.toAddress }}</el-descriptions-item>
        <el-descriptions-item label="物品描述" :span="2">{{ detail.moveItemDesc || '—' }}</el-descriptions-item>
        <el-descriptions-item label="拆装家具">
          <el-tag v-if="detail.needDisassembly === '1'" type="warning">需要</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="期望时间">{{ parseTime(detail.expectTime) }}</el-descriptions-item>
        <el-descriptions-item label="申请备注" :span="2">{{ detail.applyRemark || '—' }}</el-descriptions-item>
        <el-descriptions-item label="服务公司">{{ detail.companyName || '—' }}</el-descriptions-item>
        <el-descriptions-item label="分配人">{{ detail.assignedBy || '—' }}</el-descriptions-item>
        <el-descriptions-item label="分配时间" :span="2">{{ parseTime(detail.assignedTime) || '—' }}</el-descriptions-item>
        <el-descriptions-item label="分配备注" :span="2">{{ detail.assignRemark || '—' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间" :span="2">{{ parseTime(detail.finishTime) || '—' }}</el-descriptions-item>
        <el-descriptions-item label="评分">
          <el-rate v-if="detail.rateScore" :value="detail.rateScore" disabled />
          <span v-else>—</span>
        </el-descriptions-item>
        <el-descriptions-item label="评价时间">{{ parseTime(detail.rateTime) || '—' }}</el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2">{{ detail.rateContent || '—' }}</el-descriptions-item>
        <el-descriptions-item label="取消原因" :span="2" v-if="detail.cancelReason">{{ detail.cancelReason }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ parseTime(detail.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 分配对话框 -->
    <el-dialog title="分配服务公司" :visible.sync="assignOpen" width="540px" append-to-body>
      <el-form ref="assignForm" :model="assignForm" :rules="assignRules" label-width="100px">
        <el-form-item label="订单号">
          <span>{{ assignForm.orderNo }}</span>
        </el-form-item>
        <el-form-item label="申请人">
          <span>{{ assignForm.applicantName }} / {{ assignForm.applicantPhone }}</span>
        </el-form-item>
        <el-form-item label="服务公司" prop="companyId">
          <el-select v-model="assignForm.companyId" placeholder="请选择服务公司" filterable style="width:100%">
            <el-option v-for="c in companyOptions" :key="c.companyId"
              :label="c.companyName + (c.companyType === '3' ? '（综合）' : '')"
              :value="c.companyId" />
          </el-select>
        </el-form-item>
        <el-form-item label="分配备注" prop="assignRemark">
          <el-input v-model="assignForm.assignRemark" type="textarea" :rows="3"
            placeholder="可填联系约定、注意事项等" maxlength="500" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAssign">确 定</el-button>
        <el-button @click="assignOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMoveOrder, getMoveOrder, assignMoveOrder, finishMoveOrder, delMoveOrder } from "@/api/gangzhu/moveOrder";
import { activeServiceCompanies } from "@/api/gangzhu/serviceCompany";

export default {
  name: "MoveOrder",
  dicts: ["hz_service_order_status"],
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      orderList: [],
      detailOpen: false,
      detail: {},
      assignOpen: false,
      assignForm: {},
      companyOptions: [],
      assignRules: {
        companyId: [{ required: true, message: "请选择服务公司", trigger: "change" }]
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        orderNo: null,
        applicantName: null,
        applicantPhone: null,
        status: null
      }
    };
  },
  created() { this.getList(); },
  methods: {
    getList() {
      this.loading = true;
      listMoveOrder(this.queryParams).then(res => {
        this.orderList = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(s) {
      this.ids = s.map(it => it.orderId);
      this.multiple = !s.length;
    },
    handleView(row) {
      getMoveOrder(row.orderId).then(res => {
        this.detail = res.data || {};
        this.detailOpen = true;
      });
    },
    handleAssign(row) {
      this.assignForm = {
        orderId: row.orderId,
        orderNo: row.orderNo,
        applicantName: row.applicantName,
        applicantPhone: row.applicantPhone,
        companyId: row.companyId || null,
        assignRemark: ""
      };
      activeServiceCompanies("2").then(res => {
        this.companyOptions = res.data || [];
        this.assignOpen = true;
      });
    },
    submitAssign() {
      this.$refs["assignForm"].validate(valid => {
        if (!valid) return;
        assignMoveOrder({
          orderId: this.assignForm.orderId,
          companyId: this.assignForm.companyId,
          assignRemark: this.assignForm.assignRemark
        }).then(() => {
          this.$modal.msgSuccess("分配成功");
          this.assignOpen = false;
          this.getList();
        });
      });
    },
    handleFinish(row) {
      this.$modal.confirm(`确认将订单【${row.orderNo}】标记为已完成？`).then(() =>
        finishMoveOrder(row.orderId)
      ).then(() => {
        this.$modal.msgSuccess("已标记完成");
        this.getList();
      }).catch(() => {});
    },
    handleDelete(row) {
      const ids = row.orderId || this.ids;
      this.$modal.confirm('是否确认删除选中的搬家订单？').then(() =>
        delMoveOrder(ids)
      ).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('gangzhu/moveOrder/export', { ...this.queryParams },
        `move_order_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>
