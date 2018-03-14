package action.dev;

import java.util.List;

import jdo.dev.DEVMaintenanceMasterTool;
import jdo.dev.DEVMaintenanceRecordTool;
import jdo.dev.DEVMaintenanceTool;
import jdo.dev.DEVMeasureDicTool;
import jdo.dev.DevCheckTool;
import jdo.dev.DevInStorageTool;
import jdo.dev.DevOutRequestDTool;
import jdo.dev.DevOutRequestMTool;
import jdo.dev.DevOutStorageTool;
import jdo.dev.DevPurChaseTool;
import jdo.dev.DevRequestTool;
import jdo.dev.DevTrackTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: �豸ACTION</p>  
 *
 * <p>Description:�豸ACTION</p>
 *  
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: javahis</p>  
 *
 * @author  fux  
 * @version 1.0
 */  
public class DevAction extends TAction {
	/**
	 * �豸�빺����
	 * 
	 * @param parm
	 *            TParm   
	 * @return TParm    DEV_PURCHASEM
	 */
	public TParm saveDevPurChase(TParm parm) {
		TParm result = new TParm(); 
		TConnection connection = getConnection();
		result = DevPurChaseTool.getInstance().saveDevPurChase(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		return result;
	}

	/**
	 * �豸�빺ϸ����
	 * 
	 * @param parm
	 *            TParm   
	 * @return TParm    DEV_PURCHASED
	 */  
	public TParm saveDevPurChased(TParm parm) {
		TParm result = new TParm(); 
		TConnection connection = getConnection();
		result = DevPurChaseTool.getInstance().saveDevPurChase(parm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		return result;
	}
	/**
	 * �豸��Ᵽ��
	 * 
	 * @param parm  
	 *            TParm  
	 * @return TParm 
	 */  
	public TParm generateInStorageReceipt(TParm parm) {
		TParm result = new TParm();
		                           
		TConnection connection = getConnection(); 
		
		if(parm.getValue("CHECK_IN").equals("N")){	
			// ������� %
			result = DevInStorageTool.getInstance().insertDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
//		System.out.println("�������result"+result);
//		System.out.println("// ������� %");
			if (result.getErrCode() < 0) {
				//connection.rollback();
				err(result.getErrText());
				connection.close();
				return result;
			}
			// ���ϸ��  %
			TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");  
//		System.out.println("inWareHouseD"+inWareHouseD);
			for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	  System.out.println("// ���ϸ��  %");
				result = DevInStorageTool.getInstance().insertDevInwarehouseD(inWareHouseD.getRow(i), connection);
				//		System.out.println("���ϸ�� result"+result); 
				
				if (result.getErrCode() < 0) {
					//connection.rollback();
					err(result.getErrText());
					connection.close();  
					return result;       
				}    
			} 
			// �����ϸ�� % ORGIN_CODE
			TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
//	    System.out.println("inWareHouseDD"+inWareHouseDD);
			for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	System.out.println("// �����ϸ�� % ORGIN_CODE");
//	    	if(inWareHouseDD.getBoolean(""))
				result = DevInStorageTool.getInstance().insertDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
//						System.out.println("�����ϸ��result"+result);
				if (result.getErrCode() < 0) { 
					//connection.rollback();
					err(result.getErrText());
					connection.close();
					return result;       
				}   
			}
//			System.out.println("// �����ϸ�� %");
			result = DevInStorageTool.getInstance().updateDevRecMFinalFlg(parm.getParm("DEV_INWAREHOUSEM"), connection);
//			System.out.println("// ............�����ϸ�� %");
//			System.out.println(parm.getParm("DEV_INWAREHOUSEM"));
//			System.out.println("parm:"+parm);
//			System.out.println("result:"+result);
			if (result.getErrCode() < 0) {
				//connection.rollback();
	        	err(result.getErrText());
				connection.close(); 
				return result; 
			}
//			System.out.println("//111111111111�����ϸ�� %");
		}else if(parm.getValue("CHECK_IN").equals("Y")){
	   	    
			// �������(��������)  %
//			System.out.println("����");
		    TParm stockM = parm.getParm("DEV_STOCKM");
		    for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
				result = DevInStorageTool.getInstance().insertOrUpdateStockM(stockM.getRow(i), connection);
		//		System.out.println(" �������result"+result);
				if (result.getErrCode() < 0) { 
					//connection.rollback();
					err(result.getErrText());
					connection.close();
					return result;                            
				}   
		    }
//		    System.out.println("����");
	//	    System.out.println("// �������(��������)  %");
			// ���ϸ��  %
	        TParm stockD = parm.getParm("DEV_STOCKD");
	        for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
				result = DevInStorageTool.getInstance().insertOrUpdateStockD(stockD.getRow(i), connection);
		//		System.out.println(" ���ϸ��result"+result);
				if (result.getErrCode() < 0) {
					//connection.rollback();
					err(result.getErrText());
					connection.close(); 
					return result;    
				}              
	        }
//	        System.out.println("ϸ��");
//	        System.out.println("// ���ϸ��  %");
			// �����ϸ��--��Ź����豸  % 
	        TParm stockDD = parm.getParm("DEV_STOCKDD");
	        for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) {  
				result = DevInStorageTool.getInstance().insertOrUpdateStockDD(stockDD.getRow(i), connection);
		//		System.out.println(" �����ϸ��result"+result);
				if (result.getErrCode() < 0) {
					//connection.rollback();
					err(result.getErrText());
					connection.close(); 
					return result; 
				}          
	        } 
