package com.javahis.ui.inp;
import java.sql.Timestamp;
import java.util.Date;

import jdo.inp.ConsApplicationTool;
import jdo.sys.MailUtil;
import jdo.sys.MailVO;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.XmlUtil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
/**
*
* <p>Title: 会诊申请报表</p>
*
* <p>Description: 会诊申请报表</p>
*
* <p>Copyright: Copyright (c) caoyong 20139023</p>
*
* <p>Company: JavaHis</p>
*
* @author caoyong
* @version 1.0
*/
public class INPConsApplicationControl extends TControl {
	/**
	 * 传入参数
	 */
   
	private String caseNo;
	private TTable table;
	/**
	 * 邮件TParm
	 */
	private TParm tmessage;
	
	/**
	 * 病案号
	 */
	private String mrno;
	/**
	 * 病患姓名
	 */
	private String patName;
	/**
	 * 背会值班医生科室;
	 */
	private String accdeptcode;
	/**
	 * 就诊号
	 */
	private String caseno;
	/**
	 * 住院日期
	 */
	private Timestamp admDate;
	/**
	 * 住院号
	 */
	private String ipdNo;
	/**
	 * 邮箱地址
	 * 
	 */
	/**
	 * 会诊号
	 */
	private String consCode;
	
	/**
	 * 会诊种类
	 */
	private String kindCode;
	
	/**
	 * 申明常量
	 */
	public static final String STRMESS="请选择一条会诊清单数据";
	
	private  String  SQL="SELECT A.USER_NAME,A.ROLE_ID,A.USER_ID,A.E_MAIL,B.EXAMINE_CODE,B.EXAMINE_DATE,C.URG_FLG "+
                         "FROM JAVAHIS.SYS_OPERATOR A,JAVAHIS.MRO_CHRTVETREC B ,JAVAHIS.MRO_CHRTVETSTD C "+
                         "WHERE   A.USER_ID= B.VS_CODE (+) "+
                         "AND B.EXAMINE_CODE= C.EXAMINE_CODE (+)  AND  ";
	
	public String getIpdNo() {
		return ipdNo;
	}
	public void setIpdNo(String ipdNo) {
		this.ipdNo = ipdNo;
	}
	
	
	public String getAccdeptcode() {
		return accdeptcode;
	}
	public void setAccdeptcode(String accdeptcode) {
		this.accdeptcode = accdeptcode;
	}
	public String getAssdeptcode() {
		return assdeptcode;
	}
	public void setAssdeptcode(String assdeptcode) {
		this.assdeptcode = assdeptcode;
	}
	private String assdeptcode;//背会指定医生科室
	public String getPatName() {
		return patName;
	}
	public void setPatName(String patName) {
		this.patName = patName;
	}
	public void setMrno(String mrno){
		this.mrno=mrno;
	}
	public String getMrno(){
		return this.mrno;
	}
	
	
	public Timestamp getAdmDate() {
		return admDate;
	}
	public void setAdmDate(Timestamp admDate) {
		this.admDate = admDate;
	}
	
	public String getCaseno() {
		return caseno;
	}
	public void setCaseno(String caseno) {
		this.caseno = caseno;
	}
	public TParm getTmessage() {
		return tmessage;
	}
	public void setTmessage(TParm tmessage) {
		this.tmessage = tmessage;
	}
	
	public String getKindCode() {
		return kindCode;
	}
	public void setKindCode(String kindCode) {
		this.kindCode = kindCode;
	}
	public void setConsCode(String consCode) {
		this.consCode = consCode;
	}
	public String getConsCode(){
		return this.consCode;
	}
	/**
	 * 得到TABLE对象
	 * @param tagName
	 * @return
	 */
	 private TTable getTable(String tagName) {
			return (TTable) getComponent(tagName);
		}
	
