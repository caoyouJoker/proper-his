package com.javahis.ui.opb;

import com.dongyang.control.*;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import java.text.DecimalFormat;
import java.util.Iterator;
import com.dongyang.util.StringTool;
import java.util.HashSet;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.DictionaryTool;
import java.sql.Timestamp;
import jdo.opb.OPBExecutiveDeptListTool;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;
/**
 *
 * <p>Title: 执行科室统计表</p>
 *
 * <p>Description: 执行科室统计表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author not attributable
 * @version 1.0
 */
public class OPBExecutiveDeptListControl
    extends TControl {
TParm data = new TParm();
private String header = ""; //保存TABLE的HEADER
private String ParmMap = ""; //保存TABLE的ParmMap
private String chageList = ""; //记录费用类型
private TTable TABLE;
private String startDate = ""; //查询起日
private String endDate = ""; //查询止日
/**
 * 初始化界面
 */
public void onInit() {
    super.onInit();
    //table1的双击侦听事件
    callFunction("UI|TABLE|addEventListener",
                 "TABLE->" + TTableEvent.DOUBLE_CLICKED, this,
                 "onDoubleClick");
    TABLE = (TTable)this.getComponent("TABLE");
    //给TABLE初始化
    initTable();
    onClear();
    //================pangben modify 20110405 start 区域锁定
    setValue("REGION_CODE", Operator.getRegion());
    //================pangben modify 20110405 stop
    //========pangben modify 20110421 start 权限添加
    TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
    cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
            getValueString("REGION_CODE")));
    //===========pangben modify 20110421 stop
    pageInit();
}

/**
 * 页面初始化
 */
private void pageInit() {
    //组长权限
    if(this.getPopedem("LEADER")){
       callFunction("UI|DR_CODE|setEnabled",true);
    }
    //组长权限
    if(this.getPopedem("ALL")){
       callFunction("UI|DEPT_CODE|setEnabled",true);
       callFunction("UI|DR_CODE|setEnabled",true);
    }
    this.setValue("DEPT_CODE", Operator.getDept());
    this.setValue("DR_CODE", Operator.getID());
}

/**
 * 清空事件
 */
public void onClear() {
    //获取系统时间
    Timestamp now = SystemTool.getInstance().getDate();
    this.setValue("DATE_S",now);
    this.setValue("TIME_S", StringTool.getTimestamp("00:00:00","HH:mm:ss"));
    this.setValue("DATE_E",now);
    this.setValue("TIME_E",now);
    TABLE.removeRowAll();
    this.clearValue("DEPT_CODE;DR_CODE;ADM_TYPE");
    //============pangben modify 20110422
    this.setValue("REGION_CODE", Operator.getRegion());
}

/**
 * 表格双击事件
 * @param row int
 */
public void onDoubleClick(int row) {
    TParm parm = new TParm();
    parm.setData("DATE_S", startDate);
    parm.setData("DATE_E", endDate);
    parm.setData("DEPT_CODE", data.getValue("DEPT_CODE", row));
    parm.setData("DR_CODE", data.getValue("DR_CODE", row));
    this.openDialog("%ROOT%\\config\\opb\\OPBExecutiveDetial.x", parm);
}

/**
 * 给TABLE赋HEADER
 */
