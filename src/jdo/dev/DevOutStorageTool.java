package jdo.dev;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SystemTool;

/**
 * <p>Title: 出库工具类</p>
 *
 * <p>Description:出库工具类 </p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: javahis</p>  
 *
 * @author sundx 
 * @version 1.0
 */
public class DevOutStorageTool extends TJDOTool{

    /**
     * 构造器
     */
    public DevOutStorageTool() {
        setModuleName("dev\\DevOutStorageModule.x");
        onInit();
    }

    /**
     * 实例
     */
    private static DevOutStorageTool instanceObject;

    /**
     * 得到实例
     * @return MainStockRoomTool
     */
    public static DevOutStorageTool getInstance()
    {
        if(instanceObject == null)
            instanceObject = new DevOutStorageTool();
        return instanceObject;
    }
    /**
     * 取得出库单号
     * @return String
     */
    public String getExwarehouseNo(){
        return SystemTool.getInstance().getNo("ALL", "DEV",
                "EXWAREHOUSE_NO", "EXWAREHOUSE_NO"); 
    }

    /**
     * 得到出库单信息  
     * @param parm TParm
     * @return TParm 
     */ 
    public TParm selectDevOutStorageInf(TParm parm){
        parm = query("selectDevOutStorageInf",parm);
        //system.out.println("得到出库单信息parm"+parm);
        return parm;
    }  
    /** 
     * 得到设备出库信息
     * @param parm TParm
     * @return TParm
     */
    public TParm getExStorgeInf(TParm parm){
        if(parm.getValue("SEQMAN_FLG").equals("N"))
            parm = query("getExStorgeInf1",parm);
        else
            parm = query("getExStorgeInf2",parm);
        return parm;
    }

    /**
     * 取得出库设备当前库存
     * @param parm TParm
     * @return TParm
     */
    public TParm getDeptExStorgeQty(TParm parm){
        parm = query("getDeptExStorgeQty",parm);
        return parm;
    }