	  /**
     * 初始化
     */
    public void onInit(){
    	 callFunction("UI|Tble|addEventListener","Tble->"+TTableEvent.CLICKED,this,"onTABLEClicked");//单击事件
    	//接受传入的参数
    	TParm outsideParm = (TParm) this.getParameter();
		String mrno=outsideParm.getValue("INW","MR_NO");//病案号
		String admDate=outsideParm.getValue("INW","ADM_DATE");//住院日期
		String patname=outsideParm.getValue("INW","PAT_NAME");//患者姓名
		String caseno=outsideParm.getValue("INW","CASE_NO");//就诊号
		String ipdno=outsideParm.getValue("INW","IPD_NO");//住院号
		
		String odiFlg=outsideParm.getValue("INW","ODI_FLG");//住院医生站管理关闭按钮
		if(odiFlg!=null&&odiFlg.length()>0){
			callFunction("UI|close|setEnabled",false);
		}
		
		//电子病历传值
        Timestamp ipdDate = StringTool.getTimestamp(admDate,"yyyyMMdd");
		this.setMrno(mrno);//病案号
		this.setCaseno(caseno);//就诊号
		this.setAdmDate(ipdDate);//住院日期
		this.setIpdNo(ipdno);//住院号
		
         //初始化会诊单号
 		this.setValue("CONS_CODE", this.getTConsCode());//会诊单号
 		
 		//发送公告患者姓名
 		this.setPatName(patname);//患者姓名
    	onQueryall() ;
    	this.initPage();
        
    }
    /**
     * 自动获取会诊单号
     */
    public synchronized String getTConsCode(){
    	String conscode="";
    	conscode = SystemTool.getInstance().getNo("ALL", "INP","CONS_NO", "CONS_NO");//自动获取会诊单号
    	return conscode;
    }
    /**
     * 查询一个患者的所有会诊申请记录
     */
    public void onQueryall(){
    	TParm result=new TParm();
		TParm parm= new TParm();
		TParm outsideParm = (TParm) this.getParameter();//接受传入的参数
		this.caseNo=outsideParm.getValue("INW","CASE_NO");
		parm.setData("CASE_NO",this.caseNo);
		result=ConsApplicationTool.getInstance().selectdataall(parm);
		
		 table=this.getTable("Tble");
		
	     table.setParmValue(result);
    	
    }
    /**
     * 初始化
     */
    private void initPage() {
    	
			Timestamp date = StringTool.getTimestamp(new Date());
		    this.setValue("CONS_DATE",date.toString().substring(0,10).replace('-', '/'));//初始化开单时间
		    this.setValue("DR_CODE",Operator.getID());//初始化开单医生
		    this.setValue("DR_TEL", this.getTel(Operator.getID()));
		    this.setValue("DR_DEPT", Operator.getDept());//初始化开单科室
		    this.setValue("KIND_CODE", "01");//初始化开单科室
		    this.setValue("ADM_TYPE", "I");
			    
    }
    /**
     * 保存
     */
    public void onSave(){
    	
    	TParm parm=new TParm();
    	TParm result= new TParm();
    	TParm tparm=new TParm();
    	if("".equals(this.getValueString("CONS_CODE"))){
    		this.messageBox("会诊单号不能为空");
    		this.grabFocus("CONS_CODE");
    		return;
    	}
    	/*if("".equals(this.getValueString("ACCEPT_DEPT_CODE"))){
    		this.messageBox("被会科室不能为空");
    		this.grabFocus("ACCEPT_DEPT_CODE");
    		return;
    	}*/
    	/*if("".equals(this.getValueString("ASSEPT_DEPT_CODE"))){
    		this.messageBox("指定被会科室不能为空");
    		this.grabFocus("ASSEPT_DEPT_CODE");
    		return;
    	}
    	if("".equals(this.getValueString("ASSIGN_DR_CODE"))){
    		this.messageBox("指定被会医生不能为空");
    		this.grabFocus("ASSIGN_DR_CODE");
    		return;
    	}*/
    	TParm outsideParm = (TParm) this.getParameter();//接受传入的参数
		this.caseNo=outsideParm.getValue("INW","CASE_NO");
    	parm.setData("URGENT_FLG",this.getValueString("URGENT_FLG"));//急
    	parm.setData("ADM_TYPE",this.getValueString("ADM_TYPE"));// 门急住别
    	parm.setData("ASSEPT_DEPT_CODE",this.getValueString("ASSEPT_DEPT_CODE"));//指定被会科室：
    	parm.setData("CASE_NO",this.caseNo);//诊断号
    	//parm.setData("SEX_CODE",this.getValueString("SEX_CODE"));//性别
    	//parm.setData("AGE",this.getValueString("AGE"));//年龄
    	//parm.setData("DEPT_CODE",this.getValueString("DEPT_CODE"));//科别
    	//parm.setData("PAT_AREA",this.getValueString("PAT_AREA"));//病区
    	//parm.setData("BED_NO",this.getValueString("BED_NO"));//床号
    	//parm.setData("VC_CODE",this.getValueString("VC_CODE"));//经治医生
    	//parm.setData("IN_DATE",this.getValueString("IN_DATE"));//入院时间
    	
    	parm.setData("KIND_CODE",this.getValueString("KIND_CODE"));//会诊类型
    	parm.setData("CONS_CODE",this.getValueString("CONS_CODE"));//会诊单号
    	parm.setData("CONS_DATE",this.getValue("CONS_DATE"));//开单时间
    	parm.setData("DR_CODE",this.getValueString("DR_CODE"));//开单医生
    	parm.setData("DR_TEL",this.getValueString("DR_TEL"));//开单医生电话
    	parm.setData("ACCEPT_DEPT_CODE",this.getValueString("ACCEPT_DEPT_CODE"));//社科医生
    	parm.setData("ACCEPT_DR_CODE",this.getValueString("ACCEPT_DR_CODE"));//社科值班医生
    	parm.setData("ACCEPT_DR_TEL",this.getValueString("ACCEPT_DR_TEL"));//社科医生电话
    	parm.setData("ASSIGN_DR_CODE",this.getValueString("ASSIGN_DR_CODE"));//被指定医生
    	parm.setData("ASSIGN_DR_TEL",this.getValueString("ASSIGN_DR_TEL"));//被指定医生电话
    	
    	parm.setData("REAL_DR_CODE",this.getValueString("ASSIGN_DR_TEL"));//实际被会医生，yanjing 20131115
    	parm.setData("DR_DEPT",Operator.getDept());//开单科室
    	parm.setData("REGION_CODE",Operator.getRegion());//区域
    	parm.setData("OPT_TERM", Operator.getIP());
    	parm.setData("OPT_USER", Operator.getID());
    	
    	tparm.setData("CONS_CODE",this.getValueString("CONS_CODE"));
    	String sql="SELECT CONS_CODE FROM INP_CONS WHERE CONS_CODE='"+this.getValueString("CONS_CODE")+"'";
    	TParm sparm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(sparm.getCount()>0){
    		result=ConsApplicationTool.getInstance().updatedata(parm);
    		if(result.getErrCode()<0){
          		 this.messageBox("修改失败");
          		 return ;
          	     }
          		 this.messageBox("修改成功");
          		 //this.onQueryall();
          		// this.getConsCode();//跟新会诊单号
          		 this.onClear();
          		
    	}else{
        	result= ConsApplicationTool.getInstance().insertdata(parm);
        	if(result.getErrCode()<0){
         		 this.messageBox("添加失败");
         		 return ;
         	     }
         		 this.messageBox("添加成功");
    		    // this.onQueryall();
    		    // this.getConsCode();//更新会诊单号
         		
    		  if(!"".equals(this.getValueString("ACCEPT_DEPT_CODE"))){
    		    	 onBoardMessage(this.getValueString("ACCEPT_DEPT_CODE"),"0") ;//发送公告
    		    }
    		  if(!"".equals(this.getValueString("ASSIGN_DR_CODE"))){
    			  onBoardMessage(this.getValueString("ASSIGN_DR_CODE"),"1") ;//发送公告
    			  
    		  }   
    		     
    		     this.onClear();
    		     
    	}
    	
    }
    /**
     * 被会指定医生不为空时，清空背会值班科室和值班医生
     */
    public void setNull(){
    	if(this.getValueString("ASSIGN_DR_CODE").length()>0){
    		this.setValue("ACCEPT_DEPT_CODE", "");//背会值班医生
    		this.setValue("ACCEPT_DR_CODE", "");//被会值班医生
    		this.setValue("ACCEPT_DR_TEL", "");
    		callFunction("UI|ACCEPT_DEPT_CODE|setEnabled", false);//锁被会值班医生
    		callFunction("UI|ACCEPT_DR_CODE|setEnabled", false);//锁被会值班医生
    	}else{
    		callFunction("UI|ACCEPT_DEPT_CODE|setEnabled", true);//锁被会值班医生
    		callFunction("UI|ACCEPT_DR_CODE|setEnabled", true);//锁被会值班医生
    		this.setValue("ASSIGN_DR_TEL", "");
    	}
    }
    /**
	 * *增加对Table的监听
	 */
	public void onTABLEClicked(int row){
	 TParm tparm = table.getParmValue().getRow(row);
	 
	 //如果被会值班医生有值则锁定被会指定科室和医生
	  if(tparm.getValue("ACCEPT_DEPT_CODE").length()>0){
		
		  callFunction("UI|ASSEPT_DEPT_CODE|setEnabled", true);//锁被会指定科室
  		  callFunction("UI|ASSIGN_DR_CODE|setEnabled", true);//锁被会指定医生
  		  callFunction("UI|ACCEPT_DEPT_CODE|setEnabled", true);//
 		  callFunction("UI|ACCEPT_DR_CODE|setEnabled", true);//
	  }
	  //被会指定医生医生有值锁定被会值班科室和医生
	  if(tparm.getValue("ASSIGN_DR_CODE").length()>0){
		  
		  callFunction("UI|ASSEPT_DEPT_CODE|setEnabled", true);//
 		  callFunction("UI|ASSIGN_DR_CODE|setEnabled", true);//
		  callFunction("UI|ACCEPT_DEPT_CODE|setEnabled", false);//锁被会值班医生
  		  callFunction("UI|ACCEPT_DR_CODE|setEnabled", false);//锁被会值班医生
  		 
	  }
			  this.setValue("KIND_CODE", tparm.getValue("KIND_CODE"));
			  this.setValue("CONS_CODE",  tparm.getValue("CONS_CODE"));
			  this.setValue("CONS_DATE", tparm.getValue("CONS_DATE").substring(0,10).replace("-","/"));
			  this.setValue("DR_DEPT",  tparm.getValue("DR_DEPT"));
			  this.setValue("ACCEPT_DR_CODE",   tparm.getValue("ACCEPT_DR_CODE")); 
			  this.setValue("ACCEPT_DEPT_CODE",  tparm.getValue("ACCEPT_DEPT_CODE"));
			  
			  this.setValue("ASSEPT_DEPT_CODE", tparm.getValue("ASSEPT_DEPT_CODE"));
			  this.setValue("ASSIGN_DR_CODE",   tparm.getValue("ASSIGN_DR_CODE"));
			  this.setValue("URGENT_FLG", tparm.getValue("URGENT_FLG"));
			 
			  this.setValue("DR_CODE", tparm.getValue("DR_CODE"));
			  this.setValue("DR_TEL", tparm.getValue("DR_TEL"));
			  this.setValue("ACCEPT_DR_TEL", tparm.getValue("ACCEPT_DR_TEL"));
			  this.setValue("ASSIGN_DR_TEL", tparm.getValue("ASSIGN_DR_TEL"));
			  
			  //this.setAssdeptcode(tparm.getValue("ASSEPT_DEPT_CODE"));//发送公告被指定科室
			  //
			  this.setConsCode(tparm.getValue("CONS_CODE"));
			  this.setKindCode(tparm.getValue("KIND_CODE"));
			  //发送邮件赋值
			  TParm mparm=new TParm();
			  if(tparm.getValue("ACCEPT_DEPT_CODE").length()>0){
				  mparm.setData("ACCEPT_DEPT_CODE",tparm.getValue("ACCEPT_DEPT_CODE"));
			  }
			  if(tparm.getValue("ASSIGN_DR_CODE").length()>0){
				  mparm.setData("ASSIGN_DR_CODE",tparm.getValue("ASSIGN_DR_CODE"));
				  mparm.setData("ASSIGN_DR_TEL",tparm.getValue("ASSIGN_DR_TEL"));
			  }
			  mparm.setData("OPT_USER",Operator.getID());
			  mparm.setData("OPT_TERM",Operator.getIP());
			  this.setTmessage(mparm);
	}
	/**
	 * 查询会诊是否完成
	 * @param tparm
	 * @return
	 */
	
