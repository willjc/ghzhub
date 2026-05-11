<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="所属项目" prop="projectId" v-if="!embedded">
        <el-select v-model="queryParams.projectId" placeholder="请选择项目" clearable style="width: 200px">
          <el-option
            v-for="project in projectList"
            :key="project.projectId"
            :label="project.projectName"
            :value="project.projectId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="户型名称" prop="houseTypeName">
        <el-input
          v-model="queryParams.houseTypeName"
          placeholder="请输入户型名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="卧室数" prop="bedroomCount">
        <el-input-number v-model="queryParams.bedroomCount" controls-position="right" :min="0" placeholder="卧室数" />
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
          v-hasPermi="['gangzhu:houseType:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gangzhu:houseType:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="houseTypeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="所属项目" align="center" prop="projectName" width="150" show-overflow-tooltip />
      <el-table-column label="户型名称" align="center" prop="houseTypeName" width="120" show-overflow-tooltip />
      <el-table-column label="卧室" align="center" prop="bedroomCount" width="80" />
      <el-table-column label="客厅" align="center" prop="livingroomCount" width="80" />
      <el-table-column label="卫生间" align="center" prop="bathroomCount" width="80" />
      <el-table-column label="厨房" align="center" prop="kitchenCount" width="80" />
      <el-table-column label="阳台" align="center" prop="balconyCount" width="80" />
      <el-table-column label="典型面积(㎡)" align="center" prop="typicalArea" width="120" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="300">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['gangzhu:houseType:query']"
          >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:houseType:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-upload2"
            @click="handlePushToHouses(scope.row)"
            v-hasPermi="['gangzhu:houseType:edit']"
          >下发到房源</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:houseType:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      :auto-scroll="false"
      @pagination="getList"
    />

    <!-- 添加或修改户型对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="650px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-form-item label="所属项目" prop="projectId">
          <el-select v-model="form.projectId" placeholder="请选择所属项目" style="width: 100%" :disabled="embedded" filterable>
            <el-option
              v-for="project in projectList"
              :key="project.projectId"
              :label="project.projectName"
              :value="project.projectId"
            />
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="12">
            <el-form-item label="户型名称" prop="houseTypeName">
              <el-input v-model="form.houseTypeName" placeholder="请输入户型名称，如：一室一厅" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="卧室数量" prop="bedroomCount">
              <el-input-number v-model="form.bedroomCount" controls-position="right" :min="0" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="客厅数量" prop="livingroomCount">
              <el-input-number v-model="form.livingroomCount" controls-position="right" :min="0" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="卫生间数量" prop="bathroomCount">
              <el-input-number v-model="form.bathroomCount" controls-position="right" :min="0" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="厨房数量" prop="kitchenCount">
              <el-input-number v-model="form.kitchenCount" controls-position="right" :min="0" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="阳台数量" prop="balconyCount">
              <el-input-number v-model="form.balconyCount" controls-position="right" :min="0" :max="10" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="典型面积" prop="typicalArea">
              <el-input-number v-model="form.typicalArea" controls-position="right" :min="0" :precision="2" placeholder="平方米" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="显示顺序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" controls-position="right" :min="0" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <!-- 房型图片 - 分类上传（与房源页面对齐） -->
        <el-divider content-position="left">房型图片</el-divider>

        <el-form-item label="主图">
          <image-upload v-model="form.mainImageList" :limit="9" :fileSize="5" :fileType="['png', 'jpg', 'jpeg']" />
          <div class="form-tip">用于列表封面展示，同房型的房源可直接继承</div>
        </el-form-item>
        <el-form-item label="户型图">
          <image-upload v-model="form.layoutImageList" :limit="5" :fileSize="5" :fileType="['png', 'jpg', 'jpeg']" />
          <div class="form-tip">房屋平面布局图</div>
        </el-form-item>
        <el-form-item label="卧室">
          <image-upload v-model="form.bedroomImageList" :limit="9" :fileSize="5" :fileType="['png', 'jpg', 'jpeg']" />
        </el-form-item>
        <el-form-item label="卫生间">
          <image-upload v-model="form.bathroomImageList" :limit="5" :fileSize="5" :fileType="['png', 'jpg', 'jpeg']" />
        </el-form-item>
        <el-form-item label="室内">
          <image-upload v-model="form.indoorImageList" :limit="9" :fileSize="5" :fileType="['png', 'jpg', 'jpeg']" />
          <div class="form-tip">客厅、厨房等室内其他区域照片</div>
        </el-form-item>
        <el-form-item label="室外">
          <image-upload v-model="form.outdoorImageList" :limit="9" :fileSize="5" :fileType="['png', 'jpg', 'jpeg']" />
          <div class="form-tip">阳台、外景等室外照片</div>
        </el-form-item>
        <el-form-item label="VR看房">
          <image-upload v-model="form.vrUrl" :limit="1" :fileSize="50" />
          <div class="form-tip">户型级 VR，房源选中该户型时自动继承（仅支持 1 张/链接）</div>
        </el-form-item>

        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看户型对话框 -->
    <el-dialog title="查看户型" :visible.sync="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="户型名称">{{ viewData.houseTypeName }}</el-descriptions-item>
        <el-descriptions-item label="卧室数量">{{ viewData.bedroomCount }}</el-descriptions-item>
        <el-descriptions-item label="客厅数量">{{ viewData.livingroomCount }}</el-descriptions-item>
        <el-descriptions-item label="卫生间数量">{{ viewData.bathroomCount }}</el-descriptions-item>
        <el-descriptions-item label="厨房数量">{{ viewData.kitchenCount }}</el-descriptions-item>
        <el-descriptions-item label="阳台数量">{{ viewData.balconyCount }}</el-descriptions-item>
        <el-descriptions-item label="典型面积">{{ viewData.typicalArea }} ㎡</el-descriptions-item>
        <el-descriptions-item label="显示顺序">{{ viewData.sortOrder }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="viewData.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">停用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '无' }}</el-descriptions-item>
      </el-descriptions>

      <div style="margin-top: 20px;" v-if="viewImages.length > 0">
        <el-divider content-position="left">房型图片</el-divider>
        <el-tabs type="border-card">
          <el-tab-pane v-if="getViewImagesByType('1').length > 0">
            <span slot="label"><i class="el-icon-picture-outline"></i> 主图 ({{ getViewImagesByType('1').length }})</span>
            <el-image v-for="(img, i) in getViewImagesByType('1')" :key="'m'+i" :src="getImageUrl(img.imageUrl)" :preview-src-list="getViewPreviewList('1')" style="width:150px;height:150px;margin-right:10px;margin-bottom:10px;border-radius:4px;" fit="cover" />
          </el-tab-pane>
          <el-tab-pane v-if="getViewImagesByType('2').length > 0">
            <span slot="label"><i class="el-icon-tickets"></i> 户型图 ({{ getViewImagesByType('2').length }})</span>
            <el-image v-for="(img, i) in getViewImagesByType('2')" :key="'l'+i" :src="getImageUrl(img.imageUrl)" :preview-src-list="getViewPreviewList('2')" style="width:150px;height:150px;margin-right:10px;margin-bottom:10px;border-radius:4px;" fit="cover" />
          </el-tab-pane>
          <el-tab-pane v-if="getViewImagesByType('3').length > 0">
            <span slot="label"><i class="el-icon-s-home"></i> 卧室 ({{ getViewImagesByType('3').length }})</span>
            <el-image v-for="(img, i) in getViewImagesByType('3')" :key="'b'+i" :src="getImageUrl(img.imageUrl)" :preview-src-list="getViewPreviewList('3')" style="width:150px;height:150px;margin-right:10px;margin-bottom:10px;border-radius:4px;" fit="cover" />
          </el-tab-pane>
          <el-tab-pane v-if="getViewImagesByType('4').length > 0">
            <span slot="label"><i class="el-icon-toilet-paper"></i> 卫生间 ({{ getViewImagesByType('4').length }})</span>
            <el-image v-for="(img, i) in getViewImagesByType('4')" :key="'w'+i" :src="getImageUrl(img.imageUrl)" :preview-src-list="getViewPreviewList('4')" style="width:150px;height:150px;margin-right:10px;margin-bottom:10px;border-radius:4px;" fit="cover" />
          </el-tab-pane>
          <el-tab-pane v-if="getViewImagesByType('5').length > 0">
            <span slot="label"><i class="el-icon-office-building"></i> 室内 ({{ getViewImagesByType('5').length }})</span>
            <el-image v-for="(img, i) in getViewImagesByType('5')" :key="'i'+i" :src="getImageUrl(img.imageUrl)" :preview-src-list="getViewPreviewList('5')" style="width:150px;height:150px;margin-right:10px;margin-bottom:10px;border-radius:4px;" fit="cover" />
          </el-tab-pane>
          <el-tab-pane v-if="getViewImagesByType('6').length > 0">
            <span slot="label"><i class="el-icon-sunny"></i> 室外 ({{ getViewImagesByType('6').length }})</span>
            <el-image v-for="(img, i) in getViewImagesByType('6')" :key="'o'+i" :src="getImageUrl(img.imageUrl)" :preview-src-list="getViewPreviewList('6')" style="width:150px;height:150px;margin-right:10px;margin-bottom:10px;border-radius:4px;" fit="cover" />
          </el-tab-pane>
        </el-tabs>
      </div>
      <div style="margin-top: 20px;" v-else>
        <el-divider content-position="left">户型图片</el-divider>
        <el-empty description="暂无图片" :image-size="100"></el-empty>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listHouseType, getHouseType, delHouseType, addHouseType, updateHouseType } from "@/api/gangzhu/houseType";
