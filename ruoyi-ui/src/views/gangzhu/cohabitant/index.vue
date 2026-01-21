<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="申请人姓名" prop="tenantName">
        <el-input
          v-model="queryParams.tenantName"
          placeholder="请输入申请人姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="审核状态" clearable>
          <el-option label="待审核" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="已拒绝" value="2" />
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

    <el-table v-loading="loading" :data="cohabitantList">
      <el-table-column label="合同编号" align="center" prop="contractNo" width="180" show-overflow-tooltip />
      <el-table-column label="申请人" align="center" prop="mainTenantName" width="100" />
      <el-table-column label="合租人姓名" align="center" prop="tenantName" width="100" />
      <el-table-column label="身份证号" align="center" prop="idCard" width="180" />
      <el-table-column label="联系电话" align="center" prop="phone" width="130" />
      <el-table-column label="与主承租人关系" align="center" prop="relationship" width="100" />
      <el-table-column label="房间地址" align="center" prop="roomAddress" min-width="200" show-overflow-tooltip />
      <el-table-column label="申请时间" align="center" prop="applyTime" width="110" />
      <el-table-column label="审核状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="success">已通过</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="danger">已拒绝</el-tag>
        </template>
      </el-table-column>
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
            icon="el-icon-check"
            @click="handleAudit(scope.row)"
            v-if="scope.row.status === '0'"
          >审核</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:cohabitant:remove']"
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
    <el-dialog title="合租户申请详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="合同编号">{{ detailForm.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detailForm.mainTenantName }}</el-descriptions-item>
        <el-descriptions-item label="合租人姓名">{{ detailForm.tenantName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailForm.idCard }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailForm.phone }}</el-descriptions-item>
        <el-descriptions-item label="与主承租人关系">{{ detailForm.relationship }}</el-descriptions-item>
        <el-descriptions-item label="房间地址">{{ detailForm.roomAddress }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailForm.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="入住时间">{{ detailForm.checkinDate }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="detailForm.status === '0'" type="warning">待审核</el-tag>
          <el-tag v-else-if="detailForm.status === '1'" type="success">已通过</el-tag>
          <el-tag v-else-if="detailForm.status === '2'" type="danger">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" v-if="detailForm.auditOpinion">
          {{ detailForm.auditOpinion }}
        </el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="detailForm.auditTime">
          {{ detailForm.auditTime }}
        </el-descriptions-item>
        <el-descriptions-item label="申请备注" v-if="detailForm.remark">
          {{ detailForm.remark }}
        </el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog title="审核合租户申请" :visible.sync="auditOpen" width="600px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="100px">
        <el-form-item label="合同编号">
          <el-input v-model="auditForm.contractNo" disabled />
        </el-form-item>
        <el-form-item label="申请人">
          <el-input :value="auditForm.mainTenantName + ' - ' + auditForm.phone" disabled />
        </el-form-item>
        <el-form-item label="合租人信息">
          <el-input :value="auditForm.tenantName + ' - ' + auditForm.idCard + ' - ' + auditForm.phone" disabled />
        </el-form-item>
        <el-form-item label="房间地址">
          <el-input v-model="auditForm.roomAddress" disabled />
        </el-form-item>
        <el-form-item label="入住时间">
          <el-input v-model="auditForm.checkinDate" disabled />
        </el-form-item>
        <el-form-item label="关系">
          <el-input v-model="auditForm.relationship" disabled />
        </el-form-item>
        <el-form-item label="申请时间">
          <el-input v-model="auditForm.applyTime" disabled />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="auditForm.remark" type="textarea" :rows="2" disabled />
        </el-form-item>
        <el-form-item label="审核结果" prop="status">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="auditOpinion">
          <el-input v-model="auditForm.auditOpinion" type="textarea" placeholder="请输入审核意见" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAudit">确 定</el-button>
        <el-button @click="auditOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCohabitant, getCohabitant, auditCohabitant, delCohabitant } from "@/api/gangzhu/cohabitant";

export default {
  name: "Cohabitant",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      cohabitantList: [],
      detailOpen: false,
      detailForm: {},
      auditOpen: false,
      auditForm: {},
      auditRules: {
        status: [
          { required: true, message: "请选择审核结果", trigger: "change" }
        ],
        auditOpinion: [
          { required: true, message: "请输入审核意见", trigger: "blur" }
        ]
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tenantName: null,
        status: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询合租户申请列表 */
    getList() {
      this.loading = true;
      listCohabitant(this.queryParams).then(response => {
        this.cohabitantList = response.rows;
        this.total = response.total;
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

    /** 详情操作 */
    handleDetail(row) {
      const coTenantId = row.coTenantId;
      getCohabitant(coTenantId).then(response => {
        this.detailForm = response.data;
        this.detailOpen = true;
      });
    },

    /** 审核操作 */
    handleAudit(row) {
      const coTenantId = row.coTenantId;
      getCohabitant(coTenantId).then(response => {
        this.auditForm = response.data;
        this.auditForm.status = '';
        this.auditForm.auditOpinion = '';
        this.auditOpen = true;
        this.$nextTick(() => {
          this.$refs.auditForm && this.$refs.auditForm.clearValidate();
        });
      });
    },

    /** 提交审核 */
    submitAudit() {
      this.$refs["auditForm"].validate(valid => {
        if (valid) {
          const auditData = {
            coTenantId: this.auditForm.coTenantId,
            status: this.auditForm.status,
            remark: this.auditForm.auditOpinion
          };

          auditCohabitant(auditData).then(response => {
            this.$modal.msgSuccess("审核成功");
            this.auditOpen = false;
            this.getList();
          });
        }
      });
    },

    /** 删除操作 */
    handleDelete(row) {
      this.$modal.confirm('是否确认删除该合租人申请？').then(() => {
        return delCohabitant(row.coTenantId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
