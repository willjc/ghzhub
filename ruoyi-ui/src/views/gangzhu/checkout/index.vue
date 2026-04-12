<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="申请ID" prop="applyId">
        <el-input
          v-model="queryParams.applyId"
          placeholder="请输入申请ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="申请状态" prop="applyStatus">
        <el-select v-model="queryParams.applyStatus" placeholder="请选择申请状态" clearable>
          <el-option label="审批中" value="0" />
          <el-option label="待确认" value="4" />
          <el-option label="审批驳回" value="2" />
          <el-option label="已取消" value="3" />
          <el-option label="已确认" value="5" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="checkOutList" @selection-change="handleSelectionChange">
      <el-table-column label="申请ID" align="center" prop="applyId" width="80" />
      <el-table-column label="合同编号" align="center" prop="contractNo" width="150" show-overflow-tooltip />
      <el-table-column label="房源" align="center" min-width="150">
        <template slot-scope="scope">
          {{ scope.row.houseCode }} / {{ scope.row.houseNo }}
        </template>
      </el-table-column>
      <el-table-column label="退租原因" align="center" prop="checkoutReason" show-overflow-tooltip min-width="120" />
      <el-table-column label="计划退租日期" align="center" prop="planCheckoutDate" width="110" />
      <el-table-column label="合同期限" align="center" min-width="150">
        <template slot-scope="scope">
          {{ scope.row.startDate }} 至 {{ scope.row.endDate }}
        </template>
      </el-table-column>
      <el-table-column label="租期/缴费周期" align="center" min-width="120">
        <template slot-scope="scope">
          {{ scope.row.rentMonths }}个月 / {{ scope.row.paymentCycleText }}
        </template>
      </el-table-column>
      <el-table-column label="月租金/押金" align="center" min-width="120">
        <template slot-scope="scope">
          ¥{{ scope.row.rentPrice }} / ¥{{ scope.row.deposit }}
        </template>
      </el-table-column>
      <el-table-column label="房间设施" align="center" prop="facilities" show-overflow-tooltip min-width="150" />
      <el-table-column label="申请时间" align="center" prop="applyTime" width="160" />
      <el-table-column label="申请状态" align="center" prop="applyStatus" width="90">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.applyStatus === '0'" type="warning">审批中</el-tag>
          <el-tag v-else-if="scope.row.applyStatus === '4'" type="primary">待确认</el-tag>
          <el-tag v-else-if="scope.row.applyStatus === '2'" type="danger">审批驳回</el-tag>
          <el-tag v-else-if="scope.row.applyStatus === '3'" type="info">已取消</el-tag>
          <el-tag v-else-if="scope.row.applyStatus === '5'" type="success">已确认</el-tag>
          <el-tag v-else type="info">{{ scope.row.applyStatus }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240">
        <template slot-scope="scope">
          <!-- 审批中状态：显示审批按钮 -->
          <el-button
            v-if="scope.row.applyStatus === '0'"
            size="mini"
            type="text"
            icon="el-icon-s-check"
            @click="handleApprove(scope.row)"
            v-hasPermi="['gangzhu:checkout:approve']"
          >审批</el-button>

          <!-- 所有状态都可以查看详情 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['gangzhu:checkout:query']"
          >详情</el-button>

          <!-- 删除按钮 -->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['gangzhu:checkout:remove']"
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

    <!-- 审批对��框 -->
    <el-dialog title="退租审批" :visible.sync="approveOpen" width="900px" append-to-body>
      <el-form ref="approveForm" :model="approveForm" label-width="100px">
        <!-- 基本信息 -->
        <el-divider content-position="left">申请信息</el-divider>
        <el-row>
          <el-col :span="8">
            <el-form-item label="申请ID">
              <span>{{ currentForm.applyId }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="合同编号">
              <span>{{ currentForm.contractNo }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="房源">
              <span>{{ currentForm.houseCode }} / {{ currentForm.houseNo }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="24">
            <el-form-item label="退租原因">
              <span>{{ currentForm.checkoutReason }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="12">
            <el-form-item label="计划退租日期">
              <span>{{ currentForm.planCheckoutDate || '未填写' }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="申请时间">
              <span>{{ currentForm.applyTime }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row v-if="currentForm.tenantSignature">
          <el-col :span="24">
            <el-form-item label="租户签名">
              <el-image
                style="width: 200px; height: 100px; border: 1px solid #dcdfe6; border-radius: 4px;"
                :src="getImageUrl(currentForm.tenantSignature)"
                :preview-src-list="[getImageUrl(currentForm.tenantSignature)]"
                fit="contain"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 合同信息 -->
        <el-divider content-position="left">合同信息</el-divider>
        <el-row>
          <el-col :span="8">
            <el-form-item label="合同期限">
              <span>{{ currentForm.startDate }} 至 {{ currentForm.endDate }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="租期">
              <span>{{ currentForm.rentMonths }} 个月</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="缴费周期">
              <span>{{ currentForm.paymentCycleText }}</span>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="月租金">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ currentForm.rentPrice }}</span>
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="押金">
              <span style="color: #f56c6c; font-weight: bold;">¥{{ currentForm.deposit }}</span>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 缴费记录 -->
        <el-divider content-position="left">缴费记录</el-divider>
        <el-table :data="billList" size="small" border>
          <el-table-column label="账单编号" align="center" prop="billNo" width="150" show-overflow-tooltip />
          <el-table-column label="账单类型" align="center" prop="billTypeText" width="80" />
          <el-table-column label="账单期" align="center" prop="billPeriod" width="100" />
          <el-table-column label="账单金额" align="right" prop="billAmount" width="100">
            <template slot-scope="scope">
              ¥{{ scope.row.billAmount }}
            </template>
          </el-table-column>
          <el-table-column label="已付金额" align="right" prop="paidAmount" width="100">
            <template slot-scope="scope">
              ¥{{ scope.row.paidAmount }}
            </template>
          </el-table-column>
          <el-table-column label="未付金额" align="right" prop="unpaidAmount" width="100">
            <template slot-scope="scope">
              <span :style="{ color: scope.row.unpaidAmount > 0 ? '#f56c6c' : '#67c23a' }">
                ¥{{ scope.row.unpaidAmount }}
              </span>
            </template>
          </el-table-column>
          <el-table-column label="支付状态" align="center" prop="billStatusText" width="80">
            <template slot-scope="scope">
              <el-tag :type="scope.row.billStatus === '1' ? 'success' : 'warning'" size="small">
                {{ scope.row.billStatusText }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="支付时间" align="center" prop="payTime" width="160" />
        </el-table>
        <el-row v-if="billList.length > 0" style="margin-top: 10px;">
          <el-col :span="24">
            <div style="text-align: right; padding-right: 20px;">
              <span style="margin-right: 20px;">总账单金额: <strong style="color: #409EFF;">¥{{ totalBillAmount }}</strong></span>
              <span style="margin-right: 20px;">已付金额: <strong style="color: #67C23A;">¥{{ totalPaidAmount }}</strong></span>
              <span>未付金额: <strong style="color: #F56C6C;">¥{{ totalUnpaidAmount }}</strong></span>
            </div>
          </el-col>
        </el-row>

        <!-- 房源信息 -->
        <el-divider content-position="left">房源信息</el-divider>
        <el-row>
          <el-col :span="24">
            <el-form-item label="房间设施">
              <el-tag v-if="!currentForm.facilities || currentForm.facilities === ''" type="info">无</el-tag>
              <div v-else class="facilities-status-list">
                <div v-for="(facility, index) in facilitiesList" :key="index" class="facility-item">
                  <el-tag :type="facility.status === 'good' ? 'success' : 'danger'" class="facility-tag">
                    {{ facility.name }}
                  </el-tag>
                  <el-radio-group v-model="facility.status" size="small" class="facility-status">
                    <el-radio label="good">完好</el-radio>
                    <el-radio label="damaged">损坏</el-radio>
                  </el-radio-group>
                </div>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <!-- 审批意见 -->
        <el-divider content-position="left">审批操作</el-divider>
        <el-form-item label="审批意见" required>
          <el-input v-model="approveForm.approveOpinion" type="textarea" placeholder="请输入审批意见" :rows="2" />
        </el-form-item>

        <!-- 显示费用计算按钮 -->
        <el-form-item>
          <el-button type="primary" plain size="small" @click="approveType = approveType === 'pass' ? 'view' : 'pass'">
            <i :class="approveType === 'pass' ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
            {{ approveType === 'pass' ? '隐藏费用计算' : '显示费用计算（通过时必填）' }}
          </el-button>
        </el-form-item>

        <!-- 费用计算（通过时需要填写） -->
        <template v-if="approveType === 'pass'">
          <el-divider content-position="left">费用计算</el-divider>
          <el-row>
            <el-col :span="8">
              <el-form-item label="水表读数">
                <el-input-number v-model="approveForm.meterReadingWater" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="电表读数">
                <el-input-number v-model="approveForm.meterReadingElectric" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="燃气表读数">
                <el-input-number v-model="approveForm.meterReadingGas" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="水费(元)">
                <el-input-number v-model="approveForm.waterFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="电费(元)">
                <el-input-number v-model="approveForm.electricFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="燃气费(元)">
                <el-input-number v-model="approveForm.gasFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="暖气费(元)">
                <el-input-number v-model="approveForm.heatingFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="物业费(元)">
                <el-input-number v-model="approveForm.propertyFee" :min="0" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="损坏扣款(元)">
                <el-input-number v-model="approveForm.damageDeduction" :min="0" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="违约金(元)">
                <el-input-number v-model="approveForm.penaltyAmount" :min="0" :precision="2" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="钥匙归还">
                <el-input-number v-model="approveForm.keyReturned" :min="0" controls-position="right" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="物品损坏情况">
                <el-input v-model="approveForm.damageDescription" type="textarea" placeholder="请输入物品损坏情况" :rows="2" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="8">
              <el-form-item label="押金">
                <span style="font-weight: bold; color: #409EFF;">¥{{ currentForm.deposit || 0 }}</span>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="应退租金">
                <span style="color: #67C23A; font-weight: bold;">¥{{ refundableRent }}</span>
                <el-tooltip content="从已支付的租金账单中统计，表示未使用月份应退还的租金" placement="top">
                  <i class="el-icon-question" style="margin-left: 5px; color: #909399;"></i>
                </el-tooltip>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="应退总额(元)">
                <el-input-number
                  v-model="approveForm.refundAmount"
                  :precision="2"
                  :placeholder="'建议: ¥' + calculatedRefund"
                  controls-position="right"
                  style="width: 100%;"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="24">
              <el-form-item label="计算说明">
                <div style="color: #909399; font-size: 12px; line-height: 1.8;">
                  <p>• 应退总额 = 押金 + 应退租金 - 违约金 - 损坏扣款 - 水费 - 电费 - 燃气费 - 暖气费 - 物业费 - 未付账单</p>
                  <p>• 应退租金: 从已支付的租金账单中统计（当前已付租金 ¥{{ refundableRent }}）</p>
                  <p>• 建议金额: <span style="color: #67C23A; font-weight: bold;">¥{{ calculatedRefund }}</span>（可直接编辑上方输入框调整金额）</p>
                </div>
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="approveOpen = false">取 消</el-button>
        <el-button type="success" @click="submitApprovePass">通过</el-button>
        <el-button type="danger" @click="submitApproveReject">拒绝</el-button>
      </div>
    </el-dialog>

    <!-- 拒绝对话框（已废弃，保留以防兼容性问题） -->
    <!-- <el-dialog title="拒绝退租申请" :visible.sync="rejectOpen" width="500px" append-to-body>
      <el-form ref="rejectForm" :model="rejectForm" label-width="80px">
        <el-form-item label="拒绝原因" required>
          <el-input v-model="rejectForm.rejectReason" type="textarea" placeholder="请输入拒绝原因" :rows="4" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="rejectOpen = false">取 消</el-button>
        <el-button type="danger" @click="submitReject">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="退租详情" :visible.sync="detailOpen" width="1000px" append-to-body>
      <!-- 基本信息 -->
      <el-divider content-position="left">申请信息</el-divider>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="申请ID">{{ detailForm.applyId }}</el-descriptions-item>
        <el-descriptions-item label="合同编号">{{ detailForm.contractNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="房源">
          <span v-if="detailForm.houseCode || detailForm.houseNo">
            {{ detailForm.houseCode }} / {{ detailForm.houseNo }}
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="退租原因">{{ detailForm.checkoutReason }}</el-descriptions-item>
        <el-descriptions-item label="计划退租日期">{{ detailForm.planCheckoutDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="申请时间">{{ detailForm.applyTime }}</el-descriptions-item>
        <el-descriptions-item label="申请状态">
          <el-tag v-if="detailForm.applyStatus === '0'" type="warning">审批中</el-tag>
          <el-tag v-else-if="detailForm.applyStatus === '4'" type="primary">待确认</el-tag>
          <el-tag v-else-if="detailForm.applyStatus === '2'" type="danger">审批驳回</el-tag>
          <el-tag v-else-if="detailForm.applyStatus === '3'" type="info">已取消</el-tag>
          <el-tag v-else-if="detailForm.applyStatus === '5'" type="success">已完成</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="审批人">{{ detailForm.approveBy || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批时间">{{ detailForm.approveTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="审批意见" :span="2">{{ detailForm.approveOpinion || '-' }}</el-descriptions-item>
      </el-descriptions>

      <!-- 合同信息 -->
      <el-divider content-position="left">合同信息</el-divider>
      <el-descriptions :column="3" border v-if="detailForm.startDate">
        <el-descriptions-item label="合同编号">{{ detailForm.contractNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="合同期限">
          <span v-if="detailForm.startDate && detailForm.endDate">
            {{ detailForm.startDate }} 至 {{ detailForm.endDate }}
          </span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="租期">{{ detailForm.rentMonths || 0 }} 个月</el-descriptions-item>
        <el-descriptions-item label="缴费周期">{{ detailForm.paymentCycleText || '-' }}</el-descriptions-item>
        <el-descriptions-item label="月租金">
          <span v-if="detailForm.rentPrice" style="color: #f56c6c; font-weight: bold;">¥{{ detailForm.rentPrice }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="押金">
          <span v-if="detailForm.deposit" style="color: #f56c6c; font-weight: bold;">¥{{ detailForm.deposit }}</span>
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 缴费记录 -->
      <el-divider content-position="left">缴费记录</el-divider>
      <el-table :data="billList" size="small" border>
        <el-table-column label="账单编号" align="center" prop="billNo" width="150" show-overflow-tooltip />
        <el-table-column label="账单类型" align="center" prop="billTypeText" width="80" />
        <el-table-column label="账单期" align="center" prop="billPeriod" width="100" />
        <el-table-column label="账单金额" align="right" prop="billAmount" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.billAmount }}
          </template>
        </el-table-column>
        <el-table-column label="已付金额" align="right" prop="paidAmount" width="100">
          <template slot-scope="scope">
            ¥{{ scope.row.paidAmount }}
          </template>
        </el-table-column>
        <el-table-column label="未付金额" align="right" prop="unpaidAmount" width="100">
          <template slot-scope="scope">
            <span :style="{ color: scope.row.unpaidAmount > 0 ? '#f56c6c' : '#67c23a' }">
              ¥{{ scope.row.unpaidAmount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="支付状态" align="center" prop="billStatusText" width="80">
          <template slot-scope="scope">
            <el-tag :type="scope.row.billStatus === '1' ? 'success' : 'warning'" size="small">
              {{ scope.row.billStatusText }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="支付时间" align="center" prop="payTime" width="160" />
      </el-table>
      <el-row v-if="billList.length > 0" style="margin-top: 10px;">
        <el-col :span="24">
          <div style="text-align: right; padding-right: 20px;">
            <span style="margin-right: 20px;">总账单金额: <strong style="color: #409EFF;">¥{{ totalBillAmount }}</strong></span>
            <span style="margin-right: 20px;">已付金额: <strong style="color: #67C23A;">¥{{ totalPaidAmount }}</strong></span>
            <span>未付金额: <strong style="color: #F56C6C;">¥{{ totalUnpaidAmount }}</strong></span>
          </div>
        </el-col>
      </el-row>

      <!-- 房源信息 -->
      <el-divider content-position="left" v-if="detailForm.houseCode">房源信息</el-divider>
      <el-descriptions :column="3" border v-if="detailForm.houseCode">
        <el-descriptions-item label="房源编号">{{ detailForm.houseCode || '-' }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ detailForm.houseNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ detailForm.floor || '-' }}</el-descriptions-item>
        <el-descriptions-item label="面积">{{ detailForm.area || '-' }} ㎡</el-descriptions-item>
        <el-descriptions-item label="朝向">{{ detailForm.orientation || '-' }}</el-descriptions-item>
        <el-descriptions-item label="装修情况">{{ detailForm.decoration || '-' }}</el-descriptions-item>
        <el-descriptions-item label="房间设施" :span="3">
          <el-tag v-if="!detailForm.facilities || detailForm.facilities === ''" type="info">无</el-tag>
          <div v-else class="facilities-detail-list">
            <el-tag v-for="(facility, index) in (detailForm.facilities || '').split(',')" :key="index" style="margin: 0 5px 5px 0;">
              {{ facility }}
            </el-tag>
          </div>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 表读数 -->
      <el-divider content-position="left">表读数</el-divider>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="水表读数">{{ detailForm.meterReadingWater || '-' }}</el-descriptions-item>
        <el-descriptions-item label="电表读数">{{ detailForm.meterReadingElectric || '-' }}</el-descriptions-item>
        <el-descriptions-item label="燃气表读数">{{ detailForm.meterReadingGas || '-' }}</el-descriptions-item>
        <el-descriptions-item label="钥匙归还">{{ detailForm.keyReturned || 0 }} 把</el-descriptions-item>
      </el-descriptions>

      <!-- 费用明细 -->
      <el-divider content-position="left">费用明细</el-divider>
      <el-descriptions :column="3" border>
        <el-descriptions-item label="水费">{{ detailForm.waterFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="电费">{{ detailForm.electricFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="燃气费">{{ detailForm.gasFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="暖气费">{{ detailForm.heatingFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="物业费">{{ detailForm.propertyFee || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="损坏扣款">{{ detailForm.damageDeduction || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="违约金">{{ detailForm.penaltyAmount || 0 }} 元</el-descriptions-item>
        <el-descriptions-item label="应退租金">
          <span style="color: #67C23A; font-weight: bold;">¥{{ calculateRefundableRent(detailForm) }}</span>
        </el-descriptions-item>
        <el-descriptions-item label="应退总额">
          <span style="font-size: 16px; color: #f56c6c; font-weight: bold;">¥{{ detailForm.refundAmount || 0 }}</span>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 房间设施状态 -->
      <el-divider content-position="left" v-if="detailForm.facilities">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="房间设施状态">
            <div class="facilities-detail-list">
              <div v-for="(facility, index) in parseFacilitiesStatus(detailForm.damageDescription)" :key="index" class="facility-detail-item">
                <el-tag :type="facility.status === '完好' ? 'success' : 'danger'" size="medium">
                  {{ facility.name }} - {{ facility.status }}
                </el-tag>
              </div>
            </div>
          </el-descriptions-item>
        </el-descriptions>
      </el-divider>

      <!-- 物品损坏情况 -->
      <el-divider content-position="left" v-if="getDamageDescription(detailForm.damageDescription)">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="物品损坏情况">{{ getDamageDescription(detailForm.damageDescription) }}</el-descriptions-item>
        </el-descriptions>
      </el-divider>

      <!-- 签字信息 -->
      <el-divider content-position="left" v-if="recordForm.recordId">签字信息</el-divider>
      <el-descriptions :column="2" border v-if="recordForm.recordId">
        <el-descriptions-item label="退租时间">
          {{ recordForm.checkoutDate }}
        </el-descriptions-item>
        <el-descriptions-item label="钥匙归还">
          {{ recordForm.keyReturned || 0 }} 把
        </el-descriptions-item>
        <el-descriptions-item label="退款状态">
          <el-tag v-if="recordForm.refundStatus === '0'" type="warning">待退还</el-tag>
          <el-tag v-else-if="recordForm.refundStatus === '1'" type="success">已退还</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="退款时间">
          {{ recordForm.refundTime || '-' }}
        </el-descriptions-item>
        <el-descriptions-item label="租户签字" :span="2">
          <el-image v-if="recordForm.tenantSignature"
                    style="width: 200px; height: 100px;"
                    :src="getImageUrl(recordForm.tenantSignature)"
                    :preview-src-list="[getImageUrl(recordForm.tenantSignature)]" />
          <span v-else>-</span>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
import { listCheckout, getCheckout, delCheckout, approveCheckout, calculateCheckout, getCheckoutRecordByApplyId, getContractBills } from "@/api/gangzhu/checkout";

export default {
  name: "CheckOut",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      checkOutList: [],

      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        applyId: null,
        applyStatus: null,
      },

      // 当前选中行
      currentForm: {},

      // 设施状态列表
      facilitiesList: [],

      // 审批类型
      approveType: 'view', // view=查看, pass=通过, reject=拒绝

      // 审批表单
      approveOpen: false,
      approveForm: {
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
        approveOpinion: '',
        refundAmount: null  // 应退总额（管理员可编辑）
      },

      // 拒绝表单（已废弃）
      rejectOpen: false,
      rejectForm: {
        rejectReason: ''
      },

      // 详情表单
      detailOpen: false,
      detailForm: {},
      recordForm: {},

      // 详情页账单列表
      billList: []
    };
  },
  created() {
    this.getList();
  },
  computed: {
    // 计算应退租金（从已支付的租金账单中，筛选出退租日期之后的月份）
    refundableRent() {
      // 如果没有计划退租日期，返回0
      if (!this.currentForm.planCheckoutDate) {
        return 0;
      }

      // 1. 获取计划退租日期
      const planCheckoutDate = new Date(this.currentForm.planCheckoutDate);

      // 2. 筛选已支付的租金账单 (billType = '2' 表示租金, billStatus = '1' 表示已支付)
      const paidRentBills = this.billList.filter(bill =>
        bill.billType === '2' && bill.billStatus === '1'
      );

      // 3. 找出账单周期在退租日期之后的月份
      const refundableBills = paidRentBills.filter(bill => {
        if (!bill.billPeriod) return false;

        // billPeriod 格式：2026-04, 2026-05 等
        const [year, month] = bill.billPeriod.split('-');
        const billDate = new Date(parseInt(year), parseInt(month) - 1, 1);

        // 账单月份 > 计划退租月份（不扣除退租当月）
        return billDate > planCheckoutDate;
      });

      // 4. 统计应退租金总额
      return refundableBills.reduce((sum, bill) => sum + (bill.paidAmount || 0), 0);
    },
    // 计算建议的应退总额（供管理员参考）
    calculatedRefund() {
      const deposit = this.currentForm.deposit || 0;  // 押金
      const refundableRent = this.refundableRent;     // 应退租金
      const penalty = this.approveForm.penaltyAmount || 0;
      const damage = this.approveForm.damageDeduction || 0;
      const water = this.approveForm.waterFee || 0;
      const electric = this.approveForm.electricFee || 0;
      const gas = this.approveForm.gasFee || 0;
      const heating = this.approveForm.heatingFee || 0;
      const property = this.approveForm.propertyFee || 0;
      const unpaid = this.currentForm.unpaidBills || 0;

      return Math.max(0, deposit + refundableRent - penalty - damage - water - electric - gas - heating - property - unpaid);
    },
    // 计算应退总额（优先使用管理员手动输入的值，如果没有则使用计算值）
    totalRefund() {
      // 如果管理员手动输入了金额，使用手动输入的值
      if (this.approveForm.refundAmount !== null && this.approveForm.refundAmount !== undefined) {
        return this.approveForm.refundAmount;
      }
      // 否则使用自动计算的值
      return this.calculatedRefund;
    },
    // 计算账单总金额
    totalBillAmount() {
      return this.billList.reduce((sum, bill) => sum + (bill.billAmount || 0), 0);
    },
    // 计算已付金额
    totalPaidAmount() {
      return this.billList.reduce((sum, bill) => sum + (bill.paidAmount || 0), 0);
    },
    // 计算未付金额
    totalUnpaidAmount() {
      return this.billList.reduce((sum, bill) => sum + (bill.unpaidAmount || 0), 0);
    }
  },
  methods: {
    getImageUrl(url) {
      if (!url) return '';
      if (url.startsWith('http://') || url.startsWith('https://')) return url;
      const baseUrl = process.env.VUE_APP_BASE_API;
      if (url.indexOf(baseUrl) !== -1) return url;
      return baseUrl + (url.startsWith('/') ? url : '/' + url);
    },
    getList() {
      this.loading = true;
      listCheckout(this.queryParams).then(response => {
        this.checkOutList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },

    handleSelectionChange(selection) {
      // 可以在这里处理多选逻辑
    },

    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },

    // 打开审批对话框
    handleApprove(row) {
      this.currentForm = { ...row };
      this.approveType = 'view';

      // 解析房间设施字符串，初始化设施状态
      if (row.facilities) {
        // 将 "空调,冰箱,彩电,洗衣机,落地窗" 解析为设施数组
        const facilities = row.facilities.split(',').map(f => f.trim()).filter(f => f);
        this.facilitiesList = facilities.map(facility => ({
          name: facility,
          status: 'good' // 默认为完好
        }));
      } else {
        this.facilitiesList = [];
      }

      this.approveForm = {
        meterReadingWater: row.meterReadingWater,
        meterReadingElectric: row.meterReadingElectric,
        meterReadingGas: row.meterReadingGas,
        waterFee: row.waterFee || 0,
        electricFee: row.electricFee || 0,
        gasFee: row.gasFee || 0,
        heatingFee: row.heatingFee || 0,
        propertyFee: row.propertyFee || 0,
        damageDeduction: row.damageDeduction || 0,
        penaltyAmount: row.penaltyAmount || 0,
        keyReturned: row.keyReturned || 0,
        damageDescription: row.damageDescription || '',
        approveOpinion: '',
        refundAmount: null  // 先设置为null，加载账单后会自动计算
      };

      // 加载账单列表
      this.loadContractBills(row.contractId);

      this.approveOpen = true;
    },

    // 加载合同账单列表
    loadContractBills(contractId) {
      if (!contractId) {
        this.billList = [];
        return;
      }
      getContractBills(contractId).then(response => {
        this.billList = response.data || [];

        // 账单加载完成后，如果 refundAmount 为空，自动计算初始值
        if (this.approveForm.refundAmount === null || this.approveForm.refundAmount === undefined) {
          // 计算初始应退总额
          const initialRefund = this.calculateInitialRefund();
          this.approveForm.refundAmount = initialRefund;
        }
      }).catch(() => {
        this.billList = [];
      });
    },

    // 计算初始应退总额（在加载账单后调用）
    calculateInitialRefund() {
      const deposit = this.currentForm.deposit || 0;  // 押金

      // 计算应退租金
      let refundableRent = 0;
      if (this.currentForm.planCheckoutDate) {
        const planCheckoutDate = new Date(this.currentForm.planCheckoutDate);
        const paidRentBills = this.billList.filter(bill =>
          bill.billType === '2' && bill.billStatus === '1'
        );

        const refundableBills = paidRentBills.filter(bill => {
          if (!bill.billPeriod) return false;
          const [year, month] = bill.billPeriod.split('-');
          const billDate = new Date(parseInt(year), parseInt(month) - 1, 1);
          return billDate > planCheckoutDate;
        });

        refundableRent = refundableBills.reduce((sum, bill) => sum + (bill.paidAmount || 0), 0);
      }

      const penalty = this.approveForm.penaltyAmount || 0;
      const damage = this.approveForm.damageDeduction || 0;
      const water = this.approveForm.waterFee || 0;
      const electric = this.approveForm.electricFee || 0;
      const gas = this.approveForm.gasFee || 0;
      const heating = this.approveForm.heatingFee || 0;
      const property = this.approveForm.propertyFee || 0;
      const unpaid = this.currentForm.unpaidBills || 0;

      return Math.max(0, deposit + refundableRent - penalty - damage - water - electric - gas - heating - property - unpaid);
    },

    // 加载合同账单列表（详情页面使用）
    loadContractBillsForDetail(contractId) {
      if (!contractId) {
        this.billList = [];
        return;
      }
      getContractBills(contractId).then(response => {
        this.billList = response.data || [];
      }).catch(() => {
        this.billList = [];
      });
    },

    // 计算详情页的应退租金
    calculateRefundableRent(row) {
      // 如果没有计划退租日期或账单列表为空，返回0
      if (!row.planCheckoutDate || !this.billList || this.billList.length === 0) {
        return 0;
      }

      // 1. 获取计划退租日期
      const planCheckoutDate = new Date(row.planCheckoutDate);

      // 2. 筛选已支付的租金账单
      const paidRentBills = this.billList.filter(bill =>
        bill.billType === '2' && bill.billStatus === '1'
      );

      // 3. 找出账单周期在退租日期之后的月份
      const refundableBills = paidRentBills.filter(bill => {
        if (!bill.billPeriod) return false;
        const [year, month] = bill.billPeriod.split('-');
        const billDate = new Date(parseInt(year), parseInt(month) - 1, 1);

        // 账单月份 > 计划退租月份
        return billDate > planCheckoutDate;
      });

      // 4. 统计应退租金总额
      return refundableBills.reduce((sum, bill) => sum + (bill.paidAmount || 0), 0);
    },

    // 审批通过
    submitApprovePass() {
      if (!this.approveForm.approveOpinion) {
        this.$modal.msgWarning("请输入审批意见");
        return;
      }

      // 组建设施状态信息
      const facilitiesStatus = this.facilitiesList.map(f => ({
        name: f.name,
        status: f.status === 'good' ? '完好' : '损坏'
      }));

      this.$modal.confirm('确认通过该退租申请吗?').then(() => {
        const loading = this.$loading({
          lock: true,
          text: '提交中...',
          background: 'rgba(0, 0, 0, 0.7)'
        });

        // 准备提交的数据
        const submitData = {
          applyId: this.currentForm.applyId,
          contractId: this.currentForm.contractId,
          tenantId: this.currentForm.tenantId,
          houseId: this.currentForm.houseId,
          // 表读数
          meterReadingWater: this.approveForm.meterReadingWater,
          meterReadingElectric: this.approveForm.meterReadingElectric,
          meterReadingGas: this.approveForm.meterReadingGas,
          // 费用信息
          waterFee: this.approveForm.waterFee,
          electricFee: this.approveForm.electricFee,
          gasFee: this.approveForm.gasFee,
          heatingFee: this.approveForm.heatingFee,
          propertyFee: this.approveForm.propertyFee,
          damageDeduction: this.approveForm.damageDeduction,
          penaltyAmount: this.approveForm.penaltyAmount,
          // 将设施状态和物品损坏情况序列化为JSON
          damageDescription: JSON.stringify({
            facilities: facilitiesStatus,
            description: this.approveForm.damageDescription || ''
          }),
          // 其他信息
          keyReturned: this.approveForm.keyReturned,
          refundAmount: this.approveForm.refundAmount,
          // 审批信息（会保存到数据库）
          approveOpinion: this.approveForm.approveOpinion
        };

        // 调用 calculateCheckout 接口保存费用计算并审批通过
        calculateCheckout(submitData).then(() => {
          loading.close();
          this.$modal.msgSuccess("审批成功，已提交用户确认");
          this.approveOpen = false;
          this.getList();
        }).catch((error) => {
          loading.close();
          console.error('审批失败:', error);
          this.$modal.msgError("审批失败：" + (error.message || '未知错误'));
        });
      }).catch(() => {});
    },

    // 审批拒绝
    submitApproveReject() {
      if (!this.approveForm.approveOpinion) {
        this.$modal.msgWarning("请输入审批意见");
        return;
      }

      this.$modal.confirm('确认拒绝该退租申请吗?').then(() => {
        return approveCheckout({
          applyId: this.currentForm.applyId,
          applyStatus: '2', // 2=审批驳回
          approveOpinion: this.approveForm.approveOpinion
        });
      }).then(() => {
        this.$modal.msgSuccess("已拒绝");
        this.approveOpen = false;
        this.getList();
      }).catch(() => {});
    },

    // 审批拒绝（已废弃，保留以防兼容性问题）
    handleReject(row) {
      this.currentForm = { ...row };
      this.rejectForm.rejectReason = '';
      this.rejectOpen = true;
    },

    submitReject() {
      if (!this.rejectForm.rejectReason) {
        this.$modal.msgWarning("请输入拒绝原因");
        return;
      }

      this.$modal.confirm('确认拒绝该退租申请吗?').then(() => {
        return approveCheckout({
          applyId: this.currentForm.applyId,
          applyStatus: '2',
          approveOpinion: this.rejectForm.rejectReason
        });
      }).then(() => {
        this.$modal.msgSuccess("已拒绝");
        this.rejectOpen = false;
        this.getList();
      }).catch(() => {});
    },

    // 查看详情
    handleDetail(row) {
      this.currentForm = { ...row };
      this.detailForm = { ...row };
      this.recordForm = {};

      // 清空账单列表
      this.billList = [];

      // 如果有退租记录，获取记录信息
      getCheckoutRecordByApplyId(row.applyId).then(response => {
        if (response.code === 200 && response.data) {
          this.recordForm = response.data;
        }
      });

      // 加载账单列表
      if (row.contractId) {
        this.loadContractBillsForDetail(row.contractId);
      }

      this.detailOpen = true;
    },

    // 删除退租申请
    handleDelete(row) {
      this.$modal.confirm('确认删除退租申请ID为"' + row.applyId + '"的数据项吗？').then(() => {
        return delCheckout(row.applyId);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },

    // 解析设施状态
    parseFacilitiesStatus(damageDescription) {
      if (!damageDescription) return [];

      try {
        // 尝试解析 JSON 格式的设施状态
        const parsed = JSON.parse(damageDescription);
        if (parsed.facilities && Array.isArray(parsed.facilities)) {
          return parsed.facilities;
        }
      } catch (e) {
        // 如果不是 JSON 格式，返回空数组
      }

      return [];
    },

    // 获取损坏描述（排除设施状态的JSON部分）
    getDamageDescription(damageDescription) {
      if (!damageDescription) return '';

      try {
        // 尝试解析 JSON 格式
        const parsed = JSON.parse(damageDescription);
        // 如果有 description 字段，返回它
        if (parsed.description) {
          return parsed.description;
        }
        // 如果只有设施列表，返回空字符串
        if (parsed.facilities) {
          return '';
        }
      } catch (e) {
        // 如果不是 JSON 格式，直接返回原字符串
        return damageDescription;
      }

      return '';
    }
  }
};
</script>

<style scoped>
.facilities-status-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.facility-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: #fafafa;
}

.facility-tag {
  min-width: 60px;
  text-align: center;
}

.facility-status {
  display: flex;
  gap: 8px;
}

.facility-status ::v-deep .el-radio__label {
  margin-left: 0;
}

/* 详情页面的设施列表 */
.facilities-detail-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.facility-detail-item {
  margin: 0;
}
</style>
