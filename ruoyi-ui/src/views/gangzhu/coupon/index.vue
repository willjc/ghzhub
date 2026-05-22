<template>
  <div class="app-container">
    <!-- 搜索 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="名称" prop="couponName">
        <el-input v-model="queryParams.couponName" placeholder="优惠券名称" clearable
          style="width:180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="couponType">
        <el-select v-model="queryParams.couponType" placeholder="类型" clearable style="width:120px">
          <el-option v-for="dict in dict.type.hz_coupon_type"
            :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width:120px">
          <el-option label="启用" value="0" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini"
          @click="handleAdd" v-hasPermi="['gangzhu:coupon:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple"
          @click="handleDelete" v-hasPermi="['gangzhu:coupon:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini"
          @click="handleExport" v-hasPermi="['gangzhu:coupon:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="名称" align="center" prop="couponName" min-width="160" show-overflow-tooltip />
      <el-table-column label="类型" align="center" prop="couponType" width="90">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hz_coupon_type" :value="scope.row.couponType" />
        </template>
      </el-table-column>
      <el-table-column label="优惠金额" align="center" prop="discountAmount" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.couponType === '2'">{{ scope.row.discountRate }}%</span>
          <span v-else>¥{{ scope.row.discountAmount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="最低金额" align="center" prop="minAmount" width="100">
        <template slot-scope="scope">
          <span>¥{{ scope.row.minAmount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="发行/已领" align="center" width="110">
        <template slot-scope="scope">
          <span>{{ scope.row.receivedCount || 0 }} / {{ scope.row.totalCount === 0 ? '不限' : scope.row.totalCount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="生效日" align="center" prop="validStartDate" width="110">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.validStartDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="失效日" align="center" prop="validEndDate" width="110">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.validEndDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status === '0' ? 'success' : 'info'">
            {{ scope.row.status === '0' ? '启用' : '停用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit"
            @click="handleEdit(scope.row)" v-hasPermi="['gangzhu:coupon:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-tickets"
            @click="handleReceiveList(scope.row)" v-hasPermi="['gangzhu:coupon:list']">领取记录</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color:#F56C6C"
            @click="handleDelete(scope.row)" v-hasPermi="['gangzhu:coupon:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total"
      :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改 -->
    <el-dialog :title="dialogTitle" :visible.sync="dialogOpen" width="640px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="优惠券名称" prop="couponName">
          <el-input v-model="form.couponName" placeholder="例如：新户首单立减30元" maxlength="50" />
        </el-form-item>
        <el-form-item label="编码" prop="couponCode">
          <el-input v-model="form.couponCode" placeholder="选填，便于检索" maxlength="50" />
        </el-form-item>
        <el-form-item label="类型" prop="couponType">
          <el-radio-group v-model="form.couponType">
            <el-radio v-for="d in dict.type.hz_coupon_type" :key="d.value" :label="d.value">{{ d.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="优惠金额" prop="discountAmount" v-if="form.couponType !== '2'">
              <el-input-number v-model="form.discountAmount" :min="0" :precision="2" :step="1" />
            </el-form-item>
            <el-form-item label="折扣率(%)" prop="discountRate" v-else>
              <el-input-number v-model="form.discountRate" :min="0" :max="100" :precision="0" :step="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最低使用">
              <el-input-number v-model="form.minAmount" :min="0" :precision="2" :step="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最高优惠" v-if="form.couponType === '2'">
              <el-input-number v-model="form.maxDiscount" :min="0" :precision="2" :step="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发行总量">
              <el-input-number v-model="form.totalCount" :min="0" :step="1" />
              <span style="margin-left:8px;color:#909399">(0=不限)</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="有效期" required>
          <el-date-picker v-model="form.validRange" type="daterange" value-format="yyyy-MM-dd"
            start-placeholder="开始日期" end-placeholder="结束日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="适用类型">
          <el-input v-model="form.applicableType" placeholder="自由文本，例如：保洁服务/全场通用" maxlength="100" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="dialogOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 领取记录 -->
    <el-dialog title="领取记录" :visible.sync="receiveOpen" width="900px" append-to-body>
      <el-form :inline="true" size="small">
        <el-form-item label="领取状态">
          <el-select v-model="receiveQuery.receiveStatus" placeholder="全部" clearable style="width:120px">
            <el-option v-for="d in dict.type.hz_coupon_receive_status"
              :key="d.value" :label="d.label" :value="d.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="loadReceiveList">查询</el-button>
        </el-form-item>
      </el-form>
      <el-table v-loading="receiveLoading" :data="receiveList">
        <el-table-column label="领取ID" prop="receiveId" width="100" />
        <el-table-column label="租户ID" prop="tenantId" width="100" />
        <el-table-column label="领取时间" prop="receiveTime" width="160">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.receiveTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" prop="receiveStatus" width="100">
          <template slot-scope="scope">
            <dict-tag :options="dict.type.hz_coupon_receive_status" :value="scope.row.receiveStatus" />
          </template>
        </el-table-column>
        <el-table-column label="使用时间" prop="useTime" width="160">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.useTime, '{y}-{m}-{d} {h}:{i}') || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="订单ID" prop="orderId" />
      </el-table>
      <pagination v-show="receiveTotal > 0" :total="receiveTotal"
        :page.sync="receiveQuery.pageNum" :limit.sync="receiveQuery.pageSize"
        @pagination="loadReceiveList" />
    </el-dialog>
  </div>
</template>

<script>
import { listCoupon, getCoupon, addCoupon, updateCoupon, delCoupon, listCouponReceive } from "@/api/gangzhu/coupon";

export default {
  name: "Coupon",
  dicts: ["hz_coupon_type", "hz_coupon_receive_status"],
  data() {
    return {
      loading: true,
      ids: [],
      multiple: true,
      showSearch: true,
      total: 0,
      dataList: [],
      dialogOpen: false,
      dialogTitle: "",
      form: this.emptyForm(),
      rules: {
        couponName: [{ required: true, message: "名称必填", trigger: "blur" }],
        couponType: [{ required: true, message: "类型必选", trigger: "change" }],
        discountAmount: [{ required: true, message: "金额必填", trigger: "blur" }],
        discountRate: [{ required: true, message: "折扣率必填", trigger: "blur" }]
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        couponName: null,
        couponType: null,
        status: null
      },
      // 领取记录
      receiveOpen: false,
      receiveLoading: false,
      receiveList: [],
      receiveTotal: 0,
      receiveQuery: { pageNum: 1, pageSize: 10, couponId: null, receiveStatus: null }
    };
  },
  created() { this.getList(); },
  methods: {
    emptyForm() {
      return {
        couponId: null, couponName: "", couponCode: "", couponType: "1",
        discountAmount: 0, discountRate: 0, minAmount: 0, maxDiscount: 0,
        totalCount: 0, validStartDate: null, validEndDate: null,
        validRange: [], applicableType: "", status: "0"
      };
    },
    getList() {
      this.loading = true;
      listCoupon(this.queryParams).then(res => {
        this.dataList = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(s) {
      this.ids = s.map(it => it.couponId);
      this.multiple = !s.length;
    },
    handleAdd() {
      this.form = this.emptyForm();
      this.dialogTitle = "新增优惠券";
      this.dialogOpen = true;
    },
    handleEdit(row) {
      getCoupon(row.couponId).then(res => {
        const d = res.data || {};
        this.form = {
          ...this.emptyForm(),
          ...d,
          validRange: d.validStartDate && d.validEndDate
            ? [this.fmt(d.validStartDate), this.fmt(d.validEndDate)] : []
        };
        this.dialogTitle = "修改优惠券";
        this.dialogOpen = true;
      });
    },
    fmt(d) {
      if (!d) return null;
      const dt = new Date(d);
      const m = String(dt.getMonth() + 1).padStart(2, '0');
      const day = String(dt.getDate()).padStart(2, '0');
      return `${dt.getFullYear()}-${m}-${day}`;
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return;
        if (!this.form.validRange || this.form.validRange.length !== 2) {
          this.$modal.msgError("请选择有效期"); return;
        }
        const payload = { ...this.form };
        payload.validStartDate = this.form.validRange[0];
        payload.validEndDate = this.form.validRange[1];
        delete payload.validRange;
        const fn = payload.couponId ? updateCoupon : addCoupon;
        fn(payload).then(() => {
          this.$modal.msgSuccess(payload.couponId ? "修改成功" : "新增成功");
          this.dialogOpen = false;
          this.getList();
        });
      });
    },
    handleDelete(row) {
      const ids = row.couponId || this.ids;
      this.$modal.confirm('是否确认删除选中的优惠券？').then(() =>
        delCoupon(ids)
      ).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('gangzhu/coupon/export', { ...this.queryParams },
        `coupon_${new Date().getTime()}.xlsx`);
    },
    handleReceiveList(row) {
      this.receiveQuery = { pageNum: 1, pageSize: 10, couponId: row.couponId, receiveStatus: null };
      this.receiveOpen = true;
      this.loadReceiveList();
    },
    loadReceiveList() {
      this.receiveLoading = true;
      listCouponReceive(this.receiveQuery).then(res => {
        this.receiveList = res.rows;
        this.receiveTotal = res.total;
        this.receiveLoading = false;
      });
    }
  }
};
</script>
