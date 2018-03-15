package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;

import jdo.sys.SystemTool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;

import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: �豸�����ֵ�
 * </p>
 * 
 * <p>
 * Description: �豸�����ֵ�
 * </p>
 * <p>
 * Copyright: ProperSoft 2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wangjc
 * @version 1.0
 */
public class DEVMeasureDicControl extends TControl {
	
	private ArrayList<Boolean> updated;// �ж��ǲ����޸Ĺ�
	
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		this.setValue("ACTIVE_FLG", "Y");
		TParm parm = new TParm();
		((TMenuItem) getComponent("save")).setEnabled(false);
		getTextField("DEV_CODE").setPopupMenuParameter("",
	            getConfigParm().newConfig("%ROOT%\\config\\dev\\DEVBasePopup.x"),
	            parm);
	    // ������ܷ���ֵ����
	    getTextField("DEV_CODE").addEventListener(
	            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
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
	 * ����
	 */
	public void onSave(){
		TTable tableM = this.getTable("TABLEM");
//		System.out.println("SelectedRow:"+tableM.getSelectedRow());
		String dev_code = this.getValueString("DEV_CODE");//�豸����
//		String measurem_code = this.getValueString("MEASUREM_CODE");//��������
		String measurem_desc = this.getValueString("MEASUREM_DESC");//��������
		int measurem_price = this.getValueInt("MEASUREM_PRICE");//��������
		int measurem_cycle = this.getValueInt("MEASUREM_CYCLE");//����
		String measurem_unit = this.getValueString("MEASUREM_UNIT");//���ڵ�λ
		if(dev_code.equals("")){
			this.messageBox("�豸���벻��Ϊ��!");
			return;
		}
		if(measurem_desc.equals("")){
			this.messageBox("�������Ʋ���Ϊ��!");
			return;
		}
		if(measurem_price == 0){
			this.messageBox("�������۲���Ϊ0!");
			return;
		}
		if(measurem_cycle == 0){
			this.messageBox("�������ڲ���Ϊ0!");
			return;
		}
		if(measurem_unit.equals("")){
			this.messageBox("�������ڵ�λ����Ϊ��!");
			return;
		}
		TParm mParm = tableM.getParmValue().getRow(tableM.getSelectedRow());
		mParm.setData("DEV_CODE", dev_code);
		mParm.setData("MEASUREM_DESC", measurem_desc);
		mParm.setData("MEASUREM_PRICE", measurem_price);
		mParm.setData("MEASUREM_CYCLE", measurem_cycle);
		mParm.setData("MEASUREM_UNIT", measurem_unit);
		mParm.setData("OPT_USER", Operator.getID());
		mParm.setData("OPT_TERM", Operator.getIP());
		mParm.setData("ACTIVE_FLG", this.getValue("ACTIVE_FLG"));
		
//		System.out.println("mParm=="+mParm);
		TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onUpdateMeasuremDic", mParm);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			err(result.getErrText());
			return;
		}
		this.messageBox("����ɹ�");
		this.onClear();
	}
	
