package com.javahis.ui.onw;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import jdo.ekt.EKTIO;
import jdo.erd.ERDLevelTool;
import jdo.onw.ONWTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.reg.SchDayTool;
import jdo.reg.SessionTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.OdoUtil;
import com.javahis.util.SelectResult;

/**
 * <p>Title: 护士分诊界面</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis </p>
 *
 * @author JiaoY 2008.09.28
 * @version 1.0
 */
public class ONWRegNurseStationControl
    extends TControl {
    TParm dataM, comboldata, dataD, selData;
    int selectRowL = -1;
    int selectRowR = -1;
    private String admType = "O";
    private TParm eventParmEmr;//结构化病历返回的方法列表
    public void onInit() {
        super.onInit();
        admType = (String)this.getParameter(); //从界面接参 “O”门诊 “E”急诊
        if (admType == null || "".equals(admType)) {
            this.messageBox("未设置门急参数");
        }
        callFunction("UI|LTABLE|addEventListener",
                     "LTABLE->" + TTableEvent.CLICKED, this, "onLTABLEClicked");
        callFunction("UI|RTABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this, "onClickBox");
        addEventListener("RTABLE->" + TTableEvent.CHANGE_VALUE,
                         "onChangeValue");
        this.setUI();
        this.onQuery();
    }

    /**
     * UI初始化
     */
    public void setUI() {
        this.callFunction("UI|detach|setEnabled", false); //分诊
        this.callFunction("UI|patdata|setEnabled", false); //病患信息
        this.callFunction("UI|erdLevel|setEnabled", false); //病患信息
        callFunction("UI|setTitle", "O".equals(admType) ? "门诊护士站" : "急诊护士站");
		if ("O".equals(admType)) {
			this.callFunction("UI|erd|setVisible", false);
			this.callFunction("UI|LEVEL_FLG|setVisible", false);
			this.callFunction("UI|IN_HOSP|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
        }else{
			this.callFunction("UI|erd|setVisible", true);
			this.callFunction("UI|LEVEL_FLG|setVisible", true);
			this.callFunction("UI|IN_HOSP|setVisible", true);
			this.callFunction("UI|create|setVisible", true);
			this.callFunction("UI|transfer|setVisible", true);
        }
        setValue("REGION_CODE", Operator.getRegion());
        setValue("ADM_TYPE", admType);
        //初始化日期
        Timestamp time = SystemTool.getInstance().getDate();
        setValue("ADM_DATE", time);
        //初始化时段Combo,取得默认时段
        String defSession = SessionTool.getInstance().getDefSessionNow(admType,Operator.getRegion());
        setValue("SESSION_CODE", defSession);
        //初始化得到默认诊区
        setValue("CLINICAREA_CODE", Operator.getStation());
        callFunction("UI|SESSION_CODE|setAdmType", admType);
        callFunction("UI|SESSION_CODE|onQuery");
        callFunction("UI|CLINICTYPE_CODE|onQuery");
        callFunction("UI|DEPT_CODE|onQuery");
        callFunction("UI|DR_CODE|onQuery");
    }

    /**
     * 医技进度
     */
    public void onDocplan() {
        this.openDialog("%ROOT%\\config\\onw\\ONWMEDProgress.x");
    }

    /**
     * 分诊
     */
    public void onDetach() {
        String date = StringTool.getString(TCM_Transform.getTimestamp(getValue(
            "ADM_DATE")), "yyyyMMdd"); //拿到界面的时间
        String now = StringTool.getString(SystemTool.getInstance().getDate(),
                                          "yyyyMMdd"); //拿到当前的时间
        if (!date.equals(now)) {
            this.messageBox("不可隔日分诊");
            return;
        }
        TTable RTABLE = (TTable) callFunction("UI|RTABLE|getThis"); //拿到table控件
        RTABLE.acceptText();
        int count = RTABLE.getRowCount(); //等待的病人数量
        TParm patInfo = new TParm();
        int t = 0;
        for (int i = 0; i < count; i++) {
            if (!"Y".equals(RTABLE.getValueAt(i, 0)))//判断分诊标记是否选中
                continue;
            patInfo.setRowData(dataD.getRow(i));
            patInfo.setData("CLINICTYPE_CODE",
                            dataM.getData("CLINICTYPE_CODE", selectRowL)); //号别
            patInfo.setData("ADM_TYPE", admType); //门急别
            TParm reParm = (TParm)this.openDialog(
                "%ROOT%\\config\\onw\\ONWAssign.x", patInfo);
        }
        TParm selData = new TParm();
        selData.setData("ADM_TYPE", dataM.getData("ADM_TYPE", selectRowL));
        selData.setData("ADM_DATE", dataM.getData("ADM_DATE", selectRowL));
        selData.setData("REALDEPT_CODE",
                        dataM.getData("DEPT_CODE", selectRowL));
        selData.setData("REALDR_CODE", dataM.getData("DR_CODE", selectRowL));
        selData.setData("SESSION_CODE",
                        dataM.getData("SESSION_CODE", selectRowL));
        selData.setData("CLINICROOM_NO",
                        dataM.getData("CLINICROOM_NO", selectRowL));
        this.onSelPat(selData);
    }

    /**
     * 报告进度
     */
    public void onPlanrep() {
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("MR_NO",dataD.getValue("MR_NO",selectedRow));
        parm.setData("CASE_NO",dataD.getValue("CASE_NO",selectedRow));
        parm.setData("PAT_NAME",dataD.getValue("PAT_NAME",selectedRow));
        parm.setData("SEX_CODE",dataD.getValue("SEX_CODE",selectedRow));
        parm.setData("DEPT_CODE",dataD.getValue("REALDEPT_CODE",selectedRow));
        parm.setData("CLINICROOM_NO",dataD.getValue("CLINICROOM_NO",selectedRow));
        parm.setData("DR_CODE",dataD.getValue("REALDR_CODE",selectedRow));
        this.openDialog("%ROOT%\\config\\onw\\ONWPlanReport.x",parm);
    }

    /**
     * 病患资料
     */
    public void onPatdata() {
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        TParm patParm = table.getParmValue();
        String mrNo = patParm.getValue("MR_NO", table.getSelectedRow());
        TParm sendParm = new TParm();
        sendParm.setData("ONW", "ONW");
        sendParm.setData("MR_NO", mrNo);
        this.openWindow("%ROOT%\\config\\sys\\SYSPatInfo.x", sendParm);
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = getParmForTag(
            "ADM_DATE:timestamp;SESSION_CODE;DEPT_CODE;CLINICTYPE_CODE;DR_CODE;CLINICROOM_NO;ADM_TYPE;CLINICAREA_CODE", true);
        dataM = new TParm();
        //===========pangben modify 20110421 start
        String regionCode=Operator.getRegion();
        if(!"".equals(regionCode)&&null!=regionCode)
            parm.setData("REGION_CODE",regionCode);
        //===========pangben modify 20110421 stop
        dataM = SchDayTool.getInstance().selectdata(parm);
        this.callFunction("UI|LTABLE|setParmValue", dataM);
    }

    /**
     * 点击左边table查询医师的挂号信息
     * @param row int
     */
    public void onLTABLEClicked(int row) {
        if (row < 0) {
            return;
        }
        setValueForParm(
            "SESSION_CODE;DEPT_CODE;CLINICTYPE_CODE;CLINICROOM_NO;DR_CODE",
            dataM, row);
        selectRowL = row;
        TTable LTABLE = (TTable) callFunction("UI|LTABLE|getThis"); //拿到table控件
        selData = new TParm();
        selData.setData("ADM_TYPE", dataM.getData("ADM_TYPE", selectRowL));
        selData.setData("ADM_DATE", dataM.getData("ADM_DATE", selectRowL));
        selData.setDataN("REALDEPT_CODE", dataM.getData("DEPT_CODE", selectRowL));
        selData.setDataN("REALDR_CODE", dataM.getData("DR_CODE", selectRowL));
        selData.setData("SESSION_CODE",
                        dataM.getData("SESSION_CODE", selectRowL));
        selData.setData("CLINICROOM_NO",
                        dataM.getData("CLINICROOM_NO", selectRowL));
        this.onClinic();
        this.onSelPat(selData);
        this.clearValue("SELALL");//清空全选按钮
        this.callFunction("UI|detach|setEnabled", false); //分诊
        this.callFunction("UI|patdata|setEnabled", false); //病患信息
    }

    /**
     * 显示病患信息
     * @param parm TParm
     */
    public void onSelPat(TParm parm) {
    	if (null == parm || StringUtils.isEmpty(parm.getValue("ADM_DATE"))) {
    		return;
    	}
        dataD = new TParm();
        // modify by wangb 2016/1/25 查询已办理住院登记的病患
		if (this.getValueBoolean("IN_HOSP")) {
			if (this.getValueBoolean("LEVEL_FLG")) {
				parm.setData("LEVEL_FLG", "Y");
			} else {
				parm.setData("LEVEL_FLG", "N");
			}
			// 查询急诊护士站中已办理住院登记手续的病患数据
			dataD = ONWTool.getInstance().queryInHospData(parm);
		} else {
			if (this.getValueBoolean("LEVEL_FLG")) {
				dataD = PatAdmTool.getInstance().selectdata_erd(parm);
			} else {
				dataD = PatAdmTool.getInstance().selectdata_name(parm);
			}
		}
        
        int count = dataD.getCount("SESSION_CODE");
        Color red = new Color(255, 0, 0);
		Color pink =new Color(255,170,255);
		HashMap map = new HashMap();
		HashMap wmap = new HashMap();
        for (int i = 0; i < count; i++) {
            dataD.addData("SELECT", "N");
            //====zhangp 20120227 modify start
            if(StringUtils.isNotEmpty(dataD.getValue("REG_ADM_TIME", i))){
            	String admTime =
            		dataD.getData("REG_ADM_TIME", i).toString().substring(0,2)+":"+dataD.getData("REG_ADM_TIME", i).toString().substring(2,4);
            	dataD.setData("REG_ADM_TIME", i , admTime);
            }
            Object er = dataD.getValue("ENTER_ROUTE", i);
			Object pk = dataD.getValue("PATH_KIND",i);
			if(er !=null && !"E01".equals(er) && !"".equals(er)){
				map.put(i, pink);
				
			}
			if("P01".equals(pk)){
				wmap.put(i, red);
			}
            //======zhangp 20120227 modify end
        }
       
	    TTable table = (TTable) this.getComponent("RTABLE");
//		SelectResult sr = new SelectResult(dataD);
//		int cnt = sr.size();
//		for(int i=0;i<cnt;i++){
//			Object er = sr.getRowField(i,"ENTER_ROUTE");
//			Object pk = sr.getRowField(i,"PATH_KIND");
//			if(er !=null && !"E01".equals(er) && !"".equals(er)){
//				map.put(i, pink);
//				
//			}
//			if(pk !=null && !"P01".equals(pk) && !"".equals(pk)){
//				wmap.put(i, red);
//			}
//		}
        this.clearValue("RTABLE");
        this.callFunction("UI|RTABLE|setParmValue", dataD);
        
		if (map.size() > 0) {
			table.setRowColorMap(map);
			
		}
		if(wmap.size()>0){
			table.setRowTextColorMap(wmap);
		}
    }

    /**
     * 通过诊室查询诊区
     */
    public void onClinic() {
        TParm parm = new TParm();
        parm.setData("CLINICROOM_NO", getValue("CLINICROOM_NO").toString().trim());
        parm.setData("SESSION_CODE", getValue("SESSION_CODE").toString().trim());
        parm.setData("ADM_DATE", getValue("ADM_DATE"));
        TParm result = SchDayTool.getInstance().SELECT_REG_SCHDAY_CLINICROOM(
            parm);
        setValue("CLINICAREA_CODE", result.getValue("CLINICAREA_CODE", 0));
    }


    /**
     * 清空
     */
    public void onClear() {
        this.setValue("CLINICAREA_CODE", "");
        this.setValue("DEPT_CODE", "");
        this.setValue("CLINICTYPE_CODE", "");
        this.setValue("CLINICROOM_NO", "");
        this.setValue("DR_CODE", "");
        this.callFunction("UI|LTABLE|removeRowAll");
        this.callFunction("UI|RTABLE|removeRowAll");
        this.callFunction("UI|detach|setEnabled", false); //分诊
        this.callFunction("UI|patdata|setEnabled", false); //病患信息
        this.callFunction("UI|erdLevel|setEnabled", false); //病患信息
    }

    /**
     * 属性table改变值
     * @param obj Object
     * @return boolean
     */
    public boolean onChangeValue(Object obj) {
        TTable table = (TTable) callFunction("UI|RTABLE|getThis"); //拿到table控件
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
//        int selectRow = node.getRow();
//        String admStatus = table.getValueAt(selectRow, 5).toString();
//        if (!admStatus.equals("1")) {
//            this.messageBox("病患 " + table.getValueAt(selectRow, 3) + " 不可分诊");
//            return true;
//        }
        this.callFunction("UI|detach|setEnabled", true);
        this.callFunction("UI|patdata|setEnabled", true); //病患信息
        //判断表格中是否还有其他的选中行
        boolean flg = false;
        if("N".equals(node.getValue().toString())){
            for(int i=0;i<table.getRowCount();i++){
                if("Y".equals(table.getItemString(i,0))&&i!=node.getRow()){
                    flg = true;
                }
            }
            //如果没有其他的选中行 那么分诊按钮 设置为不可编辑
            if (!flg) {
                this.callFunction("UI|detach|setEnabled", false);
            }
        }
        this.clearValue("SELALL");//清空全选按钮
        return false;
    }

    /**
     * table 中 checkbox 事件
     * @param object Object
     */
    public void onClickBox(Object object) {
        TTable obj = (TTable) object;
        obj.acceptText();
    }

    /**
     * 病患table 选中  事件
     */
    public void onPatInfo() {
    	this.callFunction("UI|patdata|setEnabled", true);
        this.callFunction("UI|erdLevel|setEnabled", true);
    }

    /**
     * 全选
     */
    public void onSelectAll() {
        TTable table = (TTable) callFunction("UI|RTABLE|getThis"); //拿到table控件
//        Boolean check = false;
        int count = table.getRowCount();
        for (int i = 0; i < count; i++) {
            if (table.getValueAt(i, 5).equals("1")) {
                table.setValueAt(getValue("SELALL"), i, 0);
//                check = true;
            }
        }
        if ("Y".equals(this.getValueString("SELALL"))){
            this.callFunction("UI|detach|setEnabled", true);
            this.callFunction("UI|patdata|setEnabled", false);
        }
        else{
            this.callFunction("UI|detach|setEnabled", false);
        }
    }
    /**
     * 时段Combo选择事件 清空以时段为条件查询的Combo
     */
    public void onSESSION_CODE(){
        //清空科别，诊室，看诊医师 三个combo
        this.clearValue("DEPT_CODE;CLINICROOM_NO;DR_CODE");
    }
    /**
     * 清空医师combo
     */
    public void clearDr_CODE(){
        this.clearValue("DR_CODE");
    }
    /**
     * 日期改变事件
     */
    public void onADM_DATE_Selected(){
        this.clearValue("SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO");
        //初始化时段Combo,取得默认时段
        String defSession = SessionTool.getInstance().getDefSessionNow(admType,Operator.getRegion());
        setValue("SESSION_CODE", defSession);
        //初始化得到默认诊区
        setValue("CLINICAREA_CODE", Operator.getStation());
        callFunction("UI|SESSION_CODE|setAdmType", admType);
        callFunction("UI|SESSION_CODE|onQuery");
        callFunction("UI|CLINICTYPE_CODE|onQuery");
        callFunction("UI|DEPT_CODE|onQuery");
    }
    /**
     * 条码打印
     */
    public void onBarcode(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        if(dataD.getInt("ADM_STATUS",selectedRow)<=1){
            this.messageBox_("该病患尚未看诊，不可打印！");
            return;
        }
        //参数
        TParm parm = new TParm();
        parm.setData("DEPT_CODE", dataD.getValue("REALDEPT_CODE", selectedRow)); //科室
        parm.setData("ADM_TYPE", admType); //门急住别
        parm.setData("CASE_NO", dataD.getValue("CASE_NO", selectedRow)); //CASE_NO
        parm.setData("MR_NO", dataD.getValue("MR_NO", selectedRow)); //MR_NO
        parm.setData("PAT_NAME", dataD.getValue("PAT_NAME", selectedRow)); //病患姓名
        parm.setData("ADM_DATE", dataD.getTimestamp("ADM_DATE", selectedRow)); //就诊日期
        parm.setData("CLINICAREA_CODE",
                     dataD.getValue("CLINICAREA_CODE", selectedRow)); //诊区
        parm.setData("CLINICROOM_NO",
                     dataD.getValue("CLINICROOM_NO", selectedRow)); //诊室
        parm.setData("POPEDEM", "1"); //一般权限
        this.openDialog("%ROOT%\\config\\med\\MEDApply.x", parm);
    }
    /**
     * 体征采集  调用机构化病历
     */
    public void onBody(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int row = table.getSelectedRow();
        if(row<0){
            this.messageBox_("请选择病患！");
            return;
        }
        TParm parm = new TParm();
        TParm emrParm = new TParm();
        String caseNo = dataD.getValue("CASE_NO",row);
        emrParm.setData("MR_CODE", TConfig.getSystemValue("ONWEmrMRCODE"));
        emrParm.setData("CASE_NO", caseNo);
        emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
        parm.setData("SYSTEM_TYPE", "ONW");
        parm.setData("ADM_TYPE", admType);
        parm.setData("CASE_NO", caseNo);
        parm.setData("PAT_NAME", dataD.getValue("PAT_NAME",row));
        parm.setData("MR_NO", dataD.getValue("MR_NO",row));
        parm.setData("ADM_DATE", dataD.getTimestamp("ADM_DATE",row));
        parm.setData("DEPT_CODE", dataD.getValue("REALDEPT_CODE",row));
        parm.setData("EMR_FILE_DATA", emrParm);
        parm.setData("RULETYPE","2");//修改权限
        parm.addListener("EMR_LISTENER",this,"emrListener");
        parm.addListener("EMR_SAVE_LISTENER",this,"emrSaveListener");
        this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }
    
    /**
     * 检伤评估表单查看
     */
    public void onErdTriage(){
    	 TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
         int row = table.getSelectedRow();
         if(row<0){
             this.messageBox_("请选择病患！");
             return;
         }
         String caseNo = dataD.getValue("CASE_NO",row);
         String mrNo = dataD.getValue("MR_NO",row);
         String triageNo = dataD.getValue("TRIAGE_NO",row);
         if(triageNo.length() == 0){
        	 this.messageBox("请选择有检伤号的病患！");
        	 return;
         }
        String[] saveFiles = ERDLevelTool.getInstance().getELFile(triageNo);
    	TParm parm = new TParm();
    	parm.setData("CASE_NO", caseNo);
    	parm.setData("MR_NO", mrNo);
    	Pat pat = Pat.onQueryByMrNo(mrNo);
    	Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
    	parm.setData("ADM_DATE", reg.getAdmDate());
    	parm.setData("PAT_NAME", pat.getName());
    	parm.setData("SEX", pat.getSexString());
    	parm.setData("AGE", OdoUtil.showAge(pat.getBirthday(),
 				SystemTool.getInstance().getDate())); //年龄
    	TParm emrFileData = new TParm();
        emrFileData.setData("FILE_PATH", saveFiles[0]);
        emrFileData.setData("FILE_NAME", saveFiles[1]);
        emrFileData.setData("FLG", true);
        parm.setData("EMR_FILE_DATA", emrFileData);
        parm.setData("SYSTEM_TYPE", "EMG");
        parm.setData("RULETYPE", "1");
        parm.setData("ERD",true);
    	this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }
    
    /**
     * 检验报告
     */
    public void onCheckrep(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        SystemTool.getInstance().OpenLisWeb(dataD.getValue("MR_NO",selectedRow));
    }
    /**
     * 检查报告
     */
    public void onTestrep(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        SystemTool.getInstance().OpenRisWeb(dataD.getValue("MR_NO",selectedRow));
    }
    /**
     * 补充计价
     */
    public void onSupcharge(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("MR_NO",dataD.getValue("MR_NO",selectedRow));
        parm.setData("CASE_NO",dataD.getValue("CASE_NO",selectedRow));
        parm.setData("SYSTEM","ONW");
        parm.setData("ONW_TYPE",admType);//=====pangben 2013-5-15 门急诊护士站解锁使用，监听不同的界面
        this.openDialog("%ROOT%\\config\\opb\\OPBChargesM.x",parm);
        unLockPat(dataD.getValue("MR_NO",selectedRow));
    }
    /**
	 * 病患解锁
	 * ============pangben 2014-7-11
	 */
	public void unLockPat(String mr_no) {
		if (null==mr_no || mr_no.length()<=0) {
			return;
		}
		// 判断是否加锁
		if (PatTool.getInstance().isLockPat(mr_no)) {
			TParm parm = PatTool.getInstance().getLockPat(mr_no);
			if (Operator.getIP().equals(parm.getValue("OPT_TERM", 0))
					&&Operator.getID().equals(parm.getValue("OPT_USER", 0))) {
				if ("OPB".equals(parm.getValue("PRG_ID", 0))
						||"ONW".equals(parm.getValue("PRG_ID", 0))
								||"ENW".equals(parm.getValue("PRG_ID", 0))) {
					PatTool.getInstance().unLockPat(mr_no);
				}
			}
		}
	}
    /**
     * 就诊记录 查询
     */
    public void onOPDRecord(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow(); //获取选中行
        if (selectedRow < 0) {
            this.messageBox_("请选择病患！");
            return;
        }
//        System.out.println("就诊记录入参"+selectedRow);
//        System.out.println("就诊记录入参=病案号"+dataD.getValue("MR_NO", selectedRow));
        //===zhangp 20120703 start
//        Object obj = this.openDialog("%ROOT%\\config\\opd\\OPDViewCaseHistory.x",
//                                     dataD.getValue("MR_NO", selectedRow));
        Object obj = this.openDialog("%ROOT%\\config\\opd\\OPDCaseHistory.x",
                                     dataD.getValue("MR_NO", selectedRow));
        //===zhangp 20120703 end
    }
    /**
     * 皮试
     */
    public void onPSManage(){
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO",dataD.getData("CASE_NO",selectedRow));
        parm.setData("MR_NO",dataD.getData("MR_NO",selectedRow));
        this.openDialog("%ROOT%\\config\\onw\\ONWNSExec.x",parm);
    }

    /**
     * EMR监听
     * @param parm TParm
     */
    public void emrListener(TParm parm) {
        eventParmEmr = parm;
    }

    /**
     * EMR保存监听 取结构化病历中填写的值
     * @param parm TParm
     */
    public void emrSaveListener(TParm parm) {
        List name = new ArrayList(); //将取值控件的名字以List的形式 传入
        name.add("weight");
        name.add("height");
        //调用EMR中的取值方法， 返回Object数值
        Object[] obj = (Object[]) eventParmEmr.runListener(
            "getCaptureValueArray", name);
        if (obj == null) {
            this.messageBox_("保存病患数据失败");
            return;
        }
        if (obj.length < 1) {
            this.messageBox_("保存病患数据失败");
            return;
        }
        Object obj0 = obj[0];
        Map map = (HashMap) obj0;
        TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox_("请选择病患！");
            return;
        }
        String caseNo = dataD.getValue("CASE_NO", row);
        //对返回值进行操作
        String updateSql = "UPDATE REG_PATADM SET WEIGHT=" +
            TypeTool.getDouble(map.get("weight")) + " , HEIGHT=" +
            TypeTool.getDouble(map.get("height")) + ",OPT_USER='" +
            Operator.getID() +
            "' ,OPT_DATE=TO_DATE('" +
            StringTool.getString(TJDODBTool.getInstance().getDBTime(),
                                 "yyyyMMddHHmmss") +
            "','YYYYMMDDHH24MISS'),OPT_TERM='" + Operator.getIP() +
            "' WHERE CASE_NO='" + caseNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(updateSql));
        if (result.getErrCode() != 0) {
            System.out.println("errMsg=" + result.getErrText());
        }
    }
    /**
     * 读医疗卡
     * =======zhangp 20120227
     */
    public void onEKTcard(){
    	TParm ektparm = EKTIO.getInstance().TXreadEKT();
    	if (null == ektparm || ektparm.getValue("MR_NO").length() <= 0) {
            this.messageBox("请查看医疗卡是否正确使用");
            return;
    	}
    	if(ektparm.getErrCode()<0){
    		messageBox(ektparm.getErrText());
    	}
    	String mrNo = ektparm.getValue("MR_NO");
    	String sql =
    		"SELECT ADM_TYPE,A.CASE_NO,A.MR_NO,A.REGION_CODE,A.ADM_DATE," +
    		" A.REG_DATE,A.SESSION_CODE,A.CLINICAREA_CODE,A.CLINICROOM_NO,A.QUE_NO," +
    		" A.REG_ADM_TIME,A.DEPT_CODE,A.DR_CODE,A.REALDEPT_CODE,A.REALDR_CODE," +
    		" A.APPT_CODE,A.VISIT_CODE,A.REGMETHOD_CODE,A.CTZ1_CODE,A.CTZ2_CODE," +
    		" A.CTZ3_CODE,A.TRANHOSP_CODE,A.TRIAGE_NO,A.CONTRACT_CODE,A.ARRIVE_FLG," +
    		" A.REGCAN_USER,A.REGCAN_DATE,A.ADM_REGION,A.PREVENT_SCH_CODE,A.DRG_CODE," +
    		" A.HEAT_FLG,A.ADM_STATUS,A.REPORT_STATUS,A.WEIGHT,A.HEIGHT," +
    		" A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PAT_NAME,A.CLINICTYPE_CODE," +
    		" A.VIP_FLG,B.SEX_CODE,A.SERVICE_LEVEL " +
    		" FROM REG_PATADM A,SYS_PATINFO B  WHERE A.MR_NO=B.MR_NO  AND A.REGCAN_USER IS NULL " +
    		" AND A.MR_NO = '"+mrNo+"' ORDER BY QUE_NO";
    	TParm rParm = new TParm(TJDODBTool.getInstance().select(sql));
//    	System.out.println("rParm==="+rParm);
    	if(rParm.getCount()<0){
    		messageBox("无数据");
    		return;
    	}
    	if(rParm.getErrCode()<0){
    		messageBox(rParm.getErrText());
    		return;
    	}
    	String regionCode = "";
    	String admtype = "";
    	String admDate = "";
    	String sessionCode = "";
    	String clinic = "";
    	TParm lParm = new TParm();
    	for (int i = 0; i < rParm.getCount(); i++) {
    		rParm.addData("SELECT", "N");
    		regionCode = rParm.getData("REGION_CODE", i).toString();
    		admtype = rParm.getData("ADM_TYPE", i).toString();
    		admDate = rParm.getData("ADM_DATE", i).toString();
    		admDate = admDate.substring(0, 4) + admDate.substring(5, 7) + admDate.substring(8, 10);
    		sessionCode = rParm.getData("SESSION_CODE", i).toString();
    		clinic = rParm.getData("CLINICROOM_NO", i).toString();
    		sql = this.getSqll(regionCode, admtype, admDate, sessionCode, clinic);
//    		System.out.println(sql);
    		TParm tempParm = new TParm(TJDODBTool.getInstance().select(sql));
    		for (int j = 0; j < tempParm.getCount(); j++) {
				lParm.addData("REGION_CODE", tempParm.getData("REGION_CODE", j));
				lParm.addData("ADM_TYPE", tempParm.getData("ADM_TYPE", j));
				lParm.addData("ADM_DATE", tempParm.getData("ADM_DATE", j));
				lParm.addData("SESSION_CODE", tempParm.getData("SESSION_CODE", j));
				lParm.addData("CLINICROOM_NO", tempParm.getData("CLINICROOM_NO", j));
				lParm.addData("WEST_MEDI_FLG", tempParm.getData("WEST_MEDI_FLG", j));
				lParm.addData("DEPT_CODE", tempParm.getData("DEPT_CODE", j));
				lParm.addData("REG_CLINICAREA", tempParm.getData("REG_CLINICAREA", j));
				lParm.addData("DR_CODE", tempParm.getData("DR_CODE", j));
				lParm.addData("REALDEPT_CODE", tempParm.getData("REALDEPT_CODE", j));
				lParm.addData("REALDR_CODE", tempParm.getData("REALDR_CODE", j));
				lParm.addData("CLINICTYPE_CODE", tempParm.getData("CLINICTYPE_CODE", j));
				lParm.addData("QUEGROUP_CODE", tempParm.getData("QUEGROUP_CODE", j));
				lParm.addData("QUE_NO", tempParm.getData("QUE_NO", j));
				lParm.addData("MAX_QUE", tempParm.getData("MAX_QUE", j));
				lParm.addData("VIP_FLG", tempParm.getData("VIP_FLG", j));
				lParm.addData("CLINICTMP_FLG", tempParm.getData("CLINICTMP_FLG", j));
				lParm.addData("STOP_SESSION", tempParm.getData("STOP_SESSION", j));
				lParm.addData("REFRSN_CODE", tempParm.getData("REFRSN_CODE", j));
				lParm.addData("OPT_USER", tempParm.getData("OPT_USER", j));
				lParm.addData("OPT_DATE", tempParm.getData("OPT_DATE", j));
				lParm.addData("OPT_TERM", tempParm.getData("OPT_TERM", j));
			}
		}
    	dataD = rParm;
    	dataM = lParm;
    	this.callFunction("UI|LTABLE|setParmValue", lParm);
    	this.callFunction("UI|RTABLE|setParmValue", rParm);
    }
    /**
     * 得到sql
     * =====zhangp 20120227
     * @param regionCode
     * @param admType
     * @param admDate
     * @param sessionCode
     * @param clinic
     * @return
     */
    public String getSqll (String regionCode,String admtype,String admDate,String sessionCode,String clinic){
    	String sql =
    		"SELECT REGION_CODE, ADM_TYPE, ADM_DATE, SESSION_CODE, CLINICROOM_NO," +
    		" WEST_MEDI_FLG, DEPT_CODE, REG_CLINICAREA, DR_CODE, REALDEPT_CODE," +
    		" REALDR_CODE, CLINICTYPE_CODE, QUEGROUP_CODE, QUE_NO, MAX_QUE, VIP_FLG," +
    		" CLINICTMP_FLG, STOP_SESSION, REFRSN_CODE, OPT_USER, OPT_DATE, OPT_TERM" +
    		" FROM REG_SCHDAY WHERE REGION_CODE = '"+regionCode+
    		"' AND ADM_TYPE = '"+admtype+"' AND ADM_DATE = '"+admDate+"'" +
    		" AND SESSION_CODE = '"+sessionCode+"' AND CLINICROOM_NO = '"+clinic+"'";
    	return sql;
    }
    /**
	 * 解锁监听方法
	 * 
	 * @param prgId
	 *            String
	 * @param mrNo
	 *            String
	 * @param prgIdU
	 *            String
	 * @param userId
	 *            String
	 * @return Object
	 * ======pangben 2013-5-14
	 */
	public Object onListenPm(String prgId, String mrNo, String prgIdU,
			String userId) {
		if ("ONW".equalsIgnoreCase(prgId)||"ENW".equalsIgnoreCase(prgId)) {
		}else{
			return null;
		}
		TParm parm = new TParm();
		parm.setData("PRG_ID", prgId);
		parm.setData("MR_NO", mrNo);
		parm.setData("PRG_ID_U", prgIdU);
		parm.setData("USE_ID", userId);
		String flg = (String) openDialog(
				"%ROOT%\\config\\sys\\SYSPatUnLcokMessage.x", parm);
//		if ("OK".equals(flg)) {
//			this.onClear();
//			return "OK";
//		}
		if ("OK".equals(flg)) {
			String aa = PatTool.getInstance().getLockParmString(mrNo);
			PatTool.getInstance().unLockPat(mrNo);
			PATLockTool.getInstance().log(
					"ODO->" + SystemTool.getInstance().getDate() + " "
							+ Operator.getID() + " "
							+ Operator.getName() + " 强制解锁[" + aa
							+ " 病案号：" + mrNo + "]");
			
		}
		if ("OK".equals(flg)) {
			//this.onClear();
			//this.closeWindow();
			
			return "OK";
		}
		return "";
	}
	
	
	//================================================
	/**
     * CDR
     */
    public void onQuerySummaryInfo() {
        TParm parm = new TParm();
        TTable table = (TTable)this.getComponent("RTABLE");
        
        int selRow = table.getSelectedRow();
        if (selRow < 0) {
            this.messageBox("请选中要查看的病人信息");
            return;
        }    
        Container container = (Container) callFunction("UI|getThis");
        while (!(container instanceof TTabbedPane)) {
            container = container.getParent();
        }
        TTabbedPane tabbedPane = (TTabbedPane) container;

        parm.setData("MR_NO", table.getParmValue().getRow(selRow).getValue("MR_NO"));
        // 打开综合查询界面
        tabbedPane.openPanel("CDR_SUMMARY_UI",
                "%ROOT%\\config\\emr\\EMRCdrSummaryInfo.x", parm);
        TComponent component = (TComponent) callFunction(
                "UI|SYSTEM_TAB|findObject", "CDR_SUMMARY_UI");
        if (component != null) {
            tabbedPane.setSelectedComponent((Component) component);
            return;
        }
    }
    /**
     * 病历浏览
     */
    public void onCxShow(){  
    	TTable table =(TTable)this.getComponent("RTABLE");
     	TParm parm = table.getParmValue();
    	String mrNo = parm.getValue("MR_NO", table.getSelectedRow());
    	String caseNo = parm.getValue("CASE_NO", table.getSelectedRow());
        TParm result = queryPassword();
        String user_password = result.getValue("USER_PASSWORD",0);
        String url = "http://"+getWebServicesIp()+"?userId="+Operator.getID()+"&password="+user_password+"&mrNo="+mrNo+"&caseNo="+caseNo;
        try {
            Runtime.getRuntime().exec(String.valueOf(String.valueOf((new
                    StringBuffer("cmd.exe /c start iexplore \"")).append(
                            url).append("\""))));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private TParm queryPassword(){
        String sql = "SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID = '"+Operator.getID()+"' AND REGION_CODE = '"+Operator.getRegion()+"'";
        return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 获取配置文件中的电子病历服务器IP
     * @return
     */
    public static String getWebServicesIp() {
        TConfig config = getProp();
        String url = config.getString("", "EMRIP");
        return url;
    }
    
    /**
     * 获取配置文件
     * @author shendr
     */
    public static TConfig getProp() {
        TConfig config=null;
        try{
         config = TConfig
                .getConfig("WEB-INF\\config\\system\\TConfig.x");
        }catch(Exception e){
            e.printStackTrace();
        }
        return config;
    }
    //====================================================
    
     public void onQueryErdLevel(){
    	 this.onSelPat(selData);
     }
     
     /**
      * 检伤补录
      */
     public void onErdLevel(){
 		TTable table = (TTable) this.getComponent("RTABLE");
 		int row = table.getSelectedRow();
 		TParm tableParm = table.getParmValue();		
 		String caseNo = tableParm.getValue("CASE_NO", row);
 		this.onSelPat(selData);
 		table = (TTable) this.getComponent("RTABLE");
 		tableParm = table.getParmValue();	
 		row = -1;
 		for (int i = 0; i < tableParm.getCount("MR_NO"); i++) {
 			if(caseNo.equals(tableParm.getValue("CASE_NO", i))){
 				table.setSelectedRow(i);
 				row = i;
 				break;
 			}
 		}
 		if(row == -1){
 			this.messageBox("界面数据出现问题，请重新刷新界面");
 			return;
 		}
 		
 		if(tableParm.getValue("TRIAGE_NO", row).length() > 0){
 			this.messageBox("请选择无检伤号的病患");
 			return;
 		}
 		String mrNo = tableParm.getValue("MR_NO", row);
 		String sql = "SELECT MR_NO,IDNO,BIRTH_DATE,PAT_NAME,SEX_CODE FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
 		TParm pat = new TParm(TJDODBTool.getInstance().select(sql));
 		TParm parm = new TParm();
 		parm.setData("FLG", "NEW_ONW");
 		parm.setData("CASE_NO", tableParm.getValue("CASE_NO", row));
 		parm.setData("ERD_LEVEL", tableParm.getValue("ERD_LEVEL", row));
 		parm.setData("MR_NO", mrNo);
 		parm.setData("PAT_NAME", pat.getValue("PAT_NAME", 0));
 		parm.setData("IDNO", pat.getValue("IDNO", 0));
 		parm.setData("BIRTH_DATE", pat.getValue("BIRTH_DATE", 0));
 		parm.setData("SEX_CODE", pat.getValue("SEX_CODE", 0));
 		
// 		List l = new ArrayList();
//		l.add(parm);

 		// modified by wangqing 20170623
 		this.openWindow("%ROOT%\\config\\reg\\REGPreviewingTriage.x", parm);
 		this.onSelPat(selData);
 		
 		
 	}
     
	
	
	/**
     * 血糖报告
     */
    public void getXTReport(){
    	TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
    	SystemTool.getInstance().OpenTnbWeb(dataD.getValue("MR_NO",selectedRow));
    }
    
    /**
     * 心电检查
     */
    public void getPdfReport(){
    	TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
        int selectedRow = table.getSelectedRow();//获取选中行
        if(selectedRow<0){
            this.messageBox_("请选择病患！");
            return;
        }
        String sql = "SELECT  DISTINCT MED_APPLY_NO  FROM OPD_ORDER WHERE CASE_NO = '"+dataD.getValue("CASE_NO",selectedRow)+"' AND ORDER_CAT1_CODE = 'ECC'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount() <= 0){
    		this.messageBox("该病患没有心电检查医嘱");
    		return;
    	}
    	TParm parm = new TParm();
    	String opbBookNo = "";
    	for(int i = 0; i < result.getCount(); i++){
    		opbBookNo += "'"+result.getValue("MED_APPLY_NO", i)+"'"+",";
    	}
    	parm.setData("OPE_BOOK_NO",opbBookNo.substring(0, opbBookNo.length()-1));
		parm.setData("CASE_NO", dataD.getValue("CASE_NO",selectedRow));
		parm.setData("TYPE","3");
    	this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
    }
    
    /**
     * 查询已住院登记的病患
     */
	public void onQueryInHosp() {
		this.onSelPat(selData);
	}
	
	/**
	 * 生成交接单
	 */
	public void onCreate() {
		TParm action = new TParm();
		TTable table = (TTable) this.getComponent("RTABLE");
		int index = table.getSelectedRow();// 选中行
		if (index < 0) {
			this.messageBox("请选择病患");
			return;
		}
		TParm parm = table.getParmValue();
		
		String inCaseNo = parm.getValue("IN_CASE_NO", index);
		String inDeptCode = parm.getValue("IN_DEPT_CODE", index);
		
		if (this.getValueBoolean("IN_HOSP")) {
			if (StringUtils.isEmpty(parm.getValue("IN_CASE_NO", index))) {
				this.messageBox("该病患尚未办理住院登记手续");
				return;
			}
		} else {
			TParm queryParm = new TParm();
			queryParm.setData("ADM_DATE", selData.getValue("ADM_DATE"));
			queryParm.setData("CASE_NO", parm.getValue("CASE_NO", index));
			
			// 查询急诊护士站中已办理住院登记手续的病患数据
			TParm result = ONWTool.getInstance().queryInHospData(queryParm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("查询已办理住院手续病患信息错误");
				return;
			} else if (result.getCount() < 1) {
				this.messageBox("该病患尚未办理住院登记手续");
				return;
			} else {
				inCaseNo = result.getValue("IN_CASE_NO", 0);
				inDeptCode = result.getValue("IN_DEPT_CODE", 0);
			}
		}
		
		action.setData("MR_NO", parm.getValue("MR_NO", index));// 病案号
		action.setData("CASE_NO", inCaseNo);// 就诊号(住院)
		action.setData("REG_CASE_NO", parm.getValue("CASE_NO", index));// 就诊号(门急诊)
		action.setData("PAT_NAME", parm.getValue("PAT_NAME", index));// 姓名
		action.setData("FROM_DEPT", parm.getValue("DEPT_CODE", index));// 转出科室
		action.setData("TO_DEPT", inDeptCode);// 转入科室
		action.setData("DEPT_TYPE_FLG", "ERD");// 用于科室选择界面显示科室标记
		this.openWindow("%ROOT%\\config\\odi\\ODITransfertype.x", action);
	}
	
	/**
	 * 交接一览表
	 */
	public void onTransfer() {
		TParm action = new TParm();
		TTable table = (TTable) this.getComponent("RTABLE");
		int index = table.getSelectedRow();// 选中行
		if (index > 0) {
			TParm parm = table.getParmValue();
			action.setData("MR_NO", parm.getValue("MR_NO", index));// 病案号
			action.setData("CASE_NO", parm.getValue("IN_CASE_NO", index));// 就诊号
		}
		this.openWindow("%ROOT%\\config\\inw\\INWTransferSheet.x", action);
	}
	

	/**
	 * 采血病历
	 * modified by WangQing 20170309
	 * 胸痛中心专用，打开急诊护士站-胸痛急诊护士记录病历
	 */
	public void onBloodEmr(){
//		this.messageBox("采血病历！！！");	
		TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");	
		if(table.getSelectedRow() == -1){
			this.messageBox("请选择病患！！！");
			return;
		}
		TParm patParm = table.getParmValue();
		System.out.println("+++rowParm:::"+patParm.getRow(table.getSelectedRow()));
		TParm sysParm = new TParm();
		sysParm.setData("TRIAGE_NO", patParm.getData("TRIAGE_NO", table.getSelectedRow()));// 检伤号
		sysParm.setData("CASE_NO", patParm.getData("CASE_NO", table.getSelectedRow()));// 就诊号
		sysParm.setData("MR_NO", patParm.getData("MR_NO", table.getSelectedRow()));// 病案号
		sysParm.setData("PAT_NAME", patParm.getData("MR_NO", table.getSelectedRow()));// 患者姓名
		sysParm.setData("SEX_CODE", patParm.getData("SEX_CODE", table.getSelectedRow()));// 患者性别
		sysParm.setData("AGE", OdiUtil.getInstance().showAge(this.getBirthdayByMrNo((String) patParm.getData("MR_NO", table.getSelectedRow())),
				SystemTool.getInstance().getDate()));// 患者年龄
		System.out.println("+++sysParm:::"+sysParm);
		this.openWindow("%ROOT%\\config\\reg\\REGPreviewingWindow.x", sysParm);
	}
	
	/**
	 * 根据病案号获取出生日期
	 * @param mrNo
	 * @return
	 */
	public Timestamp getBirthdayByMrNo(String mrNo){
		String sql = "SELECT A.BIRTH_DATE FROM SYS_PATINFO A WHERE A.MR_NO = '" + mrNo + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result.getTimestamp("BIRTH_DATE", 0);
	}
	
	
	/**
	 * 床旁超声
	 * 胸痛中心专用
	 * modified by WangQing 20170309
	 */
	public void onBedUT(){
//		this.messageBox("床旁超声！！！");
		TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
		if(table.getSelectedRow() == -1){
			this.messageBox("请选择病患！！！");
			return;
		}
		TParm patParm = table.getParmValue();		
		TParm sysParm = new TParm();
		sysParm.setData("CASE_NO", patParm.getData("CASE_NO", table.getSelectedRow()));
		sysParm.setData("UT_TRIAGE_NO", patParm.getData("TRIAGE_NO", table.getSelectedRow()));
		this.openWindow("%ROOT%\\config\\reg\\REGColourTriage_new.x", sysParm, false);
	}

	/**
	 * 院前信息
	 * @author WangQing 20170314
	 */
	public void onPreInfo(){
//		this.messageBox("院前信息！！！");
		TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
		if(table.getSelectedRow() == -1){
			this.messageBox("请选择病患！！！");
			return;
		}
		TParm sysParm = new TParm();
		this.openWindow("%ROOT%\\config\\reg\\REGPreInfo.x", sysParm, false);	
	}
	
	/**
	 * 急诊抢救记录
	 */
	public void onRescueRecord(){
//		this.messageBox("急诊抢救记录！！！");
		TTable table = (TTable)this.callFunction("UI|RTABLE|getThis");
		if(table.getSelectedRow() == -1){
			this.messageBox("请选择病患！！！");
			return;
		}
		TParm patParm = table.getParmValue().getRow(table.getSelectedRow());	
		System.out.println("++++++patParm:::"+patParm);		
		TParm sysParm = new TParm();
		sysParm.setData("TRIAGE_NO", patParm.getData("TRIAGE_NO"));// 检伤号
		sysParm.setData("ADM_DATE", patParm.getData("ADM_DATE"));// 到院时间
		sysParm.setData("SERVICE_LEVEL", patParm.getData("SERVICE_LEVEL"));// 检伤等级
		sysParm.setData("PAT_NAME", patParm.getData("PAT_NAME"));// 患者姓名	
		this.openWindow("%ROOT%\\config\\reg\\REGSaveLabel.x", sysParm, false);	
	}
	
	/**
	 * 其他报告
	 *   == 20180115 zhanglei add
	 */
	public void getPDFQiTa() {
		TTable t = (TTable) this.getComponent("RTABLE");
		int row = t.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择一行数据！");
			return;
		}
		TParm rowParm = t.getParmValue().getRow(row);
		this.messageBox("CASE_NO " + rowParm.getValue("CASE_NO") + " MR_NO " + rowParm.getValue("MR_NO"));
		TParm parm = new TParm();
		parm.setData("CASE_NO", rowParm.getValue("CASE_NO"));
		parm.setData("MR_NO", rowParm.getValue("MR_NO"));
//		this.messageBox("CASE_NO " + rowParm.getValue("CASE_NO") + " MR_NO " +  rowParm.getValue("MR_NO"));
		this.openDialog("%ROOT%\\config\\sys\\SYSOpeQiTaPDF.x", parm);
	}
	
	
	/**
	 * 跌倒、疼痛评估
	 * @author wangqing 20180124
	 */
	public void onFallAndPainAssessment(){
		TTable table = (TTable) this.getComponent("RTABLE");
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox("请选择一行数据");
			return;
		}
		TParm tblParm = table.getParmValue();
		String triageNo = tblParm.getValue("TRIAGE_NO", row);
		if(triageNo==null || triageNo.trim().length()<=0){
			return;
		}
		String mrNo = tblParm.getValue("MR_NO", row);
		TParm parm = new TParm();
		parm.setData("TRIAGE_NO", triageNo);
		parm.setData("MR_NO", mrNo);
		this.openWindow("%ROOT%\\config\\reg\\REGFallAndPainAssessment.x", parm);	
	}
	
}
