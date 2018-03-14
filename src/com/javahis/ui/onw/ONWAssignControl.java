package com.javahis.ui.onw;

import com.dongyang.data.TParm;
import com.dongyang.control.TControl;
import jdo.reg.PatAdmTool;
import jdo.sys.Operator;
import com.dongyang.manager.TCM_Transform;
import jdo.bil.BIL;
import jdo.reg.SessionTool;
import jdo.sys.SystemTool;
import jdo.reg.SchDayTool;
import com.dongyang.ui.TComboBox;
import jdo.device.CallNo;
import com.dongyang.util.StringTool;
import jdo.sys.Pat;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.ui.reg.REGPatAdmControl;

import jdo.reg.Reg;

/**
 * <p>Title:分诊 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:JavaHis </p>
 *
 * @author JiaoY
 * @version 1.0
 */
public class ONWAssignControl extends TControl {
    TParm recptype = new TParm(); //接参
    double regFee;
    String receiptType = "REG_FEE";
    private String admType = "O";
    String defSession = "";//记录当前时间属于哪个挂号时段
    String oldSession ="";//记录病患挂号的时段 用于比较是否是分诊到同时段
    String service_level = "";//服务等级
    public void onInit() {
        super.onInit();
        init();
        callFunction("UI|SESSION_CODE|onQuery");
        setValue("REGION_CODE", Operator.getRegion());
        //接受参数
        recptype = (TParm)this.getParameter();
        setValue("ADM_DATE", SystemTool.getInstance().getDate());
        setValue("ADM_TYPE", recptype.getData("ADM_TYPE"));
        oldSession = recptype.getValue("SESSION_CODE");
        admType = recptype.getValue("ADM_TYPE");
        defSession = SessionTool.getInstance().getDefSessionNow(admType,Operator.getRegion());
        setValue("SESSION_CODE", defSession);
        callFunction("UI|SESSION_CODE|onQuery");
        callFunction("UI|REALDEPT_CODE|onQuery");
        callFunction("UI|CLINICROOM_NO|onQuery");
        setValue("CLINICTYPE_CODE", recptype.getData("CLINICTYPE_CODE"));
        setValue("MR_NO", recptype.getData("MR_NO"));
        setValue("CASE_NO", recptype.getData("CASE_NO"));
        setValue("PAT_NAME", recptype.getData("PAT_NAME"));
        setValue("DEPT_CODE", recptype.getData("REALDEPT_CODE"));
        setValue("DR_CODE", recptype.getData("REALDR_CODE"));
        setValue("CTZ1_CODE",recptype.getData("CTZ1_CODE"));
        service_level = recptype.getValue("SERVICE_LEVEL");
        onCLINICTYPE_Chg();//根据号别 刷新其他combo
        regFee = BIL.getRegFee(admType,recptype.getValue("CLINICTYPE_CODE"),service_level);
    }
    /**
     * 保存
     */
    public void onSave() {
        if (this.getValue("REALDR_CODE").toString().equals("")) {
            this.messageBox_("请选择医师");
            return;
        }
        if(this.getValueString("SESSION_CODE").equals("")){
            this.messageBox_("请选择时段");
            return;
        }
        if(this.getValueString("REALDEPT_CODE").equals("")){
            this.messageBox_("请选择科别");
            return;
        }
        if(this.getValueString("CLINICROOM_NO").equals("")){
            this.messageBox_("请选择诊室");
            return;
        }
        if(!oldSession.equals(this.getValueString("SESSION_CODE"))){
            if(this.messageBox("提示","不同时段是否进行转诊？",0)==1){
                return;
            }
        }
        if(this.getValueString("REALDR_CODE").equals(this.getValueString("DR_CODE"))){
            this.messageBox_("不可分诊到同一个医师");
            return;
        }
        //判断费用 是否高于原有号别的费用
        int re = this.onCLINICTYPE_CODE();
        if (re==2) {
            this.messageBox_("号别费用高于原号别费用，不可分诊！");
            return;
        }else if(re==1){
            if(this.messageBox("提示","号别费用低于原号别费用，要进行分诊操作吗?",0)==1){
                return;
            }
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", getValue("CASE_NO"));
        parm.setDataN("SESSION_CODE",getValue("SESSION_CODE"));
        parm.setData("REALDEPT_CODE", getValue("REALDEPT_CODE"));
        parm.setData("REALDR_CODE", getValue("REALDR_CODE"));
        parm.setData("CLINICROOM_NO", getValue("CLINICROOM_NO"));
        parm.setData("ADM_TYPE",admType);
        parm.setData("ADM_DATE",this.getValue("ADM_DATE"));
        parm.setData("QUE_NO",recptype.getValue("QUE_NO"));
        parm.setData("REGION_CODE",Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm dataD = TIOM_AppServer.executeAction(
            "action.onw.ONWAssignAction",
            "onAssign", parm); //分诊
        if (dataD.getErrCode() < 0) {
            this.messageBox("E0005");
        } else {
            this.messageBox("P0005");
            /** 调用叫号系统（需确认转诊向叫号系统发什么消息格式） add by duzhw start */
            REGPatAdmControl regPatAdm = new REGPatAdmControl();
            String result = regPatAdm.callNo("UNREG", getValue("CASE_NO").toString());
            
            if("true".equals(result)){
            	/** add by duzhw end */
            	try{
                    onCall_T(); //调用排队叫号 接口 退号
                    onCall_G(); //调用排队叫号 接口 挂号
                }catch(Exception e){

                }finally{
                callFunction("UI|onClose");
                }
            }
            
        }
    }

    /**
     * 判断号别费用
     * @return int  0:可以转诊 号别和原号别费用相同;
     *              1:可以转诊 新号别费用低于原号别费用 需要提示;
     *              2:不可转诊，新号别费用高于原号别费用 禁止低费用向高费用转诊
     */
    public int onCLINICTYPE_CODE() {
        double reg_fee = BIL.getRegFee(admType,
                                       TCM_Transform.getString(getValue("CLINICTYPE_CODE")),service_level);
        if (reg_fee < 0)
            return 0;
        if (reg_fee > regFee) //新号别费用高于原号别费用
            return 2;
        else if (reg_fee < regFee)//新号别费用低于原号别费用
            return 1;
        else
            return 0;
    }

    /**
     * 通过号别筛选医师
     */
    public void onDr_code() {
        TParm parm = new TParm();
        parm.setData("SESSION_CODE", getValue("SESSION_CODE"));
        parm.setData("ADM_DATE", getValue("ADM_DATE"));
        TParm dataD = SchDayTool.getInstance().selDrByClinicType(parm);
        setValue("REALDR_CODE",dataD);
    }
    /**
     * 时段改变 清空诊室和科别的选中项
     */
    public void onSESSION_CODE(){
        this.clearValue("CLINICROOM_NO;REALDEPT_CODE");
    }
    /**
     * 号别改变 清空医生Cobom的选中值
     */
    public void onCLINICTYPE_Chg(){
        //清空医师
        this.clearValue("REALDR_CODE");
        //筛选该诊室的医师
        this.callFunction("UI|REALDR_CODE|onQuery");
    }
    /**
     * 诊室改变 修改医师combo的选中值
     */
    public void onCLINICROOM_NO_Chg(){
        if(this.getValueString("CLINICROOM_NO").length()<=0){
            return;
        }
        //清空医师
        this.clearValue("REALDR_CODE");
        //筛选该诊室的医师
        this.callFunction("UI|REALDR_CODE|onQuery");
        TComboBox Dr = (TComboBox)this.getComponent("REALDR_CODE");
        //如果医师Combo选项多余1个标示 此诊间有看诊医生 默认选择该看诊医生
        if(Dr.getItemCount()>1){
            Dr.setSelectedIndex(1);
            //查询日班表 带出给号组别
            TParm parm = new TParm();
            parm.setData("ADM_DATE", recptype.getData("ADM_DATE"));
            parm.setData("REGION_CODE", recptype.getValue("REGION_CODE"));
            parm.setData("ADM_TYPE", recptype.getValue("ADM_TYPE"));
            parm.setData("SESSION_CODE", getValue("SESSION_CODE"));
            parm.setData("DR_CODE", getValue("REALDR_CODE"));
            TParm schDay = SchDayTool.getInstance().selectdata(parm); //查询日班表
            this.setValue("CLINICROOM_NO", schDay.getValue("CLINICROOM_NO", 0));
            this.setValue("REALDEPT_CODE", schDay.getValue("DEPT_CODE", 0));
        }
    }
    /**
     * 转入医师改变事件
     */
    public void onREALDR_CODE_Chg(){
        if(this.getValueString("REALDR_CODE").length()<=0){
            return;
        }
        //根据用户ID 查询日班表 带出诊间
        //查询日班表 带出给号组别
        TParm parm = new TParm();
        parm.setData("ADM_DATE", recptype.getData("ADM_DATE"));
        parm.setData("REGION_CODE",recptype.getValue("REGION_CODE"));
        parm.setData("ADM_TYPE",recptype.getValue("ADM_TYPE"));
        parm.setData("SESSION_CODE",getValue("SESSION_CODE"));
        parm.setData("DR_CODE",getValue("REALDR_CODE"));
        TParm schDay = SchDayTool.getInstance().selectdata(parm); //查询日班表
        this.setValue("CLINICROOM_NO",schDay.getValue("CLINICROOM_NO",0));
        this.setValue("REALDEPT_CODE",schDay.getValue("DEPT_CODE",0));
    }
    /**
     * 清除
     */
    public void onClear(){
        setValue("SESSION_CODE", defSession);
        this.clearValue("CLINICTYPE_CODE;CLINICROOM_NO;REALDEPT_CODE;REALDR_CODE");
    }
//    /**
//     * 科室列表初始化
//     */
//    private void deptComboInit(){
//        String sql =
//            " SELECT DISTINCT A.DEPT_CODE AS ID,B.DEPT_ABS_DESC AS NAME, B.PY1 AS PY1, B.PY2 AS PY2 " +
//            "   FROM REG_SCHDAY A,SYS_DEPT B " +
//            "  WHERE B.ACTIVE_FLG='Y' " +
//            "    AND A.DEPT_CODE = B.DEPT_CODE ";
//        String sql1 = " ORDER BY A.DEPT_CODE ";
//
//        StringBuffer sb = new StringBuffer();
//
//        String regionCode = this.getValueString("REGION_CODE");
//        if (regionCode != null && regionCode.length() > 0)
//            sb.append(" AND A.REGION_CODE = '" + regionCode + "' ");
//        String admType = this.getValueString("ADM_TYPE");
//        if (admType != null && admType.length() > 0) {
//            sb.append(" AND A.ADM_TYPE = '" + admType + "' ");
//        }
//        Object value = this.getValue("ADM_DATE");
//        String admDate = "";
//        if (value instanceof Timestamp)
//            admDate = StringTool.getString(TCM_Transform.getTimestamp(value),
//                                           "yyyyMMdd");
//        if (admDate != null && admDate.length() > 0) {
//            sb.append(" AND A.ADM_DATE = '" + admDate + "' ");
//        }
//        String sessionCode = this.getValueString("SESSION_CODE");
//        if (sessionCode != null && sessionCode.length() > 0) {
//            sb.append(" AND A.SESSION_CODE = '" + sessionCode + "' ");
//        }
//        String CLINICTYPE_CODE = this.getValueString("CLINICTYPE_CODE");
//        if(CLINICTYPE_CODE != null&& CLINICTYPE_CODE.length()>0){
//            sb.append(" AND A.CLINICTYPE_CODE='"+ CLINICTYPE_CODE +"' ");
//        }
//        if (sb.length() > 0)
//            sql += sb.toString() + sql1;
//        else
//            sql = sql + sql1;
//        TTextFormat deptList = (TTextFormat)this.getComponent("tTextFormat_0");
//        deptList.setPopupMenuSQL(sql);
//    }
    /**
     * 叫号接口 (分诊保存时调用) 挂号
     */
    public void onCall_G(){
        Pat pat  = Pat.onQueryByMrNo(recptype.getValue("MR_NO"));
        CallNo call = new CallNo();
        Reg reg = Reg.onQueryByCaseNo(pat,recptype.getValue("CASE_NO"));
        if(call.init()){
            String reg_date = StringTool.getString(recptype.getTimestamp(
                "REG_DATE"), "yyyy-MM-dd HH:mi:ss");
            String ctz_desc = ( (TComboBox)this.getComponent("CTZ1_CODE")).
                getSelectedName();
            String CLINICTYPE_DESC = ( (TComboBox)this.getComponent(
                "CLINICTYPE_CODE")).getSelectedName();
            String realDr = this.getValueString("REALDR_CODE");
            String realDept = this.getValueString("REALDEPT_CODE");
            String Session = ( (TComboBox)this.getComponent("SESSION_CODE")).getSelectedName();
            String s = call.SyncClinicMaster(reg_date, //挂号日期
                                             recptype.getValue("CASE_NO"), //就诊序号
                                             CLINICTYPE_DESC, //诊别
                                             reg.getQueNo()+"", //诊号
                                             recptype.getValue("MR_NO"), //病案号
                                             pat.getName(), //姓名
                                             pat.getSexString(), //性别
                                             StringTool.CountAgeByTimestamp(pat.
                getBirthday(), SystemTool.getInstance().getDate())[0], //年龄
                                             ctz_desc, //主身份
                                             realDept, //科室
                                             reg_date, //挂号时间
                                             realDr, //医生
                                             "0", "2",Session);
        }
    }
    /**
     * 叫号接口 (分诊保存时调用) 退号
     */
    public void onCall_T(){
        Pat pat  = Pat.onQueryByMrNo(recptype.getValue("MR_NO"));
        CallNo call = new CallNo();
        if(call.init()){
            String reg_date = StringTool.getString(recptype.getTimestamp(
                "REG_DATE"), "yyyy-MM-dd HH:mi:ss");
            String ctz_desc = ( (TComboBox)this.getComponent("CTZ1_CODE")).
                getSelectedName();
            String CLINICTYPE_DESC = ( (TComboBox)this.getComponent(
                "CLINICTYPE_CODE")).getSelectedName();
            String Session = ( (TComboBox)this.getComponent("SESSION_CODE")).getSelectedName();
            String s = call.SyncClinicMaster(reg_date, //挂号日期
                                             recptype.getValue("CASE_NO"), //就诊序号
                                             CLINICTYPE_DESC, //诊别
                                             recptype.getValue("QUE_NO"), //诊号
                                             recptype.getValue("MR_NO"), //病案号
                                             pat.getName(), //姓名
                                             pat.getSexString(), //性别
                                             StringTool.CountAgeByTimestamp(pat.
                getBirthday(), SystemTool.getInstance().getDate())[0], //年龄
                                             ctz_desc, //主身份
                                             recptype.getValue("REALDEPT_CODE"), //科室
                                             reg_date, //挂号时间
                                             recptype.getValue("REALDR_CODE"), //医生
                                             "1", "3",Session);
        }
    }

}
