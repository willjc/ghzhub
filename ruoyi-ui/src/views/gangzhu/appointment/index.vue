<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="预约编号" prop="appointmentNo">
        <el-input
          v-model="queryParams.appointmentNo"
          placeholder="请输入预约编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="预约状态" prop="appointmentStatus">
        <el-select v-model="queryParams.appointmentStatus" placeholder="请选择预约状态" clearable>
          <el-option label="待确认" value="0" />
          <el-option label="已确认" value="1" />
          <el-option label="用户已确认看房" value="2" />
          <el-option label="已完成" value="3" />
          <el-option label="已取消" value="4" />
          <el-option label="已过期" value="5" />
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
          v-hasPermi="['gangzhu:appointment:add']"
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
          v-hasPermi="['gangzhu:appointment:edit']"
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
          v-hasPermi="['gangzhu:appointment:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:appointment:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="appointmentList" @selection-change="handleSelectionChange" style="width: 100%">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="预约编号" align="center" prop="appointmentNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="联系人" align="center" prop="visitorName" width="120" />
      <el-table-column label="用户昵称" align="center" prop="userName" width="130" />
      <el-table-column label="联系电话" align="center" prop="visitorPhone" width="130" />
      <el-table-column label="项目名称" align="center" prop="projectName" min-width="150" show-overflow-tooltip />
      <el-table-column label="房源名称" align="center" prop="houseName" min-width="150" show-overflow-tooltip />
      <el-table-column label="预约日期" align="center" prop="appointmentDate" width="120" />
      <el-table-column label="预约时间" align="center" prop="appointmentTime" width="100" />
      <el-table-column label="预约人数" align="center" prop="visitorCount" width="100" />
      <el-table-column label="预约状态" align="center" prop="appointmentStatus" width="120">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.appointmentStatus === '0'" type="warning">待确认</el-tag>
          <el-tag v-else-if="scope.row.appointmentStatus === '1'" type="success">已确认</el-tag>
          <el-tag v-else-if="scope.row.appointmentStatus === '2'" type="primary">用户已确认看房</el-tag>
          <el-tag v-else-if="scope.row.appointmentStatus === '3'" type="info">已完成</el-tag>
          <el-tag v-else-if="scope.row.appointmentStatus === '4'" type="danger">已取消</el-tag>
          <el-tag v-else type="info">已过期</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="340">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:appointment:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:appointment:edit']"
          >修改</el-button>
          <el-button
            v-if="scope.row.appointmentStatus === '0'"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleConfirm(scope.row)"
            v-hasPermi="['gangzhu:appointment:confirm']"
          >确认</el-button>
          <el-button
            v-if="scope.row.appointmentStatus === '2'"
            size="mini"
            type="text"
            icon="el-icon-success"
            @click="handleComplete(scope.row)"
            v-hasPermi="['gangzhu:appointment:complete']"
          >审核完成</el-button>
          <el-button
            v-if="scope.row.appointmentStatus === '0' || scope.row.appointmentStatus === '1'"
            size="mini"
            type="text"
            icon="el-icon-close"
            @click="handleCancel(scope.row)"
            v-hasPermi="['gangzhu:appointment:cancel']"
          >取消</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:appointment:remove']"
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

    <!-- 添加或修改预约看房对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="预约编号" prop="appointmentNo">
              <el-input v-model="form.appointmentNo" placeholder="自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租户ID" prop="tenantId">
              <el-input-number v-model="form.tenantId" controls-position="right" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="房源ID" prop="houseId">
              <el-input-number v-model="form.houseId" controls-position="right" :min="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目ID" prop="projectId">
              <el-input-number v-model="form.projectId" controls-position="right" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactName">
              <el-input v-model="form.contactName" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="预约日期" prop="appointmentDate">
              <el-date-picker
                v-model="form.appointmentDate"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预约时段" prop="timeSlot">
              <el-select v-model="form.timeSlot" placeholder="请选择预约时段">
                <el-option label="上午(9:00-12:00)" value="1" />
                <el-option label="下午(14:00-17:00)" value="2" />
                <el-option label="晚上(18:00-20:00)" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="预约人数" prop="visitorCount">
          <el-input-number v-model="form.visitorCount" controls-position="right" :min="1" :max="10" />
        </el-form-item>
        <el-form-item label="预约状态" prop="appointmentStatus">
          <el-select v-model="form.appointmentStatus" placeholder="请选择预约状态">
            <el-option label="待确认" value="0" />
            <el-option label="已确认" value="1" />
            <el-option label="用户已确认看房" value="2" />
            <el-option label="已完成" value="3" />
            <el-option label="已取消" value="4" />
            <el-option label="已过期" value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否到访" prop="isVisited">
          <el-radio-group v-model="form.isVisited">
            <el-radio label="0">否</el-radio>
            <el-radio label="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="评价" prop="evaluation">
          <el-input v-model="form.evaluation" type="textarea" placeholder="请输入评价" :rows="3" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 预约详情对话框 -->
    <el-dialog title="预约详情" :visible.sync="detailOpen" width="900px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="预约编号">{{ detailData.appointmentNo }}</el-descriptions-item>
        <el-descriptions-item label="预约来源">{{ detailData.appointmentSource === 'H5' ? 'H5用户端' : '管理端' }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ detailData.visitorName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.visitorPhone }}</el-descriptions-item>
        <el-descriptions-item label="用户昵称">{{ detailData.userName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预约人数">{{ detailData.visitorCount }}人</el-descriptions-item>
        <el-descriptions-item label="项目名称" :span="2">{{ detailData.projectName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="项目地址" :span="2">{{ detailData.projectAddress || '-' }}</el-descriptions-item>
        <el-descriptions-item label="房源编码">{{ detailData.houseCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="房源名称">{{ detailData.houseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="预约日期">{{ detailData.appointmentDate }}</el-descriptions-item>
        <el-descriptions-item label="预约时间">{{ detailData.appointmentTime }}</el-descriptions-item>
        <el-descriptions-item label="预约状态">
          <el-tag v-if="detailData.appointmentStatus === '0'" type="warning">待确认</el-tag>
          <el-tag v-else-if="detailData.appointmentStatus === '1'" type="success">已确认</el-tag>
          <el-tag v-else-if="detailData.appointmentStatus === '2'" type="primary">用户已确认看房</el-tag>
          <el-tag v-else-if="detailData.appointmentStatus === '3'" type="info">已完成</el-tag>
          <el-tag v-else-if="detailData.appointmentStatus === '4'" type="danger">已取消</el-tag>
          <el-tag v-else type="info">已过期</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="是否到访">
          <el-tag v-if="detailData.isVisited === '1'" type="success">是</el-tag>
          <el-tag v-else type="info">否</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="确认人" v-if="detailData.confirmBy">{{ detailData.confirmBy }}</el-descriptions-item>
        <el-descriptions-item label="确认时间" v-if="detailData.confirmTime">{{ detailData.confirmTime }}</el-descriptions-item>
        <el-descriptions-item label="取消原因" :span="2" v-if="detailData.cancelReason">{{ detailData.cancelReason }}</el-descriptions-item>
        <el-descriptions-item label="取消时间" :span="2" v-if="detailData.cancelTime">{{ detailData.cancelTime }}</el-descriptions-item>
        <el-descriptions-item label="创建时间" :span="2">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="更新时间" :span="2">{{ detailData.updateTime }}</el-descriptions-item>
        <el-descriptions-item label="评价" :span="2">{{ detailData.evaluation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAppointment, getAppointment, addAppointment, updateAppointment, delAppointment, confirmAppointment, cancelAppointment, completeAppointment } from "@/api/gangzhu/appointment";

export default {
  name: "Appointment",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      appointmentList: [],
      title: "",
      open: false,
      detailOpen: false,
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        appointmentNo: null,
        appointmentStatus: null,
      },
      form: {},
      rules: {
        tenantId: [
          { required: true, message: "租户ID不能为空", trigger: "blur" }
        ],
        houseId: [
          { required: true, message: "房源ID不能为空", trigger: "blur" }
        ],
        contactName: [
          { required: true, message: "联系人不能为空", trigger: "blur" }
        ],
        contactPhone: [
          { required: true, message: "联系电话不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ],
        appointmentDate: [
          { required: true, message: "预约日期不能为空", trigger: "change" }
        ],
        timeSlot: [
          { required: true, message: "预约时段不能为空", trigger: "change" }
        ],
        visitorCount: [
          { required: true, message: "预约人数不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    getList() {
      this.loading = true;
      listAppointment(this.queryParams).then(response => {
        this.appointmentList = response.rows;
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
        appointmentId: null,
        appointmentNo: null,
        tenantId: null,
        houseId: null,
        projectId: null,
        appointmentDate: null,
        timeSlot: null,
        contactName: null,
        contactPhone: null,
        visitorCount: 1,
        appointmentStatus: "0",
        isVisited: "0",
        evaluation: null,
        remark: null
      };
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
      this.ids = selection.map(item => item.appointmentId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加预约看房";
    },
    handleUpdate(row) {
      this.reset();
      const appointmentId = row.appointmentId || this.ids
      getAppointment(appointmentId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改预约看房";
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.appointmentId != null) {
            updateAppointment(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addAppointment(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const appointmentIds = row.appointmentId || this.ids;
      this.$modal.confirm('是否确认删除预约编号为"' + appointmentIds + '"的数据项?').then(function() {
        return delAppointment(appointmentIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleConfirm(row) {
      this.$modal.confirm('是否确认该预约?').then(function() {
        return confirmAppointment(row.appointmentId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("确认成功");
      }).catch(() => {});
    },
    handleCancel(row) {
      this.$prompt('请输入取消原因', '取消预约', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPattern: /.+/,
        inputErrorMessage: '取消原因不能为空'
      }).then(({ value }) => {
        return cancelAppointment(row.appointmentId, value);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("取消成功");
      }).catch(() => {});
    },
    handleComplete(row) {
      this.$modal.confirm('是否确认该预约已完成看房?').then(function() {
        return completeAppointment(row.appointmentId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("审核完成");
      }).catch(() => {});
    },
    handleDetail(row) {
      getAppointment(row.appointmentId).then(response => {
        this.detailData = response.data;
        this.detailOpen = true;
      });
    },
    handleExport() {
      this.download('system/appointment/export', {
        ...this.queryParams
      }, `appointment_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
