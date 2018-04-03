package com.javahis.ui.mro;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Iterator; 
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;


import jdo.sta.bean.AAA02CType;
import jdo.sta.bean.AAA05CType;
import jdo.sta.bean.AAA06CType;
import jdo.sta.bean.AAA08CType;
import jdo.sta.bean.AAA13CType;
import jdo.sta.bean.AAA16CType;
import jdo.sta.bean.AAA18CType;
import jdo.sta.bean.AAA23CType;
import jdo.sta.bean.AAA26CType;
import jdo.sta.bean.AAAType;
import jdo.sta.bean.AAB02CType;
import jdo.sta.bean.AAB06CType;
import jdo.sta.bean.AABType;
import jdo.sta.bean.AAC02CType;
import jdo.sta.bean.AACType;
import jdo.sta.bean.AAType;
import jdo.sta.bean.ABAType;
import jdo.sta.bean.ABC03CType;
import jdo.sta.bean.ABCType;
import jdo.sta.bean.ABDSType;
import jdo.sta.bean.ABDType;
import jdo.sta.bean.ABF02CType;
import jdo.sta.bean.ABF03CType;
import jdo.sta.bean.ABFType;
import jdo.sta.bean.ABGType;
import jdo.sta.bean.ABHType;
import jdo.sta.bean.ABType;
import jdo.sta.bean.ACA06CType;
import jdo.sta.bean.ACA07CType;
import jdo.sta.bean.ACA0902CType;
import jdo.sta.bean.ACA0903CType;
import jdo.sta.bean.ACA09SType;
import jdo.sta.bean.ACA09Type;
import jdo.sta.bean.ACASType;
import jdo.sta.bean.ACAType;
import jdo.sta.bean.ACType;
import jdo.sta.bean.ADAType;
import jdo.sta.bean.ADType;
import jdo.sta.bean.AEBType;
import jdo.sta.bean.AED01CType;
import jdo.sta.bean.AEDType;
import jdo.sta.bean.AEEType;
import jdo.sta.bean.AEG01CType;
import jdo.sta.bean.AEG02CType;
import jdo.sta.bean.AEGType;
import jdo.sta.bean.AEI01CType;
import jdo.sta.bean.AEIType;
import jdo.sta.bean.AEJType;
import jdo.sta.bean.AEKSType;
import jdo.sta.bean.AEKType;
import jdo.sta.bean.AEMType;
import jdo.sta.bean.AENSType;
import jdo.sta.bean.AENType;
import jdo.sta.bean.AEType;
import jdo.sta.bean.AType;
import jdo.sta.bean.Age;
import jdo.sta.bean.BbdmDic;
import jdo.sta.bean.BirthedDays;
import jdo.sta.bean.CASES;
import jdo.sta.bean.CASEType;
import jdo.sta.bean.Email;
import jdo.sta.bean.IDCard;
import jdo.sta.bean.Min1Integer;
import jdo.sta.bean.Minute;
import jdo.sta.bean.MobilePhone;
import jdo.sta.bean.Morethan0Prize;
import jdo.sta.bean.ObjectFactory;
import jdo.sta.bean.OneORtwoType;
import jdo.sta.bean.Prize;
import jdo.sta.bean.TimeStamp;
import jdo.sta.bean.ZA01CType;
import jdo.sta.bean.ZA02CType;
import jdo.sta.bean.ZAType;
import jdo.sta.bean.ZB01CType;
import jdo.sta.bean.ZB02Type;
import jdo.sta.bean.ZB03Type;
import jdo.sta.bean.ZBType;
import jdo.sta.bean.ZType;
import jdo.sta.bean.ZipCode;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;


/**
 * @author caoyou
 * @date 2018年4月2日 上午11:11:56
 * @description 首页上传
 */
