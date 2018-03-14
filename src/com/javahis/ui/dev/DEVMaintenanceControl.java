package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;

import jdo.sys.SystemTool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �豸����/�ʿ�
 * </p>
 * 
 * <p>
 * Description: �豸����/�ʿ�
 * </p>
 * <p>
 * Copyright: BlueCore 2015
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author wangjc
 * @version 1.0
 */
public class DEVMaintenanceControl extends TControl {
	
	private String sysID = "";//������B    �ʿأ�Z
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		this.sysID = this.getParameter().toString();
		if(this.sysID.equals("B")){
			this.getComboBox("MTN_KIND").setValue(0);
			this.setTitle("�豸����");
		}else if(this.sysID.equals("Z")){
			this.getComboBox("MTN_KIND").setValue(1);
			this.setTitle("�豸�ʿ�");
		}
		TParm parm = new TParm();
		//sysID.equals("MRO")
//		((TMenuItem) getComponent("save")).setEnabled(false);
		getTextField("DEV_CODE").setPopupMenuParameter("",
	            getConfigParm().newConfig("%ROOT%\\config\\dev\\DEVBasePopup.x"),
	            parm);
	    // ������ܷ���ֵ����
	    getTextField("DEV_CODE").addEventListener(
	            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
	    TParm parm1 = new TParm();
	    parm1.setData("DEV_CODE", this.getValueString("DEV_CODE"));
	    parm1.setData("MTN_KIND", this.getValueString("MTN_KIND"));
	    getTextField("MTN_TYPE_CODE").setPopupMenuParameter("",
	            getConfigParm().newConfig("%ROOT%\\config\\dev\\DEVMtnListPopup.x"),
	            parm1);
	    // ������ܷ���ֵ����
	    getTextField("MTN_TYPE_CODE").addEventListener(
	            TPopupMenuEvent.RETURN_VALUE, this, "devMtnListPopReturn");
	    //��ʼ����ѯ����
	    this.setValue("START_DATE", StringTool.rollDate(SystemTool
				.getInstance().getDate(), -7));
		this.setValue("END_DATE", SystemTool.getInstance().getDate());
		this.setValue("NEXT_MTN_DATE", SystemTool.getInstance().getDate());
	}
	
