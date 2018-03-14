package com.javahis.ui.hrm;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.swing.SwingUtilities;

import jdo.bil.BILComparator;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMPatAdm;
import jdo.hrm.HRMPatInfo;
import jdo.hrm.HRMSchdayDr;
import jdo.odo.MedApply;
import jdo.spc.StringUtils;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.DText;
import com.dongyang.tui.text.CopyOperator;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EPage;
import com.dongyang.tui.text.EPanel;
import com.dongyang.tui.text.EPic;
import com.dongyang.tui.text.ETD;
import com.dongyang.tui.text.ETR;
import com.dongyang.tui.text.ETable;
import com.dongyang.tui.text.EText;
import com.dongyang.tui.text.MFocus;
import com.dongyang.ui.TMovePane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.FileTool;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.OdiUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 健检总检 </p>
 *
 * <p>Description: 健检总检 </p>
 *
 * <p>Copyright: javahis 20090922 </p>
 *
 * <p>Company:JavaHis </p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMTotViewControl extends TControl {

    // 患者信息TABLE,医嘱TABLE
    private TTable patTable, orderTable;
    // 病历书写器
    private TWord word;
    private DText text;// wanglong add 20150504
    // 医嘱对象
    private HRMOrder order;
    // 结构化病例是否为文件
    private boolean isOld;
    // EMR共用数据
    private TParm pathParm;
    // 保存时使用的fileNo
    private String fileNo;
    private TParm patUndoParm = new TParm();//wanglong add 20141114
    private TParm patDoParm = new TParm();
    private TParm patAllParm = new TParm();
    private BILComparator compare = new BILComparator();// add by wanglong 20150515
    private boolean ascending = false;
    private int sortColumn = -1;
    private TWindow window1;//片语window wanglong add 20150316
    private TWindow window2;//子模板window wanglong add 20150317
    private String serverPath = "";
    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        // 初始化控件
        initComponent();
        if (this.getPopedem("SYSDBA")) {// 最高权限 wanglong add 20150504
            this.callFunction("UI|ModifyFontCombo|setVisible", true);
            this.callFunction("UI|ModifyFontSizeCombo|setVisible", true);
            this.callFunction("UI|AlignmentLeft|setVisible", true);
            this.callFunction("UI|AlignmentCenter|setVisible", true);
            this.callFunction("UI|AlignmentRight|setVisible", true);
            this.callFunction("UI|FontBMenu|setVisible", true);
            this.callFunction("UI|FontIMenu|setVisible", true);
            this.callFunction("UI|EditWord|setVisible", true);
            this.callFunction("UI|Preview|setVisible", true);
            this.callFunction("UI|InsertTableBase|setVisible", true);
            this.callFunction("UI|TableProperty|setVisible", true);
            this.callFunction("UI|TRProperty|setVisible", true);
            this.callFunction("UI|UniteTD|setVisible", true);
            this.callFunction("UI|InsertPanel|setVisible", true);
            this.callFunction("UI|ParagraphEdit|setVisible", true);
            this.callFunction("UI|DIVProperty|setVisible", true);
            this.callFunction("UI|InsertPic|setVisible", true);
            this.callFunction("UI|DeletePic|setVisible", true);
            this.callFunction("UI|PicDIVProperty|setVisible", true);
            this.callFunction("UI|openFixedProperty|setVisible", true);
            this.callFunction("UI|RemoveFixed|setVisible", true);
        } else if (this.getPopedem("SYSOPERATOR")) {// 角色权限
            this.callFunction("UI|ModifyFontCombo|setVisible", true);
            this.callFunction("UI|ModifyFontSizeCombo|setVisible", true);
            this.callFunction("UI|AlignmentLeft|setVisible", true);
            this.callFunction("UI|AlignmentCenter|setVisible", true);
            this.callFunction("UI|AlignmentRight|setVisible", true);
            this.callFunction("UI|FontBMenu|setVisible", true);
            this.callFunction("UI|FontIMenu|setVisible", true);
            this.callFunction("UI|EditWord|setVisible", true);
            this.callFunction("UI|Preview|setVisible", true);
            this.callFunction("UI|InsertTableBase|setVisible", true);
            this.callFunction("UI|TableProperty|setVisible", true);
            this.callFunction("UI|TRProperty|setVisible", true);
            this.callFunction("UI|UniteTD|setVisible", true);
            this.callFunction("UI|InsertPanel|setVisible", true);
            this.callFunction("UI|ParagraphEdit|setVisible", true);
            this.callFunction("UI|DIVProperty|setVisible", false);
            this.callFunction("UI|InsertPic|setVisible", false);
            this.callFunction("UI|DeletePic|setVisible", false);
            this.callFunction("UI|PicDIVProperty|setVisible", false);
            this.callFunction("UI|openFixedProperty|setVisible", false);
            this.callFunction("UI|RemoveFixed|setVisible", false);
        } else {
            this.callFunction("UI|ModifyFontCombo|setVisible", false);
            this.callFunction("UI|ModifyFontSizeCombo|setVisible", false);
            this.callFunction("UI|AlignmentLeft|setVisible", false);
            this.callFunction("UI|AlignmentCenter|setVisible", false);
            this.callFunction("UI|AlignmentRight|setVisible", false);
            this.callFunction("UI|FontBMenu|setVisible", false);
            this.callFunction("UI|FontIMenu|setVisible", false);
            this.callFunction("UI|EditWord|setVisible", false);
            this.callFunction("UI|Preview|setVisible", false);
            this.callFunction("UI|InsertTableBase|setVisible", false);
            this.callFunction("UI|TableProperty|setVisible", false);
            this.callFunction("UI|TRProperty|setVisible", false);
            this.callFunction("UI|UniteTD|setVisible", false);
            this.callFunction("UI|InsertPanel|setVisible", false);
            this.callFunction("UI|ParagraphEdit|setVisible", false);
            this.callFunction("UI|DIVProperty|setVisible", false);
            this.callFunction("UI|InsertPic|setVisible", false);
            this.callFunction("UI|DeletePic|setVisible", false);
            this.callFunction("UI|PicDIVProperty|setVisible", false);
            this.callFunction("UI|openFixedProperty|setVisible", false);
            this.callFunction("UI|RemoveFixed|setVisible", false);
        }
        // initData();
        // 清空
        onClear();
    }

    /**
     * 初始化控件
     */
    private void initComponent() {
        patTable = (TTable) this.getComponent("PAT_TABLE");
        addSortListener(patTable);// add by wanglong 20130515
        orderTable = (TTable) this.getComponent("ORDER_TABLE");
        word = (TWord) this.getComponent("WORD");
        text = word.getWordText();// wanglong add 20150504
        word.setFontComboTag("ModifyFontCombo");// wanglong add 20150504
        word.setFontSizeComboTag("ModifyFontSizeCombo");
        word.setAlignmentLeftButtonTag("AlignmentLeft");
        word.setAlignmentCenterButtonTag("AlignmentCenter");
        word.setAlignmentRightButtonTag("AlignmentRight");
        word.setFontBoldButtonTag("FontBMenu");
        word.setFontItalicButtonTag("FontIMenu");
        
        // zhanglei 20170907 start 增加初始化默认光标位置
        onFocus();
//        //单击事件
//        callFunction("UI|ORDER_TABLE|addEventListener",
//        		"ORDER_TABLE->" + TTableEvent.CLICKED, this,"onOrder");
//        //双击事件
//        callFunction("UI|ORDER_TABLE|addEventListener",
//        		"ORDER_TABLE->" + TTableEvent.DOUBLE_CLICKED, this,"orderTableDoubleClick");
        // zhanglei 20170907 end 增加初始化默认光标位置
    }
    


	/**
	 * 清空事件
	 */
    public void onClear() {
        initData();
        patTable.removeRowAll();
        orderTable.removeRowAll();
        word.onNewFile();
        word.update();
        fileNo = "";
        this.clearText("MR_NO;PAT_NAME;SEX_CODE;BIRTHDAY");
        clearValue("UNDONE_NUM;DONE_NUM;ALL_NUM");// add by wanglong 20130328
    
        // zhanglei 20170907 start 增加初始化默认光标位置
        this.grabFocus("MR_NO");
        // zhanglei 20170907 end 增加初始化默认光标位置 
        
    }

    /**
     * 初始化数据
     */
    private void initData() {
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String date = StringTool.getString(now, "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(date + "000000", "yyyyMMddHHmmss"));
        this.setValue("END_DATE", StringTool.getTimestamp(date + "235959", "yyyyMMddHHmmss"));
        // Timestamp tomorrow=StringTool.rollDate(now, 1L);
        // this.setValue("START_DATE", now);
        // this.setValue("END_DATE", tomorrow);
        order = new HRMOrder();
        this.setValue("UNDONE", "Y");
        String deptAtt = HRMSchdayDr.getDeptAttribute();
        if (StringUtil.isNullString(deptAtt)) {
            this.messageBox("取得科别属性错误");
            return;
        }
        this.setValue("DEPT_ATT", deptAtt);
    }
    
    /**
     * 获得焦点
     * zhanglei 20170907 start 增加初始化默认光标位置
     */
    public void onFocus(){
    	final TTextField tt = (TTextField)this.getComponent("MR_NO");
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				tt.requestFocusInWindow();
			}
		});
    }
    
    /**
	 * TABLE双击事件 zhanglei 20170908
	 * @param row int
	 */
