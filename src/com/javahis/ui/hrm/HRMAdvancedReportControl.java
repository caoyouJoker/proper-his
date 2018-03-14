package com.javahis.ui.hrm;

import java.sql.Timestamp;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import jdo.emr.GetWordValue;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ���߶˷��� </p>
 * 
 * <p> Description: ���߶˷��� </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author WangLong 2014.05.19
 * @version 1.0
 */
public class HRMAdvancedReportControl extends TControl {

    private TTable table;
    //20170703 zhanglei �����ϸ��ѯ���� START
    private TTextFormat t1;
    private TTextFormat t2;
    private TTable TABLE;
    String headerName;
    //20170703 zhanglei �����ϸ��ѯ���� END

    /**
     * ��ʼ������
     */
    public void onInit() {
        table = (TTable) this.getComponent("TABLE");
        //20170703 zhanglei �����ϸ��ѯ���� START
        t1 =  (TTextFormat)this.getComponent("t1");
        t2 =  (TTextFormat)this.getComponent("t2");
        this.TABLE = (TTable) this.getComponent("TABLE");
        //20170703 zhanglei �����ϸ��ѯ���� END
    }

    /**
     * ��������ѯ
     */
    public void onQuery() {
    	//20170703 zhanglei �����ϸ��ѯ���� START
    	String caseNo1 = this.getValueString("t1").trim();
    	String caseNo2 = this.getValueString("t2").trim();
    	//this.messageBox("caseNo1:" + caseNo1.length());
    	if(caseNo1.length() < 1 || caseNo2.length() < 1){
    		//this.messageBox("1onMrNo");
    		onMrNo();
    	}else{
    		//this.messageBox("2else");
    		query();
    	}
    	//20170703 zhanglei �����ϸ��ѯ���� END
        
    }

    /**
     * �س���ѯ
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO").trim();
        if (mrNo.length() < 1) {
            this.messageBox("�����벡����");
            return;
        }
        mrNo = PatTool.getInstance().checkMrno(mrNo);
        //this.setValue("MR_NO", mrNo);
        // modify by huangtt 20160929 EMPI���߲�����ʾ start
        Pat pat= Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			 this.setValue("MR_NO", mrNo);
		}
        // modify by huangtt 20160929 EMPI���߲�����ʾ end
        
        TParm sysPatInfo = PatTool.getInstance().getInfoForMrno(mrNo);
        if (sysPatInfo.getCount() > 0) {
            this.setValue("MR_NO", mrNo);// ������
            this.setValue("PAT_NAME", sysPatInfo.getValue("PAT_NAME", 0));// ����
            this.setValue("SEX_CODE", sysPatInfo.getValue("SEX_CODE", 0));// �Ա� SEX
            Timestamp date = SystemTool.getInstance().getDate();
            this.setValue("AGE",
                          StringUtil.getInstance()
                                  .showAge(sysPatInfo.getTimestamp("BIRTH_DATE", 0), date));
       }
       //20170703 zhanglei �����ϸ��ѯ���� START
       String sqlt = "SELECT C.CONTRACT_DESC AS ID, "
//				+ "TO_CHAR (A.REPORT_DATE, 'YYYY/MM/DD') AS REPORT_DATE, "
				+ "TO_CHAR (A.REPORT_DATE, 'YYYY/MM/DD') AS NAME, "
				+ " B.COMPANY_DESC, "
				+ " A.CASE_NO "
				+ " FROM HRM_PATADM A, HRM_COMPANY B, HRM_CONTRACTM C, HRM_PACKAGEM D"
//				+ " WHERE A.MR_NO = '000000470608' "
				+ " WHERE A.MR_NO = '"+ this.getValueString("MR_NO") +"' "
				+ " AND A.COMPANY_CODE = B.COMPANY_CODE(+) "
				+ " AND A.CONTRACT_CODE = C.CONTRACT_CODE(+) "
				+ " AND A.PACKAGE_CODE = D.PACKAGE_CODE(+) "
				+ " AND A.REPORT_DATE IS NOT NULL "
				+ " ORDER BY A.CASE_NO DESC ";
//        System.out.println("SQL1111111111" + sqlt);
        TParm result = new TParm(TJDODBTool.getInstance().select(sqlt));
        if (result.getErrCode() != 0) {
            this.messageBox("E0035");// ����ʧ��
            return;
        }
        if (result.getCount() <= 0) {
            table.setParmValue(new TParm());
            this.messageBox("E0116");// û������
            return;
        }
        //this.messageBox_(result);
        t1.setPopupMenuData(result);
        t1.setComboSelectRow();
        t1.popupMenuShowData();
        t2.setPopupMenuData(result);
        t2.setComboSelectRow();
        t2.popupMenuShowData();
        //this.messageBox("" + result.getCount("CASE_NO"));
        if(result.getCount("CASE_NO") < 2){
        	this.setValue("t1", result.getValue("CASE_NO", 0));
        }else{
        	this.setValue("t1", result.getValue("CASE_NO", 1));
        }
//        this.setValue("t1", result.getValue("CASE_NO", 1));
        this.setValue("t2", result.getValue("CASE_NO", 0));
      //20170703 zhanglei �����ϸ��ѯ���� END
        query();
    }
    
    /**
     * �������
     */
    public void CS() {
    	this.messageBox("" + this.getValue("t1"));
    }
    

