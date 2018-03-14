package com.javahis.ui.mro;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.mro.MROChrtvetrecTool;
import jdo.mro.MROChrtvetstdTool;
import jdo.mro.MROPrintTool;
import jdo.mro.MROQlayControlMTool;
import jdo.mro.MRORecordTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.adm.ADMInpTool;
import jdo.adm.ADMTool;
import jdo.sys.Pat;
import javax.swing.SwingUtilities;
import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;


/**
 * <p>Title: 病案审核</p>
 *
 * <p>Description:病案审核 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-4-30
 * @version 1.0
 */
public class MROChrtvetrecControl extends TControl {
    private TParm patInfo;//病患信息 add by wanglong 20130819
    private String CASE_NO;//就诊序号
    private String MR_NO;
    private String IPD_NO;
    private String VS_CODE;//经治医师
    private String EXAMINE_CODE;//检核标准
    private String EXAMINE_DATE;//检核日期
    TParm data;
    int selectRow = -1;
    private String State = "mro";//记录该窗口调用状态（被哪个模块掉用）
    private boolean MRO_CHAT_FLG = false;//记录是否已经审核通过
    
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        this.initPage();
    }
    
    /**
     * 刷新初始化参数
     */
    public void onInitReset(){
        //初始化页面
        this.initPage();
    }
    
    /**
     * 初始化页面
     */
    public void initPage() {
        Object obj = this.getParameter();
        if (obj != null) {
            TParm parmData = (TParm) obj;
            CASE_NO = parmData.getValue("MRO", "CASE_NO");
            // 接受参数 "odi"表示住院医生站
            if (parmData.getValue("MRO", "STATE").trim().length() > 0) {
                State = parmData.getValue("MRO", "STATE");
            }
        } else {
            this.messageBox_("参数初始化失败！");
        }
        onClear();
        TTable table = (TTable) this.getComponent("MROTable");
        table.addEventListener("MROTable->" + TTableEvent.CLICKED, this, "onTableClicked");
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBox");
        // 转换时间格式(调用内部类)
        OrderList orderDesc = new OrderList();
        table.addItem("OrderList", orderDesc);
        this.onQuery();
        queryPatInfo(); // 查询患者信息
        if (State.equals("ODI")) {// 被住院医生站掉用
            ((TMenuItem) getComponent("delete")).setVisible(false);// 隐藏删除按钮
            // 设置不可编辑项
            this.callFunction("UI|VS_CODE|setEnabled", false);
            this.callFunction("UI|EXAMINE_DATE|setEnabled", false);
            this.callFunction("UI|TYPE_CODE|setEnabled", false);
            this.callFunction("UI|EXAMINE_CODE|setEnabled", false);
            this.callFunction("UI|DEDUCT_NOTE|setEnabled", false);
            this.callFunction("UI|DEDUCT_SCORE|setEnabled", false);
            this.callFunction("UI|URG_FLG|setEnabled", false);
            this.callFunction("UI|AUTO_QLAY|setEnabled", false);//add by wanglong 20131025
            this.callFunction("UI|MANUAL_QLAY|setEnabled", false);
            // 设置可编辑项
            if (!MRO_CHAT_FLG) {//add by wanglong 20130909
                ((TTextField) this.getComponent("REPLY_REMK")).setEnabled(true);// 完成回复备注
            }
            // 隐藏“审核完成”复选框
            ((TCheckBox) this.getComponent("MRO_CHAT_FLG")).setEnabled(false);
        }
    }
    
    /**
     * 拿到菜单
     * @param tag String
     * @return TMenuItem
     */
    public TMenuItem getTMenuItem(String tag){
        return (TMenuItem)this.getComponent(tag);
    }

    /**
     * 增加对Table的监听
     * @param row int
     */
    public void onTableClicked(int row) {
        // 选中行
        if (row < 0) return;
        setValueForParm("VS_CODE;DEDUCT_NOTE;TYPE_CODE;EXAMINE_CODE;REPLY_REMK;DEDUCT_SCORE;REPLY_DTTM;REPLY_DR_CODE",
                        data, row);
        //如果是住院调用此窗口
        if (State.equals("ODI")) {
            // 如果回复时间为空
            if (this.getValue("REPLY_DTTM") == null) {
                // 初始化回复时间和恢复人员
                this.setValue("REPLY_DTTM", SystemTool.getInstance().getDate());
                ((TComboBox) this.getComponent("REPLY_DR_CODE")).setValue(Operator.getID()); // 回复医师
            }
        }
        selectRow = row;
        //是否急件
        if(data.getValue("URG_FLG",row).equals("Y"))
            ((TCheckBox)this.getComponent("URG_FLG")).setSelected(true);
        else
            ((TCheckBox)this.getComponent("URG_FLG")).setSelected(false);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(true);
        EXAMINE_CODE = data.getValue("EXAMINE_CODE",row);//记录选中行检核代码
        EXAMINE_DATE = data.getValue("EXAMINE_DATE",row);//记录选中行检核日期
        //修改审核日期格式
        this.setValue("EXAMINE_DATE",StringTool.getTimestamp(data.getValue("EXAMINE_DATE",row),"yyyyMMdd"));
    }
    
    /**
     * CheckBox点选事件
     * @param obj
     * @return
     */
    public boolean onTableCheckBox(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        return true;
    }

    /**
     * 清除
     */
    public void onClear(){
        selectRow = -1;
        this.clearValue("DEDUCT_SCORE;TYPE_CODE;EXAMINE_CODE;DEDUCT_NOTE;REPLY_REMK");//modify by wanglong 20130819
        EXAMINE_CODE = "";// 检核标准
        EXAMINE_DATE = "";// 检核日期
//        patInfo = new TParm();//add by wanglong 20130819
        if(State.equals("ODI")){
            //初始化回复时间和恢复人员
            this.setValue("REPLY_DTTM",SystemTool.getInstance().getDate());
            ((TComboBox)this.getComponent("REPLY_DR_CODE")).setValue(Operator.getID());//回复医师
        }else{
            this.clearValue("REPLY_DTTM;REPLY_DR_CODE");
        }
        if(MRO_CHAT_FLG){
            this.clearValue("VS_CODE");
        }
        // 清除选中行
        ((TTable) this.getComponent("MROTable")).clearSelection();
        // 急件取消选择
        ((TCheckBox) this.getComponent("URG_FLG")).setSelected(false);
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        // 初始化检核时间
        this.setValue("EXAMINE_DATE", SystemTool.getInstance().getDate());
        // 设置全部查询
        ((TRadioButton) this.getComponent("rbt1")).setSelected(true);
        // 重新查询审核信息
        // queryChr();
    }
    
    /**
     * 查询
     */
    public void onQuery() {
        selectRow = -1;
        this.clearValue("DEDUCT_SCORE;TYPE_CODE;EXAMINE_CODE;DEDUCT_NOTE;REPLY_REMK");
        if(State.equals("ODI")){
            //初始化回复时间和恢复人员
            this.setValue("REPLY_DTTM",SystemTool.getInstance().getDate());
            ((TComboBox)this.getComponent("REPLY_DR_CODE")).setValue(Operator.getID());//回复医师
        }else{
            this.clearValue("REPLY_DTTM;REPLY_DR_CODE");
        }
        // 清除选中行
        ((TTable) this.getComponent("MROTable")).clearSelection();
        // 急件取消选择
        ((TCheckBox) this.getComponent("URG_FLG")).setSelected(false);
        // 初始化检核时间
        this.setValue("EXAMINE_DATE", SystemTool.getInstance().getDate());
        TParm parm = new TParm();
        // 已回复的检核信息
        if (((TRadioButton) this.getComponent("rbt2")).isSelected()) parm.setData("REPLY", "Y");
        else if (((TRadioButton) this.getComponent("rbt3")).isSelected()) parm.setData("REPLY", "N");
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("VS_CODE", this.getValue("VS_CODE"));
        data = MROChrtvetrecTool.getInstance().queryData(parm);
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        // 设置删除按钮状态
        ((TMenuItem) getComponent("delete")).setEnabled(false);
        ((TTable) getComponent("MROTable")).setParmValue(data);
        checkCHAT_FLG();
        //========= add by wanglong 20130819
        TParm parms = new TParm();
        parms.addData("CASE_NO", CASE_NO);
        TParm result =
                TIOM_AppServer.executeAction("action.mro.MROQlayControlAction", "updateScore", parms);//更新得分
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
            return;
        }
        result = MROQlayControlMTool.getInstance().queryQlayControlSUM(parms.getRow(0));//查询得分
        if(result.getErrCode() != 0){
            this.messageBox(result.getErrText());
            return;
        }
        this.setValue("SCORE", result.getValue("SUMSCODE", 0));//显示得分到界面
        //========= add end
    }
    
    /**
     * 查看 病案审核标记
     */
    private void checkCHAT_FLG() {
        // 获取病患首页信息 查看 病案审核标记
        TParm mroParm = new TParm();
        mroParm.setData("CASE_NO", CASE_NO);
        TParm MROInfo = MROQlayControlMTool.getInstance().queryQlayControlSUM(mroParm);
//        TParm MROInfo = MRORecordTool.getInstance().getInHospInfo(mroParm);
        if ("2".equals(MROInfo.getValue("MRO_CHAT_FLG", 0))) {
            this.setValue("MRO_CHAT_FLG", true);
            MRO_CHAT_FLG = true;
            lock(false);
        } else {
            this.setValue("MRO_CHAT_FLG", "N");
            MRO_CHAT_FLG = false;
            lock(true);
        }
    }
    
    /**
     * 查询患者住院信息
     */
    private void queryPatInfo(){
        if(CASE_NO.length()==0){
            this.messageBox("初始化参数错误没有就诊号！");
            return;
        }
        patInfo = MROChrtvetrecTool.getInstance().selectdata(CASE_NO);
        // 判断错误值
        if (patInfo.getErrCode() < 0) {
            messageBox(patInfo.getErrText());
            return;
        }
        //赋值
        VS_CODE = patInfo.getValue("VS_DR_CODE",0);//经治医生
        ((TComboBox)this.getComponent("VS_CODE")).setSelectedID(VS_CODE);
        IPD_NO = patInfo.getValue("IPD_NO",0);//住院号
        MR_NO = patInfo.getValue("MR_NO",0);//病案号
    }
    
    /**
     * 查询患者审核信息
     */
    private void queryChr(){
        if(CASE_NO.length()==0){
            this.messageBox("初始化参数错误，没有就诊号！");
            return;
        }
        data = MROChrtvetrecTool.getInstance().selectAllChrtvetrec(CASE_NO);
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ((TTable) getComponent("MROTable")).setParmValue(data);
        checkCHAT_FLG();
    }

    /**
     * 新增
     */
    public void onInsert() {
        TParm parm = this.getParmForTag("VS_CODE;DEDUCT_NOTE;DEDUCT_SCORE;TYPE_CODE;EXAMINE_CODE;REPLY_DTTM;REPLY_DR_CODE;REPLY_REMK");
        //===============add by wanglong 20130909
        if (StringUtil.isNullString(parm.getValue("VS_CODE"))) {
            this.messageBox("请填写经治医师");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (sdf.parse(this.getText("EXAMINE_DATE"), new ParsePosition(0))==null) {
            this.messageBox("请填写正确的审查日期");
            return;
        }
        String date = StringTool.getString((Timestamp) this.getValue("EXAMINE_DATE"), "yyyyMMdd");
        parm.setData("EXAMINE_DATE", date);// 时间
        if (StringUtil.isNullString(parm.getValue("TYPE_CODE"))) {
            this.messageBox("请填写审查项目");
            return;
        }
        if (StringUtil.isNullString(parm.getValue("EXAMINE_CODE"))) {
            this.messageBox("请填写欠缺内容");
            return;
        }
        if (StringUtil.isNullString(parm.getValue("DEDUCT_SCORE"))) {
            this.messageBox("请填写扣分");
            return;
        }
        //===============add end
        //是否急件
        if ( ( (TCheckBox)this.getComponent("URG_FLG")).isSelected())
            parm.setData("URG_FLG", "Y");
        else
            parm.setData("URG_FLG", "N");
        parm.setData("MR_NO",MR_NO);//病案号
        parm.setData("IPD_NO",IPD_NO);//住院号
        parm.setData("CASE_NO",CASE_NO);//就诊序号
        // 取得质控方法的信息
        String sql =
                "SELECT A.METHOD_CODE, A.METHOD_DESC, C.METHOD_TYPE_CODE, C.METHOD_TYPE_DESC, B.EXAMINE_CODE, "
                        + "       B.TYPE_CODE, B.EXAMINE_DESC, B.ENNAME, B.DESCRIPTION, B.SCORE, B.URG_FLG, B.SPCFY_DEPT, "
                        + "       B.METHOD_PARM, B.CHECK_RANGE, B.CHECK_FLG, B.CHECK_SQL, C.EMR_CHECK_NULL, C.EMR_CHECK_TIME, "
                        + "       C.TABLE_CHECK_NULL, C.TABLE_CHECK_TIME, C.TIME_VALUE "
                        + "  FROM MRO_METHOD A, MRO_CHRTVETSTD B, MRO_METHOD_TYPE C "
                        + " WHERE A.METHOD_CODE = B.METHOD_CODE "
                        + "   AND A.METHOD_TYPE_CODE = C.METHOD_TYPE_CODE "
                        + "   AND B.CHECK_FLG = 'Y' "
                        + "   AND A.METHOD_CODE = '#' ";
        sql = sql.replaceFirst("#", this.getValueString("EXAMINE_CODE"));
        TParm methodTypeParm = new TParm(TJDODBTool.getInstance().select(sql));
        double interval = methodTypeParm.getDouble("TIME_VALUE", 0);
        if (interval != 0) {// add by wanglong 2013909
            TParm patParm = ADMInpTool.getInstance().selectall(parm);
            if ("1".equals(methodTypeParm.getValue("CHECK_RANGE", 0))) {
                Timestamp offset = rollHour(patParm.getTimestamp("IN_DATE", 0), interval);
                Timestamp now = SystemTool.getInstance().getDate();
                if (offset.getTime() > now.getTime()) {
                    if (this.messageBox("提示", "该病患入院未超过" + TypeTool.getInt(interval)
                            + "小时（在时效期内），这种情况下不应进行该项审核！\n是否继续？", 0) != 0) {
                        return;
                    }
                }
            } else {
                Timestamp offset = rollHour(patParm.getTimestamp("DS_DATE", 0), interval);
                Timestamp now = SystemTool.getInstance().getDate();
                if (offset.getTime() > now.getTime()) {
                    if (this.messageBox("提示", "该病患出院未超过" + TypeTool.getInt(interval)
                            + "小时（在时效期内），这种情况下不应进行该项审核！\n是否继续？", 0) != 0) {
                        return;
                    }
                }
            }
        }
        // =============add by wanglong 20130819
        parm.setData("STATUS", "0");// 未通过检核
        parm.setData("QUERYSTATUS", "1");// 已做检核
        TParm rangeParm = MROChrtvetstdTool.getInstance().selectdata(parm.getValue("EXAMINE_CODE"));
        if (rangeParm.getErrCode() != 0) {
            this.messageBox(rangeParm.getErrText());
            return;
        }
        parm.setData("CHECK_RANGE", rangeParm.getValue("CHECK_RANGE", 0));
        parm.setData("CHECK_USER", Operator.getID());
        parm.setData("CHECK_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", parm.getData("CHECK_DATE"));
        parm.setData("OPT_TERM", Operator.getIP());
        // ==============add end
        parm.setData("DEDUCT_SCORECOUNT", "1");
        TParm result = TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction", "insertdata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox_("添加失败！" + result.getErrText()+result.getErrName());
            return;
        }
        // ====================addd by wanglong 20130929
        if(((TCheckBox) this.getComponent("SEND_MSG")).isSelected()){
            TParm msgParm = new TParm();
            msgParm.setData("OPT_USER", Operator.getID());
            msgParm.setData("OPT_TERM", Operator.getIP());
            TParm caseParm = new TParm();
            caseParm.addData("CASE_NO", CASE_NO);
            msgParm.setData("CASE_NO", caseParm.getData());
            // 执行数据新增
            TParm msgResult =
                    TIOM_AppServer.executeAction("action.mro.MROQlayControlAction", "onBoardMessage",
                                                 msgParm);
            // 保存判断
            if (msgResult == null || !msgResult.getErrText().equals("")) {
                this.messageBox("公告栏消息发送失败" + " , " + msgResult.getErrText());
                return;
            }
            // this.messageBox("发送成功");  
        }
        // ====================add end
        // 显示新增数据
        int row =
                ((TTable) getComponent("MROTable")).addRow(parm,
                //add by wanglong 20130819
                "FLG;VS_CODE;EXAMINE_DATE;TYPE_CODE;EXAMINE_CODE;DEDUCT_SCORE;URG_FLG;DEDUCT_NOTE;REPLY_DTTM;REPLY_DR_CODE;REPLY_REMK");
        data.setRowData(row, parm);
        this.messageBox("P0005");
        this.onClear();
        this.onQuery();
    }
    
    /**
     * 更新
     */
    public void onUpdate() {
//        if (selectRow == -1) {
//            this.messageBox_("请选中某一行数据！");
//            return;
//        }
//        if (data.getValue("REPLY_DR_CODE", selectRow).trim().length() > 0) {
//            this.messageBox_("医生已完成，不能修改！");
//            return;
//        }
        if (State.equals("ODI")) {
//            String sql =
//                    "SELECT EMAIL_STATUS,BOARD_STATUS FROM MRO_CHRTVETREC WHERE CASE_NO='#' AND EXAMINE_CODE='#'";
//            sql = sql.replaceFirst("#", CASE_NO);
//            sql = sql.replaceFirst("#", this.getValueString("EXAMINE_CODE"));
//            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//            if (!result.getValue("EMAIL_STATUS", 0).equals("Y")
//                    && !result.getValue("BOARD_STATUS", 0).equals("Y")) {
//                this.messageBox("未给您发过通告(或邮件)的项目不能回复");
//                return;
//            }
            if (this.getValueString("DEDUCT_NOTE").trim().equals("")) {
                this.messageBox("没有备注信息的项目无需回复备注");
                return;
            }
        }
        TParm parm = this.getParmForTag("VS_CODE;DEDUCT_NOTE;DEDUCT_SCORE;TYPE_CODE;EXAMINE_CODE;REPLY_DR_CODE;REPLY_REMK");
        //===============add by wanglong 20130909
        if (StringUtil.isNullString(parm.getValue("VS_CODE"))) {
            this.messageBox("请填写经治医师");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        if (sdf.parse(this.getText("EXAMINE_DATE"), new ParsePosition(0)) == null) {
            this.messageBox("请填写正确的审查日期");
            return;
        }
        // 新时间
        String date = StringTool.getString((Timestamp) this.getValue("EXAMINE_DATE"), "yyyyMMdd");
        parm.setData("EXAMINE_DATE", date);// 时间
        if (StringUtil.isNullString(parm.getValue("TYPE_CODE"))) {
            this.messageBox("请填写审查项目");
            return;
        }
        if (StringUtil.isNullString(parm.getValue("EXAMINE_CODE"))) {
            this.messageBox("请填写欠缺内容");
            return;
        }
        if (StringUtil.isNullString(parm.getValue("DEDUCT_SCORE"))) {
            this.messageBox("请填写扣分");
            return;
        }
        //===============add end
        //是否急件
        if (((TCheckBox) this.getComponent("URG_FLG")).isSelected()) parm.setData("URG_FLG", "Y");
        else parm.setData("URG_FLG", "N");
        parm.setData("CASE_NO", CASE_NO);// 就诊序号
        // 原有时间
        parm.setData("OLD_EXAMINE_DATE", EXAMINE_DATE);
        // 判断是否是从住院医生站进入的 医生进行回复的
        if (State.equals("ODI")) {// 如果是回复时间为当前时间
            parm.setData("REPLY_DTTM", SystemTool.getInstance().getDate());
        } else {// 如果不是 不记录时间
            parm.setData("REPLY_DTTM", "");
        }
        parm.setData("OLD_EXAMINE_CODE", EXAMINE_CODE);//检核标准
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("DEDUCT_SCORECOUNT", data.getData("DEDUCT_SCORECOUNT",selectRow));//add by wanglong 20130909
        TParm result = TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction", "updatedata", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox_("修改失败！" + result.getErrText());
            return;
        }
        // 选中行
        int row = ((TTable) getComponent("MROTable")).getSelectedRow();
        if (row < 0) return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ((TTable) getComponent("MROTable")).setRowParmValue(row, data);
        this.messageBox_("修改成功！");
        this.onQuery();
    }
    /**
     * 保存
     */
    public void onSave() {
        TParm parmr = new TParm();
        parmr.setData("CASE_NO", CASE_NO);
        parmr = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmr);//在MRO_RECORD中查询是否完成，以及总分数信息
        if (parmr.getErrCode() < 0) {
            this.messageBox(parmr.getErrText());
            return;
        }
        if ((MRO_CHAT_FLG && "0".equals(parmr.getValue("TYPERESULT", 0)))
                || (!MRO_CHAT_FLG && "1".equals(parmr.getValue("TYPERESULT", 0)))) {
            this.messageBox("审核完成状态异常，请退出界面重新进，再进行操作");
            return;
        }
        //如果是医生站调用 并且病案已经通过审核 则不可保存
        if (State.equals("ODI") && MRO_CHAT_FLG) {
            this.messageBox_("病案审核已经通过，不可保存！");
            return;
        }
        //取消审核状态
        if (MRO_CHAT_FLG && "N".equals(this.getValueString("MRO_CHAT_FLG"))) {
            int re = this.messageBox("提示", "确认要取消审核通过状态吗？", 0);
            if (re == 1) return;
            else {
                TParm parm = new TParm();
                parm.setData("CASE_NO", CASE_NO);
                parm.setData("MRO_CHAT_FLG", "1");
                parm.setData("TYPERESULT", "0");
                TParm result =
                        TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction",
                                                     "updateMRO_CHAT_FLG", parm);
                if (result.getErrCode() < 0) {
                    this.messageBox("E0005");
                    return;
                }
                this.messageBox("P0005");
                this.onClear();
                queryChr();
                return;
            }
        }
        //审核完成
        if ("Y".equals(this.getValueString("MRO_CHAT_FLG"))) {
            TParm parm = new TParm();
            parm.setData("CASE_NO", CASE_NO);
            parm.setData("MRO_CHAT_FLG", "2");
            parm.setData("TYPERESULT", "1");
            TParm result =
                    TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction",
                                                 "updateMRO_CHAT_FLG", parm);
            if (result.getErrCode() < 0) {
                this.messageBox("E0005");
                return;
            }
            this.messageBox("P0005");
            this.onClear();
            queryChr();
        }else{
            if ("UPDATE".equals(updateCheck())) {
                onUpdate();
            } else {
                if (!State.equals("ODI")) onInsert();
            }
            // ========= add by wanglong 20130819
            TParm parm = new TParm();
            parm.addData("CASE_NO", CASE_NO);
            TParm result =
                    TIOM_AppServer.executeAction("action.mro.MROQlayControlAction", "updateScore", parm);//更新得分
            if (result.getErrCode() != 0) {
                this.messageBox(result.getErrText());
                return;
            }
            result = MROQlayControlMTool.getInstance().queryQlayControlSUM(parm.getRow(0));// 查询得分
            if (result.getErrCode() != 0) {
                this.messageBox(result.getErrText());
                return;
            }
            this.setValue("SCORE", result.getValue("SUMSCODE", 0));//显示得分到界面
            //========= add end
        }
    }

    /**
     * 删除
     */
    public void onDelete() {
        if (selectRow == -1) return;
        if (MRO_CHAT_FLG) {// add by wanglong 20131025
            this.messageBox("请先取消审核");
            return;
        }
        TParm parmr = new TParm();
        parmr.setData("CASE_NO", CASE_NO);
        parmr.setData("MR_NO", MR_NO);
        parmr = MROQlayControlMTool.getInstance().queryQlayControlSUM(parmr);// 在MRO_RECORD中查询是否完成，以及总分数信息
        // 查找是否已经提交完成
        if ("1".equals(parmr.getValue("TYPERESULT", 0))) {
            this.messageBox("审核完成状态异常，请刷新界面，再进行操作");
            return;
        }
        int i = 0;
        for ( ; i < data.getCount(); i++) {
            if (data.getValue("FLG", i).equals("Y")) {
                break;
            }
        }
        if (i == data.getCount()) {
            this.messageBox("请先勾选一行数据");
            return;
        }
        TTable table= ((TTable) getComponent("MROTable"));
        int successCount = 0;
        if (this.messageBox("提示", "是否删除", 2) == 0) {
            for (int j = 0; j < data.getCount(); j++) {
                if (data.getValue("FLG", j).equals("Y")) {
                    if (data.getValue("REPLY_DR_CODE", j).trim().length() > 0) {
                        if (this.messageBox("提示",
                                            table.getShowParmValue().getValue("EXAMINE_CODE", j)
                                                    + "，医生已完成回复，是否删除", 2) != 0) {
                            continue;
                        }
                    }
                    TParm parm = new TParm();
                    parm.setData("CASE_NO", CASE_NO);
                    parm.setData("EXAMINE_CODE", data.getValue("EXAMINE_CODE", j));
                    parm.setData("EXAMINE_DATE", data.getValue("EXAMINE_DATE", j));
                    TParm result =
                            TIOM_AppServer.executeAction("action.mro.MROChrtvetrecAction",
                                                         "deletedata", parm);
                    if (result.getErrCode() < 0) {
                        messageBox(result.getErrText());
                        return;
                    }
                    successCount++;
                }
            }
            if (successCount > 0) {
                this.messageBox("删除成功！");
            }
            onClear();
            onQuery();
        }
    }
    /**
     * 页签选中事件
     */
    public void Change() {
        TTabbedPane tp = (TTabbedPane)this.getComponent("tTabbedPane_0");
        int p_num = tp.getSelectedIndex();
        if (p_num == 0) {
            TParm data = MROPrintTool.getInstance().getNewMroRecordprintData(CASE_NO);
            if (data.getErrCode() < 0) {
                this.messageBox("E0005");
            }
            this.openPrintWindow("%ROOT%\\config\\prt\\MRO\\MRO_NEWRECORD.jhw", data);
            tp.setSelectedIndex(7);
        } else if (p_num == 1) {
            TParm admParm = new TParm();
            admParm.setData("CASE_NO", CASE_NO);
            TParm result = ADMTool.getInstance().getADM_INFO(admParm);
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            TParm parm = new TParm();
            parm.setData("SYSTEM_TYPE", "ODI");
            parm.setData("ADM_TYPE", "I");
            parm.setData("CASE_NO", CASE_NO);
            parm.setData("PAT_NAME", pat.getName());
            parm.setData("MR_NO", pat.getMrNo());
            parm.setData("IPD_NO", result.getValue("IPD_NO", 0));
            parm.setData("ADM_DATE", result.getTimestamp("IN_DATE", 0));
            parm.setData("DEPT_CODE", result.getValue("DEPT_CODE", 0));
            parm.setData("RULETYPE", "1");
            parm.setData("EMR_DATA_LIST", new TParm());
            parm.addListener("EMR_LISTENER", this, "emrListener");
            parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
            tp.setSelectedIndex(7);
        } else if (p_num == 2) {
            TParm parm = new TParm();
            parm.setData("INW", "CASE_NO", CASE_NO);
            this.openWindow("%ROOT%\\config\\inw\\INWOrderSheetPrtAndPreView.x", parm);
            tp.setSelectedIndex(7);
        } else if (p_num == 3) {
            // =================chenxi modefy 2012.05.10
            TParm admParm = new TParm();
            admParm.setData("CASE_NO", CASE_NO);
            TParm result = ADMTool.getInstance().getADM_INFO(admParm);
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            SystemTool.getInstance().OpenIE("http://172.20.40.50/ami/html/webviewer.html?showlist&un=his&pw=hishis&ris_pat_id=" + pat.getMrNo());
            tp.setSelectedIndex(7);
        } else if (p_num == 4) {
            TParm admParm = new TParm();
            admParm.setData("CASE_NO", CASE_NO);
            TParm result = ADMTool.getInstance().getADM_INFO(admParm);
            Pat pat = Pat.onQueryByMrNo(result.getValue("MR_NO", 0));
            SystemTool.getInstance().OpenIE("http://172.20.109.241/reportform.ASPX?patNO=" + pat.getMrNo());
            tp.setSelectedIndex(7);
            // ==================chenxi modefy 2012.05.10
        } else if (p_num == 5) {
            SystemTool.getInstance().OpenIE("http:///tra/index.asp?caseno=" + CASE_NO + "&CLV=1111");
            tp.setSelectedIndex(7);
        } else if (p_num == 6) {
            TParm parm = new TParm();
            parm.setData("SUM", "CASE_NO", CASE_NO);
            parm.setData("SUM", "ADM_TYPE", "I");
            parm.setData("SUM", "FLG", "MRO");
            this.openWindow("%ROOT%\\config\\sum\\SUMVitalSign.x", parm);
            tp.setSelectedIndex(7);
        }
    }

    /**
     * 页签选中事件
     */
    public void TPChange() {
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                Change();
            }
        });
    }

    /**
     * 欠缺内容列表框 变动事件
     */
    public void selectChange() {
        // 自动获取分数值
        this.setValue("DEDUCT_SCORE", ((TComboBox) this.getComponent("EXAMINE_CODE")).getSelectedText());
    }
    
    /**
     * 关闭事件
     * @return boolean
     */
    public boolean onClosing() {
        switch (messageBox("提示信息", "退出编辑状态?", this.YES_NO_OPTION)) {
            case 0:
                break;
            case 1:
                return false;
        }
        return true;
    }
    
    /**
     * 关闭
     */
    public Object onClosePanel() {
        if (State.equals("ODI")) {
            this.closeWindow();
            return "OK";
        }
        return null;
    }

    /**
     * 日期字符串格式转换
     * @param date String  格式为 20090101 转换为 2009/01/01
     */
    public String changeDF(String date){
        String result =date.substring(0,4) + "/" +date.substring(4,6) + "/" +date.substring(6);
        return result;
    }
    
    /**
     * 转变EXAMINE_DATE字段格式（内部类）
     */
    public class OrderList extends TLabel {

        public String getTableShowValue(String s) {
            if (s.length() == 8) {
                String date = s.substring(0, 4) + "/" + s.substring(4, 6) + "/" + s.substring(6, 8);
                return date;
            } else return s;
        }
    }
    
    /**
     * 处理当前TOOLBAR (给ODIMainUI.x框架调用的)
     */
    public void onShowWindowsFunction() {
        // 显示UIshowTopMenu
        callFunction("UI|showTopMenu");
    }
    
    /**
     * 审核完成 事件
     */
    public void onMRO_CHAT_FLG() {
        onClear();
        if ("Y".equals(this.getValueString("MRO_CHAT_FLG"))) {
            lock(false);
        } else if ("N".equals(this.getValueString("MRO_CHAT_FLG"))) {
            if (!MRO_CHAT_FLG) lock(true);
        }
    }
    
    /**
     * 设置控件是否可编辑
     * @param flg boolean
     */
    private void lock(boolean flg) {
        if (State.equals("ODI")) {//add by wanglong 20131104
            flg = false;
        }
        callFunction("UI|VS_CODE|setEnabled", flg);
        callFunction("UI|EXAMINE_DATE|setEnabled", flg);
        callFunction("UI|DEDUCT_NOTE|setEnabled", flg);
        callFunction("UI|TYPE_CODE|setEnabled", flg);
        callFunction("UI|EXAMINE_CODE|setEnabled", flg);
        callFunction("UI|DEDUCT_SCORE|setEnabled", flg);
        // ===================add by wanglong 20131025
        callFunction("UI|rbt1|setEnabled", flg);
        callFunction("UI|rbt2|setEnabled", flg);
        callFunction("UI|rbt3|setEnabled", flg);
        callFunction("UI|URG_FLG|setEnabled", flg);
        callFunction("UI|SEND_MSG|setEnabled", flg);
        callFunction("UI|AUTO_QLAY|setEnabled", flg);
//        callFunction("UI|MANUAL_QLAY|setEnabled", flg);
        // ===================add end
    }
    
    /**
     * 判断是inset还是update
     * @return String
     */
    private String updateCheck(){
        String re = "SAVE";
        //根据主键查询数据库中是否存在该条 信息
        TParm parm = new TParm();
        parm.setData("CASE_NO",CASE_NO);
        //modify by wanglong 20130909
//        parm.setData("EXAMINE_DATE",StringTool.getString((Timestamp)this.getValue("EXAMINE_DATE"),"yyyyMMdd"));
        parm.setData("EXAMINE_CODE",this.getValue("EXAMINE_CODE"));
        TParm check = MROChrtvetrecTool.getInstance().selectChrData(parm);
        //如果存在数据 返回 UPDATE
        if(check.getCount()>0){
            re = "UPDATE";
        }
        return re;
    }
    
    /**
     * 发送公告栏消息
     * @return
     */
    public void onSendBoardMessage() {//add by wanglong 20130819
        TParm caseParm = new TParm();
        caseParm.addData("CASE_NO", CASE_NO);
        TParm parm = new TParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("CASE_NO", caseParm.getData());
        // 执行数据新增
        TParm result = TIOM_AppServer.executeAction(
                "action.mro.MROQlayControlAction", "onBoardMessage", parm);
        // 保存判断
        if (result == null || !result.getErrText().equals("")) {
            this.messageBox("发送失败" + " , " + result.getErrText());
            return;
        }
        this.messageBox("发送成功");
    }
    
    /**
     * 自动质控
     * @return
     */
    public void onAutoQlayControl() {//add by wanglong 20130819
        if (MRO_CHAT_FLG) {
            this.messageBox("已经提交，不能进行自动质控");
            return;
        }
        TParm parm = new TParm();
        parm.setData("CASE_NO", CASE_NO);
        TParm temp = MROQlayControlMTool.getInstance().queryQlayControlSUM(parm);
        if (!temp.getValue("TYPERESULT", 0).equals("0")) {
            this.messageBox("已经提交，不能进行自动质控");
            return;
        }
        temp = new TParm();
        temp.setData("CASE_NO", CASE_NO);
        temp.setData("OPT_USER", Operator.getID());
        temp.setData("OPT_TERM", Operator.getIP());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("parmTEMP", temp.getData());
        TParm result =
                TIOM_AppServer.executeAction("action.mro.MROQlayControlAction",
                                             "onQlayControlMethod", parm);
        // 保存判断
        if (result == null || !result.getErrText().equals("")) {
            this.messageBox("保存失败" + ", " + result.getErrText());
            return;
        }
        temp = MROQlayControlMTool.getInstance().queryQlayControlSUM(parm);
        if(temp.getErrCode() != 0){
            this.messageBox(temp.getErrText());
            return;
        }
        this.setValue("SCORE", temp.getValue("SUMSCODE", 0));// 显示得分到界面
        this.setValue("VS_CODE", "");
        onQuery();
    }
    
    /**
     * 手动质控
     * @return
     */
    public void onManualQlayControl(){//add by wanglong 20130819
        TParm parm = new TParm();
        if (patInfo.getValue("DS_DATE", 0).length()==0) {
            parm.setData("TYPE", "TYPE_IN");// 在院
        } else parm.setData("TYPE", "TYPE_OUT");// 出院
        parm.setData("CASE_NO", CASE_NO);
        parm.setData("MR_NO", MR_NO);
        parm.setData("PAT_NAME", patInfo.getData("PAT_NAME", 0));
        parm.setData("IPD_NO", IPD_NO);
        parm.setData("ADM_DATE", patInfo.getData("ADM_DATE", 0));
        parm.setData("DEPT_CODE", patInfo.getData("DEPT_CODE", 0));
        parm.setData("STATION_CODE", patInfo.getData("STATION_CODE", 0));
        parm.setData("TYPERESULT", patInfo.getData("TYPERESULT", 0)); // 是否完成
        parm.setData("OPEN_USER", Operator.getName());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("VS_DR_CODE", patInfo.getValue("VS_DR_CODE", 0));// add by wanglong 20121105
        parm.addListener("onReturnContent", this, "onReturnContent");
        this.openWindow("%ROOT%\\config\\mro\\MROQlayDataControlUI.x", parm);// 进入个人细项分值与手动质控
//        onQuery();
    }
    
    /**
     * 传回
     * @param value
     */
    public void onReturnContent(String value) {//add by wanglong 20130819
//        this.setValue("SCORE", value.split(";")[1]);//显示得分到界面
        this.setValue("VS_CODE", "");
        initPage();
    }
    
    /**
     * 汇出Excel
     */
    public void onExport() {//add by wanglong 20130819
        TTable table = (TTable)this.getComponent("MROTable");
        if (table.getRowCount()<1) {
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "病案审核项目清单");
    }
    
    /**
     * 日期偏移小时
     * @param t Timestamp
     * @param hour double
     * @return Timestamp
     */
    public Timestamp rollHour(Timestamp t, double hour) {// add by wanglong 20130909
        return new Timestamp(t.getTime() + (long) hour * 60 * 60 * 1000);
    }
    
    public void onExamCodeQuery(String tag){
//        this.clearValue(tag);
        TComboBox a=((TComboBox)this.getComponent(tag));
        a.removeAll();
        a.onQuery();
    }
}
