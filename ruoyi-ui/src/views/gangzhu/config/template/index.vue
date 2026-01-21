<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模版名称" prop="templateName">
        <el-input
          v-model="queryParams.templateName"
          placeholder="请输入模版名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同类型" prop="contractType">
        <el-select v-model="queryParams.contractType" placeholder="请选择合同类型" clearable>
          <el-option label="人才公寓" value="1" />
          <el-option label="保租房" value="2" />
          <el-option label="市场租赁" value="3" />
        </el-select>
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
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['gangzhu:template:add']"
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
          v-hasPermi="['gangzhu:template:edit']"
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
          v-hasPermi="['gangzhu:template:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模版名称" align="center" prop="templateName" min-width="150" />
      <el-table-column label="模版编码" align="center" prop="templateCode" width="120" />
      <el-table-column label="合同类型" align="center" prop="contractType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hz_contract_type" :value="scope.row.contractType"/>
        </template>
      </el-table-column>
      <el-table-column label="付款周期" align="center" prop="paymentCycle" width="100">
        <template slot-scope="scope">
          {{ formatPaymentCycle(scope.row.paymentCycle) }}
        </template>
      </el-table-column>
      <el-table-column label="合同期限" align="center" prop="contractDuration" width="100">
        <template slot-scope="scope">
          {{ scope.row.contractDuration }}个月
        </template>
      </el-table-column>
      <el-table-column label="押金规则" align="center" prop="depositMonths" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.depositMonths">押{{ scope.row.depositMonths }}个月</span>
          <span v-else-if="scope.row.depositAmount">固定{{ scope.row.depositAmount }}元</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="违约金" align="center" prop="penaltyRate" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.penaltyRate">{{ scope.row.penaltyRate }}%</span>
          <span v-else-if="scope.row.penaltyAmount">固定{{ scope.row.penaltyAmount }}元</span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="版本" align="center" prop="version" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="220">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['gangzhu:template:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:template:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:template:remove']"
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

    <!-- 添加或修改合同模版对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="模版名称" prop="templateName">
              <el-input v-model="form.templateName" placeholder="请输入模版名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模版编码" prop="templateCode">
              <el-input v-model="form.templateCode" placeholder="请输入模版编码" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="合同类型" prop="contractType">
              <el-select v-model="form.contractType" placeholder="请选择合同类型" style="width: 100%">
                <el-option label="人才公寓" value="1" />
                <el-option label="保租房" value="2" />
                <el-option label="市场租赁" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="付款周期" prop="paymentCycle">
              <el-select v-model="form.paymentCycle" placeholder="请选择付款周期" style="width: 100%">
                <el-option label="按月" value="monthly" />
                <el-option label="按季" value="quarterly" />
                <el-option label="半年" value="half_yearly" />
                <el-option label="按年" value="yearly" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="合同期限" prop="contractDuration">
              <el-input-number v-model="form.contractDuration" :min="1" :max="120" placeholder="请输入合同期限（月）" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本号" prop="version">
              <el-input v-model="form.version" placeholder="请输入版本号" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="押金方式">
              <el-radio-group v-model="depositType">
                <el-radio label="months">按月数</el-radio>
                <el-radio label="amount">固定金额</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="押金月数" v-if="depositType === 'months'">
              <el-input-number v-model="form.depositMonths" :min="0" :max="12" :precision="1" placeholder="请输入押金月数" style="width: 100%" />
            </el-form-item>
            <el-form-item label="押金金额（元）" v-if="depositType === 'amount'">
              <el-input-number v-model="form.depositAmount" :min="0" :precision="2" placeholder="请输入押金金额" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="违约金方式">
              <el-radio-group v-model="penaltyType">
                <el-radio label="rate">按比例</el-radio>
                <el-radio label="amount">固定金额</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="12">
            <el-form-item label="违约金比例（%）" v-if="penaltyType === 'rate'">
              <el-input-number v-model="form.penaltyRate" :min="0" :max="100" :precision="2" placeholder="请输入违约金比例" style="width: 100%" />
            </el-form-item>
            <el-form-item label="违约金金额（元）" v-if="penaltyType === 'amount'">
              <el-input-number v-model="form.penaltyAmount" :min="0" :precision="2" placeholder="请输入违约金金额" style="width: 100%" />
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

        <el-row>
          <el-col :span="24">
            <el-form-item label="合同模版内容" prop="templateContent">
              <editor v-model="form.templateContent" :min-height="300"/>
              <div class="template-tip">
                <p>变量说明：可在合同内容中使用以下变量，系统将自动替换为实际值</p>
                <el-tag size="mini" style="margin-right: 5px">${{tenantName}}</el-tag>租户姓名
                <el-tag size="mini" style="margin: 0 5px">${{tenantIdCard}}</el-tag>租户身份证号
                <el-tag size="mini" style="margin: 0 5px">${{tenantPhone}}</el-tag>租户电话
                <el-tag size="mini" style="margin: 0 5px">${{houseAddress}}</el-tag>房源地址
                <el-tag size="mini" style="margin: 0 5px">${{rentPrice}}</el-tag>租金
                <el-tag size="mini" style="margin: 0 5px">${{deposit}}</el-tag>押金
                <el-tag size="mini" style="margin: 0 5px">${{startDate}}</el-tag>开始日期
                <el-tag size="mini" style="margin: 0 5px">${{endDate}}</el-tag>结束日期
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看合同模版详情对话框 -->
    <el-dialog title="合同模版详情" :visible.sync="viewOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="模版名称">{{ viewData.templateName }}</el-descriptions-item>
        <el-descriptions-item label="模版编码">{{ viewData.templateCode }}</el-descriptions-item>
        <el-descriptions-item label="合同类型">
          <dict-tag :options="dict.type.hz_contract_type" :value="viewData.contractType"/>
        </el-descriptions-item>
        <el-descriptions-item label="付款周期">
          {{ formatPaymentCycle(viewData.paymentCycle) }}
        </el-descriptions-item>
        <el-descriptions-item label="合同期限">
          {{ viewData.contractDuration }}个月
        </el-descriptions-item>
        <el-descriptions-item label="押金规则">
          <span v-if="viewData.depositMonths">押{{ viewData.depositMonths }}个月</span>
          <span v-else-if="viewData.depositAmount">固定{{ viewData.depositAmount }}元</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="违约金">
          <span v-if="viewData.penaltyRate">{{ viewData.penaltyRate }}%</span>
          <span v-else-if="viewData.penaltyAmount">固定{{ viewData.penaltyAmount }}元</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="版本号">{{ viewData.version }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="dict.type.sys_normal_disable" :value="viewData.status"/>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ parseTime(viewData.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
        </el-descriptions-item>
        <el-descriptions-item label="更新时间">
          {{ parseTime(viewData.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">合同模版内容</el-divider>
      <div class="template-content-view" v-html="viewData.templateContent"></div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTemplate, getTemplate, delTemplate, addTemplate, updateTemplate } from "@/api/gangzhu/template";
import Editor from '@/components/Editor';

export default {
  name: "Template",
  dicts: ['hz_contract_type', 'sys_normal_disable'],
  components: {
    Editor
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 合同模版表格数据
      templateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      viewOpen: false,
      // 详情数据
      viewData: {},
      // 押金方式
      depositType: 'months',
      // 违约金方式
      penaltyType: 'rate',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateName: null,
        contractType: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        templateName: [
          { required: true, message: "模版名称不能为空", trigger: "blur" }
        ],
        templateCode: [
          { required: true, message: "模版编码不能为空", trigger: "blur" }
        ],
        contractType: [
          { required: true, message: "合同类型不能为空", trigger: "change" }
        ],
        paymentCycle: [
          { required: true, message: "付款周期不能为空", trigger: "change" }
        ],
        contractDuration: [
          { required: true, message: "合同期限不能为空", trigger: "blur" }
        ],
        templateContent: [
          { required: true, message: "合同模版内容不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询合同模版列表 */
    getList() {
      this.loading = true;
      listTemplate(this.queryParams).then(response => {
        this.templateList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        templateId: null,
        templateName: null,
        templateCode: null,
        contractType: null,
        templateContent: null,
        paymentCycle: null,
        contractDuration: null,
        depositMonths: null,
        depositAmount: null,
        penaltyRate: null,
        penaltyAmount: null,
        version: "1.0",
        isDefault: "0",
        status: "0",
        remark: null
      };
      this.depositType = 'months';
      this.penaltyType = 'rate';
      this.resetForm("form");
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.templateId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加合同模版";
    },
    /** 查看详情按钮操作 */
    handleView(row) {
      const templateId = row.templateId || this.ids;
      getTemplate(templateId).then(response => {
        this.viewData = response.data;
        this.viewOpen = true;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const templateId = row.templateId || this.ids
      getTemplate(templateId).then(response => {
        this.form = response.data;

        // 判断押金方式
        if (this.form.depositMonths != null && this.form.depositMonths > 0) {
          this.depositType = 'months';
        } else if (this.form.depositAmount != null && this.form.depositAmount > 0) {
          this.depositType = 'amount';
        }

        // 判断违约金方式
        if (this.form.penaltyRate != null && this.form.penaltyRate > 0) {
          this.penaltyType = 'rate';
        } else if (this.form.penaltyAmount != null && this.form.penaltyAmount > 0) {
          this.penaltyType = 'amount';
        }

        this.open = true;
        this.title = "修改合同模版";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 根据选择的方式清空另一个字段
          if (this.depositType === 'months') {
            this.form.depositAmount = null;
          } else {
            this.form.depositMonths = null;
          }

          if (this.penaltyType === 'rate') {
            this.form.penaltyAmount = null;
          } else {
            this.form.penaltyRate = null;
          }

          if (this.form.templateId != null) {
            updateTemplate(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addTemplate(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const templateIds = row.templateId || this.ids;
      this.$modal.confirm('是否确认删除合同模版编号为"' + templateIds + '"的数据项？').then(function() {
        return delTemplate(templateIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 格式化付款周期 */
    formatPaymentCycle(cycle) {
      const map = {
        'monthly': '按月',
        'quarterly': '按季',
        'half_yearly': '半年',
        'yearly': '按年'
      };
      return map[cycle] || cycle;
    }
  }
};
</script>

<style scoped>
.template-tip {
  margin-top: 10px;
  padding: 10px;
  background-color: #f4f4f5;
  border-radius: 4px;
  font-size: 12px;
  color: #606266;
}
.template-tip p {
  margin: 0 0 8px 0;
  font-weight: bold;
}
.template-content-view {
  padding: 20px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background-color: #fff;
  max-height: 500px;
  overflow-y: auto;
  line-height: 1.8;
}
.template-content-view >>> h2,
.template-content-view >>> h3 {
  margin-top: 20px;
  margin-bottom: 10px;
  color: #303133;
}
.template-content-view >>> p {
  margin: 8px 0;
  color: #606266;
}
.template-content-view >>> strong {
  color: #E6A23C;
}
</style>
