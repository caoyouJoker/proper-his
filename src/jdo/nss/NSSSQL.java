package jdo.nss;

/**
 * <p>Title: 膳食SQL封装</p>
 *
 * <p>Description: 膳食SQL封装</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.24
 * @version 1.0
 */
public class NSSSQL {
    public NSSSQL() {
    }

    /**
     *
     * @return String
     */
    public static String getNSSMeunRule() {
        return "SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5, "
            + "SERIAL_NUMBER,TOT_NUMBER,RULE_TYPE "
            + "FROM SYS_RULE WHERE RULE_TYPE = 'MENU_TYPE'";
    }

    /**
     *
     * @return String
     */
    public static String getNSSMeunCategory() {
        return "SELECT CATEGORY_CODE,CATEGORY_CHN_DESC,DETAIL_FLG "
            + "FROM SYS_CATEGORY WHERE RULE_TYPE = 'MENU_TYPE' ORDER BY SEQ";
    }

    /**
     *
     * @return String
     */
    public static String getNSSMeunCategoryLength() {
        return "SELECT DISTINCT LENGTH(CATEGORY_CODE) CATEGORY_LENGTH "
            + "FROM SYS_CATEGORY WHERE  RULE_TYPE = 'MENU_TYPE' "
            + "ORDER BY CATEGORY_LENGTH ";
    }

    /**
     *
     * @param type_code String
     * @return String
     */
    public static String getNSSMaxSerialNumber(String invtype_code) {
        return "SELECT MAX(MENU_CODE) AS MENU_CODE FROM NSS_MENU "
            + "WHERE NSSTYPE_CODE  = '" + invtype_code + "'";
    }

    /**
     *
     * @param nsstype_code String
     * @return String
     */
    public static String getNSSvBaseByTypeCode(String nsstype_code) {
        return "SELECT * FROM NSS_MENU WHERE NSSTYPE_CODE = '" + nsstype_code +
            "' ORDER BY MENU_CODE";
    }

    /**
     *
     * @param pack_code String
     * @param mral_code String
     * @return String
     */
    public static String getNSSPackDD(String pack_code, String meal_code) {
        return "SELECT * FROM NSS_PACKDD WHERE PACK_CODE = '" + pack_code +
            "' AND MEAL_CODE = '" + meal_code + "' ORDER BY SEQ";
    }

}
