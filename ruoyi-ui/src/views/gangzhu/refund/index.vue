<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="退款编号" prop="refundNo">
        <el-input
          v-model="queryParams.refundNo"
          placeholder="请输入退款编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同编号" prop="contractNo">
        <el-input
          v-model="queryParams.contractNo"
          placeholder="请输入合同编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="退款状态" prop="refundStatus">
        <el-select v-model="queryParams.refundStatus" placeholder="退款状态" clearable>
          <el-option
            v-for="dict in dict.type.refund_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择项目" clearable filterable style="width: 200px">
          <el-option
            v-for="project in projectList"
            :key="project.projectId"
            :label="project.projectName"
            :value="project.projectId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="退款类型" prop="refundType">
        <el-select v-model="queryParams.refundType" placeholder="请选择类型" clearable style="width: 180px">
          <el-option label="退租退款" value="checkout" />
          <el-option label="入住超时自动退款" value="auto-cancel-checkin" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="refundList">
      <el-table-column label="退款编号" align="center" prop="refundNo" width="100" />
      <el-table-column label="合同编号" align="center" prop="contractNo" min-width="150" show-overflow-tooltip />
      <el-table-column label="退款类型" align="center" prop="refundType" width="140">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.refundType === 'auto-cancel-checkin'" type="danger" size="small">入住超时自动退款</el-tag>
          <el-tag v-else type="info" size="small">{{ scope.row.refundTypeText || '退租退款' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="退款金额" align="center" prop="refundAmount" width="120">
        <template slot-scope="scope">
          <span style="color: #f56c6c; font-weight: bold;">{{ scope.row.refundAmount }}元</span>
        </template>
      </el-table-column>
      <el-table-column label="退款原因" align="center" prop="refundReason" min-width="150" show-overflow-tooltip />
      <el-table-column label="退款状态" align="center" prop="refundStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.refundStatus === '0'" type="warning">待退还</el-tag>
          <el-tag v-else-if="scope.row.refundStatus === '1'" type="success">已退还</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="applyTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-money"
            style="color: #07c160;"
            @click="handleWechatRefund(scope.row)"
            v-if="scope.row.refundStatus === '0'"
            v-hasPermi="['gangzhu:refund:payment']"
          >微信退款</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handlePaymentInfo(scope.row)"
            v-if="scope.row.refundStatus === '0'"
            v-hasPermi="['gangzhu:refund:payment']"
          >其他方式</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:refund:remove']"
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
    <el-dialog title="退款详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <!-- 申请信息 -->
      <el-divider content-position="left">申请信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="退款编号">{{ detailForm.refundNo }}</el-descriptions-item>
        <el-descriptions-item label="合同编号">{{ detailForm.contractNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="退款金额">
          <span style="color: #f56c6c; font-weight: bold; font-size: 16px;">¥{{ detailForm.refundAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="退款状态">
          <el-tag v-if="detailForm.refundStatus === '0'" type="warning">待退还</el-tag>
          <el-tag v-else-if="detailForm.refundStatus === '1'" type="success">已退还</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="退款原因" :span="2">{{ detailForm.refundReason || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailForm.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detailForm.approveBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间" :span="2">{{ detailForm.approveTime || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 费用明细 -->
      <el-divider content-position="left">费用明细</el-divider>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="水费">{{ detailForm.waterFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="电费">{{ detailForm.electricFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="燃气费">{{ detailForm.gasFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="暖气费">{{ detailForm.heatingFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="物业费">{{ detailForm.propertyFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="损坏扣款">{{ detailForm.damageDeduction || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="违约金">{{ detailForm.penaltyAmount || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="押金">{{ detailForm.deposit || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="应退押金">
          <span style="color: #67c23a; font-weight: bold;">¥{{ depositRefundAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="应退租金">
          <span style="color: #67c23a; font-weight: bold;">¥{{ rentRefundAmount }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="应退总额">
          <span style="font-size: 16px; color: #f56c6c; font-weight: bold;">¥{{ detailForm.refundAmount || 0 }}</span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 退款拆分明细（按账单维度溯源） -->
      <el-divider content-position="left">退款拆分明细</el-divider>
      <el-alert
        v-if="!depositBills.length && !paidRentBills.length"
        title="未查询到该合同的已付账单（可能是历史数据或老合同）"
        type="info" :closable="false" show-icon style="margin-bottom: 10px;">
      </el-alert>
      <el-table
        v-else
        :data="splitRows"
        border size="small"
        :show-summary="false"
        style="width: 100%;">
        <el-table-column label="账单号" prop="billNo" width="180" show-overflow-tooltip />
        <el-table-column label="账单类型" prop="billTypeText" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.billType === '1'" type="warning" size="mini">押金</el-tag>
            <el-tag v-else type="info" size="mini">租金</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="原支付方式" prop="payMethod" width="100" align="center">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.payMethod === 'wechat'" type="success" size="mini">微信</el-tag>
            <el-tag v-else-if="scope.row.payMethod" type="info" size="mini">{{ scope.row.payMethod }}</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="原付款金额" prop="paidAmount" width="110" align="right">
          <template slot-scope="scope">¥{{ scope.row.paidAmount || 0 }}</template>
        </el-table-column>
        <el-table-column label="原微信交易号" prop="transactionNo" min-width="200" show-overflow-tooltip>
          <template slot-scope="scope">{{ scope.row.transactionNo || '-' }}</template>
        </el-table-column>
        <el-table-column label="付款时间" prop="payTime" width="160" />
      </el-table>
      <div v-if="splitRows.length" style="margin-top: 8px; color: #909399; font-size: 12px;">
        提示：微信原路退款将按上述账单分别发起退款请求（押金笔退至原押金支付通道，租金笔退至对应租金账单）。
      </div>

      <!-- 付款信息 -->
      <el-divider content-position="left">付款信息</el-divider>
      <el-descriptions :column="2" border v-if="detailForm.refundStatus === '1'">
        <el-descriptions-item label="付款方式">{{ detailForm.paymentMethodText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="付款时间">{{ detailForm.paymentTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="付款备注" :span="2">{{ detailForm.paymentRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="付款凭证" :span="2" v-if="detailForm.paymentVoucher">
          <el-image
            v-if="detailForm.paymentVoucher"
            style="width: 200px; height: 200px;"
            :src="getImageUrl(detailForm.paymentVoucher)"
            :preview-src-list="[getImageUrl(detailForm.paymentVoucher)]"
            fit="contain"
          />
        </el-descriptions-item>
      </el-descriptions>
      <el-empty v-else description="暂未提交付款信息" :image-size="100"></el-empty>
    </el-dialog>

    <!-- 付款信息对话框 -->
    <el-dialog title="提交付款信息" :visible.sync="paymentOpen" width="500px" append-to-body>
      <el-form ref="paymentFormRef" :model="paymentForm" :rules="paymentRules" label-width="100px">
        <el-form-item label="退款编号">
          <el-input v-model="paymentForm.refundNo" :disabled="true" />
        </el-form-item>
        <el-form-item label="退款金额">
          <el-input v-model="paymentForm.refundAmount" :disabled="true">
            <template slot="append">元</template>
          </el-input>
        </el-form-item>
        <el-form-item label="付款方式" prop="paymentMethod">
          <el-select v-model="paymentForm.paymentMethod" placeholder="请选择付款方式">
            <el-option label="现金" value="1"></el-option>
            <el-option label="支付宝" value="2"></el-option>
            <el-option label="微信" value="3"></el-option>
            <el-option label="银行转账" value="4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="付款凭证" prop="paymentVoucher">
          <image-upload
            v-model="paymentForm.paymentVoucher"
            :limit="1"
          />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="paymentForm.remark" type="textarea" placeholder="请输入备注" :rows="2" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitPayment">确 定</el-button>
        <el-button @click="paymentOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRefund, getRefund, submitPayment, wechatRefund, delRefund } from "@/api/gangzhu/refund";
import { listProject } from "@/api/gangzhu/project";
import { getContractBills } from "@/api/gangzhu/checkout";

export default {
  name: "Refund",
  dicts: ['refund_status'],
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      refundList: [],
      projectList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        refundNo: null,
        contractNo: null,
        refundStatus: null,
        projectId: null,
        refundType: null
      },
      // 详情
      detailOpen: false,
      detailForm: {},
      // 详情对应合同的账单列表（用于退款拆分溯源）
      billList: [],
      // 付款信息
      paymentOpen: false,
      paymentForm: {
        refundId: null,
        refundNo: null,
        refundAmount: null,
        paymentMethod: null,
        paymentVoucher: null,
        remark: null
      },
      paymentRules: {
        paymentMethod: [
          { required: true, message: "付款方式不能为空", trigger: "change" }
        ],
        paymentVoucher: [
          { required: true, message: "付款凭证不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getProjectList();
  },
  computed: {
    // 应退押金（来自后端 hz_checkout_apply.deposit_refund）
    depositRefundAmount() {
      const v = Number(this.detailForm.depositRefund);
      return Number.isFinite(v) && v > 0 ? v : 0;
    },
    // 应退租金 = 总应退 - 应退押金（保留 2 位）
    rentRefundAmount() {
      const total = Number(this.detailForm.refundAmount) || 0;
      const rent = total - this.depositRefundAmount;
      return rent > 0 ? Math.round(rent * 100) / 100 : 0;
    },
    // 已付押金账单（billType=1, billStatus=1）
    depositBills() {
      return (this.billList || []).filter(b => b.billType === '1' && b.billStatus === '1');
    },
    // 已付租金账单（billType=2, billStatus=1）
    paidRentBills() {
      return (this.billList || []).filter(b => b.billType === '2' && b.billStatus === '1');
    },
    // 退款拆分明细行（押金笔在前，租金笔按付款时间升序）
    splitRows() {
      const sortByTime = (a, b) => {
        const ta = a.payTime ? new Date(a.payTime).getTime() : 0;
        const tb = b.payTime ? new Date(b.payTime).getTime() : 0;
        return ta - tb;
      };
      return [
        ...this.depositBills.slice().sort(sortByTime),
        ...this.paidRentBills.slice().sort(sortByTime)
      ];
    }
  },
  methods: {
    getProjectList() {
      listProject({ pageNum: 1, pageSize: 1000, status: "0" }).then(response => {
        this.projectList = response.rows || response.data || [];
      });
    },
    getList() {
      this.loading = true;
      listRefund(this.queryParams).then(response => {
        this.refundList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 查看详情
    handleDetail(row) {
      getRefund(row.refundId).then(response => {
        this.detailForm = response.data || {};
        this.billList = [];
        // 拉取合同账单列表用于退款拆分溯源
        if (this.detailForm.contractId) {
          getContractBills(this.detailForm.contractId).then(res => {
            this.billList = res.data || [];
          }).catch(() => {
            this.billList = [];
          });
        }
        this.detailOpen = true;
      });
    },
    // 微信原路退款
    handleWechatRefund(row) {
      // 拉取账单明细组装弹窗文案，用于二次确认
      const buildAndConfirm = (bills) => {
        const depositBills = bills.filter(b => b.billType === '1' && b.billStatus === '1');
        const rentBills = bills.filter(b => b.billType === '2' && b.billStatus === '1');
        const lines = [];
        lines.push(`合同编号：${row.contractNo || '-'}`);
        lines.push(`总退款金额：¥${row.refundAmount} 元`);
        if (depositBills.length || rentBills.length) {
          lines.push('');
          lines.push('将按以下账单原路退款：');
          depositBills.forEach(b => {
            lines.push(`  · 押金账单 ${b.billNo}：¥${b.paidAmount}（${b.payMethod === 'wechat' ? '微信' : (b.payMethod || '未知')}）`);
          });
          rentBills.forEach(b => {
            lines.push(`  · 租金账单 ${b.billNo}：¥${b.paidAmount}（${b.payMethod === 'wechat' ? '微信' : (b.payMethod || '未知')}）`);
          });
        } else {
          lines.push('');
          lines.push('（未查询到已付账单明细，将直接按总额发起退款）');
        }
        lines.push('');
        lines.push('退款后状态将自动更新为"已退还"，操作不可撤销。');
        const message = lines.join('\n');
        this.$confirm(message, '微信原路退款确认', {
          confirmButtonText: '确认退款',
          cancelButtonText: '取消',
          type: 'warning',
          customClass: 'refund-confirm-dialog',
          dangerouslyUseHTMLString: false
        }).then(() => {
          const loading = this.$loading({ lock: true, text: '退款处理中...', background: 'rgba(0,0,0,0.7)' });
          wechatRefund(row.refundId).then(res => {
            loading.close();
            this.$modal.msgSuccess(res.msg || '微信退款申请成功，预计2分钟内到账');
            this.getList();
          }).catch(() => {
            loading.close();
          });
        }).catch(() => {});
      };

      if (row.contractId) {
        getContractBills(row.contractId).then(res => {
          buildAndConfirm(res.data || []);
        }).catch(() => {
          buildAndConfirm([]);
        });
      } else {
        buildAndConfirm([]);
      }
    },
    // 提交付款信息
    handlePaymentInfo(row) {
      this.paymentForm = {
        refundId: row.refundId,
        refundNo: row.refundNo,
        refundAmount: row.refundAmount,
        paymentMethod: null,
        paymentVoucher: null,
        remark: null
      };
      this.paymentOpen = true;
      this.$nextTick(() => {
        this.resetForm("paymentFormRef");
      });
    },
    // 提交付款
    submitPayment() {
      this.$refs.paymentFormRef.validate(valid => {
        if (valid) {
          const data = {
            refundId: this.paymentForm.refundId,
            paymentMethod: this.paymentForm.paymentMethod,
            paymentVoucher: this.paymentForm.paymentVoucher,
            paymentRemark: this.paymentForm.remark
          };
          submitPayment(data).then(() => {
            this.$modal.msgSuccess("付款信息提交成功");
            this.paymentOpen = false;
            this.getList();
          });
        }
      });
    },
    // 获取图片URL
    getImageUrl(url) {
      if (!url) return '';
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }
      return process.env.VUE_APP_BASE_API + url;
    },
    // 删除操作
    handleDelete(row) {
      this.$modal.confirm('是否确认删除退款编号为"' + row.refundNo + '"的数据项？').then(() => {
        return delRefund(row.refundId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
::v-deep .el-divider__text {
  background-color: #f5f7fa;
}
</style>
<style>
.refund-confirm-dialog .el-message-box__message {
  white-space: pre-wrap;
  font-size: 13px;
  line-height: 1.7;
  max-height: 400px;
  overflow-y: auto;
}
</style>