    /**
     * ִ�в�ѯ
     */
    public void query() {
    	String MrNo = this.getValueString("MR_NO");
        if (MrNo.equals("")) {
        	this.messageBox("�����벡����");
            return;
        }
        TParm result = new TParm();
        String patSql =
                "SELECT * "
                        + "  FROM (SELECT '�����' AS CASE_NO, '������' AS MR_NO, '��������' AS REPORT_DATE, "
                        + "               '����/��ͬ' AS COMPANY_CONTRACT, '�ײ�' AS PACKAGE_DESC "
                        + "          FROM DUAL "
                        + "        UNION "
                        + "        SELECT A.CASE_NO, A.MR_NO, TO_CHAR( A.REPORT_DATE, 'YYYY/MM/DD') AS REPORT_DATE, "
                        + "               B.COMPANY_DESC || '/' || C.CONTRACT_DESC AS COMPANY_CONTRACT, D.PACKAGE_DESC "
                        + "          FROM HRM_PATADM A, HRM_COMPANY B, HRM_CONTRACTM C, HRM_PACKAGEM D "
                        + "         WHERE A.MR_NO = '#' "
                        + "           AND A.COMPANY_CODE = B.COMPANY_CODE(+) "
                        + "           AND A.CONTRACT_CODE = C.CONTRACT_CODE(+) "
                        + "           AND A.PACKAGE_CODE = D.PACKAGE_CODE(+) "
                        //20170703 zhanglei �����ϸ��ѯ���� START
                        + "		  	  AND A.CASE_NO IN('" + this.getValue("t2") + "','" + this.getValue("t1") + "') "
						//+ "		  	  AND A.CASE_NO IN('160114000729' ,'150123000428') "
                        //20170703 zhanglei �����ϸ��ѯ���� END			  
                        + "           AND A.REPORT_DATE IS NOT NULL) "
                        + "ORDER BY DECODE(REPORT_DATE, '��������', '0', REPORT_DATE)";
        patSql = patSql.replaceFirst("#", MrNo);
        //zhanglei ���
        System.out.println("patSql" + patSql);
        
        result = new TParm(TJDODBTool.getInstance().select(patSql));
        
        //zhanglei ���
        //System.out.println("patSqlresult" + result);
        
        if (result.getErrCode() != 0) {
            this.messageBox("E0035");// ����ʧ��
            return;
        }
        if (result.getCount() <= 0) {
            table.setParmValue(new TParm());
            this.messageBox("E0116");// û������
            return;
        }
//        result.addData("IN_JWS", "����ʷ");
//        result.addData("IN_XYS", "����ʷ");
//        result.addData("IN_YJS", "����ʷ");
//        result.addData("IN_JZS", "����ʷ");
//        result.addData("IN_SSY", "����ѹ");
//        result.addData("IN_SZY", "����ѹ");
//        result.addData("OUT_SG", "���");
//        result.addData("OUT_TZ", "����");
//        result.addData("OUT_TZZS", "����ָ��");
//        result.addData("WOMAN_WY", "����");
//        result.addData("WOMAN_YD", "����");
//        result.addData("WOMAN_GJ", "����");
//        result.addData("WOMAN_QT", "����");
//        result.addData("EYE_LXD", "��϶��");
//        result.addData("EYE_YD", "�۵�");
//        result.addData("EYE_SL", "����");
//        result.addData("EYE_JZ", "����");
//        result.addData("EYE_SJJC", "ɫ�����");
//        result.addData("FACE_BD", "���");
//        result.addData("FACE_YB", "�ʲ�");
//        result.addData("FACE_WED", "�����");
//        result.addData("FACE_TLJC", "�������");
        
        //zhanglei ���
       // System.out.println("��һ��" + result + "��һ��for����" + result.getCount());
        
//        for (int i = 1; i < result.getCount(); i++) {
////            result.addData("IN_JWS", "");// ����ʷ
////            result.addData("IN_XYS", "");// ����ʷ
////            result.addData("IN_YJS", "");// ����ʷ
////            result.addData("IN_JZS", "");// ����ʷ
////            result.addData("IN_SSY", "");// ����ѹ
////            result.addData("IN_SZY", "");// ����ѹ
////            result.addData("OUT_SG", "");// ���
////            result.addData("OUT_TZ", "");// ����
////            result.addData("OUT_TZZS", "");// ����ָ��
////            result.addData("WOMAN_WY", "");// ����
////            result.addData("WOMAN_YD", "");// ����
////            result.addData("WOMAN_GJ", "");// ����
////            result.addData("WOMAN_QT", "");// ����
////            result.addData("EYE_LXD", "");// ��϶��
////            result.addData("EYE_YD", "");// �۵�
////            result.addData("EYE_SL", "");// ����
////            result.addData("EYE_JZ", "");// ���� add by wanglong 20140220
////            result.addData("EYE_SJJC", "");// ɫ����� add by wanglong 20131217
////            result.addData("FACE_BD", "");// ���
////            result.addData("FACE_YB", "");// �ʲ�
////            result.addData("FACE_WED", "");// �����
////            result.addData("FACE_TLJC", "");// ������� add by wanglong 20131217
//            String fileSql =
//                    "SELECT A.FILE_PATH, A.FILE_NAME FROM EMR_FILE_INDEX A, EMR_TEMPLET B "
//                            + " WHERE A.SUBCLASS_CODE = B.SUBCLASS_CODE "
//                            + "   AND B.HRM_FLG = 'Y' AND B.SYSTEM_CODE = 'HRM' "
//                            + "   AND A.FILE_NAME LIKE '%����%' AND A.MR_NO = '"
//                            + result.getData("MR_NO", i) + "' AND A.CASE_NO = '"
//                            + result.getData("CASE_NO", i) + "'";
//            
//            //zhanglei ���
//            System.out.println("fileSql" + fileSql);
//            
//            TParm fileList = new TParm(TJDODBTool.getInstance().select(fileSql));
//            if (fileList.getErrCode() != 0 || fileList.getCount() == 0) {
//                continue;
//            } else {
//                for (int j = 0; j < fileList.getCount(); j++) {
//                	//�ڿ�
//                    if (fileList.getValue("FILE_NAME", j).indexOf("�ڿ�") != -1) {
//                        TParm param = new TParm();
//                        param.setData("FILE_PATH", "JHW\\" + fileList.getValue("FILE_PATH", j));
//                        param.setData("FILE_NAME", fileList.getValue("FILE_NAME", j));
//                        String[] inTitle = new String[]{"jws", "xys", "yjs", "jzs", "xy" };
//                        TParm resultParm =
//                                GetWordValue.getInstance().getWordValueByName(param, inTitle);
//                        if (resultParm.getData("jws_VALUE", 0) != null) {// =====================��ģ��
//                            if (resultParm.getCount("jws") > 0) {// ����ʷ
//                                if (result.getValue("IN_JWS", i).equals("")) {
//                                    result.setData("IN_JWS", i, resultParm.getValue("jws_VALUE", 0));
//                                }
//                            }
//                            if (resultParm.getCount("xys") > 0) {// ����ʷ
//                                if (result.getValue("IN_XYS", i).equals("")) {
//                                    result.setData("IN_XYS", i, resultParm.getValue("xys_VALUE", 0));
//                                }
//                            }
//                            if (resultParm.getCount("yjs") > 0) {// ����
//                                if (result.getValue("IN_YJS", i).equals("")) {
//                                    result.setData("IN_YJS", i, resultParm.getValue("yjs_VALUE", 0));
//                                }
//                            }
//                            if (resultParm.getCount("jzs") > 0) {// ����ʷ
//                                if (result.getValue("IN_JZS", i).equals("")) {
//                                    result.setData("IN_JZS", i, resultParm.getValue("jzs_VALUE", 0));
//                                }
//                            }
//                            if (resultParm.getCount("xy") > 0) {// ����ѹ������ѹ
//                                if (result.getValue("IN_SSY", i).equals("")
//                                        && result.getValue("IN_SZY", i).equals("")) {
//                                    String xy =
//                                            resultParm.getValue("xy_VALUE", 0).split("mmHg")[0]
//                                                    .replaceAll(" ", "");
//                                    result.setData("IN_SSY", i, xy.split("/")[0]);
//                                    try {
//                                        result.setData("IN_SZY", i, xy.split("/")[1]);
//                                    }
//                                    catch (ArrayIndexOutOfBoundsException e) {
//                                        result.setData("IN_SZY", i, "");
//                                    }
//                                }
//                            }
//                        } else {//=====================================���ڿ�ģ��
//                            inTitle =
//                                    new String[]{"in_jws", "in_xys", "in_yjs", "in_jzs",
//                                            "in_xy",// �ڿ�
//                                            "out_sg", "out_tz",
//                                            "out_tzzs", // ���
//                                            "woman_wy", "woman_yd", "woman_gj", "woman_qt", // ����
//                                            "eye_lxd", "eye_yd", "eye_sl","eye_jz", "eye_sjjc", // �ۿ�
//                                            "face_bd", "face_yb", "face_wed", "face_tljc" // ��ٿ�
//                                    };
//                            TParm resultParm1 =
//                                    GetWordValue.getInstance().getWordValueByName(param, inTitle);
//                            if (resultParm1.getCount("in_jws") > 0) {// ����ʷ
//                                if (result.getValue("IN_JWS", i).equals("")) {
//                                    result.setData("IN_JWS", i,
//                                                   resultParm1.getValue("in_jws_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("in_xys") > 0) {// ����ʷ
//                                if (result.getValue("IN_XYS", i).equals("")) {
//                                    result.setData("IN_XYS", i,
//                                                   resultParm1.getValue("in_xys_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("in_yjs") > 0) {// ����
//                                if (result.getValue("IN_YJS", i).equals("")) {
//                                    result.setData("IN_YJS", i,
//                                                   resultParm1.getValue("in_yjs_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("in_jzs") > 0) {// ����ʷ
//                                if (result.getValue("IN_JZS", i).equals("")) {
//                                    result.setData("IN_JZS", i,
//                                                   resultParm1.getValue("in_jzs_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("in_xy") > 0) {// ����ѹ������ѹ
//                                if (result.getValue("IN_SSY", i).equals("")
//                                        && result.getValue("IN_SZY", i).equals("")) {
//                                    String xy =
//                                            resultParm1.getValue("in_xy_VALUE", 0).split("mmHg")[0]
//                                                    .replaceAll(" ", "");
//                                    result.setData("IN_SSY", i, xy.split("/")[0]);
//                                    try {
//                                        result.setData("IN_SZY", i, xy.split("/")[1]);
//                                    }
//                                    catch (ArrayIndexOutOfBoundsException e) {
//                                        result.setData("IN_SZY", i, "");
//                                    }
//                                }
//                            }
//                            if (resultParm1.getCount("out_sg") > 0) {// ���
//                                if (result.getValue("OUT_SG", i).equals("")) {
//                                    result.setData("OUT_SG", i,
//                                                   resultParm1.getValue("out_sg_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("out_tz") > 0) {// ����
//                                if (result.getValue("OUT_TZ", i).equals("")) {
//                                    result.setData("OUT_TZ", i,
//                                                   resultParm1.getValue("out_tz_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("out_tzzs") > 0) {// ����ָ��
//                                if (result.getValue("OUT_TZZS", i).equals("")) {
//                                    result.setData("OUT_TZZS", i,
//                                                   resultParm1.getValue("out_tzzs_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("woman_wy") > 0) {// ����
//                                if (result.getValue("WOMAN_WY", i).equals("")) {
//                                    result.setData("WOMAN_WY", i,
//                                                   resultParm1.getValue("woman_wy_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("woman_yd") > 0) {// ����
//                                if (result.getValue("WOMAN_YD", i).equals("")) {
//                                    result.setData("WOMAN_YD", i,
//                                                   resultParm1.getValue("woman_yd_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("woman_gj") > 0) {// ����
//                                if (result.getValue("WOMAN_GJ", i).equals("")) {
//                                    result.setData("WOMAN_GJ", i,
//                                                   resultParm1.getValue("woman_gj_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("woman_qt") > 0) {// ��������
//                                if (result.getValue("WOMAN_QT", i).equals("")) {
//                                    result.setData("WOMAN_QT", i,
//                                                   resultParm1.getValue("woman_qt_VALUE", 0));
//                                }
//                            }
//
//                            if (resultParm1.getCount("eye_lxd") > 0) {// ��϶��
//                                if (result.getValue("EYE_LXD", i).equals("")) {
//                                    result.setData("EYE_LXD", i,
//                                                   resultParm1.getValue("eye_lxd_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("eye_yd") > 0) {// �۵�
//                                if (result.getValue("EYE_YD", i).equals("")) {
//                                    result.setData("EYE_YD", i,
//                                                   resultParm1.getValue("eye_yd_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("eye_sl") > 0) {// ����
//                                if (result.getValue("EYE_SL", i).equals("")) {
//                                    String sl = resultParm1.getValue("eye_sl_VALUE", 0);
//                                    String slValue =
//                                            sl.replaceFirst("��", "").replaceFirst("��", "")
//                                                    .replaceAll(" ", "");
//                                    if (slValue.equals("")) {
//                                        result.setData("EYE_SL", i, "");
//                                    } else {
//                                        sl = sl.replaceAll(" ", "").replaceFirst("��", " ��");
//                                        result.setData("EYE_SL", i, sl);
//                                    }
//                                }
//                            }
//                            if (resultParm1.getCount("eye_jz") > 0) {// ���� add by wanglong 20140220
//                                if (result.getValue("EYE_JZ", i).equals("")) {
//                                    result.setData("EYE_JZ", i,
//                                                   resultParm1.getValue("eye_jz_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("eye_sjjc") > 0) {// ɫ����� add by wanglong 20131217
//                                if (result.getValue("EYE_SJJC", i).equals("")) {
//                                    result.setData("EYE_SJJC", i,
//                                                   resultParm1.getValue("eye_sjjc_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("face_bd") > 0) {// ���
//                                if (result.getValue("FACE_BD", i).equals("")) {
//                                    result.setData("FACE_BD", i,
//                                                   resultParm1.getValue("face_bd_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("face_yb") > 0) {// �ʲ�
//                                if (result.getValue("FACE_YB", i).equals("")) {
//                                    result.setData("FACE_YB", i,
//                                                   resultParm1.getValue("face_yb_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("face_wed") > 0) {// �����
//                                if (result.getValue("FACE_WED", i).equals("")) {
//                                    result.setData("FACE_WED", i,
//                                                   resultParm1.getValue("face_wed_VALUE", 0));
//                                }
//                            }
//                            if (resultParm1.getCount("face_tljc") > 0) {// ������� add by wanglong 20131217
//                                if (result.getValue("FACE_TLJC", i).equals("")) {
//                                    result.setData("FACE_TLJC", i,
//                                                   resultParm1.getValue("face_tljc_VALUE", 0));
//                                }
//                            }
//                            
//                        }
//                    } else if (fileList.getValue("FILE_NAME", j).indexOf("���") != -1) {
//                        TParm param = new TParm();
//                        param.setData("FILE_PATH", "JHW\\" + fileList.getValue("FILE_PATH", j));
//                        param.setData("FILE_NAME", fileList.getValue("FILE_NAME", j));
//                        String[] inTitle = new String[]{"sg", "tz", "tzzs" };
//                        TParm resultParm =
//                                GetWordValue.getInstance().getWordValueByName(param, inTitle);
//                        if (resultParm.getCount("sg") > 0) {// ���
//                            if (result.getValue("OUT_SG", i).equals("")) {
//                                result.setData("OUT_SG", i, resultParm.getValue("sg_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("tz") > 0) {// ����
//                            if (result.getValue("OUT_TZ", i).equals("")) {
//                                result.setData("OUT_TZ", i, resultParm.getValue("tz_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("tzzs") > 0) {// ����ָ��
//                            if (result.getValue("OUT_TZZS", i).equals("")) {
//                                result.setData("OUT_TZZS", i, resultParm.getValue("tzzs_VALUE", 0));
//                            }
//                        }
//                    } else if (fileList.getValue("FILE_NAME", j).indexOf("����") != -1) {
//                        TParm param = new TParm();
//                        param.setData("FILE_PATH", "JHW\\" + fileList.getValue("FILE_PATH", j));
//                        param.setData("FILE_NAME", fileList.getValue("FILE_NAME", j));
//                        String[] inTitle = new String[]{"wy", "yd", "gj", "gt", "fj", "md", "dc" };
//                        TParm resultParm =
//                                GetWordValue.getInstance().getWordValueByName(param, inTitle);
//                        if (resultParm.getCount("wy") > 0) {// ����
//                            if (result.getValue("WOMAN_WY", i).equals("")) {
//                                result.setData("WOMAN_WY", i, resultParm.getValue("wy_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("yd") > 0) {// ����
//                            if (result.getValue("WOMAN_YD", i).equals("")) {
//                                result.setData("WOMAN_YD", i, resultParm.getValue("yd_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("gj") > 0) {// ����
//                            if (result.getValue("WOMAN_GJ", i).equals("")) {
//                                result.setData("WOMAN_GJ", i, resultParm.getValue("gj_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("gt") > 0) {// ����
//                            if (result.getValue("WOMAN_GT", i).equals("")) {
//                                result.setData("WOMAN_GT", i, resultParm.getValue("gt_VALUE", 0));
//                            }
//                        }
//                    } else if (fileList.getValue("FILE_NAME", j).indexOf("�ۿ�") != -1) {
//                        TParm param = new TParm();
//                        param.setData("FILE_PATH", "JHW\\" + fileList.getValue("FILE_PATH", j));
//                        param.setData("FILE_NAME", fileList.getValue("FILE_NAME", j));
//                        String[] inTitle = new String[]{"lxd", "yd", "sl" };
//                        TParm resultParm =
//                                GetWordValue.getInstance().getWordValueByName(param, inTitle);
//                        if (resultParm.getCount("lxd") > 0) {// ��϶��
//                            if (result.getValue("EYE_LXD", i).equals("")) {
//                                result.setData("EYE_LXD", i, resultParm.getValue("lxd_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("yd") > 0) {// �۵�
//                            if (result.getValue("EYE_YD", i).equals("")) {
//                                result.setData("EYE_YD", i, resultParm.getValue("yd_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("sl") > 0) {// ����
//                            if (result.getValue("EYE_SL", i).equals("")) {
//                                String sl = resultParm.getValue("sl_VALUE", 0);
//                                String slValue =
//                                        sl.replaceFirst("��", "").replaceFirst("��", "")
//                                                .replaceAll(" ", "");
//                                if (slValue.equals("")) {
//                                    result.setData("EYE_SL", i, "");
//                                } else {
//                                    sl = sl.replaceAll(" ", "").replaceFirst("��", " ��");
//                                    result.setData("EYE_SL", i, sl);
//                                }
//                            }
//                        }
//                        if (resultParm.getCount("jz") > 0) { //���� add by wanglong 20140220
//                            if (result.getValue("EYE_JZ", i).equals("")) {
//                                result.setData("EYE_JZ", i, resultParm.getValue("jz_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("sjjc") > 0) { //ɫ����� add by wanglong 20131217
//                            if (result.getValue("EYE_SJJC", i).equals("")) {
//                                result.setData("EYE_SJJC", i, resultParm.getValue("sjjc_VALUE", 0));
//                            }
//                        }
//                    } else if (fileList.getValue("FILE_NAME", j).indexOf("��ٿ�") != -1) {
//                        TParm param = new TParm();
//                        param.setData("FILE_PATH", "JHW\\" + fileList.getValue("FILE_PATH", j));
//                        param.setData("FILE_NAME", fileList.getValue("FILE_NAME", j));
//                        String[] inTitle = new String[]{"bd", "yb", "wed" };
//                        TParm resultParm =
//                                GetWordValue.getInstance().getWordValueByName(param, inTitle);
//                        if (resultParm.getCount("bd") > 0) {// ���
//                            if (result.getValue("FACE_BD", i).equals("")) {
//                                result.setData("FACE_BD", i, resultParm.getValue("bd_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("yb") > 0) {// �ʲ�
//                            if (result.getValue("FACE_YB", i).equals("")) {
//                                result.setData("FACE_YB", i, resultParm.getValue("yb_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("wed") > 0) {// �����
//                            if (result.getValue("FACE_WED", i).equals("")) {
//                                result.setData("FACE_WED", i, resultParm.getValue("wed_VALUE", 0));
//                            }
//                        }
//                        if (resultParm.getCount("tljc") > 0) {// ������� add by wanglong 20131217
//                            if (result.getValue("FACE_TLJC", i).equals("")) {
//                                result.setData("FACE_TLJC", i, resultParm.getValue("tljc_VALUE", 0));
//                            }
//                        }
//                    }
//                }
//            }
//        }
        
//        result.addData("SYSTEM", "COLUMNS", "IN_JWS");
//        result.addData("SYSTEM", "COLUMNS", "IN_XYS");
//        result.addData("SYSTEM", "COLUMNS", "IN_YJS");
//        result.addData("SYSTEM", "COLUMNS", "IN_JZS");
//        result.addData("SYSTEM", "COLUMNS", "IN_SSY");
//        result.addData("SYSTEM", "COLUMNS", "IN_SZY");
//        result.addData("SYSTEM", "COLUMNS", "OUT_SG");
//        result.addData("SYSTEM", "COLUMNS", "OUT_TZ");
//        result.addData("SYSTEM", "COLUMNS", "OUT_TZZS");
//        result.addData("SYSTEM", "COLUMNS", "WOMAN_WY");
//        result.addData("SYSTEM", "COLUMNS", "WOMAN_YD");
//        result.addData("SYSTEM", "COLUMNS", "WOMAN_GJ");
//        result.addData("SYSTEM", "COLUMNS", "WOMAN_QT");
//        result.addData("SYSTEM", "COLUMNS", "EYE_LXD");
//        result.addData("SYSTEM", "COLUMNS", "EYE_YD");
//        result.addData("SYSTEM", "COLUMNS", "EYE_SL");
//        result.addData("SYSTEM", "COLUMNS", "EYE_JZ");
//        result.addData("SYSTEM", "COLUMNS", "EYE_SJJC");
//        result.addData("SYSTEM", "COLUMNS", "FACE_BD");
//        result.addData("SYSTEM", "COLUMNS", "FACE_YB");
//        result.addData("SYSTEM", "COLUMNS", "FACE_WED");
//        result.addData("SYSTEM", "COLUMNS", "FACE_TLJC");
        String lisSql =
                "SELECT D.CASE_NO, C.ORDER_DESC, E.APPLICATION_NO, E.TESTITEM_CODE, E.TESTITEM_CHN_DESC, "
                        + "       E.TEST_VALUE || ' ' || E.TEST_UNIT || "
                        + "       CASE WHEN REGEXP_LIKE( E.TEST_VALUE, '(^[+-]?\\d{0,}\\.?\\d{0,}$)') "
                        + "            THEN (CASE WHEN E.TEST_VALUE > E.UPPE_LIMIT THEN '��' "
                        + "                       WHEN E.TEST_VALUE < E.LOWER_LIMIT THEN '��' END) END AS TEST_VALUE "
                        + "  FROM HRM_CONTRACTD A, HRM_PATADM B, HRM_ORDER C, MED_APPLY D, MED_LIS_RPT E "
                        + " WHERE A.MR_NO = '#'                     "
                        + "   AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "   AND A.MR_NO = B.MR_NO                 "
                        + "   AND B.CASE_NO = C.CASE_NO             "
                        + "   AND C.CAT1_TYPE = 'LIS'               "
                        + "   AND C.MED_APPLY_NO IS NOT NULL        "
                        + "   AND C.CAT1_TYPE = D.CAT1_TYPE         "
                        + "   AND C.MED_APPLY_NO = D.APPLICATION_NO "
                        + "   AND C.CASE_NO = D.ORDER_NO            "
                        + "   AND D.CAT1_TYPE = E.CAT1_TYPE           "
                        + "   AND D.APPLICATION_NO = E.APPLICATION_NO "
                        + "   AND D.ORDER_NO = E.ORDER_NO             "
                        + "   AND (E.TEST_VALUE > E.UPPE_LIMIT OR E.TEST_VALUE < E.LOWER_LIMIT) "
                        + "ORDER BY E.TESTITEM_CODE, D.CASE_NO";
        lisSql = lisSql.replaceFirst("#", MrNo);
        TParm lisParm = new TParm(TJDODBTool.getInstance().select(lisSql));
        Set<String> testItemSet = new TreeSet<String>();
        for (int i = 0; i < lisParm.getCount(); i++) {
            testItemSet.add(lisParm.getValue("TESTITEM_CODE", i));
        }
        for (String testItem : testItemSet) {
            result.addData("SYSTEM", "COLUMNS", testItem);
        }
        for (int i = 0; i < result.getCount(); i++) {
            for (String testItem : testItemSet) {
                result.addData(testItem, "");
            }
            for (int j = 0; j < lisParm.getCount(); j++) {
                if (0 == i) {
                    result.setData(lisParm.getValue("TESTITEM_CODE", j), i,
                                   lisParm.getValue("TESTITEM_CHN_DESC", j));
                } else if (result.getValue("CASE_NO", i).equals(lisParm.getValue("CASE_NO", j))) {
                    result.setData(lisParm.getValue("TESTITEM_CODE", j), i,
                                   lisParm.getValue("TEST_VALUE", j));
                }
            }
        }
        String risSql =
                "SELECT C.CASE_NO, C.ORDER_CODE, D.APPLICATION_NO, "
                        + "       C.ORDER_DESC, REPLACE(E.OUTCOME_CONCLUSION,' ','') AS OUTCOME_CONCLUSION "
                        + "  FROM HRM_CONTRACTD A, HRM_PATADM B, HRM_ORDER C, MED_APPLY D, MED_RPTDTL E "
                        + " WHERE A.MR_NO = '#'                     "
                        + "   AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "   AND A.MR_NO = B.MR_NO                 "
                        + "   AND B.CASE_NO = C.CASE_NO             "
                        + "   AND C.CAT1_TYPE = 'RIS'               "
                        + "   AND C.MED_APPLY_NO IS NOT NULL        "
                        + "   AND C.CAT1_TYPE = D.CAT1_TYPE         "
                        + "   AND C.MED_APPLY_NO = D.APPLICATION_NO "
                        + "   AND C.CASE_NO = D.ORDER_NO            "
                        + "   AND D.CAT1_TYPE = E.CAT1_TYPE         "
                        + "   AND D.APPLICATION_NO = E.APPLICATION_NO "
                        + "   AND E.OUTCOME_TYPE = 'H'                "
                        + "ORDER BY C.DEV_CODE, C.ORDER_DESC, D.CASE_NO";
        risSql = risSql.replaceFirst("#", MrNo);
        TParm risParm = new TParm(TJDODBTool.getInstance().select(risSql));
        Set<String> outComeSet = new TreeSet<String>();
        for (int i = 0; i < risParm.getCount(); i++) {
            outComeSet.add(risParm.getValue("ORDER_CODE", i));
        }
        for (String outCome : outComeSet) {
            result.addData("SYSTEM", "COLUMNS", outCome);
        }
        for (int i = 0; i < result.getCount(); i++) {
            for (String outCome : outComeSet) {
                result.addData(outCome, "");
            }
            for (int j = 0; j < risParm.getCount(); j++) {
                if (0 == i) {
                    result.setData(risParm.getValue("ORDER_CODE", j), i,
                                   risParm.getValue("ORDER_DESC", j));
                } else if (result.getValue("CASE_NO", i).equals(risParm.getValue("CASE_NO", j))) {
                    result.setData(risParm.getValue("ORDER_CODE", j), i,
                                   risParm.getValue("OUTCOME_CONCLUSION", j));
                }
            }
        }
        //===========================��ʼ��װ���ս�������ʾ������=============================
        headerName = "��Ŀ,200";// ���Header
        String parmMap = "COL_1";// ���ParmMap
        String columnHorizontalAlignment = "0,left";// ���ColumnHorizontalAlignment
        TParm parmValue = new TParm();// �������
        Vector<String> rowHeadVct = (Vector<String>) result.getGroupData("SYSTEM").get("COLUMNS");
        for (int i = rowHeadVct.size() - 1; i >= 0; i--) {
            if (rowHeadVct.get(i).equals("CASE_NO") || rowHeadVct.get(i).equals("MR_NO")
                    || rowHeadVct.get(i).equals("REPORT_DATE")) {// ���ս������ʾ����źͲ����ţ���������
                rowHeadVct.remove(i);
            }
        }
        for (int i = 0; i < result.getCount(); i++) {
            if (i > 0) {
                headerName += ";" + result.getValue("REPORT_DATE", i) + ",250";
                parmMap += ";" + "COL_" + (i + 1);
                columnHorizontalAlignment += ";" + i + ",left";
            }
            for (String rowHead : rowHeadVct) {// ��ת��
                parmValue.addData("COL_" + (i + 1), result.getValue(rowHead, i));
            }
        }
        parmValue.setCount(rowHeadVct.size());
        table.setDSValue();
        table.setHeader(headerName);// ���硰��Ŀ�v��������,250;2012/02/02,250;2013/03/03,250��
        //this.messageBox(headerName.substring(22, 32));
        table.setParmMap(parmMap);// ���硰COL_1;COL_2;COL_3��
        table.setHorizontalAlignmentData(columnHorizontalAlignment);// ���硰0,left;1,left;2,left��
        table.setParmValue(parmValue);
    }

