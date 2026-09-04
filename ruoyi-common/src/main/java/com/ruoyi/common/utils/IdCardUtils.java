package com.ruoyi.common.utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/**
 * 身份证工具类
 *
 * @author ruoyi
 */
public class IdCardUtils
{
    /** 合法18位身份证正则（前17位数字 + 校验位 0-9 或 X/x） */
    private static final String ID_CARD_18 = "^[0-9]{17}[0-9Xx]$";

    private static final int[] CHECKSUM_WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    private static final char[] CHECKSUM_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER =
            DateTimeFormatter.ofPattern("uuuuMMdd").withResolverStyle(ResolverStyle.STRICT);

    /**
     * 严格校验18位大陆居民身份证，并按完整年月日计算周岁。
     *
     * @param idCard 身份证号
     * @return 当前周岁；证件号非法时返回 null
     */
    public static Integer calculateAge(String idCard)
    {
        if (StringUtils.isEmpty(idCard))
        {
            return null;
        }
        String id = idCard.trim().toUpperCase();
        if (!id.matches("^[1-8]\\d{16}[0-9X]$") || "000".equals(id.substring(14, 17)))
        {
            return null;
        }

        int sum = 0;
        for (int i = 0; i < CHECKSUM_WEIGHTS.length; i++)
        {
            sum += (id.charAt(i) - '0') * CHECKSUM_WEIGHTS[i];
        }
        if (id.charAt(17) != CHECKSUM_CODES[sum % 11])
        {
            return null;
        }

        try
        {
            LocalDate birthDate = LocalDate.parse(id.substring(6, 14), BIRTH_DATE_FORMATTER);
            LocalDate today = LocalDate.now();
            if (birthDate.isAfter(today))
            {
                return null;
            }
            return Period.between(birthDate, today).getYears();
        }
        catch (DateTimeException e)
        {
            return null;
        }
    }

    /**
     * 根据身份证号推算性别（港好住 hz_user.gender 库内编码：1=男 2=女）。
     * <p>
     * 规则：合法18位大陆居民身份证，第17位奇数=男、偶数=女；
     * 排除以'9'开头的外国人永久居留证（该规则不适用）。
     *
     * @param idCard 身份证号
     * @return "1"=男 / "2"=女；无法推算时返回 null
     */
    public static String deriveGender(String idCard)
    {
        if (StringUtils.isEmpty(idCard))
        {
            return null;
        }
        String id = idCard.trim();
        if (!id.matches(ID_CARD_18))
        {
            return null;
        }
        // 外国人永久居留证以'9'开头，性别不按第17位奇偶编码，排除
        if (id.startsWith("9"))
        {
            return null;
        }
        char c = id.charAt(16);
        if (!Character.isDigit(c))
        {
            return null;
        }
        return ((c - '0') % 2 == 1) ? "1" : "2";
    }

    /**
     * 性别回填：仅当当前性别为空或未知("0")、且身份证可推算时，返回推算出的性别；
     * 否则原样返回当前性别（不覆盖已有的男/女）。
     *
     * @param currentGender 当前库内性别值（1=男 2=女 0/空=未知）
     * @param idCard        身份证号
     * @return 回填后的性别值
     */
    public static String backfillGender(String currentGender, String idCard)
    {
        boolean unknown = StringUtils.isEmpty(currentGender) || "0".equals(currentGender);
        if (!unknown)
        {
            return currentGender;
        }
        String derived = deriveGender(idCard);
        return derived != null ? derived : currentGender;
    }
}
