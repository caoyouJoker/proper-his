package com.javahis.ui.bil;


import java.sql.Timestamp;
import jdo.bil.BILContractDTool;
import jdo.bil.BILContractMTool;
import jdo.sys.Operator;
import jdo.sys.PATLockTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: 合同单位信息维护
 * </p>
 * 
 * <p>
 * Description: 合同单位信息维护
 * </p>
 * 
 * <p>
 * Copyright: Copyright bluecore
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */
public class BILContractConfigControl extends TControl{


	//action的路径
	private static final String actionName = "action.bil.BILContractAction";
	
    //选项卡控制
	
	// 上次点选的页签索引
	private int lastSelectedIndex = 0;
	private TTabbedPane tTabbedPane_0;
	// 病患对象
	private Pat pat;
	
	/**
	 * 第一个页签的控制  合同单位信息
	 * */
	private TComboBox  REGION_CODE;
	private TTextField CONTRACT_CODE;
	private TTextField CONTRACT_DESC;
	private TTextField TEL1;
	private TTextField CONTACT;
	private TTextField TEL2;
	private TTextField ADDRESS;
	private TTextField DESCRIPTION;
	private TTextField LIMIT_AMT;
	private TTable table1;//合同表
	
	/**
	 * 第二个页签的控制  患者信息
	 * */  

	private TTextField CONTRACT_CODE1;
	private TTextField CONTRACT_DESC1;
	private TTextField MR_NO;
	private TTextField PAT_NAME;
	private TComboBox SEX; 
	private TTextField TEL;
	private TCheckBox FOREIGN_FLG;
	private TTextField IDNO;
	private TTextFormat BIRTH_DATE;
	private TComboBox CTZ1_CODE;
	private TComboBox CTZ2_CODE;
	private TComboBox CTZ3_CODE;
	private TTextField  LIMIT;
    private TTable table2;//员工表
    //复诊病患TCheckBox
    private TCheckBox oldPat;
    
  
    int selectRow = -1;
    public BILContractConfigControl() {

    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        //获得全部控件
        getAllComponent();  
        oldPat.setSelected(true);
        //查询全部信息
        this.onQuery();
        //合同单位信息表
		callFunction("UI|Table1|addEventListener", "Table1->"
				+ TTableEvent.CLICKED, this, "onTableClickedForTable1");
		//患者信息表
		callFunction("UI|Table2|addEventListener", "Table2->"
				+ TTableEvent.CLICKED, this, "onTableClickedForTable2");	
		this.setValue("REGION_CODE", Operator.getRegion());
		
    }
  

    /**
     * 初始化页面
     */
    private void getAllComponent() {
    	/**
		 * 获得选项卡控件
		 */
		this.tTabbedPane_0 = (TTabbedPane) this.getComponent("TablePane");
		/**
		 * 第一个页签
		 * */
		this.REGION_CODE = (TComboBox) this.getComponent("REGION_CODE");
		this.CONTRACT_CODE = (TTextField)this.getComponent("CONTRACT_CODE");
		this.CONTRACT_DESC =  (TTextField)this.getComponent("CONTRACT_DESC");
		this.TEL1 = (TTextField)this.getComponent("TEL1");
		this.CONTACT = (TTextField)this.getComponent("CONTACT");
		this.TEL2 = (TTextField)this.getComponent("TEL2");
		this.ADDRESS =  (TTextField)this.getComponent("ADDRESS");
		this.DESCRIPTION =  (TTextField)this.getComponent("DESCRIPTION");
		this.LIMIT_AMT = (TTextField)this.getComponent("LIMIT_AMT");		
    	this.table1=(TTable) this.getComponent("Table1");
    	/**
    	 * 第二个页签
    	 * */
    	
    	this.CONTRACT_CODE1 = (TTextField)this.getComponent("CONTRACT_CODE1");	
    	this.CONTRACT_DESC1 = (TTextField)this.getComponent("CONTRACT_DESC1");	   
    	this.MR_NO = (TTextField)this.getComponent("MR_NO");	
    	this.PAT_NAME = (TTextField)this.getComponent("PAT_NAME");	
    	this.SEX = (TComboBox)this.getComponent("SEX");	
    	this.TEL = (TTextField)this.getComponent("TEL");	
    	this.FOREIGN_FLG = (TCheckBox)this.getComponent("FOREIGN_FLG");	
        this.IDNO = (TTextField)this.getComponent("IDNO");	
        this.BIRTH_DATE=(TTextFormat)this.getComponent("BIRTH_DATE");	
        this.CTZ1_CODE = (TComboBox)this.getComponent("CTZ1_CODE");	
        this.CTZ2_CODE = (TComboBox)this.getComponent("CTZ2_CODE");	
        this.CTZ3_CODE = (TComboBox)this.getComponent("CTZ3_CODE");	
        this.LIMIT = (TTextField)this.getComponent("LIMIT");	
    	this.table2 = (TTable)this.getComponent("Table2");
    	this.oldPat = (TCheckBox)this.getComponent("FLG");
    	//System.out.println(oldPat.getTag());
    	//给表增加排序功能
//    	addListener(table1);
//    	addListener(table2);
    	
    }
    
