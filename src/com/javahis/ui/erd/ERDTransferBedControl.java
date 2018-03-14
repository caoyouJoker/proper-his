package com.javahis.ui.erd;


import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.control.TControl;
import com.dongyang.ui.TButton;
import com.dongyang.data.TParm;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTextFormat;
import jdo.erd.ErdForBedAndRecordTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Description: 住院留观转床小界面</p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH 2009-9-10
 *
 * @version 1.0
 */

public class ERDTransferBedControl
    extends TControl {

    private TTextFormat fromRegion;
    private TTextFormat toRegion;
    private TTextFormat fromBed;
    private TTextFormat toBed;
    private TButton ok, cancel;
    private String caseNo, mrNo;

    public ERDTransferBedControl() {
    }

    public void onInit() {
        super.onInit();
        initParmFromOutside();
        myInitCtl();
        initUIData();
    }

    /**
     * 初始化界面参数caseNo/mrNo（从外部病患管理界面传来的参数）
     */
    public void initParmFromOutside() {
        //从病案管理界面拿到参数TParm
        TParm outsideParm = (TParm)this.getParameter();
        if (outsideParm != null) {
            //按就诊号查询的caseNo
            setCaseNo(outsideParm.getValue("CASE_NO"));
            //按病区查询的stationCode
            setMrNo(outsideParm.getValue("MR_NO"));
        }
    }


    /**
     *  得到控件
     */
    public void myInitCtl() {

        ok = (TButton)this.getComponent("OK");
        cancel = (TButton)this.getComponent("CANCEL");
        fromRegion = (TTextFormat)this.getComponent("fromRegion");
        toRegion = (TTextFormat)this.getComponent("toRegion");
        fromBed = (TTextFormat)this.getComponent("fromBed");
        toBed = (TTextFormat)this.getComponent("toBed");

    }
    /**
     * 保存
     * @return boolean
     */
    public boolean onSave() {
        TParm saveData=new TParm();
        saveData.setData("CASE_NO",this.getCaseNo());
        saveData.setData("MR_NO",this.getMrNo());
        saveData.setData("fromRegion",fromRegion.getValue());
        saveData.setData("toRegion",toRegion.getValue());
        saveData.setData("fromBed",fromBed.getValue());
        saveData.setData("toBed",toBed.getValue());
        saveData.setData("OPT_USER",Operator.getID());
        saveData.setData("OPT_TERM",Operator.getIP());

        //保存方法
        //调用action执行事务
        TParm result = TIOM_AppServer.executeAction(
            "action.erd.ERDDynamicRcdAction",
            "onTransfer", saveData);
        if (result.getErrCode() < 0) {
            this.messageBox_(result);
            return false;
        }

        return true;
    }

    /**
     * 点击确定按钮前检查数据是否合法
     * @return boolean
     */
    public boolean check(){
       if(fromRegion == null ||
          fromRegion.getValue() == null ||
          ("" + fromRegion.getValue()).length() == 0)
           return false;
       if(toRegion == null||
          toRegion.getValue() == null ||
          ("" + toRegion.getValue()).length() == 0)
           return false;
       if(fromBed == null||
          fromBed.getValue() == null ||
          ("" + fromBed.getValue()).length() == 0)
           return false;
       if(toBed == null||
          toBed.getValue() == null ||
          ("" + toBed.getValue()).length() == 0)
           return false;
       return true;
   }

    /**
     * 选择打印
     */
    public void onOK() {
        if(!check())
            return;
        if (!onSave()) {
            this.messageBox("转床错误！");
            return;
        }
        this.setReturnValue("OK");
        this.closeWindow();
    }

    /**
     * 选择取消
     */
    public void onCANCEL() {
        this.setReturnValue("CANCEL");
        this.closeWindow();
    }


    public String getCaseNo() {
        return caseNo;
    }

    public String getMrNo() {
        return mrNo;
    }

    public void setCaseNo(String caseNo) {
        this.caseNo = caseNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    /**
     * 初始化界面数据
     */
    public void initUIData(){
        TParm parm = new TParm();
        parm.setData("CASE_NO",getCaseNo());
        parm = ErdForBedAndRecordTool.getInstance().selERDRegionBedByPat(parm);
        if(parm.getErrCode() < 0)
            return;
        if(parm.getCount() <= 0)
            return;
        setValue("fromRegion",parm.getData("ERD_REGION_CODE",0));
        setValue("fromBed",parm.getData("BED_NO",0));
    }

    //测试用例
    public static void main(String[] args) {
        JavaHisDebug.initClient();
        //JavaHisDebug.TBuilder();

//        JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("erd\\ERDTransferBed.x");
    }


}
