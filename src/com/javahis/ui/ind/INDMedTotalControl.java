package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
/**
* <p>Title: </p>
*
* <p>Description: סԺҩ������ͳ��</p>
*
* <p>Copyright: Copyright (c) 2009</p>
*
* <p>Company: javahis</p>
*
* @author lij
* @version 1.0
*/
public class INDMedTotalControl extends TControl {
	public void onInit() {
		super.onInit();
		initPage();
	}
	private void initPage() {
		this.clearValue("START_DATE;END_DATE;DEPT_CODE;BIRTH_FLG");
		//��ʼ����ѯ����ʱ
		Timestamp date = getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
				SystemTool.getInstance().getDate(),"yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()),-1);
		String end_day = StringTool.getString(rollDay,"yyyy/MM/dd 23:59:59");
		this.setValue("START_DATE", date);
		this.setValue("END_DATE", end_day);
		Timestamp birth_date = SystemTool.getInstance().getDate();//add by wangjc 20171204 �������ղ�ѯ����
		this.setValue("BIRTH_DATE", birth_date);//add by wangjc 20171204 �������ղ�ѯ����
		this.callFunction("UI|TABLE|setParmValue", new TParm());

	}

	/**
	 * ��ѯ
	 */
	public void onQuery(){
		TParm result = new TParm();
		String startDate = this.getValueString("START_DATE").substring(0, 10);
		String endDate = this.getValueString("END_DATE").substring(0, 10);
		String deptCode = this.getValueString("DEPT_CODE");
		String sql = "SELECT B.DEPT_CHN_DESC, COUNT(DEPT_CHN_DESC) AS TOT_TOTAL "
				+ " FROM ODI_ORDER A,SYS_DEPT B, SYS_PATINFO C "
				+ " WHERE A.DEPT_CODE = B.DEPT_CODE "
				+ " AND A.CAT1_TYPE = 'PHA' "
				+ " AND A.MR_NO = C.MR_NO ";
		if(!"".equals(startDate) && !"".equals(endDate)){
			sql += " AND A.ORDER_DATE BETWEEN TO_DATE('"
					+ startDate
					+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
					+ endDate
					+ " 235959','yyyy-MM-dd HH24miss') ";
		}
		if(!"".equals(deptCode)){
			sql += " AND A.DEPT_CODE = '"+ deptCode +"' "; 
		}
		//�������� add by wangjc 20171204 �������ղ�ѯ���� start
		String birthDate = this.getValueString("BIRTH_DATE").substring(0, 10);
		String birthFlg = this.getValueString("BIRTH_FLG");
		if(!"".equals(birthDate)){
			if(">".equals(birthFlg)){
				sql += "AND C.BIRTH_DATE > TO_DATE('"+ birthDate +" 235959', 'yyyy-MM-dd HH24miss')";
			} else if("<".equals(birthFlg)){
				sql += "AND C.BIRTH_DATE < TO_DATE('"+ birthDate +" 000000', 'yyyy-MM-dd HH24miss')";
			} else if("=".equals(birthFlg)){
				sql += " AND C.BIRTH_DATE BETWEEN TO_DATE('"+ birthDate
						+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
						+ birthDate
						+ " 235959','yyyy-MM-dd HH24miss') ";
			} else if(">=".equals(birthFlg)){
				sql += "AND C.BIRTH_DATE >= TO_DATE('"+ birthDate +" 000000', 'yyyy-MM-dd HH24miss')";
			} else if("<=".equals(birthFlg)){
				sql += "AND C.BIRTH_DATE <= TO_DATE('"+ birthDate +" 235959', 'yyyy-MM-dd HH24miss')";
			}
		}
		//�������� add by wangjc 20171204 �������ղ�ѯ���� end
		sql += " GROUP BY B.DEPT_CHN_DESC ORDER BY B.DEPT_CHN_DESC";
//		System.out.println("sql:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		int sum = 0;
		int count = 0;
		for(int i=0;i<parm.getCount();i++){
			result.addData("DEPT_CHN_DESC", parm.getData("DEPT_CHN_DESC",i));
			result.addData("TOT_TOTAL", parm.getData("TOT_TOTAL",i));
			count = Integer.parseInt(parm.getData("TOT_TOTAL",i).toString());
			sum += count;
		}
		if(parm.getCount("DEPT_CHN_DESC") > 1){
			result.addData("DEPT_CHN_DESC", "�ϼ�");
			result.addData("TOT_TOTAL", sum);
		}
		
		this.getTable("TABLE").setParmValue(result);
		if (this.getTable("TABLE").getRowCount() < 1) {
			// ��������
			this.messageBox("��������");
		}
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		initPage();
	}
	
	/**
	 * ���Excel
	 */
	public void onExport() {
		if (getTable("TABLE").getRowCount() <= 0) {
			this.messageBox("û��Ҫ���������");
			return;
		}

		ExportExcelUtil.getInstance()
				.exportExcel(getTable("TABLE"), "סԺҩ������ͳ��");

	}
	/**
     * ��ӡ
     */
//    public void onPrint(){
//    	getTable("TABLE").acceptText();
//    	TParm tableParm = getTable("TABLE").getShowParmValue();
//    	if(tableParm.getCount()<0){
//    		this.messageBox("û��Ҫ��ӡ������");
//    	}
//    	Timestamp datetime = SystemTool.getInstance().getDate();
//    	TParm data = new TParm();
//    	data.setData("TITLE", "TEXT", "סԺҩ������ͳ�Ʊ���");
//    	data.setData("USER", "TEXT", "�Ʊ��ˣ�"+Operator.getName());
//    	data.setData("DATE", "TEXT", "�Ʊ����ڣ�"+datetime.toString().substring(0, 10).replace('-', '/'));
//    	
//    	TParm parm = new TParm();
//    	for(int i=0;i<tableParm.getCount();i++){
//    		parm.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i));
//    		parm.addData("TOT_TOTAL", tableParm.getValue("TOT_TOTAL", i));
//    	}
//    	
//    	parm.setCount(parm.getCount("DEPT_CHN_DESC"));  
//    	parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
//        parm.addData("SYSTEM", "COLUMNS", "TOT_TOTAL");
//        
//        data.setData("TABLE",parm.getData());
//        this.openPrintWindow("%ROOT%\\config\\prt\\ind\\INDMedTotalPrint.jhw", data);
//    	
//    }
	/**
	 * �õ��ϸ���
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = defaultFormatter.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}
	/**
	 * ��ʼ��ʱ������
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}

	/**
	 * ȡ��Table�ؼ�
	 * 
	 * @param tableTag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}
}
