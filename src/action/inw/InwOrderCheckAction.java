package action.inw;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import action.inw.client.SpcInwCheckDto;
import action.inw.client.SpcInwCheckDtos;
import action.inw.client.SpcOdiDspnm;
import action.inw.client.SpcOdiDspnms;
import action.inw.client.SpcOdiService_SpcOdiServiceImplPort_Client;

import com.dongyang.Service.Server;
import com.dongyang.action.TAction;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

import jdo.inw.InwOrderCheckTool;
import jdo.inw.InwOrderExecTool;
import jdo.spc.SPCIndCabdspnTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title: סԺ��ʿվ���Action
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class InwOrderCheckAction extends TAction {
	public InwOrderCheckAction() {
	}

	/**
	 * չ�����
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm onSave(TParm parm) {

		TParm result = new TParm();
		// ����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();
		// ���ó��ڴ��õ�չ������
		try {
			result = InwOrderCheckTool.getInstance().onCheck(parm, connection);
//			System.out.println("============="+result);
			if(result.getErrCode()<0){
				connection.rollback();
			}
		} catch (Exception e) {
			connection.rollback();
			e.printStackTrace();
		} finally {
			connection.close();
			return result;
		}
	}

	/**
	 * ȡ�����չ�����
	 * 
	 * @param parm
	 * @return TParm
	 */
	public TParm onUndoSave(TParm parm) {
		TParm result = new TParm();
		// ����һ�����ӣ��ڶ������ʱ�����Ӹ�������ʹ��
		TConnection connection = getConnection();
		// ���ó��ڴ��õ�չ������
		result = InwOrderCheckTool.getInstance().onUndoCheck(parm, connection);
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
	 * �ÿ������
	 * @param parm
	 * @return
	 */
    public TParm getIndQty(TParm  parm){
    	TParm  result=new TParm();
    	double qty=0;
    	try {
    		 SpcInwCheckDto dto=new SpcInwCheckDto();
             dto.setOrderCode(parm.getValue("ORDER_CODE"));
             List<SpcInwCheckDto> dtoList =new ArrayList<SpcInwCheckDto>();
             dtoList.add(dto);
             SpcInwCheckDtos dtos = new SpcInwCheckDtos();
             dtos.setRegionCode(parm.getValue("REGION_CODE"));
             dtos.setStationCode(parm.getValue("STATION_CODE"));
             dtos.setSpcInwCheckDtos(dtoList);
             SpcInwCheckDtos returnDto = SpcOdiService_SpcOdiServiceImplPort_Client.inwCheck(dtos);
             List<SpcInwCheckDto> dtoReturnList = returnDto.getSpcInwCheckDtos();
             SpcInwCheckDto rDto = (SpcInwCheckDto)dtoReturnList.get(0);  
             qty=dtoReturnList.get(0).getQty();
//             System.out.println(parm.getValue("ORDER_CODE")+" "+parm.getValue("STATION_CODE")+qty);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("��ʿ���У����WSʧ��");
			System.out.println(e);
			result.setErrCode(-1);
			result.setErrText("����������WSʧ��");
		}
        result.setData("QTY", qty);
    	return result;
    }
    /**
     * ���ܹ񱣴���ù�ҩ(��ʱֻ�����ʱ)
     * @param parm
     * @return
     */
    public TParm onSendGYPha(TParm  parm){
    	TParm result=new TParm();
    	String type=parm.getValue("TYPE");
//    	System.out.println("���===========parm======"+parm);
    	try {
    		Map pat = InwOrderExecTool.getInstance().groupByPatParm(parm);
    		Iterator it = pat.values().iterator();
    		while (it.hasNext()) {
        		SpcOdiDspnms dspnms=new SpcOdiDspnms();
        		List<SpcOdiDspnm> spcOdiDspnms =new ArrayList<SpcOdiDspnm>();
    			TParm patParm = (TParm) it.next();
    			for (int i = 0; i < patParm.getCount(); i++) {
    				String caseNo=patParm.getValue("CASE_NO",i);
    				String orderNo=patParm.getValue("ORDER_NO",i);
    				String orderSeq=patParm.getValue("ORDER_SEQ",i);
    				String sql="SELECT CASE_NO,ORDER_NO,ORDER_SEQ,START_DTTM,END_DTTM,REGION_CODE,"+
    				"STATION_CODE,DEPT_CODE,VS_DR_CODE,BED_NO,MR_NO,DSPN_KIND,"+
    				"DSPN_DATE,DSPN_USER,ORDER_CAT1_CODE,CAT1_TYPE,ORDER_CODE,"+
    				"ORDER_DESC,GOODS_DESC,SPECIFICATION,MEDI_QTY,MEDI_UNIT,"+
    				"FREQ_CODE,ROUTE_CODE,DOSAGE_QTY,DOSAGE_UNIT,DISPENSE_QTY,"+
    				"DISPENSE_UNIT,PHA_DISPENSE_NO,TAKEMED_ORG,TAKEMED_NO,OPT_USER,"+
    				"OPT_DATE,OPT_TERM,CTRLDRUGCLASS_CODE FROM ODI_DSPNM WHERE CASE_NO='"+caseNo+"'" 
    				+" AND ORDER_NO='"+orderNo+"' AND ORDER_SEQ='"+orderSeq+"'"; 				
//    				System.out.println("SQL=========="+sql);
    				TParm selParm=new TParm(TJDODBTool.getInstance().select(sql));
    				if(selParm.getCount()<0)
    					continue;
    				TParm dspnmParm=selParm.getRow(0);
    				SpcOdiDspnm dspnm=new SpcOdiDspnm();
    				dspnm.setCaseNo(dspnmParm.getValue("CASE_NO"));
    				dspnm.setOrderNo(dspnmParm.getValue("ORDER_NO"));
    				dspnm.setOrderSeq(dspnmParm.getInt("ORDER_SEQ"));
    				dspnm.setStartDttm(dspnmParm.getValue("START_DTTM"));
    				dspnm.setEndDttm(dspnmParm.getValue("END_DTTM"));
    				dspnm.setRegionCode(dspnmParm.getValue("REGION_CODE"));
    				dspnm.setStationCode(dspnmParm.getValue("STATION_CODE"));
    				dspnm.setDeptCode(dspnmParm.getValue("DEPT_CODE"));
    				dspnm.setVsDrCode(dspnmParm.getValue("VS_DR_CODE"));
    				dspnm.setBedNo(dspnmParm.getValue("BED_NO"));
    				dspnm.setMrNo(dspnmParm.getValue("MR_NO"));
    				dspnm.setDspnKind(dspnmParm.getValue("DSPN_KIND"));
    				dspnm.setDspnDate(dspnmParm.getValue("DSPN_DATE"));
    				dspnm.setDspnUser(dspnmParm.getValue("DSPN_USER"));
    				dspnm.setOrderCat1Code(dspnmParm.getValue("ORDER_CAT1_CODE"));
    				dspnm.setCat1Type(dspnmParm.getValue("CAT1_TYPE"));
    				dspnm.setOrderCode(dspnmParm.getValue("ORDER_CODE"));
    				dspnm.setOrderDesc(dspnmParm.getValue("ORDER_DESC"));
    				dspnm.setGoodsDesc(dspnmParm.getValue("GOODS_DESC"));
    				dspnm.setSpecification(dspnmParm.getValue("SPECIFICATION"));
    				dspnm.setMediQty(dspnmParm.getDouble("MEDI_QTY"));
    				dspnm.setMediUnit(dspnmParm.getValue("MEDI_UNIT"));
    				dspnm.setFreqCode(dspnmParm.getValue("FREQ_CODE"));
    				dspnm.setRouteCode(dspnmParm.getValue("ROUTE_CODE"));
    				dspnm.setDosageQty(dspnmParm.getDouble("DOSAGE_QTY"));
    				dspnm.setDosageUnit(dspnmParm.getValue("DOSAGE_UNIT"));
    				dspnm.setDispenseQty(dspnmParm.getDouble("DISPENSE_QTY"));
    				dspnm.setDispenseUnit(dspnmParm.getValue("DISPENSE_UNIT"));
    				dspnm.setPhaDispenseNo(dspnmParm.getValue("PHA_DISPENSE_NO"));
    				dspnm.setTakemedOrg(dspnmParm.getValue("TAKEMED_ORG"));
    				dspnm.setTakemedNo(dspnmParm.getValue("TAKEMED_NO"));
    				dspnm.setOptUser(dspnmParm.getValue("OPT_USER"));
    				dspnm.setOptDate(dspnmParm.getValue("OPT_DATE"));
    				dspnm.setOptTerm(dspnmParm.getValue("OPT_TERM"));
    				dspnm.setCtrldrugclassCode(dspnmParm.getValue("CTRLDRUGCLASS_CODE"));
//    				System.out.println("CtrldrugclassCode======"+dspnm.getCtrldrugclassCode());
    				spcOdiDspnms.add(dspnm);	
    			}
    			dspnms.setSpcOdiDspnms(spcOdiDspnms);
    			String str=SpcOdiService_SpcOdiServiceImplPort_Client.OnSaveIndCabdspn(dspnms,type);
    			
//    			System.out.println("���======"+str);
    			if(!str.toLowerCase().toString().equals("success")){
    				result.setErrCode(-1);
    				result.setErrText("����������WSʧ��"+str);
    			}
    		}
		} catch (Exception e) {
			// TODO: handle exception
			result.setErrCode(-1);
			result.setErrText("����������WS�쳣");
		}
    	return result;	
    }
    
    /**
     * ����ҽ�����
     * @param parm
     * @return
     */
    public TParm onCheckOP(TParm parm) { // wanglong add 20140707
        TParm result = new TParm();
        TConnection connection = getConnection();
        
        
        
        //add by yangjj 20150625
        TParm dataParm = parm.getParm("dataParm");
        
        TParm dataParm1 = new TParm();
        TParm d1 = new TParm();
        dataParm1.setData("OPT_USER", parm.getData("OPT_USER"));
        dataParm1.setData("OPT_DATE", parm.getData("OPT_DATE"));
        dataParm1.setData("OPT_TERM", parm.getData("OPT_TERM"));
        
        
        TParm dataParm2 = new TParm();
        TParm d2 = new TParm();
        dataParm2.setData("OPT_USER", parm.getData("OPT_USER"));
        dataParm2.setData("OPT_DATE", parm.getData("OPT_DATE"));
        dataParm2.setData("OPT_TERM", parm.getData("OPT_TERM"));
        dataParm2.setData("OPE", "Y");
        
        for(int i = 0 ; i < dataParm.getCount() ; i++){
        	String orderNo = dataParm.getValue("ORDER_NO", i);
        	String orderSeq = dataParm.getValue("ORDER_SEQ", i);
        	String caseNo = dataParm.getValue("CASE_NO", i);
        	String s1 = "SELECT A.CLASSIFY FROM SYS_DEPT A , ODI_ORDER B " +
        			" WHERE A.DEPT_CODE = B.EXEC_DEPT_CODE AND B.ORDER_NO = '"+orderNo+"' AND B.ORDER_SEQ = '"+orderSeq+"' AND CASE_NO = '"+caseNo+"'";
        	TParm r = new TParm(TJDODBTool.getInstance().select(s1));
        	//ҩƷ
        	if("2".equals(r.getValue("CLASSIFY",0))){
        		d2.addRowData(dataParm, i);
        	}
        	//��ҩƷ
        	else{
        		d1.addRowData(dataParm, i);
        	}
        }
        
        dataParm1.setData("dataParm", d1.getData());
        dataParm2.setData("dataParm", d2.getData());
        
        if(dataParm2 != null&&d2.getCount()>0){
        	result = onSave(dataParm2);
        }
        
        
        if(dataParm1 != null&&d1.getCount()>0){
        	result = InwOrderCheckTool.getInstance().onCheckOPOrder(parm, connection);//���ҽ��
            if (result == null || result.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return result;
            }
            
            String sql = "SELECT * FROM ODI_ORDER WHERE 1 <> 1";
            TParm feeParm = new TParm(TJDODBTool.getInstance().select(sql));
            if (feeParm.getErrCode() < 0) {
                connection.rollback();
                connection.close();
                return feeParm;
            }
            for (int i = 0; i < d1.getCount(); i++) {
                if (!d1.getValue("ORDER_CODE", i).equals(d1.getValue("ORDERSET_CODE", i))) {
                    String orderSql =
                            "SELECT * FROM ODI_ORDER WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDER_SEQ=#";
                    orderSql = orderSql.replaceFirst("#", d1.getValue("CASE_NO", i));
                    orderSql = orderSql.replaceFirst("#", d1.getValue("ORDER_NO", i));
                    orderSql = orderSql.replaceFirst("#", d1.getValue("ORDER_SEQ", i));
                    TParm rowParm = new TParm(TJDODBTool.getInstance().select(orderSql));
                    if (rowParm.getErrCode() < 0) {
                        connection.rollback();
                        connection.close();
                        return rowParm;
                    }
                    feeParm.addRowData(rowParm, 0);// rowParmֻ��һ��
                } else {
                    String ordersSql =
                            "SELECT * FROM ODI_ORDER WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDERSET_GROUP_NO=# ORDER BY ORDER_SEQ";
                    ordersSql = ordersSql.replaceFirst("#", d1.getValue("CASE_NO", i));
                    ordersSql = ordersSql.replaceFirst("#", d1.getValue("ORDER_NO", i));
                    ordersSql = ordersSql.replaceFirst("#", d1.getValue("ORDERSETGROUP_NO", i));
                    TParm rowsParm = new TParm(TJDODBTool.getInstance().select(ordersSql));
                    if (rowsParm.getErrCode() < 0) {
                        connection.rollback();
                        connection.close();
                        return rowsParm;
                    }
                    for (int j = 0; j < rowsParm.getCount(); j++) {
                        feeParm.addRowData(rowsParm, j);// rowsParm��������ҽ������ϸ��
                    }
                }
            }
            //=========pangben 2015-9-14 �޸Ŀ�������
            for (int i = 0; i < feeParm.getCount("ORDER_CODE"); i++) {
            	 feeParm.setData("ORDER_DEPT_CODE", i, feeParm.getValue("DEPT_CODE",i));
			}
            feeParm.setData("DATA_TYPE", 6);// ����ҽ��Ϊ6
            feeParm.setData("FLG", "ADD");
            
            result = InwOrderCheckTool.getInstance().onFee(feeParm, connection);//�Ʒѣ�дIBS_ORDM��IBS_ORDD
            if (result == null || result.getErrCode() < 0) {
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
     * ����ҽ��ȡ�����
     * @param parm
     * @return
     */
    public TParm onUndoCheckOP(TParm parm) { // wanglong add 20140707
        TParm result = new TParm();
        TConnection connection = getConnection();
        result = InwOrderCheckTool.getInstance().onUndoCheckOPOrder(parm, connection);//��Ч���ҽ��
        if (result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        TParm dataParm = parm.getParm("dataParm");
        TParm dataParm1 = new TParm();
        TParm d1 = new TParm();
        dataParm1.setData("OPT_USER", parm.getData("OPT_USER"));
        dataParm1.setData("OPT_DATE", parm.getData("OPT_DATE"));
        dataParm1.setData("OPT_TERM", parm.getData("OPT_TERM"));
        
        
        TParm dataParm2 = new TParm();
        TParm d2 = new TParm();
        dataParm2.setData("OPT_USER", parm.getData("OPT_USER"));
        dataParm2.setData("OPT_DATE", parm.getData("OPT_DATE"));
        dataParm2.setData("OPT_TERM", parm.getData("OPT_TERM"));
        dataParm2.setData("OPE", "Y");
        
        for(int i = 0 ; i < dataParm.getCount() ; i++){
        	String orderNo = dataParm.getValue("ORDER_NO", i);
        	String orderSeq = dataParm.getValue("ORDER_SEQ", i);
        	String caseNo = dataParm.getValue("CASE_NO", i);
        	String s1 = "SELECT A.CLASSIFY FROM SYS_DEPT A , ODI_ORDER B " +
        			" WHERE A.DEPT_CODE = B.EXEC_DEPT_CODE AND B.ORDER_NO = '"+orderNo+"' AND B.ORDER_SEQ = '"+orderSeq+"' AND CASE_NO = '"+caseNo+"'";
        	TParm r = new TParm(TJDODBTool.getInstance().select(s1));
        	//ҩƷ
        	if("2".equals(r.getValue("CLASSIFY",0))){
        		d2.addRowData(dataParm, i);
        	}
        	//��ҩƷ
        	else{
        		d1.addRowData(dataParm, i);
        	}
        }
        
        dataParm1.setData("dataParm", d1.getData());
        dataParm2.setData("dataParm", d2.getData());
        
        System.out.println("dataParm2:"+dataParm2);
        if(dataParm2 != null&&d2.getCount()>0){
        	
				String date = SystemTool.getInstance().getDate().toString().substring(0, 19);
				for(int i = 0 ; i < d2.getCount() ; i++){
					String sql = "UPDATE ODI_DSPNM SET NS_EXEC_CODE = '' , NS_EXEC_DATE = NULL " +
					" WHERE ORDER_NO = '"+d2.getValue("ORDER_NO", i)+"' AND ORDER_SEQ = '"+d2.getValue("ORDER_SEQ", i)+"' AND CASE_NO = '"+d2.getValue("CASE_NO", i)+"'";
				
					
					result = new TParm(TJDODBTool.getInstance().update(sql,connection));
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode()
								+ result.getErrText()
								+ result.getErrName());
						return result;
					}
					
				}
				connection.commit();
				
        	result = onUndoSave(d2);
        }
        
        
        if(dataParm1 != null&&d1.getCount()>0){
        String sql = "SELECT * FROM ODI_ORDER WHERE 1 <> 1";
        TParm feeParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (feeParm.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return feeParm;
        }
        for (int i = 0; i < d1.getCount(); i++) {
            if (!d1.getValue("ORDER_CODE", i).equals(d1.getValue("ORDERSET_CODE", i))) {
                String orderSql =
                        "SELECT * FROM ODI_ORDER WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDER_SEQ=#";
                orderSql = orderSql.replaceFirst("#", d1.getValue("CASE_NO", i));
                orderSql = orderSql.replaceFirst("#", d1.getValue("ORDER_NO", i));
                orderSql = orderSql.replaceFirst("#", d1.getValue("ORDER_SEQ", i));
                TParm rowParm = new TParm(TJDODBTool.getInstance().select(orderSql));
                if (rowParm.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return rowParm;
                }
                feeParm.addRowData(rowParm, 0);// rowParmֻ��һ��
            } else {
                String ordersSql =
                        "SELECT * FROM ODI_ORDER WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDERSET_GROUP_NO=# ORDER BY ORDER_SEQ";
                ordersSql = ordersSql.replaceFirst("#", d1.getValue("CASE_NO", i));
                ordersSql = ordersSql.replaceFirst("#", d1.getValue("ORDER_NO", i));
                ordersSql = ordersSql.replaceFirst("#", d1.getValue("ORDERSET_GROUP_NO", i));
                TParm rowsParm = new TParm(TJDODBTool.getInstance().select(ordersSql));
                
                System.out.println("rowsParm:"+rowsParm);
                if (rowsParm.getErrCode() < 0) {
                    connection.rollback();
                    connection.close();
                    return rowsParm;
                }
                for (int j = 0; j < rowsParm.getCount(); j++) {
                    feeParm.addRowData(rowsParm, j);// rowsParm��������ҽ������ϸ��
                }
            }
        }
        feeParm.setData("DATA_TYPE", 6);// ����ҽ��Ϊ6
        feeParm.setData("FLG", "SUB");
        
        result = InwOrderCheckTool.getInstance().onFee(feeParm, connection);//�˷ѣ�дIBS_ORDM��IBS_ORDD
        if (result == null || result.getErrCode() < 0) {
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
     * ��ʱҽ����ʿ��˱���(���ܹ�)
     * @param parm
     * @return
     * @author wangbin 2015/1/27
     */
    public TParm onSavePha(TParm  parm){
    	TParm result=new TParm();
    	String type=parm.getValue("TYPE");
    	try {
    		Map pat = InwOrderExecTool.getInstance().groupByPatParm(parm);
    		Iterator it = pat.values().iterator();
    		TParm patParm = new TParm();
    		TParm mParm = new TParm();
    		while (it.hasNext()) {
    			patParm = (TParm) it.next();
    			mParm = new TParm();
    			for (int i = 0; i < patParm.getCount(); i++) {
					String caseNo = patParm.getValue("CASE_NO", i);
					String orderNo = patParm.getValue("ORDER_NO", i);
					String orderSeq = patParm.getValue("ORDER_SEQ", i);
    				String sql = "SELECT CASE_NO,ORDER_NO,ORDER_SEQ,START_DTTM,END_DTTM,REGION_CODE,"+
    				"STATION_CODE,DEPT_CODE,VS_DR_CODE,BED_NO,MR_NO,DSPN_KIND,"+
    				"DSPN_DATE,DSPN_USER,ORDER_CAT1_CODE,CAT1_TYPE,ORDER_CODE,"+
    				"ORDER_DESC,GOODS_DESC,SPECIFICATION,MEDI_QTY,MEDI_UNIT,"+
    				"FREQ_CODE,ROUTE_CODE,DOSAGE_QTY,DOSAGE_UNIT,DISPENSE_QTY,"+
    				"DISPENSE_UNIT,PHA_DISPENSE_NO,TAKEMED_ORG,TAKEMED_NO,OPT_USER,"+
    				"OPT_DATE,OPT_TERM,CTRLDRUGCLASS_CODE,TAKE_DAYS FROM ODI_DSPNM WHERE CASE_NO='"+caseNo+"'" 
    				+" AND ORDER_NO='"+orderNo+"' AND ORDER_SEQ='"+orderSeq+"'"; 				
					TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
    				if(selParm.getCount()<0) {
    					continue;
    				}
					TParm dspnmParm = selParm.getRow(0);
    				mParm.setData("CASE_NO",i,dspnmParm.getValue("CASE_NO"));
    	        	mParm.setData("ORDER_NO",i,dspnmParm.getValue("ORDER_NO"));
    	        	mParm.setData("ORDER_SEQ",i,dspnmParm.getInt("ORDER_SEQ"));
    	        	mParm.setData("START_DTTM",i,dspnmParm.getValue("START_DTTM"));
    	        	mParm.setData("END_DTTM",i,dspnmParm.getValue("END_DTTM"));
    	        	mParm.setData("REGION_CODE",i,dspnmParm.getValue("REGION_CODE"));
    	        	mParm.setData("STATION_CODE",i,dspnmParm.getValue("STATION_CODE"));
    	        	mParm.setData("DEPT_CODE",i,dspnmParm.getValue("DEPT_CODE"));
    	        	mParm.setData("VS_DR_CODE",i,dspnmParm.getValue("VS_DR_CODE"));
    	        	mParm.setData("BED_NO",i,dspnmParm.getValue("BED_NO"));
    	        	mParm.setData("MR_NO",i,dspnmParm.getValue("MR_NO"));
    	        	mParm.setData("DSPN_KIND",i,dspnmParm.getValue("DSPN_KIND")); 
    	        	String dspnDate = dspnmParm.getValue("DSPN_DATE");
    	        	if(dspnDate == null || dspnDate.equals("") ){
    	        		mParm.setData("DSPN_DATE",i,new TNull(Timestamp.class));
    	        	}else{
    	        		mParm.setData("DSPN_DATE",i,StringTool.getTimestamp(dspnDate, "yyyy-MM-dd HH:mm:ss"));
    	        	}
    	        	mParm.setData("DSPN_USER",i,dspnmParm.getValue("DSPN_USER"));
    	        	mParm.setData("ORDER_CAT1_CODE",i,dspnmParm.getValue("ORDER_CAT1_CODE"));
    	        	mParm.setData("CAT1_TYPE",i,dspnmParm.getValue("CAT1_TYPE"));
    	        	mParm.setData("ORDER_CODE",i,dspnmParm.getValue("ORDER_CODE")); 
    	        	mParm.setData("ORDER_DESC",i,dspnmParm.getValue("ORDER_DESC")); 
    	        	mParm.setData("GOODS_DESC",i,dspnmParm.getValue("GOODS_DESC")); 
    	        	mParm.setData("SPECIFICATION",i,dspnmParm.getValue("SPECIFICATION")); 
    	        	mParm.setData("MEDI_QTY",i,dspnmParm.getValue("MEDI_QTY")); 
    	        	mParm.setData("MEDI_UNIT",i,dspnmParm.getValue("MEDI_UNIT")); 
    	        	mParm.setData("FREQ_CODE",i,dspnmParm.getValue("FREQ_CODE")); 
    	        	mParm.setData("ROUTE_CODE",i,dspnmParm.getValue("ROUTE_CODE")); 
    	        	mParm.setData("TAKE_DAYS",i,dspnmParm.getValue("TAKE_DAYS")); 
    	        	mParm.setData("DOSAGE_QTY",i,dspnmParm.getValue("DOSAGE_QTY")); 
    	        	mParm.setData("DOSAGE_UNIT",i,dspnmParm.getValue("DOSAGE_UNIT")); 
    	        	mParm.setData("DISPENSE_QTY",i,dspnmParm.getValue("DISPENSE_QTY")); 
    	        	mParm.setData("DISPENSE_UNIT",i,dspnmParm.getValue("DISPENSE_UNIT")); 
    	        	mParm.setData("PHA_DISPENSE_NO",i,dspnmParm.getValue("PHA_DISPENSE_NO")); 
    	        	mParm.setData("TAKEMED_NO",i,dspnmParm.getValue("TAKEMED_NO"));
    	        	mParm.setData("TAKEMED_ORG",i,dspnmParm.getValue("TAKEMED_ORG"));
    	        	mParm.setData("IS_CONFIRM",i,"");
    	        	mParm.setData("TAKEMED_USER",i,"");
    	        	mParm.setData("TAKEMED_DATE",i,new TNull(Timestamp.class));
    	        	mParm.setData("IS_RECLAIM",i,"N");
    	        	mParm.setData("RECLAIM_USER",i,"");
    	        	mParm.setData("RECLAIM_DATE",i,new TNull(Timestamp.class));
    	        	mParm.setData("TOXIC_ID1",i,"");
    	        	mParm.setData("TOXIC_ID2",i,"");
    	        	mParm.setData("TOXIC_ID3",i,"");
    	        	mParm.setData("OPT_USER",i,dspnmParm.getValue("OPT_USER"));
    	        	String optDate = dspnmParm.getValue("OPT_DATE");
    	        	if(optDate == null || optDate.equals("")){
    	        		mParm.setData("OPT_DATE",i,new TNull(Timestamp.class));
    	        	}else{
    	        		mParm.setData("OPT_DATE",i,StringTool.getTimestamp(optDate, "yyyy-MM-dd HH:mm:ss"));
    	        	}
    	        	mParm.setData("OPT_TERM",i,dspnmParm.getValue("OPT_TERM"));
    	        	mParm.setData("CTRLDRUGCLASS_CODE",i,dspnmParm.getValue("CTRLDRUGCLASS_CODE"));	
    			}
    			
    			// ȡ�����,ɾ��
				if (type != null && type.equals("D")) {
					result = this.onDeleteIndCabdspn(mParm);
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText());
						return result;
					}
				} else {
					// ���,����
					result = this.onSaveIndCabdspn(mParm);
					if (result.getErrCode() < 0) {
						err("ERR:" + result.getErrCode() + result.getErrText());
						return result;
					}
				}
    		}
		} catch (Exception e) {
			result.setErrCode(-1);
			result.setErrText("�����쳣");
			err("ERR:" + e.getStackTrace());
		}
    	return result;	
    }
    
    /**
     * ��ʱҽ�������ܹ񱣴�
     * @param parm
     * @return
     * @author wangbin 2015/1/27
     */
    public TParm onSaveIndCabdspn(TParm parm ){
		TParm result = new TParm();
		int count = parm.getCount("CASE_NO");
		TConnection conn = getConnection();
		
		for(int i = 0 ; i < count ; i++){
			TParm rowParm = parm.getRow(i);
			 
			result = SPCIndCabdspnTool.getInstance().insert(rowParm, conn);

            if (result.getErrCode() < 0) {
            	result.setErrCode(-1);
            	System.out.println("���ʧ��");
	            result.setErrText("���ʧ��");
            	conn.rollback();
                conn.close();
                return result;
            }
		}
		conn.commit();
	    conn.close();
		return result ;
	}
	
    /**
     * ��ʱҽ�������ܹ�ɾ��
     * @param parm
     * @return
     * @author wangbin 2015/1/27
     */
	public TParm onDeleteIndCabdspn(TParm parm){
		TParm result = new TParm();
		int count = parm.getCount("CASE_NO");
		TConnection conn = getConnection();
	
		for(int i = 0 ; i < count ; i++){
			TParm rowParm = parm.getRow(i);
			 
			result = SPCIndCabdspnTool.getInstance().delete(rowParm, conn);

            if (result.getErrCode() < 0) {
            	 
            	result.setErrCode(-1);
            	System.out.println("ȡ�����ʧ��");
	            result.setErrText("ȡ�����ʧ��");
            	conn.rollback();
                conn.close();
                return result;
            }
		}
		conn.commit();
	    conn.close();
		return result ;
	}
}