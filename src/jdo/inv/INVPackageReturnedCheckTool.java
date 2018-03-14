package jdo.inv;

import java.util.Map;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
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
 * @author wangm	2013.11.22
 * @version 1.0
 */
public class INVPackageReturnedCheckTool extends TJDOTool{

	
	public static INVPackageReturnedCheckTool instanceObject;
	
	/**
     * ������
     */
    public INVPackageReturnedCheckTool() {
        setModuleName("inv\\INVPackageReturnedCheckModule.x");
        onInit();
    }

    /**
     * �õ�ʵ��
     *
     * @return IndPurPlanMTool
     */
    public static INVPackageReturnedCheckTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVPackageReturnedCheckTool();
        return instanceObject;
    }
    
    /**
     * �½����ư��˻�������������ϸ��
     * */
    
    public TParm insertPackageReturnedCheck(TParm parm, TConnection connection){
    
    	TParm result = this.update("insertPReturnM", parm.getParm("RETURNM").getRow(0), connection);
        if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
            
    	TParm tp = parm.getParm("RETURND");
    	for(int i=0;i<tp.getCount("PACK_CODE")-1;i++){
    		result = this.update("insertPReturnD", tp.getRow(i), connection);
            if (result.getErrCode() < 0){
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
    	}
    	

    	
    	return result;
    }
	
	
    /**
     * ��ѯ���ư��˻�������
     * */
	public TParm queryPackageReturnedCheckM(TParm parm){
		
		TParm result = this.query("queryPReturnM", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	
	
	/**
     * ��ѯ���ư��˻���ϸ��
     * */
	public TParm queryPackageReturnedCheckD(TParm parm){
		
		TParm result = this.query("queryPReturnD", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	

	public TParm deletePackageReturnedCheck(TParm parm, TConnection connection){
		

		
		TParm result = this.update("delPReturnD", parm, connection);
        if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
        result = this.update("delPReturnM", parm, connection);
        if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }

            
		return result;
	}
	
	
	
	
	
	public TParm updatePackageReturnedCheck(TParm parm, TConnection connection){
		
		TParm result = new TParm();
		result = this.update("delPReturnD",  parm.getParm("RETURNM"), connection);
        if (result.getErrCode() < 0){
        	System.out.println("����ʧ��:---------" + parm.getParm("RETURNM"));
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
		
        TParm tp = parm.getParm("RETURND");
        
		for(int i=0;i<tp.getCount("PACK_CODE")-1;i++){
    		result = this.update("insertPReturnD", tp.getRow(i), connection);
            if (result.getErrCode() < 0){
            	System.out.println("ϸ��ʧ��:---------" + tp);
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
    	}
    	
    	return result;
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public TParm confirmPackageReturnedCheck(TParm parm, TConnection connection){
		
		
		TParm result = new TParm();
		
		TParm p = new TParm();
		p.setData("PACKAGERETURNED_NO", parm.getValue("PACKAGERETURNED_NO", 0));
		p.setData("CONFIRM_USER", parm.getValue("CONFIRM_USER", 0));
		p.setData("CONFIRM_DATE", parm.getValue("CONFIRM_DATE", 0));
		result = this.update("updatePConfirmStatus", p, connection);
        if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
        
        result = this.update("updatePReturnDSec", p, connection);
        if (result.getErrCode() < 0){
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }

		//�ӻ����������
		for(int i=0;i<parm.getCount("PACK_CODE");i++){
    		result = this.update("updatePackstockMQty", parm.getRow(i), connection);
            if (result.getErrCode() < 0){
            	System.out.println("��һ������-------------------------------");
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
            
            result = this.query("queryPackD", parm.getRow(i));
            if (result.getErrCode() < 0){
            	System.out.println("�ڶ�������-------------------------------");
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
            
            for(int j=0;j<result.getCount("INV_CODE");j++){
            	result.setData("QTY", j, result.getDouble("QTY", j)*parm.getDouble("QTY", i));
            	TParm t = this.update("updatePackstockDQty", result.getRow(j));
            	if (t.getErrCode() < 0){
            		System.out.println("����������-------------------------------");
                	err(t.getErrCode() + " " + t.getErrText());
                	return t;
                }
            }
    	}
		
		TParm supD = new TParm();	//����µ�inv_sup_dispensed����
		TParm supDD = new TParm();	//����µ�inv_sup_dispensedd����
		
		//��ȥ�ѳ��⵽��λ��inv_sup_dispensed���
		for(int i=0;i<parm.getCount("PACK_CODE");i++){
			
			System.out.println("��ۿ���������Ϣ-------"+parm.getRow(i));  //���˻�������ϸ��

			//��ѯ������
			TParm dTP = this.query("querySupDispense", parm.getRow(i));
			if (dTP.getErrCode() < 0){
            	err(dTP.getErrCode() + " " + dTP.getErrText());
            	return dTP;
            }

			
			double qty = parm.getRow(i).getDouble("QTY");  //������������

			for(int j=0;j<dTP.getCount("INV_CODE");j++){
				
				if(qty == 0){
					break;
				}
				
				if(dTP.getRow(j).getDouble("ACTUAL_QTY")>qty){
					supD.addData("DISPENSE_NO", dTP.getRow(j).getValue("DISPENSE_NO"));
					supD.addData("INV_CODE", dTP.getRow(j).getValue("INV_CODE"));
					supD.addData("PACK_BATCH_NO", dTP.getRow(j).getValue("PACK_BATCH_NO"));
					supD.addData("QTY", qty*-1 );
					
//					for(int m=0;m<packTP.getCount("INV_CODE");m++){
//						supDD.addData("DISPENSE_NO", dTP.getRow(j).getValue("DISPENSE_NO"));
//						supDD.addData("PACk_CODE", dTP.getRow(j).getValue("INV_CODE"));
//						supDD.addData("PACK_BATCH_NO", dTP.getRow(j).getValue("PACK_BATCH_NO"));
//						supDD.addData("INV_CODE", packTP.getRow(m).getValue("INV_CODE"));
//						supDD.addData("QTY", (dTP.getRow(j).getDouble("ACTUAL_QTY")-qty)*packTP.getRow(m).getDouble("QTY")*-1 );
//					}
					break;
				}else if(dTP.getRow(j).getDouble("ACTUAL_QTY")<=qty){
					
					supD.addData("DISPENSE_NO", dTP.getRow(j).getValue("DISPENSE_NO"));
					supD.addData("INV_CODE", dTP.getRow(j).getValue("INV_CODE"));
					supD.addData("PACK_BATCH_NO", dTP.getRow(j).getValue("PACK_BATCH_NO"));
					supD.addData("QTY", (dTP.getRow(j).getDouble("ACTUAL_QTY"))*-1 );
					
//					for(int m=0;m<packTP.getCount("INV_CODE");m++){
//						supDD.addData("DISPENSE_NO", dTP.getRow(j).getValue("DISPENSE_NO"));
//						supDD.addData("PACk_CODE", dTP.getRow(j).getValue("INV_CODE"));
//						supDD.addData("PACK_BATCH_NO", dTP.getRow(j).getValue("PACK_BATCH_NO"));
//						supDD.addData("INV_CODE", packTP.getRow(m).getValue("INV_CODE"));
//						supDD.addData("QTY", dTP.getRow(j).getDouble("ACTUAL_QTY")*packTP.getRow(m).getDouble("QTY")*-1 );
//					}
					
					qty = qty - dTP.getRow(j).getDouble("ACTUAL_QTY");
				}
				
			}

			
		}
		
		
		//�۳�inv_sup_dispensedd������ϸ��
		for(int i=0;i<supD.getCount("INV_CODE");i++){
			
			double stockQty = 0;
			
			//��ѯ����������
			TParm packTP = this.query("queryPackDSec", supD.getRow(i));
	        if (packTP.getErrCode() < 0){
	        	err(packTP.getErrCode() + " " + packTP.getErrText());
	        	return packTP;
	        }
	        
	        for(int m=0;m<packTP.getCount("INV_CODE");m++){
	        	stockQty = supD.getRow(i).getDouble("QTY") * -1 * packTP.getRow(m).getDouble("QTY");
	        	
	        	TParm tp = supD.getRow(i);
	        	tp.setData("INV_CODE_SEC", packTP.getRow(m).getValue("INV_CODE"));
	        	
	        	System.out.println("��ѯdd��������"+tp);
	        	
	        	//��ѯ���ϸ�����   2013-12-27����
				TParm ddTP = this.query("querySupDispenseDD", tp);
				if (ddTP.getErrCode() < 0){
		        	err(ddTP.getErrCode() + " " + ddTP.getErrText());
		        	return ddTP;
		        }
				
				System.out.println("inv_sup_dispdd�����ݣ�"+ddTP);
	        	
	        	for(int n=0;n<ddTP.getCount("INV_CODE");n++){
	        		
	        		if(stockQty == 0){
	        			break;
	        		}
	        		
	        		if(stockQty>=ddTP.getDouble("QTY", n)){
						supDD.addData("DISPENSE_NO", ddTP.getRow(n).getValue("DISPENSE_NO"));
						supDD.addData("PACK_CODE", ddTP.getRow(n).getValue("PACK_CODE"));
						supDD.addData("PACK_BATCH_NO", ddTP.getRow(n).getValue("PACK_BATCH_NO"));
						supDD.addData("SEQ_NO", ddTP.getRow(n).getValue("SEQ_NO"));
						supDD.addData("INV_CODE", ddTP.getRow(n).getValue("INV_CODE"));
						supDD.addData("QTY", ddTP.getRow(n).getDouble("QTY")*-1 );
						
						stockQty = stockQty - ddTP.getRow(n).getDouble("QTY");
	        		}else if(stockQty<ddTP.getDouble("QTY", n)){
	        			supDD.addData("DISPENSE_NO", ddTP.getRow(n).getValue("DISPENSE_NO"));
						supDD.addData("PACK_CODE", ddTP.getRow(n).getValue("PACK_CODE"));
						supDD.addData("PACK_BATCH_NO", ddTP.getRow(n).getValue("PACK_BATCH_NO"));
						supDD.addData("SEQ_NO", ddTP.getRow(n).getValue("SEQ_NO"));
						supDD.addData("INV_CODE", ddTP.getRow(n).getValue("INV_CODE"));
						supDD.addData("QTY", stockQty*-1 );
						
						stockQty = 0;
	        		}
	        		
	        	}
	        }
		}
		
		System.out.println("�������ۿ���Ϣ-------"+supD);
		System.out.println("������ϸ��ۿ���Ϣ-------"+supDD);
		
		
		
		for(int i = 0;i<supD.getCount("INV_CODE");i++){
			TParm t = this.update("updateSupDispenseD", supD.getRow(i));
        	if (t.getErrCode() < 0){
        		System.out.println("��ϸ�����-------------------------------");
            	err(t.getErrCode() + " " + t.getErrText());
            	return t;
            }
		}
		
		for(int i = 0;i<supDD.getCount("INV_CODE");i++){
			TParm t = this.update("updateSupDispenseDD", supDD.getRow(i));
        	if (t.getErrCode() < 0){
        		System.out.println("��ϸϸ�����-------------------------------");
            	err(t.getErrCode() + " " + t.getErrText());
            	return t;
            }
		}
		

    	
    	return result;
		
	}
	
	
}
