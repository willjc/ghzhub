package com.ruoyi.common.utils;

/**
 * 身份证工具类
 *
 * @author ruoyi
 */
public class IdCardUtils
{
    /** 合法18位身份证正则（前17位数字 + 校验位 0-9 或 X/x） */
    private static final String ID_CARD_18 = "^[0-9]{17}[0-9Xx]$";

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
