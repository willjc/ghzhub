<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="用户ID" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入用户ID"
          clearable
          style="width: 200px"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 120px">
          <el-option label="待处理" value="0" />
          <el-option label="已处理" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="催办状态" prop="isUrged">
        <el-select v-model="queryParams.isUrged" placeholder="请选择" clearable style="width: 120px">
          <el-option label="未催办" value="0" />
          <el-option label="已催办" value="1" />
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
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gangzhu:complaint:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:complaint:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="complaintList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="complaintId" width="70" />
      <el-table-column label="用户ID" align="center" prop="userId" width="90" />
      <el-table-column label="投诉标题" align="center" prop="title" min-width="150" show-overflow-tooltip />
      <el-table-column label="联系方式" align="center" prop="contactPhone" width="120" />
      <el-table-column label="处理状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="warning">待处理</el-tag>
          <el-tag v-else type="success">已处理</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="催办状态" align="center" prop="isUrged" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isUrged === '1'" type="danger">已催办</el-tag>
          <el-tag v-else type="info">未催办</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="催办次数" align="center" prop="urgeCount" width="80" />
      <el-table-column label="提交时间" align="center" prop="createTime" width="155">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
            v-hasPermi="['gangzhu:complaint:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleHandle(scope.row)"
            v-hasPermi="['gangzhu:complaint:handle']"
            v-if="scope.row.status === '0'"
          >处理</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:complaint:remove']"
            style="color: #F56C6C"
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

    <!-- 详情对话框 -->
    <el-dialog title="投诉建议详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="投诉ID">{{ detailData.complaintId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detailData.userId }}</el-descriptions-item>
        <el-descriptions-item label="投诉标题">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="投诉内容">{{ detailData.content }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ detailData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="上传图片" v-if="detailData.images">
          <div v-for="(img, index) in imageList" :key="index" style="display: inline-block; margin-right: 10px;">
            <el-image
              style="width: 100px; height: 100px;"
              :src="baseUrl + img"
              :preview-src-list="fullImageList"
              fit="cover">
            </el-image>
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag v-if="detailData.status === '0'" type="warning">待处理</el-tag>
          <el-tag v-else type="success">已处理</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="催办状态">
          <el-tag v-if="detailData.isUrged === '1'" type="danger">已催办 ({{ detailData.urgeCount || 0 }}次)</el-tag>
          <el-tag v-else type="info">未催办</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="催办时间" v-if="detailData.urgeTime">{{ parseTime(detailData.urgeTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" v-if="detailData.handleResult">{{ detailData.handleResult }}</el-descriptions-item>
        <el-descriptions-item label="处理人" v-if="detailData.handleBy">{{ detailData.handleBy }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" v-if="detailData.handleTime">{{ parseTime(detailData.handleTime) }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 处理对话框 -->
    <el-dialog title="处理投诉建议" :visible.sync="handleOpen" width="600px" append-to-body>
      <el-form ref="handleForm" :model="handleForm" label-width="100px">
        <el-form-item label="投诉标题">
          <span>{{ handleForm.title }}</span>
        </el-form-item>
        <el-form-item label="投诉内容">
          <div style="max-height: 100px; overflow-y: auto;">{{ handleForm.content }}</div>
        </el-form-item>
        <el-form-item label="联系方式">
          <span>{{ handleForm.contactPhone }}</span>
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult" :rules="[{ required: true, message: '请填写处理结果', trigger: 'blur' }]">
          <el-input
            v-model="handleForm.handleResult"
            type="textarea"
            :rows="5"
            placeholder="请输入处理结果"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitHandle">确 定</el-button>
        <el-button @click="handleOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listComplaint, getComplaint, handleComplaint, delComplaint, exportComplaint } from "@/api/gangzhu/complaint";

export default {
  name: "Complaint",
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
      // 投诉建议表格数据
      complaintList: [],
      // 弹出层标题
      title: "",
      // 是否显示详情弹出层
      detailOpen: false,
      // 详情数据
      detailData: {},
      // 是否显示处理弹出层
      handleOpen: false,
      // 处理表单
      handleForm: {
        complaintId: null,
        title: '',
        content: '',
        contactPhone: '',
        handleResult: ''
      },
      // 图片访问地址
      baseUrl: process.env.VUE_APP_BASE_API,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        status: null,
        isUrged: null
      }
    };
  },
  computed: {
    // 图片列表
    imageList() {
      if (!this.detailData.images) return [];
      return this.detailData.images.split(',').filter(img => img);
    },
    // 完整图片URL列表（用于预览）
    fullImageList() {
      return this.imageList.map(img => this.baseUrl + img);
    }
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询投诉建议列表 */
    getList() {
      this.loading = true;
      listComplaint(this.queryParams).then(response => {
        this.complaintList = response.rows;
        this.total = response.total;
        this.loading = false;
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.complaintId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 详情按钮操作 */
    handleView(row) {
      const complaintId = row.complaintId;
      getComplaint(complaintId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    /** 处理按钮操作 */
    handleHandle(row) {
      this.handleForm = {
        complaintId: row.complaintId,
        title: row.title,
        content: row.content,
        contactPhone: row.contactPhone,
        handleResult: ''
      };
      this.handleOpen = true;
    },
    /** 提交处理 */
    submitHandle() {
      this.$refs["handleForm"].validate(valid => {
        if (valid) {
          handleComplaint(this.handleForm).then(response => {
            this.$modal.msgSuccess("处理成功");
            this.handleOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const complaintIds = row.complaintId || this.ids;
      this.$modal.confirm('是否确认删除投诉建议编号为"' + complaintIds + '"的数据项？').then(function() {
        return delComplaint(complaintIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gangzhu/complaint/export', {
        ...this.queryParams
      }, `complaint_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
