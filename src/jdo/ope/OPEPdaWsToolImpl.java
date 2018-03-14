package jdo.ope;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jws.WebService;

import jdo.sys.SystemTool;

import com.dongyang.Service.Server;

import com.dongyang.data.TParm;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 手术PDA werbservice接口
 * </p>
 * 
 * <p>
 * Description: 手术PDA werbservice接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>  
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author liuzhen 2012-6-27
 * @version 4.0
 */

@WebService
public class OPEPdaWsToolImpl implements OPEPdaWsTool {
	/** 用户登录 */
	public String login(String userID, String password) {  

		String enPass = encrypt(password);



		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String dstr = format.format(new Date());

		String sql = "SELECT count(1) AS COUNT " + "FROM SYS_OPERATOR "
				+ "WHERE USER_ID = '" + userID + "' " + "AND USER_PASSWORD='"
				+ enPass + "' " + "AND END_DATE>TO_DATE('" + dstr
				+ "','yy-mm-dd hh24:mi:ss')";

		// System.out.println("-------sql---"+sql);

		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int count = Integer.parseInt(result.getValue("COUNT", 0));
		/** 密码错误 */
		if (0 == count) {
			return "1";
		} else {
			return "0";
		}

	}

	/**
	 * 加密字串
	 * 
	 * @param text
	 *            String 源字串
	 * @return String 加密后字串
	 */
	private String encrypt(String text) {
		String av_str = "";
		try {
			byte aa[] = text.getBytes("UTF-16BE");

			StringBuffer sb = new StringBuffer();

			for (int i = 0; i < aa.length; i++) {
				aa[i] = (byte) (~aa[i]);
				sb.append(Integer.toHexString(aa[i]).substring(6));
			}
			av_str = sb.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return av_str;
	}

	/** 获取系统术间信息 */
	public String getHisRooms() {

		StringBuffer strBff = new StringBuffer();
		strBff.append("<OPE_PDA_ROOMS>");
		strBff.append("");

		String sql = "select ID,CHN_DESC from SYS_DICTIONARY where GROUP_ID = 'OPE_OPROOM'";
		Server.autoInit(this);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		for (int i = 0; i < result.getCount(); i++) {
			String id = result.getValue("ID", i);
			String chnDesc = result.getValue("CHN_DESC", i);
			strBff.append("<ROOM>");
			strBff.append("<ID>");
			strBff.append(id);
			strBff.append("</ID>");
			strBff.append("<CHN_DESC>");
			strBff.append(chnDesc);
			strBff.append("</CHN_DESC>");
			strBff.append("</ROOM>");
		}
		strBff.append("</OPE_PDA_ROOMS>");

		return strBff.toString();
	}
	/**
	 * 查询接口
	 * @param mr_No
	 * @param dateStr
	 * @param roomNo
	 * @return
	 */
	public String getOP_Data(String mr_No, String dateStr,String roomNo) {
		// 1.输入参数不合法（两个参数，具体的参数要求包括，长度，合法性。日期格式，日期限制是否是今天）
		// 2.查询过程中出错，具体出错原因javahis是否能出来
		// 3.返回状态：0成功返回，1查询出错, 2 病案号校验不通过，3日期校验不通过，4查无结果，5查出多条记录
		// 4.日志记录

		/** 校验病案号格式 */
		if (1 == this.checkStr(mr_No, "")) {  
			return this.returnIncorrectMsg(2);
		}

		/** 校验日期格式 */
		if (1 == this.checkStr(dateStr, "")) {
			return this.returnIncorrectMsg(3);
		}
		String sql = "SELECT A.CASE_NO,A.ROOM_NO, A.OP_DATE, A.MR_NO, A.MAIN_SURGEON AS MAIN_SURGEON_ID, "
				+ "A.BOOK_AST_1 AS BOOK_AST_1_ID, A.CIRCULE_USER1 AS CIRCULE_USER1_ID, "
				+ "A.CIRCULE_USER2 AS CIRCULE_USER2, A.ANA_USER1 AS ANA_USER1_ID, "
				+ "A.EXTRA_USER1 AS EXTRA_USER1_ID,A.REMARK,A.OPBOOK_SEQ, A.ANA_CODE,"
				+ " (SELECT B.CHN_DESC FROM SYS_DICTIONARY B WHERE   B.GROUP_ID = 'OPE_OPROOM' "
				+ " AND A.ROOM_NO = B.ID(+)) AS  OP_ROOM,C.BIRTH_DATE, C.HEIGHT,C.WEIGHT,C.PAT_NAME, "
				+ "F.CHN_DESC AS SEX,G.ICD_CHN_DESC,H.OPT_CHN_DESC,I.USER_NAME AS MAIN_SURGEON ,"
				+ "J.USER_NAME AS BOOK_AST_1,K.USER_NAME AS CIRCULE_USER1,L.USER_NAME AS CIRCULE_USER2,"
				+ "M.USER_NAME AS ANA_USER1,N.USER_NAME AS EXTRA_USER1,X.DEPT_CHN_DESC,Y.STATION_DESC,"
				+ "(SELECT Z.CHN_DESC FROM SYS_DICTIONARY Z  WHERE   Z.GROUP_ID = 'OPE_SITE'  AND A.PART_CODE = Z.ID(+)) AS  PART_CODE" +
				//fux modify 20151029 
				  " ,A.GDVAS_CODE,A.READY_FLG,A.VALID_DATE_FLG,A.SPECIFICATION_FLG "  
				+ "FROM OPE_OPBOOK A,SYS_PATINFO C,SYS_DEPT X,SYS_STATION Y,SYS_DICTIONARY F,"
				+ "SYS_DIAGNOSIS G,SYS_OPERATIONICD H,SYS_OPERATOR  I,SYS_OPERATOR J,"  
				+ "SYS_OPERATOR K,SYS_OPERATOR L,SYS_OPERATOR M,SYS_OPERATOR N  "
				+ "WHERE  A.MR_NO = C.MR_NO (+) "
				+ "AND A.OP_DEPT_CODE=X.DEPT_CODE "
				+ "AND A.OP_STATION_CODE=Y.STATION_CODE "
				+ "AND F.GROUP_ID = 'SYS_SEX' AND C.SEX_CODE = F.ID(+) "
				+ "AND A.DIAG_CODE1 = G.ICD_CODE(+)   "
				+ "AND A.OP_CODE1 = H.OPERATION_ICD(+)  "
				+ "AND A.MAIN_SURGEON = I.USER_ID(+) "
				+ "AND A.BOOK_AST_1 = J.USER_ID(+) "
				+ "AND A.CIRCULE_USER1 = K.USER_ID(+) "
				+ "AND A.CIRCULE_USER2 = L.USER_ID(+) "  
				+ "AND A.ANA_USER1 = M.USER_ID(+) "
				+ "AND A.EXTRA_USER1 = N.USER_ID(+) "
				+ "AND A.MR_NO='"
				+ mr_No
				+ "' "
				+ "AND OP_DATE BETWEEN "
				+ "TO_DATE('"
				+ dateStr
				+ " 00:00:00','yyyymmdd hh24:mi:ss')"
				+ "AND "
				+ "TO_DATE('"
				+ dateStr
				+ " 23:59:59','yyyymmdd hh24:mi:ss')"
				+ "AND A.CANCEL_FLG <> 'Y' " + "ORDER BY OPBOOK_SEQ";

		Server.autoInit(this);
        System.out.println("查询接口=============="+sql);  
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		/** 查询中出错 */  
		if (result.getErrCode() < 0) {  
			return this.returnIncorrectMsg(1);
		}
		/** 查无结果 */
		if (result.getCount() < 1) {
			return this.returnIncorrectMsg(4);
		}  
	
		//介入 过敏与传染病  
		String sqlAller = " SELECT ALLERGY,SCREENING,SCREENING_RESULT FROM  ADM_INP  WHERE MR_NO='"
				+ mr_No
				+ "' ";  
		TParm Allerresult = new TParm(TJDODBTool.getInstance().select(sqlAller));     
		   
        //INW_TransferSheet_WO 
		String sqlINW = " SELECT  A.BED,A.FROM_DEPT AS FROM_DEPT1 ,A.TO_DEPT AS TO_DEPT1,B.DEPT_CHN_DESC AS TO_DEPT," +
				" C.DEPT_CHN_DESC AS FROM_DEPT,A.TRANSFER_DATE,A.TEMPERATURE,PULSE," +
				" A.RESPIRE,A.SYSTOLICPRESSURE,A.DIASTOLICPRESSURE,A.ACTIVE_TOOTH_FLG,A.FALSE_TOOTH_FLG," +
				" A.GENERAL_MARK,A.ALLERGIC_FLG,A.INFECT_FLG,A.WEIGHT AS WEIGHT_MON,A.SKIN_BREAK_FLG," +
				" A.SKIN_BREAK_POSITION,A.BLOOD_TYPE,A.RHPOSITIVE_FLG,A.CROSS_MATCH,A.OPE_PRE_MARK," +
				" A.OPE_INFORM_FLG,A.ANA_SINFORM_FLG,A.BLOOD_INFORM_FLG,A.SKIN_PREPARATION_FLG,A.CROSSMATCH_FLG," +
				" A.SKIN_TEST_FLG,A.PREPARE_EDUCATION_FLG,A.BOWEL_PREPARATION_FLG,A.DENTAL_CARE_FLG,A.NASAL_CARE_FLG " +
				" FROM  INW_TransferSheet_WO A,SYS_DEPT B,SYS_DEPT C    WHERE  A.MR_NO= '"+mr_No+"' " +  
				" AND A.TRANSFER_DATE BETWEEN "
				+ "TO_DATE('"    
				+ dateStr  
				+ " 00:00:00','yyyy/mm/dd hh24:mi:ss')"              
				+ "AND "     
				+ "TO_DATE('"    
				+ dateStr
				+ " 23:59:59','yyyy/mm/dd hh24:mi:ss') AND A.TO_DEPT = B.DEPT_CODE AND A.FROM_DEPT = C.DEPT_CODE  ";
		TParm INWDresult = new TParm(TJDODBTool.getInstance().select(sqlINW)); 
		System.out.println("sqlINW:"+sqlINW);
		
		/** 查询出过敏结果 则 过敏标记 为1 有过敏 为0 是无过敏*/    
		if (Allerresult.getValue("ALLERGY",0).equals("1")) {  
			result.setData("ALLERGIC_FLG","YES");     
		}  
		else if (Allerresult.getValue("ALLERGY",0).equals("0")) {
			result.setData("ALLERGIC_FLG","NO");  
		}
		/** 术间 */
		String opRoom = result.getValue("ROOM_NO", 0);
		/** 查无结果 */
		if (!roomNo.equals("")&&!opRoom.equals("")&&!opRoom.equals(roomNo)) {
			return this.returnIncorrectMsg(9);
		}
		/** 手术单号 */
		String opbookSeq = result.getValue("OPBOOK_SEQ", 0);
		/** 时间 */
		String opDate = result.getValue("OP_DATE", 0);
		/** 姓名 */
		String patName = result.getValue("PAT_NAME", 0);
		/** 性别 */
		String sex = result.getValue("SEX", 0);
		/** 生日 */
		Date birthDate = (Date) result.getData("BIRTH_DATE", 0);
		Date today = new Date();
		int age = today.getYear() - birthDate.getYear();// 年龄

		/** 身高 */
		String height = result.getValue("HEIGHT", 0);
		/** 体重 */
		String weight = result.getValue("WEIGHT", 0);
		/** 病案号 */
		String mrNo = result.getValue("MR_NO", 0);
		/** 诊断ICD */
		String icdChnDesc = result.getValue("ICD_CHN_DESC", 0);
		/** 手术ICD */
		String optChnDesc = result.getValue("OPT_CHN_DESC", 0);
		/** 备注 */
		String remark = result.getValue("REMARK", 0);

		/** 术者 */
		String mainSurgeon = result.getValue("MAIN_SURGEON", 0);
		/** 术者id */
		//String mainSurgeonid = result.getValue("MAIN_SURGEON_ID", 0);
		/** 一助 */
		String bookAst1 = result.getValue("BOOK_AST_1", 0);
		/** 护士1 */
		String circuleUser1 = result.getValue("CIRCULE_USER1", 0);
		/** 护士2 */
		String circuleUser2 = result.getValue("CIRCULE_USER2", 0);
		/** 麻醉 */
		String anaUser1 = result.getValue("ANA_USER1", 0);
		/** 体外 */
		String extraUser1 = result.getValue("EXTRA_USER1", 0);
        
		String dept=result.getValue("DEPT_CHN_DESC", 0);
		
		String station=result.getValue("STATION_DESC", 0);
		
		String part=result.getValue("PART_CODE", 0);
		//fux modify 20151029
		String gdvas=result.getValue("GDVAS_CODE", 0);
		
		String aller_flg=result.getValue("ALLERGIC_FLG", 0);
		
		String ready_flg=result.getValue("READY_FLG", 0);
		
		String valid_flg=result.getValue("VALID_DATE_FLG", 0);
		
		String spec=result.getValue("SPECIFICATION_FLG", 0);

		
		
		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<OPE_PDA_SEARCH_RESULT>");

		strBuf.append("<STATUS>");
		if (result.getCount() == 1) {
			strBuf.append(0);
		} else {
			strBuf.append(5);
		}
		strBuf.append("</STATUS>");

		strBuf.append("<COUNT>");
		strBuf.append(result.getCount());
		strBuf.append("</COUNT>");

		strBuf.append("<RESULT>");
		strBuf.append("<OPBOOK_SEQ>");
		strBuf.append(opbookSeq);
		strBuf.append("</OPBOOK_SEQ>");

		strBuf.append("<OP_ROOM>");
		strBuf.append(opRoom);
		strBuf.append("</OP_ROOM>");

		strBuf.append("<OP_DATE>");
		strBuf.append(opDate);
		strBuf.append("</OP_DATE>");

		strBuf.append("<PAT_NAME>");
		strBuf.append(patName);
		strBuf.append("</PAT_NAME>");

		strBuf.append("<SEX>");
		strBuf.append(sex);
		strBuf.append("</SEX>");

		strBuf.append("<AGE>");
		strBuf.append(age);
		strBuf.append("</AGE>");

		strBuf.append("<HEIGHT>");
		strBuf.append(height);
		strBuf.append("</HEIGHT>");

		strBuf.append("<WEIGHT>");
		strBuf.append(weight);
		strBuf.append("</WEIGHT>");

		strBuf.append("<MR_NO>");
		strBuf.append(mrNo);
		strBuf.append("</MR_NO>");

		strBuf.append("<ICD_CHN_DESC>");
		strBuf.append(icdChnDesc);
		strBuf.append("</ICD_CHN_DESC>");

		strBuf.append("<OPT_CHN_DESC>");
		strBuf.append(optChnDesc);
		strBuf.append("</OPT_CHN_DESC>");

		strBuf.append("<REMARK>");
		strBuf.append(remark);
		strBuf.append("</REMARK>");

//		strBuf.append("<MAIN_SURGEON_ID>");
//		strBuf.append(mainSurgeonid);
//		strBuf.append("</MAIN_SURGEON_ID>");
		
		strBuf.append("<MAIN_SURGEON>");
		strBuf.append(mainSurgeon);
		strBuf.append("</MAIN_SURGEON>");

		strBuf.append("<BOOK_AST_1>");
		strBuf.append(bookAst1);
		strBuf.append("</BOOK_AST_1>");

		strBuf.append("<CIRCULE_USER1>");
		strBuf.append(circuleUser1);
		strBuf.append("</CIRCULE_USER1>");

		strBuf.append("<CIRCULE_USER2>");
		strBuf.append(circuleUser2);
		strBuf.append("</CIRCULE_USER2>");

		strBuf.append("<ANA_USER1>");
		strBuf.append(anaUser1);
		strBuf.append("</ANA_USER1>");

		strBuf.append("<EXTRA_USER1>");
		strBuf.append(extraUser1);
		strBuf.append("</EXTRA_USER1>");
		
		strBuf.append("<DEPT_CHN_DESC>");
		strBuf.append(dept);
		strBuf.append("</DEPT_CHN_DESC>");
		
		strBuf.append("<STATION_DESC>");
		strBuf.append(station);
		strBuf.append("</STATION_DESC>");
		
		strBuf.append("<PART_CODE>");
		strBuf.append(part);
		strBuf.append("</PART_CODE>");
		
		strBuf.append("<GDVAS_CODE>");
		strBuf.append(gdvas);
		strBuf.append("</GDVAS_CODE>");
		
		strBuf.append("<ALLERGIC_FLG>");
		strBuf.append(aller_flg);
		strBuf.append("</ALLERGIC_FLG>");
		
		strBuf.append("<READY_FLG>");
		strBuf.append(ready_flg);
		strBuf.append("</READY_FLG>");

		strBuf.append("<VALID_DATE_FLG>");
		strBuf.append(valid_flg);
		strBuf.append("</VALID_DATE_FLG>");
		
		strBuf.append("<SPECIFICATION_FLG>");
		strBuf.append(spec);
		strBuf.append("</SPECIFICATION_FLG>");
		//转出科室	
		String fromdept = INWDresult.getValue("FROM_DEPT", 0);
		//转入科室	
		String todept = INWDresult.getValue("TO_DEPT", 0);  
		//床号	
		String bed = INWDresult.getValue("BED", 0);
		//交接时间	
		String ransfer_date = INWDresult.getValue("TRANSFER_DATE", 0);
		//体温
		String temperature = INWDresult.getValue("TEMPERATURE", 0);
		//脉搏
		String pulse = INWDresult.getValue("PULSE", 0);
		//呼吸
		String respire = INWDresult.getValue("RESPIRE", 0);
		//高压
		String sys = INWDresult.getValue("SYSTOLICPRESSURE", 0);
		//低压
		String dia = INWDresult.getValue("DIASTOLICPRESSURE", 0);
		//是否活动牙齿
		String actve = INWDresult.getValue("ACTIVE_TOOTH_FLG", 0);
		//是否义齿
		String falseTooth = INWDresult.getValue("FALSE_TOOTH_FLG", 0);
		//一般情况备注
		String genMark = INWDresult.getValue("GENERAL_MARK", 0);
		//是否过敏
		String allergic = INWDresult.getValue("ALLERGIC_FLG", 0);
		//是否传染病
		String infectflg = INWDresult.getValue("INFECT_FLG", 0);
		//术晨体重
		String weightMon = INWDresult.getValue("WEIGHT_MON", 0);
		//是否皮损
		String skinflg = INWDresult.getValue("SKIN_BREAK_FLG", 0);
		//皮损部位
		String skinbreak = INWDresult.getValue("SKIN_BREAK_POSITION", 0);
		//血型
		String bloodtype = INWDresult.getValue("BLOOD_TYPE", 0);
		//是否RH阳性
		String rhpositiveflg = INWDresult.getValue("RHPOSITIVE_FLG", 0);
		//交叉配血
		String crossmatch = INWDresult.getValue("CROSS_MATCH", 0);
		//术前准备备注
		String opepremark = INWDresult.getValue("OPE_PRE_MARK", 0);
		//有无手术同意书
		String opeinformflg = INWDresult.getValue("OPE_INFORM_FLG", 0);
		//有无麻醉同意书
		String anasinformflg = INWDresult.getValue("ANA_SINFORM_FLG", 0);
		//有无输血同意书
		String bloodinformflg = INWDresult.getValue("BLOOD_INFORM_FLG", 0);
		//有无皮肤准备
		String skinpreflg = INWDresult.getValue("SKIN_PREPARATION_FLG", 0);
		//有无交叉配血
		String crossmatchflg = INWDresult.getValue("CROSSMATCH_FLG", 0);
		//有无皮试
		String skintestflg = INWDresult.getValue("SKIN_TEST_FLG", 0);
		//有无肠道准备
		String bowelflg = INWDresult.getValue("BOWEL_PREPARATION_FLG", 0);
		//有无术前宣教
		String prepareflg = INWDresult.getValue("PREPARE_EDUCATION_FLG", 0);
		//有无口腔清洁
		String dentalcareflg = INWDresult.getValue("DENTAL_CARE_FLG", 0);
		//有无鼻腔清洁
		String nasalcareflg = INWDresult.getValue("NASAL_CARE_FLG", 0);
//		TRANSFER_CODE	N		VARCHAR2 (20 Byte)	交接单号
//		FROM_DEPT	N		VARCHAR2 (20 Byte)	转出科室
//		TO_DEPT	N		VARCHAR2 (20 Byte)	转出科室
//		BED	N		VARCHAR2 (20 Byte)	床号
//		PAT_NAME	N		VARCHAR2 (20 Byte)	姓名
//		SEX	N		VARCHAR2 (1 Byte)	性别
//		AGE	N		VARCHAR2 (20 Byte)	年龄
//		TRANSFER_DATE	N		DATE	交接时间
//		DIAGNOSIS	Y		VARCHAR2 (50 Byte)	诊断
//		OPERATION_CODE	Y		VARCHAR2 (20 Byte)	拟行手术
		  
//		TEMPERATURE	Y		VARCHAR2 (20 Byte)	体温    
//		PULSE	Y		VARCHAR2 (20 Byte)	脉搏
//		RESPIRE	Y		VARCHAR2 (20 Byte)	呼吸
//		BP	Y		VARCHAR2 (20 Byte)	血压（SYSTOLICPRESSURE）  （DIASTOLICPRESSURE）
//		ACTIVE_TOOTH_FLG	Y		VARCHAR2 (1 Byte)	是否活动牙齿
//		FALSE_TOOTH_FLG	Y		VARCHAR2 (1 Byte)	是否义齿
//		GENERAL_MARK	Y		VARCHAR2 (100 Byte)	一般情况备注
//		ALLERGIC_FLG	Y		VARCHAR2 (1 Byte)	是否过敏
//		INFECT_FLG	Y		VARCHAR2 (1 Byte)	是否传染病
//		WEIGHT	Y		VARCHAR2 (10 Byte)	术晨体重
//		SKIN_BREAK_FLG	Y		VARCHAR2 (1 Byte)	是否皮损
//		SKIN_BREAK_POSITION	Y		VARCHAR2 (10 Byte)	皮损部位
//		BLOOD_TYPE	Y		VARCHAR2 (10 Byte)	血型
//		RHPOSITIVE_FLG	Y		VARCHAR2 (1 Byte)	是否RH阳性
//		CROSS_MATCH	Y		VARCHAR2 (10 Byte)	交叉配血
//		OPE_PRE_MARK	Y		VARCHAR2 (100 Byte)	术前准备备注
//		OPE_INFORM_FLG	N		VARCHAR2 (1 Byte)	有无手术同意书
//		ANA_SINFORM_FLG	N		VARCHAR2 (1 Byte)	有无麻醉同意书
//		BLOOD_INFORM_FLG	N		VARCHAR2 (1 Byte)	有无输血同意书
//		SKIN_PREPARATION_FLG	N		VARCHAR2 (1 Byte)	有无皮肤准备
//		CROSSMATCH_FLG	N		VARCHAR2 (1 Byte)	有无交叉配血
//		SKIN_TEST_FLG	N		VARCHAR2 (1 Byte)	有无皮试
//		BOWEL_PREPARATION_FLG	N		VARCHAR2 (1 Byte)	有无肠道准备
//		PREPARE_EDUCATION_FLG	N		VARCHAR2 (1 Byte)	有无术前宣教
//		DENTAL_CARE_FLG	N		VARCHAR2 (1 Byte)	有无口腔清洁
//		NASAL_CARE_FLG	N		VARCHAR2 (1 Byte)	有无鼻腔清洁
		
		strBuf.append("<TRANSFER_DATE>");
		strBuf.append(ransfer_date);
		strBuf.append("</TRANSFER_DATE>");
		
		
		strBuf.append("<TEMPERATURE>");
		strBuf.append(temperature);
		strBuf.append("</TEMPERATURE>");
		
		strBuf.append("<PULSE>");
		strBuf.append(pulse);
		strBuf.append("</PULSE>");
		
		strBuf.append("<RESPIRE>");
		strBuf.append(respire);
		strBuf.append("</RESPIRE>");
		
		strBuf.append("<SYSTOLICPRESSURE>");
		strBuf.append(sys);
		strBuf.append("</SYSTOLICPRESSURE>");
		
		strBuf.append("<DIASTOLICPRESSURE>");
		strBuf.append(dia);
		strBuf.append("</DIASTOLICPRESSURE>");
		

		strBuf.append("<ACTIVE_TOOTH_FLG>");
		strBuf.append(actve);
		strBuf.append("</ACTIVE_TOOTH_FLG>");

		strBuf.append("<FALSE_TOOTH_FLG>");
		strBuf.append(falseTooth);
		strBuf.append("</FALSE_TOOTH_FLG>");
		

		strBuf.append("<GENERAL_MARK>");
		strBuf.append(genMark);
		strBuf.append("</GENERAL_MARK>");
		

		strBuf.append("<ALLERGIC>");
		strBuf.append(allergic);
		strBuf.append("</ALLERGIC>");
		

		strBuf.append("<INFECT_FLG>");
		strBuf.append(infectflg);
		strBuf.append("</INFECT_FLG>");
		

		strBuf.append("<WEIGHT_MON>");
		strBuf.append(weightMon);
		strBuf.append("</WEIGHT_MON>");
		

		strBuf.append("<SKIN_BREAK_FLG>");
		strBuf.append(skinflg);
		strBuf.append("</SKIN_BREAK_FLG>");
		

		strBuf.append("<SKIN_BREAK_POSITION>");
		strBuf.append(skinbreak);
		strBuf.append("</SKIN_BREAK_POSITION>");
		

		strBuf.append("<BLOOD_TYPE>");
		strBuf.append(bloodtype);
		strBuf.append("</BLOOD_TYPE>");
		

		strBuf.append("<RHPOSITIVE_FLG>");
		strBuf.append(rhpositiveflg);
		strBuf.append("</RHPOSITIVE_FLG>");
		

		strBuf.append("<CROSS_MATCH>");
		strBuf.append(crossmatch);
		strBuf.append("</CROSS_MATCH>");
		

		strBuf.append("<OPE_PRE_MARK>");
		strBuf.append(opepremark);
		strBuf.append("</OPE_PRE_MARK>");
		

		strBuf.append("<OPE_INFORM_FLG>");
		strBuf.append(opeinformflg);
		strBuf.append("</OPE_INFORM_FLG>");
		

		strBuf.append("<ANA_SINFORM_FLG>");
		strBuf.append(anasinformflg);
		strBuf.append("</ANA_SINFORM_FLG>");
		

		strBuf.append("<BLOOD_INFORM_FLG>");
		strBuf.append(bloodinformflg);
		strBuf.append("</BLOOD_INFORM_FLG>");
		

		strBuf.append("<SKIN_PREPARATION_FLG>");
		strBuf.append(skinpreflg);
		strBuf.append("</SKIN_PREPARATION_FLG>");
		

		strBuf.append("<CROSSMATCH_FLG>");
		strBuf.append(crossmatchflg);
		strBuf.append("</CROSSMATCH_FLG>");
		

		strBuf.append("<SKIN_TEST_FLG>");
		strBuf.append(skintestflg);
		strBuf.append("</SKIN_TEST_FLG>");
		

		strBuf.append("<BOWEL_PREPARATION_FLG>");
		strBuf.append(bowelflg);
		strBuf.append("</BOWEL_PREPARATION_FLG>");
		

		strBuf.append("<PREPARE_EDUCATION_FLG>");
		strBuf.append(prepareflg);
		strBuf.append("</PREPARE_EDUCATION_FLG>");
		
		
		strBuf.append("<DENTAL_CARE_FLG>");
		strBuf.append(dentalcareflg);
		strBuf.append("</DENTAL_CARE_FLG>");
		
		
		strBuf.append("<NASAL_CARE_FLG>");
		strBuf.append(nasalcareflg);
		strBuf.append("</NASAL_CARE_FLG>");
		
		strBuf.append("<FROM_DEPT>");
		strBuf.append(fromdept);
		strBuf.append("</FROM_DEPT>");  
		
		strBuf.append("<TO_DEPT>");
		strBuf.append(todept);
		strBuf.append("</TO_DEPT>");
		
		
		strBuf.append("<BED>");  
		strBuf.append(bed);  
		strBuf.append("</BED>");

		strBuf.append("</RESULT>");
		strBuf.append("</OPE_PDA_SEARCH_RESULT>");
		return strBuf.toString();
	}

	/**
	 * 查询不成功的报文 。如校验不通过时，或者查询数据库出错时 入参：返回的状态码 出参：int类型：0通过，1不通过
	 */
	private String returnIncorrectMsg(int status) {
		StringBuffer strBuf = new StringBuffer();
		strBuf.append("<OPE_PDA_SEARCH_RESULT>");
		strBuf.append("<STATUS>");
		strBuf.append(String.valueOf(status));
		strBuf.append("</STATUS>");
		strBuf.append("<COUNT>");
		strBuf.append(String.valueOf(0));
		strBuf.append("</COUNT>");
		strBuf.append("<RESULT>");
		strBuf.append("<OPBOOK_SEQ></OPBOOK_SEQ>");
		strBuf.append("<OP_ROOM></OP_ROOM>");
		strBuf.append("<OP_DATE></OP_DATE>");
		strBuf.append("<PAT_NAME></PAT_NAME>");
		strBuf.append("<SEX></SEX>");
		strBuf.append("<AGE></AGE>");
		strBuf.append("<HEIGHT></HEIGHT>");
		strBuf.append("<WEIGHT></WEIGHT>");
		strBuf.append("<MR_NO></MR_NO>");
		strBuf.append("<ICD_CHN_DESC></ICD_CHN_DESC>");
		strBuf.append("<OPT_CHN_DESC></OPT_CHN_DESC>");
		strBuf.append("<REMARK></REMARK>");
		strBuf.append("<MAIN_SURGEON></MAIN_SURGEON>");
		strBuf.append("<BOOK_AST_1></BOOK_AST_1>");
		strBuf.append("<CIRCULE_USER1></CIRCULE_USER1>");
		strBuf.append("<CIRCULE_USER2></CIRCULE_USER2>");
		strBuf.append("<ANA_USER1></ANA_USER1>");
		strBuf.append("<EXTRA_USER1></EXTRA_USER1>");
		strBuf.append("</RESULT>");
		strBuf.append("</OPE_PDA_SEARCH_RESULT>");
		return strBuf.toString();
	}
	
	
	/**
	 * 根据 术间查询手术类型
	 * */

	public String getOPType(String roomNo) {
		// 是否应该只能查询出来没有接受人员的 ？
		String sql = "SELECT A.TYPE_CODE,A.ROOM_NO "
				+ " FROM OPE_IPROOM A,SYS_DICTIONARY B " 
				+ " WHERE A.TYPE_CODE = B.ID" +
				  " AND B.GROUP_ID = 'OPE_TYPE'"
				+ " AND A.ROOM_NO = '"   
				+ roomNo + "'";                 
		// Server.autoInit(this);  
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// System.out.println("---sql-查询术间信息-"+sql);
		/** 查询中出错 */
		if (result.getErrCode() < 0) {
			return this.returnIncorrectMsg(1);
		}
		/** 查无结果 */
		if (result.getCount() < 1) {
			return this.returnIncorrectMsg(4);     
		}

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<OPE_TYPE_RESULT>");

		strBuf.append("<STATUS>");
		if (result.getCount() == 1) {
			strBuf.append(0);
		} else {
			strBuf.append(5);
		}
		strBuf.append("</STATUS>");

		strBuf.append("<COUNT>");
		strBuf.append(result.getCount());
		strBuf.append("</COUNT>");
		strBuf.append("<TYPE>");
		strBuf.append(result.getValue("TYPE_CODE", 0));  
		strBuf.append("</TYPE>");

		strBuf.append("</OPE_TYPE_RESULT>");
		// System.out.println(strBuf.toString());
		return strBuf.toString();
	}
	
	
	/**
	 * 插入 ope_check介入安全核查表
	 * */

	public String saveOpe_check(String str) {
		//srr 里有 手术单号 核查时间等信息
		// 是否应该只能查询出来没有接受人员的 ？
		
		int opbook_start = str.indexOf("<OPBOOK_SEQ>");
		int opbook_end = str.indexOf("</OPBOOK_SEQ>");
		String lengthopbook = "<OPBOOK_SEQ>";
		int lengthnumopbook = lengthopbook.length();
		String opbookSeq = str.substring(opbook_start + lengthnumopbook,
				opbook_end);
		//System.out.println("opbookSeq:"+opbookSeq);
		
		int ip_start = str.indexOf("<IP>");
		int ip_end = str.indexOf("</IP>");
		String lengthip = "<IP>";
		int lengthnumip = lengthip.length();
		String ip = str.substring(ip_start + lengthnumip,
				ip_end);
		//System.out.println("ip:"+ip);
		
		
		int checkDate_start = str.indexOf("<CHECK_DATE>");
		int checkDate_end = str.indexOf("</CHECK_DATE>");
		String lengthcheckDate = "<CHECK_DATE>";
		int lengthnumcheckDate = lengthcheckDate.length();
		String checkDate = str.substring(checkDate_start + lengthnumcheckDate,
				checkDate_end);
		//System.out.println("checkDate:"+checkDate);
		
		int user_start = str.indexOf("<CHECK_NS_CODE>");
		int user_end = str.indexOf("</CHECK_NS_CODE>");
		String lengthuser = "<CHECK_NS_CODE>";
		int lengthnumuser = lengthuser.length();
		String user = str.substring(user_start + lengthnumuser,
				user_end);
		//System.out.println("user:"+user);  
		
		
		String sqlSelect = " SELECT OPBOOK_SEQ FROM OPE_CHECK WHERE OPBOOK_SEQ = '"+opbookSeq+"'  ";
		
		TParm resultquery = new TParm(TJDODBTool.getInstance().select(
				sqlSelect)); 
		TParm result = new TParm();
		//查询的到 不执行插入动作 
		if(resultquery.getCount("OPBOOK_SEQ")>0){
			result = resultquery;
			
		}
		//查询不到 进行ope_check的插入动作
		else{
			String sql = "SELECT A.ROOM_NO, A.OP_DATE, A.MR_NO, A.MAIN_SURGEON AS MAIN_SURGEON_ID, "
				+ "A.BOOK_AST_1 AS BOOK_AST_1_ID, A.CIRCULE_USER1 AS CIRCULE_USER1_ID, "
				+ "A.CIRCULE_USER2 AS CIRCULE_USER2, A.ANA_USER1 AS ANA_USER1_ID, "
				+ "A.EXTRA_USER1 AS EXTRA_USER1_ID,A.REMARK,A.OPBOOK_SEQ, A.ANA_CODE,"
				+ "B.CHN_DESC AS OP_ROOM,C.BIRTH_DATE, C.HEIGHT,C.WEIGHT,C.PAT_NAME, "
				+ "F.CHN_DESC AS SEX,G.ICD_CHN_DESC,H.OPT_CHN_DESC,I.USER_NAME AS MAIN_SURGEON ,"
				+ "J.USER_NAME AS BOOK_AST_1,K.USER_NAME AS CIRCULE_USER1,L.USER_NAME AS CIRCULE_USER2,"
				+ "M.USER_NAME AS ANA_USER1,N.USER_NAME AS EXTRA_USER1,A.GDVAS_CODE," +
				  " A.READY_FLG,A.VALID_DATE_FLG,A.SPECIFICATION_FLG,A.OP_CODE1,O.ALLERGY,C.SEX_CODE "
				+ "FROM OPE_OPBOOK A,SYS_DICTIONARY B,SYS_PATINFO C,SYS_DICTIONARY F,"
				+ "SYS_DIAGNOSIS G,SYS_OPERATIONICD H,SYS_OPERATOR  I,SYS_OPERATOR J,"
				+ "SYS_OPERATOR K,SYS_OPERATOR L,SYS_OPERATOR M,SYS_OPERATOR N,ADM_INP O  "
				+ "WHERE B.GROUP_ID = 'OPE_OPROOM'AND A.ROOM_NO = B.ID(+) "
				+ "AND A.MR_NO = C.MR_NO (+) "
				+ "AND F.GROUP_ID = 'SYS_SEX' AND C.SEX_CODE = F.ID(+) "  
				+ "AND A.DIAG_CODE1 = G.ICD_CODE(+)   " 
				+ "AND A.OP_CODE1 = H.OPERATION_ICD(+)  "
				+ "AND A.MAIN_SURGEON = I.USER_ID(+) "   
				+ "AND A.BOOK_AST_1 = J.USER_ID(+) "
				+ "AND A.CIRCULE_USER1 = K.USER_ID(+) "
				+ "AND A.CIRCULE_USER2 = L.USER_ID(+) "  
				+ "AND A.ANA_USER1 = M.USER_ID(+) "
				+ "AND A.EXTRA_USER1 = N.USER_ID(+)" +
				  "AND A.MR_NO = O.MR_NO(+) "
				+ "AND A.OPBOOK_SEQ = '"+opbookSeq+"'  ORDER BY OPBOOK_SEQ";               
			// Server.autoInit(this);  

			    resultquery = new TParm(TJDODBTool.getInstance().select(
					sql)); 
			    
				String checkNo = SystemTool.getInstance().getNo("ALL", "OPE",
						"CHECK_NO", "CHECK_NO"); 
				
				SystemTool.getInstance().getNo("ALL", "OPE", "OPBOOK_SEQ",
		        "OPBOOK_SEQ");
				
				Timestamp now = SystemTool.getInstance().getDate();
				String time = StringTool.getString(now, "yyyyMMddHHmmss");    
				StringBuffer strBuf = new StringBuffer();  
				//TO_DATE('1958-06-08 00:00:00.0','YYYYMMDDHH24MISS')    
				strBuf.append("INSERT INTO OPE_CHECK (CHECK_NO,MR_NO,PAT_NAME,SEX,BIRTH_DATE,OPBOOK_SEQ,TYPE_CODE," +  
						"OPERATION_ICD,OPT_CHN_DESC,ALLERGIC_FLG, READY_FLG,VALID_DATE_FLG," +
						"SPECIFICATION_FLG,CHECK_DR_CODE,CHECK_NS_CODE, CHECK_DATE,OPT_USER,OPT_TERM,OPT_DATE)" +  
						" VALUES ('"+checkNo+"','"+resultquery.getValue("MR_NO",0)+"','"+resultquery.getValue("PAT_NAME",0)+"'," +
						"'"+resultquery.getValue("SEX_CODE",0)+"',TO_DATE('"+resultquery.getValue("BIRTH_DATE",0).replace('-', '/').substring(0, 10)+"','YYYY/MM/DD')," +
						" '"+opbookSeq+"','"+resultquery.getValue("GDVAS_CODE",0)+"','"+resultquery.getValue("OP_CODE1",0)+"'," +    
						" '"+resultquery.getValue("OPT_CHN_DESC",0)+"'," +    
						" '"+resultquery.getValue("ALLERGY",0)+"','"+resultquery.getValue("READY_FLG",0)+"','"+resultquery.getValue("VALID_DATE_FLG",0)+"'," +
						"'"+resultquery.getValue("SPECIFICATION_FLG",0)+"','"+resultquery.getValue("MAIN_SURGEON_ID",0)+"','"+user+"'," +
						"TO_DATE('"+checkDate+"','YYYYMMDDHH24MISS'),'"+user+"','"+ip+"',TO_DATE('"+time+"','YYYYMMDDHH24MISS'))");
			     result = new TParm(TJDODBTool.getInstance().update(
						strBuf.toString()));  
		}
		

		if (result.getErrCode() < 0) {      
			return this.returnSaveMsg(1);  
		}  
		// System.out.println(strBuf.toString());        
		return this.returnSaveMsg(0);  
	}
	
	
	
	

	/** 保存接口 */
	// 校验参数
	// 返回状态：0 保存成功，1 保存数据库失败，2 OPBOOK_SEQ校验不通过，3 ROOM_NO校验不通过，4
	// TRANSFER_USER校验不通过，
	// 5 TRANSFER_DATE校验不通过，6 TIMEOUT_USER校验不通过，7 TIMEOUT_DATE校验不通过，
	// 8 DR_CONFORM_FLG校验不通过，9 ANA_CONFORM_FLG校验不通过
	// 日志
	public String saveOP_Data(String opeBookSeq, String roomNo,
			String transferUser, String transfer_Date, String timeoutUser,
			String timeoutDate, String drConformFlg, String anaConformFlg,String type) {
		/** 校验opeBookSeq */
		if (this.isBlank(opeBookSeq) || 1 == this.checkStr(opeBookSeq, "")) {
			return this.returnSaveMsg(2);
		}
		/** 校验roomNo */
		if (!this.isBlank(roomNo) && 1 == this.checkStr(roomNo, "")) {
			return this.returnSaveMsg(3);
		}
		/** 校验transferUser */
		if (!this.isBlank(transferUser) && 1 == this.checkStr(transferUser, "")) {
			return this.returnSaveMsg(4);
		}
		/** 校验transfer_Date */
		if (!this.isBlank(transfer_Date)
				&& 1 == this.checkStr(transfer_Date, "")) {
			return this.returnSaveMsg(5);
		}
		/** 校验timeoutUser */
		if (!this.isBlank(timeoutUser) && 1 == this.checkStr(timeoutUser, "")) {
			return this.returnSaveMsg(6);
		}
		/** 校验timeoutDate */
		if (!this.isBlank(timeoutDate) && 1 == this.checkStr(timeoutDate, "")) {
			return this.returnSaveMsg(7);
		}
		/** 校验drConformFlg */
		if (!this.isBlank(drConformFlg) && 1 == this.checkStr(drConformFlg, "")) {
			return this.returnSaveMsg(8);
		}
		/** 校验anaConformFlg */
		if (!this.isBlank(anaConformFlg)
				&& 1 == this.checkStr(anaConformFlg, "")) {
			return this.returnSaveMsg(9);
		}

		StringBuffer strBuf = new StringBuffer();
		strBuf.append("UPDATE OPE_OPBOOK SET ");

		/** roomNo */
		if (!this.isBlank(roomNo)) {
			strBuf.append("ROOM_NO='");
			strBuf.append(roomNo);
			strBuf.append("',");
		}
		/** transferUser */
		if (!this.isBlank(transferUser)) {
			strBuf.append("TRANSFER_USER='");
			strBuf.append(transferUser);
			strBuf.append("', ");
		}
		/** transfer_Date */
		if (!this.isBlank(transfer_Date)) {
			strBuf.append("TRANSFER_DATE=TO_DATE('");
			strBuf.append(transfer_Date);
			strBuf.append("','yy-mm-dd hh24:mi:ss'),");
		}

		/** timeoutUser */
		if (!this.isBlank(timeoutUser)) {
			strBuf.append("TIMEOUT_USER='");
			strBuf.append(timeoutUser);
			strBuf.append("', ");
		}
		/** timeoutDate */
		if (!this.isBlank(timeoutDate)) {
			strBuf.append(" TIMEOUT_DATE=TO_DATE('");
			strBuf.append(timeoutDate);
			strBuf.append("','yy-mm-dd hh24:mi:ss'),");
		}
		/** drConformFlg */
		if (!this.isBlank(drConformFlg)) {
			strBuf.append("DR_CONFORM_FLG='");
			strBuf.append(drConformFlg);
			strBuf.append("',");
		}
		/** anaConformFlg */
		if (!this.isBlank(anaConformFlg)) {
			strBuf.append("ANA_CONFORM_FLG='");
			strBuf.append(anaConformFlg);
			strBuf.append("', ");
		}
		strBuf.append("OPBOOK_SEQ='");
		strBuf.append(opeBookSeq);
		strBuf.append("' ");

		strBuf.append(" WHERE OPBOOK_SEQ='");
		strBuf.append(opeBookSeq);
		strBuf.append("'");

		Server.autoInit(this);

		// System.out.println("---strBuf.toString()--:"+strBuf.toString());

		TParm result = new TParm(TJDODBTool.getInstance().update(
				strBuf.toString()));

		if (result.getErrCode() < 0) {  
			return this.returnSaveMsg(1);
		}
		
		//介入(安全核查)/手术(手术交接) 
		if(type.equals("SAVE")){
			 //介入安全核查  绑定 + 插入 ope_check表
			 strBuf = new StringBuffer();
				strBuf.append("UPDATE OPE_IPROOM SET ");
				/** roomNo */
				if (!this.isBlank(roomNo)) {
					strBuf.append("OPBOOK_SEQ='");
					strBuf.append(opeBookSeq);          
					strBuf.append("' ");
				}  
				strBuf.append(" WHERE ROOM_NO='");
				strBuf.append(roomNo);      
				strBuf.append("'");

				Server.autoInit(this);
		    
			    System.out.println("---strBuf.toString()--:"+strBuf.toString());

			    result = new TParm(TJDODBTool.getInstance().update(
						strBuf.toString()));   
				if (result.getErrCode() < 0) {
					return this.returnSaveMsg(1);
				}
				
				
		}
	   
		


		return this.returnSaveMsg(0);
	}

	/**
	 * 保存接口返回报文 入参：返回的状态码 出参：int类型：0通过，1不通过
	 */
	private String returnSaveMsg(int status) {

		StringBuffer strBuf = new StringBuffer();

		strBuf.append("<OPE_PAD_SAVE_RESULT>");
		strBuf.append("<STATUS>");
		strBuf.append(String.valueOf(status));
		strBuf.append("</STATUS>");
		strBuf.append("</OPE_PAD_SAVE_RESULT>");

		return strBuf.toString();
	}

	/**
	 * 校验字符串 入参：正则表达式 出参：int类型：0通过，1不通过
	 */
	private int checkStr(String str, String regMath) {
		// if(!str.matches(regMath)){
		// return 1;
		// }
		return 0;
	}

	/** 校验是否为空 */
	private boolean isBlank(String str) {
		if (null == str || "".equals(str.trim())) {
			return true;
		} else {
			return false;
		}
	}

	
	//SUM_VTSNTPRDTL
	//
	
}
