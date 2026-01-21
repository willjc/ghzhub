<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="用户昵称" prop="userNickname">
        <el-input
          v-model="queryParams.userNickname"
          placeholder="请输入用户昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="项目名称" prop="projectId">
        <el-input
          v-model="queryParams.projectName"
          placeholder="请输入项目名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="承诺书类型" prop="commitmentType">
        <el-select v-model="queryParams.commitmentType" placeholder="请选择承诺书类型" clearable>
          <el-option label="人才公寓" value="1" />
          <el-option label="保租房" value="2" />
          <el-option label="市场租赁" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="有效" value="0" />
          <el-option label="失效" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item label="签署时间">
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
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gangzhu:commitment:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="commitmentList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户昵称" align="center" prop="userNickname" width="100" />
      <el-table-column label="用户账号" align="center" prop="userAccount" width="120" />
      <el-table-column label="项目名称" align="center" prop="projectName" min-width="180" show-overflow-tooltip />
      <el-table-column label="项目编码" align="center" prop="projectCode" width="120" />
      <el-table-column label="承诺书类型" align="center" prop="commitmentType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.commitmentType === '1'" type="success">人才公寓</el-tag>
          <el-tag v-else-if="scope.row.commitmentType === '2'" type="warning">保租房</el-tag>
          <el-tag v-else type="info">市场租赁</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="签署时间" align="center" prop="signTime" width="160" />
      <el-table-column label="IP地址" align="center" prop="ipAddress" width="120" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">有效</el-tag>
          <el-tag v-else type="danger" size="small">失效</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:commitment:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:commitment:remove']"
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
    <el-dialog :title="'承诺书详情'" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户昵称">{{ detail.userNickname }}</el-descriptions-item>
        <el-descriptions-item label="用户账号">{{ detail.userAccount }}</el-descriptions-item>
        <el-descriptions-item label="项目名称">{{ detail.projectName }}</el-descriptions-item>
        <el-descriptions-item label="项目编码">{{ detail.projectCode }}</el-descriptions-item>
        <el-descriptions-item label="承诺书类型">
          <el-tag v-if="detail.commitmentType === '1'" type="success">人才公寓</el-tag>
          <el-tag v-else-if="detail.commitmentType === '2'" type="warning">保租房</el-tag>
          <el-tag v-else type="info">市场租赁</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="承诺日期">{{ detail.signTime }}</el-descriptions-item>
        <el-descriptions-item label="IP地址">{{ detail.ipAddress }}</el-descriptions-item>
        <el-descriptions-item label="设备信息">{{ detail.deviceInfo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detail.status === '0'" type="success">有效</el-tag>
          <el-tag v-else type="danger">失效</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detail.remark }}</el-descriptions-item>
        <el-descriptions-item label="承诺内容" :span="2">
          <div class="commitment-content" v-html="detail.commitmentContent"></div>
        </el-descriptions-item>
        <el-descriptions-item label="签名图片" :span="2">
          <el-image
            v-if="detail.signatureImage"
            :src="getImageUrl(detail.signatureImage)"
            style="width: 300px; height: auto;"
            :preview-src-list="[getImageUrl(detail.signatureImage)]"
          />
          <span v-else>无</span>
        </el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCommitment, getCommitment, delCommitment } from "@/api/gangzhu/commitment";

export default {
  name: "Commitment",
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
      // 承诺书记录表格数据
      commitmentList: [],
      // 弹出层标题
      title: "",
      // 是否显示详情弹出层
      detailOpen: false,
      // 详情数据
      detail: {},
      // 日期范围
      dateRange: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tenantId: null,
        projectId: null,
        commitmentType: null,
        status: null,
        userNickname: null,
        projectName: null
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询承诺书记录列表 */
    getList() {
      this.loading = true;
      listCommitment(this.addDateRange(this.queryParams, this.dateRange)).then(response => {
        this.commitmentList = response.rows;
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
      this.dateRange = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.commitmentId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      const commitmentId = row.commitmentId || this.ids[0];
      getCommitment(commitmentId).then(response => {
        this.detail = response.data;
        this.detailOpen = true;
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const commitmentIds = row.commitmentId ? [row.commitmentId] : this.ids;
      this.$modal.confirm('是否确认删除选中的承诺书记录?').then(function() {
        return delCommitment(commitmentIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 获取图片完整URL - 支持base64和文件路径 */
    getImageUrl(url) {
      if (!url) return '';

      // 如果是base64数据，直接返回
      if (url.startsWith('data:image/')) {
        return url;
      }

      // 如果是外部链接，直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }

      // 如果是文件路径，拼接baseUrl
      const baseUrl = process.env.VUE_APP_BASE_API;
      if (url.indexOf(baseUrl) !== -1) {
        return url;
      }
      return baseUrl + (url.startsWith('/') ? url : '/' + url);
    }
  }
};
</script>

<style scoped>
.commitment-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #f5f7fa;
}

.commitment-content ::v-deep p {
  margin: 10px 0;
  line-height: 1.8;
}

.commitment-content ::v-deep h1,
.commitment-content ::v-deep h2,
.commitment-content ::v-deep h3 {
  margin: 15px 0 10px 0;
  color: #303133;
}

.commitment-content ::v-deep ul,
.commitment-content ::v-deep ol {
  padding-left: 20px;
  margin: 10px 0;
}
</style>
