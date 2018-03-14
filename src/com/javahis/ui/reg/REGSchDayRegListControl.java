package com.javahis.ui.reg;

import com.dongyang.control.*;
import com.dongyang.data.TParm;
import jdo.reg.PatAdmTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TCheckBox;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TTableEvent;
import jdo.opd.OrderTool;
import jdo.bil.BILInvrcptTool;
import jdo.ekt.EKTIO;
import jdo.bil.BILREGRecpTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TButton;
import jdo.sys.SystemTool;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 挂号日班表查询挂号信息界面</p>
 *
 * <p>Description: 挂号日班表查询挂号信息界面</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-04-14
 * =====================pangben modify 20110602 修改
 * @version 1.0
 */
public class REGSchDayRegListControl extends TControl {
    public void onInit() {
        super.onInit();
        //账单table专用的监听
        getTable("Table").addEventListener(TTableEvent.
                                           CHECK_BOX_CLICKED, this,
                                           "onTableComponent");
        pageInit();
        tableAllParm = new TParm();
    }

    //======pangben modify 20110602 选中的行
    private TParm tableAllParm;
    private boolean LEADER = true; //权限
    private boolean UnRegMessage=true;//退挂操作消息框显示
    private String allMrNo="";//累计退挂失败病患NO
    private TParm obj=null;//参数
    public void pageInit() {
        obj = (TParm)this.getParameter();
        this.setValue("ADM_DATE",
                      StringTool.getTimestamp(obj.getValue("ADM_DATE"),
                                              "yyyyMMdd"));
        this.setValue("REGION_CODE", obj.getValue("REGION_CODE"));
        this.setValue("ADM_TYPE", obj.getValue("ADM_TYPE"));
        this.setValue("SESSION_CODE", obj.getValue("SESSION_CODE"));
        this.setValue("CLINICROOM_NO", obj.getValue("CLINICROOM_NO"));
        this.setValue("DR_CODE", obj.getValue("DR_CODE"));
        LEADER = obj.getBoolean("LEADER");
        onQuery();
    }
    /**
     * 查询
     */
    public void onQuery() {

        TParm result = PatAdmTool.getInstance().getSchDayRegInfo(obj);
        TTable table = (TTable)this.getComponent("Table");
        table.removeRowAll();
        table.setParmValue(result);

    }
    /**
     * 退挂全选复选框执行方法
     * ================pangben modify 20110602
     */
    public void onCheckSelectAll() {
        TTable table = this.getTable("Table");
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }

        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.getParmValue().setData("REGCAN_FLG", i, "Y");
            }
            tableAllParm = table.getParmValue();
        } else {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.getParmValue().setData("REGCAN_FLG", i, "N");
            }
            tableAllParm = new TParm();
        }
        table.setParmValue(table.getParmValue());
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
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }

    /**
     * table监听checkBox改变
     * @param obj Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        TParm tableParm = table.getParmValue();
        tableAllParm = new TParm();
        int count = tableParm.getCount("CASE_NO");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("REGCAN_FLG", i)) {
                tableAllParm.addData("REGION_CODE",
                                     tableParm.getValue("REGION_CODE", i));
                tableAllParm.addData("QUE_NO",
                                     tableParm.getValue("QUE_NO", i));
                tableAllParm.addData("CASE_NO",
                                     tableParm.getValue("CASE_NO", i));
                tableAllParm.addData("MR_NO",tableParm.getValue("MR_NO", i));
            }
        }
        return true;
    }


    /**
     * 多笔数据退挂操作
     * 现在的逻辑：多人退挂时，没有放到一个事物中，而是每一个退挂人员一个事物，将出现错误的退挂人员累计显示消息框，退挂成功的人员不显示表格中
     */
    public void onUnReg() {
        if (!LEADER) {
            this.messageBox("非组长不能退挂!");
            return;
        }
        if(tableAllParm.getCount("CASE_NO")<0){
            this.messageBox("请选择要退挂的病患");
            return;
        }
        for (int i = 0; i < tableAllParm.getCount("CASE_NO"); i++) {
            TParm parm = tableAllParm.getRow(i);
            oneUnReg(parm);
        }
        if (UnRegMessage) {
            this.messageBox("退挂成功!");
        } else{//显示退挂失败病患号码
            this.messageBox(allMrNo.substring(0, allMrNo.length()-1) +
                            " 病患,退挂失败!");
        }
         onQuery();
    }

    /**
     * 单笔数据退挂操作
     * @param parm TParm
     * ======pangben modify 20110603
     */
    public void oneUnReg(TParm parm) {

        String optUser = Operator.getID();
        String optTerm = Operator.getIP();
        TParm unRegParm = new TParm();
        TParm patFeeParm = new TParm();
        String case_no = parm.getValue("CASE_NO");
        patFeeParm.setData("CASE_NO", case_no);
        patFeeParm.setData("REGCAN_USER", optUser);
        //查询当前病患是否产生费用
        TParm selPatFeeForREG = OrderTool.getInstance().selPatFeeForREG(
                patFeeParm);
        TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
                case_no);
        String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
        TParm inInvRcpParm = new TParm();
        inInvRcpParm.setData("RECEIPT_NO", recpNo);
        inInvRcpParm.setData("CANCEL_FLG", 0);//======pangben 2012-3-23
        inInvRcpParm.setData("RECP_TYPE", "REG");//======pangben 2012-3-23
        TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
                inInvRcpParm);
        unRegParm.setData("CASE_NO", case_no);
        unRegParm.setData("REGCAN_USER", optUser);
        unRegParm.setData("OPT_USER", optUser);
        unRegParm.setData("OPT_TERM", optTerm);
        unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
        unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
        if (selPatFeeForREG.getDouble("AR_AMT", 0) == 0) {
            TParm result = new TParm();
            //现金退挂动作
            result = TIOM_AppServer.executeAction("action.reg.REGAction",
                                                  "onUnReg", unRegParm);
            if (result.getErrCode() < 0) {
                err(result.getErrName() + " " + result.getErrText());
                allMrNo+=parm.getValue("MR_NO")+"、";;
                UnRegMessage=false;
                return;
            }
        } else {
            this.messageBox(parm.getValue("MR_NO") + "病患已产生费用,不能退挂!");
            allMrNo+=parm.getValue("MR_NO")+"、";;
            return;
        }
    }
    /**
     * 打印方法
     * ===========pangben modify 20110607
     */
    public void onPrint() {
        TTable table = this.getTable("Table");
        TParm parm = table.getParmValue();
        if (parm.getCount() < 0) {
            this.messageBox("没有需要打印的数据");
            return;
        }
         TParm result=new TParm();
         TParm tableParm=new TParm();
         for(int i=0;i<parm.getCount();i++){
             tableParm.addData("QUE_NO", parm.getValue("QUE_NO", i));
             tableParm.addData("MR_NO", parm.getValue("MR_NO", i));
             tableParm.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
             tableParm.addData("SEX_CODE",
                               parm.getValue("SEX_CODE", i).equals("1") ? "男" :
                               "女");
             tableParm.addData("TEL_HOME", parm.getValue("TEL_HOME", i));
         }
         tableParm.setCount(parm.getCount());
        tableParm.addData("SYSTEM", "COLUMNS", "QUE_NO");
        tableParm.addData("SYSTEM", "COLUMNS", "MR_NO");
        tableParm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        tableParm.addData("SYSTEM", "COLUMNS", "SEX_CODE");
        tableParm.addData("SYSTEM", "COLUMNS", "TEL_HOME");
        result.setData("T1",tableParm.getData());

        //挂号时间
        result.setData("DATE", "TEXT",
                       this.getValueString("ADM_DATE").
                       substring(0, this.getValueString("ADM_DATE").indexOf(" ")));
        result.setData("ADM_TYPE", "TEXT",
                       this.getValue("ADM_TYPE").equals("O") ? "门诊" : "急诊");
        result.setData("REG_SESSION", "TEXT", parm.getValue("SESSION_DESC", 0));
        result.setData("CLINICROOM_NO", "TEXT",
                       ((TTextFormat)this.getComponent("CLINICROOM_NO")).
                       getText());
        result.setData("DR_CODE", "TEXT",
                       ((TTextFormat)this.getComponent("DR_CODE")).getText());
        result.setData("userName", "TEXT", Operator.getName());
        result.setData("userDate", "TEXT",
                       StringTool.getString(SystemTool.getInstance().getDate(),
                                            "yyyy/MM/dd"));
        result.setData("TITLE", "TEXT",
                       Operator.getHospitalCHNFullName() + "就诊医生挂号人员信息");
        this.openPrintWindow("%ROOT%\\config\\prt\\REG\\REGSchDayRegList.jhw",
                             result);
    }
   /**
    * 导出EXECL
    */
   public void onExecl() {
       TTable table = (TTable) callFunction("UI|Table|getThis");
       ExportExcelUtil.getInstance().exportExcel(table,
                                                 "就诊医生挂号人员信息");
   }
}
