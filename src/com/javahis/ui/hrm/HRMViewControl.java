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

//zhanglei 20170907 ȫѡ start
import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
//zhanglei 20170907 ȫѡ end

/**
 * <p>Title: ����������������</p>
 *
 * <p>Description: ����������������</p>
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
    // �Ʊ�����
    private String deptAtt, caseNo;
    // ҽ������
    private HRMOrder order;
    // �ṹ����������·��
    private String[] saveFile;
    // �ṹ������·��
    private TParm pathParm, patParm;
    private TParm patDoParm = new TParm();// wanglong add 20141114
    private TParm patUndoParm = new TParm();
    private BILComparator compare = new BILComparator();// add by wanglong 20130515
    private boolean ascending = false;
    private int sortColumn = -1;
    
    /**
     * ��ʼ���¼�
     */
    public void onInit() {
        super.onInit();
        // ��ʼ���ؼ�
        initComponent();
        // ��ʼ������
        initData();
        
        
        
    }

    /**
     * ��ʼ���ؼ�
     */
    private void initComponent() {
        table = (TTable) this.getComponent("TABLE");
        addSortListener(table);// add by wanglong 20130515
        word = (TWord) this.getComponent("WORD");
        // caowl 20130326 start ���ӳ�ʼ��ʱ��
        Timestamp now = TJDODBTool.getInstance().getDBTime();
        String date = StringTool.getString(now, "yyyyMMdd");
        this.setValue("START_DATE", StringTool.getTimestamp(date + "000000", "yyyyMMddHHmmss"));
        this.setValue("END_DATE", StringTool.getTimestamp(date + "235959", "yyyyMMddHHmmss"));
        // caowl 20130326 end
        
        // zhanglei 20170907 start ���ӳ�ʼ��Ĭ�Ϲ��λ��
        onFocus();
        // zhanglei 20170907 end ���ӳ�ʼ��Ĭ�Ϲ��λ��
    }
    
    

    /**
     * ��ʼ������
     */
    private void initData() {
        order = new HRMOrder();
        // ȡ�õ�ǰ���ҡ�ҽʦ�ĿƱ�����
        deptAtt = HRMSchdayDr.getDeptAttribute();
        if (StringUtil.isNullString(deptAtt)) {
            this.messageBox("ȡ�ÿƱ����Դ���");
            return;
        }
        this.setValue("DEPT_ATT", deptAtt);
        onClear();

    }
    
    /**
     * ��ý���
     * zhanglei 20170907 start ���ӳ�ʼ��Ĭ�Ϲ��λ��
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
     * ����¼�
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
        
        // zhanglei 20170907 start ���ӳ�ʼ��Ĭ�Ϲ��λ��
        this.grabFocus("MR_NO");
        // zhanglei 20170907 end ���ӳ�ʼ��Ĭ�Ϲ��λ�� 
    }

    /**
     * ������ںͿƱ�����ʱ��ִ�в�ѯ
     */
    public void onDoQuery() {
        onQuery();
    }

    /**
     * ��ѯ
     */
    public void onQuery() {
        Timestamp now = (Timestamp) this.getValue("START_DATE");
        Timestamp tomorrow = (Timestamp) this.getValue("END_DATE");
        String startDate = StringTool.getString(now, "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(tomorrow, "yyyyMMdd") + "235959";
        // ȡ�õ�ǰҽʦӦ������Ĳ����б�
        patDoParm =
                order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), "", true, startDate,
                                             endDate);// �����
        patUndoParm =
                order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), "", false, startDate,
                                             endDate);// δ���
        if (patDoParm.getErrCode() != 0 || patUndoParm.getErrCode() != 0) {
            this.messageBox("��ѯʧ�� " + patDoParm.getErrText() + patUndoParm.getErrText());
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            table.setParmValue(new TParm());
            return;
        }
        
        // add by wangb 2016/06/24 ���ݵ�¼��ɫ���͹������� START
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
     	// add by wangb 2016/06/24 ���ݵ�¼��ɫ���͹������� END
        
        if ((patDoParm == null || patDoParm.getCount() <= 0)
                && (patUndoParm == null || patUndoParm.getCount() <= 0)) {
            this.messageBox("�����ݣ�");
            this.setValue("UNDONE_NUM", "0��");
            this.setValue("DONE_NUM", "0��");
            table.setParmValue(new TParm());
            return;
        }
        this.setValue("UNDONE_NUM", (patUndoParm.getCount() < 0 ? 0 : patUndoParm.getCount()) + "��");
        this.setValue("DONE_NUM", (patDoParm.getCount() < 0 ? 0 : patDoParm.getCount()) + "��");
        if (TypeTool.getBoolean(this.getValue("DONE"))) {// �����
            table.setParmValue(patDoParm);
        } else {// δ���
            table.setParmValue(patUndoParm);
        }
    }
    
    /**
     * ����ѡ��MR_NO��ѯ������ҽ��
     */
    public void onMrNo() {
        if (StringUtil.isNullString(this.getValueString("DEPT_ATT"))) {
            this.messageBox("��ѡ��Ʊ�����");
            return;
        }
        if ("N".equals(this.getValueString("DONE")) && "N".equals(this.getValueString("UNDONE"))) {
            this.messageBox("��ѡ��״̬");
            return;
        }
        String mrNo = this.getValueString("MR_NO");
        if (StringUtil.isNullString(mrNo)) {
            return;
        }
        mrNo = StringTool.fill0(mrNo, PatTool.getInstance().getMrNoLength()); // ========= chenxi
        this.setValue("MR_NO", mrNo);
        
        // modify by huangtt 20160929 EMPI���߲�����ʾ start
        Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
			 this.setValue("MR_NO", mrNo);
		}
		// modify by huangtt 20160929 EMPI���߲�����ʾ end
        
        
        // onQuery();
        String startDate = ""; // =====================�����Ų�ѯ��ʱ�򲻿�����
        String endDate = "";
        // ȡ�õ�ǰҽʦӦ������Ĳ����б�
        patDoParm =
                order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), mrNo, true,
                                             startDate, endDate);// ����� modify by wanglong 20130515
        patUndoParm =
                order.onQueryByDeptAttribute(this.getValueString("DEPT_ATT"), mrNo, false,
                                             startDate, endDate);// δ���
        if (patDoParm.getErrCode() != 0 || patUndoParm.getErrCode() != 0) {
            this.messageBox("��ѯʧ�� " + patDoParm.getErrText() + patUndoParm.getErrText());
            this.setValue("UNDONE_NUM", "");
            this.setValue("DONE_NUM", "");
            table.setParmValue(new TParm());
            return;
        }
        
        // add by wangb 2016/06/24 ���ݵ�¼��ɫ���͹������� START
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
     	// add by wangb 2016/06/24 ���ݵ�¼��ɫ���͹������� END
        
        if ((patDoParm == null || patDoParm.getCount() <= 0)
                && (patUndoParm == null || patUndoParm.getCount() <= 0)) {
            this.messageBox("�����ݣ�");
            this.setValue("UNDONE_NUM", "0��");
            this.setValue("DONE_NUM", "0��");
            table.setParmValue(new TParm());
            return;
        }
        this.setValue("UNDONE_NUM", (patUndoParm.getCount() < 0 ? 0 : patUndoParm.getCount()) + "��");
        this.setValue("DONE_NUM", (patDoParm.getCount() < 0 ? 0 : patDoParm.getCount()) + "��");
        String sql = // wanglong add 20141114
                "SELECT C.EXEC_DR_CODE FROM HRM_CONTRACTD A, HRM_PATADM B, HRM_ORDER C "
                        + " WHERE A.MR_NO = B.MR_NO AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "   AND B.CASE_NO = C.CASE_NO AND C.SETMAIN_FLG = 'Y' "
                        + "   AND C.DEPT_ATTRIBUTE = '03' AND A.MR_NO = '#' "
                        + "ORDER BY B.CASE_NO DESC";
        sql = sql.replaceFirst("#", mrNo);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            this.messageBox("��ѯʧ��2 " + result.getErrText());
            return;
        }
        if (!result.getValue("EXEC_DR_CODE", 0).equals("")) {//��ѯ��������һ�ξ����¼���Ϊ�����
            this.setValue("DONE", "Y");
            table.setParmValue(patDoParm);
        } else {// δ���
            this.setValue("UNDONE", "Y");
            table.setParmValue(patUndoParm);
        }
        table.setSelectedRow(0);
        onChoosePat();
    }
    
    /**
     * ѡ��״̬
     */
    public void onChooseState() {
        this.setValue("UNDONE_NUM", (patUndoParm.getCount() < 0 ? 0 : patUndoParm.getCount()) + "��");
        this.setValue("DONE_NUM", (patDoParm.getCount() < 0 ? 0 : patDoParm.getCount()) + "��");
        if (TypeTool.getBoolean(this.getValue("DONE"))) {// �����
            table.setParmValue(patDoParm);
        } else {// δ���
            table.setParmValue(patUndoParm);
        }
    }
    
    /**
     * TABLE˫���¼���ѡ�񲡻���ҽ��
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
        // ���ô򿪽ṹ�������Ĳ������򿪽ṹ������������
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
     * ��ʾphoto
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
     * ����
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
                this.messageBox("����ʧ��1 " + result.getErrText());
                return;
            }
			result=order.saveByCheck(Operator.getRegion(), Operator.getDept(), caseNo, this.getValueString("DEPT_ATT"), parm.getValue("FILE_SEQ"), true);
            if (result.getErrCode() != 0) {
                this.messageBox("����ʧ��2 " + result.getErrText());
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
                this.messageBox("����ʧ��1 " + result.getErrText());
                return;
            }
            result = order.saveByCheck(Operator.getRegion(), Operator.getDept(), caseNo, this.getValueString("DEPT_ATT"), "", false);
            if (result.getErrCode() != 0) {
                this.messageBox("����ʧ��2 " + result.getErrText());
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
        
        // zhanglei 20170907 start ���ӳ�ʼ��Ĭ�Ϲ��λ��
        this.grabFocus("MR_NO");
        allXuan();
        // zhanglei 20170907 end ���ӳ�ʼ��Ĭ�Ϲ��λ�� 
    }
    
    /**
     * ȫѡ�¼�
     * ���ڱ����ȫѡ������ʹ��
     * 20170907
     * zhanglei 
     */
    public void allXuan(){

    	try {
			Robot robot = new Robot();//����һ��robot����
			keyPressWithCtrl(robot,KeyEvent.VK_A); //���� ctrl+A ȫѡ
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
    	
    }
    
    /**
     * ctrl+ ����
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
     * ��ӡ�¼�
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
     * ���µ绰
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
            this.messageBox("����ʧ�� " + result.getErrText());
            return;
        } else {
            this.messageBox("���³ɹ�");
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
    
    // ====================������begin======================add by wanglong 20130515
    /**
     * �����������������
     * @param table
     */
    public void addSortListener(final TTable table) {
        table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent mouseevent) {
                int i = table.getTable().columnAtPoint(mouseevent.getPoint());
                int j = table.getTable().convertColumnIndexToModel(i);
                if (j == sortColumn) {
                    ascending = !ascending;// �����ͬ�У���ת����
                } else {
                    ascending = true;
                    sortColumn = j;
                }
                TParm tableData = table.getParmValue();// ȡ�ñ��е�����
                String columnName[] = tableData.getNames("Data");// �������
                String strNames = "";
                for (String tmp : columnName) {
                    strNames += tmp + ";";
                }
                strNames = strNames.substring(0, strNames.length() - 1);
                Vector vct = getVector(tableData, "Data", strNames, 0);
                String tblColumnName = table.getParmMap(sortColumn); // ������������;
                int col = tranParmColIndex(columnName, tblColumnName); // ����ת��parm�е�������
                compare.setDes(ascending);
                compare.setCol(col);
                java.util.Collections.sort(vct, compare);
                // ��������vectorת��parm;
                cloneVectoryParam(vct, new TParm(), strNames, table);
            }
        });
    }

    /**
     * �����������ݣ���TParmתΪVector
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
     * ����ָ���������������е�index
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
     * �����������ݣ���Vectorת��Parm
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
    // ====================������end======================
    public void saveWord() {
		boolean mSwitch = word.getMessageBoxSwitch();
		// this.messageBox("mSwitch" + mSwitch);
		word.setMessageBoxSwitch(false);
		word.onSave();
		word.setMessageBoxSwitch(mSwitch);
	}
}
