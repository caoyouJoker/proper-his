package com.javahis.ui.nss;

import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

/**
 * <p>
 * Title: ����Ӫ�������ֵ�
 * </p>
 * 
 * <p>
 * Description: ����Ӫ�������ֵ�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author wangb 2015.3.13
 * @version 1.0
 */
public class NSSEnteralNutritionContainerDictControl extends TControl {
    public NSSEnteralNutritionContainerDictControl() {
        super();
    }

    private TTable table;

    /**
     * ��ʼ������
     */
    public void onInit() {
        table = getTable("TABLE");
        onQuery();
    }
    
    /**
     * ��ѯ����
     */
    public void onQuery() {
    	TParm parm = new TParm();
    	parm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
    	TParm resultParm = NSSEnteralNutritionTool.getInstance().selectDataRQ(parm);
    	table.setParmValue(resultParm);
    }
    
    /**
     * �������
     * add by lich
     */
    public void onSave(){
    	TTextField ContainerCode = (TTextField) getComponent("CONTAINER_CODE");
    	if(ContainerCode.isEnabled()){
    		messageBox("���������ݣ����������ť");
    		return;
    	}
    	TParm parm = new TParm();
    	parm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
    	TParm codeFlg = NSSEnteralNutritionTool.getInstance().isExistRQ(parm);
    	
    	//��������
    	if(0 == Integer.parseInt(codeFlg.getValue("COUNT", 0))){
    		TParm insertParm = new TParm();
    		//ҽ������
    		insertParm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
    		//ҽ����������
    		String chnDesc = this.getValueString("CONTAINER_DESC");
    		if(null == chnDesc || "".equals(chnDesc)){
    			messageBox("����дӪ���ɷ���������");
    			return;
    		}else{    			
    			insertParm.setData("CONTAINER_DESC", chnDesc);
    		}
    		//����
    		Double capacity = this.getValueDouble("CAPACITY");
    		if(0 == capacity){
    			messageBox("����д����");
    			return;
    		}else{    			
    			insertParm.setData("CAPACITY",capacity );
    		}
    		//��λ
    		String capacityUnit = this.getValueString("CAPACITY_UNIT");
    		if(null == capacityUnit||"".equals(capacityUnit)){
    			messageBox("����д��λ");
    			return;
    		}else{    			
    			insertParm.setData("CAPACITY_UNIT", capacityUnit);
    		}
    		insertParm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
    		insertParm.setData("OPT_USER", Operator.getID());
    		insertParm.setData("OPT_TERM", Operator.getIP());
    		String date = SystemTool.getInstance().getDate().toString();
    		insertParm.setData("OPT_DATE", date.substring(0, date.length()-2));
    		TParm resultParm = NSSEnteralNutritionTool.getInstance().insertDataRQ(insertParm);
    		if(resultParm.getErrCode()<0){
    			this.messageBox("����ʧ�ܣ�");
    			return;
    		}else{
    			this.messageBox("����ɹ���");
    		}
    		
    		//�޸Ĳ���	
    	}else{
    		TParm updateParm = new TParm();
    		updateParm.setData("CONTAINER_CODE", this.getValueString("CONTAINER_CODE"));
    		updateParm.setData("CONTAINER_DESC", this.getValueString("CONTAINER_DESC"));
    		updateParm.setData("CAPACITY", this.getValueDouble("CAPACITY"));
    		updateParm.setData("CAPACITY_UNIT", this.getValueString("CAPACITY_UNIT"));
    		updateParm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG"));
    		
    		updateParm.setData("OPT_USER", Operator.getID());
    		updateParm.setData("OPT_TERM", Operator.getIP());
    		String date = SystemTool.getInstance().getDate().toString();
    		updateParm.setData("OPT_DATE", date.substring(0, date.length()-2));
    		TParm resultParm = NSSEnteralNutritionTool.getInstance().updateDataRQ(updateParm);
    		if(resultParm.getErrCode()<0){
    			this.messageBox("����ʧ�ܣ�");
    			return;
    		}else{
    			this.messageBox("����ɹ���");
    		}
    	}
    	onClear();
    }
    
    
    
    
    
    /**
     * ɾ������
     * add by lich
     */
    public void onDelete(){
    	TParm delParm = new TParm();
    	
    	if (StringUtils.isEmpty(this.getValueString("CONTAINER_CODE"))) {
    		this.messageBox("��ѡ��Ҫɾ��������");
    		return;
    	}
    	
    	if (JOptionPane.showConfirmDialog(null, "�Ƿ�ɾ��ѡ�����ݣ�", "��Ϣ",
				JOptionPane.YES_NO_OPTION) == 0) {
    		delParm.setData("CONTAINER_CODE", this.getValue("CONTAINER_CODE"));
			TParm result =  NSSEnteralNutritionTool.getInstance().deleteDataRQ(delParm);;
			if(result.getErrCode()<0){
	    		this.messageBox("ɾ��ʧ�ܣ�");
	    	}else{
	    		this.messageBox("ɾ���ɹ���");
	    		onInit();
	    		onClear();
	    	}
		}
    	onInit();
    }
    /**
     * ��ղ���
     */
    public void onClear(){
    	this.clearValue("CONTAINER_CODE;CONTAINER_DESC;CAPACITY;CAPACITY_UNIT;ACTIVE_FLG ");
    	callFunction("UI|CONTAINER_CODE|setEnabled", true);
    	onInit();
    }
    
    
    
    /**
     * ����������ݺ󣬴��������Ϣ
     */
    public void onTableClick(){
    	TTable table = getTable("TABLE");
    	int row = table.getSelectedRow();
        this.setValue("CONTAINER_CODE", table.getItemData(row,"CONTAINER_CODE"));
        this.setValue("CONTAINER_DESC",  table.getItemData(row,"CONTAINER_DESC"));
        this.setValue("CAPACITY", table.getItemData(row,"CAPACITY"));
        this.setValue("CAPACITY_UNIT",  table.getItemData(row,"CAPACITY_UNIT"));
        this.setValue("ACTIVE_FLG", table.getItemData(row,"ACTIVE_FLG"));
       
        callFunction("UI|CONTAINER_CODE|setEnabled", false);
        callFunction("UI|CONTAINER_DESC|setEnabled", true);
    	
    }
    
    /**
     * �����µĳɷִ�������
     */
    public void onNewContainerCode(){
    	TParm maxCode = NSSEnteralNutritionTool.getInstance().getMaxContainerCode();
    	String newCode = getNewCode(maxCode.getValue("MAX", 0));
    	this.setValue("CONTAINER_CODE", newCode);
    	callFunction("UI|CONTAINER_CODE|setEnabled", false);
    }
    
    /**
     * ��ȡ�������������NUTRITION_CODE
     * @param NutritionCode
     * @return
     * add by lich
     */
    private String getNewCode(String Code){
    	
    	DecimalFormat df = new DecimalFormat("0000");
    	double code = 1;
    	try {
    		
    		code = Double.parseDouble(Code);
    		code += 1;		
    		return df.format(code);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return df.format(code);
    }
    
    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     * add by lich
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
}
