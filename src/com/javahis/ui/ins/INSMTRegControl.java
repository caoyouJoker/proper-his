package com.javahis.ui.ins;

import java.util.Map;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.ui.TWord;

/**
 * 
 * @author lixiang
 *
 */
public class INSMTRegControl extends TControl {
	/**
	 * 
	 */
	public static final String TN_TEMPLET_PATH="JHW\\住院志\\入院记录";
	/**
	 * 
	 */
	public static final String TN_EMT_FILENAME="糖尿病门特登记";
	
	public static final String SEPARATED="@";
	
	/**
	 * WORD对象
	 */
	private static final String TWORD = "WORD";
	
	private String caseNo;
	private String mrNo;
	/**
	 * WORD对象
	 */
	private TWord word;
	public void onInit() {
		super.onInit();
		// 初始化WORD
		initWord();
		// 初始化界面
		initPage();
	}

	public void initWord() {
		word = this.getTWord(TWORD);
		this.setWord(word);
	}

	public String getCaseNo() {
		return caseNo;
	}

	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	public String getMrNo() {
		return mrNo;
	}

	public void setMrNo(String mrNo) {
		this.mrNo = mrNo;
	}

	/**
	 * 得到WORD对象
	 * 
	 * @param tag
	 *            String
	 * @return TWord
	 */
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}

	/**
	 * 
	 * @param word
	 *            TWord
	 */
	public void setWord(TWord word) {
		this.word = word;
	}

	public TWord getWord() {
		return this.word;
	}

	/**
	 * 初始化界面
	 */
	public void initPage() {
        //
		Object obj = this.getParameter();
		if(obj!=null){
			this.setMrNo(((TParm) obj).getValue("MR_NO"));
			this.setCaseNo(((TParm) obj).getValue("CASE_NO"));
			
		}else{
		    //test 
			this.setMrNo("000000032649");
			this.setCaseNo("120320000769");			
		}
		// 1.通过就诊号取 REG_PATADM 表中 DISEASE_HISTORY， MED_HISTORY ，ASSISTANT_EXAMINE，ASSSISTANT_STUFF
		//假如不空
		//取数据， 构造结构化文档;
		
		//是空则读模版,新增
		// 打开病历
		if (!this.getWord().onOpen(
				TN_TEMPLET_PATH,
				TN_EMT_FILENAME, 2, false)) {

			return;
		}
		//this.messageBox("file name====="+this.getWord().getFileOpenName());
		
		if (this.getWord().getFileOpenName() == null) {
			// 没有需要编辑的文件
			this.messageBox("E0100");
			return;
		}
		this.getWord().update();
		TParm allParm = new TParm();
		//allParm.setData("FILE_TITLE_TEXT", "TEXT", this.hospAreaName);
		//allParm.setData("FILE_TITLE_EN_TEXT", "TEXT", this.hospEngAreaName);
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		//allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		allParm.setData("FILE_128CODE", "TEXT", this.getMrNo());
		allParm.addListener("onDoubleClicked", this, "onDoubleClicked");
		allParm.addListener("onMouseRightPressed", this, "onMouseRightPressed");
		this.getWord().setWordParameter(allParm);
		
		// 设置不可编辑
		//this.getWord().setCanEdit(false);
		setMicroField();
		this.getWord().setCanEdit(true);
		//this.messageBox("需要");
		// 编辑状态(非整洁)
		this.getWord().onEditWord();
		//是否存在记录
		//存在
		TParm parm=getRegedInfo(this.getMrNo(),this.getCaseNo());
		if(parm!=null){
			//存在
			if(!parm.getValue("DISEASE_HISTORY", 0).equals("")){
				//病史
				this.setDiseaseHistory(parm.getValue("DISEASE_HISTORY", 0));
				//辅助检查
				this.setExamine(parm.getValue("ASSISTANT_EXAMINE", 0));
				//既往史
				this.setMedHistory(parm.getValue("MED_HISTORY", 0));					
				//辅助材料
				this.setStuff(parm.getValue("ASSSISTANT_STUFF", 0));
				
			}
			
		}
			
	}
	/**
	 * 
	 */
	public void setMicroField(){
		TParm allParm = new TParm();
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", this.getMrNo());
		//allParm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT", this.getIpdNo());
		
		Map map = this.getDBTool().select(
				"SELECT * FROM MACRO_PATINFO_VIEW WHERE 1=1 AND MR_NO='"
						+ this.getMrNo() + "'");
		this.getWord().setWordParameter(allParm);
		TParm parm = new TParm(map);
		if (parm.getErrCode() < 0) {
			// 取得病人基本资料失败
			this.messageBox("E0110");
			return;
		}
		
		if (parm.getCount() > 0) {
			for (String parmName : parm.getNames()) {
				parm.addData(parmName, parm.getValue(parmName, 0));
			}

		} else {
			for (String parmName : parm.getNames()) {
				parm.addData(parmName, "");
			}

		}
		//
		String names[] = parm.getNames();
		TParm obj = (TParm) this.getWord().getFileManager().getParameter();
		String strSex="";
		for (String temp : names) {			
			if ("性别".equals(temp)) {
				if (parm.getInt(temp, 0) == 0) {
					strSex="未说明";
				} else if(parm.getInt(temp, 0) == 1){
					strSex="男";
					// 1.男 2.女
					//this.getWord().setSexControl(parm.getInt(temp, 0));				
				} else if(parm.getInt(temp, 0) == 2){
					strSex="女";
					
				} else if(parm.getInt(temp, 0) == 9){
					strSex="未说明";
				}
				
				this.getWord().setMicroField(temp, strSex);
			}else{
				obj.setData(temp, "TEXT", parm.getValue(temp, 0));
				//System.out.println("===temp==="+temp);
				
				this.getWord().setMicroField(temp, parm.getValue(temp,0));
				this.getWord().setWordParameter(obj);
				
			}
			
		}
		
		
	}

	/**
	 * 保存
	 */
	public void onSave() {
		//1. 通过况诊号更新 REG_PATADM 表中的 DISEASE_HISTORY， MED_HISTORY ，ASSISTANT_EXAMINE，ASSSISTANT_STUFF
	    //this.messageBox("==come in==");
		//病史
		String diseaseHistory=getDiseaseHistory();
		//检查
		String examine=getExamine();
		//即往史
		String medHis=getMedHistory();
		//材料
		String stuff=getStuff();
		String sql="UPDATE REG_PATADM SET DISEASE_HISTORY='"+diseaseHistory+"',";
		       sql+="ASSISTANT_EXAMINE='"+examine+"',";
		       sql+="MED_HISTORY='"+medHis+"',";
		       sql+="ASSSISTANT_STUFF='"+stuff+"'";
		       sql+=" WHERE CASE_NO='"+this.getCaseNo()+"'";
		       sql+=" AND MR_NO='"+this.getMrNo()+"'";
		       
		       
		this.getDBTool().update(sql);
		
		
		
	}

	/**
	 * 构造病史
	 * 
	 * @return
	 */
	private void setDiseaseHistory(String strDH){
		String str[]=getArray(strDH,SEPARATED);
		for(int i=1;i<=4;i++){
			if(str[i-1].equals("0")){
				getCheckboxCom("A"+i+"_1").setChecked(true);
			}else if(str[i-1].equals("1")){
				getCheckboxCom("A"+i+"_2").setChecked(true);
			}			
		}
		//
		getNumCom("A5").setText(str[4]);
		getNumCom("A6").setText(str[5]);
		
		
	}

	/**
	 * 构造即往历
	 * 
	 * @return
	 */
	private void setMedHistory(String strMH) {
		String str[]=getArray(strMH,SEPARATED);
		for(int i=1;i<=4;i++){
			if(str[i-1].equals("0")){
				getCheckboxCom("C"+i+"_1").setChecked(true);
			}else if(str[i-1].equals("1")){
				getCheckboxCom("C"+i+"_2").setChecked(true);
			}				
		}
		//
		this.setCaptureValue("C5",str[4]);
		
	}

	/**
	 * 辅助检查
	 * 
	 * @return
	 */
	private void setExamine(String strExa) {
		String str[]=getArray(strExa,SEPARATED);
		//
		for(int i=1;i<=11;i++){
			getNumCom("B"+i).setText(str[i-1]);			
		}
	
	}

	/**
	 * 辅助材料
	 * 
	 * @return
	 */
	private void setStuff(String strSuff) {
		String str[]=getArray(strSuff,SEPARATED);
		for(int i=1;i<=3;i++){
			if(str[i-1].equals("0")){
				getCheckboxCom("D"+i+"_1").setChecked(true);
			}else if(str[i-1].equals("1")){
				getCheckboxCom("D"+i+"_2").setChecked(true);
			}				
		}

	}
	
	/**
	 * 
	 * @param str
	 * @param suff
	 * @return
	 */
	private String[] getArray(String str,String suff){
		String arr[]=str.split(suff);
		
		return arr;
		
	}

	/**
	 * 即往历
	 * 
	 * @return
	 */
	private String  getMedHistory() {
		
		String result=getString(4 ,2,"C");
		result+="@";
		result+=getCaptureValue("C5");
		
		return result;

	}
	
	

	/**
	 * 辅助检查
	 * 
	 * @return
	 */
	private String getExamine() {		
		String result="";
		for(int i=1;i<=11;i++){
			result+=getNum("B"+i)+"@";			
		}
		//
		result=result.substring(0, result.length()-1);
			
		return result;
	}

	/**
	 * 辅助材料
	 * 
	 * @return
	 */
	private String getStuff() {
		String result=getString(3,2,"D");
		return result;
	}
	
	/**
	 * 获取病史
	 * @return
	 */
	private String getDiseaseHistory(){		
		String  result="";
		result+=getString(4 ,2,"A");
		result+="@";
		result+=getNum("A5");
		result+="@";
		result+=getNum("A6");
		return result;
	}
	
	/**
	 * 获取医保需保存的字符串
	 * @param row
	 * @param col
	 * @param tableName
	 * @return
	 */
	private String getString(int row ,int col,String tableName){
		String  result="";
		//检查一下，一组的只能选一个
		//行
		boolean flg=false;
		for(int i=1;i<=row;i++){
			boolean group1=false;
			boolean group2=false;
			//列
			for(int j=1;j<=col;j++){
				flg=getCheckBox(tableName+i+"_"+j);
				//第一列值为0
				if(j==1){
				  if(flg){
					  group1=true;
					  result+="0"; 
				  }
					
				//第二列值为1
				}else if(j==2){
					if(flg){
						group2=true;
						result+="1";   
					}
				}
			}
		    //同组都为true报"同一组只能选择一个"
			if(group1&&group2){
				this.messageBox("同一组只能选择一个");
				break;
			}else{
				result+="@";
			}
		}
		result=result.substring(0, result.length()-1);
		//System.out.println("===str==="+result);
		return result;
		
	}
	
	
	/**
	 *  通过checkbox取
	 */
	private boolean getCheckBox(String comName){
		/**EComponent com = this.getWord().getPageManager().findObject(
				comName, EComponent.CHECK_BOX_CHOOSE_TYPE);	**/
		
		ECheckBoxChoose checkbox = getCheckboxCom(comName);
	
		return checkbox.isChecked();
		
	}
	
	private ECheckBoxChoose getCheckboxCom(String comName){
		EComponent com = this.getWord().getPageManager().findObject(
				comName, EComponent.CHECK_BOX_CHOOSE_TYPE);	
		ECheckBoxChoose checkbox = (ECheckBoxChoose) com;
		return checkbox;
	}
	
	/**
	 * 返回数字控件
	 * @param comName
	 * @return
	 */
	private String getNum(String comName){
		
		EComponent com = this.getWord().getPageManager().findObject(
				comName, EComponent.NUMBER_CHOOSE_TYPE);	
		ENumberChoose numberChoose = (ENumberChoose) com;
		
		return numberChoose.getText();
		
	}
	
	private ENumberChoose getNumCom(String comName){
		
		EComponent com = this.getWord().getPageManager().findObject(
				comName, EComponent.NUMBER_CHOOSE_TYPE);	
		ENumberChoose numberChoose = (ENumberChoose) com;
		
		return numberChoose;
	}
	
	/**
	 * 获取抓取值
	 * 
	 * @param name
	 *            String
	 * @return String
	 */
	public String getCaptureValue(String name) {
		return this.getWord().getCaptureValue(name);
	}
	/**
	 * 设置值
	 * @param name
	 * @param value
	 */
	public void  setCaptureValue(String name,String value){
		ECapture ecap = this.getWord().findCapture(name);
		if (ecap == null) {
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		this.getWord().pasteString(value);
	}
	/**
	 * 是否已登记
	 * @param mrNo   病案号
	 * @param caseNo
	 * @return
	 */
	private TParm getRegedInfo(String mrNo,String caseNo){
		String sql="SELECT DISEASE_HISTORY,MED_HISTORY,ASSISTANT_EXAMINE,ASSSISTANT_STUFF FROM REG_PATADM";
		       sql+=" WHERE MR_NO='"+mrNo+"'";
		       sql+=" AND CASE_NO='"+caseNo+"'";
		TParm parm=new TParm(this.getDBTool().select(sql));
		if (parm.getErrCode() < 0) {
			this.messageBox("未取到病患信息！");
			return null;
		}
		
		return parm;

	}
	
	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}
	
	/**
	 * 续印
	 */
	public void onPrintXDDialog() {
		if (this.getWord().getFileOpenName() != null) {
			this.getWord().onPreviewWord();
			this.getWord().printXDDialog();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}

}
