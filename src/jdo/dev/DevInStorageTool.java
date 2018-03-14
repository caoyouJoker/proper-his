package jdo.dev;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.db.TConnection;

import jdo.sys.SystemTool;


/**
 * <p>Title:设备入库 </p>
 *
 * <p>Description:设备入库 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company:javahis </p>
 *
 * @author sundx
 * @version 1.0
 */
public class DevInStorageTool  extends TJDOTool{

    /**
     * 构造器
     */
    public DevInStorageTool() {
        setModuleName("dev\\DevInStorageModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static DevInStorageTool instanceObject;

    /**
     * 得到实例
     * @return MainStockRoomTool
     */
    public static DevInStorageTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevInStorageTool();
        return instanceObject;
    }

    /**
     * 得到库存基本档信息
     * @param parm TParm
     * @return TParm
     */ 
    public TParm selectDevInStorageInf(TParm parm){
        if(parm.getValue("RECEIPT_NO").length() != 0 ||
           parm.getValue("RECEIPT_START_DATE").length() != 0 ||
           parm.getValue("RECEIPT_END_DATE").length() != 0)
            parm = query("selectDevInStorageInf1",parm);
        else
            parm = query("selectDevInStorageInf2",parm);
        return parm;
    } 
    /**
     * 得到验收单信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDevReceipt(TParm parm){
        parm = query("selectDevReceipt",parm);
        return parm;
    }
 
    
    /**
     * 得到验收单信息
     * @param receiptNo String
     * @return TParm
     */
    public TParm selectReceiptD(String receiptNo){
        TParm parm = new TParm();
        parm.setData("RECEIPT_NO",receiptNo);
        parm = query("selectReceiptD",parm);
        return parm; 
    }
    
    /**
     * 得到出库单信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectDevexwarehouse(TParm parm){
        parm = query("selectDevexwareHouse",parm);
        return parm;
    } 
    /** 
     * 得到出库单信息
     * @param EXWAREHOUSENo String
     * @return TParm
     */ 
    public TParm selectDevexwarehouseD(String receiptNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",receiptNo); 
        parm = query("selectDevexwarehouseD",parm);  
        return parm; 
    }
    
    /** 
     * 得到出库单序号管理信息
     * @param EXWAREHOUSENo String
     * @return TParm
     */
    public TParm selectDevexwarehouseDD(String receiptNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",receiptNo);
        parm = query("selectDevexwarehouseDD",parm); 
        return parm;
    }
 
    /**
     * 取得批号
     * @param devCode String
     * @param depDate Timestamp
     * @param guarepDate Timestamp
     * @return TParm
     */
    public TParm getBatchNo(String devCode,Timestamp depDate,Timestamp guarepDate){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode);
        parm.setData("DEP_DATE",depDate);
        parm.setData("GUAREP_DATE",guarepDate);
        parm = query("getBatchNo",parm);
        return parm;
    }
    
