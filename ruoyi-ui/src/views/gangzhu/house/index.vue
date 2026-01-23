<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="所属项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择项目" clearable @change="handleQueryProjectChange" style="width: 200px">
          <el-option
            v-for="project in projectList"
            :key="project.projectId"
            :label="project.projectName"
            :value="project.projectId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="楼栋" prop="buildingId">
        <el-select v-model="queryParams.buildingId" placeholder="请选择楼栋" clearable @change="handleQueryBuildingChange" style="width: 150px">
          <el-option
            v-for="building in queryBuildingList"
            :key="building.buildingId"
            :label="building.buildingName"
            :value="building.buildingId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="单元" prop="unitId">
        <el-select v-model="queryParams.unitId" placeholder="请选择单元" clearable style="width: 150px">
          <el-option
            v-for="unit in queryUnitList"
            :key="unit.unitId"
            :label="unit.unitName"
            :value="unit.unitId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="房源编码" prop="houseCode">
        <el-input
          v-model="queryParams.houseCode"
          placeholder="请输入房源编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="房间号" prop="houseNo">
        <el-input
          v-model="queryParams.houseNo"
          placeholder="请输入房间号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="房源状态" prop="houseStatus">
        <el-select v-model="queryParams.houseStatus" placeholder="请选择房源状态" clearable>
          <el-option label="空置" value="0" />
          <el-option label="已预订" value="1" />
          <el-option label="已出租" value="2" />
          <el-option label="维修中" value="3" />
          <el-option label="下架" value="4" />
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
          v-hasPermi="['gangzhu:house:add']"
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
          v-hasPermi="['gangzhu:house:edit']"
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
          v-hasPermi="['gangzhu:house:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImport"
          v-hasPermi="['gangzhu:house:import']"
        >导入</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:house:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="houseList" @selection-change="handleSelectionChange" style="width: 100%">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="项目名称" align="center" prop="projectName" width="150" show-overflow-tooltip />
      <el-table-column label="房源编码" align="center" prop="houseCode" width="120" />
      <el-table-column label="房间号" align="center" prop="houseNo" width="100" />
      <el-table-column label="户型" align="center" prop="houseTypeName" width="120" />
      <el-table-column label="楼层" align="center" prop="floor" width="80" />
      <el-table-column label="面积(㎡)" align="center" prop="area" width="100" />
      <el-table-column label="租金(元/月)" align="center" prop="rentPrice" width="120" />
      <el-table-column label="押金(元)" align="center" prop="deposit" width="120" />
      <el-table-column label="房源状态" align="center" prop="houseStatus" min-width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.houseStatus === '0'" type="success">空置</el-tag>
          <el-tag v-else-if="scope.row.houseStatus === '1'" type="warning">已预订</el-tag>
          <el-tag v-else-if="scope.row.houseStatus === '2'" type="danger">已出租</el-tag>
          <el-tag v-else-if="scope.row.houseStatus === '3'" type="info">维修中</el-tag>
          <el-tag v-else type="info">下架</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="精选" align="center" prop="isFeatured" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isFeatured === '1'" type="success" size="small">是</el-tag>
          <el-tag v-else type="info" size="small">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="浏览次数" align="center" prop="viewCount" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:house:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:house:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:house:remove']"
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

    <!-- 添加或修改房源对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属项目" prop="projectId">
              <el-select v-model="form.projectId" placeholder="请选择项目" style="width: 100%" @change="handleProjectChange">
                <el-option
                  v-for="project in projectList"
                  :key="project.projectId"
                  :label="project.projectName"
                  :value="project.projectId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属楼栋" prop="buildingId">
              <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%" @change="handleBuildingChange">
                <el-option
                  v-for="building in buildingList"
                  :key="building.buildingId"
                  :label="building.buildingName"
                  :value="building.buildingId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属单元" prop="unitId">
              <el-select v-model="form.unitId" placeholder="请选择单元" style="width: 100%">
                <el-option
                  v-for="unit in unitList"
                  :key="unit.unitId"
                  :label="unit.unitName"
                  :value="unit.unitId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房源编码" prop="houseCode">
              <el-input v-model="form.houseCode" placeholder="请输入房源编码" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="房间号" prop="houseNo">
              <el-input v-model="form.houseNo" placeholder="请输入房间号" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼层" prop="floor">
              <el-input-number v-model="form.floor" controls-position="right" :min="1" :max="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="选择户型" prop="houseTypeId">
              <el-select v-model="form.houseTypeId" placeholder="请选择户型" style="width: 100%" @change="handleHouseTypeChange" clearable>
                <el-option
                  v-for="houseType in houseTypeList"
                  :key="houseType.houseTypeId"
                  :label="`${houseType.houseTypeName}(${houseType.bedroomCount}室${houseType.livingroomCount}厅${houseType.bathroomCount}卫)`"
                  :value="houseType.houseTypeId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="建筑面积(㎡)" prop="area">
              <el-input-number v-model="form.area" controls-position="right" :min="0" :precision="2" :step="0.01" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="朝向" prop="orientation">
              <el-select v-model="form.orientation" placeholder="请选择朝向" style="width: 100%">
                <el-option label="东" value="东" />
                <el-option label="南" value="南" />
                <el-option label="西" value="西" />
                <el-option label="北" value="北" />
                <el-option label="东南" value="东南" />
                <el-option label="东北" value="东北" />
                <el-option label="西南" value="西南" />
                <el-option label="西北" value="西北" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租金(元/月)" prop="rentPrice">
              <el-input-number v-model="form.rentPrice" controls-position="right" :min="0" :precision="2" :step="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="押金(元)" prop="deposit">
              <el-input-number v-model="form.deposit" controls-position="right" :min="0" :precision="2" :step="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="装修情况" prop="decoration">
              <el-select v-model="form.decoration" placeholder="请选择装修情况" style="width: 100%">
                <el-option label="毛坯" value="毛坯" />
                <el-option label="简装" value="简装" />
                <el-option label="精装" value="精装" />
                <el-option label="豪华装修" value="豪华装修" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="分配类型" prop="allocationType">
              <el-select v-model="form.allocationType" placeholder="请选择分配类型" style="width: 100%">
                <el-option label="常规分配" value="1" />
                <el-option label="集中分配" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应届生房源" prop="isFreshGraduate">
              <el-radio-group v-model="form.isFreshGraduate">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="管家电话" prop="managerPhone">
              <el-input v-model="form.managerPhone" placeholder="请输入管家电话" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房源状态" prop="houseStatus">
              <el-select v-model="form.houseStatus" placeholder="请选择房源状态" style="width: 100%">
                <el-option label="空置" value="0" />
                <el-option label="已预订" value="1" />
                <el-option label="已出租" value="2" />
                <el-option label="维修中" value="3" />
                <el-option label="下架" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="是否精选" prop="isFeatured">
          <el-radio-group v-model="form.isFeatured">
            <el-radio label="0">否</el-radio>
            <el-radio label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="房间设施" prop="facilities">
          <el-input v-model="form.facilities" type="textarea" placeholder="多个设施用逗号分隔,如:空调,冰箱,洗衣机" :rows="2" />
        </el-form-item>
        <el-form-item label="房源简介" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入房源简介,将在用户端房源详情页展示" :rows="4" />
          <div class="form-tip">房源简介将在用户端详情页显示，建议填写房源亮点、周边配套等信息</div>
        </el-form-item>

        <!-- 房源图片 - 分类上传 -->
        <el-divider content-position="left">房源图片</el-divider>

        <el-form-item label="主图">
          <image-upload v-model="form.mainImageList" :limit="9" />
          <div class="form-tip">用于首页展示和列表封面，建议上传高清图片</div>
        </el-form-item>

        <el-form-item label="户型图">
          <image-upload v-model="form.layoutImageList" :limit="5" />
          <div class="form-tip">房屋平面布局图</div>
        </el-form-item>

        <el-form-item label="卧室">
          <image-upload v-model="form.bedroomImageList" :limit="9" />
          <div class="form-tip">卧室实景照片</div>
        </el-form-item>

        <el-form-item label="卫生间">
          <image-upload v-model="form.bathroomImageList" :limit="5" />
          <div class="form-tip">卫生间实景照片</div>
        </el-form-item>

        <el-form-item label="室内">
          <image-upload v-model="form.indoorImageList" :limit="9" />
          <div class="form-tip">客厅、厨房等室内其他区域照片</div>
        </el-form-item>

        <el-form-item label="室外">
          <image-upload v-model="form.outdoorImageList" :limit="9" />
          <div class="form-tip">阳台、外景等室外照片</div>
        </el-form-item>

        <el-form-item label="VR看房">
          <image-upload v-model="form.vrList" :limit="5"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 房源详情对话框 -->
    <el-dialog title="房源详情" :visible.sync="detailOpen" width="1000px" append-to-body>
      <div class="house-detail">
        <!-- 基本信息 -->
        <el-card class="detail-card">
          <div slot="header" class="card-header">
            <span><i class="el-icon-house"></i> 基本信息</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-item">
                <label>房源编码:</label>
                <span>{{ detailData.house ? detailData.house.houseCode : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <label>房间号:</label>
                <span>{{ detailData.house ? detailData.house.houseNo : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <label>楼层:</label>
                <span>{{ detailData.house ? detailData.house.floor + '层' : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <label>面积:</label>
                <span>{{ detailData.house ? detailData.house.area + '㎡' : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <label>租金:</label>
                <span>{{ detailData.house ? detailData.house.rentPrice + '元/月' : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <label>押金:</label>
                <span>{{ detailData.house ? detailData.house.deposit + '元' : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <label>朝向:</label>
                <span>{{ detailData.house ? detailData.house.orientation : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <label>装修情况:</label>
                <span>{{ detailData.house ? detailData.house.decoration : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <label>分配类型:</label>
                <span>{{ detailData.house && detailData.house.allocationType === '1' ? '常规分配' : detailData.house && detailData.house.allocationType === '2' ? '集中分配' : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <label>应届生房源:</label>
                <span>{{ detailData.house && detailData.house.isFreshGraduate === '1' ? '是' : '否' }}</span>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-item">
                <label>管家电话:</label>
                <span>{{ detailData.house ? (detailData.house.managerPhone || '-') : '-' }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <label>房源状态:</label>
                <el-tag v-if="detailData.house && detailData.house.houseStatus === '0'" type="success">空置</el-tag>
                <el-tag v-else-if="detailData.house && detailData.house.houseStatus === '1'" type="warning">已预订</el-tag>
                <el-tag v-else-if="detailData.house && detailData.house.houseStatus === '2'" type="danger">已出租</el-tag>
                <el-tag v-else-if="detailData.house && detailData.house.houseStatus === '3'" type="info">维修中</el-tag>
                <el-tag v-else type="info">下架</el-tag>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <label>房间设施:</label>
                <span>{{ detailData.house ? (detailData.house.facilities || '无') : '无' }}</span>
              </div>
            </el-col>
            <el-col :span="24">
              <div class="info-item">
                <label>备注:</label>
                <span>{{ detailData.house ? (detailData.house.remark || '无') : '无' }}</span>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 关联信息 -->
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <el-card class="detail-card">
              <div slot="header" class="card-header">
                <span><i class="el-icon-office-building"></i> 项目信息</span>
              </div>
              <div class="info-item">
                <label>项目名称:</label>
                <span>{{ detailData.project ? detailData.project.projectName : '-' }}</span>
              </div>
              <div class="info-item">
                <label>项目地址:</label>
                <span>{{ detailData.project ? detailData.project.address : '-' }}</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="detail-card">
              <div slot="header" class="card-header">
                <span><i class="el-icon-map-location"></i> 楼栋单元</span>
              </div>
              <div class="info-item">
                <label>楼栋:</label>
                <span>{{ detailData.building ? detailData.building.buildingName : '-' }}</span>
              </div>
              <div class="info-item">
                <label>单元:</label>
                <span>{{ detailData.unit ? detailData.unit.unitName : '-' }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <el-card class="detail-card">
              <div slot="header" class="card-header">
                <span><i class="el-icon-s-home"></i> 户型信息</span>
              </div>
              <div class="info-item">
                <label>户型名称:</label>
                <span>{{ detailData.houseType ? detailData.houseType.houseTypeName : '-' }}</span>
              </div>
              <div class="info-item">
                <label>户型详情:</label>
                <span>{{ detailData.houseType ? (detailData.houseType.remark || '-') : '-' }}</span>
              </div>
            </el-card>
          </el-col>
          <el-col :span="12">
            <el-card class="detail-card">
              <div slot="header" class="card-header">
                <span><i class="el-icon-data-line"></i> 统计信息</span>
              </div>
              <div class="info-item">
                <label>浏览次数:</label>
                <span>{{ detailData.house ? (detailData.house.viewCount || 0) : 0 }}次</span>
              </div>
              <div class="info-item">
                <label>是否精选:</label>
                <el-tag v-if="detailData.house && detailData.house.isFeatured === '1'" type="success" size="small">是</el-tag>
                <el-tag v-else type="info" size="small">否</el-tag>
              </div>
              <div class="info-item">
                <label>创建时间:</label>
                <span>{{ detailData.house ? detailData.house.createTime : '-' }}</span>
              </div>
            </el-card>
          </el-col>
        </el-row>

        <!-- 房源图片 - 分类展示 -->
        <el-card class="detail-card" style="margin-top: 20px;" v-if="detailData.images && detailData.images.length > 0">
          <div slot="header" class="card-header">
            <span><i class="el-icon-picture"></i> 房源图片</span>
          </div>

          <el-tabs type="border-card">
            <!-- 主图 -->
            <el-tab-pane v-if="getImagesByType('1').length > 0">
              <span slot="label"><i class="el-icon-picture-outline"></i> 主图 ({{ getImagesByType('1').length }})</span>
              <el-row :gutter="10">
                <el-col :span="6" v-for="(image, index) in getImagesByType('1')" :key="'main-' + index">
                  <div class="image-item">
                    <el-image
                      :src="getImageUrl(image.imageUrl)"
                      :preview-src-list="getImagePreviewList('1')"
                      fit="cover"
                      style="width: 100%; height: 200px;"
                    >
                      <div slot="error" class="image-error">
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </el-image>
                    <div class="image-caption">
                      {{ image.isCover === '1' ? '封面' : '主图' + (index + 1) }}
                    </div>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>

            <!-- 户型图 -->
            <el-tab-pane v-if="getImagesByType('2').length > 0">
              <span slot="label"><i class="el-icon-tickets"></i> 户型图 ({{ getImagesByType('2').length }})</span>
              <el-row :gutter="10">
                <el-col :span="6" v-for="(image, index) in getImagesByType('2')" :key="'layout-' + index">
                  <div class="image-item">
                    <el-image
                      :src="getImageUrl(image.imageUrl)"
                      :preview-src-list="getImagePreviewList('2')"
                      fit="cover"
                      style="width: 100%; height: 200px;"
                    >
                      <div slot="error" class="image-error">
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </el-image>
                    <div class="image-caption">户型图{{ index + 1 }}</div>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>

            <!-- 卧室 -->
            <el-tab-pane v-if="getImagesByType('3').length > 0">
              <span slot="label"><i class="el-icon-s-home"></i> 卧室 ({{ getImagesByType('3').length }})</span>
              <el-row :gutter="10">
                <el-col :span="6" v-for="(image, index) in getImagesByType('3')" :key="'bedroom-' + index">
                  <div class="image-item">
                    <el-image
                      :src="getImageUrl(image.imageUrl)"
                      :preview-src-list="getImagePreviewList('3')"
                      fit="cover"
                      style="width: 100%; height: 200px;"
                    >
                      <div slot="error" class="image-error">
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </el-image>
                    <div class="image-caption">卧室{{ index + 1 }}</div>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>

            <!-- 卫生间 -->
            <el-tab-pane v-if="getImagesByType('4').length > 0">
              <span slot="label"><i class="el-icon-toilet-paper"></i> 卫生间 ({{ getImagesByType('4').length }})</span>
              <el-row :gutter="10">
                <el-col :span="6" v-for="(image, index) in getImagesByType('4')" :key="'bathroom-' + index">
                  <div class="image-item">
                    <el-image
                      :src="getImageUrl(image.imageUrl)"
                      :preview-src-list="getImagePreviewList('4')"
                      fit="cover"
                      style="width: 100%; height: 200px;"
                    >
                      <div slot="error" class="image-error">
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </el-image>
                    <div class="image-caption">卫生间{{ index + 1 }}</div>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>

            <!-- 室内 -->
            <el-tab-pane v-if="getImagesByType('5').length > 0">
              <span slot="label"><i class="el-icon-office-building"></i> 室内 ({{ getImagesByType('5').length }})</span>
              <el-row :gutter="10">
                <el-col :span="6" v-for="(image, index) in getImagesByType('5')" :key="'indoor-' + index">
                  <div class="image-item">
                    <el-image
                      :src="getImageUrl(image.imageUrl)"
                      :preview-src-list="getImagePreviewList('5')"
                      fit="cover"
                      style="width: 100%; height: 200px;"
                    >
                      <div slot="error" class="image-error">
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </el-image>
                    <div class="image-caption">室内{{ index + 1 }}</div>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>

            <!-- 室外 -->
            <el-tab-pane v-if="getImagesByType('6').length > 0">
              <span slot="label"><i class="el-icon-sunny"></i> 室外 ({{ getImagesByType('6').length }})</span>
              <el-row :gutter="10">
                <el-col :span="6" v-for="(image, index) in getImagesByType('6')" :key="'outdoor-' + index">
                  <div class="image-item">
                    <el-image
                      :src="getImageUrl(image.imageUrl)"
                      :preview-src-list="getImagePreviewList('6')"
                      fit="cover"
                      style="width: 100%; height: 200px;"
                    >
                      <div slot="error" class="image-error">
                        <i class="el-icon-picture-outline"></i>
                      </div>
                    </el-image>
                    <div class="image-caption">室外{{ index + 1 }}</div>
                  </div>
                </el-col>
              </el-row>
            </el-tab-pane>
          </el-tabs>
        </el-card>

        <!-- VR看房 -->
        <el-card class="detail-card" style="margin-top: 20px;" v-if="detailData.vrs && detailData.vrs.length > 0">
          <div slot="header" class="card-header">
            <span><i class="el-icon-view"></i> VR看房</span>
          </div>
          <div style="text-align: center; padding: 20px;">
            <el-button type="primary" icon="el-icon-video-camera" @click="handleVrView">进入VR看房</el-button>
            <p style="color: #909399; margin-top: 10px;">共 {{ detailData.vrs.length }} 个VR全景</p>
          </div>
        </el-card>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- VR查看器 -->
    <vr-viewer :visible.sync="vrViewerVisible" :vrList="vrList" />

    <!-- 房源导入对话框 -->
    <el-dialog :title="upload.title" :visible.sync="upload.open" width="400px" append-to-body>
      <el-upload
        ref="upload"
        :limit="1"
        accept=".xlsx, .xls"
        :headers="upload.headers"
        :action="upload.url + '?updateSupport=' + upload.updateSupport"
        :disabled="upload.isUploading"
        :on-progress="handleFileUploadProgress"
        :on-success="handleFileSuccess"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip text-center" slot="tip">
          <div class="el-upload__tip" slot="tip">
            <el-checkbox v-model="upload.updateSupport" />是否更新已经存在的房源数据
          </div>
          <span>仅允许导入xls、xlsx格式文件。</span>
          <el-link type="primary" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitFileForm">确 定</el-button>
        <el-button @click="upload.open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listHouse, getHouse, getHouseDetail, addHouse, updateHouse, delHouse, getHouseImages, saveHouseImages, getHouseVrs, saveHouseVrs } from "@/api/gangzhu/house";
import { listProject } from "@/api/gangzhu/project";
import { listBuilding } from "@/api/gangzhu/building";
import { listUnit } from "@/api/gangzhu/unit";
import { listHouseType } from "@/api/gangzhu/houseType";
import ImageUpload from '@/components/ImageUpload';
import VrViewer from '@/components/VrViewer';

export default {
  name: "House",
  components: {
    ImageUpload,
    VrViewer
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
      // 房源表格数据
      houseList: [],
      // 项目列表
      projectList: [],
      // 楼栋列表
      buildingList: [],
      // 查询用楼栋列表（单独的）
      queryBuildingList: [],
      // 单元列表
      unitList: [],
      // 查询用单元列表（单独的）
      queryUnitList: [],
      // 房型列表
      houseTypeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // VR查看器是否显示
      vrViewerVisible: false,
      // VR列表
      vrList: [],
      // 是否显示导入对话框
      upload: {
        open: false,
        title: "",
        isUploading: false,
        updateSupport: false,
        headers: { Authorization: "Bearer " + this.$store.getters.token },
        url: process.env.VUE_APP_BASE_API + "/system/house/importData"
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        projectId: null,
        buildingId: null,
        unitId: null,
        houseCode: null,
        houseNo: null,
        houseStatus: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 详情数据
      detailData: {
        house: {},
        project: {},
        building: {},
        unit: {},
        houseType: {},
        images: []
      },
      // 表单校验
      rules: {
        houseCode: [
          { required: true, message: "房源编码不能为空", trigger: "blur" }
        ],
        houseNo: [
          { required: true, message: "房间号不能为空", trigger: "blur" }
        ],
        floor: [
          { required: true, message: "楼层不能为空", trigger: "blur" }
        ],
        houseTypeId: [
          { required: true, message: "请选择户型", trigger: "change" }
        ],
        area: [
          { required: true, message: "建筑面积不能为空", trigger: "blur" }
        ],
        rentPrice: [
          { required: true, message: "租金不能为空", trigger: "blur" }
        ],
        deposit: [
          { required: true, message: "押金不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
    this.getProjectList();
    // 移除初始加载房型列表，改为选择项目时加载
  },
  methods: {
    /** 查询房源列表 */
    getList(params) {
      // 支持从分页组件接收参数
      if (params) {
        this.queryParams.pageNum = params.page;
        this.queryParams.pageSize = params.limit;
      }
      this.loading = true;
      listHouse(this.queryParams).then(response => {
        this.houseList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 查询项目列表 */
    getProjectList() {
      listProject({ status: "0" }).then(response => {
        this.projectList = response.rows || response.data || [];
      });
    },
    /** 查询房型列表 */
    getHouseTypeList(projectId) {
      if (projectId) {
        listHouseType({ projectId: projectId, status: "0" }).then(response => {
          this.houseTypeList = response.rows || response.data || [];
        });
      } else {
        this.houseTypeList = [];
      }
    },
    /** 项目变更 - 加载对应楼栋和房型 */
    handleProjectChange(projectId) {
      this.form.buildingId = null;
      this.form.unitId = null;
      this.form.houseTypeId = null;
      this.buildingList = [];
      this.unitList = [];
      this.houseTypeList = [];
      if (projectId) {
        // 加载楼栋列表
        listBuilding({ projectId: projectId, status: "0" }).then(response => {
          this.buildingList = response.rows || response.data || [];
        });
        // 加载房型列表
        this.getHouseTypeList(projectId);
      }
    },
    /** 楼栋变更 - 加载对应单元 */
    handleBuildingChange(buildingId) {
      this.form.unitId = null;
      this.unitList = [];
      if (buildingId) {
        listUnit({ buildingId: buildingId, status: "0" }).then(response => {
          this.unitList = response.rows || response.data || [];
        });
      }
    },
    /** 房型选择变更 - 自动填充户型名称和面积 */
    handleHouseTypeChange(houseTypeId) {
      if (houseTypeId) {
        const selectedHouseType = this.houseTypeList.find(item => item.houseTypeId === houseTypeId);
        if (selectedHouseType) {
          this.form.houseTypeName = selectedHouseType.houseTypeName;
          this.form.houseTypeDetail = selectedHouseType.remark; // remark字段存储了详细描述
          // 自动填充面积（来自户型的典型面积）
          this.form.area = selectedHouseType.typicalArea;
        }
      } else {
        this.form.houseTypeName = null;
        this.form.houseTypeDetail = null;
        this.form.area = null;
      }
    },
    /** 查询条件-项目变更 - 加载对应楼栋 */
    handleQueryProjectChange(projectId) {
      this.queryParams.buildingId = null;
      this.queryParams.unitId = null;
      this.queryBuildingList = [];
      this.queryUnitList = [];
      if (projectId) {
        listBuilding({ projectId: projectId, status: "0" }).then(response => {
          this.queryBuildingList = response.rows || response.data || [];
        });
      }
    },
    /** 查询条件-楼栋变更 - 加载对应单元 */
    handleQueryBuildingChange(buildingId) {
      this.queryParams.unitId = null;
      this.queryUnitList = [];
      if (buildingId) {
        listUnit({ buildingId: buildingId, status: "0" }).then(response => {
          this.queryUnitList = response.rows || response.data || [];
        });
      }
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        houseId: null,
        projectId: null,
        buildingId: null,
        unitId: null,
        houseCode: null,
        houseNo: null,
        floor: null,
        houseTypeId: null,
        houseTypeName: null,
        houseTypeDetail: null,
        area: null,
        rentPrice: null,
        deposit: null,
        orientation: null,
        decoration: null,
        facilities: null,
        allocationType: "1",     // 默认常规分配
        isFreshGraduate: "0",    // 默认否
        managerPhone: null,
        houseStatus: "0",
        isFeatured: "0",
        status: "0",
        remark: null,
        mainImageList: "",
        layoutImageList: "",
        bedroomImageList: "",
        bathroomImageList: "",
        indoorImageList: "",
        outdoorImageList: "",
        vrList: ""
      };
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
      this.ids = selection.map(item => item.houseId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加房源";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const houseId = row.houseId || this.ids
      getHouse(houseId).then(response => {
        this.form = response.data;
        // 加载对应的楼栋、单元和房型
        if (this.form.projectId) {
          listBuilding({ projectId: this.form.projectId, status: "0" }).then(res => {
            this.buildingList = res.rows || res.data || [];
          });
          this.getHouseTypeList(this.form.projectId);
        }
        if (this.form.buildingId) {
          listUnit({ buildingId: this.form.buildingId, status: "0" }).then(res => {
            this.unitList = res.rows || res.data || [];
          });
        }
        // 加载房源图片
        this.loadHouseImages(houseId);
        // 加载房源VR
        this.loadHouseVrs(houseId);
        this.open = true;
        this.title = "修改房源";
      });
    },
    /** 加载房源图片 */
    loadHouseImages(houseId) {
      getHouseImages(houseId).then(response => {
        if (response.data && response.data.length > 0) {
          // 按图片类型分组
          const imagesByType = {
            '1': [], // 主图
            '2': [], // 户型图
            '3': [], // 卧室
            '4': [], // 卫生间
            '5': [], // 室内
            '6': []  // 室外
          };

          response.data.forEach(item => {
            const type = item.imageType || '1';
            if (imagesByType[type]) {
              imagesByType[type].push(item.imageUrl);
            }
          });

          this.$nextTick(() => {
            this.form.mainImageList = imagesByType['1'].join(',');
            this.form.layoutImageList = imagesByType['2'].join(',');
            this.form.bedroomImageList = imagesByType['3'].join(',');
            this.form.bathroomImageList = imagesByType['4'].join(',');
            this.form.indoorImageList = imagesByType['5'].join(',');
            this.form.outdoorImageList = imagesByType['6'].join(',');
          });
        } else {
          this.form.mainImageList = "";
          this.form.layoutImageList = "";
          this.form.bedroomImageList = "";
          this.form.bathroomImageList = "";
          this.form.indoorImageList = "";
          this.form.outdoorImageList = "";
        }
      });
    },
    /** 加载房源VR */
    loadHouseVrs(houseId) {
      getHouseVrs(houseId).then(response => {
        if (response.data && response.data.length > 0) {
          const vrUrls = response.data.map(item => item.vrUrl).join(',');
          this.$nextTick(() => {
            this.form.vrList = vrUrls;
          });
        } else {
          this.form.vrList = "";
        }
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.houseId != null) {
            // 修改房源
            updateHouse(this.form).then(response => {
              // 保存图片
              this.saveImages(this.form.houseId);
              // 保存VR
              if (this.form.vrList) {
                this.saveVrs(this.form.houseId);
              }
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            // 新增房源
            addHouse(this.form).then(response => {
              // 保存图片 - response.data就是新插入的houseId
              const houseId = response.data;
              if (houseId) {
                this.saveImages(houseId);
              }
              // 保存VR
              if (houseId && this.form.vrList) {
                this.saveVrs(houseId);
              }
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 保存房源图片 */
    saveImages(houseId) {
      const allImages = [];

      // 定义图片类型配置
      const imageTypeConfig = [
        { field: 'mainImageList', type: '1', name: '主图' },
        { field: 'layoutImageList', type: '2', name: '户型图' },
        { field: 'bedroomImageList', type: '3', name: '卧室' },
        { field: 'bathroomImageList', type: '4', name: '卫生间' },
        { field: 'indoorImageList', type: '5', name: '室内' },
        { field: 'outdoorImageList', type: '6', name: '室外' }
      ];

      // 遍历各类型图片
      imageTypeConfig.forEach(config => {
        const imageList = this.form[config.field];
        if (imageList && imageList.trim()) {
          const urls = imageList.split(',').filter(url => url.trim());
          urls.forEach((url, index) => {
            allImages.push({
              houseId: houseId,
              imageUrl: url.trim(),
              imageType: config.type,
              // 只有主图的第一张设为封面
              isCover: (config.type === '1' && index === 0) ? '1' : '0',
              sortOrder: allImages.length
            });
          });
        }
      });

      // 如果有图片则保存
      if (allImages.length > 0) {
        saveHouseImages({
          houseId: houseId,
          images: allImages
        });
      }
    },
    /** 保存房源VR */
    saveVrs(houseId) {
      if (!this.form.vrList) {
        return;
      }
      const vrUrls = this.form.vrList.split(',').filter(url => url.trim());
      if (vrUrls.length === 0) {
        return;
      }
      const vrs = vrUrls.map((url, index) => ({
        houseId: houseId,
        vrName: `VR${index + 1}`,
        vrUrl: url.trim(),
        sortOrder: index
      }));
      saveHouseVrs({
        houseId: houseId,
        vrs: vrs
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const houseIds = row.houseId || this.ids;
      this.$modal.confirm('是否确认删除房源编号为"' + houseIds + '"的数据项？').then(function() {
        return delHouse(houseIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/house/export', {
        ...this.queryParams
      }, `house_${new Date().getTime()}.xlsx`)
    },

    /** 导入按钮操作 */
    handleImport() {
      this.upload.title = "房源数据导入";
      this.upload.open = true;
    },
    /** 下载模板操作 */
    importTemplate() {
      this.download('/system/house/importTemplate', {
      }, `house_template_${new Date().getTime()}.xlsx`)
    },
    /** 文件上传中处理 */
    handleFileUploadProgress(event, file, fileList) {
      this.upload.isUploading = true;
    },
    /** 文件上传成功处理 */
    handleFileSuccess(response, file, fileList) {
      this.upload.open = false;
      this.upload.isUploading = false;
      this.$refs.upload.clearFiles();
      this.$alert("\u003cdiv style='overflow: auto;overflow-x: hidden;max-height: 70vh;padding: 10px 20px 0;'\u003e" + response.msg + "\u003c/div\u003e", "导入结果", { dangerouslyUseHTMLString: true });
      this.getList();
    },
    /** 提交上传文件 */
    submitFileForm() {
      this.$refs.upload.submit();
    },

    /** 查看详情按钮操作 */
    handleDetail(row) {
      const houseId = row.houseId;
      getHouseDetail(houseId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },

    /** 进入VR看房 */
    handleVrView() {
      if (this.detailData.vrs && this.detailData.vrs.length > 0) {
        this.vrList = this.detailData.vrs;
        this.vrViewerVisible = true;
      } else {
        this.$message.warning('暂无VR资源');
      }
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

    /** 根据图片类型筛选图片 */
    getImagesByType(type) {
      if (!this.detailData.images || this.detailData.images.length === 0) {
        return [];
      }
      return this.detailData.images.filter(image => image.imageType === type);
    },

    /** 获取指定类型图片的预览列表 */
    getImagePreviewList(type) {
      const images = this.getImagesByType(type);
      return images.map(image => this.getImageUrl(image.imageUrl));
    }
  }
};
</script>

<style lang="scss" scoped>
.form-tip {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
  line-height: 1.5;
}

.house-detail {
  .detail-card {
    margin-bottom: 20px;

    .card-header {
      font-weight: 600;
      color: #303133;

      span {
        display: flex;
        align-items: center;

        i {
          margin-right: 8px;
          color: #409EFF;
        }
      }
    }

    .info-item {
      margin-bottom: 16px;
      display: flex;

      label {
        width: 120px;
        font-weight: 500;
        color: #606266;
        flex-shrink: 0;
      }

      span {
        color: #303133;
        word-break: break-all;
      }
    }
  }

  .image-item {
    margin-bottom: 16px;
    border: 1px solid #e4e7ed;
    border-radius: 6px;
    overflow: hidden;

    .image-error {
      display: flex;
      justify-content: center;
      align-items: center;
      width: 100%;
      height: 200px;
      background-color: #f5f7fa;
      color: #909399;
      font-size: 24px;
    }

    .image-caption {
      text-align: center;
      padding: 8px;
      background-color: #f8f9fa;
      font-size: 12px;
      color: #606266;
    }
  }
}
</style>
