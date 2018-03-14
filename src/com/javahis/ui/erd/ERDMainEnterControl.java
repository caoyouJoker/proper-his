package com.javahis.ui.erd;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.SwingUtilities;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TPanel;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

import jdo.ekt.EKTIO;
import jdo.erd.ERDLevelTool;
import jdo.erd.ErdForBedAndRecordTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

/**
 * <p>Title: 急诊留观入口程序  </p>
 *
 * <p>Description: 急诊留观入口程序 </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * @author ZangJH 2009-9-10
 *
 * @version 1.0
 */

public class ERDMainEnterControl extends TControl {

    //启动画面参数
    private String runFlg = "RECORD";

    //医嘱类别
    private TRadioButton Radio0;
    private TRadioButton Radio1;
    private TRadioButton Radio2;

    private TTextFormat from_Date;
    private TTextFormat to_Date;

    private TTextField MR_NO;
    private TTextField NAME;
    private TTextField BED_DESC;
    private TPanel panel;
    private TTable table;

    private String workPanelTag;

    /**
     * 初始化函数
     */
    public void onInit() {
        super.onInit();
        initParmFromOutside();
        // 本界面的初始化
        myInitControler();
        if ("RECORD".equals(this.getRunFlg())) {
            this.setTitle("急诊抢救设定");
        } else if ("CHECK".equals(this.getRunFlg())) {
            this.setTitle("急诊抢救护士站");
            this.callFunction("UI|erdTriage|setVisible", true);
            Radio1.setSelected(true);
        }
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                try {
                    onQuery();// 执行查询
                }
                catch (Exception e) {}
            }
        });
    }

    /**
     * 初始化界面参数caseNo/stationCode（从外部病患管理界面传来的参数）
     */
    public void initParmFromOutside() {
        // 从框架拿到
        Object obj = this.getParameter();
        // this.messageBox_(outsideParm);
        if (obj != null) {
            this.setRunFlg(this.getParameter().toString());
        }
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        table.setParmValue(new TParm());
        // 初始化当前table
        initTable();
    }

    /**
     * 初始化table
     * 查询条件是： caseNo/病区
     */
    public void initTable() {
        TParm selParm = new TParm();
        selParm = getQueryParm();
        if (selParm == null) return;
        TParm query = ErdForBedAndRecordTool.getInstance().selPatNew(selParm);
        filterQuery(query);
        if (query.getCount() <= 0) {
            table.setParmValue(query);
            this.messageBox("没有相关数据！");
            return;
        }
        // 循环修改时间格式
        for (int i = 0; i < query.getCount(); i++) {
            String regDate =
                    StringTool.getString((Timestamp) query.getData("REG_DATE", i), "yyyy/MM/dd");
            query.setData("REG_DATE", i, regDate);
        }
        table.setParmValue(query);
        return;
    }

    private void filterQuery(TParm query) {
        int row = query.getCount();
        int effDays = getRegParm().getInt("EFFECT_DAYS", 0);
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        for (int i = row - 1; i >= 0; i--) {
            if (effDays < StringTool.getDateDiffer(now, query.getTimestamp("ADM_DATE", i))) {
                query.removeRow(i);
                continue;
            }
            return;
        }
    }

    private TParm getRegParm() {
        String SQL = "SELECT EFFECT_DAYS FROM REG_SYSPARM";
        return new TParm(TJDODBTool.getInstance().select(SQL));
    }

    /**
     * 获得界面上的所有查询参数
     * 
     * @return TParm
     */
    public TParm getQueryParm() {
        TParm parm = new TParm();
        if (Radio0.isSelected()) parm.setData("ADM_STATUS", 2); // 待诊
        else if (Radio1.isSelected()) parm.setData("ADM_STATUS", 5); // 留观
        else if (Radio2.isSelected()) {
            if (getValueString("from_Date").length() != 0
                    && getValueString("to_Date").length() == 0) {
                messageBox("时间日期不合法");
                return null;
            }
            if (getValueString("from_Date").length() == 0
                    && getValueString("to_Date").length() != 0) {
                messageBox("时间日期不合法");
                return null;
            }
            if (getValueString("from_Date").length() != 0
                    && getValueString("to_Date").length() != 0) {
                if (getValueString("from_Date").compareTo(getValueString("to_Date")) > 0) {
                    messageBox("时间日期不合法");
                    return null;
                }
                parm.setData("START_DATE", this.getValue("from_Date"));
                parm.setData("END_DATE", this.getValue("to_Date"));
            }
            parm.setData("ADM_STATUS", 9); // 已转出
        }
        // 通过MR_NO拿到CASE_NO
//        String mrNo = MR_NO.getValue();
//        if (!"".equals(mrNo)) {
//            String case_no =
//                    ((TParm) PatAdmTool.getInstance()
//                            .selMaxCaseNoByMrNo(
//                                                PatTool.getInstance()
//                                                        .checkMrno(getValueString("MR_NO")),
//                                                Operator.getRegion())).getValue("CASE_NO", 0);
//            parm.setData("CASE_NO", case_no);
//        }
        if (getValueString("ERD_REGION").length() != 0) parm.setData("ERD_REGION",
                                                                     getValue("ERD_REGION"));
        if (getValueString("MR_NO").length() != 0){
        	String mrNo = getValueString("MR_NO");
        	Pat pat = Pat.onQueryByMrNo(TypeTool.getString(mrNo));
            String srcMrNo = PatTool.getInstance().checkMrno(mrNo);
            if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
                this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
            }
        	
        	this.setValue("MR_NO", pat.getMrNo());
        	parm.setData("MR_NO", this.getValueString("MR_NO"));
        } 
        
       /* modified by Eric 20170524 start
        add time limit*/
        SimpleDateFormat dft = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");		
		Date beginDate = new Date();		
		Calendar date = Calendar.getInstance();		
		date.setTime(beginDate);		
		date.set(Calendar.DATE, date.get(Calendar.DATE) - 30);	
		Date endDate = null;
		try {
			endDate = dft.parse(dft.format(date.getTime()));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String date2 = this.dateToString(endDate);
		parm.setData("date2", date2);
		/*modified by Eric 20170524 end*/
        return parm;
    }

    /**
     * 首先得到所有UI的控件对象/注册相应的事件
     */
    public void myInitControler() {
    	 this.callFunction("UI|erdTriage|setVisible", false);
        // 得到时间控件
        from_Date = (TTextFormat) this.getComponent("from_Date");
        to_Date = (TTextFormat) this.getComponent("to_Date");
        // 得到table控件
        table = (TTable) this.getComponent("TABLE");
        // 得到查询条件UI的对象
        Radio0 = (TRadioButton) this.getComponent("Radio0");
        Radio1 = (TRadioButton) this.getComponent("Radio1");
        Radio2 = (TRadioButton) this.getComponent("Radio2");
        MR_NO = (TTextField) this.getComponent("MR_NO");
        NAME = (TTextField) this.getComponent("NAME");
        BED_DESC = (TTextField) this.getComponent("BED_DESC");
        panel = (TPanel) this.getComponent("PANEL");
        // TABLE双击事件
        callFunction("UI|TABLE|addEventListener", "TABLE" + "->" + TTableEvent.DOUBLE_CLICKED,
                     this, "onTableDoubled");
    }

    /**
     * 双击事件
     * 
     * @param row
     *            int
     */
    public void onTableDoubled(int row) {
        if (row < 0) return;
        // lockUpContorl(false);
        TParm tableValue = table.getParmValue();
        String mrNo = (String) tableValue.getData("MR_NO", row);
        String name = (String) tableValue.getData("PAT_NAME", row);
        String caseNo = (String) tableValue.getData("CASE_NO", row);
        if ("RECORD".equals(getRunFlg())) {
            lockUpContorl(false);
            // 调用界面传参
            TParm parmToErd = new TParm();
            parmToErd.setData("MR_NO", mrNo);
            parmToErd.setData("PAT_NAME", name);
            parmToErd.setData("CASE_NO", caseNo);
            // 护士调用标记，有别于医生
            parmToErd.setData("FLG", "NURSE");
            // 加载ERD记录主界面
            table.setVisible(false);
            panel
                    .addItem("ERDDynamicRcd", "%ROOT%\\config\\erd\\ERDDynamicRcd.x", parmToErd,
                             false);
            workPanelTag = "ERDDynamicRcd";
        } else if ("CHECK".equals(getRunFlg())) {
            // ===只有留观可以进入主界面
//            if (this.getValueBoolean("Radio1")) {// 留观中
                lockUpContorl(false);
                // 调用界面传参
                TParm parmToExec = new TParm();
                parmToExec.setData("MR_NO", mrNo);
                parmToExec.setData("PAT_NAME", name);
                parmToExec.setData("CASE_NO", caseNo);
                // 加载ERD记录主界面
                table.setVisible(false);
                panel.addItem("ERDDynamicRcd", "%ROOT%\\config\\erd\\ERDOrderExecMain.x",
                              parmToExec, false);
                workPanelTag = "ERDDynamicRcd";
//            } else {
//                this.messageBox("非留观中不可进入。");
//                return;
//            }
        }
    }

    /**
     * 单击
     */
    public void onClick() {
        String mrNo = (String) table.getValueAt(table.getSelectedRow(), table
                                                .getColumnIndex("MR_NO"));
        String name = (String) table.getValueAt(table.getSelectedRow(), table
                                                .getColumnIndex("PAT_NAME"));
        // 初始化控件
        MR_NO.setValue(mrNo);
        NAME.setValue(name);
        TParm tableParm = table.getParmValue();
        setValue("ERD_REGION", tableParm.getValue("ERD_REGION_CODE", table.getSelectedRow()));
        BED_DESC.setValue(tableParm.getValue("BED_DESC", table.getSelectedRow()));
    }

    /**
     * 补齐MR_NO
     */
    public void onMrNo() {
        String mrNo = MR_NO.getValue();
        
        Pat pat = Pat.onQueryByMrNo(TypeTool.getString(mrNo));
        String srcMrNo = PatTool.getInstance().checkMrno(mrNo);
        if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
            this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
        }
        
        MR_NO.setValue(pat.getMrNo());
        NAME.setValue(pat.getName());
        
        
