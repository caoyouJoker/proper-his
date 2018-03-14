package com.javahis.ui.opb;

import com.dongyang.control.TControl;
import jdo.opb.OPBReceiptTool;
import com.dongyang.ui.TTable;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.IReportTool;
import jdo.sys.Pat;
import com.dongyang.data.TParm;
import jdo.bil.BILContractRecordTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.opb.OPB;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.opb.OPBReceiptList;
import jdo.sys.SystemTool;
import jdo.opd.OrderTool;
import com.dongyang.util.StringTool;
import jdo.util.Manager;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;

/**
 * <p>Title: 门诊费用明细查询：记账退费界面</p>
 *
 * <p>Description:门诊费用明细查询： 记账退费界面</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110823
 * @version 1.0
 */

public class OPBBackContractControl extends TControl {
    public OPBBackContractControl() {
    }

    /**
     * 票据档
     */
    TTable tableM;
    /**
     * 医嘱档
     */
    TTable tableD;
    /**
     * 计价对象
     */
    OPB opb;
    /**
     * table数据
     */
    TParm tableMParm;
    /**
     * 印刷号
     */
    String printNoOnly;
    /**
     * 退费权限
     */
    boolean backBill = false;
    /**
     * 是否可退费
     */
    boolean returnFeeFlg = false;
    //批号
    private TParm batchNoParm;
    private String printNo;//票号
    private Timestamp bill_date;//收费日期

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        tableM = (TTable)this.getComponent("TABLEM");
        tableD = (TTable)this.getComponent("TABLED");
        //处理数据
        dealData();
        batchNoParm=new TParm(TJDODBTool.getInstance().select("SELECT ORDER_CODE, BATCH_NO FROM IND_STOCK"));
    }

    /**
     * 处理数据
     */
    public void dealData() {
        //初始化权限
        if (!initPopedem())
            return;
//        //把数据放入界面
//        if (!getReceiptList())
//            return;
        //得到收费列表
        getReceiptParm();
    }

    /**
     * 接受前台传来数据和初始化权限
     * @return boolean
     */
    public boolean initPopedem() {
        TParm parm;
        //前台传来的计价对象
        if (this.getParameter() != null) {
            parm = (TParm)this.getParameter();
            //加载opb
            if (!initOpb(parm))
                return false;
        }
        //退费权限
        if (!getPopedem("BACKBILL")) {
            backBill = false;
        }
        return true;
    }

    /**
     * 加载opb
     * @param parm TParm
     * @return boolean
     */
    public boolean initOpb(TParm parm) {
        String caseNo = parm.getValue("CASE_NO");
        String mrNo = parm.getValue("MR_NO");
        Pat pat = Pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.messageBox("查无此病案号");
            return false;
        }
        //界面赋值
        setValueForParm(
                "MR_NO;PAT_NAME;IDNO;SEX_CODE;COMPANY_DESC",
                pat.getParm());
        Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
        //判断挂号信息
        if (reg == null)
            return false;
        //三身份
        callFunction("UI|CTZ1_CODE|setValue", reg.getCtz1Code());
        callFunction("UI|CTZ2_CODE|setValue", reg.getCtz2Code());
        callFunction("UI|CTZ3_CODE|setValue", reg.getCtz3Code());
        //通过reg和caseNo得到pat
        opb = OPB.onQueryByCaseNo(reg);
        //给界面上部分地方赋值
        if (opb == null) {
            this.messageBox("此病人尚未就诊!");
            return false;
        }
        return true;
    }
    /**
     * 得到收费界面上收费列表
     */
    public void getReceiptParm() {
        TParm parm = OPBReceiptTool.getInstance().getContractReceipt(opb.getReg().
                caseNo());
//            opb.getReceiptList().getParm(opb.getReceiptList().PRIMARY);
        tableM.setParmValue(parm);
//        opb.getReceiptList().initOrder(opb.getPrescriptionList());
    }

    /**
     * 主表点击事件
     */
    public void onClickTableM() {
        tableMParm = new TParm();
        returnFeeFlg = false;
        int row = tableM.getSelectedRow();
        TParm tableParm = tableM.getParmValue();
        printNo= tableParm.getValue("PRINT_NO",row);//票号
        bill_date=  tableParm.getTimestamp("BILL_DATE",row);//收费日期
//        System.out.println("主表数据"+tableParm);
//        //拿到一张票据
//        OPBReceipt opbReceipt = (OPBReceipt) opb.getReceiptList().get(row);
//        //得到其中的parm
//        TParm parm = opbReceipt.getOrderList().getParm(OrderList.PRIMARY);
        TParm orderParm = new TParm();
        orderParm.setData("RECEIPT_NO", tableParm.getData("RECEIPT_NO", row));
        orderParm.setData("CASE_NO", tableParm.getData("CASE_NO", row));
        TParm parm = OrderTool.getInstance().queryFill(orderParm);
//        System.out.println("明细表数据"+parm);
        tableMParm = parm;
        tableD.setParmValue(parm);
        //校验是否已到检或已发药
//        int count = parm.getCount("EXEC_FLG");
//        String exeFlg = "";
//        for (int i = 0; i < count; i++) {
//            exeFlg = parm.getValue("EXEC_FLG", i);
//            if ("Y".equals(exeFlg)) {
//                returnFeeFlg = true;
//                return;
//            }
//        }
    }

    /**
     * 退费入口
     * @return boolean
     */
    public boolean onSave() {
        int row = tableM.getSelectedRow();
        if (row < 0) {
            messageBox("请选择要退费的票据!");
        }
        switch (this.messageBox("提示信息",
                                "是否退费", this.YES_NO_OPTION)) {
        case 0:
            if (!backReceipt(row))
                return false;
            break;
        case 1:
            return true;
        }
        return true;
    }

    /**
     * 退费方法
     * @param row int
     * @return boolean
     */
    public boolean backReceipt(int row) {
        if (returnFeeFlg) {
            this.messageBox("已发药或已到检，不可退费!");
            return false;
        }
        TParm parm = new TParm();
        parm.setData("RECEIPT_NO",
                     tableM.getParmValue().getValue("RECEIPT_NO", row));
        parm.setData("RECEIPT_TYPE", "OPB");
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        //查询当前就诊病患是否记账
        TParm result = BILContractRecordTool.getInstance().regRecodeQuery(parm);
        String bilStauts = result.getValue("BIL_STATUS", 0);
        //记账操作：没有执行收费打票,不去更改BIL_INVRCP表的数据
        if (null != bilStauts && "1".equals(bilStauts)) {
            //this.messageBox("没有执行结算,不用退费");
             //门诊收费，退挂时，已经记账但是没有进行结账操作时,退费操作之后将不显示此条数据
            if (!onSaveBackReceiptStatus(row)) {
                //保存失败
                messageBox("E0001");
                return false;
            }
        } else {
            //        if (!opb.onSaveBackReceipt(row)) {
            if (!onSaveBackReceipt(row)) {
                //保存失败
                messageBox("E0001");
                return false;
            }
            //保存成功
            messageBox("P0001");
        }
        //保存后重新初始化票据
        afterSave();
        return true;
    }

    /**
     * 保存后重新初始化票据
     */
    public void afterSave() {
//        //把数据放入界面
//        if (!getReceiptList())
//            return;
        //得到收费列表
        dealData();
        getReceiptParm();
        tableD.removeRowAll();
    }

    /**
     * 打印费用清单
     */
