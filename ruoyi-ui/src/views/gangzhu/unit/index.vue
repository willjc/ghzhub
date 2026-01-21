<template>
  <div :class="embedded ? '' : 'app-container'">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="所属项目" prop="projectId" v-if="!embedded">
        <el-select v-model="queryParams.projectId" placeholder="请选择项目" clearable style="width: 180px" @change="handleProjectChange">
          <el-option
            v-for="project in projectList"
            :key="project.projectId"
            :label="project.projectName"
            :value="project.projectId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属楼栋" prop="buildingId" v-if="!embedded">
        <el-select v-model="queryParams.buildingId" placeholder="请选择楼栋" clearable style="width: 180px">
          <el-option
            v-for="building in buildingList"
            :key="building.buildingId"
            :label="building.buildingName"
            :value="building.buildingId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="单元名称" prop="unitName">
        <el-input
          v-model="queryParams.unitName"
          placeholder="请输入单元名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unitList" @selection-change="handleSelectionChange" :height="embedded ? '500px' : 'calc(100vh - 320px)'" style="width: 100%">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="单元名称" align="center" prop="unitName" width="150" />
      <el-table-column label="单元编码" align="center" prop="unitCode" width="120" />
      <el-table-column label="所属楼栋" align="center" prop="buildingName" width="150" />
      <el-table-column label="所属项目" align="center" prop="projectName" width="180" show-overflow-tooltip />
      <el-table-column label="总房源" align="center" prop="totalHouses" width="100" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success" size="small">正常</el-tag>
          <el-tag v-else type="danger" size="small">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
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

    <!-- 添加或修改单元对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row v-if="!embedded">
          <el-col :span="12">
            <el-form-item label="所属项目" prop="projectId">
              <el-select v-model="form.projectId" placeholder="请选择项目" style="width: 100%" @change="handleFormProjectChange">
                <el-option
                  v-for="project in projectList"
                  :key="project.projectId"
                  :label="project.projectName"
                  :value="project.projectId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="所属楼栋" prop="buildingId">
              <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%">
                <el-option
                  v-for="building in formBuildingList"
                  :key="building.buildingId"
                  :label="building.buildingName"
                  :value="building.buildingId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="单元名称" prop="unitName">
              <el-input v-model="form.unitName" placeholder="请输入单元名称" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单元编码" prop="unitCode">
              <el-input v-model="form.unitCode" placeholder="请输入单元编码" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="排序" prop="sortOrder">
              <el-input-number v-model="form.sortOrder" :min="0" :max="9999" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listUnit, getUnit, addUnit, updateUnit, delUnit } from "@/api/gangzhu/unit";
import { listProject } from "@/api/gangzhu/project";
import { listBuilding } from "@/api/gangzhu/building";

export default {
  name: "Unit",
  props: {
    projectId: {
      type: Number,
      default: null
    },
    embedded: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      unitList: [],
      projectList: [],
      buildingList: [],
      formBuildingList: [],
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        projectId: null,
        buildingId: null,
        unitName: null,
        unitCode: null,
        status: null
      },
      form: {},
      rules: {
        projectId: [
          { required: true, message: "所属项目不能为空", trigger: "change" }
        ],
        buildingId: [
          { required: true, message: "所属楼栋不能为空", trigger: "change" }
        ],
        unitName: [
          { required: true, message: "单元名称不能为空", trigger: "blur" }
        ],
        unitCode: [
          { required: true, message: "单元编码不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    // 如果传入了projectId,预设查询条件并加载楼栋列表
    if (this.projectId) {
      this.queryParams.projectId = this.projectId;
      this.handleProjectChange(this.projectId);
    }
    this.getList();
    // 如果是嵌入模式,不加载项目列表
    if (!this.embedded) {
      this.getProjectList();
    }
  },
  methods: {
    /** 查询单元列表 */
    getList() {
      this.loading = true;
      listUnit(this.queryParams).then(response => {
        this.unitList = response.rows || response.data || [];
        this.total = response.total || 0;
        this.loading = false;
      });
    },
    /** 查询项目列表 */
    getProjectList() {
      listProject({ status: "0" }).then(response => {
        this.projectList = response.rows || response.data || [];
      });
    },
    /** 查询条件-项目变更 */
    handleProjectChange(projectId) {
      this.queryParams.buildingId = null;
      this.buildingList = [];
      if (projectId) {
        listBuilding({ projectId: projectId, status: "0" }).then(response => {
          this.buildingList = response.rows || response.data || [];
        });
      }
    },
    /** 表单-项目变更 */
    handleFormProjectChange(projectId) {
      this.form.buildingId = null;
      this.formBuildingList = [];
      if (projectId) {
        listBuilding({ projectId: projectId, status: "0" }).then(response => {
          this.formBuildingList = response.rows || response.data || [];
        });
      }
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        unitId: null,
        buildingId: null,
        projectId: null,
        unitName: null,
        unitCode: null,
        totalHouses: 0,
        status: "0",
        sortOrder: 0,
        remark: null
      };
      this.formBuildingList = [];
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.buildingList = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.unitId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      // 如果是嵌入模式,自动设置项目ID并加载楼栋列表
      if (this.embedded && this.projectId) {
        this.form.projectId = this.projectId;
        listBuilding({ projectId: this.projectId, status: "0" }).then(res => {
          this.formBuildingList = res.rows || res.data || [];
        });
      }
      this.open = true;
      this.title = "添加单元";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const unitId = row.unitId || this.ids
      getUnit(unitId).then(response => {
        this.form = response.data;
        // 加载对应的楼栋列表
        if (this.form.projectId) {
          listBuilding({ projectId: this.form.projectId, status: "0" }).then(res => {
            this.formBuildingList = res.rows || res.data || [];
          });
        }
        this.open = true;
        this.title = "修改单元";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.unitId != null) {
            updateUnit(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addUnit(this.form).then(response => {
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
      const unitIds = row.unitId || this.ids;
      this.$modal.confirm('是否确认删除单元编号为"' + unitIds + '"的数据项?').then(function() {
        return delUnit(unitIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