	public TParm getCons(){
		   TParm xparm=new TParm();
	  		xparm.setData("CONS_CODE",this.getConsCode());
	  		xparm.setData("KIND_CODE",this.getKindCode());
	  		xparm.setData("CASE_NO",this.getCaseno());
	  		
	  	   TParm xresult=ConsApplicationTool.getInstance().selectdCons(xparm);
	  	   return xresult;
	}
   
    /**
     * 电话号事件
     */
    public void getDtel(){
    	if(!"".equals(getValueString("DR_CODE"))){//取得开单医生电话
            this.setValue("DR_TEL", this.getTel(getValueString("DR_CODE")));
    	}
    	if(!"".equals(getValueString("ACCEPT_DR_CODE"))){//取得被会值班医生电话
    		this.setValue("ACCEPT_DR_TEL", this.getTel(getValueString("ACCEPT_DR_CODE")));
    	}
    	if(!"".equals(getValueString("ASSIGN_DR_CODE"))){//取得指定被会医生电话
    		this.setValue("ASSIGN_DR_TEL", this.getTel(getValueString("ASSIGN_DR_CODE")));
    	}
    		
    }
    /**
     * 查询医生所对应的电话号码
     */
    public String getTel(String value){
    	
    	if(!"".equals(value)){
    		String sqlt="SELECT TEL1 FROM SYS_OPERATOR WHERE USER_ID='"+value+"'";
        	TParm parmt = new TParm(TJDODBTool.getInstance().select(sqlt));
        	return parmt.getValue("TEL1", 0);
    	}else{
    		return "";
    	}
    	
    	
    }
    /**
	 * 清空
	 */
	public void onClear() {
		String clearString= "KIND_CODE;CONS_DATE;DR_CODE;DR_TEL;ACCEPT_DEPT_CODE;ASSEPT_DEPT_CODE; " +
				            "ACCEPT_DR_TEL;ASSIGN_DR_CODE;ASSIGN_DR_TEL;ACCEPT_DR_CODE;REPORT_DEPT_CODE;URGENT_FLG";
		clearValue(clearString);
		  callFunction("UI|ASSEPT_DEPT_CODE|setEnabled", true);
		  callFunction("UI|ASSIGN_DR_CODE|setEnabled", true);
		  callFunction("UI|ACCEPT_DEPT_CODE|setEnabled", true);
 		  callFunction("UI|ACCEPT_DR_CODE|setEnabled", true);

		this.onInit();
		/*this.initPage();
		table.removeRowAll();*/
		
	}
    /**
     * 查询数据
     */
	public void onQuery() {
		/*TParm result=new TParm();
		TParm parm= new TParm();
		//TParm outsideParm = (TParm) this.getParameter();//接受传入的参数
		//this.caseNo=outsideParm.getValue("INW","CASE_NO");
		if("".equals(this.getValueString("CONS_CODE"))){
			this.messageBox("请输入查询的会诊单号");
			this.grabFocus("CONS_CODE");
			return;
		}
		parm.setData("CONS_CODE",this.getValueString("CONS_CODE"));
		result=ConsApplicationTool.getInstance().selectdata(parm);
		if(result.getErrCode()<0){
			this.messageBox("查询出现问题");
			return;
		}
		if(result.getCount()<=0){
			this.messageBox("没有查询数据");
			this.onClear();
			return;
		}
		 table=this.getTable("Tble");
	     table.setParmValue(result);*/
		 this.openDialog("%ROOT%\\config\\inp\\INPSchedlu.x");
		 
	}
	 /**
     *取消会诊
     */
    public void onCancel() {
    	int row =table.getSelectedRow();
    	if(row<0){
    		this.messageBox("请选择一条取消病患的信息");
    		return;
    	}
    	TParm result=this.getCons();
    	if("Y".equals(result.getValue("REPORT_FLG",0))){
    		this.messageBox("会诊已完成，不可以取消");
    		return;
    	}
    	TParm rparm= table.getParmValue().getRow(row);
    	TParm parm = new TParm();
    	String sqlq="SELECT A.MR_NO,A.IPD_NO,A.DEPT_CODE,A.BED_NO," +
				    "A.IN_DATE,A.STATION_CODE,A.VS_DR_CODE,B.PAT_NAME, " +
				    "B.BIRTH_DATE,B.SEX_CODE  "+
				    "FROM " +
				    "ADM_INP A,SYS_PATINFO B "+
				    "WHERE "+
				    "A.MR_NO=B.MR_NO(+) AND " +
				    "A.CASE_NO='"+this.caseNo+"'";
    	
	TParm parmq = new TParm(TJDODBTool.getInstance().select(sqlq));
			parm.setData("MR_NO", parmq.getValue("MR_NO",0));//病案号
			parm.setData("IPD_NO", parmq.getValue("IPD_NO",0));//住院号
			parm.setData("PAT_NAME", parmq.getValue("PAT_NAME",0));//姓名
			parm.setData("SEX_CODE", "1".equals(parmq.getValue("SEX_CODE",0)) ? "男" :"女");//性别
			parm.setData("DEPT_CODE", parmq.getValue("DEPT_CODE",0));//科别
			parm.setData("VS_DR_CODE", parmq.getValue("VS_DR_CODE",0));//经治医生
			parm.setData("STATION_CODE", parmq.getValue("STATION_CODE",0));//病区
			//parm.setData("BED_NO", parmq.getValue("BED_NO",0));//床号
			parm.setData("IN_DATE", parmq.getValue("IN_DATE",0).substring(0, 10).replace("-", "/"));//住院日期
			parm.setData("CONS_CODE",rparm.getValue("CONS_CODE"));
			parm.setData("CANCEL_DR_CODE",Operator.getID());
			
       /* String value = getValueString("RECP_TYPE");
        if (value.length() == 0) {
            this.messageBox("E0012");
            return;
        }*/
        this.openDialog("%ROOT%\\config\\inp\\INPConsCancel.x",parm);
       // onClear();
        //this.onQuery();
    }
	/**
	 * 删除数据
	 */
	
