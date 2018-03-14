package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p> Title: ��ֵ����SN��ͳ�Ʊ��� </p>
 * 
 * <p>  Description: ��ֵ����SN��ͳ�Ʊ��� </p>
 * 
 * <p> Copyright: Copyright (c) 2014 </p>
 * 
 * <p> Company: JavaHis </p>
 * 
 * @author fux 20140513
 * @version 1.0
 */
public class INVQuerySNControl extends TControl {

	private static TTable mainTable;

	/**
	 * ��ʼ������
	 */
    public void onInit() {
        super.init();
        // ���TABLE����
        mainTable = (TTable) getComponent("TABLE");
        TParm parm = new TParm();
        // ���õ����˵�
        getTextField("INV_CODE")
                .setPopupMenuParameter("UD",
                                       getConfigParm()
                                               .newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
                                       parm);
        // ������ܷ���ֵ����
        getTextField("INV_CODE").addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        this.initPage();
        // if (this.getPopedem("ALL")) {
        // this.callFunction("UI|ORG_CODE|setEnabled", false);
        // }else{
        // this.callFunction("UI|ORG_CODE|setEnabled", true);
        // }
    }

	/**
	 * ��ʼ��������
	 */
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
//        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        this.setValue("START_TIME", date.toString().substring(0, 10).replace('-', '/')
                + " 00:00:00");
        this.setValue("END_TIME", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("ORG_CODE", Operator.getDept());
    }

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		mainTable.removeRowAll();// �������
		String org_code = "";
		String inv_code = "";
		String startTime = "";
		String endTime = "";
		// START_TIME END_TIME

		// ���Ŵ���
		org_code = getValueString("ORG_CODE");
		if (org_code == null || org_code.length() <= 0) {
			this.messageBox("��ѡ�����ղ���");
			return;
		}
		// ���ʱ���
		inv_code = getValueString("INV_CODE");
		// if (inv_code == null || inv_code.length() <= 0) {
		// this.messageBox("��ѡ���ѯ����");
		// return;
		// }
		// ��Ӧ��
		String sup_code = getValueString("SUP_CODE");
		// ���ʷ���
		String kind = getValueString("KIND");

		// ��ѯsql
		// ���ʱ���,150;��������,200;ʹ������,100;����ͺ�,180;���(SN),200;��Ʒ����,100;����,100;��Ӧ����,200
		// INV_CODE;INV_CHN_DESC;BILL_DATE;DESCRIPTION;BARCODE;BATCH_NO;ORG_CODE;SUP_CODE
		// ������˵���Ⱥ��Ҳ�����м�¼���ᱻ��ʾ
		// ������˵���Ⱥ��������м�¼���ᱻ��ʾ
        String sql =
                "SELECT  T.INV_CODE,T.INV_CHN_DESC,F.BILL_DATE,T.DESCRIPTION,B.ORGIN_CODE,"
                        + "    B.BATCH_NO,B.ORG_CODE,T.SUP_CODE , F.ORDER_DEPT_CODE, F.ORDER_DR_CODE "// wanglong add 20140826
                        + "  FROM INV_BASE T, INV_STOCKDD B, SYS_DEPT C, SYS_SUPPLIER E, SPC_INV_RECORD F "// ��ֵ
                        + " WHERE T.SEQMAN_FLG = 'Y'            "
                        + "   AND T.EXPENSIVE_FLG = 'Y'         "
                        + "   AND B.WAST_FLG = 'Y'              "
                        + "   AND B.ORGIN_CODE IS NOT NULL      "
                        + "	  AND B.INV_CODE = T.INV_CODE       "
                        + "	  AND B.ORG_CODE = C.DEPT_CODE      "
                        + "	  AND T.SUP_CODE = E.SUP_CODE       "
                        + "   AND B.RFID = F.BAR_CODE       ";
		startTime = this.getText("START_TIME");
		endTime = this.getText("END_TIME");
		// ʹ������
		 
		if (!"".equals(startTime) && !"".equals(endTime)) {
			startTime = startTime.substring(0, 10).replaceAll("/", "").trim();     
			endTime = endTime.substring(0, 10).replaceAll("/", "").trim();  
			sql += " AND F.BILL_DATE BETWEEN TO_DATE('"+startTime+"000000"+"','YYYYMMDDHH24MISS') " +
					" AND TO_DATE('"+endTime+"235959"+"','YYYYMMDDHH24MISS')";  
		}

		// ����
		if (!"".equals(org_code)) {   
			sql += " AND B.ORG_CODE = '" + org_code + "'";
		}
		// ���ʱ���
		if (!"".equals(inv_code)) {
			sql += " AND T.INV_CODE = '" + inv_code + "'";
		}
		// ��Ӧ��
		if (!"".equals(sup_code)) {
			sql += " AND T.SUP_CODE = '" + sup_code + "'";
		}
		sql += " ORDER BY T.INV_CODE,F.BILL_DATE ";