//	        System.out.println("��ϸ��");
	      //����DEV_INWAREHOUSEM��CHECK_FLG���������ʶ
			result = DevInStorageTool.getInstance().updateDevInwCheckFlg(parm.getParm("DEV_INWAREHOUSEM"), connection);
			if (result.getErrCode() < 0) {
				//connection.rollback();
				err(result.getErrText());
				connection.close();
				return result;
			}
//			 System.out.println("CHECK_FLG");
			
			//����DEV_MAINTENANCE_DATE
			result = DevInStorageTool.getInstance().insertDevMtnDate(parm.getParm("DEV_MTN_DATE"), connection);
			if (result.getErrCode() < 0) {
				//connection.rollback();
				err(result.getErrText());
				connection.close();
				return result;
			}
	        
		}
//        System.out.println("// �����ϸ��--��Ź����豸  % ");
		connection.commit(); 
		connection.close();
		return result;
	}

	
	/**
	 * �豸��Ᵽ��
	 * 
	 * @param parm  
	 *            TParm   
	 * @return TParm 
	 */  
	public TParm generateOutReqInStorageReceipt(TParm parm) {
		TParm result = new TParm();
		                           
		TConnection connection = getConnection(); 
		// ������� %
		result = DevInStorageTool.getInstance().insertDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
//		System.out.println("�������result"+result);
//		System.out.println("// ������� %");
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result; 
		}
		// ���ϸ��  %
		TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");  
//		System.out.println("inWareHouseD"+inWareHouseD);
	    for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	  System.out.println("// ���ϸ��  %");
		result = DevInStorageTool.getInstance().insertDevInwarehouseD(inWareHouseD.getRow(i), connection);
//		System.out.println("���ϸ�� result"+result); 
		
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();  
			return result;       
		}     
		  }  
		// �����ϸ�� % ORGIN_CODE
	    TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
//	    System.out.println("inWareHouseDD"+inWareHouseDD);
	    for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	System.out.println("// �����ϸ�� % ORGIN_CODE");
//	    	if(inWareHouseDD.getBoolean(""))
		result = DevInStorageTool.getInstance().insertDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
//		System.out.println("�����ϸ��result"+result);
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;       
		}   
	    }
	    
	   	    
		// �������(��������)  %
	    TParm stockM = parm.getParm("DEV_STOCKM_IN");//20150602 wangjc modify
	    for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertStockM(stockM.getRow(i), connection);
//		System.out.println(" �������result"+result);
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;                            }   
	    }
//	    System.out.println("// �������(��������)  %");
		// ���ϸ��  %
        TParm stockD = parm.getParm("DEV_STOCKD_IN");//20150602 wangjc modify
        for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertStockD(stockD.getRow(i), connection);
//		System.out.println(" ���ϸ��result"+result);
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close(); 
			return result;    
		            }              
        }
//        System.out.println("// ���ϸ��  %");
		// �����ϸ��--��Ź����豸  % 
        TParm stockDD = parm.getParm("DEV_STOCKDD_IN");//20150602 wangjc modify
        for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) {  
		result = DevInStorageTool.getInstance().UpdateStockDD(stockDD.getRow(i), connection);
//		System.out.println(" �����ϸ��result"+result);
		if (result.getErrCode() < 0) {
			//connection.rollback(); 
			connection.close(); 
			return result; 
		   }          
        } 
