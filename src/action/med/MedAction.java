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
     * �豸�빺����
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
     * ���ɰ����ӡ��������ͼ�ο�����
     * @param chnParm
     *            chnParm��������Ϊ�����ı����ı����ɵĿ�����д����Ӧ����
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
     * ���ɰ����ӡ��������ͼ�ο�����(ָ������)
     * @param chnParm
     *            chnParm��������Ϊ�����ı����е����ݼ�Ϊ�����ַ���
     *            �����ַ���ʾ����FONT:����;DEGREE:0;FONTHEIGHT:14;FONTWEIGHT:0;BOLD:0;ITALIC:0
     *            FONT ����
     *            DEGREE ��ת�Ƕ�0,90,180,270
     *            FONTHEIGHT ����߶�
     *            FONTWEIGHT �����ȣ�ͨ����0
     *            BOLD 1��֣�0����
     *            ITALIC 1б�壬0����
     * @return
     */
    public TParm getCHNControlCodeByParameter(TParm chnParm) {// wanglong add 20150410
        String chineseText = "";
        String font = "����";
        String degree = "0";// ����ת
        String fontHeight = "14";
        String fontWeight = "0";
        String bold = "1";// ����
        String italic = "0";// ��б��
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
