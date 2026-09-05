package com.ruoyi.system.service.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzHouseOrderMapper;
import com.ruoyi.system.util.RentalDeposit;

/** 无数据库、无网络的回归入口；在允许测试的环境中运行 main。 */
public class RentalBillingCheck {
    public static void main(String[] args) throws Exception {
        BigDecimal rent = new BigDecimal("700.00");
        require(RentalDeposit.resolve("2", rent).compareTo(new BigDecimal("1000")) == 0);
        require(RentalDeposit.resolve("3", null).compareTo(new BigDecimal("1000")) == 0);
        require(RentalDeposit.resolve("1", rent).equals(rent));

        List<HzBill> bills = new ArrayList<>();
        HzBillMapper billMapper = (HzBillMapper) Proxy.newProxyInstance(
                HzBillMapper.class.getClassLoader(), new Class<?>[]{HzBillMapper.class},
                (proxy, method, values) -> {
                    if ("selectCount".equals(method.getName())) return 0L;
                    if ("insert".equals(method.getName())) {
                        bills.add((HzBill) values[0]);
                        return 1;
                    }
                    throw new UnsupportedOperationException(method.getName());
                });
        HzHouseOrderMapper orders = emptyMapper(HzHouseOrderMapper.class);
        HzHouseMapper houses = emptyMapper(HzHouseMapper.class);
        EsignServiceImpl service = new EsignServiceImpl(null, null, orders, billMapper,
                houses, null, null, null, null, null, null, null, null, null);

        HzContract contract = new HzContract();
        contract.setContractId(1L);
        contract.setContractNo("REGRESSION");
        contract.setRentMonths(12);
        contract.setPaymentCycle("1");
        contract.setRentPrice(rent);
        contract.setDeposit(RentalDeposit.resolve("2", rent));
        for (String start : new String[]{"2026-09-08", "2026-09-13", "2027-01-31", "2028-02-29"}) {
            LocalDate startDate = LocalDate.parse(start);
            contract.setStartDate(start);
            contract.setEndDate(startDate.plusMonths(12).minusDays(1).toString());
            for (String methodName : new String[]{"generateBills", "generateRentBillsOnly"}) {
                bills.clear();
                Method generate = EsignServiceImpl.class.getDeclaredMethod(methodName, HzContract.class);
                generate.setAccessible(true);
                generate.invoke(service, contract);
                int offset = "generateBills".equals(methodName) ? 1 : 0;
                require(bills.size() == 12 + offset);
                if (offset == 1) require(bills.get(0).getBillAmount().compareTo(new BigDecimal("1000")) == 0);
                for (int i = 0; i < 12; i++) {
                    HzBill bill = bills.get(i + offset);
                    require(bill.getBillAmount().compareTo(rent) == 0);
                    require(bill.getPeriodStartDate().equals(startDate.plusMonths(i).toString()));
                    require(bill.getPeriodEndDate().equals(startDate.plusMonths(i + 1).minusDays(1).toString()));
                }
            }
        }

        Method date = EsignServiceImpl.class.getDeclaredMethod("rentalSignDate",
                String.class, int.class, double.class, double.class, boolean.class);
        date.setAccessible(true);
        JsonObject field = (JsonObject) date.invoke(service, "example", 13, 380d, 667d, true);
        require(field.get("signFieldType").getAsInt() == 2);
        require(field.getAsJsonObject("dateSignFieldConfig").get("autoSign").getAsBoolean());
        require(field.getAsJsonObject("dateSignFieldConfig").get("signDatePositionPage").getAsInt() == 13);
    }

    private static <T> T emptyMapper(Class<T> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, values) -> null));
    }

    private static void require(boolean value) {
        if (!value) throw new AssertionError("租赁账单或签署日期回归失败");
    }
}
