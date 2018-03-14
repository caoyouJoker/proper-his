package com.javahis.ui.udd;

import java.util.ArrayList;
import java.util.List;

import jdo.adm.ADMInpTool;
import jdo.bil.BILTool;
import jdo.ekt.EKTIO;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TCheckBox;
import jdo.util.Manager;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 住院药房西药退药确认
 * </p>
 *
 * <p>
 * Description: 住院药房西药退药确认
 * </p>
 *
 * <p>
 * Copyright: javahis 2008
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author ehui
 * @version 1.0
 */
public class UddReturnConfirm
    extends TControl {
    public static final String NULL = "";
    public static final String N = "N";
    public static final String Y = "Y";
    public TTable tblPat, tblDtl;
    public List execList = new ArrayList();

    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        tblPat = (TTable)this.getComponent("TBL_PAT");
        tblDtl = (TTable)this.getComponent("TBL_DTL");
        tblDtl.addEventListener("TBL_DTL->" + TTableEvent.CHANGE_VALUE, this,
                                "onClickDtl");
        tblDtl.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
                                "onCheckBoxClick");
        onClear();
    }

    /**
     * 清空事件
     */
    public void onClear() {
        this.setValue("EXEC_DEPT_CODE", Operator.getDept()); 
        this.setValue("ORDER_NO", NULL);
        this.setValue("START_DATE", SystemTool.getInstance().getDate());
        this.setValue("END_DATE", SystemTool.getInstance().getDate());
        this.setValue("STATION", NULL);
        this.setValue("MR_NO", Y);
        this.setValue("NO", NULL);
        this.setValue("NAME", NULL);
        this.setValue("UNDONE", Y);
        this.setValue("SHOW_GOODS", N);
        this.callFunction("UI|RTN_NO|setVisible", false);
        //=========pangben 2012-5-25 start
        callFunction("UI|save|setEnabled",
				true);
      //=========pangben 2012-5-25 stop
        tblPat.removeRowAll();
        tblDtl.removeRowAll();
        execList = new ArrayList();
    }

    /**
     * 查询
     */
    public void onQuery() {
    	//add by wangjc 20171129 已完成的退药不允许再修改table中的数值
    	if (TCM_Transform.getBoolean(this.getValue("UNDONE"))) {
    		this.tblDtl.setLockColumns("1,2,3,5,6,8,9,10,11,12");
    	}else{
    		this.tblDtl.setLockColumns("1,2,3,4,5,6,7,8,9,10,11,12,13,14");
    	}
    	//处理mr_no 自动补充0  luhai 2012-04-16 begin 
    	TRadioButton tb = (TRadioButton)this.getComponent("MR_NO");
    	if("true".equals(tb.getValue())){
    		this.setValue("NO", PatTool.getInstance().checkMrno(this.getValue("NO")+""));
    		
    		
    	}
    	//处理mr_no 自动补充0  luhai 2012-04-16 end 
        //执,40,boolean;退药单号,100;床号,80;姓名,80; 护士站,120;   病案号,120;住院号,120
        //EXEC;        ORDER_NO;   BED_NO;PAT_NAME;STATION_CODE;MR_NO;    IPD_NO
        //EXEC;        ORDER_NO;   BED_NO;PAT_NAME;STATION_CODE;MR_NO;    IPD_NO
        String sql = "SELECT 'N' AS EXEC,A.ORDER_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO,A.CASE_NO FROM ODI_DSPNM A ,SYS_PATINFO B";
        String where = getWhere();
        if (StringUtil.isNullString(where)) {
            this.messageBox_("查询条件不全");
            return;
        }
        //===========pangben modify 20110512 start
        String region = "";
        if (null != Operator.getRegion() &&
            Operator.getRegion().length() > 0) {
            region = " AND A.REGION_CODE='" + Operator.getRegion() +
                     "' ";
        }
        //===========pangben modify 20110512 stop

        sql += (" WHERE " + where + region+" AND B.MR_NO=A.MR_NO GROUP BY A.ORDER_NO,A.BED_NO,B.PAT_NAME,A.STATION_CODE,A.MR_NO,A.IPD_NO ,A.CASE_NO");
//        System.out.println("confirm->sql:" + sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        tblPat.removeRowAll();
        tblDtl.removeRowAll();
        tblPat.setParmValue(parm);
        this.setValue("NAME", parm.getValue("PAT_NAME", 0));
        execList = new ArrayList();
    }

    /**
     * 取得查询的where条件
     * @return
     */
    public String getWhere() {
        StringBuffer result = new StringBuffer();
        String startDate = this.getValueString("START_DATE");
        startDate = startDate.substring(0, startDate.lastIndexOf(" ")) +
            "000000";
        String endDate = this.getValueString("END_DATE");
        endDate = endDate.substring(0, endDate.lastIndexOf(" ")) + "235959";
        if (TCM_Transform.getBoolean(this.getValue("UNDONE"))) {
            result.append(" (DSPN_DATE>=TO_DATE('" + startDate +
                          "','YYYY-MM-DD HH24MISS') AND DSPN_DATE<=TO_DATE('" +
                          endDate +
                          "','YYYY-MM-DD HH24MISS')) AND (PHA_RETN_CODE IS NULL OR PHA_RETN_CODE ='') AND ");
        }
        else {
            result.append(" (PHA_RETN_DATE>=TO_DATE('" + startDate +
                          "','YYYY-MM-DD HH24MISS') AND PHA_RETN_DATE<=TO_DATE('" +
                          endDate + "','YYYY-MM-DD HH24MISS')) AND ");
        }
        if (StringUtil.isNullString(this.getValueString("EXEC_DEPT_CODE")) &&
            StringUtil.isNullString(this.getValueString("ORDER_NO"))) {
            this.messageBox_("执行科室不能为空");
            return result.toString();
        }
        if (!StringUtil.isNullString(this.getValueString("ORDER_NO"))) {
            result.append(" ORDER_NO='" + this.getValueString("ORDER_NO") +
                          "' ");
            return result.toString();
        }
        result.append(" (EXEC_DEPT_CODE='" +
                      this.getValueString("EXEC_DEPT_CODE") + "' OR EXEC_DEPT_CODE=(SELECT ORG_CODE FROM IND_ORG WHERE ATC_FLG='Y' AND ATC_ORG_CODE = '"+getValueString("EXEC_DEPT_CODE")+"')) ");
        
//        if (!TCM_Transform.getBoolean(this.getValue("UNDONE"))) {
//            result.append(" AND (PHA_RETN_DATE>=TO_DATE('" + startDate +
//                          "','YYYY-MM-DD HH24MISS') AND PHA_RETN_DATE<=TO_DATE('" +
//                          endDate + "','YYYY-MM-DD HH24MISS'))");
//        }
        
        if (TCM_Transform.getBoolean(this.getValue("STA"))) {
            if (!StringUtil.isNullString(this.getValueString("STATION")))
                result.append(" AND STATION_CODE='" +
                              this.getValueString("STATION") + "' ");
        }
        else if (TCM_Transform.getBoolean(this.getValue("MR_NO"))) {
            if (!StringUtil.isNullString(this.getValueString("NO"))) {
                String mrNo = this.getValueString("NO");
                mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); //======chenxi 
                this.setValue("NO", mrNo);
                
             // modify by huangtt 20160928 EMPI患者查重提示 start
    			Pat pat = Pat.onQueryByMrNo(getValueString("NO"));
    			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
    				messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
    				setValue("NO", pat.getMrNo());
    				mrNo =  pat.getMrNo();
    			}
    			// modify by huangtt 20160928 EMPI患者查重提示 end
                
                result.append(" AND A.MR_NO='" + mrNo + "' ");
            }
        }
        else {
            if (!StringUtil.isNullString(this.getValueString("BED_NO")))
                result.append(" AND BED_NO='" + this.getValueString("NO") +
                              "' ");
        }
        result.append(" AND DSPN_KIND='RT' ");
        return result.toString();
    }

    /**
     * 病患列表点击事件
     */
    public void onTblPatClick() {
        for (int i = 0; i < tblPat.getRowCount(); i++) {
            tblPat.setValueAt(false, i, 0);
        }
        tblPat.setValueAt(true, tblPat.getSelectedRow(), 0);
        int row = tblPat.getSelectedRow();
        TParm parm = tblPat.getParmValue();
        String caseNo = parm.getValue("CASE_NO", row);
        String orderNo = parm.getValue("ORDER_NO", row);
        //=========pangben 2012-5-25 start  出院病人不可以再次操作退药
        if (!checkRecp(caseNo)) {
			callFunction("UI|save|setEnabled",
					false);
			this.messageBox("此病患已出院,不能执行退药操作");
			//return ;
		}else{
			callFunction("UI|save|setEnabled",true);
		}
      //=========pangben 2012-5-25 stop
        String where = "";
        if (TCM_Transform.getBoolean(this.getValue("UNDONE"))) {
            where = " AND A.PHA_RETN_DATE IS NULL ";
        }else{
            where = " AND A.PHA_RETN_DATE IS NOT NULL ";
        }
        //===========pangben modify 20110512 start
        String region = "";
        if (null != Operator.getRegion() &&
            Operator.getRegion().length() > 0) {
            region = " AND A.REGION_CODE='" + Operator.getRegion() +
                     "' ";
        }
        //===========pangben modify 20110512 stop

        /*
         * 序号,50;药嘱,100;实收数,80,double;终止数,80,double;登记数,80,double;
         * 单位,100;终止原因,100;退药原因,100;单价,80,double;金额,80,double;
         * 实际扣库量,100;退药人员,100;退药时间,100
         * ORDER_SEQ;ORDER_DESC;DISPENSE_QTY;CANCEL_DOSAGE_QTY;RTN_DOSAGE_QTY;RTN_DOSAGE_UNIT;CANCELRSN_CODE;TRANSMIT_RSN_CODE;OWN_PRICE;TOT_AMT;PHA_RETN_DATE;PHA_RETN_CODE
         */
        //luhai modify 2012-04-13 加入床号 begin
//        String sql = "SELECT 'N' AS EXEC, A.ORDER_SEQ,A.ORDER_DESC,(A.RTN_DOSAGE_QTY-A.CANCEL_DOSAGE_QTY) AS REAL_QTY," +
//            "A.CANCEL_DOSAGE_QTY,A.RTN_DOSAGE_QTY,A.RTN_DOSAGE_UNIT,A.CANCELRSN_CODE," +
//            "A.TRANSMIT_RSN_CODE," +
////            "CASE WHEN A.PHA_RETN_CODE IS NULL      THEN B.OWN_PRICE      " +
////            "     ELSE A.OWN_PRICE      END OWN_PRICE      ," +
//            "A.OWN_PRICE," +
//            "A.TOT_AMT,A.DISPENSE_QTY,A.PHA_RETN_CODE,A.PHA_RETN_DATE,A.ORDER_NO,A.START_DTTM,A.CASE_NO,A.EXEC_DEPT_CODE,A.ORDER_CODE, A.BED_NO, A.MR_NO, B.PAT_NAME, C.UNIT_CHN_DESC AS UNIT, D.STOCK_PRICE * A.RTN_DOSAGE_QTY AS COST_AMT " +
//            "FROM ODI_DSPNM A, SYS_PATINFO B, SYS_UNIT C, PHA_BASE D  " +
//            //",SYS_FEE B" +
//            " WHERE A.MR_NO = B.MR_NO AND A.RTN_DOSAGE_UNIT = C.UNIT_CODE AND A.ORDER_CODE = D.ORDER_CODE AND A.CASE_NO='" + caseNo + "' AND A.ORDER_NO='" + orderNo +
//            "' " +
//            //"AND B.ORDER_CODE(+)=A.ORDER_CODE";
//            where +region+
//            " ORDER BY ORDER_SEQ ";
        String sql = "SELECT 'N' AS EXEC, A.ORDER_SEQ,A.ORDER_DESC,(A.RTN_DOSAGE_QTY-A.CANCEL_DOSAGE_QTY) AS REAL_QTY," +
            "A.CANCEL_DOSAGE_QTY,A.RTN_DOSAGE_QTY,A.RTN_DOSAGE_UNIT,A.CANCELRSN_CODE," +
            "A.TRANSMIT_RSN_CODE," +
//            "CASE WHEN A.PHA_RETN_CODE IS NULL      THEN B.OWN_PRICE      " +
//            "     ELSE A.OWN_PRICE      END OWN_PRICE      ," +
            "A.OWN_PRICE," +
            "A.TOT_AMT,A.DISPENSE_QTY,A.PHA_RETN_CODE,A.PHA_RETN_DATE,A.ORDER_NO,A.START_DTTM,A.CASE_NO,A.EXEC_DEPT_CODE,A.ORDER_CODE, A.BED_NO, A.MR_NO, B.PAT_NAME, C.UNIT_CHN_DESC AS UNIT, D.STOCK_PRICE * A.RTN_DOSAGE_QTY AS COST_AMT,E.BED_NO_DESC " +
            "FROM ODI_DSPNM A, SYS_PATINFO B, SYS_UNIT C, PHA_BASE D,SYS_BED E   " +
            //",SYS_FEE B" +
            " WHERE A.MR_NO = B.MR_NO AND A.RTN_DOSAGE_UNIT = C.UNIT_CODE AND A.ORDER_CODE = D.ORDER_CODE AND A.CASE_NO='" + caseNo + "' AND A.ORDER_NO='" + orderNo +
            "' " +
            //"AND B.ORDER_CODE(+)=A.ORDER_CODE";
            " AND  A.BED_NO=E.BED_NO  "+
            where +region+
            " ORDER BY ORDER_SEQ ";
        //luhai modify 2012-04-13 加入床号 begin
//        System.out.println("sql---"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            this.messageBox_("取得数据失败");
            return;
        }
        int count = result.getCount("EXEC");
        for (int i = 0; i < count; i++) {
            double ownPrice = result.getDouble("OWN_PRICE", i);
            result.setData("DISPENSE_QTY", i,
                           result.getDouble("RTN_DOSAGE_QTY", i));
            //luhai 2012-04-13 begin
//            double qty = result.getDouble("DISPENSE_QTY", i);
            double qty = result.getDouble("DISPENSE_QTY", i)-result.getDouble("CANCEL_DOSAGE_QTY", i);
            //luhai 2012-04-13 end 
            double amt = ownPrice * qty;
            result.setData("TOT_AMT", i, amt);
        }
        //System.out.println("result============"+result);
        tblDtl.removeRowAll();
        tblDtl.setParmValue(result);
        setCpntValue(parm, row);
    }
    /**
     * 校验是否出院
     * @param caseNo
     * @return
     */
    private  boolean checkRecp(String caseNo){
    	TParm admParm = new TParm();
        admParm.setData("CASE_NO", caseNo);
        TParm selAdmParm = ADMInpTool.getInstance().selectall(admParm);
        String dsdate=selAdmParm.getValue("DS_DATE", 0);
        if(!dsdate.equals("")){
        	return false;
        }
        return true;
    }
    /**
     * 点击病患列表为列头赋值事件
     * @param parm
     * @param row
     */
    public void setCpntValue(TParm parm, int row) {
        String orderNo = parm.getValue("ORDER_NO", row);
        String stationCode = parm.getValue("STATION_CODE", row);
        String name = parm.getValue("PAT_NAME", row);
        TComboBox station = (TComboBox)this.getComponent("DTL_STATION");
        station.setSelectedID(stationCode);
        this.setValue("DTL_ORDER_NO", orderNo);
        this.setValue("DTL_NAME", name);
    }

    /**
     *
     * @param tNode
     * @return 假：成功，真：失败
     */
    public boolean onClickDtl(TTableNode tNode) {
        String cName = tblDtl.getParmMap(tNode.getColumn());
        int row = tNode.getRow();
        //EXEC,CANCEL_DOSAGE_QTY,CANCELRSN_CODE
        //luhai lmdify 加入实际收数量的管控删除退药数量的管控 2012-3-2 begin
        if (!"EXEC".equalsIgnoreCase(cName) &&
            !"REAL_QTY".equalsIgnoreCase(cName) &&
            !"CANCELRSN_CODE".equalsIgnoreCase(cName)) {
            return true;
        }
//        if (!"EXEC".equalsIgnoreCase(cName) &&
//        		!"CANCEL_DOSAGE_QTY".equalsIgnoreCase(cName) &&
//        		!"CANCELRSN_CODE".equalsIgnoreCase(cName)) {
//        	return true;
//        }
      //luhai lmdify 加入实际收数量的管控删除退药数量的管控 2012-3-2 end
        if ("EXEC".equalsIgnoreCase(cName)) {
            String date = StringTool.getString(TJDODBTool.getInstance().
                                               getDBTime(), "yyyy/MM/dd HH:mm");
            tblDtl.getParmValue().setData("PHA_RETN_CODE", row, Operator.getID());
            tblDtl.getParmValue().setData("PHA_RETN_DATE", row, date);
            tblDtl.setValueAt(Operator.getID(), row,
                              tblDtl.getColumnIndex("PHA_RETN_CODE"));
            tblDtl.setValueAt(date, row, tblDtl.getColumnIndex("PHA_RETN_DATE"));
            return false;
        }
        if ("CANCELRSN_CODE".equalsIgnoreCase(cName)) {
            return false;
        }
        if (!tblDtl.getParmValue().getBoolean("EXEC", tNode.getRow())) {
            this.messageBox_("请先点选执行");
            return true;
        }
        double oldValue = tblDtl.getParmValue().getDouble("RTN_DOSAGE_QTY",
            tblDtl.getSelectedRow());
        double value = TCM_Transform.getDouble(tNode.getValue());
        if (oldValue < value) {
            this.messageBox_("数量超过开药数量，请重新输入");
            return true;
        }
        //20151016 wangjc add start
        //实收数量不能为负数
        if (value < 0) {
            this.messageBox_("实收数量不能小于0，请重新输入");
            return true;
        }
        //20151016 wangjc add end
        //***********************************************************************
        //luhai modify 2012-3-2 将原有的可以修改终止数的逻辑改成可以修改实收数的逻辑  begin
        //***********************************************************************
//        double dispenseQty = value;
//        double own_price = tblDtl.getParmValue().getDouble("OWN_PRICE",
//            tNode.getRow());
//        double totAmt = own_price * dispenseQty;
////        this.messageBox((oldValue-dispenseQty)+"");
//        tblDtl.getParmValue().setData("CANCEL_DOSAGE_QTY", tNode.getRow(),
//        		(oldValue-dispenseQty));//(oldValue-dispenseQty)
//        tblDtl.setValueAt((oldValue-dispenseQty),  tNode.getRow(), tblDtl.getColumnIndex("CANCEL_DOSAGE_QTY"));
//        this.messageBox(dispenseQty+"");
//        tblDtl.getParmValue().setData("DISPENSE_QTY", tNode.getRow(),
//        		dispenseQty);
        double dispenseQty = value;
        double own_price = tblDtl.getParmValue().getDouble("OWN_PRICE",
        		tNode.getRow());
        double totAmt = own_price * dispenseQty;
        tblDtl.getParmValue().setData("DISPENSE_QTY", tNode.getRow(),
        		dispenseQty);
        tblDtl.getParmValue().setData("CANCEL_DOSAGE_QTY", tNode.getRow(),(oldValue-dispenseQty));
        tblDtl.setValueAt((oldValue-dispenseQty), tNode.getRow(),
        		tblDtl.getColumnIndex("CANCEL_DOSAGE_QTY"));
//        double dispenseQty = oldValue - value;
//        double own_price = tblDtl.getParmValue().getDouble("OWN_PRICE",
//        		tNode.getRow());
//        double totAmt = own_price * dispenseQty;
//        tblDtl.getParmValue().setData("DISPENSE_QTY", tNode.getRow(),
//        		dispenseQty);
        //***********************************************************************
        //luhai modify 2012-3-2 将原有的可以修改终止数的逻辑改成可以修改实收数的逻辑  end
        //***********************************************************************
        tblDtl.getParmValue().setData("REAL_QTY", tNode.getRow(), dispenseQty);
        tblDtl.getParmValue().setData("TOT_AMT", tNode.getRow(), totAmt);
        //luahi delte 
//        tblDtl.setValueAt(dispenseQty, tNode.getRow(),
//                          tblDtl.getColumnIndex("REAL_QTY"));
        tblDtl.setValueAt(dispenseQty, tNode.getRow(),
                          tblDtl.getColumnIndex("DISPENSE_QTY"));
        tblDtl.setValueAt(totAmt, tNode.getRow(),
                          tblDtl.getColumnIndex("TOT_AMT"));
        //System.out.println("tParm=================="+tblDtl.getParmValue());
        //System.out.println("herererer");
        return false;
    }

    /**
     * 药品详细checkBox点击事件
     * @param obj
     */
    public void onCheckBoxClick(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        boolean value = TCM_Transform.getBoolean(table.getValueAt(table.
            getSelectedRow(), table.getSelectedColumn()));
        if (value) {
            execList.add(table.getSelectedRow());
        }
        else {
            execList.remove( (Object) table.getSelectedRow());
        }
    }

    /**
     * 保存事件
     */
    public void onSave() {
        if (execList == null || execList.size() < 1) {
            return;
        }
        TParm parm = new TParm();
        //luhai 2012-04-16  加入接收表格中正在编辑的值的代码
        // 解决 保存时table还存在编辑状态，用户输入的新值没有保存上的问题begin
        this.tblDtl.acceptText();
        //luhai 2012-04-16  加入接收表格中正在编辑的值的代
        // 解决 保存时table还存在编辑状态，用户输入的新值没有保存上的问题 end
        TParm table = tblDtl.getParmValue();
        //String caseNo, orderNo, startDttm;
        //int orderSeq;
        String userId = Operator.getID();
        String userIp = Operator.getIP();
        for (int i = 0; i < execList.size(); i++) {
            int j = (Integer) execList.get(i);
            //caseNo = table.getValue("CASE_NO", j);
            //orderNo = table.getValue("ORDER_NO", j);
            //startDttm = table.getValue("START_DTTM", j);
            //orderSeq = table.getInt("ORDER_SEQ", j);
            //zhangyong20100719 begin
            String sql = "SELECT * FROM ODI_DSPNM WHERE CASE_NO = '" +
                table.getValue("CASE_NO", j) + "' AND ORDER_NO = '" +
                table.getValue("ORDER_NO", j) + "' AND ORDER_SEQ = " +
                table.getInt("ORDER_SEQ", j) + " AND START_DTTM = '" +
                table.getValue("START_DTTM", j) + "'";

            TParm odi_dspnm = new TParm(TJDODBTool.getInstance().select(sql));
            if (odi_dspnm.getCount("PHA_RETN_CODE") > 0 &&
                odi_dspnm.getData("PHA_RETN_CODE", 0) != null &&
                !"".equals(odi_dspnm.getValue("PHA_RETN_CODE", 0))) {
                this.messageBox("药品已经退药，请重新查询");
                return;
            }

            //20151016 wangjc add start
            //实收数量不能为负数
            if (table.getDouble("REAL_QTY", j) < 0) {       
                this.messageBox("第"+(j+1)+"行,实收数为负数，请重新输入!");    
                return;    
            }
            //20151016 wangjc add end
            //zhangyong20101228 begin
            String pha_sql = "SELECT STOCK_PRICE FROM PHA_BASE WHERE ORDER_CODE = '" +
                odi_dspnm.getValue("ORDER_CODE", 0) + "'";
            TParm pha_parm = new TParm(TJDODBTool.getInstance().select(pha_sql));
            double cost_amt = pha_parm.getDouble("STOCK_PRICE", 0) *
                table.getDouble("RTN_DOSAGE_QTY", j);
            //zhangyong20101228 end

            String[] columnd = odi_dspnm.getNames();
            for (int k = 0; k < columnd.length; k++) {
                if ("OPT_USER".equals(columnd[k]) ||
                    "OPT_TERM".equals(columnd[k]) ||
                    "PHA_RETN_CODE".equals(columnd[k]) ||
                    "TOT_AMT".equals(columnd[k]) ||
                    "ORDER_DR_CODE".equals(columnd[k]) ||
                    "ORDER_DEPT_CODE".equals(columnd[k]) ||
                    "OWN_AMT".equals(columnd[k])) {
                    continue;
                }
                if ("DEPT_CODE".equals(columnd[k])) {
                    parm.addData("ORDER_DEPT_CODE",
                                 odi_dspnm.getData(columnd[k], 0));
                    parm.addData("DEPT_CODE",
                                 odi_dspnm.getData(columnd[k], 0));
                    continue;
                }
                if ("VS_DR_CODE".equals(columnd[k])) {
                    parm.addData("ORDER_DR_CODE",
                                 odi_dspnm.getData(columnd[k], 0));
                    continue;
                }
                if ("RT_KIND".equals(columnd[k])) {// add by wanglong 20130628
                    if (odi_dspnm.getValue(columnd[k], 0).equals("DS")) {
                        parm.addData("DS_FLG", "Y");
                    }
                    continue;
                }
                parm.addData(columnd[k], odi_dspnm.getData(columnd[k], 0));
            }
            //zhangyong20100719 end

            //parm.addData("CASE_NO", table.getValue("CASE_NO", j));
            //parm.addData("ORDER_NO", table.getValue("ORDER_NO", j));
            //parm.addData("EXEC_DEPT_CODE", table.getValue("EXEC_DEPT_CODE", j));
            //parm.addData("ORDER_CODE", table.getValue("ORDER_CODE", j));
            //parm.addData("START_DTTM", table.getValue("START_DTTM", j));
            //parm.addData("ORDER_SEQ", table.getInt("ORDER_SEQ", j));
            //parm.addData("OWN_PRICE", table.getDouble("OWN_PRICE", j));
            //luhai modify 2012-04-13 begin 
//            parm.addData("TOT_AMT",
//                         table.getDouble("OWN_PRICE", j) *
//                         table.getDouble("RTN_DOSAGE_QTY", j));
//            parm.addData("OWN_AMT",
//                         table.getDouble("OWN_PRICE", j) *
//                         table.getDouble("RTN_DOSAGE_QTY", j));
            parm.addData("TOT_AMT",
                         StringTool.round(table.getDouble("OWN_PRICE", j)*
                         (table.getDouble("RTN_DOSAGE_QTY", j)-table.getDouble("CANCEL_DOSAGE_QTY", j)),2));
            parm.addData("OWN_AMT",
            		StringTool.round(table.getDouble("OWN_PRICE", j) *
                         (table.getDouble("RTN_DOSAGE_QTY", j)-table.getDouble("CANCEL_DOSAGE_QTY", j)),2));
            //luhai modify 2012-04-13 end  
            //取消数量 luhai 2012-3-3 add( 根据页面输入值覆盖原有值)
            parm.setData("CANCEL_DOSAGE_QTY",j,
                         table.getDouble("CANCEL_DOSAGE_QTY", j));
//            this.messageBox(table.getDouble("CANCEL_DOSAGE_QTY", j)+"");
//            System.out.println("@@@****************CANCEL:"+table.getDouble("CANCEL_DOSAGE_QTY", j));
            //parm.addData("CANCELRSN_CODE", table.getValue("CANCELRSN_CODE", j));
            //parm.addData("DISPENSE_QTY", table.getDouble("DISPENSE_QTY", j));
            parm.addData("OPT_USER", userId);
            parm.addData("OPT_TERM", userIp);
            parm.addData("PHA_RETN_CODE", userId);
            parm.addData("COST_AMT", StringTool.round(cost_amt, 2));
        }
        //zhangyong20110516 添加区域REGION_CODE
        parm.setData("REGION_CODE", Operator.getRegion());
        
		if("Y".equals(Operator.getSpcFlg())) {
			parm.setData("SPC_FLG","Y");			
		}else {
			parm.setData("SPC_FLG","N");
		}
        //parm.setData("SPC_FLG", Operator.getSpcFlg());// 国药开关 shibl add 20130423
        //System.out.println("parm ofr save============="+parm);
        parm = TIOM_AppServer.executeAction(
            "action.udd.UddRtnRgsAction", "onUpdateRtnCfm", parm);
        if (parm.getErrCode() != 0) {
            //this.messageBox("E0001");
            this.messageBox_(parm.getErrText());
        }
        else {
            this.messageBox("P0001");
            onPrint();
            onClear();
        }
    }

    /**
     * 打印退药确认单
     */
    public void onPrint() {
        TTable table = getTable("TBL_DTL");
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("TITLE", "TEXT", "住院退药确认单");
        if ("".equals(this.getValueString("STATION"))) {
            date.setData("STATION_CODE", "TEXT", "统计部门: 全院");
        }
        else {
            date.setData("STATION_CODE", "TEXT", "统计部门: " + this.getText("STATION"));
        }
        date.setData("DATE_AREA", "TEXT", "退药时间:" +
                     getValue("START_DATE").toString().substring(0, 10).
                     replace('-', '/') + "~" +
                     getValue("END_DATE").toString().
                     substring(0, 10).replace('-', '/'));
        date.setData("RET_NO", "TEXT", "退药单号:" + this.getValueString("DTL_ORDER_NO"));
        // 表格数据
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();
        //System.out.println("tableParm----"+tableParm);
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!"Y".equals(tableParm.getValue("EXEC", i))) {
                continue;
            }
            //luhai modify 2012-04-13 begin