//	public void onDoubleTableClicked(int row){
//		this.messageBox("11111");
//		TParm parm = orderTable.getParmValue().getRow(row);
//		this.messageBox("row" + row + "parm" + parm);
//		this.setReturnValue(parm);
//		this.closeWindow();
//	}

    /**
     * 点击日期和科别属性时，执行查询
     */
    public void onDoQuery() {
        onQuery();
    }
    
	/**
	 * 查询
	 */
    public void onQuery() {
        Timestamp now = (Timestamp) this.getValue("START_DATE");
        Timestamp tomorrow = (Timestamp) this.getValue("END_DATE");
        String startDate = StringTool.getString(now, "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(tomorrow, "yyyyMMdd") + "235959";
        fileNo = "";
        order = new HRMOrder();
        patUndoParm = order.getFinalCheckPat("", startDate, endDate, "1");// 未完成
        patDoParm = order.getFinalCheckPat("", startDate, endDate, "2");// 已完成
        patAllParm = order.getFinalCheckPat("", startDate, endDate, "");// 全部
        if (patUndoParm.getErrCode() != 0 || patDoParm.getErrCode() != 0
                || patAllParm.getErrCode() != 0) {
            this.messageBox("查询失败 " + patUndoParm.getErrText() + patDoParm.getErrText()
                    + patAllParm.getErrText());
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            this.setValue("ALL_NUM", "");
            patTable.removeRowAll();
            return;
        }
        
        // add by wangb 2016/06/24 根据登录角色类型过滤数据 START
		String filter = "";
		filter = this.getPopedemParm().getValue("ID");
		int doCount = patDoParm.getCount();
		int undoCount = patUndoParm.getCount();
		int allCount = patAllParm.getCount();
		for (int i = doCount - 1; i > -1; i--) {
			if (!filter.contains(patDoParm.getValue("ROLE_TYPE", i))) {
				patDoParm.removeRow(i);
			}
		}

		for (int j = undoCount - 1; j > -1; j--) {
			if (!filter.contains(patUndoParm.getValue("ROLE_TYPE", j))) {
				patUndoParm.removeRow(j);
			}
		}

		for (int k = allCount - 1; k > -1; k--) {
			if (!filter.contains(patAllParm.getValue("ROLE_TYPE", k))) {
				patAllParm.removeRow(k);
			}
		}
     	// add by wangb 2016/06/24 根据登录角色类型过滤数据 END
        
        if ((patUndoParm == null || patUndoParm.getCount() <= 0)
                && (patDoParm == null || patDoParm.getCount() <= 0)
                && (patAllParm == null || patAllParm.getCount() <= 0)) {
            this.messageBox("无数据！");
            this.setValue("UNDONE_NUM", "0人");
            this.setValue("DONE_NUM", "0人");
            this.setValue("ALL_NUM", "0人");
            patTable.removeRowAll();
            return;
        }
        undoCount = patUndoParm.getCount() <= 0 ? 0 : patUndoParm.getCount();
        doCount = patDoParm.getCount() <= 0 ? 0 : patDoParm.getCount();
        allCount = patAllParm.getCount() <= 0 ? 0 : patAllParm.getCount();
        this.setValue("UNDONE_NUM", undoCount + "人");
        this.setValue("DONE_NUM", doCount + "人");
        this.setValue("ALL_NUM", allCount + "人");
        if (this.getValueBoolean("UNDONE")) {// 未完成
            patTable.setParmValue(patUndoParm);
        } else if (this.getValueBoolean("DONE")) {// 完成
            patTable.setParmValue(patDoParm);
        } else {// 全部
            patTable.setParmValue(patAllParm);
        }
    }

    /**
     * 根据MR_NO查询数据
     */
    public void onMrNo() {
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); // ==== chenxi
        this.setValue("MR_NO", mrNo);
        
        // modify by huangtt 20160929 EMPI患者查重提示 start
        Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			 this.setValue("MR_NO", mrNo);
		}
		// modify by huangtt 20160929 EMPI患者查重提示 end
        
        // ========================= caowl 20130326 start
        String startDate = "";//不卡时间条件
        String endDate = "";
        fileNo = "";
        order = new HRMOrder();
        patUndoParm = order.getFinalCheckPat(mrNo, startDate, endDate, "1");
        patDoParm = order.getFinalCheckPat(mrNo, startDate, endDate, "2");
        patAllParm = order.getFinalCheckPat(mrNo, startDate, endDate, "");
        if (patUndoParm.getErrCode() != 0 || patDoParm.getErrCode() != 0
                || patAllParm.getErrCode() != 0) {
            this.messageBox("查询失败 " + patUndoParm.getErrText() + patDoParm.getErrText()
                    + patAllParm.getErrText());
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            this.setValue("ALL_NUM", "");
            patTable.removeRowAll();
            return;
        }
        
        // add by wangb 2016/06/24 根据登录角色类型过滤数据 START
        String filter = "";
		filter = this.getPopedemParm().getValue("ID");
		int doCount = patDoParm.getCount();
		int undoCount = patUndoParm.getCount();
		int allCount = patAllParm.getCount();
		for (int i = doCount - 1; i > -1; i--) {
			if (!filter.contains(patDoParm.getValue("ROLE_TYPE", i))) {
				patDoParm.removeRow(i);
			}
		}

		for (int j = undoCount - 1; j > -1; j--) {
			if (!filter.contains(patUndoParm.getValue("ROLE_TYPE", j))) {
				patUndoParm.removeRow(j);
			}
		}

		for (int k = allCount - 1; k > -1; k--) {
			if (!filter.contains(patAllParm.getValue("ROLE_TYPE", k))) {
				patAllParm.removeRow(k);
			}
		}
     	// add by wangb 2016/06/24 根据登录角色类型过滤数据 END
        
        if ((patUndoParm == null || patUndoParm.getCount() <= 0)
                && (patDoParm == null || patDoParm.getCount() <= 0)
                && (patAllParm == null || patAllParm.getCount() <= 0)) {
            this.messageBox("无数据！");
            this.setValue("UNDONE_NUM", "0人");
            this.setValue("DONE_NUM", "0人");
            this.setValue("ALL_NUM", "0人");
            patTable.removeRowAll();
            return;
        }
        undoCount = patUndoParm.getCount() <= 0 ? 0 : patUndoParm.getCount();
        doCount = patDoParm.getCount() <= 0 ? 0 : patDoParm.getCount();
        allCount = patAllParm.getCount() <= 0 ? 0 : patAllParm.getCount();
        this.setValue("UNDONE_NUM", undoCount + "人");
        this.setValue("DONE_NUM", doCount + "人");
        this.setValue("ALL_NUM", allCount + "人");
        if (patAllParm.getValue("EXEC_DR_CODE", 0).equals("")) {// 查询出的最新一次就诊记录如果为已完成
            this.setValue("UNDONE", "Y");// 未完成
            patTable.setParmValue(patUndoParm);
        } else {// 完成
            this.setValue("DONE", "Y");
            patTable.setParmValue(patDoParm);
        }
        patTable.setSelectedRow(0);
        onPatChoose();
    }
    
    /**
     * 选择状态
     */
    public void onChooseState() {
        int undoCount = patUndoParm.getCount() <= 0 ? 0 : patUndoParm.getCount();
        int doCount = patDoParm.getCount() <= 0 ? 0 : patDoParm.getCount();
        int allCount = patAllParm.getCount() <= 0 ? 0 : patAllParm.getCount();
        this.setValue("UNDONE_NUM", undoCount + "人");
        this.setValue("DONE_NUM", doCount + "人");
        this.setValue("ALL_NUM", allCount + "人");
        if (this.getValueBoolean("UNDONE")) {// 未完成
            patTable.setParmValue(patUndoParm);
        } else if (this.getValueBoolean("DONE")) {// 完成
            patTable.setParmValue(patDoParm);
        } else {// 全部
            patTable.setParmValue(patAllParm);
        }
    }
    /**
     * 总检抓取
     */
    public void onCrawl() {
        if (this.word.getFileOpenName().length() == 0) {
            this.messageBox("无总检文件");
            return;
        }
        int row = patTable.getSelectedRow();
        TParm parm = patTable.getParmValue();
        getEmrAddress(parm.getValue("CASE_NO", row), "04");
        // TWord word = new TWord();
        // word.onOpen("//10//12//000000002078", "101221000014_健检总检_05", 3, false);
        // word.setFileRemark("05|01|06");
        // word.onSave();
        // // System.out.println("" + word.getFileRemark());
    }

    /**
     * 总检部分抓取
     */
    public void onCrawls() {
        // this.messageBox("-------come in===========");
        if (this.word.getFileOpenName().length() == 0) {
            this.messageBox("无总检文件");
            return;
        }
        // 取到人
        int row = patTable.getSelectedRow();
        TParm parm = patTable.getParmValue();
        // System.out.println("-----orderTable----"+ orderTable.getParmValue());
        List<TParm> orders = new ArrayList<TParm>();
        this.orderTable.acceptText();
        // 取要生成的文件
        for (int i = 0; i < orderTable.getParmValue().getCount("ORDER_DESC"); i++) {
            if ((orderTable.getParmValue().getValue("SFLG", i)).equals("Y")) {
                // System.out.println("-----SFLG is Y-------"+orderTable.getParmValue().getRow(i));
                orders.add(orderTable.getParmValue().getRow(i));
            }
        }
        // 如果没有选择的任何文件， 报提示
        if (orders.size() <= 0) {
            this.messageBox("请选择要抓取的问诊医嘱！");
            return;
        }
        // 将文件内容追加到总检最后
        this.onMerge(parm.getValue("CASE_NO", row), "04", orders);
    }

	/**
	 * 病患选择
	 */
	public void onPatChoose(){
        int row = patTable.getSelectedRow();
        TParm parm = patTable.getParmValue();
        if (parm == null) {
            return;
        }
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        String caseNo = parm.getValue("CASE_NO", row);
        if (StringUtil.isNullString(caseNo)) {
            this.messageBox("取得医嘱数据失败");
            return;
        }
        this.setValue("MR_NO", parm.getValue("MR_NO", row));
        this.setValue("PAT_NAME", parm.getValue("PAT_NAME", row));
        this.setValue("SEX_CODE", parm.getValue("SEX_CODE", row));
        this.setValue("BIRTHDAY", parm.getTimestamp("BIRTHDAY", row));
        fileNo = "";
        getPatData(caseNo);
    }

    /**
     * 病患选择
     */
    public void onPatChooseFullWindow() {
        onPatChoose();
        onUnfold();
    }

    /**
     * 根据给入CASE_NO查询数据
     * 
     * @param caseNo
     */
    private void getPatData(String caseNo) {
        if (StringUtil.isNullString(caseNo)) {
            return;
        }
        // System.out.println("----come in getPatData-----");
        TParm parm = order.getParmForReview(caseNo);
        if (parm.getErrCode() != 0) {
            this.messageBox("取得医嘱数据失败 " + parm.getErrText());
            return;
        }
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        TParm review = new TParm();
        for (int i = 0; i < count; i++) {
            if ("04".equalsIgnoreCase(parm.getValue("DEPT_ATTRIBUTE", i))) {
                review = parm.getRow(i);
                break;
            }
        }
        orderTable.setParmValue(parm);
        openFile(review);
    }

    /**
     * 根据给入信息打开结构化病历
     * 
     * @param parm
     */
    private void openFile(TParm parm) {
        if ("LIS".equals(parm.getValue("CAT1_TYPE")) || "RIS".equals(parm.getValue("CAT1_TYPE"))) {
            return;
        }
        TParm emrParm = new TParm();
        emrParm.setData("MR_CODE", parm.getValue("MR_CODE"));
        emrParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        emrParm.setData("ADM_TYPE", "H");
        emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
        if (StringUtil.isNullString(emrParm.getValue("SUBCLASS_CODE"))) {
            this.messageBox("打开病历错误");
            return;
        }
        pathParm = emrParm;
        isOld = TypeTool.getBoolean(emrParm.getData("FLG"));
        //modify by wanglong 20130522
        String caseNo = parm.getValue("CASE_NO");
        String mrNo = parm.getValue("MR_NO");
        HRMPatInfo pat = new HRMPatInfo();
        TParm patParm = pat.getHRMPatInfo(mrNo, caseNo);
        emrParm.setData("PAT_NAME", "TEXT", patParm.getValue("PAT_NAME", 0));
        emrParm.setData("TEL_HOME","TEXT",  patParm.getValue("TEL", 0));
        emrParm.setData("SEX", "TEXT", getDictionary("SYS_SEX", patParm.getValue("SEX_CODE", 0)));
        emrParm.setData("AGE", "TEXT", StringUtil.showAge(patParm.getTimestamp("BIRTHDAY", 0), TJDODBTool.getInstance().getDBTime()));
        emrParm.setData("MARRY","TEXT",  getDictionary("SYS_MARRIAGE", patParm.getValue("MARRIAGE_CODE", 0)));
        emrParm.setData("CHECK_DATE", "TEXT", StringTool.getString(patParm.getTimestamp("START_DATE", 0), "yyyy/MM/dd"));
        //add by wanglong 20130813
        emrParm.setData("COMPANY_DESC", "TEXT", patParm.getValue("COMPANY_DESC", 0));// 团体
        emrParm.setData("CONTRACT_DESC", "TEXT", patParm.getValue("CONTRACT_DESC", 0));// 合同
        emrParm.setData("SEQ_NO", "TEXT", patParm.getValue("SEQ_NO", 0));// 序号
        emrParm.setData("PAT_DEPT", "TEXT", patParm.getValue("PAT_DEPT", 0));// 部门
        emrParm.setData("STAFF_NO", "TEXT", patParm.getValue("STAFF_NO", 0));// 工号
        emrParm.setData("MR_NO1","TEXT", validMrNo(mrNo));
        //modify end
        emrParm.setData("MR_NO", parm.getValue("MR_NO"));
       
        emrParm.setData("CASE_NO", parm.getValue("CASE_NO"));
        emrParm.setData("TXT_MR_NO", parm.getValue("MR_NO"));
        emrParm.setData("REAL_MR", parm.getValue("MR_NO"));
        emrParm.setData("PAT_NAME", parm.getValue("PAT_NAME"));
        Timestamp repDate = EmrUtil.getInstance().getReportDate(parm.getValue("CASE_NO")).getTimestamp("REPORT_DATE");
        emrParm.setData("ADM_DATE", repDate);
        emrParm.setData("FILE_TITLE_TEXT", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
        emrParm.setData("FILE_TITLEENG_TEXT", "TEXT", Manager.getOrganization().getHospitalENGFullName(Operator.getRegion()));
        emrParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", parm.getValue("MR_NO"));
        emrParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", "");
        emrParm.setData("FILE_128CODE", "TEXT", parm.getValue("MR_NO"));
        // ============xueyf modify 20120223 start
        // emrParm.setData("TEMPLET_PATH","JHW\\"+emrParm.getValue("TEMPLET_PATH"));
        emrParm.setData("TEMPLET_PATH", emrParm.getValue("TEMPLET_PATH"));
        // ============xueyf modify 20120223 stop
        // System.out.println("xueyf emrParm="+emrParm);
        if (!isOld) {
            word.onOpen(emrParm.getValue("TEMPLET_PATH"), emrParm.getValue("EMT_FILENAME"), 2, false);
            word.setNodeIndex(-1);
            saveWord();
            emrParm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            word.setWordParameter(emrParm);
            word.setCanEdit(true);
            word.setVisible(false);
            getEmrAddress(parm.getValue("CASE_NO"));//追加问诊等其他病历信息
            word.setVisible(true);
            setMicroField(emrParm);
           // == 20171123 add by zhanglei 增加普华模版信息传入start
            if(TConfig.getSystemValue("HRM.SignIn").equals("普华")){
	            word.setMicroField("PAT_NAME", patParm.getValue("PAT_NAME", 0));//姓名
	            word.setMicroField("SEX", getDictionary("SYS_SEX", patParm.getValue("SEX_CODE", 0)));//性别
	            word.setMicroField("AGE", StringUtil.showAge(patParm.getTimestamp("BIRTHDAY", 0), TJDODBTool.getInstance().getDBTime()));//年龄
	            word.setMicroField("MARRY", getDictionary("SYS_MARRIAGE", patParm.getValue("MARRIAGE_CODE", 0)));//婚否
	            word.setMicroField("COMPANY_DESC", patParm.getValue("COMPANY_DESC", 0));//工作单位
		        TParm patParm2 = new TParm();//得到SYS_PATINFO的用户数据
		        patParm2 = this.queryPat(mrNo);
		        word.setMicroField("BIRTH_DATE", patParm.getValue("BIRTHDAY", 0).substring(0, 10).replace('-', '/'));//出生日期
		        word.setMicroField("IDNO", patParm.getValue("ID_NO", 0));//身份证号
		        word.setMicroField("TEL", patParm.getValue("TEL", 0));//电话
		        word.setMicroField("ADDRESS", patParm2.getValue("ADDRESS", 0));//通讯地址
		        String nationCode = patParm2.getValue("NATION_CODE", 0);//得到国籍
		        String nationDesc = StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", "GROUP_ID = 'SYS_NATION' AND ID = '" + nationCode + "'");
		        word.setMicroField("NATION_DESC", nationDesc);//国籍    
		        String speciesCode = patParm2.getValue("SPECIES_CODE", 0);//得到民族
		        String speciesDesc = StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", "GROUP_ID = 'SYS_SPECIES' AND ID = '" + speciesCode + "'");
		        word.setMicroField("SPECIES_DESC", speciesDesc);//民族  
		        word.setMicroField("E_MAIL", patParm2.getValue("E_MAIL", 0));//电子邮件 E_MAIL
            }
	        // == 20171123 add zhanglei end   
            if (!insertTableData(parm.getValue("CASE_NO"), parm
					.getValue("MR_NO"))) {
				return;
			}
        } else {
            // ============xueyf modify 20120223 start
            String filePath =
                    emrParm.getValue("FILE_PATH").indexOf("JHW") < 0 ? "JHW\\" + emrParm.getValue("FILE_PATH") : emrParm.getValue("FILE_PATH");
            word.onOpen(filePath, emrParm.getValue("FILE_NAME"), 3, false);
            word.setNodeIndex(-1);
            saveWord();
            // ============xueyf modify 20120223 stop
            emrParm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            word.setWordParameter(emrParm);
            word.setCanEdit(true);
        }

        // == 20171123 add by zhanglei 增加普华模版信息传入start
        if(TConfig.getSystemValue("HRM.SignIn").equals("普华")){
        	return;
        }
        // == 20171123 add by zhanglei 增加普华模版信息传入end
        
        // 新增一个病案号
        word.setMicroField("MR_NO", mrNo);
        word.setMicroField("MR_NO1", validMrNo(mrNo));
        word.setMicroField("PAT_NAME", patParm.getValue("PAT_NAME", 0));
        word.setMicroField("TEL_HOME", patParm.getValue("TEL", 0));
        word.setMicroField("SEX", getDictionary("SYS_SEX", patParm.getValue("SEX_CODE", 0)));
        word.setMicroField("AGE", StringUtil.showAge(patParm.getTimestamp("BIRTHDAY", 0), TJDODBTool.getInstance().getDBTime()));
        word.setMicroField("MARRY", getDictionary("SYS_MARRIAGE", patParm.getValue("MARRIAGE_CODE", 0)));
        word.setMicroField("CHECK_DATE", StringTool.getString(patParm.getTimestamp("START_DATE", 0), "yyyy/MM/dd"));
        // add by wanglong 20130813
        word.setMicroField("COMPANY_DESC", patParm.getValue("COMPANY_DESC", 0));//团体
        word.setMicroField("CONTRACT_DESC", patParm.getValue("CONTRACT_DESC", 0));//合同
        word.setMicroField("SEQ_NO", patParm.getValue("SEQ_NO", 0));//序号
        word.setMicroField("PAT_DEPT", patParm.getValue("PAT_DEPT", 0));//部门
        word.setMicroField("STAFF_NO", patParm.getValue("STAFF_NO", 0));//工号
        // add end
		// == 20171123 add by zhanglei 增加普华模版信息传入start
//        TParm patParm2 = new TParm();//得到SYS_PATINFO的用户数据
//        patParm2 = this.queryPat(mrNo);
//        word.setMicroField("BIRTH_DATE", patParm.getValue("BIRTHDAY", 0).substring(0, 10).replace('-', '/'));//出生日期
//        word.setMicroField("IDNO", patParm.getValue("ID_NO", 0));//身份证号
//        word.setMicroField("TEL", patParm.getValue("TEL", 0));//电话
//        word.setMicroField("ADDRESS", patParm.getValue("ADDRESS", 0));//通讯地址
//        String nationCode = patParm2.getValue("NATION_CODE", 0);//得到国籍
//        String nationDesc = StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", "GROUP_ID = 'SYS_NATION' AND ID = '" + nationCode + "'");
//        word.setMicroField("NATION_DESC", nationDesc);//国籍    
//        String speciesCode = patParm2.getValue("SPECIES_CODE", 0);//得到民族
//        String speciesDesc = StringUtil.getDesc("SYS_DICTIONARY", "CHN_DESC", "GROUP_ID = 'SYS_SPECIES' AND ID = '" + speciesCode + "'");
//        word.setMicroField("SPECIES_DESC", speciesDesc);//民族  
//        word.setMicroField("E_MAIL", patParm2.getValue("E_MAIL", 0));//电子邮件 E_MAIL
        // == 20171123 add zhanglei end    
        word.setMicroField("CASE_NO", caseNo);
        word.setMicroField("OPERATOR", Operator.getName());
        word.setMicroField("CURRENTDATE", StringTool.getString(TJDODBTool.getInstance().getDBTime(), "yyyy/MM/dd"));
    }
    /**
     * 查询病患基本资料
     * SYS_PATINFO表
     * 20171124 zhanglei
     */
    public TParm queryPat(String mrNo){
    	String sql = "SELECT ADDRESS,NATION_CODE,SPECIES_CODE,E_MAIL FROM SYS_PATINFO WHERE MR_NO = '" + mrNo + "'";
    	System.out.println("666666666sql:" + sql);
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		return parm;
    }
    /**
     * 病案号去零
     */
    public String  validMrNo(String mrNo){
    	int len = mrNo.length();
    	int index = 0;
    	char strs[] = mrNo.toCharArray();
    	for(int i=0; i<len; i++){
    		if('0'!=strs[i]){
	    		index=i;// 找到非零字符串并跳出
	    		break;
    		}
    	}
    	String strLast = mrNo.substring(index, len);// 截取字符串
    	return strLast;
    }
    /**
     * 设置宏
     */
    public void setMicroField(TParm microParm) {
        Map map = this.getDBTool().select("SELECT A.PAT_NAME AS 姓名,A.IDNO AS 身份证号,TO_CHAR(A.BIRTH_DATE,'YYYY-MM-DD') AS 出生日期,"+
                                          " D.CTZ_DESC AS 付款方式,A.TEL_HOME AS 家庭电话,A.SEX_CODE AS 性别,A.CONTACTS_NAME AS 联系人,"+
                                          " TO_CHAR(A.FIRST_ADM_DATE,'YYYY-MM-DD') AS 初诊日期,A.CELL_PHONE AS 手机,"+
                                          " A.ADDRESS AS 家庭住址,A.OCC_CODE AS 职业,A.COMPANY_DESC AS 工作单位,A.TEL_COMPANY AS 单位电话,"+
                                          " A.RELATION_CODE AS 与患者关系,A.CONTACTS_TEL AS 联系人电话,A.CONTACTS_ADDRESS AS 联系人地址,"+
                                          " A.SPECIES_CODE AS 民族,A.HEIGHT AS 身高,A.WEIGHT AS 体重,A.ADDRESS AS 户籍地址,"+
                                          " A.RESID_POST_CODE AS 户籍邮编,"+
                                          " A.POST_CODE AS 邮编,A.MARRIAGE_CODE AS 婚姻状况,"+
                                          " A.RESID_ADDRESS AS 出生地,A.NATION_CODE AS 国籍 "+
                                          " FROM SYS_PATINFO A,SYS_CTZ D"+
                                          " WHERE A.CTZ1_CODE=D.CTZ_CODE(+)"+
                                          " AND A.MR_NO='"+microParm.getValue("MR_NO")+"'");
        TParm parm = new TParm(map);
        if(parm.getErrCode()<0){
            //取得病人基本资料失败
            this.messageBox("E0110");
            return;
        }
        // System.out.println("parm"+parm.getData("出生日期",0).getClass());
        // System.out.println("parm.getTimestamp(,0)"+parm.getValue("出生日期",0));
        Timestamp tempBirth =
                parm.getValue("出生日期", 0).length() == 0 ? SystemTool.getInstance().getDate()
                        : StringTool.getTimestamp(parm.getValue("出生日期", 0), "yyyy-MM-dd");
        // System.out.println("tempBirth"+tempBirth);
        // System.out.println("this.getAdmDate()"+this.getAdmDate());
        // 计算年龄
        String age = "0";
        if (microParm.getTimestamp("ADM_DATE") != null) age =
                OdiUtil.getInstance().showAge(tempBirth, microParm.getTimestamp("ADM_DATE"));
        else age = "";
        String dateStr = StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd HH:mm:ss");
        parm.addData("年龄",age);
        parm.addData("就诊号",microParm.getValue("CASE_NO"));
        parm.addData("病案号",microParm.getValue("MR_NO"));
        parm.addData("住院号",microParm.getValue("IPD_NO"));
        parm.addData("科室",this.getDeptDesc(Operator.getDept()));
        parm.addData("操作者",Operator.getName());
        parm.addData("申请日期",dateStr);
        parm.addData("日期",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy/MM/dd"));
        parm.addData("时间",StringTool.getString(SystemTool.getInstance().getDate(),"HH:mm:ss"));
        parm.addData("病历时间",dateStr);
        parm.addData("入院时间",StringTool.getString(microParm.getTimestamp("ADM_DATE"),"yyyy/MM/dd HH:mm:ss"));
        parm.addData("调用科室",this.getDeptDesc(Operator.getDept()));
        parm.addData("SYSTEM","COLUMNS","年龄");
        parm.addData("SYSTEM","COLUMNS","就诊号");
        parm.addData("SYSTEM","COLUMNS","病案号");
        parm.addData("SYSTEM","COLUMNS","住院号");
        parm.addData("SYSTEM","COLUMNS","科室");
        parm.addData("SYSTEM","COLUMNS","操作者");
        parm.addData("SYSTEM","COLUMNS","申请日期");
        parm.addData("SYSTEM","COLUMNS","病历时间");
        parm.addData("SYSTEM","COLUMNS","入院时间");
        parm.addData("SYSTEM","COLUMNS","调用科室");
        
        
        // 查询住院基本信息(床号，住院诊断)
        TParm odiParm = new TParm(this.getDBTool().select("SELECT B.BED_NO_DESC,C.ICD_CHN_DESC FROM ADM_INP A,SYS_BED B,SYS_DIAGNOSIS C "
                                                        + " WHERE A.CASE_NO=B.CASE_NO "
                                                        + " AND A.MR_NO=B.MR_NO "
                                                        + " AND A.IPD_NO=B.IPD_NO "
                                                        + " AND A.BED_NO=B.BED_NO "
                                                        + " AND A.MAINDIAG=C.ICD_CODE(+) "
                                                        + " AND A.CASE_NO='" + microParm.getValue("CASE_NO") + "'"));
        if (odiParm.getCount() > 0) {
            parm.addData("床号", odiParm.getValue("BED_NO_DESC", 0));
            parm.addData("住院最新诊断", odiParm.getValue("ICD_CHN_DESC", 0));
        } else {
            parm.addData("床号", "");
            parm.addData("住院最新诊断", "");
        }
        if ("HRM".equals("HRM")) {
            parm.addData("门急住别", "健康检查");
            parm.addData("SYSTEM", "COLUMNS", "门急住别");
        }

        //公用信息
        //过敏史(MR_NO)
        StringBuffer drugStr = new StringBuffer();
        TParm drugParm = new TParm(this.getDBTool().select("SELECT A.DRUG_TYPE,A.DRUGORINGRD_CODE,A.ADM_TYPE,"
                                                         + "CASE WHEN A.DRUG_TYPE='A' THEN C.CHN_DESC "
                                                         + "WHEN A.DRUG_TYPE='B' THEN B.ORDER_DESC "
                                                         + "ELSE D.CHN_DESC END ORDER_DESC "
                                                         + " FROM OPD_DRUGALLERGY A,SYS_FEE B, "
                                                         + " (SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='PHA_INGREDIENT') C, "
                                                         + " (SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ALLERGYTYPE') D "
                                                         + " WHERE A.DRUGORINGRD_CODE=B.ORDER_CODE(+) "
                                                         + " AND A.DRUGORINGRD_CODE=C.ID(+) "
                                                         + " AND A.DRUGORINGRD_CODE=D.ID(+) "
                                                         + " AND A.MR_NO='" + microParm.getValue("MR_NO") + "'"));
        if (drugParm.getCount() > 0) {
            int rowCount = drugParm.getCount();
            for (int i = 0; i < rowCount; i++) {
                TParm temp = drugParm.getRow(i);
                drugStr.append(temp.getValue("ORDER_DESC") + ",");
            }
            parm.addData("过敏史", drugStr.toString());
        }else{
            parm.addData("过敏史", "");
        }
        parm.addData("SYSTEM", "COLUMNS", "过敏史");
        //本次看诊门诊主诉现病史(CASE_NO)
        TParm subjParm = new TParm(this.getDBTool().select("SELECT SUBJ_TEXT FROM OPD_SUBJREC WHERE CASE_NO='"+microParm.getValue("CASE_NO")+"'"));
        if(subjParm.getCount()>0){
            parm.addData("主诉现病史", subjParm.getValue("SUBJ_TEXT", 0));
        }else{
            parm.addData("主诉现病史", "");
        }
        parm.addData("SYSTEM", "COLUMNS", "主诉现病史");
        //既往史(MR_NO)
        StringBuffer medhisStr = new StringBuffer();
        TParm medhisParm = new TParm(this.getDBTool().select("SELECT B.ICD_CHN_DESC,A.DESCRIPTION FROM OPD_MEDHISTORY A,SYS_DIAGNOSIS B WHERE A.MR_NO='"+microParm.getValue("MR_NO")+"' AND A.ICD_CODE=B.ICD_CODE"));
        if(medhisParm.getCount()>0){
            int rowCount = medhisParm.getCount();
            for(int i=0;i<rowCount;i++){
                TParm temp = medhisParm.getRow(i);
                medhisStr.append(temp.getValue("ICD_CHN_DESC").length()!=0?temp.getValue("ICD_CHN_DESC"):""+temp.getValue("DESCRIPTION"));
            }
            parm.addData("既往史", medhisStr.toString());
        }else{
            parm.addData("既往史", "");
        }
        parm.addData("SYSTEM", "COLUMNS", "既往史");
        //体征
        TParm sumParm = new TParm(this.getDBTool().select("SELECT TEMPERATURE,PLUSE,RESPIRE,SYSTOLICPRESSURE,DIASTOLICPRESSURE FROM SUM_VTSNTPRDTL WHERE CASE_NO='"+microParm.getValue("CASE_NO")+"' ORDER BY EXAMINE_DATE||EXAMINESESSION DESC"));
        if(sumParm.getCount()>0){
            parm.addData("体温", sumParm.getValue("TEMPERATURE", 0));
            parm.addData("脉搏", sumParm.getValue("PLUSE", 0));
            parm.addData("呼吸", sumParm.getValue("RESPIRE", 0));
            parm.addData("收缩压", sumParm.getValue("SYSTOLICPRESSURE", 0));
            parm.addData("舒张压", sumParm.getValue("DIASTOLICPRESSURE", 0));
        }else{
            parm.addData("体温", "");
            parm.addData("脉搏", "");
            parm.addData("呼吸", "");
            parm.addData("收缩压", "");
            parm.addData("舒张压", "");
        }
        parm.addData("SYSTEM","COLUMNS","体温");
        parm.addData("SYSTEM","COLUMNS","脉搏");
        parm.addData("SYSTEM","COLUMNS","呼吸");
        parm.addData("SYSTEM","COLUMNS","收缩压");
        parm.addData("SYSTEM","COLUMNS","舒张压");
        String names[] = parm.getNames();
        for (String temp : names) {
            // System.out.println(temp+":"+parm.getValue(temp,0));
            if ("性别".equals(temp)) {
                if (parm.getInt(temp, 0) == 9) {
                    this.word.setSexControl(0);
                } else {
                    this.word.setSexControl(parm.getInt(temp, 0));
                }
                this.word.setMicroField(temp, getDictionary("SYS_SEX", parm.getValue(temp,0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_SEX", parm.getValue(temp,0)));
                continue;
            }
            if ("婚姻状况".equals(temp)) {
                this.word.setMicroField(temp, getDictionary("SYS_MARRIAGE", parm.getValue(temp,0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_MARRIAGE", parm.getValue(temp,0)));
                continue;
            }
            if ("国籍".equals(temp)) {
                this.word.setMicroField(temp, getDictionary("SYS_NATION", parm.getValue(temp,0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_NATION", parm.getValue(temp,0)));
                continue;
            }
            if ("民族".equals(temp)) {
                this.word.setMicroField(temp, getDictionary("SYS_SPECIES", parm.getValue(temp,0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_SPECIES", parm.getValue(temp,0)));
                continue;
            }
            if ("与患者关系".equals(temp)) {
                this.word.setMicroField(temp, getDictionary("SYS_RELATIONSHIP", parm.getValue(temp,0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_RELATIONSHIP", parm.getValue(temp,0)));
                continue;
            }
            if ("职业".equals(temp)) {
                this.word.setMicroField(temp, getDictionary("SYS_OCCUPATION", parm.getValue(temp, 0)));
                this.setCaptureValueArray(temp, getDictionary("SYS_OCCUPATION", parm.getValue(temp, 0)));
                continue;
            }
            String tempValue = parm.getValue(temp, 0);
            this.word.setMicroField(temp, tempValue);
            this.setCaptureValueArray(temp, tempValue);
        }
    }

    /**
     * 设置抓取框
     * 
     * @param name String
     * @param value String
     */
    public void setCaptureValueArray(String name, String value) {
        ECapture ecap = this.word.findCapture(name);
        if (ecap == null) return;
        ecap.setFocusLast();
        ecap.clear();
        this.word.pasteString(value);
    }

    /**
     * 拿到科室
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode){
        TParm parm = new TParm(this.getDBTool().select("SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"+deptCode+"'"));
        return parm.getValue("DEPT_CHN_DESC",0);
    }

    /**
     * 拿到字典信息
     * @param groupId String
     * @param id String
     * @return String
     */
    public String getDictionary(String groupId,String id){
        String result="";
        TParm parm = new TParm(this.getDBTool().select("SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"+groupId+"' AND ID='"+id+"'"));
        result = parm.getValue("CHN_DESC",0);
        return result;
    }

    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * 删除Table原有数据
     * @param table ETable
     */
    public void removeTableData(ETable table) {
        ETable t = table.getNextTable();
        while (t != null) {
            t.removeThis();
            t = table.getNextTable();
        }
        while (table.size() > 1) table.remove(table.get(table.size() - 1));
        table.setModify(true);
        table.update();
    }
    
	/**
	 * 插入LIS_TABLE\RIS_TABLE的数据
	 * @return
	 */
	private boolean insertTableData(String caseNo,String mrNo){
        MedApply medApply = new MedApply();
        TParm lisParm = medApply.getLisParm(caseNo);
        if (lisParm == null || lisParm.getErrCode() != 0) {
            this.messageBox("取得检验结果失败");
            return false;
        }
        
        int count = lisParm.getCount();
        TParm risParm = medApply.getRisParm(caseNo);
        if (risParm == null || risParm.getErrCode() != 0) {
            this.messageBox("取得检查结果失败");
            return false;
        }
        
        // modify by wangb 2016/6/14 总检报告检验检查结果样式(用于区分泰心与普华)
		String reportStyle = TConfig
				.getSystemValue("HRM.LIS&RIS.REPORT_STYLE");
		// 目前泰心使用新样式，普华沿用老样式
		if ("NEW".equalsIgnoreCase(reportStyle)) {
			// 取得LIS合码医嘱
	        TParm lisMergeOrder = medApply.getLisMergeOrder(caseNo);
	        if (lisMergeOrder.getErrCode() < 0) {
	        	this.messageBox("取得检验合码医嘱失败");
	            return false;
	        }
	        Map<String, String> lisMergeMap = new HashMap<String,String>();
	        for (int i = 0; i < lisMergeOrder.getCount(); i++) {
	        	lisMergeMap.put(lisMergeOrder.getValue("APPLICATION_NO", i), lisMergeOrder.getValue("ORDER_DESC", i));
	        }
	        
	        ETR tr = null;
	        ETD td = null;
	        ETable table = (ETable) word.findObject("LIS_TABLE", EComponent.TABLE_TYPE);
	        if (table != null) {
	        	int index = 0;
	            // 上下限
	            String limitRange = "";
	            // 医嘱名称
	            String orderDesc = "";
	            // 医嘱名称list
	        	List<String> orderDescList = new ArrayList<String>();
	        	// 合码去重list
	        	List filterList = new ArrayList();
	            String filterKey = "";
	            // 删除Table原有数据
	            removeTableData(table);
	           
	            for (int i = 0; i < count; i++) {
	            	// 合码结果去重
					filterKey = lisParm.getValue("APPLICATION_NO", i)
							+ lisParm.getValue("RPDTL_SEQ", i)
							+ lisParm.getValue("TESTITEM_CODE", i);
					if (!filterList.contains(filterKey)) {
						filterList.add(filterKey);
					} else {
						continue;
					}
	            	
	            	orderDesc = lisParm.getValue("ORDER_DESC", i);
	            	// 医嘱主项占用一行，细项结果按左右结构在下方显示
	            	if (!orderDescList.contains(orderDesc)) {
	            		orderDescList.add(orderDesc);
	            		tr = table.appendTR();
	            		if (i == 0) {
	            			tr.setShowBorder(1, true);
	            		}
	            		// 医嘱名称
	                    td = tr.get(0);
	                    td.setString(lisMergeMap.get(lisParm.getValue("APPLICATION_NO", i)));
	                    // 医嘱主项字体加粗
	                    ((EText) td.get(0).get(0)).modifyBold(true);
	                    
	                    // 医嘱主项上方划分割线
	                    tr.setShowBorder(1, true);
	                    
	                    // 合并单元格
	                    table.uniteTD(table.getRow(tr), 0, table.getRow(tr), 7);
	                    
	                    tr = table.appendTR();
	                    index = 0;
	            	} else {
	            		if (StringUtils.isNotEmpty(tr.get(4).getString())) {
	            			tr = table.appendTR();
	            			index = 0;
	            		} else {
	            			index = 4;
	            		}
	            	}
	            	
	            	// 检验项目
	                td = tr.get(0 + index);
	                td.setString(lisParm.getValue("TESTITEM_CHN_DESC", i));
	                
	                // 检验单位
	                td = tr.get(2 + index);
	                td.setString(lisParm.getValue("TEST_UNIT", i));
	                
	                // 上下限值
	                td = tr.get(3 + index);
	                if (StringUtils.isNotEmpty(lisParm.getValue("UPPE_LIMIT", i))
	                		&& StringUtils.isNotEmpty(lisParm.getValue("LOWER_LIMIT", i))) {
	                	limitRange = lisParm.getValue("LOWER_LIMIT", i) + "~" + lisParm.getValue("UPPE_LIMIT", i);
	                } else if (StringUtils.isNotEmpty(lisParm.getValue("LOWER_LIMIT", i))) {
	                	limitRange = lisParm.getValue("LOWER_LIMIT", i);
	                } else {
	                	limitRange = lisParm.getValue("UPPE_LIMIT", i);
	                }
	                td.setString(limitRange);
	                
	                // 检验值
	                td = tr.get(1 + index);
	                String testValue = lisParm.getValue("TEST_VALUE", i);
	                String upperLimit = lisParm.getValue("UPPE_LIMIT", i);
	                String lowerLimit = lisParm.getValue("LOWER_LIMIT", i);
	                // add by wangb 迪安外送检验回传结果中带有异常判断，以厂商的异常判断为准
	                if ("H".equals(lisParm.getValue("REMARK", i)) ||
	                		"L".equals(lisParm.getValue("REMARK", i))) {
	                	if ("H".equals(lisParm.getValue("REMARK", i))) {
	                		td.setString(testValue + "↑");
	                	} else if ("L".equals(lisParm.getValue("REMARK", i))) {
	                		td.setString(testValue + "↓");
	                	}
        				// 检验项目名称较长有换行现象，换行的标红需要这种方式将每一block块均改为红色
        				int blockSize = td.getTR().get(0 + index).get(0).getBlockSize();
						for (int j = 0; j < blockSize; j++) {
							((EText) tr.get(0 + index).get(0).get(j))
									.modifyColor(new Color(255, 0, 0));
						}
						((EText) tr.get(1 + index).get(0).get(0))
								.modifyColor(new Color(255, 0, 0));
						((EText) tr.get(2 + index).get(0).get(0))
								.modifyColor(new Color(255, 0, 0));
						((EText) tr.get(3 + index).get(0).get(0))
								.modifyColor(new Color(255, 0, 0));
	                } else {
		                // 根据检验结果及范围设定显示样式
		                this.showLisResult(testValue, upperLimit, lowerLimit, tr, td, index);
	                }
	            }
	            
	            // 修改字体大小，除抬头外统一调整为9号
	            for (int i = 1 ; i < table.getTableRowCount(); i++) {
	            	for (int j = 0; j < table.getColumnCount(); j++) {
	            		((EText) table.getTR(i).get(j).get(0).get(0)).modifyFontSize(9);
	            	}
	            }
	            
	            table.update();
	        }
	        table = (ETable) word.findObject("RIS_TABLE", EComponent.TABLE_TYPE);
	        if (table != null) {
	        	// 医嘱细分类(ECC_电生理，ULT_超声，RIS_放射，PET_核医学)
	        	String orderCat1Code = "";
	        	String orderCat1Desc = "";
	        	// 医嘱分类list
	        	List<String> orderCat1List = new ArrayList<String>();
	            count = risParm.getCount();
	            // 删除Table原有数据
	            removeTableData(table);
	            
	            for (int i = 0; i < count; i++) {
	            	orderCat1Desc = "";
	            	orderCat1Code = risParm.getValue("ORDER_CAT1_CODE", i);
	            	// add by wangb 2017/6/1 C13、C14原本细分类不同，但需要归为同类
	            	if ("C13,C14".contains(orderCat1Code)) {
	            		orderCat1Code = "C";
	            	}
	            	// 医嘱分类占用一行
	            	if (!orderCat1List.contains(orderCat1Code)) {
	            		orderCat1List.add(orderCat1Code);
	            		tr = table.appendTR();
	            		if (i == 0) {
	            			tr.setShowBorder(1, true);
	            		}
	            		
	            		if ("ECC".equals(orderCat1Code)) {
	            			orderCat1Desc = "电生理";
	            		} else if ("ULT".equals(orderCat1Code)) {
	            			orderCat1Desc = "超声";
	            		} else if ("RIS".equals(orderCat1Code)) {
	            			orderCat1Desc = "放射";
	            		} else if ("PET".equals(orderCat1Code)) {
	            			orderCat1Desc = "核医学";
	            		} else if ("C".equals(orderCat1Code)) {
	            			orderCat1Desc = "胃幽门螺旋杆菌检测呼气实验";
	            		}
	            		
	            		// 医嘱分类
	                    td = tr.get(0);
	                    td.setString(orderCat1Desc);
	                    // 医嘱主项字体加粗
	                    ((EText) td.get(0).get(0)).modifyBold(true);
	                    
	                    // 医嘱主项上方划分割线
	                    tr.setShowBorder(1, true);
	                    
	                    // 合并单元格
	                    table.uniteTD(table.getRow(tr), 0, table.getRow(tr), 1);
	                    
	            	}
	            	
	            	tr = table.appendTR();
	                // 医嘱名称
	                td = tr.get(0);
	                td.setString(risParm.getValue("ORDER_DESC", i));
	                
	                // modify by wangb 2017/12/14
	                // PACS系统升级完成后，印象与结论已经分开传送，可以带入报告中
	                // 但是目前只有心电和PET结论和所见对应HIS的字段正确存放，故其他项目取所见作为结论显示
					td = tr.get(1);
					if ("ECC".equals(risParm.getValue("ORDER_CAT1_CODE", i))
							|| "PET".equals(risParm.getValue("ORDER_CAT1_CODE",
									i))) {
						td.setString(risParm.getValue("OUTCOME_CONCLUSION", i));
					} else {
						// 因当前接口搞混印象与结论，故取表中OUTCOME_DESCRIBE字段作为检查结果的所见/结论显示
						td.setString(risParm.getValue("OUTCOME_DESCRIBE", i));
					}
	            }
	            table.update();
	        }
		} else {
			ETable table = (ETable) word.findObject("LIS_TABLE", EComponent.TABLE_TYPE);
	        if (table != null) {
	            // 删除Table原有数据
	            removeTableData(table);
	            // 相同的ORDERDESC 只显示一行
	            String orderDescOld = "";
	            orderDescOld = lisParm.getValue("ORDER_DESC", 0);
	            String orderDesc = "";
	            for (int i = 0; i < count; i++) {
	                ETR tr = table.appendTR();
	                // System.out.println("tr"+tr);
	                // System.out.println("tr"+tr.size());
	                // 医嘱名称
	                ETD td = tr.get(0);
	                orderDesc = lisParm.getValue("ORDER_DESC", i);
	                if (i == 0) {
	                    td.setString(orderDesc);
	                } else {
	                    if (!orderDesc.equals(orderDescOld)) {
	                        orderDescOld = orderDesc;
	                        td.setString(orderDesc);
	                    }
	                }
	                // 检验项目
	                td = tr.get(1);
	                td.setString(lisParm.getValue("TESTITEM_CHN_DESC", i));
	                // 检验值
	                td = tr.get(2);
	                String testValue = lisParm.getValue("TEST_VALUE", i);
	                String uppeLimit = lisParm.getValue("UPPE_LIMIT", i);
	                String lowerLimit = lisParm.getValue("LOWER_LIMIT", i);
	                if (isNumber(testValue) && isNumber(uppeLimit) && isNumber(lowerLimit)) {
	                    Double test = Double.parseDouble(testValue);
	                    Double uppe = Double.parseDouble(uppeLimit);
	                    Double lower = Double.parseDouble(lowerLimit);
	                    if (test > uppe) {
	                        td.setString(lisParm.getValue("TEST_VALUE", i) + "↑");
	                        ((EText) td.get(0).get(0)).modifyColor(new Color(255, 0, 0));
	                    }
	                    if (test < lower) {
	                        td.setString(lisParm.getValue("TEST_VALUE", i) + "↓");
	                        ((EText) td.get(0).get(0)).modifyColor(new Color(255, 0, 0));
	                    }
	                    if (test < uppe && test > lower) {
	                        td.setString(lisParm.getValue("TEST_VALUE", i));
	                    }
	                } else {
	                    td.setString(lisParm.getValue("TEST_VALUE", i));
	                }
	                // 检验单位
	                td = tr.get(3);
	                td.setString(lisParm.getValue("TEST_UNIT", i));
	                // 上限值
	                td = tr.get(4);
	                // System.out.println("td"+td);
	                // System.out.println("LIS"+lisParm.getValue("UPPE_LIMIT",i));
	                td.setString(lisParm.getValue("UPPE_LIMIT", i));
	                // 下限值
	                td = tr.get(5);
	                td.setString(lisParm.getValue("LOWER_LIMIT", i));
	            }
//	            table.setLockEdit(true);//delete by wanglong 20131209
	            table.update();
	        }
	        table = (ETable) word.findObject("RIS_TABLE", EComponent.TABLE_TYPE);
	        if (table != null) {
	            count = risParm.getCount();
	            // 删除Table原有数据
	            removeTableData(table);
	            for (int i = 0; i < count; i++) {
	                ETR tr = table.appendTR();
	                // 医嘱名称
	                ETD td = tr.get(0);
	                td.setString(risParm.getValue("ORDER_DESC", i));
	                // 检验项目
	                td = tr.get(1);
	                String outcomeType = risParm.getValue("OUTCOME_TYPE", i);
	                if ("T".equalsIgnoreCase(outcomeType)) {//modify by wanglong 20140123
	                    td.setString("阴性");
	                } else if ("H".equalsIgnoreCase(outcomeType)) {
	                    td.setString("阳性");
	                }
	                // 检验值
	                td = tr.get(2);
	                td.setString(risParm.getValue("OUTCOME_DESCRIBE", i));
	                // 检验单位
	                td = tr.get(3);
	                td.setString(risParm.getValue("OUTCOME_CONCLUSION", i));
	            }
//	            table.setLockEdit(true);//delete by wanglong 20131209
	            table.update();
	        }
		}
        
        word.update();
        return true;
    }
	
	/**
	 * 根据检验结果及范围设定显示样式
	 */
	private void showLisResult(String testValue, String upperLimit,
			String lowerLimit, ETR tr, ETD td, int index) {
		if (isNumber(testValue) && isNumber(upperLimit) && isNumber(lowerLimit)) {
			Double test = Double.parseDouble(testValue);
			Double uppe = Double.parseDouble(upperLimit);
			Double lower = Double.parseDouble(lowerLimit);
			if (test > uppe) {
				td.setString(testValue + "↑");
				// 检验值文字标红并加箭头符号
				((EText) td.get(0).get(0)).modifyColor(new Color(255, 0, 0));
			}
			if (test < lower) {
				td.setString(testValue + "↓");
				((EText) td.get(0).get(0)).modifyColor(new Color(255, 0, 0));
			}
			if (test <= uppe && test >= lower) {
				td.setString(testValue);
			} else {
				// 检验项目名称较长有换行现象，换行的标红需要这种方式将每一block块均改为红色
				int blockSize = td.getTR().get(0 + index).get(0).getBlockSize();
				for (int i = 0; i < blockSize; i++) {
					((EText) tr.get(0 + index).get(0).get(i))
							.modifyColor(new Color(255, 0, 0));
				}
				((EText) tr.get(2 + index).get(0).get(0))
						.modifyColor(new Color(255, 0, 0));
				((EText) tr.get(3 + index).get(0).get(0))
						.modifyColor(new Color(255, 0, 0));
			}
		} else if("阳性".equals(testValue) || "异常".equals(testValue)||(testValue != null && testValue.contains("阳"))){
			td.setString(testValue);//这种情况只能 显示为红色，箭头无法确认
			((EText) td.get(0).get(0)).modifyColor(new Color(255, 0, 0));
			// 检验项目名称较长有换行现象，换行的标红需要这种方式将每一block块均改为红色
			int blockSize = td.getTR().get(0 + index).get(0).getBlockSize();
			for (int i = 0; i < blockSize; i++) {
				((EText) tr.get(0 + index).get(0).get(i))
						.modifyColor(new Color(255, 0, 0));
			}
			((EText) tr.get(2 + index).get(0).get(0))
					.modifyColor(new Color(255, 0, 0));
			((EText) tr.get(3 + index).get(0).get(0))
					.modifyColor(new Color(255, 0, 0));
		}else if(isNumber(testValue) &&  !isNumber(lowerLimit)){//结果值为数值，下限值 不为数值(当下限制不为数值时，上限制是为空字符串，所以不考虑上限字段是否为数值)
			td.setString(testValue);
			Double test = Double.parseDouble(testValue);
			if(lowerLimit.contains("<")){
				
				String lowerValue = lowerLimit.substring(lowerLimit.indexOf("<")+1,lowerLimit.length());
				if(isNumber(lowerValue)){//再次确认截取的字串是否为数值
					Double lower = Double.parseDouble(lowerValue);
					if(test >= lower){//表示异常值
						td.setString(testValue + "↑");
						((EText) td.get(0).get(0)).modifyColor(new Color(255, 0, 0));
						// 检验项目名称较长有换行现象，换行的标红需要这种方式将每一block块均改为红色
						int blockSize = td.getTR().get(0 + index).get(0).getBlockSize();
						for (int i = 0; i < blockSize; i++) {
							((EText) tr.get(0 + index).get(0).get(i))
									.modifyColor(new Color(255, 0, 0));
						}
						((EText) tr.get(2 + index).get(0).get(0))
								.modifyColor(new Color(255, 0, 0));
						((EText) tr.get(3 + index).get(0).get(0))
								.modifyColor(new Color(255, 0, 0));
					}
				}
			}else if(lowerLimit.contains(">")){
				String lowerValue = lowerLimit.substring(lowerLimit.indexOf(">")+1,lowerLimit.length());
				if(isNumber(lowerValue)){//再次确认截取的字串是否为数值
					Double lower = Double.parseDouble(lowerValue);
					if(test < lower){//表示异常值
						td.setString(testValue + "↓");
						((EText) td.get(0).get(0)).modifyColor(new Color(255, 0, 0));
						// 检验项目名称较长有换行现象，换行的标红需要这种方式将每一block块均改为红色
						int blockSize = td.getTR().get(0 + index).get(0).getBlockSize();
						for (int i = 0; i < blockSize; i++) {
							((EText) tr.get(0 + index).get(0).get(i))
									.modifyColor(new Color(255, 0, 0));
						}
						((EText) tr.get(2 + index).get(0).get(0))
								.modifyColor(new Color(255, 0, 0));
						((EText) tr.get(3 + index).get(0).get(0))
								.modifyColor(new Color(255, 0, 0));
					}
				}
			}else{
				td.setString(testValue);
			}
			
		}else {
			td.setString(testValue);
		}
	}

    /**
     * 是否是数字
     * 
     * @return boolean
     */
    public boolean isNumber(String str) {
        if (str == null || str.length() == 0) {
            return false;
        }
        Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * 右键事件
     */
    public void showPopMenu() {
        TParm action = orderTable.getParmValue().getRow(orderTable.getSelectedRow());
        if (("LIS".equals(action.getValue("CAT1_TYPE")) && "Y".equals(action.getValue("DONE")))
         || ("RIS".equals(action.getValue("CAT1_TYPE")) && "Y".equals(action.getValue("DONE")))) {
            orderTable.setPopupMenuSyntax("查看报告,showRept");
            return;
        } else {
            orderTable.setPopupMenuSyntax("");
            return;
        }
    }

    /**
     * 查看报告
     */
    public void showRept() {
        TParm action = orderTable.getParmValue().getRow(orderTable.getSelectedRow());
        // LIS报告
        if ("LIS".equals(action.getValue("CAT1_TYPE"))) {
            String labNo = action.getValue("MED_APPLY_NO");
            if (labNo.length() == 0) {
                this.messageBox("E0188");
                return;
            }
            SystemTool.getInstance().OpenLisWeb(action.getValue("MR_NO"), null, "", "", "", "");
        }
        // RIS报告
        if ("RIS".equals(action.getValue("CAT1_TYPE"))) {
            SystemTool.getInstance().OpenRisWeb(action.getValue("MR_NO"));
        }
    }

    /**
     * 根据给入的地址，循环插入总检报告
     * @param add
     */
    private void insertWord(TParm add) {
        if (add == null) {
            this.messageBox("取得文件失败");
            return;
        }
        int count = add.getCount("FILE_PATH");
        if (count <= 0) {
            this.messageBox("没有新文件需要带入！");
            return;
        }
        // this.messageBox("-count--"+count);
        for (int i = 0; i < count; i++) {
            word.onInsertFileFrontFixed("INSERT0", add.getValue("FILE_PATH", i), add.getValue("FILE_NAME", i), 3, false);
            word.update();
        }
        if (add.getValue("REMARK", "FILE").length() != 0) {
            word.setFileRemark(add.getValue("REMARK", "FILE"));
        }
    }
    
	/**
	 * 根据CASE_NO取得其他结构化病例，并取得其地址
	 * @param caseNo
	 * @return
	 */
    private TParm getEmrAddress(String caseNo) {
        TParm result = new TParm();
        // ============xueyf modify 20120224 start
        // String root = TConfig.getSystemValue("FileServer.Main.Root") + "\\" + TConfig.getSystemValue("EmrData") + "\\JHW\\";
        String root = "\\JHW\\";
        // ============xueyf modify 20120224 stop
        String sql =
                "SELECT A.FILE_PATH, A.FILE_NAME, B.DEPT_ATTRIBUTE "
                        + " FROM EMR_FILE_INDEX A, HRM_ORDER B, HRM_DEPT_ATTRIBUTE F "
                        + "	WHERE A.CASE_NO = '" + caseNo + "' AND A.CASE_NO = B.CASE_NO "
                        + "   AND A.FILE_SEQ = B.FILE_NO " + " AND B.SETMAIN_FLG = 'Y' "
                        + "  AND B.DEPT_ATTRIBUTE = F.DEPT_ATTRIBUTE(+) "
                        + "   AND B.DEPT_ATTRIBUTE <> '04' " + " ORDER BY F.SEQ";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("getEmrAddress.sql=="+sql);
        if (result.getErrCode() != 0) {
            // this.messageBox(result.getErrText());
            this.messageBox("没有问诊资料");
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            this.messageBox("没有问诊资料");
            return null;
        }
        StringBuffer deptAttribute = new StringBuffer();
        for (int i = 0; i < count; i++) {
            String wholePath = root + result.getValue("FILE_PATH", i);
            result.setData("FILE_PATH", i, wholePath);
            result.setData("FILE_NAME", i, result.getValue("FILE_NAME", i) + ".jhw");
            deptAttribute.append(result.getValue("DEPT_ATTRIBUTE", i));
            deptAttribute.append("|");
        }
        if (deptAttribute.toString().length() != 0) result.setData("REMARK", "FILE", deptAttribute.toString());
        else result.setData("REMARK", "FILE", "");
        insertWord(result);
        return result;
    }
	
	/**
	 * 部分抓取合并功能
	 * @param caseNo
	 * @param deptAttribute
	 * @param orders
	 * @return
	 */
    private TParm onMerge(String caseNo, String deptAttribute, List<TParm> orders) {
        TParm result = new TParm();
        // 构造医嘱的名子
        String strInSQL = "";
        // caseNo_code + file_no
        for (TParm parm : orders) {
            strInSQL += "'" + parm.getValue("FILE_NO") + "',";
        }
        if (strInSQL != null && strInSQL.length() > 0) {
            strInSQL = strInSQL.substring(0, strInSQL.length() - 1);
        }
        // 合并选中的医嘱文件
        // String root = TConfig.getSystemValue("FileServer.Main.Root") + "\\" + TConfig.getSystemValue("EmrData") + "\\JHW\\";
        String root = "\\JHW\\";
        String sql =
                "SELECT A.FILE_PATH, A.FILE_NAME, B.DEPT_ATTRIBUTE "
                        + " FROM EMR_FILE_INDEX A, HRM_ORDER B, HRM_DEPT_ATTRIBUTE F "
                        + "	WHERE A.CASE_NO = '" + caseNo + "' AND A.CASE_NO = B.CASE_NO "
                        + " AND A.FILE_SEQ = B.FILE_NO " + " AND B.SETMAIN_FLG = 'Y' "
                        + " AND B.DEPT_ATTRIBUTE = F.DEPT_ATTRIBUTE(+) "
                        + " AND B.DEPT_ATTRIBUTE <> '" + deptAttribute + "' AND FILE_NO IN ("
                        + strInSQL + ") ORDER BY F.SEQ";
        // System.out.println("===========onMerge SQL============"+sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() != 0) {
            // this.messageBox(result.getErrText());
            this.messageBox("没有问诊资料！");
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            this.messageBox("没有问诊资料！");
            return null;
        }
        String[] remark = this.word.getFileRemark().split("\\|");
        TParm fileList = new TParm();
        StringBuffer deptAttributes = new StringBuffer();
        for (int i = 0; i < count; i++) {
            String deptAttributeTemp = result.getValue("DEPT_ATTRIBUTE", i);
            // 已经存在的是否还加入
            // if (isDeptAttribute(remark, deptAttributeTemp)) {
            //      continue;
            // }
            String wholePath = root + result.getValue("FILE_PATH", i);
            fileList.addData("FILE_PATH", wholePath);
            fileList.addData("FILE_NAME", result.getValue("FILE_NAME", i) + ".jhw");
            deptAttributes.append(this.word.getFileRemark() + "|" + deptAttributeTemp);
            deptAttributes.append("|");
        }
        if (deptAttributes.toString().length() != 0) fileList.setData("REMARK", "FILE", deptAttributes.toString());
        else fileList.setData("REMARK", "FILE", "");
        insertWord(fileList);
        return result;
    }

    /**
     * 根据CASE_NO取得其他结构化病例，并取得其地址
     * 
     * @param caseNo
     * @return
     */
    private TParm getEmrAddress(String caseNo, String deptAttribute) {
        TParm result = new TParm();
        // ============xueyf modify 20120224 start
        // String root = TConfig.getSystemValue("FileServer.Main.Root") + "\\" + TConfig.getSystemValue("EmrData") + "\\JHW\\";
        String root = "\\JHW\\";
        // ============xueyf modify 20120224 stop
        String sql =
                "SELECT A.FILE_PATH, A.FILE_NAME, B.DEPT_ATTRIBUTE "
                        + " FROM EMR_FILE_INDEX A, HRM_ORDER B, HRM_DEPT_ATTRIBUTE F "
                        + "	WHERE A.CASE_NO = '" + caseNo + "' AND A.CASE_NO = B.CASE_NO "
                        + " AND A.FILE_SEQ = B.FILE_NO " + " AND B.SETMAIN_FLG = 'Y' "
                        + " AND B.DEPT_ATTRIBUTE = F.DEPT_ATTRIBUTE(+) "
                        + " AND B.DEPT_ATTRIBUTE <> '" + deptAttribute + "' ORDER BY F.SEQ";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        // System.out.println("getEmrAddress.sql=="+sql);
        if (result.getErrCode() != 0) {
            this.messageBox("查询问诊资料失败 " +result.getErrText());
            return null;
        }
        int count = result.getCount();
        if (count <= 0) {
            this.messageBox("没有问诊资料！");
            return null;
        }
        String[] remark = this.word.getFileRemark().split("\\|");
        TParm fileList = new TParm();
        StringBuffer deptAttributes = new StringBuffer();
        for (int i = 0; i < count; i++) {
            String deptAttributeTemp = result.getValue("DEPT_ATTRIBUTE", i);
            if (isDeptAttribute(remark, deptAttributeTemp)) {
                continue;
            }
            String wholePath = root + result.getValue("FILE_PATH", i);
            fileList.addData("FILE_PATH", wholePath);
            fileList.addData("FILE_NAME", result.getValue("FILE_NAME", i) + ".jhw");
            deptAttributes.append(this.word.getFileRemark() + "|" + deptAttributeTemp);
            deptAttributes.append("|");
        }
        if (deptAttributes.toString().length() != 0) fileList.setData("REMARK", "FILE", deptAttributes.toString());
        else fileList.setData("REMARK", "FILE", "");
        insertWord(fileList);
        return result;
    }

    /**
     * 检核是否存在
     * 
     * @param deptAtts String[]
     * @param deptAtt String
     * @return boolean
     */
    public boolean isDeptAttribute(String[] deptAtts, String deptAtt) {
        boolean falg = false;
        // System.out.println("deptAtt=="+deptAtt);
        for (String temp : deptAtts) {
            // System.out.println("temp=="+temp);
            if (temp.equals(deptAtt)) {
                falg = true;
                break;
            }
        }
        return falg;
    }

    /**
     * ORDER_TABLE单击事件
     */
    public void onOrder() {
//    	this.setValue("PAT_NAME", "单击");
        int row = orderTable.getSelectedRow();
        
        if (row < 0) {
            return;
        }
        TParm parm = orderTable.getParmValue();
        if (parm.getCount() <= 0) {
            return;
        }
        //this.messageBox("parm" + parm.getRow(row) + "row" + row);
        //列
        int columnIndex = orderTable.getSelectedColumn();
        //this.messageBox("---columnIndex---"+columnIndex);
        if (columnIndex != 1) {
        	//this.messageBox("22222");
            openFile(parm.getRow(row));
        } else {
            String cat = parm.getValue("ORDER_CAT1_CODE", row);
            if (cat != null && !cat.equals("ORD")) {
                this.messageBox("非问诊医嘱不能选择！");
                return;
            }
        }
    }

    /**
     * 保存
     */
    public void onSave() {
        TParm parm = orderTable.getParmValue();
        int row = orderTable.getSelectedRow();
        if (orderTable.getRowCount() <= 0) {
            return;
        }
        if (row < 0) {
            row = orderTable.getRowCount() - 1;
        }
        if (parm == null) {
            return;
        }
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        if (!isSaveble(parm)) {
            if (this.messageBox("提示信息", "该患者没有完成所有检查，是否写入总检报告", this.YES_NO_OPTION) != 0) {
                return;
            }
        }
        // 保存结构化病历
        if (!this.saveEmr(parm.getValue("CASE_NO", row), parm.getValue("MR_NO", row))) {
            this.messageBox("保存病历失败");
            return;
        }
        if (!this.saveData(parm.getValue("CASE_NO", row), fileNo, !StringUtil.isNullString(fileNo))) {
            this.messageBox("保存数据失败");
            return;
        }
        
     // zhanglei 20170907 start 增加初始化默认光标位置
        this.grabFocus("MR_NO");
        allXuan();
        // zhanglei 20170907 end 增加初始化默认光标位置 
    }
    
    /**
     * 全选事件
     * 用于保存后全选病案号使用
     * 20170907
     * zhanglei 
     */
    public void allXuan(){

    	try {
			Robot robot = new Robot();//创建一个robot对象
			keyPressWithCtrl(robot,KeyEvent.VK_A); //按下 ctrl+A 全选
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
    }
    
    /**
     * ctrl+ 按键
     * 20170907
     * zhanglei 
     * @throws AWTException 
     */
    public static void keyPressWithCtrl(Robot r, int key) {

        r.keyPress(KeyEvent.VK_CONTROL);
        r.keyPress(key);
        r.keyRelease(key);
        r.keyRelease(KeyEvent.VK_CONTROL);
    }

    /**
     * ORDER_TABLE中如有未完成的检查，则返回false,否则返回true
     * @param parm
     * @return
     */
    private boolean isSaveble(TParm parm) {
        int count = parm.getCount();
        if (count <= 0) {
            return false;
        }
        for (int i = 0; i < count; i++) {
            boolean isDone = TypeTool.getBoolean(parm.getData("DONE", i));
            if (!isDone) {
                if (!"04".equalsIgnoreCase(parm.getValue("DEPT_ATTRIBUTE", i))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 结构化病例保存方法
     * 
     * @param caseNo
     * @param mrNo
     * @return
     */
    private boolean saveEmr(String caseNo, String mrNo) {
        if (StringUtil.isNullString(caseNo) || StringUtil.isNullString(mrNo)) {
            return false;
        }
        if (!isOld) {
            pathParm.setData("CASE_NO", caseNo);
            pathParm.setData("MR_NO", mrNo);
            TParm parm = EmrUtil.getInstance().getFileServerEmrName(pathParm);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            // ============xueyf modify 20120222 start
            parm.setData("CREATOR_DATE", TJDODBTool.getInstance().getDBTime());
            parm.setData("REPORT_FLG", "N");
            // ============xueyf modify 20120222 stop
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("CREATOR_USER", Operator.getID());
            parm.setData("CURRENT_USER", Operator.getID());
            fileNo = parm.getValue("FILE_SEQ");
            TParm result =
                    TIOM_AppServer.executeAction("action.odi.ODIAction", "saveNewEmrFile", parm);
            if (result.getErrCode() != 0) {
                this.messageBox("保存失败 " + result.getErrText());
                return false;
            }
            // ============xueyf modify 20120223 start
            String filePath =
                    parm.getValue("FILE_PATH").indexOf("JHW") < 0 ? "JHW\\" + parm.getValue("FILE_PATH") : parm.getValue("FILE_PATH");
            word.onSaveAs(filePath, parm.getValue("FILE_NAME"), 3);
            // ============xueyf modify 20120223 stop
        } else {
            pathParm.setData("CASE_NO", caseNo);
            pathParm.setData("MR_NO", mrNo);
            TParm parm = EmrUtil.getInstance().getFileServerEmrName(pathParm);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            parm.setData("OPT_TERM", Operator.getIP());
//            parm.setData("CREATOR_USER", Operator.getID());
            parm.setData("CURRENT_USER", Operator.getID());
            // System.out.println("parm====="+parm);
            TParm result = TIOM_AppServer.executeAction("action.odi.ODIAction", "updateEmrFile", parm);
            if (result.getErrCode() != 0) {
                this.messageBox("新增失败 " + result.getErrText());
                return false;
            }
            word.onSave();
        }
        return true;
    }
	
    /**
     * 保存HRM_ORDER，HRM_PATADM的数据
     * @return
     */
    private boolean saveData(String caseNo, String fileNo, boolean isFirst) {
        TParm orderParm = orderTable.getParmValue();
        int row = orderTable.getSelectedRow();
        if (orderParm == null) {
            return false;
        }
        if (orderTable.getRowCount() <= 0) {
            return false;
        }
        if (row < 0) {
            row = orderTable.getRowCount() - 1;
        }
        if (orderParm.getCount() <= 0) {
            return false;
        }
        order = new HRMOrder();
        order.onQuery();
        order.setFilter("CASE_NO='" + orderParm.getValue("CASE_NO", row) 
                + "' AND ORDERSET_CODE='" + orderParm.getValue("ORDERSET_CODE", row) 
                + "' AND ORDERSET_GROUP_NO=" + orderParm.getInt("ORDERSET_GROUP_NO", row));
        order.filter();
        // System.out.println("after order filter");
        // order.showDebug();
        TParm result =
                order.saveByCheck(Operator.getRegion(), Operator.getDept(), caseNo, "04", fileNo,
                                  isFirst);
        if (result.getErrCode() != 0) {
            this.messageBox(result.getErrText());
            return false;
        }
        order.resetModify();
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        HRMPatAdm patAdm = new HRMPatAdm();
        patAdm.onQueryByCaseNo(caseNo);
        patAdm.setItem(0, "FINAL_JUDGE_DR", Operator.getID());
        patAdm.setItem(0, "FINAL_JUDGE_DATE", now);
        // patAdm.setItem(0, "REPORT_DATE", now);
        patAdm.setItem(0, "END_DATE", now);
        patAdm.setItem(0, "OPT_USER", Operator.getID());
        patAdm.setItem(0, "OPT_DATE", now);
        patAdm.setItem(0, "OPT_TERM", Operator.getIP());
        if (!patAdm.update()) {
            this.messageBox(patAdm.getErrText());
            return false;
        }
        patAdm.resetModify();
        return true;
    }

    /**
     * 打印结构化病历
     */
    public void onPrint() {
        TParm parm = orderTable.getParmValue();
        if (parm == null) {
            return;
        }
        if (parm.getCount() <= 0) {
            return;
        }
        word.onPreviewWord();
        // word.printXDDialog();
        word.printDialog();
        // word.print();
        String isPrintRisReport = TConfig.getSystemValue("isPrintRisReport");
        if("Y".equals(isPrintRisReport)){
        	 printRisReportPDF();//打印检查报告
        }
       
        
    }
    
    /**
     * 打印检查报告
     */
	public void printRisReportPDF() {
		serverPath = "";
		int row = patTable.getSelectedRow();
		TParm patTableParm = patTable.getParmValue();
		String caseNo = patTableParm.getValue("CASE_NO", row);
		String mrNo = patTableParm.getValue("MR_NO", row);
		String tempPath = "C:\\JavaHisFile\\temp\\pdf";
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}

		String pdfPath = "PDF\\" + caseNo.substring(0, 2) + "\\"
				+ caseNo.substring(2, 4) + "\\" + mrNo;
		serverPath = TConfig.getSystemValue("FileServer.Main.Root") + "\\"
				+ TConfig.getSystemValue("EmrData") + "\\" + pdfPath;
		String pdfFileArray[] = TIOM_FileServer.listFile(TIOM_FileServer
				.getSocket(), serverPath);
		if (pdfFileArray == null || pdfFileArray.length < 1) {
			return;
		}

		String medSql = "SELECT * FROM MED_APPLY WHERE CASE_NO ='" + caseNo
				+ "' AND CAT1_TYPE = 'RIS' ";
		TParm medParm = new TParm(TJDODBTool.getInstance().select(medSql));
		int count = pdfFileArray.length;
		String pdfFileName = "";
		byte[] data;
		Runtime runtime;
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < medParm.getCount(); j++) {
				pdfFileName = caseNo + "_检查报告_"
						+ medParm.getValue("MED_APPLY_NO", j);
				if (pdfFileArray[i].contains(pdfFileName)) {
					data = TIOM_FileServer.readFile(
							TIOM_FileServer.getSocket(), serverPath + "\\"
									+ pdfFileArray[i]);
					if (data == null) {
						continue;
					}
					try {
						FileTool.setByte(tempPath + "\\" + pdfFileArray[i],
								data);
						runtime = Runtime.getRuntime();
						runtime.exec("cmd.exe /C start acrord32 /P /h "
								+ tempPath + "\\" + pdfFileArray[i]);
					} catch (Exception ex) {
						System.out.println(ex.getMessage());
					}
					break;
				}
			}
		}
	}
    
    /**
     * ORDER_TABLE双击事件
     * zhanglei add 20171016 双击打开相应的PDF
     */
    public void orderTableDoubleClick(){
//    	this.setValue("PAT_NAME", "双击");
    	
    	serverPath = "";
    	int orderRow = orderTable.getSelectedRow();
    	TParm  orderTableParm = orderTable.getParmValue().getRow(orderRow);
    	//this.messageBox("orderTableParm" + orderTableParm + "  orderRow" + orderRow);
    	//放射科 电生理  碳14医嘱
    	/*if(!"0404".equals(orderTableParm.getValue("DEPT_CODE",orderRow)) && 
    			!"ECC".equals(orderTableParm.getValue("ORDER_CAT1_CODE",orderRow)) && 
    			!"Y0102032".equals(orderTableParm.getValue("ORDER_CODE",orderRow))){
    		return;
    	}*/
//    	System.out.println("11111111111111111" + orderTableParm);
//    	this.messageBox("2" + orderTableParm);
    	if(!"RIS".equals(orderTableParm.getValue("CAT1_TYPE"))){
//    		this.messageBox("1111");
    		return;
    	}
//    	this.messageBox("22222");
    	int patRow = patTable.getSelectedRow();
    	TParm patTableParm = patTable.getParmValue();
        String caseNo = patTableParm.getValue("CASE_NO", patRow);
        String mrNo = patTableParm.getValue("MR_NO", patRow);
        
        String tempPath = "C:\\JavaHisFile\\temp\\pdf";
    	File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
		String pdfPath =
            "PDF\\" + caseNo.substring(0, 2) + "\\" + caseNo.substring(2, 4) + "\\"
                    + mrNo;
//		this.messageBox("pdfPath  " + pdfPath);
		serverPath =
            TConfig.getSystemValue("FileServer.Main.Root") + "\\"
                    + TConfig.getSystemValue("EmrData") + "\\" + pdfPath;
//		this.messageBox("serverPath  " + serverPath);
	    String pdfFile[] = TIOM_FileServer.listFile(TIOM_FileServer.getSocket(), serverPath);
//	    String j = null;
//	    for(int i = 0;i<pdfFile.length;i++){
//	    	this.messageBox(pdfFile[i]);
//	    	j = j + "    " + pdfFile[i];
//	    }
//	    this.messageBox("pdfFile[]  " + j + "pdfFile.length  " + pdfFile.length);
	    if (pdfFile == null || pdfFile.length < 1) {
	        return ;
	    }
	    List pdfList = Arrays.asList(pdfFile);
    	//String type = orderTableParm.getValue("CAT1_TYPE").equalsIgnoreCase("LIS") ? "检验报告" : "检查报告";
    	String type = "检查报告";
        String pdfFileName = caseNo + "_" + type + "_" + orderTableParm.getValue("MED_APPLY_NO") + ".pdf";
//        this.messageBox("pdfFileName  " + pdfFileName);
//        this.messageBox("pdfList.contains(pdfFileName)  " + pdfList.contains(pdfFileName));
        if(pdfList.contains(pdfFileName)){
	        byte data[] =
	            TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), serverPath + "\\"
	                    + pdfFileName);
//	        String y = null;
//		    for(int x = 0;x<pdfFile.length;x++){
//		    	this.messageBox(pdfFile[x]);
//		    	y = y + "    " + pdfFile[x];
//		    }
//	        this.messageBox("data[]  " + y);
		    if (data == null) {
		        this.messageBox_("服务器上没有找到文件 " + serverPath + "\\" + pdfFileName);
		    	return;
		    }
		    try {
//		    	this.messageBox("FileTool.setByte(tempPath + '\\' + pdfFileName, data)");
		        FileTool.setByte(tempPath + "\\" + pdfFileName, data);
		    }
		    catch (Exception e) {
		        e.printStackTrace();
		    }
		    Runtime runtime = Runtime.getRuntime();
		    try {
				runtime.exec("cmd.exe /C start  " + tempPath + "\\"
		                + pdfFileName); 
//				this.messageBox("cmd.exe /C start acrord32 /P /h " + tempPath + "\\"
//		                + pdfFileName);
		    }
		    catch (Exception ex) {
		        ex.printStackTrace();
		    }
        }else{
        	this.messageBox_("服务器上没有找到文件 " + serverPath + "\\" + pdfFileName);
	    	return;
        }
	}
    
//    /**
//     * ORDER_TABLE双击事件
//     * 20170920z之前版本 经宝龙确认此功能不起任何作用
//     */
//    public void orderTableDoubleClick(int row){
//    	serverPath = "";
//    	TParm  orderTableParm = orderTable.getParmValue();
//    	int orderRow = orderTable.getSelectedRow();
//    	//this.messageBox("orderTableParm" + orderTableParm + "  orderRow" + orderRow);
//    	//放射科 电生理  碳14医嘱
//    	/*if(!"0404".equals(orderTableParm.getValue("DEPT_CODE",orderRow)) && 
//    			!"ECC".equals(orderTableParm.getValue("ORDER_CAT1_CODE",orderRow)) && 
//    			!"Y0102032".equals(orderTableParm.getValue("ORDER_CODE",orderRow))){
//    		return;
//    	}*/
//    	if(!"RIS".equals(orderTableParm.getValue("CAT1_TYPE"))){
//    		this.messageBox("1111");
//    		return;
//    	}
//    	this.messageBox("22222");
//    	int patRow = patTable.getSelectedRow();
//    	TParm patTableParm = patTable.getParmValue();
//        String caseNo = patTableParm.getValue("CASE_NO", patRow);
//        String mrNo = patTableParm.getValue("MR_NO", patRow);
//        
//        String tempPath = "C:\\JavaHisFile\\temp\\pdf";
//    	File f = new File(tempPath);
//		if (!f.exists()) {
//			f.mkdirs();
//		}
//		String pdfPath =
//            "PDF\\" + caseNo.substring(0, 2) + "\\" + caseNo.substring(2, 4) + "\\"
//                    + mrNo;
//		serverPath =
//            TConfig.getSystemValue("FileServer.Main.Root") + "\\"
//                    + TConfig.getSystemValue("EmrData") + "\\" + pdfPath;
//	    String pdfFile[] = TIOM_FileServer.listFile(TIOM_FileServer.getSocket(), serverPath);
//	    if (pdfFile == null || pdfFile.length < 1) {
//	        return ;
//	    }
//	    
//	    List pdfList = Arrays.asList(pdfFile);
//    	//String type = orderTableParm.getValue("CAT1_TYPE").equalsIgnoreCase("LIS") ? "检验报告" : "检查报告";
//    	String type = "检查报告";
//        String pdfFileName = caseNo + "_" + type + "_" + orderTableParm.getValue("MED_APPLY_NO",orderRow) + ".pdf";
//        if(pdfList.contains(pdfFileName)){
//	        byte data[] =
//	            TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), serverPath + "\\"
//	                    + pdfFileName);
//		    if (data == null) {
//		        this.messageBox_("服务器上没有找到文件 " + serverPath + "\\" + pdfFileName);
//		    	return;
//		    }
//		    try {
//		        FileTool.setByte(tempPath + "\\" + pdfFileName, data);
//		    }
//		    catch (Exception e) {
//		        e.printStackTrace();
//		    }
//		    Runtime runtime = Runtime.getRuntime();
//		    try {
//				runtime.exec("cmd.exe /C start acrord32 /P /h " + tempPath + "\\"
//		                + pdfFileName); 
//		    }
//		    catch (Exception ex) {
//		        ex.printStackTrace();
//		    }
//        }
//	}
    /**
     * 总检展开
     */
    public void onUnfold() {
        TMovePane mp = (TMovePane) callFunction("UI|MOVE|getThis");
        mp.onDoubleClicked();
        TMovePane mp1 = (TMovePane) callFunction("UI|MP2|getThis");
        mp1.onDoubleClicked();
    }

    /**
     * 临床数据
     */
    public void onInsertLCSJ() {
        int row = patTable.getSelectedRow();
        TParm parm = patTable.getParmValue();
        if (parm == null) {
            return;
        }
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        String caseNo = parm.getValue("CASE_NO", row);
        if (StringUtil.isNullString(caseNo)) {
            this.messageBox("取得医嘱数据失败");
            return;
        }
        TParm inParm = new TParm();
        inParm.setData("CASE_NO", caseNo);
        inParm.addListener("onReturnContent", this, "onReturnContent");
        // this.openWindow("%ROOT%\\config\\emr\\EMRMEDDataUI.x",inParm);
        TWindow window =
                (TWindow) this.openWindow("%ROOT%\\config\\emr\\EMRMEDDataUI.x", inParm, true);
        window.setX(ImageTool.getScreenWidth() - window.getWidth());
        window.setY(0);
        window.setVisible(true);
    }

    /**
     * 片语记录
     * 
     * @param value String
     */
    public void onReturnContent(String value) {
        if (!this.word.pasteString(value)) {
            this.messageBox("E0005");// 执行失败
        }
    }

    /**
     * 片语
     */
    public void onInsertPY() {
        TParm inParm = new TParm();
        inParm.setData("TYPE", "2");
        inParm.setData("ROLE", "2");
        inParm.setData("DR_CODE", Operator.getID());
        inParm.setData("DEPT_CODE", Operator.getDept());
        inParm.addListener("onReturnContent", this, "onReturnContent");
        // this.openWindow("%ROOT%\\config\\emr\\EMRComPhraseQuote.x",inParm);
        window1 =
                (TWindow) this.openWindow("%ROOT%\\config\\emr\\EMRComPhraseQuote.x", inParm, true);
        window1.setX(ImageTool.getScreenWidth() - window1.getWidth());
        window1.setY(0);
        window1.setVisible(true);
    }

    /**
     * 清空系统剪贴板
     */
    public void onClearMenu() {
        CopyOperator.clearComList();
    }

    /**
     * 删除表格
     */
    public void onDelTable() {
        if (this.word.getFileOpenName().length() != 0) {
            int yesNo = this.messageBox("提示信息", "确认删除本焦点下的表格？", this.YES_NO_OPTION);
            if (yesNo == 0) {
                this.word.deleteTable();
            }
        } else {
            this.messageBox("请选择总检报告！");
        }
    }

    /**
     * 检验检查结果传回
     */
    public void onInsertLisRisResult() {// add by wanglong 20131209
        int row = patTable.getSelectedRow();
        TParm parm = patTable.getParmValue();
        if (parm == null) {
            return;
        }
        int count = parm.getCount();
        if (count <= 0) {
            return;
        }
        String caseNo = parm.getValue("CASE_NO", row);
        if (StringUtil.isNullString(caseNo)) {
            this.messageBox("取得医嘱数据失败");
            return;
        }
        if (!insertTableData(caseNo, parm.getValue("MR_NO", row))) {
            return;
        }
    }
    
    /**
     * 删除表格行
     */
    public void onDelTableRow() {//wanglong add 20141216
        if (this.word.getFileOpenName() != null) {
            this.word.deleteTR();
        } else {
            // 请选择病历
            this.messageBox("E0099");
        }
    }
    
    /**
     * 关闭事件
     * 
     * @return boolean
     */
    public boolean onClosing() {// wanglong add 20150316
        if (window1 != null && !window1.isClose()) {
            this.messageBox("请关闭片语窗口");
            return false;
        }
        if (window2 != null && !window2.isClose()) {
            this.messageBox("请关闭子模板窗口");
            return false;
        }
        return true;
    }
    
    /**
     * “子模板”单击事件
     */
    public void onSubTemplate() {//wanglong add 20150317
        if (!word.canEdit()) {
            messageBox("先选择病例模版!");
            return;
        } else {
            openSubTemplate();
        }
    }

    /**
     * 打开子模板选择窗口
     */
    public void openSubTemplate() {//wanglong add 20150317
        TParm parm = new TParm();
        parm.addListener("onReturnTemplateContent", this, "onReturnTemplateContent");
        parm.setData("TWORD", word);
        if (window2 != null&&!window2.isClose()) {
            return;
        }
        window2 = (TWindow) openWindow("%ROOT%\\config\\emr\\EMRSubTemplate.x", parm, true);
        window2.setX(ImageTool.getScreenWidth() - window2.getWidth());
        window2.setY(0);
        window2.setVisible(true);
    }
    
    public void onReturnTemplateContent(TWord templateWord) {//wanglong add 20150317
        this.word.onPaste();
    }
    
    public static void main(String[] args) {
        String FINAL_CHEfCK_PAT =
                "SELECT DISTINCT A.*,B.PAT_NAME,B.SEX_CODE,B.REPORT_DATE,A.DEPT_CODE,B.BIRTHDAY,B.REPORT_STATUS"
                        + "     FROM HRM_ORDER A,HRM_PATADM B" + "  WHERE A.EXEC_DEPT_CODE='#'"
                        + "       AND A.DEPT_ATTRIBUTE='04'" + "          AND A.CASE_NO=B.CASE_NO"
                        + "       AND B.REPORT_DATE >=TO_DATE('#','YYYYMMDDHH24MISS')"
                        + "       AND B.REPORT_DATE<TO_DATE('#','YYYYMMDDHH24MISS')"
                        + "          #" + "       AND A.SETMAIN_FLG='Y'" + "    ORDER BY A.SEQ_NO";
        String str = "1111.2235";
        // System.out.println(FINAL_CHEfCK_PAT);
        Pattern pattern = Pattern.compile("[0-9]*(\\.?)[0-9]*");
        // System.out.println(""+pattern.matcher(str).matches());
        String str1 = "08|05";
        String[] s = str1.split("\\|");
        for (String temp : s) {
            // System.out.println("temp=="+temp);
        }
        // System.out.println("=="+s.length);
        TWord word = new TWord();
        word.onOpen("10\01\000000000311", "100123000005_门诊家族史_02.jhw", 3, false);
        //====================add by huangjw
        word.setNodeIndex(-1);
        boolean mSwitch = word.getMessageBoxSwitch();
        word.setFileRemark("05|01|06");
		word.setMessageBoxSwitch(false);
		word.onSave();
		word.setMessageBoxSwitch(mSwitch);
		//====================add by huangjw
        // System.out.println(""+word.getFileRemark());
    }

    /**
     * 编辑
     */
    public void onEditWord() {//wanglong add 20150504
        word.onEditWord();
    }

    /**
     * 阅览
     */
    public void onPreviewWord() {//wanglong add 20150504
        word.onPreviewWord();
    }
    
    /**
     * 插入Table
     */
    public void onInsertTableBase() {//wanglong add 20150504
        word.insertBaseTableDialog();
    }

    /**
     * Table属性
     */
    public void onTableProperty() {//wanglong add 20150504
        ETable table = text.getPM().getFocusManager().getFocusTable();
        if (table == null) return;
        openDialog("%ROOT%\\config\\database\\TablePropertyDialog.x", table);
    }

    /**
     * TR属性
     */
    public void onTRProperty() {//wanglong add 20150504
        ETR tr = text.getPM().getFocusManager().getFocusTR();
        if (tr == null) return;
        openDialog("%ROOT%\\config\\database\\TRPropertyDialog.x", tr);
    }

    /**
     * 合并单元格
     */
    public void onUniteTD() {//wanglong add 20150504
        word.onUniteTD();
    }

    /**
     * 表格前插入空行
     */
    public void onTableInsertPanel() {//wanglong add 20150504
        ETable table = word.getFocusManager().getFocusTable();
        if (table == null) return;
        EPanel panel = table.getPanel();
        int index = panel.findIndex();
        EPage page = panel.getPage();
        EPanel p = page.newPanel(index);
        p.newText();
        word.update();
    }
    
    /**
     * 段落
     */
    public void onParagraphEdit() {//wanglong add 20150504
        word.onParagraphDialog();
    }
    
    /**
     * 图层控制
     */
    public void onDIVProperty() {//wanglong add 20150504
        text.getPM().getPageManager().getMVList().openProperty();
    }
    
    /**
     * 插入图区
     */
    public void onInsertPic() {//wanglong add 20150504
        MFocus focusManager = text.getFocusManager();
        if (focusManager == null) return;
        // 插入图区
        focusManager.insertPic();
        // 更新
        focusManager.update();
    }

    /**
     * 删除图区
     */
    public void onDeletePic() {//wanglong add 20150504
        EComponent com = text.getFocusManager().getFocus();
        if (!(com instanceof EPic)) return;
        ((EPic) com).removeThis();
        text.getPM().getFocusManager().update();
    }

    /**
     * 图区图层控制
     */
    public void onPicDIVProperty() {//wanglong add 20150504
        EComponent com = text.getFocusManager().getFocus();
        if (!(com instanceof EPic)) return;
        ((EPic) com).getMVList().openProperty();
    }
    
    /**
     * 固定文本属性
     */
    public void onOpenFixedProperty() {//wanglong add 20150504
        word.onOpenFixedProperty();
    }

    /**
     * 删除固定文本
     */
    public void onRemoveFixed() {//wanglong add 20150504
        word.deleteFixed();
    }
    
    // ====================排序功能begin======================add by wanglong 20130515
    /**
     * 加入表格排序监听方法
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// 点击相同列，翻转排序
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// 取得表单中的数据
                String columnName[] = tableData.getNames("Data");// 获得列名
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // 表格排序的列名;
                int col = tranParmColIndex(columnName, tblColumnName); // 列名转成parm中的列索引
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // 将排序后的vector转成parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * 根据列名数据，将TParm转为Vector
     * @param parm
     * @param group
     * @param names
     * @param size
     * @return
     */
    private Vector getVector(TParm parm, String group, String names, int size) {
        Vector data = new Vector();
        String nameArray[] = StringTool.parseLine(names, ";");
        if (nameArray.length == 0) {
            return data;
        }
        int count = parm.getCount(group, nameArray[0]);
        if (size > 0 && count > size)
            count = size;
        for (int i = 0; i < count; i++) {
            Vector row = new Vector();
            for (int j = 0; j < nameArray.length; j++) {
                row.add(parm.getData(group, nameArray[j], i));
            }
            data.add(row);
        }
        return data;
    }

    /**
     * 返回指定列在列名数组中的index
     * @param columnName
     * @param tblColumnName
     * @return int
     */
    private int tranParmColIndex(String columnName[], String tblColumnName) {
        int index = 0;
        for (String tmp : columnName) {
            if (tmp.equalsIgnoreCase(tblColumnName)) {
                return index;
            }
            index++;
        }
        return index;
    }

    /**
     * 根据列名数据，将Vector转成Parm
     * @param vectorTable
     * @param parmTable
     * @param columnNames
     * @param table
     */
    private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
            String columnNames, final TTable table) {
        String nameArray[] = StringTool.parseLine(columnNames, ";");
        for (Object row : vectorTable) {
            int rowsCount = ((Vector) row).size();
            for (int i = 0; i < rowsCount; i++) {
                Object data = ((Vector) row).get(i);
                parmTable.addData(nameArray[i], data);
            }
        }
        parmTable.setCount(vectorTable.size());
        table.setParmValue(parmTable);
    }
    // ====================排序功能end======================
    public void saveWord() {
		boolean mSwitch = word.getMessageBoxSwitch();
		// this.messageBox("mSwitch" + mSwitch);
		word.setMessageBoxSwitch(false);
		word.onSave();
		word.setMessageBoxSwitch(mSwitch);
	}
    
	/**
	 * 检验报告
	 */
	public void onLis() {
		SystemTool.getInstance().OpenLisWeb(this.getValueString("MR_NO"));
	}

	/**
	 * 检查报告
	 */
	public void onRis() {
		SystemTool.getInstance().OpenRisWeb(this.getValueString("MR_NO"));
	}
	
	/**
     * 心电检查
     */
    public void getPdfReport(){
        int row = patTable.getSelectedRow();
        TParm patParm = patTable.getParmValue();
        if (patParm == null) {
            return;
        }
        int count = patParm.getCount();
        if (count <= 0) {
            return;
        }
        String caseNo = patParm.getValue("CASE_NO", row);
    	String sql = "SELECT DISTINCT MED_APPLY_NO  FROM HRM_ORDER WHERE CASE_NO = '"+caseNo+"' AND ORDER_CAT1_CODE = 'ECC'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount() <= 0){
    		this.messageBox("该病患没有心电检查医嘱");
    		return;
    	}
    	// 电生理调阅pdf
		TParm parm = new TParm();
		String opbBookNo = "";
    	for(int i = 0; i < result.getCount(); i++){
    		opbBookNo += "'"+result.getValue("MED_APPLY_NO", i)+"'"+",";
    	}
    	parm.setData("CASE_NO",caseNo);
    	parm.setData("TYPE","3");
    	parm.setData("OPE_BOOK_NO",opbBookNo.substring(0, opbBookNo.length()-1));
    	this.openDialog("%ROOT%\\config\\odi\\ODIOpeMr.x", parm);
    }
    
    /**
     * 整洁预览
     */
    public void onPrintClear() {
        TParm parm = orderTable.getParmValue();
        if (parm == null) {
            return;
        }
        if (parm.getCount() <= 0) {
            return;
        }
        word.onPreviewWord();
    }
    /**
	 * 调阅分布式存储病历
	 * 
	 * @param filePath 文件路径
	 * @param fileName 文件名称
	 * @return parm
	 */
	public TParm readFile(String filePath, String fileName) {
		TParm parm = new TParm();
		String serverRoot = TConfig.getSystemValue("FileServer.Main.Root");
		TSocket socket = TIOM_FileServer.getSocket();

		if (StringUtils.isNotEmpty(fileName) && fileName.indexOf("_") != -1) {
			String sYear = fileName.substring(0, 2);
			String root = TConfig.getSystemValue("FileServer." + sYear
					+ ".Root");
			String ip = TConfig.getSystemValue("FileServer." + sYear + ".IP");
			if (StringUtils.isNotEmpty(root)) {
				serverRoot = root;
			}
			if (StringUtils.isNotEmpty(ip)) {
				socket = new TSocket(ip, 8103);
			}
		}
		String serverPath = serverRoot + "\\"
				+ TConfig.getSystemValue("EmrData") + "\\"
				+ filePath.replaceFirst("JHW", "PDF");
		parm.setData("FILE_DATA", TIOM_FileServer.readFile(socket, serverPath
				+ "\\" + fileName));
		parm.setData("SERVER_PATH", serverPath);
		return parm;
	}
}