//    public void onFill() {
//        if (opb == null)
//            return;
//        System.out.println("tableMParm" + tableMParm);
//        int count = tableMParm.getCount("SETMAIN_FLG");
//        String setmainFlg = "";
//        for (int i = count - 1; i >= 0; i--) {
//            setmainFlg = tableMParm.getValue("SETMAIN_FLG", i);
//            if ("Y".equals(setmainFlg)) {
//                tableMParm.removeRow(i);
//            }
//        }
//        TParm parm = opb.getReceiptList().dealTParm(tableMParm,batchNoParm);
//        if (parm == null)
//            return;
//        TParm pringListParm = new TParm();
//        pringListParm.setData("TABLEORDER", parm.getData());
//        //病案号
//        pringListParm.setData("MR_NO", opb.getPat().getMrNo());
//        //病患姓名
//        pringListParm.setData("PAT_NAME", opb.getPat().getName());
//        //就诊序号
//        pringListParm.setData("CASE_NO", opb.getReg().caseNo());
//        String sql =
//                " SELECT CHN_DESC FROM SYS_DICTIONARY " +
//                "  WHERE GROUP_ID ='SYS_SEX' " +
//                "    AND ID = '" + opb.getPat().getSexCode() + "'";
//        TParm sexParm = new TParm(TJDODBTool.getInstance().select(sql));
//        //性别
//        pringListParm.setData("SEX_CODE", sexParm.getValue("CHN_DESC", 0));
//        //就诊日期
//        pringListParm.setData("ADM_DATE", opb.getReg().getAdmDate());
//        //医院名称
//        pringListParm.setData("HOSP",
//                              Operator.getHospitalCHNFullName() + "门诊费用清单");
//        this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderList.jhw",
//                             pringListParm);
//    }
    /**
        * 打印费用清单
        * ============泰心医院费用清单打印
        */
       public void onFill() {
           if (opb == null)
               return;
           //System.out.println("tableMParm" + tableMParm);
           //ORDER_DESC;MEDI_QTY;MEDI_UNIT;FREQ_CODE;TAKE_DAYS;
           //DOSAGE_QTY;DOSAGE_UNIT;OWN_PRICE;AR_AMT
           int count = tableMParm.getCount("SETMAIN_FLG");
           String setmainFlg = "";
           for (int i = count - 1; i >= 0; i--) {
               setmainFlg = tableMParm.getValue("SETMAIN_FLG", i);//集合医嘱注记
               if ("Y".equals(setmainFlg)) {
                   tableMParm.removeRow(i);
               }
           }
           TParm parm = opb.getReceiptList().dealTParm(tableMParm,batchNoParm);
           if (parm == null)
               return;
           double sum=parm.getDouble("SUM");//合计金额
           TParm pringListParm = new TParm();
           pringListParm.setData("TABLEORDER", parm.getData());
           //病患姓名
           pringListParm.setData("PAT_NAME","TEXT", opb.getPat().getName());//患者姓名
           pringListParm.setData("HOSP","TEXT",Operator.getHospitalCHNFullName());//医院名称
           pringListParm.setData("TITLE","TEXT",Operator.getHospitalCHNFullName());//医院名称
           pringListParm.setData("BILL_DATE","TEXT",StringTool.getString(TypeTool.getTimestamp(bill_date),"yyyyMMddHHmmss"));//收费日期

           pringListParm.setData("PRINT_NO","TEXT",printNo);//票号
           String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool.
                  getInstance().getDBTime()), "yyyy/MM/dd"); //年月日
           pringListParm.setData("DATE","TEXT",yMd);//日期
           pringListParm.setData("TOTAL","TEXT",sum);//日期
