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
     * ʵ��
     */
    public static ODOEmeHospitalTool instanceObject;
    /**
     * �õ�ʵ��
     * @return SchWeekTool
     */
    public static ODOEmeHospitalTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ODOEmeHospitalTool();
        return instanceObject;
    }

    /**
     * ������
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
	 * ������ҳ��Ϣ��סԺ�Ǽǽӿڣ�
	 * 
	 * @param parm
	 *            TParm ������CASE_NO��MR_NO��OPT_USER,OPT_TERM;HOSP_ID
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
     * ���벡������
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
	 * �޸�סԺ��Ϣ��סԺ�Ǽǽӿڣ�
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
  		// Patinfoȡ�ò���
  		parm.setData("CASE_NO", p.getValue("CASE_NO"));
  		parm.setData("PAT_NAME", pat.getName());
  		parm.setData("IDNO", pat.getIdNo());
  		parm.setData("SEX", pat.getSexCode());
  		parm.setData("BIRTH_DATE", StringTool.getString(pat.getBirthday(),
  				"yyyyMMdd"));
  		parm.setData("MARRIGE", pat.getMarriageCode());
  		parm.setData("AGE", p.getValue("AGE"));// ��������
  		// �������պ���Ժʱ�����
  		parm.setData("NATION", pat.getNationCode());
  		parm.setData("FOLK", pat.getSpeciesCode());
  		parm.setData("CTZ1_CODE", pat.getCtz1Code());
  		parm.setData("H_TEL", "");// ���ڵ绰 �õ� ��ͥ�绰
  		parm.setData("H_ADDRESS", pat.getResidAddress());// ������ַ
  		parm.setData("H_POSTNO", pat.getResidPostCode());// �����ʱ�
  		parm.setData("OCCUPATION", pat.getOccCode());// ְҵ
  		parm.setData("OFFICE", pat.getCompanyDesc());// ��λ	
  		// wanglong add 20141103 ����סԺ����
          parm.setData("CANCEL_FLG", "N");
          //modify by guoy 20150804 ------start------
          TParm incountParm = new TParm();
          incountParm.setData("MR_NO",p.getValue("MR_NO"));
          incountParm.setData("CANCEL_FLG", "N");
          TParm INCOUNT = ADMInpTool.getInstance().selectall(incountParm); // ��ѯ�ò��˵�����δȡ����סԺ��Ϣ
          //------end-------------------
          if (INCOUNT.getErrCode() < 0) {
              parm.setData("IN_COUNT", 1);// סԺ����
          } else {
              parm.setData("IN_COUNT", INCOUNT.getCount());// סԺ����
          }
  		parm.setData("O_TEL", pat.getTelCompany());// ��λ�绰
  		parm.setData("O_ADDRESS", pat.getCompanyAddress());// ��λ��ַ
  		parm.setData("O_POSTNO", pat.getCompanyPost());// ��λ�ʱ�
  		parm.setData("CONTACTER", pat.getContactsName());// ��ϵ��
  		parm.setData("RELATIONSHIP", pat.getRelationCode());// ��ϵ�˹�ϵ
  		parm.setData("CONT_TEL", pat.getContactsTel());// ��ϵ�˵绰
  		parm.setData("CONT_ADDRESS", pat.getContactsAddress());// ��ϵ�˵�ַ
  		parm.setData("HOMEPLACE_CODE", pat.gethomePlaceCode());// �����ش���
  		// ----------------shibl modify 20120512
  		parm.setData("BLOOD_TYPE", getBloodTypeTran(pat.getBloodType()));// Ѫ��
  		parm.setData("RH_TYPE", getBloodRHType(pat.getBloodRHType()));// RH����
  		// add
  		String mroCtz = "";
  		TParm ctzParm = CTZTool.getInstance().getMroCtz(pat.getCtz1Code());
  		if (ctzParm.getCount() > 0) {
  			mroCtz = ctzParm.getValue("MRO_CTZ", 0);
  		}
  		parm.setData("MRO_CTZ", mroCtz);// ������ҳ���
  		parm.setData("BIRTHPLACE", pat.getBirthPlace());// ����
  		parm.setData("ADDRESS", pat.getAddress());// ͨ�ŵ�ַ
  		parm.setData("POST_NO", pat.getPostCode());// ͨ���ʱ�
  		parm.setData("TEL", pat.getTelHome());// �绰
  		parm.setData("NHI_NO", pat.getNhiNo()); // ҽ������
  		parm.setData("NHICARD_NO", pat.getNhicardNo()); // ��������
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
 	 * 1.A 2.B 3.O 4.AB 5.���� 6.δ��
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
 	 * 1.�� 2.�� 3.���� 4.δ��
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
    		result = this.update("updateOpeSql2", parm, conn);//��ԭ����ע��
        	if(result.getErrCode()<0){
                conn.close();
                return result;
            }
        	result = this.update("updateOpeSql3", parm, conn);//ȡ��סԺ����
        	if(result.getErrCode()<0){
                conn.close();
                return result;
            }
    	}
    	//result = this.update("updateAdmInp", parm, conn);
    	String sql2 = "SELECT OPBOOK_SEQ FROM OPE_OPBOOK WHERE CASE_NO = '"+parm.getValue("CASE_NO")+"' AND ADM_TYPE = 'I' AND STATE = '1'";
		TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql2));
		if(parm3.getCount()>0){
			result = this.update("updateOpeSql", parm, conn);//����סԺ�����ų��ֶ�
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