public void initTable() {
    TParm parm = new TParm();
    parm.setData("ADM_TYPE", getValueString("ADM_TYPE"));
    header += "区域,120;科别,100;执行人员,80;总金额,80;";
    ParmMap += "REGION_CHN_DESC;DEPT_DESC;DR_NAME;TOT_AMT;";
    String alignment = "0,left;1,left;2,left;3,right;"; //列对齐方式
    //查询收据费用名称及编码
    TParm charge = DictionaryTool.getInstance().getGroupList(
        "SYS_CHARGE");
    //===zhangp 20120313 start
    String chargeAll = "";
    for (int i = 1; i < 31; i++) {
    	if(i<10){
    		chargeAll+=",CHARGE0"+i;
    	}else{
    		chargeAll+=",CHARGE"+i;
    	}
	}
    chargeAll = chargeAll.substring(1, chargeAll.length());
    String sql = 
    	"SELECT "+chargeAll+" FROM BIL_RECPPARM WHERE ADM_TYPE = 'O'";
    TParm chargeParm = new TParm(TJDODBTool.getInstance().select(sql));
    TParm p = new TParm();
    String chargeo = "";
    for (int j = 1; j < 31; j++) {
    	for (int i = 0; i < charge.getCount(); i++) {
    		if(j<10){
    			chargeo = "CHARGE0"+j;
    		}else{
    			chargeo = "CHARGE"+j;
    		}
    		if(charge.getData("ID", i).equals(chargeParm.getData(chargeo, 0))){
    			p.addData("CHARGE", charge.getValue("NAME", i));
    			p.addData("ID", charge.getValue("ID", i));
    		}
		}
	}
    for (int i = 0; i < p.getCount("CHARGE"); i++) {
        header += p.getValue("CHARGE", i) + ",70;";
        chageList += p.getValue("ID", i) + ";";
        alignment += i + 4 + ",right;";
    }
    //===zhangp 20120313 end
    ParmMap += chageList;
    TABLE.setHeader(header.substring(0, header.length() - 1)); //绑定表格时去掉最后一个分号
    TABLE.setParmMap(ParmMap.substring(0, ParmMap.length() - 1)); //绑定表格时去掉最后一个分号
    TABLE.setColumnHorizontalAlignmentData(alignment.substring(0,
        alignment.length() - 1)); //设置列对齐
}

/**
 * 查询配参
 * @return TParm
 */
public TParm finishData() {
    TParm parm = new TParm();
    //区域
    if (getValueString("REGION_CODE").length() > 0)
        parm.setData("REGION_CODE", getValueString("REGION_CODE"));
    //门急住别
    if (getValueString("ADM_TYPE").length() > 0)
        parm.setData("ADM_TYPE", getValueString("ADM_TYPE"));
    //开单科室
    if (getValueString("DEPT_CODE").length() > 0)
        parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
    //开单医师
    if (getValueString("DR_CODE").length() > 0)
        parm.setData("DR_CODE", getValueString("DR_CODE"));
    //起日
    if(getValueString("DATE_S").length()>0)
        parm.setData("DATE_S",
                     StringTool.getString(TCM_Transform.getTimestamp(
                                          getValue("DATE_S")), "yyyyMMdd") +
                     StringTool.getString(TCM_Transform.getTimestamp(getValue(
            "TIME_S")), "HHmmss"));
    //迄日
    if(getValueString("DATE_E").length()>0)
        parm.setData("DATE_E",
                     StringTool.getString(TCM_Transform.getTimestamp(
                                          getValue("DATE_E")), "yyyyMMdd") +
                     StringTool.getString(TCM_Transform.getTimestamp(getValue(
            "TIME_E")), "HHmmss"));
    return parm;
}

/**
 * 条件查询
 */
public void onQuery() {
    DecimalFormat df = new DecimalFormat("0.00"); //pangben modify 20110425
    if (this.getValueString("DATE_S").length() <= 0 ||
        this.getValueString("DATE_E").length() <= 0) {
        this.messageBox_("请选择查询起讫日期");
        return;
    }
    TParm parm = finishData();
    TParm result = OPBExecutiveDeptListTool.getInstance().selectData(parm);
    if (result.getErrCode() < 0) {
        this.messageBox("E0005");
        return;
    }
    if (result.getCount() == 0) {
        this.messageBox("E0008");
        TABLE.removeRowAll();
        return;
    }
    //整理数据
    data = getTableData(result);
    //===============pangben modify 20110415 start
    int countAll = data.getCount("DEPT_CODE");
    for (int i = countAll - 1; i >= 0; i--) {
        if ("".equals(data.getValue("DEPT_CODE", i)) ||
            data.getData("DEPT_CODE", i) == null) {
            data.removeRow(i);
        }
    }
    double[] columnValue = new double[19];
    double sumTotAMT = 0.00; //合计行
    for (int i = 0; i < countAll; i++) {
        for (int j = 0; j < columnValue.length; j++) {
            columnValue[j] +=
                    StringTool.round(data.getDouble((101 + j) + "", i),
                                     2);
        }
        sumTotAMT += StringTool.round(data.getDouble("TOT_AMT", i),
                                      2);
    }
    for (int i = 0; i < columnValue.length; i++) {
        data.setData((101 + i) + "", countAll, df.format(columnValue[i]));
    }
    data.setData("DEPT_CODE", countAll, "");
    data.setData("REGION_CHN_DESC", countAll, "合计:");
    data.setData("DR_CODE", countAll, "");
    data.setData("DEPT_DESC", countAll, "");
    data.setData("DR_NAME", countAll, "");
    data.setData("TOT_AMT", countAll, df.format(sumTotAMT));
    //===============pangben modify 20110415 stop
  //  System.out.println("data"+data);
    TABLE.setParmValue(data);
    startDate = StringTool.getString(TCM_Transform.getTimestamp(
        getValue("DATE_S")), "yyyyMMdd");
    endDate = StringTool.getString(TCM_Transform.getTimestamp(
        getValue("DATE_E")), "yyyyMMdd");
}

