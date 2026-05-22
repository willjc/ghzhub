<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="公司名称" prop="companyName">
        <el-input v-model="queryParams.companyName" placeholder="请输入公司名称" clearable
          style="width: 200px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="服务类型" prop="companyType">
        <el-select v-model="queryParams.companyType" placeholder="请选择" clearable style="width: 120px">
          <el-option v-for="dict in dict.type.hz_service_company_type"
            :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input v-model="queryParams.contactPerson" placeholder="联系人" clearable
          style="width: 140px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input v-model="queryParams.contactPhone" placeholder="联系电话" clearable
          style="width: 140px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px">
          <el-option label="启用" value="0" />
          <el-option label="停用" value="1" />
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini"
          @click="handleAdd" v-hasPermi="['gangzhu:serviceCompany:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single"
          @click="handleUpdate" v-hasPermi="['gangzhu:serviceCompany:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple"
          @click="handleDelete" v-hasPermi="['gangzhu:serviceCompany:remove']">删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-download" size="mini"
          @click="handleExport" v-hasPermi="['gangzhu:serviceCompany:export']">导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="companyList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="companyId" width="70" />
      <el-table-column label="公司名称" align="center" prop="companyName" min-width="180" show-overflow-tooltip />
      <el-table-column label="服务类型" align="center" prop="companyType" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.hz_service_company_type" :value="scope.row.companyType"/>
        </template>
      </el-table-column>
      <el-table-column label="联系人" align="center" prop="contactPerson" width="100" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" width="120" />
      <el-table-column label="公司地址" align="center" prop="address" min-width="180" show-overflow-tooltip />
      <el-table-column label="排序" align="center" prop="sortOrder" width="70" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">启用</el-tag>
          <el-tag v-else type="info">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="155">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="160">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit"
            @click="handleUpdate(scope.row)" v-hasPermi="['gangzhu:serviceCompany:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" style="color:#F56C6C"
            @click="handleDelete(scope.row)" v-hasPermi="['gangzhu:serviceCompany:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total"
      :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增/修改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="640px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="公司名称" prop="companyName">
          <el-input v-model="form.companyName" placeholder="请输入公司名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="服务类型" prop="companyType">
          <el-select v-model="form.companyType" placeholder="请选择服务类型" style="width:100%">
            <el-option v-for="dict in dict.type.hz_service_company_type"
              :key="dict.value" :label="dict.label" :value="dict.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系人" prop="contactPerson">
          <el-input v-model="form.contactPerson" placeholder="请输入联系人" maxlength="50" />
        </el-form-item>
        <el-form-item label="联系电话" prop="contactPhone">
          <el-input v-model="form.contactPhone" placeholder="请输入联系电话" maxlength="20" />
        </el-form-item>
        <el-form-item label="公司地址" prop="address">
          <el-input v-model="form.address" placeholder="请输入公司地址" maxlength="200" />
        </el-form-item>
        <el-form-item label="服务区域" prop="serviceArea">
          <el-input v-model="form.serviceArea" placeholder="如：航空港区、新郑市等" maxlength="200" />
        </el-form-item>
        <el-form-item label="公司简介" prop="intro">
          <el-input v-model="form.intro" type="textarea" :rows="3"
            placeholder="可填业务范围、资质等" maxlength="500" />
        </el-form-item>
        <el-form-item label="排序值" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="9999" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">启用</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" maxlength="500" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  listServiceCompany, getServiceCompany,
  addServiceCompany, updateServiceCompany, delServiceCompany
} from "@/api/gangzhu/serviceCompany";

export default {
  name: "ServiceCompany",
  dicts: ["hz_service_company_type"],
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      companyList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        companyName: null,
        companyType: null,
        contactPerson: null,
        contactPhone: null,
        status: null
      },
      form: {},
      rules: {
        companyName: [{ required: true, message: "公司名称不能为空", trigger: "blur" }],
        companyType: [{ required: true, message: "请选择服务类型", trigger: "change" }],
        contactPhone: [
          { pattern: /^1[3-9]\d{9}$|^0\d{2,3}-?\d{7,8}$/, message: "请输入合法的电话号码", trigger: "blur" }
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
      listServiceCompany(this.queryParams).then(res => {
        this.companyList = res.rows;
        this.total = res.total;
        this.loading = false;
      });
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList(); },
    resetQuery() { this.resetForm("queryForm"); this.handleQuery(); },
    handleSelectionChange(selection) {
      this.ids = selection.map(it => it.companyId);
      this.single = selection.length !== 1;
      this.multiple = !selection.length;
    },
    reset() {
      this.form = {
        companyId: null,
        companyName: "",
        companyType: "3",
        contactPerson: "",
        contactPhone: "",
        address: "",
        serviceArea: "",
        intro: "",
        sortOrder: 0,
        status: "0",
        remark: ""
      };
      this.resetForm("form");
    },
    cancel() { this.open = false; this.reset(); },
    handleAdd() {
      this.reset();
      this.title = "新增服务公司";
      this.open = true;
    },
    handleUpdate(row) {
      this.reset();
      const id = row.companyId || (this.ids.length === 1 ? this.ids[0] : null);
      if (!id) return;
      getServiceCompany(id).then(res => {
        this.form = res.data;
        this.title = "修改服务公司";
        this.open = true;
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (!valid) return;
        const fn = this.form.companyId ? updateServiceCompany : addServiceCompany;
        fn(this.form).then(() => {
          this.$modal.msgSuccess(this.form.companyId ? "修改成功" : "新增成功");
          this.open = false;
          this.getList();
        });
      });
    },
    handleDelete(row) {
      const ids = row.companyId || this.ids;
      this.$modal.confirm('是否确认删除选中的服务公司？').then(() =>
        delServiceCompany(ids)
      ).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('gangzhu/serviceCompany/export', { ...this.queryParams },
        `service_company_${new Date().getTime()}.xlsx`);
    }
  }
};
</script>
