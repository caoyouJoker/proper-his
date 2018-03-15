package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.Date;

import jdo.reg.REGQueMethodTool;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * <p>Title: 医师增加诊号</p>
 *
 * <p>Description:医师增加诊号 </p>
 *
 * <p>Copyright: ProperSoft </p>
 *
 * <p>Company:javahis </p>
 *
 * @author huangtt 20160601
 * @version 1.0
 */

public class REGQueUnfoldControl extends TControl{
	private static TTable table;
	private static boolean vip_flg;
	
	/**
     * 初始化
     */
    public void onInit() {
    	//初始化REGION登陆默认登录区域
        callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE"))); 
        setValue("ADM_DATE", SystemTool.getInstance().getDate());
        callFunction("UI|DEPT_CODE|onQuery");
        table = (TTable) getComponent("TABLE");        
        
        callFunction("UI|TABLE|addEventListener",
                "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        
        onQuery();
       
        
    }
    
    public void onQuery(){
    	String regionCode = this.getValueString("REGION_CODE");
    	String admDate = this.getValueString("ADM_DATE").replace("-", "").replace("/","").substring(0,8);
    	String sql="SELECT A.REGION_CODE, A.ADM_TYPE, A.ADM_DATE, A.SESSION_CODE, A.CLINICROOM_NO," +
    			" A.DEPT_CODE, A.DR_CODE, A.CLINICTYPE_CODE, A.MAX_QUE, A.VIP_FLG, A.OPT_USER," +
    			" A.OPT_DATE, A.OPT_TERM,A.QUE_NO" +
    			" FROM REG_SCHDAY A" +
    			" WHERE " +
    			" A.REGION_CODE = '"+regionCode+"' AND A.ADM_DATE = '"+admDate+"'";
    	if (!getValueString("ADM_TYPE").equals("")) {
			sql += " AND A.ADM_TYPE = '" + getValueString("ADM_TYPE") + "'";
		}
		if (!getValueString("SESSION_CODE").equals("")) {
			sql += " AND A.SESSION_CODE = '" + getValueString("SESSION_CODE") + "'";
		}
		if (!getValueString("CLINICROOM_NO").equals("")) {
			sql += " AND A.CLINICROOM_NO = '" + getValueString("CLINICROOM_NO") + "'";
		}
		if (!getValueString("DEPT_CODE").equals("")) {
			sql += " AND A.DEPT_CODE = '" + getValueString("DEPT_CODE") + "'";
		}
		if (!getValueString("DR_CODE").equals("")) {
			sql += " AND A.DR_CODE = '" + getValueString("DR_CODE") + "'";
		}
//		System.out.println("sql==="+sql);
    	TParm tbl = new TParm(TJDODBTool.getInstance().select(sql));
    	table.setParmValue(tbl);
    	
    }
    
    /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row){
    	if (row < 0)
            return;
    	TParm tblParm = table.getParmValue();
    	setValue("REGION_CODE",tblParm.getValue("REGION_CODE", row));
    	setValue("REGION_CODE",tblParm.getValue("REGION_CODE", row));
    	setValue("ADM_TYPE",tblParm.getData("ADM_TYPE", row));
    	setValue("SESSION_CODE",tblParm.getValue("SESSION_CODE", row));
    	setValue("CLINICROOM_NO",tblParm.getValue("CLINICROOM_NO", row));
    	setValue("DEPT_CODE",tblParm.getValue("DEPT_CODE", row));
    	setValue("DR_CODE",tblParm.getValue("DR_CODE", row));
    	setValue("MAX_QUE",tblParm.getValue("MAX_QUE", row));
    	setValue("ADD_QUE","1");
    	vip_flg=tblParm.getBoolean("VIP_FLG", row);
    	
    }
    
