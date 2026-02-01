<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="申请编号" prop="applyNo">
        <el-input
          v-model="queryParams.applyNo"
          placeholder="请输入申请编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发票抬头" prop="invoiceTitle">
        <el-input
          v-model="queryParams.invoiceTitle"
          placeholder="请输入发票���头"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请状态" prop="applyStatus">
        <el-select v-model="queryParams.applyStatus" placeholder="请选择申请状态" clearable>
          <el-option label="开票中" value="1" />
          <el-option label="已开票" value="2" />
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
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:invoice:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="invoiceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="申请编号" align="center" prop="applyNo" min-width="180" show-overflow-tooltip />
      <el-table-column label="租户姓名" align="center" prop="tenantName" width="120" show-overflow-tooltip />
      <el-table-column label="发票抬头" align="center" prop="invoiceTitle" width="200" show-overflow-tooltip />
      <el-table-column label="发票类型" align="center" prop="invoiceType" width="140">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.invoiceType === '1'" type="primary">增值税普通发票</el-tag>
          <el-tag v-else-if="scope.row.invoiceType === '2'" type="success">增值税专用发票</el-tag>
          <el-tag v-else>其他</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="开票金额(元)" align="center" prop="invoiceAmount" width="120" />
      <el-table-column label="收件人" align="center" prop="receiverName" width="120" show-overflow-tooltip />
      <el-table-column label="收件电话" align="center" prop="receiverPhone" width="120" show-overflow-tooltip />
      <el-table-column label="申请状态" align="center" prop="applyStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.applyStatus === '1'" type="warning">开票中</el-tag>
          <el-tag v-else-if="scope.row.applyStatus === '2'" type="success">已开票</el-tag>
          <el-tag v-else type="info">待审核</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="申请时间" align="center" prop="createTime" width="180" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:invoice:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleComplete(scope.row)"
            v-hasPermi="['gangzhu:invoice:complete']"
            v-if="scope.row.applyStatus === '1'"
          >完成开票</el-button>
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

    <!-- 开票详情对话框 -->
    <el-dialog title="开票详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请编号">{{ detailForm.applyNo }}</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag v-if="detailForm.applyStatus === '1'" type="warning">开票中</el-tag>
          <el-tag v-else-if="detailForm.applyStatus === '2'" type="success">已开票</el-tag>
          <el-tag v-else type="info">待审核</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发票抬头">{{ detailForm.invoiceTitle }}</el-descriptions-item>
        <el-descriptions-item label="发票类型">
          <span v-if="detailForm.invoiceType === '1'">增值税普通发票</span>
          <span v-else-if="detailForm.invoiceType === '2'">增值税专用发票</span>
          <span v-else>其他</span>
        </el-descriptions-item>
        <el-descriptions-item label="纳税人识别号">{{ detailForm.taxNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="开票金额">{{ detailForm.invoiceAmount }}元</el-descriptions-item>
        <el-descriptions-item label="收件人">{{ detailForm.receiverName }}</el-descriptions-item>
        <el-descriptions-item label="收件电话">{{ detailForm.receiverPhone }}</el-descriptions-item>
        <el-descriptions-item label="开票内容">{{ detailForm.invoiceContent || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间" :span="2">{{ detailForm.createTime }}</el-descriptions-item>
      </el-descriptions>

      <!-- 账单信息 -->
      <el-divider content-position="left">关联账单信息</el-divider>
      <el-descriptions :column="2" border v-if="billInfo">
        <el-descriptions-item label="账单编号">{{ billInfo.billNo }}</el-descriptions-item>
        <el-descriptions-item label="账单金额">{{ billInfo.billAmount }}元</el-descriptions-item>
        <el-descriptions-item label="账单类型">
          <span v-if="billInfo.billType === '1'">押金</span>
          <span v-else-if="billInfo.billType === '2'">租金</span>
          <span v-else-if="billInfo.billType === '3'">水费</span>
          <span v-else-if="billInfo.billType === '4'">电费</span>
          <span v-else-if="billInfo.billType === '5'">燃气费</span>
          <span v-else-if="billInfo.billType === '6'">物业费</span>
          <span v-else>其他</span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 已开票信息 -->
      <el-divider content-position="left" v-if="invoiceInfo">已开票信息</el-divider>
      <el-descriptions :column="2" border v-if="invoiceInfo">
        <el-descriptions-item label="发票号码">{{ invoiceInfo.invoiceNo }}</el-descriptions-item>
        <el-descriptions-item label="开票日期">{{ invoiceInfo.invoiceDate }}</el-descriptions-item>
        <el-descriptions-item label="发票文件" :span="2">
          <!-- 图片文件 -->
          <el-image
            v-if="invoiceInfo.invoiceFile && isImageFileUrl(invoiceInfo.invoiceFile)"
            :src="getInvoiceFileUrl(invoiceInfo.invoiceFile)"
            :preview-src-list="[getInvoiceFileUrl(invoiceInfo.invoiceFile)]"
            style="width: 200px; height: 150px"
            fit="cover"
          >
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
          <!-- PDF/其他文件 -->
          <div v-else-if="invoiceInfo.invoiceFile" class="attachment-item">
            <i class="el-icon-document"></i>
            <el-link :href="getInvoiceFileUrl(invoiceInfo.invoiceFile)" target="_blank" type="primary">
              {{ getFileName(invoiceInfo.invoiceFile) }}
            </el-link>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 完成开票对话框 -->
    <el-dialog title="完成开票" :visible.sync="completeOpen" width="600px" append-to-body>
      <el-form ref="completeForm" :model="completeForm" :rules="completeRules" label-width="100px">
        <el-form-item label="发票号码" prop="invoiceNo">
          <el-input v-model="completeForm.invoiceNo" placeholder="请输入发票号码（可选）" />
        </el-form-item>
        <el-form-item label="发票文件" prop="invoiceFile">
          <image-upload
            v-model="completeForm.invoiceFile"
            :limit="1"
            :file-type="['jpg', 'jpeg', 'png', 'pdf']"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="completeOpen = false">取 消</el-button>
        <el-button type="primary" @click="submitComplete">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInvoice, getInvoice, completeInvoice } from '@/api/gangzhu/invoice'
import ImageUpload from '@/components/ImageUpload'

export default {
  name: 'Invoice',
  components: {
    ImageUpload
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
      // 开票申请表格数据
      invoiceList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applyNo: null,
        invoiceTitle: null,
        applyStatus: null
      },
      // 详情对话框
      detailOpen: false,
      // 完成开票对话框
      completeOpen: false,
      // 详情表单
      detailForm: {},
      // 账单信息
      billInfo: null,
      // 发票信息
      invoiceInfo: null,
      // 完成开票表单
      completeForm: {
        applyId: null,
        invoiceNo: null,
        invoiceFile: null
      },
      // 表单校验
      completeRules: {
        // invoiceNo: [{ required: true, message: '请输入发票号码', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询开票申请列表 */
    getList() {
      this.loading = true
      listInvoice(this.queryParams).then(response => {
        this.invoiceList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm('queryForm')
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.applyId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.detailForm = row
      this.billInfo = null
      this.invoiceInfo = null
      this.detailOpen = true

      // 获取完整详情
      getInvoice(row.applyId).then(response => {
        if (response.code === 200 && response.data) {
          this.detailForm = response.data.apply || row
          this.billInfo = response.data.bill
          this.invoiceInfo = response.data.invoice
        }
      })
    },
    /** 完成开票按钮操作 */
    handleComplete(row) {
      this.completeForm.applyId = row.applyId
      this.completeForm.invoiceNo = null
      this.completeForm.invoiceFile = null
      this.completeOpen = true
      this.resetForm('completeForm')
    },
    // 提交完成开票
    submitComplete() {
      completeInvoice({
        applyId: this.completeForm.applyId,
        invoiceNo: this.completeForm.invoiceNo,
        invoiceFile: this.completeForm.invoiceFile
      }).then(() => {
        this.$modal.msgSuccess('开票成功')
        this.completeOpen = false
        this.getList()
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      this.$modal.confirm('是否确认导出所有开票申请数据项?', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.download('/gangzhu/invoice/export', {
          ...this.queryParams
        }, `invoice_${new Date().getTime()}.xlsx`)
      })
    },
    // 获取发票图片URL
    getInvoiceFileUrl(path) {
      if (!path) return ''
      if (path.startsWith('http')) return path
      return process.env.VUE_APP_BASE_API + path
    },
    // 判断文件URL是否为图片
    isImageFileUrl(path) {
      if (!path) return false
      const ext = path.substring(path.lastIndexOf('.') + 1).toLowerCase()
      return ['jpg', 'jpeg', 'png', 'gif', 'bmp', 'webp'].includes(ext)
    },
    // 从路径中获取文件名
    getFileName(path) {
      if (!path) return ''
      const parts = path.split('/')
      return parts[parts.length - 1]
    }
  }
}
</script>

<style lang="scss" scoped>
.upload-preview {
  position: relative;
  display: inline-block;
  margin-top: 10px;

  .upload-img {
    width: 200px;
    height: 150px;
    border-radius: 4px;
  }

  .upload-pdf {
    display: flex;
    align-items: center;
    padding: 12px 20px;
    background-color: #f5f7fa;
    border-radius: 4px;
    border: 1px solid #dcdfe6;

    .upload-pdf-icon {
      font-size: 32px;
      color: #f56c6c;
      margin-right: 12px;
    }

    .upload-pdf-name {
      font-size: 14px;
      color: #606266;
      max-width: 200px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }
  }

  .upload-close {
    position: absolute;
    top: -5px;
    right: -5px;
    font-size: 20px;
    color: #999;
    cursor: pointer;

    &:hover {
      color: #333;
    }
  }
}

// 附件样式
.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background-color: #f5f7fa;
  border-radius: 4px;
  width: fit-content;

  i {
    color: #409eff;
    font-size: 16px;
  }
}

// 图片错误插槽样式
.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 200px;
  height: 150px;
  background-color: #f5f7fa;
  color: #909399;
  font-size: 30px;
  border-radius: 4px;
}
</style>
