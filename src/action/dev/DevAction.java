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
 * <p>Title: 设备ACTION</p>  
 *
 * <p>Description:设备ACTION</p>
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
	 * 设备请购保存
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
	 * 设备请购细表保存
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
	 * 设备入库保存
	 * 
	 * @param parm  
	 *            TParm  
	 * @return TParm 
	 */  
	public TParm generateInStorageReceipt(TParm parm) {
		TParm result = new TParm();
		                           
		TConnection connection = getConnection(); 
		
		if(parm.getValue("CHECK_IN").equals("N")){	
			// 入库主表 %
			result = DevInStorageTool.getInstance().insertDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
//		System.out.println("入库主表result"+result);
//		System.out.println("// 入库主表 %");
			if (result.getErrCode() < 0) {
				//connection.rollback();
				err(result.getErrText());
				connection.close();
				return result;
			}
			// 入库细表  %
			TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");  
//		System.out.println("inWareHouseD"+inWareHouseD);
			for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	  System.out.println("// 入库细表  %");
				result = DevInStorageTool.getInstance().insertDevInwarehouseD(inWareHouseD.getRow(i), connection);
				//		System.out.println("入库细表 result"+result); 
				
				if (result.getErrCode() < 0) {
					//connection.rollback();
					err(result.getErrText());
					connection.close();  
					return result;       
				}    
			} 
			// 入库明细表 % ORGIN_CODE
			TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
//	    System.out.println("inWareHouseDD"+inWareHouseDD);
			for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	System.out.println("// 入库明细表 % ORGIN_CODE");
//	    	if(inWareHouseDD.getBoolean(""))
				result = DevInStorageTool.getInstance().insertDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
//						System.out.println("入库明细表result"+result);
				if (result.getErrCode() < 0) { 
					//connection.rollback();
					err(result.getErrText());
					connection.close();
					return result;       
				}   
			}
//			System.out.println("// 入库明细表 %");
			result = DevInStorageTool.getInstance().updateDevRecMFinalFlg(parm.getParm("DEV_INWAREHOUSEM"), connection);
//			System.out.println("// ............入库明细表 %");
//			System.out.println(parm.getParm("DEV_INWAREHOUSEM"));
//			System.out.println("parm:"+parm);
//			System.out.println("result:"+result);
			if (result.getErrCode() < 0) {
				//connection.rollback();
	        	err(result.getErrText());
				connection.close(); 
				return result; 
			}
//			System.out.println("//111111111111入库明细表 %");
		}else if(parm.getValue("CHECK_IN").equals("Y")){
	   	    
			// 库存主表(插入或更新)  %
//			System.out.println("更新");
		    TParm stockM = parm.getParm("DEV_STOCKM");
		    for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
				result = DevInStorageTool.getInstance().insertOrUpdateStockM(stockM.getRow(i), connection);
		//		System.out.println(" 库存主表result"+result);
				if (result.getErrCode() < 0) { 
					//connection.rollback();
					err(result.getErrText());
					connection.close();
					return result;                            
				}   
		    }
//		    System.out.println("主表");
	//	    System.out.println("// 库存主表(插入或更新)  %");
			// 库存细表  %
	        TParm stockD = parm.getParm("DEV_STOCKD");
	        for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
				result = DevInStorageTool.getInstance().insertOrUpdateStockD(stockD.getRow(i), connection);
		//		System.out.println(" 库存细表result"+result);
				if (result.getErrCode() < 0) {
					//connection.rollback();
					err(result.getErrText());
					connection.close(); 
					return result;    
				}              
	        }
//	        System.out.println("细表");
//	        System.out.println("// 库存细表  %");
			// 库存明细表--序号管理设备  % 
	        TParm stockDD = parm.getParm("DEV_STOCKDD");
	        for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) {  
				result = DevInStorageTool.getInstance().insertOrUpdateStockDD(stockDD.getRow(i), connection);
		//		System.out.println(" 库存明细表result"+result);
				if (result.getErrCode() < 0) {
					//connection.rollback();
					err(result.getErrText());
					connection.close(); 
					return result; 
				}          
	        } 
//	        System.out.println("明细表");
	      //更新DEV_INWAREHOUSEM的CHECK_FLG，审核入库标识
			result = DevInStorageTool.getInstance().updateDevInwCheckFlg(parm.getParm("DEV_INWAREHOUSEM"), connection);
			if (result.getErrCode() < 0) {
				//connection.rollback();
				err(result.getErrText());
				connection.close();
				return result;
			}
