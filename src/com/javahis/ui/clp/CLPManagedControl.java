package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import java.util.regex.Pattern;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import jdo.sys.SystemTool;
import com.dongyang.util.StringTool;
import java.util.regex.Matcher;
import java.sql.Timestamp;
import jdo.clp.CLPManagedTool;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import com.dongyang.data.TNull;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import java.util.Map;
import java.util.HashMap;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TKeyListener;
import java.awt.event.KeyEvent;
import jdo.sys.PatTool;
import com.dongyang.ui.TTextField;

/**
 * <p>Title: 展开标准</p>
 *
 * <p>Description: 展开标准</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPManagedControl extends TControl {
    //记录选择行数
    int selectRow = -1;
    //人员table中的选择框对应的column索引
    int checkPatientTableIndex=0;
    public CLPManagedControl() {

    }

    /**
     * 页面初始化方法
     */
    public void onInit() {
        super.onInit();
        initPage();
        this.onClear();
    }

    /**
     * 初始化页面
     */
    private void initPage() {
        //初始化数据
//        initStation();
        //初始化展开时间控件值
        //绑定病患信息表格
        callFunction("UI|PatientTable|addEventListener",
                     "PatientTable->" + TTableEvent.CLICKED, this,
                     "onTableClicked");
        //绑定病患信息表格valueChange
        callFunction("UI|PatientTable|addEventListener",
                     "PatientTable->" + TTableEvent.CHANGE_VALUE, this,
                     "onTableChangeValue");
        //绑定控件事件
        callFunction("UI|MR_NO|addEventListener",
                     "MR_NO->" + TKeyListener.KEY_RELEASED, this,
                     "onKeyReleased");
    }

    public void onKeyReleased(KeyEvent e) {
        if (e.getKeyCode() != 10) {
            return;
        }
        //TTextField ipdNo=(TTextField)this.getComponent("IPD_NO");
        //ipdNo.setFocusable(true);
        //this.onQuery();
    }

    public void onTableClicked(int row) {
        if (row < 0) {
            return;
        }
        initPatientOrderCodeTable();
        //设置选择checkbox选中状态 begin
        TTable patientTable=(TTable)this.getComponent("PatientTable");
        int selectedIndex=patientTable.getSelectedColumn();
        if(selectedIndex==this.checkPatientTableIndex){
            TParm tableParm = patientTable.getParmValue();
            String status=tableParm.getRow(row).getValue("STATUS");
            if("Y".equals(status)){
                status="N";
            }else{
                status="Y";
            }
            tableParm.setData("STATUS",row,status);
            patientTable.setParmValue(tableParm);
        }
        //设置选择checkbox选中状态 end
        selectRow = row;
        //更新病人的选中个数信息
        setpatientPeopleCount();
    }
    /**
     * 增加对Table值改变的监听
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return;
        if (node.getColumn() != 0)
            return;
        //设置人员总数
        this.setpatientPeopleCount();
    }

    public void initPatientOrderCodeTable() {
        int row = this.getSelectedRow("PatientTable");
        TTable table = (TTable)this.getComponent("PatientTable");
        TParm tableTParm = table.getParmValue();
        TParm parmRow = tableTParm.getRow(row);
        //this.putBasicSysInfoIntoParm(parmRow);
        TParm result = CLPManagedTool.getInstance().selectPatientOrderInfo(
                parmRow);
        this.callFunction("UI|TABLE|setParmValue", result);
    }

    /**
     * 查询病人窗口
     */
    public void onQuery() {
        TParm selectParm = getSelectedParm();
        this.putBasicSysInfoIntoParm(selectParm);
        TParm result = CLPManagedTool.getInstance().selectPatient(selectParm);
        if (result.getErrCode()<0) {
			this.messageBox("查询失败");
			return;
		}
        if (result.getCount()<=0) {
			this.messageBox("没有查询的数据");
			this.setValue("total",0+"");
			callFunction("UI|PatientTable|setParmValue", new TParm());
			return;
		}
        String sql="SELECT CASE_NO FROM CLP_MANAGED WHERE CASE_NO='";
        TParm clpParm=null;
        String openUp=this.getValueString("OPEN_UP");
        TParm parmValue=new TParm();
        //int index=0;
        String tempSql="";
        for (int i = 0; i < result.getCount(); i++) {
        	tempSql=sql+result.getValue("CASE_NO",i)+"'";
        	clpParm= new TParm(TJDODBTool.getInstance().select(tempSql));
			if (openUp.equals("2")) {
				if (clpParm.getCount()>0) {
					getParmValue(parmValue, result, i);
					parmValue.addData("OPEN_UP", "2");
				}
			
			}else if(openUp.equals("3")){
				if (clpParm.getCount()<=0) {
					getParmValue(parmValue, result, i);
					parmValue.addData("OPEN_UP","3");
				}
			}else{
				if (clpParm.getCount()>0) {
					getParmValue(parmValue, result, i);
					parmValue.addData("OPEN_UP", "2");
				}else{
					getParmValue(parmValue, result, i);
					parmValue.addData("OPEN_UP","3");
				}
			}
		}
        parmValue.setCount(parmValue.getCount("CASE_NO"));
        callFunction("UI|PatientTable|setParmValue", parmValue);
        //将人数信息设置为0
        this.setValue("total",parmValue.getCount("CASE_NO")<0?0:parmValue.getCount("CASE_NO")+"");
    }
    private void getParmValue(TParm parm,TParm result,int index){
    	parm.addData("STATION_CODE", result.getValue("STATION_CODE",index));
    	parm.addData("PAT_NAME", result.getValue("PAT_NAME",index));
    	parm.addData("DEPT_CODE", result.getValue("DEPT_CODE",index));
    	parm.addData("STATUS", result.getValue("STATUS",index));
    	parm.addData("CASE_NO", result.getValue("CASE_NO",index));
    	parm.addData("MR_NO", result.getValue("MR_NO",index));
    	parm.addData("IPD_NO", result.getValue("IPD_NO",index));
    	parm.addData("BED_NO", result.getValue("BED_NO",index));
    	parm.addData("VERSION", result.getValue("VERSION",index));
    	parm.addData("REGION_CODE", result.getValue("REGION_CODE",index));
    	parm.addData("CLNCPATH_CODE", result.getValue("CLNCPATH_CODE",index));
    }
    /**
     * 得到查询条件
     * @return TParm
     */
    private TParm getSelectedParm() {
        TParm parm = new TParm();
        this.putParamWithObjNameForQuery("DEPT_CODE", parm);
        this.putParamWithObjNameForQuery("STATION_CODE", parm);
        this.putParamLikeWithObjName("BED_NO", parm);
        String mr_no = this.getValueString("MR_NO");
        if (!(mr_no == null || mr_no.length() <= 0)) {
            //自动补齐mr_no
            mr_no = PatTool.getInstance().checkMrno(mr_no);
            parm.setData("MR_NO",mr_no);
        }
        return parm;
    }

    /**
     * 清空方法
     */
    public void onClear() {
        //清空时间
//        List<String> inputList = new ArrayList<String>();
//        inputList.add("DEPT_CODE");
//        inputList.add("STATION_CODE");
//        inputList.add("BED_NO");
//        inputList.add("MR_NO");
//        this.clearInput(inputList);
        this.clearValue("CLNCPATH_CODE;DEPT_CODE;STATION_CODE;BED_NO;MR_NO");
        this.setValue("AMD_TYPE", "2");
        Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.setValue("OPEN_UP", "1");
		callFunction("UI|PatientTable|setParmValue", new TParm());
    }

    /**
     * 展开执行护嘱
     */
    public void nurseWorkListOpen() {

    }

    /**
     * 展开实际
     */
    public void deployPractice() {
        //验证
        if(!checkTablePatientCount()){
            return;
        }
        //记录查询出的病人
        TParm patientTParm = getPatientTABLEInfo();
        if (patientTParm.getCount()<=0) {
			this.messageBox("没有需要操作的数据");
			return;
		}
        TParm clpParm=null;
        TParm clpSParm=null;
        for (int i = 0; i < patientTParm.getCount(); i++) {
        	clpParm= new TParm(TJDODBTool.getInstance().select(
    				"SELECT SCHD_CODE FROM IBS_ORDD WHERE CASE_NO='"
    						+ patientTParm.getValue("CASE_NO",i) + "' GROUP BY SCHD_CODE"));
        	clpSParm= new TParm(TJDODBTool.getInstance().select(
    				"SELECT CLNCPATH_CODE FROM IBS_ORDD WHERE CASE_NO='"
    						+ patientTParm.getValue("CASE_NO",i) + "' GROUP BY CLNCPATH_CODE"));
        	if (clpSParm.getCount()<=0) {
				this.messageBox("病案号："+ patientTParm.getValue("MR_NO",i)+
						"姓名:"+patientTParm.getValue("PAT_NAME",i)+"没有获得此病患医嘱数据");
				return;
			}
        	if (clpSParm.getCount()>1) {
        		if (this.messageBox("提示","病案号："+ patientTParm.getValue("MR_NO",i)+
						"姓名:"+patientTParm.getValue("PAT_NAME",i)+"交易表中存在为空值或多个路径的情况,是否继续",2)!=0) {
					return;
				}
			}
        	for (int j = 0; j < clpParm.getCount(); j++) {
				if (null==clpParm.getValue("SCHD_CODE",j)||clpParm.getValue("SCHD_CODE",j).length()<=0) {
					if (this.messageBox("提示","病案号："+ patientTParm.getValue("MR_NO",i)+
						"姓名:"+patientTParm.getValue("PAT_NAME",i)+"存在为空值时程,是否继续",2)!=0) {
						return;
					}
				}
			}
		}
        TParm inputParm = new TParm();
        inputParm.setData("patientTParm", patientTParm.getData());
        inputParm.setData("deployDate", this.getDeployDate());
        inputParm.setData("operator", getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagedAction",
                "deployPractice", inputParm);
        if (result.getErrCode() < 0) {
            this.messageBox("展开失败");
        } else {
            this.messageBox("展开成功");
        }

    }
    /**
     * 检查查询病患且选中病患的个数是否大于0方法-查询调用
     * @return boolean
     */
    private boolean checkTablePatientCount(){
        boolean flag=true;
        //设置选中的病患个数
        this.setpatientPeopleCount();
        int totalCount=this.getIntValue(this.getValueString("total"));
        if(totalCount>0){
            flag=true;
        }else{
            this.messageBox("请先选择需要展开的病患信息");
            flag=false;
        }
        return flag;
    }

    /**
     * 全选病人按钮点击
     */
    public void checkAllChecked() {
        setPatientTableCheck();
        //设置表格的选中状态
        setPatientTableCheck();
        //更新病患总数
        this.setpatientPeopleCount();
    }

    /**
     * 设置病患表格的选中状态
     */
    private void setPatientTableCheck() {
        TCheckBox tchkbox = (TCheckBox)this.getComponent("checkAll");
        TTable table = (TTable)this.getComponent("PatientTable");
        TParm tableParm = table.getParmValue();
        String tmpValue = "N";
        if (tchkbox.isSelected()) {
            tmpValue = "Y";
        }
        for (int i = 0; i < tableParm.getCount(); i++) {
            tableParm.setData("STATUS", i, tmpValue);
        }
        table.setParmValue(tableParm);
    }

