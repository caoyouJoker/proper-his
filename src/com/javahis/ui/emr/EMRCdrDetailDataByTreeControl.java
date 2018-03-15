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
 * Title: CDR明细查询界面(带有树状结构)
 * </p>
 * 
 * <p>
 * Description: CDR明细查询界面(带有树状结构)
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
	private TParm parameterParm;// 页面传参
	private TTable tableData;// 明细数据Table
    private TTree tree;//树
    private TTreeNode treeRoot;// 树根
    private SYSRuleTool ruleTool;// 编号规则类别工具
    private TDataStore treeDataStore = new TDataStore();// 树的数据放入datastore用于对树的数据管理
    private String rootText;// 树根节点
    private String rootType;// 树类型
    private String tempPath;
	Window window=null;
    public EMRCdrDetailDataByTreeControl() {
        super();
    }
    
    /**
     * 初始化方法
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
					this.messageBox("页面传参错误");
					return;
				}
			} else {
				this.messageBox("页面传参错误");
				return;
			}
		} else {
			this.messageBox("页面传参错误");
			return;
		}
		// 初始化页面
		this.onInitPage();
    }
    
	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		tableData = getTable("TABLE_DATA");
		tree = (TTree) callMessage("UI|TREE|getThis");
        //给tree添加监听事件  
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        // 设定表格数据
		this.queryTableData();
		// 初始化树  
        this.onInitTree();
        // 初始化结点
        this.onInitNode();

	}
	
    /**
     * 设定表格数据
     */
	private void queryTableData() {
		// 页面标题
		String title = "";
		// 数据表格列标题
		String tableHeader = "";
		// 数据表格列数据
		String tableParmMap = "";
		// 数据表格列对齐方式
		String tableAlignData = "";
		// 数据锁定列
		String lockColumns = "";
		// 数据表格查询结果
		TParm result = new TParm();
		
		if (StringUtils.equals("6", parameterParm.getValue("DATA_TYPE"))) {
			title = "药嘱";
			rootText = "药品分类";
			rootType = "PHA_RULE";
			tableHeader = "连嘱,30;药品通用名,250;商品名,120;单次用量,80;给药频率,80;给药途径,70;总剂量,80;医嘱备注,120;启用时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;停用时间,140,Timestamp,yyyy/MM/dd HH:mm:ss;处方类型,100;剂型,80;药理大分类,120;药理次分类,120;抗菌素用药目的,120;抗菌素分级,100;管制药品分级,80;就诊类型,80;就诊日期,80,Timestamp,yyyy/MM/dd;离院日期,80,Timestamp,yyyy/MM/dd;就诊科室,120";
			tableParmMap = "IVA_LINK_NO;DRUG_DESC;GOODS_DESC;MEDI_QTY;FREQUENCY;ROUTE;DOSAGE_QTY;DR_NOTE;START_DATE;END_DATE;RX_KIND;DOSE_DESC;CATE1_DESC;CATE2_DESC;ANTIBIOTIC_WAY;ANTIBIOTIC_LEVEL;CTRLCLASS_DESC;ADM_TYPE_DESC;ADM_DATE;DISCHARGE_DATE;DEPT_DESC;STATUS";
			tableAlignData = "0,left;1,left;2,left;3,right;4,left;5,left;6,right;7,right;8,left;9,left;10,left;11,left;12,left;13,left;14,left;15,left;16,left;17,left;18,left;19,left;20,left";
			lockColumns = "all";
			// 查询药嘱数据
			result = EMRCdrTool.getInstance().getMedData(parameterParm);
			// 设置页面标题
			this.setTitle(title);
			// 设置表格列
			tableData.setHeader(tableHeader);
			tableData.setParmMap(tableParmMap);
			tableData.setColumnHorizontalAlignmentData(tableAlignData);
			tableData.setLockColumns(lockColumns);
		} else if (StringUtils.equals("7", parameterParm.getValue("DATA_TYPE"))) {
			rootText = "检验";
			rootType = "EXM_RULE";
			// 查询检验数据
			result = EMRCdrTool.getInstance().getLisData(parameterParm);
			// 检验报告勾选监听事件
			tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "eventCheckBox");
		} else if (StringUtils.equals("8", parameterParm.getValue("DATA_TYPE"))) {
			rootText = "检查";
			rootType = "EXM_RULE";
			// 查询检验数据 ;
			result = EMRCdrTool.getInstance().getExmData(parameterParm);
			// 检查调阅影像勾选监听事件
			tableData.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "OpenRisWeb");
		}
		tableData.setParmValue(result);
	}
	
    /**
     * 初始化树
     */
    public void onInitTree() {
        //得到树根
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
        if (treeRoot == null) {
            return;
        }
        //给根节点添加文字显示
        treeRoot.setText(rootText);
        //给根节点赋tag
        treeRoot.setType("Root");
        //设置根节点的id
        treeRoot.setID("");
        //清空所有节点的内容
        treeRoot.removeAllChildren();
        //调用树点初始化方法
        callMessage("UI|TREE|update");
    }

    /**
     * 初始化树的结点
     */
    public void onInitNode() {
    	TComboBox comboBox = new TComboBox();
    	String comboName = "";
    	int count = 0;
    	List<String> comboBoxList = new ArrayList<String>();
    	
    	if (StringUtils.equals("7", parameterParm.getValue("DATA_TYPE"))) {
    		// 检验
    		comboName = "LIS_COMBO";
    	} else if (StringUtils.equals("8", parameterParm.getValue("DATA_TYPE"))) {
    		// 检查
    		comboName = "RIS_COMBO";
    	}
    	
    	if (StringUtils.isNotEmpty(comboName)) {
    		comboBox = ((TComboBox)this.getComponent(comboName));
    		count = comboBox.getItemCount();
    		// 从前台页面上的COMBOBOX取得检验检查的代码，便于动态构建相应的分类项目
        	for (int i = 0; i < count; i++) {
        		comboBoxList.add(comboBox.getItem(i).getID());
        	}
    	}
    	
        //给dataStore赋值
		treeDataStore.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='"+rootType+"'");
        //如果从dataStore中拿到的数据小于0
        if (treeDataStore.retrieve() <= 0)
            return;
        //过滤数据,是编码规则中的科室数据
        ruleTool = new SYSRuleTool(rootType);
        if (ruleTool.isLoad()) { //给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
            TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
                "CATEGORY_CODE",
                "CATEGORY_CHN_DESC", "Path", "SEQ");
            //循环给树安插节点
            for (int i = 0; i < node.length; i++) {
            	// 根据取得的分类代码构建树状结构
            	if (comboBoxList.size() > 0) {
            		if (comboBoxList.contains(node[i].getID())) {
            			treeRoot.addSeq(node[i]);
            		}
            	} else {
            		treeRoot.addSeq(node[i]);
            	}
            }
        }
        //更新树
        tree.update();
        //设置树的默认选中节点
        tree.setSelectNode(treeRoot);
    }
    
    /**
     * 单击树
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
    	TTreeNode node = tree.getSelectNode();
        if (node == null) {
            return;
        }
        //判断点击的是否是树的根结点
        if (node.getType().equals("Root")) {
        	parameterParm.setData("FILTER_DATA", "");
            //如果是树的根接点table上不显示数据
        	tableData.removeRowAll();
        } else { //如果点的不是根结点
            //拿到当前选中的节点的id值
            String id = node.getID();
            parameterParm.setData("FILTER_DATA", id);
        }
        
        // 设定表格数据
		this.queryTableData();
    }
    
    /**
	 * 检验报告内容勾选方法
	 * 
     * @param obj
	 */
	public void eventCheckBox(Object obj) {
		
		
		
		TTable reportTable;
		TTabbedPane tTabledPane=(TTabbedPane) this.getComponent("tTabbedPane_0");
		//切换checkBox 时将tTabledPane置为可编辑
		tTabledPane.setEnabledAt(0,false);
		tTabledPane.setEnabledAt(1,false);
		tTabledPane.setEnabledAt(2,false);
		//切换checkBox 时将tTabledPane中的表格都清空
		reportTable = (TTable) this.getComponent("TABLE1");//一般检验表格
		reportTable.removeRowAll();
		reportTable = (TTable) this.getComponent("TABLE2");//药敏实验表格
		reportTable.removeRowAll();
		reportTable = (TTable) this.getComponent("TABLE3");//细菌培养表格
		reportTable.removeRowAll();
		
		TTable table = (TTable) obj;
		table.acceptText();
		TParm tableParm = table.getParmValue();
		int row = table.getSelectedRow();
		TParm parm = new TParm();
		if(window!=null){//关闭该界面弹出的其他窗口
			window.dispose();
		}
		if ("Y".equals(table.getParmValue().getValue("LIS_WORD", row))) {
			
			for(int i = 0;i<tableParm.getCount();i++){//点击其他checkBox时，将之前的勾置空
				if(tableParm.getValue("LIS_WORD", i).equals("Y")&&i!=row){
					table.setItem(i, "LIS_WORD", "N");
				}
			}
			
			parm.setData("CAT1_TYPE", tableParm.getValue("CAT1_TYPE", row));
			parm.setData("APPLY_NO", tableParm.getValue("APPLY_NO", row));
			//parm.setData("LAB_TYPE", tableParm.getValue("LAB_TYPE", row));
			TParm resultLis=EMRCdrTool.getInstance().getLisData1(parm);//一般检验
			TParm resultAnt=EMRCdrTool.getInstance().getLisAntitest(parm);//药敏实验
			TParm resultLisCulr=EMRCdrTool.getInstance().getLisCulrpt(parm);//细菌培养
			if(resultLis.getCount()>0){//一般检验
				reportTable = (TTable) this.getComponent("TABLE1");
				reportTable.setParmValue(resultLis);
				tTabledPane.setEnabledAt(0, true);
				tTabledPane.setSelectedIndex(0);
			}
			int count = 0;
			if(resultAnt.getCount()>0){//药敏实验
				count++;
				reportTable = (TTable) this.getComponent("TABLE2");
				reportTable.setParmValue(resultAnt);
				tTabledPane.setEnabledAt(1, true);
				tTabledPane.setSelectedIndex(1);
			}
			if(resultLisCulr.getCount()>0){//细菌培养
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
	 * 打开泰心RIS报告(调阅影像)
	 * 
     * @param obj
	 */
	public void OpenRisWeb(Object obj) {
		TFrame tFrame = (TFrame) this.getComponent("UI");//获得页面
		TTable table = (TTable) obj;
		TTable tableParam = (TTable) this.getComponent("TABLE");//生理参数表格
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
					table.setItem(i,"SEEIMAGE","N");//清空其他列
				}
				if(tableParm.getValue("RIS_REPORT", i).equals("Y")){//点击其他checkBox时，将之前的勾置空
					table.setItem(i, "RIS_REPORT", "N");
				}
			}
			if("Y".equals(tableParm.getValue("IS_PACS",row))){//非电生理 调阅影像
				SystemTool.getInstance()
						.OpenRisByMrNoAndApplyNo(parameterParm.getValue("MR_NO"),tableParm.getValue("APPLY_NO",row));
				flg = true;
			}else{//电生理调阅pdf 文件
				parm.setData("CASE_NO",tableParm.getValue("CASE_NO", row));
				parm.setData("OPE_BOOK_NO",tableParm.getValue("APPLY_NO", row));
				TParm pathData = EMRCdrTool.getInstance().getWordPath(parm);
				
				for (int i = 0; i < pathData.getCount(); i++) {
					String fileName = pathData.getValue("FILE_NAME", i) + ".pdf";
					String filePath = pathData.getValue("FILE_PATH", i);
					parm.setData("FILE_NAME", fileName);
					Runtime runtime = Runtime.getRuntime();
					// 调阅分布式存储病历
					TParm fileParm = EMRCdrTool.getInstance().readFile(filePath, fileName);
					byte data[] = (byte[]) fileParm.getData("FILE_DATA");
					if (data == null) {
						messageBox_("服务器上没有找到文件 " + fileParm.getValue("SERVER_PATH") + "\\" + fileName);
						return;
					}
					try {
						FileTool.setByte(tempPath + "\\" + fileName, data);
					} catch (Exception e) {
						e.printStackTrace();
						return;
					}
					try {
						// 打开文件
						runtime.exec("rundll32 url.dll FileProtocolHandler "
								+ tempPath + "\\" + fileName);
						flg = true;
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
				
			}
			if(flg){
				tFrame.setExtendedState(TFrame.ICONIFIED); //窗口最小化
			}
			return;
		}
		
		if(tableParm.getValue("RIS_REPORT", row).equals("N")){//将原有打勾的控件 置空，则清空报告内容
			this.setValue("OUTCOME_DESCRIBE", null);
			this.setValue("OUTCOME_CONCLUSION",null);
			this.setValue("OUTCOME_TYPE", null);
			tableParam.removeRowAll();
			return;
		}
		
		if(column == 8 && "Y".equals(tableParm.getValue("RIS_REPORT", row))){
			for(int i = 0;i<tableParm.getCount();i++){
				if(tableParm.getValue("RIS_REPORT", i).equals("Y")&&i!=row){//点击其他checkBox时，将之前的勾置空
					table.setItem(i, "RIS_REPORT", "N");
				}
				if(tableParm.getValue("SEEIMAGE", i).equals("Y")){
					table.setItem(i,"SEEIMAGE","N");//清空其他列
				}
			}
			this.setValue("OUTCOME_DESCRIBE", tableParm.getValue("OUTCOME_DESCRIBE",row));
			this.setValue("OUTCOME_CONCLUSION", tableParm.getValue("OUTCOME_CONCLUSION",row));
			this.setValue("OUTCOME_TYPE", tableParm.getValue("OUTCOME_TYPE",row));
			parm.setData("APPLICATION_NO",tableParm.getValue("APPLY_NO", row));
			TParm data = EMRCdrTool.getInstance().getPhiscalParam(parm);//获取生理参数的数据
			if(data.getCount()>0){
				tabbedPane.setEnabledAt(1, true);
				tableParam.setParmValue(data);
			}
		}
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
    
}