    /**
     * ���Excel
     */
    public void onExport() {
        if (table.getRowCount() < 1) {
            messageBox("E0116");// û������
            return;
        }
        ExportExcelUtil.getInstance().exportExcelWithoutTime(table,
                                                             this.getText("PAT_NAME") + "_����߶˷���");

    }

    /**
     * ���
     */
    public void onClear() {
        this.clearValue("MR_NO;PAT_NAME;SEX_CODE;AGE;t1;t2");
        table.removeRowAll();
    }
    
    /**
     * ��ӡ
     */
    public void onPrint(){
    	if(headerName.length() < 22) {
			this.messageBox("��ѡ�����ε�����");
			return;
		}
    	//�õ�����������
    	TParm tableParm = TABLE.getParmValue();
    	//��ӡ����
    	TParm prtParm = new TParm();
    	// �������
    	TParm parm = new TParm();
    	//��ñ���
    	prtParm.setData("Title", "TEXT", this.getValue("PAT_NAME") + "�Ľ���������");
    	//��ñ�ͷ
    	//��õ�һ��
		parm.addData("COL_1", headerName.substring(0,2));
		//��õڶ���
		parm.addData("COL_2", headerName.substring(7,17));
		//��õ�����
		
		parm.addData("COL_3", headerName.substring(22,32));
    	for (int i = 0; i < TABLE.getRowCount(); i++) {
    		//��õ�һ��
			parm.addData("COL_1", tableParm.getValue("COL_1", i));
			//��õ�һ��
			parm.addData("COL_2", tableParm.getValue("COL_2", i));
			//��õ�һ��
			parm.addData("COL_3", tableParm.getValue("COL_3", i));
    	}
    	
    	parm.setCount(parm.getCount("COL_1"));
    	parm.addData("SYSTEM", "COLUMNS", "COL_1");
    	parm.addData("SYSTEM", "COLUMNS", "COL_2");
    	parm.addData("SYSTEM", "COLUMNS", "COL_3");
    	prtParm.setData("TABLE", parm.getData());
    	//this.messageBox("" + prtParm);
    	// ���ô�ӡ����,����·��
    	this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMAdvancedReport.jhw",
    			prtParm);
//    	this.openPrintWindow("%ROOT%\\config\\prt\\HRM\\HRMAdvancedReport.jhw",
//    			prtParm);
    }

}