//			 System.out.println("CHECK_FLG");
			
			//插入DEV_MAINTENANCE_DATE
			result = DevInStorageTool.getInstance().insertDevMtnDate(parm.getParm("DEV_MTN_DATE"), connection);
			if (result.getErrCode() < 0) {
				//connection.rollback();
				err(result.getErrText());
				connection.close();
				return result;
			}
	        
		}
//        System.out.println("// 库存明细表--序号管理设备  % ");
		connection.commit(); 
		connection.close();
		return result;
	}

	
	/**
	 * 设备入库保存
	 * 
	 * @param parm  
	 *            TParm   
	 * @return TParm 
	 */  
	public TParm generateOutReqInStorageReceipt(TParm parm) {
		TParm result = new TParm();
		                           
		TConnection connection = getConnection(); 
		// 入库主表 %
		result = DevInStorageTool.getInstance().insertDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
//		System.out.println("入库主表result"+result);
//		System.out.println("// 入库主表 %");
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();
			return result; 
		}
		// 入库细表  %
		TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");  
//		System.out.println("inWareHouseD"+inWareHouseD);
	    for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	  System.out.println("// 入库细表  %");
		result = DevInStorageTool.getInstance().insertDevInwarehouseD(inWareHouseD.getRow(i), connection);
//		System.out.println("入库细表 result"+result); 
		
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close();  
			return result;       
		}     
		  }  
		// 入库明细表 % ORGIN_CODE
	    TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
//	    System.out.println("inWareHouseDD"+inWareHouseDD);
	    for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	System.out.println("// 入库明细表 % ORGIN_CODE");
//	    	if(inWareHouseDD.getBoolean(""))
		result = DevInStorageTool.getInstance().insertDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
//		System.out.println("入库明细表result"+result);
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;       
		}   
	    }
	    
	   	    
		// 库存主表(插入或更新)  %
	    TParm stockM = parm.getParm("DEV_STOCKM_IN");//20150602 wangjc modify
	    for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertStockM(stockM.getRow(i), connection);
//		System.out.println(" 库存主表result"+result);
		if (result.getErrCode() < 0) { 
			//connection.rollback();
			connection.close();
			return result;                            }   
	    }
//	    System.out.println("// 库存主表(插入或更新)  %");
		// 库存细表  %
        TParm stockD = parm.getParm("DEV_STOCKD_IN");//20150602 wangjc modify
        for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
		result = DevInStorageTool.getInstance().insertStockD(stockD.getRow(i), connection);
//		System.out.println(" 库存细表result"+result);
		if (result.getErrCode() < 0) {
			//connection.rollback();
			connection.close(); 
			return result;    
		            }              
        }
//        System.out.println("// 库存细表  %");
		// 库存明细表--序号管理设备  % 
        TParm stockDD = parm.getParm("DEV_STOCKDD_IN");//20150602 wangjc modify
        for (int i = 0; i < stockDD.getCount("DEV_CODE"); i++) {  
		result = DevInStorageTool.getInstance().UpdateStockDD(stockDD.getRow(i), connection);
//		System.out.println(" 库存明细表result"+result);
		if (result.getErrCode() < 0) {
			//connection.rollback(); 
			connection.close(); 
			return result; 
		   }          
        } 
