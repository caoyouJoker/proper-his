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
     * 构造器                     
     */
    public INVNewBackDisnfectionTool() {
        setModuleName("inv\\INVNewBackDisinfectionModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static INVNewBackDisnfectionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVNewBackDisnfectionTool();
        return instanceObject;
    }
    
    /**
     * 查询手术包信息
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
     * 根据有效条码查询手术包信息
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
     * 根据全条码查询手术包信息  
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
     * 查询手术包明细信息
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
     * 根据条件查询回收单信息
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
     * 根据回收单号查询回收单信息
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
     * 查询打印条码信息
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
     * 查询科室名称
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
     * 查询人员姓名
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
     * 查询手术包物资信息（损耗管理查询）
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
     * 新建回收单方法
     * */
	public TParm insertBackDisnfection(TParm parm, TConnection connection){
		
		TParm disTable = parm.getParm("DISNFECTIONTABLE");			//回收表
		
		TParm packageMTable = parm.getParm("PACKAGEMAINTABLE");		//手术包表
		Object obj = parm.getParm("RECOUNTTIME");					//折损明细
		
		  
		//disTable:::{Data={OPT_USER=[D001], DELETE_FLG=[N], RECYCLE_NO=[140326000002], ORG_CODE=[0409], OPT_TERM=[127.0.0.1], 
		//OPT_DATE=[2014-03-26 09:42:35]}, ACTION={COUNT=1}}
		//packageMTable:::{Data={DISINFECTION_VALID_DATE=[2014/09/22 09:43:08], DISINFECTION_USER=[D001], PACK_CODE=[101010],
		//RECYCLE_USER=[D001], PACK_DESC=[介入起搏器综合包], STATUS=[回收], DISINFECTION_DATE=[2014-03-26 09:43:08], WASH_USER=[D001], 
		//WASH_DATE=[2014-03-26 09:43:08], BARCODE=[140326000004], QTY=[1], DISINFECTION_PROGRAM=[1], PACK_SEQ_NO=[3], 
		//RECYCLE_DATE=[2014-03-26 09:43:08], DISINFECTION_POTSEQ=[1]}, ACTION={COUNT=1}}
		TParm result = new TParm();
		
		//fux modify 20140334 低值耐用回收补库存  
		//如果是 低值耐用品   stockM +1  stockD+1 
		String invKind = "";
		 //回收时   
	     //查询手术包物资构成 
		String orgCode = disTable.getData("ORG_CODE", 0).toString();
		//扣库存问题  
		TParm tpS = new TParm();  
		tpS.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE"));
		//无论如何回收时将耐用品加回库存？  
		for (int i = 0; i < tpS.getRow(0).getCount("PACK_CODE"); i++) {
			System.out.println(""+packageMTable.getData("STATUS", i)); 
			if(packageMTable.getData("STATUS", i).equals("在库")){ 
				//[出库]
				//[在库]     
				//continue;  
				//在库状态将所有库存加回(包括一次性)
				String packcode = tpS.getRow(0).getValue("PACK_CODE",i); 
				//System.out.println("packCode==="+packcode);      
				TParm stockParm = new TParm (TJDODBTool.getInstance().select(queryPackDByPackCode(packcode)));
				for(int j=0; j<stockParm.getCount();j++){	//每种物资  
				    String invCode = stockParm.getValue("INV_CODE", j);   
					TParm invBaseKind =   new TParm (TJDODBTool.getInstance().select(getInvKind(invCode))); 
					invKind = invBaseKind.getValue("INVKIND_CODE",0).toString();    
						TParm stock = new TParm();   
						stock = stockParm.getRow(j);  
						//更新主库库存(加库存)
						stock.setData("STOCK_QTY", stock.getDouble("QTY") * +1);
						stock.setData("PACK_SEQ_NO",  packageMTable.getData("PACK_SEQ_NO"));
						stock.setData("ONCE_USE_FLG",  "Y"); 
						result = InvStockMTool.getInstance().updateStockMQty(stock,connection);
						if (result.getErrCode() < 0) {
							err(result.getErrCode() + " " + result.getErrText());
							return result;
						}
							 
						List<TParm> stockDList = new ArrayList<TParm>();
						stockDList = this.chooseBatchSeq(stock, orgCode);	//查询细库批号
//						INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(stock, connection);//删除已有的手术包明细
						//更新细表库存(加库存) 
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
			for(int j=0; j<stockParm.getCount();j++){	//每种物资  
			    String invCode = stockParm.getValue("INV_CODE", j);   
				TParm invBaseKind =   new TParm (TJDODBTool.getInstance().select(getInvKind(invCode))); 
				invKind = invBaseKind.getValue("INVKIND_CODE",0).toString();    
				if("C".equals(invKind)){    
					TParm stock = new TParm();   
					stock = stockParm.getRow(j);  
					//更新主库库存(加库存)
					stock.setData("STOCK_QTY", stock.getDouble("QTY") * +1);
					stock.setData("PACK_SEQ_NO",  packageMTable.getData("PACK_SEQ_NO"));
					stock.setData("ONCE_USE_FLG",  "Y"); 
					result = InvStockMTool.getInstance().updateStockMQty(stock,connection);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
						 
					List<TParm> stockDList = new ArrayList<TParm>();
					stockDList = this.chooseBatchSeq(stock, orgCode);	//查询细库批号
//					INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(stock, connection);//删除已有的手术包明细
					//更新细表库存(加库存)
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
  
		
		//插入回收单表      修改手术包状态
		for(int i=0;i<packageMTable.getCount();i++){
			//用于修改状态不为“出库”状态的手术包状态start
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
				for(int k=0; k<stockParm.getCount();k++){	//每种物资  
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
						//在库不能删除明细(删除一次性明细)同已出库(保留耐用品)
						//[[id,name],[,],['0',在库],['1',出库],['2',已回收],['3',已消毒],['4',维修中]]
						INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(changeStatusNew, connection);//删除已有的手术包明细
					}
				  }
				} 
			}
  
  
		   if(statusStr.equals("[2]")){
				INVSterilizationHelpTool.getInstance().updatePackageDisStatus(changeStatus.getRow(0), connection);
			}else if(statusStr.equals("[3]")){
				INVSterilizationHelpTool.getInstance().updatePackageSterStatus(changeStatus.getRow(0), connection);
			}
			//用于修改状态不为“出库”状态的手术包状态end
			
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
			insertValue.setData("STATUS", 0, "2" );	//状态更新为“回收清洗消毒”  
			insertValue.setData("FINISH_FLG", 0, "N" );	//该单完成状态为“未完成”
			
			TParm t = insertValue.getRow(0);
			System.out.println("t::::"+t);
			result = INVDisnfectionHelpTool.getInstance().insertDisnfection(t, connection);//插入回收单表
			
			result = INVDisnfectionHelpTool.getInstance().updatePackageStatus(t, connection);//修改手术包状态

		}
//		System.out.println("obj---"+obj);
		//修改折损次数
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
				//更新器械折损次数
				result = INVDisnfectionHelpTool.getInstance().updateRecountTime(temp, connection);
				
				
		//暂无用2013		
//				//查询器械的批号
//				TParm res = INVDisnfectionHelpTool.getInstance().queryBatchNo(temp, connection);
//				System.out.println("res---"+res);
//				tp.setData("BATCH_NO",0,res.getData("BATCH_NO", 0));
//				temp = tp.getRow(0);
//				System.out.println("temp---"+temp);
//				//更新主表库存
//				result = INVDisnfectionHelpTool.getInstance().updateStockMQTY(temp, connection);
//				//更新细表库存
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

	//选择库存批次 ----------------------待查
	private List<TParm> chooseBatchSeq(TParm parm,String orgCode){

		List<TParm> list = new ArrayList<TParm>();	//存放明细物资扣库信息
	
		//需要的物资数量
        double qty = parm.getDouble("QTY");

        //得到所有此物资的批次序号
        TParm result = this.getBatchSeqInv(parm,orgCode);

        if (result == null || result.getErrCode() < 0)
            return list;
        //查出的物资批次个数
        int rowCount = result.getCount();
        //循环取出所有批次
        for (int i = 0; i < rowCount; i++) {
            //拿出一个
            TParm oneRow = result.getRow(i);
            double stockQty = oneRow.getDouble("STOCK_QTY");
            //如果物资足够(首先不能为0)
            if (stockQty > 0) {
                if (stockQty >= qty) {
                    oneRow.setData("STOCK_QTY", qty);
                    //调用插入一行的方法
                    list.add(oneRow);
                    //够了就走
                    return list;
                }
                //如果不足
                if (stockQty < qty) {
                    //存贮差值
                    qty = qty - stockQty;
                    //调用插入一行的方法
                    list.add(oneRow);
                }
            }
        }
        return list;
	}  
	
	/** 
     * 得到无序号管理的物资
     * @param parm TParm
     * @return TParm
     */
    private TParm getBatchSeqInv(TParm parm,String oCode) {
    	INV inv = new INV();
        //科室代码
        String orgCode = oCode;
        //物资代码
        String invCode = parm.getValue("INV_CODE");
        //得到所有此物资的批次序号
        TParm result = inv.getAllStockQty(orgCode, invCode);
        
        return result;
    }
	
	
}
