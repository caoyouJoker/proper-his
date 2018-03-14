/**
 *
 */
package com.javahis.ui.sys;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import jdo.bil.BILComparator;
import jdo.sys.Operator;
import jdo.sys.SYSOperationicdTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.ui.base.TTableCellEditor;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TRadioButton;
import com.javahis.util.StringUtil;

/**
 *
 * <p>Title: 诊断码</p>
 *
 * <p>Description:诊断码 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author ehui 200800901
 * @version 1.0
 */
public class SYSDiagnosisControl
    extends TControl {
	
	//$$=============add by wangjc 20160519 加入排序功能start==================$$//
		private BILComparator compare = new BILComparator();//modify by wanglong 20121128
		private boolean ascending = false;
		private int sortColumn = -1;
	    //$$=============add by wangjc 20160519 加入排序功能end==================$$//
	
    TParm data;
    int selectRow = -1;
    TDataStore allTds, ccmd;
    TCheckBox syndromeFlg;
    TTable table;
    TTextField text;
    String oldText = "";
    String icdType;
    String icdCode;
	TTable table1;
	TTextFormat tf;
    public final static String INIT_QUERY = "SELECT ICD_TYPE, ICD_CODE, "
        + " ICD_CHN_DESC, ICD_ENG_DESC, PY1, PY2, SEQ, "
        + " DESCRIPTION, SYNDROME_FLG, MDC_CODE, CCMD_CODE, "
        + " MAIN_DIAG_FLG, CAT_FLG, STANDARD_DAYS, CHLR_FLG, "
        + " DISEASETYPE_CODE, MR_CODE, CHRONIC_FLG, START_AGE, "
        + " LIMIT_DEPT_CODE, LIMIT_SEX_CODE, END_AGE, "
        + " AVERAGE_FEE, OPT_USER, OPT_DATE, OPT_TERM,STA1_CODE "
        + " ,STA1_DESC "//add by wanglong 20131025
        + " FROM SYS_DIAGNOSIS "
        + " WHERE ICD_CODE LIKE 'A%' AND ICD_TYPE='W' ORDER BY SEQ,ICD_CODE";
    public final static String ICD_QUERY = "SELECT ICD_TYPE, ICD_CODE, "
        + " ICD_CHN_DESC, ICD_ENG_DESC, PY1, PY2, SEQ, "
        + " DESCRIPTION, SYNDROME_FLG, MDC_CODE, CCMD_CODE, "
        + " MAIN_DIAG_FLG, CAT_FLG, STANDARD_DAYS, CHLR_FLG, "
        + " DISEASETYPE_CODE, MR_CODE, CHRONIC_FLG, START_AGE, "
        + " LIMIT_DEPT_CODE, LIMIT_SEX_CODE, END_AGE, "
        + " AVERAGE_FEE, OPT_USER, OPT_DATE, OPT_TERM,STA1_CODE"
        + " ,STA1_DESC "//add by wanglong 20131025
        + " FROM SYS_DIAGNOSIS ";
    public final static String CCMD_QUERY =
        " SELECT GROUP_ID, ID, CHN_DESC, ENG_DESC, PY1,"
        + "PY2, SEQ, DESCRIPTION, TYPE, PARENT_ID,"
        + "STATE, DATA, STA1_CODE, STA2_CODE, STA3_CODE,"
        + "OPT_USER, OPT_DATE, OPT_TERM "
        + " FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_CCMD'";
    public final static String TAG = "ICD_CODE;SYNDROME_FLG;SEQ;ICD_CHN_DESC;PY1;PY2;ICD_ENG_DESC;DESCRIPTION;MDC_CODE;LIMIT_SEX_CODE;START_AGE;END_AGE;LIMIT_DEPT_CODE;STANDARD_DAYS;AVERAGE_FEE;MAIN_DIAG_FLG;CAT_FLG;CHRONIC_FLG;CHLR_FLG;DISEASETYPE_CODE"
        + ";STA1_CODE;STA1_DESC";//modify by wanglong 20131025
    
    @Override
    public void onInit() {
        super.onInit();
        table1=(TTable) this.getComponent("TABLE1");
        init();
        //监听table1
        callFunction("UI|TABLE1|addEventListener", "TABLE1->"
				+ TTableEvent.CHANGE_VALUE, this, "onTable1ChangeValue");
        
        tf = (TTextFormat) table1.getItem("TAG_CODE");
        //TPanel tp = tf.getPanelPopupMenu();
		tf.setEditValueAction("selectEvent");
//		System.out.println("加入排序监听1");
		this.addListener(this.getTTable("TABLE"));//20160519 wangjc add
//		System.out.println("加入排序监听2");
    }
    
    /**
     * 监听Table1
     */
    public void onTable1ChangeValue(TTableNode tNode){
    	table1.acceptText();
    	TParm parm=table1.getParmValue();
    	int row = tNode.getRow();
    	String columnName = table1.getDataStoreColumnName(tNode.getColumn());
    	
    	if("TAG_CODE".equals(columnName)){
    		if(isExist((String)tNode.getValue(),row)){
    			this.messageBox("不可重复选择");
    			//this.messageBox(""+tNode.getOldValue());
    			if(tNode.getOldValue()==null)
    				tNode.setValue("");
    			else
    				tNode.setValue(tNode.getOldValue());
    			return;
    		}
    	}
    	if("TAG_CODE".equals(columnName)&&row==parm.getCount()-1){
    		String oldValue=(String) tNode.getOldValue();
    		String value=(String) tNode.getValue();
    		//this.messageBox("oldValue::"+oldValue+"::::value:::"+value);
    		if(!oldValue.equals(value)&&!"".equals(value)){
	    		int row1=table1.addRow();
	    		parm.setData("ICD_CODE",row1,icdCode);
	    		parm.setData("TAG_CODE",row1,"");
	    		parm.setCount(parm.getCount("ICD_CODE"));
	    		table1.setParmValue(parm);
    		}
    	}
    }
    /**
     * 判断表格中的数据是否重复
     * @param tagCode
     * @param row
     * @return
     */
    public boolean isExist(String tagCode,int row){
    	boolean flg=false;
    	TParm parm=table1.getParmValue();
    	for(int i=0;i<parm.getCount();i++){
    		//this.messageBox("i:"+i+":row:"+row+":::tagCode:::"+tagCode+":parmtag_code:"+parm.getValue("TAG_CODE",i));
    		if(i!=row && tagCode.equals(parm.getValue("TAG_CODE",i))){
    			flg=true;
    			break;
    		}
    	}
    	return flg;
    }
    
    public void onTableClick() {
        int row = table.getSelectedRow();
        selectRow = row;
        //20160519 wangjc modify
        TParm parm = table.getParmValue().getRow(row);
//        TParm parm = allTds.getBuffer(allTds.PRIMARY);
        //System.out.println("parm" + parm);
//        this.setValue(TAG, parm);
        String[] tags = TAG.split(";");
        for(String tag : tags){
        	this.setValue(tag, parm.getValue(tag));
        }
//        setValueForParm(
//            TAG,
//            parm, row);
        if (StringTool.getBoolean(this.getValueString("MAIN_DIAG_FLG"))) {
            this.callFunction("UI|CAT_FLG|setEnabled", false);
        }
        else {
            this.callFunction("UI|CAT_FLG|setEnabled", true);
        }
        icdCode=this.getValueString("ICD_CODE");
        onViewTable1(icdCode);
    }
    
    /**
     * 显示table1
     * @param icdCode
     */
    public void onViewTable1(String icdCode){
    	if("".equals(icdCode))
    		return;
    	String sql="SELECT ICD_CODE,TAG_CODE FROM SYS_OPERATIONICD_TAGS WHERE ICD_CODE='"+icdCode+"'";
    	TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
    	table1=(TTable) this.getComponent("TABLE1");
    	if(parm.getCount()>0){
    		parm.setData("ICD_CODE",parm.getCount(),icdCode);
    		parm.setData("TAG_CODE",0,parm.getValue("TAG_CODE",0));
    		parm.setCount(parm.getCount("ICD_CODE"));
    	}else{
    		parm.setData("ICD_CODE",0,icdCode);
    		parm.setData("TAG_CODE",0,"");
    		parm.setCount(1);
    	}
    	
    	table1.setParmValue(parm);
    }

    /**
     * ICD代码回车事件
     */
    public void operationIcdEvent(){
    	icdCode=this.getValueString("ICD_CODE");
    	onViewTable1(icdCode);
    	
    }
    /**
     *清空
     */
    public void onClear() {

        this.clearValue(TAG);
        this.setValue("LIMIT_SEX_CODE", "1");
        this.setValue("W", "Y");
        syndromeFlg.setEnabled(false);
        icdType = "W";
        table = (TTable)this.getComponent("TABLE");
        table.removeRowAll();
        //20160519 wangjc modify
//        allTds = table.getDataStore();
//        allTds.setSQL(INIT_QUERY);
//        allTds.retrieve();
//        if (allTds == null) {
//            //System.out.println("allTds is null");
//        }
//        table.setDSValue();
        
        onQuery();
        
        //this.messageBox("");
        selectRow=-1;//add by huangjw 20150421
        table1=(TTable) this.getComponent("TABLE1");
        table1.setParmValue(new TParm());

    }
    /**
	 * 插入table1的数据
	 */
	public void onInsertTable1(){
		table1=(TTable) this.getComponent("TABLE1");
		TParm data=table1.getParmValue();
		if(data.getCount()<=1)
			return;
		TParm result=new TParm();
		SystemTool st = new SystemTool();
		for(int i=0;i<data.getCount()-1;i++){
			result.addData("ID", getUid());
			result.addData("ICD_CODE", icdCode);
			result.addData("TAG_CODE", data.getValue("TAG_CODE",i));
			result.addData("OPT_USER", Operator.getID());
			result.addData("OPT_DATE", st.getDate());
			result.addData("OPT_TERM", Operator.getIP());
		}
		result.setCount(data.getCount()-1);
		result=SYSOperationicdTool.getInstance().insertTable1Data(result);
		if(result.getErrCode()<0){
			this.messageBox(result.getErrText());
			return;
		}
	}
	
	/**
	 * 获得主键值
	 */
	public String getUid(){
		String sql="SELECT SYS_GUID() ID FROM DUAL";
		TParm parm=new TParm(TJDODBTool.getInstance().select(sql));
		return parm.getValue("ID",0);
	}
    /**
     * 初始化界面，查询所有的数据
     *
     * @return TParm
     */
    public void init() {
        ccmd = new TDataStore();
        ccmd.setSQL(CCMD_QUERY);
        ccmd.retrieve();
        syndromeFlg = (TCheckBox)this.getComponent("SYNDROME_FLG");
        onClear();
        
    }

    /**
     * 西医诊断
     */
    public void onW() {
        TComboBox mdcCode = (TComboBox)this.getComponent("MDC_CODE");
        TCheckBox synd = (TCheckBox)this.getComponent("SYNDROME_FLG");
        synd.setSelected(false);
        synd.setEnabled(false);
        mdcCode.setValue("");
        mdcCode.setEnabled(true);
//      this.callFunction("UI|SYNDROME_FLG|setEnabled", false);
//      //MDC_CODE中医不使用
//      this.callFunction("UI|MDC_CODE|setEnabled", true);
        icdType = "W";
//      StringBuffer sb = new StringBuffer(ICD_QUERY);
//      sb.append("WHERE ICD_TYPE='" + icdType + "'");
//      allTds.setSQL(sb.toString());
//      allTds.retrieve();
//      table.setDSValue();
    }

    /**
     * 中医诊断
     */
    public void onC() {
        TComboBox mdcCode = (TComboBox)this.getComponent("MDC_CODE");
        TCheckBox synd = (TCheckBox)this.getComponent("SYNDROME_FLG");
        synd.setSelected(false);
        synd.setEnabled(true);
        mdcCode.setValue("");
        mdcCode.setEnabled(false);
//      this.callFunction("UI|SYNDROME_FLG|setEnabled", true);
//      this.callFunction("UI|MDC_CODE|setEnabled", false);
        icdType = "C";
//      StringBuffer sb = new StringBuffer(ICD_QUERY);
//      sb.append("WHERE ICD_TYPE='" + icdType + "'");
//      allTds.setSQL(sb.toString());
//      allTds.retrieve();
//      table.setDSValue();
    }

    /**
     * ICD_CODE栏回车时的查询事件
     */
    public void onQueryIcd() {
        StringBuffer sb = new StringBuffer(ICD_QUERY);
        String code = this.getValueString("ICD_CODE").toUpperCase();
        //20160118 wangjc 增加中文名称与拼音查询条件  start
        String icdChnDesc = this.getValueString("ICD_CHN_DESC");
        String py1 = this.getValueString("PY1").toUpperCase();
        sb.append("WHERE ICD_TYPE='").append(icdType).append("' ");
        if(!code.equals("")){
        	sb.append(" AND ICD_CODE LIKE '%").append(code).append("%' ");
        }
        if(!icdChnDesc.equals("")){
        	sb.append(" AND ICD_CHN_DESC LIKE '%").append(icdChnDesc).append("%' ");
        }
        if(!py1.equals("")){
        	sb.append(" AND PY1 LIKE '%").append(py1).append("%' ");
        }
        sb.append(" ORDER BY SEQ,ICD_CODE");
        //20160118 wangjc 增加中文名称与拼音查询条件  end
        
//        sb.append("WHERE (ICD_CODE LIKE '%").append(code).append(
//            "%' OR ICD_ENG_DESC LIKE '%").append(code).append(
//                "%' OR ICD_CHN_DESC LIKE '%").append(code).append(
//                    "%' OR PY1 LIKE '%").append(code).append(
//            "%')  AND ICD_TYPE='").append(icdType).append(
//                "' ORDER BY SEQ,ICD_CODE");
//        System.out.println(sb.toString());
        //20160519 wangjc modify
        TParm result = new TParm(TJDODBTool.getInstance().select(sb.toString()));
        table.setParmValue(result);
//        allTds.setSQL(sb.toString());
//        allTds.retrieve();
//        table.setDSValue();
    }

    /**
     * 查询
     */
    public void onQuery() {
    	operationIcdEvent();
        onQueryIcd();
    }

    /**
     * CCMD combol点击时将汉字带入到文本框中
     */
    public void onClickCCMD() {
        /*序号,60;疾病代码,120;中文名称,160;拼音,80;注记码,80;英文名称,160;诊断类别,80,ICD_TYPE;
         * 主诊断,100,boolean;MDC,180,MDC_CODE;
         * CCMD代码,180,CCMD_CODE;操作人员,120;操作日期,120
         * 1,right;2,left;3,left;4,left;5,left;6,left;8,left;9,left;10,left;11,right
         */
        String ccmdCode = this.getValueString("CCMD_CODE").toUpperCase();
        ccmd.setFilter("ID='" + ccmdCode + "'");
        ccmd.filter();
        this.setValue("CCMD_DESC", ccmd.getItemData(0, "CHN_DESC"));
        StringBuffer sb = new StringBuffer(ICD_QUERY);
        sb.append("WHERE CCMD_CODE='" + ccmdCode + "'");
        //20160519 wangjc modify
        TParm result = new TParm(TJDODBTool.getInstance().select(sb.toString()));
        table.setParmValue(result);
//        allTds.setSQL(sb.toString());
//        allTds.retrieve();
//        table.setDSValue();
        
    }

    /**
     * mdc Combo改变时的查询事件
     */
    public void onClickMdc() {
        String mdcCode = this.getValueString("MDC_CODE");
        StringBuffer sb = new StringBuffer(ICD_QUERY);
        sb.append("WHERE MDC_CODE='" + mdcCode + "'");
      //20160519 wangjc modify
        TParm result = new TParm(TJDODBTool.getInstance().select(sb.toString()));
        table.setParmValue(result);
//        allTds.setSQL(sb.toString());
//        allTds.retrieve();
//        table.setDSValue();
    }

    /**
     * 主诊断checkbox选中时，不可点击粗码chechbox
     */
    public void onMAINDIAGFLGclick() {
        if ("Y".equalsIgnoreCase(TCM_Transform.getString(this
            .getValue("MAIN_DIAG_FLG")))) {
            this.setValue("CAT_FLG","N");
            this.callFunction("UI|CAT_FLG|setEnabled", false);
            return;
        }
        this.callFunction("UI|CAT_FLG|setEnabled", true);
        return;
    }

    /**
     * 校验合法年龄，并且起必须小于末
     */
    public void onCheckAge() {
        int sAge = this.getValueInt("START_AGE");
        int eAge = this.getValueInt("END_AGE");
        TNumberTextField s = (TNumberTextField)this.getComponent("START_AGE");
        if (sAge >= 200 || eAge >= 200 || sAge > eAge) {
            this.messageBox_("年龄限制不合法");
            this.setValue("START_AGE", 0);
            this.setValue("END_AGE", 0);
            s.grabFocus();
        }

    }

    /**
     * 删除事件
     */
    public void onDelete() {
//        if (selectRow < 0) {
//            this.messageBox_("没有可删除的数据");
//            return;
//        }
//        allTds.deleteRow(selectRow);
//        String[] sql = allTds.getUpdateSQL();
//        if (sql == null || sql.length < 1) {
//            this.messageBox("E0003");
//            onClear();
//            return;
//        }
//        for (String temp : sql) {
//            //System.out.println("temp->" + temp);
//        }
        
        int row = table.getSelectedRow();
        if(row < 0){
        	this.messageBox_("没有可删除的数据");
            return;
        }
        String icdCode=this.getValueString("ICD_CODE");
        String icdType = StringTool.getBoolean(this.getValueString("W")) == true?"W":"C";
        String sql = "DELETE FROM SYS_DIAGNOSIS WHERE  ICD_CODE='"+icdCode+"' AND ICD_TYPE='"+icdType+"'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
//        result = SYSOperationicdTool.getInstance().deleteTable1Data(icdCode);
        if (result.getErrCode() != 0) {
            this.messageBox("E0003");
        }
        else {
            this.messageBox("P0003");
        }
        onClear();
        return;

    }

    /**
     * 保存事件
     */
    public void onSave() {
//        int row = -1;
//        if (selectRow < 0) {
//            row = allTds.insertRow();
//        }
//        else {
//            row = table.getSelectedRow();
//        }
//        if (row < 0) {
//            row = 0;
//        }
        //ICD_TYPE
//        allTds.setItem(row, "ICD_TYPE",
//                       (StringTool.getBoolean(this.getValueString("W")) == true) ?
//                       "W" : "C");
//        //ICD_CODE
//        allTds.setItem(row, "ICD_CODE", this.getValueString("ICD_CODE"));
//        //ICD_CHN_DESC
//        allTds.setItem(row, "ICD_CHN_DESC", this.getValueString("ICD_CHN_DESC"));
//        //ICD_ENG_DESC
//        allTds.setItem(row, "ICD_ENG_DESC", this.getValueString("ICD_ENG_DESC"));
//        //PY1
//        allTds.setItem(row, "PY1", this.getValueString("PY1"));
//        //PY2
//        allTds.setItem(row, "PY2", this.getValueString("PY2"));
//        //SEQ
//        allTds.setItem(row, "SEQ", TCM_Transform.getLong(this.getValue("SEQ")));
//        //DESCRIPTION
//        allTds.setItem(row, "DESCRIPTION", this.getValueString("DESCRIPTION"));
//        //SYNDROME_FLG
//        //this.messageBox_(this.getValueString("ICD_CHN_DESC"));
//        allTds.setItem(row, "SYNDROME_FLG", this.getValueString("SYNDROME_FLG"));
//        //MDC_CODE
//        allTds.setItem(row, "MDC_CODE", this.getValueString("MDC_CODE"));
//        //CCMD_CODE
//        allTds.setItem(row, "CCMD_CODE", this.getValueString("CCMD_CODE"));
//        //MAIN_DIAG_FLG
//        allTds.setItem(row, "MAIN_DIAG_FLG",
//                       this.getValueString("MAIN_DIAG_FLG"));
//        //CAT_FLG
//        allTds.setItem(row, "CAT_FLG", this.getValueString("CAT_FLG"));
//        //STANDARD_DAYS
//        allTds.setItem(row, "STANDARD_DAYS",
//                       this.getValueString("STANDARD_DAYS"));
//        //CHLR_FLG
//        allTds.setItem(row, "CHLR_FLG", this.getValueString("CHLR_FLG"));
//        //DISEASETYPE_CODE
//        allTds.setItem(row, "DISEASETYPE_CODE",
//                       this.getValueString("DISEASETYPE_CODE"));
//        //MR_CODE
//        allTds.setItem(row, "MR_CODE", this.getValueString("MR_CODE"));
//        //CHRONIC_FLG
//        allTds.setItem(row, "CHRONIC_FLG", this.getValueString("CHRONIC_FLG"));
//        //START_AGE
//        allTds.setItem(row, "START_AGE",
//                       TCM_Transform.getLong(this.getValue("START_AGE")));
//        //LIMIT_DEPT_CODE
//        allTds.setItem(row, "LIMIT_DEPT_CODE",
//                       this.getValueString("LIMIT_DEPT_CODE"));
//        //LIMIT_SEX_CODE
//        allTds.setItem(row, "LIMIT_SEX_CODE",
//                       this.getValueString("LIMIT_SEX_CODE"));
//        //END_AGE
//        allTds.setItem(row, "END_AGE",
//                       TCM_Transform.getLong(this.getValue("END_AGE")));
//        //AVERAGE_FEE
//        allTds.setItem(row, "AVERAGE_FEE",
//                       TCM_Transform.getLong(this.getValue("AVERAGE_FEE")));
//        //OPT_USER
//        allTds.setItem(row, "OPT_USER", Operator.getID());
//        //OPT_DATE
//        allTds.setItem(row, "OPT_DATE", TJDODBTool.getInstance().getDBTime());
//        //OPT_TERM
//        allTds.setItem(row, "OPT_TERM", Operator.getIP());
//        allTds.setItem(row, "STA1_CODE", this.getValueString("STA1_CODE"));
//        allTds.setItem(row, "STA1_DESC", this.getValueString("STA1_DESC"));//add by wanglong 20131025
//        String[] sql = allTds.getUpdateSQL();
    	
    	//20160519 wangjc add start
        int row = table.getSelectedRow();
        String sql = "";
        String icdType = StringTool.getBoolean(this.getValueString("W")) == true?"W":"C";
        if(row < 0){
        	sql = "INSERT INTO SYS_DIAGNOSIS (ICD_TYPE,ICD_CODE,ICD_CHN_DESC,ICD_ENG_DESC,PY1,"
        			+"PY2,SEQ,DESCRIPTION,SYNDROME_FLG,MDC_CODE,CCMD_CODE,MAIN_DIAG_FLG,"
        			+"CAT_FLG,STANDARD_DAYS,CHLR_FLG,DISEASETYPE_CODE,MR_CODE,CHRONIC_FLG,"
        			+"START_AGE,LIMIT_DEPT_CODE,LIMIT_SEX_CODE,END_AGE,AVERAGE_FEE,"
        			+"OPT_USER,OPT_DATE,OPT_TERM,STA1_CODE,STA1_DESC) VALUES ('"+icdType+"','"
        			+this.getValueString("ICD_CODE")+"','"
        			+this.getValueString("ICD_CHN_DESC")+"','"
        			+this.getValueString("ICD_ENG_DESC")+"','"
        			+this.getValueString("PY1")+"','"
        			+this.getValueString("PY2")+"','"
        			+TCM_Transform.getLong(this.getValue("SEQ"))+"','"
        			+this.getValueString("DESCRIPTION")+"','"
        			+this.getValueString("SYNDROME_FLG")+"','"
        			+this.getValueString("MDC_CODE")+"','"
        			+this.getValueString("CCMD_CODE")+"','"
        			+this.getValueString("MAIN_DIAG_FLG")+"','"
        			+this.getValueString("CAT_FLG")+"','"
        			+this.getValueString("STANDARD_DAYS")+"','"
        			+this.getValueString("CHLR_FLG")+"','"
        			+this.getValueString("DISEASETYPE_CODE")+"','"
        			+this.getValueString("MR_CODE")+"','"
        			+this.getValueString("CHRONIC_FLG")+"','"
        			+TCM_Transform.getLong(this.getValue("START_AGE"))+"','"
        			+this.getValueString("LIMIT_DEPT_CODE")+"','"
        			+this.getValueString("LIMIT_SEX_CODE")+"','"
        			+TCM_Transform.getLong(this.getValue("END_AGE"))+"','"
        			+TCM_Transform.getLong(this.getValue("AVERAGE_FEE"))+"','"
        			+Operator.getID()+"',"
        			+"SYSDATE,'"+Operator.getIP()+"','"
        			+this.getValueString("STA1_CODE")+"','"
        			+this.getValueString("STA1_DESC")+"')";
        }else{
        	TParm rowParm = table.getParmValue().getRow(row);
        	sql = "UPDATE SYS_DIAGNOSIS SET ICD_TYPE='"+icdType
        			+"',ICD_CODE='"+this.getValueString("ICD_CODE")
        			+"',ICD_CHN_DESC='"+this.getValueString("ICD_CHN_DESC")
        			+"',ICD_ENG_DESC='"+this.getValueString("ICD_ENG_DESC")
        			+"',PY1='"+this.getValueString("PY1")
        			+"',PY2='"+this.getValueString("PY2")
        			+"',SEQ='"+TCM_Transform.getLong(this.getValue("SEQ"))
        			+"',DESCRIPTION='"+this.getValueString("DESCRIPTION")
        			+"',SYNDROME_FLG='"+this.getValueString("SYNDROME_FLG")
        			+"',MDC_CODE='"+this.getValueString("MDC_CODE")
        			+"',CCMD_CODE='"+this.getValueString("CCMD_CODE")
        			+"',MAIN_DIAG_FLG='"+this.getValueString("MAIN_DIAG_FLG")
        			+"',CAT_FLG='"+this.getValueString("CAT_FLG")
        			+"',STANDARD_DAYS='"+this.getValueString("STANDARD_DAYS")
        			+"',CHLR_FLG='"+this.getValueString("CHLR_FLG")
        			+"',DISEASETYPE_CODE='"+this.getValueString("DISEASETYPE_CODE")
        			+"',MR_CODE='"+this.getValueString("MR_CODE")
        			+"',CHRONIC_FLG='"+this.getValueString("CHRONIC_FLG")
        			+"',START_AGE='"+TCM_Transform.getLong(this.getValue("START_AGE"))
        			+"',LIMIT_DEPT_CODE='"+this.getValueString("LIMIT_DEPT_CODE")
        			+"',LIMIT_SEX_CODE='"+this.getValueString("LIMIT_SEX_CODE")
        			+"',END_AGE='"+TCM_Transform.getLong(this.getValue("END_AGE"))
        			+"',AVERAGE_FEE='"+TCM_Transform.getLong(this.getValue("AVERAGE_FEE"))
        			+"',OPT_USER='"+Operator.getID()
        			+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()
        			+"',STA1_CODE='"+this.getValueString("STA1_CODE")
        			+"',STA1_DESC='"+this.getValueString("STA1_DESC")+"' WHERE ICD_CODE='"+rowParm.getValue("ICD_CODE")+"' AND ICD_TYPE='"+rowParm.getValue("ICD_TYPE")+"'";
        }
//        System.out.println(sql);
      //20160519 wangjc add end
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
        if (result.getErrCode() != 0) {
            //this.messageBox_(result.getErrText());
            this.messageBox("保存成功");
        }
        else {
            this.messageBox("保存成功");
        }
        onInsertTable1();
        onClear();
    }
    
    /**
	 * 只删除标签数据
	 */
	public void onDeleteTable1Data(){
		int row = table.getSelectedRow();
		int row1= (Integer) callFunction("UI|TABLE1|getSelectedRow");
		if (row1<0) {
			this.messageBox("请选择一条数据");
			return;
		}
		if(row1>=0){
			if (this.messageBox("确定删除", "询问", 2) == 0) {
				String tagCode = table1.getParmValue().getValue("TAG_CODE",row1);
				String icdCode = table1.getParmValue().getValue("ICD_CODE",row1);
				if(tagCode!=null&&!"".equals(tagCode)){
					data = SYSOperationicdTool.getInstance().deleteTable1DataByTagCode(tagCode,icdCode);
				}
				
			} else {
				return;
			}
			
			if (data.getErrCode() < 0) {
				this.messageBox("E0003");
				onClear();
				init();
				return;
			}
		}
		this.messageBox("P0003");
		onClear();
		init();
		table.setSelectedRow(row);
		onTableClick();
	}
	
	public void selectEvent(){
		TTableCellEditor tce = table1.getCellEditor(table1.getSelectedColumn());
		if(table1.getSelectedColumn() == 1 && tf.getComboPopupMenu().isShowing() && tf.getComboValue().length() > 0){
			tce.stopCellEditing();
		}
	}
//  public void onCode(){
//      String code=this.getValueString("ICD_CHN_DESC");
//      StringUtil s;
//      if(StringUtil.isNullString(code)){
//          return ;
//      }
//
//  }
	
	//$$==============add by wangjc 20160519 加入排序功能start=============$$//
		/**
		 * 加入表格排序监听方法
		 * 
		 * @param table
		 */
		public void addListener(final TTable table) {
//			 System.out.println("==========加入事件===========");
			// System.out.println("++当前结果++"+masterTbl.getParmValue());
			// TParm tableDate = masterTbl.getParmValue();
			// System.out.println("===tableDate排序前==="+tableDate);
			table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent mouseevent) {
					int i = table.getTable().columnAtPoint(mouseevent.getPoint());
					int j = table.getTable().convertColumnIndexToModel(i);
					// System.out.println("+i+"+i);
					// System.out.println("+i+"+j);
					// 调用排序方法;
					// 转换出用户想排序的列和底层数据的列，然后判断 f
					if (j == sortColumn) {
						ascending = !ascending;
					} else {
						ascending = true;
						sortColumn = j;
					}
					// table.getModel().sort(ascending, sortColumn);

					// 表格中parm值一致,
					// 1.取paramw值;
					TParm tableData = getTTable("TABLE").getParmValue();
					// 2.转成 vector列名, 行vector ;
					String columnName[] = tableData.getNames("Data");
					String strNames = "";
					for (String tmp : columnName) {
						strNames += tmp + ";";
					}
					strNames = strNames.substring(0, strNames.length() - 1);
					// System.out.println("==strNames=="+strNames);
					Vector vct = getVector(tableData, "Data", strNames, 0);
					// System.out.println("==vct=="+vct);

					// 3.根据点击的列,对vector排序
					// System.out.println("sortColumn===="+sortColumn);
					// 表格排序的列名;
					String tblColumnName = getTTable("TABLE").getParmMap(sortColumn);
					// 转成parm中的列
					int col = tranParmColIndex(columnName, tblColumnName);
					// System.out.println("==col=="+col);

					compare.setDes(ascending);
					compare.setCol(col);
					java.util.Collections.sort(vct, compare);
					// 将排序后的vector转成parm;
					cloneVectoryParam(vct, new TParm(), strNames);

					// getTMenuItem("save").setEnabled(false);
				}
			});
		}

		/**
		 * vectory转成param
		 */
		private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
				String columnNames) {
			//
			// System.out.println("===vectorTable==="+vectorTable);
			// 行数据->列
			// System.out.println("========names==========="+columnNames);
			String nameArray[] = StringTool.parseLine(columnNames, ";");
			// 行数据;
			for (Object row : vectorTable) {
				int rowsCount = ((Vector) row).size();
				for (int i = 0; i < rowsCount; i++) {
					Object data = ((Vector) row).get(i);
					parmTable.addData(nameArray[i], data);
				}
			}
			parmTable.setCount(vectorTable.size());
			getTTable("TABLE").setParmValue(parmTable);
			// System.out.println("排序后===="+parmTable);

		}
		
		
		/**
		 * 得到 Vector 值
		 * 
		 * @param group
		 *            String 组名
		 * @param names
		 *            String "ID;NAME"
		 * @param size
		 *            int 最大行数
		 * @return Vector
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
		 * 
		 * @param columnName
		 * @param tblColumnName
		 * @return
		 */
		private int tranParmColIndex(String columnName[], String tblColumnName) {
			int index = 0;
			for (String tmp : columnName) {

				if (tmp.equalsIgnoreCase(tblColumnName)) {
					// System.out.println("tmp相等");
					return index;
				}
				index++;
			}

			return index;
		}
		//$$==============add by wangjc 20160519 加入排序功能end=============$$//
		
		private TTable getTTable(String tagName) {
			return (TTable) getComponent(tagName);
		}
	
}