    public void onSave(){
    	TParm result = new TParm();
    	int addQue = getValueInt("ADD_QUE");
    	String regionCode = getValueString("REGION_CODE");
    	String admType=getValueString("ADM_TYPE");
    	String admDate=getValueString("ADM_DATE").substring(0, 10).replace("-", "");
    	String clinicroomNo=getValueString("CLINICROOM_NO");
    	String sessionCode = getValueString("SESSION_CODE");
    	String deptCode = getValueString("DEPT_CODE");
    	String drCode = getValueString("DR_CODE");
    	if(vip_flg){
    		String sql="SELECT * FROM REG_CLINICQUE" +
    				" WHERE ADM_TYPE = '"+admType+"'" +
    				" AND ADM_DATE = '"+admDate+"'" +
    				" AND SESSION_CODE = '"+sessionCode+"'" +
    				" AND CLINICROOM_NO = '"+clinicroomNo+"'" +
    				" ORDER BY QUE_NO DESC";
    		TParm regP = new TParm(TJDODBTool.getInstance().select(sql));
    		int queNo = regP.getInt("QUE_NO", 0);
    		String startTime=regP.getValue("START_TIME", 0);
    		TParm parm = new TParm();
    		Timestamp date = StringTool.getTimestamp(new Date());
    		for(int i=0;i<addQue;i++){
    			queNo++;
    			startTime=REGQueMethodTool.getInstance().addTime(startTime, "5");
    			parm.addData("ADM_TYPE", admType);
    			parm.addData("ADM_DATE", admDate);
    			parm.addData("SESSION_CODE", sessionCode);
    			parm.addData("CLINICROOM_NO", clinicroomNo);
    			parm.addData("QUE_NO", queNo);
    			parm.addData("QUE_STATUS", "N");
    			parm.addData("VISIT_CODE","N");
    			parm.addData("APPT_CODE", "N");
    			parm.addData("REGMETHOD_CODE", "A");
    			parm.addData("START_TIME", startTime);
    			parm.addData("OPT_USER", Operator.getID());
    			parm.addData("OPT_DATE", date);
    			parm.addData("OPT_TERM", Operator.getIP());
    			parm.addData("ADD_FLG", "Y");

    		}
    		parm.setCount(parm.getCount("ADM_TYPE"));
    		result = TIOM_AppServer.executeAction("action.reg.REGAction",
					"addRegVipQue", parm);
    		if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				this.messageBox("保存失败");
				return;
			}
    		
    	}
    	
    	String updateSql="UPDATE REG_SCHDAY" +
    			" SET MAX_QUE = MAX_QUE+"+addQue+
    			" WHERE REGION_CODE = '"+regionCode+"'" +
    			" AND ADM_TYPE = '"+admType+"'" +
    			" AND ADM_DATE = '"+admDate+"'" +
    			" AND SESSION_CODE = '"+sessionCode+"'" +
    			" AND CLINICROOM_NO = '"+clinicroomNo+"'" +
    			" AND DR_CODE = '"+drCode+"'" +
    			" AND DEPT_CODE = '"+deptCode+"'";
    	result = new TParm(TJDODBTool.getInstance().update(updateSql));
    	if(result.getErrCode()<0){
			messageBox(result.getErrText());
			this.messageBox("保存失败");
			return;
		}
    	this.messageBox("保存成功");
//    	clearValue("MAX_QUE;ADD_QUE");
    	onQuery();
    	
    }
    
    public void onClear(){
    	table.removeRowAll();
		clearValue("REGION_CODE;ADM_TYPE;SESSION_CODE;CLINICROOM_NO;DEPT_CODE;DR_CODE;CLINICTYPE_CODE;MAX_QUE;ADD_QUE");
		 callFunction("UI|REGION_CODE|setValue", Operator.getRegion());
	        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
	        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
	                getValueString("REGION_CODE"))); 
	        setValue("ADM_DATE", SystemTool.getInstance().getDate());
	        callFunction("UI|DEPT_CODE|onQuery");
    }

}
