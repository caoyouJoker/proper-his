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
 * Title:�����ʲ���ϸ
 * </p> 
 *
 * <p>
 * Description:�����ʲ���ϸ
 * </p>  
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author duzhw 2013.08.16
 * @version 1.0
 */
public class DEVIntangibleAssetListControl extends TControl{
	
	private TTable table;
	
	public DEVIntangibleAssetListControl(){
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
		String devKind = getValueString("DEV_KIND");	//�����ʲ����-�ʽ���Դ
		String devFinan = getValueString("DEV_FINAN");	//�������
//		if(devKind.length() == 0){
//			messageBox("�ʽ���Դ����Ϊ��!");
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
		sql.append("SELECT T.DEV_CODE,T.DEV_CHN_DESC AS DEV_NAME,T.SPECIFICATION AS DEV_SPEC,")
				.append("		E.UNIT_CHN_DESC AS STOCK_UNIT,B.STOCK_QTY AS DEV_NUM,B.UNIT_PRICE,")
				.append("		TO_CHAR(B.INWAREHOUSE_DATE, 'YYYY/MM/DD') AS BUY_DATE,")
				.append("		B.DEP_PRICE AS ACC_AMOUNT,B.CURR_PRICE AS PRE_ASSET,D.COST_CENTER_ABS_DESC AS COST_CENTER,")
				.append("		C.CHN_DESC AS SOURCE_FUND")
				.append(" FROM DEV_BASE T,DEV_STOCKDD B, SYS_DICTIONARY C, SYS_COST_CENTER D, SYS_UNIT E ")
				.append(" WHERE T.DEV_CODE = B.DEV_CODE AND T.INTANGIBLE_FLG ='Y' ")
				.append(" AND C.ID=T.FUNDSOU_CODE AND C.GROUP_ID='FUNDSOU_CODE'")
				.append(" AND B.DEPT_CODE=D.COST_CENTER_CODE AND D.ACTIVE_FLG = 'Y' AND T.UNIT_CODE = E.UNIT_CODE");
		sql.append(" AND B.INWAREHOUSE_DATE BETWEEN TO_DATE ('").append(firstDayOfMonth+"000000")
				.append("', 'YYYYMMDDHH24MISS')AND TO_DATE ('").append(lastDayOfMonth+"235959").append("', 'YYYYMMDDHH24MISS')");
		if(devKind.length() > 0){
			sql.append(" AND T.FUNDSOU_CODE = '").append(devKind).append("'");
		} 		if(devFinan.length() > 0){
			sql.append(" AND T.FINAN_KIND = '").append(devFinan).append("'"); 
		} 
		System.out.println("�����ʲ���ϸ sql="+sql.toString());  
		TParm returnParm = new TParm(TJDODBTool.getInstance().select(
				 sql.toString()));
		//��Ӻϼ� 
		double all_all_price = 0.0;			//�ܼ�
		double all_dep_price_all = 0.0;		//�ۼ�̯�����ܼ�
		double all_pre_price = 0.0;			//��ֵ�ܼ�
		int all_num = 0;					//������
		int j = 0;
		//TParm newParm = new TParm();
		for (int i = 0; i < returnParm.getCount(); i++) {
			int num = returnParm.getInt("DEV_NUM",i);					//����
			double unitPrice = returnParm.getDouble("UNIT_PRICE",i);	//����
			double all_price = unitPrice*num;							//�ܼ�=����*����
			BigDecimal bdi = new BigDecimal(all_price);
			bdi = bdi.setScale(1, 4);									//�ܼ���ֵ����һλС��
			all_price = bdi.doubleValue();
			double acc_dep = returnParm.getDouble("ACC_AMOUNT",i);
			double pre_price = returnParm.getDouble("PRE_ASSET",i);
			all_all_price += all_price;
			all_dep_price_all += acc_dep;
			all_pre_price += pre_price;
			all_num += num;
			returnParm.setData("ALL_PRICE",i,all_price);
			j++;
			
		} 
		all_all_price = TiMath.round(all_all_price, 1);  
		all_pre_price = TiMath.round(all_pre_price, 1); 
		all_dep_price_all =  TiMath.round(all_dep_price_all, 1);  
		//�ϼ�
		returnParm.setData("DEV_CODE",j, "�ϼ�:");
		returnParm.setData("DEV_NAME",j, "");
		returnParm.setData("DEV_SPEC", j, "");
		returnParm.setData("STOCK_UNIT", j,"");
		returnParm.setData("DEV_NUM", j, all_num);
		returnParm.setData("UNIT_PRICE", j, "");
		returnParm.setData("ALL_PRICE", j, all_all_price);
		returnParm.setData("BUY_DATE", j, "");
		returnParm.setData("ACC_AMOUNT", j, all_dep_price_all);
		returnParm.setData("PRE_ASSET", j, all_pre_price);
		returnParm.setData("COST_CENTER", j, "");
		returnParm.setData("SOURCE_FUND", j, "");
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
		String devKind = turnPoint.getText();	//�ʽ���Դ
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
					"�����ʲ���ϸ��");
			data.setData("DEV_KIND","TEXT", "�ʽ���Դ��" + devKind);
			data.setData("Q_DATE", "TEXT", "���ڣ�" + date);
			//�������
			TParm parm = new TParm();
			  
