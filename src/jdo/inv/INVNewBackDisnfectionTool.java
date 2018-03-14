package jdo.inv;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;


/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>  
 *
 * @author wangm	2013.06.25  
 * @version 1.0
 */
public class INVNewBackDisnfectionTool extends TJDOTool{

	public static INVNewBackDisnfectionTool instanceObject;
	
	/**
     * ������                     
     */
    public INVNewBackDisnfectionTool() {
        setModuleName("inv\\INVNewBackDisinfectionModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static INVNewBackDisnfectionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVNewBackDisnfectionTool();
        return instanceObject;
    }
    
    /**
     * ��ѯ��������Ϣ
     * */
	public TParm queryPackageInfo(TParm parm){
		
		TParm result = this.query("queryPackageInfo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * ������Ч�����ѯ��������Ϣ
     * */
	public TParm queryPackageInfoByBarcode(TParm parm){
		 
		TParm result = this.query("queryPackageInfoByBarcode", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;   
	}  
	 
	
	/**
     * ����ȫ�����ѯ��������Ϣ  
     * */
	public TParm queryPackageInfoByAllBarcode(TParm parm){
		 
		TParm result = this.query("queryPackageInfoByAllBarcode", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result; 
	}
	
	
	 /**
     * ��ѯ��������ϸ��Ϣ
     * */
	public TParm queryPackageDetailInfo(TParm parm){
		
		TParm result = this.query("queryPackageDetailInfo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * ����������ѯ���յ���Ϣ
     * */
	public TParm queryBackDisnfection(TParm parm){
		
		TParm result = this.query("queryBackDisinfection", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	/**
     * ���ݻ��յ��Ų�ѯ���յ���Ϣ
     * */
	public TParm queryDisnfectionByNo(TParm parm){
		
		TParm result = this.query("queryDisinfectionByRecycleNo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	 
	
	/**
     * ��ѯ��ӡ������Ϣ
     * */
	public TParm queryBarcodeInfo(TParm parm){
		
		TParm result = this.query("queryBarcodeInfo", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * ��ѯ��������
     * */
	public TParm queryDeptName(TParm parm){
		
		TParm result = this.query("queryDeptName", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	/**
     * ��ѯ��Ա����
     * */
	public TParm queryUserName(TParm parm){
		
		TParm result = this.query("queryUserName", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * ��ѯ������������Ϣ����Ĺ����ѯ��
     * */
	public TParm queryPackMaterialInfoByBarcode(TParm parm){
		
		TParm result = this.query("queryPMInfoByBarcode", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	/**
     * �½����յ�����
     * */
	public TParm insertBackDisnfection(TParm parm, TConnection connection){
		
		TParm disTable = parm.getParm("DISNFECTIONTABLE");			//���ձ�
		
		TParm packageMTable = parm.getParm("PACKAGEMAINTABLE");		//��������
		Object obj = parm.getParm("RECOUNTTIME");					//������ϸ
		
		  
		//disTable:::{Data={OPT_USER=[D001], DELETE_FLG=[N], RECYCLE_NO=[140326000002], ORG_CODE=[0409], OPT_TERM=[127.0.0.1], 
		//OPT_DATE=[2014-03-26 09:42:35]}, ACTION={COUNT=1}}
		//packageMTable:::{Data={DISINFECTION_VALID_DATE=[2014/09/22 09:43:08], DISINFECTION_USER=[D001], PACK_CODE=[101010],
		//RECYCLE_USER=[D001], PACK_DESC=[���������ۺϰ�], STATUS=[����], DISINFECTION_DATE=[2014-03-26 09:43:08], WASH_USER=[D001], 
		//WASH_DATE=[2014-03-26 09:43:08], BARCODE=[140326000004], QTY=[1], DISINFECTION_PROGRAM=[1], PACK_SEQ_NO=[3], 
		//RECYCLE_DATE=[2014-03-26 09:43:08], DISINFECTION_POTSEQ=[1]}, ACTION={COUNT=1}}
		TParm result = new TParm();
		
		//fux modify 20140334 ��ֵ���û��ղ����  
		//����� ��ֵ����Ʒ   stockM +1  stockD+1 
		String invKind = "";
		 //����ʱ   
	     //��ѯ���������ʹ��� 
		String orgCode = disTable.getData("ORG_CODE", 0).toString();
		//�ۿ������  
		TParm tpS = new TParm();  
		tpS.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE"));
		//������λ���ʱ������Ʒ�ӻؿ�棿  
		for (int i = 0; i < tpS.getRow(0).getCount("PACK_CODE"); i++) {
			System.out.println(""+packageMTable.getData("STATUS", i)); 
			if(packageMTable.getData("STATUS", i).equals("�ڿ�")){ 
				//[����]
				//[�ڿ�]     
				//continue;  
				//�ڿ�״̬�����п��ӻ�(����һ����)
				String packcode = tpS.getRow(0).getValue("PACK_CODE",i); 
				//System.out.println("packCode==="+packcode);      
				TParm stockParm = new TParm (TJDODBTool.getInstance().select(queryPackDByPackCode(packcode)));
				for(int j=0; j<stockParm.getCount();j++){	//ÿ������  
				    String invCode = stockParm.getValue("INV_CODE", j);   
					TParm invBaseKind =   new TParm (TJDODBTool.getInstance().select(getInvKind(invCode))); 
					invKind = invBaseKind.getValue("INVKIND_CODE",0).toString();    
						TParm stock = new TParm();   
						stock = stockParm.getRow(j);  
						//����������(�ӿ��)
						stock.setData("STOCK_QTY", stock.getDouble("QTY") * +1);
						stock.setData("PACK_SEQ_NO",  packageMTable.getData("PACK_SEQ_NO"));
						stock.setData("ONCE_USE_FLG",  "Y"); 
						result = InvStockMTool.getInstance().updateStockMQty(stock,connection);
						if (result.getErrCode() < 0) {
							err(result.getErrCode() + " " + result.getErrText());
							return result;
						}
							 
						List<TParm> stockDList = new ArrayList<TParm>();
						stockDList = this.chooseBatchSeq(stock, orgCode);	//��ѯϸ������
//						INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(stock, connection);//ɾ�����е���������ϸ
						//����ϸ����(�ӿ��) 
						for(int m=0;m<stockDList.size();m++){
							TParm tt = new TParm();
							tt = stockDList.get(m);
							tt.setData("STOCK_QTY", tt.getDouble("STOCK_QTY") * +1);
							result = InvStockDTool.getInstance().updateStockQty(tt,connection);  
							if (result.getErrCode() < 0) {
								err(result.getErrCode() + " " + result.getErrText());
								return result;
							}
						}
				} 
				
			}else{
			String packcode = tpS.getRow(0).getValue("PACK_CODE",i); 
			//System.out.println("packCode==="+packcode);      
			TParm stockParm = new TParm (TJDODBTool.getInstance().select(queryPackDByPackCode(packcode)));
			for(int j=0; j<stockParm.getCount();j++){	//ÿ������  
			    String invCode = stockParm.getValue("INV_CODE", j);   
				TParm invBaseKind =   new TParm (TJDODBTool.getInstance().select(getInvKind(invCode))); 
				invKind = invBaseKind.getValue("INVKIND_CODE",0).toString();    
				if("C".equals(invKind)){    
					TParm stock = new TParm();   
					stock = stockParm.getRow(j);  
					//����������(�ӿ��)
					stock.setData("STOCK_QTY", stock.getDouble("QTY") * +1);
					stock.setData("PACK_SEQ_NO",  packageMTable.getData("PACK_SEQ_NO"));
					stock.setData("ONCE_USE_FLG",  "Y"); 
					result = InvStockMTool.getInstance().updateStockMQty(stock,connection);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
						 
					List<TParm> stockDList = new ArrayList<TParm>();
					stockDList = this.chooseBatchSeq(stock, orgCode);	//��ѯϸ������
//					INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(stock, connection);//ɾ�����е���������ϸ
					//����ϸ����(�ӿ��)
					for(int m=0;m<stockDList.size();m++){
						TParm tt = new TParm();
						tt = stockDList.get(m);
						tt.setData("STOCK_QTY", tt.getDouble("STOCK_QTY") * +1);
						result = InvStockDTool.getInstance().updateStockQty(tt,connection);  
						if (result.getErrCode() < 0) {
							err(result.getErrCode() + " " + result.getErrText());
							return result;
						}
					}
					
				}
			
			} 
			}
		}
  
		
		//������յ���      �޸�������״̬
		for(int i=0;i<packageMTable.getCount();i++){
			//�����޸�״̬��Ϊ�����⡱״̬��������״̬start
			TParm changeStatus = new TParm();
			changeStatus.setData("PACK_SEQ_NO",0,packageMTable.getData("PACK_SEQ_NO", i));
			changeStatus.setData("PACK_CODE",0,packageMTable.getData("PACK_CODE", i));
			changeStatus.setData("ONCE_USE_FLG",0,"Y"); 
			TParm statusParm = INVSterilizationHelpTool.getInstance().queryPackageStatus(changeStatus.getRow(0), connection);
			String statusStr =  statusParm.getData("STATUS").toString();
			TParm tpSD = new TParm();  
			tpSD.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE"));
			
			
			for (int j = 0; j < tpSD.getRow(0).getCount("PACK_CODE"); j++) {
				String packcode = tpSD.getRow(0).getValue("PACK_CODE",j); 
				TParm stockParm = new TParm (TJDODBTool.getInstance().select(queryPackDByPackCode(packcode)));
				for(int k=0; k<stockParm.getCount();k++){	//ÿ������  
				    String invCode = stockParm.getValue("INV_CODE", k);   
					TParm invBaseKind =   new TParm (TJDODBTool.getInstance().select(getInvKind(invCode))); 
					invKind = invBaseKind.getValue("INVKIND_CODE",0).toString();
					TParm changeStatusNew = new TParm();  
				if(!"C".equals(invKind)){ 
					changeStatusNew.setData("INV_CODE",invCode);    
					changeStatusNew.setData("PACK_SEQ_NO",packageMTable.getData("PACK_SEQ_NO", i));
					changeStatusNew.setData("PACK_CODE",packageMTable.getData("PACK_CODE", i));
					changeStatusNew.setData("ONCE_USE_FLG","Y");   
					if(statusStr.equals("[0]")){    
						//�ڿⲻ��ɾ����ϸ(ɾ��һ������ϸ)ͬ�ѳ���(��������Ʒ)
						//[[id,name],[,],['0',�ڿ�],['1',����],['2',�ѻ���],['3',������],['4',ά����]]
						INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(changeStatusNew, connection);//ɾ�����е���������ϸ
					}
				  }
				} 
			}
  
  
		   if(statusStr.equals("[2]")){
				INVSterilizationHelpTool.getInstance().updatePackageDisStatus(changeStatus.getRow(0), connection);
			}else if(statusStr.equals("[3]")){
				INVSterilizationHelpTool.getInstance().updatePackageSterStatus(changeStatus.getRow(0), connection);
			}
			//�����޸�״̬��Ϊ�����⡱״̬��������״̬end
			
			TParm insertValue = new TParm();
			insertValue.setData("RECYCLE_NO", 0, disTable.getData("RECYCLE_NO", 0) );
			insertValue.setData("ORG_CODE", 0, disTable.getData("ORG_CODE", 0) );
			insertValue.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE", i) );
			insertValue.setData("PACK_SEQ_NO", 0, packageMTable.getData("PACK_SEQ_NO", i) );
			insertValue.setData("QTY", 0, packageMTable.getData("QTY", i) );
			insertValue.setData("RECYCLE_DATE", 0, packageMTable.getData("RECYCLE_DATE", i) );
			insertValue.setData("RECYCLE_USER", 0, packageMTable.getData("RECYCLE_USER", i) );
			insertValue.setData("WASH_DATE", 0, packageMTable.getData("WASH_DATE", i) );
			insertValue.setData("WASH_USER", 0, packageMTable.getData("WASH_USER", i) );
			insertValue.setData("DISINFECTION_POTSEQ", 0, packageMTable.getData("DISINFECTION_POTSEQ", i) );
			insertValue.setData("DISINFECTION_PROGRAM", 0, packageMTable.getData("DISINFECTION_PROGRAM", i) );
			insertValue.setData("DISINFECTION_NUM", 0, packageMTable.getData("DISINFECTION_NUM", i) ); 
			insertValue.setData("DISINFECTION_OPERATIONSTAFF", 0, packageMTable.getData("DISINFECTION_USER", i) );
			insertValue.setData("DISINFECTION_DATE", 0, packageMTable.getData("DISINFECTION_DATE", i) );
			insertValue.setData("DISINFECTION_VALID_DATE", 0, packageMTable.getData("DISINFECTION_VALID_DATE", i) );
			insertValue.setData("DISINFECTION_USER", 0, packageMTable.getData("DISINFECTION_USER", i) );
			insertValue.setData("OPT_USER", 0, disTable.getData("OPT_USER", 0) );
			insertValue.setData("OPT_DATE", 0, disTable.getData("OPT_DATE", 0) );
			insertValue.setData("OPT_TERM", 0, disTable.getData("OPT_TERM", 0) );
			insertValue.setData("BARCODE", 0, packageMTable.getData("BARCODE", i) );
			insertValue.setData("STATUS", 0, "2" );	//״̬����Ϊ��������ϴ������  
			insertValue.setData("FINISH_FLG", 0, "N" );	//�õ����״̬Ϊ��δ��ɡ�
			
			TParm t = insertValue.getRow(0);
			System.out.println("t::::"+t);
			result = INVDisnfectionHelpTool.getInstance().insertDisnfection(t, connection);//������յ���
			
			result = INVDisnfectionHelpTool.getInstance().updatePackageStatus(t, connection);//�޸�������״̬

		}
//		System.out.println("obj---"+obj);
		//�޸��������
		if(obj!=null){
//			System.out.println("objobj--------"+obj);
//			System.out.println("obj2--------"+obj.toString());
			String tStr = obj.toString().substring(0, obj.toString().indexOf("}"));
			tStr = tStr.substring(tStr.indexOf("{")+1);
			if(tStr.length() == 0){
				return result;
			}
//			System.out.println("tStr---"+tStr);
			String [] detailInfo = tStr.split(", ");
//			System.out.println("detailInfo---"+detailInfo[0]);
			for(int i=0;i<detailInfo.length;i++){
				String [] str = detailInfo[i].split("=");
				String [] info = str[0].split("-");
				
//				System.out.println("str---"+str[0]);
//				System.out.println("info[0]---"+info[0]);
//				System.out.println("info---"+info);
				
				String packCode = info[0].trim();
				String packSeqNo = info[1].trim();
				String invCode = info[2].trim();
				String invSeqNo = info[3].trim();
				String recountTime = str[1].trim();
				
//				System.out.println(packCode+"---"+packSeqNo+"---"+invCode+"---"+invSeqNo+"---"+recountTime);
				
				TParm tp = new TParm();
				tp.setData("PACK_CODE",0, packCode);
				tp.setData("PACK_SEQ_NO",0, packSeqNo);
				tp.setData("INV_CODE",0, invCode);
				tp.setData("INVSEQ_NO",0, invSeqNo);
				tp.setData("RECOUNT_TIME",0, recountTime);
				tp.setData("ORG_CODE",0 ,disTable.getData("ORG_CODE", 0));
				TParm temp = tp.getRow(0);
				
				
//				System.out.println("temp---"+temp);
				//������е�������
				result = INVDisnfectionHelpTool.getInstance().updateRecountTime(temp, connection);
				
				
		//������2013		
//				//��ѯ��е������
//				TParm res = INVDisnfectionHelpTool.getInstance().queryBatchNo(temp, connection);
//				System.out.println("res---"+res);
//				tp.setData("BATCH_NO",0,res.getData("BATCH_NO", 0));
//				temp = tp.getRow(0);
//				System.out.println("temp---"+temp);
//				//����������
//				result = INVDisnfectionHelpTool.getInstance().updateStockMQTY(temp, connection);
//				//����ϸ����
//				result = INVDisnfectionHelpTool.getInstance().updateStockDQTY(temp, connection);
			}
		}
		return result;
	}
	
	
	
	
	private String queryPackDByPackCode(String packCode) {
		String sql = " SELECT INV_CODE, QTY, PACK_CODE FROM INV_PACKD " +
				" WHERE SEQMAN_FLG = 'N' " +
				" AND PACK_TYPE = '1' " + 
				" AND PACK_CODE =  '"+packCode+"' ";  
		return sql;
	}

	private String getInvKind(String invCode) {
		String sql = " SELECT INVKIND_CODE FROM INV_BASE " +
				" WHERE INV_CODE = '"+invCode+"'"; 
		return sql; 
	}

	//ѡ�������� ----------------------����
	private List<TParm> chooseBatchSeq(TParm parm,String orgCode){

		List<TParm> list = new ArrayList<TParm>();	//�����ϸ���ʿۿ���Ϣ
	
		//��Ҫ����������
        double qty = parm.getDouble("QTY");

        //�õ����д����ʵ��������
        TParm result = this.getBatchSeqInv(parm,orgCode);

        if (result == null || result.getErrCode() < 0)
            return list;
        //������������θ���
        int rowCount = result.getCount();
        //ѭ��ȡ����������
        for (int i = 0; i < rowCount; i++) {
            //�ó�һ��
            TParm oneRow = result.getRow(i);
            double stockQty = oneRow.getDouble("STOCK_QTY");
            //��������㹻(���Ȳ���Ϊ0)
            if (stockQty > 0) {
                if (stockQty >= qty) {
                    oneRow.setData("STOCK_QTY", qty);
                    //���ò���һ�еķ���
                    list.add(oneRow);
                    //���˾���
                    return list;
                }
                //�������
                if (stockQty < qty) {
                    //������ֵ
                    qty = qty - stockQty;
                    //���ò���һ�еķ���
                    list.add(oneRow);
                }
            }
        }
        return list;
	}  
	
	/** 
     * �õ�����Ź��������
     * @param parm TParm
     * @return TParm
     */
    private TParm getBatchSeqInv(TParm parm,String oCode) {
    	INV inv = new INV();
        //���Ҵ���
        String orgCode = oCode;
        //���ʴ���
        String invCode = parm.getValue("INV_CODE");
        //�õ����д����ʵ��������
        TParm result = inv.getAllStockQty(orgCode, invCode);
        
        return result;
    }
	
	
}
