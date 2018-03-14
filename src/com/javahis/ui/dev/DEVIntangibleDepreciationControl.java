package com.javahis.ui.dev;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.tiis.util.TiMath;
/**
 * <p>
 * Title:�����ʲ�̯�������
 * </p>
 *
 * <p>
 * Description:�����ʲ�̯�������
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: bluecore
 * </p>
 *
 * @author fux 2013.09.23
 * @version 1.0
 */
public class DEVIntangibleDepreciationControl extends TControl{
	
	private TTable table;
	
	public DEVIntangibleDepreciationControl(){
		super();
	}
	/**
	 * ��ʼ������
	 */ 
	public void onInit(){
		initPage();
	}
	private void initPage(){ 
		table = (TTable) this.getComponent("TABLE");
		/**  ���ڣ�Ĭ�ϵ�ǰ���� ��ʽ��YYYY/MM  **/
		String now = StringTool.getString(SystemTool.getInstance().getDate(),
		"yyyyMM");
		this.setValue("Q_DATE", StringTool.getTimestamp(now, "yyyyMM"));// ��ѯ����
	}
	
	/**
	 * ��ѯ����
	 */
	public void onQuery(){ 
		
//		if(devKind.length() == 0){
//			messageBox("���������Ϊ��!");
//			return;
//		} 
		String qdate = StringTool.getString(TypeTool
				.getTimestamp(getValue("Q_DATE")), "yyyy-MM");
		if (qdate.length() == 0) {
			messageBox("���ڲ���ȷ!"); 
			return;
		} 
		String yearAndMonth[] = qdate.split("-");
		//��ȡĳ��ĳ�µĵ�һ�졾�꣺yearAndMonth[0]  �£�yearAndMonth[1]��
		String firstDayOfMonth = getFirstDayOfMonth(Integer.parseInt(yearAndMonth[0]),Integer.parseInt(yearAndMonth[1])-1);
		//��ȡĳ��ĳ�µ����һ��
		String lastDayOfMonth = getLastDayOfMonth(Integer.parseInt(yearAndMonth[0]),Integer.parseInt(yearAndMonth[1])-1);
		
		StringBuffer sql = new StringBuffer(); 
		//(MONTHS_BETWEEN(TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM'), 'YYYY-MM'),TO_DATE(TO_CHAR(B.INWAREHOUSE_DATE,'YYYY-MM'), 'YYYY-MM'))+1)*12 AS USED_YEAR
		//+1 �ж��Ƿ���ͬһ��
		
		sql.append("SELECT T.INTANGIBLE_FLG,T.DEV_CODE ,T.DEV_CHN_DESC AS DEV_NAME,T.SPECIFICATION AS DEV_SPEC,")
				.append("		E.UNIT_CHN_DESC AS STOCK_UNIT,B.STOCK_QTY AS DEV_NUM,B.UNIT_PRICE,")
				.append("		C.DEPT_CHN_DESC AS USED_DEPT,") 
				.append("		TO_CHAR(B.INWAREHOUSE_DATE, 'YYYY/MM/DD') AS BUY_DATE,D.CHN_DESC AS DEV_KIND,12*T.DEP_DEADLINE AS DEP_YEAR,")
				.append("		MONTHS_BETWEEN(TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM'), 'YYYY-MM'),TO_DATE(TO_CHAR(B.INWAREHOUSE_DATE,'YYYY-MM'), 'YYYY-MM')) AS USED_YEAR,")
				.append("		B.MDEP_PRICE AS DEP_PRICE_M,B.DEP_PRICE AS DEP_PRICE_ALL,B.CURR_PRICE AS DEP_PRICE_M_JZ")
				.append(" FROM DEV_BASE T,DEV_STOCKDD B, SYS_DEPT C, SYS_DICTIONARY D, SYS_UNIT E ")
				.append(" WHERE T.DEV_CODE = B.DEV_CODE AND T.INTANGIBLE_FLG ='Y'") 
				.append(" AND B.DEPT_CODE = C.DEPT_CODE ") 
				.append(" AND D.GROUP_ID='DEVKIND_CODE' AND T.DEVKIND_CODE=D.ID AND T.UNIT_CODE = E.UNIT_CODE");
		sql.append(" AND B.INWAREHOUSE_DATE BETWEEN TO_DATE ('").append(firstDayOfMonth+"000000")
				.append("', 'YYYYMMDDHH24MISS')AND TO_DATE ('").append(lastDayOfMonth+"235959").append("', 'YYYYMMDDHH24MISS')");
		String devKind = getValueString("DEV_KIND");	//�������
		messageBox("devKind"+devKind); 
		if(devKind.length() > 0){    
			sql.append(" AND T.FINAN_KIND = '").append(devKind).append("'");
		}    
		sql.append(" ORDER BY T.DEV_CODE");
		System.out.println("�����ʲ�̯������ sql="+sql.toString()); 
		TParm returnParm = new TParm(TJDODBTool.getInstance().select(
				 sql.toString())); 
		
		//��Ӻϼ�
		double all_dep_price_m = 0.0;		//����̯����
		double all_dep_price_all = 0.0;		//�ۼ�̯����
		double all_dep_price_m_jz = 0.0;	//����̯����(��ֵ)
		double all_all_price = 0.0;			//�ܼ� 
		int all_num = 0;//������
		int j = 0;
		//TParm newParm = new TParm();
		for (int i = 0; i < returnParm.getCount(); i++) {
			int num = returnParm.getInt("DEV_NUM",i);					//����
			double unitPrice = returnParm.getDouble("UNIT_PRICE",i);	//����
			double all_price = unitPrice*num;							//�ܼ�=����*����
			BigDecimal bdi = new BigDecimal(all_price);
			bdi = bdi.setScale(1, 4);									//�ܼ���ֵ����һλС��
			all_price = bdi.doubleValue();
			double dep_price_m = returnParm.getDouble("DEP_PRICE_M",i);
			double dep_price_all = returnParm.getDouble("DEP_PRICE_ALL",i);
			double dep_price_m_jz = returnParm.getDouble("DEP_PRICE_M_JZ",i);
			
			all_dep_price_m += dep_price_m; 
			all_dep_price_all += dep_price_all;
			all_dep_price_m_jz += dep_price_m_jz;
			all_num += num;
			all_all_price += all_price;
			returnParm.setData("ALL_PRICE",i,all_price);
			j++;
			
		}
		all_all_price = TiMath.round(all_all_price, 1);   
		all_dep_price_m = TiMath.round(all_dep_price_m, 1); 
		all_dep_price_all =  TiMath.round(all_dep_price_all, 1); 
		all_dep_price_m_jz = TiMath.round(all_dep_price_m_jz, 1); 
		//�ϼ�
		returnParm.setData("DEV_NAME",j, "�ϼ�:");
		returnParm.setData("DEV_SPEC", j, "");
		returnParm.setData("STOCK_UNIT", j,"");
		returnParm.setData("DEV_NUM", j, all_num);
		returnParm.setData("UNIT_PRICE", j, "");
		returnParm.setData("ALL_PRICE", j, all_all_price);
		returnParm.setData("USED_DEPT", j, "");
		returnParm.setData("BUY_DATE", j, "");
		returnParm.setData("PRO_CODE", j, "");
		returnParm.setData("DEV_KIND", j, "");
		returnParm.setData("DEP_YEAR", j, "");
		returnParm.setData("USED_YEAR", j, "");
		returnParm.setData("DEP_PRICE_M", j, all_dep_price_m);
		returnParm.setData("DEP_PRICE_ALL", j, all_dep_price_all);
		returnParm.setData("DEP_PRICE_M_JZ", j, all_dep_price_m_jz);
		if(returnParm.getCount() < 0){
			messageBox("�������ݣ�");
			TParm resultparm = new TParm();
			this.table.setParmValue(resultparm);
			return;
		}
		//������װ��TParm���ݷŵ�table�ؼ�����ʾ
		this.table.setParmValue(returnParm);
		
	}
	
