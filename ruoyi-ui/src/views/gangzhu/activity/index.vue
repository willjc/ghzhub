<template>
  <div class="app-container">
    <!-- 搜索区域 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="活动标题" prop="activityTitle">
        <el-input
          v-model="queryParams.activityTitle"
          placeholder="请输入活动标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="活动类型" prop="activityType">
        <el-select v-model="queryParams.activityType" placeholder="请选择活动类型" clearable>
          <el-option
            v-for="dict in dict.type.activity_type"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="主办单位" prop="organizer">
        <el-input
          v-model="queryParams.organizer"
          placeholder="请输入主办单位"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="活动时间" prop="activityStartTime">
        <el-date-picker
          v-model="dateRange"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_normal_disable"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['gangzhu:activity:add']"
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
          v-hasPermi="['gangzhu:activity:edit']"
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
          v-hasPermi="['gangzhu:activity:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:activity:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 数据表格 -->
    <el-table v-loading="loading" :data="activityList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="活动标题" prop="activityTitle" show-overflow-tooltip min-width="200" />
      <el-table-column label="活动主图" prop="coverImage" width="120" align="center">
        <template slot-scope="scope">
          <el-image
            v-if="scope.row.coverImage"
            :src="getImageUrl(scope.row.coverImage)"
            :preview-src-list="[getImageUrl(scope.row.coverImage)]"
            fit="cover"
            style="width: 80px; height: 60px; border-radius: 4px;"
          />
          <span v-else style="color: #999;">暂无图片</span>
        </template>
      </el-table-column>
      <el-table-column label="活动地点" prop="activityLocation" width="160" show-overflow-tooltip />
      <el-table-column label="活动类型" prop="activityType" width="120">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.activity_type" :value="scope.row.activityType"/>
        </template>
      </el-table-column>
      <el-table-column label="报名人数" width="120" align="center">
        <template slot-scope="scope">
          {{ scope.row.currentParticipants || 0 }} / {{ scope.row.maxParticipants || 0 }}
        </template>
      </el-table-column>
      <el-table-column label="浏览次数" prop="viewCount" width="100" align="center" />
      <el-table-column label="状态" prop="status" width="80" align="center">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="260" fixed="right" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:activity:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-s-data"
            @click="handleViewRegistrations(scope.row)"
            v-hasPermi="['gangzhu:activity:list']"
          >活动数据</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:activity:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:activity:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="活动标题" prop="activityTitle">
              <el-input v-model="form.activityTitle" placeholder="请输入活动标题" maxlength="200" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动地点" prop="activityLocation">
              <el-input v-model="form.activityLocation" placeholder="请输入活动地点" maxlength="200" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="活动类型" prop="activityType">
              <el-select v-model="form.activityType" placeholder="请选择活动类型" style="width: 100%">
                <el-option
                  v-for="dict in dict.type.activity_type"
                  :key="dict.value"
                  :label="dict.label"
                  :value="dict.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="主办单位" prop="organizer">
              <el-input v-model="form.organizer" placeholder="请输入主办单位" maxlength="100" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="活动开始时间" prop="activityStartTime">
              <el-date-picker
                v-model="form.activityStartTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择活动开始时间"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="活动结束时间" prop="activityEndTime">
              <el-date-picker
                v-model="form.activityEndTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择活动结束时间"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="报名开始时间" prop="registrationStartTime">
              <el-date-picker
                v-model="form.registrationStartTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择报名开始时间"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="报名结束时间" prop="registrationEndTime">
              <el-date-picker
                v-model="form.registrationEndTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="选择报名结束时间"
                style="width: 100%"
              ></el-date-picker>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="最大参与人数" prop="maxParticipants">
              <el-input-number
                v-model="form.maxParticipants"
                :min="0"
                :max="9999"
                controls-position="right"
                placeholder="请输入最大参与人数"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in dict.type.sys_normal_disable"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" maxlength="50" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="20" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="活动主图" prop="coverImage">
              <image-upload v-model="form.coverImage" :limit="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="活动详情" prop="activityContent">
              <editor v-model="form.activityContent" :min-height="300"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" maxlength="500" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="活动详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="活动标题" :span="2">{{ detailData.activityTitle }}</el-descriptions-item>
        <el-descriptions-item label="活动地点" :span="2">{{ detailData.activityLocation }}</el-descriptions-item>
        <el-descriptions-item label="活动类型">
          <dict-tag :options="dict.type.activity_type" :value="detailData.activityType"/>
        </el-descriptions-item>
        <el-descriptions-item label="主办单位">{{ detailData.organizer }}</el-descriptions-item>
        <el-descriptions-item label="活动开始时间">{{ detailData.activityStartTime }}</el-descriptions-item>
        <el-descriptions-item label="活动结束时间">{{ detailData.activityEndTime }}</el-descriptions-item>
        <el-descriptions-item label="报名开始时间">{{ detailData.registrationStartTime }}</el-descriptions-item>
        <el-descriptions-item label="报名结束时间">{{ detailData.registrationEndTime }}</el-descriptions-item>
        <el-descriptions-item label="最大参与人数">{{ detailData.maxParticipants }}</el-descriptions-item>
        <el-descriptions-item label="当前报名人数">{{ detailData.currentParticipants || 0 }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detailData.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="浏览次数">{{ detailData.viewCount }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="dict.type.sys_normal_disable" :value="detailData.status"/>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ detailData.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="活动主图" :span="2" v-if="detailData.coverImage">
          <el-image
            :src="getImageUrl(detailData.coverImage)"
            :preview-src-list="[getImageUrl(detailData.coverImage)]"
            fit="contain"
            style="max-width: 100%; max-height: 400px;"
          />
        </el-descriptions-item>
        <el-descriptions-item label="活动详情" :span="2">
          <div class="activity-content" v-html="detailData.activityContent"></div>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 活动数据 - 报名人列表对话框 -->
    <el-dialog :title="'活动数据 - ' + currentActivityTitle" :visible.sync="registrationDialogVisible" width="800px" append-to-body>
      <el-table :data="registrationList" v-loading="registrationLoading" empty-text="暂无报名数据">
        <el-table-column label="序号" type="index" width="55" align="center" />
        <el-table-column label="姓名" prop="realName" width="120" />
        <el-table-column label="联系方式" prop="phone" width="150" />
        <el-table-column label="报名时间" prop="createTime" width="180" />
        <el-table-column label="状态" prop="registrationStatus" width="100" align="center">
          <template slot-scope="scope">
            <el-tag :type="scope.row.registrationStatus === '0' ? 'success' : 'info'" size="small">
              {{ scope.row.registrationStatus === '0' ? '已报名' : '已取消' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" prop="remark" show-overflow-tooltip />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="registrationDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listActivity, getActivity, addActivity, updateActivity, delActivity, getRegistrations } from "@/api/gangzhu/activity";
import Editor from '@/components/Editor';
import ImageUpload from '@/components/ImageUpload';

export default {
  name: "Activity",
  dicts: ['activity_type', 'sys_normal_disable'],
  components: {
    Editor,
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
      // 活动表格数据
      activityList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 详情数据
      detailData: {},
      // 报名人列表弹窗
      registrationDialogVisible: false,
      registrationList: [],
      registrationLoading: false,
      currentActivityTitle: '',
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        activityTitle: null,
        activityType: null,
        organizer: null,
        activityStartTime: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        activityTitle: [
          { required: true, message: "活动标题不能为空", trigger: "blur" }
        ],
        activityType: [
          { required: true, message: "活动类型不能为空", trigger: "change" }
        ],
        organizer: [
          { required: true, message: "主办单位不能为空", trigger: "blur" }
        ],
        activityStartTime: [
          { required: true, message: "活动开始时间不能为空", trigger: "change" }
        ],
        activityEndTime: [
          { required: true, message: "活动结束时间不能为空", trigger: "change" }
        ],
        registrationStartTime: [
          { required: true, message: "报名开始时间不能为空", trigger: "change" }
        ],
        registrationEndTime: [
          { required: true, message: "报名结束时间不能为空", trigger: "change" }
        ],
        activityLocation: [
          { required: true, message: "活动地点不能为空", trigger: "blur" }
        ],
        maxParticipants: [
          { required: true, message: "最大参与人数不能为空", trigger: "blur" }
        ],
        activityContent: [
          { required: true, message: "活动详情不能为空", trigger: "blur" }
        ],
        status: [
          { required: true, message: "状态不能为空", trigger: "change" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询活动列表 */
    getList() {
      this.loading = true;
      listActivity(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.activityList = response.rows;
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
        activityId: null,
        activityTitle: null,
        activityType: null,
        organizer: null,
        activityStartTime: null,
        activityEndTime: null,
        registrationStartTime: null,
        registrationEndTime: null,
        activityLocation: null,
        maxParticipants: null,
        currentParticipants: 0,
        contactPerson: null,
        contactPhone: null,
        coverImage: null,
        activityContent: null,
        viewCount: 0,
        status: "0",
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
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.activityId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加活动";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const activityId = row.activityId || this.ids[0];
      getActivity(activityId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改活动";
      });
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      const activityId = row.activityId;
      getActivity(activityId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.activityId != null) {
            updateActivity(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addActivity(this.form).then(response => {
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
      const ids = row.activityId || this.ids;
      this.$modal.confirm('是否确认删除活动编号为"' + ids + '"的数据项?').then(function() {
        return delActivity(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gangzhu/activity/export', {
        ...this.queryParams
      }, `activity_${new Date().getTime()}.xlsx`)
    },
    /** 查看活动报名数据 */
    handleViewRegistrations(row) {
      this.currentActivityTitle = row.activityTitle;
      this.registrationDialogVisible = true;
      this.registrationLoading = true;
      this.registrationList = [];
      getRegistrations(row.activityId).then(response => {
        this.registrationList = response.data || [];
        this.registrationLoading = false;
      }).catch(() => {
        this.registrationLoading = false;
      });
    },
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
      return baseUrl + (url.startsWith('/') ? url : '/' + url);
      // 返回: /dev-api + /profile/upload/xxx.jpg
    }
  }
};
</script>

<style scoped>
.activity-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.activity-content >>> img {
  max-width: 100%;
  height: auto;
}
</style>
