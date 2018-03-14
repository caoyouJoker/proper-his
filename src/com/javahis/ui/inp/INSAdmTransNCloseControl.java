package com.javahis.ui.inp;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.adm.ADMInpTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
/**
 * 
 * <p>
 * Title:转外就医登记下载和开立
 * </p>
 * 
 * <p>
 * Description:转外就医登记下载和开立:住院未结案
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author pangb 2011-11-25
 * @version 2.0
 */
public class INSAdmTransNCloseControl  extends TControl{
	private int selectrow = -1;//选择的行
	SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd");
	String flg="";
	public void onInit() {
		super.onInit();
		//得到前台传来的数据并显示在界面上
		TParm recptype = (TParm) getParameter();
		setValueForParm("REGION_CODE;DEPT_CODE", recptype, -1);
		//================pangben 2012-6-18 start 
		if (null!=recptype.getValue("MR_NO") && recptype.getValue("MR_NO").length()>0) {
			this.setValue("MR_NO", recptype.getValue("MR_NO"));
		}
		flg=recptype.getValue("FLG");//显示数据 SQL 修改数据
		if (null!=flg && flg.equals("Y")) {
			this.setTitle("出院病患信息查询");
		}
		   //table1的单击侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //table1的单击侦听事件
        callFunction("UI|TABLE|addEventListener",
                     "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                     "onTableDoubleClicked");
        //预设就诊时间段
        //DateFormat df = new SimpleDateFormat("yyyy");
       // String date=df.format(SystemTool.getInstance().getDate())+"-01-01";
        this.callFunction("UI|STARTTIME|setValue",
        		 SystemTool.getInstance().getDate());
        this.callFunction("UI|ENDTIME|setValue",
                          SystemTool.getInstance().getDate());
//        onQuery();
	}
	  /**
     *增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        //接收所有事件
        this.callFunction("UI|TABLE|acceptText");
//   TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        selectrow = row;
    }

    public void onTableDoubleClicked(int row) {
        TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
        this.setReturnValue(data.getRow(row));
        this.callFunction("UI|onClose");
    }
	/**
	 * 查询方法
	 */
	public void onQuery(){
		TParm parm=new TParm();
		//区域
		if(this.getValue("REGION_CODE").toString().length()>0){
			parm.setData("REGION_CODE",this.getValue("REGION_CODE"));	
		}
		//病案号码
		if(this.getValueString("MR_NO").length()>0){
			parm.setData("MR_NO",this.getValue("MR_NO"));	
		}
		//科室
		if(this.getValueString("DEPT_CODE").length()>0){
			parm.setData("DEPT_CODE",this.getValue("DEPT_CODE"));	
		}
		//病区
		if (this.getValueString("STATION_CODE").length()>0) {
			parm.setData("STATION_CODE",this.getValue("STATION_CODE"));	
		}
		//开始时间
		if(null!=this.getValue("STARTTIME")){
			parm.setData("STARTTIME",df1.format(getValue("STARTTIME"))+"000000");	
		}
		//结束时间
		if(null!=this.getValue("ENDTIME")){
			parm.setData("ENDTIME",df1.format(getValue("ENDTIME"))+"235959");	
		}
		TParm result=null;
		//=========pangben 2012-6-18 start 费用分割查询病患信息确定唯一数据
//		if (null!=flg && flg.equals("Y")) {
//			result=ADMInpTool.getInstance().queryAdmNCloseInsBalance(parm);
//		}else{
//			result=ADMInpTool.getInstance().queryAdmNClose(parm);
//		}
		result=this.queryAdmNCloseInsBalance(parm);
		//=========pangben 2012-6-18 stop
		if(result.getErrCode()<0 ){
			this.messageBox("E0005");
			err(result.getErrText()+":"+result.getErrName());
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("E0008");
			TTable table = (TTable) this.callFunction("UI|TABLE|getThis");
			table.removeRowAll();
			return;
		}
		this.callFunction("UI|TABLE|setParmValue", result);
	}
	/**
     * 病案号文本框回车事件
     */
    public void onMrNo() {
//		TParm parm = getTableSeleted();
//		if (null == parm) {
//			return;
//		}
        Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
        if (pat == null) {
            this.messageBox("无此病案号!");
            return;
        }
       // this.setValue("PAT_NAME", pat.getName());
        this.setValue("MR_NO", pat.getMrNo());
        //TParm result = INSIbsTool.getInstance().queryIbsSum(parm);// 查询数据给界面赋值
        //setSumValue(result, parm);
    }
    /**
    *
    */
   public void onOK() {
       TParm data = (TParm) callFunction("UI|TABLE|getParmValue");
       this.setReturnValue(data.getRow(selectrow));
       this.callFunction("UI|onClose");
   }
	private TParm queryAdmNCloseInsBalance(TParm parm) {
//		System.out.println("onQuery==="+parm);
		String Sql = 
			" SELECT A.REGION_CODE,A.MR_NO,B.PAT_NAME,B.IDNO,A.IN_DATE,A.CASE_NO,A.DEPT_CODE,B.SEX_CODE,B.PAT_AGE,B.PERSONAL_NO,C.TEL_HOME,B.CONFIRM_NO,B.INS_UNIT,B.IN_STATUS "+
			" FROM ADM_INP A,INS_ADM_CONFIRM B,SYS_PATINFO C "+
			" WHERE A.REGION_CODE='"+parm.getValue("REGION_CODE")+"' "+
		    " AND A.IN_DATE BETWEEN TO_DATE('"+parm.getValue("STARTTIME")+"','YYYYMMDDHH24MISS') AND TO_DATE('"+parm.getValue("ENDTIME")+"','YYYYMMDDHH24MISS') ";
		    if(!StringUtil.isNullString(parm.getValue("MR_NO"))){
				Sql =Sql +" AND A.MR_NO='"+parm.getValue("MR_NO")+"' ";
			}
		    if(!StringUtil.isNullString(parm.getValue("STATION_CODE"))){
				Sql =Sql +" AND A.STATION_CODE='"+parm.getValue("STATION_CODE")+"' ";
			}
		    if(!StringUtil.isNullString(parm.getValue("DEPT_CODE"))){
				Sql =Sql +" AND A.DEPT_CODE='"+parm.getValue("DEPT_CODE")+"' ";
			}
			Sql =Sql +
			" AND A.CANCEL_FLG='N' "+
			" AND B.CONFIRM_NO=A.CONFIRM_NO "+
			" AND B.IN_STATUS IN ('0','7','1','2','4') "+
			" AND C.MR_NO=A.MR_NO";

//			parm.setData("REGION_CODE",this.getValue("REGION_CODE"));	
//		//病案号码
//			parm.setData("MR_NO",this.getValue("MR_NO"));	
//		//科室
//			parm.setData("DEPT_CODE",this.getValue("DEPT_CODE"));	
//		//病区
//			parm.setData("STATION_CODE",this.getValue("STATION_CODE"));	
//		//开始时间
//			parm.setData("START_DATE",df1.format(getValue("STARTTIME"))+"000000");	
//		//结束时间
//			parm.setData("END_DATE",df1.format(getValue("ENDTIME"))+"235959");	
//		System.out.println("onQuery==="+Sql);
		TParm tabParm = new TParm(TJDODBTool.getInstance().select(Sql));

		if (tabParm.getCount("MR_NO") < 0) {
			this.messageBox("没有查询到相应记录");
			return null;
		}
		return tabParm;
	}
}
