package com.javahis.ui.adm;

import java.awt.Color;
import java.sql.Timestamp;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.sys.Operator;
import jdo.sys.SYSBedTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.javahis.util.OdiUtil;

/**
 * 床位管理明细
 * 
 * @author Administrator
 * 
 */
public class ADMBedDetailControl extends TControl {
	TTable tableIn;
	TTable tableOut;
	TTable in;
	TParm admPat = null;
	int nullBed;
	int notNullBed;
	private String type ;
	private String  sexCode;
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		
		// 获取预约住院界面传入的参数
		Object obj = this.getParameter();
		TParm parm = null;
		if (obj instanceof TParm)
			parm = (TParm) obj;
		this.setValue("PRETREAT_DATE", parm.getValue("PRETREAT_DATE")
				.toString().substring(0, 10).replaceAll("-", "/")+" 23:59:59");
		this.setValue("DEPT_CODE", parm.getValue("DEPT_CODE"));
		this.setValue("STATION_CODE", parm.getValue("STATION_CODE"));
		sexCode=parm.getValue("SEX_CODE");
		type = parm.getValue("TYPE") ; 
		tableIn = (TTable) this.getComponent("TABLE_IN");
		tableOut = (TTable) this.getComponent("TABLE_OUT");
		in = (TTable) this.getComponent("in");
		onQuery();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String sysDate=SystemTool.getInstance().getDate().toString().substring(0,19).replaceAll("-", "/");
		String date = this.getValue("PRETREAT_DATE").toString()
				.substring(0, 19).replaceAll("-", "/");
		TParm inParam = new TParm();
		TParm outParm = new TParm();
		TParm parm = new TParm();
		if (this.getValue("PRETREAT_DATE") != null
				&& !"".equals(this.getValue("PRETREAT_DATE"))) {
			inParam.setData("START_DATE", sysDate );
			inParam.setData("END_DATE", date );
			outParm.setData("START_DATE", sysDate);
			outParm.setData("END_DATE", date);
		}

		if (this.getValue("STATION_CODE") != null
				&& !"".equals(this.getValue("STATION_CODE"))) {
			inParam.setData("PRETREAT_IN_STATION", this
					.getValue("STATION_CODE"));
			outParm.setData("PRETREAT_OUT_STATION", this
					.getValue("STATION_CODE"));
			
			parm.setData("STATION_CODE", this
					.getValue("STATION_CODE"));
		}
	/*	if (this.getValue("DEPT_CODE") != null
				&& !"".equals(this.getValue("DEPT_CODE"))) {
			inParam.setData("PRETREAT_IN_DEPT", this.getValue("DEPT_CODE"));
			outParm.setData("PRETREAT_OUT_DEPT", this.getValue("DEPT_CODE"));
		}*/
		//inParam.setData("EXEC_FLG","N");
		TParm result = ADMResvTool.getInstance().queryPretreat(inParam);
		for(int i=0;i<result.getCount();i++){
			result.setData("AGE",i,this.patAge(result.getTimestamp("BIRTH_DATE",i)));
		}
		tableIn.setParmValue(result);
		String sql="SELECT MR_NO  FROM ADM_WAIT_TRANS WHERE IN_STATION_CODE='"+this.getValueString("STATION_CODE")+"'";
		TParm countParm=new TParm(TJDODBTool.getInstance().select(sql));
		int inNum=(result.getCount()>0?result.getCount():0)+(countParm.getCount()>0?countParm.getCount():0);
		
		this.setValue("PRE_IN_NUM", inNum+"");
		
		result = ADMResvTool.getInstance().queryPretreat(outParm);
		for(int i=0;i<result.getCount();i++){
			result.setData("AGE",i,this.patAge(result.getTimestamp("BIRTH_DATE",i)));
		}
		tableOut.setParmValue(result);
		this.setValue("PRE_OUT_NUM", result.getCount()+"");//待转出
		
		
		
		/*if(type.equals("RESV")){
       	 	parm.setData("DR_APPROVE_FLG", "Y");	
        }*/
			
