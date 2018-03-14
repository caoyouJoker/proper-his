package action.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

//import org.apache.ws.xnh.XNHService;

import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSIpdHistoryTool;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;

/**
 *
 * <p>
 * Title:��ũ����������
 * </p>
 * Description:��ũ����������
 * Copyright: Copyright (c) 2017
 * </p>
 * @version 2.0
 */
public class XNHINSBalanceAction extends TAction {
	 private int count = 1; // ��� INS_IBS_ORDER �ۼƸ���
	 // У�����INS_IBS_ORDER�����Ƿ�Ϊ��
    private String[] nameIbsOrder = {"ADM_SEQ", "INSBRANCH_CODE",
                                    "HOSP_NHI_NO", "ORDER_CODE",
                                    "NHI_ORDER_CODE", "ORDER_DESC",
                                    "OWN_RATE", "DOSE_CODE", "STANDARD",
                                    "OP_FLG", "ADDPAY_FLG",
                                    "NHI_ORD_CLASS_CODE", "PHAADD_FLG",
                                    "CARRY_FLG","EXE_DEPT_CODE","DOSAGE_UNIT"};
    // INS_XNH_UPLOAD���޸Ľ�������ʱ����У��Ϊ��
    private String[] nameIbsUpLoadAdvance = {"ORDER_CODE", "ORDER_DESC",
                                        "NHI_ORDER_CODE", 
                                        "NHI_ORD_CLASS_CODE"};
    