//        System.out.println("// 库存明细表--序号管理设备  % ");
        connection.commit();
        connection.close();
		return result;
	}

	
	
	
	
	
	
	
	
	
	
	/**
	 * 设备入库保存(update)(序号   非序号)
	 * 
	 * @param parm 
	 *            TParm
	 * @return TParm     
	 */ 
	public TParm updateInStorageReceipt(TParm parm) {
		TParm result = new TParm();

		TConnection connection = getConnection();
			// 入库主表
			result = DevInStorageTool.getInstance().updateDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
			if (result.getErrCode() < 0) {
				//connection.rollback();
				connection.close();
				return result;
			}
			
			
			// 入库细表 
			TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");
			for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
				result = DevInStorageTool.getInstance().updateDevInwarehouseD(inWareHouseD.getRow(i), connection);
				if (result.getErrCode() < 0) {
					//connection.rollback();
					connection.close();
					return result;       
				}       
			} 
			// 入库明细表
			TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
			for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
				result = DevInStorageTool.getInstance().updateDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
				if (result.getErrCode() < 0) {
					//connection.rollback();
					connection.close();
					return result;       
				}   
			}
			// 库存主表(插入或更新)  是否更新？
			TParm stockM = parm.getParm("DEV_STOCKM");
			for (int i = 0; i < stockM.getCount("DEV_CODE"); i++) {
				result = DevInStorageTool.getInstance().insertOrUpdateStockM(stockM.getRow(i), connection);
				if (result.getErrCode() < 0) {
					//connection.rollback(); 
					connection.close();
					return result;
				}   
			}
			
			// 库存细表  更新库存
			TParm stockD = parm.getParm("DEV_STOCKD"); 
			for (int i = 0; i < stockD.getCount("DEV_CODE"); i++) {
				result = DevInStorageTool.getInstance().insertOrUpdateStockD(stockD.getRow(i), connection);
				if (result.getErrCode() < 0) {
					//connection.rollback(); 
					connection.close();
					return result;   
				}              
			} 
			// 库存明细表--序号管理设备？？？
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
	 * 设备请领保存(插入)
	 * @param parm
	 * @return 
	 */
	public TParm InsertRequest(TParm parm) {  
//		System.out.println("STARTTTTTTT--INSERT"); 
		TParm result = new TParm(); 
		TConnection connection = getConnection(); 
		// 插入请领主表 
		result = DevOutRequestMTool.getInstance().createNewRequestM(parm.getParm("DEV_REQUESTMM"), connection);
//		System.out.println("主result"+result); 
		if (result.getErrCode() < 0) {
			connection.close();  
			return result;   
		}    
		// 插入请领明细表 
		result = DevOutRequestDTool.getInstance().createNewRequestD(parm.getParm("DEV_REQUESTDD"), connection);
//		System.out.println("细result"+result);
		if (result.getErrCode() < 0) { 
			connection.close();   
			return result;   
		}   
		
		connection.commit();
		connection.close(); 
		return result;

	}
	/**
	 * 设备请领保存(更新)
	 * @param parm
	 * @return
	 */
	public TParm UpdateRequest(TParm parm) {
//		System.out.println("STARTTTTTTT----UPDATE"); 
		TParm result = new TParm();
		TConnection connection = getConnection(); 
		// 更新请领主表    
		result = DevOutRequestMTool.getInstance().updateRequestM(parm.getParm("DEV_REQUESTMM"), connection);
		if (result.getErrCode() < 0) {  
			connection.close();   
			return result;    
		}
		// 更新请领明细表  
		result = DevOutRequestDTool.getInstance().updateRequestD(parm.getParm("DEV_REQUESTDD"), connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}   
//	    // 更新请领明细表状态
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
	 * 设备出库保存(new)
	 * @param parm
	 * @return
	 */
	public TParm generateExStorageReceipt(TParm parm) {
		TParm result = new TParm();
		String check_in = parm.getValue("CHECK_IN");
		TConnection connection = getConnection();
		if(check_in.equals("N")){
			
			// 出库主表
			result = DevOutStorageTool.getInstance().insertExStorgeM(parm.getParm("DEV_EXWAREHOUSEM"), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			
			TParm ParmD = parm.getParm("DEV_EXWAREHOUSED"); 
//		System.out.println("出库明细表ParmD"+ParmD);
			// 出库明细表
			result = DevOutStorageTool.getInstance().insertExStorgeD(ParmD, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;  
			}     
			TParm ParmDD = parm.getParm("DEV_EXWAREHOUSEDD");
//		System.out.println("出库序号明细表ParmDD"+ParmDD); 
			// 出库序号明细表
			result = DevOutStorageTool.getInstance().insertExStorgeDD(ParmDD, connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;  
			} 
		}else if(check_in.equals("Y")){
			
			//更新出库主表审核出库状态
			result = DevInStorageTool.getInstance().updateDevExwM(parm.getParm("DEV_EXWAREHOUSEM"), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;
			}
			
			// 更新出库明细表审核出库状态
			result = DevInStorageTool.getInstance().updateDevExwD(parm.getParm("DEV_EXWAREHOUSED"), connection);
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result;  
			}
			
			// 减库存主表，细表，和插入DD表数量(未完成) 
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
			//待查
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
//		//更新请领主表
//		result = DevOutStorageTool.getInstance().UpdateRequsetMFinal(requestMParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;    
//		}  
//		//更新请领细表
//		result = DevOutStorageTool.getInstance().UpdateRequsetDFate(requestDParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		}  
			
			//20150602 wangjc add
			// 入库主表 %
			result = DevInStorageTool.getInstance().insertDevInwarehouseM(parm.getParm("DEV_INWAREHOUSEM"), connection);
//		System.out.println("入库主表result"+result);
//		System.out.println("// 入库主表 %");
			if (result.getErrCode() < 0) {
				connection.rollback();
				connection.close();
				return result; 
			}
			// 入库细表  %
			TParm inWareHouseD = parm.getParm("DEV_INWAREHOUSED");  
//		System.out.println("inWareHouseD"+inWareHouseD);
			for (int i = 0; i < inWareHouseD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	  System.out.println("// 入库细表  %");
				result = DevInStorageTool.getInstance().insertDevInwarehouseD(inWareHouseD.getRow(i), connection);
//		System.out.println("入库细表 result"+result); 
				
				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();  
					return result;       
				}     
			}  
			// 入库明细表 % ORGIN_CODE
			TParm inWareHouseDD = parm.getParm("DEV_INWAREHOUSEDD");
//	    System.out.println("inWareHouseDD"+inWareHouseDD);
			for (int i = 0; i < inWareHouseDD.getCount("INWAREHOUSE_NO"); i++) { 
//	    	System.out.println("// 入库明细表 % ORGIN_CODE");
//	    	if(inWareHouseDD.getBoolean(""))
				result = DevInStorageTool.getInstance().insertDevInwarehouseDD(inWareHouseDD.getRow(i), connection);
//		System.out.println("入库明细表result"+result);
				if (result.getErrCode() < 0) { 
					connection.rollback();
					connection.close();
					return result;       
				}   
			}
			
			
//		// 库存主表(加入库存(将在途库存加上))  %
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
//	    System.out.println("// 库存主表(插入或更新)  %");
			// 库存细表  %    
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
//		 库存明细表--序号管理设备  %
//        System.out.println("库存明细表--序号管理设备");
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
//        System.out.println("// 库存明细表--序号管理设备  % ");
			//20150602 WANGJC ADD
		}

		connection.commit();  
		connection.close(); 
		return result;
	}
	/**
	 * 设备出库保存(update)
	 * @param parm
	 * @return
	 */
	public TParm updateExStorageReceipt(TParm parm) {
		TParm result = new TParm();
		
		TConnection connection = getConnection();
		// 出库主表
		result = DevOutStorageTool.getInstance().insertExStorgeM(parm.getParm("DEV_EXWAREHOUSEM"), connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		
		TParm tmepParm = parm.getParm("DEV_EXWAREHOUSED");
		// 出库明细表
		result = DevOutStorageTool.getInstance().insertExStorgeD(tmepParm, connection);
		if (result.getErrCode() < 0) {
			connection.close();
			return result;
		}
		// 减库存主表数量(未完成)
//		result = DevOutStorageTool.getInstance().deductStockM(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		}  
//		// 序号管理 更新明细表数量；非序号管理 更新/插入主表数据
//		result = DevOutStorageTool.getInstance().updateStockMD(tmepParm, connection);
//		if (result.getErrCode() < 0) {
//			connection.close();
//			return result;  
//		} 
		// 修改DEV_RFIDBASE表所属科室信息
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
	 * 设备追踪 出库
	 * @param parm
	 * @return
	 */
	public TParm onTrackOut(TParm parm) {
		TParm result = new TParm();
//		TConnection connection = getConnection(); 
		// 插入设备追踪表
		result = DevTrackTool.getInstance().insertDevTrackInf(parm.getParm("OUT"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		// 更新DEV_RFIDBASE表 状态为正常
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
	 * 设备追踪 入库
	 * @param parm
	 * @return
	 */
	public TParm onTrackIn(TParm parm) {
		TParm result = new TParm();
	//	TConnection connection = getConnection();
//		System.out.println("1:" + parm);
		// 插入设备追踪表
		result = DevTrackTool.getInstance().insertDevTrackInf(parm.getParm("IN"));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
//		System.out.println("2:" + parm);
		// 更新DEV_RFIDBASE表 状态为正常
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
	 * 根据盘点结果更新库存表
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
     * 设备请购保存
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
    
    /*------------------设备二期-------------------------------------------------------*/
    /**
     * 新增库存主档
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
     * 更新库存主档，插入库存细表
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
     * 新增库存主档
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
     * 新增设备计量字典
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
     * 更新设备计量字典
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
     * 删除设备计量字典
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
     * 更新设备维护/计量时间
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
     * 新增设备维护/计量记录
     * 更新下次维护/计量时间
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