    /**
     * 取得批号
     * @param devCode String
     * @param depDate Timestamp
     * @param guarepDate Timestamp
     * @return TParm 
     */
    public TParm getBatchNo(String devCode){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode);
        parm = query("getBatchNo",parm);
        return parm;
    }
    
    /**
     * 取得最大批号
     * @param devCode String 
     * @return TParm
     */ 
    public TParm getMaxBatchNo(String devCode){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode);
        parm = query("getMaxBatchNo",parm);
        return parm;
    }

    /**
     * 取得入库单号
     * @return String
     */
    public String getInwarehouseNo(){
        return SystemTool.getInstance().getNo("ALL", "DEV",
                "INWAREHOUSE_NO", "INWAREHOUSE_NO");
    }

    /**
     * 取得最大设备序号
     * @param devCode String
     * @return int
     */
    public int getMaxDevSeqNo(String devCode){
        TParm parm = new TParm();
        parm.setData("DEV_CODE",devCode);
      //得到设备最大顺序号
//        SELECT MAX(DEVSEQ_NO) DEVSEQ_NO 
//        FROM   DEV_STOCKDD 
//        WHERE  DEV_CODE=<DEV_CODE>  
        parm = query("getMaxDevSeqNo",parm);
        if(parm.getErrCode() < 0)              
            return 1;
        if(parm.getCount("DEVSEQ_NO") <= 0) 
            return 1;
        if(parm.getValue("DEVSEQ_NO",0) == null||
           parm.getValue("DEVSEQ_NO",0).length() == 0)
            return 1;    
        //system.out.println("取得最大设备序号"+parm.getInt("DEVSEQ_NO",0) + 1);
        return parm.getInt("DEVSEQ_NO",0) + 1;
    }

    /**
     * 取得当前使用的批号
     * @param devCode String
     * @param depDate Timestamp
     * @param guarepDate Timestamp
     * @return int
     */
    public int getUseBatchSeq(String devCode,Timestamp depDate,Timestamp guarepDate){
        TParm parmBat = getBatchNo(devCode,depDate,guarepDate);
        //没查询到批号
      if(parmBat.getErrCode() < 0)
            return 1;
       if(parmBat.getCount("BATCH_SEQ") > 0)
            return parmBat.getInt("BATCH_SEQ",0);  
        //得到最大批号
        TParm parmMaxBat = getMaxBatchNo(devCode);
        if(parmMaxBat.getErrCode() < 0)   
            return 1;
        if(parmMaxBat.getCount("BATCH_SEQ") <= 0)
            return 1;
        if(parmMaxBat.getValue("BATCH_SEQ",0) == null ||
           parmMaxBat.getValue("BATCH_SEQ",0).length() == 0)
            return 1;
        return parmMaxBat.getInt("BATCH_SEQ",0) + 1;
    }

    /**
     * 取得当前使用的批号
     * @param devCode String
     * @return int
     */
    public int getUseBatchSeq(String devCode){
        TParm parmBat = getBatchNo(devCode);
        //没查询到批号 
        if(parmBat.getErrCode() < 0)
            return 1;
        if(parmBat.getCount("BATCH_SEQ") > 0)
            return parmBat.getInt("BATCH_SEQ",0);  
        //得到最大批号
        TParm parmMaxBat = getMaxBatchNo(devCode);
        if(parmMaxBat.getErrCode() < 0)   
            return 1;
        if(parmMaxBat.getCount("BATCH_SEQ") <= 0)
            return 1;
        if(parmMaxBat.getValue("BATCH_SEQ",0) == null ||
           parmMaxBat.getValue("BATCH_SEQ",0).length() == 0)
            return 1;
        return parmMaxBat.getInt("BATCH_SEQ",0) + 1;
    }
    
    
    /**
     * 写入设备入库单主档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertDevInwarehouseM(TParm parm, TConnection connection){
    	List<TParm> parmList = getTParmList(parm);
    	//system.out.println("parmList"+parmList);
    	for (TParm tParm : parmList) { 
			parm = update("insertDevInwarehouseM",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm; 
			}
		} 
        return parm;
    }
    /**
     * 写入设备入库明细档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertDevInwarehouseD(TParm parm, TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	//for (TParm tParm : parmList) {  
    		//system.out.println("难道没走这里？？？"); 
			parm = update("insertDevInwarehouseD",parm,connection);
			//system.out.println("写入设备入库明细档parm"+parm); 
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm; 
			}
		//}
        
        return parm;
    }
    /**
     * 写入设备入库明细序号管理档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */ 
    public TParm insertDevInwarehouseDD(TParm parm, TConnection connection){
		//system.out.println("SEQMAN_FLG？？？"+parm.getValue("SEQMAN_FLG",0));
		if ("Y".equals(parm.getValue("SEQMAN_FLG"))) {  
			parm = update("insertDevInwarehouseDD",parm,connection);
			if (parm.getErrCode() < 0) {
				connection.close(); 
				return parm;      
			}
		}
        return parm;
    }
    /**
     * 根据入库单号查询设备入库明细信息
     * @param inwarehouseNo String
     * @return TParm
     */
    public TParm selectDevInwarehouseD(String inwarehouseNo){
        TParm parm = new TParm();
        parm.setData("INWAREHOUSE_NO",inwarehouseNo);
        return query("selectDevInwarehouseD",parm);
    }
    /**
     * 根据入库单号查询入库明细序号管理信息
     * @param inwarehouseNo String
     * @return TParm
     */
    public TParm selectDevInwarehouseDD(String inwarehouseNo){
        TParm parm = new TParm();
        parm.setData("INWAREHOUSE_NO",inwarehouseNo); 
        return query("selectDevInwarehouseDD",parm);
    }
    /**
     * 更新设备入库明细信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateDevInwarehouseM(TParm parm, TConnection connection){
        parm = update("updateDevInwarehouseM",parm,connection);   
        return parm;
    }
    /**
     * 更新设备入库明细信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateDevInwarehouseD(TParm parm, TConnection connection){
        parm = update("updateDevInwarehouseD",parm,connection);
        return parm;
    }
    /**
     * 更新设备入库序号管理明细信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateDevInwarehouseDD(TParm parm, TConnection connection){
    	if(parm == null){
    		return null; 
    	}        
        parm = update("updateDevInwarehouseDD",parm,connection);
        return parm;
    }

    /**
     * 得到设备当前库存
     * @param parm TParm
     * @return TParm
     */
    public TParm getStock(TParm parm){
        parm = query("getStock",parm);
        return parm;
    }
    
    /**
     * 更新设备库存序号管理明细信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStock(TParm parm, TConnection connection){
        parm = update("updateStock",parm,connection);
        return parm;
    }
    
    /**
     * 更新设备入库存序号管理明细信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockIn(TParm parm, TConnection connection){
        parm = update("updateStockIn",parm,connection);
        return parm;
    }
    
    /**
     * 更新设备库细表信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockD(TParm parm, TConnection connection){
        parm = update("updateStockD",parm,connection);
        return parm;
    }
    
//    /**
//     * 更新设备库存序号管理明细信息
//     * @param parm TParm
//     * @param connection TConnection 
//     * @return TParm
//     */ 
//    public TParm updateStockDD(TParm parm, TConnection connection){
//        parm = update("updateStockDD",parm,connection);
//        return parm;
//    }
    
    /**
     * 写入设备库存信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertStock(TParm parm, TConnection connection){
    	parm = update("insertStock",parm);
        return parm;
    }
    
    
    /**
     * 新增或者更新库存主表
     * @param parm
     * @param connection
     * @return  
     */
    public TParm insertOrUpdateStockM(TParm parm, TConnection connection) {
    	    //查询Stockm表若无相关数据则插入，如有则更新库存
    	//system.out.println("新增或者更新库存主表parm"+parm);
    		if (0 == selectCountOfStock(parm, connection)) { 
    			//system.out.println("新建"); 
				parm = insertStock(parm, connection);
				if (parm.getErrCode() < 0) {
					return parm;   
				}                         
			}else { 
				//system.out.println("更新"); 
				parm = updateStock(parm, connection);
				if (parm.getErrCode() < 0) {
					return parm;
				} 
			}
		
    	 
    	return parm;
    }
    /**
     * 写入设备库存细表
     * @param parm TParm
     * @param connection TConnection
     * @return TParm  
     */
    public TParm insertOrUpdateStockD(TParm parm, TConnection connection){
    	 //查询Stockd表若无相关数据则插入，如有则更新库存
    	//system.out.println("写入设备库存细表parm"+parm);
    			if (0 == selectCountOfStockD(parm, connection)) {
    				parm = update("insertStockD",parm,connection);
    				if (parm.getErrCode() < 0) {
    					return parm;
    				} 
    			}else {  
    				//更新库存量 
    				parm = updateStockD(parm, connection);
    				if (parm.getErrCode() < 0) {
    					return parm;
    				}	
    			}
        return parm;  
    }
    
    
    /**
     * 写入设备库存细表
     * @param parm TParm
     * @param connection TConnection
     * @return TParm  
     */
    public TParm ReqInsertorUpdateStockD(TParm parm, TConnection connection){
    	 //查询Stockd表若无相关数据则插入，如有则更新库存  
    	//system.out.println("写入设备库存细表parm"+parm);
    			if (0 == selectCountOfStockD(parm, connection)) {
    				parm = update("insertStockD",parm,connection);
    				if (parm.getErrCode() < 0) {
    					return parm;
    				}   
    			}else {  
    				//更新库存量 
    				parm = updateStockD(parm, connection);
    				if (parm.getErrCode() < 0) {
    					return parm;
    				}	
    			}
        return parm;
    }
    
    
    /**
     * 写入设备库存序号管理信息明细表
     * @param parm TParm
     * @param connection TConnection 
     * @return TParm    
     */  
    public TParm insertOrUpdateStockDD(TParm parm, TConnection connection){
    	//序号管理才写入    
    	//system.out.println("序号管理parm"+parm);
    		//system.out.println("设备库存序号管理信息明细表SEQMAN_FLGY"+parm.getValue("SEQMAN_FLG"));
    		if ("Y".equals(parm.getValue("SEQMAN_FLG"))) {
    			//if (0 == selectCountOfStockD(parm, connection)) {
    				parm = update("insertStockDD",parm,connection);
    				if (parm.getErrCode() < 0) {
    					return parm; 
    		//		}  
//    			}else {     
//    				//updateStockDD更新操作如何。。。
//    				parm = updateStockDD(parm, connection);
//    				if (parm.getErrCode() < 0) {
//    					return parm;
//    				} 
    			}
			} 
    	
        return parm;
    }
    
    
    
    /**
     * 入库确认插入库存主表或者加入库存(判断科室和设备名称)
     * @param parm
     * @param connection
     * @return  
     */ 
    public TParm insertStockM(TParm parm, TConnection connection) {
    	    //查询Stockm表若无相关数据则插入，如有则更新库存
    	        //system.out.println("新增库存主表parm"+parm);
    			//system.out.println("新建"); 
				parm = insertStock(parm, connection);
				if (parm.getErrCode() < 0) {
					return parm;   
				}                         

    	return parm;
    }
    /**
     * 入库确认一次性插入库存细表
     * @param parm TParm
     * @param connection TConnection
     * @return TParm  
     */
    public TParm insertStockD(TParm parm, TConnection connection){
    	 //查询Stockd表若无相关数据则插入，如有则更新库存
    				parm = update("insertStockD",parm,connection);
    				if (parm.getErrCode() < 0) {
    					return parm;
    				} 
        return parm;
    }
     
    
    /**
     * 更新设备库存序号管理信息明细表(DEPT_CODE和WAIT_DEPT_CODE)
     * @param parm TParm
     * @param connection TConnection 
     * @return TParm     
     */   
    public TParm UpdateStockDD(TParm parm, TConnection connection){
    	//序号管理才写入    
    	//system.out.println("序号管理parm"+parm);
    		//system.out.println("设备库存序号管理信息明细表SEQMAN_FLGY"+parm.getValue("SEQMAN_FLG"));
//    		if ("Y".equals(parm.getValue("SEQMAN_FLG"))) {
			parm = update("updateStockDD",parm,connection);
			if (parm.getErrCode() < 0) {
				return parm; 
			} 
//			} 
    	
        return parm;
    }
    
    
    /**
     * 更新设备库存量
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockDSumQty(TParm parm, TConnection connection){
        parm = update("updateStockDSumQty",parm,connection);
        return parm;
    }
    
    /**
     * 查询某设备在库存主表中的数量
     * @param parm
     * @param connection
     * @return
     */ 
    public int selectCountOfStock(TParm parm, TConnection connection) {
    	//system.out.println("查询某设备在库存主表中的数量parm"+parm);
    	parm = query("getCountOfStock", parm);  
    	//system.out.println("getCountOfStock-parm"+parm);
    	if (parm.getErrCode() < 0) {     
			connection.close();
		}       
    	//system.out.println("数量1"+parm.getValue("NUM",0));
    	int count =0;   
    	if("0".equals(parm.getValue("NUM",0))||parm.getValue("NUM",0)== "0"){
    	   count = 0; 
    	}  
    	else {
    		count = 1;	
    	}
    	//system.out.println("count1"+count); 
    	return count;
    } 
    
    /** 
     * 查询某设备在库存细表中的数量 
     * @param parm
     * @param connection
     * @return  
     */
    public int selectCountOfStockD(TParm parm, TConnection connection) {
    	//system.out.println("查询某设备在库存细表中的数量 parm"+parm);
    	parm = query("getCountOfStockD", parm);
    	//system.out.println("getCountOfStockD-parm"+parm);
    	if (parm.getErrCode() < 0) {   
			connection.close();
		}   
    	//system.out.println("数量2"+parm.getValue("NUM",0)); 
    	int count =0;
    	if("0".equals(parm.getValue("NUM",0))||parm.getValue("NUM",0)=="0"){
    	  //system.out.println("查询某设备在库存细表中的数量"); 
    	   count = 0; 
    	}  
    	else {
    		count = 1;	
    	}
    	//system.out.println("count2"+count); 
    	return count;
    }
    //fux modify
    /**
     * 删除库存细表DEV_INWAREHOUSED
     * @param parm
     * @param connection
     * @return
     */
    public TParm deleteinwarehoused(TParm parm) {
    	parm = update("deleteinwarehoused", parm);
		if (parm.getErrCode() < 0) {
   		    err("ERR:" + parm.getErrCode() + parm.getErrText() +
			             parm.getErrName());
	        return parm;  
		}    
    	return parm;
    }   
    /**
     * 删除库存序列表DEV_INWAREHOUSEDD
     * @param parm
     * @param connection 
     * @return 
     */
    public TParm deleteinwarehousedd(TParm parm) {
    	parm = update("deleteinwarehousedd", parm);
    	if (parm.getErrCode() < 0) {
    		 err("ERR:" + parm.getErrCode() + parm.getErrText() +
    				      parm.getErrName());
    		return parm;
		}
    	return parm;
    }
    
    
    /**
     * 验收单细项状态(更新验收单细项完成状态)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm 
     */ 
    public TParm onUpdateDevReceiptD(TParm parm, TConnection conn){
        TParm result = this.update("updateDevReceiptD", parm, conn);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result; 
    }
    /**
     * 验收单主项状态(更新验收单主项完成状态)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */ 
    public TParm onUpdateDevReceiptM(TParm parm, TConnection conn) {
        TParm result = this.update("updateDevReceiptM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新出库单细项完成状态
     * @param parm TParm
     * @param conn TConnection
     * @return TParm 
     */ 
    public TParm onUpdateDevExWareHouseD(TParm parm, TConnection conn){
        TParm result = this.update("UpdateDevExWareHouseD", parm, conn);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;  
    }  
     
    /**
     * 更新出库单主项完成状态  
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */ 
    public TParm onUpdateDevExWareHouseM(TParm parm, TConnection conn) {
        TParm result = this.update("UpdateDevExWareHouseM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 将多条parm转换成parmList
     * @param parm
     * @return
     */
    public List<TParm> getTParmList(TParm parm){
    	// 返回结果list
    	List<TParm> parmList = new ArrayList<TParm>();
    	TParm tempParm;
    	String[] names = parm.getNames();
    	// 一个parm里存放多少条数据
    	int count = parm.getCount(names[0]);
    	for (int i=0; i<count; i++) {
			tempParm = new TParm();
			for (String name : names) {
				tempParm.setData(name, parm.getData(name, i));
			}
			parmList.add(tempParm);
		}
    	
    	return parmList;
    }
    
    /**
     * 更新出库主表审核出库状态 
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * wangjc add
     */ 
    public TParm updateDevExwM(TParm parm, TConnection conn) {
    	String updateOutM = "UPDATE DEV_EXWAREHOUSEM SET DISCHECK_FLG='"
				+parm.getValue("DISCHECK_FLG",0)
				+"' WHERE EXWAREHOUSE_NO='"
				+parm.getValue("EXWAREHOUSE_NO",0)
				+"'";
//    	System.out.println("updateOutM>>>>>>"+updateOutM);
    	TParm result = new TParm(TJDODBTool.getInstance().update(updateOutM,conn));
//        TParm result = this.update("UpdateDevExWareHouseM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新出库明细表审核出库状态 
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * wangjc add
     */ 
    public TParm updateDevExwD(TParm parm, TConnection conn) {
    	String updateOutD = "UPDATE DEV_EXWAREHOUSED SET DISCHECK_FLG='"
				+parm.getValue("DISCHECK_FLG",0)
				+"' WHERE EXWAREHOUSE_NO='"
				+parm.getValue("EXWAREHOUSE_NO",0)
				+"'";
    	TParm result = new TParm(TJDODBTool.getInstance().update(updateOutD,conn));
//        TParm result = this.update("UpdateDevExWareHouseM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新DEV_RECEIPTM完成状态 
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * wangjc add
     */ 
    public TParm updateDevRecMFinalFlg(TParm parm, TConnection conn) {
    	String sql = "UPDATE DEV_RECEIPTM SET FINAL_FLG='1' WHERE RECEIPT_NO='"
				+parm.getValue("VERIFY_NO",0)
				+"'";
//    	System.out.println(">>>>>>>>>>>"+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
//        TParm result = this.update("UpdateDevExWareHouseM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新DEV_RECEIPTM完成状态 
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * wangjc add
     */ 
    public TParm updateDevInwCheckFlg(TParm parm, TConnection conn) {
    	String sql = "UPDATE DEV_INWAREHOUSEM SET CHECK_FLG='Y' WHERE INWAREHOUSE_NO='"
				+parm.getValue("INWAREHOUSE_NO",0)
				+"'";
//    	System.out.println(">>>>>>>>>>>"+sql);
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql,conn));
//        TParm result = this.update("UpdateDevExWareHouseM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 插入DEV_MTN_DATE 
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     * wangjc add
     */ 
    public TParm insertDevMtnDate(TParm parm, TConnection conn) {
//    	String sql = "UPDATE DEV_INWAREHOUSEM SET CHECK_FLG='Y' WHERE INWAREHOUSE_NO='"
//				+parm.getValue("INWAREHOUSE_NO",0)
//				+"'";
    	TParm result = new TParm();
    	for(int i=0;i<parm.getCount();i++){
    		String sql = "INSERT INTO DEV_MAINTENANCE_DATE (DEV_CODE, MTN_KIND, MTN_TYPE_CODE,"
    				+ " DEVSEQ_NO, NEXT_MTN_DATE, OPT_USER, OPT_DATE, OPT_TERM) "
    				+ " VALUES('"
    				+ parm.getValue("DEV_CODE", i)
    				+ "', '"
    				+ parm.getValue("MTN_KIND", i)
    				+ "', '"
    				+ parm.getValue("MTN_TYPE_CODE", i)
    				+ "', '"
    				+ parm.getValue("DEVSEQ_NO", i)
    				+ "', TO_DATE('"
    				+ parm.getValue("NEXT_MTN_DATE", i)
    				+ "', 'YYYY/MM/DD'), "
    				+" '"
    				+ parm.getValue("OPT_USER", i)
    				+ "', SYSDATE, '"
    				+ parm.getValue("OPT_TERM", i)
    				+ "') ";
//    	System.out.println(">>>>>>>>>>>"+sql);
    		result = new TParm(TJDODBTool.getInstance().update(sql,conn));
//        TParm result = this.update("UpdateDevExWareHouseM", parm, conn);
    		if (result.getErrCode() < 0) {
    			err("ERR:" + result.getErrCode() + result.getErrText()
    					+ result.getErrName());
    			return result;
    		}
    	}
        return result;
    }
}
