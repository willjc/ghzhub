<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="审批状态" prop="approveStatus">
        <el-select v-model="queryParams.approveStatus" placeholder="全部" clearable style="width: 140px">
          <el-option label="待审批" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="已驳回" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="房源编号" prop="houseCode">
        <el-input v-model="queryParams.houseCode" placeholder="请输入房源编号" clearable style="width: 180px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>

    <el-table v-loading="loading" :data="auditList" border stripe style="width: 100%">
      <el-table-column label="房源编号" prop="houseCode" min-width="140" />
      <el-table-column label="房源位置" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          <span v-if="scope.row.projectName">{{ scope.row.projectName }}-{{ scope.row.buildingName || '' }}{{ scope.row.houseNo ? scope.row.houseNo + '号' : '' }}</span>
          <span v-else style="color: #C0C4CC;">-</span>
        </template>
      </el-table-column>
      <el-table-column label="当前状态" prop="currentStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.currentStatus)" size="small">
            {{ statusText(scope.row.currentStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="目标状态" prop="targetStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag :type="statusTagType(scope.row.targetStatus)" size="small">
            {{ statusText(scope.row.targetStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请人" prop="applyBy" width="100" align="center" />
      <el-table-column label="申请时间" prop="applyTime" width="160" align="center" />
      <el-table-column label="审批状态" prop="approveStatus" width="100" align="center">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.approveStatus === '0'" type="warning" size="small">待审批</el-tag>
          <el-tag v-else-if="scope.row.approveStatus === '1'" type="success" size="small">已通过</el-tag>
          <el-tag v-else-if="scope.row.approveStatus === '2'" type="danger" size="small">已驳回</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审批人" prop="approveBy" width="100" align="center" />
      <el-table-column label="审批时间" prop="approveTime" width="160" align="center" />
      <el-table-column label="审批意见" prop="approveOpinion" min-width="140" show-overflow-tooltip />
      <el-table-column label="操作" width="160" align="center" fixed="right">
        <template slot-scope="scope">
          <template v-if="scope.row.approveStatus === '0'">
            <el-button size="mini" type="text" icon="el-icon-check" @click="handleApprove(scope.row, '1')"
              v-hasPermi="['gangzhu:house:statusApprove']">通过</el-button>
            <el-button size="mini" type="text" icon="el-icon-close" style="color: #F56C6C" @click="handleApprove(scope.row, '2')"
              v-hasPermi="['gangzhu:house:statusApprove']">驳回</el-button>
          </template>
          <span v-else style="color: #909399; font-size: 12px;">已处理</span>
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

    <!-- 审批对话框 -->
    <el-dialog :title="approveDialog.title" :visible.sync="approveDialog.visible" width="450px" append-to-body>
      <el-form label-width="80px">
        <el-form-item label="房源编号">
          <span>{{ approveDialog.houseCode }}</span>
        </el-form-item>
        <el-form-item label="状态变更">
          <span>{{ statusText(approveDialog.currentStatus) }} → {{ statusText(approveDialog.targetStatus) }}</span>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="approveDialog.opinion" type="textarea" :rows="3" placeholder="请输入审批意见（可选）" />
          <div style="margin-top: 8px;">
            <el-tag v-for="tag in quickOpinions" :key="tag" size="small" style="cursor:pointer; margin-right: 6px; margin-bottom: 4px;"
              @click="approveDialog.opinion = tag">{{ tag }}</el-tag>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="approveDialog.visible = false">取 消</el-button>
        <el-button type="primary" :loading="approveDialog.loading" @click="submitApprove">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listStatusAudit, approveStatusAudit } from '@/api/gangzhu/statusAudit'

const STATUS_MAP = {
  '0': '空置',
  '1': '已预订',
  '2': '已出租',
  '3': '维修中',
  '4': '下架'
}

const STATUS_TAG_TYPE = {
  '0': 'success',
  '1': 'warning',
  '2': '',
  '3': 'info',
  '4': 'danger'
}

export default {
  name: 'HouseStatusAudit',
  data() {
    return {
      loading: false,
      showSearch: true,
      auditList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 20,
        approveStatus: null,
        houseCode: null
      },
      approveDialog: {
        visible: false,
        loading: false,
        title: '',
        auditId: null,
        houseCode: '',
        currentStatus: '',
        targetStatus: '',
        approveStatus: '',
        opinion: ''
      },
      quickOpinions: ['同意', '情况属实，同意变更', '不符合变更条件，驳回']
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listStatusAudit(this.queryParams).then(res => {
        const data = res.data || {}
        this.auditList = data.records || []
        this.total = data.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    statusText(val) {
      return STATUS_MAP[val] || '未知'
    },
    statusTagType(val) {
      return STATUS_TAG_TYPE[val] || 'info'
    },
    handleApprove(row, approveStatus) {
      this.approveDialog = {
        visible: true,
        loading: false,
        title: approveStatus === '1' ? '审批通过' : '审批驳回',
        auditId: row.auditId,
        houseCode: row.houseCode,
        currentStatus: row.currentStatus,
        targetStatus: row.targetStatus,
        approveStatus: approveStatus,
        opinion: approveStatus === '1' ? '同意' : ''
      }
    },
    submitApprove() {
      this.approveDialog.loading = true
      approveStatusAudit({
        auditId: this.approveDialog.auditId,
        approveStatus: this.approveDialog.approveStatus,
        opinion: this.approveDialog.opinion
      }).then(res => {
        this.$modal.msgSuccess('操作成功')
        this.approveDialog.visible = false
        this.approveDialog.loading = false
        this.getList()
      }).catch(() => {
        this.approveDialog.loading = false
      })
    }
  }
}
</script>
