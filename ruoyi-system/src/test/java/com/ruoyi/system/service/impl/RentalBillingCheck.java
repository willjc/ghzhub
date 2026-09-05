package com.ruoyi.system.service.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonParser;
import com.google.gson.JsonObject;
import com.ruoyi.system.domain.HzBill;
import com.ruoyi.system.domain.HzContract;
import com.ruoyi.system.domain.HzUser;
import com.ruoyi.system.mapper.HzBillMapper;
import com.ruoyi.system.mapper.HzHouseMapper;
import com.ruoyi.system.mapper.HzHouseOrderMapper;
import com.ruoyi.system.util.RentalDeposit;
import com.ruoyi.system.service.HzFacilityTemplateMappingService;

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
                houses, null, null, null, null, null, null, null, null,
                new HzFacilityTemplateMappingService(null, null));

        HzContract contract = new HzContract();
        contract.setContractId(1L);
        contract.setContractNo("REGRESSION");
        contract.setRentMonths(12);
        contract.setPaymentCycle("1");
        contract.setRentPrice(rent);
        contract.setDeposit(RentalDeposit.resolve("2", rent));
        contract.setHouseAddress("测试路1号1号楼1单元101室");
        Method fill = EsignServiceImpl.class.getDeclaredMethod("buildRentalTemplateComponents", HzContract.class, HzUser.class);
        fill.setAccessible(true);
        for (String start : new String[]{"2026-09-08", "2026-09-13", "2027-01-31", "2028-02-29"}) {
            LocalDate startDate = LocalDate.parse(start);
            contract.setStartDate(start);
            contract.setEndDate(startDate.plusMonths(12).minusDays(1).toString());
            LocalDate beforeFill = LocalDate.now();
            String json = (String) fill.invoke(service, contract, new HzUser());
            Map<String, String> components = new HashMap<>();
            JsonParser.parseString(json).getAsJsonArray().forEach(item -> {
                JsonObject component = item.getAsJsonObject();
                components.put(component.get("componentId").getAsString(), component.get("componentValue").getAsString());
            });
            require(startDate.equals(componentDate(components, "879dd04f6592434db28dd13862bcede7",
                    "51773c4695124db9b0e8dda1cd45042e", "6da5c952abd24d40935f3cae1d33af87")));
            require(LocalDate.parse(contract.getEndDate()).equals(componentDate(components, "89736a2d0fdb42f587a4a9804a1805b3",
                    "333fac06959343cabd3719f0e4ec9463", "0932a0c15cb4439b8edd72c69b49ab11")));
            LocalDate coverDate = componentDate(components, "afa0dfe2cfdb470fafa6bdefca08b94c",
                    "915518903a8a4fcfa1abdeaf2e7d4cdd", "f4f1ad76a10648168bf622eef1e498f5");
            require(!coverDate.isBefore(beforeFill) && !coverDate.isAfter(LocalDate.now()));
            require(contract.getHouseAddress().equals(components.get("a232a0bda4904c969de30e24ed1f0903")));
            require(!components.containsKey("9401ecfc562647bd9bbecb3283a67257"));
            require(!components.containsKey("0aa2782647e64466a3e1c000fbb84a3a"));
            require(!components.containsKey("5ddcd63eeca4475d9acc9b755a5626dc"));
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

    private static LocalDate componentDate(Map<String, String> components, String year, String month, String day) {
        return LocalDate.of(Integer.parseInt(components.get(year)),
                Integer.parseInt(components.get(month)), Integer.parseInt(components.get(day)));
    }

    private static <T> T emptyMapper(Class<T> type) {
        return type.cast(Proxy.newProxyInstance(type.getClassLoader(), new Class<?>[]{type},
                (proxy, method, values) -> null));
    }

    private static void require(boolean value) {
        if (!value) throw new AssertionError("租赁账单或签署日期回归失败");
    }
}
