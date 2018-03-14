package com.javahis.ui.ekt;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.ekt.EKTIO;
import jdo.ekt.EKTTool;
import com.dongyang.ui.TTable;
import jdo.adm.ADMTool;
import jdo.adm.ADMInpTool;
import jdo.sys.Pat;
import com.dongyang.ui.TTextField;
import javax.swing.SwingUtilities;
import java.sql.Timestamp;
import com.dongyang.ui.TMenuItem;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import jdo.bil.BILGreenPathTool;
import jdo.sys.SystemTool;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import jdo.reg.PatAdmTool;
import jdo.opb.OPB;
import jdo.reg.Reg;
import jdo.ekt.EKTGreenPathTool;

/**
 * <p>Title: 医疗卡绿色通道</p>
 *
 * <p>Description: 医疗卡绿色通道</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20111008
 * @version 1.0
 */
public class EKTGreenPathControl extends TControl{
	private TParm parmEKT;//医疗卡读卡数据
    public EKTGreenPathControl() {
    }
    TParm data;
    int selectRow = -1;
    TParm accptDate = new TParm();
    private boolean isSave=false;//管控是否已经执行查询操作

    public void onInit() {
        super.onInit();
        Timestamp now = SystemTool.getInstance().getDate();
        setValue("APPLY_DATE", now); //预定日期
        setValue("APPROVE_DATE", now); //预定日期
        setValue("APPLY_USER", Operator.getID());
        setValue("APPROVE_USER",Operator.getID());
        ( (TTable) getComponent("TABLE")).addEventListener("TABLE->"
            + TTableEvent.CLICKED, this, "onTableClicked");
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            accptDate = (TParm) obj;
        }
        setValue("ADM_TYPE", accptDate.getData("ADM_TYPE"));
        if (accptDate.getData("MR_NO") == null ||
            "".equals(accptDate.getData("MR_NO")))
            setValue("MR_NO", "");
        else {
            String mr_no = accptDate.getData("MR_NO").toString();
            this.setValue("MR_NO", mr_no);
           // onMrNo();
        }
    }

    /**
     * 增加对Table的监听
     *
     * @param row
     *            int
     */
    public void onTableClicked(int row) {
        // 选中行
        if (row < 0)
            return;
        setValueForParm(
            "ADM_TYPE;CASE_NO;MR_NO;PAT_NAME;APPLY_DATE;APPLY_AMT;APPLY_USER;DESCRIPTION;APPLY_CAUSE;CANCLE_FLG;APPROVE_DATE;APPROVE_AMT;APPROVE_USER",
            data, row);
        selectRow = row;
        // 不可编辑
        ( (TTextField) getComponent("MR_NO")).setEnabled(false);
        // 设置删除按钮状态
        ( (TMenuItem) getComponent("delete")).setEnabled(true);
    }

    /**
     * 病案号查询
     */
    public void onMrNo() {
        Pat pat = new Pat();
        String mrNo = getValue("MR_NO").toString().trim();
        pat = pat.onQueryByMrNo(mrNo);
        if (pat == null) {
            this.messageBox("查无病患！");
            return;
        }
        TParm parm=new TParm();
        parm.setData("MR_NO", pat.getMrNo());
        if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
            parm.setData("REGION_CODE", Operator.getRegion());
        TParm check =PatAdmTool.getInstance().selEKTByMrNo(parm);
        if (check.getData("MR_NO", 0) == null ||
            "".equals(check.getData("MR_NO", 0))) {
            this.messageBox("此病患没有执行挂号操作！");
            this.onClear();
            return;
        }

        if(check.getCount()==1){
            afterInitOpb(check,pat);
            return;
        }
       onRecord(check, pat);
    }
    /**
     *   执行显示数据操作
     */
    private void afterInitOpb(TParm caseNo,Pat pat) {
        this.setValue("MR_NO", pat.getMrNo());
        this.setValue("CASE_NO", caseNo.getData("CASE_NO", 0));
        this.setValue("PAT_NAME", pat.getName());
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    onQuery();
                } catch (Exception e) {
                }
            }
        });
        isSave=true;//执行查询操作
        this.grabFocus("APPLY_AMT");
        // 不可编辑
        ((TTextField) getComponent("MR_NO")).setEnabled(false);

    }
    /**
    * 就诊记录选择
    */
   public void onRecord(TParm check,Pat pat) {
       //初始化pat
       pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
       if (pat == null) {
           messageBox_("查无此病案号!");
           //若无此病案号则不能查找挂号信息
           callFunction("UI|record|setEnabled", false);
           return;
       }
       TParm parm = new TParm();
       parm.setData("MR_NO", pat.getMrNo());
       parm.setData("PAT_NAME", pat.getName());
       parm.setData("SEX_CODE", pat.getSexCode());
       //判断是否从明细点开的就诊号选择
       parm.setData("count", "0");
       String caseNo = (String) openDialog(
           "%ROOT%\\config\\ekt\\EKTChooseVisit.x", parm);
       if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null")) {
           return;
       }
       Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
       if (reg == null) {
           messageBox("挂号信息错误!");
           return;
       }
       check=new TParm();
       check.addData("CASE_NO", reg.caseNo());
       //reg得到的数据放入界面
       //通过reg和caseNo得到pat
       //初始化opb后数据处理
       afterInitOpb(check,pat);
   }

    /**
     * 新增
     */
    public void onInsert() {
        TParm parm = new TParm();
        parm.setData("CASE_NO",this.getValueString("CASE_NO"));
        parm.setData("MR_NO",this.getValueString("MR_NO"));
        parm.setData("PAT_NAME",this.getValueString("PAT_NAME"));
        parm.setData("ADM_TYPE",this.getValueString("ADM_TYPE"));
        parm.setData("APPLY_AMT",this.getValueString("APPLY_AMT"));
        parm.setData("APPLY_USER",this.getValueString("APPLY_USER"));
        parm.setData("DESCRIPTION",this.getValueString("DESCRIPTION"));
        parm.setData("APPLY_CAUSE",this.getValueString("APPLY_CAUSE"));
        parm.setData("APPLY_DATE", getValue("APPLY_DATE"));
        parm.setData("APPROVE_DATE", getValue("APPROVE_DATE"));
        parm.setData("APPROVE_USER", getValue("APPROVE_USER"));
        parm.setData("APPROVE_AMT", getValue("APPROVE_AMT"));
        parm.setData("CANCLE_FLG", "N");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(
            "action.ekt.EKTAction",
            "insertData", parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        //校验是否停止划价
        TParm checkStopFee = ADMTool.getInstance().checkStopFee(this.
            getValueString("CASE_NO"));
        if (checkStopFee.getErrCode() < 0) {
            err(checkStopFee.getErrCode() + " " + checkStopFee.getErrText());
            return;
        }
        //onMrNo();
        this.messageBox("P0001");
    }

    /**
     * 更新
     */
    public void onUpdate() {
        TTable table = (TTable) getComponent("TABLE");
        int row = table.getSelectedRow();
        //判断是否已经作废
        if(table.getValueAt(row,11).toString().equals("Y")){
            this.messageBox_("已经作废不可修改");
            return;
        }
        TParm parm = getParmForTag("CASE_NO;MR_NO;PAT_NAME;ADM_TYPE;APPLY_AMT;APPLY_USER;DESCRIPTION;APPLY_CAUSE");
        parm.setData("APPLY_DATE", getValue("APPLY_DATE"));
        parm.setData("APPROVE_DATE", getValue("APPROVE_DATE"));
        parm.setData("APPROVE_USER", getValue("APPROVE_USER"));
        parm.setData("APPROVE_AMT", getValue("APPROVE_AMT"));
        parm.setData("CANCLE_FLG", "N");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = EKTGreenPathTool.getInstance().updatedata(parm);
        // 判断错误值
        if (result.getErrCode() < 0) {
            messageBox(result.getErrText());
            return;
        }
        if (row < 0)
            return;
        // 刷新，设置末行某列的值
        data.setRowData(row, parm);
        ( (TTable) getComponent("TABLE")).setRowParmValue(row, data);
        this.messageBox("P0005");
    }

    /**
     * 保存
     */
    public void onSave() {
        if (!this.getPopedem("LEADER")) {
           this.messageBox("非组长不能添加绿色通道数据!");
           return;
       }
       if(!isSave){
           this.messageBox("请查询此病患是否存在挂号信息");
           return;
       }
        if(!checkSaveData()){
            return;
        }
        onInsert();
        onClear();
    }

    /**
     * 删除
     */
    public void onDelete() {
        if (!this.getPopedem("LEADER")) {
            this.messageBox("非组长不能删除绿色通道数据!");
            return;
        }
        if (selectRow == -1) {
            return;
        }
        TParm parm = data.getRow(selectRow);
        if (parm.getValue("CANCLE_FLG").equals("Y")) {
            this.messageBox("已经作废");
            return;
        }
        if (this.messageBox("提示", "确认要作废该条绿色通道申请吗？", 2) == 0) {

            parm.setData("APPLY_DATE",StringTool.getString(data.getTimestamp("APPLY_DATE",selectRow),"yyyyMMddHHmmss"));
            parm.setData("OPT_USER",Operator.getID());
            parm.setData("OPT_TERM",Operator.getIP());
            if(!checkGreenPath(parm)){
                this.messageBox_("不可作废");
                return;
            }
            TParm result = TIOM_AppServer.executeAction(
            "action.ekt.EKTAction",
            "cancelGreenPath", parm);
            if (result.getErrCode() < 0) {
                messageBox(result.getErrText());
                return;
            }
            TTable table = ( (TTable) getComponent("TABLE"));
            int row = table.getSelectedRow();
            if (row < 0)
                return;
            this.messageBox("P0005");
          //  this.clearValue("APPLY_AMT;APPLY_CAUSE;DESCRIPTION;APPROVE_USER");
            //Timestamp now = SystemTool.getInstance().getDate();
//            setValue("APPLY_DATE", now); //预定日期
//            setValue("APPROVE_DATE", now); //预定日期
//            setValue("APPLY_USER", Operator.getID());
//            setValue("APPROVE_USER", Operator.getID());
            //this.onMrNo();
            selectRow=-1;
            onClear();
        }
        else {
            return;
        }
    }

    /**
     * 查询
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("MR_NO", getValueString("MR_NO"));
        parm.setDataN("CASE_NO", getValueString("CASE_NO"));
        data = EKTGreenPathTool.getInstance().selectGreenPath(parm);
        // 判断错误值
        if (data.getErrCode() < 0) {
            messageBox(data.getErrText());
            return;
        }
        ( (TTable) getComponent("TABLE")).setParmValue(data);
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue(
            "MR_NO;CASE_NO;PAT_NAME;APPLY_AMT;APPROVE_AMT;APPLY_CAUSE;DESCRIPTION");
        ( (TTable) getComponent("TABLE")).clearSelection();
        selectRow = -1;
        // 设置删除按钮状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TTextField) getComponent("MR_NO")).setEnabled(true);
        onQuery();
        setValue("APPLY_DATE", SystemTool.getInstance().getDate()); //预定日期
        setValue("APPLY_USER", Operator.getID());
        isSave=false;
    }
    /**
     * 检核保存数据
     * @return boolean
     */
    public boolean checkSaveData(){
        //检查申请日期不可为空
        if(this.getValue("APPLY_DATE")==null){
            this.messageBox_("请选择申请日期");
            this.grabFocus("APPLY_DATE");
            return false;
        }
        //检查申请费用
        if(!(this.getValueString("APPLY_AMT").length()>0&&Double.valueOf(this.getValueString("APPLY_AMT"))!=0)){
            this.messageBox_("请填写申请金额");
            this.grabFocus("APPLY_AMT");
            return false;
        }
        //检查申请人员
        if(this.getValueString("APPLY_USER").length()<=0){
            this.messageBox_("请选择申请人员");
            this.grabFocus("APPLY_USER");
            return false;
        }
        //检查批准日期不可为空
        if(this.getValue("APPROVE_DATE")==null){
            this.messageBox_("请选择批准日期");
            this.grabFocus("APPROVE_DATE");
            return false;
        }
        //检查批准费用
        if(!(this.getValueString("APPROVE_AMT").length()>0&&Double.valueOf(this.getValueString("APPROVE_AMT"))!=0)){
            this.messageBox_("请填写批准金额");
            this.grabFocus("APPROVE_AMT");
            return false;
        }
        //检查批准人员
        if(this.getValueString("APPROVE_USER").length()<=0){
            this.messageBox_("请选择批准人员");
            this.grabFocus("APPROVE_USER");
            return false;
        }
        return true;
    }
    /**
     * 查询REG_PATADM中的绿色通道的值 是否大于要作废的金额，如果小于要作废的金额不可作废
     * @param CASE_NO String
     * @param greenPath double
     * @return boolean  true:可以作废   false:不可作废
     */
    private boolean checkGreenPath(TParm parm){
        TParm result = new TParm();
        result.setData("MR_NO",parm.getValue("MR_NO"));
        result.setData("CASE_NO",parm.getValue("CASE_NO"));
        TParm adm = PatAdmTool.getInstance().selEKTByMrNo(parm);
        //比较扣款金额是否大于申请总金额
        if(adm.getDouble("GREEN_BALANCE",0)>= adm.getDouble("GREEN_PATH_TOTAL",0))
            return true;
        //比较扣款金额是否大于此次退费的金额
        else if(adm.getDouble("GREEN_BALANCE",0) >= parm.getDouble("APPROVE_AMT"))
            return true;
        else
            return false;
    }
    /**
     * 读卡操作
     */
    public void onReadEktCard(){
    	parmEKT = EKTIO.getInstance().TXreadEKT();
		if (null == parmEKT || parmEKT.getErrCode() < 0
				|| parmEKT.getValue("MR_NO").length() <= 0) {
			this.messageBox(parmEKT.getErrText());
			return;
		}
		this.setValue("MR_NO", parmEKT.getValue("MR_NO"));
		onMrNo();
    }
    /**
     * 预约挂号操作
     */
    public void onSaveReg(){
    	if (null==this.getValue("ADM_TYPE")|| this.getValue("ADM_TYPE").toString().length()<=0) {
			this.messageBox("请输入门级别");
			this.grabFocus("ADM_TYPE");
			return;
		}
    	TParm parm=new TParm();
    	parm.setData("ADM_TYPE",this.getValue("ADM_TYPE"));
    	parm.setData("MR_NO",this.getValue("MR_NO"));
    	TParm regParm = (TParm) openDialog(
				"%ROOT%\\config\\reg\\REGBespeak.x", parm);
    }
}