//        System.out.println("// �����ϸ��--��Ź����豸  % ");
        connection.commit();
        connection.close();
		return result;
	}

	
	
	
	
	
	
	
	
	
	
	/**
	 * �豸��Ᵽ��(update)(���   �����)
	 * 
	 * @param parm 
	 *            TParm
	 * @return TParm     
	 */ 
	public TParm updateInStorageReceipt(TParm parm) {
		TParm result = new TParm();

		TConnection connection = getConnection();
			// �������
			result = DevInStorageTool.getInstance().updateDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
			if (result.getErrCode() < 0) {
				//connection.rollback();
				connection.close();
				return result;
			}
			
			
			// ���ϸ�� 
			TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");
			for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
				result = DevInStorageTool.getInstance().updateDevInwarehouseD(inWareHouseD.getRow(i), connection);
				if (result.getErrCode() < 0) {
					//connection.rollback();
					connection.close();
					return result;       
				}       
			} 
			// �����ϸ��
			TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
			for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
				result = DevInStorageTool.getInstance().updateDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
				if (result.getErrCode() < 0) {
					//connection.rollback();
					connection.close();
					return result;       
				}   
			}
			// �������(��������)  �Ƿ���£�
			TParm stockM = parm.getParm("DEV_STOCKM");
			for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
				result = DevInStorageTool.getInstance().insertOrUpdateStockM(stockM.getRow(i), connection);
				if (result.getErrCode() < 0) {
					//connection.rollback(); 
					connection.close();
					return result;
				}   
			}
			
			// ���ϸ��  ���¿��
			TParm stockD = parm.getParm("DEV_STOCKD"); 
			for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
				result = DevInStorageTool.getInstance().insertOrUpdateStockD(stockD.getRow(i), connection);
				if (result.getErrCode() < 0) {
					//connection.rollback(); 
					connection.close();
					return result;   
				}              
			} 
			// �����ϸ��--��Ź����豸������
			TParm stockDD = parm.getParm("DEV_STOCKDD"); 
			for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) { 
				result = DevInStorageTool.getInstance().insertOrUpdateStockDD(stockDD.getRow(i), connection);
				if (result.getErrCode() < 0) {
					//connection.rollback();
					connection.close();
					return result;
				}         
			}
			
		connection.commit();
		connection.close();
		return result;
	}	
	/**
	 * �豸���챣��(����)
	 * @param parm
	 * @return 
	 */
	public TParm InsertRequest(TParm parm) {  
//		System.out.println("STARTTTTTTT--INSERT"); 
		TParm result = new TParm(); 
		TConnection connection = getConnection(); 
		// ������������ 
		result = DevOutRequestMTool.getInstance().createNewRequestM(parm.getParm("DEV_REQUESTMM"), connection);
//		System.out.println("��result"+result); 
		if (result.getErrCode() < 0) {
			connection.close();  
			return result;   
		}    
		// ����������ϸ�� 
		result = DevOutRequestDTool.getInstance().createNewRequestD(parm.getParm("DEV_REQUESTDD"), connection);
//		System.out.println("ϸresult"+result);
		if (result.getErrCode() < 0) { 
			connection.close();   
			return result;   
		}   
		
		connection.commit();
		connection.close(); 
		return result;

	}
	/**
	 * �豸���챣��(����)
	 * @param parm
	 * @return
	 */
	public TParm UpdateRequest(TParm parm) {
//		System.out.println("STARTTTTTTT----UPDATE"); 
		TParm result = new TParm();
		TConnection connection = getConnection(); 
		// ������������    
		result = DevOutRequestMTool.getInstance().updateRequestM(parm.getParm("DEV_REQUESTMM"), connection);
		if (result.getErrCode() < 0) {  
			connection.close();   
			return result;    
		}
		// ����������ϸ��  
		result = DevOutRequestDTool.getInstance().updateRequestD(parm.getParm("DEV_REQUESTDD"), connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}   
//	    // ����������ϸ��״̬
//	    result = DevOutRequestDTool.getInstance().updateRequestDDFlg(parm.getParm("DEV_REQUESTDD"), connection);
//			if (result.getErrCode() < 0) {
//				connection.close();
//				return result;
//				}
		connection.commit();
		connection.close();
		return result;
	}
	/**
	 * �豸���Ᵽ��(new)
	 * @param parm
	 * @return
	 */
	public TParm generateExStorageReceipt(TParm parm) {
		TParm result = new TParm();
		String check_in = parm.getValue("CHECK_IN");
		TConnection connection = getConnection();
		if(check_in.equals("N")){
			
			// ��������
			result = DevOutStorageTool.getInstance().insertExStorgeM(parm.getParm("DEV_EXWAREHOUSEM"), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			
			TParm ParmD = parm.getParm("DEV_EXWAREHOUSED"); 
//		System.out.println("������ϸ��ParmD"+ParmD);
			// ������ϸ��
			result = DevOutStorageTool.getInstance().insertExStorgeD(ParmD, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;  
			}     
			TParm ParmDD = parm.getParm("DEV_EXWAREHOUSEDD");
//		System.out.println("���������ϸ��ParmDD"+ParmDD); 
			// ���������ϸ��
			result = DevOutStorageTool.getInstance().insertExStorgeDD(ParmDD, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;  
			} 
		}else if(check_in.equals("Y")){
			
			//���³���������˳���״̬
			result = DevInStorageTool.getInstance().updateDevExwM(parm.getParm("DEV_EXWAREHOUSEM"), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			
			// ���³�����ϸ����˳���״̬
			result = DevInStorageTool.getInstance().updateDevExwD(parm.getParm("DEV_EXWAREHOUSED"), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;  
			}
			
			// ���������ϸ���Ͳ���DD������(δ���) 
			TParm StockMParm = parm.getParm("DEV_STOCKM");  
			result = DevOutStorageTool.getInstance().deductStockM(StockMParm, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;  
			}  
			//fux need modify
			TParm StockDParm = parm.getParm("DEV_STOCKD"); 
			result = DevOutStorageTool.getInstance().deductStockD(StockDParm, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;  
			}  
			//����
			TParm StockDDParm = parm.getParm("DEV_STOCKDD");  
			result = DevOutStorageTool.getInstance().updateStockdd(StockDDParm, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;  
			}  
////		result = DevOutStorageTool.getInstance().updateStockMD(tmepParm, connection);
//		TParm requestMParm = parm.getParm("DEV_REQUESTM"); 
//		TParm requestDParm = parm.getParm("DEV_REQUESTD"); 
//		//������������
//		result = DevOutStorageTool.getInstance().UpdateRequsetMFinal(requestMParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;    
//		}  
//		//��������ϸ��
//		result = DevOutStorageTool.getInstance().UpdateRequsetDFate(requestDParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		}  
			
			//20150602 wangjc add
			// ������� %
			result = DevInStorageTool.getInstance().insertDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
//		System.out.println("�������result"+result);
//		System.out.println("// ������� %");
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result; 
			}
			// ���ϸ��  %
			TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");  
//		System.out.println("inWareHouseD"+inWareHouseD);
			for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	  System.out.println("// ���ϸ��  %");
				result = DevInStorageTool.getInstance().insertDevInwarehouseD(inWareHouseD.getRow(i), connection);
//		System.out.println("���ϸ�� result"+result); 
				
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();  
					return result;       
				}     
			}  
			// �����ϸ�� % ORGIN_CODE
			TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
//	    System.out.println("inWareHouseDD"+inWareHouseDD);
			for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	System.out.println("// �����ϸ�� % ORGIN_CODE");
//	    	if(inWareHouseDD.getBoolean(""))
				result = DevInStorageTool.getInstance().insertDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
//		System.out.println("�����ϸ��result"+result);
				if (result.getErrCode() < 0) { 
					connection.rollback();
					connection.close();
					return result;       
				}   
			}
			
			