//        MR_NO.setValue(PatTool.getInstance().checkMrno(mrNo));
//        // 得到病患名字
//        getPatName(mrNo);
    }

    /**
     * 获得该病人的姓名
     * 
     * @param mrNo
     *            String
     */
    private void getPatName(String mrNo) {
        NAME.setValue(PatTool.getInstance().getNameForMrno(mrNo));
    }

    /**
     * 激活时间控件
     */
    public void onGetDate(Object flg) {
        if ("2".equals(flg + "")) {
            Timestamp now = TJDODBTool.getInstance().getDBTime();
            int effDays = getRegParm().getInt("EFFECT_DAYS", 0);
            Timestamp last3day = StringTool.rollDate(now, -effDays);
            from_Date.setValue(StringTool.getDate(StringTool.getString(last3day, "yyyyMMdd")
                    + "000000", "yyyyMMddHHmmss"));
            to_Date.setValue(now);
            from_Date.setEnabled(true);
            to_Date.setEnabled(true);
        } else {
            from_Date.setValue(null);
            to_Date.setValue(null);
            from_Date.setEnabled(false);
            to_Date.setEnabled(false);
        }
        // 清空
        table.setParmValue(new TParm());
        onQuery();
    }

    /**
     * 关闭工作页面
     * 
     * @return boolean
     */
    public Object onClose() {
        if (workPanelTag == null || workPanelTag.length() == 0) return null;
        TPanel p = (TPanel) getComponent(workPanelTag);
        if (!p.getControl().onClosing()) {
            return "OK";
        }
        panel.remove(p);
        workPanelTag = null;
        table.setVisible(true);
        // 移除子UIMenuBar
        callFunction("UI|removeChildMenuBar");
        // 移除子UIToolBar
        callFunction("UI|removeChildToolBar");
        // 显示UIshowTopMenu
        callFunction("UI|showTopMenu");
        lockUpContorl(true);
        onQuery();
        return "OK";
    }

    /**
     * 锁解锁上边的控件
     * 
     * @param flg
     *            boolean
     */
    private void lockUpContorl(boolean flg) {
        Radio0.setEnabled(flg);
        Radio1.setEnabled(flg);
        Radio2.setEnabled(flg);
        MR_NO.setEnabled(flg);
        // NAME.setEnabled(flg);
    }

    public String getRunFlg() {
        return runFlg;
    }

    public void setRunFlg(String runFlg) {
        this.runFlg = runFlg;
    }

    /**
     * 清空方法
     */
    public void onClear() {
        setValue("MR_NO", "");
        setValue("NAME", "");
        setValue("ERD_REGION", "");
        setValue("BED_DESC", "");
        ((TTable) getComponent("TABLE")).removeRowAll();
    }

    /**
     * 显示子面板菜单
     */
    public void showMwnu() {
        if (workPanelTag == null || workPanelTag.length() == 0) {
            // 显示UIshowTopMenu
            callFunction("UI|showTopMenu");
            return;
        }
        TPanel p = (TPanel) getComponent(workPanelTag);
        p.getControl().callFunction("onShowWindowsFunction");
    }

    /**
     * 医疗卡读卡方法
     */
    public void onEKT() {
        TParm patParm = EKTIO.getInstance().TXreadEKT();
        // TParm patParm = EKTIO.getInstance().getPat();
        if (patParm.getErrCode() < 0) {
            this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
            return;
        }
        setValue("MR_NO", patParm.getValue("MR_NO"));
        onMrNo();
    }
    
    /**
     * 检伤评估表单查看
     */
    public void onErdTriage(){
         int row = table.getSelectedRow();
         if(row<0){
             this.messageBox_("请选择病患！");
             return;
         }
         TParm dataD = table.getParmValue();
         String caseNo = dataD.getValue("CASE_NO",row);
         String mrNo = dataD.getValue("MR_NO",row);
         String triageNo = dataD.getValue("TRIAGE_NO",row);
         if(triageNo.length() == 0){
        	 this.messageBox("请选择有检伤号的病患！");
        	 return;
         }
        String[] saveFiles = ERDLevelTool.getInstance().getELFile(triageNo);
    	TParm parm = new TParm();
    	parm.setData("CASE_NO", caseNo);
    	parm.setData("MR_NO", mrNo);
    	Pat pat = Pat.onQueryByMrNo(mrNo);
    	Reg reg = Reg.onQueryByCaseNo(pat, caseNo);
    	parm.setData("ADM_DATE", reg.getAdmDate());
    	parm.setData("PAT_NAME", pat.getName());
    	parm.setData("SEX", pat.getSexString());
    	parm.setData("AGE", OdoUtil.showAge(pat.getBirthday(),
 				SystemTool.getInstance().getDate())); //年龄
    	TParm emrFileData = new TParm();
        emrFileData.setData("FILE_PATH", saveFiles[0]);
        emrFileData.setData("FILE_NAME", saveFiles[1]);
        emrFileData.setData("FLG", true);
        parm.setData("EMR_FILE_DATA", emrFileData);
        parm.setData("SYSTEM_TYPE", "EMG");
        parm.setData("RULETYPE", "1");
        parm.setData("ERD",true); 
    	this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }
    

    // 测试用例
    public static void main(String[] args) {
        JavaHisDebug.initClient();
        // JavaHisDebug.TBuilder();
        // JavaHisDebug.TBuilder();
        JavaHisDebug.runFrame("erd\\ERDMainEnter.x");
    }

    /**
	 * Timestamp->String
	 * @author Eric
	 * @param time
	 * @return
	 */
	public String timestampToString(Timestamp ts){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			//方法一
			tsStr = sdf.format(ts);
			System.out.println(tsStr);
			//方法二
			//			tsStr = ts.toString();
			//			System.out.println(tsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tsStr;

	}
	
	/**
	 * Date->String
	 * @author Eric
	 * @param date
	 * @return
	 */
	public String dateToString(Date date){
		if(date == null){
			return null;
		}
		//		Date date = new Date();
		String dateStr = "";
		//format的格式可以任意
		//		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss");
		DateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			//			dateStr = sdf.format(date);
			//			System.out.println(dateStr);
			dateStr = sdf2.format(date);
			System.out.println(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

}
