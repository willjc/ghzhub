<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板名称" prop="templateName">
        <el-input
          v-model="queryParams.templateName"
          placeholder="请输入模板名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="类型" prop="commitmentType">
        <el-select v-model="queryParams.commitmentType" placeholder="请选择承诺书类型" clearable>
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
          v-hasPermi="['system:commitment:add']"
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
          v-hasPermi="['system:commitment:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模板名称" align="center" prop="templateName" />
      <el-table-column label="模板编码" align="center" prop="templateCode" />
      <el-table-column label="承诺书类型" align="center" prop="commitmentType">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.commitmentType === '1'" type="success">人才公寓</el-tag>
          <el-tag v-else-if="scope.row.commitmentType === '2'" type="primary">保租房</el-tag>
          <el-tag v-else-if="scope.row.commitmentType === '3'" type="info">市场租赁</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="版本" align="center" prop="version" />
      <el-table-column label="是否默认" align="center" prop="isDefault">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isDefault === '1'" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['system:commitment:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:commitment:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:commitment:remove']"
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

    <!-- 添加或修改承诺书模板对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="900px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="模板名称" prop="templateName">
              <el-input v-model="form.templateName" placeholder="请输入模板名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板编码" prop="templateCode">
              <el-input v-model="form.templateCode" placeholder="请输入模板编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="承诺书类型" prop="commitmentType">
              <el-select v-model="form.commitmentType" placeholder="请选择承诺书类型" style="width: 100%">
                <el-option label="人才公寓" value="1" />
                <el-option label="保租房" value="2" />
                <el-option label="市场租赁" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本号" prop="version">
              <el-input v-model="form.version" placeholder="请输入版本号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否默认" prop="isDefault">
              <el-radio-group v-model="form.isDefault">
                <el-radio label="1">是</el-radio>
                <el-radio label="0">否</el-radio>
              </el-radio-group>
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
        <el-row>
          <el-col :span="24">
            <el-form-item label="模板内容" prop="templateContent">
              <editor v-model="form.templateContent" :min-height="300"/>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 承诺书模板详情对话框 -->
    <el-dialog title="承诺书模板详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-form ref="detailForm" :model="detailData" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="模板名称">
              <span>{{ detailData.templateName }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="模板编码">
              <span>{{ detailData.templateCode }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="承诺书类型">
              <el-tag v-if="detailData.commitmentType === '1'" type="success">人才公寓</el-tag>
              <el-tag v-else-if="detailData.commitmentType === '2'" type="primary">保租房</el-tag>
              <el-tag v-else-if="detailData.commitmentType === '3'" type="info">市场租赁</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="版本号">
              <span>{{ detailData.version }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="是否默认">
              <el-tag v-if="detailData.isDefault === '1'" type="success">是</el-tag>
              <el-tag v-else type="info">否</el-tag>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-tag v-if="detailData.status === '0'" type="success">正常</el-tag>
              <el-tag v-else type="danger">停用</el-tag>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="创建者">
              <span>{{ detailData.createBy }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间">
              <span>{{ parseTime(detailData.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="更新者">
              <span>{{ detailData.updateBy }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="更新时间">
              <span>{{ parseTime(detailData.updateTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="模板内容">
              <div class="detail-content" v-html="detailData.templateContent"></div>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="detailData.remark">
          <el-col :span="24">
            <el-form-item label="备注">
              <span>{{ detailData.remark }}</span>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCommitmentTemplate, getCommitmentTemplate, delCommitmentTemplate, addCommitmentTemplate, updateCommitmentTemplate } from "@/api/gangzhu/commitmentTemplate";
import Editor from '@/components/Editor';

export default {
  name: "CommitmentTemplate",
  components: {
    Editor
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
      // 承诺书模板表格数据
      templateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情弹出层
      detailOpen: false,
      // 详情数据
      detailData: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateName: null,
        commitmentType: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        templateName: [
          { required: true, message: "模板名称不能为空", trigger: "blur" }
        ],
        templateCode: [
          { required: true, message: "模板编码不能为空", trigger: "blur" }
        ],
        commitmentType: [
          { required: true, message: "承诺书类型不能为空", trigger: "change" }
        ],
        templateContent: [
          { required: true, message: "模板内容不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询承诺书模板列表 */
    getList() {
      this.loading = true;
      listCommitmentTemplate(this.queryParams).then(response => {
        this.templateList = response.rows;
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
        templateId: null,
        templateName: null,
        templateCode: null,
        commitmentType: null,
        templateContent: null,
        version: "1.0",
        isDefault: "0",
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.templateId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加承诺书模板";
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      const templateId = row.templateId;
      getCommitmentTemplate(templateId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const templateId = row.templateId || this.ids
      getCommitmentTemplate(templateId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改承诺书模板";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.templateId != null) {
            updateCommitmentTemplate(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addCommitmentTemplate(this.form).then(response => {
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
      const templateIds = row.templateId || this.ids;
      this.$modal.confirm('是否确认删除承诺书模板编号为"' + templateIds + '"的数据项？').then(function() {
        return delCommitmentTemplate(templateIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>

<style scoped>
.detail-content {
  max-height: 500px;
  overflow-y: auto;
  padding: 15px;
  background: #f5f5f5;
  border-radius: 4px;
  line-height: 1.8;
}

.detail-content >>> h2,
.detail-content >>> h3 {
  margin-top: 15px;
  margin-bottom: 10px;
}

.detail-content >>> ol,
.detail-content >>> ul {
  padding-left: 30px;
}

.detail-content >>> li {
  margin-bottom: 8px;
}
</style>