//		// �������(������(����;������))  %
			TParm stockM = parm.getParm("DEV_STOCKM_IN");
//	    System.out.println("stockM>>>"+stockM);
			for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
				result = DevInStorageTool.getInstance().updateStock(stockM.getRow(i), connection);
				if (result.getErrCode() < 0) {     
					connection.rollback();  
					connection.close();
					return result;
				}   
			}  
//	    System.out.println("// �������(��������)  %");
			// ���ϸ��  %    
			TParm stockD = parm.getParm("DEV_STOCKD_IN");
//        System.out.println("stockD>>>"+stockD);
			for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
				//add or insert
				result = DevInStorageTool.getInstance().ReqInsertorUpdateStockD(stockD.getRow(i), connection);
				if (result.getErrCode() < 0) {
					connection.rollback();   
					connection.close(); 
					return result;    
				}                
			}
//		 �����ϸ��--��Ź����豸  %
//        System.out.println("�����ϸ��--��Ź����豸");
			TParm stockDD = parm.getParm("DEV_STOCKDD_IN");
//        System.out.println("stockDD>>>>"+stockDD);
			for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) {  
				result = DevInStorageTool.getInstance().UpdateStockDD(stockDD.getRow(i), connection);
				if (result.getErrCode() < 0) {
					connection.rollback();   
					connection.close(); 
					return result; 
				}          
			}
