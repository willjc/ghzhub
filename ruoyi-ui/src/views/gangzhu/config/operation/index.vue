<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="配置类型" prop="configType">
        <el-select v-model="queryParams.configType" placeholder="请选择配置类型" clearable>
          <el-option label="轮播图" value="banner" />
          <el-option label="广告位" value="ad" />
          <el-option label="通知公告" value="notice" />
          <el-option label="功能图标" value="icon" />
        </el-select>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" value="0" />
          <el-option label="禁用" value="1" />
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
          v-hasPermi="['gangzhu:config:add']"
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
          v-hasPermi="['gangzhu:config:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:config:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="configList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="图片预览" align="center" width="100">
        <template slot-scope="scope">
          <el-image
            v-if="scope.row.imageUrl"
            :src="getImageUrl(scope.row.imageUrl)"
            :preview-src-list="[getImageUrl(scope.row.imageUrl)]"
            fit="cover"
            style="width: 60px; height: 40px; border-radius: 4px; cursor: pointer;"
          />
          <span v-else style="color: #999;">无图片</span>
        </template>
      </el-table-column>
      <el-table-column label="标题" align="center" prop="title" min-width="150" show-overflow-tooltip />
      <el-table-column label="配置类型" align="center" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.configType === 'banner'" type="success">轮播图</el-tag>
          <el-tag v-else-if="scope.row.configType === 'ad'" type="warning">广告位</el-tag>
          <el-tag v-else-if="scope.row.configType === 'notice'" type="info">通知公告</el-tag>
          <el-tag v-else-if="scope.row.configType === 'icon'" type="primary">功能图标</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="内容" align="center" prop="content" min-width="180" show-overflow-tooltip />
      <el-table-column label="链接地址" align="center" prop="linkUrl" min-width="150" show-overflow-tooltip />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="状态" align="center" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200" fixed="right">
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
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:config:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:config:remove']"
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

    <!-- 添加或修改运营配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="配置类型" prop="configType">
          <el-select v-model="form.configType" placeholder="请选择配置类型" style="width: 100%">
            <el-option label="轮播图" value="banner">
              <span>轮播图</span>
              <span style="float: right; color: #8492a6; font-size: 12px;">建议尺寸：750x262</span>
            </el-option>
            <el-option label="广告位" value="ad" />
            <el-option label="通知公告" value="notice" />
            <el-option label="功能图标" value="icon" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="图片" prop="imageUrl" v-if="form.configType !== 'notice'">
          <image-upload v-model="form.imageUrl" :limit="1" />
          <div style="color: #999; font-size: 12px; margin-top: 5px;" v-if="form.configType === 'banner'">
            建议上传尺寸：750x262，格式：jpg/png
          </div>
        </el-form-item>
        <el-form-item label="内容描述" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="请输入内容描述" maxlength="500" show-word-limit />
        </el-form-item>
        <el-form-item label="链接类型" prop="linkType">
          <el-radio-group v-model="form.linkType">
            <el-radio label="page">页面路径</el-radio>
            <el-radio label="url">外部链接</el-radio>
            <el-radio label="none">无链接</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="链接地址" prop="linkUrl" v-if="form.linkType !== 'none'">
          <el-input v-model="form.linkUrl" placeholder="页面路径示例：/pages/talent/index 或外部链接：https://example.com" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" controls-position="right" style="width: 150px" />
          <span style="color: #999; font-size: 12px; margin-left: 10px;">数字越小越靠前</span>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">禁用</el-radio>
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

    <!-- 详情对话框 -->
    <el-dialog title="配置详情" :visible.sync="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="配置类型">
          <el-tag v-if="viewForm.configType === 'banner'" type="success">轮播图</el-tag>
          <el-tag v-else-if="viewForm.configType === 'ad'" type="warning">广告位</el-tag>
          <el-tag v-else-if="viewForm.configType === 'notice'" type="info">通知公告</el-tag>
          <el-tag v-else-if="viewForm.configType === 'icon'" type="primary">功能图标</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewForm.status === '0' ? 'success' : 'danger'">
            {{ viewForm.status === '0' ? '启用' : '禁用' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ viewForm.title }}</el-descriptions-item>
        <el-descriptions-item label="图片预览" :span="2" v-if="viewForm.imageUrl">
          <el-image
            :src="getImageUrl(viewForm.imageUrl)"
            :preview-src-list="[getImageUrl(viewForm.imageUrl)]"
            fit="contain"
            style="max-width: 100%; max-height: 300px; cursor: pointer;"
          />
        </el-descriptions-item>
        <el-descriptions-item label="内容描述" :span="2">{{ viewForm.content || '无' }}</el-descriptions-item>
        <el-descriptions-item label="链接类型">
          <span v-if="viewForm.linkType === 'page'">页面路径</span>
          <span v-else-if="viewForm.linkType === 'url'">外部链接</span>
          <span v-else>无链接</span>
        </el-descriptions-item>
        <el-descriptions-item label="链接地址">{{ viewForm.linkUrl || '无' }}</el-descriptions-item>
        <el-descriptions-item label="排序号">{{ viewForm.sortOrder }}</el-descriptions-item>
        <el-descriptions-item label="创建者">{{ viewForm.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ parseTime(viewForm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ parseTime(viewForm.updateTime) || '无' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewForm.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listConfig, getConfig, delConfig, addConfig, updateConfig } from "@/api/gangzhu/config";
import ImageUpload from '@/components/ImageUpload';

export default {
  name: "OperationConfig",
  components: {
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
      // 运营配置表格数据
      configList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情对话框
      viewOpen: false,
      // 详情表单数据
      viewForm: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        configType: null,
        title: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        configType: [
          { required: true, message: "配置类型不能为空", trigger: "change" }
        ],
        title: [
          { required: true, message: "标题不能为空", trigger: "blur" }
        ],
        linkType: [
          { required: true, message: "链接类型不能为空", trigger: "change" }
        ],
        sortOrder: [
          { required: true, message: "排序号不能为空", trigger: "blur" }
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
    /** 获取图片完整URL - 遵循RuoYi标准 */
    getImageUrl(url) {
      if (!url) return '';
      // 外部链接直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }
      // RuoYi标准：拼接baseUrl
      const baseUrl = process.env.VUE_APP_BASE_API;
      if (url.indexOf(baseUrl) !== -1) {
        return url;
      }
      return baseUrl + (url.startsWith('/') ? url : '/' + url);
    },
    /** 查询运营配置列表 */
    getList() {
      this.loading = true;
      listConfig(this.queryParams).then(response => {
        this.configList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset() {
      this.form = {
        configId: null,
        configType: null,
        title: null,
        imageUrl: null,
        content: null,
        linkUrl: null,
        linkType: "none",
        sortOrder: 0,
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
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 多选框选中数据 */
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.configId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加运营配置";
    },
    /** 查看详情 */
    handleView(row) {
      getConfig(row.configId).then(response => {
        this.viewForm = response.data;
        this.viewOpen = true;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const configId = row.configId || this.ids
      getConfig(configId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改运营配置";
      });
    },
    /** 状态修改 */
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "禁用";
      this.$modal.confirm('确认要"' + text + '""' + row.title + '"配置吗？').then(() => {
        return updateConfig(row);
      }).then(() => {
        this.$modal.msgSuccess(text + "成功");
      }).catch(() => {
        row.status = row.status === "0" ? "1" : "0";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.configId != null) {
            updateConfig(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addConfig(this.form).then(response => {
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
      const configIds = row.configId || this.ids;
      this.$modal.confirm('是否确认删除选中的运营配置数据？').then(() => {
        return delConfig(configIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gangzhu/config/export', {
        ...this.queryParams
      }, `运营配置_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
