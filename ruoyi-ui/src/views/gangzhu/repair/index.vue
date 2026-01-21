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
          <el-option label="已完成" value="1" />
          <el-option label="已取消" value="2" />
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
          v-hasPermi="['gangzhu:repair:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:repair:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="repairList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="repairId" width="70" />
      <el-table-column label="报修编号" align="center" prop="repairNo" width="130" />
      <el-table-column label="用户ID" align="center" prop="userId" width="90" />
      <el-table-column label="所在位置" align="center" prop="location" min-width="150" show-overflow-tooltip />
      <el-table-column label="房间号" align="center" prop="roomNo" width="90" />
      <el-table-column label="联系电话" align="center" prop="phone" width="120" />
      <el-table-column label="处理状态" align="center" prop="status" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="warning">待处理</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="success">已完成</el-tag>
          <el-tag v-else type="info">已取消</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="维修时间" align="center" prop="repairDate" width="110" />
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
            v-hasPermi="['gangzhu:repair:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleHandle(scope.row)"
            v-hasPermi="['gangzhu:repair:handle']"
            v-if="scope.row.status === '0'"
          >处理</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:repair:remove']"
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
    <el-dialog title="物业报修详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="报修ID">{{ detailData.repairId }}</el-descriptions-item>
        <el-descriptions-item label="报修编号">{{ detailData.repairNo }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detailData.userId }}</el-descriptions-item>
        <el-descriptions-item label="所在位置">{{ detailData.location }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ detailData.roomNo }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="问题描述">{{ detailData.description }}</el-descriptions-item>
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
        <el-descriptions-item label="期望维修时间">{{ detailData.repairDate }}</el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag v-if="detailData.status === '0'" type="warning">待处理</el-tag>
          <el-tag v-else-if="detailData.status === '1'" type="success">已完成</el-tag>
          <el-tag v-else type="info">已取消</el-tag>
        </el-descriptions-item>
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
    <el-dialog title="处理物业报修" :visible.sync="handleOpen" width="600px" append-to-body>
      <el-form ref="handleForm" :model="handleForm" label-width="100px">
        <el-form-item label="报修编号">
          <span>{{ handleForm.repairNo }}</span>
        </el-form-item>
        <el-form-item label="所在位置">
          <span>{{ handleForm.location }}</span>
        </el-form-item>
        <el-form-item label="房间号">
          <span>{{ handleForm.roomNo }}</span>
        </el-form-item>
        <el-form-item label="联系电话">
          <span>{{ handleForm.phone }}</span>
        </el-form-item>
        <el-form-item label="问题描述">
          <div style="max-height: 100px; overflow-y: auto;">{{ handleForm.description }}</div>
        </el-form-item>
        <el-form-item label="上传图片" v-if="handleForm.images">
          <div v-for="(img, index) in handleImageList" :key="index" style="display: inline-block; margin-right: 10px;">
            <el-image
              style="width: 80px; height: 80px;"
              :src="baseUrl + img"
              :preview-src-list="handleFullImageList"
              fit="cover">
            </el-image>
          </div>
        </el-form-item>
        <el-form-item label="期望维修时间">
          <span>{{ handleForm.repairDate }}</span>
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
import { listRepair, getRepair, handleRepair, delRepair, exportRepair } from "@/api/gangzhu/repair";

export default {
  name: "Repair",
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
      // 物业报修表格数据
      repairList: [],
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
        repairId: null,
        repairNo: '',
        location: '',
        roomNo: '',
        phone: '',
        description: '',
        images: '',
        repairDate: '',
        handleResult: ''
      },
      // 图片访问地址
      baseUrl: process.env.VUE_APP_BASE_API,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        status: null
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
    },
    // 处理对话框图片列表
    handleImageList() {
      if (!this.handleForm.images) return [];
      return this.handleForm.images.split(',').filter(img => img);
    },
    // 处理对话框完整图片URL列表
    handleFullImageList() {
      return this.handleImageList.map(img => this.baseUrl + img);
    }
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询物业报修列表 */
    getList() {
      this.loading = true;
      listRepair(this.queryParams).then(response => {
        this.repairList = response.rows;
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
      this.ids = selection.map(item => item.repairId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 详情按钮操作 */
    handleView(row) {
      const repairId = row.repairId;
      getRepair(repairId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    /** 处理按钮操作 */
    handleHandle(row) {
      this.handleForm = {
        repairId: row.repairId,
        repairNo: row.repairNo,
        location: row.location,
        roomNo: row.roomNo,
        phone: row.phone,
        description: row.description,
        images: row.images,
        repairDate: row.repairDate,
        handleResult: ''
      };
      this.handleOpen = true;
    },
    /** 提交处理 */
    submitHandle() {
      this.$refs["handleForm"].validate(valid => {
        if (valid) {
          handleRepair(this.handleForm).then(response => {
            this.$modal.msgSuccess("处理成功");
            this.handleOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const repairIds = row.repairId || this.ids;
      this.$modal.confirm('是否确认删除报修编号为"' + repairIds + '"的数据项？').then(function() {
        return delRepair(repairIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gangzhu/repair/export', {
        ...this.queryParams
      }, `repair_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