	public void onDelete(){
		 TParm result =new TParm();
		
		 TParm parm=table.getParmValue();
		 if(parm.getCount()<=0){
			 
			this.messageBox("没有保存数据");
			return;
		 }
		int row=table.getSelectedRow();
		if(row<0){
			this.messageBox("请选择要删除的数据");
			return ;
		}
		if (this.messageBox("是否删除", "确认要删除吗？", 2) == 0) {
		  TParm tparm=table.getParmValue().getRow(row);
		  TParm dparm=new TParm();
		  dparm.setData("CONS_CODE", tparm.getValue("CONS_CODE"));
		  result=ConsApplicationTool.getInstance().deletedata(dparm);
		
		  if(result.getErrCode()<0){
			  this.messageBox(" 删除失败");
			  return;
		  }
		       this.messageBox("删除成功");
		  }
		      this.onQueryall();
		      String clearString= "KIND_CODE;CONS_CODE;CONS_DATE;DR_CODE;DR_TEL;ACCEPT_DEPT_CODE;ASSEPT_DEPT_CODE; " +
	                              "ACCEPT_DR_TEL;ASSIGN_DR_CODE;ASSIGN_DR_TEL;ACCEPT_DR_CODE;REPORT_DEPT_CODE";
                clearValue(clearString);
	}
	
