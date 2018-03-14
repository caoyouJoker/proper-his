package com.javahis.ui.spc;
import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import java.util.Date;
import com.dongyang.ui.TRadioButton;
import jdo.sys.DeptTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.ind.IndDDStockTool;
import java.text.NumberFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import jdo.ind.IndStockDTool;
import jdo.ind.INDDrugMianTool;

public class INDDrugMianControl
    extends TControl {
    public INDDrugMianControl() {

    }
    //占比
    String Percent;
    //表格
    private TTable table;
    public void onInit() {
        super.onInit();
        initPage();
    }

    /**
     * 初始化界面
     */
    public void initPage() {
        //养护日期
        Timestamp startDate = StringTool.getTimestamp(new Date());
        //养护日期
        this.setValue("DATE", startDate);
        table = (TTable)this.getComponent("DrugTABLE");
        this.callFunction("UI|DrugTABLE|removeRowAll");

    }
    public void onSave() {
          if(table.getSelectedRow()<0){
            this.messageBox("请选中数据行");
          }
          if(!checkData()){
              return;
          }
         int row=table.getClickedRow();
         TParm medParm=table.getParmValue();
         String date=medParm.getValue("VALID_DATE",row);
         date=date.substring(0,4)+date.substring(5,7)+date.substring(8,10);
         TParm saveparm=new TParm();
         saveparm.setData("WAS_QTY",this.getValueDouble("WAS_QTY"));
         saveparm.setData("WAS_REASON",this.getValueString("WAS_REASON"));
         saveparm.setData("ORG_CODE",medParm.getValue("ORG_CODE",row));
         saveparm.setData("ORDER_CODE",medParm.getValue("ORDER_CODE",row));
         saveparm.setData("BATCH_SEQ",medParm.getInt("BATCH_SEQ",row));
         saveparm.setData("ORDER_DESC",medParm.getValue("ORDER_DESC",row));
         saveparm.setData("SPECIFICATION",medParm.getValue("SPECIFICATION",row));
         saveparm.setData("UNIT_CODE",medParm.getValue("UNIT_CODE",row));
         saveparm.setData("MAN_CODE",medParm.getValue("MAN_CODE",row));
         saveparm.setData("BATCH_NO",medParm.getValue("BATCH_NO",row));
         saveparm.setData("VALID_DATE",date);
         saveparm.setData("QUALIFIED_QTY",(medParm.getDouble("QTY",row)-this.getValueDouble("WAS_QTY")));
         saveparm.setData("OPT_USER",Operator.getID());
         saveparm.setData("OPT_DATE",SystemTool.getInstance().getDate());
         saveparm.setData("OPT_TERM",Operator.getIP());
         if(this.getValueDouble("WAS_QTY")>medParm.getDouble("QTY",row)){
             this.messageBox("损坏数量大于库存数量！");
             return;
         }
//         System.out.println("saveparm======="+saveparm);
         TParm check=INDDrugMianTool.getInstance().onQuery(saveparm);
//         System.out.println("check.getCount()======"+check.getCount());
         TParm wasparm=new TParm();
         if(table.getSelectedRow()>=0){
             if(check.getCount()<=0&&(this.getValueDouble("WAS_QTY")!=0.0||!this.getValueString("WAS_REASON").equals("")))
             {
             wasparm = INDDrugMianTool.getInstance().onInsert(
                     saveparm);
             }
             else if(check.getCount()>0&&(this.getValueDouble("WAS_QTY")!=0.0||!this.getValueString("WAS_REASON").equals("")))
             {
             wasparm = INDDrugMianTool.getInstance().onUpdate(
                     saveparm);
             }
             else if(check.getCount()>0&&(this.getValueDouble("WAS_QTY")==0.0&& this.getValueString("WAS_REASON").equals("")))
             {
                 wasparm = INDDrugMianTool.getInstance().onDelete(
                     saveparm);
             }
         }
         if (wasparm.getErrCode() < 0) {
            this.messageBox("E0001");
        }
        else {
            this.messageBox("P0001");
            onQuery();
        }


    }

    /**
     * 打印
     */
    public void onPrint() {
        if (this.table.getRowCount() <= 0) {
            this.messageBox("没有要打印的数据");
            return;
        }
        TParm prtParm = new TParm();
        //表头
        prtParm.setData("TITLE", "TEXT", "南京市建邺医院药品养护单");
        prtParm.setData("CREATEUSER", "TEXT", "养护人：" + Operator.getName());
        //起日
        prtParm.setData("DATE", "TEXT", "养护日期：" +
                        StringTool.getString(StringTool.getTimestamp(new Date()),
                                             "yyyy年MM月dd日"));
        prtParm.setData("PRINTDATE", "TEXT", "制表日期：" +
                        StringTool.getString(StringTool.getTimestamp(new Date()),
                                             "yyyy年MM月dd日"));
        TParm prtTableParm=new TParm();
        TParm dataParm= table.getParmValue();
        for(int i=0;i<table.getRowCount();i++){
            TParm rowParm=dataParm.getRow(i);
            prtTableParm.addData("ORDER_DESC", rowParm.getValue("ORDER_DESC"));
            prtTableParm.addData("SPECIFICATION",
                                 rowParm.getValue("SPECIFICATION"));
            prtTableParm.addData("UNIT",
                                 rowParm.getValue("UNIT"));
            prtTableParm.addData("MAN",
                                 rowParm.getValue("MAN"));
            prtTableParm.addData("BATCH_NO",
                                 rowParm.getValue("BATCH_NO"));
            prtTableParm.addData("VALID_DATE",
                                 rowParm.getValue("VALID_DATE"));
            prtTableParm.addData("WAS_QTY",
                                  rowParm.getValue("WAS_QTY"));
            prtTableParm.addData("WAS_REASON",
                                  rowParm.getValue("WAS_REASON"));
            prtTableParm.addData("QTY",
                                 rowParm.getValue("QTY"));
        }
        prtTableParm.setCount(prtTableParm.getCount("ORDER_DESC"));
        prtTableParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        prtTableParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        prtTableParm.addData("SYSTEM", "COLUMNS", "UNIT");
        prtTableParm.addData("SYSTEM", "COLUMNS", "MAN");
        prtTableParm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        prtTableParm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
        prtTableParm.addData("SYSTEM", "COLUMNS", "WAS_QTY");
        prtTableParm.addData("SYSTEM", "COLUMNS", "WAS_REASON");
        prtTableParm.addData("SYSTEM", "COLUMNS", "QTY");
        prtParm.setData("DRUGTABLE", prtTableParm.getData());
        this.openPrintWindow("%ROOT%\\config\\prt\\ind\\INDDrugMianprint.jhw",
                             prtParm);
    }

    public void onQuery() {
        TParm parm= new TParm();
        String org=this.getValueString("ORG_CODE");
        String order_cat=this.getValueString("ORDER_CAT1_CODE");
        //库存部门
        if(!org.equals("")&&org.length()>0)
        {
            parm.setData("ORG_CODE",org);
        }
        //药物种类
        if(!order_cat.equals("")&&order_cat.length()>0)
        {
            parm.setData("ORDER_CAT1_CODE",order_cat);
        }
        //院区
        if (!Operator.getRegion().equals("") &&Operator.getRegion().length() > 0) {
            parm.setData("REGION_CODE", Operator.getRegion());
        }
        TParm result=IndStockDTool.getInstance().getDrugMianStockQuery(parm);
        //处理查询出来的parm
        //统计信息begin
        DecimalFormat sf= new DecimalFormat("######0.00");
        SimpleDateFormat df= new SimpleDateFormat("yyyy.MM.dd");
        TParm tableparm = new TParm();
        for (int i = 0; i < result.getCount("ORDER_DESC"); i++)
        {
            tableparm.addData("ORDER_DESC", result.getValue("ORDER_DESC",i));
            tableparm.addData("SPECIFICATION",
                                 result.getValue("SPECIFICATION",i));
            tableparm.addData("UNIT",
                                 result.getValue("UNIT",i));
            tableparm.addData("MAN",
                                 result.getValue("MAN",i));
            tableparm.addData("BATCH_NO",
                                 result.getValue("BATCH_NO",i));
            tableparm.addData("VALID_DATE",
                                 df.format(result.getTimestamp("VALID_DATE",i)));
            tableparm.addData("WAS_QTY",
                                 sf.format(result.getDouble("WAS_QTY",i)));
            tableparm.addData("WAS_REASON",
                                 result.getValue("WAS_REASON",i));
            tableparm.addData("QTY",
                                 sf.format((result.getDouble("QTY",i)-result.getDouble("WAS_QTY",i))));
            tableparm.addData("ORG_CODE", result.getValue("ORG_CODE",i));
            tableparm.addData("ORDER_CODE", result.getValue("ORDER_CODE",i));
            tableparm.addData("BATCH_SEQ", result.getValue("BATCH_SEQ",i));
            tableparm.addData("UNIT_CODE", result.getValue("UNIT_CODE",i));
            tableparm.addData("MAN_CODE", result.getValue("MAN_CODE",i));

        }
        table.setParmValue(tableparm);
    }


   public void onTableClick(){
       this.setValueForParm("WAS_QTY;WAS_REASON;",
                             table.getParmValue().getRow(table.getSelectedRow()));
   }
    public void onClear() {
        this.clearValue("WAS_QTY;WAS_REASON;ORG_CODE;ORDER_CAT1_CODE;");
    }

    /**
     * 导出Excel
     */
    public void onExport() {
        if (table.getRowCount() > 0) {
            ExportExcelUtil.getInstance().exportExcel(table, "南京市建邺医院药品养护单");
        }
    }

    /**
     * 检查是否为空或空串
     * @return boolean
     */
    private boolean checkData() {
        if (this.getValueDouble("WAS_QTY")==0.0&&!this.getValueString("WAS_REASON").equals("")) {
            this.messageBox("损坏原因应该为空");
            return false;
        }
        if(!this.getValueString("WAS_QTY").equals("")&&this.getValueDouble("WAS_QTY")<0)
        {
            this.messageBox("损坏数量不能为负");
            return false;
        }
        return true;
    }

    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
}