import { listProject } from "@/api/gangzhu/project";
import ImageUpload from "@/components/ImageUpload";
import request from '@/utils/request'

export default {
  name: "HouseType",
  components: {
    ImageUpload
  },
  props: {
    // 是否为嵌入模式(在其他页面的对话框中使用)
    embedded: {
      type: Boolean,
      default: false
    },
    // 项目ID(嵌入模式下使用,用于自动筛选该项目的户型)
    projectId: {
      type: Number,
      default: null
    }
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
      // 户型表格数据
      houseTypeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示查看对话框
      viewOpen: false,
      // 查看数据
      viewData: {},
      // 查看图片列表
      viewImages: [],
      // 项目列表
      projectList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        projectId: null,
        houseTypeName: null,
        bedroomCount: null,
        livingroomCount: null,
        bathroomCount: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        projectId: [
          { required: true, message: "所属项目不能为空", trigger: "change" }
        ],
        houseTypeName: [
          { required: true, message: "户型名称不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    // 如果是嵌入模式且传入了项目ID,自动设置查询条件
    if (this.embedded && this.projectId) {
      this.queryParams.projectId = this.projectId;
    }
    // 先加载项目列表,再加载户型列表
    this.getProjectList();
    this.getList();
  },
  methods: {
    /** 获取图片完整URL - 遵循RuoYi标准 */
    getImageUrl(url) {
      if (!url) return '';

      // 外部链接(http/https开头),直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }

      // RuoYi标准:数据库存储相对路径,前端拼接baseUrl
      const baseUrl = process.env.VUE_APP_BASE_API;  // /dev-api

      // 如果已包含baseUrl,直接返回
      if (url.indexOf(baseUrl) !== -1) {
        return url;
      }

      // 拼接baseUrl + 相对路径
      // 例: /dev-api + /profile/upload/2025/01/19/xxx.jpg
      return baseUrl + (url.startsWith('/') ? url : '/' + url);
    },
    /** 查询项目列表 */
    getProjectList() {
      listProject({ pageNum: 1, pageSize: 1000, status: "0" }).then(response => {
        this.projectList = response.rows;
      });
    },
    /** 查询户型列表 */
    getList() {
      this.loading = true;
      // el-input-number 设了 :min="0" 会将 null 自动设为 0，需要清理
      const params = { ...this.queryParams };
      if (!params.bedroomCount) params.bedroomCount = null;
      if (!params.livingroomCount) params.livingroomCount = null;
      if (!params.bathroomCount) params.bathroomCount = null;
      listHouseType(params).then(response => {
        this.houseTypeList = response.rows || response.data || [];
        this.total = response.total || 0;
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
        houseTypeId: null,
        projectId: null,
        houseTypeName: null,
        houseTypeCode: null,
        bedroomCount: 0,
        livingroomCount: 0,
        bathroomCount: 0,
        kitchenCount: 0,
        balconyCount: 0,
        typicalArea: null,
        mainImageList: "",
        layoutImageList: "",
        bedroomImageList: "",
        bathroomImageList: "",
        indoorImageList: "",
        outdoorImageList: "",
        vrUrl: "",
        status: "0",
        sortOrder: 0,
        remark: null
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
      // 清空数字类型的搜索条件
      this.queryParams.bedroomCount = null;
      this.queryParams.livingroomCount = null;
      this.queryParams.bathroomCount = null;
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.houseTypeId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      // 如果是嵌入模式且传入了项目ID,自动设置项目ID
      if (this.embedded && this.projectId) {
        this.form.projectId = this.projectId;
      }
      this.open = true;
      this.title = "添加户型";
    },
    /** 查看按钮操作 */
    handleView(row) {
      const houseTypeId = row.houseTypeId;
      getHouseType(houseTypeId).then(response => {
        this.viewData = response.data;
        this.viewOpen = true;
        // 加载图片列表
        request({
          url: '/gangzhu/houseType/' + houseTypeId + '/images',
          method: 'get'
        }).then(res => {
          console.log('图片数据:', res);
          this.viewImages = res.data || [];
          console.log('viewImages:', this.viewImages);
        }).catch(error => {
          console.error('加载图片失败:', error);
        });
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const houseTypeId = row.houseTypeId || this.ids[0];
      getHouseType(houseTypeId).then(response => {
        // 使用Object.assign保持响应式
        Object.assign(this.form, response.data);
        // 不要在这里设置imageList为空,等待loadImages加载
        // 加载图片列表
        this.loadImages(houseTypeId);
        this.open = true;
        this.title = "修改户型";
      });
    },
    /** 加载户型图片（按类型分组回填到表单） */
    loadImages(houseTypeId) {
      request({
        url: '/gangzhu/houseType/' + houseTypeId + '/images',
        method: 'get'
      }).then(response => {
        if (response.data && response.data.length > 0) {
          // 按图片类型分组（与房源页面对齐：1=主图 2=户型图 3=卧室 4=卫生间 5=室内 6=室外）
          const byType = { '1': [], '2': [], '3': [], '4': [], '5': [], '6': [] };
          response.data.forEach(item => {
            const t = item.imageType || '1';
            if (byType[t]) byType[t].push(item.imageUrl);
          });
          this.$nextTick(() => {
            this.form.mainImageList = byType['1'].join(',');
            this.form.layoutImageList = byType['2'].join(',');
            this.form.bedroomImageList = byType['3'].join(',');
            this.form.bathroomImageList = byType['4'].join(',');
            this.form.indoorImageList = byType['5'].join(',');
            this.form.outdoorImageList = byType['6'].join(',');
          });
        }
      }).catch(error => {
        console.error('加载房型图片失败:', error);
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const saveHouseType = this.form.houseTypeId != null ? updateHouseType : addHouseType;
          const successMsg = this.form.houseTypeId != null ? "修改成功" : "新增成功";

          saveHouseType(this.form).then(response => {
            // 保存户型成功后,保存图片
            const houseTypeId = this.form.houseTypeId || response.data;
            if (houseTypeId) {
              this.saveImages(houseTypeId).then(() => {
                this.$modal.msgSuccess(successMsg);
                this.open = false;
                this.resetQuery();
              }).catch(() => {
                this.$modal.msgSuccess(successMsg);
                this.open = false;
                this.resetQuery();
              });
            } else {
              this.$modal.msgSuccess(successMsg);
              this.open = false;
              this.resetQuery();
            }
          }).catch(() => {});
        }
      });
    },
    /** 保存户型图片（分类型，与房源页面对齐） */
    saveImages(houseTypeId) {
      const allImages = [];
      const imageTypeConfig = [
        { field: 'mainImageList', type: '1' },
        { field: 'layoutImageList', type: '2' },
        { field: 'bedroomImageList', type: '3' },
        { field: 'bathroomImageList', type: '4' },
        { field: 'indoorImageList', type: '5' },
        { field: 'outdoorImageList', type: '6' }
      ];

      imageTypeConfig.forEach(config => {
        const imageList = this.form[config.field];
        if (imageList && imageList.trim()) {
          const urls = imageList.split(',').filter(url => url.trim());
          urls.forEach((url, index) => {
            allImages.push({
              imageUrl: url.trim(),
              imageType: config.type,
              isCover: (config.type === '1' && index === 0) ? '1' : '0',
              sortOrder: allImages.length + 1
            });
          });
        }
      });

      return request({
        url: '/gangzhu/houseType/' + houseTypeId + '/images',
        method: 'post',
        data: allImages
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const houseTypeIds = row.houseTypeId ? [row.houseTypeId] : this.ids;
      this.$modal.confirm('是否确认删除户型编号为"' + houseTypeIds + '"的数据项？').then(function() {
        return delHouseType(houseTypeIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 一键下发户型图片与 VR 到该户型所有房源（仅填空，不覆盖） */
    handlePushToHouses(row) {
      const typeName = row.houseTypeName || '';
      this.$modal.confirm('将把【' + typeName + '】户型的 6 类图片与 VR 下发到该户型下所有房源。<br/>仅对「房源该分类无图片 / 房源无 VR」的情况进行补齐，不会覆盖房源已有数据。是否继续？', '下发确认', {
        dangerouslyUseHTMLString: true,
        confirmButtonText: '确定下发',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        const loading = this.$loading({ lock: true, text: '下发中...', spinner: 'el-icon-loading' });
        return request({
          url: '/gangzhu/houseType/' + row.houseTypeId + '/pushToHouses',
          method: 'post'
        }).then(res => {
          loading.close();
          const d = res.data || {};
          this.$modal.msgSuccess('下发完成：共 ' + (d.total || 0) + ' 间房源，补图 ' + (d.imageFilled || 0) + ' 间，补 VR ' + (d.vrFilled || 0) + ' 间');
        }).catch(err => {
          loading.close();
          throw err;
        });
      }).catch(() => {});
    },
    /** 按类型筛选查看图片 */
    getViewImagesByType(type) {
      if (!this.viewImages || this.viewImages.length === 0) return [];
      return this.viewImages.filter(img => img.imageType === type);
    },
    /** 获取指定类型图片的预览列表 */
    getViewPreviewList(type) {
      return this.getViewImagesByType(type).map(img => this.getImageUrl(img.imageUrl));
    }
  }
};
</script>

<style scoped>
.form-tip {
  font-size: 12px;
  color: #999;
  line-height: 1.5;
}
</style>
