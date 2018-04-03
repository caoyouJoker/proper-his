package action.med;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

import jdo.hl7.BILJdo;
import jdo.med.MEDApplyTool;
import jdo.med.MEDYJGLTool;
import jdo.med.MedFntHEX;
import jdo.med.MedToLedTool;
import jdo.odi.OdiMainTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author Miracle
 * @version 1.0
 */
public class MedAction extends TAction {
    /**
     * 设备请购保存
     * @param parm TParm
     * @return TParm
     */
    public TParm saveMedApply(TParm parm){
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = MEDApplyTool.getInstance().saveMedApply(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 生成斑马打印机的中文图形控制码
     * @param chnParm
     *            chnParm的列名即为中文文本，文本生成的控制码写到对应列中
     * @return
     */
    public TParm getCHNControlCode(TParm chnParm) {// wanglong add 20150410
        TParm result = new TParm();
        for (int i = 0; i < chnParm.getNames().length; i++) {
            result.setData(chnParm.getNames()[i],
                           MedFntHEX.getCHNControlCode(chnParm.getNames()[i]));
        }
        return result;
    }

    /**
     * 生成斑马打印机的中文图形控制码(指定参数)
     * @param chnParm
     *            chnParm的列名即为中文文本，列的内容即为参数字符串
     *            参数字符串示例：FONT:宋体;DEGREE:0;FONTHEIGHT:14;FONTWEIGHT:0;BOLD:0;ITALIC:0
     *            FONT 字体
     *            DEGREE 旋转角度0,90,180,270
     *            FONTHEIGHT 字体高度
     *            FONTWEIGHT 字体宽度，通常是0
     *            BOLD 1变粗，0正常
     *            ITALIC 1斜体，0正常
     * @return
     */
    public TParm getCHNControlCodeByParameter(TParm chnParm) {// wanglong add 20150410
        String chineseText = "";
        String font = "宋体";
        String degree = "0";// 不旋转
        String fontHeight = "14";
        String fontWeight = "0";
        String bold = "1";// 粗体
        String italic = "0";// 非斜体
        TParm result = new TParm();
        for (int i = 0; i < chnParm.getNames().length; i++) {
            chineseText = chnParm.getNames()[i];
            String paramStr = chnParm.getValue(chineseText);
            String[] paramArr = paramStr.split(";");
            try {
                for (int j = 0; j < paramArr.length; j++) {
                    if (paramArr[j].split(":")[0].equalsIgnoreCase("FONT")) {
                        font = paramArr[j].split(":")[1];
                    } else if (paramArr[j].split(":")[0].equalsIgnoreCase("DEGREE")) {
                        degree = paramArr[j].split(":")[1];
                    } else if (paramArr[j].split(":")[0].equalsIgnoreCase("FONTHEIGHT")) {
                        fontHeight = paramArr[j].split(":")[1];
                    } else if (paramArr[j].split(":")[0].equalsIgnoreCase("FONTWEIGHT")) {
                        fontWeight = paramArr[j].split(":")[1];
                    } else if (paramArr[j].split(":")[0].equalsIgnoreCase("BOLD")) {
                        bold = paramArr[j].split(":")[1];
                    } else if (paramArr[j].split(":")[0].equalsIgnoreCase("ITALIC")) {
                        italic = paramArr[j].split(":")[1];
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            result.setData(chnParm.getNames()[i], MedFntHEX.getCHNControlCode(chineseText, font,
                                                                              degree, fontHeight,
                                                                              fontWeight, bold,
                                                                              italic));
        }
        return result;
    }
    
    /**
     * 更新
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm) {// add by wanglong 20131112
        TParm result = new TParm();
        if (parm == null) {
            result.setErrCode(-1);
            result.setErrText("参数错误");
            return result;
        }
        TConnection conn = getConnection(); // 取得链接
        result = MEDYJGLTool.getInstance().onSave(parm, conn);
        if (result.getErrCode() != 0) {
            conn.rollback();
            conn.close();
            return result;
        }
        String bilPoint = (String) OdiMainTool.getInstance().getOdiSysParmData("BIL_POINT");//add by wanglong 20140123
        if (parm.getValue("ADM_TYPE").equals("I") && bilPoint.equals("2")) {// add by wanglong 20131209
            TParm orderParm = parm.getParm("ORDER");
            String medApplyNo = "";
            for (int i = 0; i < orderParm.getCount(); i++) {
                medApplyNo += "'" + orderParm.getValue("APPLICATION_NO", i) + "',";
            }
            medApplyNo = medApplyNo.substring(0, medApplyNo.length() - 1);
            String sql =
                    "SELECT B.* FROM ODI_ORDER A,ODI_DSPNM B    "
                            + " WHERE A.CASE_NO = B.CASE_NO     "
                            + "   AND A.ORDER_NO = B.ORDER_NO   "
                            + "   AND A.ORDER_SEQ = B.ORDER_SEQ "
                            + "   AND A.CASE_NO = '#' AND A.MED_APPLY_NO in (#) ";
            sql = sql.replaceFirst("#", orderParm.getValue("CASE_NO", 0));
            sql = sql.replaceFirst("#", medApplyNo);
            TParm dataParm = new TParm(MEDYJGLTool.getInstance().select(sql));
            if (dataParm.getErrCode() != 0) {
                conn.rollback();
                conn.close();
                return result;
            }
            if (dataParm.getCount() <= 0) {
                conn.rollback();
                conn.close();
                result.setErr(-2, "没有查询到计费医嘱");
                return result;
            }
            for (int i = 0; i < dataParm.getCount(); i++) {
                dataParm.setData("OPT_USER", i, orderParm.getValue("OPT_USER", 0));
                dataParm.setData("OPT_TERM", i, orderParm.getValue("OPT_TERM", 0));
            }
            dataParm.setData("MED_APPLY_LUMP_FLG",null!=parm.getValue("MED_APPLY_LUMP_FLG")&&parm.getValue("MED_APPLY_LUMP_FLG").length()>0?
            		parm.getValue("MED_APPLY_LUMP_FLG"):"");
            result = BILJdo.getInstance().onIBilFee(conn, dataParm, parm.getValue("TYPE"));// add by wanglong 20131209
            if (result.getErrCode() != 0) {
                conn.rollback();
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        return result;
    }

}