//            parm.addData("BED_NO", tableParm.getValue("BED_NO", i));
            parm.addData("BED_NO_DESC", tableParm.getValue("BED_NO_DESC", i));
            //luhai modify 2012-04-13 end
            parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
            parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
            parm.addData("ORDER_SEQ", tableParm.getInt("ORDER_SEQ", i));
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("RTN_DOSAGE_QTY",
                         tableParm.getDouble("RTN_DOSAGE_QTY", i) == 0 ?
                         tableParm.getDouble("RTN_DOSAGE_QTY", i) :
                         tableParm.getDouble("RTN_DOSAGE_QTY", i) * -1.0);
            parm.addData("CANCEL_DOSAGE_QTY",
                         tableParm.getDouble("CANCEL_DOSAGE_QTY", i) == 0 ?
                         tableParm.getDouble("CANCEL_DOSAGE_QTY", i) :
                         tableParm.getDouble("CANCEL_DOSAGE_QTY", i) * -1.0);
            parm.addData("REAL_QTY",
                         tableParm.getDouble("REAL_QTY", i) == 0 ?
                         tableParm.getDouble("REAL_QTY", i) :
                         tableParm.getDouble("REAL_QTY", i) * -1.0);
            parm.addData("UNIT", tableParm.getValue("UNIT", i));
            parm.addData("OWN_PRICE", tableParm.getDouble("OWN_PRICE", i));
            parm.addData("TOT_AMT",
                         tableParm.getDouble("TOT_AMT", i) == 0 ?
                         StringTool.round(tableParm.getDouble("TOT_AMT", i),2) :
                         StringTool.round(tableParm.getDouble("TOT_AMT", i) * -1.0,2));
        }

        if (parm.getCount("ORDER_DESC") <= 0) {
            this.messageBox("没有打印数据");
            return;
        }

        parm.setCount(parm.getCount("ORDER_DESC"));
        //luhai modify 2012-04-13 begin