	/**
	 * ����
	 */
	public void onNew(){
		String dev_code = this.getValueString("DEV_CODE");//�豸����
		
//		String measurem_code = this.getValueString("MEASUREM_CODE");//��������
		String measurem_desc = this.getValueString("MEASUREM_DESC");//��������
		int measurem_price = this.getValueInt("MEASUREM_PRICE");//��������
		int measurem_cycle = this.getValueInt("MEASUREM_CYCLE");//����
		String measurem_unit = this.getValueString("MEASUREM_UNIT");//���ڵ�λ
		TParm parm = new TParm();
		if(dev_code.equals("")){
			this.messageBox("�豸���벻��Ϊ��!");
			return;
		}else{
			parm.addData("DEV_CODE", dev_code);
		}
		if(measurem_desc.equals("")){
			this.messageBox("�������Ʋ���Ϊ��!");
			return;
		}else{
			parm.addData("MEASUREM_DESC", measurem_desc);
		}
		if(measurem_price == 0){
			this.messageBox("�������۲���Ϊ0!");
			return;
		}else{
			parm.addData("MEASUREM_PRICE", measurem_price);
		}
		if(measurem_cycle == 0){
			this.messageBox("���ڲ���Ϊ0!");
			return;
		}else{
			parm.addData("MEASUREM_CYCLE", measurem_cycle);
		}
		if(measurem_unit.equals("")){
			this.messageBox("���ڵ�λ����Ϊ��!");
			return;
		}else{
			parm.addData("MEASUREM_UNIT", measurem_unit);
		}
		String devBaseSql = "SELECT MEASURE_FLG,MAINTENANCE_FLG,QUALITY_CONTROL_FLG "
				+ " FROM DEV_BASE WHERE DEV_CODE='"+dev_code+"' ";
//		System.out.println("devBaseSql:"+devBaseSql);
		TParm devBaseParm = new TParm(TJDODBTool.getInstance().select(devBaseSql));
		if(devBaseParm.getValue("MEASURE_FLG", 0).equals("N")){
			this.messageBox("�������豸�������й�ѡ���豸�ļ���ע�ǲ�����!");
			return;
		}
		String measurem_code = SystemTool.getInstance().getNo("ALL", "DEV",
                "MEASUREM_CODE", "MEASUREM_CODE");
		measurem_code = "J"+measurem_code;
		parm.addData("MEASUREM_CODE", measurem_code);
		parm.addData("OPT_USER", Operator.getID());
		parm.addData("OPT_TERM", Operator.getIP());
		parm.addData("ACTIVE_FLG", this.getValue("ACTIVE_FLG"));
		TParm saveParm = new TParm();
		saveParm.setData("DEV_MEASURE", parm.getData());
		TParm devMtnDate = this.getDevMtnDateParm(parm);
		if(devMtnDate != null){
//			this.messageBox("���û�д����豸");
//			return;
			saveParm.setData("DEV_MAINTENANCE_DATE", devMtnDate.getData());
		}else{
			saveParm.setData("DEV_MAINTENANCE_DATE", new TParm().getData());
		}
//		saveParm.setData("DEV_MAINTENANCE_DATE", devMtnDate.getData());
//		System.out.println("parm------------"+saveParm);
		TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onInsertMeasuremDic", saveParm);
		if (result.getErrCode() < 0) {
			this.messageBox("����ʧ��");
			err(result.getErrText());
			return;
		}
		this.messageBox("����ɹ�");
		this.onQuery();
	}
	
	/**
	 * ��ȡDEV_MTN_DATE����
	 * @param parm
	 * @return
	 */
	public TParm getDevMtnDateParm(TParm parm){
		TParm result = new TParm();
		String detailSql = "SELECT DEV_CODE_DETAIL FROM DEV_STOCKDD WHERE DEV_CODE='"+parm.getValue("DEV_CODE", 0)+"' ";
		TParm detailParm = new TParm(TJDODBTool.getInstance().select(detailSql));
		if(detailParm.getCount()<=0){
			return null;
		}
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
		int n = 1;
		for(int i=0;i<detailParm.getCount("DEV_CODE_DETAIL");i++){
			result.addData("DEV_CODE", parm.getValue("DEV_CODE", 0));
			result.addData("MTN_KIND", 2);//����
			result.addData("MTN_TYPE_CODE", parm.getValue("MEASUREM_CODE", 0));
			result.addData("DEVSEQ_NO", detailParm.getValue("DEV_CODE_DETAIL", i));
			result.addData("NEXT_MTN_DATE", sdf.format(new Date()));
			result.addData("OPT_USER", Operator.getID());
//						result.addData("OPT_DATE", "");
			result.addData("OPT_TERM", Operator.getIP());
			result.setCount(n);
			n++;
		}
		return result;
	}
	
