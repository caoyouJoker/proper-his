package jdo.inv;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INVNewSterilizationTool extends TJDOTool{

	public static INVNewSterilizationTool instanceObject;
	
	public INVNewSterilizationTool() {
        setModuleName("inv\\INVNewSterilizationModule.x");
        onInit();
    }
	
	/**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static INVNewSterilizationTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVNewSterilizationTool();
        return instanceObject;
    }
	
    /**
     * 根据条件查询打包单信息
     * */
	public TParm queryRepack(TParm parm){
		
		TParm result = this.query("queryRepack", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	

	/**
     * 根据打包单号查询打包单信息
     * */
	public TParm queryRepackByRepackNo(TParm parm){
		
		TParm result = this.query("queryRepackByRepackNo", parm);
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
     * 根据条件查询灭菌单信息
     * */
	public TParm querySterilization(TParm parm){
		
		TParm result = this.query("querySterilization", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	

	

	/**
     * 根据灭菌单号查询回收单信息
     * */
	public TParm querySterilizationByNo(TParm parm){
		
		TParm result = this.query("querySterilizationBySterilizationNo", parm);
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
     * 新建灭菌单方法Sterilization
     * */
	public TParm insertSterilization(TParm parm, TConnection connection){
	
		
		TParm sterTable = parm.getParm("STERILIZATIONTABLE");			//灭菌表
		TParm packageMTable = parm.getParm("PACKAGEMAINTABLE");			//手术包表
		TParm result = new TParm();
		//插入灭菌单表      修改手术包状态  
		TParm insertValue = new TParm();
		insertValue.setData("STERILIZATION_NO",0,sterTable.getData("STERILIZATION_NO", 0));
		insertValue.setData("SEQ_NO",0,packageMTable.getData("PACK_SEQ_NO"));
		insertValue.setData("PACK_CODE",0,packageMTable.getData("PACK_CODE"));
		insertValue.setData("QTY",0,packageMTable.getData("QTY"));    
		insertValue.setData("STERILIZATION_NUM",0,packageMTable.getData("STERILIZATION_NUM"));
		insertValue.setData("STERILIZATION_POTSEQ",0,packageMTable.getData("STERILIZATION_POTSEQ"));
		insertValue.setData("STERILLZATION_PROGRAM",0,packageMTable.getData("STERILLZATION_PROGRAM"));
		insertValue.setData("STERILLZATION_DATE",0,packageMTable.getData("STERILLZATION_DATE"));
		insertValue.setData("STERILLZATION_USER",0,packageMTable.getData("STERILLZATION_USER"));
		insertValue.setData("AUDIT_DATE",0,packageMTable.getData("AUDIT_DATE"));
		insertValue.setData("AUDIT_USER",0,packageMTable.getData("AUDIT_USER"));
		insertValue.setData("OPT_USER",0,sterTable.getData("OPT_USER", 0));
		insertValue.setData("OPT_DATE",0,sterTable.getData("OPT_DATE", 0));
		insertValue.setData("OPT_TERM",0,sterTable.getData("OPT_TERM", 0));
		insertValue.setData("ORG_CODE",0,sterTable.getData("ORG_CODE", 0));
		insertValue.setData("BARCODE",0, packageMTable.getData("BARCODE"));
		
//		//用于修改状态不为“出库”状态的手术包状态start
//		TParm changeStatus = parm.getParm("RECYCLENO");	
//		changeStatus.setData("STERILIZATION_NO",0,sterTable.getData("STERILIZATION_NO", 0));
//		changeStatus.setData("PACK_SEQ_NO",0,packageMTable.getData("PACK_SEQ_NO"));
//		changeStatus.setData("PACK_CODE",0,packageMTable.getData("PACK_CODE"));
//		changeStatus.setData("ONCE_USE_FLG",0,"Y");
//		
//		
//		TParm statusParm = INVSterilizationHelpTool.getInstance().queryPackageStatus(changeStatus.getRow(0), connection);
//		
//		String statusStr =  statusParm.getData("STATUS").toString();
//		if(statusStr.equals("[0]")){
//			INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(changeStatus.getRow(0), connection);//删除已有的手术包明细
//		}else if(statusStr.equals("[2]")){
//			INVSterilizationHelpTool.getInstance().updatePackageDisStatus(changeStatus.getRow(0), connection);
//		}else if(statusStr.equals("[3]")){
//			INVSterilizationHelpTool.getInstance().updatePackageSterStatus(changeStatus.getRow(0), connection);
//		}
//		//用于修改状态不为“出库”状态的手术包状态end

//		String orgCode = sterTable.getData("ORG_CODE", 0).toString();
//		//扣库存问题
//		TParm tp = new TParm();
//		tp.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE"));
//		//查询手术包物资构成
//		TParm stockParm = INVSterilizationHelpTool.getInstance().queryPackDByPackCode(tp.getRow(0), connection);
//		boolean tag = true;	//该手术包所有物资库存是否够用
//		//检查每个手术包每种物资的库存
//		for(int j=0; j<stockParm.getCount();j++){	//每种物资
//			TParm stock = new TParm();
//			stock = stockParm.getRow(j);
//			tag = this.checkQty(stock.getData("INV_CODE").toString(),orgCode,Double.parseDouble(stock.getData("QTY").toString()));
//			if(!tag){
//				break;
//			}
//		}//每种物资
			
//		//如果库存够则重新打包
//		if(tag){
//			for(int j=0; j<stockParm.getCount();j++){	//每种物资
//				TParm stock = new TParm();
//				stock = stockParm.getRow(j);
//					
//				//更新主库库存
//				stock.setData("STOCK_QTY", stock.getDouble("QTY") * -1);
//				stock.setData("PACK_SEQ_NO",  packageMTable.getData("PACK_SEQ_NO"));
//				stock.setData("ONCE_USE_FLG",  "Y");
//				result = InvStockMTool.getInstance().updateStockMQty(stock,connection);
//					
//				List<TParm> stockDList = new ArrayList<TParm>();
//				stockDList = this.chooseBatchSeq(stock, orgCode);	//查询细库批号
//
////				INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(stock, connection);//删除已有的手术包明细
//				
//				//更新手术包物资细项PACKSTOCKD
//				for(int n=0;n<stockDList.size();n++){
//					TParm tt = new TParm();
//					tt = stockDList.get(n);
//					tt.setData("PACK_CODE", stock.getData("PACK_CODE"));
//					tt.setData("PACK_SEQ_NO", stock.getData("PACK_SEQ_NO"));
//					tt.setData("ONCE_USE_FLG", stock.getData("ONCE_USE_FLG"));
//					tt.setData("INVSEQ_NO", 0);
//					tt.setData("OPT_USER", sterTable.getData("OPT_USER", 0));
//					tt.setData("OPT_DATE", sterTable.getData("OPT_DATE", 0).toString().substring(0, 10));
//					tt.setData("OPT_TERM", sterTable.getData("OPT_TERM", 0));
//					result=INVSterilizationHelpTool.getInstance().insertPackageDetailInfo(tt, connection);
//					if (result.getErrCode() < 0) {
//						err(result.getErrCode() + " " + result.getErrText());
//						return result;
//					}
//				}
//				//更新细表库存
//				for(int m=0;m<stockDList.size();m++){
//					TParm tt = new TParm();
//					tt = stockDList.get(m);
//					tt.setData("STOCK_QTY", tt.getDouble("STOCK_QTY") * -1);
//					result = InvStockDTool.getInstance().updateStockQty(tt,connection);  
//					if (result.getErrCode() < 0) {
//						err(result.getErrCode() + " " + result.getErrText());
//						return result;
//					}
//				}
//					
//			}
//			insertValue.setData("STATUS", 0, "0" );   
//			insertValue.setData("FINISH_FLG",0,"Y");
//			TParm t = insertValue.getRow(0);
//			
//			result = INVSterilizationHelpTool.getInstance().insertSterilization(t, connection);
//			result = INVSterilizationHelpTool.getInstance().updatePackageStatus(t, connection);
//			
//		}else{//库存不够
//			
//			insertValue.setData("STATUS", 0, "3" );   
//			insertValue.setData("FINISH_FLG",0,"N");
//			TParm t = insertValue.getRow(0);
//			
//			result = INVSterilizationHelpTool.getInstance().insertSterilization(t, connection);
//			result = INVSterilizationHelpTool.getInstance().updatePackageStatus(t, connection);
//			
//		}
		//此时状态改变为4(在库已灭菌)
		//insertValue.setData("STATUS", 0, "0" );  
		insertValue.setData("STATUS", 0, "9" );          
		insertValue.setData("FINISH_FLG",0,"Y");
		TParm t = insertValue.getRow(0);  
		
		result = INVSterilizationHelpTool.getInstance().insertSterilization(t, connection);
		//回更INV_PACKSTOCKM  
		result = INVSterilizationHelpTool.getInstance().updatePackageStatus(t, connection);
		
//		//被引用的打包单号
//		TParm recParm =  parm.getParm("REPACK_NO");	  		
//		recParm.setData("FINISH_FLG", 0 ,"Y");
//		recParm.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE"));
//		recParm.setData("PACK_SEQ_NO", 0, packageMTable.getData("PACK_SEQ_NO"));
		
		
//		Object reNo = recParm.getRow(0).getData("REPACK_NO");
//		if(!(reNo==null)){		//如果是新建的灭菌单则无需修改回收单信息
//			//修改回收单状态为Y
//			result = INVSterilizationHelpTool.getInstance().updateDisinfectionFinishFlg(recParm.getRow(0), connection);
//		}
		
//		if(!tag){
//			result.setData("ENOUGH", "NO");
//		}else{
//			result.setData("ENOUGH", "YES");
//		}
		
		
		
		
		return result;
	}
	

//	//选择库存批次
//	private List<TParm> chooseBatchSeq(TParm parm,String orgCode){
////		System.out.println("parm555---"+parm);
//		List<TParm> list = new ArrayList<TParm>();	//存放明细物资扣库信息
////		TParm stockD = new TParm();		
//		//需要的物资数量
//        double qty = parm.getDouble("QTY");
////        System.out.println("qty---"+qty);
//        //得到所有此物资的批次序号
//        TParm result = this.getBatchSeqInv(parm,orgCode);
// //       System.out.println("result---"+result);
//        if (result == null || result.getErrCode() < 0)
//            return list;
//        //查出的物资批次个数
//        int rowCount = result.getCount();
//        //循环取出所有批次
//        for (int i = 0; i < rowCount; i++) {
//            //拿出一个
//            TParm oneRow = result.getRow(i);
//            double stockQty = oneRow.getDouble("STOCK_QTY");
//            //如果物资足够(首先不能为0)
//            if (stockQty > 0) {
//                if (stockQty >= qty) {
//                    oneRow.setData("STOCK_QTY", qty);
//                    //调用插入一行的方法
//                    list.add(oneRow);
//                    //够了就走
//                    return list;
//                }
//                //如果不足
//                if (stockQty < qty) {
//                    //存贮差值
//                    qty = qty - stockQty;
//                    //调用插入一行的方法
//                    list.add(oneRow);
//                }
//            }
//        }
//        return list;
//	}
//	
//	/**
//     * 得到无序号管理的物资
//     * @param parm TParm
//     * @return TParm
//     */
//    private TParm getBatchSeqInv(TParm parm,String oCode) {
//    	INV inv = new INV();
//        //科室代码
//        String orgCode = oCode;
//        //物资代码
//        String invCode = parm.getValue("INV_CODE");
//        //得到所有此物资的批次序号
//        TParm result = inv.getAllStockQty(orgCode, invCode);
//        
//        return result;
//    }
//	
//	//检查库存量
//	private boolean checkQty(String invCode,String orgCode,double qty){
//		INV inv = new INV();
//		//总库存量
//        double stockQty = inv.getStockQty(orgCode, invCode);
//        if (stockQty < 0 || qty > stockQty) {
//            return false;
//        }
//        return true;
//	}  

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
}