//        parm.addData("SYSTEM", "COLUMNS", "BED_NO");
        parm.addData("SYSTEM", "COLUMNS", "BED_NO_DESC");
        //luhai modify 2012-04-13 end
        parm.addData("SYSTEM", "COLUMNS", "MR_NO");
        parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
        parm.addData("SYSTEM", "COLUMNS", "ORDER_SEQ");
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parm.addData("SYSTEM", "COLUMNS", "RTN_DOSAGE_QTY");
        parm.addData("SYSTEM", "COLUMNS", "CANCEL_DOSAGE_QTY");
        parm.addData("SYSTEM", "COLUMNS", "REAL_QTY");
        parm.addData("SYSTEM", "COLUMNS", "UNIT");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
        date.setData("TABLE", parm.getData());
       // System.out.println("parm----"+parm);
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\UDD\\UddReturnConfirm.jhw",
                             date);
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
	 * 医疗卡读卡
	 * 2012-2-27 luhai 
	 */
	public void onEKT() {
		TParm parm = EKTIO.getInstance().TXreadEKT();
        //System.out.println("parm==="+parm);
    	if (null == parm || parm.getValue("MR_NO").length() <= 0) {
            this.messageBox("请查看医疗卡是否正确使用");
            return;
        }
    	//zhangp 20120130
    	if(parm.getErrCode()<0){
    		messageBox(parm.getErrText());
    	}
		setValue("NO", parm.getValue("MR_NO"));
		TRadioButton td=(TRadioButton)this.getComponent("MR");
		td.setSelected(true);
		this.onQuery();
		//修改读医疗卡功能  end luhai 2012-2-27 
	}
	/**
	 * 
	 * 全选
	 */
	public void onSelectAll(){
		TCheckBox tbc= (TCheckBox)this.getComponent("SELECT_ALL");
		execList = new ArrayList();
		if("Y".equals(tbc.getValue())){
		int rowCount = this.tblDtl.getRowCount();
		for(int i=0;i<rowCount;i++){
			this.tblDtl.setItem(i, "EXEC", "Y");
			execList.add(i);
		}
		}else{
			int rowCount = this.tblDtl.getRowCount();
			for(int i=0;i<rowCount;i++){
				this.tblDtl.setItem(i, "EXEC", "N");
			}
			
		}
		
		
	}

}