	/**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String dev_code = parm.getValue("DEV_CODE");
        if (!StringUtil.isNullString(dev_code)){
        	this.setValue("DEV_CODE", dev_code);
        }
        String dev_desc = parm.getValue("DEV_CHN_DESC");
        if (!StringUtil.isNullString(dev_desc)){
        	this.setValue("DEV_DESC", dev_desc);
        }
    }
    
	/**
     * ���ܷ���ֵ����
     *
     * @param tag
     * @param obj
     */
    public void devMtnListPopReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String mtn_type_code = parm.getValue("MTN_TYPE_CODE");
        if (!StringUtil.isNullString(mtn_type_code)){
        	this.setValue("MTN_TYPE_CODE", mtn_type_code);
        }
        String mtn_type_desc = parm.getValue("MTN_TYPE_DESC");
        if (!StringUtil.isNullString(mtn_type_desc)){
        	this.setValue("MTN_TYPE_DESC", mtn_type_desc);
        }
    }
	
	/**
	 * ����
	 */
	public void onSave(){
		TTable tableM = this.getTable("TABLEM");
//		System.out.println("SelectedRow:"+tableM.getSelectedRow());
		String nextMtnDate = this.getValueString("NEXT_MTN_DATE");
		if(nextMtnDate.equals("")){
			this.messageBox("�´α������ڲ���Ϊ��!");
			return;
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//Сд��mm��ʾ���Ƿ���  
		try {
			Date date1=sdf.parse(nextMtnDate.substring(0, 10));
			Date date2=new Date();
			date2=sdf.parse(sdf.format(date2));
			if(date2.after(date1)){
				this.messageBox("�´�ά�����ڲ���С�ڵ�ǰʱ��");
				return;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		System.out.println("nextMtnDate:"+nextMtnDate);
		TParm mParm = tableM.getParmValue();
//		System.out.println("mParm:"+mParm);
		TParm parm = new TParm();
		int n=1;
		for(int i=0;i<mParm.getCount();i++){
			if(mParm.getValue("SELECT_FLG", i).equals("Y")){
				parm.addData("DEV_CODE", mParm.getValue("DEV_CODE", i));
				parm.addData("MTN_KIND", mParm.getValue("MTN_KIND", i));
				parm.addData("MTN_TYPE_CODE", mParm.getValue("MTN_TYPE_CODE", i));
				parm.addData("DEVSEQ_NO", mParm.getValue("DEVSEQ_NO", i));
				parm.addData("NEXT_MTN_DATE", nextMtnDate.substring(0, 19).replace("-", "/"));
				parm.addData("OPT_USER", Operator.getID());
				parm.addData("OPT_TERM", Operator.getIP());
				parm.setCount(n);
				n++;
			}
		}
//		System.out.println("parm=="+parm);
		if(parm.getCount() <= 0){
			this.messageBox("û��Ҫ��������ݣ�");
			return;
		}
		TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onUpdateMaintenance", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			err(result.getErrText());
			return;
		}
		this.messageBox("����ɹ�");
		this.onClear();
	}
	
	/**
	 * ��ѯ
	 */
    public void onQuery(){
    	this.setValue("SELECT_ALL", "N");
    	String sql = "SELECT 'N' AS SELECT_FLG,A.DEV_CODE,A.MTN_KIND,A.MTN_TYPE_CODE,A.DEVSEQ_NO,A.NEXT_MTN_DATE, "
    			+ " A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.DEPT_CODE,C.DEV_CHN_DESC,D.MTN_TYPE_DESC "
    			+ " FROM DEV_MAINTENANCE_DATE A, DEV_STOCKDD B,DEV_BASE C,DEV_MAINTENANCEM D "
    			+ " WHERE A.DEV_CODE = B.DEV_CODE "
    			+ " AND A.DEVSEQ_NO = B.DEV_CODE_DETAIL "
    			+ " AND A.DEV_CODE=C.DEV_CODE "
    			+ " AND A.MTN_KIND='"+this.getComboBox("MTN_KIND").getValue()+"' "
    			+ " AND A.MTN_TYPE_CODE=D.MTN_TYPE_CODE "
    			+ " AND D.ACTIVE_FLG='Y' ";
    	String startDate = this.getValueString("START_DATE");//��ʼʱ��
		String endDate = this.getValueString("END_DATE");//����ʱ��
		String deptCode = this.getValueString("DEPT_CODE");
		String devseqNo = this.getValueString("DEVSEQ_NO");//�ʲ����
		String dev_code = this.getValueString("DEV_CODE");//�豸����
		String mtnKind = this.getValueString("MTN_KIND");
		String mtnTypeCode = this.getValueString("MTN_TYPE_CODE");
		if(!dev_code.equals("")){
			sql += " AND A.DEV_CODE='"+dev_code+"' ";
		}
		if(!deptCode.equals("")){
			sql += " AND B.DEPT_CODE='"+deptCode+"' ";
		}
		if(!startDate.equals("") && !endDate.equals("")){
			sql += " AND A.NEXT_MTN_DATE BETWEEN TO_DATE('"
						+startDate.substring(0, 10)
						+"','YYYY/MM/DD') AND TO_DATE('"
						+ endDate.substring(0, 10)
						+ "','YYYY/MM/DD') ";
		}
		if(!devseqNo.equals("")){
			sql += " AND A.DEVSEQ_NO='"+devseqNo+"' ";
		}
		if(!mtnKind.equals("")){
			sql += " AND A.MTN_KIND='"+mtnKind+"' ";
		}
		if(!mtnTypeCode.equals("")){
			sql += " AND A.MTN_TYPE_CODE='"+mtnTypeCode+"' ";
		}
		sql += " ORDER BY A.DEV_CODE ";
//    	System.out.println("sql>>>>>>>>"+sql);
    	TTable tableM = this.getTable("TABLEM");
    	TParm mParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(mParm.getCount() <= 0){
    		this.messageBox("δ��ѯ������");
    		tableM.removeRowAll();
    		return;
    	}
    	for(int i=0;i<mParm.getCount("DEV_CODE");i++){
    		mParm.setData("OPT_DATE", i, mParm.getValue("OPT_DATE", i).substring(0, 19));
    	}
//    	System.out.println("mParm>>>>>>"+mParm);
    	tableM.setParmValue(mParm);
    }
    
    /**
     * ���
     */
    public void onClear(){
    	this.clearValue("DEPT_CODE;DEV_CODE;DEV_DESC;DEVSEQ_NO;MTN_TYPE_CODE;MTN_TYPE_DESC");
    	//��ʼ����ѯ����
	    this.setValue("START_DATE", StringTool.rollDate(SystemTool
				.getInstance().getDate(), -7));
		this.setValue("END_DATE", SystemTool.getInstance().getDate());
		this.setValue("NEXT_MTN_DATE", SystemTool.getInstance().getDate());
		this.setValue("SELECT_ALL", "N");
    	TTable tableM = this.getTable("TABLEM");
    	tableM.removeRowAll();
    }
    
    public void onAddEmrWrite(){
//    	openPrintWindow("%ROOT%\\config\\prt\\2222_������ܱ���.jhw","");
//    	if(true){
//    		return;
//    	}
//    	TParm parm = new TParm();
//    	parm.setData("AAA","TEXT", "11111");
//    	parm.addData("NAME2", "11111");
//    	parm.setCount(parm.getCount("NAME")); 
//    	parm.addData("SYSTEM", "COLUMNS", "NAME");
//    	parm.addData("SYSTEM", "COLUMNS", "NAME2");
//        TParm printParm = new TParm();         
//        
//        printParm.setData("TABLE_MES",parm.getData());  
//    	EMRTool emrTool = new EMRTool("2222","1111", this);
//		Object obj = new Object();
//		obj = this.openPrintDialog("%ROOT%\\config\\prt\\DEV\\1111.jhw",
//				parm);
////		emrTool.saveEMR(obj, "������ܱ���", "EMR040002", "EMR04000204", false);
//		emrTool.saveEMR(obj, "������ܱ���", "EMR100002", "EMR10000202", false);
//    	if(true){
//    		return;
//    	}
    	
    	TParm devMesParm = new TParm();//�豸��Ϣ
    	TParm devMaiParm = new TParm();//�豸ά��ϸ��
    	TParm result = new TParm();
    	TTable tableM = this.getTable("TABLEM");
    	TParm mParm = new TParm();
    	mParm = tableM.getParmValue();
    	int n=1;
		for(int i=0;i<mParm.getCount();i++){
			if(mParm.getValue("SELECT_FLG", i).equals("Y")){
				devMesParm.addData("DEV_CODE", mParm.getValue("DEV_CODE", i));//�豸����
				devMesParm.addData("DEV_CODE_DETAIL", mParm.getValue("DEVSEQ_NO", i));//�ʲ����
				String devSql = "SELECT BRAND,MAN_NATION,SPECIFICATION,MAN_NATION FROM DEV_BASE WHERE DEV_CODE='"+mParm.getValue("DEV_CODE", i)+"' ";
				TParm devParm = new TParm(TJDODBTool.getInstance().select(devSql));
				devMesParm.addData("BRAND", devParm.getValue("BRAND", 0));
				if(!devParm.getValue("MAN_NATION", 0).equals("")){
					String nationSql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_NATION' AND ID='"+devParm.getValue("MAN_NATION", 0)+"' ";
					TParm nationParm = new TParm(TJDODBTool.getInstance().select(nationSql));
					devMesParm.addData("MAN_NATION", nationParm.getValue("CHN_DESC", 0));//����Ʒ��
				}else{
					devMesParm.addData("MAN_NATION", "");//����Ʒ��
				}
				devMesParm.addData("SPECIFICATION", devParm.getValue("SPECIFICATION", 0));//���
				devMesParm.addData("MTN_KIND", mParm.getValue("MTN_KIND", i));
				devMesParm.addData("MTN_TYPE_CODE", mParm.getValue("MTN_TYPE_CODE", i));
				devMesParm.addData("DEV_CHN_DESC", mParm.getValue("DEV_CHN_DESC", i));//�豸����
				String deptSql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+mParm.getValue("DEPT_CODE", i)+"' ";
				TParm deptParm = new TParm(TJDODBTool.getInstance().select(deptSql));
				devMesParm.addData("DEPT_CHN_DESC", deptParm.getValue("DEPT_CHN_DESC", 0));
				devMesParm.addData("DEPT_CODE", mParm.getValue("DEPT_CODE", i));
				devMesParm.addData("NEXT_MTN_DATE", mParm.getValue("NEXT_MTN_DATE", i));
//				devMesParm.addRowData(mParm.getRow(i), n);
				devMesParm.setCount(n);
				n++;
			}
		}
		if(n>2){//����豸
			result.setData("MORE_FLG", "Y");
			for(int j=0;j<devMesParm.getCount();j++){
				if(!devMesParm.getValue("DEV_CODE", 0).equals(devMesParm.getValue("DEV_CODE", j))
						|| !devMesParm.getValue("MTN_TYPE_CODE", 0).equals(devMesParm.getValue("MTN_TYPE_CODE", j))){
					this.messageBox("��ѡ��ͬһ���豸��ͬһ��ά�����");
					return;
				}
			}
		}else if(n == 1){
			this.messageBox("�빴ѡ����һ������");
			return;
		}else{//�����豸
			result.setData("MORE_FLG", "N");
		}
		result.setData("DEV_MES_PARM", devMesParm.getData());
		String sql = "SELECT MTN_DETAIL_DESC FROM DEV_MAINTENANCED WHERE DEV_CODE='"
				+devMesParm.getValue("DEV_CODE", 0)
				+ "' AND MTN_KIND='"
				+devMesParm.getValue("MTN_KIND", 0)
				+ "' AND MTN_TYPE_CODE='"
				+devMesParm.getValue("MTN_TYPE_CODE", 0)
//				+ "' AND MTN_DETAIL_CODE='"
//				+devMesParm.getValue("DEVSEQ_NO", 0)
				+ "' ";
//		System.out.println("sql--"+sql);
		devMaiParm = new TParm(TJDODBTool.getInstance().select(sql));
		result.setData("DEV_MAI_PARM", devMaiParm.getData());
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//�������ڸ�ʽ
		result.setData("DATE", df.format(new Date())+"");
//		System.out.println("result:"+result);
    	this.openDialog("%ROOT%\\config\\dev\\DEVMaintenanceRecord.x",result);
//    	TTable table = this.getTable("TABLEM");
//    	int row = table.getSelectedRow();
//    	TParm parm = table.getParmValue().getRow(row);
////    	System.out.println("parm:"+parm);
//    	TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
//				"onDeleteMeasuremDic", parm);
//		if (result.getErrCode() < 0) {
//			this.messageBox("ɾ��ʧ��");
//			err(result.getErrText());
//			return;
//		}
//		this.messageBox("ɾ���ɹ�");
//		this.onClear();
		this.onQuery();
    }
    
    /**
     * M�����¼���������ѯϸ����Ϣ
     */
    public void onTableMClick(){
    	TTable tableM = this.getTable("TABLEM");
    	int row = tableM.getSelectedRow();
    	int col = tableM.getSelectedColumn();
    	if(row < 0){
    		return;
    	}else if(col == 0){
    		if ("N".equals(tableM.getItemString(row, "SELECT_FLG"))) {
    			tableM.setItem(row, "SELECT_FLG", "Y");
			} else if ("Y".equals(tableM.getItemString(row, "SELECT_FLG"))){
				tableM.setItem(row, "SELECT_FLG", "N");
			}
    	}
    }
    
	/**
	 * ȫѡ
	 */
	public void onSelectAll() {
		TTable tableM = this.getTable("TABLEM");
		tableM.acceptText();
		if (tableM.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		for (int i = 0; i < tableM.getRowCount(); i++) {
			tableM.setItem(i, "SELECT_FLG", getValueString("SELECT_ALL"));
		}
	}
	
    /**
     * �õ�TextField����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
    
    /**
     * �õ�Table����
     * @param tagName
     * @return
     */
    private TTable getTable(String tagName){
    	return (TTable) this.getComponent(tagName);
    }
    
    /**
     * �õ�ComboBox����
     * @param tagName
     * @return
     */
    private TComboBox getComboBox(String tagName){
    	return (TComboBox) this.getComponent(tagName);
    }
    
    /**
     * �õ�checkbox����
     * @param tagName
     * @return
     */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
}
