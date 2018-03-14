package com.javahis.ui.bms;
import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;

import javax.swing.table.TableModel;

import jdo.bms.BMSSQL;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.util.Compare;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 血液历史查询(查询，清空，关闭)
 * </p>
 *
 * <p>
 * Description: 血液历史查询 
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author fux 2012.04.19
 * @version 2.0
 */

public class BMSHistoryQueryControl extends TControl {

	private TTable table;
	
	//add by wukai start 20160811 start
	private String bldCode; //血品
	private String subcatCode;  //血品规格
	private String stateCode;  //血液状况
	private String deptCode;  //用血科室
	private String outDate;  //出库时间
	private String inDate;   //入库时间
	private String outUser;  //发血人员
	//add by wukai start 20160811 end
	
	/**
     * 初始化方法
     */
    public void onInit() {
        initPage();
    }

    
	 /**
     * 初始画面数据
     */ 
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("E_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("S_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("IN_B",
                date.toString().substring(0, 10).replace('-', '/') +
                " 23:59:59");
        this.setValue("IN_A",
                StringTool.rollDate(date, -7).toString().substring(0, 10).
                replace('-', '/') + " 00:00:00");
        this.setValue("TEST_USER", Operator.getID());
        // 初始化TABLE
        table = getTable("TABLE");
        onChkAge();
    }
    
    /**
     * 打印出库单
     */
    public void onPrint() {
    	String outNo1 = this.getValueString("OUT_NO") ;//获取申请单号
    	String sql = "SELECT A.*,B.DEPT_CHN_DESC,C.STATION_DESC,D.BED_NO FROM BMS_BLOOD A,SYS_DEPT B,SYS_STATION C,ADM_INP D WHERE A.DEPT_CODE = B.DEPT_CODE AND A.STATION_CODE = C.STATION_CODE AND A.CASE_NO = D.CASE_NO AND A.OUT_NO = '"+outNo1+"'" ;
    	TParm outData = new TParm(TJDODBTool.getInstance().select(sql));
    	if(outData.getErrCode()<0){
    		messageBox("没有此申请单号!") ;
    		return ;
    	}
    	if(outData.getCount()<=0){
    		messageBox("查无没有出库信息!") ;
    		return ;
    	}
    	
    	if(!"2".equals(outData.getValue("STATE_CODE", 0))){
    		messageBox("该申请单中的血品状态不是出库状态.") ;
    		return ;
    	}
    	Pat pat = Pat.onQueryByMrNo(outData.getValue("MR_NO", 0));
        TParm date = new TParm();
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "血品出库单");
        date.setData("APPLY_NO", "TEXT", "备血单号: " + outData.getValue("APPLY_NO", 0));
        date.setData("PAT_NAME", "TEXT", " 受血者: " + pat.getName());
        date.setData("AGE", "TEXT", "年龄: " + StringUtil.showAge(pat.getBirthday(),
                SystemTool.getInstance().getDate()));
        date.setData("SEX", "TEXT", "性别: " + pat.getSexString());
        date.setData("BLD_TYPE", "TEXT", " ABO血型: " + pat.getBloodType());
        String rh_type = "";
        if ( "+".equals(pat.getBloodRHType())) {
            rh_type = "阳性";
        }
        else if ( "-".equals(pat.getBloodRHType())) {
            rh_type = "阴性";
        }
        date.setData("RH_TYPE", "TEXT", "RH血型: " + rh_type);
        date.setData("MR_NO", "TEXT", " 病案号: " + outData.getValue("MR_NO", 0));
   
        date.setData("DEPT_CODE", "TEXT",
                     "科别: " + outData.getValue("DEPT_CHN_DESC", 0));

        date.setData("IPD_NO", "TEXT", "住院号: " + outData.getValue("IPD_NO", 0));
        date.setData("STATION_CODE", "TEXT",
                     "病区: " +
                     outData.getValue("STATION_DESC", 0));
        date.setData("BED_NO", "TEXT",
                     "床位: " + outData.getValue("BED_NO", 0));
       

