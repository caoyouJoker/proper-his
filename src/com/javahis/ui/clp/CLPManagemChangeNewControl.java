package com.javahis.ui.clp;

import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatCLPDuration;
import com.javahis.system.textFormat.TextFormatCLPEvlStandm;
/**
 * <p>Title: 病人临床路径变更 </p>
 *
 * <p>Description: 不转科操作变更临床路径，同时修改交易表路径代码和时程</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author pangben 20150810
 * 此类作废，pangben 20160922 
 * @version 1.0
 */
public class CLPManagemChangeNewControl extends TControl {
    public CLPManagemChangeNewControl() {

    }
    TTable table;
    TextFormatCLPDuration combo_schd;
    /**
     * 页面初始化方法
     */
    public void onInit() {
        super.onInit();
        initPage();
    }

    TParm sendParm;
    /**
     * 初始化页面
     */
    private void initPage() {
        sendParm = (TParm)this.getParameter();
        String oldCLNPathCode = (String) sendParm.getData("OLD_CLNCPATH_CODE");
        String sql="SELECT CLNCPATH_CODE,EVL_CODE FROM CLP_MANAGEM WHERE CASE_NO='"+sendParm.getValue("CASE_NO")+"' AND  CLNCPATH_CODE='"+oldCLNPathCode+"'";
        TParm result=new TParm(TJDODBTool.getInstance().select(
                sql.toString()));

        this.setValue("OLD_CLNCPATH_CODE", oldCLNPathCode);
        this.setValue("OLD_EVL_CODE", result.getValue("EVL_CODE",0));
        this.setValue("CLNCPATH_CODE", "");
        this.setValue("EVL_CODE", "");
        
       	//TParm deptParm=sendParm.getParm("deptParm");//转入科室数据，查询路径代码
       	//根据科室、身份、诊断查询路径代码
       	//
        table= (TTable) this.getComponent("TABLE");
        onClncpathCode();
    }

