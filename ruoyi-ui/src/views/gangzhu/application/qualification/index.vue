<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="用户昵称" prop="nickname">
        <el-input
          v-model="queryParams.nickname"
          placeholder="请输入用户昵称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理状态" prop="handleResult">
        <el-select v-model="queryParams.handleResult" placeholder="请选择处理状态" clearable>
          <el-option label="待处理" value="0" />
          <el-option label="已通过" value="1" />
          <el-option label="已拒绝" value="2" />
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
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['gangzhu:qualification:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="appealList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="用户昵称" align="center" prop="nickname" width="120" />
      <el-table-column label="手机号" align="center" prop="phone" width="120" />
      <el-table-column label="申述项" align="center" width="100">
        <template slot-scope="scope">
          <el-tag type="info">学历</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="当前学历" align="center" prop="currentEducation" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hz_education_type" :value="scope.row.currentEducation"/>
        </template>
      </el-table-column>
      <el-table-column label="申述学历" align="center" prop="newEducation" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hz_education_type" :value="scope.row.newEducation"/>
        </template>
      </el-table-column>
      <el-table-column label="附件" align="center" width="100">
        <template slot-scope="scope">
          <el-button
            v-if="scope.row.appealAttachments"
            size="mini"
            type="text"
            @click="viewImages(scope.row.appealAttachments)"
          >查看附件</el-button>
          <span v-else>无</span>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="handleResult" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.handleResult === '0'" type="warning">待处理</el-tag>
          <el-tag v-else-if="scope.row.handleResult === '1'" type="success">已通过</el-tag>
          <el-tag v-else-if="scope.row.handleResult === '2'" type="danger">已拒绝</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理意见" align="center" prop="handleOpinion" show-overflow-tooltip />
      <el-table-column label="申诉时间" align="center" prop="appealTime" width="160" />
      <el-table-column label="处理时间" align="center" prop="handleTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:qualification:query']"
          >详情</el-button>
          <el-button
            v-if="scope.row.handleResult === '0'"
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleAudit(scope.row)"
            v-hasPermi="['gangzhu:qualification:edit']"
          >审核</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:qualification:remove']"
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

    <!-- 审核对话框 -->
    <el-dialog :title="'审核资格申述'" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户昵称">
          <el-input v-model="form.nickname" :disabled="true" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" :disabled="true" />
        </el-form-item>
        <el-form-item label="当前学历">
          <dict-tag :options="dict.type.hz_education_type" :value="form.currentEducation"/>
        </el-form-item>
        <el-form-item label="申述学历">
          <dict-tag :options="dict.type.hz_education_type" :value="form.newEducation"/>
        </el-form-item>
        <el-form-item label="附件">
          <div class="image-list">
            <el-image
              v-for="(img, index) in imageList"
              :key="index"
              :src="getImageUrl(img)"
              :preview-src-list="imagePreviewList"
              fit="cover"
              class="preview-image"
            />
          </div>
          <div v-if="imageList.length === 0">无附件</div>
        </el-form-item>
        <el-form-item label="审核结果" prop="handleResult">
          <el-radio-group v-model="form.handleResult">
            <el-radio label="1">通过</el-radio>
            <el-radio label="2">不通过</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="审核意见" prop="handleOpinion">
          <el-input v-model="form.handleOpinion" type="textarea" placeholder="请输入审核意见" :rows="4" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAudit">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 图片预览对话框 -->
    <el-dialog title="附件预览" :visible.sync="imageDialogVisible" width="800px" append-to-body>
      <div class="image-preview-container">
        <el-image
          v-for="(img, index) in previewImages"
          :key="index"
          :src="getImageUrl(img)"
          :preview-src-list="previewImageList"
          fit="contain"
          class="large-preview-image"
        />
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="申诉详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="用户昵称">{{ detailData.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="当前学历">
          <dict-tag :options="dict.type.hz_education_type" :value="detailData.currentEducation"/>
        </el-descriptions-item>
        <el-descriptions-item label="申述学历">
          <dict-tag :options="dict.type.hz_education_type" :value="detailData.newEducation"/>
        </el-descriptions-item>
        <el-descriptions-item label="申诉时间" :span="2">{{ detailData.appealTime }}</el-descriptions-item>
        <el-descriptions-item label="附件" :span="2">
          <div class="image-list" v-if="detailData.appealAttachments">
            <el-image
              v-for="(img, index) in detailData.appealAttachments.split(',')"
              :key="index"
              :src="getImageUrl(img)"
              :preview-src-list="detailData.appealAttachments.split(',').map(i => getImageUrl(i))"
              style="width: 100px; height: 100px; margin-right: 10px;"
            ></el-image>
          </div>
          <span v-else>无</span>
        </el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag v-if="detailData.handleResult === '0'" type="warning">待处理</el-tag>
          <el-tag v-else-if="detailData.handleResult === '1'" type="success">已通过</el-tag>
          <el-tag v-else-if="detailData.handleResult === '2'" type="danger">已拒绝</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailData.handlerName || '未处理' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间" :span="2">{{ detailData.handleTime || '未处理' }}</el-descriptions-item>
        <el-descriptions-item label="处理意见" :span="2">{{ detailData.handleOpinion || '无' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAppeal, getAppeal, handleAppeal, delAppeal } from "@/api/gangzhu/appeal";

export default {
  name: "QualificationAppeal",
  dicts: ['hz_education_type'],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      appealList: [],
      title: "",
      open: false,
      detailOpen: false,
      detailData: {},
      imageDialogVisible: false,
      previewImages: [],
      imageList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nickname: null,
        handleResult: null
      },
      form: {},
      rules: {
        handleResult: [
          { required: true, message: "审核结果不能为空", trigger: "change" }
        ],
        handleOpinion: [
          { required: true, message: "审核意见不能为空", trigger: "blur" }
        ]
      }
    };
  },
  computed: {
    imagePreviewList() {
      return this.imageList.map(img => this.getImageUrl(img));
    },
    previewImageList() {
      return this.previewImages.map(img => this.getImageUrl(img));
    }
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listAppeal(this.queryParams).then(response => {
        this.appealList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        appealId: null,
        nickname: null,
        phone: null,
        currentEducation: null,
        newEducation: null,
        handleResult: null,
        handleOpinion: null
      };
      this.imageList = [];
      this.resetForm("form");
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.appealId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    // 查看详情
    handleDetail(row) {
      this.detailOpen = true;
      getAppeal(row.appealId).then(response => {
        this.detailData = response.data;
      });
    },
    handleAudit(row) {
      this.reset();
      getAppeal(row.appealId).then(response => {
        this.form = {
          appealId: response.data.appealId,
          nickname: row.nickname,
          phone: row.phone,
          currentEducation: row.currentEducation,
          newEducation: row.newEducation,
          handleResult: null,
          handleOpinion: null
        };

        // 处理附件图片
        if (response.data.appealAttachments) {
          this.imageList = response.data.appealAttachments.split(',').filter(img => img);
        } else {
          this.imageList = [];
        }

        this.open = true;
        this.title = "审核资格申述";
      });
    },
    submitAudit() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          const data = {
            appealId: this.form.appealId,
            handleResult: this.form.handleResult,
            handleOpinion: this.form.handleOpinion,
            appealReason: this.form.newEducation  // 传递新学历给后端
          };

          handleAppeal(data).then(response => {
            this.$modal.msgSuccess("审核成功");
            this.open = false;
            this.getList();
          });
        }
      });
    },
    handleDelete(row) {
      const appealIds = row.appealId || this.ids;
      this.$modal.confirm('是否确认删除该资格申述记录?').then(function() {
        return delAppeal(appealIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    viewImages(attachments) {
      if (attachments) {
        this.previewImages = attachments.split(',').filter(img => img);
        this.imageDialogVisible = true;
      }
    },
    getImageUrl(url) {
      if (!url) return '';

      // 外部链接,直接返回
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
  }
};
</script>

<style scoped>
.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.preview-image {
  width: 100px;
  height: 100px;
  border-radius: 4px;
  cursor: pointer;
}

.image-preview-container {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  justify-content: center;
}

.large-preview-image {
  max-width: 100%;
  max-height: 500px;
  border-radius: 4px;
}
</style>