        String outNo = "" ;//出库单号
        String reason = "" ;//输血原因
        String deptCode = "" ; //用血科室
        String diag = "" ;//诊断
        String optDate = "" ;//收检日期
        String outDate = "" ;//出库日期
        String caseNo = "" ;
        // 表格数据
        TParm parm = new TParm();
        String blood_no = "";
        TParm inparm = new TParm();
        for (int i = 0; i < outData.getCount(); i++) {
            blood_no = outData.getValue("BLOOD_NO",i);
            parm.addData("BLOOD_NO", blood_no);
            inparm = new TParm(TJDODBTool.getInstance().select(BMSSQL.
                getBMSBloodOut(blood_no)));
            
            if("".equals(outNo)){
            	outNo = inparm.getValue("OUT_NO", 0) ;
            }
            if("".equals(reason)){
            	reason += this.getTransReason(inparm.getValue("TRANRSN_CODE1", 0)) +" ";
            	reason += this.getTransReason(inparm.getValue("TRANRSN_CODE2", 0)) +" ";
            	reason += this.getTransReason(inparm.getValue("TRANRSN_CODE3", 0)) +" ";
            }
            if("".equals(deptCode)){
            	deptCode = inparm.getValue("DEPT_CHN_DESC", 0) ;
            }
            if("".equals(diag)){
            	diag += this.getDiagDesc(inparm.getValue("DIAG_CODE1", 0)) +" ";
            	diag += this.getDiagDesc(inparm.getValue("DIAG_CODE2", 0)) +" ";
            	diag += this.getDiagDesc(inparm.getValue("DIAG_CODE3", 0)) +" ";
            }
            if("".equals(optDate)){
            	optDate = inparm.getValue("OPT_DATE", 0) ;
            }
            if("".equals(outDate)){
            	outDate = inparm.getValue("OUT_DATE", 0) ;
            }
            
            if("".equals(caseNo)){
            	caseNo = inparm.getValue("CASE_NO", 0) ;
            }
            
            parm.addData("ORG_BARCODE", inparm.getValue("ORG_BARCODE",0)) ;//院外条码
            
            //modify by lim 2012/05/06 begin
            parm.addData("BLD_CODE", inparm.getValue("SUBCAT_DESC", 0));//院内条码
            parm.addData("SUBCAT_CODE", "");//规格
            //modify by lim 2012/05/06 end
            
            parm.addData("BLD_TYPE", pat.getBloodType());//血型
            parm.addData("RH_FLG", pat.getBloodRHType());//RH血型
            String crossL = " " ;
            if("0".equals(outData.getValue("CROSS_MATCH_L",i))){
            	crossL = "无凝集无溶血" ;
            }else if("1".equals(outData.getValue("CROSS_MATCH_L",i))){
            	crossL = "有凝集无溶血" ;
            }else if("2".equals(outData.getValue("CROSS_MATCH_L",i))){
            	crossL = "无凝集有溶血" ;
            }else if("3".equals(outData.getValue("CROSS_MATCH_L",i))){
            	crossL = "有凝集有溶血" ;
            }
            parm.addData("CROSS_MATCH_L",crossL);
            
            String crossS = " " ;
            if("0".equals(outData.getValue("CROSS_MATCH_S",i))){
            	crossS = "无凝集无溶血" ;
            }else if("1".equals(outData.getValue("CROSS_MATCH_S",i))){
            	crossS = "有凝集无溶血" ;
            }else if("2".equals(outData.getValue("CROSS_MATCH_S",i))){
            	crossS = "无凝集有溶血" ;
            }else if("3".equals(outData.getValue("CROSS_MATCH_S",i))){
            	crossS = "有凝集有溶血" ;
            }            
            parm.addData("CROSS_MATCH_S",crossS);
            parm.addData("RESULT",
                         "1".equals(outData.getValue("RESULT",i)) ? "相合" :
                         "相斥");
            parm.addData("TEST_DATE",outData.getValue("TEST_DATE",i).substring(0, 16).replace('-', '/'));
        }
        
        parm.setCount(parm.getCount("BLOOD_NO"));
        parm.addData("SYSTEM", "COLUMNS", "ORG_BARCODE");
        parm.addData("SYSTEM", "COLUMNS", "BLOOD_NO");
        parm.addData("SYSTEM", "COLUMNS", "BLD_CODE");
        parm.addData("SYSTEM", "COLUMNS", "SUBCAT_CODE");
        parm.addData("SYSTEM", "COLUMNS", "BLD_TYPE");
        parm.addData("SYSTEM", "COLUMNS", "RH_FLG");
        parm.addData("SYSTEM", "COLUMNS", "CROSS_MATCH_L");
        parm.addData("SYSTEM", "COLUMNS", "CROSS_MATCH_S");
        parm.addData("SYSTEM", "COLUMNS", "RESULT");
        parm.addData("SYSTEM", "COLUMNS", "TEST_DATE");

        date.setData("TABLE", parm.getData());
        
