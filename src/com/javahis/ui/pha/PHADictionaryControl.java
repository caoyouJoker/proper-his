package com.javahis.ui.pha;

import jdo.spc.bsm.ConsisServiceSoap_ConsisServiceSoap_Client;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.bsm.XmlUtils;

/**
*
* <p>Title: 同步字典类</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2013</p>
*
* <p>Company: JavaHis</p>
*
* @author chenx 2013.05.28 
* @version 4.0
*/
public class PHADictionaryControl extends TControl{     
	
	public void onQuery(){
		String xml = "" ;
		TParm result = new TParm() ;
		String  sql = " SELECT A.ORDER_CODE AS BAR_CODE,A.ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION ,C.UNIT_CHN_DESC,B.MAN_CHN_DESC," +
	      " A.OWN_PRICE,D.CHN_DESC,A.OPT_DATE,A.PY1,E.CTRLDRUGCLASS_CHN_DESC," +
	      " A.ACTIVE_FLG ,F.DOSAGE_QTY,G.HIS_ORDER_CODE AS ORDER_CODE FROM SYS_FEE  A ,PHA_BASE B ,SYS_UNIT C ,(SELECT *  FROM SYS_DIC_FOROUT WHERE GROUP_ID = 'PHA_DOSE' ) D," +
	      " SYS_CTRLDRUGCLASS  E,PHA_TRANSUNIT F,SYS_FEE_SPC G WHERE A.CAT1_TYPE = 'PHA'   AND  A.ORDER_CODE = B.ORDER_CODE " +
	      " AND A.UNIT_CODE = C.UNIT_CODE " +
	      " AND B.CTRLDRUGCLASS_CODE = E.CTRLDRUGCLASS_CODE(+) AND B.DOSE_CODE = D.ID(+) " +
	      " AND A.ORDER_CODE = F.ORDER_CODE(+)" +
		 " AND A.ORDER_CODE = G.ORDER_CODE(+)" ;
//		System.out.println("sql======="+sql);
      TParm  parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
      if(parm.getCount()<0){
    	  this.messageBox("查无数据") ;
    	  return ;
      }
      ConsisServiceSoap_ConsisServiceSoap_Client client =  new ConsisServiceSoap_ConsisServiceSoap_Client()  ;
     String outxml = "" ;
     TParm returnParm = new TParm() ;
      for(int i=0;i<parm.getCount();i++){
    	  result.addRowData(parm, i) ;
    	  if(i%100==0 && i!=0){
    		  result =onTitle(result, "101") ;
    	 xml = 	XmlUtils.onSysFeeDictionary(result).toString() ;
//    	 System.out.println("xml==========101========"+xml);
    	 outxml = client.onTransConsisData(xml) ;
//    	 System.out.println("xml======传回xml==101========"+outxml);
    	 if(outxml.equals("err")){
 			this.messageBox("webservices 链接错误") ;
 			return  ;
 		}
    	 returnParm = XmlUtils.createXmltoParm(outxml) ;
    	 if(returnParm.getErrCode()!=1){
    		 this.messageBox("同步字典失败！") ;
    		 return ;
    	 }
    		  result = new TParm() ;
    	  }
    	  if(i>parm.getCount()/100*100 && i==parm.getCount()-1){
    		  result =onTitle(result, "101") ;
      		xml = XmlUtils.onSysFeeDictionary(result).toString() ;
//      		 System.out.println("xml==========101========"+xml);
        	 outxml = client.onTransConsisData(xml) ;
//        	 System.out.println("xml======传回xml==101======"+outxml);
        	 if(outxml.equals("err")){
     			this.messageBox("webservices 链接错误") ;
     			return  ;
     		}
        	 returnParm = XmlUtils.createXmltoParm(outxml) ;
        	 if(returnParm.getErrCode()!=1){
        		 this.messageBox("同步字典失败！") ;
        		 return ;
        	 }
    	  }
      }
      if(returnParm.getErrCode()==1){
 		 this.messageBox("同步药品字典成功！") ;
 		 return ;
 	 }

	}
	public void onQueryStock(){
		String xml = "" ;
		TParm result = new TParm() ;
		String sql = " SELECT A.ORDER_CODE,B.MATERIAL_LOC_CODE FROM SYS_FEE A,IND_STOCKM B " +
				     " WHERE  A.ORDER_CODE = B.ORDER_CODE AND A.CAT1_TYPE = 'PHA' " +
				     " AND    A.ACTIVE_FLG = 'Y'  AND B.ORG_CODE = '040102' " ;
		 TParm  parm = new TParm(TJDODBTool.getInstance().select(sql)) ;
	      if(parm.getCount()<0){
	    	  this.messageBox("查无数据") ;
	    	  return ;
	      }
	      ConsisServiceSoap_ConsisServiceSoap_Client client =  new ConsisServiceSoap_ConsisServiceSoap_Client()  ;
	      String outxml = "" ;
	      TParm returnParm = new TParm() ;
	      for(int i=0;i<parm.getCount();i++){
	    	  result.addRowData(parm, i) ;
	    	  if(i%100==0 && i!=0){
	    		  result =onTitle(result, "102") ;
	      	xml = 	XmlUtils.onStockDictionary(result).toString() ;
	      
	    	 outxml = client.onTransConsisData(xml) ;
	    	
	    	 if(outxml.equals("err")){
	 			this.messageBox("webservices 链接错误") ;
	 			return  ;
	 		}
	    	 returnParm = XmlUtils.createXmltoParm(outxml) ;
	    	 if(returnParm.getErrCode()!=1){
	    		 this.messageBox("同步字典失败！") ;
	    		 return ;
	    	 }
	    		  result = new TParm() ;
	    	  }
	    	  if(i>parm.getCount()/100*100 &&  i==parm.getCount()-1){
	    		  result =onTitle(result, "102") ;
	      		xml = XmlUtils.onStockDictionary(result).toString() ;
	      		
		    	 outxml = client.onTransConsisData(xml) ;
		    	
		    	 if(outxml.equals("err")){
		 			this.messageBox("webservices 链接错误") ;
		 			return  ;
		 		}
		    	 returnParm = XmlUtils.createXmltoParm(outxml) ;
		    	 if(returnParm.getErrCode()!=1){
		    		 this.messageBox("同步字典失败！") ;
		    		 return ;
		    	 }
	    	  }
	      }
	      if(returnParm.getErrCode()==1){
	    		 this.messageBox("同步料位字典成功！") ;
	    	 }
	}
	/**
	 * parm 赋表头值
	 */
	public TParm onTitle(TParm parm,String way){
		parm.setData("OPT_TERM", Operator.getIP()) ;
		parm.setData("OPT_USER", Operator.getName()) ;
		parm.setData("OPT_CODE", Operator.getID()) ;
		parm.setData("WAY", way) ;
		parm.setData("OPWINID", "") ;
		return parm ;
	}
}