public class MROUploadControl extends TControl {
	private static String TABLE = "TABLE";
	private TTable table;
	private TCheckBox selectAll;
	private StringBuffer sb = new StringBuffer();
	/**
	 * @author caoyou
	 * @date 2018年4月2日 上午11:13:43
	 * @version 实用类包含基本的XML值作为常量
	 */
	public static final String W3C_XML_SCHEMA_NS_URI = "http://www.w3.org/2001/xmlschema";
	/**
	 * 用来存放错误信息  comment by wangqing 20171115
	 */
	private Map<String, String> map = new TreeMap<String, String>();
	private int recordCount = 0;//记录数
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	table = (TTable)this.getComponent("TABLE");
    	selectAll = (TCheckBox)this.getComponent("SELECT_ALL_FLG"); 
    	Timestamp sysDate = SystemTool.getInstance().getDate();
    	// 默认设置开始日期
    	this.setValue("START_DATE", StringTool.rollDate(sysDate, -7));
    	// 默认设置结束日期
    	this.setValue("END_DATE", sysDate);
    	table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,"onCheckBoxValue");
    	callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
    }
    
    /**
     * 查询方法
     */
    public void onQuery() {
    	String mrNo = this.getValueString("MR_NO");
    	if((!("".equals(mrNo))) && (mrNo.length()<8)){
    		String str = "";
    		for(int i = 0; i < 8-mrNo.length(); i++){
    			str+="0";
    		}
    		mrNo = str + mrNo;
    		this.setValue("MR_NO", mrNo);
    	}
    	
    	String start = this.getText("START_DATE");
    	String end = this.getText("END_DATE");
    	String inDept = this.getValueString("IN_DEPT_CODE");
    	String outDept = this.getValueString("OUT_DEPT_CODE");
    	//String mrNo = this.getValueString("MR_NO");
//    	String sql = " SELECT A.PAT_NAME,A.SEX,A.BIRTH_DATE,A.NATION,A.NB_ADM_WEIGHT,A.FOLK," +
//    			"A.IDNO,A.MARRIGE,A.H_ADDRESS,A.H_POSTNO,A.ADDRESS,A.TEL,A.POST_NO,A.OCCUPATION," +
//    			"A.O_ADDRESS,A.O_TEL,A.O_POSTNO,A.CONTACTER,A.RELATIONSHIP,A.CONT_ADDRESS,A.CONT_TEL," +
//    			"A.MRO_CTZ,A.NHI_CARDNO,A.MR_NO,A.IN_COUNT,A.IN_DATE,A.IN_DEPT,A.IN_ROOM_NO,A.ADM_SOURCE," +
//    			"A.OUT_DATE,A.OUT_DEPT,A.OUT_ROOM_NO,A.REAL_STAY_DAYS,A.OE_DIAG_CODE,A.OUT_DIAG_CODE1," +
//    			"A.PATHOLOGY_DIAG,A.PATHOLOGY_NO,A.MDIAG_BASIS,A.DIF_DEGREE,A.EX_RSN,A.TUMOR_STAG_T,A.TUMOR_STAG_N," +
//    			"A.TUMOR_STAG_M,A.ALLEGIC_FLG,A.ALLEGIC,A.QUALITY,A.CTRL_DR,A.CTRL_NURSE,A.CTRL_DATE,A.DIRECTOR_DR_CODE," +
//    			"A.PROF_DR_CODE,A.ATTEND_DR_CODE,A.ATTEND_DR_CODE,A.VS_DR_CODE,A.INDUCATION_DR_CODE,A.INTERN_DR_CODE," +
//    			"A.ENCODER,A.VS_NURSE_CODE,A.BLOOD_TYPE,A.RH_TYPE,A.RBC,A.PLATE,A.PLASMA,A.WHOLE_BLOOD,A.OTH_BLOOD," +
//    			"A.ICU_ROOM1,A.ICU_IN_DATE1,A.ICU_OUT_DATE1,A.VENTI_TIME,A.NB_WEIGHT,OUT_TYPE,A.TRAN_HOSP,A.AGN_PLAN_FLG," +
//    			"A.AGN_PLAN_INTENTION,A.BODY_CHECK,A.NURSING_GRAD_IN,A.NURSING_GRAD_OUT,A.SUM_TOT " +
//    			"FROM MRO_RECORD A " +
//    			"WHERE A.OUT_DATE BETWEEN TO_DATE('"+start+"','YYYY-MM-DD') AND TO_DATE('"+end+"','YYYY-MM-DD') ";
    	
    	String sql = " SELECT PAT_NAME,CASE_NO,MR_NO,IDNO,IN_DEPT,OUT_DEPT,IN_DATE,OUT_DATE,UPLOAD_FLG " +
    				 "FROM MRO_RECORD " +
    				 "WHERE (OUT_DATE BETWEEN TO_DATE('"+start+"','YYYY-MM-DD') AND TO_DATE('"+end+"','YYYY-MM-DD')) " +
    				 		" AND ADMCHK_FLG = 'Y' AND DIAGCHK_FLG = 'Y' AND BILCHK_FLG = 'Y'  AND QTYCHK_FLG = 'Y' ";
    				 if(!inDept.isEmpty()){
    					 sql+= "AND IN_DEPT = '"+inDept+"' ";
    				 }
    				 if(!outDept.isEmpty()){
    					 sql+="AND OUT_DEPT = '"+outDept+"' ";
    				 }
    				 if(!mrNo.isEmpty()){
    					 sql+="AND MR_NO = '"+mrNo+"' ";
    				 }
    				 
    				 sql+="ORDER BY OUT_DATE DESC ";
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	for(int i = 0; i < parm.getCount(); i++){
    		parm.addData("SELECT_FLG", false);
    	}
    	if (parm.getErrCode() < 0) {
    	    messageBox(parm.getErrText());
    	    return;      
    	}   
    	
    	if (parm == null || parm.getCount() <= 0) {
            this.messageBox("没有查询数据");
            this.callFunction("UI|TABLE|setParmValue", new TParm());
            return;
        }
    	table.setParmValue(parm);
    }
    /**
     * 生成上传的XML文件
     */
    public void onUpload(){
    	int count = table.getRowCount();
    	boolean select_flg;
    	TParm parm = new TParm();
    	TParm tableParm = table.getParmValue();
    	for(int i = 0; i < count; i++){
    		select_flg = Boolean.parseBoolean(table.getItemData(i, 0).toString());
    		if(select_flg){
    			recordCount++;
    			parm.addData("CASE_NO", tableParm.getValue("CASE_NO",i));
    		}
    	}
    	if(parm.getCount("CASE_NO")>0){
    		creatXML(parm);
    		recordCount=0;
    	}
    	else{
    		this.messageBox("请选择需要生成XML文件的数据！");
    	}
    		
    }
    public void onCancle(){
    	int count = table.getRowCount();
    	boolean select_flg;
    	TParm parm = new TParm();
    	TParm tableParm = table.getParmValue();
    	for(int i = 0; i < count; i++){
    		select_flg = Boolean.parseBoolean(table.getItemData(i, 0).toString());
    		if(select_flg){
    			parm.addData("CASE_NO", tableParm.getValue("CASE_NO",i));
    		}
    	}
    	if(parm.getCount("CASE_NO")>0){
    		String updateSql;
    		for(int i = 0; i < parm.getCount("CASE_NO"); i++){
    			updateSql = " UPDATE MRO_RECORD SET UPLOAD_FLG = '' WHERE CASE_NO = '"+parm.getValue("CASE_NO",i)+"' ";
    			TParm cancelParm =new TParm(TJDODBTool.getInstance().update(updateSql));
    			if(cancelParm.getErrCode()<0){
    	    		this.messageBox("取消失败！");
    	    	}else{
    	    		this.messageBox("取消成功！");
    	    		onQuery();
    	    	}
    		}
    	}
    	else{
    		this.messageBox("请选择需要取消已生成的数据！");
    	}
    }
    /**
     * 生成XML文件
     * @param parm
     */
    private void creatXML(TParm parm) {
    	try {
			JAXBContext context = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);//
			m.setProperty(Marshaller.JAXB_ENCODING, "GBK");//编码
			m.setProperty(Marshaller.JAXB_FRAGMENT, false);//
			ObjectFactory object = new ObjectFactory();
			CASES cases = object.createCASES();
			ZType zt = new ZType();
			
			String headSql = " SELECT * FROM MRO_UPLOADHEAD ";
			TParm headParm = new TParm(TJDODBTool.getInstance().select(headSql));
			ZAType za = new ZAType();
			if((!"".equals(headParm.getValue("DISTRICT_CODE", 0))) && (headParm.getValue("DISTRICT_CODE", 0).length()==6)){
				ZA01CType za01c = new ZA01CType();
				za01c.setValue(headParm.getValue("DISTRICT_CODE", 0));//行政区化代码
				za.setZA01C(za01c);
			}else{
				errMessage("ZA01C行政区划代码(6字符)", "NODE","");
			}
			if((!"".equals(headParm.getValue("ORGANIZATION_CODE", 0))) && (headParm.getValue("ORGANIZATION_CODE", 0).length()==9)){
				ZA02CType za02c = new ZA02CType();
				za02c.setValue(headParm.getValue("ORGANIZATION_CODE", 0));//组织机构代码
				za.setZA02C(za02c);
			}else{
				errMessage("ZA02C组织机构代码(9字符)", "NODE","");
			}
			if((!"".equals(headParm.getValue("ORGANIZATION_NAME", 0))) && (headParm.getValue("ORGANIZATION_NAME", 0).length()<=200)){
				jdo.sta.bean.String za03 = new jdo.sta.bean.String();
				za03.setValue(headParm.getValue("ORGANIZATION_NAME", 0));//机构名称
				za.setZA03(za03);
			}else{
				errMessage("ZA03机构名称(200字符内)", "NODE","");
			}
			if((!"".equals(headParm.getValue("LEADER_NAME", 0))) && (headParm.getValue("LEADER_NAME", 0).length()<=20)){
				jdo.sta.bean.String za04 = new jdo.sta.bean.String();
				za04.setValue(headParm.getValue("LEADER_NAME", 0));//单位负责人
				za.setZA04(za04);
			}else{
				errMessage("ZA04单位负责人(20字符内)", "NODE","");
			}
			zt.setZA(za);
			
			ZBType zb = new ZBType();
			ZB01CType zb01c = new ZB01CType();
			zb01c.setValue(BbdmDic.B_WT_4_2012);
			zb.setZB01C(zb01c);
			ZB02Type zb02 = new ZB02Type();
			//Calendar c = Calendar.getInstance();
			BigInteger zb02cBig = new BigInteger(String.valueOf(Calendar.getInstance().get(Calendar.YEAR)));//当前年份
			zb02.setValue(zb02cBig);
			zb.setZB02(zb02);
			ZB03Type zb03 = new ZB03Type();
			int zb03Int = Integer.parseInt(String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1));
			zb03.setValue(zb03Int);
			zb.setZB03(zb03);
			if((!"".equals(headParm.getValue("SUBMITTER_NAME", 0)))&&(headParm.getValue("SUBMITTER_NAME", 0).length()<=20)){
				jdo.sta.bean.String zb04 = new jdo.sta.bean.String();
				zb04.setValue(headParm.getValue("SUBMITTER_NAME", 0));//提交人
				zb.setZB04(zb04);
			}else{
				errMessage("ZB04填报人(20字符内)", "NODE","");
			}
			if((!"".equals(headParm.getValue("SUBMITTER_TEL", 0)))&&(headParm.getValue("SUBMITTER_TEL", 0).length()<=20)){
				jdo.sta.bean.String zb05 = new jdo.sta.bean.String();
				zb05.setValue(headParm.getValue("SUBMITTER_TEL", 0));//提交人电话
				zb.setZB05(zb05);
			}else{
				errMessage("ZB05填报人联系电话(20字符内)", "NODE","");
			}
			
			TimeStamp zb06 = new TimeStamp();
			zb06.setValue(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			zb.setZB06(zb06);
			if(String.valueOf(recordCount).length()<=4){
				Min1Integer zb07 = new Min1Integer();
				BigInteger zb07Big = new BigInteger(String.valueOf(recordCount));//记录数
				zb07.setValue(zb07Big);
				zb.setZB07(zb07);
			}else{
				errMessage("ZB07记录数(4位内)", "NODE","");
			}
			if((!"".equals(headParm.getValue("MAIL", 0)))&&(headParm.getValue("MAIL", 0).length()<=50)){
				Email zb08 = new Email();
				zb08.setValue(headParm.getValue("MAIL", 0));//邮箱
				JAXBElement<Email> zb08Jax = new JAXBElement<Email>(new QName("ZB08"), Email.class, zb08);
				zb.setZB08(zb08Jax);
			}else{
				errMessage("ZB08邮箱(50字符内)", "NODE","");
			}
			if((!"".equals(headParm.getValue("MOBILE", 0)))&&(headParm.getValue("MOBILE", 0).length()==11)){
				MobilePhone zb09 = new MobilePhone();
				zb09.setValue(headParm.getValue("MOBILE", 0));//手机
				JAXBElement<MobilePhone> zb09Jax = new JAXBElement<MobilePhone>(new QName("ZB09"), MobilePhone.class, zb09);
				zb.setZB09(zb09Jax);
			}
			//zb.setZB10(creat("ZB10"));
			zt.setZB(zb);
			
			cases.setZ(zt);
			for(int i = 0; i < parm.getCount("CASE_NO"); i++){
				// commented by wangqing 20171115 创建CASE节点
				CASEType ct = creatCaseType(parm.getValue("CASE_NO",i));
				cases.getCASE().add(ct);
			}
		    
			// 没有错误信息
			if(map.isEmpty()){
				File file = new File(getPath()+"\\"+new SimpleDateFormat("yyyy-MM-dd").format(new Date())+".xml");
//				// modified by wangqing 20171115 start
                FileOutputStream out = new FileOutputStream(file);
				m.marshal(cases, out);
//				// modified by wangqing 20171115 end
//				m.marshal(cases, file);
				//压缩xml文件
				creatZip(file.getPath());
				file.delete();
				this.messageBox("生成XML文件成功！");
				//已生成存入库中
				String updateSql;
				for(int i = 0; i < parm.getCount("CASE_NO"); i++){
					updateSql = " UPDATE MRO_RECORD SET UPLOAD_FLG = '已生成' WHERE CASE_NO = '"+parm.getValue("CASE_NO",i)+"' ";
					new TParm(TJDODBTool.getInstance().update(updateSql));
				}
			}
			// 有错误信息
			else{
				
				FileWriter fw=new FileWriter(getPath()+"\\err.txt");
				Set<String> set = map.keySet();
				for(Iterator<String> iter = set.iterator(); iter.hasNext();){  
					String key = iter.next();
		            sb.append(map.get(key)); 
		            sb.append(System.getProperty("line.separator"));// 换行		            
		            System.out.println("@test by wangqing@---{key="+key+",value="+map.get(key)+"}");
		        } 
				fw.write(sb.toString());
				fw.flush();
				fw.close();
				map.clear();
				sb.setLength(0);
				this.messageBox("生成XML文件失败！请查看错误信息！");
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			
		}
	}
    /**
     * 创建XML文件中的CASE节点
     * @param mrNo
     * @return
     */
    private CASEType creatCaseType(String caseNo){
    	
    	DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    	Date date = new Date();
    	DatatypeFactory dataTypeFactory = null;
    	
    	String sql = " SELECT A.PAT_NAME,A.SEX,A.BIRTH_DATE,(TO_NUMBER(TO_CHAR(A.IN_DATE,'YYYY')) - TO_NUMBER(TO_CHAR(A.BIRTH_DATE,'YYYY'))) AS AGE," +
    			"A.NATION,ROUND(A.IN_DATE - A.BIRTH_DATE) AS DAYS,A.NB_ADM_WEIGHT,A.FOLK," +
		"A.IDNO,A.MARRIGE,A.H_ADDRESS,A.H_POSTNO,A.ADDRESS,A.TEL,A.POST_NO,A.OCCUPATION," +
		"A.O_ADDRESS,A.O_TEL,A.O_POSTNO,A.CONTACTER,A.RELATIONSHIP,A.CONT_ADDRESS,A.CONT_TEL," +
		"A.MRO_CTZ,A.NHI_CARDNO,A.MR_NO,A.IN_COUNT,TO_CHAR(A.IN_DATE,'YYYY-MM-DD HH24:MI:SS') AS IN_DATE,A.IN_DEPT,A.IN_ROOM_NO,A.ADM_SOURCE," +
		"TO_CHAR(A.OUT_DATE,'YYYY-MM-DD HH24:MI:SS') AS OUT_DATE,A.OUT_DEPT,A.OUT_ROOM_NO,A.REAL_STAY_DAYS,A.OE_DIAG_CODE,A.OUT_DIAG_CODE1,A.OUT_DIAG_CONDITION1," +
		"A.PATHOLOGY_DIAG,A.PATHOLOGY_NO,A.MDIAG_BASIS,A.DIF_DEGREE,A.EX_RSN,A.TUMOR_STAG_T,A.TUMOR_STAG_N," +
		"A.TUMOR_STAG_M,A.TUMOR_STAG,A.ALLEGIC_FLG,A.ALLEGIC,A.QUALITY,A.CTRL_DR,A.CTRL_NURSE,A.CTRL_DATE,A.DIRECTOR_DR_CODE," +
		"A.PROF_DR_CODE,A.ATTEND_DR_CODE,A.ATTEND_DR_CODE,A.VS_DR_CODE,A.INDUCATION_DR_CODE,A.INTERN_DR_CODE," +
		"A.ENCODER,A.VS_NURSE_CODE,A.BLOOD_TYPE,A.RH_TYPE,A.RBC,A.PLATE,A.PLASMA,A.WHOLE_BLOOD,A.OTH_BLOOD,A.BE_COMA_TIME,A.AF_COMA_TIME," +
		"A.ICU_ROOM1,TO_CHAR(A.ICU_IN_DATE1,'YYYY-MM-DD HH24:MI:SS') AS ICU_IN_DATE1,TO_CHAR(A.ICU_OUT_DATE1,'YYYY-MM-DD HH24:MI:SS') AS ICU_OUT_DATE1,A.VENTI_TIME,A.NB_WEIGHT,A.NB_DEFECT_CODE,OUT_TYPE,A.TRAN_HOSP,A.AGN_PLAN_FLG," +
		"A.AGN_PLAN_INTENTION,A.BODY_CHECK,A.NURSING_GRAD_IN,A.NURSING_GRAD_OUT,A.SUM_TOT " +
		"FROM MRO_RECORD A " +
		"WHERE A.CASE_NO = '"+caseNo+"' "; 
    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	CASEType ct = new CASEType();
    	AType a = new AType();
    	
    	AAType aa = new AAType();
    	
    	AAAType aaa = new AAAType();
    	if((!"".equals(parm.getValue("PAT_NAME",0)))&&(parm.getValue("PAT_NAME",0).length()<=50)){
	    	//<AAA01 description="姓名">张三</AAA01>
	    	jdo.sta.bean.String str = new jdo.sta.bean.String();
	    	str.setValue(parm.getValue("PAT_NAME",0));
	    	aaa.setAAA01(str);
    	}else{
    		errMessage("AAA01姓名(50字符内)", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	if(!"".equals(parm.getValue("SEX",0))){
	    	//<AAA02C description="性别代码《指标》">1</AAA02C>
	    	AAA02CType aaa02c = new AAA02CType();
	    	aaa02c.setValue(parm.getValue("SEX",0));
	    	aaa.setAAA02C(aaa02c);
    	}else{
    		errMessage("AAA02C性别", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	if(!"".equals(parm.getValue("BIRTH_DATE",0))){
	    	//<AAA03 description="出生日期">1982-02-28</AAA03>
	    	try {
				date = format.parse(parm.getValue("BIRTH_DATE",0));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			try {
				dataTypeFactory = DatatypeFactory.newInstance();
			} catch (DatatypeConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			GregorianCalendar gc = new GregorianCalendar();
			gc.setTimeInMillis(date.getTime());
			XMLGregorianCalendar xmlgc = dataTypeFactory.newXMLGregorianCalendarDate(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1, gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
	    	jdo.sta.bean.Date staDate = new jdo.sta.bean.Date();
	    	staDate.setValue(xmlgc);
	    	aaa.setAAA03(staDate);
    	}else{
    		errMessage("AAA03出生日期", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	if(!"".equals(parm.getValue("AGE", 0))){
	    	//<AAA04 description="年龄(岁)">28</AAA04>
	    	Age aaa04 = new Age();
	    	aaa04.setValue(Integer.parseInt(parm.getValue("AGE", 0)));
	    	JAXBElement<Age> aaa04Jax = new JAXBElement<Age>(new QName("AAA04"), Age.class, aaa04);
	    	aaa.setAAA04(aaa04Jax);
    	}
    	if(!"".equals(parm.getValue("NATION", 0))){
	    	//卫计委对应的国籍代码
	    	String nationSql = " SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_NATION' " +
	    			     "AND ID = '"+parm.getValue("NATION", 0)+"' ";
	    	TParm nationParm = new TParm(TJDODBTool.getInstance().select(nationSql));
	    	//<AAA05C description="国籍代码《指标》">1</AAA05C>
	    	AAA05CType aaa05c = new AAA05CType();
	    	aaa05c.setValue(nationParm.getValue("STA4_CODE", 0));
	    	JAXBElement<AAA05CType> aaa05cJax = new JAXBElement<AAA05CType>(new QName("AAA05C"), AAA05CType.class, aaa05c);
	    	aaa.setAAA05C(aaa05cJax);
	    	}
	    	if((!"".equals(parm.getValue("DAYS", 0)))&&(new Integer(parm.getValue("DAYS", 0))<365)&&(new Integer(parm.getValue("DAYS", 0))>0)){
	    	//<AAA40 description="年龄不足1周岁天数">1</AAA40>
	    	BirthedDays aaa40 = new BirthedDays();
	    	aaa40.setValue(Integer.parseInt(parm.getValue("DAYS", 0)));
	    	JAXBElement<BirthedDays> aaa40Jax = new JAXBElement<BirthedDays>(new QName("AAA40"), BirthedDays.class, aaa40);
	    	aaa.setAAA40(aaa40Jax);
	    	}
	    	if((!"".equals(parm.getValue("NB_ADM_WEIGHT", 0)))&&(Integer.parseInt(parm.getValue("NB_ADM_WEIGHT", 0))>0)){
			//<AAA42 description="新生儿入院体重（克）">3500</AAA42>
	    	aaa.setAAA42(creat("AAA42", new Integer(parm.getValue("NB_ADM_WEIGHT", 0))));
    	}
    	if(!"".equals(parm.getValue("FOLK", 0))){
    		String specieSql = " SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SPECIES' " +
   	     					   "AND ID = '"+parm.getValue("FOLK", 0)+"' ";
	       	TParm specieParm = new TParm(TJDODBTool.getInstance().select(specieSql));
			//<AAA06C description="民族代码《指标》">1</AAA06C>
	    	AAA06CType aaa06c = new AAA06CType();
	    	aaa06c.setValue(specieParm.getValue("STA4_CODE",0));
	    	JAXBElement<AAA06CType> aaa06cJax = new JAXBElement<AAA06CType>(new QName("AAA06C"), AAA06CType.class, aaa06c);
	    	aaa.setAAA06C(aaa06cJax);
    	}
    	if(!"".equals(parm.getValue("IDNO", 0))&&((parm.getValue("IDNO", 0).length()==15)||(parm.getValue("IDNO", 0).length()==18))){
			//<AAA07 description="身份证号">23060219681005488X</AAA07>
	    	IDCard aaa07 = new IDCard();
	    	aaa07.setValue(parm.getValue("IDNO", 0));
	    	JAXBElement<IDCard> aaa07Jax = new JAXBElement<IDCard>(new QName("AAA07"), IDCard.class, aaa07);
	    	aaa.setAAA07(aaa07Jax);
    	}
    	if(!"".equals(parm.getValue("MARRIGE",0))){
    		String marrigeSql = " SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SPECIES' " +
				   "AND ID = '"+parm.getValue("MARRIGE", 0)+"' ";
    		TParm marrigeParm = new TParm(TJDODBTool.getInstance().select(marrigeSql));
			//<AAA08C description="婚姻状况代码《指标》">2</AAA08C>
	    	AAA08CType aaa08c = new AAA08CType();
	    	aaa08c.setValue(marrigeParm.getValue("STA4_CODE",0));
	    	aaa.setAAA08C(aaa08c);
    	}else{
    		errMessage("AAA08C婚姻状况代码", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	
		//<AAA09 description="出生地省（区、市）">山东省</AAA09>
    	//aaa.setAAA09();
    	
		//<AAA10 description="出生地市">济南市</AAA10>
    	//aaa.setAAA10();
    	
		//<AAA11 description="出生地县">历下区</AAA11>
    	//aaa.setAAA11();
    	
		//<AAA43 description="籍贯省（区、市）">山东省</AAA43>
    	//aaa.setAAA43();
    	
		//<AAA44 description="籍贯市">济南市</AAA44>
    	//aaa.setAAA44();
    	
		//<AAA45 description="户籍省（区、市）">山东省</AAA45>
    	//aaa.setAAA45();
		//<AAA46 description="户籍市">济南市</AAA46>
    	//aaa.setAAA46();
		//<AAA47 description="户籍县">历下区</AAA47>
    	//aaa.setAAA47();
    	
    	
    	if(!"".equals(parm.getValue("H_ADDRESS", 0))){
			//<AAA12 description="户籍详细地址">山东省济南市历下区</AAA12>
	    	aaa.setAAA12(creat("AAA12",parm,"H_ADDRESS"));
    	}
    	if(!"".equals(parm.getValue("H_POSTNO", 0))){
    		String postSql = " SELECT STA3_CODE FROM SYS_POSTCODE WHERE POST_CODE = '"+parm.getValue("H_POSTNO", 0)+"' ";
    		TParm postParm = new TParm(TJDODBTool.getInstance().select(postSql));
			//<AAA13C description="户籍地址区县编码《指标》">370000</AAA13C>
	    	AAA13CType aaa13c = new AAA13CType();
	    	aaa13c.setValue(postParm.getValue("STA3_CODE",0));
	    	JAXBElement<AAA13CType> aaa13cJax = new JAXBElement<AAA13CType>(new QName("AAA13C"), AAA13CType.class, aaa13c);
	    	aaa.setAAA13C(aaa13cJax);
    	}
//			//<AAA33C description="户籍街道乡镇代码《指标》">110101001010</AAA33C>
//	    	String aaa33c = parm.getValue("",0);
//	    	JAXBElement<String> aaa33cJax = new JAXBElement<String>(new QName("AAA33C"), String.class, aaa33c);
//	    	aaa.setAAA33C(aaa33cJax);
    	if(!"".equals(parm.getValue("H_POSTNO", 0))&&parm.getValue("H_POSTNO", 0).length()==6){
			//<AAA14C description="户籍地址邮政编码">163311</AAA14C>
	    	ZipCode aaa14c = new ZipCode();
	    	aaa14c.setValue(parm.getValue("H_POSTNO", 0));
	    	aaa.setAAA14C(creat("AAA14C", aaa14c));
    	}
    	if(!"".equals(parm.getValue("ADDRESS", 0))){
			//<AAA15 description="现住址详细地址（居住半年以上）">北京市东城区和平里大街</AAA15>
	    	aaa.setAAA15(creat("AAA15",parm,"ADDRESS"));
    	}
		//<AAA48 description="现住址省（区、市）（居住半年以上）">北京市</AAA48>
    	//aaa.setAAA48(creat("AAA48"));
		//<AAA49 description="现住址市">北京市</AAA49>
    	//aaa.setAAA49(creat("AAA49"));
		//<AAA50 description="现住址县">东城区</AAA50>
    	//aaa.setAAA50(creat("AAA50"));
    	if(!"".equals(parm.getValue("POST_NO", 0))){
    		//<AAA16C description="现住址区县编码（居住半年以上）《指标》">110101</AAA16C>
    		String postSql = " SELECT STA3_CODE FROM SYS_POSTCODE WHERE POST_CODE = '"+parm.getValue("POST_NO", 0)+"' ";
    		TParm postParm = new TParm(TJDODBTool.getInstance().select(postSql));
    		AAA16CType aaa16c = new AAA16CType();
        	aaa16c.setValue(postParm.getValue("STA3_CODE", 0));
        	aaa.setAAA16C(aaa16c);
    	}else{
    		errMessage("AAA16C现住址区县编码", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
		//<AAA36C description="现住址街道乡镇代码（居住半年以上）《指标》">110101004009</AAA36C>
    	//String aaa36c = parm.getValue("",0); 
    	//aaa.setAAA36C(creat("AAA36C",aaa36c));
    	if(!"".equals(parm.getValue("TEL", 0))){
			//<AAA51 description="现住址电话">01080725810</AAA51>
	    	aaa.setAAA51(creat("AAA51",parm,"TEL"));
    	}
    	if(!"".equals(parm.getValue("POST_NO", 0))&&parm.getValue("POST_NO", 0).length()==6){
			//<AAA17C description="现住址邮政编码（居住半年以上）">163311</AAA17C>
	    	ZipCode aaa17c = new ZipCode();
	    	aaa17c.setValue(parm.getValue("POST_NO", 0));
	    	aaa.setAAA17C(creat("AAA17C", aaa17c));
    	}
    	//if(!"".equals(parm.getValue("OCCUPATION", 0))){
    		//this.messageBox(parm.getValue("OCCUPATION", 0));
			//<AAA18C description="职业代码《指标》">11</AAA18C>
    		String occupationSql = "SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_OCCUPATION' AND ID = '"+parm.getValue("OCCUPATION", 0)+"' ";
    		TParm occupationParm = new TParm(TJDODBTool.getInstance().select(occupationSql));
	    	AAA18CType aaa18c = new AAA18CType();
	    	if(!"".equals(occupationParm.getValue("STA4_CODE", 0))){
	    		aaa18c.setValue(occupationParm.getValue("STA4_CODE", 0));
	    	}else{
	    		aaa18c.setValue("90");//职业代码为其他
	    	}
	    	aaa.setAAA18C(aaa18c);
    	//}
    	if(!"".equals(parm.getValue("O_ADDRESS", 0))){
			//<AAA19 description="工作单位及地址">北京市海淀区上地东路1号联想大厦</AAA19>
	    	aaa.setAAA19(creat("AAA19",parm,"O_ADDRESS"));
    	}
    	if(!"".equals(parm.getValue("O_TEL", 0))){
			//<AAA20 description="工作单位电话">829397645</AAA20>
	    	aaa.setAAA20(creat("AAA20",parm,"O_TEL"));
    	}
    	if(!"".equals(parm.getValue("O_POSTNO", 0))&&(parm.getValue("O_POSTNO", 0).length()==6)){
			//<AAA21C description="工作单位邮政编码">163311</AAA21C>
	    	ZipCode aaa21c = new ZipCode();
	    	aaa21c.setValue(parm.getValue("O_POSTNO", 0));
	    	aaa.setAAA21C(creat("AAA21C", aaa21c));
    	}
    	if(!"".equals(parm.getValue("CONTACTER", 0))){
			//<AAA22 description="联系人姓名">张三</AAA22>
	    	aaa.setAAA22(creat("AAA22",parm,"CONTACTER"));
    	}
    	if(!"".equals(parm.getValue("RELATIONSHIP", 0))){
    		String relationSql = " SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_RELATIONSHIP' AND ID = '"+parm.getValue("RELATIONSHIP", 0)+"' ";
    		TParm relationParm = new TParm(TJDODBTool.getInstance().select(relationSql));
			//<AAA23C description="联系人关系代码《指标》">9</AAA23C>
	    	AAA23CType aaa23c = new AAA23CType();
	    	aaa23c.setValue(relationParm.getValue("STA4_CODE",0));
	    	JAXBElement<AAA23CType> aaa23cJax = new JAXBElement<AAA23CType>(new QName("AAA23C"), AAA23CType.class, aaa23c);
	    	aaa.setAAA23C(aaa23cJax);
    	}
    	if(!"".equals(parm.getValue("CONT_ADDRESS", 0))){
			//AAA24 description="联系人地址">北京市海淀区上地东路1号联想大厦</AAA24>
	    	aaa.setAAA24(creat("AAA24",parm,"CONT_ADDRESS"));
    	}
    	if(!"".equals(parm.getValue("CONT_TEL", 0))){
			//<AAA25 description="联系人电话">83431231</AAA25>
	    	aaa.setAAA25(creat("AAA25",parm,"CONT_TEL"));
    	}
    	if(!"".equals(parm.getValue("MRO_CTZ", 0))){
			//<AAA26C description="医疗付费方式代码《指标》">6</AAA26C>
	    	AAA26CType aaa26c = new AAA26CType();
	    	aaa26c.setValue(parm.getValue("MRO_CTZ", 0));
	    	aaa.setAAA26C(aaa26c);
    	}else{
    		errMessage("AAA26C医疗付费方式代码", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	if(!"".equals(parm.getValue("NHI_CARDNO", 0))){
			//<AAA27 description="医疗保险手册(卡)号（健康卡号）">11111111111</AAA27>
	    	aaa.setAAA27(creat("AAA27",parm,"NHI_CARDNO"));
    	}
    	if(!"".equals(parm.getValue("MR_NO", 0))){
			//<AAA28 description="病案号">2012123101</AAA28>
	    	jdo.sta.bean.String aaa28 = new jdo.sta.bean.String();
	    	aaa28.setValue(parm.getValue("MR_NO", 0));
	    	aaa.setAAA28(aaa28);
    	}else{
    		errMessage("AAA28病案号", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	if((!"".equals(parm.getValue("IN_COUNT", 0)))&&(Integer.parseInt(parm.getValue("IN_COUNT", 0))>0)){
			//<AAA29 description="住院次数">5</AAA29>
	    	int aaa29 = Integer.parseInt(parm.getValue("IN_COUNT", 0));
	    	aaa.setAAA29(aaa29);
    	}else{
    		errMessage("AAA29住院次数", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	aa.setAAA(aaa);
    	
    	
    	AABType aab = new AABType();
    	if(!"".equals(parm.getValue("IN_DATE", 0))){
			//<AAB01 description="入院时间(时)">2013-05-05 10:10:10</AAB01>
	    	TimeStamp aab01 = new TimeStamp();
	    	aab01.setValue(parm.getValue("IN_DATE", 0));
	    	aab.setAAB01(aab01);
    	}else{
    		errMessage("AAB01入院时间(时)", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	if(!"".equals(parm.getValue("IN_DEPT", 0))){
    		String deptSql = " SELECT STA1_CODE FROM SYS_DEPT WHERE DEPT_CODE = '"+parm.getValue("IN_DEPT", 0)+"' ";
    		TParm deptParm = new TParm(TJDODBTool.getInstance().select(deptSql));
			//<AAB02C description="入院科别代码《指标》">69</AAB02C>
	    	AAB02CType aab02c = new AAB02CType();
	    	aab02c.setValue(deptParm.getValue("STA1_CODE",0));
	    	aab.setAAB02C(aab02c);
        }else{
    		errMessage("AAB02C入院科别", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	if(!"".equals(parm.getValue("IN_ROOM_NO", 0))){
			//<AAB03 description="入院病房">309</AAB03>
	    	aab.setAAB03(creat("AAB03",parm,"IN_ROOM_NO"));
    	}
    	if(!"".equals(parm.getValue("ADM_SOURCE", 0))){
    		String admSourceSql = " SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'ADM_SOURCE' " +
			   					"AND ID = '"+parm.getValue("ADM_SOURCE", 0)+"' ";
    		TParm admSourceParm = new TParm(TJDODBTool.getInstance().select(admSourceSql));
			//<AAB06C description="入院途径《指标》">2</AAB06C>
	    	AAB06CType aab06c = new AAB06CType();
	    	aab06c.setValue(admSourceParm.getValue("STA4_CODE", 0));
	    	aab.setAAB06C(aab06c);
    	}else{
    		errMessage("AAB06C入院途径", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	aa.setAAB(aab);
    	
    	AACType aac = new AACType();
    	if(!"".equals(parm.getValue("OUT_DATE", 0))){
	    	//<AAC01 description="出院时间(时)">2013-06-05 10:10:10</AAC01>
	    	TimeStamp aac01 = new TimeStamp(); 
	    	aac01.setValue(parm.getValue("OUT_DATE", 0));
	    	aac.setAAC01(aac01);
    	}else{
    		errMessage("AAC01出院时间", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	if(!"".equals(parm.getValue("OUT_DEPT", 0))){
    		String deptSql = " SELECT STA1_CODE FROM SYS_DEPT WHERE DEPT_CODE = '"+parm.getValue("OUT_DEPT", 0)+"' ";
    		TParm deptParm = new TParm(TJDODBTool.getInstance().select(deptSql));
			//<AAC02C description="出院科别代码《指标》">69</AAC02C>
	    	AAC02CType aac02c = new AAC02CType();
	    	aac02c.setValue(deptParm.getValue("STA1_CODE", 0));
	    	aac.setAAC02C(aac02c);
    	}else{
    		errMessage("AAC02C出院科别", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	if(!"".equals(parm.getValue("OUT_ROOM_NO", 0))){
			//<AAC03 description="出院病房">201</AAC03>
	    	aac.setAAC03(creat("AAC03",parm,"OUT_ROOM_NO"));
    	}
    	if(!"".equals(parm.getValue("REAL_STAY_DAYS", 0))&&(Integer.parseInt(parm.getValue("REAL_STAY_DAYS", 0))>0)){
			//<AAC04 description="实际住院(天)">30</AAC04>
	    	int aac04 = Integer.parseInt(parm.getValue("REAL_STAY_DAYS", 0));
	    	aac.setAAC04(aac04);
    	}else{
    		errMessage("AAC04实际住院(天)", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    	}
    	aa.setAAC(aac);
    	
    	//<AAD01C description="转经科别代码《指标》">04，69</AAD01C>
    	//String aad01c = parm.getValue("", 0);
    	//aa.setAAD01C(creat("AAD01C",aad01c));
    	
    	a.setAA(aa);
    	
    	
    	ABType ab = new ABType();
    	TParm diagnosisParm = new TParm();
    	if(!"".equals(parm.getValue("OE_DIAG_CODE", 0))){
    		diagnosisParm =  diagnosisCode(parm.getValue("OE_DIAG_CODE", 0),"N");
	    	if("".equals(diagnosisParm.getValue("STA1_CODE",0))||"".equals(diagnosisParm.getValue("STA2_CODE",0))){
	    		errMessage(parm.getValue("OE_DIAG_CODE", 0),"N",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
	    	}else{
	    		ABAType aba = new ABAType();
	    		//if(!"".equals(parm.getValue("OE_DIAG_CODE", 0))){
		    	//<ABA01C description="门(急)诊诊断编码(ICD-10)《指标》">R94.602</ABA01C>
		    	aba.setABA01C(creat("ABA01C",diagnosisParm,"STA1_CODE"));
		    	//}
		    	//if(!"".equals(parm.getValue("OE_DIAG_CODE", 0))){
					//<ABA01N description="门(急)诊诊断名称">低T3综合征</ABA01N>
			    	aba.setABA01N(creat("ABA01N",diagnosisParm,"STA2_CODE"));
		    	//}
		    	JAXBElement<ABAType> abaJax = new JAXBElement<ABAType>(new QName("ABA"), ABAType.class, aba);
		    	ab.setABA(abaJax);
	    	}
    	}
    	
    	//if(!"".equals(parm.getValue("OUT_DIAG_CODE1", 0))||!"".equals(parm.getValue("OUT_DIAG_CONDITION1", 0))){
    	ABCType abc = new ABCType();
    		if(!"".equals(parm.getValue("OUT_DIAG_CODE1", 0))){
    			diagnosisParm = diagnosisCode(parm.getValue("OUT_DIAG_CODE1", 0),"N");
        		if("".equals(diagnosisParm.getValue("STA1_CODE",0))||"".equals(diagnosisParm.getValue("STA2_CODE",0))){
        			errMessage(parm.getValue("OUT_DIAG_CODE1", 0),"N",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
        		}else{
        	    	if(!"".equals(diagnosisParm.getValue("STA1_CODE", 0))){
        		    	//<ABC01C description="出院时主要诊断编码(ICD-10)《指标》">R94.201</ABC01C>
        		    	jdo.sta.bean.String abc01c = new jdo.sta.bean.String();
        		    	abc01c.setValue(diagnosisParm.getValue("STA1_CODE", 0));
        				abc.setABC01C(abc01c);
       	    	    }else{
       	    			errMessage("ABC01C出院时主要诊断对照", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
       	    		}
        	    	if(!"".equals(diagnosisParm.getValue("STA2_CODE", 0))){
        		    	//<ABC01N description="出院主要诊断名称">肺换气量减少</ABC01N>
        				jdo.sta.bean.String abc01n = new jdo.sta.bean.String();
        				abc01n.setValue(diagnosisParm.getValue("STA2_CODE", 0));
        				abc.setABC01N(abc01n);
        	    	}else {
        	    		errMessage("ABC01N出院主要诊断名称对照", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    				}
        		}
    		}else{
    			errMessage("ABC01C出院时主要诊断", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    		}
    		
    		if(!"".equals(parm.getValue("OUT_DIAG_CONDITION1", 0))){
				//<ABC03C description="入院病情《指标》">1</ABC03C>
				ABC03CType abc03c = new ABC03CType();
				abc03c.setValue(parm.getValue("OUT_DIAG_CONDITION1", 0));
				abc.setABC03C(abc03c);
	    	}else {
	    		errMessage("ABC03C入院病情", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
			}
	    	ab.setABC(abc);
//    	}else {
//    		errMessage("ABC01C出院时主要诊断", "NODE");
//    		errMessage("ABC03C入院病情", "NODE");
//		}
    	
		String icdSql = " SELECT ICD_CODE,IN_PAT_CONDITION FROM MRO_RECORD_DIAG " +
				"WHERE IO_TYPE='O' AND MAIN_FLG='N' AND CASE_NO = '"+caseNo+"' ";
		TParm icdParm = new TParm(TJDODBTool.getInstance().select(icdSql));
		if(icdParm.getCount()>0){
			ABDSType abds = new ABDSType();	
			for(int i = 0; i < icdParm.getCount(); i++){
				//if(!"".equals(icdParm.getValue("ICD_CODE", i))||!"".equals(icdParm.getValue("IN_PAT_CONDITION", i))){
				ABDType abd = new ABDType();
				if(!"".equals(icdParm.getValue("ICD_CODE", i))){
					diagnosisParm = diagnosisCode(icdParm.getValue("ICD_CODE", i),"N");
					if("".equals(diagnosisParm.getValue("STA1_CODE",0))||"".equals(diagnosisParm.getValue("STA2_CODE",0))){
						errMessage(icdParm.getValue("ICD_CODE",i),"N",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
					}else{
						if(!"".equals(icdParm.getValue("ICD_CODE", i))){
							//<ABD01C description="出院时其他诊断编码(ICD-10)《指标》">S00.104</ABD01C>
							jdo.sta.bean.String abd01c = new jdo.sta.bean.String();
							abd01c.setValue(diagnosisParm.getValue("STA1_CODE", 0));
							abd.setABD01C(abd01c);
						}else{
							errMessage("ABD01C出院时其他诊断对照", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
						}
						if(!"".equals(icdParm.getValue("ICD_CODE", i))){
							//<ABD01N description="出院其他诊断名称">眼睑及眼周区挫伤</ABD01N>
							jdo.sta.bean.String abd01n = new jdo.sta.bean.String();
							abd01n.setValue(diagnosisParm.getValue("STA2_CODE", 0));
							abd.setABD01N(abd01n);
						}else{
							errMessage("ABD01C出院其他诊断名称对照", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
						}
					}
				}else{
					errMessage("ABD01C出院时其他诊断", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
				}
				
				if(!"".equals(icdParm.getValue("IN_PAT_CONDITION", i))){
					//<ABD03C description="入院病情《指标》">2</ABD03C>
					ABC03CType abd03c = new ABC03CType();
					abd03c.setValue(icdParm.getValue("IN_PAT_CONDITION", i));
					abd.setABD03C(abd03c);
				}else {
					errMessage("ABD03C入院病情", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
				}
				abds.getABD().add(abd);
					
//				}else{
//					errMessage("ABD01C出院时其他诊断", "NODE");
//					errMessage("ABD03C入院病情", "NODE");
//				}

			}
			JAXBElement<ABDSType> abdsJax = new JAXBElement<ABDSType>(new QName("ABDS"), ABDSType.class, abds);
			ab.setABDS(abdsJax);
		}
		
		
    	if(!"".equals(parm.getValue("PATHOLOGY_DIAG", 0))||!"".equals(parm.getValue("PATHOLOGY_NO", 0))||!"".equals(parm.getValue("MDIAG_BASIS", 0))||!"".equals(parm.getValue("DIF_DEGREE", 0))){
    		if(!"".equals(parm.getValue("PATHOLOGY_DIAG", 0))){
    			diagnosisParm =  diagnosisCode(parm.getValue("PATHOLOGY_DIAG", 0),"Y");
    			if("".equals(diagnosisParm.getValue("STA1_CODE",0))||"".equals(diagnosisParm.getValue("STA2_CODE",0))){
    				errMessage(parm.getValue("PATHOLOGY_DIAG", 0),"Y",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    			}else{
    	    		ABFType abf = new ABFType();
    		    	if(!"".equals(parm.getValue("PATHOLOGY_DIAG", 0))){
    			    	//<ABF01C description="病理诊断编码(M码)《指标》">M821005/3</ABF01C>
    			    	abf.setABF01C(creat("ABF01C",diagnosisParm,"STA1_CODE"));
    		    	}
    		    	//<ABF01N description="病理诊断名称">息肉内的腺癌</ABF01N>
    		    	abf.setABF01N(creat("ABF01N",diagnosisParm,"STA2_CODE"));
    		    	if(!"".equals(parm.getValue("PATHOLOGY_NO", 0))){
    		    		//<ABF04 description="病理号">01010102</ABF04>
    		    		abf.setABF04(creat("ABF04",parm,"PATHOLOGY_NO"));
    		    	}
    		    	if(!"".equals(parm.getValue("MDIAG_BASIS", 0))){
    			    	//<ABF02C description="最高诊断依据代码《指标》">8</ABF02C>
    			    	ABF02CType abf02c = new ABF02CType();
    			    	abf02c.setValue(parm.getValue("MDIAG_BASIS", 0));
    			    	JAXBElement<ABF02CType> abf02cJax = new JAXBElement<ABF02CType>(new QName("ABF02C"), ABF02CType.class, abf02c);
    			    	abf.setABF02C(abf02cJax);
    		    	}
    		    	if(!"".equals(parm.getValue("DIF_DEGREE", 0))){
    					//<ABF03C description="分化程度编码《指标》">9</ABF03C>
    			    	ABF03CType abf03c = new ABF03CType();
    			    	abf03c.setValue(parm.getValue("DIF_DEGREE", 0));
    			    	JAXBElement<ABF03CType> abf03cJax = new JAXBElement<ABF03CType>(new QName("ABF03C"), ABF03CType.class, abf03c);
    			    	abf.setABF03C(abf03cJax);
    		    	}
    		    	
    		    	JAXBElement<ABFType> abfJax = new JAXBElement<ABFType>(new QName("ABF"), ABFType.class, abf);
    		    	ab.setABF(abfJax);
    			}
    		}
    	}
    	
    	if(!"".equals(parm.getValue("EX_RSN", 0))){
    		diagnosisParm =  diagnosisCode(parm.getValue("EX_RSN", 0),"N");
    		if("".equals(diagnosisParm.getValue("STA1_CODE",0))||"".equals(diagnosisParm.getValue("STA2_CODE",0))){
    			errMessage(parm.getValue("EX_RSN", 0), "N",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    		}else{
    			ABGType abg = new ABGType();
    	    	if(!"".equals(parm.getValue("EX_RSN", 0))){
    		    	//<ABG01C description="损伤和中毒外部原因编码(ICD-10)《指标》">S00.302</ABG01C>
    		    	abg.setABG01C(creat("ABG01C",diagnosisParm,"STA1_CODE"));
    	    	}
    	    	//<ABG01N description="损伤和中毒外部原因名称">眼睑及眼周区挫伤</ABG01N>
    	    	abg.setABG01N(creat("ABG01N",diagnosisParm,"STA2_CODE"));
    	    	JAXBElement<ABGType> abgJax = new JAXBElement<ABGType>(new QName("ABG"), ABGType.class, abg);
    	    	ab.setABG(abgJax);
    		}
    	}
    	
    	String outcode = diagnosisCode(parm.getValue("OUT_DIAG_CODE1", 0),"N").getValue("STA1_CODE", 0);
    	if ((outcode.substring(0, 1).equals("C"))
				&& (Integer.parseInt(outcode.substring(1, 3)) >= 0)
				&& (Integer.parseInt(outcode.substring(1, 3)) <= 97)) {
    		ABHType abh = new ABHType();
        	if(("".equals(parm.getValue("TUMOR_STAG_T", 0)))&&("".equals(parm.getValue("TUMOR_STAG_N", 0)))
        			&&("".equals(parm.getValue("TUMOR_STAG_M", 0)))&&("".equals(parm.getValue("TUMOR_STAG", 0)))){
        		//<ABH01C description="肿瘤分期是否不详《指标》">1</ABH01C>
            	OneORtwoType abh01c = new OneORtwoType();
            	abh01c.setValue("1");
            	JAXBElement<OneORtwoType> abh01cJax = new JAXBElement<OneORtwoType>(new QName("ABH01C"), OneORtwoType.class, abh01c);
            	abh.setABH01C(abh01cJax);
        	}else{
        		//<ABH01C description="肿瘤分期是否不详《指标》">1</ABH01C>
            	OneORtwoType abh01c = new OneORtwoType();
            	abh01c.setValue("2");
            	JAXBElement<OneORtwoType> abh01cJax = new JAXBElement<OneORtwoType>(new QName("ABH01C"), OneORtwoType.class, abh01c);
            	abh.setABH01C(abh01cJax);
            	if(!"".equals(parm.getValue("TUMOR_STAG_T", 0))){
        			//<ABH0201C description="肿瘤分期 T《指标》">1</ABH0201C>
        	    	String abh0201c = parm.getValue("TUMOR_STAG_T", 0);
        	    	abh.setABH0201C(creat("ABH0201C",abh0201c));
            	}
            	if(!"".equals(parm.getValue("TUMOR_STAG_N", 0))){
        			//<ABH0202C description="肿瘤分期 N《指标》">2</ABH0202C>
        	    	String abh0202c = parm.getValue("TUMOR_STAG_N", 0);
        	    	abh.setABH0202C(creat("ABH0202C", abh0202c));
            	}
            	if(!"".equals(parm.getValue("TUMOR_STAG_M", 0))){
        			//<ABH0203C description="肿瘤分期 M《指标》">9</ABH0203C>
        	    	String abh0203c = parm.getValue("TUMOR_STAG_M", 0);
        	    	abh.setABH0203C(creat("ABH0203C", abh0203c));
            	}
            	if(!"".equals(parm.getValue("TUMOR_STAG", 0))){
            		//<ABH03C description="0～Ⅳ肿瘤分期《指标》">4</ABH03C>
                	String abh03c = parm.getValue("TUMOR_STAG", 0);
                	abh.setABH03C(creat("ABH03C", abh03c));
            	}
        	}
        	JAXBElement<ABHType> abhJax = new JAXBElement<ABHType>(new QName("ABH"), ABHType.class, abh);
        	ab.setABH(abhJax);
    		
    	}
    	
    	a.setAB(ab);
    	
    	
    	
    	String opSql = " SELECT TO_CHAR(OP_DATE,'YYYY-MM-DD HH24:MI:SS')AS OP_DATE,TO_CHAR(OP_END_DATE,'YYYY-MM-DD HH24:MI:SS')AS OP_END_DATE,MAIN_SUGEON,AST_DR1,AST_DR2,ANA_WAY,HEALTH_LEVEL,ANA_DR,OP_LEVEL,OP_CODE,OP_DESC,MAIN_FLG,OPERATION_TYPE " +
    			"FROM MRO_RECORD_OP " +
    			"WHERE CASE_NO = '"+caseNo+"' ";
    	TParm opParm = new TParm(TJDODBTool.getInstance().select(opSql));
    	//System.out.println("opSql--------"+opSql);
    	//System.out.println("opParm---------"+opParm);
    	if(opParm.getCount()>0){
	    	ACType ac = new ACType();
	    	ACASType acas = new ACASType();
	    	for(int i = 0; i < opParm.getCount(); i++){
//		        if(!"".equals(opParm.getValue("OP_DATE", i))){
		        	ACAType aca = new ACAType();
			        if(!"".equals(opParm.getValue("OP_DATE", i))){
				    	//<ACA01 description="手术日期时间（开始）">2013-01-05 20:10:10</ACA01>
				    	TimeStamp aca01 = new TimeStamp();
				    	aca01.setValue(opParm.getValue("OP_DATE", i));
				    	aca.setACA01(aca01);
			        }else {
						errMessage("ACA01手术日期时间（开始）", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
					}
			        if(!"".equals(opParm.getValue("OP_END_DATE", i))){
				    	//<ACA11 description="手术日期时间（完成）">2013-01-06 02:10:10</ACA11>
				    	TimeStamp aca11 = new TimeStamp();
				    	aca11.setValue(opParm.getValue("OP_END_DATE", i));
				    	aca.setACA11(aca11);
			        }else {
			        	errMessage("ACA11手术日期时间（完成）", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
					}
			        if(!"".equals(opParm.getValue("MAIN_SUGEON", i))){
				    	//<ACA02 description="术者（手术操作医师姓名）">王五</ACA02>
			        	TParm userParm = getName(opParm.getValue("MAIN_SUGEON", i));
				    	jdo.sta.bean.String aca02 = new jdo.sta.bean.String();
				    	aca02.setValue(userParm.getValue("USER_NAME", 0));
				    	aca.setACA02(aca02);
			        }else {
			        	errMessage("ACA02术者（手术操作医师姓名）", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
					}
			        if(!"".equals(opParm.getValue("AST_DR1", i))){
			        	TParm userParm = getName(opParm.getValue("AST_DR1", i));
				    	//<ACA03 description="Ⅰ助姓名">赵六</ACA03>
				    	aca.setACA03(creat("ACA03",userParm,"USER_NAME"));
			        }
			        if(!"".equals(opParm.getValue("AST_DR2", i))){
			        	TParm userParm = getName(opParm.getValue("AST_DR2", i));
				    	//<ACA04 description="Ⅱ助姓名">汉启</ACA04>
				    	aca.setACA04(creat("ACA04",userParm,"USER_NAME"));
			        }
			        if(!"".equals(opParm.getValue("ANA_WAY", i))){
			        	String anaWaySql = " SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'OPE_ANAMETHOD' AND ID = '"+opParm.getValue("ANA_WAY",i)+"' ";
			        	TParm anaWayParm = new TParm(TJDODBTool.getInstance().select(anaWaySql));
				    	//<ACA06C description="麻醉方式代码《指标》">01</ACA06C>
				    	ACA06CType aca06c = new ACA06CType();
				    	aca06c.setValue(anaWayParm.getValue("STA4_CODE", 0));
				    	JAXBElement<ACA06CType> aca06cJax = new JAXBElement<ACA06CType>(new QName("ACA06C"), ACA06CType.class, aca06c);
				    	aca.setACA06C(aca06cJax);
			        }
			        if(!"".equals(opParm.getValue("HEALTH_LEVEL", i))){
			        	String healthLevelSql = " SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'MRO_HEALTHLEVEL' " +
			        					   "AND ID = '"+opParm.getValue("HEALTH_LEVEL", i)+"' ";
			        	TParm healthLevelParm = new TParm(TJDODBTool.getInstance().select(healthLevelSql));
				    	//<ACA07C description="切口愈合等级代码《指标》">9</ACA07C>
				    	ACA07CType aca07c = new ACA07CType();
				    	aca07c.setValue(healthLevelParm.getValue("STA4_CODE", 0));
				    	JAXBElement<ACA07CType> aca07cJax = new JAXBElement<ACA07CType>(new QName("ACA07C"), ACA07CType.class, aca07c);
				    	aca.setACA07C(aca07cJax);
			        }
			        if(!"".equals(opParm.getValue("ANA_DR", i))){
			        	TParm userParm = getName(opParm.getValue("ANA_DR", i));
				    	//<ACA08 description="麻醉医师姓名">屠户</ACA08>
				    	aca.setACA08(creat("ACA08",userParm,"USER_NAME"));
			        }
			        if(!"".equals(opParm.getValue("OP_LEVEL", i))){
			        	String opLevelSql = " SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'OPE_RANK' " +
			        							"AND ID = '"+opParm.getValue("OP_LEVEL", i)+"' ";
			        	TParm opLevelParm = new TParm(TJDODBTool.getInstance().select(opLevelSql));
				    	//<ACA10C description="手术级别代码《指标》">2</ACA10C>
				    	String aca10c = opLevelParm.getValue("STA4_CODE", 0);
				    	if(!"5".equals(aca10c)){//当手术级别为5时候，蓝创HIS中未其他，对应卫计委不存在这个选项则不显示
				    		aca.setACA10C(creat("ACA10C",aca10c));
				    	}
			        }
//			        if(!"".equals(opParm.getValue("OP_CODE", i))){
			        	String opcodeSql = " SELECT STA1_CODE,STA1_DESC FROM SYS_OPERATIONICD WHERE OPERATION_ICD = '"+opParm.getValue("OP_CODE", i)+"' ";
			        	TParm opcodeParm = new TParm(TJDODBTool.getInstance().select(opcodeSql));
				        if("".equals(opcodeParm.getValue("STA1_CODE", 0))){
				        	errMessage(opParm.getValue("OP_CODE", i), "",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
				        }else{
					    	ACA09SType aca09s = new ACA09SType();
					    	TParm operateParm;
					    	String operateSql;
					    	for(int n = 0; n < opcodeParm.getCount(); n++){
						    	ACA09Type aca09 = new ACA09Type();
						        if(!"".equals(opParm.getValue("OP_CODE", i))){
							    	//<ACA0901C description="手术及操作编码(ICD-9-CM3)《指标》">48.41002</ACA0901C>
									jdo.sta.bean.String aca0901c = new jdo.sta.bean.String();
									aca0901c.setValue(opcodeParm.getValue("STA1_CODE", n));
							    	aca09.setACA0901C(aca0901c);
						        }else {
									errMessage("ACA0901C手术及操作编码(ICD-9-CM3)", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
								}
						        if(!"".equals(opParm.getValue("OP_DESC", i))){
							    	//<ACA0901N description="手术及操作名称">索夫氏直肠粘膜下切除术[Soave]</ACA0901N>
							    	jdo.sta.bean.String aca0901n = new jdo.sta.bean.String();
									aca0901n.setValue(opcodeParm.getValue("STA1_DESC", n));
							    	aca09.setACA0901N(aca0901n);
						        }else {
									errMessage("ACA0901N手术及操作名称", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
								}
						        //if(!"".equals(opParm.getValue("MAIN_FLG", i))){
							        //<ACA0902C description="是否主要术式《指标》">2</ACA0902C>
							    	ACA0902CType aca0902c = new ACA0902CType();
							    	if("Y".equals(opParm.getValue("MAIN_FLG", i))){
							    		aca0902c.setValue("1");//是
							    	}else{
							    		aca0902c.setValue("2");//否
							    	}
							    	aca09.setACA0902C(aca0902c);
						        //}
						       // if(!"".equals(opParm.getValue("MAIN_FLG", i))){
									//<ACA0903C description="是否主要手术或操作《指标》">2</ACA0903C>
							    	ACA0903CType aca0903c = new ACA0903CType();
//							    	operateSql = "SELECT OPERATION_TYPE FROM SYS_OPERATIONICD WHERE OPERATION_ICD = '"+opParm.getValue("OP_CODE", i)+"'";
//							    	operateParm = new TParm(TJDODBTool.getInstance().select(operateSql));
//							    	if(("1".equals(operateParm.getValue("OPERATION_TYPE", 0))) || ("2".equals(operateParm.getValue("OPERATION_TYPE", 0)))){
//							    		if("Y".equals(opParm.getValue("OPERATION_TYPE", i))){
//								    		aca0903c.setValue("1");//是
//								    	}else{
//								    		aca0903c.setValue("2");//否
//								    	}
//							    	}else if("3".equals(operateParm.getValue("OPERATION_TYPE", 0))){
							    		if("Y".equals(opParm.getValue("OPERATION_TYPE", i))){
								    		aca0903c.setValue("1");//是
								    	}else{
								    		aca0903c.setValue("2");//否
								    	}
//							    	}
							    		
						    	aca09.setACA0903C(aca0903c);
						       // }
						    	aca09s.getACA09().add(aca09);
					    	}
					    	JAXBElement<ACA09SType> aca09sJax = new JAXBElement<ACA09SType>(new QName("ACA09S"), ACA09SType.class, aca09s);
					    	aca.setACA09S(aca09sJax);
				        }
//			        }
			    	
			    	acas.getACA().add(aca);
//		        }
	    	}
			JAXBElement<ACASType> acasJax = new JAXBElement<ACASType>(new QName("ACAS"), ACASType.class, acas);
	    	ac.setACAS(acasJax);
	    	
	    	JAXBElement<ACType> acJax = new JAXBElement<ACType>(new QName("AC"), ACType.class, ac);
	    	a.setAC(acJax);
    	}
    	
    	
    	AEType ae = new AEType();
    	if(!"".equals(parm.getValue("ALLEGIC_FLG", 0))||!"".equals(parm.getValue("ALLEGIC", 0))){
	    	AEBType aeb = new AEBType();
	    	if(!"".equals(parm.getValue("ALLEGIC_FLG", 0))){
		    	//<AEB02C description="有无药物过敏《指标》">1</AEB02C>
		    	OneORtwoType aeb02c = new OneORtwoType();
		    	aeb02c.setValue(parm.getValue("ALLEGIC_FLG", 0));
		    	aeb.setAEB02C(aeb02c);
	    	}else{
	    		errMessage("AEB02C有无药物过敏", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
	    	}
	    	if(!"".equals(parm.getValue("ALLEGIC", 0))){
				//<AEB01 description="过敏药物">抗性药物过敏,其它药物过敏</AEB01>
//	    		jdo.sta.bean.String s = new jdo.sta.bean.String();
//	        	s.setValue(parm.getValue("ALLEGIC", 0).replace(",", ","));
//	        	JAXBElement<jdo.sta.bean.String> jax = new JAXBElement<jdo.sta.bean.String>(new QName("AEB01"), jdo.sta.bean.String.class, s);
		    	aeb.setAEB01(creat("AEB01",parm,"ALLEGIC"));
	    	}
	    	JAXBElement<AEBType> aebJax = new JAXBElement<AEBType>(new QName("AEB"), AEBType.class, aeb);
	    	ae.setAEB(aebJax);
    	}
    	if(!"".equals(parm.getValue("QUALITY", 0))||!"".equals(parm.getValue("CTRL_DR", 0))||!"".equals(parm.getValue("CTRL_NURSE", 0))||!"".equals(parm.getValue("CTRL_DATE",0))){
	    	AEDType aed = new AEDType();
	    	if(!"".equals(parm.getValue("QUALITY", 0))){
		    	//<AED01C description="病案质量代码《指标》">3</AED01C>
		    	AED01CType aed01c = new AED01CType();
		    	aed01c.setValue(parm.getValue("QUALITY", 0));
		    	JAXBElement<AED01CType> aed01cJax = new JAXBElement<AED01CType>(new QName("AED01C"), AED01CType.class, aed01c);
		    	aed.setAED01C(aed01cJax);
	    	}
	    	if(!"".equals(parm.getValue("CTRL_DR", 0))){
	    		TParm user = getName(parm.getValue("CTRL_DR", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AED02 description="质控医师姓名">王天一</AED02>
			    	aed.setAED02(creat("AED02",user,"USER_NAME"));
	    		}
	    	}
	    	if(!"".equals(parm.getValue("CTRL_NURSE", 0))){
	    		TParm user = getName(parm.getValue("CTRL_NURSE", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AED03 description="质控护士姓名">张添翼</AED03>
			    	aed.setAED03(creat("AED03",user,"USER_NAME"));
	    		}
	    	}
	    	if(!"".equals(parm.getValue("CTRL_DATE",0))){
				//<AED04 description="病案质量检查日期">2010-02-05</AED04>
		    	try {
					date = format.parse(parm.getValue("CTRL_DATE",0));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				try {
					dataTypeFactory = DatatypeFactory.newInstance();
				} catch (DatatypeConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				GregorianCalendar gc = new GregorianCalendar();
		    	gc.setTimeInMillis(date.getTime());
		    	XMLGregorianCalendar aed04XmlGc = dataTypeFactory.newXMLGregorianCalendarDate(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH)+1, gc.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
		    	jdo.sta.bean.Date aed04 = new jdo.sta.bean.Date();
		    	aed04.setValue(aed04XmlGc);
		    	JAXBElement<jdo.sta.bean.Date> aed04Jax = new JAXBElement<jdo.sta.bean.Date>(new QName("AED04"), jdo.sta.bean.Date.class, aed04);
		    	aed.setAED04(aed04Jax);
	    	}
	    	JAXBElement<AEDType> aedJax = new JAXBElement<AEDType>(new QName("AED"), AEDType.class, aed);
	    	ae.setAED(aedJax);
    	}
    	
//    	if(!"".equals(parm.getValue("DIRECTOR_DR_CODE", 0))||!"".equals(parm.getValue("PROF_DR_CODE", 0))||!"".equals(parm.getValue("ATTEND_DR_CODE", 0))||!"".equals(parm.getValue("VS_DR_CODE", 0))
//    			||!"".equals(parm.getValue("INDUCATION_DR_CODE", 0))||!"".equals(parm.getValue("INTERN_DR_CODE", 0))||!"".equals(parm.getValue("ENCODER", 0))||!"".equals(parm.getValue("VS_NURSE_CODE", 0))){
	    	AEEType aee = new AEEType();
	    	if(!"".equals(parm.getValue("DIRECTOR_DR_CODE", 0))){
	    		TParm user = getName(parm.getValue("DIRECTOR_DR_CODE", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AEE01 description="科主任姓名">王天一</AEE01>
			    	aee.setAEE01(creat("AEE01",user,"USER_NAME"));
	    		}
	    	}
	    	if(!"".equals(parm.getValue("PROF_DR_CODE", 0))){
	    		TParm user = getName(parm.getValue("PROF_DR_CODE", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AEE02 description="主(副主)任医师姓名">王天一</AEE02>
			    	jdo.sta.bean.String aee02 = new jdo.sta.bean.String();
			    	aee02.setValue(user.getValue("USER_NAME", 0));
			    	aee.setAEE02(aee02);
	    		}
	    	}else {
	    		errMessage("AEE02主(副主)任医师姓名", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
			}
	    	if(!"".equals(parm.getValue("ATTEND_DR_CODE", 0))){
	    		TParm user = getName(parm.getValue("ATTEND_DR_CODE", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AEE03 description="主治医师姓名">王天一</AEE03>
			    	jdo.sta.bean.String aee03 = new jdo.sta.bean.String();
			    	aee03.setValue(user.getValue("USER_NAME", 0));
			    	aee.setAEE03(aee03);
	    		}
	    	}else {
				errMessage("AEE03主治医师姓名", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
			}
	//    	if(!"".equals(parm.getValue("", 0))){
	//			//<AEE11 description="主诊医师执业证书编码">1101021021021021</AEE11>
	//	    	String aee11 = parm.getValue("", 0);
	//	    	aee.setAEE11(creat("AEE11",aee11));
	//    	}
	    	if(!"".equals(parm.getValue("VS_DR_CODE", 0))){
	    		TParm user = getName(parm.getValue("VS_DR_CODE", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AEE09 description="主诊医师姓名（主管医师姓名）">王天一</AEE09>
			    	aee.setAEE09(creat("AEE09",user,"USER_NAME"));
	    		}
	    	}
	    	if(!"".equals(parm.getValue("VS_DR_CODE", 0))){
	    		TParm user = getName(parm.getValue("VS_DR_CODE", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AEE04 description="住院医师姓名">王天一</AEE04>
			    	aee.setAEE04(creat("AEE04",user,"USER_NAME"));
	    		}
	    	}
	    	if(!"".equals(parm.getValue("INDUCATION_DR_CODE", 0))){
	    		TParm user = getName(parm.getValue("INDUCATION_DR_CODE", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AEE05 description="进修医师姓名">王天一</AEE05>
			    	aee.setAEE05(creat("AEE05",user,"USER_NAME"));
	    		}
	    	}
	    	if(!"".equals(parm.getValue("INTERN_DR_CODE", 0))){
	    		TParm user = getName(parm.getValue("INTERN_DR_CODE", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AEE07 description="实习医师姓名">王天一</AEE07>
			    	aee.setAEE07(creat("AEE07",user,"USER_NAME"));
	    		}
	    	}
	    	if(!"".equals(parm.getValue("ENCODER", 0))){
	    		TParm user = getName(parm.getValue("ENCODER", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AEE08 description="编码员姓名">王天一</AEE08>
			    	aee.setAEE08(creat("AEE08",user,"USER_NAME"));
	    		}
	    	}
	    	if(!"".equals(parm.getValue("VS_NURSE_CODE", 0))){
	    		TParm user = getName(parm.getValue("VS_NURSE_CODE", 0));
	    		if(!"".equals(user.getValue("USER_NAME", 0))){
	    			//<AEE10 description="责任护士姓名">王天一</AEE10>
			    	aee.setAEE10(creat("AEE10",user,"USER_NAME"));
	    		}
	    	}
	    	ae.setAEE(aee);
//    	}
    	if(!"".equals(parm.getValue("BLOOD_TYPE", 0))||!"".equals(parm.getValue("RH_TYPE", 0))||!"".equals(parm.getValue("RBC", 0))||!"".equals(parm.getValue("PLATE", 0))
    			||!"".equals(parm.getValue("PLASMA", 0))||!"".equals(parm.getValue("WHOLE_BLOOD", 0))||!"".equals(parm.getValue("OTH_BLOOD", 0))){
	    	AEGType aeg = new AEGType();
	    	if(!"".equals(parm.getValue("BLOOD_TYPE", 0))){
//	    		String bloodTypeSql = " SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_BLOOD' " +
//									"AND ID = '"+parm.getValue("BLOOD_TYPE", 0)+"' ";
//	    		TParm bloodTypeParm = new TParm(TJDODBTool.getInstance().select(bloodTypeSql));
		    	//<AEG01C description="血型代码《指标》">5</AEG01C>
		    	AEG01CType aeg01c = new AEG01CType();
		    	aeg01c.setValue(parm.getValue("BLOOD_TYPE", 0));
		    	JAXBElement<AEG01CType> aeg01cJax = new JAXBElement<AEG01CType>(new QName("AEG01C"), AEG01CType.class, aeg01c);
		    	aeg.setAEG01C(aeg01cJax);
	    	}
	    	if(!"".equals(parm.getValue("RH_TYPE", 0))){
//	    		String rhSql = " SELECT STA4_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_RH' " +
//									  "AND ID = '"+parm.getValue("RH_TYPE", 0)+"' ";
//	    		TParm rhParm = new TParm(TJDODBTool.getInstance().select(rhSql));
				//<AEG02C description="Rh代码《指标》">2</AEG02C>
		    	AEG02CType aeg02c = new AEG02CType();
		    	aeg02c.setValue(parm.getValue("RH_TYPE", 0));
		    	JAXBElement<AEG02CType> aeg02cJax = new JAXBElement<AEG02CType>(new QName("AEG02C"), AEG02CType.class, aeg02c);
		    	aeg.setAEG02C(aeg02cJax);
	    	}
	    	if(!"".equals(parm.getValue("RBC", 0))&&((int)Double.parseDouble(parm.getValue("RBC", 0))>0)){
		        //<AEG04 description="红细胞(单位)">100</AEG04>
		    	aeg.setAEG04(creat("AEG04", (int)Float.parseFloat(parm.getValue("RBC", 0))));
	    	}
	    	if(!"".equals(parm.getValue("PLATE", 0))&&((int)Double.parseDouble(parm.getValue("PLATE", 0))>0)){
		    	//<AEG05 description="血小板(袋)">100</AEG05>
		    	aeg.setAEG05(creat("AEG05", (int)Float.parseFloat(parm.getValue("PLATE", 0))));
	    	}
	    	if(!"".equals(parm.getValue("PLASMA", 0))&&((int)Double.parseDouble(parm.getValue("PLASMA", 0))>0)){
				//<AEG06 description="血浆(ml)">100</AEG06>
		    	aeg.setAEG06(creat("AEG06", (int)Float.parseFloat(parm.getValue("PLASMA", 0))));
	    	}
	    	if(!"".equals(parm.getValue("WHOLE_BLOOD", 0))&&((int)Double.parseDouble(parm.getValue("WHOLE_BLOOD", 0))>0)){
		    	//<AEG07 description="全血(ml)">100</AEG07>
		    	aeg.setAEG07(creat("AEG07", (int)Float.parseFloat(parm.getValue("WHOLE_BLOOD", 0))));
	    	}
	    	if(!"".equals(parm.getValue("OTH_BLOOD", 0))&&((int)Double.parseDouble(parm.getValue("OTH_BLOOD", 0))>0)){
				//<AEG08 description="其它(ml)">100</AEG08>
		    	aeg.setAEG08(creat("AEG08", (int)Float.parseFloat(parm.getValue("OTH_BLOOD", 0))));
	    	}
	    	JAXBElement<AEGType> aegJax = new JAXBElement<AEGType>(new QName("AEG"), AEGType.class, aeg);
	    	ae.setAEG(aegJax);
    	}
    	
    	if((!"".equals(parm.getValue("BE_COMA_TIME", 0)))||(!"".equals(parm.getValue("AF_COMA_TIME", 0)))){
	    	AEJType aej = new AEJType();
	    	if(!"".equals(parm.getValue("BE_COMA_TIME", 0))&&(Integer.parseInt(parm.getValue("BE_COMA_TIME", 0).substring(0, 2))>=0)){
		    	//<AEJ01 description="颅脑损伤患者入院前昏迷时间（天）">2</AEJ01>
		    	aej.setAEJ01(creat("AEJ01", Integer.parseInt(parm.getValue("BE_COMA_TIME", 0).substring(0, 2))));
	    	}
	    	if((!"".equals(parm.getValue("BE_COMA_TIME", 0)))&&(Integer.parseInt(parm.getValue("BE_COMA_TIME", 0).substring(2, 4))>=0)&&(Integer.parseInt(parm.getValue("BE_COMA_TIME", 0).substring(2, 4))<24)){
				//<AEJ02 description="颅脑损伤患者入院前昏迷时间（小时）">3</AEJ02>
		    	aej.setAEJ02(creat("AEJ02", Integer.parseInt(parm.getValue("BE_COMA_TIME", 0).substring(2, 4))));
	    	}
	    	if(!"".equals(parm.getValue("BE_COMA_TIME", 0))&&(Integer.parseInt(parm.getValue("BE_COMA_TIME", 0).substring(4, 6))>=0)&&(Integer.parseInt(parm.getValue("BE_COMA_TIME", 0).substring(4, 6))<60)){
				//<AEJ03 description="颅脑损伤患者入院前昏迷时间（分钟）">10</AEJ03>
		    	Minute aej03 = new Minute();
		    	aej03.setValue(Integer.parseInt(parm.getValue("BE_COMA_TIME", 0).substring(4, 6)));
		    	aej.setAEJ03(creat("AEJ03",aej03 ));
	    	}
	    	if(!"".equals(parm.getValue("AF_COMA_TIME", 0))&&(Integer.parseInt(parm.getValue("AF_COMA_TIME", 0).substring(0, 2))>=0)){
				//<AEJ04 description="颅脑损伤患者入院后昏迷时间（天）">1</AEJ04>
		    	aej.setAEJ04(creat("AEJ04", Integer.parseInt(parm.getValue("AF_COMA_TIME", 0).substring(0, 2))));
	    	}
	    	if(!"".equals(parm.getValue("AF_COMA_TIME", 0))&&(Integer.parseInt(parm.getValue("AF_COMA_TIME", 0).substring(2, 4))>=0)&&(Integer.parseInt(parm.getValue("AF_COMA_TIME", 0).substring(2, 4))<24)){
				//<AEJ05 description="颅脑损伤患者入院后昏迷时间（小时）">1</AEJ05>
		    	aej.setAEJ05(creat("AEJ05", Integer.parseInt(parm.getValue("AF_COMA_TIME", 0).substring(2, 4))));
	    	}
	    	if(!"".equals(parm.getValue("AF_COMA_TIME", 0))&&(Integer.parseInt(parm.getValue("AF_COMA_TIME", 0).substring(4, 6))>=0)&&(Integer.parseInt(parm.getValue("AF_COMA_TIME", 0).substring(4, 6))<60)){
		    	//<AEJ06 description="颅脑损伤患者入院后昏迷时间（分钟）">50</AEJ06>
		    	Minute aej06 = new Minute();
		    	aej06.setValue(Integer.parseInt(parm.getValue("AF_COMA_TIME", 0).substring(4, 6)));
		    	aej.setAEJ06(creat("AEJ06", aej06));
	    	}
	    	JAXBElement<AEJType> aejJax = new JAXBElement<AEJType>(new QName("AEJ"), AEJType.class, aej);
	    	ae.setAEJ(aejJax);
    	}
   	if((!"".equals(parm.getValue("ICU_ROOM1", 0)))||(!"".equals(parm.getValue("ICU_IN_DATE1", 0)))||(!"".equals(parm.getValue("ICU_OUT_DATE1", 0)))){
    		String icuSql = " SELECT STA1_CODE FROM SYS_DEPT WHERE DEPT_CODE = '"+parm.getValue("ICU_ROOM1", 0)+"' ";
    		TParm icuParm = new TParm(TJDODBTool.getInstance().select(icuSql));
	    	AEKSType aeks = new AEKSType();
	    	AEKType aek = new AEKType();
	    	if(!"".equals(icuParm.getValue("STA1_CODE", 0))){
		    	//<AEK01C description="重症监护室代码《指标》">03</AEK01C>
		    	aek.setAEK01C(icuParm.getValue("STA1_CODE", 0));
	    	}else{
	    		errMessage("AEK01C重症监护室", "NODE", parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
	    	}
	    	if(!"".equals(parm.getValue("ICU_IN_DATE1", 0))){
		    	//<AEK02 description="监护室进入日期时间">2013-02-12 10:20:20</AEK02>
		    	TimeStamp aek02 = new TimeStamp();
		    	aek02.setValue(parm.getValue("ICU_IN_DATE1", 0));
		    	aek.setAEK02(aek02);
	    	}else{
	    		errMessage("AEK02监护室进入日期时间", "NODE", parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
	    	}
	    	if(!"".equals(parm.getValue("ICU_OUT_DATE1", 0))){
			    //<AEK03 description="监护室退出日期时间">2013-02-20 09:20:20</AEK03>
		    	TimeStamp aek03 = new TimeStamp();
		    	aek03.setValue(parm.getValue("ICU_OUT_DATE1", 0));
		    	aek.setAEK03(aek03);
	    	}else{
	    		errMessage("AEK03监护室退出日期时间", "NODE", parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
	    	}
	    	aeks.getAEK().add(aek);
	    	JAXBElement<AEKSType> aeksJax = new JAXBElement<AEKSType>(new QName("AEKS"), AEKSType.class, aeks);
	    	ae.setAEKS(aeksJax);
   	}
    	
    	if(!"".equals(parm.getValue("VENTI_TIME", 0))&&(Integer.parseInt(parm.getValue("VENTI_TIME", 0))>0)){
	    	//<AEL01 description="呼吸机使用时间(小时)">10</AEL01>
	    	ae.setAEL01(creat("AEL01", Integer.parseInt(parm.getValue("VENTI_TIME", 0))));
    	}
    	
    	
    	if((!"".equals(parm.getValue("NB_WEIGHT", 0)))||(!"".equals(parm.getValue("NB_DEFECT_CODE", 0)))){
    		AENSType aens = new AENSType();
        	AENType aen = new AENType();
    		if(!"".equals(parm.getValue("NB_WEIGHT", 0))){
    			System.out.println("@test by wangqing@---NB_WEIGHT="+parm.getInt("NB_WEIGHT", 0));
    			
    			try {
    				if(parm.getInt("NB_WEIGHT", 0)>0){
    					//<AEN01 description="新生儿出生体重(克)">3501</AEN01>
    	    	    	aen.setAEN01(parm.getInt("NB_WEIGHT", 0));
    				}else{
    					errMessage("AEN01新生儿出生体重(克)(必须为大于0整数)", "NODE", parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
    				}
				} catch (Exception e) {
					errMessage("AEN01新生儿出生体重(克)(必须为大于0整数)", "NODE", parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
				}
        	}
	    	if(!"".equals(parm.getValue("NB_DEFECT_CODE", 0))){
	    		TParm defect = diagnosisCode(parm.getValue("NB_DEFECT_CODE", 0), "N");
	    		if("".equals(defect.getValue("STA1_CODE",0))||"".equals(defect.getValue("STA2_CODE",0))){
					errMessage(parm.getValue("NB_DEFECT_CODE",0),"N",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
				}else{
					//<AEN02C description="新生儿出生缺陷诊断编码(ICD-10)《指标》">M21.903</AEN02C>
			    	aen.setAEN02C(creat("AEN02C",defect,"STA1_CODE"));
			    	//<AEN02N description="新生儿出生缺陷诊断名称">后天性下肢畸形</AEN02N>
			    	aen.setAEN02N(creat("AEN02N",defect,"STA2_CODE"));
				}
	    	}
	    	aens.getAEN().add(aen);
	    	JAXBElement<AENSType> aensJax = new JAXBElement<AENSType>(new QName("AENS"), AENSType.class, aens);
	    	ae.setAENS(aensJax);
    	}

    	AEMType aem = new AEMType();
    	if(!"".equals(parm.getValue("OUT_TYPE", 0))){
    	//<AEM01C description="离院方式代码《指标》">2</AEM01C>
    	aem.setAEM01C(parm.getValue("OUT_TYPE", 0));
    	}else {
			errMessage("AEM01C离院方式", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
		}
    	if(!"".equals(parm.getValue("TRAN_HOSP", 0))){
		//<AEM02 description="医嘱转院、转社区、卫生院机构名称">北京两广中医医院</AEM02>
    	aem.setAEM02(creat("AEM02",parm,"TRAN_HOSP"));
    	}
    	
		//<AEM03C description="是否有出院31日内再住院计划《指标》">1</AEM03C>
		OneORtwoType aem03c = new OneORtwoType();
		if("Y".equals(parm.getValue("AGN_PLAN_FLG", 0))){
			aem03c.setValue("1");
		}else{
			aem03c.setValue("2");
		}
    	aem.setAEM03C(aem03c);
    	
    	if(!"".equals(parm.getValue("AGN_PLAN_INTENTION", 0))){
    	//<AEM04 description="31日内再住院目的">复查</AEM04>
    	aem.setAEM04(creat("AEM04",parm,"AGN_PLAN_INTENTION"));
    	}
    	JAXBElement<AEMType> aemJax = new JAXBElement<AEMType>(new QName("AEM"), AEMType.class, aem);
    	ae.setAEM(aemJax);
    	
    	
    	if(!"".equals(parm.getValue("BODY_CHECK", 0))||!"".equals(parm.getValue("NURSING_GRAD_IN", 0))||!"".equals(parm.getValue("NURSING_GRAD_OUT", 0))){
	    	AEIType aei = new AEIType();
	    	if(!"".equals(parm.getValue("BODY_CHECK", 0))){
		    	//<AEI01C description="是否尸检代码《指标》">2</AEI01C>
		    	AEI01CType aei01c = new AEI01CType();
		    	aei01c.setValue(parm.getValue("BODY_CHECK", 0));
		    	JAXBElement<AEI01CType> aei01cJax = new JAXBElement<AEI01CType>(new QName("AEI01C"), AEI01CType.class, aei01c);
		    	aei.setAEI01C(aei01cJax);
	    	}
//	    	this.messageBox("111");
	    	if(!"".equals(parm.getValue("NURSING_GRAD_IN", 0))){
				//<AEI09 description="日常生活能力评定量得分（入院）">50</AEI09>
	    		// mofified by wangqing 20171115 start
	    		// 日常生活能力评定量得分（入院）应该为大于等于0并且小于等于100  
//	    		this.messageBox("NURSING_GRAD_IN="+Integer.parseInt(parm.getValue("NURSING_GRAD_IN", 0)));
	    		if((Integer.parseInt(parm.getValue("NURSING_GRAD_IN", 0))>=0) 
	    				&& (Integer.parseInt(parm.getValue("NURSING_GRAD_IN", 0))<=100)){
	    			aei.setAEI09(creat("AEI09", Integer.parseInt(parm.getValue("NURSING_GRAD_IN", 0))));
	    		}else{
	    			// modified by wangqing 20171116
//	    			map.put(parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0), parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0)+"标签和含义为\"AEI09日常生活能力评定量得分（入院）\"的字段应为大于等于0，小于等于100的整数");
	    			map.put(parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0)+"_in", parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0)+"标签和含义为\"AEI09日常生活能力评定量得分（入院）\"的字段应为大于等于0，小于等于100的整数");
	    		}  
	    		// mofified by wangqing 20171115 end
	    	}
	    	if(!"".equals(parm.getValue("NURSING_GRAD_OUT", 0))){
		        //<AEI10 description="日常生活能力评定量得分（出院）">50</AEI10>
	    		// mofified by wangqing 20171115 start
	    		// 日常生活能力评定量得分（出院）应该为大于等于0并且小于等于100
//	    		this.messageBox("NURSING_GRAD_OUT="+Integer.parseInt(parm.getValue("NURSING_GRAD_OUT", 0)));
	    		if((Integer.parseInt(parm.getValue("NURSING_GRAD_OUT", 0))>=0) 
	    				&& (Integer.parseInt(parm.getValue("NURSING_GRAD_OUT", 0))<=100)){
	    			aei.setAEI10(creat("AEI10", Integer.parseInt(parm.getValue("NURSING_GRAD_OUT", 0))));
	    		}else{
	    			// modified by wangqing 20171116
//	    			map.put(parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0), parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0)+"标签和含义为\"AEI10日常生活能力评定量得分（出院）\"的字段应为大于等于0，小于等于100的整数");
	    			map.put(parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0)+"_out", parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0)+"标签和含义为\"AEI10日常生活能力评定量得分（出院）\"的字段应为大于等于0，小于等于100的整数");
	    		}
	    		// mofified by wangqing 20171115 end
	    	}
	    	//<AEI08 description="备注">备注信息</AEI08>
			//aei.setAEI08(creat("AEI08"));
	    	JAXBElement<AEIType> aeiJax = new JAXBElement<AEIType>(new QName("AEI"), AEIType.class, aei);
	    	ae.setAEI(aeiJax);
    	}
    	a.setAE(ae);
    	
    	String chargeSql = " SELECT SUM (TOT_AMT) AS TOT_AMT FROM IBS_ORDD WHERE CASE_NO = '"+caseNo+"' ";
    	TParm chargeParm;
    	ADType ad = new ADType();
    	ADAType ada = new ADAType();
    	if(!"".equals(parm.getValue("SUM_TOT", 0))){
    	//<ADA01 description="总费用">4000.00</ADA01>
    	Morethan0Prize ada01 = new Morethan0Prize();
    	BigDecimal ada01BigDec = new BigDecimal(parm.getValue("SUM_TOT", 0)); 
    	ada01.setValue(ada01BigDec);
    	ada.setADA01(ada01);
    	}else {
			errMessage("ADA01总费用", "NODE",parm.getValue("MR_NO",0)+parm.getValue("PAT_NAME",0));
		}
    	if(!"".equals(parm.getValue("SUM_TOT", 0))){
    		//<ADA0101 description="自付金额">100.00</ADA0101>
    		Prize prize = new Prize();
    		BigDecimal bigDec = new BigDecimal(parm.getValue("SUM_TOT",0));
        	prize.setValue(bigDec);
        	JAXBElement<Prize> jax = new JAXBElement<Prize>(new QName("ADA0101"), Prize.class, prize);
        	ada.setADA0101(jax);
    	}
    	
    	TParm feeParm = fee("11");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
        	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
    	        //<ADA11 description="诊察（诊疗）费">100.00</ADA11>
    	    	ada.setADA11(creat("ADA11", new Prize(), chargeParm));
        	}
    	}
    	
    	feeParm = fee("21");
    	if(feeParm.getCount()>0){
			chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA21 description="一般检查费">100.00</ADA21>
		    	ada.setADA21(creat("ADA21", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("22");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
        	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
    	        //<ADA22 description="临床物理治疗费">100.00</ADA22>
    	    	ada.setADA22(creat("ADA22", new Prize(), chargeParm));
        	}
    	}
    	
    	feeParm = fee("23");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
        	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
    	        //<ADA23 description="介入治疗费">100.00</ADA23>
    	    	ada.setADA23(creat("ADA23", new Prize(), chargeParm));
        	}
    	}
    	
    	feeParm = fee("24");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
        	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
    	        //<ADA24 description="特殊治疗费">100.00</ADA24>
    	    	ada.setADA24(creat("ADA24", new Prize(), chargeParm));
        	}
    	}
    	
    	feeParm = fee("25");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
        	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
        		//<ADA25 description="康复治疗费">100.00</ADA25>
        		ada.setADA25(creat("ADA25", new Prize(), chargeParm));
        	}
    	}
    	
    	feeParm = fee("26");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
        	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
        		//<ADA26 description="中医治疗费">100.00</ADA26>
        		ada.setADA26(creat("ADA26", new Prize(), chargeParm));
        	}
    	}
    	
    	feeParm = fee("27");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
        	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
        		//<ADA27 description="一般治疗费">100.00</ADA27>
        		ada.setADA27(creat("ADA27", new Prize(), chargeParm));
        	}
    	}
    	
    	feeParm = fee("28");
    	chargeParm = charge(chargeSql, str(feeParm));
    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
    		//<ADA28 description="精神治疗费">100.00</ADA28>
    		ada.setADA28(creat("ADA28", new Prize(), chargeParm));
    	}
    	
    	feeParm = fee("13");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
        	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
        		//<ADA13 description="接生费">100.00</ADA13>
        		ada.setADA13(creat("ADA13", new Prize(), chargeParm));
        	}
    	}
    	
    	feeParm = fee("15");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
        	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
        		//<ADA15 description="麻醉费">100.00</ADA15>
        		ada.setADA15(creat("ADA15", new Prize(), chargeParm));
        	}
    	}
    	
    	feeParm = fee("12");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
	    		//<ADA12 description="手术费">100.00</ADA12>
	    		ada.setADA12(creat("ADA12", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("29");
    	if(feeParm.getCount()>0){
	    	chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
	    		//<ADA29 description="护理治疗费">100.00</ADA29>
	    		ada.setADA29(creat("ADA29", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("03");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
	    		//<ADA03 description="护理费">100.00</ADA03>
	    		ada.setADA03(creat("ADA03", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("30");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
	    		//<ADA30 description="核素检查">100.00</ADA30>
	    		ada.setADA30(creat("ADA30", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("31");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
	    		//<ADA31 description="核素治疗">100.00</ADA31>
	    		ada.setADA31(creat("ADA31", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("32");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
	    		//<ADA32 description="超声费">100.00</ADA32>
	    		ada.setADA32(creat("ADA32", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("07");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA07 description="放射费">100.00</ADA07>
		    	ada.setADA07(creat("ADA07", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("08");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA08 description="化验费">100.00</ADA08>
		    	ada.setADA08(creat("ADA08", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("33");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA33 description="病理费">100.00</ADA33>
		    	ada.setADA33(creat("ADA33", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("34");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA34 description="监护及辅助呼吸费">100.00</ADA34>
		    	ada.setADA34(creat("ADA34", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("35");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA35 description="治疗用一次性医用材料费">100.00</ADA35>
		    	ada.setADA35(creat("ADA35", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("36");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA36 description="介入用一次性医用材料费">100.00</ADA36>
		    	ada.setADA36(creat("ADA36", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("37");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA37 description="手术用一次性医用材料费">100.00</ADA37>
		    	ada.setADA37(creat("ADA37", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("38");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA38 description="检查用一次性医用材料费">100.00</ADA38>
		    	ada.setADA38(creat("ADA38", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("02");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
	        //<ADA02 description="床位费">100.00</ADA02>
	    	ada.setADA02(creat("ADA02", new Prize(), chargeParm));
    	}
    	}
    	
    	feeParm = fee("39");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA39 description="挂号费">100.00</ADA39>
		    	ada.setADA39(creat("ADA39", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("09");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA09 description="输氧费">100.00</ADA09>
		    	ada.setADA09(creat("ADA09", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("10");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA10 description="输血费">100.00</ADA10>
		    	ada.setADA10(creat("ADA10", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("04");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA04 description="西药费">100.00</ADA04>
		    	ada.setADA04(creat("ADA04", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("40");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA40 description="抗菌药物费">100.00</ADA40>
		    	ada.setADA40(creat("ADA40", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("41");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA41 description="白蛋白类制品费">100.00</ADA41>
		    	ada.setADA41(creat("ADA41", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("42");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA42 description="球蛋白类制品费">100.00</ADA42>
		    	ada.setADA42(creat("ADA42", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("43");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA43 descrinption="凝血因子类制品费">100.00</ADA43>
		    	ada.setADA43(creat("ADA43", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("44");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA44 description="细胞因子类制品费">100.00</ADA44>
		    	ada.setADA44(creat("ADA44", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("05");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA05 description="中成药费">100.00</ADA05>
		    	ada.setADA05(creat("ADA05", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("06");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA06 description="中草药费">100.00</ADA06>
		    	ada.setADA06(creat("ADA06", new Prize(), chargeParm));
	    	}
    	}
    	
    	feeParm = fee("20");
    	if(feeParm.getCount()>0){
    		chargeParm = charge(chargeSql, str(feeParm));
	    	if((chargeParm.getCount()>0)&&(!"".equals(chargeParm.getValue("TOT_AMT",0)))){
		        //<ADA20 description="其他费用">100.00</ADA20>
		    	ada.setADA20(creat("ADA20", new Prize(), chargeParm));
	    	}
    	}
    	
    	ad.setADA(ada);
    	a.setAD(ad);
    	
    	ct.setA(a);
    	return ct;
    }
    public TParm fee(String str){
    	String feeSql = " SELECT CHARGE_HOSP_CODE FROM SYS_CHARGE_HOSP WHERE WJW_CHARGE = '"+str+"' ";
    	TParm feeParm = new TParm(TJDODBTool.getInstance().select(feeSql));
    	return feeParm;
    }
    public String str(TParm feeParm){
    	String str = feeParm.getValue("CHARGE_HOSP_CODE").toString().replaceAll(", ", "','").replace("[", "'").replace("]", "'");
    	return str;
    }
    public TParm charge(String chargeSql,String str){
    	chargeSql += "AND HEXP_CODE IN ("+str+") ";
     	TParm chargeParm = new TParm(TJDODBTool.getInstance().select(chargeSql));
    	return chargeParm;
    }
    public JAXBElement<Prize> creat(String str,Prize prize,TParm parm){
    	DecimalFormat df = new DecimalFormat("0.00");
    	prize.setValue(new BigDecimal(df.format(Double.parseDouble(parm.getValue("TOT_AMT",0)))));
    	JAXBElement<Prize> jax = new JAXBElement<Prize>(new QName(str), Prize.class, prize);
    	return jax;
    }
    public JAXBElement<Minute> creat(String str,Minute min){
    	JAXBElement<Minute> jax = new JAXBElement<Minute>(new QName(str), Minute.class, min);
    	return jax;
    }
    public JAXBElement<Integer> creat(String str,Integer in){
    	JAXBElement<Integer> jax = new JAXBElement<Integer>(new QName(str), Integer.class, in);
    	return jax;
    }
	public JAXBElement<ZipCode> creat(String str,ZipCode zip ){
    	JAXBElement<ZipCode> jax = new JAXBElement<ZipCode>(new QName(str), ZipCode.class, zip);
    	return jax;
    }
    public JAXBElement<String> creat(String str,String s ){
    	JAXBElement<String> jax = new JAXBElement<String>(new QName(str), String.class, s);
    	return jax;
    }
    public JAXBElement<jdo.sta.bean.String> creat(String str,TParm parm,String flg){
    	jdo.sta.bean.String s = new jdo.sta.bean.String();
    	s.setValue(parm.getValue(flg, 0));
    	JAXBElement<jdo.sta.bean.String> jax = new JAXBElement<jdo.sta.bean.String>(new QName(str), jdo.sta.bean.String.class, s);
    	return jax;
    }
    
    //根据用户ID查询姓名
    public TParm getName(String userid){
    	String userSql = " SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+userid+"' ";
		TParm userParm = new TParm(new TJDODBTool().getInstance().select(userSql));
		return userParm;
    }
    
    
    public void errMessage(String str,String flg,String mrNoAndName){
    	
    	if("Y".equals(flg)){
    		map.put(mrNoAndName+str, mrNoAndName+" 病理诊断编码（M码）为："+str+"所对应的上传卫计委编码或名称为空！\r\n");
    	}
    	if("N".equals(flg)){
    		map.put(mrNoAndName+str, mrNoAndName+" 门急诊诊断编码为："+str+"所对应的上传卫计委编码或名称为空！\r\n");
    	}
    	if("".equals(flg)){
    		map.put(mrNoAndName+str, mrNoAndName+" 手术及操作编码(ICD-9-CM3)："+str+"所对应的上传卫计委编码或名称为空！\r\n");
    	}
    	if("NODE".equals(flg)){
    		map.put(mrNoAndName+str, mrNoAndName+" 标签和含义为："+str+"的字段不正确！\r\n");
    	}
    }
    
    public TParm diagnosisCode(String str,String flg){
    	String codeSql = " SELECT * FROM SYS_DIAGNOSIS WHERE ICD_TYPE='W' AND MIC_FLG='"+flg+"' AND ICD_CODE = '"+str+"' ";
    	TParm codeParm = new TParm(TJDODBTool.getInstance().select(codeSql));
    	return codeParm;
    }
    
    public String getPath(){
    	JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.showOpenDialog(null);
		String path = chooser.getSelectedFile().getPath();
		return path;
    }
	/**
     * 全选
     */
	public void onSelectAll() {
		boolean flg = selectAll.isSelected();
		int count = table.getRowCount();
//		if(flg){
//			recordCount = count;//将选中数量赋给记录条数
//		}else{
//			recordCount = 0;
//		}
		
		for (int i = 0; i < count; i++) {
			table.setItem(i, "SELECT_FLG", flg);
		}
	}
    /**
     * 单击事件
     * @param row
     */
	public void onTableClicked(int row) {
		if (row < 0)
			return;
		TParm parm = table.getParmValue().getRow(row);
		this.setValue("MR_NO", parm.getValue("MR_NO"));
	}
	
	public void onCheckBoxValue(Object obj){
		table.acceptText();
		if("N".equals(table.getItemString(table.getSelectedRow(), "SELECT_FLG"))){
			table.setItem(table.getSelectedRow(), "SELECT_FLG", false);//取消选中
//			recordCount--;//记录数减1
		}else{
			table.setItem(table.getSelectedRow(), "SELECT_FLG", true);//选中
//			recordCount++;//记录数加1
		}
	}
	//判断是否为整数 
	public boolean isInteger(String str) {    
	    Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");    
	    return pattern.matcher(str).matches();    
	  } 
    
    /**
     *清空页面 
     */
    public void onClear() {
    	table.removeRowAll();
    	selectAll.setValue(false);
    	this.setValue("MR_NO", "");
    }
    /**
     * 验证
     * @param xsdPath
     * @param xmlPath
     * @return
     * @throws SAXException
     * @throws IOException
     */
	public static boolean validateXml(String xsdPath, String xmlPath)
			throws SAXException, IOException {
		
		String schemaLanguage = W3C_XML_SCHEMA_NS_URI;
		// 建立schema工厂
		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(schemaLanguage);
		// 建立验证文档文件对象，利用此文件对象所封装的文件进行schema验证
		File schemaFile = new File(xsdPath);
		// 利用schema工厂，接收验证文档文件对象生成Schema对象
		Schema schema = schemaFactory.newSchema(schemaFile);
		// 通过Schema产生针对于此Schema的验证器，利用schenaFile进行验证
		Validator validator = schema.newValidator();
		// 得到验证的数据源
		Source source = new StreamSource(xmlPath);
		// 开始验证，成功输出success!!!，失败输出fail
		validator.validate(source);
		return true;
	}
	
	public void creatZip(String string) throws IOException{
        File file = new File(string);  
        File zipFile = new File(string.replace(".xml", ".zip"));  
        InputStream input = new FileInputStream(file);  
        ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile)); 
        zipOut.putNextEntry(new ZipEntry(file.getName()));  
        // 设置注释  
        //zipOut.setComment("hello");  
        int temp = 0;  
        while((temp = input.read()) != -1){  
            zipOut.write(temp);  
        }  
        input.close();  
        zipOut.close();  
    }
	
}
