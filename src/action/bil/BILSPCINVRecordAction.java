package action.bil;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.commons.net.ntp.TimeStamp;


//import jdo.bil.BILSPCINVDto;
//import jdo.bil.BILSPCINVDtos;
import jdo.bil.BILSPCINVWsTool;
import jdo.bil.BILSPCINVWsToolImpl;
import jdo.bil.BILSPCINVRecordTool;
import jdo.bil.String2TParmTool;
import jdo.bil.client.BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client;

import jdo.ibs.IBSTool;

import jdo.sys.Operator;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

public class BILSPCINVRecordAction extends TAction{
	
	/**
	 * 根据病案号查询
	 * */
	public TParm onMrNo(TParm parm){
		TParm result = new TParm();
		String mrNo = parm.getData("MR_NO").toString();
		String admType = parm.getData("ADM_TYPE").toString();
	    System.out.println("parm---->"+parm);
	    BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client BILSPCINVWsToolImplClient = new BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client();					
		String returnDto=  BILSPCINVWsToolImplClient.onMrNo(mrNo, admType);
		String2TParmTool tool = new String2TParmTool();
		result = tool.string2Ttparm(returnDto);
		
			
		System.out.println("action--->"+result);
		return result;
	}
	/**
	 * 保存计费
	 * */
    public TParm onSaveFee(TParm parms){
    	TParm result = new TParm();
    	TConnection connection = getConnection();
    	TParm resultParm = new TParm();
   	    TParm IBSOrddParm = parms;
	     System.out.println("IBSOrddParm::"+IBSOrddParm);
	     //根据开关 spc_flg判断是否是物联网   如果是物联网，呼叫HIS的WS接口，进行计费，否则，直接计费
	     String sqlSpcFlg = "SELECT SPC_FLG FROM SYS_REGION WHERE REGION_CODE = '"+parms.getData("REGION_CODE")+"'";
	     System.out.println(sqlSpcFlg);
	     TParm spcFlgParm = new TParm(TJDODBTool.getInstance().select(sqlSpcFlg));
	     System.out.println(spcFlgParm);
	     if(spcFlgParm.getErrCode()<0){
	    	 connection.rollback();
	         connection.close();
	         return result;
	     }
	     //是物联网  调用webservice接口
	     if(spcFlgParm.getData("SPC_FLG",0).toString().equals("Y")){
	     if(!(IBSOrddParm.getData("CASE_NO",0)==null || IBSOrddParm.getData("CASE_NO",0).equals(""))){
	    	 //住院
	    	 if(IBSOrddParm.getData("ADM_TYPE").equals("I")){
	    		// 调用IBS接口返回费用数据
					
					//**************************************************
					//Map groupData = forIBSParm1.getGroupData("");
					//groupData = (Map)forIBSParm1;
	    			BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client BILSPCINVWsToolImplClient1 = new BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client();					
	    			String2TParmTool tool = new String2TParmTool();
	    			String inString = tool.tparm2String(IBSOrddParm);
	    			String onFeeData = BILSPCINVWsToolImplClient1.onFeeData(inString);
	    			TParm forIBSParm1 = tool.string2Ttparm(onFeeData);
	    			//TParm forIBSParm1 =  feeData(IBSOrddParm);
					Map data = forIBSParm1.getData();
					TParm parmForFee = new TParm();
					parmForFee.setData("forIBSParm1",forIBSParm1.getData());
					parmForFee.setData("IBSOrddParm",IBSOrddParm.getData());
					TParm parmM=  IBSOrddParm;
					
					
					String inStringM = tool.tparm2String(parmM);
					String inString1 = tool.tparm2String(forIBSParm1);
					String inString2 = tool.tparm2String(IBSOrddParm);
					System.out.println("parmM--->"+parmM);
					System.out.println("forIBSParm1-->"+forIBSParm1);
					System.out.println("IBSOrddParm--->"+IBSOrddParm);
					System.out.println("---------------------住院开始执行ws------------------------------");
					BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client BILSPCINVWsToolImplClient = new BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client();					
					String returnDto=  BILSPCINVWsToolImplClient.insertIBSOrder(inString1,inString2,inStringM);
					System.out.println("----------------------住院执行了没有完了-------------------------");
					System.out.println("returnDto--->"+returnDto);
					if(returnDto.equals(":#PKs:")){
						System.out.println("---------没有返回值-------");
						result.setErrCode(-1);
						connection.rollback();
						connection.close();
					}else{
						  TParm resultParms = tool.string2Ttparm(returnDto);
						    if(resultParms.getCount("CASE_NO_SEQ")>0){
						    	 for(int i = 0;i<resultParms.getCount("CASE_NO_SEQ");i++){
								    	resultParm.addData("CASE_NO_SEQ",resultParms.getValue("CASE_NO_SEQ",i) );
								    	resultParm.addData("SEQ_NO",resultParms.getValue("SEQ_NO",i) );
								    }
						    }
						   
							System.out.println("计价结果"+resultParm);
					}
				  
					if(resultParm.getErrCode()<0){
						connection.rollback();
						connection.close();
					}
			
	    	 }
	    	 

				
				

	     }
   
	     //4.物联网接到返参，将对应字段写入
	     if(resultParm.getCount("CASE_NO_SEQ")>0){
	    	
	    	 for(int i = 0;i<resultParm.getCount("CASE_NO_SEQ");i++){
	    		 TParm inParm = new TParm();
	    		 inParm.setData("BUSINESS_NO",IBSOrddParm.getData("BUSINESS_NO",i));
	    		 inParm.setData("SEQ",IBSOrddParm.getData("SEQ",i));
	    		 inParm.setData("CASE_NO_SEQ",resultParm.getData("CASE_NO_SEQ",i));
	    		 inParm.setData("SEQ_NO",resultParm.getData("SEQ_NO",i));
	    		result =  BILSPCINVRecordTool.getInstance().updSpcInvRecord(inParm,connection);
	    		 
	    		 if(result.getErrCode()<0){
			    	 connection.rollback();
			         connection.close();
			         return result;
			     }
	    	 }
	    	
	     }
	     
	     }
	        connection.commit();
	        connection.close();
		    return result;
    	
    }
    
    
    /**
     * 获得计费的基础数据
     * */
    public TParm feeData(TParm IBSOrddParm){
    	TParm result = new TParm();
    	TParm forIBSParm1 = new TParm();
    	String ctz1Code = "";
		String ctz2Code = "";
		String ctz3Code = "";
		String bed_no = "";
	 String	SelSql = "SELECT *" + " FROM ADM_INP WHERE CASE_NO='"
		+ IBSOrddParm.getValue("CASE_NO",0) + "'";
	 System.out.println("SelSql--->"+SelSql);
     // 得到该病人所有该执行展开的处置
     TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
		SelSql));
		
		forIBSParm1.setData("CTZ1_CODE",
				ctzParm.getData("CTZ1_CODE", 0));
		forIBSParm1.setData("CTZ2_CODE",
				ctzParm.getData("CTZ2_CODE", 0));
		forIBSParm1.setData("CTZ3_CODE",
				ctzParm.getData("CTZ3_CODE", 0));
		forIBSParm1.setData("BED_NO",ctzParm.getData("BED_NO",0));
		forIBSParm1.setData("FLG", IBSOrddParm.getData("FLG",0));//parm
		
		//result.setData("forIBSParm1",forIBSParm1);
		
		return forIBSParm1;
    }

	/**
	 * 保存
	 * */
	public TParm onSave(TParm parms){
		TParm result = new TParm();	
		TParm parm = parms.getParm("SPCINVRecord");
		TParm resultParm = new TParm();
	    if(parm==null)
	       return result.newErrParm(-1,"参数为空");
	     TConnection connection = getConnection();
	    //1.将grid的数据写入SPC_INV_RECORD表
	     int count = parm.getCount("BAR_CODE");
	     for(int i=0;i<count;i++){
	    	 System.out.println("-----------------------"+i);
	    	 resultParm.addData("BUSINESS_NO", parm.getRow(i).getValue("BUSINESS_NO"));
		     resultParm.addData("SEQ", parm.getRow(i).getValue("SEQ"));
	    	 System.out.println("parm.getRow(i)=="+parm.getRow(i));
	    	 result=BILSPCINVRecordTool.getInstance().insertData(parm.getRow(i), connection);
		        if (result==null||result.getErrCode() < 0) {
		        	connection.rollback();
		        	connection.close();
		             return result;
		        }	       
	     }
	     
	     //2.扣库
	     //按类别进行扣库，数量大于0，扣库，数量小于零，增库
	     TParm invParm = parms.getParm("INVParm");	
	     System.out.println("invParm--------->"+invParm);
	     result = this.onSaveINV(invParm, connection);
	     System.out.println("result:::"+result);
	     if (result==null||result.getErrCode() < 0) {
	        	connection.rollback();
	        	connection.close();
	             return result;
	        }
	     //3.计费
	     //根据开关 spc_flg判断是否是物联网   如果是物联网，呼叫HIS的WS接口，进行计费，否则，直接计费
	     String sqlSpcFlg = "SELECT SPC_FLG FROM SYS_REGION WHERE REGION_CODE = '"+parms.getData("REGION_CODE")+"'";
	     System.out.println(sqlSpcFlg);
	     TParm spcFlgParm = new TParm(TJDODBTool.getInstance().select(sqlSpcFlg));
	     System.out.println(spcFlgParm);
	     if(spcFlgParm.getErrCode()<0){
	    	 connection.rollback();
	         connection.close();
	         return result;
	     }
	     //是物联网  调用webservice接口
	     if(spcFlgParm.getData("SPC_FLG",0).toString().equals("Y")){
	    	 //----------调用webservice接口开始--------------
	    	 System.out.println("NNNNNNNNNNNNNNN");
	    	 TParm IBSOrddParm = parms.getParm("IBSOrddParm");
		     System.out.println("IBSOrddParm::"+IBSOrddParm);
		     if(!(IBSOrddParm.getData("CASE_NO",0)==null || IBSOrddParm.getData("CASE_NO",0).equals(""))){
		    	 //住院
		    	 if(IBSOrddParm.getData("ADM_TYPE").equals("I")){
		    		// 调用IBS接口返回费用数据
//				    	TParm forIBSParm1 = new TParm();
//				    	String ctz1Code = "";
//						String ctz2Code = "";
//						String ctz3Code = "";
//						String bed_no = "";
//					 String	SelSql = "SELECT *" + " FROM ADM_INP WHERE CASE_NO='"
//						+ parm.getValue("CASE_NO",0) + "'";
//					 System.out.println("SelSql--->"+SelSql);
//				     // 得到该病人所有该执行展开的处置
//				     TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
//						SelSql));
//						
//						forIBSParm1.setData("CTZ1_CODE",
//								ctzParm.getData("CTZ1_CODE", 0));
//						forIBSParm1.setData("CTZ2_CODE",
//								ctzParm.getData("CTZ2_CODE", 0));
//						forIBSParm1.setData("CTZ3_CODE",
//								ctzParm.getData("CTZ3_CODE", 0));
//						forIBSParm1.setData("BED_NO",ctzParm.getData("BED_NO",0));
//						forIBSParm1.setData("FLG", IBSOrddParm.getData("FLG",0));//parm
//						System.out.println("===forIBSParm1===="+forIBSParm1);
//						System.out.println(forIBSParm1.getData("CASE_NO")== null );
		    		    TParm forIBSParm1 =  feeData(IBSOrddParm);
						//**************************************************
						//Map groupData = forIBSParm1.getGroupData("");
						//groupData = (Map)forIBSParm1;
						Map data = forIBSParm1.getData();
						TParm parmForFee = new TParm();
						parmForFee.setData("forIBSParm1",forIBSParm1.getData());
						parmForFee.setData("IBSOrddParm",IBSOrddParm.getData());
						TParm parmM=  IBSOrddParm;
						String2TParmTool tool = new String2TParmTool();
						
						String inStringM = tool.tparm2String(parmM);
						String inString1 = tool.tparm2String(forIBSParm1);
						String inString2 = tool.tparm2String(IBSOrddParm);

						//System.out.println("---------------------住院开始执行ws------------------------------");
						BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client BILSPCINVWsToolImplClient = new BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client();					
						String returnDto=  BILSPCINVWsToolImplClient.insertIBSOrder(inString1,inString2,inStringM);
						//System.out.println("----------------------住院执行了没有完了-------------------------");
						//System.out.println("returnDto--->"+returnDto);
						if(returnDto.equals(":#PKs:")){
							System.out.println("---------没有返回值-------");
							resultParm.setErrCode(-1);
							connection.rollback();
							connection.close();
						}else{
							  TParm resultParms = tool.string2Ttparm(returnDto);
							    if(resultParms.getCount("CASE_NO_SEQ")>0){
							    	 for(int i = 0;i<resultParms.getCount("CASE_NO_SEQ");i++){
									    	resultParm.addData("CASE_NO_SEQ",resultParms.getValue("CASE_NO_SEQ",i) );
									    	resultParm.addData("SEQ_NO",resultParms.getValue("SEQ_NO",i) );
									    }
							    }
							   
								//System.out.println("计价结果"+resultParm);
						}
					  
						if(resultParm.getErrCode()<0){
							connection.rollback();
							connection.close();
						}
				//门诊，或者急诊计费
		    	 }else if (IBSOrddParm.getData("ADM_TYPE",0).equals("O") || IBSOrddParm.getData("ADM_TYPE",0).equals("E")){
		    			BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client BILSPCINVWsToolImplClient = new BILSPCINVWsTool_BILSPCINVWsToolImplPort_Client();					
		    	 }
		    	 

					
					// 传到后台的数据得到IBS返回的数据

		     }
	    	 //逻辑 。。。。。。。。。。。。。。。。。。
	    	 //----------调用webservice接口结束--------------
	    	 
	     //直接走HIS计费	 
	     }else{
	    	 TParm IBSOrddParm = parms.getParm("IBSOrddParm");
		     System.out.println("IBSOrddParm::"+IBSOrddParm);
		     if(!(IBSOrddParm.getData("CASE_NO",0)==null || IBSOrddParm.getData("CASE_NO",0).equals(""))){
		    	 // 调用IBS接口返回费用数据
			    	TParm forIBSParm1 = new TParm();
			    	String ctz1Code = "";
					String ctz2Code = "";
					String ctz3Code = "";
				 String	SelSql = "SELECT *" + " FROM ADM_INP WHERE CASE_NO='"
					+ parm.getValue("CASE_NO") + "'";
			     // 得到该病人所有该执行展开的处置
			     TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
					SelSql));
					forIBSParm1.setData("M", IBSOrddParm.getData());
					forIBSParm1.setData("CTZ1_CODE",
							ctzParm.getData("CTZ1_CODE", 0));
					forIBSParm1.setData("CTZ2_CODE",
							ctzParm.getData("CTZ2_CODE", 0));
					forIBSParm1.setData("CTZ3_CODE",
							ctzParm.getData("CTZ3_CODE", 0));
					forIBSParm1.setData("FLG", IBSOrddParm.getData("FLG"));//parm
					System.out.println("===forIBSParm1===="+forIBSParm1);
					System.out.println(forIBSParm1.getData("CASE_NO")== null );
					
					// 传到后台的数据得到IBS返回的数据
					TParm resultFromIBS = IBSTool.getInstance().getIBSOrderData(
							forIBSParm1);
		            System.out.println("resultFromIBS:::===="+resultFromIBS);
					
					TParm forIBSParm2 = new TParm();
					forIBSParm2.setData("DATA_TYPE", "5"); // 耗费记录调用标记5
					forIBSParm2.setData("M", resultFromIBS.getData());
					forIBSParm2.setData("FLG", IBSOrddParm.getData("FLG"));//parm
					System.out.println("forIBSParm2::==="+forIBSParm2);
					System.out.println();
				
					// 调用IBS提供的Tool继续执行
					result = BILSPCINVRecordTool.getInstance().insertIBSOrder(forIBSParm2,
							connection);
					if(result.getErrCode()<0){
						System.out.println(result.getErrText());
						connection.rollback();
						connection.close();
					}
		     }
		    
	     }
	     
		    //4.物联网接到返参，将对应字段写入
	     if(resultParm.getCount("CASE_NO_SEQ")>0){
	    	
	    	 for(int i = 0;i<resultParm.getCount("CASE_NO_SEQ");i++){
	    		 TParm inParm = new TParm();
	    		 inParm.setData("BUSINESS_NO",resultParm.getData("BUSINESS_NO",i));
	    		 inParm.setData("SEQ",resultParm.getData("SEQ",i));
	    		 inParm.setData("CASE_NO_SEQ",resultParm.getData("CASE_NO_SEQ",i));
	    		 inParm.setData("SEQ_NO",resultParm.getData("SEQ_NO",i));
	    		result =  BILSPCINVRecordTool.getInstance().updSpcInvRecord(inParm,connection);
	    		 
	    		 if(result.getErrCode()<0){
			    	 connection.rollback();
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
	 * 合并INV_CODE相同的项
	 * */
	public TParm onMerge(TParm parm){
		TParm result = new TParm();
	//	System.out.println("parm.getCount"+parm.getCount("INV_CODE"));
  	  if(parm.getCount("INV_CODE")<0){
  		  return result;
  	  }
  	  String inv_code = parm.getData("INV_CODE",0).toString();
  	  result.addData("INV_CODE", inv_code);
  	  for(int i =0;i<parm.getCount("INV_CODE");i++){
  		
  	  }
	 
		return result;
	}
	
	/**
	 * 计费方法
	 * */
	public TParm countFee(TParm parm1,TParm parm2,TParm parmM){
	
		TParm result = new TParm();
		TParm forIBSParm1 = new TParm();
			
		forIBSParm1 = parm1;
		TParm IBSOrddParm = new TParm();		
		IBSOrddParm = parm2;
	    forIBSParm1.setData("M",parmM.getData());
		Map data = forIBSParm1.getData();
		forIBSParm1 = new TParm(data);
		
		Map data2 = IBSOrddParm.getData();
		IBSOrddParm = new TParm(data2);
		TConnection connection = getConnection();
		System.out.println("forIBSParm1---------->"+forIBSParm1);
		TParm resultFromIBS = IBSTool.getInstance().getIBSOrderData(forIBSParm1);
     	System.out.println("resultFromIBS-------->"+resultFromIBS);
		TParm forIBSParm2 = new TParm();
	
		resultFromIBS.setData("EXE_DEPT_CODE",0,IBSOrddParm.getData("EXE_DEPT_CODE",0));
		resultFromIBS.setData("BED_NO",0,forIBSParm1.getData("BED_NO"));
		forIBSParm2.setData("DATA_TYPE", "5"); // 耗费记录调用标记5
		forIBSParm2.setData("M", resultFromIBS.getData());
		forIBSParm2.setData("FLG", IBSOrddParm.getData("FLG"));				
		
		TParm data3 = (TParm)forIBSParm2.getData("BILL_DATE");
		for(int i = 0;i<forIBSParm2.getCount("CASE_NO");i++){			
			Timestamp time = StringTool.getTimestamp(forIBSParm2.getData("BILL_DATE",i).toString().substring(0,19), "yyyy-MM-dd HH:ss:mm");
			forIBSParm2.setData("BILL_DATE", i, time);
			
		}
	
		// 调用IBS提供的Tool继续执行
		result = BILSPCINVRecordTool.getInstance().insertIBSOrder(forIBSParm2,
				connection);
		if(result.getErrCode()<0){
			System.out.println(result.getErrText());
			connection.rollback();
			connection.close();
		}
		connection.commit();
		connection.close();
		
		return result;
	}
	
	  /**
     * 扣库方法
     * */
      @SuppressWarnings("unchecked")
	public TParm onSaveINV(TParm parm ,TConnection connection){
    	 
    	  TParm result = new TParm();
    	
    	  if(parm.getCount("INV_CODE")<0){
    		  return result;
    	  }
    	  HashMap map = (HashMap)parm.getData("MERGE");
    	  if(map.size()<0){
    		  return result;
    	  }
    	  //如果是高值，将前面table的数据INV_CODE相同的数据合并处理  INV_STOCKM,INV_STOCKD
    	  Iterator ite = map.entrySet().iterator();				
    	  while(ite.hasNext()){			
    		  Entry<String,Double> entry =(Entry<String,Double>) ite.next();			
    		  String inv_code = entry.getKey();//map中的key	
    		//  System.out.println("---------->"+inv_code);
    		  Double qty = entry.getValue();//上面key对应的value	
    		//  System.out.println("---------->"+qty);
    		  //扣库  INV_STOCKM和INV_STOCKD
    		  String orgCode = "0306";//高值 暂时写死 
    		  String opt_user = parm.getData("OPT_USER",0).toString();
        	  String opt_term = parm.getData("OPT_TERM",0).toString();
        	  //--------------------------1.高值---------------------------------------
        	  //step1.根据ORG_CODE,INV_CODE查询INV_STOCKM  ORG_CODE 高值为介入室  	 
        	  String selectInvStockM = "SELECT ORG_CODE,INV_CODE,STOCK_QTY " +
        	  		" FROM INV_STOCKM " +
        	  		" WHERE ORG_CODE = '"+orgCode+"' AND INV_CODE = '"+inv_code+"' ";//ORG_CODE暂时写死
        	//  System.out.println("selectInvStockM---->"+selectInvStockM);
        	  //step2.更新 INV_STOCKM表的字段  stock_qty(-m)
        	  result = new TParm(TJDODBTool.getInstance().select(selectInvStockM));
        	//  System.out.println("查询INV_STOCKM表数据"+result);
        	  if(result.getErrCode()<0){
        		  connection.rollback();
        		  connection.close();
        		  return result;
        	  }
        	  String sql1  = "SELECT INV_CHN_DESC FROM INV_BASE WHERE INV_CODE = '"+inv_code+"'";
        	  TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
        	  String inv_chn_desc = parm1.getData("INV_CHN_DESC",0).toString();
    		  String sql2 = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"+orgCode+"'";
    		  TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
    		  String dept_chn_desc = parm2.getData("DEPT_CHN_DESC",0).toString();
        	  if(result.getCount("ORG_CODE")<0){
        		  result.setErrCode(-2);        		         			  
        		  result.setErrText(dept_chn_desc+"的"+inv_chn_desc+"查无此物资");
        		  connection.rollback();
        		  connection.close();
        		  return result;
        		  //查无此物资
        	  }
        	  
        	  if(Double.parseDouble(result.getData("STOCK_QTY",0).toString())<qty){
        		  //库存不足
        		  result.setErrCode(-2);
        		  result.setErrText(dept_chn_desc+"的"+inv_chn_desc+"库存不足");
        		  connection.rollback();
        		  connection.close();
        		  return result;
        		  
        	  }
        
        	  TParm updInvStockMParm = new TParm();
        	  updInvStockMParm.setData("ORG_CODE",orgCode);
        	  updInvStockMParm.setData("INV_CODE",inv_code);
        	  updInvStockMParm.setData("STOCK_QTY",qty);
        	  updInvStockMParm.setData("OPT_USER",opt_user);
        	  updInvStockMParm.setData("OPT_TERM",opt_term);
        	  result = BILSPCINVRecordTool.getInstance().updInvStockM(updInvStockMParm,connection);
        	  
        	  if(result.getErrCode()<0){
        		  result.setErrText("保存失败！");
        		  connection.rollback();
        		  connection.close();
        		  return result;
        	  }
        	  //step3.根据 ORG_CODE,INV_CODE查询INV_STOCKD 根据VALID_DATE升序排序
        	  String selectInvStockD = "SELECT INV_CODE,ORG_CODE,BATCH_SEQ,STOCK_QTY FROM INV_STOCKD WHERE INV_CODE = '"+inv_code+"' AND ORG_CODE = '"+orgCode+"' ORDER BY VALID_DATE";
        	 // System.out.println("selectInvStockD---->"+selectInvStockD);
        	  result = new TParm(TJDODBTool.getInstance().select(selectInvStockD));
        	 // System.out.println("INV_STOCKD表的数据："+result);
        	  if(result.getErrCode()<0){
        		  result.setErrText("保存失败！");
        		  connection.rollback();
        		  connection.close();
        		  return result;
        	  }
        	  if(result.getCount("ORG_CODE")<0){
        		  //查无数据
        		  result.setErrCode(-2);        		         			  
        		  result.setErrText(dept_chn_desc+"的"+inv_chn_desc+"查无此物资");
        		  connection.rollback();
        		  connection.close();
        		  return result;
        	  }
        	
        	  result = saveInvStockD(result,qty,connection,opt_user,opt_term);
        	  
    	  }
    	  //更新 INV_STOCKDD表数据
    	  for(int i=0;i<parm.getCount("INV_CODE");i++){
    		 // System.out.println("第"+i+"次循环执行");
    		  if(parm.getData("FLG",i).toString().equals("HIGH")){
    			
    		  
    			//  System.out.println("第"+i+"次执行高值扣库");
    			  String inv_code = parm.getData("INV_CODE",i).toString();
            	  String orgCode = "0306";//高值 暂时写死 
            	  String opt_user = parm.getData("OPT_USER",i).toString();
            	  String opt_term = parm.getData("OPT_TERM",i).toString();
            	  Double qty = parm.getDouble("QTY",i);//扣库数量                	 
                  //step5.查询INV_STOCKDD  根据RFID查询 ，只有一条数据
            	  String selectInvStockDD = "SELECT INV_CODE,INVSEQ_NO,RFID,STOCK_QTY FROM INV_STOCKDD WHERE INV_CODE='"+inv_code+"' AND RFID = '"+parm.getData("RFID",i)+"'";
                 // System.out.println("selectInvStockDD----->"+selectInvStockDD);
            	  result = new TParm(TJDODBTool.getInstance().select(selectInvStockDD)); 
            	//  System.out.println("INV_STOCKDD表的数据：：："+result);
                  if(result.getErrCode()<0){
                	  result.setErrText("保存失败！");
                	  connection.rollback();
            		  connection.close();
            		  return result;
                  }
                  if(result.getCount("INV_CODE")<0){
                	  result.setErrText("查无条码号："+parm.getData("RFID",i));
                	  //查无数据
                	  connection.rollback();
            		  connection.close();
            		  return result;
                  }
                  TParm updInvStockDDParm = new TParm(); 
                  updInvStockDDParm.setData("RFID",parm.getData("RFID",i));
                  if(qty>0){
                	  updInvStockDDParm.setData("OUT_USER",Operator.getID());
                      updInvStockDDParm.setData("MR_NO",parm.getData("MR_NO",i));
                      updInvStockDDParm.setData("CASE_NO",parm.getData("CASE_NO",i));
                      updInvStockDDParm.setData("RX_SEQ","");
                      updInvStockDDParm.setData("ADM_TYPE",parm.getData("ADM_TYPE",i));
                      updInvStockDDParm.setData("SEQ_NO","");
                      updInvStockDDParm.setData("WAST_ORG",parm.getData("WAST_ORG",i));
                      updInvStockDDParm.setData("INV_CODE",inv_code);
                      updInvStockDDParm.setData("STOCK_QTY",qty);                      
                      updInvStockDDParm.setData("OUT_USER",opt_user);
                      updInvStockDDParm.setData("OPT_TERM",opt_term);
                      updInvStockDDParm.setData("OPT_USER",opt_user);
                  }else{
                              
	                  updInvStockDDParm.setData("OUT_USER","");
	                  updInvStockDDParm.setData("MR_NO","");
	                  updInvStockDDParm.setData("CASE_NO","");
	                  updInvStockDDParm.setData("RX_SEQ","");
	                  updInvStockDDParm.setData("ADM_TYPE","");
	                  updInvStockDDParm.setData("SEQ_NO","");
	                  updInvStockDDParm.setData("WAST_ORG","");
	                  updInvStockDDParm.setData("INV_CODE",inv_code);
	                  updInvStockDDParm.setData("STOCK_QTY","");                      
	                  updInvStockDDParm.setData("OUT_USER",opt_user);
	                  updInvStockDDParm.setData("OPT_TERM",opt_term);
	                  updInvStockDDParm.setData("OPT_USER",opt_user);
                  }
                 // System.out.println("updInvStockDDParm::"+updInvStockDDParm);
                //step6.根据INV_CODE 扣库  更新如下字段
                  BILSPCINVRecordTool.getInstance().updInvStockDD(updInvStockDDParm,connection);
            		  
              //-------------------------低值-----------------------
    		  }else if(parm.getData("FLG",i).toString().equals("LOW")){
    			  String inv_code = parm.getData("INV_CODE",i).toString();
            	  String orgCode = parm.getData("ORG_CODE",i).toString(); 
            	  String opt_user = parm.getData("OPT_USER",i).toString();
            	  String opt_term = parm.getData("OPT_TERM",i).toString();
            	  Double qty = parm.getDouble("QTY",i);//扣库数量
            	 
            	//step1.根据ORG_CODE,INV_CODE查询INV_STOCKM  ORG_CODE 高值为介入室  	 
            	  String selectInvStockM = "SELECT ORG_CODE,INV_CODE,STOCK_QTY " +
            	  		" FROM INV_STOCKM " +
            	  		" WHERE ORG_CODE = '"+orgCode+"' AND INV_CODE = '"+inv_code+"' ";
            	 // System.out.println("selectInvStockM---->"+selectInvStockM);
            	  //step2.更新 INV_STOCKM表的字段  stock_qty(-m)
            	  result = new TParm(TJDODBTool.getInstance().select(selectInvStockM));
            	 // System.out.println("查询INV_STOCKM表数据"+result);
            	  if(result.getErrCode()<0){
            		  connection.rollback();
            		  connection.close();
            		  return result;
            	  }
            	  String sql1  = "SELECT INV_CHN_DESC FROM INV_BASE WHERE INV_CODE = '"+inv_code+"'";
            	  TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));
            	  String inv_chn_desc = parm1.getData("INV_CHN_DESC",0).toString();
        		  String sql2 = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = '"+orgCode+"'";
        		  TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
        		  String dept_chn_desc = parm2.getData("DEPT_CHN_DESC",0).toString();
            	  if(result.getCount("ORG_CODE")<0){
            		  result.setErrCode(-2);        		         			  
            		  result.setErrText(dept_chn_desc+"的"+inv_chn_desc+"查无此物资");
            		  connection.rollback();
            		  connection.close();
            		  return result;
            		  //查无此物资
            	  }
            	  
            	  if(Double.parseDouble(result.getData("STOCK_QTY",0).toString())<qty){
            		  //库存不足
            		  result.setErrCode(-2);
            		  result.setErrText(dept_chn_desc+"的"+inv_chn_desc+"库存不足");
            		  connection.rollback();
            		  connection.close();
            		  return result;
            		  
            	  }
            
            	  TParm updInvStockMParm = new TParm();
            	  updInvStockMParm.setData("ORG_CODE",orgCode);
            	  updInvStockMParm.setData("INV_CODE",inv_code);
            	  updInvStockMParm.setData("STOCK_QTY",qty);
            	  updInvStockMParm.setData("OPT_USER",opt_user);
            	  updInvStockMParm.setData("OPT_TERM",opt_term);
            	  result = BILSPCINVRecordTool.getInstance().updInvStockM(updInvStockMParm,connection);
            	  
            	  if(result.getErrCode()<0){
            		  result.setErrText("保存失败！");
            		  connection.rollback();
            		  connection.close();
            		  return result;
            	  }
            	  //step3.根据 ORG_CODE,INV_CODE查询INV_STOCKD 根据VALID_DATE升序排序
            	  String selectInvStockD = "SELECT INV_CODE,ORG_CODE,BATCH_SEQ,STOCK_QTY FROM INV_STOCKD WHERE INV_CODE = '"+inv_code+"' AND ORG_CODE = '"+orgCode+"' ORDER BY VALID_DATE";
            	 // System.out.println("selectInvStockD---->"+selectInvStockD);
            	  result = new TParm(TJDODBTool.getInstance().select(selectInvStockD));
            	  //System.out.println("INV_STOCKD表的数据："+result);
            	  if(result.getErrCode()<0){
            		  result.setErrText("保存失败！");
            		  connection.rollback();
            		  connection.close();
            		  return result;
            	  }
            	  if(result.getCount("ORG_CODE")<0){
            		  //查无数据
            		  result.setErrCode(-2);        		         			  
            		  result.setErrText(dept_chn_desc+"的"+inv_chn_desc+"查无此物资");
            		  connection.rollback();
            		  connection.close();
            		  return result;
            	  }
            	
            	  result = saveInvStockD(result,qty,connection,opt_user,opt_term);
    		  }

        	  
    	  }
    	 
    	  return result;
    	
      }
      /**
       * 循环扣inv_stockd
       * d 为要扣的数量
       * */
      public TParm saveInvStockD(TParm invSockDParm, double d,TConnection connection,String opt_user,String opt_term) {

  		double qty = invSockDParm.getDouble("STOCK_QTY", 0);
  		if(qty >= d){
  			//System.out.println("qty----111111111----->"+qty); 
  			//System.out.println("d------222222222------>"+d);
  			this.updateInvStockD(invSockDParm.getValue("ORG_CODE", 0), invSockDParm.getValue("INV_CODE", 0),invSockDParm.getValue("BATCH_SEQ", 0), qty-d,connection,opt_user,opt_term,d);
  		
  		}else{
  			//System.out.println("qty----333333333----->"+qty); 
  			//System.out.println("d------444444444------>"+d);
  			
  			this.updateInvStockD(invSockDParm.getValue("ORG_CODE", 0), invSockDParm.getValue("INV_CODE", 0),invSockDParm.getValue("BATCH_SEQ", 0), qty,connection,opt_user,opt_term,qty);
  			d = d - qty;
  			//System.out.println("invSockDParm:::--->"+invSockDParm);
  			//System.out.println("2222222"+invSockDParm.getCount("ORG_CODE"));
  			if(invSockDParm.getCount("ORG_CODE")>0){
  				invSockDParm.removeRow(0);
  				this.saveInvStockD(invSockDParm, d,connection,opt_user,opt_term);
  			}else{  				
  				return invSockDParm;
  			}
  		}
  		return invSockDParm;
  	}
    /**
     * 扣库方法
     * org
     * inv
     * batch_seq
     * qty 
     * */  
    public TParm updateInvStockD(String org,String inv,String batch_seq,double qty,TConnection connection,String opt_user,String opt_term,Double d){
  	    TParm result = new TParm();
    	//String updInvStockD = "UPDATE INV_STOCKD SET STOCK_QTY = STOCK_QTY - "+qty+" WHERE ORG_CODE = '"+org+"' AND INV_CODE = '"+inv+"' AND BATCH_SEQ = '"+batch_seq+"'";
  		//System.out.println("updateInvStockD执行。。。。");
  	    TParm updInvStockDParm = new TParm();
  		updInvStockDParm.setData("STOCK_QTY",d);
  		updInvStockDParm.setData("STOCK_QTYS",qty);
  		updInvStockDParm.setData("ORG_CODE",org);
  		updInvStockDParm.setData("INV_CODE",inv);
  		updInvStockDParm.setData("BATCH_SEQ",batch_seq);
  		updInvStockDParm.setData("OPT_USER",opt_user);
  		updInvStockDParm.setData("OPT_TERM",opt_term);
  	    result = BILSPCINVRecordTool.getInstance().updInvStockD(updInvStockDParm,connection);
  		if(result.getErrCode()<0){
  			return result;
  		}
    	return result;
  	}
 

	  
}
