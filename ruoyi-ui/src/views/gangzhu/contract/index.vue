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
      <el-form-item label="房间号" prop="houseNo">
        <el-input
          v-model="queryParams.houseNo"
          placeholder="请输入房间号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属项目" prop="projectId">
        <el-select v-model="queryParams.projectId" placeholder="请选择项目" clearable filterable style="width: 200px">
          <el-option
            v-for="project in projectList"
            :key="project.projectId"
            :label="project.projectName"
            :value="project.projectId"
          />
        </el-select>
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
          <el-option label="已失效" value="6" />
        </el-select>
      </el-form-item>
      <el-form-item label="配租方式" prop="allocationType">
        <el-select v-model="queryParams.allocationType" placeholder="请选择配租方式" clearable style="width: 160px">
          <el-option label="常规分配" value="常规分配" />
          <el-option label="集中分配" value="集中分配" />
        </el-select>
      </el-form-item>
      <el-form-item label="签约时间">
        <el-date-picker
          v-model="daterangeSignTime"
          style="width: 240px"
          value-format="yyyy-MM-dd"
          type="daterange"
          range-separator="-"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
        ></el-date-picker>
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
          v-hasPermi="['gangzhu:contract:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="contractList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="租户姓名" align="center" prop="tenantName" width="100" show-overflow-tooltip />
      <el-table-column label="合同编号" align="center" prop="contractNo" width="180" show-overflow-tooltip />
      <el-table-column label="房源位置" align="center" min-width="180" show-overflow-tooltip>
              <template slot-scope="scope">
                <span v-if="scope.row.projectName">{{ scope.row.projectName }}-{{ scope.row.buildingName || '' }}{{ scope.row.houseNo ? scope.row.houseNo + '号' : '' }}</span>
                <span v-else style="color: #C0C4CC;">-</span>
              </template>
      </el-table-column>
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
          <el-tag v-else-if="scope.row.contractStatus === '5'" type="info">已解约</el-tag>
          <el-tag v-else-if="scope.row.contractStatus === '6'" type="info">已失效</el-tag>
          <el-tag v-else type="info">未知</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="配租方式" align="center" prop="allocationType" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.allocationType === '集中分配'" type="warning">集中分配</el-tag>
          <el-tag v-else type="info">常规分配</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="合同生效日期" align="center" prop="startDate" width="120" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:contract:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="handleViewPdf(scope.row)"
            v-hasPermi="['gangzhu:contract:query']"
          >查看合同</el-button>
          <el-button
            v-if="scope.row.contractStatus === '2' || scope.row.contractStatus === '3'"
            size="mini"
            type="text"
            icon="el-icon-circle-close"
            style="color: #F56C6C;"
            @click="handleForceCheckout(scope.row)"
            v-hasPermi="['gangzhu:checkout:forceCheckout']"
          >退租</el-button>
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
          <el-tag v-else-if="detailForm.contractStatus === '5'" type="info">已解约</el-tag>
          <el-tag v-else-if="detailForm.contractStatus === '6'" type="info">已失效</el-tag>
          <el-tag v-else type="info">未知状态</el-tag>
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

      <!-- 未签署/已失效合同：内容、签名、附件均不展示，给出统一提示 -->
      <div class="detail-section" v-if="['0','1','6'].includes(detailForm.contractStatus)">
        <div class="section-title">
          <i class="el-icon-warning-outline"></i>
          <span>合同内容</span>
        </div>
        <div style="padding:16px;color:#909399;font-size:14px;background:#f4f4f5;border-radius:4px;">
          该合同未完成电子签署，无生效合同内容
        </div>
      </div>

      <!-- 已签署及之后状态：展示签名/附件/电子合同PDF -->
      <template v-else>
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

        <!-- 电子合同 PDF（e签宝签署后的链接） -->
        <div class="detail-section" v-if="detailForm.contractContent && detailForm.contractContent.startsWith('http')">
          <div class="section-title">
            <i class="el-icon-document-copy"></i>
            <span>电子合同</span>
          </div>
          <div class="attachment-area">
            <el-button type="primary" size="small" icon="el-icon-view"
              @click="handleViewPdf(detailForm)">查看 PDF</el-button>
            <span style="color:#999;font-size:12px;margin-left:8px;">链接实时刷新，点击后在新标签页打开</span>
          </div>
        </div>
      </template>
      </el-tab-pane>

      <!-- Tab 2：缴费记录 -->
      <el-tab-pane label="缴费记录" name="bills">
        <el-table :data="contractBills" border size="mini" style="width:100%">
          <el-table-column label="账单类型"   prop="billTypeText"   width="80" align="center" />
          <el-table-column label="账单号"     prop="billNo"         min-width="160" show-overflow-tooltip />
          <el-table-column label="账期" width="240" align="center">
            <template slot-scope="{ row }">
              <span v-if="row.billSeq">第{{ row.billSeq }}期
                <span style="color:#999;font-size:12px;">({{ formatShortDate(row.periodStartDate) }}~{{ formatShortDate(row.periodEndDate) }})</span>
              </span>
              <span v-else>{{ row.billPeriod }}</span>
            </template>
          </el-table-column>
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
          <el-table-column label="支付方式" width="90" align="center">
            <template slot-scope="{ row }">
              <span v-if="row.payMethod === '2' || row.payMethod === 'wechat'">微信支付</span>
              <span v-else>{{ row.payMethod || '-' }}</span>
            </template>
          </el-table-column>
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
                v-if="row.filePath"
                :src="getImageUrl(row.filePath)"
                style="width:80px;height:60px;cursor:pointer"
                :preview-src-list="[getImageUrl(row.filePath)]"
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

    <!-- 管理员直接退租对话框（丰富展现，支持导出PDF） -->
    <el-dialog title="管理员直接退租" :visible.sync="forceCheckoutOpen" width="900px" append-to-body>
      <el-form ref="forceCheckoutForm" :model="forceCheckoutForm" :rules="forceCheckoutRules" label-width="100px">

        <!-- 合同信息 -->
        <el-divider content-position="left">合同信息</el-divider>
        <el-row>
          <el-col :span="8">
            <el-form-item label="租户姓名">
              <span>{{ forceCheckoutForm.tenantName }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="合同编号">
              <span>{{ forceCheckoutForm.contractNo }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房源">
              <span>{{ forceCheckoutDetail.projectName || '-' }} {{ forceCheckoutDetail.houseNo || '' }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="合同期限">
              <span v-if="forceCheckoutDetail.startDate">{{ forceCheckoutDetail.startDate }} 至 {{ forceCheckoutDetail.endDate }}</span>
              <span v-else>-</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="租期">
              <span>{{ forceCheckoutDetail.rentMonths || '-' }} 个月</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="月租金">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ fmt(forceCheckoutDetail.rentPrice) }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="押金">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ fmt(forceCheckoutDetail.deposit) }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="缴费周期">
              <span>{{ formatPaymentCycle(forceCheckoutDetail.paymentCycle) }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 缴费记录 -->
        <el-divider content-position="left">缴费记录</el-divider>
        <el-table :data="forceCheckoutBills" size="small" border>
          <el-table-column label="账单编号" align="center" prop="billNo" width="150" show-overflow-tooltip />
          <el-table-column label="账单类型" align="center" prop="billTypeText" width="80" />
          <el-table-column label="账单期" align="center" prop="billPeriod" width="100" />
          <el-table-column label="账单金额" align="right" prop="billAmount" width="100">
            <template slot-scope="scope">¥{{ fmt(scope.row.billAmount) }}</template>
          </el-table-column>
          <el-table-column label="已付金额" align="right" prop="paidAmount" width="100">
            <template slot-scope="scope">¥{{ fmt(scope.row.paidAmount) }}</template>
          </el-table-column>
          <el-table-column label="未付金额" align="right" prop="unpaidAmount" width="100">
            <template slot-scope="scope">
              <span :style="{ color: scope.row.unpaidAmount > 0 ? '#f56c6c' : '#67c23a' }">¥{{ fmt(scope.row.unpaidAmount) }}</span>
            </template>
          </el-table-column>
          <el-table-column label="支付状态" align="center" prop="billStatusText" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.billStatus === '1' ? 'success' : 'warning'" size="small">{{ scope.row.billStatusText }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="支付时间" align="center" prop="payTime" width="160" />
        </el-table>
        <el-row v-if="forceCheckoutBills.length > 0" style="margin-top: 10px;">
          <el-col :span="24">
            <div style="text-align: right; padding-right: 20px;">
              <span style="margin-right: 20px;">总账单金额: <strong style="color: #409EFF;">¥{{ fmt(totalBillAmount) }}</strong></span>
              <span style="margin-right: 20px;">已付金额: <strong style="color: #67C23A;">¥{{ fmt(totalPaidAmount) }}</strong></span>
              <span>未付金额: <strong style="color: #F56C6C;">¥{{ fmt(totalUnpaidAmount) }}</strong></span>
            </div>
          </el-col>
        </el-row>

        <!-- 费用计算（仅展示/PDF，不提交后端） -->
        <el-divider content-position="left">费用计算（仅展示，不处理账务）</el-divider>
        <el-row>
          <el-col :span="8">
            <el-form-item label="水表读数">
              <el-input-number v-model="feeCalcForm.meterReadingWater" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="电表读数">
              <el-input-number v-model="feeCalcForm.meterReadingElectric" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="燃气表读数">
              <el-input-number v-model="feeCalcForm.meterReadingGas" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="水费(元)">
              <el-input-number v-model="feeCalcForm.waterFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="电费(元)">
              <el-input-number v-model="feeCalcForm.electricFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="燃气费(元)">
              <el-input-number v-model="feeCalcForm.gasFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="暖气费(元)">
              <el-input-number v-model="feeCalcForm.heatingFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="物业费(元)">
              <el-input-number v-model="feeCalcForm.propertyFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="损坏扣款(元)">
              <el-input-number v-model="feeCalcForm.damageDeduction" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="违约金(元)">
              <el-input-number v-model="feeCalcForm.penaltyAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="钥匙归还">
              <el-input-number v-model="feeCalcForm.keyReturned" :min="0" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="应退押金(元)">
              <el-input-number v-model="feeCalcForm.depositRefund" :min="0" :precision="2" controls-position="right" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="物品损坏情况">
              <el-input v-model="feeCalcForm.damageDescription" type="textarea" placeholder="请输入物品损坏情况" :rows="2" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="应退总额(元)">
              <el-input-number v-model="feeCalcForm.refundAmount" :min="0" :precision="2" controls-position="right" style="width: 200px;" />
              <span style="margin-left: 10px; color: #909399; font-size: 12px;">建议: ¥{{ fmt(calculatedRefund) }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 退租原因 -->
        <el-divider content-position="left">退租操作</el-divider>
        <el-form-item label="退租原因" prop="checkoutReason">
          <el-input v-model="forceCheckoutForm.checkoutReason" type="textarea" placeholder="请输入退租原因" :rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="forceCheckoutOpen = false">取 消</el-button>
        <el-button type="warning" plain icon="el-icon-download" @click="exportForceCheckoutPDF">导出PDF</el-button>
        <el-button type="danger" @click="submitForceCheckout">确认退租</el-button>
      </div>
    </el-dialog>

  </div>
</template>

<script>
import { listContract, getContract,
         getContractBills, getContractDocuments, auditDocument, getContractPdfUrl } from "@/api/gangzhu/contract";
import { listProject } from "@/api/gangzhu/project";
import { adminForceCheckout, getContractBills as getCheckoutBills } from "@/api/gangzhu/checkout";

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
      detailOpen: false,       // 详情弹窗
      detailForm: {},          // 详情数据
      detailActiveTab: 'info', // 详情弹窗当前 Tab
      contractBills: [],       // 缴费记录
      contractDocs: [],        // 用户资料
      currentDetailContractId: null, // 当前查看的合同 ID
      // 管理员直接退租
      forceCheckoutOpen: false,
      forceCheckoutForm: {
        contractId: null,
        tenantName: '',
        contractNo: '',
        checkoutReason: ''
      },
      forceCheckoutRules: {
        checkoutReason: [{ required: true, message: '请输入退租原因', trigger: 'blur' }]
      },
      forceCheckoutDetail: {},   // 合同详情（退租弹窗用）
      forceCheckoutBills: [],    // 账单列表（退租弹窗用）
      feeCalcForm: {             // 费用计算（仅展示/PDF，不提交后端）
        meterReadingWater: null,
        meterReadingElectric: null,
        meterReadingGas: null,
        waterFee: 0,
        electricFee: 0,
        gasFee: 0,
        heatingFee: 0,
        propertyFee: 0,
        damageDeduction: 0,
        penaltyAmount: 0,
        keyReturned: 0,
        damageDescription: '',
        depositRefund: 0,
        refundAmount: 0
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        tenantName: null,
        contractNo: null,
        houseNo: null,
        contractType: null,
        contractStatus: null,
        projectId: null,
        allocationType: null,
        params: {}
      },
      // 签约时间范围筛选 [开始日期, 结束日期]
      daterangeSignTime: [],
      projectList: [],
    };
  },
  created() {
    this.getList();
    this.getProjectList();
  },
  computed: {
    // 账单总金额
    totalBillAmount() {
      const sum = this.forceCheckoutBills.reduce((s, b) => s + Number(b.billAmount || 0), 0);
      return sum.toFixed(2);
    },
    // 已付金额
    totalPaidAmount() {
      const sum = this.forceCheckoutBills.reduce((s, b) => s + Number(b.paidAmount || 0), 0);
      return sum.toFixed(2);
    },
    // 未付金额
    totalUnpaidAmount() {
      const sum = this.forceCheckoutBills.reduce((s, b) => s + Number(b.unpaidAmount || 0), 0);
      return sum.toFixed(2);
    },
    // 建议应退总额（押金 - 各项扣费）
    calculatedRefund() {
      const deposit = Number(this.feeCalcForm.depositRefund) || 0;
      const penalty = Number(this.feeCalcForm.penaltyAmount || 0);
      const damage = Number(this.feeCalcForm.damageDeduction || 0);
      const water = Number(this.feeCalcForm.waterFee || 0);
      const electric = Number(this.feeCalcForm.electricFee || 0);
      const gas = Number(this.feeCalcForm.gasFee || 0);
      const heating = Number(this.feeCalcForm.heatingFee || 0);
      const property = Number(this.feeCalcForm.propertyFee || 0);
      const result = deposit - penalty - damage - water - electric - gas - heating - property;
      return Math.max(0, Number(result.toFixed(2)));
    }
  },
  methods: {
    /** 格式化日期为 YYYY-MM-DD */
    formatShortDate(dateStr) {
      if (!dateStr) return '';
      // 返回完整日期 YYYY-MM-DD
      return dateStr.substring(0, 10);
    },
    /** 查看合同 PDF（调接口获取实时链接，新标签打开） */
    handleViewPdf(row) {
      const contractId = row.contractId;
      this.$message({ message: '正在获取合同链接...', type: 'info', duration: 1500 });
      getContractPdfUrl(contractId).then(res => {
        if (res.code === 200 && res.data) {
          window.open(res.data, '_blank');
        } else {
          this.$message.error(res.msg || '该合同暂无电子 PDF');
        }
      }).catch(() => {
        this.$message.error('获取链接失败，请重试');
      });
    },
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
      // 把签约时间范围拼到 params 里，交给后端按 sign_time 字段过滤
      this.queryParams.params = {};
      if (this.daterangeSignTime && this.daterangeSignTime.length === 2) {
        this.queryParams.params["beginSignTime"] = this.daterangeSignTime[0];
        this.queryParams.params["endSignTime"] = this.daterangeSignTime[1];
      }
      listContract(this.queryParams).then(response => {
        this.contractList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    resetQuery() {
      this.daterangeSignTime = [];
      this.resetForm("queryForm");
      this.handleQuery();
    },
    /** 查询项目列表（筛选下拉） */
    getProjectList() {
      listProject({ pageNum: 1, pageSize: 1000, status: "0" }).then(response => {
        this.projectList = response.rows || response.data || [];
      });
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.contractId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleExport() {
      // 导出时也带上签约时间范围
      this.queryParams.params = {};
      if (this.daterangeSignTime && this.daterangeSignTime.length === 2) {
        this.queryParams.params["beginSignTime"] = this.daterangeSignTime[0];
        this.queryParams.params["endSignTime"] = this.daterangeSignTime[1];
      }
      // 勾选导出：将选中的合同 ids 传给后端
      const exportParams = { ...this.queryParams };
      if (this.ids && this.ids.length > 0) {
        exportParams.contractIds = this.ids;
      }
      this.download('system/contract/export', exportParams, `contract_${new Date().getTime()}.xlsx`)
    },
    /** 格式化金额：保留两位小数 */
    fmt(v) {
      if (v === null || v === undefined || v === '') return '0.00';
      const n = Number(v);
      if (isNaN(n)) return '0.00';
      return n.toFixed(2);
    },
    /** 格式化缴费周期 */
    formatPaymentCycle(cycle) {
      const map = { '1': '押一付一', '2': '押一付二', '3': '押一付三', '6': '半年付', '12': '年付' };
      return map[cycle] || '-';
    },
    /** 管理员直接退租 */
    handleForceCheckout(row) {
      this.forceCheckoutForm = {
        contractId: row.contractId,
        tenantName: row.tenantName,
        contractNo: row.contractNo,
        checkoutReason: ''
      };
      this.forceCheckoutDetail = {};
      this.forceCheckoutBills = [];
      this.feeCalcForm = {
        meterReadingWater: null,
        meterReadingElectric: null,
        meterReadingGas: null,
        waterFee: 0,
        electricFee: 0,
        gasFee: 0,
        heatingFee: 0,
        propertyFee: 0,
        damageDeduction: 0,
        penaltyAmount: 0,
        keyReturned: 0,
        damageDescription: '',
        depositRefund: 0,
        refundAmount: 0
      };
      // 加载合同详情
      getContract(row.contractId).then(res => {
        this.forceCheckoutDetail = res.data || {};
        // 默认应退押金 = 合同押金
        this.feeCalcForm.depositRefund = Number(this.forceCheckoutDetail.deposit) || 0;
      });
      // 加载账单列表（使用 checkout API，返回格式化字段）
      getCheckoutBills(row.contractId).then(res => {
        this.forceCheckoutBills = res.data || [];
      }).catch(() => { this.forceCheckoutBills = []; });
      this.forceCheckoutOpen = true;
    },
    submitForceCheckout() {
      this.$refs['forceCheckoutForm'].validate(valid => {
        if (!valid) return;
        this.$modal.confirm('确认对该合同执行退租操作？合同将变为"已解约"，房源将释放为"空置"。').then(() => {
          adminForceCheckout({
            contractId: this.forceCheckoutForm.contractId,
            checkoutReason: this.forceCheckoutForm.checkoutReason
          }).then(() => {
            this.$modal.msgSuccess('退租成功');
            this.forceCheckoutOpen = false;
            this.getList();
          });
        }).catch(() => {});
      });
    },
    /** 导出退租信息为PDF（通过浏览器打印另存为PDF） */
    exportForceCheckoutPDF() {
      const f = this.forceCheckoutForm;
      const d = this.forceCheckoutDetail;
      const af = this.feeCalcForm;
      const fmt = this.fmt;
      const stamp = (s) => s || '-';

      // 构建缴费记录表格行
      let billRows = '';
      if (this.forceCheckoutBills && this.forceCheckoutBills.length > 0) {
        this.forceCheckoutBills.forEach(bill => {
          billRows += `<tr>
            <td style="text-align:center">${stamp(bill.billNo)}</td>
            <td style="text-align:center">${stamp(bill.billTypeText)}</td>
            <td style="text-align:center">${stamp(bill.billPeriod)}</td>
            <td style="text-align:right">¥${fmt(bill.billAmount)}</td>
            <td style="text-align:right">¥${fmt(bill.paidAmount)}</td>
            <td style="text-align:right;color:${bill.unpaidAmount > 0 ? '#e5252b' : '#12a566'}">¥${fmt(bill.unpaidAmount)}</td>
            <td style="text-align:center">${stamp(bill.billStatusText)}</td>
            <td style="text-align:center">${stamp(bill.payTime)}</td>
          </tr>`;
        });
      } else {
        billRows = '<tr><td colspan="8" style="text-align:center;color:#999;padding:20px 0">暂无账单记录</td></tr>';
      }

      // 组装完整 HTML
      const html = `<!DOCTYPE html>
<html lang="zh-CN">
<head>
<meta charset="utf-8">
<title>退租信息-${stamp(f.contractNo)}</title>
<style>
  * { box-sizing: border-box; }
  body { font-family: "Microsoft YaHei", "PingFang SC", sans-serif; margin: 20px; color: #333; font-size: 13px; line-height: 1.6; }
  h1 { text-align: center; font-size: 20px; margin-bottom: 5px; }
  .sub-title { text-align: center; color: #999; font-size: 12px; margin-bottom: 20px; }
  h2 { font-size: 15px; border-left: 4px solid #409EFF; padding-left: 8px; margin: 18px 0 10px; }
  table { width: 100%; border-collapse: collapse; margin-bottom: 8px; }
  td, th { border: 1px solid #ddd; padding: 6px 8px; font-size: 12px; }
  th { background: #f5f7fa; font-weight: 600; }
  .info-table td:first-child { width: 120px; background: #f5f7fa; font-weight: 600; }
  .amount { color: #e5252b; font-weight: bold; }
  .total-row { background: #f5f7fa; font-weight: bold; }
  .fee-table td:first-child { width: 120px; background: #f5f7fa; font-weight: 600; }
  @media print { body { margin: 0; } .no-print { display: none; } }
</style>
</head>
<body>

<h1>退租信息汇总</h1>
<p class="sub-title">导出时间：${new Date().toLocaleString('zh-CN')}</p>

<h2>一、合同信息</h2>
<table class="info-table">
  <tr><td>租户姓名</td><td>${stamp(f.tenantName)}</td><td>合同编号</td><td>${stamp(f.contractNo)}</td></tr>
  <tr><td>项目名称</td><td>${stamp(d.projectName)}</td><td>房间号</td><td>${stamp(d.houseNo)}</td></tr>
  <tr><td>合同期限</td><td>${stamp(d.startDate)} 至 ${stamp(d.endDate)}</td><td>租期</td><td>${stamp(d.rentMonths)} 个月</td></tr>
  <tr><td>缴费周期</td><td>${this.formatPaymentCycle(d.paymentCycle)}</td><td>月租金</td><td class="amount">¥${fmt(d.rentPrice)}</td></tr>
  <tr><td>押金</td><td class="amount">¥${fmt(d.deposit)}</td><td>退租原因</td><td>${stamp(f.checkoutReason) || '(未填写)'}</td></tr>
</table>

<h2>二、缴费记录</h2>
<table>
  <thead>
    <tr>
      <th>账单编号</th><th>类型</th><th>账单期</th><th>账单金额</th><th>已付金额</th><th>未付金额</th><th>状态</th><th>支付时间</th>
    </tr>
  </thead>
  <tbody>
    ${billRows}
    <tr class="total-row">
      <td colspan="3" style="text-align:right">合计</td>
      <td style="text-align:right">¥${this.totalBillAmount}</td>
      <td style="text-align:right">¥${this.totalPaidAmount}</td>
      <td style="text-align:right">¥${this.totalUnpaidAmount}</td>
      <td colspan="2"></td>
    </tr>
  </tbody>
</table>

<h2>三、费用计算</h2>
<table class="fee-table">
  <tr><td>水表读数</td><td>${stamp(af.meterReadingWater)}</td><td>电表读数</td><td>${stamp(af.meterReadingElectric)}</td><td>燃气表读数</td><td>${stamp(af.meterReadingGas)}</td></tr>
  <tr><td>水费</td><td>¥${fmt(af.waterFee)}</td><td>电费</td><td>¥${fmt(af.electricFee)}</td><td>燃气费</td><td>¥${fmt(af.gasFee)}</td></tr>
  <tr><td>暖气费</td><td>¥${fmt(af.heatingFee)}</td><td>物业费</td><td>¥${fmt(af.propertyFee)}</td><td>损坏扣款</td><td>¥${fmt(af.damageDeduction)}</td></tr>
  <tr><td>违约金</td><td>¥${fmt(af.penaltyAmount)}</td><td>钥匙归还</td><td>${stamp(af.keyReturned)} 把</td><td>物品损坏情况</td><td>${stamp(af.damageDescription)}</td></tr>
</table>
<table class="info-table">
  <tr><td>押金</td><td class="amount">¥${fmt(d.deposit)}</td><td>应退押金</td><td style="color:#12a566;font-weight:bold">¥${fmt(af.depositRefund)}</td><td>应退总额</td><td style="font-size:16px;color:#e5252b;font-weight:bold">¥${fmt(af.refundAmount)}</td></tr>
</table>

<div class="no-print" style="margin-top:30px;text-align:center">
  <button onclick="window.print()" style="padding:8px 24px;font-size:14px;cursor:pointer">打印 / 另存为PDF</button>
</div>

</body>
</html>`;

      const printWin = window.open('', '_blank');
      if (!printWin) {
        this.$modal.msgWarning('浏览器拦截了弹出窗口，请允许弹出窗口后重试');
        return;
      }
      printWin.document.write(html);
      printWin.document.close();
      printWin.onload = () => {
        setTimeout(() => {
          printWin.focus();
          printWin.print();
        }, 300);
      };
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