private TParm getTableData(TParm re){
       HashSet dept_list = new HashSet();//记录所有开单科室CODE（去除重复）
       for(int i=0;i<re.getCount();i++){
           //===========pangben modify  20110414 start  对比区域和DEPT_CODE的条件判断
           String []temp=new String[2];
           temp[0]=re.getValue("DEPT_CODE",i);
           temp[1]=re.getValue("REGION_CHN_DESC",i);
           dept_list.add(temp);//填入开单科室CODE 重复的会被过滤
           //===========pangben modify  20110414 stop
       }
       TParm result = new TParm();
       //循环每一位开单部门，汇总该部门的开单信息
       Iterator list = dept_list.iterator();
       while(list.hasNext()){
           String[] dept_code = (String[])list.next();//科室CODE
      //     System.out.println("dept_code:"+dept_code[0]);
           int rowCount = re.getCount();
           TParm deptData = new TParm();//记录某一科室的所有数据
           //循环数据 将同一部门的所有数据汇总起来用以计算
           for(int i=rowCount-1;i>=0;i--){
               //=======pangben modify 20110414 start
               if(dept_code[0].equals(re.getValue("DEPT_CODE",i))&&dept_code[1].equals(re.getValue("REGION_CHN_DESC",i))){
                   deptData.addRowData(re,i);
                   re.removeRow(i);//已经汇总的数据删除掉，减少以后的循环次数
               }
               //=======pangben modify 20110414 stop
           }
           //再根据每个医生将数据整理为表格可以绑定的形式
           TParm tableData = this.getDrData(deptData);
           //将各个科室的数据汇总
           for (int i = 0; i < tableData.getCount("DEPT_CODE"); i++) {
               result.addRowData(tableData, i);
           }

      }
       return result;
}
/**
 * 将数据整理为表格可以绑定的形式
 * @param re TParm
 * @return TParm
 */
