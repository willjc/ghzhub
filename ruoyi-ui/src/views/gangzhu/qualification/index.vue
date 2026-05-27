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
      <el-form-item label="手机号" prop="phone">
        <el-input
          v-model="queryParams.phone"
          placeholder="请输入手机号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理状态" prop="handleResult">
        <el-select v-model="queryParams.handleResult" placeholder="请选择处理状态" clearable>
          <el-option label="待处理" value="0" />
          <el-option label="已处理" value="done" />
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
      <el-table-column label="姓名" align="center" prop="realName" width="100" :show-overflow-tooltip="true">
        <template slot-scope="scope">
          <span>{{ scope.row.realName || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="用户昵称" align="center" prop="nickname" width="120" :show-overflow-tooltip="true" />
      <el-table-column label="手机号" align="center" prop="phone" width="120" />
      <el-table-column label="申述项" align="center" width="140">
        <template slot-scope="scope">
          <el-tag type="info">学历 + 社保证明</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="学历" align="center" width="160">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hz_education_type" :value="scope.row.currentEducation"/>
          <span style="margin: 0 6px; color: #999;">→</span>
          <dict-tag :options="dict.type.hz_education_type" :value="scope.row.newEducation"/>
        </template>
      </el-table-column>
      <el-table-column label="学历审核" align="center" width="110">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.educationAuditStatus === '1'" type="success" size="mini">已通过</el-tag>
          <el-tag v-else-if="scope.row.educationAuditStatus === '2'" type="danger"  size="mini">已驳回</el-tag>
          <el-tag v-else-if="scope.row.educationAuditStatus === '0'" type="warning" size="mini">待审核</el-tag>
          <span v-else style="color:#bbb;">未提交</span>
        </template>
      </el-table-column>
      <el-table-column label="社保审核" align="center" width="110">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.socialAuditStatus === '1'" type="success" size="mini">已通过</el-tag>
          <el-tag v-else-if="scope.row.socialAuditStatus === '2'" type="danger"  size="mini">已驳回</el-tag>
          <el-tag v-else-if="scope.row.socialAuditStatus === '0'" type="warning" size="mini">待审核</el-tag>
          <span v-else style="color:#bbb;">未提交</span>
        </template>
      </el-table-column>
      <el-table-column label="处理状态" align="center" prop="handleResult" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.handleResult === '0'" type="warning">待处理</el-tag>
          <el-tag v-else type="success">已处理</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="处理意见" align="center" width="180">
        <template slot-scope="scope">
          <div v-if="scope.row.educationAuditStatus" style="margin-bottom:4px;">
            <span style="color:#606266;font-size:12px;margin-right:4px;">学历：</span>
            <el-tag v-if="scope.row.educationAuditStatus === '1'" size="mini" type="success">通过</el-tag>
            <el-tag v-else-if="scope.row.educationAuditStatus === '2'" size="mini" type="danger">不通过</el-tag>
            <el-tag v-else size="mini" type="warning">待审核</el-tag>
          </div>
          <div v-if="scope.row.socialAuditStatus">
            <span style="color:#606266;font-size:12px;margin-right:4px;">社保：</span>
            <el-tag v-if="scope.row.socialAuditStatus === '1'" size="mini" type="success">通过</el-tag>
            <el-tag v-else-if="scope.row.socialAuditStatus === '2'" size="mini" type="danger">不通过</el-tag>
            <el-tag v-else size="mini" type="warning">待审核</el-tag>
          </div>
          <span v-if="!scope.row.educationAuditStatus && !scope.row.socialAuditStatus" style="color:#bbb;">-</span>
        </template>
      </el-table-column>
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
            v-if="canAudit(scope.row)"
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
      <el-form ref="form" :model="form" :rules="auditRules" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="form.realName" :disabled="true" />
        </el-form-item>
        <el-form-item label="用户昵称">
          <el-input v-model="form.nickname" :disabled="true" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" :disabled="true" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input :value="maskIdCard(form.idCard)" :disabled="true" />
        </el-form-item>
        <el-form-item label="公司名">
          <el-input v-model="form.workUnit" :disabled="true" />
        </el-form-item>
        <el-form-item label="公司电话">
          <el-input v-model="form.unitContact" :disabled="true" />
        </el-form-item>
        <el-form-item label="当前学历">
          <dict-tag :options="dict.type.hz_education_type" :value="form.currentEducation"/>
        </el-form-item>
        <el-form-item label="申述学历">
          <dict-tag :options="dict.type.hz_education_type" :value="form.newEducation"/>
        </el-form-item>
        <el-form-item label="学历附件">
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
        <el-form-item label="学历说明" v-if="form.educationDesc">
          <div style="white-space: pre-wrap; line-height: 24px; color: #333;">{{ form.educationDesc }}</div>
        </el-form-item>

        <!-- 学历审核组 -->
        <div class="audit-group" v-if="hasEducation">
          <div class="audit-group-title">学历审核
            <el-tag size="mini" v-if="form.educationAuditStatusOld === '1'" type="success" style="margin-left:8px;">历史：已通过</el-tag>
            <el-tag size="mini" v-else-if="form.educationAuditStatusOld === '2'" type="danger" style="margin-left:8px;">历史：已驳回</el-tag>
          </div>
          <el-form-item label="学历审核结果" prop="educationAuditStatus">
            <el-radio-group v-model="form.educationAuditStatus">
              <el-radio label="1">通过</el-radio>
              <el-radio label="2">不通过</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="学历审核意见" prop="educationAuditOpinion" v-if="form.educationAuditStatus === '2'">
            <div class="opinion-presets">
              <span class="preset-label">快捷填入：</span>
              <el-tag
                v-for="(text, idx) in educationOpinionPresets"
                :key="'eop'+idx"
                size="small"
                effect="plain"
                class="preset-tag"
                @click="applyEduPreset(text)"
              >{{ text }}</el-tag>
            </div>
            <el-input v-model="form.educationAuditOpinion" type="textarea" placeholder="请输入驳回理由（可点击上方快捷标签自动填入，也可手动编辑）" :rows="3" />
          </el-form-item>
        </div>

        <el-form-item label="社保证明">
          <div class="image-list">
            <el-image
              v-for="(img, index) in socialImageList"
              :key="'s'+index"
              :src="getImageUrl(img)"
              :preview-src-list="socialImagePreviewList"
              fit="cover"
              class="preview-image"
            />
          </div>
          <div v-if="socialImageList.length === 0">无附件</div>
        </el-form-item>
        <el-form-item label="社保说明" v-if="form.socialDesc">
          <div style="white-space: pre-wrap; line-height: 24px; color: #333;">{{ form.socialDesc }}</div>
        </el-form-item>

        <!-- 社保审核组 -->
        <div class="audit-group" v-if="hasSocial">
          <div class="audit-group-title">社保审核
            <el-tag size="mini" v-if="form.socialAuditStatusOld === '1'" type="success" style="margin-left:8px;">历史：已通过</el-tag>
            <el-tag size="mini" v-else-if="form.socialAuditStatusOld === '2'" type="danger" style="margin-left:8px;">历史：已驳回</el-tag>
          </div>
          <el-form-item label="社保审核结果" prop="socialAuditStatus">
            <el-radio-group v-model="form.socialAuditStatus">
              <el-radio label="1">通过</el-radio>
              <el-radio label="2">不通过</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="社保审核意见" prop="socialAuditOpinion" v-if="form.socialAuditStatus === '2'">
            <div class="opinion-presets">
              <span class="preset-label">快捷填入：</span>
              <el-tag
                v-for="(text, idx) in socialOpinionPresets"
                :key="'sop'+idx"
                size="small"
                effect="plain"
                class="preset-tag"
                @click="applySocPreset(text)"
              >{{ text }}</el-tag>
            </div>
            <el-input v-model="form.socialAuditOpinion" type="textarea" placeholder="请输入驳回理由（可点击上方快捷标签自动填入，也可手动编辑）" :rows="3" />
          </el-form-item>
        </div>

        <div v-if="!hasEducation && !hasSocial" style="color:#999; padding:8px 16px;">该申诉未提交任何附件，无可审核项</div>
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
        <el-descriptions-item label="姓名">{{ detailData.realName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="用户昵称">{{ detailData.nickname }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="身份证号">{{ maskIdCard(detailData.idCard) }}</el-descriptions-item>
        <el-descriptions-item label="公司名" :span="2">{{ detailData.workUnit || '-' }}</el-descriptions-item>
        <el-descriptions-item label="公司电话" :span="2">{{ detailData.unitContact || '-' }}</el-descriptions-item>
        <el-descriptions-item label="当前学历">
          <dict-tag :options="dict.type.hz_education_type" :value="detailData.currentEducation"/>
        </el-descriptions-item>
        <el-descriptions-item label="申述学历">
          <dict-tag :options="dict.type.hz_education_type" :value="detailData.newEducation"/>
        </el-descriptions-item>
        <el-descriptions-item label="申诉时间" :span="2">{{ detailData.appealTime }}</el-descriptions-item>
        <el-descriptions-item label="学历附件" :span="2">
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
        <el-descriptions-item label="学历说明" :span="2" v-if="detailData.educationDesc">
          <div style="white-space: pre-wrap; line-height: 22px;">{{ detailData.educationDesc }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="社保证明" :span="2">
          <div class="image-list" v-if="detailData.socialAttachments">
            <el-image
              v-for="(img, index) in detailData.socialAttachments.split(',')"
              :key="'s'+index"
              :src="getImageUrl(img)"
              :preview-src-list="detailData.socialAttachments.split(',').map(i => getImageUrl(i))"
              style="width: 100px; height: 100px; margin-right: 10px;"
            ></el-image>
          </div>
          <span v-else>无</span>
        </el-descriptions-item>
        <el-descriptions-item label="社保说明" :span="2" v-if="detailData.socialDesc">
          <div style="white-space: pre-wrap; line-height: 22px;">{{ detailData.socialDesc }}</div>
        </el-descriptions-item>
        <el-descriptions-item label="处理状态">
          <el-tag v-if="detailData.handleResult === '0'" type="warning">待处理</el-tag>
          <el-tag v-else type="success">已处理</el-tag>
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
// build-version: 20260515-split-audit-v2 强制重新构建以让 webpack chunk hash 变化，绕过浏览器缓存
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
      socialImageList: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        nickname: null,
        phone: null,
        handleResult: null
      },
      form: {},
      rules: {},
      // 学历审核意见预填项
      educationOpinionPresets: [
        '国内学历申述未通过',
        '国外学历申述未通过',
        '博士申述未通过',
        '高层次人才、紧缺人才申述未通过'
      ],
      // 社保审核意见预填项
      socialOpinionPresets: [
        '您的社保审核不通过，请上传个人社保参保明细，或提供您为在港就业创业相关证明资料'
      ]
    };
  },
  computed: {
    imagePreviewList() {
      return this.imageList.map(img => this.getImageUrl(img));
    },
    socialImagePreviewList() {
      return this.socialImageList.map(img => this.getImageUrl(img));
    },
    previewImageList() {
      return this.previewImages.map(img => this.getImageUrl(img));
    },
    // 是否有学历附件
    hasEducation() {
      return this.imageList && this.imageList.length > 0;
    },
    // 是否有社保附件
    hasSocial() {
      return this.socialImageList && this.socialImageList.length > 0;
    },
    // 动态校验规则：仅对实际渲染的审核组生效
    auditRules() {
      const r = {};
      if (this.hasEducation) {
        r.educationAuditStatus = [
          { required: true, message: "学历审核结果不能为空", trigger: "change" }
        ];
        if (this.form.educationAuditStatus === '2') {
          r.educationAuditOpinion = [
            { required: true, message: "驳回理由不能为空", trigger: "blur" }
          ];
        }
      }
      if (this.hasSocial) {
        r.socialAuditStatus = [
          { required: true, message: "社保审核结果不能为空", trigger: "change" }
        ];
        if (this.form.socialAuditStatus === '2') {
          r.socialAuditOpinion = [
            { required: true, message: "驳回理由不能为空", trigger: "blur" }
          ];
        }
      }
      return r;
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
        realName: null,
        nickname: null,
        phone: null,
        idCard: null,
        workUnit: null,
        unitContact: null,
        currentEducation: null,
        newEducation: null,
        educationDesc: null,
        socialDesc: null,
        // 双独立审核字段
        educationAuditStatus: null,
        educationAuditOpinion: null,
        socialAuditStatus: null,
        socialAuditOpinion: null,
        // 历史审核状态（仅展示，不参与提交）
        educationAuditStatusOld: null,
        socialAuditStatusOld: null
      };
      this.imageList = [];
      this.socialImageList = [];
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
    // 是否可审核：有附件且对应一侧未终审通过/驳回，或处于待审核状态
    canAudit(row) {
      if (!row) return false;
      const eduPending = row.appealAttachments && row.appealAttachments.length > 0
        && (row.educationAuditStatus === '0' || row.educationAuditStatus === null || row.educationAuditStatus === undefined || row.educationAuditStatus === '');
      const socPending = row.socialAttachments && row.socialAttachments.length > 0
        && (row.socialAuditStatus === '0' || row.socialAuditStatus === null || row.socialAuditStatus === undefined || row.socialAuditStatus === '');
      return eduPending || socPending;
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
        const d = response.data;
        // 处理附件图片（先设，driver hasEducation/hasSocial）
        this.imageList = d.appealAttachments ? d.appealAttachments.split(',').filter(img => img) : [];
        this.socialImageList = d.socialAttachments ? d.socialAttachments.split(',').filter(img => img) : [];

        this.form = {
          appealId: d.appealId,
          realName: row.realName,
          nickname: row.nickname,
          phone: row.phone,
          idCard: row.idCard,
          workUnit: row.workUnit,
          unitContact: row.unitContact,
          currentEducation: row.currentEducation,
          newEducation: row.newEducation,
          educationDesc: d.educationDesc,
          socialDesc: d.socialDesc,
          // 历史已审核的不再让管理员重审 → 老状态展示，当前编辑值置 null
          educationAuditStatusOld: d.educationAuditStatus || null,
          socialAuditStatusOld: d.socialAuditStatus || null,
          educationAuditStatus: null,
          educationAuditOpinion: null,
          socialAuditStatus: null,
          socialAuditOpinion: null
        };

        this.open = true;
        this.title = "审核资格申述";
      });
    },
    submitAudit() {
      // 至少审核一项
      const hasEduAudit = !!this.form.educationAuditStatus;
      const hasSocAudit = !!this.form.socialAuditStatus;
      if (!hasEduAudit && !hasSocAudit) {
        this.$modal.msgWarning("学历和社保至少要审核一项");
        return;
      }
      this.$refs["form"].validate(valid => {
        if (valid) {
          const data = {
            appealId: this.form.appealId,
            educationAuditStatus: this.form.educationAuditStatus,
            educationAuditOpinion: this.form.educationAuditOpinion,
            socialAuditStatus: this.form.socialAuditStatus,
            socialAuditOpinion: this.form.socialAuditOpinion,
            // 透传新学历给后端，后端在学历通过时回写到 hz_user.education
            appealReason: this.form.newEducation
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
    // 学历审核意见预填（覆盖式）
    applyEduPreset(text) {
      this.$set(this.form, 'educationAuditOpinion', text);
    },
    // 社保审核意见预填（覆盖式）
    applySocPreset(text) {
      this.$set(this.form, 'socialAuditOpinion', text);
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
    },
    // 身份证号脱敏：前 3 后 4，中间 ****
    maskIdCard(idCard) {
      if (!idCard) return '-';
      const str = String(idCard);
      if (str.length < 8) return str;
      return str.substring(0, 3) + '**********' + str.substring(str.length - 4);
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

.audit-group {
  margin: 12px 0 8px;
  padding: 12px 16px 4px;
  background: #fafafa;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.audit-group-title {
  font-weight: 600;
  color: #303133;
  margin: 0 0 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #dcdfe6;
  display: flex;
  align-items: center;
}

/* 审核意见快捷预填 */
.opinion-presets {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  line-height: 1.6;
}

.opinion-presets .preset-label {
  font-size: 12px;
  color: #909399;
  margin-right: 4px;
}

.opinion-presets .preset-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.opinion-presets .preset-tag:hover {
  background-color: #ecf5ff;
  color: #409eff;
  border-color: #b3d8ff;
}
</style>
