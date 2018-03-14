package com.javahis.ui.emr;

import com.dongyang.control.*;
import com.dongyang.data.*;
import com.dongyang.jdo.*;
import com.dongyang.ui.*;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSOperator;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class EMRComPhraseQuoteControl
    extends TControl {
    private static String sqlChangeDept = " SELECT * FROM OPD_COMPHRASE  ORDER BY DEPT_OR_DR,DEPTORDR_CODE,PHRASE_CODE ";
    private String drCode;
    private String deptCode;
    /**
     * 1:科室,2:人员
     */
    private String type;
    private TParm evenParm;
    /**
     * 权限 1:一般,2:最高
     */
    private String role;
    TTable table;
    TTextArea content;//add by wanglong 20120105

    public void onInit() {
        super.onInit();
        table = (TTable)this.getComponent("TABLECOM");
        content = (TTextArea)this.getComponent("CONTENT");//add by wanglong 20120105
        Object obj = this.getParameter();
        if(obj!=null){
            content.setLineWrap(true);//add by wanglong 20120105 
            evenParm = (TParm)obj;
            this.setDrCode(evenParm.getValue("DR_CODE"));
            this.setDeptCode(evenParm.getValue("DEPT_CODE"));
            this.type = evenParm.getValue("TYPE");
            this.setRole(evenParm.getValue("ROLE"));
            this.setValue("DEPT_CODE",this.getDeptCode());
            this.setValue("OPERATOR",this.getDrCode());
            TDataStore ds = new TDataStore();
            if("1".equals(type)){
                getTRadioButton("DEPT").setSelected(true);
                ds.setSQL(sqlChangeDept);
                ds.retrieve();
                ds.setFilter("DEPTORDR_CODE = '"+this.getDeptCode()+"'");
                ds.filter();
                table.setDataStore(ds);
                table.setDSValue();

            }
            if("2".equals(type)){
                getTRadioButton("DR").setSelected(true);
                ds.setSQL(sqlChangeDept);
                ds.retrieve();
                ds.setFilter("DEPTORDR_CODE = '"+this.getDrCode()+"'");
                ds.filter();
                table.setDataStore(ds);
                table.setDSValue();
            }
            if("1".equals(this.getRole())){
                ((TextFormatDept)this.getComponent("DEPT_CODE")).setEnabled(false);
                ((TextFormatSYSOperator)this.getComponent("OPERATOR")).setEnabled(false);
            }
            if("2".equals(this.getRole())){
                ((TextFormatDept)this.getComponent("DEPT_CODE")).setEnabled(true);
                ((TextFormatSYSOperator)this.getComponent("OPERATOR")).setEnabled(true);
            }
        }
    }
    /**
     * 返回TRadioButton
     * @param tag String
     * @return TRadioButton
     */
    public TRadioButton getTRadioButton(String tag){
        return (TRadioButton)this.getComponent(tag);
    }
    /**
     * 返回TComboBox
     * @param tag String
     * @return TRadioButton
     */
    public TComboBox getTComboBox(String tag){
        return (TComboBox)this.getComponent(tag);
    }

    /**
     * TABLE单击事件
     */
    public void onTABLEPATClicked() {
        TDataStore tds = table.getDataStore();
        int row = table.getSelectedRow();
        //String oldvalue = this.getValueString("CONTENT");//delete by wanglong 20130105
        this.setValue("CONTENT",tds.getItemString(row,"PHRASE_TEXT"));//modify by wanglong 20130105
    }

    /**
     * TABLE双击事件
     */
    public void onTABLEPATDoubleClicked() {
        TDataStore tds = table.getDataStore();
        int row = table.getSelectedRow();
        String oldvalue = tds.getItemString(row, "PHRASE_TEXT");
        this.setValue("CONTENT", oldvalue);
        onFetchBack();
    }

    /**
     * 部门或医师RADIO点击事件
     */
    public void onChange() {
        TDataStore tds = table.getDataStore();
        tds.setFilter("");
        tds.filter();
        if ("Y".equalsIgnoreCase(getValue("DEPT").toString())) {
            tds.setFilter("DEPTORDR_CODE = '"+this.getDeptCode()+"'");
            tds.filter();
            table.setDSValue();
        }else {
            tds.setFilter("DEPTORDR_CODE = '"+this.getDrCode()+"'");
            tds.filter();
            table.setDSValue();
        }
    }
    /**
     * 选择事件
     */
    public void onCheckQuery(){
        TDataStore tds = table.getDataStore();
        tds.setFilter("");
        tds.filter();
        if ("Y".equalsIgnoreCase(getValue("DEPT").toString())) {
            tds.setFilter("DEPTORDR_CODE = '" + this.getValueString("DEPT_CODE") + "'");
            tds.filter();
            table.setDSValue();
        }
        else {
            tds.setFilter("DEPTORDR_CODE = '" + this.getValueString("OPERATOR") + "'");
            tds.filter();
            table.setDSValue();
        }
    }
    /**
     * 类型选择
     */
    public void onSelType(){
        TDataStore tds = table.getDataStore();
        tds.setFilter("");
        tds.filter();
        if ("Y".equalsIgnoreCase(getValue("DEPT").toString())) {
            tds.setFilter("DEPTORDR_CODE = '" + this.getValueString("DEPT_CODE") + "' AND PHRASE_TYPE='"+this.getValueString("TYPE")+"'");
            tds.filter();
            table.setDSValue();
        }
        else {
            tds.setFilter("DEPTORDR_CODE = '" + this.getValueString("OPERATOR") + "' AND PHRASE_TYPE='"+this.getValueString("TYPE")+"'");
            tds.filter();
            table.setDSValue();
        }
    }
    /**
     * 模糊查询
     */
    public void onLikeQ(){
        TDataStore tds = table.getDataStore();
        tds.setFilter("");
        tds.filter();
        if ("Y".equalsIgnoreCase(getValue("DEPT").toString())) {
            if(this.getValueString("TYPE").length()!=0){
                tds.setFilter("DEPTORDR_CODE = '" + this.getValueString("DEPT_CODE") + "' AND PHRASE_TYPE='"+this.getValueString("TYPE")+"' AND PY1 LIKE '"+this.getValueString("LIKECODE").toUpperCase()+"%'");
            }else{
                tds.setFilter("DEPTORDR_CODE = '" + this.getValueString("DEPT_CODE") + "' AND PY1 LIKE '"+this.getValueString("LIKECODE").toUpperCase()+"%'");
            }
            tds.filter();
            table.setDSValue();
        }else {
            if(this.getValueString("TYPE").length()!=0){
                tds.setFilter("DEPTORDR_CODE = '" + this.getValueString("OPERATOR") + "' AND PHRASE_TYPE='"+this.getValueString("TYPE")+"' AND PY1 LIKE '"+this.getValueString("LIKECODE").toUpperCase()+"%'");
            }else{
                tds.setFilter("DEPTORDR_CODE = '" + this.getValueString("OPERATOR") + "' AND PY1 LIKE '"+this.getValueString("LIKECODE").toUpperCase()+"%'");
            }
            tds.filter();
            table.setDSValue();
        }
    }
    /**
     * 传回
     */
    public void onFetchBack() {
        evenParm.runListener("onReturnContent",this.getValue("CONTENT"));
    }
    public void onClear(){
        this.clearValue("CONTENT");
    }
    public String getDeptCode() {
        return deptCode;
    }

    public String getDrCode() {
        return drCode;
    }

    public String getRole() {
        return role;
    }

    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    public void setDrCode(String drCode) {
        this.drCode = drCode;
    }

    public void setRole(String role) {
        this.role = role;
    }


}
