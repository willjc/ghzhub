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
      <el-form-item label="真实姓名" prop="realName">
        <el-input
          v-model="queryParams.realName"
          placeholder="请输入真实姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="项目名称" prop="projectName">
        <el-input
          v-model="queryParams.projectName"
          placeholder="请输入项目名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入联系电话"
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['gangzhu:checkin:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['gangzhu:checkin:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['gangzhu:checkin:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini" @click="handleExport" v-hasPermi="['gangzhu:checkin:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="checkInList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="入住单号" align="center" prop="checkinNo" width="180" show-overflow-tooltip />
      <el-table-column label="真实姓名" align="center" prop="realName" width="100" show-overflow-tooltip />
      <el-table-column label="联系电话" align="center" prop="phone" width="120" show-overflow-tooltip />
      <el-table-column label="项目名称" align="center" prop="projectName" width="140" show-overflow-tooltip />
      <el-table-column label="房源位置" align="center" width="160" show-overflow-tooltip>
        <template slot-scope="scope">
          {{ formatLocation(scope.row) }}
        </template>
      </el-table-column>
      <el-table-column label="朝向" align="center" prop="orientation" width="80" show-overflow-tooltip />
      <el-table-column label="合同编号" align="center" prop="contractNo" width="180" show-overflow-tooltip />
      <el-table-column label="合同开始" align="center" prop="startDate" width="110" show-overflow-tooltip />
      <el-table-column label="合同结束" align="center" prop="endDate" width="110" show-overflow-tooltip />
      <el-table-column label="剩余天数" align="center" width="120">
        <template slot-scope="scope">
          <span v-if="scope.row.remainingDays !== null && scope.row.remainingDays !== undefined">
            <span v-if="scope.row.remainingDays < 0" style="color: #F56C6C; font-weight: bold;">已到期{{ Math.abs(scope.row.remainingDays) }}天</span>
            <span v-else-if="scope.row.remainingDays <= 30" style="color: #E6A23C;">{{ scope.row.remainingDays }}天</span>
            <span v-else>{{ scope.row.remainingDays }}天</span>
          </span>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="入住状态" align="center" prop="status" width="110">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="info">待办理</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="primary">待入住确认</el-tag>
          <el-tag v-else-if="scope.row.status === '3'" type="danger">已拒绝</el-tag>
          <el-tag v-else-if="scope.row.status === '4'" type="success">已入住确认</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="入住日期" align="center" prop="checkinDate" width="110" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" min-width="250">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button v-if="scope.row.status === '1'" size="mini" type="text" icon="el-icon-check" @click="handleAudit(scope.row, '2')" v-hasPermi="['gangzhu:checkin:audit']">通过</el-button>
          <el-button v-if="scope.row.status === '1'" size="mini" type="text" icon="el-icon-close" @click="handleAudit(scope.row, '3')" v-hasPermi="['gangzhu:checkin:audit']">拒绝</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['gangzhu:checkin:remove']">删除</el-button>
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
    <el-dialog title="入住详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <!-- 基本信息区 -->
      <div class="detail-section">
        <div class="detail-section-title">基本信息</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="真实姓名">{{ detailForm.realName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="身份证号">{{ detailForm.idCard || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailForm.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="项目名称">{{ detailForm.projectName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="房源位置">{{ formatLocation(detailForm) }}</el-descriptions-item>
          <el-descriptions-item label="朝向">{{ detailForm.orientation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="面积">{{ detailForm.area ? detailForm.area + '㎡' : '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 合同信息区 -->
      <div class="detail-section">
        <div class="detail-section-title">合同信息</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="合同编号">{{ detailForm.contractNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="剩余天数">
            <span v-if="detailForm.remainingDays !== null && detailForm.remainingDays !== undefined">
              <span v-if="detailForm.remainingDays < 0" style="color: #F56C6C; font-weight: bold;">已到期{{ Math.abs(detailForm.remainingDays) }}天</span>
              <span v-else-if="detailForm.remainingDays <= 30" style="color: #E6A23C;">{{ detailForm.remainingDays }}天</span>
              <span v-else>{{ detailForm.remainingDays }}天</span>
            </span>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="合同开始日期">{{ detailForm.startDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="合同结束日期">{{ detailForm.endDate || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 入住信息区 -->
      <div class="detail-section">
        <div class="detail-section-title">入住信息</div>
        <el-descriptions :column="2" border size="small">
          <el-descriptions-item label="入住单号">{{ detailForm.checkinNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="入住状态">
            <el-tag v-if="detailForm.status === '0'" type="info">待办理</el-tag>
            <el-tag v-else-if="detailForm.status === '1'" type="warning">待审核</el-tag>
            <el-tag v-else-if="detailForm.status === '2'" type="primary">待入住确认</el-tag>
            <el-tag v-else-if="detailForm.status === '3'" type="danger">已拒绝</el-tag>
            <el-tag v-else-if="detailForm.status === '4'" type="success">已入住确认</el-tag>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="入住日期">{{ detailForm.checkinDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="实际入住日期">{{ detailForm.actualCheckinDate || '-' }}</el-descriptions-item>
          <el-descriptions-item label="用户确认时间">{{ detailForm.checkinTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请时间">{{ detailForm.createTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核人">{{ detailForm.auditBy || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核时间">{{ detailForm.auditTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审核备注" :span="2">{{ detailForm.auditRemark || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 同住人信息区 -->
      <div class="detail-section">
        <div class="detail-section-title">同住人信息</div>
        <div class="detail-section-body">
          <template v-if="parsedRoommates.length > 0">
            <el-tag v-for="(mate, index) in parsedRoommates" :key="index" style="margin-right: 8px; margin-bottom: 4px;">
              {{ mate.name }}（{{ mate.relation || '未知关系' }}）
            </el-tag>
          </template>
          <span v-else style="color: #909399;">无</span>
        </div>
      </div>

      <!-- 紧急联系人区 -->
      <div class="detail-section">
        <div class="detail-section-title">紧急联系人</div>
        <el-descriptions :column="3" border size="small">
          <el-descriptions-item label="姓名">{{ detailForm.emergencyContactName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="关系">{{ detailForm.emergencyContactRelation || '-' }}</el-descriptions-item>
          <el-descriptions-item label="联系电话">{{ detailForm.emergencyContactPhone || '-' }}</el-descriptions-item>
        </el-descriptions>
      </div>

      <!-- 租户签字区 -->
      <div class="detail-section">
        <div class="detail-section-title">租户签字</div>
        <div class="detail-section-body">
          <el-image
            v-if="detailForm.tenantSignature"
            :src="baseUrl + detailForm.tenantSignature"
            :preview-src-list="[baseUrl + detailForm.tenantSignature]"
            fit="contain"
            style="max-width: 300px; max-height: 150px; border: 1px solid #dcdfe6; border-radius: 4px;"
          />
          <span v-else style="color: #909399;">无</span>
        </div>
      </div>

      <!-- 房间设施区 -->
      <div class="detail-section">
        <div class="detail-section-title">房间设施</div>
        <div class="detail-section-body" v-loading="facilityLoading">
          <template v-if="facilityGroups.length > 0">
            <div v-for="group in facilityGroups" :key="group.category" style="margin-bottom: 8px;">
              <span style="font-weight: bold; margin-right: 8px;">{{ group.category }}：</span>
              <el-tag
                v-for="(item, idx) in group.items"
                :key="idx"
                :type="item.status === '完好' ? 'success' : (item.status === '破损' ? 'danger' : 'info')"
                size="small"
                style="margin-right: 6px; margin-bottom: 4px;"
              >
                {{ item.name }}×{{ item.quantity || 1 }}({{ item.status || '未知' }})
              </el-tag>
            </div>
          </template>
          <span v-else-if="!facilityLoading" style="color: #909399;">暂无设施信息</span>
        </div>
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
import { listHouseFacility } from "@/api/gangzhu/houseFacility";

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
      facilityLoading: false,
      facilityGroups: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        checkinNo: null,
        realName: null,
        projectName: null,
        phone: null,
        status: null,
      }
    };
  },
  computed: {
    parsedRoommates() {
      return this.parseRoommates(this.detailForm.roommateInfo);
    }
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
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        checkinNo: null,
        realName: null,
        projectName: null,
        phone: null,
        status: null,
      };
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.recordId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 格式化房源位置：楼栋-楼层-房源名 */
    formatLocation(row) {
      const parts = [];
      if (row.buildingName) parts.push(row.buildingName);
      if (row.floor) parts.push(row.floor);
      if (row.houseName) parts.push(row.houseName);
      return parts.length > 0 ? parts.join('-') : '-';
    },
    /** 解析同住人JSON */
    parseRoommates(roommateInfo) {
      if (!roommateInfo) return [];
      try {
        const arr = JSON.parse(roommateInfo);
        return Array.isArray(arr) ? arr : [];
      } catch (e) {
        console.error('解析同住人信息失败:', e);
        return [];
      }
    },
    /** 查看详情 */
    handleDetail(row) {
      const recordId = row.recordId;
      this.facilityGroups = [];
      getCheckIn(recordId).then(response => {
        this.detailForm = response.data;
        this.detailOpen = true;
        // 加载设施信息
        if (this.detailForm.houseId) {
          this.loadFacilities(this.detailForm.houseId);
        }
      });
    },
    /** 加载房间设施 */
    loadFacilities(houseId) {
      this.facilityLoading = true;
      listHouseFacility(houseId).then(response => {
        const list = response.data || response.rows || [];
        this.facilityGroups = this.groupFacilities(list);
        this.facilityLoading = false;
      }).catch(() => {
        this.facilityGroups = [];
        this.facilityLoading = false;
      });
    },
    /** 按类别分组设施 */
    groupFacilities(list) {
      const map = {};
      list.forEach(item => {
        const cat = item.facilityCategory || item.category || '其他';
        if (!map[cat]) map[cat] = [];
        map[cat].push({
          name: item.facilityName || item.name || '未命名',
          quantity: item.quantity || 1,
          status: item.facilityStatus || item.status || '未知'
        });
      });
      return Object.keys(map).map(key => ({ category: key, items: map[key] }));
    },
    /** 审核功能 */
    handleAudit(row, status) {
      this.auditForm = {
        recordId: row.recordId,
        status: status,
        auditRemark: ''
      };
      this.auditOpen = true;
    },
    /** 提交审核 */
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
    handleAdd() {
      this.$modal.msgWarning("新增功能暂未开放，请通过合同签署生成入驻单");
    },
    handleUpdate() {
      this.$modal.msgWarning("修改功能暂未开放");
    },
    handleExport() {
      this.download('system/checkin/export', {
        ...this.queryParams
      }, `checkin_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>

<style scoped>
.detail-section {
  margin-bottom: 18px;
}
.detail-section-title {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  padding: 8px 0;
  margin-bottom: 8px;
  border-bottom: 1px solid #EBEEF5;
}
.detail-section-body {
  padding: 8px 0;
}
</style>
