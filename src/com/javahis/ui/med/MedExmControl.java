package com.javahis.ui.med;

import com.dongyang.control.*;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import jdo.sys.PatTool;
import com.dongyang.data.TParm;
import com.javahis.util.OdiUtil;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import jdo.med.ExecRecordDataStore;
import com.dongyang.ui.TTableNode;
import com.javahis.util.StringUtil;
import com.dongyang.data.TNull;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class MedExmControl
    extends TControl {
    /**
     * TABLE
     */
    private static String TABLE1="TABLE1";
    /**
     * TABLE
     */
    private static String TABLE2="TABLE2";


    /**
     * 初始化参数
     */
    public void onInitParameter(){
        this.setPopedem("deptEnabled", true);
    }

    public void onInit() {
        super.onInit();
        onInitPopeDem();
        onPageInit();
    }
    /**
     * 页面初始化
     */
    public void onPageInit(){
        Timestamp sysDate = SystemTool.getInstance().getDate();
        //默认设置起始日期
        this.setValue("START_DATE", sysDate);
        //默认设置终止日期
        this.setValue("END_DATE", StringTool.rollDate(sysDate, 1));
        //设置科室
        this.setValue("DEPT_CODE", Operator.getDept());
        //TABLE1双击事件
        callFunction("UI|" + TABLE1 + "|addEventListener",TABLE1 + "->" + TTableEvent.CLICKED, this, "onTableClicked");
        //TABLE2值改变事件
        addEventListener(TABLE2+"->"+TTableEvent.CHANGE_VALUE, this,
                                   "onChangeTableValue");

        //TABLE2COMBOX值改变事件
        getTTable(TABLE2).addEventListener(TTableEvent.CHECK_BOX_CLICKED,this,"onCheckBoxValue");

    }
    /**
     * 右击MENU弹出事件
     * @param tableName
     */
    public void showPopMenu(String tableName){
       int selRow = this.getTTable(tableName).getSelectedRow();
       TParm orderP = this.getTTable(tableName).getParmValue().getRow(selRow);
        if ("Y".equals(orderP.getValue("SETMAIN_FLG"))) {
            this.getTTable(tableName).setPopupMenuSyntax("显示集合医嘱细相,openRigthPopMenu");
            return;
        }
        if ("N".equals(orderP.getValue("SETMAIN_FLG"))) {
            this.getTTable(tableName).setPopupMenuSyntax("");
            return;
        }
    }
    /**
     * 打开集合医嘱细想查询
     */
    public void openRigthPopMenu(){
        int selRow = this.getTTable(TABLE1).getSelectedRow();
        TParm orderP = this.getTTable(TABLE1).getParmValue().getRow(selRow);
        int groupNo = orderP.getInt("ORDERSET_GROUP_NO");
        String orderCode = orderP.getValue("ORDER_CODE");
        String caseNo = orderP.getValue("CASE_NO");
        String orderNo = orderP.getValue("ORDER_NO");
        String admType = orderP.getValue("ADM_TYPE");
        TParm parm = getOrderSetDetails(admType,groupNo, orderCode,orderNo,caseNo);
        this.openDialog("%ROOT%\\config\\opd\\OPDOrderSetShow.x", parm);
    }
    /**
         * 返回集合医嘱细相的TParm形式
         * @return result TParm
         */
        public TParm getOrderSetDetails(String admType,int groupNo, String orderSetCode,String orderNo,String caseNo) {
            TParm result = new TParm();
            if (groupNo < 0) {
                // System.out.println(
                //    "OpdOrder->getOrderSetDetails->groupNo is invalie");
                return result;
            }
            if (StringUtil.isNullString(orderSetCode)) {
                // System.out.println(
              //      "OpdOrder->getOrderSetDetails->orderSetCode is invalie");
                return result;
            }
            TParm parm = new TParm();
            if("O".equals(admType)){
              parm = new TParm(this.getDBTool().select("SELECT * FROM OPD_ORDER WHERE CASE_NO='"+caseNo+"' AND RX_NO='"+orderNo+"' AND ORDERSET_CODE='"+orderSetCode+"' AND ORDERSET_GROUP_NO='"+groupNo+"'"));
            }
            if("I".equals(admType)){
              parm = new TParm(this.getDBTool().select("SELECT * FROM ODI_ORDER WHERE CASE_NO='"+caseNo+"' AND ORDER_NO='"+orderNo+"' AND ORDERSET_CODE='"+orderSetCode+"' AND ORDERSET_GROUP_NO='"+groupNo+"'"));
            }

            int count = parm.getCount();
            if (count < 0) {
                return result;
            }
            String tempCode;
            int tempNo;
            //temperr细项价格
            for (int i = 0; i < count; i++) {
                tempCode = parm.getValue("ORDERSET_CODE", i);
                tempNo = parm.getInt("ORDERSET_GROUP_NO", i);
//            // System.out.println("tempCode==========" + tempCode);
//            // System.out.println("tempNO============" + tempNo);
//            // System.out.println("setmain_flg========" + parm.getBoolean("SETMAIN_FLG", i));
                if (tempCode.equalsIgnoreCase(orderSetCode) && tempNo == groupNo &&
                    !parm.getBoolean("SETMAIN_FLG", i)) {
                    //ORDER_DESC;SPECIFICATION;MEDI_QTY;MEDI_UNIT;OWN_PRICE_MAIN;OWN_AMT_MAIN;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
                    result.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
                    result.addData("SPECIFICATION",
                                   parm.getValue("SPECIFICATION", i));
                    result.addData("DOSAGE_QTY", parm.getValue("DOSAGE_QTY", i));
                    result.addData("MEDI_UNIT", parm.getValue("MEDI_UNIT", i));
                    //查询单价
                    TParm ownPriceParm = new TParm(this.getDBTool().select("SELECT OWN_PRICE FROM SYS_FEE WHERE ORDER_CODE='"+parm.getValue("ORDER_CODE", i)+"'"));
//                this.messageBox_(ownPriceParm);
                    //计算总价格
                    double ownPrice = ownPriceParm.getDouble("OWN_PRICE",0)* parm.getDouble("MEDI_QTY", i);
                    result.addData("OWN_PRICE", ownPriceParm.getDouble("OWN_PRICE",0));
                    result.addData("OWN_AMT", ownPrice);
                    result.addData("EXEC_DEPT_CODE",
                                   parm.getValue("EXEC_DEPT_CODE", i));
                    result.addData("OPTITEM_CODE", parm.getValue("OPTITEM_CODE", i));
                    result.addData("INSPAY_TYPE", parm.getValue("INSPAY_TYPE", i));
                }
            }
            return result;
    }

    /**
     * 临时医嘱修改事件监听
     * @param obj Object
     */
    public boolean onChangeTableValue(Object obj){
        //拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
        TTableNode node = (TTableNode) obj;
        if (node == null)
           return true;
       //如果改变的节点数据和原来的数据相同就不改任何数据
       // System.out.println("old=="+node.getOldValue());
       // System.out.println("new=="+node.getValue());
       if (node.getValue()==null||node.getValue().equals(node.getOldValue()))
           return true;
       //拿到table上的parmmap的列名
       String columnName = node.getTable().getDataStoreColumnName(node.getColumn());
       //判断当前列是否有医嘱
       int selRow = node.getRow();
       TParm orderP = this.getTTable(TABLE2).getDataStore().getRowParm(selRow);
       int id = (Integer)this.getTTable(TABLE2).getDataStore().getItemData(selRow,"#ID#");
       //EXEC_DATE;EXEC_STATUS;EXEC_USER;DEVICE_ID;REMARK
//       this.messageBox_(node.getValue()+"=="+columnName);
       if("Y".equals(orderP.getValue("SETMAIN_FLG"))){
           String buff = this.getTTable(TABLE2).getDataStore().isFilter()?this.getTTable(TABLE2).getDataStore().FILTER:this.getTTable(TABLE2).getDataStore().PRIMARY;
           int rowCount = this.getTTable(TABLE2).getDataStore().getBuffer(buff).getCount();
//           this.messageBox_(rowCount);
           for(int i=0;i<rowCount;i++){
               if((Integer)this.getTTable(TABLE2).getDataStore().getItemData(i,"#ID#",buff)!=id){
                   // System.out.println(orderP.getValue("EXEC_NO"));
                   // System.out.println(this.getTTable(TABLE2).getDataStore().getItemData(i,"EXEC_NO",buff).toString());
                   // System.out.println(orderP.getValue("EXEC_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"EXEC_NO",buff).toString()));
                   if(orderP.getValue("EXEC_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"EXEC_NO",buff).toString())&&orderP.getValue("MR_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"MR_NO",buff).toString())&&orderP.getValue("CASE_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"CASE_NO",buff).toString())&&orderP.getValue("RX_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"RX_NO",buff).toString())&&orderP.getValue("ORDERSET_CODE").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"ORDERSET_CODE",buff).toString())&&orderP.getValue("ORDERSET_GROUP_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"ORDERSET_GROUP_NO",buff).toString())){
                       this.getTTable(TABLE2).getDataStore().setItem(i,columnName,node.getValue(),buff);
                   }
               }
           }
       }
       // System.out.println(this.getTTable(TABLE2).getDataStore().getRowParm(selRow));
       return false;
   }

    public void onCheckBoxValue(Object obj){
        TTable table = (TTable)obj;
        table.acceptText();
        int col = table.getSelectedColumn();
        String columnName = this.getTTable(TABLE2).getDataStoreColumnName(col);
        int row = table.getSelectedRow();
        TParm linkParm = table.getDataStore().getRowParm(row);
        int id = (Integer)this.getTTable(TABLE2).getDataStore().getItemData(row,"#ID#");
        String buff = this.getTTable(TABLE2).getDataStore().isFilter()?this.getTTable(TABLE2).getDataStore().FILTER:this.getTTable(TABLE2).getDataStore().PRIMARY;
        //EXEC_DATE;EXEC_STATUS;EXEC_USER;DEVICE_ID;REMARK
        if("Y".equals(linkParm.getValue("SETMAIN_FLG"))){
            if("EXEC_STATUS".equals(columnName)){
               int rowCount = this.getTTable(TABLE2).getDataStore().getBuffer(buff).getCount();
               for(int i=0;i<rowCount;i++){
                   if((Integer)this.getTTable(TABLE2).getDataStore().getItemData(i,"#ID#",buff)!=id){
                       if(linkParm.getValue("EXEC_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"EXEC_NO",buff).toString())&&linkParm.getValue("MR_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"MR_NO",buff).toString())&&linkParm.getValue("CASE_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"CASE_NO",buff).toString())&&linkParm.getValue("RX_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"RX_NO",buff).toString())&&linkParm.getValue("ORDERSET_CODE").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"ORDERSET_CODE",buff).toString())&&linkParm.getValue("ORDERSET_GROUP_NO").equals(this.getTTable(TABLE2).getDataStore().getItemData(i,"ORDERSET_GROUP_NO",buff).toString())){
                           this.getTTable(TABLE2).getDataStore().setItem(i,columnName,linkParm.getValue("EXEC_STATUS"),buff);
                           if("Y".equals(linkParm.getValue("EXEC_STATUS"))){
                               this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_USER",Operator.getID(),buff);
                               this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DATE",SystemTool.getInstance().getDate(),buff);
                           }else{
                               this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_USER","",buff);
                               this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DATE",new TNull(Timestamp.class),buff);
                           }
                       }
                   }else{
                       if("Y".equals(linkParm.getValue("EXEC_STATUS"))){
                           this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_USER",Operator.getID(),buff);
                           this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DATE",SystemTool.getInstance().getDate(),buff);
                       }else{
                           this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_USER","",buff);
                           this.getTTable(TABLE2).getDataStore().setItem(i,"EXEC_DATE",new TNull(Timestamp.class),buff);
                       }
                   }
               }
            }
            this.getTTable(TABLE2).setDSValue();
        }else{
            if ("Y".equals(linkParm.getValue("EXEC_STATUS"))) {
                this.getTTable(TABLE2).getDataStore().setItem(row, "EXEC_USER",
                    Operator.getID());
                this.getTTable(TABLE2).getDataStore().setItem(row, "EXEC_DATE",
                    SystemTool.getInstance().getDate());
            }else {
                this.getTTable(TABLE2).getDataStore().setItem(row, "EXEC_USER",
                    "");
                this.getTTable(TABLE2).getDataStore().setItem(row, "EXEC_DATE",
                    new TNull(Timestamp.class));
            }
            this.getTTable(TABLE2).setDSValue();
        }
    }

    /**
    * 初始化权限
    */
   public void onInitPopeDem() {
       //权限可否选择科室
       if (!this.getPopedem("deptEnabled")) {
           this.callFunction("UI|DEPT_CODE|setEnabled", false);
       }
   }
   /**
     * 拿到TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }

   /**
    * 病案号查询事件
    */
   public void onQMrNo(){
       Timestamp sysDate = SystemTool.getInstance().getDate();
       this.setValue("MR_NO",PatTool.getInstance().checkMrno(this.getValueString("MR_NO")));
       this.setValue("PAT_NAME",PatTool.getInstance().getNameForMrno(PatTool.getInstance().checkMrno(this.getValueString("MR_NO"))));
       TParm patParm = PatTool.getInstance().getInfoForMrno(this.getValueString("MR_NO"));
       Timestamp temp = patParm.getTimestamp("BIRTH_DATE", 0) == null ? sysDate : patParm.getTimestamp("BIRTH_DATE", 0);
       this.setValue("SEX_CODE", patParm.getValue("SEX_CODE", 0));
       this.setValue("AGE", OdiUtil.getInstance().showAge(temp, sysDate));
       if(this.getValueString("ADM_TYPEQ").length()==0){
          this.messageBox("请输入病患来源！");
          return;
      }
      this.onQuery();
   }
   /**
    * TABLE1点击事件
    * @param row int
    */
   public void onTableClicked(int row){
       TParm rowParm = this.getTTable(TABLE1).getParmValue().getRow(row);
       ExecRecordDataStore execRecord = new ExecRecordDataStore();
       execRecord.setMrNo(rowParm.getValue("MR_NO"));
       execRecord.setCaseNo(rowParm.getValue("CASE_NO"));
       execRecord.setRxNo(rowParm.getValue("ORDER_NO"));
       execRecord.setSeqNo(rowParm.getValue("SEQ_NO"));
       execRecord.setOrderCode(rowParm.getValue("ORDER_CODE"));
       execRecord.setOrderSetCode(rowParm.getValue("ORDERSET_CODE"));
       execRecord.setOrderSetGroupNo(rowParm.getValue("ORDERSET_GROUP_NO"));
       execRecord.onQuery();
       if(execRecord.rowCount()>0){
           this.getTTable(TABLE2).setDataStore(execRecord);
           this.getTTable(TABLE2).setFilter("HIDE_FLG='N'");
//           this.getTTable(TABLE2).setSort("EXEC_NO");
           this.getTTable(TABLE2).filter();
//           this.getTTable(TABLE2).sort();
           this.getTTable(TABLE2).setDSValue();
           return;
       }
       this.getTTable(TABLE2).setDataStore(insertRowData(rowParm));
       this.getTTable(TABLE2).setFilter("HIDE_FLG = 'N'");
//       this.getTTable(TABLE2).setSort("EXEC_NO");
       this.getTTable(TABLE2).filter();
//       this.getTTable(TABLE2).sort();
       this.getTTable(TABLE2).setDSValue();
   }
   /**
    * 插入数据
    * @param rowParm TParm
    * @return ExecRecordDataStore
    */
   public ExecRecordDataStore insertRowData(TParm rowParm){
       ExecRecordDataStore execRecord = new ExecRecordDataStore();
       execRecord.onQuery();
       int addRowCount = rowParm.getInt("DISPENSE_QTY");
       TParm maxParm = new TParm(this.getDBTool().select("SELECT NVL(MAX(TO_NUMBER(EXEC_NO))+1,1) AS EXEC_NO FROM EXM_EXEC_RECORD WHERE CASE_NO='"+rowParm.getValue("CASE_NO")+"' AND RX_NO='"+rowParm.getValue("ORDER_NO")+"' AND ORDER_CODE='"+rowParm.getValue("ORDER_NO")+"'"));
       int maxNo=maxParm.getInt("EXEC_NO",0);
       for(int i=0;i<addRowCount;i++){
           if("O".equals(rowParm.getValue("ADM_TYPE"))){
               if("Y".equals(rowParm.getValue("SETMAIN_FLG"))){
                   int rowId = execRecord.insertRow();
                   execRecord.setItem(rowId,"MR_NO",rowParm.getValue("MR_NO"));
                   execRecord.setItem(rowId,"CASE_NO",rowParm.getValue("CASE_NO"));
                   execRecord.setItem(rowId,"RX_NO",rowParm.getValue("ORDER_NO"));
                   execRecord.setItem(rowId,"SEQ_NO",rowParm.getValue("SEQ_NO"));
                   execRecord.setItem(rowId,"SETMAIN_FLG",rowParm.getValue("SETMAIN_FLG"));
                   execRecord.setItem(rowId,"HIDE_FLG",rowParm.getValue("HIDE_FLG"));
                   execRecord.setItem(rowId,"ORDERSET_GROUP_NO",rowParm.getValue("ORDERSET_GROUP_NO"));
                   execRecord.setItem(rowId,"ORDERSET_CODE",rowParm.getValue("ORDERSET_CODE"));
                   execRecord.setItem(rowId,"ORDER_DESC",rowParm.getValue("ORDER_DESC"));
                   execRecord.setItem(rowId,"ORDER_CODE",rowParm.getValue("ORDER_CODE"));
                   execRecord.setItem(rowId,"EXEC_NO",maxNo);
                   execRecord.setItem(rowId,"BOOKING_DATE","");
                   execRecord.setItem(rowId,"EXEC_STATUS","N");
                   execRecord.setItem(rowId,"EXEC_DEPT",Operator.getDept());
                   execRecord.setItem(rowId,"EXEC_DATE","");
                   execRecord.setItem(rowId,"EXEC_USER","");
                   execRecord.setItem(rowId,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                   execRecord.setItem(rowId,"REMARK",rowParm.getValue("DR_NOTE"));
                   //拿细项
                   TParm itemParm = new TParm(this.getDBTool().select("SELECT A.ADM_TYPE,A.MR_NO,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,A.AR_AMT,A.DR_NOTE,A.DEV_CODE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.RX_NO AS ORDER_NO,A.SETMAIN_FLG,A.SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM OPD_ORDER A WHERE CASE_NO='"+rowParm.getValue("CASE_NO")+"' AND RX_NO='"+rowParm.getValue("ORDER_NO")+"' AND ORDERSET_GROUP_NO='"+rowParm.getValue("ORDERSET_GROUP_NO")+"' AND ORDERSET_CODE='"+rowParm.getValue("ORDER_CODE")+"' AND SETMAIN_FLG='N'"));
                   int rowCount = itemParm.getCount();
                   for(int j=0;j<rowCount;j++){
                       TParm temp = itemParm.getRow(j);
                       int rowIds = execRecord.insertRow();
                       execRecord.setItem(rowIds,"MR_NO",temp.getValue("MR_NO"));
                       execRecord.setItem(rowIds,"CASE_NO",temp.getValue("CASE_NO"));
                       execRecord.setItem(rowIds,"RX_NO",temp.getValue("ORDER_NO"));
                       execRecord.setItem(rowIds,"SEQ_NO",temp.getValue("SEQ_NO"));
                       execRecord.setItem(rowIds,"SETMAIN_FLG",temp.getValue("SETMAIN_FLG"));
                       execRecord.setItem(rowIds,"HIDE_FLG",temp.getValue("HIDE_FLG"));
                       execRecord.setItem(rowIds,"ORDERSET_GROUP_NO",temp.getValue("ORDERSET_GROUP_NO"));
                       execRecord.setItem(rowIds,"ORDERSET_CODE",temp.getValue("ORDERSET_CODE"));
                       execRecord.setItem(rowIds,"ORDER_DESC",temp.getValue("ORDER_DESC"));
                       execRecord.setItem(rowIds,"ORDER_CODE",temp.getValue("ORDER_CODE"));
                       execRecord.setItem(rowIds,"EXEC_NO",maxNo);
                       execRecord.setItem(rowIds,"BOOKING_DATE","");
                       execRecord.setItem(rowIds,"EXEC_STATUS","N");
                       execRecord.setItem(rowIds,"EXEC_DEPT",Operator.getDept());
                       execRecord.setItem(rowIds,"EXEC_DATE","");
                       execRecord.setItem(rowIds,"EXEC_USER","");
                       execRecord.setItem(rowIds,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                       execRecord.setItem(rowIds,"REMARK",temp.getValue("DR_NOTE"));
                   }
                   maxNo++;
               }else{
                   int rowId = execRecord.insertRow();
                   execRecord.setItem(rowId,"MR_NO",rowParm.getValue("MR_NO"));
                   execRecord.setItem(rowId,"CASE_NO",rowParm.getValue("CASE_NO"));
                   execRecord.setItem(rowId,"RX_NO",rowParm.getValue("ORDER_NO"));
                   execRecord.setItem(rowId,"SEQ_NO",rowParm.getValue("SEQ_NO"));
                   execRecord.setItem(rowId,"SETMAIN_FLG",rowParm.getValue("SETMAIN_FLG"));
                   execRecord.setItem(rowId,"HIDE_FLG",rowParm.getValue("HIDE_FLG"));
                   execRecord.setItem(rowId,"ORDERSET_GROUP_NO",rowParm.getValue("ORDERSET_GROUP_NO"));
                   execRecord.setItem(rowId,"ORDERSET_CODE",rowParm.getValue("ORDERSET_CODE"));
                   execRecord.setItem(rowId,"ORDER_DESC",rowParm.getValue("ORDER_DESC"));
                   execRecord.setItem(rowId,"ORDER_CODE",rowParm.getValue("ORDER_CODE"));
                   execRecord.setItem(rowId,"EXEC_NO",maxNo);
                   execRecord.setItem(rowId,"BOOKING_DATE","");
                   execRecord.setItem(rowId,"EXEC_STATUS","N");
                   execRecord.setItem(rowId,"EXEC_DEPT",Operator.getDept());
                   execRecord.setItem(rowId,"EXEC_DATE","");
                   execRecord.setItem(rowId,"EXEC_USER","");
                   execRecord.setItem(rowId,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                   execRecord.setItem(rowId,"REMARK",rowParm.getValue("DR_NOTE"));
                   maxNo++;
               }
           }
           if("I".equals(rowParm.getValue("ADM_TYPE"))){
               if("Y".equals(rowParm.getValue("SETMAIN_FLG"))){
                   int rowId = execRecord.insertRow();
                   execRecord.setItem(rowId,"MR_NO",rowParm.getValue("MR_NO"));
                   execRecord.setItem(rowId,"CASE_NO",rowParm.getValue("CASE_NO"));
                   execRecord.setItem(rowId,"RX_NO",rowParm.getValue("ORDER_NO"));
                   execRecord.setItem(rowId,"SEQ_NO",rowParm.getValue("SEQ_NO"));
                   execRecord.setItem(rowId,"SETMAIN_FLG",rowParm.getValue("SETMAIN_FLG"));
                   execRecord.setItem(rowId,"HIDE_FLG",rowParm.getValue("HIDE_FLG"));
                   execRecord.setItem(rowId,"ORDERSET_GROUP_NO",rowParm.getValue("ORDERSET_GROUP_NO"));
                   execRecord.setItem(rowId,"ORDERSET_CODE",rowParm.getValue("ORDERSET_CODE"));
                   execRecord.setItem(rowId,"ORDER_DESC",rowParm.getValue("ORDER_DESC"));
                   execRecord.setItem(rowId,"ORDER_CODE",rowParm.getValue("ORDER_CODE"));
                   execRecord.setItem(rowId,"EXEC_NO",maxNo);
                   execRecord.setItem(rowId,"BOOKING_DATE","");
                   execRecord.setItem(rowId,"EXEC_STATUS","N");
                   execRecord.setItem(rowId,"EXEC_DEPT",Operator.getDept());
                   execRecord.setItem(rowId,"EXEC_DATE","");
                   execRecord.setItem(rowId,"EXEC_USER","");
                   execRecord.setItem(rowId,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                   execRecord.setItem(rowId,"REMARK",rowParm.getValue("DR_NOTE"));
                   //拿细项
                   TParm itemParm = new TParm(this.getDBTool().select("SELECT 'I' AS ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,'' AS AR_AMT,A.DR_NOTE,A.DEV_CODE,A.NS_CHECK_DATE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.ORDER_NO,A.SETMAIN_FLG,A.ORDER_SEQ AS SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM ODI_ORDER A WHERE CASE_NO='"+rowParm.getValue("CASE_NO")+"' AND ORDER_NO='"+rowParm.getValue("ORDER_NO")+"' AND ORDERSET_GROUP_NO='"+rowParm.getValue("ORDERSET_GROUP_NO")+"' AND ORDERSET_CODE='"+rowParm.getValue("ORDER_CODE")+"' AND SETMAIN_FLG='N'"));
                   int rowCount = itemParm.getCount();
                   for(int j=0;j<rowCount;j++){
                       TParm temp = itemParm.getRow(j);
                       int rowIds = execRecord.insertRow();
                       execRecord.setItem(rowIds,"MR_NO",temp.getValue("MR_NO"));
                       execRecord.setItem(rowIds,"CASE_NO",temp.getValue("CASE_NO"));
                       execRecord.setItem(rowIds,"RX_NO",temp.getValue("ORDER_NO"));
                       execRecord.setItem(rowIds,"SEQ_NO",temp.getValue("SEQ_NO"));
                       execRecord.setItem(rowIds,"SETMAIN_FLG",temp.getValue("SETMAIN_FLG"));
                       execRecord.setItem(rowIds,"HIDE_FLG",temp.getValue("HIDE_FLG"));
                       execRecord.setItem(rowIds,"ORDERSET_GROUP_NO",temp.getValue("ORDERSET_GROUP_NO"));
                       execRecord.setItem(rowIds,"ORDERSET_CODE",temp.getValue("ORDERSET_CODE"));
                       execRecord.setItem(rowIds,"ORDER_DESC",temp.getValue("ORDER_DESC"));
                       execRecord.setItem(rowIds,"ORDER_CODE",temp.getValue("ORDER_CODE"));
                       execRecord.setItem(rowIds,"EXEC_NO",maxNo);
                       execRecord.setItem(rowIds,"BOOKING_DATE","");
                       execRecord.setItem(rowIds,"EXEC_STATUS","N");
                       execRecord.setItem(rowIds,"EXEC_DEPT",Operator.getDept());
                       execRecord.setItem(rowIds,"EXEC_DATE","");
                       execRecord.setItem(rowIds,"EXEC_USER","");
                       execRecord.setItem(rowIds,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                       execRecord.setItem(rowIds,"REMARK",temp.getValue("DR_NOTE"));
                   }
                   maxNo++;
               }else{
                   int rowId = execRecord.insertRow();
                   execRecord.setItem(rowId,"MR_NO",rowParm.getValue("MR_NO"));
                   execRecord.setItem(rowId,"CASE_NO",rowParm.getValue("CASE_NO"));
                   execRecord.setItem(rowId,"RX_NO",rowParm.getValue("ORDER_NO"));
                   execRecord.setItem(rowId,"SEQ_NO",rowParm.getValue("SEQ_NO"));
                   execRecord.setItem(rowId,"SETMAIN_FLG",rowParm.getValue("SETMAIN_FLG"));
                   execRecord.setItem(rowId,"HIDE_FLG",rowParm.getValue("HIDE_FLG"));
                   execRecord.setItem(rowId,"ORDERSET_GROUP_NO",rowParm.getValue("ORDERSET_GROUP_NO"));
                   execRecord.setItem(rowId,"ORDERSET_CODE",rowParm.getValue("ORDERSET_CODE"));
                   execRecord.setItem(rowId,"ORDER_DESC",rowParm.getValue("ORDER_DESC"));
                   execRecord.setItem(rowId,"ORDER_CODE",rowParm.getValue("ORDER_CODE"));
                   execRecord.setItem(rowId,"EXEC_NO",maxNo);
                   execRecord.setItem(rowId,"BOOKING_DATE","");
                   execRecord.setItem(rowId,"EXEC_STATUS","N");
                   execRecord.setItem(rowId,"EXEC_DEPT",Operator.getDept());
                   execRecord.setItem(rowId,"EXEC_DATE","");
                   execRecord.setItem(rowId,"EXEC_USER","");
                   execRecord.setItem(rowId,"DEVICE_ID",rowParm.getValue("DEV_CODE"));
                   execRecord.setItem(rowId,"REMARK",rowParm.getValue("DR_NOTE"));
                   maxNo++;
               }
           }
       }
       return execRecord;
   }
   /**
    * 保存
    */
   public void onSave(){
       this.getTTable(TABLE2).acceptText();
       int newRow = this.getTTable(TABLE2).getDataStore().getNewRows().length;
       int modifidRow = this.getTTable(TABLE2).getDataStore().getModifiedRows().length;
       if(newRow>0||modifidRow>0){
           String sql[] = this.getTTable(TABLE2).getDataStore().getUpdateSQL();
           for(String temp:sql){
               // System.out.println("SQL===SAVE==="+temp);
           }
           if(this.getTTable(TABLE2).getDataStore().update()){
               TParm rowParm = this.getTTable(TABLE1).getParmValue().getRow(this.getTTable(TABLE1).getSelectedRow());
               if(updateOpdOrder(rowParm)){
                   this.messageBox("保存成功！");
                   onTableClicked(this.getTTable(TABLE1).getSelectedRow());
               }else{
                   this.messageBox("保存失败！");
               }
           }else{
               this.messageBox("保存失败！");
           }
       }else{
           this.messageBox("没有需要保存的数据！");
       }
   }
   /**
    * 更新门住表状态
    * @param parm TParm
    * @return boolean
    */
   public boolean updateOpdOrder(TParm parm){
       String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),"yyyyMMddHHmmss");
//       this.messageBox_(parm.getValue("ADM_TYPE"));
//       // System.out.println("SQL====="+"SELECT RX_NO FROM EXM_EXEC_RECORD WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+"' AND EXEC_STATUS='N' ORDER BY CASE_NO,RX_NO,SEQ_NO,EXEC_NO");
       TParm tableParm = new TParm(this.getDBTool().select("SELECT RX_NO FROM EXM_EXEC_RECORD WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+"' AND EXEC_STATUS='N' ORDER BY CASE_NO,RX_NO,SEQ_NO,EXEC_NO"));
       if("O".equals(parm.getValue("ADM_TYPE"))||"E".equals(parm.getValue("ADM_TYPE"))){
//           this.messageBox_(tableParm.getCount("RX_NO"));
           if(tableParm.getCount("RX_NO")<=0){
               // System.out.println("UPDATE OPD_ORDER SET EXM_EXEC_END_DATE=TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+"'");
               TParm result = new TParm(this.getDBTool().update("UPDATE OPD_ORDER SET EXM_EXEC_END_DATE=TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("ORDER_NO")+"' AND ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+"'"));
               if(result.getErrCode()!=0)
                   return false;
           }
       }
       if("I".equals(parm.getValue("ADM_TYPE"))){
           if(tableParm.getCount("RX_NO")<=0){
               TParm result = new TParm(this.getDBTool().update("UPDATE ODI_ORDER SET EXM_EXEC_END_DATE=TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') WHERE MR_NO='"+parm.getValue("MR_NO")+"' AND CASE_NO='"+parm.getValue("CASE_NO")+"' AND ORDER_NO='"+parm.getValue("ORDER_NO")+"' AND ORDERSET_CODE='"+parm.getValue("ORDERSET_CODE")+"' AND ORDERSET_GROUP_NO='"+parm.getValue("ORDERSET_GROUP_NO")+"'"));
               if(result.getErrCode()!=0)
                   return false;
           }
       }
       return true;
   }
   /**
    * 清空
    */
   public void onClear(){
       Timestamp sysDate = SystemTool.getInstance().getDate();
        //默认设置起始日期
        this.setValue("START_DATE", sysDate);
        //默认设置终止日期
        this.setValue("END_DATE", StringTool.rollDate(sysDate, 1));
        //设置科室
        this.setValue("DEPT_CODE", Operator.getDept());
        if(this.getTTable(TABLE2).getDataStore()!=null){
           ExecRecordDataStore execRecord = new ExecRecordDataStore();
           execRecord.onQuery();
           this.getTTable(TABLE2).setDataStore(execRecord);
           this.getTTable(TABLE2).setDSValue();
       }
       this.getTTable(TABLE1).removeRowAll();
       clearValue("ADM_TYPEQ;MR_NO;PAT_NAME;SEX_CODE;AGE;");
   }
   /**
    * 读卡
    */
   public void onreadCard(){
       TParm patParm = jdo.ekt.EKTIO.getInstance().getPat();
       if (patParm.getErrCode() < 0) {
           this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
           return;
       }
       this.setValue("MR_NO", patParm.getValue("MR_NO"));
       this.onQMrNo();
   }
   /**
    * 查询
    */
   public void onQuery(){
       Timestamp sysDate = SystemTool.getInstance().getDate();
       if(this.getValueString("MR_NO").length()!=0){
           this.setValue("MR_NO",PatTool.getInstance().checkMrno(this.getValueString("MR_NO")));
           this.setValue("PAT_NAME",PatTool.getInstance().getNameForMrno(PatTool.getInstance().checkMrno(this.getValueString("MR_NO"))));
           TParm patParm = PatTool.getInstance().getInfoForMrno(this.getValueString("MR_NO"));
           Timestamp temp = patParm.getTimestamp("BIRTH_DATE", 0) == null ? sysDate : patParm.getTimestamp("BIRTH_DATE", 0);
           this.setValue("SEX_CODE", patParm.getValue("SEX_CODE", 0));
           this.setValue("AGE", OdiUtil.getInstance().showAge(temp, sysDate));
       }
       if(this.getValueString("ADM_TYPEQ").length()==0){
          this.messageBox("请输入病患来源！");
          return;
      }
       if(this.getTTable(TABLE2).getDataStore()!=null){
           ExecRecordDataStore execRecord = new ExecRecordDataStore();
           execRecord.onQuery();
           this.getTTable(TABLE2).setDataStore(execRecord);
           this.getTTable(TABLE2).setDSValue();
       }
       String admType = this.getValueString("ADM_TYPEQ");
       String sql = "";
       //门诊
       if("O".equals(admType)){
           sql = getQuerySQLOpd(getCaseNoOpb());
       }
       //住院
       if("I".equals(admType)){
           sql = getQuerySQLOdi(getCaseNoOdi());
       }
       if(sql.length()==0){
           this.messageBox("无查询SQL");
           return;
       }
       TParm parm = new TParm(getDBTool().select(sql));
       if(parm.getCount()<=0){
           this.messageBox("无就诊信息！");
           return;
       }
       int rowCount = parm.getCount();
       for(int i=0;i<rowCount;i++){
           if(parm.getValue("SETMAIN_FLG",i).equals("Y")){
               if("O".equals(admType)){
                   parm.setData("AR_AMT",i,getArAmtOrderSetFlg(admType,parm.getValue("ORDER_CODE",i),parm.getTimestamp("ORDER_DATE",i),parm.getValue("ORDER_NO",i),parm.getValue("ORDERSET_GROUP_NO",i)));
               }
               if("I".equals(admType)){
                   parm.setData("AR_AMT",i,getArAmtOrderSetFlg(admType,parm.getValue("ORDER_CODE",i),parm.getTimestamp("NS_CHECK_DATE",i),parm.getValue("ORDER_NO",i),parm.getValue("ORDERSET_GROUP_NO",i)));
               }
           }
           if(parm.getValue("SETMAIN_FLG",i).equals("N")){
                if("I".equals(admType)){
                    parm.setData("AR_AMT",i,getOwnPrice(parm.getValue("ORDER_CODE",i),parm.getTimestamp("NS_CHECK_DATE",i)));
                }
           }
       }
       this.getTTable(TABLE1).setParmValue(parm);
   }
   /**
    * 得到医嘱价格
    * @param orderCode String
    * @param date Timestamp
    * @return double
    */
   public double getOwnPrice(String orderCode,Timestamp date){
       double price =0.0;
       String dateStr = StringTool.getString(date,"yyyyMMddHHmmss");
       TParm sysParm = new TParm(this.getDBTool().select("SELECT OWN_PRICE FROM SYS_FEE_HISTORY WHERE ORDER_CODE='"+orderCode+"' AND TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') BETWEEN TO_DATE(START_DATE,'YYYYMMDDHH24MISS') AND TO_DATE(END_DATE,'YYYYMMDDHH24MISS')"));
       if(sysParm.getCount()>0){
           price = sysParm.getDouble("OWN_PRICE",0);
       }
       return price;
   }
   /**
    * 拿到集合医嘱价格
    * @param orderCode String
    * @return long
    */
   public double getArAmtOrderSetFlg(String admType,String orderCode,Timestamp date,String orderNo,String orderGroupNo){
       double arAmt = 0.0;
       String dateStr = StringTool.getString(date,"yyyyMMddHHmmss");
       if("O".equals(admType)){
           TParm opdParm = new TParm(this.getDBTool().select("SELECT AR_AMT FROM OPD_ORDER WHERE ORDERSET_CODE='"+orderCode+"' AND RX_NO='"+orderNo+"' AND ORDERSET_GROUP_NO='"+orderGroupNo+"' AND SETMAIN_FLG='N'"));
           int rowCount = opdParm.getCount();
           for(int i=0;i<rowCount;i++){
               arAmt+=opdParm.getDouble("AR_AMT",i);
           }
       }
       if("I".equals(admType)){
           TParm odiParm = new TParm(this.getDBTool().select("SELECT B.OWN_PRICE FROM ODI_ORDER A,SYS_FEE_HISTORY B WHERE A.ORDERSET_CODE='T0503006' AND ORDERSET_GROUP_NO='6' AND ORDER_NO='100427000002' AND SETMAIN_FLG='N' AND A.ORDER_CODE=B.ORDER_CODE AND TO_DATE('"+dateStr+"','YYYYMMDDHH24MISS') BETWEEN TO_DATE(B.START_DATE,'YYYYMMDDHH24MISS') AND TO_DATE(B.END_DATE,'YYYYMMDDHH24MISS')"));
           int rowCount = odiParm.getCount();
           for(int i=0;i<rowCount;i++){
               arAmt+=odiParm.getDouble("OWN_PRICE",i);
           }
       }
       // System.out.println("ARAMT============"+arAmt);
       return arAmt;
   }
   /**
    * 拿到CASE_NO
    * @return String
    */
   public String getCaseNoOpb(){
        TParm parm = new TParm();
        if(this.getValueString("MR_NO").length()==0)
            return "";
        parm.setData("MR_NO", this.getValueString("MR_NO"));
        parm.setData("PAT_NAME", this.getValueString("PAT_NAME"));
        parm.setData("SEX_CODE", this.getValueString("SEX_CODE"));
        parm.setData("AGE", this.getValueString("AGE"));
        parm.setData("ADM_TYPE",this.getValueString("ADM_TYPEQ"));
        parm.setData("START_DATE",this.getValue("START_DATE"));
        parm.setData("END_DATE",this.getValue("END_DATE"));
        //判断是否从明细点开的就诊号选择
        parm.setData("count", "0");
        parm.setData("REGION_CODE",Operator.getRegion());
        Object obj = openDialog(
            "%ROOT%\\config\\med\\MEDChooseVisitADM.x", parm);
        String caseNo = "";
        if(obj!=null){
            TParm opdParm = (TParm)obj;
//            this.messageBox_(opdParm);
            caseNo = opdParm.getValue("CASE_NO");
            this.setValue("START_DATE",opdParm.getTimestamp("ADM_DATE"));
        }

        if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null"))
            return "";
        return caseNo;
   }
   /**
    * 拿到CASE_NO
    * @return String
    */
   public String getCaseNoOdi(){
       TParm parm = new TParm();
       if(this.getValueString("MR_NO").length()==0)
            return "";
       parm.setData("MR_NO", this.getValueString("MR_NO"));
       parm.setData("PAT_NAME", this.getValueString("PAT_NAME"));
       parm.setData("SEX_CODE", this.getValueString("SEX_CODE"));
       parm.setData("AGE", this.getValueString("AGE"));
       parm.setData("ADM_TYPE",this.getValueString("ADM_TYPEQ"));
       parm.setData("START_DATE",this.getValue("START_DATE"));
       parm.setData("END_DATE",this.getValue("END_DATE"));
       //判断是否从明细点开的就诊号选择
       parm.setData("count", "0");
       parm.setData("REGION_CODE",Operator.getRegion());
       Object obj = openDialog(
            "%ROOT%\\config\\med\\MEDChooseVisitADM.x", parm);
        String caseNo = "";
        if(obj!=null){
            TParm opdParm = (TParm)obj;
//            this.messageBox_(opdParm);
            caseNo = opdParm.getValue("CASE_NO");
            this.setValue("START_DATE",opdParm.getTimestamp("ADM_DATE"));
        }
        if (caseNo == null || caseNo.length() == 0 || caseNo.equals("null"))
            return "";
        return caseNo;
   }

   /**
    * 得到查询的SQL门诊
    * @param tableName String
    * @return String
    */
   public String getQuerySQLOpd(String caseNo){
       Timestamp sDate = (Timestamp)this.getValue("START_DATE");
       String startDate = StringTool.getString(sDate,"yyyyMMdd");
       Timestamp eDate = (Timestamp)this.getValue("END_DATE");
       String endDate = StringTool.getString(eDate,"yyyyMMdd");
       String sql="";
       if(caseNo==null||caseNo.length()==0){
           sql = "SELECT A.ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,A.AR_AMT,A.DR_NOTE,A.DEV_CODE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.RX_NO AS ORDER_NO,A.SETMAIN_FLG,A.SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM OPD_ORDER A,SYS_PATINFO B,SYS_ORDER_CAT1 C WHERE A.MR_NO=B.MR_NO AND DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND A.BILL_FLG='Y' AND A.ORDER_CAT1_CODE=C.ORDER_CAT1_CODE AND C.TREAT_FLG='Y' AND ((A.SETMAIN_FLG='Y' AND A.HIDE_FLG='N') OR (A.SETMAIN_FLG='N' AND A.HIDE_FLG='N')) AND A.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDD') AND TO_DATE('"+endDate+"','YYYYMMDD') AND A.EXM_EXEC_END_DATE IS NULL ORDER BY A.MR_NO";
       }else{
           sql = "SELECT A.ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,A.AR_AMT,A.DR_NOTE,A.DEV_CODE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.RX_NO AS ORDER_NO,A.SETMAIN_FLG,A.SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM OPD_ORDER A,SYS_PATINFO B,SYS_ORDER_CAT1 C WHERE A.MR_NO=B.MR_NO AND DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND A.BILL_FLG='Y' AND A.ORDER_CAT1_CODE=C.ORDER_CAT1_CODE AND C.TREAT_FLG='Y' AND A.CASE_NO='"+caseNo+"' AND ((A.SETMAIN_FLG='Y' AND A.HIDE_FLG='N') OR (A.SETMAIN_FLG='N' AND A.HIDE_FLG='N')) AND A.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDD') AND TO_DATE('"+endDate+"','YYYYMMDD') AND A.EXM_EXEC_END_DATE IS NULL ORDER BY A.MR_NO";
       }

       // System.out.println("sqlO==="+sql);
       return sql;
   }
   /**
    * 得到查询的SQL住院
    * @param tableName String
    * @return String
    */
   public String getQuerySQLOdi(String caseNo){
       Timestamp sDate = (Timestamp)this.getValue("START_DATE");
       String startDate = StringTool.getString(sDate,"yyyyMMdd");
       Timestamp eDate = (Timestamp)this.getValue("END_DATE");
       String endDate = StringTool.getString(eDate,"yyyyMMdd");
       String sql="";
       if(caseNo==null||caseNo.length()==0){
           sql = "SELECT 'I' AS ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,'' AS AR_AMT,A.DR_NOTE,A.DEV_CODE,A.NS_CHECK_DATE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.ORDER_NO,A.SETMAIN_FLG,A.ORDER_SEQ AS SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM ODI_ORDER A,SYS_PATINFO B,SYS_ORDER_CAT1 C WHERE A.MR_NO=B.MR_NO AND DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND  A.NS_CHECK_DATE IS NOT NULL AND A.ORDER_CAT1_CODE=C.ORDER_CAT1_CODE AND C.TREAT_FLG='Y' AND ((A.SETMAIN_FLG='Y' AND A.HIDE_FLG='N') OR (A.SETMAIN_FLG='N' AND A.HIDE_FLG='N')) AND A.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDD') AND TO_DATE('"+endDate+"','YYYYMMDD') AND A.EXM_EXEC_END_DATE IS NULL ORDER BY A.MR_NO";
       }else{
           sql = "SELECT 'I' AS ADM_TYPE,A.MR_NO,B.PAT_NAME,B.SEX_CODE,A.ORDER_DESC,A.ORDER_DATE,A.DISPENSE_QTY,'' AS AR_AMT,A.DR_NOTE,A.DEV_CODE,A.NS_CHECK_DATE,A.ORDERSET_GROUP_NO,A.ORDER_CODE,A.ORDER_NO,A.SETMAIN_FLG,A.ORDER_SEQ AS SEQ_NO,A.CASE_NO,A.HIDE_FLG,A.ORDERSET_CODE FROM ODI_ORDER A,SYS_PATINFO B,SYS_ORDER_CAT1 C WHERE A.MR_NO=B.MR_NO AND DEPT_CODE='"+this.getValue("DEPT_CODE")+"' AND  A.NS_CHECK_DATE IS NOT NULL AND A.ORDER_CAT1_CODE=C.ORDER_CAT1_CODE AND C.TREAT_FLG='Y' AND A.CASE_NO='"+caseNo+"' AND ((A.SETMAIN_FLG='Y' AND A.HIDE_FLG='N') OR (A.SETMAIN_FLG='N' AND A.HIDE_FLG='N')) AND A.ORDER_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDD') AND TO_DATE('"+endDate+"','YYYYMMDD') AND A.EXM_EXEC_END_DATE IS NULL ORDER BY A.MR_NO";
       }
       // System.out.println("sqlI==="+sql);
       return sql;

   }
   /**
    * 导出EXECL
    */
   public void onExecl(){
       if(this.getValueString("ADM_TYPEQ").length()==0){
           this.messageBox("请选择患者来源！");
           return;
       }
       TParm parm = new TParm();
       parm.setData("ADM_TYPE",this.getValueString("ADM_TYPEQ"));
       parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));
       this.openDialog("%ROOT%\\config\\med\\MedExmQuery.x",parm);
   }
   /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

}
