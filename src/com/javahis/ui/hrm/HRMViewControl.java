package com.javahis.ui.hrm;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import jdo.bil.BILComparator;
import jdo.hrm.HRMOrder;
import jdo.hrm.HRMSchdayDr;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TWord;
import com.dongyang.util.ImageTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.EmrUtil;
import com.javahis.util.StringUtil;

//zhanglei 20170907 全选 start
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
//zhanglei 20170907 全选 end

/**
 * <p>Title: 健康检查问诊控制类</p>
 *
 * <p>Description: 健康检查问诊控制类</p>
 *
 * <p>Copyright: javahis 20090922</p>
 *
 * <p>Company:JavaHis</p>
 *
 * @author ehui
 * @version 1.0
 */
public class HRMViewControl extends TControl {

    // TABLE
    private TTable table;
    // TWORD
    private TWord word;
    // 科别属性
    private String deptAtt, caseNo;
    // 医嘱对象
    private HRMOrder order;
    // 结构化病历保存路径
    private String[] saveFile;
    // 结构化病例路径
    private TParm pathParm, patParm;
    private TParm patDoParm = new TParm();// wanglong add 20141114
    private TParm patUndoParm = new TParm();
    private BILComparator compare = new BILComparator();// add by wanglong 20130515
    private boolean ascending = false;
    private int sortColumn = -1;
    
    /**
     * 初始化事件
     */
    public void onInit() {
        super.onInit();
        // 初始化控件
        initComponent();
        // 初始化数据
        initData();
        
        
        
    }