//		System.out.println("sql----------------------->" + sql);
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));

		if (resultParm.getCount() < 0) {
			this.messageBox("û��Ҫ��ѯ������");
			return;
		}

		mainTable.setParmValue(resultParm);

	}

	/**
	 * ÿ��λ�Ӷ��Ŵ���
	 */
	public String feeConversion(String fee) {
		String str1 = "";
		String[] s = fee.split("\\.");// ��"."���ָ�

		str1 = new StringBuilder(s[0].toString()).reverse().toString(); // �Ƚ��ַ����ߵ�˳��
		String str2 = "";
		for (int i = 0; i < str1.length(); i++) {
			if (i * 3 + 3 > str1.length()) {
				str2 += str1.substring(i * 3, str1.length());
				break;
			}
			str2 += str1.substring(i * 3, i * 3 + 3) + ",";
		}
		if (str2.endsWith(",")) {
			str2 = str2.substring(0, str2.length() - 1);
		}
		// ����ٽ�˳��ת����
		String str3 = new StringBuilder(str2).reverse().toString();
		// ����С��������
		StringBuffer str4 = new StringBuffer(str3);
		str4 = str4.append(".").append(s[1]);
		return str4.toString();
	}

	/**
	 * �������
	 */
	public void onExcel() {   
		if (mainTable.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(mainTable, "��ֵ����SN��ͳ�Ʊ���");
		} else {
			this.messageBox("û�л������");
			return;
		}
	}
   
	/**
	 * ��շ���
	 */        
	public void onClear() {
		// fux modify 20140313   
		this.clearValue("START_TIME;END_TIME;ORG_CODE;SUP_CODE;INV_CODE;INV_DESC");
		mainTable.removeRowAll();
        this.initPage();
	}

	/**
	 * �����ѡ��
	 */
	public void onChangeRadioButton() {
		if (getRadioButton("VALID_DATE_C").isSelected()) {
			getTextFormat("VALID_DATE").setEnabled(true);
		} else {
			getTextFormat("VALID_DATE").setEnabled(false);
			this.clearValue("VALID_DATE");
		}
	}

	/**
	 * �õ�TextField����
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * ����ָ�����·ݺ������Ӽ�������Ҫ���·ݺ�����
	 * 
	 * @param Month
	 *            String �ƶ��·� ��ʽ:yyyyMM
	 * @param Day
	 *            String �ƶ��·� ��ʽ:dd
	 * @param num
	 *            String �Ӽ������� ����Ϊ��λ
	 * @return String
	 */
	public String rollMonth(String Month, String Day, int num) {
		if (Month.trim().length() <= 0) {
			return "";
		}
		Timestamp time = StringTool.getTimestamp(Month, "yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time.getTime()));
		// ��ǰ�£�num
		cal.add(cal.MONTH, num);
		// ���¸���1����Ϊ���ڳ�ʼֵ
		cal.set(cal.DATE, 1);
		Timestamp month = new Timestamp(cal.getTimeInMillis());
		String result = StringTool.getString(month, "yyyyMM");
		String lastDayOfMonth = getLastDayOfMonth(result);
		if (TypeTool.getInt(Day) > TypeTool.getInt(lastDayOfMonth)) {
			result += lastDayOfMonth;
		} else {
			result += Day;
		}
		return result;
	}

	/**
	 * ��ȡָ���·ݵ����һ�������
	 * 
	 * @param date
	 *            String ��ʽ YYYYMM
	 * @return Timestamp
	 */
	public String getLastDayOfMonth(String date) {
		if (date.trim().length() <= 0) {
			return "";
		}
		Timestamp time = StringTool.getTimestamp(date, "yyyyMM");
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date(time.getTime()));
		// ��ǰ�£�1�����¸���
		cal.add(cal.MONTH, 1);
		// ���¸���1����Ϊ���ڳ�ʼֵ
		cal.set(cal.DATE, 1);
		// �¸���1�ż�ȥһ�죬���õ���ǰ�����һ��
		cal.add(cal.DATE, -1);
		Timestamp result = new Timestamp(cal.getTimeInMillis());
		return StringTool.getString(result, "dd");
	}

	/**
	 * ��ȡָ��n����ǰ������
	 */
	public String getMonthDay(int no) {
		String str = "";
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		Calendar lastDate = Calendar.getInstance();
		// lastDate.set(Calendar.DATE, no);// ��Ϊ��ǰ�µ�n��
		lastDate.add(Calendar.MONTH, +no);// ��n���£���Ϊ���µ�1��
		// lastDate.add(Calendar.DATE,-1);//��ȥһ�죬��Ϊ�������һ��

		str = sdf.format(lastDate.getTime());
		return str;
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		if (parm == null) {
			return;
		}
		String order_code = parm.getValue("INV_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("INV_CODE").setValue(order_code);
		String order_desc = parm.getValue("INV_CHN_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("INV_DESC").setValue(order_desc);
	}

}
