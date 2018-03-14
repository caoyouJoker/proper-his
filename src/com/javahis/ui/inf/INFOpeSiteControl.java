package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
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

public class INFOpeSiteControl extends TControl {
	public void onInit() {
		super.onInit();
		initPage();
	}
	private void initPage() {
		this.clearValue("START_DATE;END_DATE");
		//��ʼ����ѯ����ʱ
		Timestamp startime = getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
				SystemTool.getInstance().getDate(),"yyyyMMdd")));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool.getInstance().getDate()),-1);
		String endtime = StringTool.getString(rollDay,"yyyy/MM/dd");
		this.setValue("START_DATE", startime);
		this.setValue("END_DATE", endtime);
		this.callFunction("UI|TABLE|setParmValue", new TParm());

		// ��������
		this.setValue("REGION_CODE", "H01");
	}
	
	/**
	 * ��ѯ
	 * @throws ParseException 
	 */
	public void onQuery() throws ParseException {
		TParm result = new TParm();
		String regionCode = this.getValueString("REGION_CODE");
		String startDate = this.getValueString("START_DATE").substring(0, 10);
		String endDate = this.getValueString("END_DATE").substring(0, 10).replace("/", "-");
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sql = "SELECT ROWNUM, A.CASE_NO, A.MR_NO, A.INF_DATE, A.OP_CODE, A.ANA_TYPE, "
				+ " C.TYPE_CODE, E.OPE_INCISION, C.OP_DATE, "
				+ " D.PART_CODE, E.LAPA_FLG, F.ANTIBIOTIC_WAY, F.ORDER_DESC, F.OPBOOK_SEQ, "
				+ " F.EFF_DATE, F.DC_DATE, G.PAT_NAME, G.SKIN_PREPARATION_FLG "
				+ " FROM INF_CASE A, ADM_INP B, OPE_OPBOOK C, INF_ICDPART D, SYS_OPERATIONICD E, "
				+ "	ODI_ORDER F, INW_TRANSFERSHEET_WO G "
				+ " WHERE A.OPBOOK_SEQ =C.OPBOOK_SEQ "
				+ "	AND A.CASE_NO = B.CASE_NO "
				+ "	AND A.INF_NO = D.INF_NO "
				+ "	AND A.INFCASE_SEQ = D.INFCASE_SEQ "
				+ " AND A.OP_CODE = E.OPERATION_ICD"
				+ " AND A.OPBOOK_SEQ = F.OPBOOK_SEQ"
				+ " AND A.OPBOOK_SEQ = G.OPBOOK_SEQ"
				+ " AND D.MAIN_FLG = 'Y' "
				+ " AND A.OP_DATE IS NOT NULL"
				+ " AND F.DC_DATE IS NOT NULL";
		if(!"".equals(regionCode)){
			sql += " AND B.REGION_CODE = '"+ regionCode +"' ";
		}
		if(!"".equals(startDate) && !"".equals(endDate)){
			sql += " AND B.IN_DATE BETWEEN TO_DATE('"
					+ startDate
					+ " 000000','yyyy-MM-dd HH24miss') AND TO_DATE('"
					+ endDate
					+ " 235959','yyyy-MM-dd HH24miss') ";
		}
//		sql += "SELECT FACT_VOL FROM BMS_BLOOD";
		System.out.println("sql:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("parm:"+parm);
		String startimeSql = "SELECT MIN(START_TIME) AS START_TIME FROM OPE_EVENT WHERE EVENT_ID = '������ʼ' AND OPE_BOOK_NO = '"+ parm.getValue("OPBOOK_SEQ") +"' ";
		TParm sparm = new TParm(TJDODBTool.getInstance().select(startimeSql));
		String endtimeSql = "SELECT MAX(END_TIME) AS END_TIME FROM OPE_EVENT WHERE OPE_BOOK_NO = '"+ parm.getValue("OPBOOK_SEQ") +"'";
		TParm eparm = new TParm(TJDODBTool.getInstance().select(endtimeSql));
		for(int i=0;i<parm.getCount("ORDER_DESC");i++){
			if(!"".equals(parm.getData("ANTIBIOTIC_WAY",i))){
				result.addData("ROWNUM", parm.getData("ROWNUM",i));//���
				result.addData("MR_NO", parm.getData("MR_NO",i));//������
				result.addData("PAT_NAME", parm.getData("PAT_NAME",i));//����
				result.addData("INF_DATE", parm.getData("INF_DATE",i));//��Ⱦ����
				result.addData("OP_DATE", parm.getData("OP_DATE",i));//��������
				result.addData("OP_CODE", parm.getData("OP_CODE",i));//��������
				result.addData("INF_OPESITE", parm.getData("PART_CODE",i));//��Ⱦ������λ
				result.addData("TYPE_CODE", parm.getData("TYPE_CODE",i));//��������
				result.addData("OPE_INCISION", parm.getData("OPE_INCISION",i));//�п�����
				if("Y".equals(parm.getData("SKIN_PREPARATION_FLG",i))){//��ǰƤ��׼��
					result.addData("SKIN_PREPARATION_FLG", "����Ƥ���������");
				} else {
					result.addData("SKIN_PREPARATION_FLG", "");
				}
				result.addData("ANA_TYPE", parm.getData("ANA_TYPE",i));//����ʽ
				result.addData("NNIS_CODE", "");//�������յȼ�
				String opStartDate = sparm.getData("START_DATE",0).toString();
				String opEndDate = eparm.getData("END_DATE",0).toString();
				long opTime = sdf.parse(opEndDate).getTime() - sdf.parse(opStartDate).getTime();
				result.addData("OP_TIME", opTime/(1000*60));//��������ʱ��
				result.addData("FACT_BLEED", "");//������س�Ѫ
				result.addData("FACT_VOL", "");//��Ѫ
				result.addData("IMPLANT", "");//ֲ����
				result.addData("IMPLANT_TYPE", "");//ֲ��������
				if("Y".equals(parm.getData("LAPA_FLG",i))){//ǻ������
					result.addData("LAPA_FLG", "��");
				} else {
					result.addData("LAPA_FLG", "��");
				}
				String dcDate = parm.getData("DC_DATE",i).toString();
				String opDate = parm.getData("OP_DATE",i).toString();
				long opd = sdf.parse(opDate).getTime() - sdf.parse(dcDate).getTime();
				if("01".equals(parm.getData("ANTIBIOTIC_WAY",i)) && opd/(1000*60) > 0){
					result.addData("ANTIBIOTIC_WAY", "ʹ��");//��ǰԤ����ҩ
				} else {
					result.addData("ANTIBIOTIC_WAY", "");
				}
				result.addData("ORDER_DESC", parm.getData("ORDER_DESC",i));//Ԥ���ÿ���ҩ������
				if(opd/(1000*60)>=30 && opd/(1000*60)<= 120){
					result.addData("OPERATION_FLG", "��");//�Ƿ���ϡ�����ǰ0.5--2Сʱ�������յ���ʼʱ������
				} else {
					result.addData("OPERATION_FLG", "��");
				}
				if(opTime/(1000*60)>=180 && !"".equals(parm.getData("OPBOOK_SEQ")) && !"".equals(parm.getData("ORDER_DESC"))){
					result.addData("ANTIBIOSIS_FLG","��");//����ʱ�����3Сʱ�ߣ�����׷�ӿ���ҩ��
				} else {
					result.addData("ANTIBIOSIS_FLG", "��");
				}
				
			}
		}
//		System.out.println("result:"+result);
		this.getTable("TABLE").setParmValue(result);
		if (this.getTable("TABLE").getRowCount() < 1) {
			// ��������
			this.messageBox("��������");
		}
	}
	
	/**
	 * ����excel
	 */
	public void onExport() {
		if (getTable("TABLE").getRowCount() <= 0) {
			this.messageBox("û�е�������");
			return;
		}
		INFOpeSiteUtil.getInstance().exportExcel(getTable("TABLE"),
				"������λ��Ⱦ��������");
	}
	

	/**
	 * ���
	 */
	public void onClear() {
		initPage();
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
