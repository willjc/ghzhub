<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="批次名称" prop="batchName">
        <el-input
          v-model="queryParams.batchName"
          placeholder="请输入批次名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="单位名称" prop="enterpriseName">
        <el-input
          v-model="queryParams.enterpriseName"
          placeholder="请输入单位名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['system:enterpriseBatch:add']"
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
          v-hasPermi="['system:enterpriseBatch:edit']"
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
          v-hasPermi="['system:enterpriseBatch:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="batchList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="批次编号" align="center" prop="batchNo" width="150" show-overflow-tooltip />
      <el-table-column label="批次名称" align="center" prop="batchName" min-width="160" show-overflow-tooltip />
      <el-table-column label="单位名称" align="center" prop="enterpriseName" min-width="160" show-overflow-tooltip />
      <el-table-column label="联系人" align="center" prop="contactPerson" width="100" />
      <el-table-column label="联系方式" align="center" prop="contactPhone" width="130" />
      <el-table-column label="所属项目" align="center" prop="projectName" min-width="140" show-overflow-tooltip />
      <el-table-column label="入驻日期" align="center" min-width="200">
        <template slot-scope="scope">
          <span v-if="scope.row.entryStartDate && scope.row.entryEndDate">
            {{ parseTime(scope.row.entryStartDate, '{y}-{m}-{d}') }} ~ {{ parseTime(scope.row.entryEndDate, '{y}-{m}-{d}') }}
          </span>
          <span v-else style="color: #909399">未设置</span>
        </template>
      </el-table-column>
      <el-table-column label="房源数量" align="center" width="100">
        <template slot-scope="scope">
          <span>{{ scope.row.houseCount || 0 }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160">
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
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:enterpriseBatch:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:enterpriseBatch:remove']"
            style="color: #F56C6C"
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

    <!-- 添加或修改企业批次对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1200px" append-to-body :close-on-click-modal="false">
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <!-- 基本信息表单 -->
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="批次名称" prop="batchName">
              <el-input v-model="form.batchName" placeholder="请输入批次名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单位名称" prop="enterpriseName">
              <el-input v-model="form.enterpriseName" placeholder="请输入单位名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人" prop="contactPerson">
              <el-input v-model="form.contactPerson" placeholder="请输入联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系方式" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系方式" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="入驻日期" prop="checkInDate">
              <el-date-picker
                v-model="form.checkInDate"
                type="date"
                placeholder="请选择入驻日期"
                value-format="timestamp"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止日期" prop="checkOutDate">
              <el-date-picker
                v-model="form.checkOutDate"
                type="date"
                placeholder="请选择截止日期"
                value-format="timestamp"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="所属项目">
              <el-select v-model="selectedProjectIds" placeholder="请选择项目（可多选）" multiple collapse-tags style="width: 100%">
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
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-divider content-position="left">房源选择</el-divider>

        <!-- 房源列表 -->
        <el-row>
          <el-col :span="24">
            <div class="section-header">
              <h4>房源列表</h4>
              <span class="count-tip">已选 {{ selectedHouses.length }} 套房源</span>
            </div>
            <el-table
              ref="houseTable"
              :data="availableHouseList"
              @selection-change="handleHouseSelectionChange"
              border
              height="400"
              style="width: 100%"
            >
              <el-table-column type="selection" width="50" align="center" />
              <el-table-column label="房源编号" prop="houseCode" width="150" show-overflow-tooltip />
              <el-table-column label="房间号" prop="houseNo" width="120" />
              <el-table-column label="楼层" prop="floor" width="80" align="center" />
              <el-table-column label="户型" prop="houseTypeName" min-width="140" show-overflow-tooltip />
              <el-table-column label="面积(㎡)" prop="area" width="100" align="center" />
              <el-table-column label="租金(元/月)" prop="rentPrice" width="120" align="center" />
            </el-table>
          </el-col>
        </el-row>

        <el-divider content-position="left">费用计算</el-divider>

        <!-- 费用计算明细 -->
        <el-row :gutter="20">
          <el-col :span="24">
            <el-descriptions :column="3" border size="small">
              <el-descriptions-item label="房源数量">{{ selectedHouses.length }} 套</el-descriptions-item>
              <el-descriptions-item label="计算月数">{{ calculatedMonths }} 个月</el-descriptions-item>
              <el-descriptions-item label="单价合计">{{ totalRent }} 元/月</el-descriptions-item>
              <el-descriptions-item label="计算总价">{{ totalAmount }} 元</el-descriptions-item>
              <el-descriptions-item label="优惠金额">
                <el-input-number
                  v-model="form.discountAmount"
                  :min="0"
                  :max="totalAmount"
                  :precision="2"
                  controls-position="right"
                  style="width: 200px"
                />
              </el-descriptions-item>
              <el-descriptions-item label="优惠后总价">
                <span style="color: #f56c6c; font-weight: bold; font-size: 16px">{{ finalAmount }} 元</span>
              </el-descriptions-item>
            </el-descriptions>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="合同文件" prop="contractFile">
              <el-upload
                ref="contractUpload"
                :limit="1"
                :action="uploadUrl"
                :headers="uploadHeaders"
                :on-success="handleContractSuccess"
                :on-remove="handleContractRemove"
                :on-exceed="handleContractExceed"
                :file-list="contractFileList"
              >
                <el-button size="small" type="primary" icon="el-icon-upload">上传合同</el-button>
                <div slot="tip" class="el-upload__tip">支持 pdf、doc、docx、jpg、png 格式，文件大小不超过 10MB</div>
              </el-upload>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm" :disabled="selectedHouses.length === 0">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog title="企业批次详情" :visible.sync="viewOpen" width="1100px" append-to-body>
      <!-- 基本信息 -->
      <el-divider content-position="left">基本信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="批次编号">{{ viewData.batchNo }}</el-descriptions-item>
        <el-descriptions-item label="批次名称">{{ viewData.batchName }}</el-descriptions-item>
        <el-descriptions-item label="单位名称">{{ viewData.enterpriseName }}</el-descriptions-item>
        <el-descriptions-item label="联系人">{{ viewData.contactPerson }}</el-descriptions-item>
        <el-descriptions-item label="联系方式">{{ viewData.contactPhone }}</el-descriptions-item>
        <el-descriptions-item label="所属项目">{{ viewData.projectName }}</el-descriptions-item>
        <el-descriptions-item label="入驻开始日期">
          {{ parseTime(viewData.entryStartDate, '{y}-{m}-{d}') }}
        </el-descriptions-item>
        <el-descriptions-item label="入驻结束日期">
          {{ parseTime(viewData.entryEndDate, '{y}-{m}-{d}') }}
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ viewData.createBy }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">
          {{ parseTime(viewData.createTime, '{y}-{m}-{d} {h}:{i}:{s}') }}
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewData.remark || '无' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 费用信息 -->
      <el-divider content-position="left">费用信息</el-divider>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="房源数量">{{ viewData.houseCount || 0 }} 套</el-descriptions-item>
        <el-descriptions-item label="计算月数">{{ viewData.months || 0 }} 个月</el-descriptions-item>
        <el-descriptions-item label="单价合计">{{ viewData.totalRent || 0 }} 元/月</el-descriptions-item>
        <el-descriptions-item label="计算总价">{{ viewData.totalAmount || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="优惠金额">{{ viewData.discountAmount || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="优惠后总价">
          <span style="color: #f56c6c; font-weight: bold; font-size: 16px">{{ viewData.finalAmount || 0 }} 元</span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 分配房源列表 -->
      <el-divider content-position="left">分配房源列表</el-divider>
      <el-table
        :data="viewData.houseList"
        border
        max-height="400"
        style="width: 100%"
        v-loading="houseLoading"
      >
        <el-table-column label="序号" type="index" width="60" align="center" />
        <el-table-column label="房源编号" prop="houseCode" width="150" show-overflow-tooltip />
        <el-table-column label="房间号" prop="houseNo" width="120" />
        <el-table-column label="楼层" prop="floor" width="80" align="center" />
        <el-table-column label="户型" prop="houseTypeName" min-width="140" show-overflow-tooltip />
        <el-table-column label="面积(㎡)" prop="area" width="100" align="center" />
        <el-table-column label="租金(元/月)" prop="rentPrice" width="120" align="center" />
      </el-table>

      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listEnterpriseBatch, getEnterpriseBatch, delEnterpriseBatch, saveEnterpriseBatchWithHouses, getEnterpriseBatchHouses, listEnterpriseBill, getEnterpriseBillByBatchId } from "@/api/gangzhu/enterprise";
import { listProject } from "@/api/gangzhu/project";
import { getAvailableHouses } from "@/api/gangzhu/batch";
import { getToken } from "@/utils/auth";

export default {
  name: "EnterpriseBatch",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      batchList: [],
      projectList: [],
      title: "",
      open: false,
      viewOpen: false,
      viewData: {},
      houseLoading: false,
      selectedProjectIds: [],  // 多选项目（独立变量，不在form中）
      availableHouseList: [],  // 可用房源列表
      selectedHouses: [],       // 已选房源
      contractFileList: [],     // 合同文件列表
      uploadUrl: process.env.VUE_APP_BASE_API + "/common/upload", // 文件上传地址
      uploadHeaders: {          // 上传请求头
        Authorization: "Bearer " + getToken()
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        batchName: null,
        enterpriseName: null
      },
      form: {},
      rules: {
        batchName: [
          { required: true, message: "批次名称不能为空", trigger: "blur" }
        ],
        enterpriseName: [
          { required: true, message: "单位名称不能为空", trigger: "blur" }
        ],
        contactPerson: [
          { required: true, message: "联系人不能为空", trigger: "blur" }
        ],
        contactPhone: [
          { required: true, message: "联系方式不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ],
        checkInDate: [
          { required: true, message: "入驻日期不能为空", trigger: "change" }
        ],
        checkOutDate: [
          { required: true, message: "截止日期不能为空", trigger: "change" }
        ]
      }
    };
  },
  computed: {
    // 计算月租金合计
    totalRent() {
      return this.selectedHouses.reduce((sum, house) => {
        return sum + (parseFloat(house.rentPrice) || 0);
      }, 0).toFixed(2);
    },
    // 计算月数（不满一个月算一个月）
    calculatedMonths() {
      if (!this.form.checkInDate || !this.form.checkOutDate) return 0;
      const startDate = new Date(this.form.checkInDate);
      const endDate = new Date(this.form.checkOutDate);

      if (endDate <= startDate) return 0;

      // 计算月数差
      const years = endDate.getFullYear() - startDate.getFullYear();
      const months = endDate.getMonth() - startDate.getMonth();
      let totalMonths = years * 12 + months;

      // 如果截止日期的日数 >= 入驻日期的日数，加1个月
      // 否则也算1个月（不满一个月算一个月）
      totalMonths += 1;

      return totalMonths > 0 ? totalMonths : 0;
    },
    // 计算总价
    totalAmount() {
      if (this.calculatedMonths <= 0) return 0;
      return (parseFloat(this.totalRent) * this.calculatedMonths).toFixed(2);
    },
    // 计算优惠后总价
    finalAmount() {
      const discount = parseFloat(this.form.discountAmount) || 0;
      const total = parseFloat(this.totalAmount) || 0;
      const final = Math.max(0, total - discount);
      return final.toFixed(2);
    }
  },
  created() {
    this.getList();
    this.getProjectList();
  },
  watch: {
    // 监听项目变化，加载可用房源
    selectedProjectIds(newVal) {
      if (newVal && newVal.length > 0) {
        this.loadAvailableHouses();
      } else {
        this.availableHouseList = [];
        this.selectedHouses = [];
      }
    }
  },
  methods: {
    /** 查询企业批次列表 */
    getList() {
      this.loading = true;
      listEnterpriseBatch(this.queryParams).then(response => {
        this.batchList = response.rows || response.data || [];
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
    cancel() {
      this.open = false;
      this.reset();
    },
    reset() {
      this.form = {
        batchId: null,
        batchNo: null,
        batchName: null,
        enterpriseName: null,
        contactPerson: null,
        contactPhone: null,
        checkInDate: null,
        checkOutDate: null,
        discountAmount: 0,
        contractFile: null,
        houseCount: 0,
        remark: null
      };
      // 重置新增的字段
      this.selectedProjectIds = [];
      this.availableHouseList = [];
      this.selectedHouses = [];
      this.contractFileList = [];
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
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.batchId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加企业批次";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const batchId = row.batchId || this.ids;

      getEnterpriseBatch(batchId).then(response => {
        this.form = response.data;

        // 将 projectIds 字符串转换为数组
        if (this.form.projectIds) {
          this.selectedProjectIds = this.form.projectIds.split(',').map(id => parseInt(id));
        }

        // 加载关联的房源列表
        getEnterpriseBatchHouses(batchId).then(houseResponse => {
          const houseList = houseResponse.data || [];
          // 等待可用房源加载后设置选中
          this.$nextTick(() => {
            if (this.$refs.houseTable && this.availableHouseList.length > 0) {
              this.selectedHouses = houseList.map(h => {
                return this.availableHouseList.find(ah => ah.houseId === h.house_id) || h;
              });
              // 设置表格选中
              this.selectedHouses.forEach(house => {
                const row = this.availableHouseList.find(ah => ah.houseId === house.houseId || ah.houseId === house.house_id);
                if (row) {
                  this.$refs.houseTable.toggleRowSelection(row, true);
                }
              });
            }
          });
        }).catch(() => {
          console.log('加载房源列表失败');
        });

        this.open = true;
        this.title = "修改企业批次";
      });
    },
    /** 详情按钮操作 */
    handleView(row) {
      this.houseLoading = true;

      getEnterpriseBatch(row.batchId).then(response => {
        this.viewData = response.data;
        this.viewData.houseList = [];
        this.viewOpen = true;

        // 先设置默认值（账单API会覆盖这些值）
        this.$set(this.viewData, 'discountAmount', 0);
        this.$set(this.viewData, 'finalAmount', 0);

        // 加载房源列表
        getEnterpriseBatchHouses(row.batchId).then(houseResponse => {
          this.viewData.houseList = houseResponse.data || [];

          // 计算月租金合计
          const houseList = this.viewData.houseList || [];
          this.viewData.totalRent = houseList.reduce((sum, h) => sum + (parseFloat(h.rentPrice) || 0), 0).toFixed(2);

          // 计算月数和总价
          if (this.viewData.checkInDate && this.viewData.checkOutDate) {
            const startDate = new Date(this.viewData.checkInDate);
            const endDate = new Date(this.viewData.checkOutDate);
            const years = endDate.getFullYear() - startDate.getFullYear();
            const months = endDate.getMonth() - startDate.getMonth();
            this.viewData.months = years * 12 + months + 1;

            // 计算总价
            const totalRent = parseFloat(this.viewData.totalRent) || 0;
            this.viewData.totalAmount = (totalRent * this.viewData.months).toFixed(2);
          }

          // 如果账单API还没返回，设置默认的finalAmount
          if (this.viewData.finalAmount === 0) {
            this.$set(this.viewData, 'finalAmount', this.viewData.totalAmount || 0);
          }

          this.houseLoading = false;
        }).catch(() => {
          this.houseLoading = false;
        });

        // 查询该批次对应的账单，获取实际优惠金额
        getEnterpriseBillByBatchId(row.batchId).then(billResponse => {
          console.log('账单API完成');
          if (billResponse && billResponse.data) {
            const bill = billResponse.data;
            console.log('账单数据 - 优惠金额:', bill.discountAmount);
            // 使用 $set 确保响应式更新
            this.$set(this.viewData, 'discountAmount', bill.discountAmount || 0);
            this.$set(this.viewData, 'finalAmount', bill.finalAmount || this.viewData.totalAmount);
            console.log('账单API设置后 - viewData.discountAmount:', this.viewData.discountAmount);
            console.log('账单API设置后 - viewData.finalAmount:', this.viewData.finalAmount);
          } else {
            console.log('没有找到账单数据或data为空');
          }
        }).catch((error) => {
          console.error('账单查询失败:', error);
        });
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 验证项目选择
          if (!this.selectedProjectIds || this.selectedProjectIds.length === 0) {
            this.$modal.msgError('请选择所属项目');
            return;
          }

          // 验证房源选择
          if (this.selectedHouses.length === 0) {
            this.$modal.msgError('请至少选择一个房源');
            return;
          }

          // 验证日期
          if (!this.form.checkInDate || !this.form.checkOutDate) {
            this.$modal.msgError('请选择入驻日期和截止日期');
            return;
          }

          // 构建提交数据
          const params = {
            batchInfo: {
              batchId: this.form.batchId,
              batchName: this.form.batchName,
              enterpriseName: this.form.enterpriseName,
              contactPerson: this.form.contactPerson,
              contactPhone: this.form.contactPhone,
              projectIds: this.selectedProjectIds.join(','),
              checkInDate: this.form.checkInDate,
              checkOutDate: this.form.checkOutDate,
              contractFile: this.form.contractFile,
              discountAmount: this.form.discountAmount || 0,
              remark: this.form.remark,
              houseCount: this.selectedHouses.length
            },
            houseList: this.selectedHouses.map(house => ({
              houseId: house.houseId,
              houseCode: house.houseCode,
              houseNo: house.houseNo,
              rentPrice: house.rentPrice
            }))
          };

          // 调用保存接口
          saveEnterpriseBatchWithHouses(params).then(response => {
            this.$modal.msgSuccess(this.form.batchId ? '修改成功' : '新增成功');
            this.open = false;
            this.getList();
          }).catch(err => {
            this.$modal.msgError(err.msg || '操作失败');
          });
        }
      });
    },
    /** 文件上传成功回调 */
    handleContractSuccess(res, file, fileList) {
      if (res.code === 200) {
        this.form.contractFile = res.fileName || res.url;
        this.$modal.msgSuccess("上传成功");
      } else {
        this.$modal.msgError(res.msg || "上传失败");
      }
    },
    /** 文件移除回调 */
    handleContractRemove(res, file, fileList) {
      this.form.contractFile = null;
    },
    /** 文件超出限制 */
    handleContractExceed() {
      this.$modal.msgWarning("只能上传一个合同文件");
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const batchIds = row.batchId || this.ids;
      this.$modal.confirm('是否确认删除企业批次编号为"' + batchIds + '"的数据项?').then(function() {
        return delEnterpriseBatch(batchIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 加载可用房源 */
    loadAvailableHouses() {
      if (!this.selectedProjectIds || this.selectedProjectIds.length === 0) {
        return;
      }
      const projectIds = this.selectedProjectIds.join(',');
      getAvailableHouses(projectIds).then(response => {
        this.availableHouseList = response.data || [];
        this.selectedHouses = [];
      }).catch(err => {
        this.$modal.msgError('加载房源失败: ' + (err.msg || '未知错误'));
      });
    },
    /** 房源选择变化 */
    handleHouseSelectionChange(selection) {
      this.selectedHouses = selection;
    }
  }
};
</script>

<style scoped>
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.section-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #303133;
}

.count-tip {
  font-size: 12px;
  color: #409EFF;
  font-weight: 500;
}
</style>
