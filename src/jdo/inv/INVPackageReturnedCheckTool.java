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
     * 构造器
     */
    public INVPackageReturnedCheckTool() {
        setModuleName("inv\\INVPackageReturnedCheckModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static INVPackageReturnedCheckTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVPackageReturnedCheckTool();
        return instanceObject;
    }
    
    /**
     * 新建诊疗包退货单（保存主、细表）
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
     * 查询诊疗包退货单主表
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
     * 查询诊疗包退货单细表
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
        	System.out.println("主项失败:---------" + parm.getParm("RETURNM"));
        	err(result.getErrCode() + " " + result.getErrText());
        	return result;
        }
		
        TParm tp = parm.getParm("RETURND");
        
		for(int i=0;i<tp.getCount("PACK_CODE")-1;i++){
    		result = this.update("insertPReturnD", tp.getRow(i), connection);
            if (result.getErrCode() < 0){
            	System.out.println("细项失败:---------" + tp);
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

		//加回手术包库存
		for(int i=0;i<parm.getCount("PACK_CODE");i++){
    		result = this.update("updatePackstockMQty", parm.getRow(i), connection);
            if (result.getErrCode() < 0){
            	System.out.println("第一步错误-------------------------------");
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
            
            result = this.query("queryPackD", parm.getRow(i));
            if (result.getErrCode() < 0){
            	System.out.println("第二步错误-------------------------------");
            	err(result.getErrCode() + " " + result.getErrText());
            	return result;
            }
            
            for(int j=0;j<result.getCount("INV_CODE");j++){
            	result.setData("QTY", j, result.getDouble("QTY", j)*parm.getDouble("QTY", i));
            	TParm t = this.update("updatePackstockDQty", result.getRow(j));
            	if (t.getErrCode() < 0){
            		System.out.println("第三步错误-------------------------------");
                	err(t.getErrCode() + " " + t.getErrText());
                	return t;
                }
            }
    	}
		
		TParm supD = new TParm();	//需更新的inv_sup_dispensed数据
		TParm supDD = new TParm();	//需更新的inv_sup_dispensedd数据
		
		//减去已出库到料位的inv_sup_dispensed库存
		for(int i=0;i<parm.getCount("PACK_CODE");i++){
			
			System.out.println("需扣库手术包信息-------"+parm.getRow(i));  //需退货手术包细项

			//查询库存情况
			TParm dTP = this.query("querySupDispense", parm.getRow(i));
			if (dTP.getErrCode() < 0){
            	err(dTP.getErrCode() + " " + dTP.getErrText());
            	return dTP;
            }

			
			double qty = parm.getRow(i).getDouble("QTY");  //需消减包数量

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
		
		
		//扣除inv_sup_dispensedd手术包细项
		for(int i=0;i<supD.getCount("INV_CODE");i++){
			
			double stockQty = 0;
			
			//查询手术包配置
			TParm packTP = this.query("queryPackDSec", supD.getRow(i));
	        if (packTP.getErrCode() < 0){
	        	err(packTP.getErrCode() + " " + packTP.getErrText());
	        	return packTP;
	        }
	        
	        for(int m=0;m<packTP.getCount("INV_CODE");m++){
	        	stockQty = supD.getRow(i).getDouble("QTY") * -1 * packTP.getRow(m).getDouble("QTY");
	        	
	        	TParm tp = supD.getRow(i);
	        	tp.setData("INV_CODE_SEC", packTP.getRow(m).getValue("INV_CODE"));
	        	
	        	System.out.println("查询dd的条件："+tp);
	        	
	        	//查询库存细项情况   2013-12-27增加
				TParm ddTP = this.query("querySupDispenseDD", tp);
				if (ddTP.getErrCode() < 0){
		        	err(ddTP.getErrCode() + " " + ddTP.getErrText());
		        	return ddTP;
		        }
				
				System.out.println("inv_sup_dispdd的内容："+ddTP);
	        	
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
		
		System.out.println("手术包扣库信息-------"+supD);
		System.out.println("手术包细项扣库信息-------"+supDD);
		
		
		
		for(int i = 0;i<supD.getCount("INV_CODE");i++){
			TParm t = this.update("updateSupDispenseD", supD.getRow(i));
        	if (t.getErrCode() < 0){
        		System.out.println("扣细表错误-------------------------------");
            	err(t.getErrCode() + " " + t.getErrText());
            	return t;
            }
		}
		
		for(int i = 0;i<supDD.getCount("INV_CODE");i++){
			TParm t = this.update("updateSupDispenseDD", supDD.getRow(i));
        	if (t.getErrCode() < 0){
        		System.out.println("扣细细表错误-------------------------------");
            	err(t.getErrCode() + " " + t.getErrText());
            	return t;
            }
		}
		

    	
    	return result;
		
	}
	
	
}
