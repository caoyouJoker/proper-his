package com.javahis.ui.emr;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JWindow;

import jdo.emr.EMRCdrTool;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.FileTool;

/**
 * <p>
 * Title: CDR��ϸ��ѯ����(������״�ṹ)
 * </p>
 * 
 * <p>
 * Description: CDR��ϸ��ѯ����(������״�ṹ)
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wangb 2015.5.16
 * @version 1.0
 */
public class EMRCdrDetailDataByTreeControl extends TControl {
	private TParm parameterParm;// ҳ�洫��
	private TTable tableData;// ��ϸ����Table
    private TTree tree;//��
    private TTreeNode treeRoot;// ����
    private SYSRuleTool ruleTool;// ��Ź�����𹤾�
    private TDataStore treeDataStore = new TDataStore();// �������ݷ���datastore���ڶ��������ݹ���
    private String rootText;// �����ڵ�
    private String rootType;// ������
    private String tempPath;
	Window window=null;
    public EMRCdrDetailDataByTreeControl() {
        super();
    }
    
    /**
     * ��ʼ������
     */
    public void onInit() {
    	Window window = (Window) this.getComponent("UI");
	    window.setAlwaysOnTop(true);
    	super.onInit();
    	
    	
    	tempPath = "C:\\JavaHisFile\\temp\\pdf";
		File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
    	Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				parameterParm = (TParm) obj;
				if (parameterParm == null) {
					this.messageBox("ҳ�洫�δ���");
					return;
				}
			} else {
				this.messageBox("ҳ�洫�δ���");
				return;
			}
		} else {
			this.messageBox("ҳ�洫�δ���");
			return;
		}
		// ��ʼ��ҳ��
		this.onInitPage();
    }
    
	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		tableData = getTable("TABLE_DATA");
		tree = (TTree) callMessage("UI|TREE|getThis");
        //��tree��Ӽ����¼�  
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        // �趨�������
		this.queryTableData();
		// ��ʼ����  
        this.onInitTree();
        // ��ʼ�����
        this.onInitNode();

	}
	
    /**
     * �趨�������
     */
	private void queryTableData() {
		// ҳ�����
		String title = "";
		// ���ݱ���б���
		String tableHeader = "";
		// ���ݱ��������
		String tableParmMap = "";
		// ���ݱ���ж��뷽ʽ
		String tableAlignData = "";
		// ����������
		String lockColumns = "";
		// ���ݱ���ѯ���
		TParm result = new TParm();
		
		if (StringUtils.equals("6", parameterParm.getValue("DATA_TYPE"))) {
			title = "ҩ��";
			rootText = "ҩƷ����";
			rootType = "PHA_RULE";
			tableHeader = "����,30;ҩƷͨ����,250;��Ʒ��,120;��������,80;��ҩƵ��,80;��ҩ;��,70;�ܼ���,80;ҽ����ע,120;����ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;ͣ��ʱ��,140,Timestamp,yyyy/MM/dd HH:mm:ss;��������,100;����,80;ҩ������,120;ҩ��η���,120;��������ҩĿ��,120;�����طּ�,100;����ҩƷ�ּ�,80;��������,80;��������,80,Timestamp,yyyy/MM/dd;��Ժ����,80,Timestamp,yyyy/MM/dd;�������,120";
			tableParmMap = "IVA_LINK_NO;DRUG_DESC;GOODS_DESC;MEDI_QTY;FREQUENCY;ROUTE;DOSAGE_QTY;DR_NOTE;START_DATE;END_DATE;RX_KIND;DOSE_DESC;CATE1_DESC;CATE2_DESC;ANTIBIOTIC_WAY;ANTIBIOTIC_LEVEL;CTRLCLASS_DESC;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC;STATUS";
			tableAlignData = "0,left;1,left;2,left;3,right;4,left;5,left;6,right;7,right;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left;17,left;18,left;19,left;20,left";
			lockColumns = "all";
			// ��ѯҩ������
			result = EMRCdrTool.getInstance().getMedData(parameterParm);
			// ����ҳ�����
			this.setTitle(title);
			// ���ñ����
			tableData.setHeader(tableHeader);
			tableData.setParmMap(tableParmMap);
			tableData.setColumnHorizontalAlignmentData(tableAlignData);
			tableData.setLockColumns(lockColumns);
		} else if (StringUtils.equals("7", parameterParm.getValue("DATA_TYPE"))) {
			rootText = "����";
			rootType = "EXM_RULE";
			// ��ѯ��������
			result = EMRCdrTool.getInstance().getLisData(parameterParm);
			// ���鱨�湴ѡ�����¼�
			tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "eventCheckBox");
		} else if (StringUtils.equals("8", parameterParm.getValue("DATA_TYPE"))) {
			rootText = "���";
			rootType = "EXM_RULE";
			// ��ѯ�������� ;
			result = EMRCdrTool.getInstance().getExmData(parameterParm);
			// ������Ӱ��ѡ�����¼�
			tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "OpenRisWeb");
		}
		tableData.setParmValue(result);
	}
	
    /**
     * ��ʼ����
     */
    public void onInitTree() {
        //�õ�����
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null) {
            return;
        }
        //�����ڵ����������ʾ
        treeRoot.setText(rootText);
        //�����ڵ㸳tag
        treeRoot.setType("Root");
        //���ø��ڵ��id
        treeRoot.setID("");
        //������нڵ������
        treeRoot.removeAllChildren();
        //���������ʼ������
        callMessage("UI|TREE|update");
    }

    /**
     * ��ʼ�����Ľ��
     */
    public void onInitNode() {
    	TComboBox comboBox = new TComboBox();
    	String comboName = "";
    	int count = 0;
    	List<String> comboBoxList = new ArrayList<String>();
    	
    	if (StringUtils.equals("7", parameterParm.getValue("DATA_TYPE"))) {
    		// ����
    		comboName = "LIS_COMBO";
    	} else if (StringUtils.equals("8", parameterParm.getValue("DATA_TYPE"))) {
    		// ���
    		comboName = "RIS_COMBO";
    	}
    	
    	if (StringUtils.isNotEmpty(comboName)) {
    		comboBox = ((TComboBox)this.getComponent(comboName));
    		count = comboBox.getItemCount();
    		// ��ǰ̨ҳ���ϵ�COMBOBOXȡ�ü�����Ĵ��룬���ڶ�̬������Ӧ�ķ�����Ŀ
        	for (int i = 0; i < count; i++) {
        		comboBoxList.add(comboBox.getItem(i).getID());
        	}
    	}
    	
        //��dataStore��ֵ
		treeDataStore.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='"+rootType+"'");
        //�����dataStore���õ�������С��0
        if (treeDataStore.retrieve() <= 0)
            return;
        //��������,�Ǳ�������еĿ�������
        ruleTool = new SYSRuleTool(rootType);
        if (ruleTool.isLoad()) { //�����۽ڵ����:datastore���ڵ����,�ڵ���ʾ����,,�ڵ�����
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ");
            //ѭ����������ڵ�
            for (int i = 0; i < node.length; i++) {
            	// ����ȡ�õķ�����빹����״�ṹ
            	if (comboBoxList.size() > 0) {
            		if (comboBoxList.contains(node[i].getID())) {
            			treeRoot.addSeq(node[i]);
            		}
            	} else {
            		treeRoot.addSeq(node[i]);
            	}
            }
        }
        //������
        tree.update();
        //��������Ĭ��ѡ�нڵ�
        tree.setSelectNode(treeRoot);
    }
    
    /**
     * ������
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
    	TTreeNode node = tree.getSelectNode();
        if (node == null) {
            return;
        }
        //�жϵ�����Ƿ������ĸ����
        if (node.getType().equals("Root")) {
        	parameterParm.setData("FILTER_DATA", "");
            //��������ĸ��ӵ�table�ϲ���ʾ����
        	tableData.removeRowAll();
        } else { //�����Ĳ��Ǹ����
            //�õ���ǰѡ�еĽڵ��idֵ
            String id = node.getID();
            parameterParm.setData("FILTER_DATA", id);
        }
        
        // �趨�������
		this.queryTableData();
    }
    
    /**
	 * ���鱨�����ݹ�ѡ����
	 * 
     * @param obj
	 */
	public void eventCheckBox(Object obj) {
		
		
		
		TTable reportTable;
		TTabbedPane tTabledPane=(TTabbedPane) this.getComponent("tTabbedPane_0");
		//�л�checkBox ʱ��tTabledPane��Ϊ�ɱ༭
		tTabledPane.setEnabledAt(0,false);
		tTabledPane.setEnabledAt(1,false);
		tTabledPane.setEnabledAt(2,false);
		//�л�checkBox ʱ��tTabledPane�еı�����
		reportTable = (TTable) this.getComponent("TABLE1");//һ�������
		reportTable.removeRowAll();
		reportTable = (TTable) this.getComponent("TABLE2");//ҩ��ʵ����
		reportTable.removeRowAll();
		reportTable = (TTable) this.getComponent("TABLE3");//ϸ���������
		reportTable.removeRowAll();
		
		TTable table = (TTable) obj;
		table.acceptText();
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		TParm parm = new TParm();
		if(window!=null){//�رոý��浯������������
			window.dispose();
		}
		if ("Y".equals(table.getParmValue().getValue("LIS_WORD", row))) {
			
			for(int i = 0;i<tableParm.getCount();i++){//�������checkBoxʱ����֮ǰ�Ĺ��ÿ�
				if(tableParm.getValue("LIS_WORD", i).equals("Y")&&i!=row){
					table.setItem(i, "LIS_WORD", "N");
				}
			}
			
			parm.setData("CAT1_TYPE", tableParm.getValue("CAT1_TYPE", row));
			parm.setData("APPLY_NO", tableParm.getValue("APPLY_NO", row));
			//parm.setData("LAB_TYPE", tableParm.getValue("LAB_TYPE", row));
			TParm resultLis=EMRCdrTool.getInstance().getLisData1(parm);//һ�����
			TParm resultAnt=EMRCdrTool.getInstance().getLisAntitest(parm);//ҩ��ʵ��
			TParm resultLisCulr=EMRCdrTool.getInstance().getLisCulrpt(parm);//ϸ������
			if(resultLis.getCount()>0){//һ�����
				reportTable = (TTable) this.getComponent("TABLE1");
				reportTable.setParmValue(resultLis);
				tTabledPane.setEnabledAt(0, true);
				tTabledPane.setSelectedIndex(0);
			}
			int count = 0;
			if(resultAnt.getCount()>0){//ҩ��ʵ��
				count++;
				reportTable = (TTable) this.getComponent("TABLE2");
				reportTable.setParmValue(resultAnt);
				tTabledPane.setEnabledAt(1, true);
				tTabledPane.setSelectedIndex(1);
			}
			if(resultLisCulr.getCount()>0){//ϸ������
				if(count == 0){
					tTabledPane.setSelectedIndex(2);
				}
				reportTable = (TTable) this.getComponent("TABLE3");
				reportTable.setParmValue(resultLisCulr);
				tTabledPane.setEnabledAt(2, true);
			}
			
			
		}
	}

	/**
	 * ��̩��RIS����(����Ӱ��)
	 * 
     * @param obj
	 */
	public void OpenRisWeb(Object obj) {
		TFrame tFrame = (TFrame) this.getComponent("UI");//���ҳ��
		TTable table = (TTable) obj;
		TTable tableParam = (TTable) this.getComponent("TABLE");//����������
		TTabbedPane tabbedPane = (TTabbedPane) this.getComponent("tTabbedPane_0");
		tabbedPane.setEnabledAt(1, false);
		table.acceptText();
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		TParm parm =new TParm();
		
		int column = table.getSelectedColumn();
		boolean flg = false;
		if (column==9 && "Y".equals(tableParm.getValue("SEEIMAGE", row))) {
			for(int i = 0;i < tableParm.getCount(); i++){
				if(i!=row && tableParm.getValue("SEEIMAGE", i).equals("Y")){
					table.setItem(i,"SEEIMAGE","N");//���������
				}
				if(tableParm.getValue("RIS_REPORT", i).equals("Y")){//�������checkBoxʱ����֮ǰ�Ĺ��ÿ�
					table.setItem(i, "RIS_REPORT", "N");
				}
			}
			if("Y".equals(tableParm.getValue("IS_PACS",row))){//�ǵ����� ����Ӱ��
				SystemTool.getInstance()
						.OpenRisByMrNoAndApplyNo(parameterParm.getValue("MR_NO"),tableParm.getValue("APPLY_NO",row));
				flg = true;
			}else{//���������pdf �ļ�
				parm.setData("CASE_NO",tableParm.getValue("CASE_NO", row));
				parm.setData("OPE_BOOK_NO",tableParm.getValue("APPLY_NO", row));
				TParm pathData = EMRCdrTool.getInstance().getWordPath(parm);
				
				for (int i = 0; i < pathData.getCount(); i++) {
					String fileName = pathData.getValue("FILE_NAME", i) + ".pdf";
					String filePath = pathData.getValue("FILE_PATH", i);
					parm.setData("FILE_NAME", fileName);
					Runtime runtime = Runtime.getRuntime();
					// ���ķֲ�ʽ�洢����
					TParm fileParm = EMRCdrTool.getInstance().readFile(filePath, fileName);
					byte data[] = (byte[]) fileParm.getData("FILE_DATA");
					if (data == null) {
						messageBox_("��������û���ҵ��ļ� " + fileParm.getValue("SERVER_PATH") + "\\" + fileName);
						return;
					}
					try {
						FileTool.setByte(tempPath + "\\" + fileName, data);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					try {
						// ���ļ�
						runtime.exec("rundll32 url.dll FileProtocolHandler "
								+ tempPath + "\\" + fileName);
						flg = true;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
			}
			if(flg){
				tFrame.setExtendedState(TFrame.ICONIFIED); //������С��
			}
			return;
		}
		
		if(tableParm.getValue("RIS_REPORT", row).equals("N")){//��ԭ�д򹴵Ŀؼ� �ÿգ�����ձ�������
			this.setValue("OUTCOME_DESCRIBE", null);
			this.setValue("OUTCOME_CONCLUSION",null);
			this.setValue("OUTCOME_TYPE", null);
			tableParam.removeRowAll();
			return;
		}
		
		if(column == 8 && "Y".equals(tableParm.getValue("RIS_REPORT", row))){
			for(int i = 0;i<tableParm.getCount();i++){
				if(tableParm.getValue("RIS_REPORT", i).equals("Y")&&i!=row){//�������checkBoxʱ����֮ǰ�Ĺ��ÿ�
					table.setItem(i, "RIS_REPORT", "N");
				}
				if(tableParm.getValue("SEEIMAGE", i).equals("Y")){
					table.setItem(i,"SEEIMAGE","N");//���������
				}
			}
			this.setValue("OUTCOME_DESCRIBE", tableParm.getValue("OUTCOME_DESCRIBE",row));
			this.setValue("OUTCOME_CONCLUSION", tableParm.getValue("OUTCOME_CONCLUSION",row));
			this.setValue("OUTCOME_TYPE", tableParm.getValue("OUTCOME_TYPE",row));
			parm.setData("APPLICATION_NO",tableParm.getValue("APPLY_NO", row));
			TParm data = EMRCdrTool.getInstance().getPhiscalParam(parm);//��ȡ�������������
			if(data.getCount()>0){
				tabbedPane.setEnabledAt(1, true);
				tableParam.setParmValue(data);
			}
		}
	}
	
    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
}