    /**
     * ת��ϸ
     * @param tempParm TParm
     * @return TParm
     */
    public TParm onExeXnh(TParm tempParm) {
        // ɾ������
        TParm parm = new TParm();
        parm.setData("CASE_NO", tempParm.getValue("CASE_NO")); // �����
        parm.setData("YEAR_MON", tempParm.getValue("YEAR_MON")); // �ں�
        parm.setData("START_DATE", tempParm.getValue("START_DATE")); // ��ʼ
        parm.setData("END_DATE", tempParm.getValue("END_DATE")); // ����ʱ��
        TParm result = null;
        result = INSIbsOrderTool.getInstance().deleteINSIbsOrder(parm);       
        if (result.getErrCode() < 0) {
            return result;
        }
        TParm tParm = null; // ��IBS_OrdD��ȡ���� ��ȡסԺ��������
        TParm ibsOrddParm = null; // ��ѯҽ������
        TParm orderParm = new TParm(); // ��ѯҽ������ ִ�к� �������������      
        TParm confirmTempParm = new TParm();       
        confirmTempParm.setData("INSBRANCH_CODE","07"); // ������
        confirmTempParm.setData("NHIHOSP_NO",tempParm.getValue("NHIHOSP_NO"));// ҽ���������
        confirmTempParm.setData("ADM_SEQ",""); // ����˳��� 
        confirmTempParm.setData("CONFIRM_NO","");
        confirmTempParm.setData("CASE_NO",tempParm.getValue("CASE_NO"));
            // ��IBS_OrdD��ȡ���� ��ȡסԺ��������
            tParm = INSIbsTool.getInstance().queryIbsOrdd(parm);
            if (tParm.getErrCode() < 0) {
                return tParm;
            }
           
            count = 1; // �����ۼ����� ��� INS_IBS_ORDER
            for (int j = 0; j < tParm.getCount(); j++) {
                ibsOrddParm = tParm.getRow(j);
                if(!getInsertIbsOrder(tempParm, confirmTempParm, ibsOrddParm, orderParm)){
                	TParm errResult=new TParm();
                	errResult.setErr(-1,ibsOrddParm.getValue("ORDER_CODE")+"  "+
                			            ibsOrddParm.getValue("ORDER_DESC")+"ҽ����������");
                	return errResult;
                }
            }
            orderParm.setCount(tParm.getCount());
//           System.out.println("���INS_IBS_ORDER ����"+orderParm);
            // ���INS_IBS_ORDER ����
            for (int j = 0; j < orderParm.getCount(); j++) {
                TParm ibsOrder = orderParm.getRow(j);
//                System.out.println("ibsOrder::::"+ibsOrder);
                // У��Ϊ��
                for (int k = 0; k < nameIbsOrder.length; k++) {
                    if (null == ibsOrder.getValue(nameIbsOrder[k]) ||
                        ibsOrder.getValue(nameIbsOrder[k]).equals("null")
                        || ibsOrder.getValue(nameIbsOrder[k]).equals("")) {
                        ibsOrder.setData(nameIbsOrder[k], "");
                    }
                }
                result = INSIbsOrderTool.getInstance().insertINSIbsOrder(
                        ibsOrder);
                if (result.getErrCode() < 0) {               
                    break;
                }
            }
            // ��ϸ�ϴ�����
            TParm insIbsUnionParm = INSIbsOrderTool.getInstance()
                                    .queryInsIbsDUnion(parm);
            if (insIbsUnionParm.getErrCode() < 0) {
                return result;
            }
//            System.out.println("��ϸ�ϴ�����insIbsUnionParm:::"+insIbsUnionParm);
            // ��� INS_XNH_UPLOAD �����
            result = onApplyInsertXnhUpLoad(
            		confirmTempParm, insIbsUnionParm,tempParm);
            if (result.getErrCode() < 0) {
            
                return result;
            }
        return result;
    }  
    /**
     * ��� INS_XNH_UPLOAD
     * @return TParm
     */
    private TParm onApplyInsertXnhUpLoad(
    		TParm confirmTempParm,TParm insIbsUnionParm,TParm sysParm) {

        TParm result = new TParm();
        String listcode = "";
        String listdesc = "";
        // ɾ��������
        if (insIbsUnionParm.getCount() > 0) {
        	String sql =" DELETE FROM INS_XNH_UPLOAD"+ 
            " WHERE CASE_NO ='"+ confirmTempParm.getValue("CASE_NO") + "'";
        	result = new TParm(TJDODBTool.getInstance().update(sql));
        }
        if (result.getErrCode() < 0) {
            return result;
        }
        // ִ����Ӳ���  
        for (int j = 0; j < insIbsUnionParm.getCount(); j++) {
        	 TParm data = new TParm();
            TParm tempParm = insIbsUnionParm.getRow(j);           
            int seqNo = j + 1;           
            String orderNo = confirmTempParm.getValue("CASE_NO")+ seqNo;
//        	System.out.println("orderNo============"+orderNo);
            data.setData("ORDER_NO", orderNo);//סԺ������ˮ��
            data.setData("SEQ_NO", seqNo);//���
            //�����ũ�Ϸ������
            String sql = " SELECT HEXP_CODE FROM IBS_ORDD"+
                         " WHERE CASE_NO = '"+ confirmTempParm.getValue("CASE_NO") + "'"+
                         " AND ORDER_CODE = '"+ tempParm.getValue("ORDER_CODE") + "'";
            TParm datahexp  = new TParm(TJDODBTool.getInstance().select(sql));
//            System.out.println("datahexp============"+datahexp);
            String hexpCode = datahexp.getValue("HEXP_CODE", 0);
//            System.out.println("hexpCode============"+hexpCode);
            String sql1 = " SELECT XNH_CHARGE_CODE FROM SYS_CHARGE_HOSP"+
                          " WHERE CHARGE_HOSP_CODE = '"+ hexpCode + "'";
//            System.out.println("sql1============"+sql1);
            TParm xnhParm  = new TParm(TJDODBTool.getInstance().select(sql1)); 
//            System.out.println("xnhParm============"+xnhParm);
            String xnhCode = xnhParm.getValue("XNH_CHARGE_CODE", 0);      
            String sql2 = " SELECT ID,CHN_DESC FROM SYS_DICTIONARY "+
                          " WHERE GROUP_ID = 'XNH_CHARGE'"+
                          " AND ID = '"+ xnhCode + "'";
            TParm classParm  = new TParm(TJDODBTool.getInstance().select(sql2));
//            System.out.println("classParm============"+classParm);
            data.setData("CLASS_CODE", classParm.getValue("ID",0));//����������           
            data.setData("CLASS_DESC", classParm.getValue("CHN_DESC",0));//�����������
            data.setData("NHI_ORDER_CODE", tempParm.getValue("NHI_ORDER_CODE"));//��Ŀҽ������
            data.setData("ORDER_CODE", tempParm.getValue("ORDER_CODE"));//HISϵͳ��Ŀ����
            data.setData("ORDER_DESC", tempParm.getValue("ORDER_DESC"));//HISϵͳ��Ŀ����
            data.setData("DOSE_DESC", tempParm.getValue("DOSE_DESC"));//����
            data.setData("STANDARD", tempParm.getValue("STANDARD"));//���
            data.setData("UNIN_DESC", "");//��λ
            data.setData("PRICE", tempParm.getDouble("PRICE"));//����
            data.setData("TOT_AMT", tempParm.getDouble("TOTAL_AMT"));//�ܽ��
            data.setData("DR_DESC", "");//ҽ������             
            data.setData("CHARGE_DATE", SystemTool.getInstance()
                             .getDateReplace(tempParm.getValue("CHARGE_DATE"), true)); // ��������
            data.setData("PAY_QTY", 0);//����
            data.setData("QTY", tempParm.getInt("QTY"));//����
            data.setData("XNH_ORDER_CODE", tempParm.getValue("NHI_ORDER_CODE"));//ũ����Ŀ����    
            data.setData("XNH_ORDER_DESC", tempParm.getValue("ORDER_DESC"));//ũ����Ŀ����     
            
            String sql3 = " SELECT INSPAY_TYPE FROM SYS_FEE"+
                         " WHERE ORDER_CODE =  '" + tempParm.getValue("ORDER_CODE") + "'";
            TParm Parm = new TParm(TJDODBTool.getInstance().select(sql3));           
            if(Parm.getValue("INSPAY_TYPE",0).equals("A")||
               Parm.getValue("INSPAY_TYPE",0).equals("B")){
            data.setData("INS_AMT", tempParm.getDouble("TOTAL_AMT"));//�ɱ������ 
            listcode = "1";
            listdesc = "��ҽ��Ŀ¼��";
            }else{
            data.setData("INS_AMT", 0);//�ɱ������ 
            listcode = "2";
            listdesc = "��ҽ��Ŀ¼��";
            }
           // CREATE_DATE ��������(��ǰʱ��)
           // UPDATE_DATE ��������(��ǰʱ��)
            data.setData("CASE_NO", confirmTempParm.getValue("CASE_NO"));//סԺ�Ǽ���ˮ��
            //data.setData("HOSP_CODE", XNHService.HOSPCODE);//��ҽ��������
            //data.setData("HOSP_DESC", XNHService.HOSPNAME);//��ҽ��������
            data.setData("IMPORT_FLG_CODE", "");//�������ڱ�ʶ����
            data.setData("IMPORT_FLG_DESC", "");//�������ڱ�ʶ����
            data.setData("DEDUCTION_AMT", 0);//�ۼ����
            data.setData("DEDUCTION_REASON", "");//�ۼ�ԭ��
            data.setData("LIST_CODE", listcode);//Ŀ¼����
            data.setData("LIST_DESC", listdesc);//Ŀ¼��������
            data.setData("BUY_SUBJECT_CODE", "");//���вɹ���Ŀ����                     
//        	System.out.println("data============"+data);           
         // ��� INS_XNH_UPLOAD ����� 
   		 String sql4= " INSERT INTO INS_XNH_UPLOAD("+
   		 " ORDER_NO,SEQ_NO,CLASS_CODE,CLASS_DESC," +
   		 " NHI_ORDER_CODE,ORDER_CODE,ORDER_DESC," +
   		 " DOSE_DESC,STANDARD,UNIN_DESC,PRICE,TOT_AMT,"+
   		 " DR_DESC,CHARGE_DATE,PAY_QTY,QTY,XNH_ORDER_CODE,"+
   		 " XNH_ORDER_DESC,INS_AMT,CREATE_DATE,UPDATE_DATE," +
   		 " CASE_NO,HOSP_CODE,HOSP_DESC,IMPORT_FLG_CODE," +
   		 " IMPORT_FLG_DESC,DEDUCTION_AMT,DEDUCTION_REASON," +
   		 " LIST_CODE,LIST_DESC,BUY_SUBJECT_CODE," +
   		 " OPT_USER,OPT_DATE,OPT_TERM) " +
   		 " VALUES('"+ data.getValue("ORDER_NO")+ "'," +
   		 " "+ data.getValue("SEQ_NO")+ "," +
   		 " '"+ data.getValue("CLASS_CODE")+ "'," +
   		 " '"+ data.getValue("CLASS_DESC")+ "'," +
   		 " '"+ data.getValue("NHI_ORDER_CODE")+ "'," +
   		 " '"+ data.getValue("ORDER_CODE")+ "'," +
   		 " '"+ data.getValue("ORDER_DESC")+ "'," +
   		 " '"+ data.getValue("DOSE_DESC")+ "'," +
   		 " '"+ data.getValue("STANDARD")+ "'," +
   		 " '"+ data.getValue("UNIN_DESC")+ "'," +
   		 " "+ data.getDouble("PRICE")+ "," +
   		 " "+ data.getDouble("TOT_AMT")+ "," + 
   		 " '"+ data.getValue("DR_DESC")+ "'," +
   		 " to_date('"+ data.getValue("CHARGE_DATE")+"','yyyyMMddHH24MISS')," +  		
   		 " "+ data.getInt("PAY_QTY")+ "," +
   		 " "+ data.getInt("QTY")+ "," +
   		 " '"+ data.getValue("XNH_ORDER_CODE")+ "'," +
   		 " '"+ data.getValue("XNH_ORDER_DESC")+ "'," +
   		 " "+ data.getDouble("INS_AMT")+ ",SYSDATE,SYSDATE," + 
   		 " '"+ data.getValue("CASE_NO")+ "'," +
   		 " '"+ data.getValue("HOSP_CODE")+ "'," +
   		 " '"+ data.getValue("HOSP_DESC")+ "'," +
   		 " '"+ data.getValue("IMPORT_FLG_CODE")+ "'," +
   		 " '"+ data.getValue("IMPORT_FLG_DESC")+ "'," +
   		 " "+ data.getDouble("DEDUCTION_AMT")+ "," + 
   		 " '"+ data.getValue("DEDUCTION_REASON")+ "'," +
   		 " '"+ data.getValue("LIST_CODE")+ "'," +
   		 " '"+ data.getValue("LIST_DESC")+ "'," +
   		 " '"+ data.getValue("BUY_SUBJECT_CODE")+ "'," +   		    		
   		 " '"+ sysParm.getValue("OPT_USER")+ "',SYSDATE," +
   		 " '"+ sysParm.getValue("OPT_TERM")+ "')";
//   		 System.out.println("sql:=============="+sql);
   		result = new TParm(TJDODBTool.getInstance().update(sql4));  
        if (result.getErrCode() < 0) {
               return result;
            }
        }
        return result;
    }
    /**
     * ��������
     * @param tempParm
     * @param confirmTempParm
     * @param ibsOrddParm
     * @param orderParm
     * @return
     */
    private boolean getInsertIbsOrder(TParm tempParm,TParm confirmTempParm,TParm ibsOrddParm,TParm orderParm){
		if (null != tempParm.getValue("REGION_CODE")
				&& tempParm.getValue("REGION_CODE").length() > 0) {
			ibsOrddParm
					.setData("REGION_CODE", tempParm.getValue("REGION_CODE"));
		}
		TParm insParm=INSIbsTool.getInstance().queryInsIbsOrderByInsRule(ibsOrddParm);
        if (insParm.getErrCode()<0 || insParm.getCount()!=1) {
        	return false;
		}
		// ������������
		getOrderParm(confirmTempParm, ibsOrddParm,
				tempParm, orderParm,insParm, 2);
		return true;
    }
    /**
     * ������������ ͳ�ƽ��ʹ��
     * @param confirmTempParm TParm
     * @param ibsParm TParm
     * @param sysFeeParm TParm
     * @param tempParm TParm
     * @param orderParm TParm
     * @param type int int type 1:ת������Ϣ��ѯ 2��ת�걨���� INS_IBS_ORDER ����
     * @return TParm
     */
    private TParm getOrderParm(TParm confirmTempParm, TParm ibsParm, TParm tempParm,
                               TParm orderParm,TParm insParm, int type) {

        if (type == 2) {
            orderParm.addData("YEAR_MON", tempParm.getValue("YEAR_MON")); // �ں�
            orderParm.addData("CASE_NO", tempParm.getValue("CASE_NO")); // �����
            orderParm.addData("INSBRANCH_CODE", confirmTempParm
                              .getValue("INSBRANCH_CODE")); // ������
            orderParm.addData("BILL_DATE", SystemTool.getInstance()
                              .getDateReplace(ibsParm.getValue("BILL_DATE1"), true)); // ��ϸ������ʱ��
            orderParm.addData("HOSP_NHI_NO", confirmTempParm
                              .getValue("NHIHOSP_NO")); // ҽ���������
            orderParm.addData("DOSE_DESC", ibsParm.getValue("DOSE_DESC")); // ��������
            orderParm.addData("OPT_USER", tempParm.getValue("OPT_USER")); // ID
            orderParm.addData("OPT_TERM", tempParm.getValue("OPT_TERM")); // IP

            orderParm.addData("SEQ_NO", count); // ˳���
            orderParm.addData("REGION_CODE", tempParm.getValue("REGION_CODE")); // �������
            orderParm.addData("ADM_SEQ", confirmTempParm.getValue("ADM_SEQ")); // ����˳���
            orderParm.addData("PRICE", ibsParm.getDouble("OWN_PRICE")); // ����
            orderParm.addData("QTY", ibsParm.getDouble("DOSAGE_QTY")); // ����
            orderParm.addData("ADDPAY_AMT", ibsParm.getDouble("ADDPAY_AMT")); // �ۼƽ��
            // ҽ����� =ҽ������*����
            orderParm.addData("TOTAL_NHI_AMT", 0.00);
            orderParm.addData("ADDPAY_FLG", "N"); // �ۼ�����ע��ACCRUAL_FLG?????? ԭ����
            // ��ѯ IBS_ORDD
            orderParm.addData("PHAADD_FLG", "N"); // ����ҩƷע��??????? ԭ���� ��ѯ
            //======pangben 2012-6-11 start �޸ĳ�Ժ��ҩ����
            orderParm.addData("CARRY_FLG", null==ibsParm.getValue("DS_FLG")||ibsParm.getValue("DS_FLG").trim().length()<=0||
            		ibsParm.getValue("DS_FLG").trim().equals("N")?"N":"Y"); // ��Ժ��ҩע�� 
            // IBS_ORDD
            // ԭ����
            // ��ѯ
            // IBS_ORDM
            orderParm.addData("ORDER_CODE", ibsParm.getValue("ORDER_CODE")); // ҽ������
            //�޸�:ҽ������ �Ժ���ҽ������֮ǰ�Ѿ��շѵ�ҽ��ҽ����ʹ�þɵ�,�¿������շ�ҽ��ʹ���µ�
            //======pangben 2012-9-7
            //�жϴ��շ�ʱ���Ƿ���ҽ���ֵ����ݿ�ʼʱ��֮ǰ
			orderParm.addData("NHI_ORDER_CODE", ibsParm
                    .getValue("INS_CODE")); // ҽ��ҽ������
            orderParm.addData("OP_FLG",
            		insParm.getValue("TJDM",0).equals("04") ? "Y" : "N"); // ��������ע��
            orderParm.addData("OWN_RATE", insParm.getDouble("ZFBL1",0)); // �Ը�����
            orderParm.addData("DOSAGE_UNIT", ibsParm.getValue("DOSAGE_UNIT"));//��ҩ��λ
            orderParm.addData("EXE_DEPT_CODE", ibsParm.getValue("EXE_DEPT_CODE"));//ִ�п���
            orderParm.addData("HYGIENE_TRADE_CODE", insParm.getValue("PZWH",0)); // ��׼�ĺ�
            orderParm.addData("ORDER_DESC", ibsParm.getValue("ORDER_DESC")); // ����
            orderParm.addData("STANDARD", null!=ibsParm.getValue("SPECIFICATION") && ibsParm.getValue("SPECIFICATION").length()>=20?//======pangben 20120801 �޸ı��泤��
            		ibsParm.getValue("SPECIFICATION").substring(0,20):ibsParm.getValue("SPECIFICATION")); // ��� 
            orderParm.addData("DOSE_CODE", ibsParm.getValue("DOSE_CODE")); // ����
            count++;
        }
        orderParm.addData("TOTAL_AMT", ibsParm.getDouble("TOT_AMT")); // �������
        orderParm.addData("OWN_AMT", ibsParm.getDouble("OWN_AMT")); // �Էѽ��
        orderParm.addData("NHI_ORD_CLASS_CODE", insParm.getValue("TJDM",0)); // ͳ�ƴ���
        // vRow.add(20, String.valueOf(vIBSOrdMD.get(18)).trim()); //��׼�ĺ�
        return orderParm;
    }
    /**
     * �޸ı�����û��ܺ���ϸ����
     * @param parm TParm
     * @return TParm
     */
    public TParm updateXnhUpLoad(TParm parm) {
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount(); i++) {
            TParm temp = parm.getRow(i);
            if (null != temp.getValue("FLG")
                && temp.getValue("FLG").length() > 0
                && temp.getValue("SEQ_NO").length() > 0) { // ��ȥ�ϼ�����
                temp.setData("OPT_USER", parm.getValue("OPT_USER"));
                temp.setData("OPT_TERM", parm.getValue("OPT_TERM"));
                temp.setData("REGION_CODE", parm.getValue("REGION_CODE"));
                temp.setData("CHARGE_DATE", SystemTool.getInstance()
                             .getDateReplace(temp.getValue("CHARGE_DATE"), true)); // ��ϸ������
                //У���Ƿ�Ϊ��ֵ
                for (int j = 0; j < nameIbsUpLoadAdvance.length; j++) {
                    if (null == temp.getValue(nameIbsUpLoadAdvance[j]) ||
                        temp.getValue(nameIbsUpLoadAdvance[j]).equals("null")
                        || temp.getValue(nameIbsUpLoadAdvance[j]).equals("")) {
                        temp.setData(nameIbsUpLoadAdvance[j], "");
                    }
                }
                if (temp.getBoolean("FLG")) { // ��Ӳ���
                	String orderNo = temp.getValue("CASE_NO")+temp.getValue("SEQ_NO");
                	 String sql= " INSERT INTO INS_XNH_UPLOAD("+
               		 " ORDER_NO,CASE_NO,SEQ_NO,CHARGE_DATE,ORDER_CODE,"+ 
               		 " ORDER_DESC,NHI_ORDER_CODE,PRICE,QTY,"+
               		 " TOT_AMT,CLASS_CODE,PAY_QTY,INS_AMT,DEDUCTION_AMT," +
               		 " CREATE_DATE,UPDATE_DATE,HOSP_CODE,HOSP_DESC," +
               		 " OPT_USER,OPT_DATE,OPT_TERM) " +
               		 " VALUES('"+ orderNo+ "'," +
               		 " '"+ temp.getValue("CASE_NO")+ "',"+
               		 " "+ temp.getValue("SEQ_NO")+ "," +
               		 " to_date('"+ temp.getValue("CHARGE_DATE")+"','yyyyMMddHH24MISS')," +
               		 " '"+ temp.getValue("ORDER_CODE")+ "'," +
               		 " '"+ temp.getValue("ORDER_DESC")+ "'," +
               		 " '"+ temp.getValue("NHI_ORDER_CODE")+ "'," +
               		 " "+ temp.getDouble("PRICE")+ "," +
               		 " "+ temp.getInt("QTY")+ "," +
               		 " "+ temp.getDouble("TOT_AMT")+ "," +  
               		 " '"+ temp.getValue("CLASS_CODE")+ "',0,0,0," +
               		 " SYSDATE,SYSDATE,'40','�С�����'," +
               		 " '"+ temp.getValue("OPT_USER")+ "',SYSDATE," +
               		 " '"+ temp.getValue("OPT_TERM")+ "')";
//               		 System.out.println("sql:=============="+sql);
               		result = new TParm(TJDODBTool.getInstance().update(sql));
                } else {
                    // �޸Ĳ���
                	 String sql= " UPDATE INS_XNH_UPLOAD SET"+
                	" CHARGE_DATE=TO_DATE('"+ temp.getValue("CHARGE_DATE")+"','YYYYMMDDHH24MISS'),"+ 
                	" NHI_ORDER_CODE='"+ temp.getValue("NHI_ORDER_CODE")+ "',"+
                	" ORDER_CODE='"+ temp.getValue("ORDER_CODE")+ "',"+ 	       
                	" ORDER_DESC='"+ temp.getValue("ORDER_DESC")+ "',"+
                	" PRICE="+ temp.getDouble("PRICE")+ ","+
                	" QTY="+ temp.getInt("QTY")+ ","+
                	" TOT_AMT="+ temp.getDouble("TOT_AMT")+ ","+ 
                	" CLASS_CODE='"+ temp.getValue("CLASS_CODE")+ "',"+
                	" OPT_USER='"+ temp.getValue("OPT_USER")+ "',"+
                	" OPT_DATE=SYSDATE,"+ 
                	" OPT_TERM='"+ temp.getValue("OPT_TERM")+ "'"+
                	" WHERE CASE_NO='"+ temp.getValue("CASE_NO")+ "'"+
                	" AND SEQ_NO="+ temp.getValue("SEQ_NO")+ "";
//                	 System.out.println("sql:=============="+sql); 
                	result = new TParm(TJDODBTool.getInstance().update(sql));
                }
            }
            if (result.getErrCode() < 0) {
                return result;
            }
        }
        return result;
    } 

}
