package action.med;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.med.MEDApplyTool;
import jdo.med.MedFntHEX;

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
}
