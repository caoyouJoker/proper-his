package com.javahis.ui.ope;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.SwingUtilities;
import jdo.sys.Pat;
import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.StringUtil;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMXMLTool;
import jdo.ope.OPEOpBookTool;
import jdo.ope.OPEOpDetailTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;


/**
 * <p>Title: 手术记录</p>
 *
 * <p>Description: 手术记录</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-9-28
 * @version 1.0
 */
public class OPEOpDetailControl
    extends TControl {
    private String OP_RECORD_NO = "";//手术记录单号
    private String OPBOOK_SEQ = "";//手术申请编号
    private String SAVE_FLG = "new";//保存方式 new:新建  update:修改
    private String MR_NO = "";//MR_NO
    private String CASE_NO = "";//CASE_NO
    private String IPD_NO = "";//住院号 住院医生站调用的时候 需要传此参数
    private TTable EXTRA_TABLE; //体外循环师表格
    private TTable CIRCULE_TABLE; //循环护士表格
    private TTable SCRUB_TABLE; //洗手护士表格
    private TTable ANA_TABLE; //麻醉医师表格
    private TParm eventParmEmr;//结构化病历返回的方法列表
    private TTable Daily_Table ;
    private TTable OP_Table;
    private TParm DetailData;//手术记录信息
    private String ADM_TYPE = "";//门急住别
    private String SYSTEM = "";//记录是哪个系统调用
    private Pat pat;//==========pangben modify 20110701 病患信息
    public void onInit() {
        super.onInit();
        Daily_Table = (TTable)this.getComponent("Daily_Table");
        OP_Table = (TTable)this.getComponent("OP_Table");
        EXTRA_TABLE = (TTable)this.getComponent("EXTRA_TABLE");
        CIRCULE_TABLE = (TTable)this.getComponent("CIRCULE_TABLE");
        SCRUB_TABLE = (TTable)this.getComponent("SCRUB_TABLE");
        ANA_TABLE = (TTable)this.getComponent("ANA_TABLE");
        ANA_TABLE.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                   "onANATableMainCharge");
        PageParmInit();
        TableInit();
    }
    /**
     * 页面参数初始化
     */
    private void PageParmInit(){
        //模拟参数
//        TParm parmR = new TParm();
//        parmR.setData("SYSTEM","OPD");//调用系统简称
//        parmR.setData("ADM_TYPE","O");
//        parmR.setData("CASE_NO","100307000010");
//        parmR.setData("MR_NO","000000000579");
        //接参  手术预约单号
        Object obj = this.getParameter();
//        Object obj = parmR;
        TParm parmObj = new TParm();
        if(obj instanceof TParm){
            parmObj = (TParm)obj;
        }else{
            return;
        }
        MR_NO = parmObj.getValue("MR_NO");
        //如果参数中 有“SYSTEM”项 表示是医生站调用的
        SYSTEM = parmObj.getValue("SYSTEM");
        ADM_TYPE = parmObj.getValue("ADM_TYPE");
        //判断门急住别 显示诊区或者病区
        TLabel tLabel_9 = (TLabel)this.getComponent("tLabel_9");
        if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E")){
            tLabel_9.setZhText("手术诊区");
            this.callFunction("UI|OP_STATION_CODE_I|setVisible", false);
            this.callFunction("UI|OP_STATION_CODE_O|setVisible", true);
        }else if(ADM_TYPE.equals("I")){
            tLabel_9.setZhText("手术病区");
            this.callFunction("UI|OP_STATION_CODE_I|setVisible", true);
            this.callFunction("UI|OP_STATION_CODE_O|setVisible", false);
        }
        if(SYSTEM.length()>0){
            this.setValue("MR_NO",MR_NO);
            CASE_NO = parmObj.getValue("CASE_NO");
            onMR_NO();
        }else{
            OPBOOK_SEQ = parmObj.getValue("OPBOOK_SEQ");
            if(parmObj.getValue("EDITABLE").equals("FALSE")){//add by wanglong 20121219
            	setOpBookData();
            	setUIFalse();
            } else//add by wanglong 20121219
            selectOpBook(OPBOOK_SEQ);
        }
    }

    /**
     * Table初始化
     */
    public void TableInit() {
        //手术Table 监听
        OP_Table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                  "onCreateEditComponentOP");
        //主手术改变事件
        OP_Table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                  "onOpTableMainCharge");
        OpList opList = new OpList();
        OP_Table.addItem("OpList", opList);
        //诊断Table监听
        Daily_Table = (TTable)this.getComponent("Daily_Table");
        Daily_Table.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
                                     "onCreateEditComponent");
        //主诊断改变事件
        Daily_Table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                     "onDiagTableMainCharge");
        OrderList orderList = new OrderList();
        Daily_Table.addItem("OrderList", orderList);
