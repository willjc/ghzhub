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
      <el-form-item label="所属项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择项目" clearable style="width: 180px">
          <el-option
            v-for="project in projectList"
            :key="project.projectId"
            :label="project.projectName"
            :value="project.projectId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="人才类型" prop="talentType">
        <el-select v-model="queryParams.talentType" placeholder="请选择人才类型" clearable style="width: 120px">
          <el-option label="普通人才" value="0" />
          <el-option label="定向人才" value="1" />
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
          v-hasPermi="['gangzhu:batch:add']"
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
          v-hasPermi="['gangzhu:batch:edit']"
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
          v-hasPermi="['gangzhu:batch:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:batch:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="batchList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="批次编号" align="center" prop="batchNo" width="150" show-overflow-tooltip />
      <el-table-column label="批次名称" align="center" prop="batchName" min-width="160" show-overflow-tooltip />
      <el-table-column label="所属项目" align="center" prop="projectName" min-width="140" show-overflow-tooltip />
      <el-table-column label="人才类型" align="center" prop="talentType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.talentType === '0'" type="info" size="small">普通人才</el-tag>
          <el-tag v-else type="success" size="small">定向人才</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="入驻日期" align="center" min-width="200">
        <template slot-scope="scope">
          <span v-if="scope.row.entryStartDate && scope.row.entryEndDate">
            {{ parseTime(scope.row.entryStartDate, '{y}-{m}-{d}') }} ~ {{ parseTime(scope.row.entryEndDate, '{y}-{m}-{d}') }}
          </span>
          <span v-else style="color: #909399">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="房源/已分配" align="center" width="110">
        <template slot-scope="scope">
          <span>{{ scope.row.houseCount }}/{{ scope.row.allocatedCount }}</span>
        </template>
      </el-table-column>
      <el-table-column label="优惠方式" align="center" width="120">
        <template slot-scope="scope">
          <span v-if="!scope.row.preferentialType || scope.row.preferentialType === '0'" style="color: #909399;">无优惠</span>
          <span v-else-if="scope.row.preferentialType === '1'" style="color: #F56C6C;">免租{{ scope.row.freeRentPeriods || 0 }}期</span>
        </template>
      </el-table-column>
      <el-table-column label="审批状态" align="center" prop="approveStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.approveStatus === '0'" type="warning" size="small">待审批</el-tag>
          <el-tag v-else-if="scope.row.approveStatus === '1'" type="success" size="small">已通过</el-tag>
          <el-tag v-else-if="scope.row.approveStatus === '2'" type="danger" size="small">已拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="applyTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.applyTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
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
            icon="el-icon-check"
            @click="handleApprove(scope.row)"
            v-if="scope.row.approveStatus === '0'"
            v-hasPermi="['gangzhu:batch:approve']"
          >审批</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:batch:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:batch:remove']"
            style="color: #F56C6C"
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

    <!-- 添加或修改配租批次对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1400px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <!-- 基本信息表单 -->
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="批次名称" prop="batchName">
              <el-input v-model="form.batchName" placeholder="请输入批次名称" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="所属项目" prop="selectedProjectIds">
              <el-select v-model="selectedProjectIds" placeholder="请选择项目（可多选）" multiple collapse-tags style="width: 100%">
                <el-option
                  v-for="project in projectList"
                  :key="project.projectId"
                  :label="project.projectName"
                  :value="project.projectId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="批次人才类型" prop="talentType">
              <el-radio-group v-model="form.talentType">
                <el-radio label="0">普通人才</el-radio>
                <el-radio label="1">定向人才</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入驻开始日期" prop="entryStartDate">
              <el-date-picker
                v-model="form.entryStartDate"
                type="date"
                placeholder="请选择入驻开始日期"
                value-format="timestamp"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="入驻结束日期" prop="entryEndDate">
              <el-date-picker
                v-model="form.entryEndDate"
                type="date"
                placeholder="请选择入驻结束日期"
                value-format="timestamp"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="优惠方式" prop="preferentialType">
              <el-radio-group v-model="form.preferentialType" @change="handlePreferentialTypeChange" :disabled="form.approveStatus === '1'">
                <el-radio label="0">无优惠</el-radio>
                <el-radio label="1">免租期数</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="12" v-if="form.preferentialType === '1'">
            <el-form-item label="免租期数" prop="freeRentPeriods">
              <el-input-number
                v-model="form.freeRentPeriods"
                :min="1"
                :disabled="form.approveStatus === '1'"
                placeholder="请输入免租期数"
                style="width: 200px"
              />
              <span style="margin-left: 10px; color: #909399;">期</span>
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">房源与人员分配</el-divider>

        <!-- 左右分区域布局 -->
        <el-row :gutter="20">
          <!-- 左侧：房源列表 -->
          <el-col :span="10">
            <div class="section-header">
              <h4>房源列表</h4>
              <span class="count-tip">已选 {{ selectedHouses.length }} 套房源</span>
            </div>
            <el-table
              ref="houseTable"
              :data="availableHouseList"
              @selection-change="handleHouseSelectionChange"
              border
              height="400"
              style="width: 100%"
            >
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column label="项目名称" prop="projectName" min-width="150" show-overflow-tooltip />
              <el-table-column label="房源编号" prop="houseCode" width="120" show-overflow-tooltip />
              <el-table-column label="楼栋" prop="buildingName" width="100" show-overflow-tooltip />
              <el-table-column label="单元" prop="unitName" width="100" show-overflow-tooltip />
              <el-table-column label="房间号" prop="houseNo" width="100" />
              <el-table-column label="楼层" prop="floor" width="70" align="center" />
              <el-table-column label="户型" prop="houseTypeName" width="120" show-overflow-tooltip />
              <el-table-column label="面积(㎡)" prop="area" width="90" align="center" />
              <el-table-column label="租金(元/月)" prop="rentPrice" width="110" align="center" />
            </el-table>
          </el-col>

          <!-- 右侧：人员列表 -->
          <el-col :span="14">
            <div class="section-header">
              <h4>人员列表</h4>
              <span class="count-tip">已导入 {{ tenantList.length }} 人</span>
            </div>
            <div class="upload-actions" style="margin-bottom: 10px;">
              <el-upload
                ref="upload"
                :http-request="handleImportExcel"
                :show-file-list="false"
                :auto-upload="true"
                accept=".xlsx,.xls"
                action="#"
              >
                <el-button size="small" type="primary" icon="el-icon-upload2">导入Excel</el-button>
              </el-upload>
              <el-button size="small" type="success" icon="el-icon-plus" @click="handleAddTenant">手动添加</el-button>
              <el-button size="small" type="info" icon="el-icon-download" @click="handleDownloadTemplate">下载模板</el-button>
            </div>
            <el-table
              :data="tenantList"
              border
              height="400"
              style="width: 100%"
            >
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="姓名" prop="tenantName" width="120" show-overflow-tooltip />
              <el-table-column label="身份证号" prop="idCard" min-width="180" show-overflow-tooltip />
              <el-table-column label="手机号" prop="phone" width="130" />
              <el-table-column label="操作" align="center" width="80">
                <template slot-scope="scope">
                  <el-button
                    size="mini"
                    type="text"
                    icon="el-icon-delete"
                    @click="handleDeleteTenant(scope.$index)"
                    style="color: #F56C6C"
                  >删除</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-col>
        </el-row>

        <!-- 数量校验提示 -->
        <el-row style="margin-top: 20px;">
          <el-col :span="24">
            <el-alert
              v-if="selectedHouses.length === tenantList.length && selectedHouses.length > 0"
              title="房源数量和人员数量一致，可以提交"
              type="success"
              :closable="false"
              show-icon
            />
            <el-alert
              v-else-if="selectedHouses.length !== tenantList.length && (selectedHouses.length > 0 || tenantList.length > 0)"
              :title="`房源数量(${selectedHouses.length})和人员数量(${tenantList.length})不一致，请调整`"
              type="warning"
              :closable="false"
              show-icon
            />
          </el-col>
        </el-row>

        <!-- 一一对应匹配预览 -->
        <el-collapse v-if="showMatchPreview" v-model="activeCollapse" style="margin-top: 20px;">
          <el-collapse-item title="查看匹配预览（点击展开）" name="1">
            <el-table
              :data="matchPreviewData"
              border
              max-height="300"
              style="width: 100%"
            >
              <el-table-column label="序号" type="index" width="60" align="center" />
              <el-table-column label="房源编号" prop="houseCode" width="150" />
              <el-table-column label="房间号" prop="houseNo" width="120" />
              <el-table-column label="租金(元/月)" prop="rentPrice" width="120" align="center" />
              <el-table-column label="匹配" width="80" align="center">
                <template>
                  <i class="el-icon-right" style="font-size: 16px; color: #67C23A;"></i>
                </template>
              </el-table-column>
              <el-table-column label="人员姓名" prop="tenantName" width="120" />
              <el-table-column label="身份证号" prop="idCard" min-width="180" show-overflow-tooltip />
              <el-table-column label="手机号" prop="phone" width="130" />
            </el-table>
          </el-collapse-item>
        </el-collapse>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="selectedHouses.length !== tenantList.length || selectedHouses.length === 0">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="配租批次详情" :visible.sync="viewOpen" width="1200px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="批次编号">{{ viewData.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="批次名称">{{ viewData.batchName }}</el-descriptions-item>
        <el-descriptions-item label="人才类型">
          <el-tag v-if="viewData.talentType === '0'" type="info" size="small">普通人才</el-tag>
          <el-tag v-else type="success" size="small">定向人才</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="入驻开始日期">
          {{ parseTime(viewData.entryStartDate, '{y}-{m}-{d}') }}
        </el-descriptions-item>
        <el-descriptions-item label="入驻结束日期">
          {{ parseTime(viewData.entryEndDate, '{y}-{m}-{d}') }}
        </el-descriptions-item>
        <el-descriptions-item label="房源数量">{{ viewData.houseCount }}</el-descriptions-item>
        <el-descriptions-item label="已分配数量">{{ viewData.allocatedCount }}</el-descriptions-item>
        <el-descriptions-item label="创建人">{{ viewData.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ parseTime(viewData.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark }}</el-descriptions-item>
        <el-descriptions-item label="优惠方式">
          <span v-if="!viewData.preferentialType || viewData.preferentialType === '0'">无优惠</span>
          <span v-else-if="viewData.preferentialType === '1'">
            免租 <span style="color: #F56C6C; font-weight: bold;">{{ viewData.freeRentPeriods || 0 }}</span> 期
          </span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 分配房源列表 -->
      <el-divider content-position="left">分配房源列表</el-divider>
      <el-table
        :data="viewData.houseList"
        border
        max-height="300"
        style="width: 100%"
        v-loading="houseLoading"
      >
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column label="房源编号" prop="houseCode" width="150" show-overflow-tooltip />
        <el-table-column label="项目名称" prop="projectName" width="150" show-overflow-tooltip />
        <el-table-column label="楼栋" prop="buildingName" width="100" show-overflow-tooltip />
        <el-table-column label="单元" prop="unitName" width="100" show-overflow-tooltip />
        <el-table-column label="房间号" prop="houseNo" width="120" />
        <el-table-column label="楼层" prop="floor" width="80" align="center" />
        <el-table-column label="户型" prop="houseTypeName" width="140" show-overflow-tooltip />
        <el-table-column label="面积(㎡)" prop="area" width="100" align="center" />
        <el-table-column label="租金(元/月)" prop="rentPrice" width="120" align="center" />
        <el-table-column label="匹配人员" prop="tenantName" min-width="120" show-overflow-tooltip />
        <el-table-column label="分配状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.allocationStatus === '0'" type="info" size="small">未分配</el-tag>
            <el-tag v-else type="success" size="small">已分配</el-tag>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分配人员列表 -->
      <el-divider content-position="left">分配人员列表</el-divider>
      <el-table
        :data="viewData.tenantList"
        border
        max-height="300"
        style="width: 100%"
        v-loading="tenantLoading"
      >
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column label="姓名" prop="tenantName" width="120" />
        <el-table-column label="身份证号" prop="idCard" min-width="180" show-overflow-tooltip />
        <el-table-column label="手机号" prop="phone" width="130" />
        <el-table-column label="分配房源" prop="houseNo" width="120" />
        <el-table-column label="分配状态" align="center" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.allocationStatus === '0'" type="info" size="small">未分配</el-tag>
            <el-tag v-else type="success" size="small">已分配</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="分配时间" prop="allocationTime" width="160">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.allocationTime, '{y}-{m}-{d} {h}:{i}') }}</span>
          </template>
        </el-table-column>
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 审批对话框 -->
    <el-dialog title="审批配组批次" :visible.sync="approveOpen" width="600px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" label-width="100px">
        <el-form-item label="批次名称">
          <span>{{ approveRow.batchName }}</span>
        </el-form-item>
        <el-form-item label="批次编号">
          <span>{{ approveRow.batchNo }}</span>
        </el-form-item>
        <el-form-item label="房源数量">
          <span>{{ approveRow.houseCount }}套</span>
        </el-form-item>
        <el-form-item label="申请时间">
          <span>{{ parseTime(approveRow.applyTime) }}</span>
        </el-form-item>
        <el-form-item label="审批结果" prop="approveStatus">
          <el-radio-group v-model="approveForm.approveStatus">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审批意见">
          <el-input v-model="approveForm.remark" type="textarea" placeholder="请输入审批意见" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApprove">确 定</el-button>
        <el-button @click="approveOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listBatch, getBatch, addBatch, updateBatch, delBatch, getAvailableHouses, downloadTenantTemplate, importTenants, saveBatchAllocation, getBatchHouses, getBatchTenants, approveBatch } from "@/api/gangzhu/batch";