    /**
	 * 检测病患相同姓名
	 */
	public void onPatName() {
		String patName = this.getValueString("PAT_NAME");
		if (StringUtil.isNullString(patName)) {
			return;
		}
		String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, B.EKT_CARD_NO "
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE PAT_NAME = '"
				+ patName
				+ "'  "
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
	
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		//setPatName1();
		// 选择病患信息
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("提示信息", "已有相同姓名病患信息,是否继续保存此人信息", 0);
			if (sameCount != 1) {
				this.grabFocus("PY1");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;				
				onMrNo1(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("PY1");
	}

    /**
     * 页面查询方法
     */
    public void onQuery() {
    	// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		//查询全部合同单位信息
    	if(selectedIndex == 0){
    		 setPrimaryKeyEnabled(true);
    	     TParm selectParm = getSelectedCondition();
    	     TParm result = BILContractMTool.getInstance().selectData("selectAll", selectParm);
    	     this.callFunction("UI|Table1|setParmValue", result);	
    	}
    	//查询某个合同单位的全部患者信息
    	if(selectedIndex == 1){   		
    		TParm parm = new TParm();     		
    	    parm.setData("CONTRACT_CODE1", CONTRACT_CODE1.getValue());
    	    putParamLikeWithObjName("MR_NO", parm);
    	    putParamLikeWithObjName("PAT_NAME", parm);
    	    putParamLikeWithObjName("CONTRACT_DESC1", parm);   	    		
    		TParm result = BILContractDTool.getInstance().selectData("selectAllPat", parm);  
    		//System.out.println("result:::"+result);
    		TParm selParm = new TParm();
    		//MR_NO;PAT_NAME;SEX_CODE;IDNO;TEL_HOME;LIMIT;CONTRACT_CODE;CONTRACT_DESC;FOREIGNER_FLG;BIRTH_DATE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE
    		int count = result.getCount();
    		for(int i = 0;i<count ;i++){
    			this.setValue("SEX_CODE",result.getData("SEX_CODE",i));
    		}
    		this.callFunction("UI|Table2|setParmValue", result);
    	}       
    }

    /**
     * 删除方法
     */
    public void onDelete() {
    	// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		//删除合同单位信息
		if(selectedIndex == 0){
			selectRow = this.getSelectedRow("Table1");
	        if (selectRow == -1) {
	            this.messageBox("请选择需要删除的数据");
	            return;
	        }
	        if (this.messageBox("询问", "是否删除", 2) == 0) {
	            TTable table = (TTable)this.getComponent("Table1");
	            int selRow = table.getSelectedRow();
	            TParm tableParm = table.getParmValue();
	            String REGION_CODE = tableParm.getValue("REGION_CODE", selRow);
	            String CONTRACT_CODE = tableParm.getValue("CONTRACT_CODE", selRow);
	            TParm parm = new TParm();
	            parm.setData("REGION_CODE", REGION_CODE);
	            parm.setData("CONTRACT_CODE", CONTRACT_CODE);
	            TParm result = TIOM_AppServer.executeAction(actionName, "onDelAcctionCompany", parm);
	            if (result.getErrCode() < 0) {
	                messageBox(result.getErrText());
	                return;
	            }
	            //删除单行显示
	            int row = (Integer) callFunction("UI|Table1|getSelectedRow");
	            if (row < 0) {
	                return;
	            }
	            this.callFunction("UI|Table1|removeRow", row);
	            this.callFunction("UI|Table1|setSelectRow", row);
	            this.messageBox("P0003");
	        } else {
	            return;
	        }
		}
		//删除某一病人信息
		if(selectedIndex ==1){
			selectRow = this.getSelectedRow("Table2");
	        if (selectRow == -1) {
	            this.messageBox("请选择需要删除的数据");
	            return;
	        }
	        if (this.messageBox("询问", "是否删除", 2) == 0) {
	            TTable table = (TTable)this.getComponent("Table2");
	            int selRow = table.getSelectedRow();
	            TParm tableParm = table.getParmValue();
	            String REGION_CODE = tableParm.getValue("REGION_CODE", selRow);
	            String CONTRACT_CODE = tableParm.getValue("CONTRACT_CODE", selRow);
	            String MR_NO = tableParm.getValue("MR_NO",selRow);
	            TParm parm = new TParm();
	            parm.setData("REGION_CODE", REGION_CODE);
	            parm.setData("CONTRACT_CODE", CONTRACT_CODE);
	            parm.setData("MR_NO",MR_NO);
	            TParm result = TIOM_AppServer.executeAction(actionName, "onDelAcctionPat", parm);
	            if (result.getErrCode() < 0) {
	                messageBox(result.getErrText());
	                return;
	            }
	            //删除单行显示
	            int row = (Integer) callFunction("UI|Table2|getSelectedRow");
	            if (row < 0) {
	                return;
	            }
	            this.callFunction("UI|Table2|removeRow", row);
	            this.callFunction("UI|Table2|setSelectRow", row);
	            this.messageBox("P0003");
	        } else {
	            return;
	        }
		}
        
    }

    /**
     * 清空方法
     */
    public void onClear() {
    	int selectedIndex = tTabbedPane_0.getSelectedIndex();
    	//第一个页签清空
    	if(selectedIndex ==0){
	    	this.setValue("REGION_CODE", "");
			this.setValue("CONTRACT_CODE", "");
			this.setValue("CONTRACT_DESC", "");
			this.setValue("TEL1", "");
			this.setValue("CONTACT", "");
			this.setValue("TEL2", "");
			this.setValue("LIMIT_AMT", "");
			this.setValue("ADDRESS", "");
			this.setValue("DESCRIPTION", "");
			CONTRACT_CODE.setEditable(true);
			table1.removeRowAll();
    	}
    	//第二个页签清空
    	if(selectedIndex == 1){    		
//    		this.setValue("CONTRACT_CODE1", "");
//    		this.setValue("CONTRACT_DESC1", "");
    		this.setValue("MR_NO", "");
    		this.setValue("PAT_NAME", "");
    		this.setValue("SEX", "");
    		this.setValue("TEL", "");
    		this.setValue("FOREIGN_FLG", "N");
    		this.setValue("IDNO", "");
    		this.setValue("BIRTH_DATE", "");
    		this.setValue("CTZ1_CODE", "");
    		this.setValue("CTZ2_CODE", "");
    		this.setValue("CTZ3_CODE", "");
    		this.setValue("LIMIT", "");   
    		MR_NO.setEditable(true);
    		PAT_NAME.setEditable(true);
//    		CONTRACT_CODE1.setEditable(true);
//    		CONTRACT_DESC1.setEditable(true);
    		table2.removeRowAll();
    		
    	}
    }

    /**
     * 保存方法
     */
    public void onSave() {
    	// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		//保存合同单位信息
		if(selectedIndex == 0){
			 //验证数据
	        if (!validBasicData()) {
	            return;
	        }
	       //根据合同名称自动生成拼音
	        String PY1 = TMessage.getPy(getValueString("CONTRACT_DESC"));
	        TParm parm = new TParm();
            parm.setData("PY1", PY1);//4
	        this.putParamWithObjName("CONTRACT_CODE", parm);//1
	        this.putParamWithObjName("REGION_CODE", parm);//19
	        this.putParamWithObjName("CONTRACT_DESC", parm);//2
	        this.putParamWithObjName("TEL1", parm);//10
	        this.putParamWithObjName("CONTACT", parm);//12
	        this.putParamWithObjName("TEL2", parm);//11
	        this.putParamWithObjName("ADDRESS", parm);//9
	        this.putParamWithObjName("DESCRIPTION", parm);//7
	        this.putParamWithObjName("LIMIT_AMT", parm);//13
	        this.putBasicSysInfoIntoParm(parm);//16,17,18
	        parm.setData("CONTRACT_ABS_DESC","");//3
	        parm.setData("PY2","");//5
	        parm.setData("DEL_FLG","N");//15
	        parm.setData("PREPAY_AMT","");//14
	        parm.setData("POST_CODE","");//8
	        //判断数据是否存在
			TParm result = BILContractMTool.getInstance().selectData("checkDataExist", parm);
			//System.out.println(result.getCount());		
	        boolean isdataExist = Integer.parseInt(result.getValue("DATACOUNT", 0)) >
	                              0 ? true : false;
	        TParm resultopt = null;
	        if (isdataExist) {
	        	//存在则更新
	        	resultopt = TIOM_AppServer.executeAction(actionName, "onUpdAcctionCompany", parm);
	             if (resultopt.getErrCode() >= 0) {
	                this.messageBox("P0001");
	            } else {
	                this.messageBox("E0001");
	            }
	        } else {
	        	//不存在则插入
	        	//查询最大的seq
	        	String seq_max = "SELECT MAX(SEQ) AS SEQ  FROM BIL_CONTRACTM WHERE DEL_FLG = 'N' ";
	        	TParm seqParm = new TParm(TJDODBTool.getInstance().select(seq_max));
	        	int seq = 0;
	        	if(seqParm.getCount()<=0){
	        		seq = 1;
	        	}else{
	        		seq = seqParm.getInt("SEQ",0) + 1;
	        	}
	        	parm.setData("SEQ",seq);
	        	resultopt = TIOM_AppServer.executeAction(actionName, "onSaveAcctionCompany", parm);
	            if (resultopt.getErrCode() >= 0) {
	                this.messageBox("P0002");
	            } else {
	                this.messageBox("E0002");
	            }
	        }
	        this.onQuery();
		}
		//保存病人信息
		if(selectedIndex == 1){
			//1.验证数据的合法性
			//2.组合数据
			TParm parm = new TParm();
			parm.setData("CONTRACT_CODE",CONTRACT_CODE.getValue());
			parm.setData("MR_NO",MR_NO.getValue());
			//判断数据是否存在
			TParm result = BILContractDTool.getInstance().selectData("checkPatDataExist", parm);
			//System.out.println(result.getCount());		
	        boolean isdataExist = Integer.parseInt(result.getValue("DATACOUNT", 0)) >
	                              0 ? true : false;
	        TParm resultopt = null;
	        //CONTRACT_CODE,MR_NO,LIMIT_AMT,PREPAY_AMT,DEL_FLG, COMPANY_FLG,PAY_FLG,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE
	        parm.setData("CONTRACT_CODE",CONTRACT_CODE.getValue());
	        parm.setData("MR_NO",MR_NO.getValue());
	        parm.setData("LIMIT_AMT",LIMIT.getValue());
	        parm.setData("PREPAY_AMT","");
	        parm.setData("DEL_FLG","N");
	        parm.setData("COMPANY_FLG","1");
	        parm.setData("PAY_FLG","1");
	        parm.setData("REGION_CODE",REGION_CODE.getValue());
	        this.putBasicSysInfoIntoParm(parm);
	        if (isdataExist) {
	        	//存在则更新
	        	resultopt = TIOM_AppServer.executeAction(actionName, "onUpdAcctionPat", parm);
	             if (resultopt.getErrCode() >= 0) {
	                this.messageBox("P0001");
	            } else {
	                this.messageBox("E0001");
	            }
	        } else {
	        	//不存在则插入
	        	resultopt = TIOM_AppServer.executeAction(actionName, "onSaveAcctionPat", parm);
	            if (resultopt.getErrCode() >= 0) {
	                this.messageBox("P0002");
	            } else {
	                this.messageBox("E0002");
	            }
	        }
	        this.onQueryPat();
		}
       
    }
    /*
     *病案号回车查询 
     */
    public void onMrNo(){
    	String mr_no = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO")));
    	String sql = "SELECT MR_NO,PAT_NAME,FOREIGNER_FLG,IDNO,BIRTH_DATE," +
    			" CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,TEL_HOME,SEX_CODE,ADDRESS " +
    			" FROM SYS_PATINFO WHERE MR_NO = '"+mr_no+"'";
    	TParm selParm  = new TParm(TJDODBTool.getInstance().select(sql));
    	if(selParm.getCount()<=0){
    		this.messageBox("此病人不存在！");
    		return;
    	}
    	this.setValue("MR_NO", selParm.getData("MR_NO",0));    		
		this.setValue("PAT_NAME", selParm.getData("PAT_NAME",0));
		this.setValue("SEX", selParm.getData("SEX_CODE",0));
		this.setValue("TEL", selParm.getData("TEL_HOME",0));
		this.setValue("FOREIGN_FLG", selParm.getData("FOREIGNER_FLG",0));
		this.setValue("IDNO", selParm.getData("IDNO",0));
		this.setValue("BIRTH_DATE", selParm.getData("BIRTH_DATE",0));
		this.setValue("CTZ1_CODE", selParm.getData("CTZ1_CODE",0));
		this.setValue("CTZ2_CODE", selParm.getData("CTZ2_CODE",0));
		this.setValue("CTZ3_CODE", selParm.getData("CTZ3_CODE",0));
		//设置控件为不可编辑
		MR_NO.setEditable(false);
		PAT_NAME.setEditable(false);
		//查询患者的信誉额度 
		String sqls = "SELECT LIMIT_AMT" +
						" FROM BIL_CONTRACTD " +
						" WHERE MR_NO = '"+mr_no+"' " +
						" AND CONTRACT_CODE = '"+this.getValueString("CONTRACT_CODE1")+"'";
		TParm selParms = new TParm(TJDODBTool.getInstance().select(sqls));		
		if(selParms.getCount()<= 0){
			this.setValue("LIMIT", ""); 
		}
		this.setValue("LIMIT", selParms.getValue("LIMIT_AMT",0));  
		this.onQuery();
    }
    
    /*
     *根据病案号查询
     */
    public void onMrNo1(String mr_no){
    	
    	String sql = "SELECT MR_NO,PAT_NAME,FOREIGNER_FLG,IDNO,BIRTH_DATE," +
    			" CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,TEL_HOME,SEX_CODE,ADDRESS " +
    			" FROM SYS_PATINFO WHERE MR_NO = '"+mr_no+"'";
    	TParm selParm  = new TParm(TJDODBTool.getInstance().select(sql));
    	if(selParm.getCount()<=0){
    		this.messageBox("此病人不存在！");
    		return;
    	}
    	this.setValue("MR_NO", selParm.getData("MR_NO",0));    		
		this.setValue("PAT_NAME", selParm.getData("PAT_NAME",0));
		this.setValue("SEX", selParm.getData("SEX_CODE",0));
		this.setValue("TEL", selParm.getData("TEL_HOME",0));
		this.setValue("FOREIGN_FLG", selParm.getData("FOREIGNER_FLG",0));
		this.setValue("IDNO", selParm.getData("IDNO",0));
		this.setValue("BIRTH_DATE", selParm.getData("BIRTH_DATE",0));
		this.setValue("CTZ1_CODE", selParm.getData("CTZ1_CODE",0));
		this.setValue("CTZ2_CODE", selParm.getData("CTZ2_CODE",0));
		this.setValue("CTZ3_CODE", selParm.getData("CTZ3_CODE",0));
		//设置控件为不可编辑
		MR_NO.setEditable(false);
		PAT_NAME.setEditable(false);
		//查询患者的信誉额度 
		String sqls = "SELECT LIMIT_AMT" +
						" FROM BIL_CONTRACTD " +
						" WHERE MR_NO = '"+mr_no+"' " +
						" AND CONTRACT_CODE = '"+this.getValueString("CONTRACT_CODE1")+"'";
		TParm selParms = new TParm(TJDODBTool.getInstance().select(sqls));		
		if(selParms.getCount()<= 0){
			this.setValue("LIMIT", ""); 
		}
		this.setValue("LIMIT", selParms.getValue("LIMIT_AMT",0));    
    }

    /**
	 * 当选项卡切换时 调用此方法
	 */
	public void onChange() {
		// 获取页签索引
		int selectedIndex = tTabbedPane_0.getSelectedIndex();
		// 查询菜单控制
		//menuControl();
		// 若当前点选的页签索引为0（合同单位信息），则不作任何处理直接返回
		if (selectedIndex <= 0) {
			//区域不可编辑
			this.REGION_CODE.setEditable(false);
			// 记录此次点选的页签索引
			lastSelectedIndex = selectedIndex;
			return;
		}
		// 若当前点选的页签索引为1（患者信息），则验证是否选择了合同单位
		if (selectedIndex == 1) {
			if (table1.getSelectedRow() < 0) {
				this.messageBox("请选择合同单位信息！");
				tTabbedPane_0.setSelectedIndex(0);
				return;
			}			
			//清空以前信息并重新查询时程信息
			if (lastSelectedIndex == 0) {
				// 清空以前信息
				onClear();
				// 模糊查询患者
				onQueryPat();
				//初始化部分控件
				CONTRACT_CODE1.setEditable(false);
				CONTRACT_DESC1.setEditable(false); 
				CONTRACT_CODE1.setValue(CONTRACT_CODE.getValue());
				CONTRACT_DESC1.setValue(CONTRACT_DESC.getValue());	
			}
		}
		
	}
	/**
	 * 模糊查询患者
	 * */
	public void onQueryPat(){
		//模糊查询参数
		TParm parm = new TParm();
		parm.setData("CONTRACT_CODE1",CONTRACT_CODE.getValue());			
		TParm result = BILContractDTool.getInstance().selectData("selectAllPat", parm);
		this.callFunction("UI|Table2|setParmValue", result);	
	}
    /**
	 * 将null转换为空字符串
	 */
	public String nullToString(String str) {
		if (str == null || "null".equals(str)) {
			return "";
		} else {
			return str.trim();
		}
	}
    /**
     * 监听合同单位信息表
     * */
	public void onTableClickedForTable1(int row) {
		if (row < 0) {
			return;
		}
		// 获取正在编辑状态的数据
		table1.acceptText();
		// 合同代码不可编辑
		CONTRACT_CODE.setEditable(false);
		
		/*
		 * 获取选中的数据，将这些数据设置到各个输入控件中
		 */
		REGION_CODE.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("REGION_CODE", row))));
		CONTRACT_CODE.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("CONTRACT_CODE", row))));
		CONTRACT_DESC.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("CONTRACT_DESC", row))));
		TEL2.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("TEL2", row))));
		ADDRESS.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("ADDRESS", row))));
		DESCRIPTION.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("DESCRIPTION", row))));
		LIMIT_AMT.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("LIMIT_AMT", row))));
		TEL1.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("TEL1", row))));
		CONTACT.setValue(nullToString(String.valueOf(table1
				.getParmValue().getData("CONTACT", row))));
		
	}
	 /**
     * 监听病患信息表
     * */
	public void onTableClickedForTable2(int row) {
		if (row < 0) {
			return;
		}
		// 获取正在编辑状态的数据
		table2.acceptText();
		// 合同代码不可编辑
		CONTRACT_CODE1.setEditable(false);		
		//合同单位名称不可编辑		
		CONTRACT_DESC1.setEditable(false);
		//病案号不可编辑
		MR_NO.setEditable(false);
//		//姓名不可编辑
//		PAT_NAME.setEditable(false);
				
		//获取选中的数据，并将数据显示在控件中
		CONTRACT_CODE1.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("CONTRACT_CODE", row))));
		CONTRACT_DESC1.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("CONTRACT_DESC", row))));
		MR_NO.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("MR_NO", row))));
		PAT_NAME.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("PAT_NAME", row))));
		SEX.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("SEX_CODE", row))));
		TEL.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("TEL_HOME", row))));
		this.setValue("FOREIGN_FLG",table2
				.getParmValue().getBoolean("FOREIGNER_FLG", row));
		IDNO.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("IDNO", row))));		
		CTZ1_CODE.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("CTZ1_CODE", row))));
		CTZ2_CODE.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("CTZ2_CODE", row))));
		CTZ3_CODE.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("CTZ3_CODE", row))));
		LIMIT.setValue(nullToString(String.valueOf(table2
				.getParmValue().getData("LIMIT", row))));
		this.setValue("BIRTH_DATE",table2
				.getParmValue().getTimestamp("BIRTH_DATE", row));
		
	}
    /**
     * 验证信息
     * @return TParm
     */
    private boolean validBasicData() {    	
        //合同单位描述不能为空
        if (!this.emptyTextCheck("CONTRACT_DESC")) {
        	this.messageBox("合同单位描述不能为空！");
            return false;
        }
        //区域不能为空
        if (!this.emptyTextCheck("REGION_CODE")) {
        	this.messageBox("区域不能为空！");
            return false;
        }  
        //合同单位编号不能为空
        if(!this.emptyTextCheck("CONTRACT_CODE")){
        	this.messageBox("合同单位编码不能为空！");
        	return false;
        }
        
        return true;
    }

    /**
     * 设置控件是否可用方法
     * @param flag boolean
     */
    private void setPrimaryKeyEnabled(boolean flag) {
        TTextField tTextField = (TTextField)this.getComponent("CONTRACT_CODE");
        tTextField.setEnabled(flag);
    }

    /**
     * 从页面得到查询条件方法
     */
    private TParm getSelectedCondition() {
        TParm selectedCondition = new TParm();
        putParamLikeWithObjName("CONTRACT_CODE", selectedCondition);
        putParamLikeWithObjName("CONTRACT_DESC", selectedCondition);
        selectedCondition.setData("REGION_CODE", Operator.getRegion());
        return selectedCondition;
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
     * 将控件值放入TParam方法(放置参数值与控件名相同)
     * @param objName String
     * @param parm TParm
     */
    private void putParamLikeWithObjName(String objName, TParm parm) {
        String objstr = this.getValueString(objName);
        if (objstr != null && objstr.length() > 0) {
            objstr = "%" + objstr.trim() + "%";
            //参数值与控件名相同
            parm.setData(objName, objstr);
        }
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
     * 向TParm中加入系统默认信息
     * @param parm TParm
     */
    private void putBasicSysInfoIntoParm(TParm parm) {
      //  int total = parm.getCount();
        parm.setData("OPT_USER", Operator.getID());
        Timestamp today = SystemTool.getInstance().getDate();
        String datestr = StringTool.getString(today, "yyyyMMdd");
        parm.setData("OPT_DATE", datestr);
        parm.setData("OPT_TERM", Operator.getIP());
    }
	//===============================
    /**
	 * 保存病患信息
	 */
	public void onSavePat() {
		if (pat != null)
			PatTool.getInstance().unLockPat(pat.getMrNo());
		// 不能输入空值
		if (getValue("BIRTH_DATE") == null) {
			this.messageBox("出生日期不能为空!");
			return;
		}
		if (!this.emptyTextCheck("PAT_NAME,SEX,CTZ1_CODE"))
			return;
		pat = new Pat();
		// 病患姓名
		pat.setName(TypeTool.getString(getValue("PAT_NAME")));
		// 英文名
		String PAT_NAME1 = SYSHzpyTool.getInstance().charToAllPy(
				TypeTool.getString(getValue("PAT_NAME")));
		pat.setName1(PAT_NAME1);
		// 姓名拼音
		String PY1 = SYSHzpyTool.getInstance().charToCode(
				TypeTool.getString(getValue("PAT_NAME")));
		pat.setPy1(PY1);
		// 身份证号
		pat.setIdNo(TypeTool.getString(getValue("IDNO")));
		// 外国人注记
		pat.setForeignerFlg(TypeTool.getBoolean(getValue("FOREIGN_FLG")));
		// 出生日期
		pat.setBirthday(TypeTool.getTimestamp(getValue("BIRTH_DATE")));
		// 性别
		pat.setSexCode(TypeTool.getString(getValue("SEX")));
		// 电话
		pat.setTelHome(TypeTool.getString(getValue("TEL")));
		// 邮编
		pat.setPostCode("");
		// 地址
		pat.setAddress("");
		// 身份1
		pat.setCtz1Code(TypeTool.getString(getValue("CTZ1_CODE")));
		// 身份2
		pat.setCtz2Code(TypeTool.getString(getValue("CTZ2_CODE")));
		// 身份3
		pat.setCtz3Code(TypeTool.getString(getValue("CTZ3_CODE")));
		// 医保卡市民卡
		pat.setNhiNo(""); // =============pangben		
		if (this.messageBox("病患信息", "是否保存", 0) != 0)
			return;
		TParm patParm = new TParm();
		patParm.setData("MR_NO", getValue("MR_NO"));
		patParm.setData("PAT_NAME", getValue("PAT_NAME"));
		patParm.setData("PAT_NAME1", PAT_NAME1);
		patParm.setData("PY1", PY1);
		patParm.setData("IDNO", getValue("IDNO"));
		patParm.setData("BIRTH_DATE", getValue("BIRTH_DATE"));
		patParm.setData("TEL_HOME", getValue("TEL"));
		patParm.setData("SEX_CODE", getValue("SEX"));
		patParm.setData("POST_CODE","123456");
		patParm.setData("ADDRESS", "1111111");
		patParm.setData("CTZ1_CODE", getValue("CTZ1_CODE"));
		patParm.setData("CTZ2_CODE", getValue("CTZ2_CODE"));
		patParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		patParm.setData("NHI_NO","1111");
		
		if (StringUtil.isNullString(getValue("MR_NO").toString())) {
			patParm.setData("MR_NO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PAT_NAME").toString())) {
			patParm.setData("PAT_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(PAT_NAME1)) {
			patParm.setData("PAT_NAME1", new TNull(String.class));
		}
		if (StringUtil.isNullString(PY1)) {
			patParm.setData("PY1", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("IDNO").toString())) {
			patParm.setData("IDNO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("BIRTH_DATE").toString())) {
			patParm.setData("BIRTH_DATE", new TNull(Timestamp.class));
		}
		if (StringUtil.isNullString(getValue("TEL").toString())) {
			patParm.setData("TEL_HOME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("SEX").toString())) {
			patParm.setData("SEX_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString("")) {
			patParm.setData("POST_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString("")) {
			patParm.setData("ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ1_CODE").toString())) {
			patParm.setData("CTZ1_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ2_CODE").toString())) {
			patParm.setData("CTZ2_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ3_CODE").toString())) {
			patParm.setData("CTZ3_CODE", new TNull(String.class));
		}
		
		if (StringUtil.isNullString("")) {
			patParm.setData("NHI_NO", new TNull(String.class));
		}
		TParm result = new TParm();
		
		if (!"".equals(getValueString("MR_NO"))) {
			
			if (getValue("MR_NO").toString().length() == 0) {
				this.messageBox("请先检索出病患");
				return;
			}
			// 更新病患
			result = PatTool.getInstance().upDateForReg(patParm);
			setValue("MR_NO", getValue("MR_NO"));
			pat.setMrNo(getValue("MR_NO").toString());
		} else {
			// 新增病患
			
			pat.onNew();
			setValue("MR_NO", pat.getMrNo());
		}
		if (result.getErrCode() != 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
		}
	
		
	
	}
	/**
     * 复诊CHECK_BOX
     */
    public void isOldPat() {
        // 选中状态
        if (oldPat.isSelected()) {
        	MR_NO.setValue("");
            this.callFunction("UI|MR_NO|grabFocus");
            MR_NO.setEnabled(true);
        }
        else {
        	MR_NO.setEnabled(false);
        }
    }
    
}