private TParm getDrData(TParm re){
   DecimalFormat df = new DecimalFormat("0.00");
   TParm result = new TParm();
   HashSet dr_list = new HashSet();//记录所有开单医师ID（去除重复）
   for(int i=0;i<re.getCount();i++){
       //=====pangben modify 20110414 start 对比区域和DR_CODE的条件判断
       String [] temp=new String[2];
       temp[0]=re.getValue("DR_CODE",i);
       temp[1]=re.getValue("REGION_CHN_DESC",i);
       dr_list.add(temp);//填入开单医师ID 重复的会被过滤
       //=====pangben modify 20110414 stop
   }
   String[] charge_code = chageList.split(";");//每种费用的code
   //循环每一位开单医师，汇总该医师的开单信息
   Iterator list = dr_list.iterator();
   while(list.hasNext()){
       //====pangben modify 20110414
       String [] dr_code = (String[])list.next();//开单医师code
       String dr_name = "";//记录医生姓名
       String dept_desc = "";//记录科室名称
       String dept_code = "";//记录科室CODE
       String region_desc="";//===========pangben modify 20110414 记录区域
       double TOT_AMT = 0;//计算各种费用的合计
       //循环每种费用的code
       for(int h=0;h<charge_code.length;h++){
           int rowCount = re.getCount("DR_CODE");
           double ar_amt = 0;//记录费用(每种费用的值)
           for (int i = rowCount - 1; i >= 0; i--) {
               //判断此条数据是否是该开单医师
               if (dr_code[0].equals(re.getValue("DR_CODE", i))&&dr_code[1].equals(re.getValue("REGION_CHN_DESC", i))) {
                   dr_name = re.getValue("USER_NAME",i);
                   dept_desc = re.getValue("DEPT_CHN_DESC",i);
                   dept_code = re.getValue("DEPT_CODE",i);
                   region_desc=re.getValue("REGION_CHN_DESC",i);//=======pangben modify 20110414
                   //判断费用类别，根据费用类别插入结果集中
                   if(charge_code[h].equals(re.getValue("REXP_CODE",i))){
                       ar_amt += re.getDouble("AR_AMT",i);
                       re.removeRow(i);
                   }
               }
           }
           TOT_AMT += ar_amt;
           result.addData(charge_code[h],df.format(ar_amt));
       }
       result.addData("REGION_CHN_DESC",region_desc);//============pangben modify 20110414
       result.addData("DEPT_CODE",dept_code);
       result.addData("DEPT_DESC",dept_desc);
       result.addData("DR_CODE",dr_code);
       result.addData("DR_NAME",dr_name);
       result.addData("TOT_AMT",df.format(TOT_AMT));
   }
   return result;
}

/**
 * 明细
 */
public void onDetial() {
    int row = TABLE.getSelectedRow();
    if (row < 0) {
        return;
    }
    TParm parm = new TParm();
    parm.setData("DATE_S", startDate);
    parm.setData("DATE_E", endDate);
    parm.setData("DEPT_CODE", data.getValue("DEPT_CODE", row));
    parm.setData("DR_CODE", data.getValue("DR_CODE", row));
    this.openDialog("%ROOT%\\config\\opb\\OPBExecutiveDetial.x", parm);
}

/**
 * 科室选择事件
 */
public void onDeptSelect() {
    this.clearValue("DR_CODE");
    callFunction("UI|DR_CODE|onQuery");
}
/**
 * 打印
 */
public void onPrint(){
    int rowCount = data.getCount("DEPT_DESC");
    if(rowCount<=0){
        return;
    }
    //System.out.println("data:"+data);
    TParm T1 = new TParm();//表格数据
    for(int i=0;i<rowCount;i++){
        T1.addRowData(data,i);
    }
    T1.setCount(rowCount);
    String[] chage = ParmMap.split(";");
    for(int i=0;i<chage.length;i++){
       // System.out.println("chage::"+ chage1[i]);
        T1.addData("SYSTEM", "COLUMNS", chage[i]);
    }
    TParm printData = new TParm();
    printData.setData("T1",T1.getData());
    String DATE = startDate.substring(0,4)+"/"+startDate.substring(4,6)+"/"+startDate.substring(6,8) +
        "～" + endDate.substring(0,4) + "/" + endDate.substring(4,6) + "/" + endDate.substring(6,8);
    printData.setData("DATE","TEXT",DATE);
    printData.setData("DEPT","TEXT",this.getText("DEPT_CODE"));
    printData.setData("printUser","TEXT",Operator.getName());
    //========pangben modify 20110419 start
    String region = ((TTable)this.getComponent("TABLE")).getParmValue().
                    getRow(0).getValue("REGION_CHN_DESC");
    printData.setData("TITLE", "TEXT",
                      (this.getValue("REGION_CODE") != null &&
                       !this.getValue("REGION_CODE").equals("") ? region :
                       "所有医院") + "执行科室统计表");
    //========pangben modify 20110419 stop

    printData.setData("printDate","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd"));
    this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBExecutiveDeptList.jhw",printData);
}
/**
 * 导出Excel表格
 */
public void onExport() {
    TTable table = (TTable) callFunction("UI|TABLE|getThis");
    if (table.getRowCount() <= 0) {
        messageBox("无导出资料");
        return;
    }
    ExportExcelUtil.getInstance().exportExcel(table, "开单科室统计表");
}

}
