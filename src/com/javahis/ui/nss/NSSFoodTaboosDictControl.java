package com.javahis.ui.nss;

import java.text.DecimalFormat;

import javax.swing.JOptionPane;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;

/**
 * <p>Title: ��ʳ�����ֵ�</p>
 *
 * <p>Description: ��ʳ�����ֵ�</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2015.2.13
 * @version 1.0
 */
public class NSSFoodTaboosDictControl extends TControl {
    public NSSFoodTaboosDictControl() {
        super();
    }

    private TTable table;

    /**
     * ��ʼ������
     */
    public void onInit() {
        table = getTable("TABLE");
        onQueryInit();
    }

    
    
    /**
     * ��ѯ����
     */
    public void onQuery() {
    	TParm parm = new TParm();
    	parm.setData("TABOO_CODE", this.getValueString("TABOO_CODE"));
		//ҽ����������
    	parm.setData("TABOO_CHN_DESC", this.getValueString("TABOO_CHN_DESC"));
    	parm.setData("OPT_USER", Operator.getID());
    	parm.setData("OPT_TERM", Operator.getIP());
		String date = SystemTool.getInstance().getDate().toString();
		parm.setData("OPT_DATE", date.substring(0, date.length()-2));
        TParm resultParm = NSSEnteralNutritionTool.getInstance().selectDataJJ(parm);
        if(resultParm.getErrCode()<0){
			this.messageBox("��ѯʧ�ܣ�");
			return;
		}
        table.setParmValue(resultParm);
    	
//        String sql = "SELECT * FROM NSS_TABOO";
//        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//        table.setParmValue(result);
    }
    /**
     * ��ʼ����ѯ
     * add by lich
     */
    public void onQueryInit() {
    	String sql = "SELECT A.TABOO_CODE,A.TABOO_CHN_DESC,A.TABOO_ENG_DESC,A.PY1," +
    				" A.PY2,A.SEQ,A.DESCRIPTION,A.TABOO_FLG,A.TABOO_SQL ," +
    				" (SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = A.OPT_USER) AS OPT_USER, " +
    				" A.OPT_DATE,A.OPT_TERM FROM NSS_TABOO A ";
    	System.out.println("init = = " +sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
 			this.messageBox("��ʼ��ʧ�ܣ�");
 			return;
 		}
         table.setParmValue(result);
    	
    }
    
    
    /**
     * �������
     * add by lich
     */
    public void onSave(){
    	TTextField TabooCode = (TTextField) getComponent("TABOO_CODE");
    	if(TabooCode.isEnabled()){
    		messageBox("���������ݣ����������ť");
    		return;
    	}
    	TParm parm = new TParm();
    	parm.setData("TABOO_CODE", this.getValueString("TABOO_CODE"));
    	TParm codeFlg = NSSEnteralNutritionTool.getInstance().isExistJJ(parm);
    	
//    	TParm parm = new TParm();
    	//��������
    	if(0 == Integer.parseInt(codeFlg.getValue("COUNT", 0))){
    		TParm insertParm = new TParm();
    		//ҽ������
    		insertParm.setData("TABOO_CODE", this.getValueString("TABOO_CODE"));
    		//ҽ����������
    		String chnDesc = this.getValueString("TABOO_CHN_DESC");
    		if(null == chnDesc || "".equals(chnDesc)){
    			messageBox("����дӪ���ɷ���������");
    			return;
    		}else{    			
    			insertParm.setData("TABOO_CHN_DESC", chnDesc);
    		}
    	
    		insertParm.setData("TABOO_ENG_DESC",this.getValueString("TABOO_ENG_DESC") );
    		insertParm.setData("PY1",this.getValueString("PY1") );
    		insertParm.setData("PY2",this.getValueString("PY2") );
    		insertParm.setData("DESCRIPTION",this.getValueString("DESCRIPTION") );
    		insertParm.setData("SEQ",this.getValueDouble("SEQ"));
    		insertParm.setData("TABOO_FLG","");
    		insertParm.setData("TABOO_SQL","");
//    		insertParm.setData("NRV", this.getValueDouble("NRV"));
//    		insertParm.setData("UNIT_CODE", this.getValueString("UNIT_CODE"));
   
    		insertParm.setData("OPT_USER", Operator.getID());
    		insertParm.setData("OPT_TERM", Operator.getIP());
    		String date = SystemTool.getInstance().getDate().toString();
    		insertParm.setData("OPT_DATE", date.substring(0, date.length()-2));
    		TParm resultParm = NSSEnteralNutritionTool.getInstance().insertDataJJ(insertParm);
    		if(resultParm.getErrCode()<0){
    			this.messageBox("����ʧ�ܣ�");
    			return;
    		}else{
    			this.messageBox("����ɹ���");
    		}
    		
    		//�޸Ĳ���	
    	}else{
    		TParm updateParm = new TParm();
    		updateParm.setData("TABOO_CODE", this.getValueString("TABOO_CODE"));
    		updateParm.setData("TABOO_CHN_DESC", this.getValueString("TABOO_CHN_DESC"));
    		updateParm.setData("TABOO_ENG_DESC", this.getValueString("TABOO_ENG_DESC"));
    		updateParm.setData("PY1", this.getValueString("PY1"));
    		updateParm.setData("PY2", this.getValueString("PY2"));
    		updateParm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
    		updateParm.setData("SEQ",this.getValueDouble("SEQ"));
    		updateParm.setData("TABOO_FLG", "");
    		updateParm.setData("TABOO_SQL", "");
    		
    		updateParm.setData("OPT_USER", Operator.getID());
    		updateParm.setData("OPT_TERM", Operator.getIP());
    		String date = SystemTool.getInstance().getDate().toString();
    		updateParm.setData("OPT_DATE", date.substring(0, date.length()-2));
    		TParm resultParm = NSSEnteralNutritionTool.getInstance().updateDataJJ(updateParm);
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
     * ����������ݺ󣬴��������Ϣ
     */
    public void onTableClick(){
    	TTable table = getTable("TABLE");
    	int row = table.getSelectedRow();
        this.setValue("TABOO_CODE", table.getItemData(row,"TABOO_CODE"));
        this.setValue("TABOO_CHN_DESC",  table.getItemData(row,"TABOO_CHN_DESC"));
        this.setValue("TABOO_ENG_DESC", table.getItemData(row,"TABOO_ENG_DESC"));
        this.setValue("PY1",  table.getItemData(row,"PY1"));
        this.setValue("PY2", table.getItemData(row,"PY2"));
        this.setValue("DESCRIPTION", table.getItemData(row,"DESCRIPTION"));
        this.setValue("SEQ", table.getItemData(row,"SEQ"));
        callFunction("UI|TABOO_CODE|setEnabled", false);
        callFunction("UI|TABOO_CHN_DESC|setEnabled", true);
    	
    }
    
    
    /**
     * ɾ������
     * add by lich
     */
    public void onDelete(){
    	TParm delParm = new TParm();
    	
    	if (JOptionPane.showConfirmDialog(null, "�Ƿ�ɾ��ѡ�����ݣ�", "��Ϣ",
				JOptionPane.YES_NO_OPTION) == 0) {
    		delParm.setData("TABOO_CODE", this.getValue("TABOO_CODE"));
			TParm result =  NSSEnteralNutritionTool.getInstance().deleteDataJJ(delParm);;
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
     * add by lich
     */
    public void onClear(){
    	this.clearValue("TABOO_CODE;TABOO_CHN_DESC;TABOO_ENG_DESC;PY1;" +
    			"PY2;DESCRIPTION");
    	callFunction("UI|TABOO_CODE|setEnabled", true);
    	onInit();
    }
    
    /**
     * �����µĳɷִ�������
     */
    public void onNewTabooCode(){
    	TParm maxCode = NSSEnteralNutritionTool.getInstance().getMaxTabooCode();
    	String newCode = getNewCode(maxCode.getValue("MAX", 0));
    	String newSeq = getNewCode(maxCode.getValue("SEQ", 0));
    	this.setValue("TABOO_CODE", newCode);
    	this.setValue("SEQ", newSeq);
    	callFunction("UI|TABOO_CODE|setEnabled", false);
    }
    
    /**
     * ��ȡ�������������
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
     * TABOO_CHN_DESC�س��¼�
     */
    public void onUserNameAction() {
    	String py = TMessage.getPy(this.getValueString("TABOO_CHN_DESC"));
        setValue("PY1", py);
        ((TTextField) getComponent("PY1")).grabFocus();
    }
    
    /**
     * �õ�Table����
     *
     * @param tagName
     *        Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
