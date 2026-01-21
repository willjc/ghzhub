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
      <el-form-item label="消息类型" prop="messageType">
        <el-select v-model="queryParams.messageType" placeholder="请选择消息类型" clearable style="width: 150px">
          <el-option label="登录提醒" value="login" />
          <el-option label="系统通知" value="system" />
          <el-option label="账单提醒" value="bill" />
          <el-option label="合同提醒" value="contract" />
        </el-select>
      </el-form-item>
      <el-form-item label="是否已读" prop="isRead">
        <el-select v-model="queryParams.isRead" placeholder="请选择状态" clearable style="width: 120px">
          <el-option label="未读" value="0" />
          <el-option label="已读" value="1" />
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
          v-hasPermi="['gangzhu:userMessage:add']"
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
          v-hasPermi="['gangzhu:userMessage:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:userMessage:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="messageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="消息ID" align="center" prop="messageId" width="80" />
      <el-table-column label="用户ID" align="center" prop="userId" width="100" />
      <el-table-column label="消息类型" align="center" prop="messageType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.messageType === 'login'" type="warning">登录提醒</el-tag>
          <el-tag v-else-if="scope.row.messageType === 'system'" type="primary">系统通知</el-tag>
          <el-tag v-else-if="scope.row.messageType === 'bill'" type="success">账单提醒</el-tag>
          <el-tag v-else-if="scope.row.messageType === 'contract'" type="info">合同提醒</el-tag>
          <el-tag v-else type="info">{{ scope.row.messageType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="消息标题" align="center" prop="messageTitle" min-width="150" show-overflow-tooltip />
      <el-table-column label="消息内容" align="center" prop="messageContent" min-width="200" show-overflow-tooltip />
      <el-table-column label="是否已读" align="center" prop="isRead" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isRead === '0'" type="danger">未读</el-tag>
          <el-tag v-else type="success">已读</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="发送时间" align="center" prop="createTime" width="160">
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
            v-hasPermi="['gangzhu:userMessage:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:userMessage:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:userMessage:remove']"
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

    <!-- 添加或修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="消息类型" prop="messageType">
          <el-select v-model="form.messageType" placeholder="请选择消息类型" style="width: 100%">
            <el-option label="登录提醒" value="login" />
            <el-option label="系统通知" value="system" />
            <el-option label="账单提醒" value="bill" />
            <el-option label="合同提醒" value="contract" />
          </el-select>
        </el-form-item>
        <el-form-item label="消息标题" prop="messageTitle">
          <el-input v-model="form.messageTitle" placeholder="请输入消息标题" />
        </el-form-item>
        <el-form-item label="消息内容" prop="messageContent">
          <el-input v-model="form.messageContent" type="textarea" :rows="4" placeholder="请输入消息内容" />
        </el-form-item>
        <el-form-item label="是否已读" prop="isRead">
          <el-radio-group v-model="form.isRead">
            <el-radio label="0">未读</el-radio>
            <el-radio label="1">已读</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="消息详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="消息ID">{{ detailData.messageId }}</el-descriptions-item>
        <el-descriptions-item label="用户ID">{{ detailData.userId }}</el-descriptions-item>
        <el-descriptions-item label="消息类型">
          <el-tag v-if="detailData.messageType === 'login'" type="warning">登录提醒</el-tag>
          <el-tag v-else-if="detailData.messageType === 'system'" type="primary">系统通知</el-tag>
          <el-tag v-else-if="detailData.messageType === 'bill'" type="success">账单提醒</el-tag>
          <el-tag v-else-if="detailData.messageType === 'contract'" type="info">合同提醒</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="消息标题">{{ detailData.messageTitle }}</el-descriptions-item>
        <el-descriptions-item label="消息内容">{{ detailData.messageContent }}</el-descriptions-item>
        <el-descriptions-item label="是否已读">
          <el-tag v-if="detailData.isRead === '0'" type="danger">未读</el-tag>
          <el-tag v-else type="success">已读</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detailData.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="阅读时间" v-if="detailData.readTime">{{ parseTime(detailData.readTime) }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUserMessage, getUserMessage, delUserMessage, addUserMessage, updateUserMessage, exportUserMessage } from "@/api/gangzhu/userMessage";

export default {
  name: "UserMessage",
  data() {
    return {
      // 遮罩��
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
      // 用户消息表格数据
      messageList: [],
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
        userId: null,
        messageType: null,
        isRead: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        messageType: [
          { required: true, message: "消息类型不能为空", trigger: "change" }
        ],
        messageTitle: [
          { required: true, message: "消息标题不能为空", trigger: "blur" }
        ],
        messageContent: [
          { required: true, message: "消息内容不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询用户消息列表 */
    getList() {
      this.loading = true;
      listUserMessage(this.queryParams).then(response => {
        this.messageList = response.rows;
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
        messageId: null,
        userId: null,
        messageType: "login",
        messageTitle: null,
        messageContent: null,
        isRead: "0"
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
      this.ids = selection.map(item => item.messageId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加用户消息";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const messageId = row.messageId || this.ids;
      getUserMessage(messageId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改用户消息";
      });
    },
    /** 详情按钮操作 */
    handleView(row) {
      const messageId = row.messageId;
      getUserMessage(messageId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.messageId != null) {
            updateUserMessage(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addUserMessage(this.form).then(response => {
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
      const messageIds = row.messageId || this.ids;
      this.$modal.confirm('是否确认删除用户消息编号为"' + messageIds + '"的数据项？').then(function() {
        return delUserMessage(messageIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('gangzhu/userMessage/export', {
        ...this.queryParams
      }, `userMessage_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