    /**
     * 清空方法
     */
    public void onClear() {
//        CLNCPATH_CODE
        this.setValue("CLNCPATH_CODE", "");
        this.setValue("CHANGE_REASON", "");
        TParm parm = table.getParmValue();
		if (parm.getCount() > 0) {
			for (int i = 0; i < parm.getCount(); i++) {
				parm.setData("NEW_CLNCPATH_CODE", i, "");
				parm.setData("NEW_SCHD_CODE", i, "");
			}
			table.setParmValue(parm);
		}
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!validData()) {
            return;
        }
        //SYSTEM.OUT.PRINTln("接收的参数:"+sendParm);
        TParm parmForInsert = new TParm(sendParm.getData());
        parmForInsert.setData("CLNCPATH_CODE", this.getValueString("CLNCPATH_CODE"));
        parmForInsert.setData("CHANGE_REASON", this.getValueString("CHANGE_REASON"));
        parmForInsert.setData("IN_DATE",this.getCurrentDateStr());
        //新加入路径后自动进入该路径
        parmForInsert.setData("START_DTTM",getCurrentDateStr("yyyyMMddHHmmss"));
        parmForInsert.setData("EVL_CODE",this.getValue("EVL_CODE"));//评估代码
        parmForInsert.setData("SCHD_CODE",this.getValue("SCHD_CODE_SUM"));//当前时程
        //放入基本信息
        putBasicSysInfoIntoParm(parmForInsert);
        TParm parmForUpadate = new TParm();
        parmForUpadate.setData("CASE_NO",parmForInsert.getValue("CASE_NO"));
        parmForUpadate.setData("CLNCPATH_CODE",parmForInsert.getValue("OLD_CLNCPATH_CODE"));
        parmForUpadate.setData("CHANGE_REASON",parmForInsert.getValue("CHANGE_REASON"));
        TParm actionParm = new TParm();
        actionParm.setData("parmForInsert",parmForInsert.getData());
        actionParm.setData("parmForUpadate",parmForUpadate.getData());
        if (null!=table && null!=table.getParmValue()) {
        	 TParm billParm =table.getParmValue();
        	 if (billParm.getCount()>0) {
             	actionParm.setData("billParm",billParm.getData());
     		}
		}
        //SYSTEM.OUT.PRINTln("开始执行临床路径变更保存方法");
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagemAction",
                "changeCLPManagem", actionParm);
        if(result.getErrCode()<0){
            this.messageBox("变更失败");
             this.setReturnValue("FAILURE");
        }else{
            this.messageBox("变更成功");
            this.setReturnValue("SUCCESS");
            this.closeWindow();
        }

    }

    /**
     * 参数验证方法
     * @return boolean
     */
    private boolean validData() {
        if (!checkNullAndEmpty(this.getValueString("CLNCPATH_CODE"))) {
            this.messageBox("请选择新路径");
            return false;
        }
        if (!checkNullAndEmpty(this.getValueString("EVL_CODE"))) {
            this.messageBox("请选择评估代码");
            return false;
        }
        //不变更路径，只转时程不需要校验变更原因
        if (!this.getValue("OLD_CLNCPATH_CODE").equals(this.getValue("CLNCPATH_CODE"))) {
        	 if (!checkNullAndEmpty(this.getValueString("CHANGE_REASON"))) {
                 this.messageBox("请输入变更原因");
                 return false;
             }
		}
        if (null!=table && null!=table.getParmValue() && table.getParmValue().getCount()>0) {
        	for (int i = 0; i < table.getParmValue().getCount(); i++) {
    			if (null==table.getParmValue().getValue("NEW_SCHD_CODE",i)||table.getParmValue().getValue("NEW_SCHD_CODE",i).length()<=0) {
    				this.messageBox("新路径时程不可以为空值");
    				return false;
    			}
    			if (null==table.getParmValue().getValue("NEW_CLNCPATH_CODE",i)||table.getParmValue().getValue("NEW_CLNCPATH_CODE",i).length()<=0) {
    				this.messageBox("新路径不可以为空值");
    				return false;
    			}
    		}
		}
        return true;
    }

    /**
     * 检查是否为空或空串
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * 向TParm中加入系统默认信息
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        //SYSTEM.OUT.PRINTln("total" + total);
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM",  Operator.getIP());
    }

    /**
     * 得到当前时间字符串方法
     * @param dataFormatStr String
     * @return String
     */
    private String getCurrentDateStr(String dataFormatStr) {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, dataFormatStr);
        return datestr;
    }

    /**
     * 得到当前时间字符串方法
     * @return String
     */
    private String getCurrentDateStr() {
        return getCurrentDateStr("yyyyMMdd");
    }

	public void onSelect() {
		int row=table.getSelectedRow();
		TParm parm=table.getParmValue();
		String cln = this.getValueString("CLNCPATH_CODE");
		if (row<0) {
			return;
		}
		String schdCode=this.getValueString("SCHD_CODE_SUM");
		if (null != cln && cln.length() > 0) {
			parm.setData("NEW_CLNCPATH_CODE", row, cln);
			
		}else{
			parm.setData("NEW_CLNCPATH_CODE", row, "");
		}
		if (null != schdCode && schdCode.length() > 0) {
			parm.setData("NEW_SCHD_CODE", row,schdCode);
		}else{
			parm.setData("NEW_SCHD_CODE", row, "");
		}
		table.setParmValue(parm);
	}
	public void onClncpathCode(){
		TextFormatCLPDuration combo_schd = (TextFormatCLPDuration) this.getComponent("SCHD_CODE_SUM");
		combo_schd.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
        combo_schd.onQuery();
        String sql="SELECT SCHD_CODE FROM CLP_THRPYSCHDM WHERE CLNCPATH_CODE='"+
        this.getValueString("CLNCPATH_CODE")+"' ORDER BY SEQ";
        TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        TextFormatCLPEvlStandm combo_evl=  (TextFormatCLPEvlStandm)this.getComponent("EVL_CODE");
        combo_evl.setClncpathCode(this.getValueString("CLNCPATH_CODE"));
        combo_evl.onQuery();
        if (result.getCount()>0) {
        	this.setValue("SCHD_CODE_SUM", result.getValue("SCHD_CODE",0));
		}
        sql="SELECT EVL_CODE FROM CLP_EVL_STANDM WHERE CLNCPATH_CODE = '" + 
        this.getValueString("CLNCPATH_CODE") + "' ORDER BY EVL_CODE,SEQ ";
        result=new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getCount()>0) {
       	    this.setValue("EVL_CODE", result.getValue("EVL_CODE",0));
		}
        if (null!=this.getValueString("CLNCPATH_CODE")
        		&&!this.getValueString("OLD_CLNCPATH_CODE").equals(this.getValueString("CLNCPATH_CODE"))) {
        	 //查询此病患是否已经进入时程的医嘱费用，把已经进入的医嘱更换路径和时程代码
            sql="SELECT CLNCPATH_CODE,SCHD_CODE,'' NEW_CLNCPATH_CODE,'' NEW_SCHD_CODE,'Y' FLG  FROM IBS_ORDD WHERE CASE_NO='"
            	+sendParm.getValue("CASE_NO")+"' AND CLNCPATH_CODE IS NOT NULL GROUP BY CLNCPATH_CODE,SCHD_CODE";
            TParm oldClnParm = new TParm(TJDODBTool.getInstance().select(
                    sql.toString()));
            if (oldClnParm.getCount()>0) {
            	table.setParmValue(oldClnParm);
    		}
		}else{
			table.setParmValue(new TParm());
		}
	}
}