		result = ADMInpTool.getInstance().queryInStation(parm);
		tableColor(result);
		nullBed=0;
		notNullBed=0;
		for(int i=0;i<result.getCount();i++){
			if("0".equals(result.getValue("BED_STATUS",i))){
				nullBed++;
			}else {
				notNullBed++;
			}
		}
		this.setValue("FILL_NUM", notNullBed+"");//占床
		this.setValue("NULL_NUM", nullBed+"");//空床
	}
	/**
	 * 按照房间号 显示 白蓝色
	 * @param result
	 */
	public void tableColor(TParm result){
		in.setParmValue(result);
		in.setRowColor(0, Color.white);
		TParm tableParm=in.getParmValue();
		String romDesc=tableParm.getValue("ROOM_DESC",0);
		for(int i=1;i<result.getCount();i++){//按照房间号 间隔显示 白色 与蓝色
			if(tableParm.getValue("ROOM_DESC",i).equals(romDesc)){
				in.setRowColor(i, in.getRowColor(i-1));//房间号相同，则取上一行的颜色
			}else{
				if(in.getRowColor(i-1)==Color.white)//房间号 不同，则先判断上一行 的颜色是什么，若为白色，则下一行为蓝色
					in.setRowColor(i, Color.lightGray);
				else //若为蓝色，则下一行为白色
					in.setRowColor(i, Color.white);
				romDesc=tableParm.getValue("ROOM_DESC",i);
			}
		}
	}
	/**
	 *清空
	 */
	public void onClear() {
		this.clearValue("PRETREAT_DATE;DEPT_CODE;STATION_CODE");
		in.removeRowAll();
		tableIn.removeRowAll();
		tableOut.removeRowAll();
		this.setValue("PRE_IN_NUM", "");
		this.setValue("PRE_OUT_NUM", "");//待转出
		this.setValue("FILL_NUM", "");//占床
		this.setValue("NULL_NUM", "");//空床
	}

	/**
	 * 刷新
	 */
	public void onRefresh() {

		onInit();
	}

	/**
	 * 关闭
	 */
	public void closeButton() {
		this.closeWindow();
	}

	/**
	 * 计算年龄
	 * 
	 * @param date
	 * @return
	 */
	private String patAge(Timestamp date) {
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp = date == null ? sysDate : date;
		String age = "0";
		age = OdiUtil.showAge(temp, sysDate);
		return age;
	}
	/**
	 * 传回床位
	 */
	public void onBedTable(){
		if (!check()) {//审核是否被占用
            return;
        }
		TTable table = (TTable)this.callFunction("UI|in|getThis");

        TParm parm = table.getParmValue();

        TParm queryParm = new TParm();
        //病床CODE
        queryParm.setData("BED_NO",
                          parm.getValue("BED_NO", table.getSelectedRow()));
        TParm sendParm = SYSBedTool.getInstance().queryBedYellowRed(queryParm);
        this.setReturnValue(sendParm);
        this.closeWindow();
	}
	
	/**
     * 检核床位
     * @return boolean
     */
    public boolean check() {
        TTable table = (TTable)this.callFunction("UI|in|getThis");
        if (table.getSelectedRow() < 0) {
            this.messageBox("未选床位");
            return false;
        }
        
        
        TParm  parm=table.getParmValue().getRow(table.getSelectedRow());
        
        if(!"0".equals(parm.getValue("BED_STATUS"))){
        	this.messageBox("此床已占用，不可预约");
        	return false;
        }
        if("Y".equals(parm.getValue("APPT_FLG"))){
        	this.messageBox("该床已经被预约");
        	return false;
        }
        
		String sql = "SELECT B.SEX_CODE FROM SYS_BED A,SYS_PATINFO B "
				+ " WHERE ROOM_CODE='" + parm.getValue("ROOM_CODE") + "' "
				+ " AND A.MR_NO=B.MR_NO";
		//System.out.println("" + sql);
		TParm data = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < data.getCount(); i++) {
			if (!sexCode.equals(data.getValue("SEX_CODE", i))) {
				if(JOptionPane.showConfirmDialog(null, "性别不相同，是否继续？", "信息",
	    				JOptionPane.YES_NO_OPTION) == 0){
					return true;
				}else{
					return false;
				}
			}
		}
        
        return true;
       /* TParm  inParm=new TParm();
        inParm.setData("BED_NO", parm.getValue("BED_NO"));
        TParm result = ADMInpTool.getInstance().QueryBed(inParm);
        String APPT_FLG=result.getCount()>0?result.getValue("APPT_FLG",0):"";
        String ALLO_FLG=result.getCount()>0?result.getValue("ALLO_FLG",0):"";
        String BED_STATUS=result.getCount()>0?result.getValue("BED_STATUS",0):"";
        if(type.equals("RESV") ){
        	 if (APPT_FLG.equals("Y")) {
        		 this.messageBox("此床已预约！不可再次预约");
                 return false; 
        	 }
        	 if((result.getValue("MEDDISCH_DATE",0).toString().equals("") || result.getValue("MEDDISCH_DATE",0)==null) 
        			 && (ALLO_FLG.equals("Y"))){
        		 this.messageBox("此病患没有预约出院时间，此床不可预约");
                 return false;
        	 }
        }
        else {
        if (ALLO_FLG.equals("Y")) {
            this.messageBox("此床已占用");
            return false;
        }
        if ("Y".equals(result.getValue("BED_OCCU_FLG",0))||BED_STATUS.equals("1")) {
            this.messageBox("此床已被包床");
            return false;
        }
        if (APPT_FLG.equals("Y")) {
        	int check = this.messageBox("消息", "此床已被预订，是否进继续？", 0);
			if (check != 0) {
				return  false;
			}
            return true;
        }
        }
        
        
        
        	
        return true;*/
    }
    
    /**
     * 空床
     */
	public void onAllo() {
		
		TCheckBox checkbox = (TCheckBox) this.callFunction("UI|ALLO|getThis");
		if (checkbox.isSelected()) {
			TParm parm = new TParm();
			parm.setData("STATION_CODE", Operator.getStation());
			if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
				parm.setData("REGION_CODE", Operator.getRegion());
			parm.setData("ALLO_FLG", "N");
			if (type.equals("RESV")) {
				parm.setData("DR_APPROVE_FLG", "Y");
			}
			TParm result = ADMInpTool.getInstance().queryInStation(parm);
			TTable table = (TTable) this.callFunction("UI|in|getThis");
			table.setParmValue(result);
			tableColor(result);
		} else {
			onSelectBed();
		}
	}
    
    /**
     * 床位检索
     */
    public void onSelectBed() {
        //TCheckBox checkbox = (TCheckBox)this.callFunction("UI|ALLO|getThis");
        TParm parm = new TParm();
        
        if(type.equals("RESV")){
       	 	parm.setData("DR_APPROVE_FLG", "Y");	
        }
        parm.setData("STATION_CODE", Operator.getStation());
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
     
       /* parm.setDataN("ROOM_CODE", this.getValue("ROOM_CODE"));
        parm.setDataN("BED_CLASS_CODE", this.getValue("BED_CLASS_CODE"));*/
        TParm result = ADMInpTool.getInstance().queryInStation(parm);
        TTable table = (TTable)this.callFunction("UI|In|getThis");
        table.setParmValue(result);
        tableColor(result);
    }
}
