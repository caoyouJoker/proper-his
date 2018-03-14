package action.inf;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.StringTool;
import com.dongyang.action.TAction;
import com.javahis.ui.spc.util.StringUtils;

import jdo.inf.INFExamTool;
import jdo.inf.INFCaseTool;
import jdo.inf.INFSmsTool;
import jdo.med.MedSmsTool;
import jdo.sys.Operator;
import jdo.sys.SYSPublishBoardTool;
import jdo.sys.SystemTool;




import jdo.util.XmlUtil;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;


/**
 * <p>Title: ��Ⱦ��������Action</p>
 *
 * <p>Description: ��Ⱦ��������Action</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class InfAction extends TAction{

	
    /**
     * д���ؼ�¼��
     * @param parm TParm
     * @return TParm
     */
    public TParm insertINFExamRecord(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        for(int i = 0;i<parm.getCount("EXAM_NO");i++){
            result = INFExamTool.getInstance().insertINFDeptExamM(parm.getRow(i),
                                                                  connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���¼�ؼ�¼��
     * @param parm TParm
     * @return TParm
     */
    public TParm updateINFExamRecord(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        for(int i = 0;i<parm.getCount("EXAM_NO");i++){
            result = INFExamTool.getInstance().updateINFExamRecord(parm.getRow(i),
                                                                  connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ɾ����ؼ�¼��
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteINFExamRecord(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        for(int i = 0;i<parm.getCount("EXAM_NO");i++){
            result = INFExamTool.getInstance().deleteINFExamRecord(parm.getRow(i),
                                                                  connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ����пصǼ���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveInfCase(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TParm infCaseParm = parm.getParm("INF_CASE");
        TParm infReasonParm = parm.getParm("INF_REASON");
        TParm infAntibioTest = parm.getParm("INF_ANTIBIOTEST");
        TParm infICDPart = parm.getParm("INF_ICDPART");//add by wanglong 20140217
        TParm inIO = parm.getParm("INF_IO");
        System.out.println("sss:"+infICDPart);
        TConnection connection = getConnection();
        result = INFCaseTool.getInstance().updateMROINFDiag(infCaseParm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        if(infCaseParm.getValue("INF_NO").length() == 0){
        	//���������Ϣinf_case
            String infNo = INFCaseTool.getInstance().getInfNo();
            infCaseParm.setData("INF_NO",infNo);
            result = INFCaseTool.getInstance().insertInfCase(infCaseParm, connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            //�����Ӧ��Ⱦ�������INF_INFREASRCD
            for(int i = 0;i < infReasonParm.getCount("INFREASON_CODE");i++){
                TParm infReasonParmI = infReasonParm.getRow(i);
                infReasonParmI.setData("INF_NO",infNo);
                result = INFCaseTool.getInstance().insertInfInfreasrcd(infReasonParmI, connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
            //�����Ӧ���ʵ����INF_ANTIBIOTEST
            for(int i = 0;i < infAntibioTest.getCount("CULURE_CODE");i++){
              TParm infAntibioTestI = infAntibioTest.getRow(i);
              infAntibioTestI.setData("INF_NO",infNo);
              result = INFCaseTool.getInstance().insertInfantibiotest(infAntibioTestI, connection);
              if (result.getErrCode() < 0) {
                  connection.rollback();
                  connection.close();
                  return result;
              }
           }
            //�����Ӧ�ĸ�Ⱦ��λ   INF_ICDPART
            for (int i = 0; i < infICDPart.getCount("PART_CODE"); i++) {//add by wanglong 20140217
                TParm infICDPartI = infICDPart.getRow(i);
                infICDPartI.setData("INF_NO", infNo);
                result = INFCaseTool.getInstance().insertInfICDPart(infICDPartI, connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
            //�����Ӧ�������Բ���  INF_IO
            for (int i = 0; i < inIO.getCount("IO_CODE"); i++) {//add by wanglong 20140217
                TParm inIOI = inIO.getRow(i);
                inIOI.setData("INF_NO", infNo);
                result = INFCaseTool.getInstance().insertInfIO(inIOI, connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }

        }
        else{
            result = INFCaseTool.getInstance().updateInfCase(infCaseParm, connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            result = INFCaseTool.getInstance().deleteInfInfreasrcd(infCaseParm, connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            for(int i = 0;i < infReasonParm.getCount("INFREASON_CODE");i++){
                result = INFCaseTool.getInstance().insertInfInfreasrcd(infReasonParm.getRow(i), connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
            result = INFCaseTool.getInstance().deleteInfantibiotest(infCaseParm, connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            for(int i = 0;i < infAntibioTest.getCount("CULURE_CODE");i++){
                result = INFCaseTool.getInstance().insertInfantibiotest(infAntibioTest.getRow(i), connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
          }
            result = INFCaseTool.getInstance().deleteInfICDPart(infCaseParm, connection);//add by wanglong 20140217
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            for(int i = 0;i < infICDPart.getCount("PART_CODE");i++){
                result = INFCaseTool.getInstance().insertInfICDPart(infICDPart.getRow(i), connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
            result = INFCaseTool.getInstance().deleteInfIO(infCaseParm, connection);//add by wanglong 20140217
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            for(int i = 0;i < inIO.getCount("IO_CODE");i++){
                result = INFCaseTool.getInstance().insertInfIO(inIO.getRow(i), connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ����пؿ�������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveInfAntibiotrcd(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        if(parm.getCount("ORDER_CODE") <= 0)
            return result;
        TConnection connection = getConnection();
        //�������¿����ؼ�¼
        Map map = new HashMap();
        for(int i = 0;i < parm.getCount("ORDER_CODE");i++){
            String caseNo = parm.getValue("CASE_NO",i);
            if(map.get(caseNo) == null){
                //ȡ�����п����
                result = INFCaseTool.getInstance().selectMaxInfNo(parm.getRow(i));
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
                String infNo = "";
                if (result.getCount() > 0 &&
                    result.getData("INF_NO", 0) != null &&
                    result.getValue("INF_NO", 0).length() != 0)
                    infNo = result.getValue("INF_NO", 0);
                //ɾ��ԭ�п����ؼ�¼
                result = INFCaseTool.getInstance().deleteAntibiotrcd(parm.getRow(i), connection);
                if (result.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return result;
                }
                map.put(caseNo,infNo);
            }
            TParm parmI = parm.getRow(i);
            parmI.setData("INF_NO",map.get(caseNo));
            result = INFCaseTool.getInstance().insertAntibiotrcd(parmI, connection);
            if (result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���¸п��ϱ���Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm updateInfCaseReport(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        result = INFCaseTool.getInstance().updateInfCaseReport(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * ���¸пؼ�¼����
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteInfCase(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "��������Ϊ�գ�");
            return result;
        }
        TConnection connection = getConnection();
        result = INFCaseTool.getInstance().deleteInfInfreasrcd(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = INFCaseTool.getInstance().deleteInfantibiotest(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = INFCaseTool.getInstance().updateInfCaseCancelFlg(parm, connection);
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * add by wukai on 20170419
     * ���Ӵ�Ⱦ�� ���ŷ��͹���
     * ���������ݿ���
     * <p>bepl2.0����</p>
     * @param parm
     * @return
     */
    public TParm onSendInfMes(TParm parm){
    	TParm result = new TParm();
    	
    	if(parm == null) {
    		result.setErr(-1, "��������Ϊ��");
    		return result;
    	}
    	TConnection connection = getConnection();
    	
    	StringBuilder sendResult = new StringBuilder("");
    	sendResult.append("��ȾԤ�����ŷ��Ϳ�ʼ ===== START\n");
    	
    	for(int i = 0; i < parm.getCount("CASE_NO"); i++) {
    		TParm parmRow = parm.getRow(i);
    		
    		//1.���һ���Ƿ����±����ˣ�����CASE_NO��TESTITEM_CODE��LAB_NO�ж�
    		TParm msmParm = INFSmsTool.getInstance().selectAllData(parmRow);
    		if(msmParm != null && msmParm.getCount() > 0) {
    			sendResult.append("������:[ " + parmRow.getValue("MR_NO") + " ] �����ѷ��ͣ�\n");
    			continue;
    		}
    		
    		//2.��ѯ������ҽ���� ����ҽ���绰��ֵ��绰
    		TParm billDrParm = INFSmsTool.getInstance().getBillDr(parmRow.getValue("BILL_DR"));
    		
    		String admType = parmRow.getValue("ADM_TYPE");
    		TParm telParm = new TParm(); //����ҽ��(���͵ĺ���)
    		
    		String deptCode = "";
    		String stationCode = "";
    		if("E".equals(admType)) {
    			//��֢
    			telParm = MedSmsTool.getInstance().getRealdrCode(admType, parmRow.getValue("CASE_NO"));
    			deptCode = telParm.getValue("DEPT_CODE", 0);
    			
    			parmRow.setData("VS_DOC_CODE", telParm.getValue("REALDR_CODE", 0));
    			
    		} else if("I".equals(admType)) {
    			//סԺ
    			telParm = MedSmsTool.getInstance().getVsDrCode(parmRow.getValue("CASE_NO"));
    			deptCode = telParm.getValue("DEPT_CODE", 0);
    			stationCode = telParm.getValue("STATION_CODE", 0);
    			
    			parmRow.setData("VS_DOC_CODE", telParm.getValue("VS_DR_CODE", 0));
    			//����ҽ����סԺҽ����һ��ʱ���ӿ���ҽ������֪ͨ
    			if (!StringUtils.equals(telParm.getValue("VS_DR_CODE", 0), billDrParm.getValue("USER_ID", 0))) {
    				if(!StringUtils.isEmpty(billDrParm.getValue("TEL1", 0)))
    					telParm.addData("TEL1", billDrParm.getValue("TEL1", 0));
				}
    			
    		} else if("O".equals(admType)) {
    			//����
    			TParm telParmBill = MedSmsTool.getInstance().getRealdrCode(admType, parmRow.getValue("CASE_NO"));
				deptCode = telParmBill.getValue("DEPT_CODE", 0);
				parmRow.setData("VS_DOC_CODE", telParmBill.getValue("REALDR_CODE", 0));
				
				String sql = " SELECT DUTY_TEL AS TEL1 FROM SYS_DEPT WHERE DEPT_CODE='020101'";
				TParm telParmDuty = new TParm(TJDODBTool.getInstance().select(sql));
				telParm.addData("TEL1", telParmBill.getValue("TEL1"));
				if (null != telParmDuty.getValue("TEL1") && telParmDuty.getValue("TEL1").length() > 0)
					telParm.addData("TEL1", telParmDuty.getValue("TEL1"));
    			
    		}
    		
			if (deptCode != null && !deptCode.equals("")) {
				TParm searchParm = new TParm();
				searchParm.setData("DEPT_CODE", deptCode);
				TParm dutyParm = MedSmsTool.getInstance().getDutyTel(searchParm);
				if (dutyParm.getCount() > 0) {
					if ("I".equals(admType) || "E".equals(admType)) {
						telParm.addData("TEL1", dutyParm.getValue("DUTY_TEL", 0));
					}
					// ������������
					parmRow.setData("DEPT_CHN_DESC", dutyParm.getValue("DEPT_CHN_DESC", 0));
				}
			}
    		
    		//3.parmRow��Ϣ���� ����ҽ�������ң����������͵���Ϣ������ʱ�䣬�����ˣ��������ݣ��������ݣ�����ID����Ⱦ������
    		parmRow.setData("BILL_DOC_CODE", billDrParm.getValue("USER_ID", 0));
    		parmRow.setData("DEPT_CODE", deptCode);
    		parmRow.setData("STATION_CODE", stationCode);
    		
    		Timestamp time = SystemTool.getInstance().getDate();
    		parmRow.setData("OPT_USER", "belpInf");
    		parmRow.setData("OPT_TERM", "127.0.0.1");
    		parmRow.setData("OPT_DATE", time);
    		
    		String content = parmRow.getValue("PAT_NAME") + "(������:" + parmRow.getValue("MR_NO") + ")"
    						+ "�� [ " + parmRow.getValue("TESTITEM_CHN_DESC") + " ] �����Ϊ [ " + parmRow.getValue("TEST_VALUE") + " ]"
    						+ "����ע�⣡";
    		
    		parmRow.setData("SEND_USER", "belpInf");
    		parmRow.setData("SEND_INFO", content);
    		parmRow.setData("SEND_DATE", time);
    		parmRow.setData("INF_DIRECTOR", TConfig.getSystemValue("INF_DIRECTOR"));
    		
    		parmRow.setData("STATE", "1");    //֪ͨҽ��
    		parmRow.setData("MES_TYPE", "11"); //��ȾԤ������
    		
    		String messageNo = SystemTool.getInstance().getNo("ALL", "PUB", "SMS_CODE", "SMS_CODE");
    		parmRow.setData("MESSAGE_NO", messageNo);
    		
    		if(!StringUtils.isEmpty(TConfig.getSystemValue("INF_DIRECTORNUM"))) {
    			telParm.addData("TEL1", TConfig.getSystemValue("INF_DIRECTORNUM"));
    		}
    		
    		//4.��INF_MES ����һ����¼
    		result = INFSmsTool.getInstance().insertInfWarnData(parmRow, connection);
    		if(result == null || result.getErrCode() < 0) {
    			sendResult.append("������:[ " + parmRow.getValue("MR_NO") + " ] ��¼����ʧ�ܣ�\n");
    			continue;
    		}
    		
    		//5.���Ͷ���
    		Timestamp reportTs = StringTool.getTimestamp(parmRow.getValue("REPORT_TIME"), "yyyyMMddHHmmss");
			String reportDate = StringTool.getString(reportTs, "yyyy��MM��dd�� HHʱmm��");
			parmRow.setData("REPORT_DATE", reportDate);
    		
			writeXml(parmRow, telParm, content);
			
			
    	}
    	
    	sendResult.append("��ȾԤ�����ŷ��ͽ��� ===== END\n");
    	
    	TIOM_FileServer.writeFile("C:/JavaHis/logs/��Ⱦ��Ԥ�����ŷ��ͽ��" + StringTool.getTimestamp(new Date()).toString(), sendResult.toString().getBytes());
    	
    	connection.commit();
		connection.close();
    	return result;
    }
    
    private void writeXml(TParm parmRow, TParm telParm, String content) {
		// д�ļ�
		TParm xmlParm = new TParm();
		xmlParm.setData("Content", content);
		xmlParm.setData("MrNo", telParm.getValue("MR_NO").replace("[", "")
				.replace("]", ""));
		// �õ�����,�ż�ס���
		String deptChnCode = parmRow.getValue("DEPT_CHN_DESC");
		String admType = parmRow.getValue("ADM_TYPE");
		String admTypeChn = "";
		admTypeChn = getAdmType(admType);

		xmlParm.setData("Name", parmRow.getValue("PAT_NAME") + "," + deptChnCode + "," + admTypeChn);
		xmlParm.setData("SysNo", "INF");
		//����ʱ��
		xmlParm.setData("ReportDate", parmRow.getValue("REPORT_DATE"));
		xmlParm.setData("Title",parmRow.getValue("TITLE"));//machao
		XmlUtil.createSmsFile(xmlParm, telParm);
	}
    
    
    private String getAdmType(String admType) {
		String admTypeChn = "";
		if (admType != null) {
			if (admType.equals("O")) {
				admTypeChn = "����";
			} else if (admType.equals("I")) {
				admTypeChn = "סԺ";
			} else if (admType.equals("E")) {
				admTypeChn = "����";
			} else if (admType.equals("H")) {
				admTypeChn = "�������";
			}
		}
		return admTypeChn;
	}
    
    /**
     * add by lij on 20170425
     * ˫��ͨ ���ŷ��͹���
     * ���������ݿ���
     * @param parm
     * @return
     */
    public TParm onComSave(TParm parmRow) {
		TParm result = new TParm();

		if(parmRow == null) {
			result.setErr(-1, "��������Ϊ��");
			return result;
		}
		TConnection connection = getConnection();

		StringBuilder sendResult = new StringBuilder("");
		sendResult.append("��ͨ���ŷ��Ϳ�ʼ ===== START\n");
		//����ҽ��(���͵ĺ���)
		TParm telParm = new TParm(); 
		String content = "";
		if("HANDLE".equals(parmRow.getValue("CLASS_NAME"))) {
					telParm = MedSmsTool.getInstance().getVsDrCode(parmRow.getValue("CASE_NO"));
//					System.out.println("telParm:"+telParm);
			content = parmRow.getValue("PAT_NAME")
					+ "�� (������:" + parmRow.getValue("MR_NO") + ")"
					+ "�� ��Ⱦ����Ϣ����Ϊ : " + parmRow.getValue("HANDLE_INFO") + " "
//						+ "�� �ٴ���Ϣ����Ϊ [ " + parmRow.getValue("SEND_INFO") + " ]"
					+ "���뵽סԺҽ��վ�鿴��";
		}else if("SEND".equals(parmRow.getValue("CLASS_NAME"))){
			content = parmRow.getValue("PAT_NAME")
					+ "�� (������:" + parmRow.getValue("MR_NO") + ")"
//					+ "�� ��Ⱦ����Ϣ����Ϊ [ " + parmRow.getValue("HANDLE_INFO") + " ]"
					+ "�� �ٴ���Ϣ����Ϊ : " + parmRow.getValue("SEND_INFO") + " "
					+ "���뵽��Ⱦ�����Ǽǲ鿴��";
			//��ѯ��Ⱦ����Ա�绰
//			if(!"".equals(parmRow.getValue("SEND_USER"))){
//				telParm = INFSmsTool.getInstance().getSendUserCode(parmRow.getValue("CASE_NO"));
//			} else {
//				if(!StringUtils.isEmpty(TConfig.getSystemValue("INF_DIRECTORNUM"))) {
					telParm.setData("TEL1", TConfig.getSystemValue("INF_DIRECTORNUM"));
					telParm.setData("MR_NO", parmRow.getValue("MR_NO"));
//					System.out.println("TEL1:"+telParm);
//				}
//			}
		}
		
		//��INF_MES ����һ����¼
		result = INFSmsTool.getInstance().insertInfSms(parmRow, connection);
		if(result == null || result.getErrCode() < 0) {
			sendResult.append("������:[ " + parmRow.getValue("MR_NO") + " ] ��¼����ʧ�ܣ�\n");
			return result;
		}
		

		//���Ͷ���
		if("Y".equals(parmRow.getValue("MESS_FLG"))) {
			Timestamp sendTs = StringTool.getTimestamp(parmRow.getValue("SEND_DATE"), "yyyyMMddHHmmss");
			String sendDate = StringTool.getString(sendTs, "yyyy��MM��dd�� HHʱmm��");
			parmRow.setData("SEND_DATE", sendDate);
			parmRow.setData("TITLE", "��ɸ֪ͨ");//machao  ��Ⱦ������ű��
			writeXml(parmRow, telParm, content);
		
		}
		
		sendResult.append("��ͨ���ŷ��ͽ��� ===== END\n");

		TIOM_FileServer.writeFile("C:/JavaHis/logs/��ͨ���ŷ��ͽ��" + StringTool.getTimestamp(new Date()).toString(), sendResult.toString().getBytes());

		connection.commit();
		connection.close();
		return result;
    }
    
    /**
     * add by lij on 20170502
     * ���͹�����
     * @param parm TParm
     * @return TParm
     */
    public TParm onBoardMessage(TParm parm) {
    	TParm result = new TParm();
        TParm resultMessage = sendBoard(parm);
       
        String message = resultMessage.getValue("MESSAGE");
    	TConnection conn = getConnection();
    	TParm inparm = new TParm();
    	inparm.setData("MESSAGE_NO", getMessageNo());
    	inparm.setData("POST_TYPE", "H");
    	inparm.setData("POST_GROUP", resultMessage.getValue("POST_GROUP"));
    	inparm.setData("USER_ID", resultMessage.getValue("USER_ID"));
    	inparm.setData("READ_FLG", "N");
    	inparm.setData("OPT_USER", parm.getValue("OPT_USER"));
     	inparm.setData("OPT_DATE", SystemTool.getInstance().getDate());
      	inparm.setData("OPT_TERM", parm.getValue("OPT_TERM"));
      	
      	inparm.setData("POST_SUBJECT", resultMessage.getValue("POST_SUBJECT"));
      	inparm.setData("URG_FLG", resultMessage.getValue("URG_FLG"));
      	inparm.setData("POST_INFOMATION", resultMessage.getValue("POST_INFOMATION"));
      	inparm.setData("RESPONSE_NO", 0);
      	inparm.setData("POST_ID", parm.getValue("OPT_USER"));
      	inparm.setData("POST_TIME", SystemTool.getInstance().getDate());
      	inparm.setData("CASE_NO", resultMessage.getValue("CASE_NO"));
      	inparm.setData("BOARD_STATUS", "Y");
      	result = SYSPublishBoardTool.getInstance().insertPostRCV(inparm, conn);
      	if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
          conn.close();
          return result;
      	}
      	result = SYSPublishBoardTool.getInstance().insertBoard(inparm, conn);
      	if (result.getErrCode() < 0 || !"".equals(result.getErrText())) {
          conn.close();
          return result;
      	}
        conn.commit();
        conn.close();
        result.setData("MESSAGE", message);
        return result;
    			
    } 
    
    /**
     * add by lij on 20170502
     * ���͹�������Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm sendBoard(TParm parm) {
        String message = "";
        String title = "";
        boolean flg = true;
        TParm result = new TParm();
        String content = "";
        if("HANDLE".equals(parm.getValue("CLASS_NAME"))){
        	title = "��Ⱦ�Ʒ���һ����Ϣ";
        	content = parm.getValue("PAT_NAME")
    				+ "�� ������:" + parm.getValue("MR_NO") + ""
    				+ "�� ��Ⱦ����Ϣ����Ϊ : " + parm.getValue("HANDLE_INFO") + " "
    				+ "���뵽סԺҽ��վ�����ͨ��ť�鿴��";
        }else if ("SEND".equals(parm.getValue("CLASS_NAME"))){
        	title = "�ٴ�ҽ��:" + parm.getValue("USER_NAME",0) + " ����һ����Ϣ";
        	content = parm.getValue("PAT_NAME")
					+ "�� ������:" + parm.getValue("MR_NO") + ""
					+ "�� �ٴ���Ϣ����Ϊ : " + parm.getValue("SEND_INFO") + " "
					+ "���뵽��Ⱦ�����Ǽǵ����ͨ��ť�鿴��";
        }
        
        result.addData("CASE_NO", parm.getValue("CASE_NO"));
        result.setData("USER_ID", parm.getValue("USER_ID"));
        result.setData("POST_GROUP", parm.getValue("ROLE_ID"));
        result.setData("POST_SUBJECT", title);
        result.setData("POST_INFOMATION", content);
        result.setData("RECIPIENTS", parm.getValue("VS_DR_CODE"));
        result.setData("URG_FLG", parm.getValue("URG_FLG"));
            message += (flg ? "��������Ϣ���ͳɹ�" : "��������Ϣ����ʧ��") + "\n";
//        }
        result.setData("MESSAGE", message);
        return result;
    }
    /**
     * add by lij on 20170502
     * ȡ����ˮ��
     * @return String
     */
    private synchronized String getMessageNo() {
        String messageNo = "";
        messageNo = SystemTool.getInstance().getNo("ALL", "PUB", "MESSAGE_NO", "MESSAGE_NO");
        return messageNo;
    }
}
