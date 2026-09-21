-- 退款拆分状态：0=待退还，1=已退还；整体 refund_status 新增 2=部分退还。
-- 先备份本次需要修正的历史部分退款记录，脚本可重复执行。
CREATE TABLE IF NOT EXISTS bak_hz_checkout_record_refund_progress_20260921 (
    record_id BIGINT NOT NULL PRIMARY KEY,
    refund_status CHAR(1),
    refund_time DATETIME,
    payment_remark VARCHAR(500),
    backed_up_at DATETIME NOT NULL
);

INSERT IGNORE INTO bak_hz_checkout_record_refund_progress_20260921
    (record_id, refund_status, refund_time, payment_remark, backed_up_at)
SELECT record_id, refund_status, refund_time, payment_remark, NOW()
FROM hz_checkout_record
WHERE payment_remark LIKE '微信退款部分%';

SET @add_deposit_status = IF(
    EXISTS(SELECT 1 FROM information_schema.columns
           WHERE table_schema = DATABASE() AND table_name = 'hz_checkout_record'
             AND column_name = 'deposit_refund_status'),
    'SELECT 1',
    'ALTER TABLE hz_checkout_record ADD COLUMN deposit_refund_status CHAR(1) NOT NULL DEFAULT ''0'' COMMENT ''押金退款状态(0待退还 1已退还)'' AFTER refund_status'
);
PREPARE stmt FROM @add_deposit_status;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_rent_status = IF(
    EXISTS(SELECT 1 FROM information_schema.columns
           WHERE table_schema = DATABASE() AND table_name = 'hz_checkout_record'
             AND column_name = 'rent_refund_status'),
    'SELECT 1',
    'ALTER TABLE hz_checkout_record ADD COLUMN rent_refund_status CHAR(1) NOT NULL DEFAULT ''0'' COMMENT ''租金退款状态(0待退还 1已退还)'' AFTER deposit_refund_status'
);
PREPARE stmt FROM @add_rent_status;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 历史整体已退款记录默认两笔均完成；随后单独纠正有明确失败明细的部分退款。
UPDATE hz_checkout_record
SET deposit_refund_status = '1', rent_refund_status = '1'
WHERE refund_status = '1';

UPDATE hz_checkout_record
SET deposit_refund_status = IF(
        payment_remark LIKE '%押金已申请退款%'
        OR payment_remark LIKE '%押金已退款(微信侧已全额退款%'
        OR payment_remark LIKE '%押金已退款(幂等)%', '1', '0'),
    rent_refund_status = IF(
        payment_remark LIKE '%已付租金退款 ¥%'
        OR payment_remark LIKE '%已付租金已退款%'
        OR payment_remark LIKE '%首期租金已申请退款%'
        OR payment_remark LIKE '%首期租金已退款(幂等)%', '1', '0'),
    refund_status = '2',
    refund_time = NULL,
    update_time = NOW()
WHERE payment_remark LIKE '微信退款部分%';

-- 验证：部分退款应至少一笔成功且至少一笔待退。
SELECT record_id, apply_id, refund_status, deposit_refund_status, rent_refund_status,
       refund_time, payment_remark
FROM hz_checkout_record
WHERE payment_remark LIKE '微信退款部分%'
ORDER BY record_id;

-- 回滚方法（确认需要回滚后单独执行）：
-- UPDATE hz_checkout_record r
-- JOIN bak_hz_checkout_record_refund_progress_20260921 b ON b.record_id = r.record_id
-- SET r.refund_status = b.refund_status, r.refund_time = b.refund_time,
--     r.payment_remark = b.payment_remark, r.update_time = NOW();
-- ALTER TABLE hz_checkout_record DROP COLUMN rent_refund_status, DROP COLUMN deposit_refund_status;
