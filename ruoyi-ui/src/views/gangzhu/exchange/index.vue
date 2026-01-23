<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="申请人" prop="tenantName">
        <el-input
          v-model="queryParams.tenantName"
          placeholder="请输入申请人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="审核状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="审核状态" clearable>
          <el-option label="待审核" value="0" />
          <el-option label="已完成" value="1" />
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

    <el-table v-loading="loading" :data="exchangeList">
      <el-table-column label="合同编号" align="center" prop="contractNo" width="180" show-overflow-tooltip />
      <el-table-column label="申请人" align="center" prop="tenantName" width="100" />
      <el-table-column label="身份证号" align="center" prop="tenantIdCard" width="180" />
      <el-table-column label="联系电话" align="center" prop="tenantPhone" width="130" />
      <el-table-column label="房间地址" align="center" prop="oldFullAddress" min-width="180" show-overflow-tooltip />
      <el-table-column label="申请调换时间" align="center" prop="applyTime" width="110" />
      <el-table-column label="申请原因" align="center" prop="exchangeReason" min-width="120" show-overflow-tooltip />
      <el-table-column label="审核状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="warning">待审核</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="success">已完成</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="danger">已拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="280">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleDetail(scope.row)">详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-office-building"
            @click="handleSelectHouse(scope.row)"
            v-if="scope.row.status === '0'"
          >处理换房</el-button>
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
            v-hasPermi="['gangzhu:exchange:remove']"
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
    <el-dialog title="调换房申请详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="合同编号">{{ detailForm.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="申请人">{{ detailForm.tenantName }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ detailForm.tenantIdCard }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailForm.tenantPhone }}</el-descriptions-item>
        <el-descriptions-item label="原房源地址">{{ detailForm.oldFullAddress || detailForm.roomAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同期限">
          {{ detailForm.contractStartDate || '-' }} ~ {{ detailForm.contractEndDate || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="目标房源地址" v-if="detailForm.newFullAddress">
          {{ detailForm.newFullAddress }}
        </el-descriptions-item>
        <el-descriptions-item label="申请调换时间">{{ detailForm.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="换房时间" v-if="detailForm.exchangeTime">{{ detailForm.exchangeTime }}</el-descriptions-item>
        <el-descriptions-item label="申请原因">{{ detailForm.exchangeReason }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag v-if="detailForm.status === '0'" type="warning">待审核</el-tag>
          <el-tag v-else-if="detailForm.status === '1'" type="success">已完成</el-tag>
          <el-tag v-else-if="detailForm.status === '2'" type="danger">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审核意见" v-if="detailForm.approveOpinion">
          {{ detailForm.approveOpinion }}
        </el-descriptions-item>
        <el-descriptions-item label="审核时间" v-if="detailForm.approveTime">
          {{ detailForm.approveTime }}
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 处理换房对话框 -->
    <el-dialog title="处理换房" :visible.sync="selectOpen" width="800px" append-to-body :close-on-click-modal="false">
      <el-form ref="selectForm" :model="selectForm" :rules="selectRules" label-width="100px">
        <!-- 原房源信息 -->
        <el-divider content-position="left">原房源信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请人">
              <el-input :value="selectForm.tenantName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input :value="selectForm.tenantPhone" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原房源">
              <el-input :value="selectForm.oldFullAddress || selectForm.roomAddress || '-'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同期限">
              <el-input :value="(selectForm.contractStartDate || '-') + ' ~ ' + (selectForm.contractEndDate || '-')" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="申请原因">
          <el-input v-model="selectForm.exchangeReason" type="textarea" :rows="2" disabled />
        </el-form-item>

        <!-- 目标房源选择 -->
        <el-divider content-position="left">选择目标房源</el-divider>
        <el-form-item label="换房时间" prop="exchangeTime">
          <el-date-picker
            v-model="selectForm.exchangeTime"
            type="date"
            placeholder="请选择换房时间"
            value-format="yyyy-MM-dd"
            :picker-options="datePickerOptions"
            style="width: 100%"
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="项目" prop="projectId">
              <el-select v-model="selectForm.projectId" placeholder="请选择项目" @change="onProjectChange" clearable filterable style="width: 100%">
                <el-option
                  v-for="item in projectList"
                  :key="item.projectId"
                  :label="item.projectName"
                  :value="item.projectId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="楼栋" prop="buildingId">
              <el-select v-model="selectForm.buildingId" placeholder="请选择楼栋" @change="onBuildingChange" clearable filterable style="width: 100%">
                <el-option
                  v-for="item in buildingList"
                  :key="item.buildingId"
                  :label="item.buildingName"
                  :value="item.buildingId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="单元">
              <el-select v-model="selectForm.unitId" placeholder="请选择单元" @change="onUnitChange" clearable filterable style="width: 100%">
                <el-option
                  v-for="item in unitList"
                  :key="item.unitId"
                  :label="item.unitName"
                  :value="item.unitId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="目标房间" prop="newHouseId">
          <el-select v-model="selectForm.newHouseId" placeholder="请选择目标房间" clearable filterable style="width: 100%">
            <el-option
              v-for="item in availableRooms"
              :key="item.houseId"
              :label="item.houseNo + '室 - ' + (item.houseTypeName || '-') + ' - ' + (item.area || '-') + '㎡ - ¥' + (item.rentPrice || 0)"
              :value="item.houseId"
            />
          </el-select>
        </el-form-item>

        <!-- 房源对比 -->
        <el-divider content-position="left">房源对比</el-divider>
        <el-row :gutter="20" type="flex" align="middle">
          <el-col :span="10">
            <el-card shadow="never">
              <div slot="header" class="compare-header">原房源</div>
              <div class="compare-content">
                <p><strong>地址：</strong>{{ selectForm.oldFullAddress || selectForm.roomAddress || '-' }}</p>
              </div>
            </el-card>
          </el-col>
          <el-col :span="4" style="text-align: center">
            <i class="el-icon-right" style="font-size: 24px; color: #409EFF;"></i>
          </el-col>
          <el-col :span="10">
            <el-card shadow="never">
              <div slot="header" class="compare-header">目标房源</div>
              <div class="compare-content">
                <p v-if="selectedRoomInfo">
                  <strong>地址：</strong>{{ selectedRoomFullAddress }}
                </p>
                <p style="color: #999;" v-else>请选择目标房源</p>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="selectOpen = false">取 消</el-button>
        <el-button type="primary" @click="submitSelectHouse" :loading="submitting">保 存</el-button>
      </div>
    </el-dialog>

    <!-- 审核对话框 -->
    <el-dialog title="审核换房申请" :visible.sync="auditOpen" width="700px" append-to-body>
      <el-form ref="auditForm" :model="auditForm" :rules="auditRules" label-width="100px">
        <!-- 申请人信息 -->
        <el-divider content-position="left">申请人信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="申请人">
              <el-input :value="auditForm.tenantName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input :value="auditForm.tenantPhone" disabled />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 房源对比 -->
        <el-divider content-position="left">房源信息</el-divider>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="原房源">
              <el-input :value="auditForm.oldFullAddress || auditForm.roomAddress" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="目标房源">
              <el-input :value="auditForm.newFullAddress || '未选择'" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合同期限">
              <el-input :value="(auditForm.contractStartDate || '') + ' ~ ' + (auditForm.contractEndDate || '')" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="换房时间">
              <el-input :value="auditForm.exchangeTime || '待定'" disabled />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="申请原因">
          <el-input v-model="auditForm.exchangeReason" type="textarea" :rows="2" disabled />
        </el-form-item>

        <!-- 审核区域 -->
        <el-divider content-position="left">审核</el-divider>
        <el-form-item label="换房时间">
          <el-date-picker
            v-model="auditForm.exchangeTime"
            type="date"
            placeholder="选择换房时间（审核通过时必填）"
            value-format="yyyy-MM-dd"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="审核结果" prop="approveResult">
          <el-radio-group v-model="auditForm.approveResult">
            <el-radio label="1">通过并换房</el-radio>
            <el-radio label="2">拒绝申请</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="approveOpinion">
          <el-input v-model="auditForm.approveOpinion" type="textarea" placeholder="请输入审核意见" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="auditOpen = false">取 消</el-button>
        <el-button type="primary" @click="submitAudit" :loading="submitting">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listExchange, getExchange, delExchange, getProjectList, getBuildings, getUnits, getAvailableRooms, assignTargetHouse, confirmExchange } from "@/api/gangzhu/exchange";

export default {
  name: "Exchange",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      exchangeList: [],
      detailOpen: false,
      detailForm: {},
      selectOpen: false,
      selectForm: {},
      selectRules: {
        newHouseId: [
          { required: true, message: "请选择目标房源", trigger: "change" }
        ]
      },
      // 日期选择器选项
      datePickerOptions: {
        disabledDate(time) {
          // 禁用今天之前的日期
          return time.getTime() < Date.now() - 24 * 60 * 60 * 1000;
        }
      },
      auditOpen: false,
      auditForm: {},
      auditRules: {
        approveResult: [
          { required: true, message: "请选择审核结果", trigger: "change" }
        ],
        approveOpinion: [
          { required: true, message: "请输入审核意见", trigger: "blur" }
        ]
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tenantName: null,
        status: null
      },
      // 级联选择数据
      projectList: [],
      buildingList: [],
      unitList: [],
      availableRooms: [],
      submitting: false
    };
  },
  computed: {
    selectedRoomInfo() {
      if (!this.selectForm.newHouseId) return null;
      return this.availableRooms.find(r => r.houseId === this.selectForm.newHouseId);
    },
    selectedRoomFullAddress() {
      if (!this.selectedRoomInfo) return '';
      const room = this.selectedRoomInfo;
      return (room.projectName || '') + (room.buildingName || '') + (room.unitName || '') + (room.houseNo || '') + '室';
    }
  },
  created() {
    this.getList();
    this.loadProjects();
  },
  methods: {
    /** 查询调换房申请列表 */
    getList() {
      this.loading = true;
      listExchange(this.queryParams).then(response => {
        this.exchangeList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    /** 加载项目列表 */
    loadProjects() {
      getProjectList().then(response => {
        // 处理分页数据
        this.projectList = response.rows || response.data || [];
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
      const exchangeId = row.exchangeId;
      getExchange(exchangeId).then(response => {
        this.detailForm = response.data;
        this.detailOpen = true;
      });
    },

    /** 选择房源操作 */
    handleSelectHouse(row) {
      const exchangeId = row.exchangeId;
      getExchange(exchangeId).then(response => {
        this.selectForm = response.data;
        this.selectForm.projectId = null;
        this.selectForm.buildingId = null;
        this.selectForm.unitId = null;
        this.selectForm.newHouseId = null;
        this.buildingList = [];
        this.unitList = [];
        this.availableRooms = [];
        this.selectOpen = true;
        this.$nextTick(() => {
          this.$refs.selectForm && this.$refs.selectForm.clearValidate();
        });
      });
    },

    /** 审核操作 */
    handleAudit(row) {
      const exchangeId = row.exchangeId;
      getExchange(exchangeId).then(response => {
        this.auditForm = response.data;
        this.auditForm.approveResult = '';
        this.auditForm.approveOpinion = '';
        // 设置默认换房时间为明天
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        this.auditForm.exchangeTime = tomorrow.toISOString().split('T')[0];
        this.auditOpen = true;
        this.$nextTick(() => {
          this.$refs.auditForm && this.$refs.auditForm.clearValidate();
        });
      });
    },

    /** 项目变化 */
    onProjectChange(projectId) {
      this.selectForm.buildingId = null;
      this.selectForm.unitId = null;
      this.selectForm.newHouseId = null;
      this.buildingList = [];
      this.unitList = [];
      this.availableRooms = [];
      if (projectId) {
        getBuildings(projectId).then(response => {
          this.buildingList = response.data || [];
        });
      }
    },

    /** 楼栋变化 */
    onBuildingChange(buildingId) {
      this.selectForm.unitId = null;
      this.selectForm.newHouseId = null;
      this.unitList = [];
      this.availableRooms = [];
      if (buildingId) {
        getUnits(buildingId).then(response => {
          this.unitList = response.data || [];
        });
      }
    },

    /** 单元变化 */
    onUnitChange(unitId) {
      this.selectForm.newHouseId = null;
      this.availableRooms = [];
      this.loadAvailableRooms();
    },

    /** 加载可用房间列表 */
    loadAvailableRooms() {
      if (!this.selectForm.projectId || !this.selectForm.buildingId) return;
      const params = {
        projectId: this.selectForm.projectId,
        buildingId: this.selectForm.buildingId
      };
      if (this.selectForm.unitId) {
        params.unitId = this.selectForm.unitId;
      }
      getAvailableRooms(params).then(response => {
        this.availableRooms = response.data || [];
      });
    },

    /** 提交选择房源（保存信息，不审核） */
    submitSelectHouse() {
      this.$refs["selectForm"].validate(valid => {
        if (valid) {
          this.submitting = true;

          const selectData = {
            exchangeId: this.selectForm.exchangeId,
            newHouseId: this.selectForm.newHouseId,
            exchangeTime: this.selectForm.exchangeTime
          };

          assignTargetHouse(selectData).then(response => {
            this.$modal.msgSuccess("保存成功");
            this.selectOpen = false;
            this.getList();
          }).finally(() => {
            this.submitting = false;
          });
        }
      });
    },

    /** 提交审核 */
    submitAudit() {
      this.$refs["auditForm"].validate(valid => {
        if (valid) {
          // 如果选择通过，必须选择目标房源和换房时间
          if (this.auditForm.approveResult === "1") {
            if (!this.auditForm.newHouseId) {
              this.$modal.msgWarning("请先通过\"处理换房\"功能选择目标房源");
              return;
            }
            if (!this.auditForm.exchangeTime) {
              this.$modal.msgWarning("请选择换房时间");
              return;
            }
          }

          this.submitting = true;

          const auditData = {
            exchangeId: this.auditForm.exchangeId,
            newHouseId: this.auditForm.newHouseId,
            approveResult: this.auditForm.approveResult,
            approveOpinion: this.auditForm.approveOpinion,
            exchangeTime: this.auditForm.exchangeTime
          };

          confirmExchange(auditData).then(response => {
            this.$modal.msgSuccess("审核成功");
            this.auditOpen = false;
            this.getList();
          }).finally(() => {
            this.submitting = false;
          });
        }
      });
    },

    /** 删除操作 */
    handleDelete(row) {
      this.$modal.confirm('是否确认删除该调换房申请？').then(() => {
        return delExchange(row.exchangeId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.compare-header {
  padding: 0;
  background: #f5f7fa;
  font-weight: bold;
}

.compare-content p {
  margin: 8px 0;
}

.compare-content strong {
  color: #333;
}
</style>