import { listProject } from "@/api/gangzhu/project";

export default {
  name: "Batch",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      batchList: [],
      projectList: [],
      title: "",
      open: false,
      viewOpen: false,
      approveOpen: false,
      approveRow: {},
      approveForm: {
        batchId: null,
        approveStatus: '1',
        remark: ''
      },
      viewData: {},
      houseLoading: false,
      tenantLoading: false,
      // 新增: 房源和人员分配相关
      selectedProjectIds: [],  // 多选项目
      availableHouseList: [],  // 可用房源列表
      selectedHouses: [],       // 已选房源
      tenantList: [],           // 人员列表
      showMatchPreview: false,  // 是否显示匹配预览
      activeCollapse: [],       // 折叠面板激活项
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        batchName: null,
        projectId: null,
        talentType: null
      },
      form: {},
      rules: {
        batchName: [
          { required: true, message: "批次名称不能为空", trigger: "blur" }
        ],
        talentType: [
          { required: true, message: "批次人才类型不能为空", trigger: "change" }
        ],
        entryStartDate: [
          { required: true, message: "入驻开始日期不能为空", trigger: "change" }
        ],
        entryEndDate: [
          { required: true, message: "入驻结束日期不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getProjectList();
  },
  watch: {
    // 监听项目变化，加载可用房源
    selectedProjectIds(newVal) {
      if (newVal && newVal.length > 0) {
        this.loadAvailableHouses();
      } else {
        this.availableHouseList = [];
        this.selectedHouses = [];
      }
    },
    // 监听房源和人员变化，检查数量匹配
    'selectedHouses.length'() {
      this.checkQuantityMatch();
    },
    'tenantList.length'() {
      this.checkQuantityMatch();
    }
  },
  computed: {
    // 计算匹配预览数据
    matchPreviewData() {
      const result = [];
      const maxLength = Math.max(this.selectedHouses.length, this.tenantList.length);
      for (let i = 0; i < maxLength; i++) {
        const house = this.selectedHouses[i] || {};
        const tenant = this.tenantList[i] || {};
        result.push({
          houseCode: house.houseCode,
          houseNo: house.houseNo,
          rentPrice: house.rentPrice,
          tenantName: tenant.tenantName,
          idCard: tenant.idCard,
          phone: tenant.phone
        });
      }
      return result;
    }
  },
  methods: {
    /** 查询配租批次列表 */
    getList() {
      this.loading = true;
      listBatch(this.queryParams).then(response => {
        this.batchList = response.rows || response.data || [];
        this.total = response.total || 0;
        this.loading = false;
      });
    },
    /** 查询项目列表 */
    getProjectList() {
      listProject({ status: "0" }).then(response => {
        this.projectList = response.rows || response.data || [];
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        batchId: null,
        batchNo: null,
        batchName: null,
        talentType: "0",  // 默认普通人才
        entryStartDate: null,
        entryEndDate: null,
        houseCount: 0,
        allocatedCount: 0,
        approveStatus: "0",
        batchStatus: "0",
        remark: null,
        preferentialType: "0",  // 默认无优惠
        freeRentPeriods: 0
      };
      // 重置新增的字段
      this.selectedProjectIds = [];
      this.availableHouseList = [];
      this.selectedHouses = [];
      this.tenantList = [];
      this.showMatchPreview = false;
      this.activeCollapse = [];
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
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.batchId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加配租批次";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const batchId = row.batchId || this.ids;

      // 使用 loading 状态
      this.loading = true;

      getBatch(batchId).then(response => {
        this.form = response.data;

        // 将 projectIds 字符串转换为数组
        if (this.form.projectIds) {
          this.selectedProjectIds = this.form.projectIds.split(',').map(id => parseInt(id));
        }

        // 使用 Promise.all 等待所有数据加载完成
        const promises = [];

        // 加载对应项目的可用房源
        if (this.form.projectIds) {
          const availableHousesPromise = new Promise((resolve) => {
            this.$nextTick(() => {
              // 编辑模式下保留已选房源
              this.loadAvailableHouses(true);
              // 等待可用房源加载完成
              const checkLoaded = () => {
                if (this.availableHouseList.length > 0) {
                  resolve();
                } else {
                  setTimeout(checkLoaded, 100);
                }
              };
              setTimeout(checkLoaded, 100);
            });
          });
          promises.push(availableHousesPromise);
        }

        // 加载批次的房源列表
        promises.push(
          getBatchHouses(batchId).then(houseList => {
            return this.setBatchHouses(houseList.data || []);
          })
        );

        // 加载批次的人员列表
        promises.push(
          getBatchTenants(batchId).then(tenantList => {
            const list = tenantList.data || [];
            this.tenantList = list.map(t => ({
              tenantName: t.tenantName,
              idCard: t.idCard,
              phone: t.phone
            }));
          })
        );

        // 等待所有数据加载完成后再打开对话框
        Promise.all(promises).then(() => {
          this.loading = false;
          this.open = true;
          this.title = "修改配租批次";
        }).catch(err => {
          console.error('加载数据失败:', err);
          this.loading = false;
          this.$modal.msgError('加载批次数据失败，请重试');
        });
      });
    },

    /** 设置批次房源（用于编辑） */
    setBatchHouses(houseList) {
      return new Promise((resolve) => {
        // 等待可用房源加载完成后再设置选中项
        const checkAndSetHouses = () => {
          if (this.availableHouseList.length > 0) {
            // 从可用房源列表中找到对应的房源并设置为选中
            this.selectedHouses = houseList.map(h => {
              return this.availableHouseList.find(ah => ah.houseId === h.houseId) || {
                houseId: h.houseId,
                houseCode: h.houseCode,
                houseNo: h.houseNo,
                floor: h.floor,
                houseTypeName: h.houseTypeName,
                area: h.area,
                rentPrice: h.rentPrice
              };
            });

            // 设置表格选中状态
            this.$nextTick(() => {
              if (this.$refs.houseTable) {
                this.selectedHouses.forEach(house => {
                  const row = this.availableHouseList.find(ah => ah.houseId === house.houseId);
                  if (row) {
                    this.$refs.houseTable.toggleRowSelection(row, true);
                  }
                });
              }
              resolve();
            });
          } else {
            // 如果可用房源还没加载完，等待一下再尝试
            setTimeout(checkAndSetHouses, 100);
          }
        };

        if (houseList.length > 0) {
          checkAndSetHouses();
        } else {
          resolve();
        }
      });
    },
    /** 详情按钮操作 */
    handleView(row) {
      this.houseLoading = true;
      this.tenantLoading = true;

      getBatch(row.batchId).then(response => {
        this.viewData = response.data;
        this.viewData.houseList = [];
        this.viewData.tenantList = [];
        this.viewOpen = true;

        // 加载房源列表
        this.loadBatchHouses(row.batchId);
        // 加载人员列表
        this.loadBatchTenants(row.batchId);
      });
    },
    /** 加载批次房源列表 */
    loadBatchHouses(batchId) {
      this.houseLoading = true;
      getBatchHouses(batchId).then(response => {
        this.viewData.houseList = response.data || [];
        this.houseLoading = false;
      }).catch(() => {
        this.houseLoading = false;
        this.viewData.houseList = [];
      });
    },
    /** 加载批次人员列表 */
    loadBatchTenants(batchId) {
      this.tenantLoading = true;
      getBatchTenants(batchId).then(response => {
        this.viewData.tenantList = response.data || [];
        this.tenantLoading = false;
      }).catch(() => {
        this.tenantLoading = false;
        this.viewData.tenantList = [];
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 验证项目选择
          if (!this.selectedProjectIds || this.selectedProjectIds.length === 0) {
            this.$modal.msgError('请选择所属项目');
            return;
          }

          // 检查房源和人员数量是否一致
          if (this.selectedHouses.length !== this.tenantList.length) {
            this.$modal.msgError('房源数量和人员数量必须一致');
            return;
          }

          if (this.selectedHouses.length === 0) {
            this.$modal.msgError('请选择房源并导入人员');
            return;
          }

          // 构建提交数据
          const params = {
            batchInfo: {
              batchId: this.form.batchId,
              batchName: this.form.batchName,
              talentType: this.form.talentType,
              projectIds: this.selectedProjectIds.join(','),
              entryStartDate: this.form.entryStartDate,
              entryEndDate: this.form.entryEndDate,
              remark: this.form.remark,
              preferentialType: this.form.preferentialType || '0',
              freeRentPeriods: this.form.preferentialType === '1' ? (this.form.freeRentPeriods || 0) : 0,
              houseCount: this.selectedHouses.length
            },
            houseList: this.selectedHouses.map(house => ({
              houseId: house.houseId,
              houseCode: house.houseCode,
              houseNo: house.houseNo
            })),
            tenantList: this.tenantList
          };

          // 调用批次分配接口
          saveBatchAllocation(params).then(response => {
            this.$modal.msgSuccess(this.form.batchId ? '修改成功' : '新增成功');
            this.open = false;
            this.getList();
          }).catch(err => {
            this.$modal.msgError(err.msg || '操作失败');
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const batchIds = row.batchId || this.ids;
      this.$modal.confirm('是否确认删除配租批次编号为"' + batchIds + '"的数据项?').then(function() {
        return delBatch(batchIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 审批按钮操作 */
    handleApprove(row) {
      this.approveRow = row;
      this.approveForm = {
        batchId: row.batchId,
        approveStatus: '1',
        remark: ''
      };
      this.approveOpen = true;
    },
    /** 提交审批 */
    submitApprove() {
      this.$modal.confirm('确认提交审批结果吗?').then(() => {
        return approveBatch(this.approveForm);
      }).then(() => {
        this.$modal.msgSuccess("审批成功");
        this.approveOpen = false;
        this.getList();
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/system/batch/export', {
        ...this.queryParams
      }, `batch_${new Date().getTime()}.xlsx`)
    },
    /** 加载可用房源 */
    loadAvailableHouses(keepSelection = false) {
      if (!this.selectedProjectIds || this.selectedProjectIds.length === 0) {
        return;
      }
      const projectIds = this.selectedProjectIds.join(',');
      getAvailableHouses(projectIds).then(response => {
        this.availableHouseList = response.data || [];
        // 只在非保留选择状态时清空已选房源（新增时清空，编辑时保留）
        if (!keepSelection) {
          this.selectedHouses = [];
        }
      }).catch(err => {
        this.$modal.msgError('加载房源失败: ' + (err.msg || '未知错误'));
      });
    },
    /** 下载人员导入模板 */
    handleDownloadTemplate() {
      downloadTenantTemplate().then(response => {
        const blob = new Blob([response], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
        const link = document.createElement('a');
        link.href = URL.createObjectURL(blob);
        link.download = '人员导入模板.xlsx';
        link.click();
        URL.revokeObjectURL(link.href);
        this.$modal.msgSuccess('模板下载成功');
      }).catch(() => {
        this.$modal.msgError('模板下载失败');
      });
    },
    /** 导入Excel */
    handleImportExcel({ file }) {
      importTenants(file).then(response => {
        if (response.code === 200) {
          this.tenantList = response.data || [];
          this.$modal.msgSuccess('导入成功！共导入 ' + this.tenantList.length + ' 人');
        } else {
          this.$modal.msgError(response.msg || '导入失败');
        }
      }).catch(err => {
        this.$modal.msgError('导入失败，请检查文件格式: ' + (err.msg || ''));
      });
    },
    /** 手动添加人员 */
    handleAddTenant() {
      this.$prompt('请输入人员信息（格式: 姓名,身份证号,手机号）', '添加人员', {
        inputPlaceholder: '例如: 张三,410123199001011234,13800138000',
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputValidator: (value) => {
          if (!value) {
            return '请输入人员信息';
          }
          const parts = value.split(',');
          if (parts.length !== 3) {
            return '格式错误，请按照格式输入: 姓名,身份证号,手机号';
          }
          return true;
        }
      }).then(({ value }) => {
        const parts = value.split(',');
        this.tenantList.push({
          tenantName: parts[0].trim(),
          idCard: parts[1].trim(),
          phone: parts[2].trim()
        });
        this.$modal.msgSuccess('添加成功');
      }).catch(() => {
        // 取消操作
      });
    },
    /** 删除人员 */
    handleDeleteTenant(index) {
      this.tenantList.splice(index, 1);
      this.$modal.msgSuccess('删除成功');
    },
    /** 房源选择变化 */
    handleHouseSelectionChange(selection) {
      this.selectedHouses = selection;
    },
    /** 检查数量匹配 */
    checkQuantityMatch() {
      this.showMatchPreview = this.selectedHouses.length > 0 && this.tenantList.length > 0;
    },
    /** 优惠方式变更处理 */
    handlePreferentialTypeChange(val) {
      if (val === '0') {
        // 切换到无优惠时，清空免租期数
        this.form.freeRentPeriods = 0;
      }
    }
  }
};
</script>

<style scoped>
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.section-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.count-tip {
  font-size: 12px;
  color: #409EFF;
  font-weight: 500;
}

.upload-actions {
  display: flex;
  gap: 10px;
}

.upload-actions .el-upload {
  display: inline-block;
}
</style>
