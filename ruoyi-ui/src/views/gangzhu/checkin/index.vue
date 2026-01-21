<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="入住单号" prop="checkinNo">
        <el-input
          v-model="queryParams.checkinNo"
          placeholder="请输入入住单号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="入住状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择入住状态" clearable>
          <el-option label="待办理" value="0" />
          <el-option label="待审核" value="1" />
          <el-option label="待入住确认" value="2" />
          <el-option label="已拒绝" value="3" />
          <el-option label="已入住确认" value="4" />
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
          v-hasPermi="['gangzhu:checkin:add']"
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
          v-hasPermi="['gangzhu:checkin:edit']"
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
          v-hasPermi="['gangzhu:checkin:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:checkin:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="checkInList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="入住单号" align="center" prop="checkinNo" width="180" show-overflow-tooltip />
      <el-table-column label="合同编号" align="center" prop="contractNo" width="180" show-overflow-tooltip />
      <el-table-column label="用户昵称" align="center" prop="tenantNickname" width="120" show-overflow-tooltip />
      <el-table-column label="房源名称" align="center" prop="houseName" width="150" show-overflow-tooltip />
      <el-table-column label="入住日期" align="center" prop="checkinDate" width="150" />
      <el-table-column label="实际入住日期" align="center" prop="actualCheckinDate" width="180" />
      <el-table-column label="入住状态" align="center" prop="status" width="150">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="info">待办理</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="primary">待入住确认</el-tag>
          <el-tag v-else-if="scope.row.status === '3'" type="danger">已拒绝</el-tag>
          <el-tag v-else-if="scope.row.status === '4'" type="success">已入住确认</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="createTime" width="160" />
      <el-table-column label="审核人" align="center" prop="auditBy" width="100" />
      <el-table-column label="审核时间" align="center" prop="auditTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" min-width="250">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
          >详情</el-button>
          <el-button
            v-if="scope.row.status === '1'"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleAudit(scope.row, '2')"
            v-hasPermi="['gangzhu:checkin:audit']"
          >通过</el-button>
          <el-button
            v-if="scope.row.status === '1'"
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleAudit(scope.row, '3')"
            v-hasPermi="['gangzhu:checkin:audit']"
          >拒绝</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:checkin:remove']"
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

    <!-- 入住详情对话框 -->
    <el-dialog title="入住详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="入住单号">{{ detailForm.checkinNo }}</el-descriptions-item>
        <el-descriptions-item label="入住状态">
          <el-tag v-if="detailForm.status === '0'" type="info">待办理</el-tag>
          <el-tag v-else-if="detailForm.status === '1'" type="warning">待审核</el-tag>
          <el-tag v-else-if="detailForm.status === '2'" type="primary">待入住确认</el-tag>
          <el-tag v-else-if="detailForm.status === '3'" type="danger">已拒绝</el-tag>
          <el-tag v-else-if="detailForm.status === '4'" type="success">已入住确认</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="合同编号">{{ detailForm.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="用户昵称">{{ detailForm.tenantNickname }}</el-descriptions-item>
        <el-descriptions-item label="房源名称">{{ detailForm.houseName }}</el-descriptions-item>
        <el-descriptions-item label="入住日期">{{ detailForm.checkinDate }}</el-descriptions-item>
        <el-descriptions-item label="实际入住日期">{{ detailForm.actualCheckinDate || '未填写' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailForm.createTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户确认时间">{{ detailForm.checkinTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合住人信息">
          <div v-if="detailForm.roommateInfo">
            <el-tag v-for="(roommate, index) in JSON.parse(detailForm.roommateInfo || '[]')" :key="index" size="small" style="margin-right: 5px; margin-bottom: 5px;">
              {{ roommate.name }}
            </el-tag>
          </div>
          <span v-else>无</span>
        </el-descriptions-item>
        <el-descriptions-item label="紧急联系人">{{ detailForm.emergencyContactName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系人关系">{{ detailForm.emergencyContactRelation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailForm.emergencyContactPhone || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核人">{{ detailForm.auditBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核时间">{{ detailForm.auditTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审核备注" :span="2">{{ detailForm.auditRemark || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注信息" :span="2">{{ detailForm.remark || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 租户签字 -->
      <div v-if="detailForm.tenantSignature" style="margin-top: 20px;">
        <div style="margin-bottom: 10px; font-weight: bold;">租户签字：</div>
        <el-image
          :src="baseUrl + detailForm.tenantSignature"
          :preview-src-list="[baseUrl + detailForm.tenantSignature]"
          fit="contain"
          style="width: 200px; height: 100px; border: 1px solid #dcdfe6; border-radius: 4px;">
        </el-image>
      </div>

      <!-- 设施确认信息 -->
      <div v-if="detailForm.remark && detailForm.remark.includes('设施确认')" style="margin-top: 15px;">
        <div style="margin-bottom: 10px; font-weight: bold;">设施确认信息：</div>
        <el-table :data="parseFacilities(detailForm.remark)" border size="small" max-height="200">
          <el-table-column prop="name" label="设备名称" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template slot-scope="scope">
              <el-tag :type="scope.row.status === 'good' ? 'success' : 'danger'" size="small">
                {{ scope.row.status === 'good' ? '完好' : '破损' }}
              </el-tag>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog title="审核入住单" :visible.sync="auditOpen" width="600px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" label-width="100px">
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio label="2">通过</el-radio>
            <el-radio label="3">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核备注">
          <el-input v-model="auditForm.auditRemark" type="textarea" placeholder="请输入审核备注（可选）" :rows="4" />
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
import { listCheckIn, getCheckIn, addCheckIn, updateCheckIn, delCheckIn, auditCheckIn } from "@/api/gangzhu/checkin";

export default {
  name: "CheckIn",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      checkInList: [],
      detailOpen: false,
      auditOpen: false,
      detailForm: {},
      auditForm: {
        recordId: null,
        status: '2',
        auditRemark: ''
      },
      baseUrl: process.env.VUE_APP_BASE_API,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        checkinNo: null,
        status: null,
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listCheckIn(this.queryParams).then(response => {
        this.checkInList = response.rows;
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
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    // 查看详情
    handleDetail(row) {
      const recordId = row.recordId;
      getCheckIn(recordId).then(response => {
        this.detailForm = response.data;
        this.detailOpen = true;
      });
    },
    // 审核功能
    handleAudit(row, status) {
      this.auditForm = {
        recordId: row.recordId,
        status: status,
        auditRemark: ''
      };
      this.auditOpen = true;
    },
    // 提交审核
    submitAudit() {
      auditCheckIn(this.auditForm).then(response => {
        this.$modal.msgSuccess("审核成功");
        this.auditOpen = false;
        this.getList();
      });
    },
    handleDelete(row) {
      const recordIds = row.recordId || this.ids;
      this.$modal.confirm('是否确认删除入住单号为"' + recordIds + '"的数据项?').then(function() {
        return delCheckIn(recordIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    // 新增按钮（暂不实现）
    handleAdd() {
      this.$modal.msgWarning("新增功能暂未开放，请通过合同签署生成入驻单");
    },
    // 修改按钮（暂不实现）
    handleUpdate() {
      this.$modal.msgWarning("修改功能暂未开放");
    },
    handleExport() {
      this.download('system/checkin/export', {
        ...this.queryParams
      }, `checkin_${new Date().getTime()}.xlsx`)
    },
    // 解析设施确认信息
    parseFacilities(remark) {
      if (!remark || !remark.includes('设施确认')) {
        return [];
      }
      try {
        // 提取设施确认部分
        const facilitiesStr = remark.substring(remark.indexOf('设施确认：') + 5);
        const facilities = JSON.parse(facilitiesStr);
        return facilities.map(f => ({
          name: f.name,
          status: f.status
        }));
      } catch (e) {
        console.error('解析设施信息失败:', e);
        return [];
      }
    }
  }
};
</script>
