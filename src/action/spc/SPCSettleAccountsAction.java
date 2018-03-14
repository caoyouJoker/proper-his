package action.spc;

import java.util.ArrayList;
import java.util.List;

import jdo.spc.SPCSettleAccountsTool;
import action.spc.accountclient.AccountClient;
import action.spc.accountclient.IndAccount;
import action.spc.accountclient.IndAccounts;
import action.spc.accountclient.IndCodeMap;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class SPCSettleAccountsAction extends TAction {

	
	public TParm onSave(TParm parm) {
		TConnection conn = getConnection();
		TParm result = SPCSettleAccountsTool.getInstance().onSave(parm,conn);
		if (result.getErrCode() < 0) {			
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback();
            conn.close();
            return result ;
        }	
		conn.commit();
        conn.close();
//		result = onSaveHis(parm);//wanglong delete 20150202
        if (result.getErrCode() >= 0) {// wanglong delete 20150202
            result.setErrText("结算成功");
        }
        return result;
	}
	
	public TParm onSaveHis(TParm parm){
		TParm result = new TParm() ;
		//组装调用WEBSERVICES数据 ，传LIST
		List<IndAccount> list = new ArrayList<IndAccount>(); 
		IndAccounts accounts = new IndAccounts();
		//String hisOrderCode = "";
		String supCode = "";
		for(int i = 0 ; i < parm.getCount("ORDER_CODE") ; i++ ){
			TParm rowParm = parm.getRow(i);
			IndAccount ia = new IndAccount() ;
			
			if(supCode == null || supCode.equals("")){
				supCode= rowParm.getValue("SUP_CODE");
			}
			
			
			ia.setCloseDate(rowParm.getValue("CLOSE_DATE"));
			ia.setOrgCode(rowParm.getValue("ORG_CODE"));
			
			ia.setOrderCode(rowParm.getValue("ORDER_CODE") );
			ia.setLastOddQty(rowParm.getDouble("LAST_ODD_QTY"));
			ia.setOutQty(rowParm.getDouble("OUT_QTY"));
			
			ia.setTotalOutQty(rowParm.getDouble("TOTAL_OUT_QTY"));
			ia.setTotalUnitCode(rowParm.getValue("TOTAL_UNIT_CODE"));
			ia.setVerifyinPrice(rowParm.getDouble("VERIFYIN_PRICE"));
			ia.setVerifyinAmt(rowParm.getDouble("VERIFYIN_AMT"));
			
			TParm searchParm = new TParm() ;
			searchParm.setData("SUP_CODE",supCode);
			searchParm.setData("SUP_ORDER_CODE",rowParm.getValue("SUP_ORDER_CODE"));
			TParm indCodeMapParm = SPCSettleAccountsTool.getInstance().onQueryIndCodeMapBy(searchParm);
			long conversionRation = 1;
			try{
				conversionRation = indCodeMapParm.getLong("CONVERSION_RATIO");
			}catch (Exception e) {
				// TODO: handle exception
				conversionRation = 1;
			}
			long accountQty = rowParm.getLong("ACCOUNT_QTY");
			long qty = conversionRation * accountQty ;
			ia.setAccountQty(qty);
			
			ia.setAccountUnitCode(rowParm.getValue("ACCOUNT_UNIT_CODE"));
			ia.setOddQty(rowParm.getDouble("ODD_QTY"));
			ia.setOddAmt(rowParm.getDouble("ODD_AMT"));
			ia.setOptUser(rowParm.getValue("OPT_USER"));
			ia.setOptDate(rowParm.getValue("OPT_DATE"));
			
			ia.setOptTerm(rowParm.getValue("OPT_TERM"));
			ia.setIsUpdate(rowParm.getValue("IS_UPDATE"));
			ia.setSupCode(rowParm.getValue("SUP_CODE"));
			ia.setSupOrderCode(rowParm.getValue("SUP_ORDER_CODE"));
			ia.setContractPrice(rowParm.getDouble("CONTRACT_PRICE"));
			
			
			list.add(ia);
		}
		accounts.setIndAccounts(list);

		TParm searchParm = new TParm();
		searchParm.setData("SUP_CODE",supCode);
		TParm codeMapParm = SPCSettleAccountsTool.getInstance().onQueryIndCodeMap(searchParm);
		
		List<IndCodeMap> codeMapList = new ArrayList<IndCodeMap>(); 
		for(int i = 0 ; i < codeMapParm.getCount() ; i++ ){
			TParm rowParm = codeMapParm.getRow(i);
			IndCodeMap icm = new IndCodeMap() ;
			icm.setSupCode(rowParm.getValue("SUP_CODE"));
			icm.setSupOrderCode(rowParm.getValue("SUP_ORDER_CODE"));
			icm.setOrderCode(rowParm.getValue("ORDER_CODE"));
			icm.setSupplyUnitCode(rowParm.getValue("SUPPLY_UNIT_CODE"));
			icm.setConversionRatio(rowParm.getDouble("CONVERSION_RATIO"));
			icm.setOptDate(rowParm.getValue("OPT_DATE"));
			icm.setOptUser(rowParm.getValue("OPT_USER"));
			icm.setOptTerm(rowParm.getValue("OPT_TERM"));
			
			codeMapList.add(icm);
		}
		
		
		accounts.setIndCodeMaps(codeMapList);                                                  
		System.out.println("accounts.getIndCodeMaps----:"+accounts.getIndCodeMaps().size());
		String resultStr = "";   
		try{
			resultStr  =  AccountClient.onSaveIndAccount(accounts);
			if(resultStr != null && resultStr.equals("-1")){
				result.setErrCode(-1);
				result.setErrText("结算成功!传送结算数据到HIS失败需同步!");
			}else{
				result.setErrText("结算、传送结算数据成功!");
			}
		}catch (Exception e) {
			// TODO: handle exception
			result.setErrCode(-1);
			result.setErrText("结算成功!传送结算数据到HIS失败需同步!");
			e.printStackTrace();
		}
		
		return result ;
	}
	
	public TParm onDelete(TParm parm){
		TConnection conn = getConnection();
		TParm result = SPCSettleAccountsTool.getInstance().onDelete(parm, conn);
		if (result.getErrCode() < 0) {			
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.rollback();
            conn.close();
            return result ;
        }	
		conn.commit();
        conn.close();
        return result;
	}
	
	/**
	 * 同步数据到HIS
	 * @param parm
	 * @return
	 */
	public TParm onSynchronous(TParm parm) {
		TParm result = new TParm();
		
		List<IndAccount> list = new ArrayList<IndAccount>(); 
		IndAccounts accounts = new IndAccounts();
		//String hisOrderCode = "";
		
		String supCode = "";
		for(int i = 0 ; i < parm.getCount("ORDER_CODE") ; i++ ){
			TParm rowParm = parm.getRow(i);
			
			
			if(supCode == null || supCode.equals("")){
				supCode = rowParm.getValue("SUP_CODE");
			}
			double accountQty = rowParm.getDouble("ACCOUNT_QTY"); 
			
			if(accountQty >  0 ){
				IndAccount ia = new IndAccount() ;
				
				ia.setCloseDate(rowParm.getValue("CLOSE_DATE"));
	
				ia.setOrderCode(rowParm.getValue("ORDER_CODE") );
				
				ia.setOrgCode(rowParm.getValue("ORG_CODE"));
				
				ia.setLastOddQty(rowParm.getDouble("LAST_ODD_QTY"));
				ia.setOutQty(rowParm.getDouble("OUT_QTY"));
				
				ia.setTotalOutQty(rowParm.getDouble("TOTAL_OUT_QTY"));
				ia.setTotalUnitCode(rowParm.getValue("TOTAL_UNIT_CODE"));
				ia.setVerifyinPrice(rowParm.getDouble("VERIFYIN_PRICE"));
				ia.setVerifyinAmt(rowParm.getDouble("VERIFYIN_AMT"));
				
				TParm searchParm = new TParm() ;
				searchParm.setData("SUP_CODE",supCode);
				searchParm.setData("SUP_ORDER_CODE",rowParm.getValue("SUP_ORDER_CODE"));
				 
				TParm indCodeMapParm = SPCSettleAccountsTool.getInstance().onQueryIndCodeMapBy(searchParm);
				double conversionRation = 1;
				try{
					conversionRation = indCodeMapParm.getDouble("CONVERSION_RATIO");
				}catch (Exception e) {
					// TODO: handle exception
					conversionRation = 1;
				}
				 
				double qty = (conversionRation * accountQty) ;
				ia.setAccountQty(((Double)qty).longValue());
				ia.setAccountUnitCode(rowParm.getValue("ACCOUNT_UNIT_CODE"));
				ia.setOddQty(rowParm.getDouble("ODD_QTY"));
				ia.setOddAmt(rowParm.getDouble("ODD_AMT"));
				ia.setOptUser(rowParm.getValue("OPT_USER"));
				ia.setOptDate(rowParm.getValue("OPT_DATE"));
				ia.setSupCode(rowParm.getValue("SUP_CODE"));
				ia.setSupOrderCode(rowParm.getValue("SUP_ORDER_CODE"));
				ia.setContractPrice(rowParm.getDouble("CONTRACT_PRICE"));
				ia.setOptTerm(rowParm.getValue("OPT_TERM"));
				ia.setIsUpdate(rowParm.getValue("IS_UPDATE"));
				list.add(ia);	
			}
		}
		 
		accounts.setIndAccounts(list);
		 
		
		TParm  searchParm = new TParm();
		searchParm.setData("SUP_CODE",supCode);
		TParm codeMapParm = SPCSettleAccountsTool.getInstance().onQueryIndCodeMap(searchParm);
		
		List<IndCodeMap> codeMapList = new ArrayList<IndCodeMap>(); 
		for(int i = 0 ; i < codeMapParm.getCount() ; i++ ){
			TParm rowParm = codeMapParm.getRow(i);
			IndCodeMap icm = new IndCodeMap() ;
			icm.setSupCode(rowParm.getValue("SUP_CODE"));
			icm.setSupOrderCode(rowParm.getValue("SUP_ORDER_CODE"));
			icm.setOrderCode(rowParm.getValue("ORDER_CODE"));
			icm.setSupplyUnitCode(rowParm.getValue("SUPPLY_UNIT_CODE"));
			icm.setConversionRatio(rowParm.getDouble("CONVERSION_RATIO"));
			icm.setOptDate(rowParm.getValue("OPT_DATE"));
			icm.setOptUser(rowParm.getValue("OPT_USER"));
			icm.setOptTerm(rowParm.getValue("OPT_TERM"));
			
			codeMapList.add(icm);
		}
		
		accounts.setIndCodeMaps(codeMapList);
		
		
		//调用服务端
		try{
			String resultStr =  AccountClient.onSaveIndAccount(accounts);
			if(resultStr != null && resultStr.equals("-1")){
				result.setErrText("重送数据到HIS失败需重送");
				result.setErrCode(-1);
			}else{
				result.setErrText("重送数据成功");
			}
		}catch (Exception e) {
			// TODO: handle exception
			result.setErrText("连接服务失败，需重送");
			result.setErrCode(-1);
		}
		
		return result ;
		
	}
}