	/**
	 * ��ӡ����
	 */
	public void onPrint(){
		TTextFormat turnPoint = (TTextFormat) getComponent("DEV_KIND");
		String devKind = turnPoint.getText();	//�������
		String date = StringTool.getString(TypeTool
				.getTimestamp(getValue("Q_DATE")), "yyyy-MM");
		
		TTable table = getTable("TABLE");
		if(table.getRowCount() > 0){
			TParm tableParm = table.getParmValue();
			//out("tableParm.getCount():" + tableParm.getCount());
			//��ӡ����
			TParm data = new TParm();
			//��ͷ����
			data.setData("TITLE", "TEXT", Manager.getOrganization().
					getHospitalCHNFullName(Operator.getRegion()) +
					"�̶��ʲ��۾ɼ��㱨��");
			data.setData("DEV_KIND","TEXT", "�������:"+ devKind);
			data.setData("Q_DATE", "TEXT", "���ڣ�" + date);
			//�������
			TParm parm = new TParm();
			
			if(tableParm.getCount() <= 0){
				this.messageBox("�������ݣ�");
			}else{
			//���������Ԫ��
			for(int i = 0; i < table.getRowCount(); i++){
				parm.addData("DEV_NAME", tableParm.getValue("DEV_NAME", i));				//����
				parm.addData("DEV_SPEC", tableParm.getValue("DEV_SPEC", i));				//���
				parm.addData("STOCK_UNIT", tableParm.getValue("STOCK_UNIT", i));			//��λ
				parm.addData("DEV_NUM", tableParm.getValue("DEV_NUM", i));					//����
				parm.addData("UNIT_PRICE", tableParm.getValue("UNIT_PRICE", i));			//����
				parm.addData("ALL_PRICE", tableParm.getValue("ALL_PRICE", i));				//�ܼ�
				parm.addData("USED_DEPT", tableParm.getValue("USED_DEPT", i));				//ʹ�ò���
				parm.addData("BUY_DATE", tableParm.getValue("BUY_DATE", i));				//��������
				parm.addData("PRO_CODE", tableParm.getValue("PRO_CODE", i));				//�ɱ���Ŀ����
				parm.addData("DEV_KIND", tableParm.getValue("DEV_KIND", i));				//���
				parm.addData("DEP_YEAR", tableParm.getValue("DEP_YEAR", i));				//�۾�����(��)
				parm.addData("USED_YEAR", tableParm.getValue("USED_YEAR", i));				//��ʹ������(��)
				parm.addData("DEP_PRICE_M", tableParm.getValue("DEP_PRICE_M", i));			//����̯����
				parm.addData("DEP_PRICE_ALL", tableParm.getValue("DEP_PRICE_ALL", i));		//�ۼ�̯����
				parm.addData("DEP_PRICE_M_JZ", tableParm.getValue("DEP_PRICE_M_JZ", i));	//����̯����(��ֵ)
				
			}
			
			//������
			parm.setCount(parm.getCount("DEV_NAME"));
			parm.addData("SYSTEM", "COLUMNS", "DEV_NAME");
			parm.addData("SYSTEM", "COLUMNS", "DEV_SPEC");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_UNIT");
			parm.addData("SYSTEM", "COLUMNS", "DEV_NUM");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "ALL_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "USED_DEPT");
			parm.addData("SYSTEM", "COLUMNS", "BUY_DATE");
			parm.addData("SYSTEM", "COLUMNS", "PRO_CODE");
			parm.addData("SYSTEM", "COLUMNS", "DEV_KIND");
			parm.addData("SYSTEM", "COLUMNS", "DEP_YEAR");
			parm.addData("SYSTEM", "COLUMNS", "USED_YEAR");
			parm.addData("SYSTEM", "COLUMNS", "DEP_PRICE_M");
			parm.addData("SYSTEM", "COLUMNS", "DEP_PRICE_ALL");
			parm.addData("SYSTEM", "COLUMNS", "DEP_PRICE_M_JZ");
			
			//�����ŵ�������
			data.setData("TABLE", parm.getData());
			//��β����
			data.setData("OPT_USER", "TEXT", "�����ˣ�"+Operator.getName());
			
			//out��־���data��Ϣ-���ڵ���
			//System.out.println("data=="+data);
			
			//���ô�ӡ���� 
			this.openPrintWindow("%ROOT%\\config\\prt\\DEV\\DEVIntangibleDepreciation.jhw", data);
			}
 
		}else {
			this.messageBox("û�д�ӡ����");
            return;
		}
		
	}
	