//    /**
//     * 得到选取的病人选择方式
//     * 返回 1:全部 2：从病区选择 3:从床号选择 4 : 从住院号选择 5:mr_no 6:ipd_no
//     */
//    private int getPatientSelectType() {
//        int returnvalue = -1;
//        //全院病人
//        TRadioButton allPatient = (TRadioButton)this.getComponent("AllPatient");
//        if (allPatient.isSelected()) {
//            returnvalue = 1;
//        }
//        //根据床号选择病人
//        TRadioButton patientByBed = (TRadioButton)this.getComponent(
//                "PatientByBed");
//        if (patientByBed.isSelected()) {
//            returnvalue = 3;
//        }
////        //根据case_no选择病人
////        TRadioButton patientByCaseNo = (TRadioButton)this.getComponent("PatientByCaseNo");
////        if(patientByCaseNo.isSelected()){
////            returnvalue=4;
////        }
//        //根据mr_no选择病人
//        TRadioButton patientByMrNo = (TRadioButton)this.getComponent(
//                "PatientByMrNo");
//        if (patientByMrNo.isSelected()) {
//            returnvalue = 5;
//        }
//        //根据ipd_no选择病人
//        TRadioButton patientByIPDNo = (TRadioButton)this.getComponent(
//                "PatientByIPDNo");
//        if (patientByIPDNo.isSelected()) {
//            returnvalue = 6;
//        }
//        //根据病区选择病人
//        TRadioButton patientByStation = (TRadioButton)this.getComponent(
//                "PatientByStation");
//        if (patientByStation.isSelected()) {
//            returnvalue = 2;
//        }
//        return returnvalue;
//    }