    /**
     * 初始化控件
     */
    private void initComponent() {
        table = (TTable) this.getComponent("TABLE");
        addSortListener(table);// add by wanglong 20130515
        word = (TWord) this.getComponent("WORD");
        // caowl 20130326 start 增加初始化时间
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String date = StringTool.getString(now, "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(date + "000000", "yyyyMMddHHmmss"));
        this.setValue("END_DATE", StringTool.getTimestamp(date + "235959", "yyyyMMddHHmmss"));
        // caowl 20130326 end
        
        // zhanglei 20170907 start 增加初始化默认光标位置
        onFocus();
        // zhanglei 20170907 end 增加初始化默认光标位置
    }
    
    

    /**
     * 初始化数据
     */
    private void initData() {
        order = new HRMOrder();
        // 取得当前诊室、医师的科别属性
        deptAtt = HRMSchdayDr.getDeptAttribute();
        if (StringUtil.isNullString(deptAtt)) {
            this.messageBox("取得科别属性错误");
            return;
        }
        this.setValue("DEPT_ATT", deptAtt);
        onClear();

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
     * 清空事件
     */
    public void onClear() {
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String date = StringTool.getString(now, "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(date + "000000", "yyyyMMddHHmmss"));
        this.setValue("END_DATE", StringTool.getTimestamp(date + "235959", "yyyyMMddHHmmss"));
        this.setValue("UNDONE", "Y");
        this.setValue("MR_NO", "");
        this.setValue("PAT_NAME", "");
        this.setValue("SEX_CODE", "");
        this.setValue("AGE", "");
        clearValue("UNDONE_NUM;DONE_NUM");// add by wanglong 20130328
        this.setValue("TEL", "");//wanglong add 20140606
        TPanel panel = (TPanel) this.getComponent("PIC");
        panel.removeAll();
        panel.repaint();
        table.removeRowAll();
        word.onNewFile();
        word.update();
        saveFile = new String[]{};
        
        // zhanglei 20170907 start 增加初始化默认光标位置
        this.grabFocus("MR_NO");
        // zhanglei 20170907 end 增加初始化默认光标位置 
    }

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
        // 取得当前医师应该问诊的病患列表
        patDoParm =
                order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), "", true, startDate,
                                             endDate);// 已完成
        patUndoParm =
                order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), "", false, startDate,
                                             endDate);// 未完成
        if (patDoParm.getErrCode() != 0 || patUndoParm.getErrCode() != 0) {
            this.messageBox("查询失败 " + patDoParm.getErrText() + patUndoParm.getErrText());
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            table.setParmValue(new TParm());
            return;
        }
        
        // add by wangb 2016/06/24 根据登录角色类型过滤数据 START
        String filter = "";
		filter = this.getPopedemParm().getValue("ID");
		int doCount = patDoParm.getCount();
		int undoCount = patUndoParm.getCount();
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
     	// add by wangb 2016/06/24 根据登录角色类型过滤数据 END
        
        if ((patDoParm == null || patDoParm.getCount() <= 0)
                && (patUndoParm == null || patUndoParm.getCount() <= 0)) {
            this.messageBox("无数据！");
            this.setValue("UNDONE_NUM", "0人");
            this.setValue("DONE_NUM", "0人");
            table.setParmValue(new TParm());
            return;
        }
        this.setValue("UNDONE_NUM", (patUndoParm.getCount() < 0 ? 0 : patUndoParm.getCount()) + "人");
        this.setValue("DONE_NUM", (patDoParm.getCount() < 0 ? 0 : patDoParm.getCount()) + "人");
        if (TypeTool.getBoolean(this.getValue("DONE"))) {// 已完成
            table.setParmValue(patDoParm);
        } else {// 未完成
            table.setParmValue(patUndoParm);
        }
    }
    
    /**
     * 根据选中MR_NO查询病患的医嘱
     */
    public void onMrNo() {
        if (StringUtil.isNullString(this.getValueString("DEPT_ATT"))) {
            this.messageBox("请选择科别属性");
            return;
        }
        if ("N".equals(this.getValueString("DONE")) && "N".equals(this.getValueString("UNDONE"))) {
            this.messageBox("请选择状态");
            return;
        }
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); // ========= chenxi
        this.setValue("MR_NO", mrNo);
        
        // modify by huangtt 20160929 EMPI患者查重提示 start
        Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			 this.setValue("MR_NO", mrNo);
		}
		// modify by huangtt 20160929 EMPI患者查重提示 end
        
        
        // onQuery();
        String startDate = ""; // =====================病案号查询的时候不卡日期
        String endDate = "";
        // 取得当前医师应该问诊的病患列表
        patDoParm =
                order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), mrNo, true,
                                             startDate, endDate);// 已完成 modify by wanglong 20130515
        patUndoParm =
                order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), mrNo, false,
                                             startDate, endDate);// 未完成
        if (patDoParm.getErrCode() != 0 || patUndoParm.getErrCode() != 0) {
            this.messageBox("查询失败 " + patDoParm.getErrText() + patUndoParm.getErrText());
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            table.setParmValue(new TParm());
            return;
        }
        
        // add by wangb 2016/06/24 根据登录角色类型过滤数据 START
        String filter = "";
		filter = this.getPopedemParm().getValue("ID");
		int doCount = patDoParm.getCount();
		int undoCount = patUndoParm.getCount();
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
     	// add by wangb 2016/06/24 根据登录角色类型过滤数据 END
        
        if ((patDoParm == null || patDoParm.getCount() <= 0)
                && (patUndoParm == null || patUndoParm.getCount() <= 0)) {
            this.messageBox("无数据！");
            this.setValue("UNDONE_NUM", "0人");
            this.setValue("DONE_NUM", "0人");
            table.setParmValue(new TParm());
            return;
        }
        this.setValue("UNDONE_NUM", (patUndoParm.getCount() < 0 ? 0 : patUndoParm.getCount()) + "人");
        this.setValue("DONE_NUM", (patDoParm.getCount() < 0 ? 0 : patDoParm.getCount()) + "人");
        String sql = // wanglong add 20141114
                "SELECT C.EXEC_DR_CODE FROM HRM_CONTRACTD A, HRM_PATADM B, HRM_ORDER C "
                        + " WHERE A.MR_NO = B.MR_NO AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "   AND B.CASE_NO = C.CASE_NO AND C.SETMAIN_FLG = 'Y' "
                        + "   AND C.DEPT_ATTRIBUTE = '03' AND A.MR_NO = '#' "
                        + "ORDER BY B.CASE_NO DESC";
        sql = sql.replaceFirst("#", mrNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("查询失败2 " + result.getErrText());
            return;
        }
        if (!result.getValue("EXEC_DR_CODE", 0).equals("")) {//查询出的最新一次就诊记录如果为已完成
            this.setValue("DONE", "Y");
            table.setParmValue(patDoParm);
        } else {// 未完成
            this.setValue("UNDONE", "Y");
            table.setParmValue(patUndoParm);
        }
        table.setSelectedRow(0);
        onChoosePat();
    }
    
    /**
     * 选择状态
     */
    public void onChooseState() {
        this.setValue("UNDONE_NUM", (patUndoParm.getCount() < 0 ? 0 : patUndoParm.getCount()) + "人");
        this.setValue("DONE_NUM", (patDoParm.getCount() < 0 ? 0 : patDoParm.getCount()) + "人");
        if (TypeTool.getBoolean(this.getValue("DONE"))) {// 已完成
            table.setParmValue(patDoParm);
        } else {// 未完成
            table.setParmValue(patUndoParm);
        }
    }
    
    /**
     * TABLE双击事件，选择病患的医嘱
     */
    public void onChoosePat() {
        TParm parm = table.getParmValue();
        int row = table.getSelectedRow();
        if (row < 0) {
            return;
        }
        if (parm == null) {
            return;
        }
        String mrNo = parm.getValue("MR_NO", row);
        caseNo = parm.getValue("CASE_NO", row);
        // System.out.println("onChoosePat.caseNo============"+caseNo);
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        patParm = parm.getRow(row);
        this.setValue("MR_NO", mrNo);
        this.setValue("PAT_NAME", parm.getValue("PAT_NAME", row));
        this.setValue("SEX_CODE", parm.getValue("SEX_CODE", row));
        this.setValue("TEL", parm.getValue("TEL", row));//wanglong add 20140606
        if (!StringUtil.isNullString(parm.getValue("BIRTHDAY", row))) {
            this.setValue("AGE", StringUtil.showAge(parm.getTimestamp("BIRTHDAY", row), TJDODBTool.getInstance().getDBTime()));
        }
        viewPhoto(mrNo);
        // 配置打开结构化病历的参数，打开结构化病历的数据
        order.filt(caseNo);
        String tempName = parm.getValue("MR_CODE", row);
        TParm emrParm = new TParm();
        emrParm.setData("MR_CODE", tempName);
        emrParm.setData("CASE_NO", caseNo);
        emrParm = EmrUtil.getInstance().getEmrFilePath(emrParm);
        emrParm.setData("FILE_TITLE_TEXT", "TEXT", Manager.getOrganization().getHospitalCHNFullName(Operator.getRegion()));
        emrParm.setData("FILE_TITLEENG_TEXT", "TEXT", Manager.getOrganization().getHospitalENGFullName(Operator.getRegion()));
        emrParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getValueString("MR_NO"));
        emrParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", "");
        emrParm.setData("FILE_128CODE", "TEXT", this.getValueString("MR_NO"));
        // ============xueyf modify 20120223 start
        // emrParm.setData("TEMPLET_PATH","JHW\\"+emrParm.getValue("TEMPLET_PATH"));
        emrParm.setData("TEMPLET_PATH", emrParm.getValue("TEMPLET_PATH"));
        // ============xueyf modify 20120223 stop
        pathParm = emrParm;
        // System.out.println("emrParm="+emrParm);
        word.onNewFile();
        word.update();
        if (TypeTool.getBoolean(this.getValue("UNDONE"))) {
            word.onOpen(emrParm.getValue("TEMPLET_PATH"), emrParm.getValue("EMT_FILENAME"), 2, false);
            word.setNodeIndex(-1);
            saveWord();
            emrParm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            word.setWordParameter(emrParm);
            word.setCanEdit(true);
            saveFile = new String[2];
        } else {
            saveFile = new String[2];
            saveFile[0] = emrParm.getValue("FILE_PATH").indexOf("JHW") < 0 ? "JHW\\" + emrParm.getValue("FILE_PATH") : emrParm.getValue("FILE_PATH");
            saveFile[1] = emrParm.getValue("FILE_NAME");
            word.onOpen(saveFile[0], saveFile[1], 3, false);
            word.setNodeIndex(-1);
            saveWord();
            emrParm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
            word.setWordParameter(emrParm);
            word.setCanEdit(true);
        }
    }

    /**
     * 显示photo
     */
    public void viewPhoto(String mrNo) {
        String photoName = mrNo + ".jpg";
        String fileName = photoName;
        try {
            TPanel viewPanel = (TPanel) getComponent("PIC");
            String root = TIOM_FileServer.getRoot();
            String dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
            dir = root + dir + mrNo.substring(0, 3) + "\\" + mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
            byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(), dir + fileName);
            if (data == null) return;
            double scale = 0.2;
            boolean flag = true;
            Image image = ImageTool.scale(data, scale, flag);
            // Image image = ImageTool.getImage(data);
            Pic pic = new Pic(image);
            pic.setSize(viewPanel.getWidth() - 2, viewPanel.getHeight() - 2);
            pic.setLocation(0, 0);
            viewPanel.removeAll();
            viewPanel.add(pic);
            pic.repaint();
        }
        catch (Exception e) {}
    }

    /**
     * 保存
     */
    public void onSave() {
        if (StringUtil.isNullString(saveFile[0])) {
            pathParm.setData("CASE_NO", patParm.getValue("CASE_NO"));
            pathParm.setData("MR_NO", patParm.getValue("MR_NO"));
            TParm parm = EmrUtil.getInstance().getFileServerEmrName(pathParm);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            parm.setData("OPT_TERM", Operator.getIP());
            // =============xueyf modify 20120212 start
            parm.setData("REPORT_FLG", "N");
            // =============xueyf modify 20120212 stop
            parm.setData("CREATOR_USER", Operator.getID());
            parm.setData("CURRENT_USER", Operator.getID());
            // System.out.println("parm====="+parm);
            TParm result = TIOM_AppServer.executeAction("action.odi.ODIAction", "saveNewEmrFile", parm);
            if (result.getErrCode() != 0) {
                // System.out.println("errText="+result.getErrText());
                this.messageBox("新增失败1 " + result.getErrText());
                return;
            }
			result=order.saveByCheck(Operator.getRegion(), Operator.getDept(), caseNo, this.getValueString("DEPT_ATT"), parm.getValue("FILE_SEQ"), true);
            if (result.getErrCode() != 0) {
                this.messageBox("新增失败2 " + result.getErrText());
                return;
            }
            // ============xueyf modify 20120223 start
            parm.setData("FILE_PATH", parm.getValue("FILE_PATH").indexOf("JHW") < 0 ? "JHW\\"
                    + parm.getValue("FILE_PATH") : parm.getValue("FILE_PATH"));
            // ============xueyf modify 20120223 stop
            word.onSaveAs(parm.getValue("FILE_PATH"), parm.getValue("FILE_NAME"), 3);
        } else {
            pathParm.setData("CASE_NO", patParm.getValue("CASE_NO"));
            pathParm.setData("MR_NO", patParm.getValue("MR_NO"));
            TParm parm = EmrUtil.getInstance().getFileServerEmrName(pathParm);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
            parm.setData("OPT_TERM", Operator.getIP());
//            pathParm.setData("CREATOR_USER", Operator.getID());
            pathParm.setData("CURRENT_USER", Operator.getID());
            TParm result = TIOM_AppServer.executeAction("action.odi.ODIAction", "updateEmrFile", parm);
            if (result.getErrCode() != 0) {
                // System.out.println("errText="+result.getErrText());
                this.messageBox("保存失败1 " + result.getErrText());
                return;
            }
            result = order.saveByCheck(Operator.getRegion(), Operator.getDept(), caseNo, this.getValueString("DEPT_ATT"), "", false);
            if (result.getErrCode() != 0) {
                this.messageBox("保存失败2 " + result.getErrText());
                return;
            }
            word.onSave();
        }
        TPanel panel = (TPanel) this.getComponent("PIC");
        panel.removeAll();
        panel.repaint();
        table.removeRowAll();
        word.onNewFile();
        word.update();
        saveFile = new String[]{};
        
        
        
        onQuery();
        
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
     * 打印事件
     */
    public void onPrint() {
        if (word == null) {
            return;
        }
        if (StringUtil.isNullString(word.getFileName())) {
            return;
        }
        word.onPreviewWord();
        word.print();
    }
    
    /**
     * 更新电话
     */
    public void onUpdateTel() {// wanglong add 20140606
        String mrNo = this.getValueString("MR_NO");
        String tel = this.getValueString("TEL").trim();
        TParm parm = new TParm();
        parm.addData("MR_NO", mrNo);
        parm.addData("CASE_NO", caseNo);
        parm.addData("TEL", tel);
        parm.setCount(1);
        TParm result =
                TIOM_AppServer.executeAction("action.hrm.HRMCompanyReportAction",
                                             "updateHRMPatTEL", parm);
        if (result.getErrCode() != 0) {
            this.messageBox("更新失败 " + result.getErrText());
            return;
        } else {
            this.messageBox("更新成功");
            int row = -1;
            TParm parmValue = table.getParmValue();
            for (int i = 0; i < parmValue.getCount(); i++) {
                if (mrNo.equals(parmValue.getValue("MR_NO", i))) {
                    row = i;
                    parmValue.setData("TEL", i, tel);
                    table.setParmValue(parmValue);
                    table.setSelectedRow(row);
                }
            }
        }
    }

    class Pic extends JLabel {

        Image image;

        public Pic(Image image) {
            this.image = image;
        }

        public void paint(Graphics g) {
            g.setColor(new Color(161, 220, 230));
            g.fillRect(4, 15, 100, 100);
            if (image != null) {
                g.drawImage(image, 4, 15, null);
            }
        }
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
}