	/**
     * ����EXCEL
     */
    public void onExport() {
    	
    	TTable table_e = getTable("TABLE");
    	if(table_e.getRowCount() > 0){
    		ExportExcelUtil.getInstance().exportExcel(table_e, "�̶��ʲ��۾ɼ����ͳ��");
    	}else {
         this.messageBox("û�л������");
         return;
    	}
    	
    } 
    
    /**
     * ��շ���
     */
    public void onClear() {
    	initPage();
    	table.removeAll();
    	callFunction("UI|TABLE|removeRowAll");
    }
    
    //��ȡĳ��ĳ�µ����һ��
    public static String getLastDayOfMonth(int year, int month) {     
        Calendar cal = Calendar.getInstance();     
        cal.set(Calendar.YEAR, year);     
        cal.set(Calendar.MONTH, month);     
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DATE));  
       return  new   SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());  
    } 
    
    //��ȡĳ��ĳ�µĵ�һ��
    public static String getFirstDayOfMonth(int year, int month) {     
        Calendar cal = Calendar.getInstance();     
        cal.set(Calendar.YEAR, year);     
        cal.set(Calendar.MONTH, month);  
        cal.set(Calendar.DAY_OF_MONTH,cal.getMinimum(Calendar.DATE));  
       return   new   SimpleDateFormat( "yyyyMMdd ").format(cal.getTime());  
    } 
    
	/**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG���� 
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
}