//        System.out.println("// �����ϸ��--��Ź����豸  % ");
			//20150602 WANGJC ADD
		}

		connection.commit();  
		connection.close(); 
		return result;
	}
	/**
	 * �豸���Ᵽ��(update)
	 * @param parm
	 * @return
	 */
	public TParm updateExStorageReceipt(TParm parm) {
		TParm result = new TParm();
		
		TConnection connection = getConnection();
		// ��������
		result = DevOutStorageTool.getInstance().insertExStorgeM(parm.getParm("DEV_EXWAREHOUSEM"), connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		
		TParm tmepParm = parm.getParm("DEV_EXWAREHOUSED");
		// ������ϸ��
		result = DevOutStorageTool.getInstance().insertExStorgeD(tmepParm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// �������������(δ���)
//		result = DevOutStorageTool.getInstance().deductStockM(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		}  
//		// ��Ź��� ������ϸ������������Ź��� ����/������������
//		result = DevOutStorageTool.getInstance().updateStockMD(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		} 
		// �޸�DEV_RFIDBASE������������Ϣ
//		result = DevOutStorageTool.getInstance().updateRFIDBase(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;
//		}  
		  
		connection.commit();
		connection.close();
		return result;
	}
	
	
	/**
	 * �豸׷�� ����
	 * @param parm
	 * @return
	 */
	public TParm onTrackOut(TParm parm) {
		TParm result = new TParm();
//		TConnection connection = getConnection(); 
		// �����豸׷�ٱ�
		result = DevTrackTool.getInstance().insertDevTrackInf(parm.getParm("OUT"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// ����DEV_RFIDBASE�� ״̬Ϊ����
		result = DevTrackTool.getInstance().updateDevRFIDBaseOnpass(parm.getParm("OUT"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
	//	connection.commit();
		return result;
	}
	
	
	/**
	 * �豸׷�� ���
	 * @param parm
	 * @return
	 */
	public TParm onTrackIn(TParm parm) {
		TParm result = new TParm();
	//	TConnection connection = getConnection();
//		System.out.println("1:" + parm);
		// �����豸׷�ٱ�
		result = DevTrackTool.getInstance().insertDevTrackInf(parm.getParm("IN"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
//		System.out.println("2:" + parm);
		// ����DEV_RFIDBASE�� ״̬Ϊ����
		result = DevTrackTool.getInstance().updateDevRFIDBaseNormal(parm.getParm("IN"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
//		connection.commit();
		return result;
	}
	
	
	/**
	 * �����̵������¿���
	 * @param parm
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TParm updateStockByCheck(TParm parm) {
		TParm result = new TParm();
		TConnection connection = getConnection();
		List<TParm> parmList = (List<TParm>)parm.getData("update");
		result = DevCheckTool.getInstance().updateStockByCheck(parmList, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		connection.commit();
		connection.close();
		return result;
	}
	
	
	/**
     * �豸�빺����
     * @param parm TParm
     * @return TParm
     */
    public TParm saveDevRequest(TParm parm){ 
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        result = DevRequestTool.getInstance().saveDevRequest(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        return result;
    }
    
    /*------------------�豸����-------------------------------------------------------*/
    /**
     * �����������
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertMaintenanceMaster(TParm parm){ 
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        result = DEVMaintenanceMasterTool.getInstance().onInsertMaintenanceMaster(parm.getParm("DEV_MAINTENANCEM").getRow(0),connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        TParm devMtnDate = parm.getParm("DEV_MAINTENANCE_DATE");
		if(devMtnDate.getCount()>0){
			for(int i=0;i<devMtnDate.getCount();i++){
				result = DEVMaintenanceMasterTool.getInstance().onInsertDevMtnDate(devMtnDate.getRow(i),connection);
		        if(result.getErrCode()<0){
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
     * ���¿��������������ϸ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertOrUpdateMaintenanceMaster(TParm parm){ 
//    	System.out.println("------------------sssssssssssssssssssssssssssssssss------------------------------");
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        result = DEVMaintenanceMasterTool.getInstance().onUpdateMaintenanceMaster(parm.getRow(0),connection);
        if(result.getErrCode()<0){
    		connection.close();
    		return result;
    	}
//        System.out.println("count:"+parm.getCount());
        for(int i=0;i<parm.getCount();i++){
//        	System.out.println("rowParm:"+parm.getRow(i));
        	if(parm.getValue("INSERT_FLG", i).equals("Y")){
        		result = DEVMaintenanceMasterTool.getInstance().onInsertMaintenanceMasterDetail(parm.getRow(i),connection);
        	}else if(parm.getValue("INSERT_FLG", i).equals("N")){
        		result = DEVMaintenanceMasterTool.getInstance().onUpdateMaintenanceMasterDetail(parm.getRow(i),connection);
        	}
        	if(result.getErrCode()<0){
        		connection.close();
        		return result;
        	}
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * �����������
     * @param parm TParm
     * @return TParm
     */
    public TParm onDeleteMaintenanceMaster(TParm parm){ 
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        if(parm.getValue("MTN_DETAIL_CODE").equals("") 
        		|| parm.getValue("MTN_DETAIL_CODE") == null){
        	result = DEVMaintenanceMasterTool.getInstance().onDeleteMaintenanceMaster(parm,connection);
        	if(result.getErrCode()<0){
                connection.close();
                return result;
            }
        	result = DEVMaintenanceMasterTool.getInstance().onDeleteAllMaintenanceMasterDetail(parm,connection);
        }else{
        	result = DEVMaintenanceMasterTool.getInstance().onDeleteOneMaintenanceMasterDetail(parm,connection);
        }
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * �����豸�����ֵ�
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertMeasuremDic(TParm parm){ 
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        result = DEVMeasureDicTool.getInstance().onInsertMeasuremDic(parm.getParm("DEV_MEASURE").getRow(0),connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        TParm devMtnDate = parm.getParm("DEV_MAINTENANCE_DATE");
		if(devMtnDate.getCount()>0){
			for(int i=0;i<devMtnDate.getCount();i++){
				result = DEVMeasureDicTool.getInstance().onInsertDevMtnDate(devMtnDate.getRow(i),connection);
		        if(result.getErrCode()<0){
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
     * �����豸�����ֵ�
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateMeasuremDic(TParm parm){
//    	System.out.println("actionParm:"+parm);
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        result = DEVMeasureDicTool.getInstance().onUpdateMeasuremDic(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * ɾ���豸�����ֵ�
     * @param parm TParm
     * @return TParm
     */
    public TParm onDeleteMeasuremDic(TParm parm){
//    	System.out.println("actionParm:"+parm);
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        result = DEVMeasureDicTool.getInstance().onDeleteMeasuremDic(parm,connection);
        if(result.getErrCode()<0){
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * �����豸ά��/����ʱ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateMaintenance(TParm parm){
//    	System.out.println("actionParm:"+parm);
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        for(int i=0;i<parm.getCount();i++){
        	result = DEVMaintenanceTool.getInstance().onUpdateMaintenance(parm.getRow(i),connection);
        	if(result.getErrCode()<0){
        		connection.close();
        		return result;
        	}
        }
        connection.commit();
        connection.close();
        return result;
    }
    
    /**
     * �����豸ά��/������¼
     * �����´�ά��/����ʱ��
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertMaintenanceRecord(TParm parm){ 
        TParm result = new TParm(); 
        TConnection connection = getConnection();
        for(int i=0;i<parm.getCount("DEV_CODE");i++){
        	result = DEVMaintenanceRecordTool.getInstance().onInsertMaintenanceRecord(parm.getRow(i),connection);
        	if(result.getErrCode()<0){
        		connection.close();
        		return result;
        	}
        	result = DEVMaintenanceRecordTool.getInstance().onUpdateMaintenanceDate(parm.getRow(i),connection);
        	if(result.getErrCode()<0){
        		connection.close();
        		return result;
        	}
        }
        connection.commit();
        connection.close();
        return result;
    }
} 
