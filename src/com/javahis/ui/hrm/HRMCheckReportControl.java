package com.javahis.ui.hrm;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.emr.GetWordValue;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMContractD;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 体检综合统计 </p>
 * 
 * <p> Description: 体检综合统计 </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author WangLong 2013.02.16
 * @version 1.0
 */
public class HRMCheckReportControl
        extends TControl {

    private TTable table;
    private TTextFormat contract;// 合同下拉框
    private HRMContractD contractD;// 合同DataStore
    private BILComparator compare = new BILComparator();// add by wanglong 20130222
    private boolean ascending = false;
    private int sortColumn = -1;
    private TTextFormat company;// add by wangb 2016/06/27 团体名称
    
    /**
     * 初始化方法
     */
    public void onInit() {
        contractD = new HRMContractD();
        contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
        table = (TTable) this.getComponent("TABLE");
        addSortListener(table);// add by wanglong 20130222
        company = (TTextFormat) this.getComponent("COMPANY_CODE");
        
		// add by wangb 2016/06/27 团体名称需要根据不同登录角色筛选
		String roleType = "";
		if (this.getPopedem("SYSDBA")) {
			roleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
		} else {
			roleType = this.getPopedemParm().getValue("ID", 0);
		}
		
		// 查询团体信息
		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
        company.setPopupMenuData(companyData);
        company.setComboSelectRow();
        company.popupMenuShowData();
        
        this.onClear();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        String companyCode = this.getValueString("COMPANY_CODE");
        String contractCode = this.getValueString("CONTRACT_CODE");
        String startDate = this.getText("START_DATE");
        String endDate = this.getText("END_DATE");
        if (companyCode.equals("") && contractCode.equals("")) {
            this.messageBox("请选择团体合同");
            return;
        }
        TParm result = new TParm();
        String sql =
                "SELECT B.MR_NO,B.CASE_NO,A.SEQ_NO,B.REPORT_DATE,A.PAT_DEPT,A.STAFF_NO,A.PAT_NAME,A.SEX_CODE,A.BIRTHDAY,A.MARRIAGE_CODE,A.TEL "
                        + "     FROM HRM_CONTRACTD A, HRM_PATADM B "
                        + "    WHERE A.COMPANY_CODE = B.COMPANY_CODE(+) "
                        + "      AND A.CONTRACT_CODE = B.CONTRACT_CODE(+) "
                        + "      AND A.MR_NO = B.MR_NO(+) "
                        + " # # # "
                        + " ORDER BY A.CONTRACT_CODE,A.SEQ_NO,B.REPORT_DATE";
        if (companyCode.equals("")) {
            sql = sql.replaceFirst("#", "");
        } else {
            sql = sql.replaceFirst("#", " AND A.COMPANY_CODE = '" + companyCode + "' ");
        }
        if (contractCode.equals("")) {
            sql = sql.replaceFirst("#", "");
        } else {
            sql = sql.replaceFirst("#", " AND A.CONTRACT_CODE='" + contractCode + "' ");
        }
        if (startDate.equals("")&&endDate.equals("")) {
            sql = sql.replaceFirst("#", "");
        } else if(!startDate.equals("")&&endDate.equals("")){
            sql =
                    sql.replaceFirst("#", " AND B.REPORT_DATE >= TO_DATE( '" + startDate
                            + "', 'yyyy/mm/dd') ");
        } else if (startDate.equals("") && !endDate.equals("")) {
            sql =
                    sql.replaceFirst("#", " AND B.REPORT_DATE <= TO_DATE( '" + endDate
                            + " 23:59:59', 'yyyy/mm/dd hh24:mi:ss') ");
        } else {
            sql =
                    sql.replaceFirst("#", " AND B.REPORT_DATE BETWEEN TO_DATE( '" + startDate
                            + "', 'yyyy/mm/dd') " + "AND TO_DATE( '" + endDate
                            + " 23:59:59', 'yyyy/mm/dd hh24:mi:ss') ");
        }
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox("E0035");// 操作失败
            return;
        }
        if (result.getCount() <= 0) {
            table.setParmValue(new TParm());
            this.messageBox("E0116");// 没有数据
            return;
        }
        for (int i = 0; i < result.getCount(); i++) {
            String age =
                    StringTool.CountAgeByTimestamp(result.getTimestamp("BIRTHDAY", i), SystemTool
                            .getInstance().getDate())[0];
            result.setData("AGE", i, age);
            result.addData("IN_JWS", "");// 既往史
            result.addData("IN_XYS", "");// 吸烟史
            result.addData("IN_YJS", "");// 饮酒史
            result.addData("IN_JZS", "");// 家族史
            result.addData("IN_SSY", "");// 收缩压
            result.addData("IN_SZY", "");// 舒张压
            result.addData("OUT_SG", "");// 身高
            result.addData("OUT_TZ", "");// 体重
            result.addData("OUT_TZZS", "");// 体重指数
            result.addData("WOMAN_WY", "");// 外阴
            result.addData("WOMAN_YD", "");// 阴道
            result.addData("WOMAN_GJ", "");// 宫颈
            result.addData("WOMAN_QT", "");// 其他
            result.addData("EYE_LXD", "");// 裂隙灯
            result.addData("EYE_YD", "");// 眼底
            result.addData("EYE_SL", "");// 视力
            result.addData("EYE_JZ", "");// 矫正 add by wanglong 20140220
            result.addData("EYE_SJJC", "");// 色觉检查 add by wanglong 20131217
            result.addData("FACE_BD", "");// 鼻窦
            result.addData("FACE_YB", "");// 咽部
            result.addData("FACE_WED", "");// 外耳道
            result.addData("FACE_TLJC", "");// 听力检查 add by wanglong 20131217
            String fileSql =
                    "SELECT A.FILE_PATH, A.FILE_NAME FROM EMR_FILE_INDEX A, EMR_TEMPLET B "
                            + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
                            + "   AND B.HRM_FLG = 'Y' AND B.SYSTEM_CODE = 'HRM' "
                            + "   AND A.FILE_NAME LIKE '%健检%' AND A.MR_NO = '"
                            + result.getData("MR_NO", i) + "' AND A.CASE_NO = '"
                            + result.getData("CASE_NO", i) + "'";
            TParm fileList = new TParm(TJDODBTool.getInstance().select(fileSql));
            if (fileList.getErrCode() != 0 || fileList.getCount() == 0) {
                continue;
            } else {
                for (int j = 0; j < fileList.getCount(); j++) {
                    if (fileList.getValue("FILE_NAME", j).indexOf("内科") != -1) {
                        TParm param = new TParm();
                        param.setData("FILE_PATH", "JHW\\" + fileList.getValue("FILE_PATH", j));
                        param.setData("FILE_NAME", fileList.getValue("FILE_NAME", j));
                        String[] inTitle = new String[]{"jws", "xys", "yjs", "jzs", "xy" };
                        TParm resultParm =
                                GetWordValue.getInstance().getWordValueByName(param, inTitle);
                        if (resultParm.getData("jws_VALUE", 0) != null) {// =====================老模板
                            if (resultParm.getCount("jws") > 0) {// 既往史
                                if (result.getValue("IN_JWS", i).equals("")) {
                                    result.setData("IN_JWS", i, resultParm.getValue("jws_VALUE", 0));
                                }
                            }
                            if (resultParm.getCount("xys") > 0) {// 吸烟史
                                if (result.getValue("IN_XYS", i).equals("")) {
                                    result.setData("IN_XYS", i, resultParm.getValue("xys_VALUE", 0));
                                }
                            }
                            if (resultParm.getCount("yjs") > 0) {// 饮酒
                                if (result.getValue("IN_YJS", i).equals("")) {
                                    result.setData("IN_YJS", i, resultParm.getValue("yjs_VALUE", 0));
                                }
                            }
                            if (resultParm.getCount("jzs") > 0) {// 家族史
                                if (result.getValue("IN_JZS", i).equals("")) {
                                    result.setData("IN_JZS", i, resultParm.getValue("jzs_VALUE", 0));
                                }
                            }
                            if (resultParm.getCount("xy") > 0) {// 收缩压、舒张压
                                if (result.getValue("IN_SSY", i).equals("")
                                        && result.getValue("IN_SZY", i).equals("")) {
                                    String xy =
                                            resultParm.getValue("xy_VALUE", 0).split("mmHg")[0]
                                                    .replaceAll(" ", "");
                                    result.setData("IN_SSY", i, xy.split("/")[0]);
                                    try {
                                        result.setData("IN_SZY", i, xy.split("/")[1]);
                                    }
                                    catch (ArrayIndexOutOfBoundsException e) {
                                        result.setData("IN_SZY", i, "");
                                    }
                                }
                            }
                        } else {//=====================================新内科模板
                            inTitle =
                                    new String[]{"in_jws", "in_xys", "in_yjs", "in_jzs",
                                            "in_xy",// 内科
                                            "out_sg", "out_tz",
                                            "out_tzzs", // 外科
                                            "woman_wy", "woman_yd", "woman_gj", "woman_qt", // 妇科
                                            "eye_lxd", "eye_yd", "eye_sl","eye_jz", "eye_sjjc", // 眼科
                                            "face_bd", "face_yb", "face_wed", "face_tljc" // 五官科
                                    };
                            TParm resultParm1 =
                                    GetWordValue.getInstance().getWordValueByName(param, inTitle);
                            if (resultParm1.getCount("in_jws") > 0) {// 既往史
                                if (result.getValue("IN_JWS", i).equals("")) {
                                    result.setData("IN_JWS", i,
                                                   resultParm1.getValue("in_jws_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("in_xys") > 0) {// 吸烟史
                                if (result.getValue("IN_XYS", i).equals("")) {
                                    result.setData("IN_XYS", i,
                                                   resultParm1.getValue("in_xys_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("in_yjs") > 0) {// 饮酒
                                if (result.getValue("IN_YJS", i).equals("")) {
                                    result.setData("IN_YJS", i,
                                                   resultParm1.getValue("in_yjs_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("in_jzs") > 0) {// 家族史
                                if (result.getValue("IN_JZS", i).equals("")) {
                                    result.setData("IN_JZS", i,
                                                   resultParm1.getValue("in_jzs_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("in_xy") > 0) {// 收缩压、舒张压
                                if (result.getValue("IN_SSY", i).equals("")
                                        && result.getValue("IN_SZY", i).equals("")) {
                                    String xy =
                                            resultParm1.getValue("in_xy_VALUE", 0).split("mmHg")[0]
                                                    .replaceAll(" ", "");
                                    result.setData("IN_SSY", i, xy.split("/")[0]);
                                    try {
                                        result.setData("IN_SZY", i, xy.split("/")[1]);
                                    }
                                    catch (ArrayIndexOutOfBoundsException e) {
                                        result.setData("IN_SZY", i, "");
                                    }
                                }
                            }
                            if (resultParm1.getCount("out_sg") > 0) {// 身高
                                if (result.getValue("OUT_SG", i).equals("")) {
                                    result.setData("OUT_SG", i,
                                                   resultParm1.getValue("out_sg_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("out_tz") > 0) {// 体重
                                if (result.getValue("OUT_TZ", i).equals("")) {
                                    result.setData("OUT_TZ", i,
                                                   resultParm1.getValue("out_tz_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("out_tzzs") > 0) {// 体重指数
                                if (result.getValue("OUT_TZZS", i).equals("")) {
                                    result.setData("OUT_TZZS", i,
                                                   resultParm1.getValue("out_tzzs_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("woman_wy") > 0) {// 外阴
                                if (result.getValue("WOMAN_WY", i).equals("")) {
                                    result.setData("WOMAN_WY", i,
                                                   resultParm1.getValue("woman_wy_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("woman_yd") > 0) {// 阴道
                                if (result.getValue("WOMAN_YD", i).equals("")) {
                                    result.setData("WOMAN_YD", i,
                                                   resultParm1.getValue("woman_yd_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("woman_gj") > 0) {// 宫颈
                                if (result.getValue("WOMAN_GJ", i).equals("")) {
                                    result.setData("WOMAN_GJ", i,
                                                   resultParm1.getValue("woman_gj_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("woman_qt") > 0) {// 病理所见
                                if (result.getValue("WOMAN_QT", i).equals("")) {
                                    result.setData("WOMAN_QT", i,
                                                   resultParm1.getValue("woman_qt_VALUE", 0));
                                }
                            }

                            if (resultParm1.getCount("eye_lxd") > 0) {// 裂隙灯
                                if (result.getValue("EYE_LXD", i).equals("")) {
                                    result.setData("EYE_LXD", i,
                                                   resultParm1.getValue("eye_lxd_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("eye_yd") > 0) {// 眼底
                                if (result.getValue("EYE_YD", i).equals("")) {
                                    result.setData("EYE_YD", i,
                                                   resultParm1.getValue("eye_yd_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("eye_sl") > 0) {// 视力
                                if (result.getValue("EYE_SL", i).equals("")) {
                                    String sl = resultParm1.getValue("eye_sl_VALUE", 0);
                                    String slValue =
                                            sl.replaceFirst("左", "").replaceFirst("右", "")
                                                    .replaceAll(" ", "");
                                    if (slValue.equals("")) {
                                        result.setData("EYE_SL", i, "");
                                    } else {
                                        sl = sl.replaceAll(" ", "").replaceFirst("右", " 右");
                                        result.setData("EYE_SL", i, sl);
                                    }
                                }
                            }
                            if (resultParm1.getCount("eye_jz") > 0) {// 矫正 add by wanglong 20140220
                                if (result.getValue("EYE_JZ", i).equals("")) {
                                    result.setData("EYE_JZ", i,
                                                   resultParm1.getValue("eye_jz_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("eye_sjjc") > 0) {// 色觉检查 add by wanglong 20131217
                                if (result.getValue("EYE_SJJC", i).equals("")) {
                                    result.setData("EYE_SJJC", i,
                                                   resultParm1.getValue("eye_sjjc_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("face_bd") > 0) {// 鼻窦
                                if (result.getValue("FACE_BD", i).equals("")) {
                                    result.setData("FACE_BD", i,
                                                   resultParm1.getValue("face_bd_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("face_yb") > 0) {// 咽部
                                if (result.getValue("FACE_YB", i).equals("")) {
                                    result.setData("FACE_YB", i,
                                                   resultParm1.getValue("face_yb_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("face_wed") > 0) {// 外耳道
                                if (result.getValue("FACE_WED", i).equals("")) {
                                    result.setData("FACE_WED", i,
                                                   resultParm1.getValue("face_wed_VALUE", 0));
                                }
                            }
                            if (resultParm1.getCount("face_tljc") > 0) {// 体力检查 add by wanglong 20131217
                                if (result.getValue("FACE_TLJC", i).equals("")) {
                                    result.setData("FACE_TLJC", i,
                                                   resultParm1.getValue("face_tljc_VALUE", 0));
                                }
                            }
                            
                        }
                    } else if (fileList.getValue("FILE_NAME", j).indexOf("外科") != -1) {
                        TParm param = new TParm();
                        param.setData("FILE_PATH", "JHW\\" + fileList.getValue("FILE_PATH", j));
                        param.setData("FILE_NAME", fileList.getValue("FILE_NAME", j));
                        String[] inTitle = new String[]{"sg", "tz", "tzzs" };
                        TParm resultParm =
                                GetWordValue.getInstance().getWordValueByName(param, inTitle);
                        if (resultParm.getCount("sg") > 0) {// 身高
                            if (result.getValue("OUT_SG", i).equals("")) {
                                result.setData("OUT_SG", i, resultParm.getValue("sg_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("tz") > 0) {// 体重
                            if (result.getValue("OUT_TZ", i).equals("")) {
                                result.setData("OUT_TZ", i, resultParm.getValue("tz_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("tzzs") > 0) {// 体重指数
                            if (result.getValue("OUT_TZZS", i).equals("")) {
                                result.setData("OUT_TZZS", i, resultParm.getValue("tzzs_VALUE", 0));
                            }
                        }
                    } else if (fileList.getValue("FILE_NAME", j).indexOf("妇科") != -1) {
                        TParm param = new TParm();
                        param.setData("FILE_PATH", "JHW\\" + fileList.getValue("FILE_PATH", j));
                        param.setData("FILE_NAME", fileList.getValue("FILE_NAME", j));
                        String[] inTitle = new String[]{"wy", "yd", "gj", "gt", "fj", "md", "dc" };
                        TParm resultParm =
                                GetWordValue.getInstance().getWordValueByName(param, inTitle);
                        if (resultParm.getCount("wy") > 0) {// 外阴
                            if (result.getValue("WOMAN_WY", i).equals("")) {
                                result.setData("WOMAN_WY", i, resultParm.getValue("wy_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("yd") > 0) {// 阴道
                            if (result.getValue("WOMAN_YD", i).equals("")) {
                                result.setData("WOMAN_YD", i, resultParm.getValue("yd_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("gj") > 0) {// 宫颈
                            if (result.getValue("WOMAN_GJ", i).equals("")) {
                                result.setData("WOMAN_GJ", i, resultParm.getValue("gj_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("gt") > 0) {// 宫体
                            if (result.getValue("WOMAN_GT", i).equals("")) {
                                result.setData("WOMAN_GT", i, resultParm.getValue("gt_VALUE", 0));
                            }
                        }
                    } else if (fileList.getValue("FILE_NAME", j).indexOf("眼科") != -1) {
                        TParm param = new TParm();
                        param.setData("FILE_PATH", "JHW\\" + fileList.getValue("FILE_PATH", j));
                        param.setData("FILE_NAME", fileList.getValue("FILE_NAME", j));
                        String[] inTitle = new String[]{"lxd", "yd", "sl" };
                        TParm resultParm =
                                GetWordValue.getInstance().getWordValueByName(param, inTitle);
                        if (resultParm.getCount("lxd") > 0) {// 裂隙灯
                            if (result.getValue("EYE_LXD", i).equals("")) {
                                result.setData("EYE_LXD", i, resultParm.getValue("lxd_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("yd") > 0) {// 眼底
                            if (result.getValue("EYE_YD", i).equals("")) {
                                result.setData("EYE_YD", i, resultParm.getValue("yd_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("sl") > 0) {// 视力
                            if (result.getValue("EYE_SL", i).equals("")) {
                                String sl = resultParm.getValue("sl_VALUE", 0);
                                String slValue =
                                        sl.replaceFirst("左", "").replaceFirst("右", "")
                                                .replaceAll(" ", "");
                                if (slValue.equals("")) {
                                    result.setData("EYE_SL", i, "");
                                } else {
                                    sl = sl.replaceAll(" ", "").replaceFirst("右", " 右");
                                    result.setData("EYE_SL", i, sl);
                                }
                            }
                        }
                        if (resultParm.getCount("jz") > 0) { //矫正 add by wanglong 20140220
                            if (result.getValue("EYE_JZ", i).equals("")) {
                                result.setData("EYE_JZ", i, resultParm.getValue("jz_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("sjjc") > 0) { //色觉检查 add by wanglong 20131217
                            if (result.getValue("EYE_SJJC", i).equals("")) {
                                result.setData("EYE_SJJC", i, resultParm.getValue("sjjc_VALUE", 0));
                            }
                        }
                    } else if (fileList.getValue("FILE_NAME", j).indexOf("五官科") != -1) {
                        TParm param = new TParm();
                        param.setData("FILE_PATH", "JHW\\" + fileList.getValue("FILE_PATH", j));
                        param.setData("FILE_NAME", fileList.getValue("FILE_NAME", j));
                        String[] inTitle = new String[]{"bd", "yb", "wed" };
                        TParm resultParm =
                                GetWordValue.getInstance().getWordValueByName(param, inTitle);
                        if (resultParm.getCount("bd") > 0) {// 鼻窦
                            if (result.getValue("FACE_BD", i).equals("")) {
                                result.setData("FACE_BD", i, resultParm.getValue("bd_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("yb") > 0) {// 咽部
                            if (result.getValue("FACE_YB", i).equals("")) {
                                result.setData("FACE_YB", i, resultParm.getValue("yb_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("wed") > 0) {// 外耳道
                            if (result.getValue("FACE_WED", i).equals("")) {
                                result.setData("FACE_WED", i, resultParm.getValue("wed_VALUE", 0));
                            }
                        }
                        if (resultParm.getCount("tljc") > 0) {// 听力检查 add by wanglong 20131217
                            if (result.getValue("FACE_TLJC", i).equals("")) {
                                result.setData("FACE_TLJC", i, resultParm.getValue("tljc_VALUE", 0));
                            }
                        }
                    }
                }
            }
        }
        table.setParmValue(result);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        if (table.getRowCount() < 1) {
            messageBox("E0116");// 没有数据
            return;
        }
        TParm parm = table.getParmValue();
        for (int i = 0; i < parm.getCount(); i++) {//add by wanglong 20130423 病案号不显示前面的0
            parm.setData("MR_NO", i, parm.getValue("MR_NO", i).replaceAll("^(0+)", ""));
        }
        table.setParmValue(parm);
        ExportExcelUtil.getInstance()
                .exportExcelWithoutTime(table,
                                        this.getText("COMPANY_CODE") + "_"
                                                + this.getText("CONTRACT_CODE") + "_健检综合报表");
        onQuery();
    }

    /**
     * 团体代码选择事件
     */
    public void onCompanyChoose() {
        String companyCode = this.getValueString("COMPANY_CODE");
        if (StringUtil.isNullString(companyCode)) {
            this.clearValue("CONTRACT_CODE");
            return;
        }
        TParm contractParm = contractD.onQueryByCompany(companyCode);// 查询相对应合同下拉框的数据
        if (contractParm == null || contractParm.getCount() <= 0 || contractParm.getErrCode() != 0) {
            this.messageBox("没有合同数据");
            return;
        }
        contract.setPopupMenuData(contractParm);
        contract.setComboSelectRow();
        contract.popupMenuShowData();
        contract.setHisOneNullRow(true);
        String contractCode = contractParm.getValue("ID", 0);
        if (StringUtil.isNullString(contractCode)) {
            this.messageBox("E0034");// 取得数据错误
            return;
        }
        contract.setValue(contractCode);
    }

    /**
     * 得到table组件
     * 
     * @param tag
     * @return
     */
    public TTable getTTable(String tag) {
        return (TTable) this.getComponent(tag);
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("START_DATE;END_DATE;CONTRACT_CODE;COMPANY_CODE");
        contract.getPopupMenuData().getData().clear();
        contract.filter();
        table.removeRowAll();
    }
    
    // ====================排序功能begin======================// add by wanglong 20130222
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// 点击相同列，翻转排序
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// 取得表单中的数据
                String columnName[] = tableData.getNames("Data");// 获得列名
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
                int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * 根据列名数据，将TParm转为Vector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size)
            count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * 返回指定列在列名数组中的index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 根据列名数据，将Vector转成Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================排序功能end======================
}