//    /**
//     * 根据用户选择查询出对应的病患信息
//     * 类型代码如下：1:全部 2：从病区选择 3:从床号选择 4 : 从住院号选择 5:mr_no 6 :ipd_no
//     * @return TParm
//     */
//    private TParm getPatientInfoByChoiceTypeId(int  choiceId){
//        if(choiceId == -1){
//            return null;
//        }
//        TParm returnParm = new TParm();
//        TParm selectParm = new TParm();
//        selectParm.setData("REGION_CODE",Operator.getRegion());
//        //全部
//        if(choiceId==1){
//            //暂无操作
//            returnParm = CLPManagedTool.getInstance().selectPatient(selectParm);
//            System.out.println("所有病人个数:" + returnParm);
//        }
//        //病区选择
//        if(choiceId==2){
//            TTable table =(TTable)this.getComponent("TABLE");
//            //接收table最后一次输入值，不加此行代码table最后一个输入的值无法获取
//            table.acceptText();
//            int rowcount= table.getRowCount();
//            List stationList = new ArrayList();
//            for(int i=0;i<rowcount;i++){
//                String isFlagStr = (String)table.getValueAt(i,3);
//                if("Y".equals(isFlagStr)){
//                    stationList.add(table.getValueAt(i,0));
//                }
//            }
//            selectParm.setData("StationList",stationList);
//            returnParm = CLPManagedTool.getInstance().selectPatientByStationCodeList(selectParm);
//        }
//        //床号选择
//        if(choiceId==3){
//            selectParm.setData("BED_NO",this.getValue("bed_no"));
//            returnParm = CLPManagedTool.getInstance().selectPatient(selectParm);
//            System.out.println("所有病人个数:" + returnParm);
//        }
//        //case_no选择
//        if(choiceId==4){
//            System.out.println("住院号查询病患");
//            selectParm.setData("CASE_NO",this.getValue("case_no"));
//            returnParm = CLPManagedTool.getInstance().selectPatient(selectParm);
//            System.out.println("所有病人个数:" + returnParm);
//        }
//        //mr_no选择
//        if(choiceId==5){
//            System.out.println("mr_no查询病患");
//            String mr_no=(String)this.getValue("mr_no");
//            mr_no = PatTool.getInstance().checkMrno(mr_no);
//            selectParm.setData("MR_NO",mr_no);
//            returnParm = CLPManagedTool.getInstance().selectPatient(selectParm);
//            System.out.println("所有病人个数:" + returnParm);
//        }
//        //ipd_no选择
//        if(choiceId==6){
//            System.out.println("住院号ipd_no查询病患");
//            selectParm.setData("IPD_NO",this.getValue("ipd_no"));
//            returnParm = CLPManagedTool.getInstance().selectPatient(selectParm);
//            System.out.println("所有病人个数:" + returnParm);
//        }
//        return returnParm;
//    }
    /**
     * 还原实际
     */
    public void returnPractice() {
        //验证
        if (!checkTablePatientCount()) {
            return;
        }
        //记录查询出的病人
        TParm patientTParm = getPatientTABLEInfo();
        TParm inputParm = new TParm();
        inputParm.setData("patientTParm", patientTParm.getData());
        inputParm.setData("operator", getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagedAction",
                "returnPractice", inputParm);
        if (result.getErrCode() < 0) {
            this.messageBox("还原失败");
        } else {
            this.messageBox("还原成功");
        }
    }

    /**
     * 展开标准
     */
    public void deployStandard() {
        //验证
        if (!checkTablePatientCount()) {
            return;
        }
        //记录查询出的病人
        TParm patientTParm = getPatientTABLEInfo();
        TParm inputParm = new TParm();
        inputParm.setData("patientTParm", patientTParm.getData());
        inputParm.setData("deployDate", this.getDeployDate());
        inputParm.setData("operator", getBasicOperatorMap());
        TParm result = TIOM_AppServer.executeAction(
                "action.clp.CLPManagedAction",
                "deployStandard", inputParm);
        if (result.getErrCode() < 0) {
            this.messageBox("展开标准失败");
        } else {
            this.messageBox("展开标准成功");
        }
    }

    /**
     * 设置病患表格的数量
     */
    private void setpatientPeopleCount() {
        TTable table = (TTable)this.getComponent("PatientTable");
//        int total = 0;
//        table.acceptText();
//        TParm tableParm = table.getParmValue();
//        for (int i = 0; i < tableParm.getCount(); i++) {
//            TParm parmRow = tableParm.getRow(i);
//            if ("Y".equalsIgnoreCase(parmRow.getValue("STATUS"))) {
//                total++;
//            }
//        }
//        this.setValue("total", total + "");
    }

    /**
     * 得到病患表格中选择的病患
     * @return TParm
     */
    private TParm getPatientTABLEInfo() {
        TParm returnParm=new TParm();
        TTable table = (TTable)this.getComponent("PatientTable");
        table.acceptText();
        TParm tableParm = (TParm)this.callFunction(
                "UI|PatientTable|getParmValue");
        int count=0;
        for (int i = 0; i < tableParm.getCount(); i++) {
            TParm rowParm = tableParm.getRow(i);
            if ("N".equalsIgnoreCase(rowParm.getValue("STATUS"))) {
            }else{
            	//============pangb  20120522 优化程序
            	returnParm.setRowData(count, tableParm, i);
            	//============pangb  20120522 stop
                count++;
            }
        }
//        for (Integer tmp : rowIdList) {
//            tableParm.removeRow(tmp);
//        }
//        tableParm.setCount(count);
        returnParm.setCount(count);
        return returnParm;
    }

    /**
     * 得到展开时间
     * @return String
     */
    private String getDeployDate() {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        String datestrtmp = this.getValueString("deployDate");
        if (!this.checkNullAndEmpty(datestrtmp)) {
            datestrtmp = datestr;
        } else {
            datestrtmp = datestrtmp.substring(0, 10).replace("-", "");
        }
        return datestrtmp;
    }

    /**
     * 将表格的对应单元格设置成可写，其他的设置成不可写
     * @param tableName String
     * @param rowNum int
     * @param columnNum int
     */
    private void setTableEnabled(String tableName, int rowNum, int columnNum) {
        TTable table = (TTable)this.getComponent(tableName);
        int totalColumnMaxLength = table.getColumnCount();
        int totalRowMaxLength = table.getRowCount();
        //锁列
        String lockColumnStr = "";
        for (int i = 0; i < totalColumnMaxLength; i++) {
            if (!(i + "").equals(columnNum + "")) {
                lockColumnStr += i + ",";
            }
        }
        lockColumnStr = lockColumnStr.substring(0, lockColumnStr.length() - 1);
        table.setLockColumns(lockColumnStr);
        //锁行
        String lockRowStr = "";
        for (int i = 0; i < totalRowMaxLength; i++) {
            if (!(i + "").equals(rowNum + "")) {
                lockRowStr += i + ",";
            }
        }
        lockRowStr = lockRowStr.substring(0,
                                          ((lockRowStr.length() - 1) < 0 ? 0 :
                                           (lockRowStr.length() - 1)));
        if (lockRowStr.length() > 0) {
            table.setLockRows(lockRowStr);
        }

    }

    /**
     * 将控件值放入TParam方法(可以传入放置参数值)
     * @param objName String
     */
    private void putParamWithObjName(String objName, TParm parm,
                                     String paramName) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        parm.setData(paramName, objstr);
    }


    /**
     * 将控件值放入TParam方法(放置参数值与控件名相同)
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        objstr = objstr;
        //参数值与控件名相同
        parm.setData(objName, objstr);
    }

    /**
     * 将控件值放入TParam方法(可以传入放置参数值)
     * @param objName String
     */
    private void putParamLikeWithObjName(String objName, TParm parm,
                                         String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            parm.setData(paramName, objstr);
        }

    }

    /**
     * 将控件值放入TParam方法(放置参数值与控件名相同)
     * @param objName String
     * @param parm TParm
     */
    private void putParamLikeWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr + "%";
            //参数值与控件名相同
            parm.setData(objName, objstr);
        }
    }

    /**
     * 用于放置用于完全匹配进行查询的控件
     * @param objName String
     * @param parm TParm
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm) {
        putParamWithObjNameForQuery(objName, parm, objName);
    }

    /**
     * 用于放置用于完全匹配进行查询的控件
     * @param objName String
     * @param parm TParm
     * @param paramName String
     */
    private void putParamWithObjNameForQuery(String objName, TParm parm,
                                             String paramName) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            //参数值与控件名相同
            parm.setData(paramName, objstr);
        }
    }

    /**
     * 检查控件是否为空
     * @param componentName String
     * @return boolean
     */
    private boolean checkComponentNullOrEmpty(String componentName) {
        if (componentName == null || "".equals(componentName)) {
            return false;
        }
        String valueStr = this.getValueString(componentName);
        if (valueStr == null || "".equals(valueStr)) {
            return false;
        }
        return true;
    }

    /**
     * 得到指定table的选中行
     * @param tableName String
     * @return int
     */
    private int getSelectedRow(String tableName) {
        int selectedIndex = -1;
        if (tableName == null || tableName.length() <= 0) {
            return -1;
        }
        Object componentObj = this.getComponent(tableName);
        if (!(componentObj instanceof TTable)) {
            return -1;
        }
        TTable table = (TTable) componentObj;
        selectedIndex = table.getSelectedRow();
        return selectedIndex;
    }

    /**
     * 数字验证方法
     * @param validData String
     * @return boolean
     */
    private boolean validNumber(String validData) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher match = p.matcher(validData);
        if (!match.matches()) {
            return false;
        }
        return true;
    }

    /**
     * 向TParm中加入系统默认信息
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
        int total = parm.getCount();
        parm.setData("REGION_CODE", Operator.getRegion());
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM",  Operator.getIP());
        //parm.setData("MR_NO", this.getValue("MR_NO"));
        parm.setData("AMD_TYPE",  this.getValue("AMD_TYPE"));
        parm.setData("CLNCPATH_CODE",  this.getValue("CLNCPATH_CODE"));
        parm.setData("IPD_NO", "");
        parm.setData("START_DATE", SystemTool.getInstance().getDateReplace(this.getValueString("START_DATE"), true).toString());
        parm.setData("END_DATE", SystemTool.getInstance().getDateReplace(this.getValueString("END_DATE"), true).toString());
    }

    /**
     * 根据Operator得到map
     * @return Map
     */
    private Map getBasicOperatorMap() {
        Map map = new HashMap();
        map.put("REGION_CODE", Operator.getRegion());
        map.put("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        map.put("OPT_DATE", datestr);
        map.put("OPT_TERM", Operator.getIP());
        return map;
    }

    /**
     * 得到当前时间字符串方法
     * @param dataFormatStr String
     * @return String
     */
    private String getCurrentDateStr(String dataFormatStr) {
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, dataFormatStr);
        return datestr;
    }

    /**
     * 得到当前时间字符串方法
     * @return String
     */
    private String getCurrentDateStr() {
        return getCurrentDateStr("yyyyMMdd");
    }

    /**
     * 检查是否为空或空串
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }

    /**
     * 拷贝TParm
     * @param from TParm
     * @param to TParm
     * @param row int
     */
    private void cloneTParm(TParm from, TParm to, int row) {
        for (int i = 0; i < from.getNames().length; i++) {
            to.addData(from.getNames()[i],
                       from.getValue(from.getNames()[i], row));
        }
    }

    /**
     * 克隆对象
     * @param parm TParm
     * @return TParm
     */
    private TParm cloneTParm(TParm from) {
        TParm returnTParm = new TParm();
        for (int i = 0; i < from.getNames().length; i++) {
            returnTParm.setData(from.getNames()[i],
                                from.getValue(from.getNames()[i]));
        }
        return returnTParm;
    }

    /**
     * 处理TParm 里的null的方法
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNullVector(TParm parm, String keyStr, Class type) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (parm.getData(keyStr, i) == null) {
                TNull tnull = new TNull(type);
                parm.setData(keyStr, i, tnull);
            }
        }
    }

    /**
     * 处理TParm 里null值方法
     * @param parm TParm
     * @param keyStr String
     * @param type Class
     */
    private void putTNull(TParm parm, String keyStr, Class type) {
        if (parm.getData(keyStr) == null) {
            TNull tnull = new TNull(type);
            parm.setData(keyStr, tnull);
        }
    }

    /**
     * 清空集合中对应的输入框的值
     * @param inputNames List
     */
    private void clearInput(List<String> inputNames) {
        for (String inputstr : inputNames) {
            this.setValue(inputstr, "");
        }
    }

    /**
     * 得到int数值
     * @param str String
     * @return int
     */
    private int getIntValue(String str) {
        int tmp = 0;
        try {
            tmp = Integer.parseInt(str);
        } catch (Exception e) {
            tmp = 0;
        }
        return tmp;
    }


}
