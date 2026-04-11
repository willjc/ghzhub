<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="租户姓名" prop="tenantName">
        <el-input
          v-model="queryParams.tenantName"
          placeholder="请输入租户姓名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同编号" prop="contractNo">
        <el-input
          v-model="queryParams.contractNo"
          placeholder="请输入合同编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="合同类型" prop="contractType">
        <el-select v-model="queryParams.contractType" placeholder="请选择合同类型" clearable>
          <el-option label="首次签约" value="1" />
          <el-option label="续租" value="2" />
          <el-option label="换房" value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="合同状态" prop="contractStatus">
        <el-select v-model="queryParams.contractStatus" placeholder="请选择合同状态" clearable>
          <el-option label="草稿" value="0" />
          <el-option label="待签署" value="1" />
          <el-option label="已签署" value="2" />
          <el-option label="履行中" value="3" />
          <el-option label="已到期" value="4" />
          <el-option label="已解约" value="5" />
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
          v-hasPermi="['gangzhu:contract:add']"
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
          v-hasPermi="['gangzhu:contract:edit']"
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
          v-hasPermi="['gangzhu:contract:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['gangzhu:contract:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="contractList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="租户姓名" align="center" prop="tenantName" width="100" show-overflow-tooltip />
      <el-table-column label="合同编号" align="center" prop="contractNo" width="180" show-overflow-tooltip />
      <el-table-column label="合同类型" align="center" prop="contractType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.contractType === '1'" type="success">首次签约</el-tag>
          <el-tag v-else-if="scope.row.contractType === '2'" type="primary">续租</el-tag>
          <el-tag v-else-if="scope.row.contractType === '3'" type="warning">换房</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="合同期限" align="center" min-width="190">
        <template slot-scope="scope">
          <span style="color: #409EFF;">{{ scope.row.startDate }}</span>
          <span style="color: #909399; margin: 0 5px;">至</span>
          <span style="color: #67C23A;">{{ scope.row.endDate }}</span>
        </template>
      </el-table-column>
      <el-table-column label="租期(月)" align="center" prop="rentMonths" width="100" />
      <el-table-column label="月租金(元)" align="center" prop="rentPrice" width="120" />
      <el-table-column label="押金(元)" align="center" prop="deposit" width="120" />
      <el-table-column label="缴费周期" align="center" prop="paymentCycle" width="100">
        <template slot-scope="scope">
          <span v-if="scope.row.paymentCycle === '1'">月付</span>
          <span v-else-if="scope.row.paymentCycle === '2'">季付</span>
          <span v-else-if="scope.row.paymentCycle === '3'">半年付</span>
          <span v-else-if="scope.row.paymentCycle === '4'">年付</span>
        </template>
      </el-table-column>
      <el-table-column label="合同状态" align="center" prop="contractStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.contractStatus === '0'" type="info">草稿</el-tag>
          <el-tag v-else-if="scope.row.contractStatus === '1'" type="warning">待签署</el-tag>
          <el-tag v-else-if="scope.row.contractStatus === '2'" type="primary">已签署</el-tag>
          <el-tag v-else-if="scope.row.contractStatus === '3'" type="success">履行中</el-tag>
          <el-tag v-else-if="scope.row.contractStatus === '4'" type="danger">已到期</el-tag>
          <el-tag v-else type="info">已解约</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="合同生效日期" align="center" prop="startDate" width="120" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="260">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:contract:query']"
          >详情</el-button>
          <el-button
            v-if="scope.row.contractStatus === '0'"
            size="mini"
            type="text"
            icon="el-icon-check"
            @click="handleApprove(scope.row)"
            v-hasPermi="['gangzhu:contract:edit']"
          >审核</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['gangzhu:contract:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:contract:remove']"
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

    <!-- 添加或修改合同对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="1100px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="110px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="合同编号" prop="contractNo">
              <el-input v-model="form.contractNo" placeholder="自动生成" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="合同类型" prop="contractType">
              <el-select v-model="form.contractType" placeholder="请选择合同类型">
                <el-option label="首次签约" value="1" />
                <el-option label="续租" value="2" />
                <el-option label="换房" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="租户ID" prop="tenantId">
              <el-input-number v-model="form.tenantId" controls-position="right" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房源ID" prop="houseId">
              <el-input-number v-model="form.houseId" controls-position="right" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="项目ID" prop="projectId">
              <el-input-number v-model="form.projectId" controls-position="right" :min="1" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租期(月)" prop="rentMonths">
              <el-input-number v-model="form.rentMonths" controls-position="right" :min="1" :max="120" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="form.startDate"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="form.endDate"
                type="date"
                placeholder="选择日期"
                value-format="yyyy-MM-dd"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="月租金(元)" prop="rentPrice">
              <el-input-number v-model="form.rentPrice" controls-position="right" :min="0" :precision="2" :step="100" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="押金(元)" prop="deposit">
              <el-input-number v-model="form.deposit" controls-position="right" :min="0" :precision="2" :step="100" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="缴费周期" prop="paymentCycle">
              <el-select v-model="form.paymentCycle" placeholder="请选择缴费周期" style="width: 100%">
                <el-option label="月付" value="1" />
                <el-option label="季付" value="2" />
                <el-option label="半年付" value="3" />
                <el-option label="年付" value="4" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="租金支付日" prop="paymentDay">
              <el-input-number v-model="form.paymentDay" controls-position="right" :min="1" :max="31" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="合同状态" prop="contractStatus">
          <el-select v-model="form.contractStatus" placeholder="请选择合同状态" style="width: 100%">
            <el-option label="草稿" value="0" />
            <el-option label="待签署" value="1" />
            <el-option label="已签署" value="2" />
            <el-option label="履行中" value="3" />
            <el-option label="已到期" value="4" />
            <el-option label="已解约" value="5" />
          </el-select>
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

    <!-- 合同详情对话框 -->
    <el-dialog title="合同详情" :visible.sync="detailOpen" width="1100px" append-to-body>
      <el-tabs v-model="detailActiveTab" type="border-card">

      <!-- Tab 1：合同信息 -->
      <el-tab-pane label="合同信息" name="info">
      <!-- 标题区域 -->
      <div class="contract-detail-header">
        <div class="contract-no">
          <span class="label">合同编号：</span>
          <span class="value">{{ detailForm.contractNo }}</span>
        </div>
        <div class="contract-status">
          <el-tag v-if="detailForm.contractStatus === '0'" type="info">草稿</el-tag>
          <el-tag v-else-if="detailForm.contractStatus === '1'" type="warning">待签署</el-tag>
          <el-tag v-else-if="detailForm.contractStatus === '2'" type="primary">已签署</el-tag>
          <el-tag v-else-if="detailForm.contractStatus === '3'" type="success">履行中</el-tag>
          <el-tag v-else-if="detailForm.contractStatus === '4'" type="danger">已到期</el-tag>
          <el-tag v-else type="info">已解约</el-tag>
        </div>
      </div>

      <!-- 租期卡片 -->
      <div class="detail-section">
        <div class="section-title">
          <i class="el-icon-date"></i>
          <span>租赁期限</span>
        </div>
        <div class="lease-period">
          <div class="period-item">
            <span class="period-label">合同生效日期</span>
            <span class="period-value start">{{ detailForm.startDate }}</span>
          </div>
          <div class="period-arrow">→</div>
          <div class="period-item">
            <span class="period-label">合同结束日期</span>
            <span class="period-value end">{{ detailForm.endDate }}</span>
          </div>
          <div class="period-item">
            <span class="period-label">租期</span>
            <span class="period-value months">{{ detailForm.rentMonths }}个月</span>
          </div>
        </div>
      </div>

      <!-- 租户信息 -->
      <div class="detail-section">
        <div class="section-title">
          <i class="el-icon-user"></i>
          <span>租户信息</span>
        </div>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">姓名</span>
            <span class="info-value">{{ detailForm.tenantName }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">身份证号</span>
            <span class="info-value">{{ detailForm.tenantIdCard }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">联系电话</span>
            <span class="info-value">{{ detailForm.tenantPhone }}</span>
          </div>
        </div>
      </div>

      <!-- 房源信息 -->
      <div class="detail-section">
        <div class="section-title">
          <i class="el-icon-house"></i>
          <span>房源信息</span>
        </div>
        <div class="info-grid">
          <div class="info-item">
            <span class="info-label">项目名称</span>
            <span class="info-value">{{ detailForm.projectName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">楼栋</span>
            <span class="info-value">{{ detailForm.buildingName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">单元</span>
            <span class="info-value">{{ detailForm.unitName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">房间号</span>
            <span class="info-value">{{ detailForm.houseNo || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">楼层</span>
            <span class="info-value">{{ detailForm.floor ? detailForm.floor + '层' : '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">朝向</span>
            <span class="info-value">{{ detailForm.orientation || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">户型</span>
            <span class="info-value">{{ detailForm.houseTypeName || '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">面积</span>
            <span class="info-value">{{ detailForm.area ? detailForm.area + '㎡' : '-' }}</span>
          </div>
          <div class="info-item">
            <span class="info-label">装修</span>
            <span class="info-value">{{ detailForm.decoration || '-' }}</span>
          </div>
          <div class="info-item full">
            <span class="info-label">房间设施</span>
            <div class="info-value facilities">
              <template v-if="detailForm.facilities">
                <el-tag
                  v-for="(facility, index) in detailForm.facilities.split(',')"
                  :key="index"
                  size="small"
                  type="success"
                  effect="plain">
                  {{ facility }}
                </el-tag>
              </template>
              <span v-else>-</span>
            </div>
          </div>
          <div class="info-item full">
            <span class="info-label">房源地址</span>
            <span class="info-value">{{ detailForm.houseAddress || '-' }}</span>
          </div>
        </div>
      </div>

      <!-- 租金信息 -->
      <div class="detail-section">
        <div class="section-title">
          <i class="el-icon-money"></i>
          <span>租金信息</span>
        </div>
        <div class="rent-info">
          <div class="rent-item">
            <span class="rent-label">月租金</span>
            <span class="rent-value price">¥{{ detailForm.rentPrice }}</span>
          </div>
          <div class="rent-item">
            <span class="rent-label">押金</span>
            <span class="rent-value price">¥{{ detailForm.deposit }}</span>
          </div>
          <div class="rent-item">
            <span class="rent-label">缴费周期</span>
            <span class="rent-value">
              <span v-if="detailForm.paymentCycle === '1'">月付</span>
              <span v-else-if="detailForm.paymentCycle === '2'">季付</span>
              <span v-else-if="detailForm.paymentCycle === '3'">半年付</span>
              <span v-else-if="detailForm.paymentCycle === '4'">年付</span>
            </span>
          </div>
          <div class="rent-item">
            <span class="rent-label">支付日</span>
            <span class="rent-value">每月{{ detailForm.paymentDay }}日</span>
          </div>
        </div>
      </div>

      <!-- 签名区域 -->
      <div class="detail-section" v-if="detailForm.tenantSignature">
        <div class="section-title">
          <i class="el-icon-edit"></i>
          <span>租户签名</span>
        </div>
        <div class="signature-area">
          <img :src="getImageUrl(detailForm.tenantSignature)" alt="租户签名" class="signature-img" />
        </div>
      </div>

      <!-- 合同附件 -->
      <div class="detail-section" v-if="detailForm.contractFile">
        <div class="section-title">
          <i class="el-icon-document"></i>
          <span>合同附件</span>
        </div>
        <div class="attachment-area">
          <el-tag type="info" size="small">{{ getFileName(detailForm.contractFile) }}</el-tag>
          <el-button type="primary" size="small" icon="el-icon-download" @click="downloadContract(detailForm.contractFile)">下载合同</el-button>
        </div>
      </div>

      <!-- 合同内容 -->
      <div class="detail-section" v-if="detailForm.contractContent">
        <div class="section-title">
          <i class="el-icon-document-copy"></i>
          <span>合同内容</span>
        </div>
        <div class="contract-content" v-html="detailForm.contractContent"></div>
      </div>
      </el-tab-pane>

      <!-- Tab 2：缴费记录 -->
      <el-tab-pane label="缴费记录" name="bills">
        <el-table :data="contractBills" border size="mini" style="width:100%">
          <el-table-column label="账单类型"   prop="billTypeText"   width="80" align="center" />
          <el-table-column label="账单号"     prop="billNo"         min-width="160" show-overflow-tooltip />
          <el-table-column label="账期"       prop="billPeriod"     width="90"  align="center" />
          <el-table-column label="账单金额"   prop="billAmount"     width="100" align="right" />
          <el-table-column label="已缴金额"   prop="paidAmount"     width="100" align="right" />
          <el-table-column label="状态"       width="90"            align="center">
            <template slot-scope="{ row }">
              <el-tag
                :type="row.billStatus==='1'?'success':(row.billStatus==='3'?'danger':'warning')"
                size="mini">{{ row.billStatusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="支付时间"   prop="payTime"        width="160" />
          <el-table-column label="支付方式"   prop="payMethod"      width="80"  align="center" />
          <el-table-column label="微信订单号" prop="transactionNo"  min-width="200" show-overflow-tooltip />
          <el-table-column label="到期日"     prop="dueDate"        width="100" align="center" />
        </el-table>
        <div v-if="contractBills.length === 0" style="text-align:center;color:#999;padding:30px 0;">暂无缴费记录</div>
      </el-tab-pane>

      <!-- Tab 3：用户资料 -->
      <el-tab-pane label="用户资料" name="docs">
        <el-table :data="contractDocs" border size="mini" style="width:100%">
          <el-table-column label="资料类型" width="100" align="center">
            <template slot-scope="{ row }">{{ docTypeText(row.documentType) }}</template>
          </el-table-column>
          <el-table-column label="图片" width="120" align="center">
            <template slot-scope="{ row }">
              <el-image
                v-if="row.documentUrl"
                :src="getImageUrl(row.documentUrl)"
                style="width:80px;height:60px;cursor:pointer"
                :preview-src-list="[getImageUrl(row.documentUrl)]"
                fit="cover"
              />
              <span v-else style="color:#ccc">未上传</span>
            </template>
          </el-table-column>
          <el-table-column label="审核状态" width="90" align="center">
            <template slot-scope="{ row }">
              <el-tag
                :type="row.auditStatus==='1'?'success':(row.auditStatus==='2'?'danger':'warning')"
                size="mini">
                {{ row.auditStatus==='0'?'待审核':(row.auditStatus==='1'?'已通过':'已拒绝') }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="上传时间" prop="createTime" width="160" />
          <el-table-column label="审核意见" prop="auditOpinion" min-width="120" show-overflow-tooltip />
          <el-table-column label="操作" width="160" align="center" fixed="right">
            <template slot-scope="{ row }">
              <el-button
                v-if="row.auditStatus === '0'"
                size="mini" type="success"
                @click="auditDoc(row, '1')"
              >通过</el-button>
              <el-button
                v-if="row.auditStatus === '0'"
                size="mini" type="danger"
                @click="auditDoc(row, '2')"
              >拒绝</el-button>
            </template>
          </el-table-column>
        </el-table>
        <div v-if="contractDocs.length === 0" style="text-align:center;color:#999;padding:30px 0;">暂无上传资料</div>
      </el-tab-pane>

      </el-tabs>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 审核合同对话框 -->
    <el-dialog title="审核合同" :visible.sync="approveOpen" width="800px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" :rules="approveRules" label-width="110px">
        <el-form-item label="合同编号">
          <el-input v-model="approveForm.contractNo" disabled />
        </el-form-item>
        <el-form-item label="租户信息">
          <el-input :value="approveForm.tenantName + ' - ' + approveForm.tenantPhone" disabled />
        </el-form-item>
        <el-form-item label="房源信息">
          <el-input v-model="approveForm.houseAddress" disabled />
        </el-form-item>
        <el-form-item label="租期">
          <el-input :value="approveForm.startDate + ' 至 ' + approveForm.endDate" disabled />
        </el-form-item>
        <el-form-item label="租金/押金">
          <el-input :value="'租金: ' + approveForm.rentPrice + '元/月  押金: ' + approveForm.deposit + '元'" disabled />
        </el-form-item>
        <el-form-item label="合同附件" prop="contractFile">
          <file-upload
            v-model="approveForm.contractFile"
            :limit="1"
            :fileType="['pdf', 'doc', 'docx']"
          />
          <div style="color: #999; font-size: 12px; margin-top: 5px;">请上传纸质合同扫描件或PDF文件</div>
        </el-form-item>
        <el-form-item label="审核备注" prop="remark">
          <el-input v-model="approveForm.remark" type="textarea" placeholder="请输入审核备注" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitApprove">审核通过</el-button>
        <el-button type="danger" @click="handleReject">审核拒绝</el-button>
        <el-button @click="approveOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listContract, getContract, addContract, updateContract, delContract, approveContract,
         getContractBills, getContractDocuments, auditDocument } from "@/api/gangzhu/contract";

export default {
  name: "Contract",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      contractList: [],
      title: "",
      open: false,
      detailOpen: false,       // 详情弹窗
      detailForm: {},          // 详情数据
      detailActiveTab: 'info', // 详情弹窗当前 Tab
      contractBills: [],       // 缴费记录
      contractDocs: [],        // 用户资料
      currentDetailContractId: null, // 当前查看的合同 ID
      approveOpen: false, // 审核弹窗
      approveForm: {},    // 审核表单
      approveRules: {
        contractFile: [
          { required: true, message: "请上传合同附件", trigger: "change" }
        ]
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tenantName: null,
        contractNo: null,
        contractType: null,
        contractStatus: null,
      },
      form: {},
      rules: {
        contractType: [
          { required: true, message: "合同类型不能为空", trigger: "change" }
        ],
        tenantId: [
          { required: true, message: "租户ID不能为空", trigger: "blur" }
        ],
        houseId: [
          { required: true, message: "房源ID不能为空", trigger: "blur" }
        ],
        projectId: [
          { required: true, message: "项目ID不能为空", trigger: "blur" }
        ],
        startDate: [
          { required: true, message: "开始日期不能为空", trigger: "change" }
        ],
        endDate: [
          { required: true, message: "结束日期不能为空", trigger: "change" }
        ],
        rentMonths: [
          { required: true, message: "租期不能为空", trigger: "blur" }
        ],
        rentPrice: [
          { required: true, message: "月租金不能为空", trigger: "blur" }
        ],
        deposit: [
          { required: true, message: "押金不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查看合同详情 */
    handleDetail(row) {
      const contractId = row.contractId;
      this.currentDetailContractId = contractId;
      this.detailActiveTab = 'info';
      this.contractBills = [];
      this.contractDocs = [];
      getContract(contractId).then(response => {
        this.detailForm = response.data;
        this.detailOpen = true;
      });
      // 并发拉取账单和资料
      getContractBills(contractId).then(res => {
        this.contractBills = (res.data && res.data.bills) ? res.data.bills : [];
      }).catch(() => {});
      getContractDocuments(contractId).then(res => {
        this.contractDocs = Array.isArray(res.data) ? res.data : [];
      }).catch(() => {});
    },
    /** 资料类型文字 */
    docTypeText(type) {
      const map = { '1': '身份证', '2': '学历证明', '3': '工作证明', '4': '收入证明', '5': '人才证书' };
      return map[type] || '其他';
    },
    /** 审核用户资料 */
    auditDoc(doc, status) {
      const doAudit = (opinion) => {
        auditDocument({
          documentId: doc.documentId,
          auditStatus: status,
          auditOpinion: opinion || '审核通过'
        }).then(res => {
          if (res.code === 200) {
            this.$message.success('审核完成');
            // 刷新资料列表
            getContractDocuments(this.currentDetailContractId).then(r => {
              this.contractDocs = Array.isArray(r.data) ? r.data : [];
            });
          } else {
            this.$message.error(res.msg || '审核失败');
          }
        });
      };
      if (status === '2') {
        this.$prompt('请输入拒绝原因', '审核拒绝', {
          inputPattern: /.+/,
          inputErrorMessage: '原因不能为空'
        }).then(({ value }) => doAudit(value)).catch(() => {});
      } else {
        doAudit('');
      }
    },
    /** 审核合同 */
    handleApprove(row) {
      const contractId = row.contractId;
      getContract(contractId).then(response => {
        this.approveForm = response.data;
        this.approveOpen = true;
      });
    },
    /** 提交审核 */
    submitApprove() {
      this.$refs["approveForm"].validate(valid => {
        if (valid) {
          const approveData = {
            contractId: this.approveForm.contractId,
            contractFile: this.approveForm.contractFile,
            remark: this.approveForm.remark
          };

          approveContract(approveData).then(response => {
            this.$modal.msgSuccess("审核成功");
            this.approveOpen = false;
            this.getList();
          });
        }
      });
    },
    /** 拒绝审核 */
    handleReject() {
      this.$modal.confirm('审核拒绝后将删除该草稿合同,是否确认?').then(() => {
        return delContract(this.approveForm.contractId);
      }).then(() => {
        this.$modal.msgSuccess("已拒绝并删除该合同");
        this.approveOpen = false;
        this.getList();
      }).catch(() => {});
    },
    /** 获取图片完整URL - 遵循RuoYi标准 */
    getImageUrl(url) {
      if (!url) return '';

      // 外部链接(http/https开头),直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }

      // RuoYi标准:数据库存储相对路径,前端拼接baseUrl
      const baseUrl = process.env.VUE_APP_BASE_API;  // /dev-api

      // 如果已包含baseUrl,直接返回
      if (url.indexOf(baseUrl) !== -1) {
        return url;
      }

      // 拼接baseUrl + 相对路径
      return baseUrl + (url.startsWith('/') ? url : '/' + url);
    },
    /** 获取文件名 */
    getFileName(url) {
      if (!url) return '';
      const parts = url.split('/');
      return parts[parts.length - 1];
    },
    /** 下载合同附件 */
    downloadContract(url) {
      if (!url) return;
      const downloadUrl = this.getImageUrl(url);
      window.open(downloadUrl, '_blank');
    },
    getList() {
      this.loading = true;
      listContract(this.queryParams).then(response => {
        this.contractList = response.rows;
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
        contractId: null,
        contractNo: null,
        contractType: null,
        tenantId: null,
        houseId: null,
        projectId: null,
        templateId: null,
        startDate: null,
        endDate: null,
        leaseTerm: null,
        monthlyRent: null,
        deposit: null,
        paymentCycle: "1",
        paymentDay: 1,
        waterPrice: null,
        electricityPrice: null,
        gasPrice: null,
        propertyFee: null,
        contractStatus: "0",
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
      this.ids = selection.map(item => item.contractId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加合同";
    },
    handleUpdate(row) {
      this.reset();
      const contractId = row.contractId || this.ids
      getContract(contractId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改合同";
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.contractId != null) {
            updateContract(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addContract(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    handleDelete(row) {
      const contractIds = row.contractId || this.ids;
      this.$modal.confirm('是否确认删除合同编号为"' + contractIds + '"的数据项?').then(function() {
        return delContract(contractIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleExport() {
      this.download('system/contract/export', {
        ...this.queryParams
      }, `contract_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>

<style scoped>
/* 合同详情页样式 */
.contract-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #EBEEF5;
  margin-bottom: 20px;
}

.contract-detail-header .contract-no {
  font-size: 16px;
  font-weight: 500;
}

.contract-detail-header .contract-no .label {
  color: #909399;
  margin-right: 8px;
}

.contract-detail-header .contract-no .value {
  color: #303133;
  font-family: 'Courier New', monospace;
}

.detail-section {
  margin-bottom: 24px;
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 15px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #EBEEF5;
}

.section-title i {
  margin-right: 6px;
  color: #409EFF;
}

/* 租期显示 */
.lease-period {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e8eef5 100%);
  border-radius: 8px;
}

.period-item {
  text-align: center;
  flex: 1;
}

.period-label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.period-value {
  display: block;
  font-size: 18px;
  font-weight: 600;
}

.period-value.start {
  color: #409EFF;
}

.period-value.end {
  color: #67C23A;
}

.period-value.months {
  color: #E6A23C;
}

.period-arrow {
  font-size: 24px;
  color: #DCDFE6;
  margin: 0 16px;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  padding: 12px;
  background: #F5F7FA;
  border-radius: 6px;
}

.info-item.full {
  grid-column: 1 / -1;
}

.info-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 6px;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

/* 房间设施标签 */
.info-value.facilities {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

/* 租金信息 */
.rent-info {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
}

.rent-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 16px;
  background: linear-gradient(135deg, #fff9f0 0%, #fff5e6 100%);
  border-radius: 8px;
  border: 1px solid #FFE4B5;
}

.rent-label {
  font-size: 12px;
  color: #909399;
  margin-bottom: 8px;
}

.rent-value {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.rent-value.price {
  color: #F56C6C;
  font-size: 20px;
}

/* 签名区域 */
.signature-area {
  padding: 20px;
  background: #F5F7FA;
  border-radius: 8px;
  text-align: center;
}

.signature-img {
  max-width: 300px;
  max-height: 150px;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  padding: 10px;
  background: #FFFFFF;
}

/* 附件区域 */
.attachment-area {
  display: flex;
  align-items: center;
  gap: 12px;
}

/* 合同内容 */
.contract-content {
  max-height: 400px;
  overflow-y: auto;
  padding: 20px;
  background: #F5F7FA;
  border-radius: 8px;
  border: 1px solid #EBEEF5;
  line-height: 1.8;
  font-size: 14px;
}
</style>
