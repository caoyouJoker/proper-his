package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
import jdo.bms.BMSApplyMTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.ui.TMenuItem;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 备血申请单查询
 * </p>
 *
 * <p>
 * Description: 备血申请单查询
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSApplyNoQueryControl  extends TControl {

    public BMSApplyNoQueryControl() {  
    }

    private TTable table;

    /**
     * 初始化方法
     */
    public void onInit() {
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery(){
        TParm parm = new TParm();
        // 门急住别
        String adm_type = "O";
        if (this.getRadioButton("ADM_TYPE_E").isSelected()) {
            adm_type = "E";
        }
        else if (this.getRadioButton("ADM_TYPE_I").isSelected()) {
            adm_type = "I";
        }
        parm.setData("ADM_TYPE", adm_type);
        // 病案号
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", getValueString("MR_NO"));
        }
        // 住院号
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parm.setData("IPD_NO", getValueString("IPD_NO"));
        }
        // 备血单号
        if (!"".equals(this.getValueString("APPLY_NO"))) {
            parm.setData("APPLY_NO", getValueString("APPLY_NO"));
        }
        // 备血日期
        parm.setData("START_DATE", getValue("START_DATE"));
        parm.setData("END_DATE", getValue("END_DATE"));
        TParm result = BMSApplyMTool.getInstance().onApplyNoQuery(parm);
        if (result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        table.setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear(){
        String clearStr = "MR_NO;IPD_NO;PAT_NAME;APPLY_NO";
        this.clearValue(clearStr);
        table.removeRowAll();
        this.getRadioButton("ADM_TYPE_O").setSelected(true);
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        ( (TMenuItem) getComponent("apply")).setEnabled(false);
        ( (TMenuItem) getComponent("check")).setEnabled(false);
        ( (TMenuItem) getComponent("cross")).setEnabled(false);
        ( (TMenuItem) getComponent("out")).setEnabled(false);

    }

    /**
     * 病案号回车事件
     */
    public void onMrNoAction() {
    	// modify by huangtt 20160928 EMPI患者查重提示 start
        String mr_no = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        this.setValue("MR_NO", mr_no);  //  chenxi modify  20121023
        Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
			mr_no = pat.getMrNo();
			this.setValue("MR_NO", mr_no);
		}
		// modify by huangtt 20160928 EMPI患者查重提示 end
    
    
    }

    /**
     * 呼叫备血申请单
     */
    public void onApply() {
        TParm parm = new TParm();
        parm.setData("APPLY_NO",
                     table.getItemString(table.getSelectedRow(), "APPLY_NO"));
        parm.setData("FROM_FLG", "2");
        TParm result = (TParm) openDialog("%ROOT%\\config\\bms\\BMSApply.x",
                                          parm);
    }

    /**
     * 呼叫检验记录
     */
    public void onCheck() {
        TParm parm = new TParm();
        parm.setData("APPLY_NO",
                     table.getItemString(table.getSelectedRow(), "APPLY_NO"));
        TParm result = (TParm) openDialog(
            "%ROOT%\\config\\bms\\BMSPatCheckInfo.x", parm);
    }

    /**
     * 呼叫交叉配血
     */
    public void onCross() {
        TParm parm = new TParm();
        parm.setData("APPLY_NO",
                     table.getItemString(table.getSelectedRow(), "APPLY_NO"));
        TParm result = (TParm) openDialog(
            "%ROOT%\\config\\bms\\BMSBloodCross.x", parm);
    }

    /**
     * 呼叫血液出库
     */
    public void onOut() {
        TParm parm = new TParm();
        parm.setData("APPLY_NO",
                     table.getItemString(table.getSelectedRow(), "APPLY_NO"));
        TParm result = (TParm) openWindow("%ROOT%\\config\\bms\\BMSBloodOut.x",
                                          parm);
    }
    //======================  chenxi 

    /**
     * 呼叫血液费用明细
     */
    public void onFeeDetail() {
    	TParm tableParm = table.getParmValue() ;
        TParm parm = new TParm();
        parm.setData("CASE_NO",tableParm.getValue("CASE_NO",table.getSelectedRow()));
        parm.setData("PAT_NAME",tableParm.getValue("PAT_NAME",table.getSelectedRow()));
        TParm result = (TParm) openWindow("%ROOT%\\config\\bms\\BMSQueryFee.x",parm);
    }
    /**
     * 表格单击事件
     */
    public void onTableClick(){
        ( (TMenuItem) getComponent("apply")).setEnabled(true);
        ( (TMenuItem) getComponent("check")).setEnabled(true);
        ( (TMenuItem) getComponent("cross")).setEnabled(true);
        ( (TMenuItem) getComponent("out")).setEnabled(true);
        ( (TMenuItem) getComponent("feeDetail")).setEnabled(true);
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        table = getTable("TABLE");
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        ( (TMenuItem) getComponent("apply")).setEnabled(false);
        ( (TMenuItem) getComponent("check")).setEnabled(false);
        ( (TMenuItem) getComponent("cross")).setEnabled(false);
        ( (TMenuItem) getComponent("out")).setEnabled(false);
        ( (TMenuItem) getComponent("feeDetail")).setEnabled(false);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    /**
	 * 取血申请 add by pangben 2016-5-6
	 */
	public void onQXResult() {

		TParm parmdate = new TParm();
		if(table.getSelectedRow()<0){
			this.messageBox("请选择要操作的病患");
			return;
		}
    	TParm tableParm = table.getParmValue() ;
		String caseno =tableParm.getValue("CASE_NO",table.getSelectedRow());
		String deptCode = tableParm.getValue("DEPT_CODE",table.getSelectedRow());
		String mrno = tableParm.getValue("MR_NO",table.getSelectedRow());
		parmdate.setData("ADM_TYPE", "I");
		parmdate.setData("IPD_NO", tableParm.getValue("IPD_NO",table.getSelectedRow()));
		parmdate.setData("USE_DATE", SystemTool.getInstance().getDate());
		parmdate.setData("DR_CODE", Operator.getID());
		parmdate.setData("CASE_NO", caseno);
		parmdate.setData("MR_NO", mrno);
		parmdate.setData("DEPT_CODE", deptCode);
		parmdate.setData("OPE_ROOM", "");
		parmdate.setData("OPE_FLG", "N");
		parmdate.setData("APPLYNO_FLG","Y");
		this.openDialog("%ROOT%\\config\\bms\\BMSBloodTake.x", parmdate);
		//PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));

	}
}
