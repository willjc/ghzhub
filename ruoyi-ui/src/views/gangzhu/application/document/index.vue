<template>
  <div class="app-container">
    <!-- 筛选区 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="84px">
      <el-form-item label="审核状态" prop="auditStatus">
        <el-select v-model="queryParams.auditStatus" placeholder="全部" clearable style="width: 140px">
          <el-option label="待审核" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="违规" value="2" />
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
          <el-tag v-else-if="scope.row.auditStatus === '2'" type="danger" size="mini">违规</el-tag>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.auditStatus !== '2'"
            size="mini" type="text" icon="el-icon-warning-outline" style="color:#E6A23C;"
            @click="handleMarkViolation(scope.row)"
            v-hasPermi="['gangzhu:document:audit']"
          >标记违规</el-button>
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

    <!-- 标记违规弹窗 -->
    <el-dialog title="标记违规" :visible.sync="rejectOpen" width="500px" append-to-body>
      <el-alert type="warning" :closable="false" show-icon style="margin-bottom: 12px;"
        title="标记违规仅做留痕（写入审核意见），不会发消息给用户、不会回滚订单与房源；后续违规处置请走线下追缴并计入诚信档案。" />
      <el-form :model="rejectForm" label-width="80px">
        <el-form-item label="违规原因">
          <el-input v-model="rejectForm.auditOpinion" type="textarea" :rows="4" placeholder="请填写违规原因（如：资料缺失/虚假填报/材料过期 等）" maxlength="200" show-word-limit />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="warning" @click="submitReject">确认标记</el-button>
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
          <el-tag v-else-if="detailData.auditStatus === '2'" type="danger" size="mini">违规</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见">{{ detailData.auditOpinion || '-' }}</el-descriptions-item>
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
        <el-button v-if="detailData.auditStatus !== '2'" type="warning" @click="handleMarkViolation(detailData); detailOpen = false">标记违规</el-button>
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDocument, markViolation } from "@/api/gangzhu/document";

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
    /** 标记违规：抽查专用，写入 audit_status=2 + [违规] 前缀的 audit_opinion */
    handleMarkViolation(row) {
      this.rejectForm = { documentId: row.documentId, auditOpinion: "" };
      this.rejectOpen = true;
    },
    submitReject() {
      if (!this.rejectForm.auditOpinion || this.rejectForm.auditOpinion.trim() === "") {
        this.$modal.msgWarning("请填写违规原因");
        return;
      }
      markViolation({
        documentId: this.rejectForm.documentId,
        violationReason: this.rejectForm.auditOpinion.trim()
      }).then(() => {
        this.$modal.msgSuccess("已标记违规");
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