			if(tableParm.getCount() <= 0){
				this.messageBox("�������ݣ�");
			}else{
			//���������Ԫ��
			for(int i = 0; i < table.getRowCount(); i++){
				parm.addData("DEV_CODE", tableParm.getValue("DEV_CODE", i));		//�ʲ�����
				parm.addData("DEV_NAME", tableParm.getValue("DEV_NAME", i));		//����
				parm.addData("DEV_SPEC", tableParm.getValue("DEV_SPEC", i));		//���
				parm.addData("STOCK_UNIT", tableParm.getValue("STOCK_UNIT", i));	//��λ
				parm.addData("DEV_NUM", tableParm.getValue("DEV_NUM", i));			//���� 
				parm.addData("UNIT_PRICE", tableParm.getValue("UNIT_PRICE", i));	//����
				parm.addData("ALL_PRICE", tableParm.getValue("ALL_PRICE", i));		//�ܼ�
				parm.addData("BUY_DATE", tableParm.getValue("BUY_DATE", i));		//��������
				parm.addData("ACC_AMOUNT", tableParm.getValue("ACC_AMOUNT", i));	//�ۼ�̯����
				parm.addData("PRE_ASSET", tableParm.getValue("PRE_ASSET", i));		//�ʲ���ֵ
				parm.addData("COST_CENTER", tableParm.getValue("COST_CENTER", i));	//�ɱ�����
				parm.addData("SOURCE_FUND", tableParm.getValue("SOURCE_FUND", i));	//�ʽ���Դ
				
			}
			
			//������
			parm.setCount(parm.getCount("DEV_CODE"));
			parm.addData("SYSTEM", "COLUMNS", "DEV_CODE");
			parm.addData("SYSTEM", "COLUMNS", "DEV_NAME");
			parm.addData("SYSTEM", "COLUMNS", "DEV_SPEC");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_UNIT");
			parm.addData("SYSTEM", "COLUMNS", "DEV_NUM");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "ALL_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "BUY_DATE");
			parm.addData("SYSTEM", "COLUMNS", "ACC_AMOUNT");
			parm.addData("SYSTEM", "COLUMNS", "PRE_ASSET");
			parm.addData("SYSTEM", "COLUMNS", "COST_CENTER");
			parm.addData("SYSTEM", "COLUMNS", "SOURCE_FUND");
			
			
			//�����ŵ�������
			data.setData("TABLE", parm.getData());
			//��β����
			data.setData("OPT_USER", "TEXT", "�����ˣ�"+Operator.getName());
			
			//out��־���data��Ϣ-���ڵ���
			//System.out.println("data=="+data);
			
			// ���ô�ӡ���� 
			this.openPrintWindow("%ROOT%\\config\\prt\\DEV\\DEVIntangibleAssetList.jhw", data);
			}

		}else {
			this.messageBox("û�д�ӡ����");
            return;
		}
		
	}
	/**
	 * ����¼�
	 *  
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
        row = table.getClickedRow(); 
        TParm rowParm = table.getParmValue().getRow(row);     
        //���õ����˵�     
		if (rowParm.getValue("DEV_NAME").length() == 0){
			return;
		}   
		// ״̬����ʾ       
		else{   
		callFunction(  
		        "UI|setSysStatus", 
		        rowParm.getValue("DEV_CODE") + " " 
		        + rowParm.getValue("DEV_NAME")
			    + " " + rowParm.getValue("DEV_SPEC")); 
		}   
	}
	
	/**
     * ����EXCEL
     */
    public void onExport() {
    	
    	TTable table_e = getTable("TABLE");
    	if(table_e.getRowCount() > 0){
    		ExportExcelUtil.getInstance().exportExcel(table_e, "�����ʲ���ϸ��ͳ��");
    	}else {
         this.messageBox("û�л������");
         return;
    	}
    }
    
    public void onExcel(){ 
    	TTable tab = this.getTable("TABLE");
    	if(tab.getRowCount()<=0){
    		this.messageBox("�޻������");
    		return;
    	}
    	else{
    	    ExportExcelUtil.getInstance().exportExcel(tab, "�����ʲ���ϸ");
    	}
    }
    
    /**
     * ��շ���
     */
    public void onClear() {
    	initPage();
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
