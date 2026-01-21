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
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input
          v-model="queryParams.contactPhone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="退租状态" prop="checkoutStatus">
        <el-select v-model="queryParams.checkoutStatus" placeholder="请选择" clearable>
          <el-option label="已申请" value="0" />
          <el-option label="已处理" value="1" />
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
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:enterpriseCheckout:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="checkoutList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="退租编号" align="center" prop="checkoutNo" width="170" show-overflow-tooltip />
      <el-table-column label="批次名称" align="center" prop="batchName" min-width="140" show-overflow-tooltip />
      <el-table-column label="企业名称" align="center" prop="enterpriseName" min-width="140" show-overflow-tooltip />
      <el-table-column label="联系人" align="center" prop="contactPerson" width="100" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="130" />
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
      <el-table-column label="退租状态" align="center" prop="checkoutStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.checkoutStatus === '0'" type="warning">已申请</el-tag>
          <el-tag v-else-if="scope.row.checkoutStatus === '1'" type="success">已处理</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="applyTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDeleteItem(scope.row)"
            v-hasPermi="['system:enterpriseCheckout:remove']"
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

    <!-- 详情对话框 -->
    <el-dialog title="企业退租申请详情" :visible.sync="viewOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="退租编号">{{ viewData.checkoutNo }}</el-descriptions-item>
        <el-descriptions-item label="批次名称">{{ viewData.batchName }}</el-descriptions-item>
        <el-descriptions-item label="企业名称">{{ viewData.enterpriseName }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ viewData.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ viewData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="房源数量">{{ viewData.houseCount }} 套</el-descriptions-item>
        <el-descriptions-item label="项目名称" :span="2">{{ viewData.projectName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入驻日期">{{ parseTime(viewData.checkInDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="截止日期">{{ parseTime(viewData.checkOutDate, '{y}-{m}-{d}') }}</el-descriptions-item>
        <el-descriptions-item label="计算月数">{{ viewData.months }} 个月</el-descriptions-item>
        <el-descriptions-item label="单价合计">¥{{ viewData.totalRent }}/月</el-descriptions-item>
        <el-descriptions-item label="计算总价">¥{{ viewData.totalAmount }}</el-descriptions-item>
        <el-descriptions-item label="优惠金额">¥{{ viewData.discountAmount }}</el-descriptions-item>
        <el-descriptions-item label="优惠后总价">
          <span style="color: #f56c6c; font-weight: bold; font-size: 16px">¥{{ viewData.finalAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="退租状态">
          <el-tag v-if="viewData.checkoutStatus === '0'" type="warning">已申请</el-tag>
          <el-tag v-else-if="viewData.checkoutStatus === '1'" type="success">已处理</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ parseTime(viewData.applyTime, '{y}-{m}-{d} {h}:{i}') }}</el-descriptions-item>
        <el-descriptions-item label="房源信息" :span="2">
          <span v-if="parseHouseInfo(viewData.houseInfo).length > 0">
            {{ parseHouseInfo(viewData.houseInfo).join(', ') }}
          </span>
          <span v-else style="color: #909399">暂无</span>
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
import { listEnterpriseCheckout, getEnterpriseCheckout, delEnterpriseCheckout } from "@/api/gangzhu/enterprise";

export default {
  name: "EnterpriseCheckout",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      checkoutList: [],
      viewOpen: false,
      viewData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        batchName: null,
        enterpriseName: null,
        contactPhone: null,
        checkoutStatus: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询企业退租申请列表 */
    getList() {
      this.loading = true;
      listEnterpriseCheckout(this.queryParams).then(response => {
        this.checkoutList = response.rows || response.data || [];
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
      this.ids = selection.map(item => item.checkoutId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 详情按钮操作 */
    handleView(row) {
      getEnterpriseCheckout(row.checkoutId).then(response => {
        this.viewData = response.data;
        // 计算单价合计
        if (this.viewData.houseCount && this.viewData.totalAmount && this.viewData.months) {
          this.viewData.totalRent = (parseFloat(this.viewData.totalAmount) / this.viewData.months).toFixed(2);
        }
        this.viewOpen = true;
      });
    },
    /** 删除按钮操作 */
    handleDelete() {
      const checkoutIds = this.ids;
      this.$modal.confirm('是否确认删除选中的退租申请记录？').then(() => {
        return delEnterpriseCheckout(checkoutIds.join(','));
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 单个删除 */
    handleDeleteItem(row) {
      this.$modal.confirm('是否确认删除该退租申请记录？').then(() => {
        return delEnterpriseCheckout(row.checkoutId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 解析房源信息JSON */
    parseHouseInfo(houseInfo) {
      if (!houseInfo) return [];
      try {
        const data = JSON.parse(houseInfo);
        if (data.houseNos && Array.isArray(data.houseNos)) {
          return data.houseNos;
        }
        return [];
      } catch (e) {
        return [];
      }
    }
  }
};
</script>

<style scoped>
</style>