    /**
     * 删除设备库存信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm deleteExStorgeQty(TParm parm,TConnection connection){
        parm = update("deleteExStorgeQty",parm,connection);
        return parm;
    }

    /**
     * 修改设备库存信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm modifyStorgeQty(TParm parm,TConnection connection){
        parm = update("modifyStorgeQty",parm,connection);
        return parm;
    }
 
    /**
     * 写入设备出库明细信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertExStorgeD(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("insertExStorgeD",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }

    
    /**
     * 写入设备出库明细信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertExStorgeDD(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("insertExStorgeDD",tParm,connection);
			if (parm.getErrCode() < 0) { 
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    
    /**
     * 写入设备库存信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertStock(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("insertStock",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    /**
     * 更新设备库存序号管理信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockD(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("updateStockD",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }

    /**
     * 写入设备出库主表信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertExStorgeM(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) { 
			parm = update("insertExStorgeM",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }

    /**
     * 更新设备出库明细信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateExStorgeD(TParm parm,TConnection connection){
        parm = update("updateExStorgeD",parm,connection);
        return parm;
    }
    
    /**
     * 根据请领单号查询设备出库明细信息
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryRequestD(String requsetD){
        TParm parm = new TParm();
        parm.setData("REQUEST_NO",requsetD);
        parm = query("REQUEST_NO",parm); 
        return parm;
    }
    /**
     * 根据出库单号查询设备在途出库明细信息
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryZTExStorgeD(String exWarehouseNo){
        TParm parm = new TParm(); 
        parm.setData("EXWAREHOUSE_NO",exWarehouseNo);  
        parm = query("queryZTExStorgeD",parm); 
        return parm;
    } 
    /**
     * 根据出库单号查询设备已完成出库明细信息
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryWCExStorgeD(String exWarehouseNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",exWarehouseNo);
        parm = query("queryWCExStorgeD",parm);
        return parm; 
    }

    
    
    /**
     * 根据出库单号查询设备出库序号管理明细信息
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryExStorgeDD(String exWarehouseNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",exWarehouseNo);
        parm = query("queryExStorgeDD",parm);
        return parm;
    }
    
    /** 
     * 根据出库单号查询设备出库信息
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryExReceiptData(String exWarehouseNo){
        TParm parm = new TParm();
        parm.setData("EXWAREHOUSE_NO",exWarehouseNo);
        parm = query("queryExReceiptData",parm);
        return parm;
    }

    /**
     * 更新设备库存信息
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockM(TParm parm,TConnection connection){
        List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("updateStockM",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    
    /**
     * 扣除库存主表
     * @param parm
     * @param connection
     * @return
     */
    public TParm deductStockM(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("deductStockMQty",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    

    
    /**
     * 更新DEV_STOCKD表数据
     * 实质是updateRFID设备的dept_code
     * 由出库代码改成入库代码
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateStockMD(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
    		// 如果是序号管理 update dev_code
    		if ("Y".equals(tParm.getData("SEQMAN_FLG"))) {
    			parm = update("updateStockDRFID",tParm,connection);
    			if (parm.getErrCode() < 0) {
					connection.close();
					return parm;
				}
			}
    		
			// 如果主表中没有该设备
			if (0 == getCountOfStockD(tParm)) {
				parm = update("insertStock",tParm,connection);
				if (parm.getErrCode() < 0) {
					connection.close();
					return parm;
				}
			}else { // 如果存在该设备，则增加库存量
				parm = addStockMQty(tParm, connection);
				if (parm.getErrCode() < 0) {
					connection.close();
					return parm;
				}
			}
		}
        
        return parm;
    }
    
    /**
     * 扣除库存明细表 
     * @param parm
     * @param connection
     * @return
     */
    public TParm deductStockD(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) { 
			parm = update("deductStockDQty",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			} 
		}
        
        return parm;
    }
    
    /**
     * 根据DEV_CODE  DEPT_CODE  BATCH_SEQ
     * 查询该设备在 DEV_STOCKM表中的数量
     * @param parm
     * @return
     */
    public int getCountOfStockD(TParm parm) {
    	parm = query("getCountOfStock", parm);
    	if (parm.getErrCode() < 0) {
			return -1;
		}
    	int num = parm.getInt("NUM", 0);
    	return num;
    }
    
    /**
     * 出库时，增加入库科室的设备数量 
     * @param parm
     * @param connection
     * @return
     */
    public TParm addStockMQty(TParm parm, TConnection connection) {
    	parm = update("addStockMQty", parm, connection);
    	return parm;
    }
    
    /**
     * 设备出库明细信息(设备分类)
     * @param exWarehouseNo String
     * @return TParm
     */
    public TParm queryStockDD(String devCode,String deptCode){
        TParm parm = new TParm();  
        parm.setData("DEV_CODE",devCode);   
        parm.setData("DEPT_CODE",deptCode);
        parm = query("queryStockDD",parm);  
        return parm;              
    }
    
    
    /**
     * 设备出库明细信息(资产编号)
     * @param exWarehouseNo String
     * @return TParm  
     */
    public TParm queryStockDDDetail(String devCodeDetail,String deptCode){
        TParm parm = new TParm();      
        parm.setData("DEV_CODE_DETAIL",devCodeDetail);   
        parm.setData("DEPT_CODE",deptCode);  
        parm = query("queryStockDDDetail",parm);    
        return parm;              
    }
         
    
    /**
     * 出库更新stockDD表科室
     * @param parm
     * @param connection
     * @return
     */
    public TParm updateStockdd(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {  
			parm = update("updateStockDD",tParm,connection);
			if (parm.getErrCode() < 0) { 
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    
    /**
     * 出库回更请领主表
     * @param parm
     * @param connection
     * @return
     */
    public TParm UpdateRequsetMFinal(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) { 
			parm = update("UpdateRequsetMFinal",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
    }
    
    
    
    
    
    /**
     * 出库回更请领细表
     * @param parm
     * @param connection
     * @return
     */ 
    public TParm UpdateRequsetDFate(TParm parm, TConnection connection) {
    	List<TParm> parmList = getTParmList(parm);
    	for (TParm tParm : parmList) {
			parm = update("UpdateRequsetDFate",tParm,connection);
			if (parm.getErrCode() < 0) {
				connection.close();
				return parm;
			}
		}
        
        return parm;
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
     * 依照科室和设备编码查询STOCKD表数据
     * @param parm TParm
     * @return TParm 
     */ 
    public TParm selectDevStockD(TParm parm){
        parm = query("selectDevStockD",parm);
        return parm;
    } 

}
