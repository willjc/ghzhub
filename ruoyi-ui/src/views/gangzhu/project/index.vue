<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="项目名称" prop="projectName">
        <el-input
          v-model="queryParams.projectName"
          placeholder="请输入项目名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="项目类型" prop="projectType">
        <el-select v-model="queryParams.projectType" placeholder="请选择项目类型" clearable>
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
          v-hasPermi="['gangzhu:project:add']"
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
          v-hasPermi="['gangzhu:project:edit']"
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
          v-hasPermi="['gangzhu:project:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="projectList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="项目名称" align="center" prop="projectName" width="180" show-overflow-tooltip />
      <el-table-column label="项目编码" align="center" prop="projectCode" width="120" />
      <el-table-column label="项目类型" align="center" prop="projectType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.projectType === '1'" type="success">人才公寓</el-tag>
          <el-tag v-else-if="scope.row.projectType === '2'" type="warning">保租房</el-tag>
          <el-tag v-else type="info">市场租赁</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="项目地址" align="center" prop="address" show-overflow-tooltip />
      <el-table-column label="负责人" align="center" prop="managerName" width="100" />
      <el-table-column label="联系电话" align="center" prop="managerPhone" width="120" />
      <el-table-column label="总楼栋数" align="center" prop="totalBuildings" width="100" />
      <el-table-column label="总房源" align="center" prop="totalHouses" width="100" />
      <el-table-column label="可用房源" align="center" prop="availableHouses" width="100" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="420">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:project:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:project:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:project:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-office-building"
            @click="handleManageBuilding(scope.row)"
            v-hasPermi="['gangzhu:building:list']"
          >管理楼栋</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-menu"
            @click="handleManageUnit(scope.row)"
            v-hasPermi="['gangzhu:unit:list']"
          >管理单元</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-tickets"
            @click="handleManageHouseType(scope.row)"
            v-hasPermi="['gangzhu:houseType:list']"
          >房型管理</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-magic-stick"
            @click="handleGenerateHouseTypes(scope.row)"
            v-hasPermi="['gangzhu:project:edit']"
          >生成房型</el-button>
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

    <!-- 添加或修改项目对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1000px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目名称" prop="projectName">
              <el-input v-model="form.projectName" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目编码" prop="projectCode">
              <el-input v-model="form.projectCode" placeholder="请输入项目编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目类型" prop="projectType">
              <el-select v-model="form.projectType" placeholder="请选择项目类型">
                <el-option label="人才公寓" value="1" />
                <el-option label="保租房" value="2" />
                <el-option label="市场租赁" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示顺序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="项目地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入项目地址" />
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="经度" prop="longitude">
              <el-input-number v-model="form.longitude" controls-position="right" :precision="6" :step="0.000001" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="纬度" prop="latitude">
              <el-input-number v-model="form.latitude" controls-position="right" :precision="6" :step="0.000001" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label=" ">
          <el-alert type="info" :closable="false" style="padding: 8px 16px;">
            <template slot="title">
              <span style="color: #606266;">如果不清楚坐标，请使用</span>
              <a href="https://lbs.amap.com/tools/picker" target="_blank" style="color: #409EFF; text-decoration: none; margin: 0 4px;">高德地图坐标拾取器</a>
              <span style="color: #606266;">来选择坐标</span>
            </template>
          </el-alert>
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="总楼栋数" prop="totalBuildings">
              <el-input-number v-model="form.totalBuildings" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="总房源数" prop="totalHouses">
              <el-input-number v-model="form.totalHouses" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="可用房源数" prop="availableHouses">
              <el-input-number v-model="form.availableHouses" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="负责人姓名" prop="managerName">
              <el-input v-model="form.managerName" placeholder="请输入负责人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人电话" prop="managerPhone">
              <el-input v-model="form.managerPhone" placeholder="请输入负责人电话" maxlength="11" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="项目介绍" prop="projectIntro">
          <el-input v-model="form.projectIntro" type="textarea" placeholder="请输入项目介绍" :rows="3" />
        </el-form-item>
        <el-form-item label="项目主图" prop="coverImage">
          <image-upload v-model="form.coverImage" :limit="1"/>
        </el-form-item>
        <el-form-item label="起租价格" prop="price">
          <el-input-number v-model="form.price" :precision="2" :step="100" :min="0" controls-position="right" style="width: 200px" />
          <span style="margin-left: 10px; color: #909399;">元/月</span>
        </el-form-item>
        <el-form-item label="配套设施" prop="facilities">
          <el-checkbox-group v-model="facilitiesList">
            <el-checkbox v-for="dict in dict.type.project_facilities" :key="dict.value" :label="dict.value">{{ dict.label }}</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
        <el-form-item label="交通信息" prop="transportation">
          <el-input v-model="form.transportation" type="textarea" placeholder="请输入交通信息" :rows="2" />
        </el-form-item>
        <el-form-item label="房源分布" prop="distribution">
          <el-input v-model="form.distribution" placeholder="请输入房源分布(如: 58号楼1-11层/63号楼1-11层)" />
        </el-form-item>
        <el-form-item label="户型" prop="houseType">
          <el-input v-model="form.houseType" placeholder="请输入户型(如: 一室一厅/二室二厅/三室三厅)" />
        </el-form-item>
        <el-form-item label="面积范围" prop="area">
          <el-input v-model="form.area" placeholder="请输入面积范围(如: 60平/90平/120平)" />
        </el-form-item>
        <el-form-item label="租金详情" prop="rentDetail">
          <el-input v-model="form.rentDetail" type="textarea" placeholder="请输入租金详情(可换行)" :rows="3" />
        </el-form-item>
        <el-form-item label="物业公司" prop="propertyCompany">
          <el-input v-model="form.propertyCompany" placeholder="请输入物业公司名称" />
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="物业费" prop="propertyFee">
              <el-input v-model="form.propertyFee" placeholder="如: 2.5元/平方米/月" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="电费" prop="electricFee">
              <el-input v-model="form.electricFee" placeholder="如: 0.6元/度" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="水费" prop="waterFee">
              <el-input v-model="form.waterFee" placeholder="如: 4.5元/吨" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="燃气费" prop="gasFee">
          <el-input v-model="form.gasFee" placeholder="如: 2.58元/立方" style="width: 300px" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="2" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 楼栋管理对话框 -->
    <el-dialog
      :title="'【' + currentProjectName + '】楼栋管理'"
      :visible.sync="buildingDialogVisible"
      width="1200px"
      top="5vh"
      append-to-body
      :close-on-click-modal="false"
    >
      <building-list
        v-if="buildingDialogVisible"
        :project-id="currentProjectId"
        :embedded="true"
      ></building-list>
    </el-dialog>

    <!-- 单元管理对话框 -->
    <el-dialog
      :title="'【' + currentProjectName + '】单元管理'"
      :visible.sync="unitDialogVisible"
      width="1200px"
      top="5vh"
      append-to-body
      :close-on-click-modal="false"
    >
      <unit-list
        v-if="unitDialogVisible"
        :project-id="currentProjectId"
        :embedded="true"
      ></unit-list>
    </el-dialog>

    <!-- 房型管理对话框 -->
    <el-dialog
      :title="'【' + currentProjectName + '】房型管理'"
      :visible.sync="houseTypeDialogVisible"
      width="1200px"
      top="5vh"
      append-to-body
      :close-on-click-modal="false"
    >
      <house-type-list
        v-if="houseTypeDialogVisible"
        :project-id="currentProjectId"
        :embedded="true"
      ></house-type-list>
    </el-dialog>

    <!-- 项目详情对话框 -->
    <el-dialog title="项目详情" :visible.sync="detailDialogVisible" width="900px" append-to-body>
      <div class="project-detail">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="项目名称">{{ detailData.projectName }}</el-descriptions-item>
          <el-descriptions-item label="项目编码">{{ detailData.projectCode }}</el-descriptions-item>
          <el-descriptions-item label="项目类型">
            <el-tag v-if="detailData.projectType === '1'" type="success" size="small">人才公寓</el-tag>
            <el-tag v-else-if="detailData.projectType === '2'" type="warning" size="small">保租房</el-tag>
            <el-tag v-else-if="detailData.projectType === '3'" type="info" size="small">市场租赁</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag v-if="detailData.status === '0'" type="success" size="small">正常</el-tag>
            <el-tag v-else type="danger" size="small">停用</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="项目地址" :span="2">{{ detailData.address }}</el-descriptions-item>
          <el-descriptions-item label="经度">{{ detailData.longitude }}</el-descriptions-item>
          <el-descriptions-item label="纬度">{{ detailData.latitude }}</el-descriptions-item>
          <el-descriptions-item label="总楼栋数">{{ detailData.totalBuildings || 0 }}</el-descriptions-item>
          <el-descriptions-item label="总房源数">{{ detailData.totalHouses || 0 }}</el-descriptions-item>
          <el-descriptions-item label="可用房源数">{{ detailData.availableHouses || 0 }}</el-descriptions-item>
          <el-descriptions-item label="房源分布" :span="2">{{ detailData.distribution || '无' }}</el-descriptions-item>
          <el-descriptions-item label="户型">{{ detailData.houseType || '无' }}</el-descriptions-item>
          <el-descriptions-item label="面积范围">{{ detailData.area || '无' }}</el-descriptions-item>
          <el-descriptions-item label="租金详情" :span="2" style="white-space: pre-wrap;">{{ detailData.rentDetail || '无' }}</el-descriptions-item>
          <el-descriptions-item label="物业公司" :span="2">{{ detailData.propertyCompany || '无' }}</el-descriptions-item>
          <el-descriptions-item label="物业费">{{ detailData.propertyFee || '无' }}</el-descriptions-item>
          <el-descriptions-item label="电费">{{ detailData.electricFee || '无' }}</el-descriptions-item>
          <el-descriptions-item label="水费">{{ detailData.waterFee || '无' }}</el-descriptions-item>
          <el-descriptions-item label="燃气费">{{ detailData.gasFee || '无' }}</el-descriptions-item>
          <el-descriptions-item label="显示顺序">{{ detailData.sortOrder || 0 }}</el-descriptions-item>
          <el-descriptions-item label="负责人姓名">{{ detailData.managerName }}</el-descriptions-item>
          <el-descriptions-item label="负责人电话">{{ detailData.managerPhone }}</el-descriptions-item>
          <el-descriptions-item label="项目介绍" :span="2">{{ detailData.projectIntro || '无' }}</el-descriptions-item>
          <el-descriptions-item label="项目主图" :span="2" v-if="detailData.coverImage">
            <el-image
              :src="getImageUrl(detailData.coverImage)"
              :preview-src-list="[getImageUrl(detailData.coverImage)]"
              style="width: 200px; height: 150px"
              fit="cover"
            >
              <div slot="error" class="image-error">
                <i class="el-icon-picture-outline"></i>
              </div>
            </el-image>
          </el-descriptions-item>
          <el-descriptions-item label="起租价格" :span="2" v-if="detailData.price !== null && detailData.price !== undefined">
            <span style="color: #e5252b; font-size: 18px; font-weight: bold;">{{ detailData.price }}</span>
            <span style="color: #666; margin-left: 5px;">元/月 起</span>
          </el-descriptions-item>
          <el-descriptions-item label="配套设施" :span="2">
            <el-tag
              v-for="(facility, index) in facilitiesArray"
              :key="index"
              size="small"
              style="margin-right: 8px; margin-bottom: 8px"
            >{{ facility }}</el-tag>
            <span v-if="!facilitiesArray || facilitiesArray.length === 0">无</span>
          </el-descriptions-item>
          <el-descriptions-item label="交通信息" :span="2">{{ detailData.transportation || '无' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '无' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间" :span="2">{{ detailData.createTime }}</el-descriptions-item>
        </el-descriptions>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProject, getProject, addProject, updateProject, delProject, generateHouseTypes } from "@/api/gangzhu/project";
import BuildingList from '@/views/gangzhu/building/index'
import UnitList from '@/views/gangzhu/unit/index'
import HouseTypeList from '@/views/gangzhu/houseType/index'
import ImageUpload from '@/components/ImageUpload';

export default {
  name: "Project",
  dicts: ['project_facilities'],
  components: {
    BuildingList,
    UnitList,
    HouseTypeList,
    ImageUpload
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
      // 项目表格数据
      projectList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 楼栋管理对话框
      buildingDialogVisible: false,
      // 单元管理对话框
      unitDialogVisible: false,
      // 房型管理对话框
      houseTypeDialogVisible: false,
      // 详情对话框
      detailDialogVisible: false,
      // 详情数据
      detailData: {},
      // 当前项目ID
      currentProjectId: null,
      // 当前项目名称
      currentProjectName: '',
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        projectName: null,
        projectType: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 配套设施列表(用于checkbox绑定)
      facilitiesList: [],
      // 表单校验
      rules: {
        projectName: [
          { required: true, message: "项目名称不能为空", trigger: "blur" }
        ],
        projectCode: [
          { required: true, message: "项目编码不能为空", trigger: "blur" }
        ],
        projectType: [
          { required: true, message: "项目类型不能为空", trigger: "change" }
        ],
        address: [
          { required: true, message: "项目地址不能为空", trigger: "blur" }
        ],
        managerName: [
          { required: true, message: "负责人姓名不能为空", trigger: "blur" }
        ],
        managerPhone: [
          { required: true, message: "负责人电话不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询项目列表 */
    getList() {
      this.loading = true;
      listProject(this.queryParams).then(response => {
        this.projectList = response.rows;
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
        projectId: null,
        projectName: null,
        projectCode: null,
        projectType: null,
        address: null,
        longitude: null,
        latitude: null,
        totalBuildings: 0,
        totalHouses: 0,
        availableHouses: 0,
        projectIntro: null,
        coverImage: null,
        price: null,
        facilities: null,
        transportation: null,
        managerId: null,
        managerName: null,
        managerPhone: null,
        status: "0",
        sortOrder: 0,
        remark: null
      };
      this.facilitiesList = [];
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
      this.ids = selection.map(item => item.projectId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加项目";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const projectId = row.projectId || this.ids
      getProject(projectId).then(response => {
        this.form = response.data;
        // 将逗号分隔的配套设施字符串转换为数组
        if (this.form.facilities) {
          this.facilitiesList = this.form.facilities.split(',');
        } else {
          this.facilitiesList = [];
        }
        this.open = true;
        this.title = "修改项目";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 将配套设施数组转换为逗号分隔的字符串
          this.form.facilities = this.facilitiesList.join(',');

          if (this.form.projectId != null) {
            updateProject(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProject(this.form).then(response => {
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
      const projectIds = row.projectId || this.ids;
      this.$modal.confirm('是否确认删除项目编号为"' + projectIds + '"的数据项？').then(function() {
        return delProject(projectIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 管理楼栋按钮操作 */
    handleManageBuilding(row) {
      this.currentProjectId = row.projectId;
      this.currentProjectName = row.projectName;
      this.buildingDialogVisible = true;
    },
    /** 管理单元按钮操作 */
    handleManageUnit(row) {
      this.currentProjectId = row.projectId;
      this.currentProjectName = row.projectName;
      this.unitDialogVisible = true;
    },
    /** 房型管理按钮操作 */
    handleManageHouseType(row) {
      this.currentProjectId = row.projectId;
      this.currentProjectName = row.projectName;
      this.houseTypeDialogVisible = true;
    },
    /** 批量生成房型按钮操作 */
    handleGenerateHouseTypes(row) {
      this.$modal.confirm('确认为项目"' + row.projectName + '"生成标准房型（一室~六室）吗？').then(() => {
        return generateHouseTypes(row.projectId);
      }).then(response => {
        this.$modal.msgSuccess(response.msg || "生成成功");
        // 可选：生成后自动打开房型管理对话框
        // this.handleManageHouseType(row);
      }).catch(() => {});
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      const projectId = row.projectId;
      getProject(projectId).then(response => {
        this.detailData = response.data;
        this.detailDialogVisible = true;
      });
    },
    /** 获取图片完整URL */
    getImageUrl(url) {
      if (!url) return '';

      // 外部链接(http/https开头),直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }

      // RuoYi标准:数据库存储相对路径,前端拼接baseUrl
      const baseUrl = process.env.VUE_APP_BASE_API;

      // 如果已包含baseUrl,直接返回
      if (url.indexOf(baseUrl) !== -1) {
        return url;
      }

      // 拼接baseUrl + 相对路径
      return baseUrl + (url.startsWith('/') ? url : '/' + url);
    }
  },
  computed: {
    // 配套设施数组（用于详情展示）
    facilitiesArray() {
      if (!this.detailData.facilities) return [];
      return this.detailData.facilities.split(',').filter(item => item.trim());
    }
  }
};
</script>

<style scoped>
.project-detail {
  padding: 20px;
}

.image-error {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 30px;
}
</style>