        //modify by lim 2012/05/17 begin
        String deptDesc = "" ;
        String sql1 = "SELECT B.DEPT_CHN_DESC FROM ADM_INP A,SYS_DEPT B WHERE A.DEPT_CODE = B.DEPT_CODE AND A.CASE_NO='"+outData.getValue("CASE_NO", 0)+"'" ;
        //System.out.println("sql1::::::::::::::::::"+sql1);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql1)) ;
        
        if(result.getErrCode()<0 || result.getCount()<=0){
        	deptDesc = "" ;
        }else{
        	deptDesc = result.getValue("DEPT_CHN_DESC", 0) ;
        }
        //modify by lim 2012/05/17 end
        
        
        TParm inparm2 = SYSOperatorTool.getInstance().selectdata(outData.getValue("TEST_USER",0 ))  ;      
        //date.setData("PEIXUEZHE","TEXT","配血者:"+inparm2.getData("USER_NAME", 0)) ;
        //wuxy start 20170705
        int index  = table.getSelectedRow();
        TParm parmValue = new TParm();
        parmValue = table.getShowParmValue().getRow(index);
        
        // add by wangqing 20171225 宝龙要求，不显示配血人、复核人、发血
        /*date.setData("OUT_USER","TEXT",parmValue.getValue("OUT_USER")) ;//发血人
        date.setData("TEXT_USER","TEXT",parmValue.getValue("TEST_USER")) ;//配血人
        date.setData("RECHECK_USER","TEXT",parmValue.getValue("RECHECK_USER")) ;//复核人
*/        //wuxy end 20170705
        date.setData("OUT_NO", "TEXT", outNo);
        date.setData("REASON", "TEXT","输血原因:"+reason) ;
        date.setData("YXKS", "TEXT", "用血科室:"+deptDesc) ;
        String recpDate = "" ;
        if(null!=optDate && !"".equals(optDate)){
        	recpDate =optDate.substring(0, 16).replace('-', '/') ;
        }
        date.setData("TEST_DATE","TEXT"," 收检日期:"+recpDate) ;
        date.setData("JWSH","TEXT","诊断: "+diag) ;
        if(!"".equals(outDate)&& outDate!=null){
        	outDate = outDate.substring(0, 19).replace("-", "/") ;
        }
        date.setData("OUT_DATE","TEXT",outDate) ;
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSBloodOut.jhw", date);
    }
    
    private String getDiagDesc(String diagCode){
    	String desc = "" ;
    	if(diagCode!=null && !"".equals(diagCode)){
    		String sql = "SELECT A.ICD_CHN_DESC FROM SYS_DIAGNOSIS A WHERE A.ICD_CODE='"+diagCode+"'" ;
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result.getErrCode()<0){
    			return desc ;
    		}
    		if(result.getCount() <= 0){
    			return desc ;
    		}
    		desc = result.getValue("ICD_CHN_DESC", 0) ; 
    	}
    	return desc ;
    }
    
    private String getTransReason(String transReasonCode){
    	String desc = "" ;
    	if(transReasonCode!=null && !"".equals(transReasonCode)){
    		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='BMS_TRANRSN' AND ID='"+transReasonCode+"'" ;
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    		if(result.getErrCode()<0){
    			return desc ;
    		}
    		if(result.getCount() <= 0){
    			return desc ;
    		}
    		desc = result.getValue("CHN_DESC", 0) ; 
    	}
    	return desc ;
    }
    
    /**
     * 查询方法 
     */
    public  void  onQuery(){
    	String bldcode = this.getValueString("BLD_CODE");//血品
    	String subcatcode = this.getValueString("SUBCAT_CODE");//血品规格
    	String statecode = this.getValueString("STATE_CODE");//血液状况
    	String deptcode = this.getValueString("DEPT_CODE");//用血科室
    	
    	String sDate = this.getValueString("S_DATE") ;//出库时间 begin
    	String eDate = this.getValueString("E_DATE") ;//出库时间 end
    	//String indate = this.getValueString("IN_DATE");//入库日期
    	String ina = this.getValueString("IN_A") ;//入库时间 begin
    	String inb = this.getValueString("IN_B") ;//入库时间 end
    	String bldtype = this.getValueString("BLD_TYPE");//血型
    	String mrno  = this.getValueString("MR_NO");//病案号
    	String outNO = this.getValueString("OUT_NO") ;//出库单号
    	String textUser=this.getValueString("TEST_USER") ;//配血人员
    	String outUser=this.getValueString("OUT_USER") ;//发血人员
    	
    	this.bldCode = this.getTComBox("BLD_CODE").getSelectedName();
    	this.subcatCode = this.getTComBox("SUBCAT_CODE").getSelectedName();
    	this.stateCode = this.getTComBox("STATE_CODE").getSelectedName();
    	this.deptCode = this.getTComBox("DEPT_CODE").getSelectedName();
    	this.outUser = this.getText("OUT_USER");
    	
    	// add by wangqing 20180115 新增RH血型查询条件
    	String rhFlg = this.getValueString("RH_FLG") ;
//    	this.messageBox("{rhFlg="+rhFlg+"}");
    	
    	
    	
//    	String sql=" SELECT A.BLOOD_NO, A.RH_FLG, A.BLD_CODE, A.SUBCAT_CODE,"+
//    	  	       "        A.IN_DATE, A.BLD_TYPE, A.SHIT_FLG, A.END_DATE, A.IN_PRICE, A.BLOOD_VOL,"+ 
//    	  	       "        A.ORG_BARCODE, A.STATE_CODE, A.APPLY_NO, A.MR_NO, A.IPD_NO,"+ 
//    	  	       "        A.CASE_NO, A.ID_NO, A.USE_DATE, A.CROSS_MATCH_L, A.CROSS_MATCH_S,"+ 
//    	   	       "        A.ANTI_A, A.ANTI_B, A.RESULT, A.TEST_DATE, A.TEST_USER,"+ 
//    	   	       "        A.PRE_U, A.PRE_D, A.T, A.P, A.R,"+ 
//    	  	       "        A.WORK_USER, A.OUT_NO, A.OUT_DATE, A.OUT_USER,"+ 
//    	  	       "        A.TRAN_RESN,A.TRAN_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.DEPT_CODE ,B.PAT_NAME "+
//    	  	       " FROM BMS_BLOOD A ,SYS_PATINFO B "+ 
//    	  	       " WHERE A.MR_NO = B.MR_NO(+) " ; 
    	String sql=" SELECT A.BLOOD_NO, A.RH_FLG, A.BLD_CODE, A.SUBCAT_CODE,"+
	       "        A.IN_DATE, A.BLD_TYPE, A.SHIT_FLG, A.END_DATE, A.IN_PRICE, A.BLOOD_VOL,"+ 
	       "        A.ORG_BARCODE, A.STATE_CODE, A.APPLY_NO, A.MR_NO, A.IPD_NO,"+ 
	       "        A.CASE_NO, A.ID_NO, A.USE_DATE, A.CROSS_MATCH_L, A.CROSS_MATCH_S,"+ 
	       "        A.ANTI_A, A.ANTI_B, A.RESULT, A.TEST_DATE, A.TEST_USER,"+ 
	       "        A.PRE_U, A.PRE_D, A.T, A.P, A.R,"+ 
	       "        A.WORK_USER, A.OUT_NO, A.OUT_DATE, A.OUT_USER,"+ 
	       "        A.TRAN_RESN,A.TRAN_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.DEPT_CODE ,B.PAT_NAME,D.UNIT_CHN_DESC,C.UNIT_CODE, "+
	       
	       //add by yangjj 20150527
	       " 		A.RECHECK_USER,A.RECHECK_TIME " +  
	       
	       " FROM BMS_BLOOD A ,SYS_PATINFO B ,BMS_BLDSUBCAT C,SYS_UNIT D "+ 
	       " WHERE A.MR_NO = B.MR_NO(+) " +
	       "   AND A.BLD_CODE = C.BLD_CODE " +
	       "   AND A.SUBCAT_CODE = C.SUBCAT_CODE " +
	       "   AND C.UNIT_CODE = D.UNIT_CODE " ;     
   
    	StringBuilder sbuilder = new StringBuilder(sql) ;
    	if(bldcode!=null && !"".equals(bldcode)){//血品
    		sbuilder.append(" AND A.BLD_CODE= '"+bldcode+"'") ;
    	}
    	
    	// add by wangqing 20180115 新增RH血型查询条件
    	if(rhFlg!=null && !"".equals(rhFlg)){//血品
    		sbuilder.append(" AND A.RH_FLG= '"+rhFlg+"'") ;
    	}
    	  	
    	if(subcatcode!=null && !"".equals(subcatcode)){//血品规格
    		sbuilder.append(" AND A.SUBCAT_CODE= '"+subcatcode+"'") ;
    	}
    	if(statecode!=null && !"".equals(statecode)){//血液状态
    		sbuilder.append(" AND A.STATE_CODE= '"+statecode+"'") ;
    	} 
    	if(deptcode!=null && !"".equals(deptcode)){//用血科室
    		sbuilder.append(" AND A.DEPT_CODE='"+deptcode+"'") ;
    	}
    	
    	
    	this.outDate = "";
    	if((sDate!=null && !"".equals(sDate)) ){
    		sDate = sDate.substring(0, 19) ;
    		this.outDate = sDate.substring(0, 10);
    		sbuilder.append(" AND TO_CHAR(OUT_DATE,'YYYY-MM-DD HH:mm:ss') >= '"+sDate+"'") ;
    	} 
    	if((eDate!=null && !"".equals(eDate)) ) {
    		eDate = eDate.substring(0, 19) ;
    		if(!"".equals(this.outDate)) {
    			this.outDate += (" 至 " + eDate.substring(0, 10));
    		} else {
    			this.outDate += (" 0000-00-00 至 " + eDate.substring(0,10));
    		}
    		sbuilder.append(" AND TO_CHAR(OUT_DATE,'YYYY-MM-DD HH:mm:ss') <= '"+eDate+"'") ;
    	} 
    	
    	//add by huzc 20160308
    	this.inDate = "";
    	if((ina!=null && !"".equals(ina)) ){
    		ina = ina.substring(0, 19) ;
    		this.inDate = ina.substring(0, 10);
    		sbuilder.append(" AND TO_CHAR(IN_DATE,'YYYY-MM-DD HH:mm:ss') >= '"+ina+"'") ;
    	} 
    	if((inb!=null && !"".equals(inb)) ){
    		inb = inb.substring(0, 19) ;
    		if(!"".equals(this.outDate)) {
    			this.inDate += (" 至 " + inb.substring(0, 10));
    		} else {
    			this.inDate += (" 0000-00-00 至 " + inb.substring(0,10));
    		}
    		sbuilder.append(" AND TO_CHAR(IN_DATE,'YYYY-MM-DD HH:mm:ss') <= '"+inb+"'") ;
    	} else {
    		this.inDate += " 至 ";
    	}
    	//===pangben 2016-5-6 年龄查询条件
    	if(((TCheckBox)this.getComponent("CHK_AGE")).isSelected()){
    		if(this.getValueDouble("END_AGE")==0){
    			this.messageBox("结束年龄不能为0");
    			return;
    		}
    		sbuilder.append(" AND  TRUNC(MONTHS_BETWEEN(SYSDATE, B.BIRTH_DATE)/12) BETWEEN "+
    				this.getValueInt("START_AGE")+" AND "+this.getValueInt("END_AGE"));
    	}
    	/*  --change by huzc 20160308
    	if(indate!=null && !"".equals(indate)){//入库时间
    		String[] inDateArray = indate.split(" ") ;
    		sbuilder.append(" AND TO_CHAR(IN_DATE,'YYYY-MM-dd')= '"+inDateArray[0]+"' ") ;
    	}
    	*/
    	if(bldtype!=null && !"".equals(bldtype)){//血型
    		sbuilder.append(" AND A.BLD_TYPE= '"+bldtype+"' ") ;
    	}
    	if(mrno!=null && !"".equals(mrno)){//血型
    		sbuilder.append(" AND A.MR_NO= '"+mrno+"' ") ;
    	}  
    	if(outNO!=null && !"".equals(outNO)){//备血单号
    		sbuilder.append(" AND A.OUT_NO='"+outNO+"'") ;
    	}
    	if(textUser!=null&&!"".equals(textUser)){
    		sbuilder.append(" AND A.TEST_USER='"+textUser+"'");
    	}
    	if(outUser!=null&&!"".equals(outUser)){
    		sbuilder.append(" AND A.OUT_USER='"+outUser+"'");
    	}
    	sbuilder.append(" ORDER BY BLOOD_NO ") ;
    	System.out.println("sbuilder:::ssss:"+sbuilder);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sbuilder.toString()));
    	
    	
    	//   获得错误信息消息
    	if (result.getErrCode() < 0) {
    	    messageBox(result.getErrText());
    	    return;      
    	}
        if (result.getCount() <= 0) {
            messageBox("查无数据");
            this.callFunction("UI|TABLE|setParmValue", new TParm());
            return;
        }       	
    	
    	Set<String>  unitSet = new HashSet<String>() ;
    	double bldVol = 0.0 ;
    	for (int i = 0; i < result.getCount(); i++) {
    		unitSet.add(result.getValue("UNIT_CHN_DESC", i)) ;
    		bldVol+=result.getDouble("BLOOD_VOL", i) ;
			String crossMatchL = result.getValue("CROSS_MATCH_L", i) ;
			
			//modify by yangjj 20150527
			if("0".equals(crossMatchL)){
				result.addData("crossMatchL","无凝集无溶血") ;
			}else if("1".equals(crossMatchL)){
				result.addData("crossMatchL", "有凝集无溶血") ;
			}else if("2".equals(crossMatchL)){
				result.addData("crossMatchL","无凝集有溶血") ;
            }else if("3".equals(crossMatchL)){
            	result.addData("crossMatchL","有凝集有溶血") ;
            }else{
				result.addData("crossMatchL", " ") ;
			}
			
			String crossMatchS = result.getValue("CROSS_MATCH_S", i) ;
			if("0".equals(crossMatchS)){
				result.addData("crossMatchS","无凝集无溶血") ;
			}else if("1".equals(crossMatchS)){
				result.addData("crossMatchS", "有凝集无溶血") ;
			}else if("2".equals(crossMatchS)){
				result.addData("crossMatchS","无凝集有溶血") ;
            }else if("3".equals(crossMatchS)){
            	result.addData("crossMatchS","有凝集有溶血") ;
            }else{
				result.addData("crossMatchS", " ") ;
			}
			//配血结果
			String crossResult = result.getValue("RESULT", i) ;
			if("1".equals(crossResult)){
				result.addData("crossResult", "相合") ;
			}else if("2".equals(crossResult)){
				result.addData("crossResult", "相斥") ;
			}else{
				result.addData("crossResult", " ") ;
			}
		}
    	
    	if(unitSet.size()==1){
    		this.setValue("BLOOD_VOL", bldVol) ;
    		this.setValue("tLabel_14", unitSet.iterator().next() ) ;
    	}


    	//   呼叫功能parm赋值       
        this.callFunction("UI|TABLE|setParmValue", result);
};
    
    
     
    /**
     * 清空方法   
     */
    public void onClear() {
    	// add by wangqing 20180115 新增RH_FLG
        String clearString =
            "BLD_CODE;SUBCAT_CODE;IN_DATE;DEPT_CODE;MR_NO;" +
            "BLD_TYPE;STATE_CODE;OUT_DATE;BLOOD_VOL;OUT_NO;OUT_USER;RH_FLG";
        this.clearValue(clearString);
        this.setValue("TEST_USER", Operator.getID());
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间 
        this.setValue("E_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("S_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        ((TCheckBox)this.getComponent("CHK_AGE")).setSelected(true);
        onChkAge();
        table.removeRowAll(); 
    }
    
    /**
     * 汇出Excel
     */
    public void onExport() {
        //得到UI对应控件对象的方法
        TParm parm = table.getParmValue();
        if (null == parm || parm.getCount("BLD_CODE") <= 0) {
            this.messageBox("没有需要导出的数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "血库历史查询");
    }
    
    /**
     * 打印所有信息   add by wukai on 20160811
     */
    public void onPrintAll() {
    	if(table.getRowCount() <= 0) {
    		this.messageBox("无可打印数据！");
    		return;
    	}
    	
    	//System.out.println("RowCount ::::::::::::::::   "  + table.getRowCount());
    	if(table.getRowCount() > 1000) {
    		this.messageBox("打印数据不能超过 1000 条，当前条数 " + table.getRowCount());
    		return;
    	} 
    	
    	TParm data = new TParm();
    	//表头数据
    	data.setData("TITLE", "TEXT", "血库历史统计表");
    	data.setData("BLD_CODE", "TEXT", this.bldCode);
    	data.setData("SUBCAT_CODE", "TEXT", this.subcatCode);
    	data.setData("STATE_CODE", "TEXT", this.stateCode);
    	data.setData("OUT_DATE", "TEXT", this.outDate);
    	data.setData("IN_DATE", "TEXT", this.inDate);
    	data.setData("DEPT_CODE", "TEXT", this.deptCode);
    	data.setData("OUT_USER", "TEXT", this.outUser);
    	
    	//表格数据
    	TParm parm = new TParm();
    	TParm tableParm = table.getShowParmValue();
    	for(int i = 0; i < tableParm.getCount(); i++) {
    		parm.addData("BLOOD_NO", tableParm.getData("BLOOD_NO", i));
    		parm.addData("BLD_CODE", tableParm.getData("BLD_CODE", i));
    		parm.addData("SUBCAT_CODE", tableParm.getData("SUBCAT_CODE", i));
    		parm.addData("IN_DATE", tableParm.getData("IN_DATE", i));
    		parm.addData("STATE_CODE", tableParm.getData("STATE_CODE", i));
    		parm.addData("BLD_TYPE", tableParm.getData("BLD_TYPE", i));
    		
    		// add by wangqing 20180115 新增RH血型
    		parm.addData("RH_FLG", tableParm.getData("RH_FLG", i));
    		
    		parm.addData("SHIT_FLG", tableParm.getData("SHIT_FLG", i));
    		parm.addData("END_DATE", tableParm.getData("END_DATE", i));
    		parm.addData("APPLY_NO", tableParm.getData("APPLY_NO", i));
    		parm.addData("MR_NO", tableParm.getData("MR_NO", i));
    		parm.addData("PAT_NAME", tableParm.getData("PAT_NAME", i));
    		parm.addData("USE_DATE", tableParm.getData("USE_DATE", i));
    		parm.addData("crossMatchL", tableParm.getData("crossMatchL", i));
    		parm.addData("crossMatchS", tableParm.getData("crossMatchS", i));
    		parm.addData("ANTI_A", tableParm.getData("ANTI_A", i));
    		parm.addData("ANTI_B", tableParm.getData("ANTI_B", i));
    		parm.addData("crossResult", tableParm.getData("crossResult", i));
    		parm.addData("TEST_DATE", tableParm.getData("TEST_DATE", i));
    		parm.addData("TEST_USER", tableParm.getData("TEST_USER", i));
    		parm.addData("OUT_NO", tableParm.getData("OUT_NO", i));
    		parm.addData("TRAN_RESN", tableParm.getData("TRAN_RESN", i));
    		parm.addData("TRAN_DATE", tableParm.getData("TRAN_DATE", i));
    		parm.addData("OPT_USER", tableParm.getData("OPT_USER", i));
    		parm.addData("OPT_DATE", tableParm.getData("OPT_DATE", i));
    		parm.addData("OPT_TERM", tableParm.getData("OPT_TERM", i));
    		parm.addData("DEPT_CODE", tableParm.getData("DEPT_CODE", i));
    		parm.addData("RECHECK_USER", tableParm.getData("RECHECK_USER", i));
    		parm.addData("RECHECK_TIME", tableParm.getData("RECHECK_TIME", i));
    		parm.addData("OUT_USER", tableParm.getData("OUT_USER", i));
    		parm.addData("OUT_DATE", tableParm.getData("OUT_DATE", i));
    		
    		
    		//TEST_USER;OUT_NO;TRAN_RESN;TRAN_DATE;OPT_USER;OPT_DATE;OPT_TERM;DEPT_CODE;RECHECK_USER;RECHECK_TIME;OUT_USER;OUT_DATE
    	}
    	//BLOOD_NO;BLD_CODE;SUBCAT_CODE;IN_DATE;STATE_CODE;BLD_TYPE;SHIT_FLG;END_DATE;APPLY_NO;MR_NO;PAT_NAME;USE_DATE;
    	//crossMatchL;crossMatchS;ANTI_A;ANTI_B;crossResult;TEST_DATE;
    	parm.setCount(tableParm.getCount("BLOOD_NO"));
    	parm.addData("SYSTEM","COLUMNS","BLOOD_NO");
    	parm.addData("SYSTEM","COLUMNS","BLD_CODE");
    	parm.addData("SYSTEM","COLUMNS","SUBCAT_CODE");
    	parm.addData("SYSTEM","COLUMNS","IN_DATE");
    	parm.addData("SYSTEM","COLUMNS","STATE_CODE");
    	parm.addData("SYSTEM","COLUMNS","BLD_TYPE");
    	
    	// add by wangqing 20180115 新增RH血型
    	parm.addData("SYSTEM","COLUMNS","RH_FLG");
    	
    	parm.addData("SYSTEM","COLUMNS","SHIT_FLG");
    	parm.addData("SYSTEM","COLUMNS","END_DATE");
    	parm.addData("SYSTEM","COLUMNS","APPLY_NO");
    	parm.addData("SYSTEM","COLUMNS","MR_NO");
    	parm.addData("SYSTEM","COLUMNS","PAT_NAME");
    	parm.addData("SYSTEM","COLUMNS","USE_DATE");
    	parm.addData("SYSTEM","COLUMNS","crossMatchL");
    	parm.addData("SYSTEM","COLUMNS","crossMatchS");
    	parm.addData("SYSTEM","COLUMNS","ANTI_A");
    	parm.addData("SYSTEM","COLUMNS","ANTI_B");
    	parm.addData("SYSTEM","COLUMNS","crossResult");
    	parm.addData("SYSTEM","COLUMNS","TEST_DATE");
    	parm.addData("SYSTEM","COLUMNS","TEST_USER");
    	parm.addData("SYSTEM","COLUMNS","OUT_NO");
    	parm.addData("SYSTEM","COLUMNS","TRAN_RESN");
    	parm.addData("SYSTEM","COLUMNS","TRAN_DATE");
    	parm.addData("SYSTEM","COLUMNS","OPT_USER");
    	parm.addData("SYSTEM","COLUMNS","OPT_DATE");
    	parm.addData("SYSTEM","COLUMNS","OPT_TERM");
    	parm.addData("SYSTEM","COLUMNS","DEPT_CODE");
    	parm.addData("SYSTEM","COLUMNS","RECHECK_USER");
    	parm.addData("SYSTEM","COLUMNS","RECHECK_TIME");
    	parm.addData("SYSTEM","COLUMNS","OUT_USER");
    	parm.addData("SYSTEM","COLUMNS","OUT_DATE");
    	
    	
    	data.setData("TABLE",parm.getData());
    	
    	//表尾数据
    	data.setData("OPT_USER", "TEXT", Operator.getName());
		data.setData("OPT_TIME", "TEXT", SystemTool.getInstance().getDate().toString().substring(0, 10));
		//System.out.println( "print data :::::::   " + data);
		this.openPrintDialog("%ROOT%\\config\\prt\\bms\\BMSHistoryQuery.jhw", data);
    }
    
 /**
  * 表格单击事件
  */ 
  public void onTableClicked() {
     int row = table.getSelectedRow();
     if (row != -1) {
        this.setValue("IN_DATE", table.getItemData(row, "IN_DATE"));
        this.setValue("STATE_CODE", table.getItemData(row, "STATE_CODE"));
        this.setValue("ORG_BARCODE", table.getItemData(row, "ORG_BARCODE"));
        this.setValue("BLOOD_NO", table.getItemData(row, "BLOOD_NO"));
        this.setValue("BLD_CODE", table.getItemData(row, "BLD_CODE"));
        this.setValue("SUBCAT_CODE", table.getItemData(row, "SUBCAT_CODE"));
        this.setValue("BLD_TYPE", table.getItemData(row, "BLD_TYPE"));
        this.setValue("DEPT_CODE", table.getItemData(row, "DEPT_CODE"));
        this.setValue("MR_NO", table.getItemData(row, "MR_NO"));
        this.setValue("OUT_NO", table.getItemData(row, "OUT_NO")) ;
        // add by wangqing 20180115 新增RH血型
        this.setValue("RH_FLG", table.getItemData(row, "RH_FLG")) ;
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
	
	public void onMrNoAction(){
        String mr_no = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        this.setValue("MR_NO", mr_no) ;
        
		// modify by huangtt 20160928 EMPI患者查重提示 start
		Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			messageBox("病案号" + mr_no + " 已合并至 " + "" + pat.getMrNo());
			mr_no = pat.getMrNo();
			this.setValue("MR_NO", mr_no);
		}
		// modify by huangtt 20160928 EMPI患者查重提示 end
        
	}
	
	/**
	 * 获取TTextFormat对象
	 * @param tag
	 * @return
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) this.getComponent(tag);
	}
	
	/**
	 * 获取Combox
	 * @param tag
	 * @return
	 */
	public TComboBox getTComBox(String tag) {
		return (TComboBox) this.getComponent(tag);
	}
	
	/**
	 * 拿到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
	/**
	 * 年龄条件使用
	 */
	public void onChkAge(){
		if(((TCheckBox)this.getComponent("CHK_AGE")).isSelected()){
			callFunction("UI|START_AGE|setEnabled", true);	
			callFunction("UI|END_AGE|setEnabled", true);	
			this.setValue("START_AGE", 0);
			this.setValue("END_AGE", 150);
		}else{
			callFunction("UI|START_AGE|setEnabled", false);	
			callFunction("UI|END_AGE|setEnabled", false);	
			this.setValue("START_AGE", 0);
			this.setValue("END_AGE", 0);
		}
	}
}





















