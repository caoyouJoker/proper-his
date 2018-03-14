package com.javahis.ui.mro;

import java.sql.Timestamp;

import jdo.mro.MROQueueTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 病案出入库管理</p>
 *
 * <p>Description: 病案出入库管理</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk  2009-5-6
 * @version 1.0
 */
public class MROQueueControl
    extends TControl {
    private TParm data;
    private TParm resultPage1;
    private TParm resultPage2;
    private TParm resultPage3;
    private String IN_FLG;
    public void onInit() {
        super.onInit();
        this.setValue("t3_STATE","0");
    }
    /**
     * 清空
     */
    public void onClear(){
    	int selPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
    	switch (selPage) {
		case 0:
			//清空第一页签
	        this.clearValue("t1_IPD_NO;t1_MR_NO;t1_QUE_SEQ;t1_QUE_DATE;t1_RTN_DATE;t1_DUE_DATE;t1_LEND_CODE");
	        ((TTable)this.getComponent("Table1")).clearSelection();
	        ((TTable)this.getComponent("Table1")).removeRowAll();
			break;
		case 1:
			//清空第二页签
	        this.clearValue("t2_IPD_NO;t2_MR_NO;isOutHp");
	        ((TTable)this.getComponent("Table2")).clearSelection();	
	        ((TTable)this.getComponent("Table2")).removeRowAll();
			break;
		case 2:
			//清空第三页签
	        this.clearValue("t3_LEND_CODE;t3_RTN_DATE;t3_REQ_DEPT;t3_MR_PERSON;t3_isOutHp;t3_OUT_DATE");
	        ((TTable)this.getComponent("Table3")).clearSelection();
	        ((TTable)this.getComponent("Table3")).removeRowAll();
	        ont3_isOutHp();
	        this.setValue("t3_STATE", "0");
			break;
		case 3:
			//清空第四页签
	        this.clearValue("t4_IPD_NO;t4_MR_NO;QUE_DATE_START;QUE_DATE_END;IN_DATE_START;IN_DATE_END");
	        ((TTable)this.getComponent("Table4")).clearSelection();
	        ((TTable)this.getComponent("Table4")).removeRowAll();
			break;
		default:
			break;
		}
    }
    /**
     * 查询
     */
    public void onQuery(){
        //获取当前选择的页签 索引
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        //第一页签
        if(selectedPage==0){
            query1();
        }else if(selectedPage==1){
            query2();
        }else if(selectedPage==2){
            query3();
        }else if(selectedPage==3){
            query4();
        }
    }
    /**
     * 第一页签查询
     */
    private void query1() {
        TParm parm = new TParm();
        resultPage1 = new TParm();
        //IPD_NO
        if (getValueString("t1_IPD_NO").trim().length() > 0) {
            String ipd_no = PatTool.getInstance().checkIpdno(getValueString(
                "t1_IPD_NO"));
            this.setValue("t1_IPD_NO", ipd_no);
            parm.setData("IPD_NO", ipd_no);
        }
        //MR_NO
        if (getValueString("t1_MR_NO").trim().length() > 0) {
            String mr_no = PatTool.getInstance().checkMrno(getValueString(
                "t1_MR_NO"));
            this.setValue("t1_MR_NO", mr_no);
         // modify by huangtt 20160929 EMPI患者查重提示 start
            Pat pat = Pat.onQueryByMrNo(mr_no);
            if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
				this.setValue("t1_MR_NO", pat.getMrNo());// 病案号
				mr_no = pat.getMrNo();
			}
         // modify by huangtt 20160929 EMPI患者查重提示 end   
            parm.setData("MR_NO", mr_no);
        }
        //借阅号
        if (getValueString("t1_QUE_SEQ").trim().length() > 0) {
            parm.setData("QUE_SEQ", getValueString("t1_QUE_SEQ").trim());
        }
        parm.setData("ISSUE_CODE", "0"); //查询待出库状态的病案
        //借阅日期
        if(this.getValueString("t1_QUE_DATE").trim().length() > 0){
        	parm.setData("QUE_DATE", this.getValueString("t1_QUE_DATE").replace("-", "").substring(0, 8));
        }
        resultPage1 = MROQueueTool.getInstance().queryQueue(parm);
        if (resultPage1.getErrCode() < 0) {
            this.messageBox("查询失败！");
            return;
        }
        if(resultPage1.getCount() <= 0){
        	return;
        }
        TTable table = (TTable)this.getComponent("Table1");
        table.setParmValue(resultPage1);
    }
    /**
     * 第二页签查询
     */
    private void query2(){ 
        TParm parm = new TParm();
        resultPage2 = new TParm();
        //IPD_NO
        if (getValueString("t2_IPD_NO").trim().length() > 0) {
            String ipd_no = PatTool.getInstance().checkIpdno(getValueString(
                "t2_IPD_NO"));
            this.setValue("t2_IPD_NO", ipd_no);
            parm.setData("IPD_NO", ipd_no);
        }
        //MR_NO
        if (getValueString("t2_MR_NO").trim().length() > 0) {
            String mr_no = PatTool.getInstance().checkMrno(getValueString(
                "t2_MR_NO"));
            this.setValue("t2_MR_NO", mr_no);
            // modify by huangtt 20160929 EMPI患者查重提示 start
            Pat pat = Pat.onQueryByMrNo(mr_no);
            if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
				this.setValue("t2_MR_NO", pat.getMrNo());// 病案号
				mr_no = pat.getMrNo();
			}
         // modify by huangtt 20160929 EMPI患者查重提示 end  
            parm.setData("MR_NO", mr_no);
        }
        resultPage2 = MROQueueTool.getInstance().selectIn(parm);
        if (resultPage2.getErrCode() < 0) {
            this.messageBox("查询失败！" + resultPage2.getErrCode() + resultPage2.getErrName() + resultPage2.getErrText());
            ((TTable)this.getComponent("Table2")).removeRowAll();
            return;
        }
        if(resultPage2.getCount() <= 0){
        	return;
        }
        TTable table = (TTable)this.getComponent("Table2");
        table.setParmValue(resultPage2);
    }
    /**
     * 第三页签查询
     */
    private void query3(){
        TParm parm = new TParm();
        resultPage3 = new TParm();
        String flg = this.getValueString("t3_isOutHp");
        //===========pangben modify 20110518 start 添加区域参数
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            parm.setData("REGION_CODE",Operator.getRegion());
        //===========pangben modify 20110518 stop

        //如果是 出院病历归档查询
        if("Y".equals(flg)){
            if(this.getValue("t3_OUT_DATE")!=null){
                parm.setData("DS_DATE",this.getValue("t3_OUT_DATE").toString().replace("-", "").substring(0, 8));
            }
            resultPage3 = MROQueueTool.getInstance().selectOutHp(parm);
        }else{
            if(this.getValueString("t3_STATE").length()>0){
                parm.setData("ISSUE_CODE",this.getValueString("t3_STATE"));
            }
            if(this.getValueString("t3_LEND_CODE").length()>0){
                parm.setData("LEND_CODE",this.getValueString("t3_LEND_CODE"));
            }
            if(this.getValueString("t3_REQ_DEPT").length()>0){
                parm.setData("REQ_DEPT",this.getValueString("t3_REQ_DEPT"));
            }
            if(this.getValueString("t3_MR_PERSON").length()>0){
                parm.setData("MR_PERSON",this.getValueString("t3_MR_PERSON"));
            }
            if(this.getValue("t3_RTN_DATE")!=null){
                parm.setData("RTN_DATE",this.getValueString("t3_RTN_DATE").replace("-","").substring(0, 8));
            }
            resultPage3 = MROQueueTool.getInstance().selectOutQueue(parm);
            IN_FLG = this.getValueString("t3_STATE");
        }
        if (resultPage3.getErrCode() < 0) {
            this.messageBox("E0005");
            ((TTable)this.getComponent("Table3")).removeRowAll();
            return;
        }
        if (resultPage3.getCount() <= 0) {
            ((TTable)this.getComponent("Table3")).removeRowAll();
            return;
        }
        ((TTable)this.getComponent("Table3")).setParmValue(resultPage3);
    }
    /**
     * 第四页签查询
     */
    private void query4(){
        TParm parm = new TParm();
        if(this.getValueString("t4_IPD_NO").trim().length()>0){
            String ipd_no = PatTool.getInstance().checkIpdno(getValueString("t4_IPD_NO"));
            this.setValue("t4_IPD_NO", ipd_no);
            parm.setData("IPD_NO", ipd_no);
        }
        //===========pangben modify 20110518 start 添加区域参数
        if(null!=Operator.getRegion()&&Operator.getRegion().length()>0)
            parm.setData("REGION_CODE",Operator.getRegion());
        //===========pangben modify 20110518 stop
        //MR_NO
        if (getValueString("t4_MR_NO").trim().length() > 0) {
            String mr_no = PatTool.getInstance().checkMrno(getValueString("t4_MR_NO"));
            this.setValue("t4_MR_NO", mr_no);
         // modify by huangtt 20160929 EMPI患者查重提示 start
            Pat pat = Pat.onQueryByMrNo(mr_no);
            if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
				this.setValue("t4_MR_NO", pat.getMrNo());// 病案号
				mr_no = pat.getMrNo();
			}
         // modify by huangtt 20160929 EMPI患者查重提示 end  
            parm.setData("MR_NO", mr_no);
        }
        //出库日期
        if(this.getValue("QUE_DATE_START") != null){
            parm.setData("QUE_DATE_START",this.getValue("QUE_DATE_START").toString().replace("-", "").substring(0, 8));
        }
        if(this.getValue("QUE_DATE_END") != null){
        	parm.setData("QUE_DATE_END",this.getValue("QUE_DATE_END").toString().replace("-", "").substring(0, 8));
        }
        //入库日期  需要转换成字符串 格式的日期 YYYYMMDD
        if(this.getValue("IN_DATE_START") != null){
            parm.setData("IN_DATE_START",this.getValue("IN_DATE_START").toString().replace("-", "").substring(0, 8));
        }
        if(this.getValue("IN_DATE_END") != null){
        	parm.setData("IN_DATE_END",this.getValue("IN_DATE_END").toString().replace("-", "").substring(0, 8));
        }
        data = MROQueueTool.getInstance().selectTRANHIS(parm);
        if (data.getErrCode() < 0) {
            this.messageBox("查询失败！");
            ((TTable)this.getComponent("Table4")).removeRowAll();
            return;
        }
        if(data.getCount() <= 0){
        	((TTable)this.getComponent("Table4")).removeRowAll();
        	return;
        }
        TTable table = (TTable)this.getComponent("Table4");
        table.setParmValue(data);
    }
    /**
     * 保存
     */
    public void onSave(){
        //获取当前选择的页签 索引
        int selectedPage = ((TTabbedPane)this.getComponent("tTabbedPane_0")).getSelectedIndex();
        //第一页签 病历出库
        if(selectedPage==0){
            save1();
        }else if(selectedPage==1){
            save2();
        }else if(selectedPage==2){
            save3();
        }
    }
    /**
     * 第一页签保存方法
     */
    public void save1(){
        int row = ( (TTable)this.getComponent("Table1")).getSelectedRow();
        if(row<0){
            this.messageBox("请选择病案!");
            return;
        }
        TParm submit = new TParm();
        TParm parm = new TParm();
        
        //病案主档修改参数
        parm.setData("MR_NO", resultPage1.getValue("MR_NO", row));
        parm.setData("IN_FLG", "1"); //病历主档 病历库存状态
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        submit.setData("MRV",parm.getData());

        //病案待借修改参数
        parm = new TParm();
        parm.setData("QUE_SEQ", resultPage1.getValue("QUE_SEQ", row));
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        parm.setData("ISSUE_CODE", "1"); //病历待出库档 病历库存状态
        submit.setData("Queue",parm.getData());

        //历史表修改参数
        parm = new TParm();
        //插入历史记录表需要的参数
        parm.setData("IPD_NO",resultPage1.getValue("IPD_NO",row));
        parm.setData("MR_NO", resultPage1.getValue("MR_NO", row));
        parm.setData("QUE_DATE",StringTool.getString((Timestamp)resultPage1.getData("QUE_DATE",row), "yyyyMMdd") + 
        		SystemTool.getInstance().getDate().toString().substring(0, SystemTool.getInstance().getDate().toString().lastIndexOf("."))
    			.replace("-", "").replace(" ", "").replace(":", "").substring(8, 14));
        parm.setData("TRAN_KIND","0");//出入库别：0 出库 1入库
        parm.setData("QUE_SEQ",resultPage1.getValue("QUE_SEQ",row));
        parm.setData("LEND_CODE",resultPage1.getValue("LEND_CODE",row));
        parm.setData("CURT_LOCATION","");//目前位置
        parm.setData("REGION_CODE","");//挂号区域
        parm.setData("MR_PERSON",resultPage1.getValue("MR_PERSON",row));//借阅人
        parm.setData("TRAN_HOSP",resultPage1.getValue("QUE_HOSP",row));//出入库院区
        TNull t = new TNull(Timestamp.class);
        parm.setData("IN_DATE",t);//归还入库日期
        parm.setData("IN_PERSON","");//入库人员
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        parm.setData("REGION_CODE", Operator.getRegion());
        submit.setData("Tranhis",parm.getData());
        TParm result = TIOM_AppServer.executeAction(
            "action.mro.MROQueueAction",
            "updateOUT",submit);
        if (result.getErrCode() < 0) {
            this.messageBox("出库操作失败！" + result.getErrName() + result.getErrCode() + result.getErrText());
            return;
        }
        this.messageBox("出库操作成功！");
        onClear();
    }
    /**
     * 第二页签保存方法
     */
    public void save2(){
        TParm submit = new TParm();
        TParm parm = new TParm();
        TParm result = new TParm();
        //如果是 “出院病历归档” 只修改 病历主档表的状态位 IN_FLG 设置为在库
        if(((TCheckBox)this.getComponent("isOutHp")).isSelected()){
            if(this.getValueString("t2_MR_NO").trim().length()<=0){
                this.messageBox_("请输入病案号!");
                return;
            }
            //“出院病历归档” 的病案 在MRO_Queue表中并不存在 要获取输入的MR_NO
            String mr_no =PatTool.getInstance().checkMrno(this.getValueString("t2_MR_NO"));
            parm.setData("MR_NO",mr_no);
            parm.setData("IN_FLG", "2"); //病历主档 病历库存状态 (在库)
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            this.setValue("t2_MR_NO",mr_no);
            result = TIOM_AppServer.executeAction(
                "action.mro.MROQueueAction",
                "updateIN_FLG", parm);
        }
        else{
            int row = ( (TTable)this.getComponent("Table2")).getSelectedRow(); //获取选中行
            if (row < 0) {
                this.messageBox_("请选择病案!");
                return;
            }
            if(resultPage2.getData("QUE_SEQ", row)==null){
                this.messageBox_("此病例为出院病历，请选择出院病历归档！");
                return;
            }
            //病案主档修改参数
            parm.setData("QUE_SEQ", resultPage2.getValue("QUE_SEQ", row));
            parm.setData("ISSUE_CODE", "2"); //病历待出库档 病历库存状态 (归还)
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            submit.setData("Queue",parm.getData());

            //病案待借修改参数
            parm = new TParm();
            parm.setData("MR_NO",resultPage2.getValue("MR_NO",row));
            parm.setData("IN_FLG", "2"); //病历主档 病历库存状态 (在库)
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            submit.setData("MRV",parm.getData());

            //插入历史记录表需要的参数
            parm = new TParm();
            parm.setData("IPD_NO", resultPage2.getValue("IPD_NO", row));
            parm.setData("MR_NO", resultPage2.getValue("MR_NO", row));
            parm.setData("QUE_DATE",
                         StringTool.getString( (Timestamp) resultPage2.
                                              getData("QUE_DATE", row),
                                              "yyyyMMddHHmmss"));
            parm.setData("TRAN_KIND", "1"); //出入库别：0 出库 1入库
            parm.setData("QUE_SEQ",resultPage2.getValue("QUE_SEQ",row));
            parm.setData("LEND_CODE", resultPage2.getValue("LEND_CODE", row));
            parm.setData("CURT_LOCATION", ""); //目前位置
            parm.setData("REGION_CODE", Operator.getRegion()); //挂号区域
            parm.setData("MR_PERSON", resultPage2.getValue("MR_PERSON", row)); //借阅人
            parm.setData("TRAN_HOSP", resultPage2.getValue("QUE_HOSP", row)); //出入库院区
            parm.setData("IN_DATE", SystemTool.getInstance().getDate()); //归还入库日期
            parm.setData("IN_PERSON", Operator.getID()); //入库人员
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", SystemTool.getInstance().getDate()); //操作日期
            parm.setData("OPT_TERM", Operator.getIP());
            submit.setData("Tranhis", parm.getData());

            result = TIOM_AppServer.executeAction(
                "action.mro.MROQueueAction",
                "updateIN", submit);
        }
        if (result.getErrCode() < 0) {
            this.messageBox_("入库操作失败！" + result.getErrName());
            return;
        }
        this.messageBox_("入库操作成功！");
        onClear();
    }
    //第三页签保存操作
    private void save3(){
        TParm parm = new TParm();
        TParm result = new TParm();
        int row = ( (TTable)this.getComponent("Table3")).getSelectedRow(); //获取选中行
        if(row<0){
            this.messageBox_("请选中一行信息！");
            return;
        }
        //如果是 “出院病历归档” 只修改 病历主档表的状态位 IN_FLG 设置为在库
        if(((TCheckBox)this.getComponent("t3_isOutHp")).isSelected()){
            parm.setData("MR_NO",resultPage3.getValue("MR_NO",row));
            parm.setData("IN_FLG", "2"); //病历主档 病历库存状态 (在库)
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            result = TIOM_AppServer.executeAction("action.mro.MROQueueAction","updateIN_FLG", parm);
            if(result.getErrCode()<0){
                this.messageBox("E0005");
                return;
            }
        }else{
            if ("0".equals(IN_FLG)) {//出库操作
                TParm submit = new TParm();
                TParm parm1 = new TParm();
                //病案主档修改参数
                parm1.setData("MR_NO", resultPage3.getValue("MR_NO", row));
                parm1.setData("IN_FLG", "1"); //病历主档 病历库存状态
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                submit.setData("MRV", parm1.getData());

                //病案待借修改参数
                parm1 = new TParm();
                parm1.setData("QUE_SEQ", resultPage3.getValue("QUE_SEQ", row));
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                parm1.setData("ISSUE_CODE", "1"); //病历待出库档 病历库存状态
                submit.setData("Queue", parm1.getData());

                //历史表修改参数
                parm1 = new TParm();
                //插入历史记录表需要的参数
                parm1.setData("IPD_NO", resultPage3.getValue("IPD_NO", row));
                parm1.setData("MR_NO", resultPage3.getValue("MR_NO", row));
                parm1.setData("QUE_DATE",
                             StringTool.getString( (Timestamp) resultPage3.
                                                  getData("QUE_DATE", row),
                                                  "yyyyMMddHHmmss"));
                parm1.setData("TRAN_KIND", "0"); //出入库别：0 出库 1入库
                parm1.setData("QUE_SEQ", resultPage3.getValue("QUE_SEQ", row));
                parm1.setData("LEND_CODE", resultPage3.getValue("LEND_CODE", row));
                parm1.setData("CURT_LOCATION", ""); //目前位置
                parm1.setData("REGION_CODE", ""); //挂号区域
                parm1.setData("MR_PERSON", resultPage3.getValue("MR_PERSON", row)); //借阅人
                parm1.setData("TRAN_HOSP", resultPage3.getValue("QUE_HOSP", row)); //出入库院区
                TNull t = new TNull(Timestamp.class);
                parm1.setData("IN_DATE", t); //归还入库日期
                parm1.setData("IN_PERSON", ""); //入库人员
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                submit.setData("Tranhis", parm1.getData());
                TParm re = TIOM_AppServer.executeAction("action.mro.MROQueueAction","updateOUT", submit);
                if (re.getErrCode() < 0) {
                    this.messageBox_("出库操作失败！" + re.getErrName() + re.getErrCode() + re.getErrText());
                    return;
                }
                this.messageBox_("出库操作成功！");

            }else if("1".equals(IN_FLG)){//入库操作
                TParm submit = new TParm();
                TParm parm1 = new TParm();
                //病案主档修改参数
                parm1.setData("QUE_SEQ", resultPage3.getValue("QUE_SEQ", row));
                parm1.setData("ISSUE_CODE", "2"); //病历待出库档 病历库存状态 (归还)
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                submit.setData("Queue", parm1.getData());

                //病案待借修改参数
                parm1 = new TParm();
                parm1.setData("MR_NO", resultPage3.getValue("MR_NO", row));
                parm1.setData("IN_FLG", "2"); //病历主档 病历库存状态 (在库)
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                submit.setData("MRV", parm1.getData());

                //插入历史记录表需要的参数
                parm1 = new TParm();
                parm1.setData("IPD_NO", resultPage3.getValue("IPD_NO", row));
                parm1.setData("MR_NO", resultPage3.getValue("MR_NO", row));
                parm1.setData("QUE_DATE",
                             StringTool.getString( (Timestamp) resultPage3.
                                                  getData("QUE_DATE", row),
                                                  "yyyyMMddHHmmss"));
                parm1.setData("TRAN_KIND", "1"); //出入库别：0 出库 1入库
                parm1.setData("QUE_SEQ", resultPage3.getValue("QUE_SEQ", row));
                parm1.setData("LEND_CODE", resultPage3.getValue("LEND_CODE", row));
                parm1.setData("CURT_LOCATION", ""); //目前位置
                parm1.setData("REGION_CODE", ""); //挂号区域
                parm1.setData("MR_PERSON", resultPage3.getValue("MR_PERSON", row)); //借阅人
                parm1.setData("TRAN_HOSP", resultPage3.getValue("QUE_HOSP", row)); //出入库院区
                parm1.setData("IN_DATE", SystemTool.getInstance().getDate()); //归还入库日期
                parm1.setData("IN_PERSON", Operator.getID()); //入库人员
                parm1.setData("OPT_USER", Operator.getID());
                parm1.setData("OPT_TERM", Operator.getIP());
                submit.setData("Tranhis", parm1.getData());
                TParm re = TIOM_AppServer.executeAction(
                    "action.mro.MROQueueAction",
                    "updateIN", submit);
                if (re.getErrCode() < 0) {
                    this.messageBox_("入库操作失败！" + re.getErrName() + re.getErrCode() + re.getErrText());
                    return;
                }
                this.messageBox_("入库操作成功！");
            }
        }
        onClear();
    }
    /**
     * 页签切换
     */
    public void pageChange(){
        TTabbedPane tp = (TTabbedPane)this.getComponent("tTabbedPane_0");
        int index = tp.getSelectedIndex();
        if(index==3){
            //隐藏按钮
            ((TMenuItem) getComponent("save")).setVisible(false);
        }
        else{
            //显示按钮
            ((TMenuItem) getComponent("save")).setVisible(true);
        }
    }

    /**
     * Table1点击事件
     */
    public void selectRow1() {
        TTable table = (TTable)this.getComponent("Table1");
        int row = table.getSelectedRow();
        this.setValue("t1_IPD_NO",resultPage1.getData("IPD_NO",row));//住院号
        this.setValue("t1_MR_NO",resultPage1.getData("MR_NO",row));//病案号
        this.setValue("t1_QUE_SEQ",resultPage1.getData("QUE_SEQ",row));//借阅好
        this.setValue("t1_QUE_DATE",resultPage1.getData("QUE_DATE",row));//借阅日期
        this.setValue("t1_RTN_DATE",resultPage1.getData("RTN_DATE",row));//应归还日期
        this.setValue("t1_DUE_DATE",resultPage1.getData("DUE_DATE",row));//应完成日
        this.setValue("t1_LEND_CODE",resultPage1.getData("LEND_CODE",row));//借阅原因
    }
    /**
     * Table2点击事件
     */
    public void selectRow2() {
        TTable table = (TTable)this.getComponent("Table2");
        int row = table.getSelectedRow();
        this.setValue("t2_IPD_NO",resultPage2.getData("IPD_NO",row));//住院号
        this.setValue("t2_MR_NO",resultPage2.getData("MR_NO",row));//病案号
    }
    /**
     * 页签三 出院病历归档 点击事件
     */
    public void ont3_isOutHp(){
        if("Y".equals(this.getValueString("t3_isOutHp"))){
            this.callFunction("UI|t3_STATE|setEnabled", false);
            this.callFunction("UI|t3_LEND_CODE|setEnabled", false);
            this.callFunction("UI|t3_RTN_DATE|setEnabled", false);
            this.callFunction("UI|t3_REQ_DEPT|setEnabled", false);
            this.callFunction("UI|t3_MR_PERSON|setEnabled", false);
            this.callFunction("UI|t3_OUT_DATE|setEnabled", true);
        }else {
            this.callFunction("UI|t3_STATE|setEnabled", true);
            this.callFunction("UI|t3_LEND_CODE|setEnabled", true);
            this.callFunction("UI|t3_RTN_DATE|setEnabled", true);
            this.callFunction("UI|t3_REQ_DEPT|setEnabled", true);
            this.callFunction("UI|t3_MR_PERSON|setEnabled", true);
            this.callFunction("UI|t3_OUT_DATE|setEnabled", false);
        }
        this.clearValue("t3_STATE;t3_LEND_CODE;t3_RTN_DATE;t3_REQ_DEPT;t3_MR_PERSON;t3_OUT_DATE;");
        TTable table3 = (TTable)this.getComponent("Table3");
        table3.removeRowAll();
    }
}