	/**
	 * 电子病历
	 */
	
	public void onConsApplyDetail(){
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox(STRMESS);
			return;
		}
		
		onInpDepApp(this.getMrno(),this.getCaseno(),this.getAdmDate(),this.getIpdNo());//电子病历传值
		
	}
	/**
	 * 电子病历接收值
	 * 
	 */
	public void onInpDepApp(String mrno,String caseno,Timestamp admDate,String ipdno) {
		TParm parm = new TParm();
		parm.setData("MR_NO", mrno);
		parm.setData("CASE_NO", caseno);
		parm.setData("ADM_DATE",admDate);
		parm.setData("IPD_NO",ipdno);
		this.openDialog("%ROOT%\\config\\inp\\INPDeptApp.x", parm);
	}
	
	/**
	 * 
	 */
	public String getBody(){
		 String   body =Operator.getName()+"医生给病案号:"+this.getMrno()+"姓名为:"+this.getPatName()+"的病人申请会诊。" ;
		 return body;
		
	}
	/**
	 * 会诊发布公布栏
	 * 
	 */
	public void onBoardMessage(String typecode, String type) {
		
		TParm parm =new TParm();
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("MR_NO", this.getMrno());
		parm.setData("USER_NAME", Operator.getName());
		parm.setData("PAT_NAME", this.getPatName());
		if("1".equals(type)){
		parm.setData("ASSIGN_DR_CODE", typecode);//被会指定医生
		}else{
		parm.setData("ACCEPT",typecode);//背会值班科室/
		}
		
		//add by yangjj 20160614
		parm.setData("DR_DEPT", ((TTextFormat)this.getComponent("DR_DEPT")).getText());
		
		//parm.setData("ASSEPT_DEPT_CODE",this.getAccdeptcode());//背会值班科室/被会指定医生
		// 执行数据新增
		TParm result =this.onOdiBoardMessage(parm);
		// 保存判断
		if (result == null || result.getErrCode()<0) {
			this.messageBox("公告和短信发送失败" + " , " + result.getErrText());
			return;
		}
		   this.messageBox("公告和短信发送成功");
	}
	/**
	 * 插入公告栏 和接受档
	 * @param parm
	 * @return
	 */
	  public TParm onOdiBoardMessage(TParm parm) {
	    	 TParm result = new TParm();
	    	 TParm sendMessage=new TParm();
	    	 TParm parmacc=new TParm();
	    	 
	    	 if(parm.getValue("ACCEPT").length()>0){
	    		 parmacc.setData("DEPT_CODE",parm.getValue("ACCEPT"));
		         sendMessage = ConsApplicationTool.getInstance().selectdacc(parmacc);//查询被会值班医生
	    	 }
	    	 
	    	if(parm.getValue("ASSIGN_DR_CODE").length()>0){
	    		 sendMessage.setData("ASSIGN_DR_CODE", parm.getValue("ASSIGN_DR_CODE"));
	    	}
	         
	         
	         if (sendMessage == null||sendMessage.getCount() <= 0) {
	             result.setErrText("没有未发送的邮件！");
	         }
	        
	         
	         // 2.发送公布栏信息并且更新公布栏状态
	         TParm resultMessage = sendBoardOid(parm,sendMessage);
	         
	         //add by yangjj 20160613
	         String d = SystemTool.getInstance().getDate().toString();
	         d = d.substring(0, d.length()-2);
	         String message = d+" "+parm.getValue("DR_DEPT")+" "+resultMessage.getValue("BODY",0);
	         String title = resultMessage.getValue("TITLE",0);
	         
	              TParm inparm = new TParm();
	         for (int i = 0; i < resultMessage.getCount("USER_ID"); i++) {
	        	
	             inparm.setData("MESSAGE_NO", getMessageNo());
	             inparm.setData("POST_TYPE", "P");
	             inparm.setData("POST_GROUP","ODI");
	             inparm.setData("USER_ID", resultMessage.getValue("USER_ID",i));
	             inparm.setData("READ_FLG", "N");
	             inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
	             inparm.setData("POST_TIME", SystemTool.getInstance().getDate());
	             inparm.setData("OPT_DATE", SystemTool.getInstance().getDate());
	             inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
	             inparm.setData("POST_SUBJECT", resultMessage.getValue("TITLE",0));
	             inparm.setData("URG_FLG", "Y");
	             inparm.setData("POST_INFOMATION", resultMessage.getValue("BODY",0));
	             inparm.setData("RESPONSE_NO", 0);//回应次数
	             inparm.setData("POST_ID", parm.getValue("OPT_USER"));
	            result = ConsApplicationTool.getInstance().insertPostRCV(inparm);
	            
	            //add by yangjj 20160613
	            String sqlTel = "SELECT TEL1 FROM SYS_OPERATOR WHERE USER_ID='"+resultMessage.getValue("USER_ID", i)+"'";
	            TParm telParm = new TParm(TJDODBTool.getInstance().select(sqlTel));
	            if((telParm.getCount("TEL1") > 0) && (!"".equals(telParm.getValue("TEL1", 0)))){
	            	XmlUtil.createInpXml(telParm.getValue("TEL1", 0),title,message);
	            }
	            
	            
	             if (result.getErrCode() < 0) {
	                 return result;
	             }
	              
	             result = ConsApplicationTool.getInstance().insertBoard(inparm);
	             if (result.getErrCode() < 0) {
	                 return result;
	             }
	        }
	         return result;
	    }
		/**
		 * 报告标题和内容,发送公告的人员
		 */
		private TParm sendBoardOid(TParm parm, TParm sendMessage) {
			TParm result = new TParm();
			String title = "";
//			String body = "";
			title = "会诊申请报告单";// 会诊报告标题
//			body = parm.getValue("USER_NAME") + "医生给病案号为：" + parm.getValue("MR_NO")
//					+ ",姓名为：" + parm.getValue("PAT_NAME") + "的病人申请会诊。";
			result.addData("TITLE", title);
			result.addData("BODY", getBody());
			if (sendMessage.getValue("ASSIGN_DR_CODE").length() > 0) {
				result.addData("USER_ID", sendMessage.getValue("ASSIGN_DR_CODE"));// 被指定医生
			} else {
				for (int i = 0; i < 6; i++) {
					if (null!=sendMessage.getValue("DR_CODE"+(i+1), 0)
							&&sendMessage.getValue("DR_CODE"+(i+1), 0).length()>0) {
						result.addData("USER_ID", sendMessage.getValue("DR_CODE"+(i+1), 0));// 值班医生
					}
				}
			}
			return result;
		}
	  /**
	     * 取得流水号
	     * @return String
	     */
	    private synchronized String getMessageNo() {
	        String messageNo = "";
	        messageNo = SystemTool.getInstance().getNo("ALL", "PUB", "MESSAGE_NO", "MESSAGE_NO");
	        return messageNo;
	    }
	    
	    
	    
	    /**
	     * 发送邮件
	     */
	    
	    public void onBoardMessage(){
	    	int row = table.getSelectedRow();
	    	if(row<0){
	    		this.messageBox(STRMESS);
	    		return;
	    	}
	    	TParm result=this.onSendMessage(this.getTmessage());
	    	this.messageBox(result.getValue("MESSAGE"));
	    	
	    
	    	
	    	
	    }
	    
	    /**
	     * 发送邮件
	     * @param parm TParm
	     * @return TParm
	     */
	    public TParm onSendMessage(TParm parm) {
	    
	        TParm result = new TParm();
	        TParm sendMessage=new TParm();
	        TParm parmacc=new TParm();
	        TParm treslut=new TParm();
	        // 1.查询未发送的邮件
	        

	    	 if(parm.getValue("ACCEPT_DEPT_CODE").length()>0){
	    		 parmacc.setData("DEPT_CODE",parm.getValue("ACCEPT_DEPT_CODE"));
		         sendMessage = ConsApplicationTool.getInstance().selectdacc(parmacc);//查询被会值班医生
		        
		         String sql1=" A.USER_ID='"+sendMessage.getValue("DR_CODE1",0)+"'";
		         String sql2=" A.USER_ID='"+sendMessage.getValue("DR_CODE2",0)+"'";
		         String sql3=" A.USER_ID='"+sendMessage.getValue("DR_CODE3",0)+"'";
		         String sql4=" A.USER_ID='"+sendMessage.getValue("DR_CODE4",0)+"'";
		         String sql5=" A.USER_ID='"+sendMessage.getValue("DR_CODE5",0)+"'";
		         String sql6=" A.USER_ID='"+sendMessage.getValue("DR_CODE6",0)+"'";
		         
		         TParm paramess1 = new TParm(TJDODBTool.getInstance().select(SQL+sql1));//被会值班医生1
		         TParm paramess2 = new TParm(TJDODBTool.getInstance().select(SQL+sql2));//被会值班医生2
		         TParm paramess3 = new TParm(TJDODBTool.getInstance().select(SQL+sql3));//被会值班医生3
		         TParm paramess4 = new TParm(TJDODBTool.getInstance().select(SQL+sql4));//被会值班医生4
		         TParm paramess5 = new TParm(TJDODBTool.getInstance().select(SQL+sql5));//被会值班医生5
		         TParm paramess6 = new TParm(TJDODBTool.getInstance().select(SQL+sql6));//被会值班医生6
		        
		              if(paramess1.getCount()>0){
		            	  treslut.addRowData(paramess1,0);
		              }
		              if(paramess2.getCount()>0){
		            	  treslut.addRowData(paramess2,0);
		              }
		              if(paramess3.getCount()>0){
		            	  treslut.addRowData(paramess3,0);
		              }
		              if(paramess4.getCount()>0){
		            	  treslut.addRowData(paramess4,0);
		              }
		              if(paramess5.getCount()>0){
		            	  treslut.addRowData(paramess5,0);
		              }
		              if(paramess6.getCount()>0){
		            	  treslut.addRowData(paramess6,0);
		              }
	    	 }
	    	 
	    	if(parm.getValue("ASSIGN_DR_CODE").length()>0){
	    		 //sendMessage.setData("ASSIGN_DR_CODE", parm.getValue("ASSIGN_DR_CODE"));
	    		 String messql=" A.USER_ID='"+parm.getValue("ASSIGN_DR_CODE")+"'"; 
	    		 TParm taprm = new TParm(TJDODBTool.getInstance().select(SQL+messql));//被会指定医生
	    		 treslut.addRowData(taprm, 0);
	    	}
	        
	        if (sendMessage == null || sendMessage.getCount() <= 0) {
	            result.setErrText("没有未发送的邮件！");
	        }
		        // 1.发送邮件
		        TParm resultMessage = sendEMail(treslut);
		        String message = resultMessage.getValue("MESSAGE");
		        // 2.更新邮件状态
	        for (int i = 0; i < resultMessage.getCount("CASE_NO"); i++) {
		            TParm inparm = new TParm();
		            inparm.setData("CASE_NO", this.getCaseno());
		            inparm.setData("EXAMINE_CODE", resultMessage.getValue("EXAMINE_CODE", i));
		            inparm.setData("EXAMINE_DATE", resultMessage.getValue("EXAMINE_DATE", i));
		            inparm.setData("EMAIL_STATUS", "Y");
		            inparm.setData("OPT_USER",Operator.getID());
		            inparm.setData("OPT_TERM",Operator.getIP());
		            result = ConsApplicationTool.getInstance().updateEMail(inparm);
	          
	            if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
	                return result;
	            }
	           }
			        result.setData("MESSAGE", message);
			        return result;
			    }
	    
	    /**
	     * 创建邮件并且发送
	     * @param parm TParm
	     * @return TParm
	     */
	    public TParm sendEMail(TParm parm) {
//	    System.out.println("======输出传入parm："+parm);
	        String message = "";
	        boolean flg = false;
	        TParm result = new TParm();
	        String e_mail = "";
	        String title = "";
	        String dc_name_message = "";//yanjing 20131113 统计发送失败医生名单
	       /// String body = "";
	        //String dr_name = "";
	        for (int i = 0; i < parm.getCount("E_MAIL"); i++) {
	        	
	            e_mail = parm.getValue("E_MAIL", i);
	            title = "会诊申请邮件通告";
	            //System.out.println("================"+e_mail +"||"+title+"||"+this.getBody());
	            //dr_name = parm.getValue("USER_ID", i);
	            //body =Operator.getName()+"医生给病案号为："+this.getMrno()+",姓名为："+this.getPatName()+"的病人申请会诊。" ;
	            
	            flg = sendEMail(e_mail, title, this.getBody());
	            if (flg) {
	                result.addData("CASE_NO", this.getCaseno());
	                result.addData("EXAMINE_CODE", parm.getValue("EXAMINE_CODE", i));
	                result.addData("EXAMINE_DATE", parm.getValue("EXAMINE_DATE", i));
	                result.addData("ROLE_ID", parm.getValue("ROLE_ID", i));
	                result.addData("USER_ID", parm.getValue("USER_ID", i));
	                result.addData("TITLE", title);
	                result.addData("BODY", this.getBody());//邮件内容
	                result.addData("URG_FLG", parm.getValue("URG_FLG", i));
	            }else{
	            dc_name_message +=parm.getValue("USER_NAME", i)+ ",\n";
	            //message += dr_name + (flg ? "邮件发送成功" : "邮件发送失败") + "\n";
	            } 
	        }
	           message=(flg ? "邮件发送成功" : dc_name_message+"邮件发送失败") + "\n";
		         result.setData("MESSAGE", message);
		         return result;
	    }
	    /**
	     * 创建邮件并且发送
	     * @param email_address String
	     * @param title String
	     * @param body String
	     * @return TParm
	     */
	    public boolean sendEMail(String email_address, String title, String body) {
	        MailVO mail = new MailVO();
	        mail.getToAddress().add(email_address);
	        mail.setSubject(title);
	        mail.setContent(body);
	        TParm MailSendResult = MailUtil.getInstance().sendMail(mail);
	        if (MailSendResult.getErrCode() < 0) {
	            return false;
	        }
	        else {
	            return true;
	        }
	    }
	    
	    /**
	     * 发送短消息
	     */
	    public void onMessage(){
	    	int row = table.getSelectedRow();
	    	if(row<0){
	    		this.messageBox(STRMESS);
	    		return;
	    	}
	    	TParm parm = new TParm();
	    	TParm tparm=this.getTmessage();//选中一行得到行里部分数据
	    	
	    	TParm parmacc=this.getTmessage();
	    	TParm sendMessage= new TParm();
	    	TParm tel= new TParm();
	    	String Doctor="";
		    	 if(tparm.getValue("ACCEPT_DEPT_CODE").length()>0){//给被会值班科室下的医生发短信息
		    		  Doctor="您是被会值班医生。";
		    		  parmacc.setData("DEPT_CODE",tparm.getValue("ACCEPT_DEPT_CODE"));
			          sendMessage = ConsApplicationTool.getInstance().selectdMess(parmacc);//查询被会值班医生电话号码
		    	 }
		    	 if(tparm.getValue("ASSIGN_DR_TEL").length()>0){//被会指定医生电话
		    		 Doctor="您是被会指定医生。";
		    		 tel.addData("TEL1", tparm.getValue("ASSIGN_DR_TEL"));
		    	 }
		    	 
		    	 if(sendMessage.getValue("DR_TEL1",0).length()>0){//背会值班医生1电话
		    		tel.addData("TEL1", sendMessage.getValue("DR_TEL1",0));
		    	 }
		    	 if(sendMessage.getValue("DR_TEL2",0).length()>0){//被会值班医生2电话
		    	 	tel.addData("TEL1", sendMessage.getValue("DR_TEL2",0));
		    	 }
		    	 if(sendMessage.getValue("DR_TEL3",0).length()>0){//被会值班医生3电话
		    		tel.addData("TEL1", sendMessage.getValue("DR_TEL3",0));
		    	 }
		    	 if(sendMessage.getValue("DR_TEL4",0).length()>0){//被会值班医生4电话
		    		tel.addData("TEL1", sendMessage.getValue("DR_TEL4",0));
		    	 }
		    	 if(sendMessage.getValue("DR_TEL5",0).length()>0){//被会值班医生5电话
		    		 tel.addData("TEL1", sendMessage.getValue("DR_TEL5",0));
		    	 }
		    	 if(sendMessage.getValue("DR_TEL6",0).length()>0){//被会值班医生6电话
		    		 tel.addData("TEL1", sendMessage.getValue("DR_TEL6",0));
		    	  }
		    	 
	    	 
				parm.setData("Content", this.getBody().replace("。","，")+Doctor);//短信内容
				parm.setData("MrNo", this.mrno);//病案号
				//parm.setData("CaseNo", this.caseno);//就诊号
				parm.setData("Name", this.getPatName());//病患姓名
				parm.setData("Title","会诊通知");//标题
				parm.setData("SysNo","INP");//标题	
				XmlUtil.createSmsFile(parm, tel);//创建短信XML内容
	    
	    }
	    
	    
	    
}
