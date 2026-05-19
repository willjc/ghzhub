<template>
  <div class="app-container">
    <!-- 筛选区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="84px">
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部" clearable style="width: 140px">
          <el-option label="待审核" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="已驳回" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="资料类型" prop="documentType">
        <el-select v-model="queryParams.documentType" placeholder="全部" clearable style="width: 140px">
          <el-option label="身份证" value="1" />
          <el-option label="学历证明" value="2" />
          <el-option label="工作证明" value="3" />
          <el-option label="收入证明" value="4" />
          <el-option label="人才证书" value="5" />
          <el-option label="其他" value="6" />
        </el-select>
      </el-form-item>
      <el-form-item label="用户姓名" prop="tenantName">
        <el-input v-model="queryParams.tenantName" placeholder="请输入姓名" clearable style="width: 160px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="合同编号" prop="contractNo">
        <el-input v-model="queryParams.contractNo" placeholder="请输入合同号" clearable style="width: 200px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="上传时间">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 列表 -->
    <el-table v-loading="loading" :data="documentList" border>
      <el-table-column label="用户姓名" align="center" prop="tenantName" min-width="100" />
      <el-table-column label="手机号" align="center" prop="tenantPhone" min-width="120" />
      <el-table-column label="合同编号" align="center" prop="contractNo" min-width="170">
        <template slot-scope="scope">
          <span v-if="scope.row.contractNo">{{ scope.row.contractNo }}</span>
          <el-tag v-else size="mini" type="info">未关联合同</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="项目-房源" align="center" min-width="220" show-overflow-tooltip>
        <template slot-scope="scope">
          <span v-if="scope.row.projectName">
            {{ scope.row.projectName }}
            <span v-if="scope.row.buildingName"> / {{ scope.row.buildingName }}</span>
            <span v-if="scope.row.unitName"> / {{ scope.row.unitName }}</span>
            <span v-if="scope.row.houseNo"> / {{ scope.row.houseNo }}</span>
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="资料类型" align="center" prop="documentType" width="100">
        <template slot-scope="scope">
          <el-tag :type="docTypeTagType(scope.row.documentType)" size="mini">
            {{ docTypeLabel(scope.row.documentType) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="文件预览" align="center" width="100">
        <template slot-scope="scope">
          <el-image
            v-if="scope.row.filePath"
            style="width: 60px; height: 60px; cursor: pointer; border-radius: 4px;"
            :src="resolveUrl(scope.row.filePath)"
            :preview-src-list="[resolveUrl(scope.row.filePath)]"
            fit="cover"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="上传时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="审核状态" align="center" prop="auditStatus" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.auditStatus === '0'" type="warning" size="mini">待审核</el-tag>
          <el-tag v-else-if="scope.row.auditStatus === '1'" type="success" size="mini">已通过</el-tag>
          <el-tag v-else-if="scope.row.auditStatus === '2'" type="danger" size="mini">已驳回</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.auditStatus === '0'"
            size="mini" type="text" icon="el-icon-check" style="color:#67C23A;"
            @click="handleApprove(scope.row)"
            v-hasPermi="['gangzhu:document:audit']"
          >通过</el-button>
          <el-button
            v-if="scope.row.auditStatus !== '2'"
            size="mini" type="text" icon="el-icon-close" style="color:#F56C6C;"
            @click="handleReject(scope.row)"
            v-hasPermi="['gangzhu:document:audit']"
          >驳回</el-button>
          <el-button
            size="mini" type="text" icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:document:query']"
          >详情</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total > 0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 驳回弹窗 -->
    <el-dialog title="驳回资料" :visible.sync="rejectOpen" width="520px" append-to-body>
      <el-alert type="info" :closable="false" show-icon style="margin-bottom: 16px;"
        title="驳回后将发送站内通知给用户，不会影响已签订的合同和账单。" />
      <el-form :model="rejectForm" label-width="90px">
        <el-form-item label="驳回原因">
          <el-radio-group v-model="rejectForm.auditOpinion" style="display:flex;flex-direction:column;gap:12px;">
            <el-radio label="请重新上传工作证明">请重新上传工作证明</el-radio>
            <el-radio label="经审核，您不符合郑州航空港区人才公寓申请政策，请于3个工作日内退房。">经审核，您不符合郑州航空港区人才公寓申请政策，请于3个工作日内退房</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="danger" @click="submitReject">确认驳回</el-button>
        <el-button @click="rejectOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog title="资料详情" :visible.sync="detailOpen" width="640px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户姓名">{{ detailData.tenantName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.tenantPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailData.tenantIdCard || '-' }}</el-descriptions-item>
        <el-descriptions-item label="资料类型">{{ docTypeLabel(detailData.documentType) }}</el-descriptions-item>
        <el-descriptions-item label="合同编号">{{ detailData.contractNo || '未关联' }}</el-descriptions-item>
        <el-descriptions-item label="上传时间">{{ parseTime(detailData.createTime, '{y}-{m}-{d} {h}:{i}') }}</el-descriptions-item>
        <el-descriptions-item label="项目-房源" :span="2">
          {{ detailData.projectName || '-' }}
          <span v-if="detailData.buildingName"> / {{ detailData.buildingName }}</span>
          <span v-if="detailData.unitName"> / {{ detailData.unitName }}</span>
          <span v-if="detailData.houseNo"> / {{ detailData.houseNo }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="detailData.auditStatus === '0'" type="warning" size="mini">待审核</el-tag>
          <el-tag v-else-if="detailData.auditStatus === '1'" type="success" size="mini">已通过</el-tag>
          <el-tag v-else-if="detailData.auditStatus === '2'" type="danger" size="mini">已驳回</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="驳回原因">{{ detailData.auditOpinion || '-' }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 16px; text-align: center;">
        <el-image
          v-if="detailData.filePath"
          style="max-width: 100%; max-height: 480px;"
          :src="resolveUrl(detailData.filePath)"
          :preview-src-list="[resolveUrl(detailData.filePath)]"
          fit="contain"
        />
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button v-if="detailData.auditStatus === '0'" type="success" @click="handleApprove(detailData); detailOpen = false">通过</el-button>
        <el-button v-if="detailData.auditStatus !== '2'" type="danger" @click="handleReject(detailData); detailOpen = false">驳回</el-button>
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDocument, auditDocument } from "@/api/gangzhu/document";

export default {
  name: "DocumentAudit",
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      documentList: [],
      detailOpen: false,
      detailData: {},
      rejectOpen: false,
      rejectForm: { documentId: null, auditOpinion: "" },
      dateRange: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        auditStatus: null,
        documentType: null,
        tenantName: null,
        contractNo: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      const params = { ...this.queryParams };
      if (this.dateRange && this.dateRange.length === 2) {
        params.startTime = this.dateRange[0];
        params.endTime = this.dateRange[1];
      }
      listDocument(params).then(res => {
        this.documentList = res.rows || [];
        this.total = res.total || 0;
        this.loading = false;
      }).catch(() => { this.loading = false; });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleDetail(row) {
      this.detailData = { ...row };
      this.detailOpen = true;
    },
    /** 通过审核 */
    handleApprove(row) {
      this.$confirm('确认通过该资料审核？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'success'
      }).then(() => {
        auditDocument({
          documentId: row.documentId,
          auditStatus: '1',
          auditOpinion: '审核通过'
        }).then(() => {
          this.$modal.msgSuccess('已通过');
          this.getList();
        });
      }).catch(() => {});
    },
    /** 驳回：打开弹窗 */
    handleReject(row) {
      this.rejectForm = { documentId: row.documentId, auditOpinion: "" };
      this.rejectOpen = true;
    },
    submitReject() {
      if (!this.rejectForm.auditOpinion) {
        this.$modal.msgWarning("请选择驳回原因");
        return;
      }
      auditDocument({
        documentId: this.rejectForm.documentId,
        auditStatus: '2',
        auditOpinion: this.rejectForm.auditOpinion
      }).then(() => {
        this.$modal.msgSuccess("已驳回，通知已发送给用户");
        this.rejectOpen = false;
        this.getList();
      });
    },
    docTypeLabel(type) {
      const map = { "1": "身份证", "2": "学历证明", "3": "工作证明", "4": "收入证明", "5": "人才证书", "6": "其他" };
      return map[type] || "资料";
    },
    docTypeTagType(type) {
      const map = { "1": "info", "2": "success", "3": "primary", "4": "warning", "5": "danger", "6": "" };
      return map[type] || "";
    },
    resolveUrl(filePath) {
      if (!filePath) return "";
      if (/^https?:\/\//.test(filePath)) return filePath;
      const base = process.env.VUE_APP_BASE_API || "";
      return base + (filePath.startsWith("/") ? filePath : "/" + filePath);
    }
  }
};
</script>