//        //诊断Grid值改变事件
        this.addEventListener("Daily_Table->" + TTableEvent.CHANGE_VALUE,
                              "onDiagTableValueCharge");

        //手术Grid值改变事件
        this.addEventListener("OP_Table->" + TTableEvent.CHANGE_VALUE,
                              "onOpTableValueCharge");
    }
    /**
     * 保存
     */
    public void onSave(){
        delTable();
        if(!checkData())
            return;
        //没有预约单号 不进行保存
        if(OPBOOK_SEQ.length()<=0){
            return;
        }
        if("new".equals(SAVE_FLG)){
            insert();
        }else if("update".equals(SAVE_FLG)){
            update();
        }
    }
    /**
     * 插入手术记录
     */
    private void insert() {
        //手术记录号
        OP_RECORD_NO = SystemTool.getInstance().getNo("ALL", "OPE", "OP_RECORD_NO",
                                                "OP_RECORD_NO"); //调用取号原则
        TParm parm = this.getSaveData();
        TParm result = TIOM_AppServer.executeAction(
            "action.ope.OPEDetailAction",
            "insertData", parm);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        this.messageBox("P0005");
        //生成电视屏XML wanglong add 20141010
        TParm xmlParm = ADMXMLTool.getInstance().creatOPEInfoXMLFile(CASE_NO, OPBOOK_SEQ);
        if (xmlParm.getErrCode() < 0) {
            this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
        }
    }
    /**
     * 修改手术记录
     */
    private void update(){
        TParm updateData = getSaveData();
        TParm result = OPEOpDetailTool.getInstance().updateData(updateData);
        if(result.getErrCode()<0){
            this.messageBox("E0005");
            return;
        }
        this.messageBox("P0005");
        //生成电视屏XML wanglong add 20141010
        TParm xmlParm = ADMXMLTool.getInstance().creatOPEInfoXMLFile(CASE_NO, OPBOOK_SEQ);
        if (xmlParm.getErrCode() < 0) {
            this.messageBox("电视屏接口发送失败 " + xmlParm.getErrText());
        }
    }
    /**
     * 设置病患基本信息
     * @param pat Pat
     */
    private void setPatInfo(Pat pat){
        this.setValue("MR_NO",pat.getMrNo());
        if("en".equals(this.getLanguage())){
            this.setValue("PAT_NAME",pat.getName1());//姓名
            //计算年龄
            String[] res = StringTool.CountAgeByTimestamp(pat.getBirthday(),SystemTool.getInstance().getDate());
            this.setValue("AGE",res[0]+"Y");
        }else{
            this.setValue("PAT_NAME",pat.getName());//姓名
            this.setValue("AGE",
                      StringUtil.getInstance().showAge(pat.getBirthday(),
            SystemTool.getInstance().getDate()));//岁数
        }
        this.setValue("SEX",pat.getSexCode());
        this.setValue("Weight",pat.getWeight());
    }
    /**
     * 汇总页面信息 获取保存信息
     */
    private TParm getSaveData() {
        TParm parm = new TParm();
        parm.setData("OPBOOK_NO",OPBOOK_SEQ);//手术申请编号
        parm.setData("OP_RECORD_NO",OP_RECORD_NO);//手术记录编号
        parm.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
        parm.setData("MR_NO",MR_NO);
        parm.setData("IPD_NO",IPD_NO);
        parm.setData("CASE_NO",CASE_NO);
        parm.setData("BED_NO",this.getValue("BED_NO"));
        parm.setData("URGBLADE_FLG",this.getValueString("URGBLADE_FLG"));//急做手术标记
        parm.setData("OP_DATE",StringTool.getString((Timestamp)this.getValue("OP_DATE"),"yyyyMMddHHmmss"));//手术时间
        parm.setData("TIME_NEED",this.getValue("TIME_NEED"));//所需时间
        parm.setData("ROOM_NO",this.getValue("ROOM_NO"));//手术间
        parm.setData("TYPE_CODE",this.getValue("TYPE_CODE"));//手术类型
        parm.setData("ANA_CODE",this.getValue("ANA_CODE"));//麻醉方式
        parm.setData("ASA_CODE",this.getValueString("ASA_CODE"));//麻醉分级   add by wanglong 20121206
        parm.setData("NNIS_CODE",this.getValueString("NNIS_CODE"));//手术风险分级   add by wanglong 20121206
        parm.setData("PART_CODE",this.getValueString("PART_CODE"));//手术部位   add by wanglong 20121206
        parm.setData("ISO_FLG",this.getValueString("ISO_FLG"));//隔离手术标记   add by wanglong 20121206
        parm.setData("OP_DEPT_CODE",this.getValue("OP_DEPT_CODE"));//手术科室
        if(ADM_TYPE.equals("I"))
            parm.setData("OP_STATION_CODE",this.getValue("OP_STATION_CODE_I"));//手术病区
        else if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E"))
            parm.setData("OP_STATION_CODE",this.getValue("OP_STATION_CODE_O"));//手术诊区
        parm.setData("RANK_CODE",this.getValue("RANK_CODE"));//手术等级
        parm.setData("WAY_CODE",this.getValue("WAY_CODE"));//手术方法
        TParm Daily_Data = this.getDailyData();//获取诊断信息
        parm.setData("DIAG_CODE1",Daily_Data.getValue("DIAG_CODE1"));
        parm.setData("DIAG_CODE2",Daily_Data.getValue("DIAG_CODE2"));
        parm.setData("DIAG_CODE3",Daily_Data.getValue("DIAG_CODE3"));
        parm.setData("BOOK_DEPT_CODE",this.getValue("BOOK_DEPT_CODE"));//预约部门
        TParm Op_Data = this.getOpData();//获取手术信息
        parm.setData("OP_CODE1",Op_Data.getValue("OP_CODE1"));
        parm.setData("OP_CODE2",Op_Data.getValue("OP_CODE2"));
        parm.setData("BOOK_DR_CODE",this.getValueString("BOOK_DR_CODE"));//预约医师
        parm.setData("MAIN_SURGEON",this.getValueString("MAIN_SURGEON"));//主刀医师
        parm.setData("REAL_AST1",this.getValueString("REAL_AST1"));
        parm.setData("REAL_AST2",this.getValueString("REAL_AST2"));
        parm.setData("REAL_AST3",this.getValueString("REAL_AST3"));
        parm.setData("REAL_AST4",this.getValueString("REAL_AST4"));
        //流动护士
        TParm CIRCULE = this.getCIRCULEData();
        parm.setData("CIRCULE_USER1",CIRCULE.getValue("CIRCULE_USER1"));
        parm.setData("CIRCULE_USER2",CIRCULE.getValue("CIRCULE_USER2"));
        parm.setData("CIRCULE_USER3",CIRCULE.getValue("CIRCULE_USER3"));
        parm.setData("CIRCULE_USER4",CIRCULE.getValue("CIRCULE_USER4"));
        //刷手护士
        TParm SCRUB = this.getSCRUBData();
        parm.setData("SCRUB_USER1",SCRUB.getValue("SCRUB_USER1"));
        parm.setData("SCRUB_USER2",SCRUB.getValue("SCRUB_USER2"));
        parm.setData("SCRUB_USER3",SCRUB.getValue("SCRUB_USER3"));
        parm.setData("SCRUB_USER4",SCRUB.getValue("SCRUB_USER4"));
        //麻醉医师
        TParm ANA = this.getANAData();
        parm.setData("ANA_USER1",ANA.getValue("ANA_USER1"));
        parm.setData("ANA_USER2",ANA.getValue("ANA_USER2"));
        //体外循环师
        TParm EXTRA = this.getEXTRAData();
        parm.setData("EXTRA_USER1",EXTRA.getValue("EXTRA_USER1"));
        parm.setData("EXTRA_USER2",EXTRA.getValue("EXTRA_USER2"));
        parm.setData("DRG_CODE","");//DRG代码 （保留）
        parm.setData("NURSE_START_DATE","");//护理起时（保留）
        parm.setData("ENTER_DATE","");//推入时间（保留）
        //手术下刀时间（保留）暂时和OP_DATE保持一致  modify by wanglong 20121219
        parm.setData("OP_START_DATE",StringTool.getString((Timestamp)this.getValue("OP_DATE"),"yyyyMMddHHmmss"));
        //手术缝合时间（保留） modify by wanglong 20121219
        parm.setData("OP_END_DATE",StringTool.getString((Timestamp)this.getValue("OP_END_DATE"),"yyyyMMddHHmmss"));
        parm.setData("NURSE_END_DATE","");//护理迄时（保留）
        parm.setData("EXIT_DATE","");//推出时间（保留）
        parm.setData("BIOPSY_FLG","");//切片注记（保留）
        parm.setData("BILL_FLG","");//批过价注记（保留）
        parm.setData("OPT_USER",Operator.getID());
        parm.setData("OPT_TERM",Operator.getIP());
        return parm;
    }
    /**
     * 获取诊断数据
     */
    private TParm getDailyData(){
        TParm parm = new TParm();
        int index = 2;//诊断数 以2作为开始值 因为主诊断是1
        for(int i=0;i<Daily_Table.getRowCount();i++){
            if(Daily_Table.getValueAt(i,2).toString().trim().length()>0){
                //判断主诊断
                if("Y".equals(Daily_Table.getValueAt(i,1).toString())){
                    parm.setData("DIAG_CODE1",Daily_Table.getValueAt(i,2));
                }else{
                    parm.setData("DIAG_CODE"+index,Daily_Table.getValueAt(i,2));
                    index++;
                }
            }
        }
        return parm;
    }
    /**
     * 获取手术术式数据
     * @return TParm
     */
    private TParm getOpData(){
        TParm parm = new TParm();
        for(int i=0;i<OP_Table.getRowCount();i++){
            if(OP_Table.getValueAt(i,2).toString().trim().length()>0){
                //判断主诊断
                if("Y".equals(OP_Table.getValueAt(i,1))){
                    parm.setData("OP_CODE1",OP_Table.getValueAt(i,2));
                }else{
                    parm.setData("OP_CODE2",OP_Table.getValueAt(i,2));
                }
            }
        }
        return parm;
    }

    /**
     * 删除诊断和手术表格的信息（勾选删除标记的）
     */
    private void delTable(){
        OP_Table.acceptText();
        Daily_Table.acceptText();
        for(int i=OP_Table.getRowCount()-1;i>=0;i--){
            if("Y".equals(OP_Table.getValueAt(i,0))){
                OP_Table.removeRow(i);
            }
        }
        for(int i=Daily_Table.getRowCount()-1;i>=0;i--){
            if("Y".equals(Daily_Table.getValueAt(i,0))){
                Daily_Table.removeRow(i);
            }
        }
    }

    /**
     * 检查数据
     */
    private boolean checkData(){
        if("".equals(this.getValueString("ADM_TYPE"))){
            this.messageBox("E0075");
            this.grabFocus("ADM_TYPE");
            return false;
        }
        if(this.getValue("OP_DATE")==null){
            this.messageBox("E0076");//请填写手术日期
            this.grabFocus("OP_DATE");
            return false;
        }
        if(this.getValue("OP_END_DATE")==null){// add by wanglong 20121219
            this.messageBox("请填写手术结束日期");
            this.grabFocus("OP_END_DATE");
            return false;
        }
        if("".equals(this.getValueString("OP_DEPT_CODE"))){
            this.messageBox("E0077");//请选择手术科室
            this.grabFocus("OP_DEPT_CODE");
            return false;
        }
        OP_Table.acceptText();
        Daily_Table.acceptText();
        boolean flg = false;//主诊断标识 true:存在主诊断（主手术） false:不存在主诊断(主手术)
        for(int i=0;i<OP_Table.getRowCount();i++){
            if("Y".equals(OP_Table.getValueAt(i,1))){
                flg = true;
            }
        }
        if(!flg){
            this.messageBox("E0078");//请选择一条手术编码作为主手术
            return flg;
        }
        flg = false;
        for(int i=0;i<Daily_Table.getRowCount();i++){
            if("Y".equals(Daily_Table.getValueAt(i,1))){
                flg = true;
            }
        }
        if(!flg){
            this.messageBox("E0079");
            return flg;
        }
        //判断是否选了主麻醉医师
        flg = false;
        for(int i=0;i<ANA_TABLE.getRowCount();i++){
            if ("Y".equals(ANA_TABLE.getValueAt(i,0).toString())){
                flg = true;
            }
        }
        if(!flg){
            this.messageBox("E0080");
            return flg;
        }
        return flg;
    }
    /**
     * 病案号回车事件
     */
    public void onMR_NO(){
        pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
        if(pat==null){
            this.messageBox("E0081");
            return;
        }
     // modify by huangtt 20160928 EMPI患者查重提示 start
         MR_NO = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        this.setValue("MR_NO",MR_NO);
        if (!StringUtil.isNullString(MR_NO) && !MR_NO.equals(pat.getMrNo())) {
			messageBox("病案号" + MR_NO + " 已合并至 " + "" + pat.getMrNo());
			MR_NO = pat.getMrNo();
			this.setValue("MR_NO", MR_NO);
		}
        
//        MR_NO = pat.getMrNo();
//        this.setValue("MR_NO",MR_NO);
        
     // modify by huangtt 20160928 EMPI患者查重提示 end
        
        TParm opParm = new TParm();
        opParm.setData("MR_NO",MR_NO);
        if(SYSTEM.length()>0){
            opParm.setData("CASE_NO",CASE_NO);
        }
        //查询手术预约信息
        //===============pangben modify 20110630 start
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
            opParm.setData("REGION_CODE", Operator.getRegion());
        }
        //=============pangben modify 20110630 stop

        TParm opBook = OPEOpBookTool.getInstance().selectOpBook(opParm);
        //判断是否存在手术预约信息
        int opBookCount = opBook.getCount();
        if(opBookCount>0){
            TParm parm = new TParm();
            parm.setData("MR_NO",MR_NO);
            if (SYSTEM.length() > 0) {
                parm.setData("CASE_NO", CASE_NO);
            }
            Object obj = this.openDialog("%ROOT%/config/ope/OPEOpBookChoose.x",parm);
            TParm re = new TParm();
            if(obj instanceof TParm){
                re = (TParm)obj;
            }else {
                this.setValue("MR_NO","");
                return;
            }
            SAVE_FLG = re.getValue("FLG");//返回状态  new:新建 update:修改
            OPBOOK_SEQ = re.getValue("OPBOOK_SEQ");
            this.setValue("OPBOOK_NO",OPBOOK_SEQ);
            if("new".equals(SAVE_FLG)){
                setOpBookData();//获取预约手术信息并赋值
            }
            if("update".equals(SAVE_FLG)){
                OP_RECORD_NO = re.getValue("OP_RECORD_NO");
                setOpRecordData();
            }
            if ("close".equals(SAVE_FLG)) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            closeWindow();
                        }
                        catch (Exception e) {
                        }
                    }
                });
            }
            callFunction("UI|MR_NO|setEnabled", false);
            callFunction("UI|OPBOOK_NO|setEnabled", false);
            setPatInfo(pat);
        }else{
            this.messageBox("E0082");
            return;
        }
    }
    /**
     * 预约号回车事件
     */
    public void onOPBOOK_NO(){
        String seq = this.getValueString("OPBOOK_NO");
        this.selectOpBook(seq);
    }
    /**
     * 根据 预约号 查询手术预约信息
     * @param OpBookNo String
     */
    private void selectOpBook(String OpBookNo){
        //根据手术预约单号 查询手术预约信息
        TParm parm = new TParm();
        parm.setData("OPBOOK_SEQ",OpBookNo);//手术预约单号
        //查询手术预约
        TParm OpBook = OPEOpBookTool.getInstance().selectOpBook(parm);
        if(OpBook.getCount()<=0){
            this.messageBox("E0083");
            return;
        }
        OPBOOK_SEQ = OpBook.getValue("OPBOOK_SEQ",0);
        MR_NO  = OpBook.getValue("MR_NO",0);
        //根据预约单号 查询手术记录
        TParm opRecord = OPEOpDetailTool.getInstance().selectData(parm);
        //如果该预约单号 不存在手术记录 那么新建手术记录
        if(opRecord.getCount()<=0){
            SAVE_FLG = "new";//返回状态  new:新建 update:修改
            this.setValue("OPBOOK_NO",OPBOOK_SEQ);
            setOpBookData();//获取预约手术信息并赋值
            callFunction("UI|MR_NO|setEnabled", false);
            callFunction("UI|OPBOOK_NO|setEnabled", false);
            pat= Pat.onQueryByMrNo(MR_NO);//======pangben modify 20110701 获得病患信息
            setPatInfo(pat);
        }else{
            TParm p = new TParm();
            p.setData("OPBOOK_SEQ",OPBOOK_SEQ);
            Object obj = this.openDialog("%ROOT%/config/ope/OPEOpBookChoose.x",p);
            TParm re = new TParm();
            if(obj instanceof TParm){
                re = (TParm)obj;
            }else {
                this.setValue("OPBOOK_NO","");
                return;
            }
            SAVE_FLG = re.getValue("FLG");//返回状态  new:新建 update:修改
            OPBOOK_SEQ = re.getValue("OPBOOK_SEQ");
            this.setValue("OPBOOK_NO",OPBOOK_SEQ);
            if("new".equals(SAVE_FLG)){
                setOpBookData();//获取预约手术信息并赋值
            }
            if("update".equals(SAVE_FLG)){
                OP_RECORD_NO = re.getValue("OP_RECORD_NO");
                setOpRecordData();
            }
            if ("close".equals(SAVE_FLG)) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        try {
                            closeWindow();
                        }
                        catch (Exception e) {
                        }
                    }
                });
            }
            callFunction("UI|MR_NO|setEnabled", false);
            callFunction("UI|OPBOOK_NO|setEnabled", false);
            pat= Pat.onQueryByMrNo(MR_NO);//======pangben modify 20110701 获得病患信息
            setPatInfo(pat);
        }
    }
    /**
     * 获取预约手术信息并赋值
     */
    private void setOpBookData(){
        this.setValue("OPBOOK_NO",OPBOOK_SEQ);//add by wanglong 20140411
        TParm opBook = new TParm();
        opBook.setData("OPBOOK_SEQ",OPBOOK_SEQ);
        TParm result = OPEOpBookTool.getInstance().selectOpBook(opBook);
        MR_NO = result.getValue("MR_NO",0);
        CASE_NO = result.getValue("CASE_NO",0);
        IPD_NO = result.getValue("IPD_NO",0);
        this.setValue("IPD_NO",result.getValue("IPD_NO",0));
        this.setValue("URGBLADE_FLG",result.getBoolean("URGBLADE_FLG",0));//急做标记
        this.setValue("OP_DATE",result.getTimestamp("OP_DATE",0));//手术日期
        this.setValue("ADM_TYPE",result.getValue("ADM_TYPE",0));
        this.setValue("OP_DEPT_CODE",result.getValue("OP_DEPT_CODE",0));
        ADM_TYPE = result.getValue("ADM_TYPE",0);
        if(ADM_TYPE.equals("I"))
            this.setValue("OP_STATION_CODE_I",result.getValue("OP_STATION_CODE",0));
        else if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E"))
            this.setValue("OP_STATION_CODE_O",result.getValue("OP_STATION_CODE",0));
        this.setValue("BED_NO",result.getValue("BED_NO",0));
        this.setValue("ROOM_NO",result.getValue("ROOM_NO",0));
        this.setValue("TYPE_CODE",result.getValue("TYPE_CODE",0));
        this.setValue("RANK_CODE",result.getValue("RANK_CODE",0));
        this.setValue("ANA_CODE",result.getValue("ANA_CODE",0));
        this.setValue("PART_CODE",result.getValue("PART_CODE",0));//手术部位  add by wanglong 20121206
        this.setValue("ISO_FLG",result.getBoolean("ISO_FLG",0));//隔离手术标记 add by wanglong 20121206
        this.setValue("MAIN_SURGEON",result.getValue("MAIN_SURGEON",0));
        this.setValue("REAL_AST1",result.getValue("BOOK_AST_1",0));
        this.setValue("REAL_AST2",result.getValue("BOOK_AST_2",0));
        this.setValue("REAL_AST3",result.getValue("BOOK_AST_3",0));
        this.setValue("REAL_AST4",result.getValue("BOOK_AST_4",0));
        Daily_Table.removeRowAll();
        //诊断
        if(result.getValue("DIAG_CODE1",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index,2,result.getValue("DIAG_CODE1",0));
            Daily_Table.setItem(index,3,result.getValue("DIAG_CODE1",0));
            Daily_Table.setItem(index,1,"Y");
        }
        if(result.getValue("DIAG_CODE2",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index, 2, result.getValue("DIAG_CODE2", 0));
            Daily_Table.setItem(index, 3, result.getValue("DIAG_CODE2", 0));
        }
        if(result.getValue("DIAG_CODE3",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index, 2, result.getValue("DIAG_CODE3", 0));
            Daily_Table.setItem(index, 3, result.getValue("DIAG_CODE3", 0));
        }
        Daily_Table.addRow();
        //手术方式
        OP_Table.removeRowAll();
        if(result.getValue("OP_CODE1",0).length()>0){
            int index = OP_Table.addRow();
            OP_Table.setItem(index,2,result.getValue("OP_CODE1",0));
            OP_Table.setItem(index,3,result.getValue("OP_CODE1",0));
            OP_Table.setItem(index,1,"Y");
        }
        if(result.getValue("OP_CODE2",0).length()>0){
            int index = OP_Table.addRow();
            OP_Table.setItem(index,2,result.getValue("OP_CODE2",0));
            OP_Table.setItem(index,3,result.getValue("OP_CODE2",0));
        }
        OP_Table.addRow();
        int row = 0;
        //体外循环师
        if(result.getValue("EXTRA_USER1",0).length()>0){
            row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,result.getValue("EXTRA_USER1",0));
        }
        if(result.getValue("EXTRA_USER2",0).length()>0){
            row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,result.getValue("EXTRA_USER2",0));
        }
        //循环护士
        if(result.getValue("CIRCULE_USER1",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,result.getValue("CIRCULE_USER1",0));
        }
        if(result.getValue("CIRCULE_USER2",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,result.getValue("CIRCULE_USER2",0));
        }
        if(result.getValue("CIRCULE_USER3",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,result.getValue("CIRCULE_USER3",0));
        }
        if(result.getValue("CIRCULE_USER4",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,result.getValue("CIRCULE_USER4",0));
        }
        //洗手护士
        if(result.getValue("SCRUB_USER1",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,result.getValue("SCRUB_USER1",0));
        }
        if(result.getValue("SCRUB_USER2",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,result.getValue("SCRUB_USER2",0));
        }
        if(result.getValue("SCRUB_USER3",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,result.getValue("SCRUB_USER3",0));
        }
        if(result.getValue("SCRUB_USER4",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,result.getValue("SCRUB_USER4",0));
        }
        //麻醉医师
        if(result.getValue("ANA_USER1",0).length()>0){
            row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,0,"Y");
            ANA_TABLE.setItem(row,1,result.getValue("ANA_USER1",0));
        }
        if(result.getValue("ANA_USER2",0).length()>0){
            row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,0,"N");
            ANA_TABLE.setItem(row,1,result.getValue("ANA_USER2",0));
        }
        TLabel tLabel_9 = (TLabel)this.getComponent("tLabel_9");
        if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E")){
            tLabel_9.setZhText("手术诊区");
            this.callFunction("UI|OP_STATION_CODE_I|setVisible", false);
            this.callFunction("UI|OP_STATION_CODE_O|setVisible", true);
        }else if(ADM_TYPE.equals("I")){
            tLabel_9.setZhText("手术病区");
            this.callFunction("UI|OP_STATION_CODE_I|setVisible", true);
            this.callFunction("UI|OP_STATION_CODE_O|setVisible", false);
        }
        pat= Pat.onQueryByMrNo(MR_NO);//add by wanglong 20140411 获得病患信息
        setPatInfo(pat);
    }
    /**
     * 获取手术记录信息并赋值
     */
    private void setOpRecordData(){
        TParm opRecord = new TParm();
        opRecord.setData("OP_RECORD_NO",OP_RECORD_NO);
        DetailData = OPEOpDetailTool.getInstance().selectData(opRecord);
        CASE_NO = DetailData.getValue("CASE_NO",0);
        IPD_NO = DetailData.getValue("IPD_NO",0);
        this.setValue("IPD_NO",DetailData.getValue("IPD_NO",0));
        this.setValue("ADM_TYPE",DetailData.getValue("ADM_TYPE",0));
        this.setValue("OP_DATE",DetailData.getTimestamp("OP_DATE",0));
        this.setValue("OP_END_DATE",DetailData.getTimestamp("OP_END_DATE",0));//手术结束时间   add by wanglong 20121219
        this.setValue("OP_DEPT_CODE",DetailData.getValue("OP_DEPT_CODE",0));
        ADM_TYPE=DetailData.getValue("ADM_TYPE",0);
        if(ADM_TYPE.equals("I"))
            this.setValue("OP_STATION_CODE_I",DetailData.getValue("OP_STATION_CODE",0));
        else if(ADM_TYPE.equals("O")||ADM_TYPE.equals("E"))
            this.setValue("OP_STATION_CODE_O",DetailData.getValue("OP_STATION_CODE",0));
        this.setValue("BED_NO",DetailData.getValue("BED_NO",0));
        this.setValue("ROOM_NO",DetailData.getValue("ROOM_NO",0));
        this.setValue("TYPE_CODE",DetailData.getValue("TYPE_CODE",0));
        this.setValue("RANK_CODE",DetailData.getValue("RANK_CODE",0));
        this.setValue("WAY_CODE",DetailData.getValue("WAY_CODE",0));
        this.setValue("ANA_CODE",DetailData.getValue("ANA_CODE",0));
        this.setValue("URGBLADE_FLG",DetailData.getValue("URGBLADE_FLG",0));
        this.setValue("ASA_CODE",DetailData.getValue("ASA_CODE",0));//麻醉分级   add by wanglong 20121206
        this.setValue("NNIS_CODE",DetailData.getValue("NNIS_CODE",0));//手术风险分级   add by wanglong 20121206
        this.setValue("PART_CODE",DetailData.getValue("PART_CODE",0));//手术部位   add by wanglong 20121206
        this.setValue("ISO_FLG",DetailData.getValue("ISO_FLG",0));//隔离手术标记   add by wanglong 20121206
        this.setValue("MAIN_SURGEON",DetailData.getValue("MAIN_SURGEON",0));
        this.setValue("REAL_AST1",DetailData.getValue("REAL_AST1",0));
        this.setValue("REAL_AST2",DetailData.getValue("REAL_AST2",0));
        this.setValue("REAL_AST3",DetailData.getValue("REAL_AST3",0));
        this.setValue("REAL_AST4",DetailData.getValue("REAL_AST4",0));
        Daily_Table.removeRowAll();
        if(DetailData.getValue("DIAG_CODE1",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index,2,DetailData.getValue("DIAG_CODE1",0));
            Daily_Table.setItem(index,3,DetailData.getValue("DIAG_CODE1",0));
            Daily_Table.setItem(index,1,"Y");
        }
        if(DetailData.getValue("DIAG_CODE2",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index, 2, DetailData.getValue("DIAG_CODE2", 0));
            Daily_Table.setItem(index, 3, DetailData.getValue("DIAG_CODE2", 0));
            Daily_Table.setItem(index,1,"N");
        }
        if(DetailData.getValue("DIAG_CODE3",0).length()>0){
            int index = Daily_Table.addRow();
            Daily_Table.setItem(index, 2, DetailData.getValue("DIAG_CODE3", 0));
            Daily_Table.setItem(index, 3, DetailData.getValue("DIAG_CODE3", 0));
            Daily_Table.setItem(index,1,"N");
        }
        Daily_Table.addRow();
        OP_Table.removeRowAll();
        if(DetailData.getValue("OP_CODE1",0).length()>0){
            int index = OP_Table.addRow();
            OP_Table.setItem(index,2,DetailData.getValue("OP_CODE1",0));
            OP_Table.setItem(index,3,DetailData.getValue("OP_CODE1",0));
            OP_Table.setItem(index,1,"Y");
        }
        if(DetailData.getValue("OP_CODE2",0).length()>0){
            int index = OP_Table.addRow();
            OP_Table.setItem(index,2,DetailData.getValue("OP_CODE2",0));
            OP_Table.setItem(index,3,DetailData.getValue("OP_CODE2",0));
            OP_Table.setItem(index,1,"N");
        }
        OP_Table.addRow();
        int row;
        //体外循环师
        if(DetailData.getValue("EXTRA_USER1",0).length()>0){
            row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,DetailData.getValue("EXTRA_USER1",0));
        }
        if(DetailData.getValue("EXTRA_USER2",0).length()>0){
            row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row,0,DetailData.getValue("EXTRA_USER2",0));
        }
        //循环护士
        if(DetailData.getValue("CIRCULE_USER1",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,DetailData.getValue("CIRCULE_USER1",0));
        }
        if(DetailData.getValue("CIRCULE_USER2",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,DetailData.getValue("CIRCULE_USER2",0));
        }
        if(DetailData.getValue("CIRCULE_USER3",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,DetailData.getValue("CIRCULE_USER3",0));
        }
        if(DetailData.getValue("CIRCULE_USER4",0).length()>0){
            row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row,0,DetailData.getValue("CIRCULE_USER4",0));
        }
        //洗手护士
        if(DetailData.getValue("SCRUB_USER1",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,DetailData.getValue("SCRUB_USER1",0));
        }
        if(DetailData.getValue("SCRUB_USER2",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,DetailData.getValue("SCRUB_USER2",0));
        }
        if(DetailData.getValue("SCRUB_USER3",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,DetailData.getValue("SCRUB_USER3",0));
        }
        if(DetailData.getValue("SCRUB_USER4",0).length()>0){
            row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row,0,DetailData.getValue("SCRUB_USER4",0));
        }
        //麻醉医师
        if(DetailData.getValue("ANA_USER1",0).length()>0){
            row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,0,"Y");
            ANA_TABLE.setItem(row,1,DetailData.getValue("ANA_USER1",0));
        }
        if(DetailData.getValue("ANA_USER2",0).length()>0){
            row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row,0,"N");
            ANA_TABLE.setItem(row,1,DetailData.getValue("ANA_USER2",0));
        }
    }
    /**
     * 体外循环师 添加事件
     */
    public void onEXTRA_ADD() {
        String user_id = this.getValueString("EXTRA_USER");
        if (!checkGrid(EXTRA_TABLE, user_id, 0))
            return;
        if (EXTRA_TABLE.getRowCount() >= 2) {
            this.messageBox("E0084");
            return;
        }
        if (user_id.length() > 0) {
            int row = EXTRA_TABLE.addRow();
            EXTRA_TABLE.setItem(row, 0, user_id);
            this.clearValue("EXTRA_USER");
        }
    }

    /**
     * 体外循环师 删除事件
     */
    public void onEXTRA_DEL() {
        int row = EXTRA_TABLE.getSelectedRow();
        if (row > -1) {
            EXTRA_TABLE.removeRow(row);
        }
    }

    /**
     * 巡回护士 添加事件
     */
    public void onCIRCULE_ADD() {
        String user_id = this.getValueString("CIRCULE_USER");
        if (!checkGrid(CIRCULE_TABLE, user_id, 0))
            return;
        if (CIRCULE_TABLE.getRowCount() >= 4) {
            this.messageBox("E0085");
            return;
        }
        if (user_id.length() > 0) {
            int row = CIRCULE_TABLE.addRow();
            CIRCULE_TABLE.setItem(row, 0, user_id);
            this.clearValue("CIRCULE_USER");
        }
    }

    /**
     * 巡回护士 删除事件
     */
    public void onCIRCULE_DEL() {
        int row = CIRCULE_TABLE.getSelectedRow();
        if (row > -1) {
            CIRCULE_TABLE.removeRow(row);
        }
    }

    /**
     * 洗手护士 添加事件
     */
    public void onSCRUB_ADD() {
        String user_id = this.getValueString("SCRUB_USER");
        if (!checkGrid(SCRUB_TABLE, user_id, 0))
            return;
        if (SCRUB_TABLE.getRowCount() >= 4) {
            this.messageBox("E0086");
            return;
        }
        if (user_id.length() > 0) {
            int row = SCRUB_TABLE.addRow();
            SCRUB_TABLE.setItem(row, 0, user_id);
            this.clearValue("SCRUB_USER");
        }
    }

    /**
     * 洗手护士 删除事件
     */
    public void onSCRUB_DEL() {
        int row = SCRUB_TABLE.getSelectedRow();
        if (row > -1) {
            SCRUB_TABLE.removeRow(row);
        }
    }

    /**
     * 麻醉医师 添加事件
     */
    public void onANA_ADD() {
        String user_id = this.getValueString("ANA_USER");
        if (!checkGrid(ANA_TABLE, user_id, 1))
            return;
        if (ANA_TABLE.getRowCount() >= 2) {
            this.messageBox("E0087");
            return;
        }
        if (user_id.length() > 0) {
            int row = ANA_TABLE.addRow();
            ANA_TABLE.setItem(row, 1, user_id);
            ANA_TABLE.setItem(row, 0, "N");
            this.clearValue("ANA_USER");
        }
    }

    /**
     * 麻醉医师 删除事件
     */
    public void onANA_DEL() {
        int row = ANA_TABLE.getSelectedRow();
        if (row > -1) {
            ANA_TABLE.removeRow(row);
        }
    }

    /**
     * 检查Grid中是否有重复的人员
     * @param table TTable
     * @param user_id String
     * @return boolean
     */
    private boolean checkGrid(TTable table, String user_id, int column) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (user_id.equals(table.getValueAt(i, column).toString())) {
                this.messageBox("E0088");
                return false;
            }
        }
        return true;
    }

    /**
     * 获取体外循环师Grid数据
     * @return TParm
     */
    private TParm getEXTRAData() {
        TParm parm = new TParm();
        for (int i = 0; i < EXTRA_TABLE.getRowCount(); i++) {
            if (EXTRA_TABLE.getValueAt(i, 0).toString().trim().length() > 0) {
                parm.setData("EXTRA_USER" + (i + 1),
                             EXTRA_TABLE.getValueAt(i, 0));
            }
        }
        return parm;
    }

    /**
     * 获取流动护士Grid数据
     * @return TParm
     */
    private TParm getCIRCULEData() {
        TParm parm = new TParm();
        for (int i = 0; i < CIRCULE_TABLE.getRowCount(); i++) {
            if (CIRCULE_TABLE.getValueAt(i, 0).toString().trim().length() > 0) {
                parm.setData("CIRCULE_USER" + (i + 1),
                             CIRCULE_TABLE.getValueAt(i, 0));
            }
        }
        return parm;
    }

    /**
     * 获取洗手护士Grid数据
     * @return TParm
     */
    private TParm getSCRUBData() {
        TParm parm = new TParm();
        for (int i = 0; i < SCRUB_TABLE.getRowCount(); i++) {
            if (SCRUB_TABLE.getValueAt(i, 0).toString().trim().length() > 0) {
                parm.setData("SCRUB_USER" + (i + 1),
                             SCRUB_TABLE.getValueAt(i, 0));
            }
        }
        return parm;
    }

    /**
     * 获取麻醉医师Grid数据
     * @return TParm
     */
    private TParm getANAData() {
        TParm parm = new TParm();
        int index = 2;
        for (int i = 0; i < ANA_TABLE.getRowCount(); i++) {
            if (ANA_TABLE.getValueAt(i, 1).toString().trim().length() > 0) {
                if ("Y".equals(ANA_TABLE.getValueAt(i, 0).toString().trim()))
                    parm.setData("ANA_USER1", ANA_TABLE.getValueAt(i, 1));
                else
                    parm.setData("ANA_USER" + index, ANA_TABLE.getValueAt(i, 1));
            }
        }
        return parm;
    }

    /**
     * 麻醉医师 主标识点击事件
     */
    public void onANATableMainCharge(Object obj) {
        ANA_TABLE.acceptText();
        if (ANA_TABLE.getSelectedColumn() == 0) {
            int row = ANA_TABLE.getSelectedRow();
            for (int i = 0; i < ANA_TABLE.getRowCount(); i++) {
                ANA_TABLE.setItem(i, 0, "N");
            }
            ANA_TABLE.setItem(row, 0, "Y");
        }
    }

    /**
     *手术弹出界面 OpICD
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComponentOP(Component com, int row, int column) {
        //弹出ICD10对话框的列
        if (column != 2)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //给table上的新text增加ICD10弹出窗口
        textfield.setPopupMenuParameter("OP_ICD",
                                        getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSOpICD.x"));
        //给新text增加接受ICD10弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newOPOrder");
    }

    /**
     * 取得手术ICD返回值
     * @param tag String
     * @param obj Object
     */
    public void newOPOrder(String tag, Object obj) {
        TTable table = (TTable)this.callFunction("UI|OP_Table|getThis");
        //sysfee返回的数据包
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("OPERATION_ICD");
        table.setItem(table.getSelectedRow(), "OP_ICD", orderCode);
        if("en".equals(this.getLanguage()))
            table.setItem(table.getSelectedRow(), "OP_DESC",
                          parm.getValue("OPT_ENG_DESC"));
        else
            table.setItem(table.getSelectedRow(), "OP_DESC",
                          parm.getValue("OPT_CHN_DESC"));
    }

    /**
     *诊断弹出界面 ICD10
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComponent(Component com, int row, int column) {
        //弹出ICD10对话框的列
        if (column != 2)
            return;
        if (! (com instanceof TTextField))
            return;
        TTextField textfield = (TTextField) com;
        textfield.onInit();
        //给table上的新text增加ICD10弹出窗口
        textfield.setPopupMenuParameter("ICD10",
                                        getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSICDPopup.x"));
        //给新text增加接受ICD10弹出窗口的回传值
        textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                   "newAgentOrder");
    }

    /**
     * 取得ICD10返回值
     * @param tag String
     * @param obj Object
     */
    public void newAgentOrder(String tag, Object obj) {
        TTable table = (TTable)this.callFunction("UI|Daily_Table|getThis");
        //sysfee返回的数据包
        TParm parm = (TParm) obj;
        String orderCode = parm.getValue("ICD_CODE");
        table.setItem(table.getSelectedRow(), "DAILY_CODE", orderCode);
        if("en".equals(this.getLanguage()))
            table.setItem(table.getSelectedRow(), "DAILY_DESC",
                          parm.getValue("ICD_ENG_DESC"));
        else
            table.setItem(table.getSelectedRow(), "DAILY_DESC",
                          parm.getValue("ICD_CHN_DESC"));
    }

    /**
     * 诊断Grid 值改变事件
     * @param obj Object
     */
    public void onDiagTableValueCharge(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node.getColumn() == 2) {
            if (node.getRow() == (Daily_Table.getRowCount() - 1))
                Daily_Table.addRow();
        }
    }

    /**
     * 手术Grid 值改变事件
     * @param obj Object
     */
    public void onOpTableValueCharge(Object obj) {
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node.getColumn() == 2) {
            if (node.getRow() == (OP_Table.getRowCount() - 1))
                OP_Table.addRow();
        }
    }

    /**
     * 诊断Grid 主诊断标记修改事件
     * @param obj Object
     */
    public void onDiagTableMainCharge(Object obj) {
        Daily_Table.acceptText();
        if (Daily_Table.getSelectedColumn() == 1) {
            int row = Daily_Table.getSelectedRow();
            for (int i = 0; i < Daily_Table.getRowCount(); i++) {
                Daily_Table.setItem(i, "MAIN_FLG", "N");
            }
            Daily_Table.setItem(row, "MAIN_FLG", "Y");
        }
    }

    /**
     * 诊断Grid 主诊断标记修改事件
     * @param obj Object
     */
    public void onOpTableMainCharge(Object obj) {
        OP_Table.acceptText();
        if (OP_Table.getSelectedColumn() == 1) {
            int row = OP_Table.getSelectedRow();
            for (int i = 0; i < OP_Table.getRowCount(); i++) {
                OP_Table.setItem(i, "MAIN_FLG", "N");
            }
            OP_Table.setItem(row, "MAIN_FLG", "Y");
        }
    }
    
    /**
     * 选择科常用手术
     */
    public void onDeptOp(){
        String dept_code = this.getValueString("OP_DEPT_CODE");
        if(dept_code.length()<=0){
            this.messageBox("E0077");
            return;
        }
        String op_icd = (String)this.openDialog("%ROOT%/config/ope/OPEDeptOpShow.x",dept_code);
        if(op_icd==null||op_icd.length()<=0){
            return;
        }
        //将回传值 显示在表格上
        OP_Table.setValueAt_(op_icd, OP_Table.getRowCount() - 1, 2);
        OP_Table.setValueAt_(op_icd, OP_Table.getRowCount() - 1, 3);
        OP_Table.addRow();
    }
    
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("MR_NO;IPD_NO;PAT_NAME;AGE;SEX;Weight;OPBOOK_NO;ADM_TYPE;OP_DATE;OP_END_DATE;OP_DEPT_CODE;OP_STATION_CODE_I;OP_STATION_CODE_O;");//modify by wanglong 20130514
        this.clearValue("BED_NO;ROOM_NO;TYPE_CODE;RANK_CODE;ANA_CODE;WAY_CODE;URGBLADE_FLG;MAIN_SURGEON;");
        this.clearValue("REAL_AST1;REAL_AST2;REAL_AST3;REAL_AST4;ASA_CODE;NNIS_CODE;PART_CODE;ISO_FLG");//modify by wanglong 20121212
        EXTRA_TABLE.removeRowAll();
        CIRCULE_TABLE.removeRowAll();
        SCRUB_TABLE.removeRowAll();
        ANA_TABLE.removeRowAll();
        Daily_Table.removeRowAll();
        OP_Table.removeRowAll();
        Daily_Table.addRow();
        OP_Table.addRow();
        callFunction("UI|MR_NO|setEnabled", true);
        callFunction("UI|OPBOOK_NO|setEnabled", true);
    }
    
    /**
     * 诊断CODE替换中文 模糊查询（内部类）
     */
    public class OrderList
        extends TLabel {
        TDataStore dataStore = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("ICD_CODE");
            Vector d = (Vector) parm.getData("ICD_CHN_DESC");
            Vector e = (Vector) parm.getData("ICD_ENG_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i))){
                    if ("en".equals(OPEOpDetailControl.this.getLanguage())) {
                        return "" + e.get(i);
                    }
                    else {
                        return "" + d.get(i);
                    }
                }
            }
            return s;
        }
    }

    /**
     * 手术CODE替换中文 模糊查询（内部类）
     */
    public class OpList
        extends TLabel {
        TDataStore dataStore = new TDataStore();
        public OpList(){
            dataStore.setSQL("select * from SYS_OPERATIONICD");
            dataStore.retrieve();
        }
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("OPERATION_ICD");
            Vector d = (Vector) parm.getData("OPT_CHN_DESC");
            Vector e = (Vector) parm.getData("OPT_ENG_DESC");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i))){
                    if ("en".equals(OPEOpDetailControl.this.getLanguage())) {
                        return "" + e.get(i);
                    }
                    else {
                        return "" + d.get(i);
                    }
                }
            }
            return s;
        }
    }
    
    /**
     * 手术科室选择事件
     */
    public void onOP_DEPT_CODE(){
        this.clearValue("OP_STATION_CODE;BED_NO;MAIN_SURGEON");
    }
    /**
     * 病区选择事件
     */
    public void onOP_STATION_CODE(){
        this.clearValue("BED_NO");
    }
    
    /**
     * 调用结构化病历 填写手术记录
     */
    public void onEmr(){//此方法不再使用 by wanglong 20121220
        if(OP_RECORD_NO.length()<=0){
            return;
        }
        TParm opParm = new TParm();
        opParm.setData("OP_RECORD_NO",OP_RECORD_NO);
        TParm emrData = OPEOpDetailTool.getInstance().selectData(opParm);
        TParm parm = new TParm();
        TParm emrParm = new TParm();
        emrParm.setData("MR_CODE", TConfig.getSystemValue("OPEEmrMRCODE"));//获取手术记录模版的ID
        emrParm.setData("CASE_NO", emrData.getValue("CASE_NO",0));
        emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
        String adm_type = emrData.getValue("ADM_TYPE",0);
        String SYSTEM_TYPE = "";
        if("O".equals(adm_type)){
            SYSTEM_TYPE = "ODO";
        }else if("E".equals(adm_type)){
            SYSTEM_TYPE = "EMG";
        }else if("I".equals(adm_type)){
            SYSTEM_TYPE = "ODI";
        }else if("H".equals(adm_type)){
            SYSTEM_TYPE = "HRM";
        }
        parm.setData("SYSTEM_TYPE", SYSTEM_TYPE);
        parm.setData("ADM_TYPE", adm_type);
        parm.setData("STYLETYPE","1");//样式
        parm.setData("CASE_NO", emrData.getValue("CASE_NO",0));
        parm.setData("MR_NO", emrData.getValue("MR_NO",0));
        parm.setData("IPD_NO", emrData.getValue("IPD_NO",0));
        parm.setData("EMR_FILE_DATA", emrParm);
        parm.setData("RULETYPE","2");//修改权限
        parm.addListener("EMR_LISTENER",this,"emrListener");
        parm.addListener("EMR_SAVE_LISTENER",this,"emrSaveListener");
       //System.out.println("========onEmr parm========="+parm);
        this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }
    
    /**
     * EMR监听
     * @param parm TParm
     */
    public void emrListener(TParm parm){//modify by wanglong 20121220
        NameList nl = new NameList();
		eventParmEmr = parm;
		TParm data = OPEOpDetailTool.getInstance().selectForEmr(OP_RECORD_NO);
		parm.runListener("setMicroData", "科室", data.getValue("DEPT_DESC", 0));// 科室
		parm.runListener("setMicroData", "病区", data.getValue("STATION_DESC", 0));// 病区
		parm.runListener("setMicroData", "手术日期", data.getValue("OP_DATE", 0));// 手术日期
		parm.runListener("setMicroData", "手术开始时间", data.getValue("OP_START_DATE", 0));// 手术开始时间
		parm.runListener("setMicroData", "手术结束时间", data.getValue("OP_END_DATE", 0));// 手术结束时间
		parm.runListener("setMicroData", "诊断名称", data.getValue("ICD_DESC", 0));// 诊断名称
		parm.runListener("setMicroData", "手术名称", data.getValue("OPT_DESC", 0));// 手术名称
		parm.runListener("setMicroData", "主刀医生", data.getValue("SURGEON_USER", 0));// 主刀医生
		parm.runListener("setMicroData", "一助", data.getValue("AST_USER1", 0));// 一助
		parm.runListener("setMicroData", "二助", data.getValue("AST_USER2", 0));// 二助
		parm.runListener("setMicroData", "三助", data.getValue("AST_USER3", 0));// 三助
		parm.runListener("setMicroData", "四助", data.getValue("AST_USER4", 0));// 四助
		parm.runListener("setMicroData", "麻醉医师", data.getValue("ANA_USER", 0));// 麻醉医师
		parm.runListener("setMicroData", "麻醉方式", data.getValue("ANA_DESC", 0));// 麻醉方式
		parm.runListener("setMicroData", "麻醉分级", data.getValue("ASA_DESC", 0));// 麻醉分级
		parm.runListener("setMicroData", "手术部位", data.getValue("PART_DESC", 0));// 手术部位
    }
    
    /**
     * EMR保存监听 取结构化病历中填写的值
     * @param parm TParm
     */
    public void emrSaveListener(TParm parm){
        List name = new ArrayList();//将取值控件的名字以List的形式 传入
        name.add("weight");
        name.add("height");
        //调用EMR中的取值方法， 返回Object数值
        Object[] obj  = (Object[])eventParmEmr.runListener("getCaptureValueArray",name);
        //队返回值进行操作
    }
    /**
     * 模糊查询（内部类） 用户姓名替换
     */
    public class NameList extends TLabel {
        TDataStore dataStore;
        public NameList(){
            dataStore = new TDataStore();
            dataStore.setSQL("SELECT USER_ID,USER_NAME FROM SYS_OPERATOR");
            dataStore.retrieve();
        }
        public String getTableShowValue(String s) {
            if (dataStore == null)
                return s;
            String bufferString = dataStore.isFilter() ? dataStore.FILTER :
                dataStore.PRIMARY;
            TParm parm = dataStore.getBuffer(bufferString);
            Vector v = (Vector) parm.getData("USER_ID");
            Vector d = (Vector) parm.getData("USER_NAME");
            int count = v.size();
            for (int i = 0; i < count; i++) {
                if (s.equals(v.get(i)))
                    return "" + d.get(i);
            }
            return s;
        }
    }
    
    /**
     * 打印
     */
	public void onPrint() {// 此方法暂时并没有被使用  by wanglong 20121220
        if(DetailData==null||OP_RECORD_NO.length()<=0){
            return;
        }
        OrderList ol = new OrderList();
        OpList op = new OpList();
        NameList nl = new NameList();
        Pat pat = Pat.onQueryByMrNo(DetailData.getValue("MR_NO",0));
        TParm printData = new TParm();
        printData.setData("OP_RECORD_NO","TEXT",DetailData.getValue("OPBOOK_NO",0));
        printData.setData("PAT_NAME","TEXT",pat.getName());
        printData.setData("OP_DATE","TEXT",StringTool.getString(DetailData.getTimestamp("OP_DATE",0),"yyyy年MM月dd日"));
        printData.setData("SEX","TEXT",pat.getSexCode());
        printData.setData("AGE","TEXT",StringUtil.showAge(pat.getBirthday(),DetailData.getTimestamp("OP_DATE",0)));
        printData.setData("WEIGHT","TEXT",pat.getWeight()+" kg");
        printData.setData("DIAG_CODE1","TEXT",ol.getTableShowValue(DetailData.getValue("DIAG_CODE1",0)));
        printData.setData("DIAG_CODE2","TEXT",ol.getTableShowValue(DetailData.getValue("DIAG_CODE2",0)));
        printData.setData("DIAG_CODE3","TEXT",ol.getTableShowValue(DetailData.getValue("DIAG_CODE3",0)));
        printData.setData("OP_CODE1","TEXT",op.getTableShowValue(DetailData.getValue("OP_CODE1",0)));
        printData.setData("MAIN_SURGEON","TEXT",nl.getTableShowValue(DetailData.getValue("MAIN_SURGEON",0)));
        printData.setData("REAL_AST1","TEXT",nl.getTableShowValue(DetailData.getValue("REAL_AST1",0)));
        printData.setData("REAL_AST2","TEXT",nl.getTableShowValue(DetailData.getValue("REAL_AST2",0)));
        printData.setData("REAL_AST3","TEXT",nl.getTableShowValue(DetailData.getValue("REAL_AST3",0)));
        printData.setData("REAL_AST4","TEXT",nl.getTableShowValue(DetailData.getValue("REAL_AST4",0)));
        this.openPrintDialog("%ROOT%\\config\\prt\\OPE\\OPEDetail.jhw",printData);
    }
	
    /**
     * 手术模板创建
     * =============pangben modify 20110701
     */
    public void onOpstmp(){
        if(null==pat || MR_NO==null || this.getValueString("MR_NO").equals("")){//modify by wanglong 20130514
            this.messageBox("请输入病案号");
            return ;
        }
        TParm opParm = new TParm();
        opParm.setData("CASE_NO",CASE_NO);
        opParm.setData("REGION_CODE",Operator.getRegion());
        TParm parm = new TParm();
        String SYSTEM_TYPE="";
        if("O".equals(ADM_TYPE)){
            SYSTEM_TYPE = "ODO";
        }else if("E".equals(ADM_TYPE)){
            SYSTEM_TYPE = "EMG";
        }else if("I".equals(ADM_TYPE)){
            SYSTEM_TYPE = "ODI";
        }else if("H".equals(ADM_TYPE)){
            SYSTEM_TYPE = "HRM";
        }
        TParm result = ADMInpTool.getInstance().selectall(opParm);//住院信息
        //parm.setData("SYSTEM_TYPE", SYSTEM_TYPE);//delete by wanglong 20121220
        parm.setData("SYSTEM_TYPE", "OPE");//add by wanglong 20121220
        parm.setData("ADM_TYPE", ADM_TYPE);
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("PAT_NAME", pat.getName());
        parm.setData("MR_NO", MR_NO);
        parm.setData("IPD_NO", IPD_NO);
        parm.setData("ADM_DATE", result.getTimestamp("ADM_DATE",0));
        parm.setData("DEPT_CODE", this.getValue("OP_DEPT_CODE"));
        parm.setData("STATION_CODE", this.getValue("OP_STATION_CODE_I"));
//       if (this.isOidrFlg()) {
//           parm.setData("RULETYPE", "3");
//           //写入类型(会诊)
//           parm.setData("WRITE_TYPE","OIDR");
//       }else {
           parm.setData("RULETYPE", "2");
           //写入类型(会诊)
           parm.setData("WRITE_TYPE","");
 //      }
        String opBookNo = this.getValueString("OPBOOK_NO");//add by wanglong 20130608
        if (StringUtil.isNullString(opBookNo)) {
            return;
        }
        String opRecordNo = OPEOpDetailTool.getInstance().selectForEmrByBookNo(opBookNo);
        if (!StringUtil.isNullString(opRecordNo)) {// 对于手术记录传参，对于手术申请不传参 modify by wanglong 20130608
            result = OPEOpDetailTool.getInstance().selectForEmr(OP_RECORD_NO);
            TParm action = new TParm();
            action.setData("科室", result.getValue("DEPT_DESC", 0));// 科室
            action.setData("病区", result.getValue("STATION_DESC", 0));// 病区
            action.setData("手术日期", result.getValue("OP_DATE", 0));// 手术日期
            if (StringUtil.isNullString(result.getValue("OP_START_DATE", 0))) {
                action.setData("手术开始时间", result.getValue("OP_DATE", 0));// 手术开始时间
            } else {
                action.setData("手术开始时间", result.getValue("OP_START_DATE", 0));// 手术开始时间
            }
            action.setData("手术结束时间", result.getValue("OP_END_DATE", 0));// 手术结束时间
            action.setData("诊断名称", result.getValue("ICD_DESC", 0));// 诊断名称
            action.setData("术前诊断", result.getValue("ICD_DESC", 0));// 术前诊断
            action.setData("手术名称", result.getValue("OPT_DESC", 0));// 手术名称
            action.setData("手术类型", result.getValue("TYPE_DESC", 0));// 手术类型
            action.setData("主刀医生", result.getValue("SURGEON_USER", 0));// 主刀医生
            action.setData("主刀医师", result.getValue("SURGEON_USER", 0));// 主刀医生
            action.setData("一助", result.getValue("AST_USER1", 0));// 一助
            action.setData("二助", result.getValue("AST_USER2", 0));// 二助
            action.setData("三助", result.getValue("AST_USER3", 0));// 三助
            action.setData("四助", result.getValue("AST_USER4", 0));// 四助
            action.setData("麻醉医师", result.getValue("ANA_USER", 0));// 麻醉医师
            action.setData("麻醉方式", result.getValue("ANA_DESC", 0));// 麻醉方式
            action.setData("麻醉分级", result.getValue("ASA_DESC", 0));// 麻醉分级
            action.setData("手术部位", result.getValue("PART_DESC", 0));// 手术部位
            parm.setData("OPE_DATA", action);
        }
        parm.setData("TYPE", "F");// 不为F，则可以显示该病患每次就诊的手术记录；为F，则只显示本次就诊时的手术记录 add by wanglong 20121220
        parm.setData("EMR_DATA_LIST", new TParm());
        parm.addListener("EMR_LISTENER", this, "emrListener");
        parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
        this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }
    
    /**
     * 设置界面上所有输入组件都不可编辑
     */
	public void setUIFalse() {// add by wanglong 20121219
		((TTextField) this.getComponent("MR_NO")).setEnabled(false);
		((TTextField) this.getComponent("OPBOOK_NO")).setEnabled(false);
		((TComboBox) this.getComponent("ADM_TYPE")).setEnabled(false);
		((TTextFormat) this.getComponent("OP_DATE")).setEnabled(false);
		((TTextFormat) this.getComponent("OP_END_DATE")).setEnabled(false);
		((TTextFormat) this.getComponent("OP_DEPT_CODE")).setEnabled(false);
		((TTextFormat) this.getComponent("OP_STATION_CODE_I")).setEnabled(false);
		((TTextFormat) this.getComponent("OP_STATION_CODE_O")).setEnabled(false);
		((TTextFormat) this.getComponent("BED_NO")).setEnabled(false);
		((TTextFormat) this.getComponent("ROOM_NO")).setEnabled(false);
		((TComboBox) this.getComponent("TYPE_CODE")).setEnabled(false);
		((TComboBox) this.getComponent("RANK_CODE")).setEnabled(false);
		((TComboBox) this.getComponent("WAY_CODE")).setEnabled(false);
		((TComboBox) this.getComponent("ANA_CODE")).setEnabled(false);
		((TCheckBox) this.getComponent("URGBLADE_FLG")).setEnabled(false);
		((TTextFormat) this.getComponent("ASA_CODE")).setEnabled(false);
		((TTextFormat) this.getComponent("NNIS_CODE")).setEnabled(false);
		((TTextFormat) this.getComponent("PART_CODE")).setEnabled(false);
		((TCheckBox) this.getComponent("ISO_FLG")).setEnabled(false);
		((TTextFormat) this.getComponent("MAIN_SURGEON")).setEnabled(false);
		((TTextFormat) this.getComponent("REAL_AST1")).setEnabled(false);
		((TTextFormat) this.getComponent("REAL_AST2")).setEnabled(false);
		((TTextFormat) this.getComponent("REAL_AST3")).setEnabled(false);
		((TTextFormat) this.getComponent("REAL_AST4")).setEnabled(false);
		((TTable) this.getComponent("Daily_Table")).setEnabled(false);
		((TButton) this.getComponent("SelectDEPT_OP")).setEnabled(false);
		((TTable) this.getComponent("OP_Table")).setEnabled(false);
		((TTable) this.getComponent("EXTRA_TABLE")).setEnabled(false);
		((TTextFormat) this.getComponent("EXTRA_USER")).setEnabled(false);
		((TButton) this.getComponent("EXTRA_ADD")).setEnabled(false);
		((TButton) this.getComponent("EXTRA_DEL")).setEnabled(false);
		((TTable) this.getComponent("CIRCULE_TABLE")).setEnabled(false);
		((TTextFormat) this.getComponent("CIRCULE_USER")).setEnabled(false);
		((TButton) this.getComponent("CIRCULE_ADD")).setEnabled(false);
		((TButton) this.getComponent("CIRCULE_DEL")).setEnabled(false);
		((TTable) this.getComponent("SCRUB_TABLE")).setEnabled(false);
		((TTextFormat) this.getComponent("SCRUB_USER")).setEnabled(false);
		((TButton) this.getComponent("SCRUB_ADD")).setEnabled(false);
		((TButton) this.getComponent("SCRUB_DEL")).setEnabled(false);
		((TTable) this.getComponent("ANA_TABLE")).setEnabled(false);
		((TTextFormat) this.getComponent("ANA_USER")).setEnabled(false);
		((TButton) this.getComponent("ANA_ADD")).setEnabled(false);
		((TButton) this.getComponent("ANA_DEL")).setEnabled(false);
		((TMenuItem) this.getComponent("save")).setEnabled(false);
		((TMenuItem) this.getComponent("clear")).setEnabled(false);
//		((TMenuItem) this.getComponent("emr")).setEnabled(false);//delete by wanglong 20140411
		((TMenuItem) this.getComponent("opstmp")).setEnabled(false);
	}
	
}