//           this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBOrderPrint.jhw",pringListParm);
           this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBOrderPrint.jhw"),
                                IReportTool.getInstance().getReportParm("OPBOrderPrint.class", pringListParm));//报表合并modify by wanglong 20130730
    }
    /**
     * 现金作废票据
     * @param row int
     * @return boolean
     */
    public boolean onSaveBackReceipt(int row) {
        TParm saveParm = tableM.getParmValue();
        TParm actionParm = saveParm.getRow(row);
        actionParm.setData("OPT_USER_T", Operator.getID());
        actionParm.setData("OPT_TERM_T", Operator.getIP());
        actionParm.setData("OPT_DATE_T", SystemTool.getInstance().getDate());
        if (saveParm == null)
            return false;
        //调用opbaction
        TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
                "backOPBRecp", actionParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * 现金作废票据:BIL_STATUS =1 记账不打印账单
     * @param row int
     * @return boolean
     * ==================pangben modify 20110822
     */
    public boolean onSaveBackReceiptStatus(int row) {
        TParm saveParm = tableM.getParmValue();
        TParm actionParm = saveParm.getRow(row);
        actionParm.setData("OPT_USER_T", Operator.getID());
        actionParm.setData("OPT_TERM_T", Operator.getIP());
        actionParm.setData("OPT_DATE_T", SystemTool.getInstance().getDate());
        if (saveParm == null)
            return false;
        //调用opbaction
        TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
                "backOPBRecpStatus", actionParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * 票据补打
     * @param row int
     * @return boolean
     */
    public boolean onSaveRePrint(int row) {
        TParm saveRePrintParm = getRePrintData(row);
        if (saveRePrintParm == null)
            return false;
        //调用opbaction
        TParm result = TIOM_AppServer.executeAction("action.opb.OPBAction",
                "saveOPBRePrint", saveRePrintParm);
        printNoOnly = result.getValue("PRINT_NO");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }

        return true;
    }

    /**
     * 得到补印数据
     * @param row int
     * @return TParm
     */
    public TParm getRePrintData(int row) {
        TParm saveParm = tableM.getParmValue();
        TParm actionParm = saveParm.getRow(row);
        actionParm.setData("OPT_USER", Operator.getID());
        actionParm.setData("OPT_TERM", Operator.getIP());
        actionParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
//        System.out.println("actionParm"+actionParm);
        return actionParm;

    }

}
