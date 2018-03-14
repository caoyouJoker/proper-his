package jdo.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jdo.sys.SystemTool;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class INVNewRepackTool extends TJDOTool{

	public static INVNewRepackTool instanceObject;
	
	public INVNewRepackTool() {
        setModuleName("inv\\INVNewRepackModule.x");
        onInit();
    }
	
	/**
     * 得到实例
     * 
     * @return IndPurPlanMTool
     */
    public static INVNewRepackTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVNewRepackTool();
        return instanceObject;
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
     * 根据回收单号查询回收单信息    打包流程已用勿删
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
     * 根据灭菌单号查询回收单信息
     * */
	public TParm queryRepackByNo(TParm parm){
		
		TParm result = this.query("queryRepackByRepackNo", parm);
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
     * 新建打包单方法   打包流程已用勿删
     * */
	public TParm insertRepack(TParm parm, TConnection connection){
	
		String newBarcode = "";
		  
		TParm repackTable = parm.getParm("REPACK");			//打包表
		//System.out.println("repackTable"+repackTable);
		TParm packageMTable = parm.getParm("PACKAGEMAINTABLE");			//手术包表
		TParm HOMaterial = parm.getParm("HOMATERIAL");			//当前手术包打包需要的序管且一次性物品
		TParm result = new TParm();
		//插入打包单表      修改手术包状态  
		TParm insertValue = new TParm();
		insertValue.setData("REPACK_NO",0,repackTable.getData("REPACK_NO", 0));
		insertValue.setData("PACK_SEQ_NO",0,packageMTable.getData("PACK_SEQ_NO" ));
		insertValue.setData("PACK_CODE",0,packageMTable.getData("PACK_CODE" ));
		insertValue.setData("OLDBARCODE",0,packageMTable.getData("BARCODE" ));
		insertValue.setData("QTY",0,packageMTable.getData("QTY" ));
		insertValue.setData("AUDIT_DATE",0,packageMTable.getData("AUDIT_DATE").toString().substring(0, 19));
		//insertValue.setData("AUDIT_DATE",0,packageMTable.getData("AUDIT_DATE").toString().substring(0, 19));
		//insertValue.setData("AUDIT_USER",0,packageMTable.getData("AUDIT_USER" ));
		insertValue.setData("AUDIT_USER",0,repackTable.getData("OPT_USER",0));  
		insertValue.setData("REPACK_DATE",0,packageMTable.getData("REPACK_DATE").toString().substring(0, 19));
		//insertValue.setData("REPACK_DATE",0,packageMTable.getData("REPACK_DATE").toString().substring(0, 19));
		insertValue.setData("REPACK_USER",0,repackTable.getData("OPT_USER", 0));   
		//insertValue.setData("REPACK_USER",0,packageMTable.getData("REPACK_USER" )); 
		insertValue.setData("OPT_USER",0,repackTable.getData("OPT_USER", 0));
		insertValue.setData("OPT_DATE",0,repackTable.getData("OPT_DATE", 0).toString().substring(0, 19));  
		//insertValue.setData("OPT_DATE",0,repackTable.getData("OPT_DATE", 0).toString().substring(0, 19));
		insertValue.setData("OPT_TERM",0,repackTable.getData("OPT_TERM", 0));
		insertValue.setData("ORG_CODE",0,repackTable.getData("ORG_CODE", 0));
		
		//用于修改状态不为“出库”状态的手术包状态start
		TParm changeStatus = parm.getParm("RECYCLENO");	    
		changeStatus.setData("REPACK_NO",0,repackTable.getData("REPACK_NO", 0));
		changeStatus.setData("BARCODE",0,packageMTable.getData("BARCODE" ));
		changeStatus.setData("PACK_SEQ_NO",0,packageMTable.getData("PACK_SEQ_NO" ));
		changeStatus.setData("PACK_CODE",0,packageMTable.getData("PACK_CODE" )); 
		changeStatus.setData("ONCE_USE_FLG",0,"Y");
		TParm statusParm = INVRepackHelpTool.getInstance().queryPackageStatus(changeStatus.getRow(0), connection);
		String statusStr =  statusParm.getData("STATUS").toString();  
		if(statusStr.equals("[0]")){//在库状态
			//查询手术包中物资为一次性且为高值的
			TParm tp = INVRepackHelpTool.getInstance().queryMaterialAttr(changeStatus.getRow(0), connection);	
			
			//更新高值耗材的WAST_FLG为Y
			if(tp!=null){
				for(int i=0; i<tp.getCount("INV_CODE");i++){
					result = INVRepackHelpTool.getInstance().updateStockDDWastFlg(tp.getRow(i), connection);
					if (result.getErrCode() < 0) {
						System.out.println("[0]更新高值耗材的WAST_FLG为Y");
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
				}
			}
			
			result = INVRepackHelpTool.getInstance().deletePackageDetailInfo(changeStatus.getRow(0), connection);//删除已有的手术包明细
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}else if(statusStr.equals("[2]")){//消毒状态
			result = INVRepackHelpTool.getInstance().updatePackageDisStatus(changeStatus.getRow(0), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}else if(statusStr.equals("[3]")){//打包状态
			result = INVRepackHelpTool.getInstance().updateRepackStatus(changeStatus.getRow(0), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}else if(statusStr.equals("[4]")){//灭菌状态
			//查询手术包中物资为一次性且为高值的
			TParm tp = INVRepackHelpTool.getInstance().queryMaterialAttr(changeStatus.getRow(0), connection);	
			
			//更新高值耗材的WAST_FLG为Y
			if(tp!=null){
				for(int i=0; i<tp.getCount("INV_CODE");i++){
					result = INVRepackHelpTool.getInstance().updateStockDDWastFlg(tp.getRow(i), connection);
					if (result.getErrCode() < 0) {
						System.out.println("[4]更新高值耗材的WAST_FLG为Y");
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
				}
			}
			
			result = INVRepackHelpTool.getInstance().deletePackageDetailInfo(changeStatus.getRow(0), connection);//删除已有的手术包明细
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			result = INVRepackHelpTool.getInstance().updatePackageSterStatus(changeStatus.getRow(0), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}
		//用于修改状态不为“出库”状态的手术包状态end

		
		
		
		String orgCode = repackTable.getData("ORG_CODE", 0).toString();
		//扣库存问题
		TParm tp = new TParm();
		tp.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE" ));
		//查询手术包物资构成
		TParm stockParm = INVRepackHelpTool.getInstance().queryPackDByPackCode(tp.getRow(0), connection);
		boolean tag = true;	//该手术包所有物资库存是否够用
		//物资分类
		String invKind = "";
		//检查每个手术包每种物资的库存
		for(int j=0; j<stockParm.getCount();j++){	//每种物资
			TParm stock = new TParm();
			stock = stockParm.getRow(j);
			tag = this.checkQty(stock.getData("INV_CODE").toString(),orgCode,Double.parseDouble(stock.getData("QTY").toString()));
			if(!tag){
				break;
			}
		}//每种物资
			
		//如果库存够则重新打包
		if(tag){   
			//fux modify 20140425 不再重新取条码,单条码  
			String Barcode =  packageMTable.getValue("BARCODE");  
			System.out.println("Barcode:"+Barcode);   
			long old =Long.parseLong(Barcode) ;  
			long New = old+1;                    
			newBarcode =  New+"";         
			System.out.println("newBarcode::"+newBarcode); 
			//newBarcode = SystemTool.getInstance().getNo("ALL", "INV","INV_PACKSTOCKM", "No");  
			for(int j=0; j<stockParm.getCount();j++){	//每种物资 
				TParm stock = new TParm();
				stock = stockParm.getRow(j);     
				//更新主库库存
				stock.setData("STOCK_QTY", stock.getDouble("QTY") * -1);
				stock.setData("PACK_SEQ_NO",  packageMTable.getData("PACK_SEQ_NO" ));
				stock.setData("ONCE_USE_FLG",  "Y");
				result = InvStockMTool.getInstance().updateStockMQty(stock,connection);
				if (result.getErrCode() < 0) {
					err(result.getErrCode() + " " + result.getErrText());
					return result;
				}
					
				List<TParm> stockDList = new ArrayList<TParm>();
				stockDList = this.chooseBatchSeq(stock, orgCode);	//查询细库批号

//				INVSterilizationHelpTool.getInstance().deletePackageDetailInfo(stock, connection);//删除已有的手术包明细
				
				//更新手术包物资细项PACKSTOCKD
				for(int n=0;n<stockDList.size();n++){
					
					TParm tempTP = new TParm();
					tempTP = stockDList.get(n);
					
					TParm tt = new TParm();
					tt = stockDList.get(n);
					tt.setData("PACK_CODE", stock.getData("PACK_CODE"));
					tt.setData("PACK_SEQ_NO", stock.getData("PACK_SEQ_NO"));
					tt.setData("ONCE_USE_FLG", stock.getData("ONCE_USE_FLG")); 
					tt.setData("BARCODE", newBarcode);  
					tt.setData("INVSEQ_NO", 0);
					tt.setData("OPT_USER", repackTable.getData("OPT_USER", 0));
					tt.setData("OPT_DATE", repackTable.getData("OPT_DATE", 0).toString().substring(0,19));
					tt.setData("OPT_TERM", repackTable.getData("OPT_TERM", 0));
					
					tt.setData("QTY", tempTP.getData("STOCK_QTY"));
					tt.setData("USED_QTY", 0);
					tt.setData("NOTUSED_QTY", 0);
					tt.setData("RECOUNT_TIME", 0);
					tt.setData("COST_PRICE", tempTP.getData("COST_PRICE"));
					tt.setData("PACK_BATCH_NO", 0);
	
					if(stock.getData("DESCRIPTION") == null){
						tt.setData("DESCRIPTION", ""); 
					}
					else{
					    tt.setData("DESCRIPTION", stock.getData("DESCRIPTION")); 
					}
//					if(tempTP.getData("DESCRIPTION") == null){ 
//						tt.setData("DESCRIPTION", "");
//					}
//					else{
//						tt.setData("DESCRIPTION", stock.getData("DESCRIPTION"));  
//					}
					//向packstockD表插入数据
    			    String invCode = tt.getValue("INV_CODE");   
    				TParm invBaseKind =   new TParm (TJDODBTool.getInstance().select(getInvKind(invCode))); 
    				invKind = invBaseKind.getValue("INVKIND_CODE",0).toString(); 
    				//不是耐用品 ,即一次性用品是才插入PackStockD
					if(!"C".equals(invKind)){
					result=INVRepackHelpTool.getInstance().insertPackageDetailInfo(tt, connection);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
					}
					//新增 向history表存入数据(插入历史表 全部插入)
					result=InvPackStockMDHistoryTool.getInstance().onInsertRepackPackDHistory(tt,connection);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					
					}
					
					
					
				}
				
				//更新细表库存
				for(int m=0;m<stockDList.size();m++){
					TParm tt = new TParm();
					tt = stockDList.get(m);
					tt.setData("STOCK_QTY", tt.getDouble("STOCK_QTY") * -1);
					result = InvStockDTool.getInstance().updateStockQty(tt,connection);  
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
				}
				
			}
			
			//查询packstockD表中非一次性物品并插入到history表 start
			TParm bar = new TParm();
			bar.setData("BARCODE", packageMTable.getData("BARCODE" ));
			result=INVRepackHelpTool.getInstance().queryPackageDInfoByBarcode(bar);
			System.out.println("result.getCount---"+result.getCount("BARCODE"));
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			for(int m=0;m<result.getCount("BARCODE");m++){
				result.setData("BARCODE", m, newBarcode);
				result.setData("OPT_USER", m, repackTable.getData("OPT_USER", 0));
				result.setData("OPT_DATE", m, repackTable.getData("OPT_DATE", 0).toString().substring(0,19));
				result.setData("OPT_TERM", m, repackTable.getData("OPT_TERM", 0));
				result.setData("USED_QTY", m, 0);
				result.setData("NOTUSED_QTY", m, 0);
				  
				if(result.getData("DESCRIPTION", m) == null){
					result.setData("DESCRIPTION", m, "");
				} 
				else{   
					result.setData("DESCRIPTION", m, result.getData("DESCRIPTION", m)); 
				} 
				
				System.out.println("非一次性物品---"+result);
				TParm pm = InvPackStockMDHistoryTool.getInstance().onInsertRepackPackDHistory(result.getRow(m),connection);
				if (pm.getErrCode() < 0) {
					err(pm.getErrCode() + " " + pm.getErrText());
					return pm;
				}
			}
			//查询packstockD表中非一次性物品并插入到history表 end
			
			//更新手术包物资细项PACKSTOCKD中的序管且一次性物资
			if(HOMaterial!=null&&HOMaterial.getCount("RFID")!=-1){
				System.out.println("进入到 高值且一次性物资处理");
				for(int n=0;n<HOMaterial.getCount("RFID");n++){
					//查询rfid物资信息
					TParm material = INVRepackHelpTool.getInstance().queryMaterialByRFID(HOMaterial.getRow(n));
					
					material.setData("RFID", 0, HOMaterial.getRow(n).getValue("RFID"));
					material.setData("PACK_CODE", 0, HOMaterial.getRow(n).getValue("PACK_CODE"));
					material.setData("PACK_SEQ_NO", 0, HOMaterial.getRow(n).getValue("PACK_SEQ_NO"));
					material.setData("ONCE_USE_FLG", 0, "Y");
					material.setData("BARCODE", 0, newBarcode);
					material.setData("OPT_USER", 0, repackTable.getData("OPT_USER", 0));
					material.setData("OPT_DATE", 0, repackTable.getData("OPT_DATE", 0).toString().substring(0,19));
					material.setData("OPT_TERM", 0, repackTable.getData("OPT_TERM", 0));
					material.setData("PACK_FLG", 0, "Y");
					
					
					//更新stockdd表物资状态
					result = InvStockDDTool.getInstance().updatePackAge(material.getRow(0),connection);   
			        if (result.getErrCode() < 0) {
			            err(result.getErrCode() + " " + result.getErrText());
			            return result;
			        }
					
					//更新packstockdd  高值一次性用品是否同低值？？？
					//向packstockD表插入数据
    			    String invCode = material.getValue("INV_CODE");   
    				TParm invBaseKind =   new TParm (TJDODBTool.getInstance().select(getInvKind(invCode))); 
    				invKind = invBaseKind.getValue("INVKIND_CODE",0).toString(); 
    				//不是耐用品 ,即一次性用品是才插入PackStockD
					if(!"C".equals(invKind)){
					result=INVRepackHelpTool.getInstance().insertPackageDetailInfo(material.getRow(0), connection);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
					}
					material.setData("STOCK_QTY", 0, Integer.parseInt(material.getData("STOCK_QTY",0).toString())*-1);
					
					//更新stockm数量
					result = InvStockMTool.getInstance().updateStockMQty(material.getRow(0),connection);
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
					//更新stockd数量
					result = InvStockDTool.getInstance().updateStockQty(material.getRow(0),connection);  
					if (result.getErrCode() < 0) {
						err(result.getErrCode() + " " + result.getErrText());
						return result;
					}
					
					
				}
			}
			
			TParm qValid = new TParm();
			parm.setData("BARCODE", packageMTable.getData("BARCODE" ));
			TParm dParm = INVNewBackDisnfectionTool.getInstance().queryPackageInfoByAllBarcode(parm);	//查询手术包信息（根据条码）
			System.out.println("dParm:"+dParm);  
			int valid = Integer.parseInt(dParm.getData("VALUE_DATE",0).toString()); 	//有效期限   一般是“天”单位  
			Timestamp date = SystemTool.getInstance().getDate();
			Calendar cal = new GregorianCalendar();  
			cal.setTime(date);
	        cal.add(cal.DATE,valid);//把日期往后增加N天
	        Date d=cal.getTime(); 
	        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        String dateString = formatter.format(d);
	        insertValue.setData("VALID_DATE", 0,  dateString);  
			insertValue.setData("BARCODE", 0,  newBarcode);    
			insertValue.setData("STATUS", 0, "3" );   
			insertValue.setData("FINISH_FLG",0,"Y");  
			TParm t = insertValue.getRow(0);  
			System.out.println("t::::::Toollll：：：：："+t);  
			result = INVRepackHelpTool.getInstance().insertRepack(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			
			result = INVRepackHelpTool.getInstance().updatePackageStatus(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			
			//根据原条码查询packstockm表数据
			TParm bars = new TParm();
			bars.setData("BARCODE", packageMTable.getData("BARCODE" ));
			result = INVRepackHelpTool.getInstance().queryPackageInfoByBarcode(bars);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			
			if(result.getData("DESCRIPTION", 0) == null){
				result.setData("DESCRIPTION", 0, "");
			}
			result.setData("OPT_DATE", 0, result.getData("OPT_DATE", 0).toString().substring(0,19));
			result.setData("BARCODE", 0, newBarcode);
			//更新historyM表
			result = InvPackStockMDHistoryTool.getInstance().onInsertRepackPackMHistory(result.getRow(0), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			
			//更新条形码
			result = INVRepackHelpTool.getInstance().updatePackStockMBarcode(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			result = INVRepackHelpTool.getInstance().updatePackStockDBarcode(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
			

		}else{//库存不够
			
			insertValue.setData("BARCODE", 0,  packageMTable.getData("BARCODE" ));  
			insertValue.setData("STATUS", 0, "3" );   
			insertValue.setData("FINISH_FLG",0,"N");
			TParm t = insertValue.getRow(0);
			
			result = INVRepackHelpTool.getInstance().insertRepack(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}  
			result = INVRepackHelpTool.getInstance().updatePackageStatus(t, connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}  
			
		}
		//被引用的回收单号 
		TParm recParm =  parm.getParm("RECYCLENO");			  
		recParm.setData("FINISH_FLG", 0 ,"Y");  
		recParm.setData("PACK_CODE", 0, packageMTable.getData("PACK_CODE" ));  
		recParm.setData("PACK_SEQ_NO", 0, packageMTable.getData("PACK_SEQ_NO" ));
		
		
		Object recNo = recParm.getRow(0).getData("RECYCLENO");
		if(!(recNo==null)){		//如果是新建的打包单则无需修改回收单信息
			//修改回收单状态为Y
			result = INVRepackHelpTool.getInstance().updateDisinfectionFinishFlg(recParm.getRow(0), connection);
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				return result;
			}
		}
		 
		if(!tag){
			result.setData("ENOUGH", "NO");
		}else{  
			result.setData("ENOUGH", "YES");
		}
		return result;
	}
	
	private String getInvKind(String invCode) {
		String sql = " SELECT INVKIND_CODE FROM INV_BASE " +
				" WHERE INV_CODE = '"+invCode+"'"; 
		return sql;
	}
	
	
	//选择库存批次
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
	
	//检查库存量
	private boolean checkQty(String invCode,String orgCode,double qty){
		INV inv = new INV();
		//总库存量
        double stockQty = inv.getStockQty(orgCode, invCode);
        if (stockQty < 0 || qty > stockQty) {
            return false;
        }
        return true;
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
     * 查询手术包构成
     * */
	public TParm queryPackList(TParm parm){
		TParm result = this.query("queryPackList", parm);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
	}
	
	/**
     * 查询配置单需要信息
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
	
}
