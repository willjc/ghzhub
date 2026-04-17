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
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="520">
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
            icon="el-icon-setting"
            @click="handleFacilityManage(scope.row)"
          >房间设施管理</el-button>
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
    <el-dialog title="项目详情" :visible.sync="detailDialogVisible" width="1100px" append-to-body class="project-detail-dialog">
      <div class="project-detail" v-if="detailData.projectId">
        <!-- 顶部基本信息卡片 -->
        <div class="detail-header">
          <div class="header-cover">
            <el-image
              :src="getImageUrl(detailData.coverImage)"
              :preview-src-list="[getImageUrl(detailData.coverImage)]"
              fit="cover"
              class="cover-image"
            >
              <div slot="error" class="image-error">
                <i class="el-icon-office-building"></i>
              </div>
            </el-image>
          </div>
          <div class="header-info">
            <div class="info-top">
              <h2 class="project-name">{{ detailData.projectName }}</h2>
              <div class="tags">
                <el-tag v-if="detailData.projectType === '1'" type="success" size="small">人才公寓</el-tag>
                <el-tag v-else-if="detailData.projectType === '2'" type="warning" size="small">保租房</el-tag>
                <el-tag v-else type="info" size="small">市场租赁</el-tag>
                <el-tag v-if="detailData.status === '0'" type="success" size="small" plain>正常</el-tag>
                <el-tag v-else type="danger" size="small" plain>停用</el-tag>
              </div>
            </div>
            <div class="info-price" v-if="detailData.price">
              <span class="price-value">{{ detailData.price }}</span>
              <span class="price-unit">元/月 起</span>
            </div>
            <div class="info-address">
              <i class="el-icon-location-outline"></i>
              {{ detailData.address || '暂无地址' }}
            </div>
            <div class="info-stats">
              <div class="stat-item">
                <span class="stat-value">{{ detailData.totalBuildings || 0 }}</span>
                <span class="stat-label">楼栋</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-value">{{ detailData.totalHouses || 0 }}</span>
                <span class="stat-label">总房源</span>
              </div>
              <div class="stat-divider"></div>
              <div class="stat-item">
                <span class="stat-value available">{{ detailData.availableHouses || 0 }}</span>
                <span class="stat-label">可租</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 信息卡片区域 -->
        <el-row :gutter="20" class="detail-content">
          <!-- 基本信息 -->
          <el-col :span="12">
            <div class="info-card">
              <div class="card-title">
                <i class="el-icon-info"></i>
                <span>基本信息</span>
              </div>
              <div class="card-body">
                <div class="info-row">
                  <span class="label">显示顺序</span>
                  <span class="value">{{ detailData.sortOrder || 0 }}</span>
                </div>
                <div class="info-row">
                  <span class="label">房源分布</span>
                  <span class="value">{{ detailData.distribution || '无' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">户型</span>
                  <span class="value">{{ detailData.houseType || '无' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">面积范围</span>
                  <span class="value">{{ detailData.area || '无' }}</span>
                </div>
              </div>
            </div>
          </el-col>

          <!-- 联系信息 -->
          <el-col :span="12">
            <div class="info-card">
              <div class="card-title">
                <i class="el-icon-phone-outline"></i>
                <span>联系信息</span>
              </div>
              <div class="card-body">
                <div class="info-row">
                  <span class="label">负责人</span>
                  <span class="value">{{ detailData.managerName || '-' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">联系电话</span>
                  <span class="value highlight">{{ detailData.managerPhone || '-' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">物业公司</span>
                  <span class="value">{{ detailData.propertyCompany || '无' }}</span>
                </div>
              </div>
            </div>
          </el-col>

          <!-- 租金信息 -->
          <el-col :span="12" v-if="detailData.rentDetail || detailData.propertyFee || detailData.electricFee || detailData.waterFee || detailData.gasFee">
            <div class="info-card">
              <div class="card-title">
                <i class="el-icon-coin"></i>
                <span>费用信息</span>
              </div>
              <div class="card-body">
                <div class="info-row" v-if="detailData.rentDetail">
                  <span class="label">租金详情</span>
                  <span class="value multiline">{{ detailData.rentDetail }}</span>
                </div>
                <div class="info-row" v-if="detailData.propertyFee">
                  <span class="label">物业费</span>
                  <span class="value">{{ detailData.propertyFee }}</span>
                </div>
                <div class="info-row" v-if="detailData.electricFee">
                  <span class="label">电费</span>
                  <span class="value">{{ detailData.electricFee }}</span>
                </div>
                <div class="info-row" v-if="detailData.waterFee">
                  <span class="label">水费</span>
                  <span class="value">{{ detailData.waterFee }}</span>
                </div>
                <div class="info-row" v-if="detailData.gasFee">
                  <span class="label">燃气费</span>
                  <span class="value">{{ detailData.gasFee }}</span>
                </div>
              </div>
            </div>
          </el-col>

          <!-- 配套设施 -->
          <el-col :span="12" v-if="facilitiesArray && facilitiesArray.length > 0">
            <div class="info-card">
              <div class="card-title">
                <i class="el-icon-present"></i>
                <span>配套设施</span>
              </div>
              <div class="card-body">
                <div class="facilities-list">
                  <el-tag
                    v-for="(facility, index) in facilitiesArray"
                    :key="index"
                    type="info"
                    size="small"
                    effect="plain"
                  >{{ facility }}</el-tag>
                </div>
              </div>
            </div>
          </el-col>

          <!-- 交通信息 -->
          <el-col :span="12" v-if="detailData.transportation">
            <div class="info-card">
              <div class="card-title">
                <i class="el-icon-position"></i>
                <span>交通信息</span>
              </div>
              <div class="card-body">
                <div class="info-row">
                  <span class="value multiline">{{ detailData.transportation }}</span>
                </div>
              </div>
            </div>
          </el-col>

          <!-- 项目介绍 -->
          <el-col :span="12" v-if="detailData.projectIntro">
            <div class="info-card">
              <div class="card-title">
                <i class="el-icon-document"></i>
                <span>项目介绍</span>
              </div>
              <div class="card-body">
                <div class="info-row">
                  <span class="value multiline">{{ detailData.projectIntro }}</span>
                </div>
              </div>
            </div>
          </el-col>

          <!-- 坐标信息 -->
          <el-col :span="12" v-if="detailData.longitude || detailData.latitude">
            <div class="info-card">
              <div class="card-title">
                <i class="el-icon-map-location"></i>
                <span>坐标信息</span>
              </div>
              <div class="card-body">
                <div class="info-row">
                  <span class="label">经度</span>
                  <span class="value">{{ detailData.longitude || '-' }}</span>
                </div>
                <div class="info-row">
                  <span class="label">纬度</span>
                  <span class="value">{{ detailData.latitude || '-' }}</span>
                </div>
              </div>
            </div>
          </el-col>

          <!-- 备注 -->
          <el-col :span="12" v-if="detailData.remark">
            <div class="info-card">
              <div class="card-title">
                <i class="el-icon-notebook-2"></i>
                <span>备注</span>
              </div>
              <div class="card-body">
                <div class="info-row">
                  <span class="value multiline">{{ detailData.remark }}</span>
                </div>
              </div>
            </div>
          </el-col>

          <!-- 创建时间 -->
          <el-col :span="12">
            <div class="info-card">
              <div class="card-title">
                <i class="el-icon-time"></i>
                <span>系统信息</span>
              </div>
              <div class="card-body">
                <div class="info-row">
                  <span class="label">创建时间</span>
                  <span class="value">{{ detailData.createTime || '-' }}</span>
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="detailDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 房间设施管理对话框 -->
    <el-dialog title="房间设施管理" :visible.sync="facilityDialogVisible" width="1000px" append-to-body :close-on-click-modal="false">
      <div style="display: flex; height: 600px;">
        <!-- 左侧：户型列表 -->
        <div style="width: 200px; border-right: 1px solid #eee; overflow-y: auto; padding-right: 10px;">
          <div v-for="type in facilityHouseTypeList" :key="type.houseTypeId"
               :class="['type-item', { active: currentFacilityHouseTypeId === type.houseTypeId }]"
               @click="selectHouseType(type)">
            {{ type.houseTypeName }}
          </div>
          <div v-if="facilityHouseTypeList.length === 0" style="text-align: center; color: #999; padding-top: 50px;">
            暂无户型
          </div>
        </div>
        <!-- 右侧：设施配置表格 -->
        <div style="flex: 1; overflow-y: auto; padding-left: 15px;">
          <div v-if="!currentFacilityHouseTypeId" style="text-align: center; color: #999; padding-top: 200px;">
            请先选择左侧的户型
          </div>
          <div v-else>
            <div v-for="category in facilityCategories" :key="category" style="margin-bottom: 15px;">
              <h4 style="margin: 0 0 8px; color: #333; border-bottom: 1px solid #eee; padding-bottom: 5px;">{{ category }}</h4>
              <el-table :data="getFacilitiesByCategory(category)" size="mini" border>
                <el-table-column width="50" align="center">
                  <template slot-scope="scope">
                    <el-checkbox v-model="scope.row.checked"></el-checkbox>
                  </template>
                </el-table-column>
                <el-table-column label="物品名称" prop="facilityName" width="120" />
                <el-table-column label="数量" width="100">
                  <template slot-scope="scope">
                    <el-input-number v-model="scope.row.quantity" :min="1" :max="99" size="mini" :disabled="!scope.row.checked" />
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="120">
                  <template slot-scope="scope">
                    <el-select v-model="scope.row.itemStatus" size="mini" :disabled="!scope.row.checked">
                      <el-option label="完好" value="完好" />
                      <el-option label="破损" value="破损" />
                      <el-option label="缺失" value="缺失" />
                    </el-select>
                  </template>
                </el-table-column>
                <el-table-column label="说明">
                  <template slot-scope="scope">
                    <el-input v-model="scope.row.remark" size="mini" placeholder="选填" :disabled="!scope.row.checked" />
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer">
        <el-button @click="facilityDialogVisible = false">取 消</el-button>
        <el-button type="primary" @click="saveFacilities" :loading="facilitySaving">保 存</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProject, getProject, addProject, updateProject, delProject } from "@/api/gangzhu/project";
import { listHouseType } from "@/api/gangzhu/houseType";
import { listFacilityItem } from "@/api/gangzhu/facilityItem";
import { listHouseTypeFacility, batchSaveHouseTypeFacility } from "@/api/gangzhu/houseTypeFacility";
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
      // 设施管理相关
      facilityDialogVisible: false,
      currentFacilityHouseTypeId: null,
      facilityHouseTypeList: [],
      allFacilityItems: [],
      facilityConfigList: [],
      facilityCategories: ['电器类', '门窗类', '灯类', '卫浴区', '家具类', '洗菜池', '其他'],
      facilitySaving: false,
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
    },
    /** 房间设施管理按钮操作 */
    handleFacilityManage(row) {
      this.currentProjectId = row.projectId
      this.currentProjectName = row.projectName
      this.currentFacilityHouseTypeId = null
      this.facilityConfigList = []
      this.facilityDialogVisible = true
      // 加载该项目的户型列表
      listHouseType({ projectId: row.projectId }).then(res => {
        this.facilityHouseTypeList = res.rows || res.data || []
      })
      // 加载设施总表
      if (this.allFacilityItems.length === 0) {
        listFacilityItem().then(res => {
          this.allFacilityItems = res.data || res.rows || []
        })
      }
    },
    /** 选择户型 */
    selectHouseType(type) {
      this.currentFacilityHouseTypeId = type.houseTypeId
      // 基于总表构建配置列表，默认全部未勾选
      this.facilityConfigList = this.allFacilityItems.map(item => ({
        facilityItemId: item.facilityItemId,
        facilityName: item.facilityName,
        facilityCategory: item.facilityCategory,
        checked: false,
        quantity: 1,
        itemStatus: '完好',
        remark: ''
      }))
      // 加载该户型已保存的配置，回填
      listHouseTypeFacility(type.houseTypeId).then(res => {
        const saved = res.data || res.rows || []
        saved.forEach(s => {
          const target = this.facilityConfigList.find(f => f.facilityItemId === s.facilityItemId)
          if (target) {
            target.checked = true
            target.quantity = s.quantity || 1
            target.itemStatus = s.itemStatus || '完好'
            target.remark = s.remark || ''
          }
        })
      })
    },
    /** 按类别过滤设施 */
    getFacilitiesByCategory(category) {
      return this.facilityConfigList.filter(f => f.facilityCategory === category)
    },
    /** 保存设施配置 */
    saveFacilities() {
      if (!this.currentFacilityHouseTypeId) {
        this.$modal.msgWarning('请先选择户型')
        return
      }
      const checkedItems = this.facilityConfigList.filter(f => f.checked)
      this.facilitySaving = true
      batchSaveHouseTypeFacility({
        houseTypeId: this.currentFacilityHouseTypeId,
        facilities: checkedItems
      }).then(() => {
        this.$modal.msgSuccess('保存成功')
        this.facilitySaving = false
      }).catch(() => {
        this.facilitySaving = false
      })
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
  padding: 0;
}

/* 顶部头部区域 */
.detail-header {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8ecf1 100%);
  border-radius: 12px;
  margin-bottom: 20px;
}

.header-cover {
  flex-shrink: 0;
  width: 240px;
  height: 180px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.cover-image {
  width: 100%;
  height: 100%;
}

.header-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.info-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.project-name {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.tags {
  display: flex;
  gap: 8px;
}

.info-price {
  margin-top: 8px;
}

.price-value {
  font-size: 32px;
  font-weight: bold;
  color: #e5252b;
}

.price-unit {
  font-size: 14px;
  color: #909399;
  margin-left: 4px;
}

.info-address {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #606266;
  font-size: 14px;
  margin-top: 8px;
}

.info-address i {
  font-size: 16px;
  color: #909399;
}

.info-stats {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 12px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.stat-value.available {
  color: #67c23a;
}

.stat-label {
  font-size: 12px;
  color: #909399;
  margin-top: 2px;
}

.stat-divider {
  width: 1px;
  height: 36px;
  background-color: #dcdfe6;
}

/* 详情内容区 */
.detail-content {
  margin-top: 20px;
}

.info-card {
  background: #fff;
  border: 1px solid #ebeef5;
  border-radius: 10px;
  margin-bottom: 20px;
  overflow: hidden;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 16px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #ebeef5;
  font-size: 14px;
  font-weight: 500;
  color: #606266;
}

.card-title i {
  font-size: 16px;
  color: #409eff;
}

.card-body {
  padding: 12px 16px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px dashed #ebeef5;
}

.info-row:last-child {
  border-bottom: none;
}

.info-row .label {
  color: #909399;
  font-size: 14px;
  flex-shrink: 0;
}

.info-row .value {
  color: #303133;
  font-size: 14px;
  text-align: right;
}

.info-row .value.highlight {
  color: #409eff;
  font-weight: 500;
}

.info-row .value.multiline {
  white-space: pre-wrap;
  line-height: 1.6;
}

/* 配套设施列表 */
.facilities-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

/* 图片错误占位 */
.image-error {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #e8ecf1 0%, #d4d9e2 100%);
  color: #909399;
  font-size: 48px;
}

/* 户型列表项 */
.type-item {
  padding: 10px 12px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 4px;
  font-size: 14px;
}
.type-item:hover {
  background: #f5f7fa;
}
.type-item.active {
  background: #ecf5ff;
  color: #409eff;
  font-weight: bold;
}
</style>
