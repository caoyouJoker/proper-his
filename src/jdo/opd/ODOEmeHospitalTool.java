package jdo.opd;


import jdo.adm.ADMInpTool;
import jdo.adm.ADMResvTool;
import jdo.sys.CTZTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

public class ODOEmeHospitalTool extends TJDOTool{
	
	/**
     * 实例
     */
    public static ODOEmeHospitalTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ODOEmeHospitalTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ODOEmeHospitalTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ODOEmeHospitalTool() {
        setModuleName("opd\\ODOEmeHospitalModule.x");
        onInit();
    }
    
    public TParm doSave(TParm parm){
    	TParm result = new TParm();
    	//String stateFlg = parm.getValue("STATE_FLG");
    	TParm selectParm=ADMResvTool.getInstance().selectAll(parm);
    	if(selectParm.getErrCode()<0){
    		return selectParm;
    	}
    	if(selectParm.getCount()<=0){
	    	result = this.update("insertResv", parm);
			if (result.getErrCode() < 0) {
	            err("ERR:" + result.getErrCode() + result.getErrText() +
	                result.getErrName());
	            return result;
	        }
		}else{
            result = ADMResvTool.getInstance().upDateForInp(parm);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() +
                    result.getErrName());
                return result;
            }
		}
    	return result;
    	
    	
    }
    public TParm insertOperSql(TParm operParm,TConnection conn){
    	TParm result = this.update("insertOperSql", operParm, conn);
    	return result;
    }
    public TParm cancelOpBook(TParm operParm,TConnection conn){
    	TParm result = this.update("cancelOpBook", operParm, conn);
		
		return result;
    }
    public TParm updateOperSql(TParm parmStatus,TConnection conn){
    	
    	TParm result = this.update("updateOperSql", parmStatus, conn);
		
		return result;
    }
    public TParm updateSysSql(TParm parm,TConnection conn){
//    	String updateSysSql = "UPDATE SYS_PATINFO SET IPD_NO='"
//			+ parm.getValue("IPD_NO") + "',RCNT_IPD_DATE=to_date('"
//			+ parm.getValue("RESV_DATE")
//			+ "','yyyy-mm-dd hh24-mi-ss') WHERE MR_NO='"
//			+ parm.getValue("MR_NO") + "'";
    	TParm result = this.update("updateSysSql", parm, conn);
		return result;
    }
    public TParm updateInpSql(TParm parm,TConnection conn){
//    	String updateInpSql = "UPDATE ADM_INP SET URG_FLG='Y' WHERE CASE_NO='"
//			+ parm.getValue("CASE_NO") + "'";
    	TParm result = this.update("updateInpSql", parm, conn);
		return result;
    }
    
    /**
	 * 创建首页信息（住院登记接口）
	 * 
	 * @param parm
	 *            TParm 参数：CASE_NO，MR_NO，OPT_USER,OPT_TERM;HOSP_ID
	 * @return TParm
	 */
	public TParm insertMRO(TParm parm,TConnection conn) {
		TParm result = this.update("creatMRO", parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
     * 插入病历主档
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm insertMRO_MRV(TParm parm,TConnection conn){
        TParm result = this.update("insertMRO_MRV",parm,conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
	 * 修改住院信息（住院登记接口）
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm updateADMData(TParm parm,TConnection conn) {
		TParm result = this.update("updateADMData", parm,conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
    
    public TParm insertEmrFileIndex(TParm parm,TConnection conn){
//    	String updateInpSql = "UPDATE ADM_INP SET URG_FLG='Y' WHERE CASE_NO='"
//			+ parm.getValue("CASE_NO") + "'";
    	TParm result = this.update("insertEmrFileIndexSql", parm, conn);
		return result;
    }
    
    public TParm updateMROPatInfo(TParm p,TConnection conn) {
  		TParm parm = new TParm();
  		TParm result = new TParm();
  		Pat pat = Pat.onQueryByMrNo(p.getValue("MR_NO"));
  		// Patinfo取得参数
  		parm.setData("CASE_NO", p.getValue("CASE_NO"));
  		parm.setData("PAT_NAME", pat.getName());
  		parm.setData("IDNO", pat.getIdNo());
  		parm.setData("SEX", pat.getSexCode());
  		parm.setData("BIRTH_DATE", StringTool.getString(pat.getBirthday(),
  				"yyyyMMdd"));
  		parm.setData("MARRIGE", pat.getMarriageCode());
  		parm.setData("AGE", p.getValue("AGE"));// 计算年龄
  		// 根据生日和入院时间计算
  		parm.setData("NATION", pat.getNationCode());
  		parm.setData("FOLK", pat.getSpeciesCode());
  		parm.setData("CTZ1_CODE", pat.getCtz1Code());
  		parm.setData("H_TEL", "");// 户口电话 用的 家庭电话
  		parm.setData("H_ADDRESS", pat.getResidAddress());// 户籍地址
  		parm.setData("H_POSTNO", pat.getResidPostCode());// 户籍邮编
  		parm.setData("OCCUPATION", pat.getOccCode());// 职业
  		parm.setData("OFFICE", pat.getCompanyDesc());// 单位	
  		// wanglong add 20141103 增加住院次数
          parm.setData("CANCEL_FLG", "N");
          //modify by guoy 20150804 ------start------
          TParm incountParm = new TParm();
          incountParm.setData("MR_NO",p.getValue("MR_NO"));
          incountParm.setData("CANCEL_FLG", "N");
          TParm INCOUNT = ADMInpTool.getInstance().selectall(incountParm); // 查询该病人的所有未取消的住院信息
          //------end-------------------
          if (INCOUNT.getErrCode() < 0) {
              parm.setData("IN_COUNT", 1);// 住院次数
          } else {
              parm.setData("IN_COUNT", INCOUNT.getCount());// 住院次数
          }
  		parm.setData("O_TEL", pat.getTelCompany());// 单位电话
  		parm.setData("O_ADDRESS", pat.getCompanyAddress());// 单位地址
  		parm.setData("O_POSTNO", pat.getCompanyPost());// 单位邮编
  		parm.setData("CONTACTER", pat.getContactsName());// 联系人
  		parm.setData("RELATIONSHIP", pat.getRelationCode());// 联系人关系
  		parm.setData("CONT_TEL", pat.getContactsTel());// 联系人电话
  		parm.setData("CONT_ADDRESS", pat.getContactsAddress());// 联系人地址
  		parm.setData("HOMEPLACE_CODE", pat.gethomePlaceCode());// 出生地代码
  		// ----------------shibl modify 20120512
  		parm.setData("BLOOD_TYPE", getBloodTypeTran(pat.getBloodType()));// 血型
  		parm.setData("RH_TYPE", getBloodRHType(pat.getBloodRHType()));// RH类型
  		// add
  		String mroCtz = "";
  		TParm ctzParm = CTZTool.getInstance().getMroCtz(pat.getCtz1Code());
  		if (ctzParm.getCount() > 0) {
  			mroCtz = ctzParm.getValue("MRO_CTZ", 0);
  		}
  		parm.setData("MRO_CTZ", mroCtz);// 病案首页身份
  		parm.setData("BIRTHPLACE", pat.getBirthPlace());// 籍贯
  		parm.setData("ADDRESS", pat.getAddress());// 通信地址
  		parm.setData("POST_NO", pat.getPostCode());// 通信邮编
  		parm.setData("TEL", pat.getTelHome());// 电话
  		parm.setData("NHI_NO", pat.getNhiNo()); // 医保卡号
  		parm.setData("NHICARD_NO", pat.getNhicardNo()); // 健康卡号
  		parm.setData("OPT_USER", p.getValue("OPT_USER"));
  		parm.setData("OPT_TERM", p.getValue("OPT_TERM"));
  		result = this.update("updateMROPatInfo", parm,conn);
  		if (result.getErrCode() < 0) {
  			err("ERR:" + result.getErrCode() + result.getErrText()
  					+ result.getErrName());
  			return result;
  		}
  		return result;
  	}
    
    /**
 	 * 1.A 2.B 3.O 4.AB 5.不详 6.未查
 	 * 
 	 * @param type
 	 * @return
 	 */
 	private String getBloodTypeTran(String type) {
 		String tranType = "";
 		if (type.equalsIgnoreCase("A")) {
 			tranType = "1";
 		} else if (type.equalsIgnoreCase("B")) {
 			tranType = "2";
 		} else if (type.equalsIgnoreCase("O")) {
 			tranType = "3";
 		} else if (type.equalsIgnoreCase("AB")) {
 			tranType = "4";
 		} else if (type.equalsIgnoreCase("T")) {
 			tranType = "5";
 		} else {
 			tranType = "6";
 		}
 		return tranType;
 	}
 	
 	/**
 	 * 1.阴 2.阳 3.不详 4.未查
 	 * 
 	 * @param type
 	 * @return
 	 */
 	private String getBloodRHType(String type) {
 		String tranType = "";
 		if (type.equals("+")) {
 			tranType = "1";
 		} else if (type.equals("-")) {
 			tranType = "2";
 		} else {
 			tranType = "4";
 		}
 		return tranType;
 	}
    
    public TParm doCancel(TParm parm,TConnection conn){
    	TParm result = new TParm();
    	String sql = "SELECT OPBOOK_SEQ FROM OPE_OPBOOK WHERE CASE_NO = '"+parm.getValue("CASE_NO")+"' AND ADM_TYPE = 'I'";
    	TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql));
    	if(parm2.getCount()>0){
    		result = this.update("updateOpeSql2", parm, conn);//还原急诊注记
        	if(result.getErrCode()<0){
                conn.close();
                return result;
            }
        	result = this.update("updateOpeSql3", parm, conn);//取消住院手术
        	if(result.getErrCode()<0){
                conn.close();
                return result;
            }
    	}
    	//result = this.update("updateAdmInp", parm, conn);
    	String sql2 = "SELECT OPBOOK_SEQ FROM OPE_OPBOOK WHERE CASE_NO = '"+parm.getValue("CASE_NO")+"' AND ADM_TYPE = 'I' AND STATE = '1'";
		TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql2));
		if(parm3.getCount()>0){
			result = this.update("updateOpeSql", parm, conn);//更新住院手术排程字段
			if(result.getErrCode()<0){
	            conn.close();
	            return result;
	        }
//	    	result = this.update("updateOpeSql2", parm, conn);
//	    	if(result.getErrCode()<0){
//	            conn.close();
//	            return result;
//	        }
		}
		
//    	result = this.update("updateOpeSql", parm, conn);
//    	result = this.update("updateOpeSql2", parm, conn);
//    	String updateOpeSql = "UPDATE OPE_OPBOOK SET ROOM_NO = NULL,CIRCULE_USER1= NULL,CIRCULE_USER2= NULL,CIRCULE_USER3= NULL,CIRCULE_USER4= NULL,SCRUB_USER1= NULL,SCRUB_USER2= NULL,SCRUB_USER3= NULL,SCRUB_USER4= NULL,ANA_USER1= NULL,ANA_USER2= NULL,EXTRA_USER1= NULL,EXTRA_USER2= NULL,STATE= NULL,READY_FLG= NULL,VALID_DATE_FLG= NULL,SPECIFICATION_FLG= NULL,APROVE_DATE= NULL,APROVE_USER= NULL,OPT_USER= NULL,OPT_DATE= '"+parm.getValue("OPT_DATE")+"',OPT_TERM= '"+parm.getValue("OPT_TERM")+"' WHERE OPBOOK_SEQ = '"+parm.getData("OPBOOK_SEQ")+"'";
//    	TParm result = new TParm(TJDODBTool.getInstance().update(updateOpeSql));
//    	String updateOpeSql2 = "UPDATE OPE_OPBOOK SET CANCEL_FLG = 'N' WHERE CASE_NO = '"+parm.getValue("OPD_CASE_NO", 0)+"' AND ADM_TYPE = 'O'";
//    	result = (TParm) TJDODBTool.getInstance().update(updateOpeSql2);
    	return result;
    }
    
    public TParm getMessage(TParm parm){
    	String oper = " SELECT OPBOOK_SEQ,ADM_TYPE,MR_NO,IPD_NO,CASE_NO,BED_NO,URGBLADE_FLG,OP_DATE,TF_FLG,TIME_NEED,ROOM_NO,TYPE_CODE,ANA_CODE,OP_DEPT_CODE,OP_STATION_CODE,DIAG_CODE1,DIAG_CODE2,DIAG_CODE3,BOOK_DEPT_CODE,OP_CODE1,OP_CODE2,BOOK_DR_CODE,MAIN_SURGEON,BOOK_AST_1,BOOK_AST_2,BOOK_AST_3,BOOK_AST_4,REMARK,STATE,CANCEL_FLG,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,PART_CODE,ISO_FLG,GDVAS_CODE,GRANT_AID FROM OPE_OPBOOK WHERE CASE_NO='"+parm.getValue("OPD_CASE_NO")+"' AND  ADM_TYPE IN('O','E') AND CANCEL_FLG<>'Y' ORDER BY OP_DATE DESC";
    	TParm operParm = new TParm(TJDODBTool.getInstance().select(oper));
    	return operParm;

    }
}