	/**
	 * ��ѯ
	 */
    public void onQuery(){
    	String sql = "SELECT A.ACTIVE_FLG,A.DEV_CODE,B.DEV_CHN_DESC,A.MEASUREM_CODE,A.MEASUREM_DESC,A.MEASUREM_PRICE,"
    			+ " A.MEASUREM_CYCLE,A.MEASUREM_UNIT,A.OPT_USER,A.OPT_DATE,A.OPT_TERM "
    			+ " FROM DEV_MEASURE A,DEV_BASE B WHERE A.DEV_CODE=B.DEV_CODE ";
    	String dev_code = this.getValueString("DEV_CODE");//�豸����
		String measurem_desc = this.getValueString("MEASUREM_DESC");//��������
		int measurem_price = this.getValueInt("MEASUREM_PRICE");//��������
		int measurem_cycle = this.getValueInt("MEASUREM_CYCLE");//����
		String measurem_unit = this.getValueString("MEASUREM_UNIT");//���ڵ�λ
		TParm parm = new TParm();
		if(!dev_code.equals("")){
			sql += " AND A.DEV_CODE='"+dev_code+"' ";
		}
		if(!measurem_desc.equals("")){
			sql += " AND A.MEASUREM_DESC LIKE '%"+measurem_desc+"%' ";
		}
		if(measurem_price != 0){
			sql += " AND A.MEASUREM_PRICE='"+measurem_price+"' ";
		}
		if(measurem_cycle != 0 && !measurem_unit.equals("")){
			sql += " AND A.MEASUREM_CYCLE='"+measurem_cycle
					+"' AND MEASUREM_UNIT='"+measurem_unit+"' ";
		}else if(measurem_cycle == 0 && !measurem_unit.equals("")){
			this.messageBox("����������������ڵ�λ����ͬʱ��д��");
			return;
		}else if(measurem_cycle != 0 && measurem_unit.equals("")){
			this.messageBox("����������������ڵ�λ����ͬʱ��д��");
			return;
		}
		sql += " ORDER BY A.DEV_CODE ";
//    	System.out.println("sql>>>>>>>>"+sql);
    	TTable tableM = this.getTable("TABLEM");
    	TParm mParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(mParm.getCount() <= 0){
    		this.messageBox("δ��ѯ������");
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
    	this.setValue("ACTIVE_FLG", "Y");
    	this.clearValue("DEV_CODE;DEV_DESC;MEASUREM_CODE;MEASUREM_DESC;"
    			+ "MEASUREM_PRICE;MEASUREM_CYCLE;MEASUREM_UNIT");
    	((TMenuItem) getComponent("save")).setEnabled(false);
    	((TMenuItem) getComponent("new")).setEnabled(true);
    	TTable tableM = this.getTable("TABLEM");
    	tableM.removeRowAll();
    }
    
    public void onDelete(){
    	TTable table = this.getTable("TABLEM");
    	int row = table.getSelectedRow();
    	TParm parm = table.getParmValue().getRow(row);
    	String sql = "SELECT * FROM DEV_MAINTENANCE_DATE "
    			+ " WHERE MTN_TYPE_CODE='"+parm.getValue("MEASUREM_CODE")+"' ";
    	TParm mtnDateParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(mtnDateParm.getCount()>0){
    		this.messageBox("�������ѱ�ʹ�ã���ֹɾ������ѡ��ͣ�ã�");
    		return;
    	}
//    	System.out.println("parm:"+parm);
    	TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"onDeleteMeasuremDic", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("ɾ��ʧ��");
			err(result.getErrText());
			return;
		}
		this.messageBox("ɾ���ɹ�");
		this.onClear();
		this.onQuery();
    }
    
    /**
     * M�����¼���������ѯϸ����Ϣ
     */
    public void onTableMClick(){
    	TTable tableM = this.getTable("TABLEM");
    	int row = tableM.getSelectedRow();
    	if(row < 0){
    		return;
    	}
    	((TMenuItem) getComponent("save")).setEnabled(true);
    	((TMenuItem) getComponent("new")).setEnabled(false);
    	TParm mParm = tableM.getParmValue().getRow(row);
    	this.setValue("DEV_CODE", mParm.getValue("DEV_CODE"));
    	this.setValue("DEV_DESC", mParm.getValue("DEV_CHN_DESC"));
    	this.setValue("MEASUREM_CODE", mParm.getValue("MEASUREM_CODE"));
    	this.setValue("MEASUREM_DESC", mParm.getValue("MEASUREM_DESC"));
    	this.setValue("MEASUREM_PRICE", mParm.getValue("MEASUREM_PRICE"));
    	this.setValue("MEASUREM_CYCLE", mParm.getValue("MEASUREM_CYCLE"));
    	this.setValue("MEASUREM_UNIT", mParm.getValue("MEASUREM_UNIT"));
    	this.setValue("ACTIVE_FLG", mParm.getValue("ACTIVE_FLG"));
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
}
