package action.spc.services;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.jws.WebService;

import jdo.label.Constant;
import jdo.label.LabelImpl;
import jdo.label.LabelInf;
import jdo.spc.SPCGenDrugPutUpTool;
import jdo.spc.SPCIndCabdspnTool;
import jdo.spc.SPCOdiDspnTool;
import jdo.spc.SPCSysFeeTool;
import jdo.spc.inf.SpcOdiDaoImpl;
import jdo.sys.SystemTool; 
import action.spc.services.dto.SpcIndStock;
import action.spc.services.dto.SpcInwCheckDto;
import action.spc.services.dto.SpcInwCheckDtos;
import action.spc.services.dto.SpcOdiDspnd;
import action.spc.services.dto.SpcOdiDspnm;
import action.spc.services.dto.SpcOdiDspnms;

import com.dongyang.Service.Server;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

@WebService  
public class SpcOdiServiceImpl implements SpcOdiService {
	  
	@Override
	public SpcInwCheckDtos inwCheck(SpcInwCheckDtos dtos) {
		// TODO Auto-generated method stub
		TParm inParm = new TParm();
		inParm.setData("REGION_CODE",dtos.getRegionCode());
		inParm.setData("STATION_CODE",dtos.getStationCode()); 
		  
		 
		LogUtil.writerInwCheckLog("进入WEBSERVICE----------------------------------------------------BEGION"+SystemTool.getInstance().getDate());
		LogUtil.writerInwCheckLog("参数：REGION_CODE="+dtos.getRegionCode()+" STATION_CODE="+dtos.getStationCode()+"  数量="+dtos.getSpcInwCheckDtos().size());
		
		
		TParm inDParm = new TParm() ;
		List<SpcInwCheckDto> list = dtos.getSpcInwCheckDtos() ;
		for(int i = 0 ; i < list.size() ; i++ ) {
			SpcInwCheckDto dto = list.get(i);
			inDParm.setData("ORDER_CODE",i,dto.getOrderCode());
		}
		inParm.setData("ORDER",inDParm.getData());
		TParm result = SpcOdiDaoImpl.getInstance().inwCheck(inParm);
		SpcInwCheckDtos checkDtos = new SpcInwCheckDtos() ;
		checkDtos.setRegionCode(dtos.getRegionCode());
		checkDtos.setStationCode(dtos.getStationCode());
		TParm orderParm = result.getParm("ORDER");
		
		List<SpcInwCheckDto> checkdtoList = new ArrayList<SpcInwCheckDto>() ;
		for(int i = 0 ; i < orderParm.getCount("ORDER_CODE") ; i++){
			TParm rowParm = (TParm)orderParm.getRow(i) ;
			SpcInwCheckDto dto  = new SpcInwCheckDto() ;
			dto.setOrderCode(rowParm.getValue("ORDER_CODE"));
			dto.setQty(rowParm.getDouble("QTY"));
			LogUtil.writerInwCheckLog("ORDER_CODE---:"+dto.getOrderCode()+" ----:qty---:"+dto.getQty());
			checkdtoList.add(dto);
		}
		
		checkDtos.setSpcInwCheckDtos(checkdtoList);

		LogUtil.writerInwCheckLog("进入WEBSERVICE----------------------------------------------------END");
		return checkDtos ;
	}
	
	@Override
	public String examine(SpcOdiDspnms odiDspnms) {
		
		LogUtil.writerLog("\n\r");
        LogUtil.writerLog("进入WEBSERVICE----------------------------------------------------BEGION"+SystemTool.getInstance().getDate());
        	//组装ODI_DSPNM,ODI_DSPND表数据  fux modify 加入 batchNo到DSPNM的数据集中
    		TParm parm = builderDspnmdParm(odiDspnms);
    	    if(parm.getErrCode() <= -1  ){
            	return parm.getErrText();
            }
    		
            TParm result =  new TParm();
            
            /**********************扣库动作*******************************/
           
            parm = builderIndStockParm(odiDspnms, parm);
            
           
            
            LogUtil.writerLog("参数：："+parm.getParm("DSPN_M").getCount("CASE_NO")+"---:CASE_NO=:"+parm.getParm("DSPN_M").getRow(0).getValue("CASE_NO"));
            LogUtil.writerLog("参数：："+parm.getParm("DSPN_M"));
            LogUtil.writerLog("参数：："+parm.getParm("DSPN_D").getCount("CASE_NO"));
            LogUtil.writerLog("参数：："+parm.getParm("DSPN_D"));
            LogUtil.writerLog("参数：IND_STOCK数据："+parm.getParm("IND_STOCK_CHECK"));
            
            
            //插入ODI_DSPNM,ODI_DSPND ，并扣库
            Server.autoInit(this);
    		result = Server.executeAction("action.spc.SpcOdiAction",
                    "onSaveOdiDspnmd", parm);
    		LogUtil.writerLog("返回结果："+result.getErrText() ); 
    		String returnStr = "";
    		if(result.getErrCode() <  0 ){
    			returnStr = " "+result.getErrText() ;
    			LogUtil.writerLogErr(result+"");
    		}else{
    			returnStr = "success" ;
    		}
    		
    		LogUtil.writerLog("进入WEBSERVICE----------------------------------------------------END---:"+returnStr);
    		return returnStr;
      
        
	
		
	}
	  
	public String onUpdateRtnCfm(SpcOdiDspnms dspnms){
		
		LogUtil.writerLogRtn("\n\r");
		LogUtil.writerLogRtn("进入WEBSERVICE----------------------------------------------------BEGION"+"当前时间："+SystemTool.getInstance().getDate());
		TParm result = new TParm() ;
		//组装ODI_DSPNM,ODI_DSPND表数据
		TParm parm = builderDspnmdRtnParm(dspnms);
		
		String msg = parm.getValue("MSG");
		if(msg != null && !msg.equals("")){
			return msg;
		}
		LogUtil.writerLogRtn("参数："+parm);
		 
		Server.autoInit(this);
		result = Server.executeAction("action.spc.SpcOdiAction",
                "onUpdateRtnCfm", parm);
		LogUtil.writerLogRtn("返回结果："+result.getErrText());
		String returnStr = "";
		if(result.getErrCode() < 0 ){
			returnStr = "失败:"+ result.getErrText();
		}else{
			returnStr = "success" ;
		}
		LogUtil.writerLogRtn("进入WEBSERVICE----------------------------------------------------END----:"+returnStr);
		return  returnStr;
	}
	
	
	public String sendElectronicTag(String caseNo,String patName,String stationDesc,String bedNoDesc,String mrNo,String ip){
		 
		LogUtil.writerLogSignExe("\n\r");
		LogUtil.writerLogSignExe("进入WEBSERVICE----------------------------------------------------BEGION"+SystemTool.getInstance().getDate());
		LogUtil.writerLogSignExe("参数：caseNo="+caseNo+" patName="+patName+" stationDesc="+stationDesc+" bedNoDesc="+bedNoDesc+"--IP:"+ip);
		
		if(caseNo == null || caseNo.equals("")){
			return "fail";
		}
		TParm inParm = new TParm() ;
		inParm.setData("CASE_NO",caseNo);
		

		TParm result = SPCOdiDspnTool.getInstance().onQuery(inParm);
		

		
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>() ;
		String boxEslId = "";
		for(int i = 0 ; i < result.getCount("BOX_ESL_ID"); i++ ){
			boxEslId = result.getValue("BOX_ESL_ID",i);
			Map<String, Object> map = new LinkedHashMap<String, Object> ();		
			map.put("ProductName", patName);
			
			map.put("SPECIFICATION",  stationDesc +" "+bedNoDesc );
			map.put("TagNo", boxEslId );
			map.put("Light", 20);
			map.put("APRegion",ip );
			list.add(map);
		}
		try{
			String url = Constant.LABELDATA_URL;
			LogUtil.writerLogSignExe("APRegion="+ip+"   TagNo="+boxEslId);
			sendLabelDate(list, url);
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
	    	System.out.println("调用服务失败");

		}
		LogUtil.writerLogSignExe("进入WEBSERVICE----------------------------------------------------END");
		return "success";
	}
	

	public String onSaveIndCabdspn(SpcOdiDspnms odiDspnms,String status){
		LogUtil.writerIndCabdspnLog( "\n\r");
		LogUtil.writerIndCabdspnLog("进入WEBSERVICE----------------------------------------------------BEGION"+"当前时间："+SystemTool.getInstance().getDate());
		
		List<SpcOdiDspnm>  listDspnms =  odiDspnms.getSpcOdiDspnms();
		 
		TParm parm = builderIndcabdspn(listDspnms);
		if(parm.getErrCode() <= -1 ){
			return parm.getErrText() ;
		}
		
		LogUtil.writerIndCabdspnLog("参数：数组大小"+listDspnms.size() +"  状态：=====:"+status);
		Server.autoInit(this);
		LogUtil.writerIndCabdspnLog("参数："+parm);
		if(status != null && status.equals("D")){
			TParm result = Server.executeAction("action.spc.SpcOdiAction",
	                "onDeleteIndCabdspn", parm);
			if(result.getErrCode()< 0 ){
				return "失败";
			}
		}else{
			TParm result = Server.executeAction("action.spc.SpcOdiAction",
	                "onSaveIndCabdspn", parm);
			if(result.getErrCode() <  0 ){
				return "失败";
			}
		}
        LogUtil.writerIndCabdspnLog("保存进入WEBSERVICE----------------------------------------------------END"+"当前时间："+SystemTool.getInstance().getDate());
		return "success";
	}
	
	public String onCheckStockQty(SpcOdiDspnms odiDspnms){
		TParm result = new TParm();
		TParm parm = new TParm();
		parm = builderIndStockParm(odiDspnms, parm);
		if(parm.getErrCode() <= -1 ){
			return parm.getErrText() ;
		}
		LogUtil.writerLog("\n\r");
        LogUtil.writerLog("进入WEBSERVICE----------------------------------------------------BEGION"+SystemTool.getInstance().getDate());
        LogUtil.writerLog("参数：IND_STOCK数据："+parm.getParm("IND_STOCK_CHECK"));
        //插入ODI_DSPNM,ODI_DSPND ，并扣库
        Server.autoInit(this);
		result = Server.executeAction("action.spc.SpcOdiAction",
                "onCheckStockQty", parm);
		LogUtil.writerLog("返回结果："+result.getErrText() ); 
		String returnStr = "";
		if(result.getErrCode() <  0 ){
			returnStr = " "+result.getErrText() ;
			LogUtil.writerLogErr(result+"");
		}else{
			returnStr = "success" ;  
		}
		 LogUtil.writerLog("进入WEBSERVICE----------------------------------------------------END"+returnStr);
		return returnStr;
	}
	
	//fux modify 20150507
	public String onQueryBatch(SpcOdiDspnms odiDspnms){
		TParm result = new TParm();
		TParm parm = new TParm();
		//fux 单纯取批号用原来方法组装  
		parm = builderIndStockParm(odiDspnms, parm);
		if(parm.getErrCode() <= -1 ){
			return parm.getErrText() ;
		}
		LogUtil.writerLog("\n\r");
        LogUtil.writerLog("进入WEBSERVICE----------------------------------------------------BEGION"+SystemTool.getInstance().getDate());
        LogUtil.writerLog("参数：IND_STOCK数据："+parm.getParm("IND_STOCK_CHECK"));
        //插入ODI_DSPNM,ODI_DSPND ，并扣库
        Server.autoInit(this);
        //有问题 传回字符串才行
		result = Server.executeAction("action.spc.SpcOdiAction",
                "onQueryBatch", parm);
		LogUtil.writerLog("返回结果："+result.getErrText() );   
		String returnStr = "";
		if(result.getErrCode() <  0 ){  
			//returnStr = " "+result.getErrText() ;
			LogUtil.writerLogErr(result+"");  
		}else{
			returnStr = result.getValue("BATCH_NO", 0) ;
		}
		 LogUtil.writerLog("进入WEBSERVICE----------------------------------------------------END"+returnStr);
		return returnStr;
	}
	
	
	public String onCheckStockQtyBatch(SpcOdiDspnms odiDspnms){
		TParm result = new TParm();
		TParm parm = new TParm();
		//直接取出来信息(ORG_CODE,ORDER_CODE)
		 //fux parm 可以传入oid_dspnm 可以传空  
		parm = builderIndStockParm(odiDspnms, parm);
		if(parm.getErrCode() <= -1 ){
			return parm.getErrText() ;
		}
		LogUtil.writerLog("\n\r");
        LogUtil.writerLog("进入WEBSERVICE----------------------------------------------------BEGION"+SystemTool.getInstance().getDate());
        LogUtil.writerLog("参数：IND_STOCK数据："+parm.getParm("IND_STOCK_CHECK"));
        //插入ODI_DSPNM,ODI_DSPND ，并扣库
        Server.autoInit(this);
		result = Server.executeAction("action.spc.SpcOdiAction",
                "onCheckStockQtyBatch", parm);
		LogUtil.writerLog("返回结果："+result.getErrText() ); 
		String returnStr = "";
		if(result.getErrCode() <  0 ){  
			returnStr = " "+result.getErrText() ;
			LogUtil.writerLogErr(result+"");
		}else{
			returnStr = "success" ;
		}
		 LogUtil.writerLog("进入WEBSERVICE----------------------------------------------------END"+returnStr);
		return returnStr;
	}
	
	

	
	
	@Override   
	public String examineBatch(SpcOdiDspnms odiDspnms) {
		  
		LogUtil.writerLog("\n\r");       
        LogUtil.writerLog("进入WEBSERVICE----------------------------------------------------BEGION"+SystemTool.getInstance().getDate());
		//组装ODI_DSPNM,ODI_DSPND表数据--(m表里传入批号,现在需要比照服务等级传入)
		TParm parm = builderDspnmdParm(odiDspnms);
	    if(parm.getErrCode() <= -1  ){
        	return parm.getErrText();
        }
		
        TParm result =  new TParm();
        
        /**********************扣库动作*******************************/
        //fux 按照批号扣库   ---  组装条件改为从科室药品  加上批号
        parm = builderIndStockParm(odiDspnms, parm);
        
       
        
        LogUtil.writerLog("参数：："+parm.getParm("DSPN_M").getCount("CASE_NO")+"---:CASE_NO=:"+parm.getParm("DSPN_M").getRow(0).getValue("CASE_NO"));
        LogUtil.writerLog("参数：："+parm.getParm("DSPN_M"));
        LogUtil.writerLog("参数：："+parm.getParm("DSPN_D").getCount("CASE_NO"));
        LogUtil.writerLog("参数：："+parm.getParm("DSPN_D"));
        LogUtil.writerLog("参数：IND_STOCK数据："+parm.getParm("IND_STOCK_CHECK"));
        
        
        //插入ODI_DSPNM,ODI_DSPND ，并扣库
        Server.autoInit(this);
		result = Server.executeAction("action.spc.SpcOdiAction",
                "onSaveOdiDspnmdBatch", parm);
		LogUtil.writerLog("返回结果："+result.getErrText() ); 
		String returnStr = "";
		if(result.getErrCode() <  0 ){
			returnStr = " "+result.getErrText() ;
			LogUtil.writerLogErr(result+"");
		}else{
			returnStr = "success" ;
		}
		
		LogUtil.writerLog("进入WEBSERVICE----------------------------------------------------END---:"+returnStr);
		return returnStr;
	}


	private TParm  builderIndcabdspn(List<SpcOdiDspnm> listDspnms) {
		String regionCode = "";
		
		double dosageQty = 0 ;
		TParm mParm = new TParm();
        for(int i = 0 ; i < listDspnms.size() ; i++ ){
        	SpcOdiDspnm odiDspnm = (SpcOdiDspnm)listDspnms.get(i);
        	mParm.setData("CASE_NO",i,odiDspnm.getCaseNo());
        	mParm.setData("ORDER_NO",i,odiDspnm.getOrderNo());
        	mParm.setData("ORDER_SEQ",i,odiDspnm.getOrderSeq());
        	mParm.setData("START_DTTM",i,odiDspnm.getStartDttm());
        	mParm.setData("END_DTTM",i,odiDspnm.getEndDttm());
        	
        	String regionCode1 = odiDspnm.getRegionCode();
        	if(regionCode == null || regionCode.equals("") || regionCode.equals("null")){
        		if(regionCode1 != null && !regionCode1.equals("") && !regionCode1.equals("null")){
        			regionCode = regionCode1 ;
        		}
        	}
        	mParm.setData("REGION_CODE",i,regionCode1);
        	mParm.setData("STATION_CODE",i,odiDspnm.getStationCode());
        	mParm.setData("DEPT_CODE",i,odiDspnm.getDeptCode());
        	mParm.setData("VS_DR_CODE",i,odiDspnm.getVsDrCode());
        	mParm.setData("BED_NO",i,odiDspnm.getBedNo());
        	
        	mParm.setData("MR_NO",i,odiDspnm.getMrNo());
        	mParm.setData("DSPN_KIND",i,odiDspnm.getDspnKind()); 
        	String dspnDate = odiDspnm.getDspnDate() ;
        	if(dspnDate == null || dspnDate.equals("") ){
        		mParm.setData("DSPN_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("DSPN_DATE",i,StringTool.getTimestamp(dspnDate, "yyyy-MM-dd HH:mm:ss"));
        	}
        	mParm.setData("DSPN_USER",i,odiDspnm.getDspnUser());
        	mParm.setData("ORDER_CAT1_CODE",i,odiDspnm.getOrderCat1Code());
        	
        	mParm.setData("CAT1_TYPE",i,odiDspnm.getCat1Type());
       
        	
        	String orderCode = odiDspnm.getOrderCode();
        	mParm.setData("ORDER_CODE",i,orderCode); 
        	mParm.setData("ORDER_DESC",i,odiDspnm.getOrderDesc()); 
        	mParm.setData("GOODS_DESC",i,odiDspnm.getGoodsDesc()); 
        	mParm.setData("SPECIFICATION",i,odiDspnm.getSpecification()); 
        	
        	mParm.setData("MEDI_QTY",i,odiDspnm.getMediQty()); 
        	mParm.setData("MEDI_UNIT",i,odiDspnm.getMediUnit()); 
        	mParm.setData("FREQ_CODE",i,odiDspnm.getFreqCode()); 
        	mParm.setData("ROUTE_CODE",i,odiDspnm.getRouteCode()); 
        	mParm.setData("TAKE_DAYS",i,odiDspnm.getTakeDays()); 
        	
        	dosageQty = odiDspnm.getDosageQty() ;
        	mParm.setData("DOSAGE_QTY",i,dosageQty); 
        	mParm.setData("DOSAGE_UNIT",i,odiDspnm.getDosageUnit()); 
        	mParm.setData("DISPENSE_QTY",i,odiDspnm.getDispenseQty()); 
        	mParm.setData("DISPENSE_UNIT",i,odiDspnm.getDispenseUnit()); 
        	mParm.setData("PHA_DISPENSE_NO",i,odiDspnm.getPhaDispenseNo()); 
      
    
        	mParm.setData("TAKEMED_NO",i,odiDspnm.getTakemedNo());
        	
        	String takemedOrg = odiDspnm.getTakemedOrg() ;
        
        	mParm.setData("TAKEMED_ORG",i,takemedOrg);
        	mParm.setData("IS_CONFIRM",i,"");
        	mParm.setData("TAKEMED_USER",i,"");
        	mParm.setData("TAKEMED_DATE",i,new TNull(Timestamp.class));
        	
        	mParm.setData("IS_RECLAIM",i,"N");
        	mParm.setData("RECLAIM_USER",i,"");
        	mParm.setData("RECLAIM_DATE",i,new TNull(Timestamp.class));
        	mParm.setData("TOXIC_ID1",i,"");
        	mParm.setData("TOXIC_ID2",i,"");

        	mParm.setData("TOXIC_ID3",i,"");
        	mParm.setData("OPT_USER",i,odiDspnm.getOptUser());
        	String optDate = odiDspnm.getOptDate() ;
        	if(optDate == null || optDate.equals("")){
        		mParm.setData("OPT_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("OPT_DATE",i,StringTool.getTimestamp(optDate, "yyyy-MM-dd HH:mm:ss"));
        	}
        	mParm.setData("OPT_TERM",i,odiDspnm.getOptTerm());
        	mParm.setData("CTRLDRUGCLASS_CODE",i,odiDspnm.getCtrldrugclassCode());
        }
        
        return mParm;
	}
	

//	private TParm builderIndStockParm(SpcOdiDspnms odiDspnms, TParm parm) {
//		String regionCode = "";
//		List<SpcOdiDspnm> listDspnm = odiDspnms.getSpcOdiDspnms() ;
//		for(SpcOdiDspnm dspnm:listDspnm){
//			String regionCode1 = dspnm.getRegionCode() ;
//			if(regionCode == null || regionCode.equals("") || regionCode.equals("null")){
//				if(regionCode1 != null && !regionCode1.equals("") && !regionCode1.equals("null")){
//					regionCode = regionCode1 ;
//					break;  
//				}
//			}else{
//				break;
//			}
//		}
//		if(regionCode == null || regionCode.equals("")){
//			LogUtil.writerLogErr("REGION_CODE为空-----");
//			regionCode = "H01";
//		}
//		  
//		  
//		List<SpcIndStock> list = odiDspnms.getSpcIndStocks() ;
//  
//		for(int i = 0;i < list.size();i++) { 
//			SpcIndStock indStock = (SpcIndStock)list.get(i);
//			String orgCode = indStock.getOrgCode() ;
//			String orderCode = indStock.getOrderCode() ;
//			double dosageQty = indStock.getDosageQty() ;
//			for(int j = i+1;j < list.size();j++){       
//				SpcIndStock stock = (SpcIndStock)list.get(j);
//				String orgCodeNew = stock.getOrgCode() ;
//				String orderCodeNew = stock.getOrderCode() ;
//				double dosageQtyNew = stock.getDosageQty() ;
//			   if(orgCode.equals(orgCodeNew) && orderCode.equals(orderCodeNew)){ 
//			   		dosageQty += dosageQtyNew ;
//			   		indStock.setDosageQty(dosageQty);
//				   list.remove(j); 
//				   j--; 
//			   }
//			}
//		} 
//		    
//		TParm mParm = parm.getParm("DSPN_M") ; 
//		int count = mParm.getCount("CASE_NO");
//        TParm parmToINDCheck = new TParm();
//        for (int i = 0; i < list.size(); i++) {
//        	SpcIndStock indStock = (SpcIndStock)list.get(i);
//        
//        	TParm resultParm = getOrderCodeByHisOrderCode(regionCode, indStock.getOrderCode());
//        	String orderCode = resultParm.getValue("ORDER_CODE");
//        	
//        	double dosageQty = indStock.getDosageQty();
//        
//        	for (int j = 0; j < count; j++) {
//				TParm mrowParm = mParm.getRow(j);
//				String takemedOrg = mrowParm.getValue("TAKEMED_ORG");
//				String morderCode = mrowParm.getValue("ORDER_CODE");
//				double mdosageQty = mrowParm.getDouble("DOSAGE_QTY");
//				if(takemedOrg == null ){
//					takemedOrg = "" ;
//				}
//				if(morderCode == null ){
//					morderCode = "";
//				}
//				if(orderCode.equals(morderCode) && takemedOrg.equals("1")){
//					TParm searchParm = new TParm();
//					searchParm.setData("ORDER_CODE",orderCode);
//					
//					TParm result =  SPCIndCabdspnTool.getInstance().onQueryIsDrug(searchParm);
//					  
//					if(result.getCount() >  0 ){
//						dosageQty = dosageQty - mdosageQty ;
//					}else {
//				 		String isDrugOutUdd = getIsDrugOutUdd() ;
//				 		
//						if(isDrugOutUdd.equals("N")){
//							dosageQty = dosageQty - mdosageQty ;
//						}
//					}
//					
//				}
//			}
//        	
//        	String orderDesc = resultParm.getValue("ORDER_DESC");
//        	
//        	
//        	String spec = resultParm.getValue("SPECIFICATION");
//        	
//        	LogUtil.writerLog("扣库组装原始数据为:ORG_CODE=:"+indStock.getOrgCode()+" SENDATCT_FLG=:"+indStock.getSendatcFlg()+"orderDesc:"+orderDesc);
//        	String orgCode = getOrgCode(orderCode, indStock.getOrgCode(),indStock.getSendatcFlg());
//        	
//         
//        	parmToINDCheck.setData("ORG_CODE",i,orgCode );
//        	parmToINDCheck.setData("ORDER_CODE",i,orderCode);
//        	parmToINDCheck.setData("DOSAGE_QTY",i,dosageQty);
//        	parmToINDCheck.setData("OPT_DATE",i, StringTool.getTimestamp(indStock.getOptDate(), "yyyy-MM-dd HH:mm:ss"));
//        	parmToINDCheck.setData("OPT_USER",i,indStock.getOptUser());
//        	parmToINDCheck.setData("OPT_TERM",i,indStock.getOptTerm());
//            
//           
//        	parmToINDCheck.setData("SERVICE_LEVEL",i,indStock.getServiceLevel());
//        	parmToINDCheck.setData("ORDER_DESC",i,orderDesc);
//        	parmToINDCheck.setData("HIS_ORDER_CODE",i,indStock.getOrderCode());
//        	parmToINDCheck.setData("SPECIFICATION",i,spec);
//        }
//        
//     
//        parm.setData("IND_STOCK_CHECK",parmToINDCheck.getData());
//        
//        return parm ;
//	}
     
	
	//fux modify 20150507  加上批号条件组装 是否正确
	private TParm builderIndStockParm(SpcOdiDspnms odiDspnms, TParm parm) {
		String regionCode = "";
		List<SpcOdiDspnm> listDspnm = odiDspnms.getSpcOdiDspnms() ;
		for(SpcOdiDspnm dspnm:listDspnm){
			String regionCode1 = dspnm.getRegionCode() ;
			if(regionCode == null || regionCode.equals("") || regionCode.equals("null")){
				if(regionCode1 != null && !regionCode1.equals("") && !regionCode1.equals("null")){
					regionCode = regionCode1 ;
					break;  
				}
			}else{
				break;
			}
		}
		if(regionCode == null || regionCode.equals("")){
			LogUtil.writerLogErr("REGION_CODE为空-----");
			regionCode = "H01";
		}
		  
		  
		List<SpcIndStock> list = odiDspnms.getSpcIndStocks() ;
  
		for(int i = 0;i < list.size();i++) { 
			SpcIndStock indStock = (SpcIndStock)list.get(i);
			String orgCode = indStock.getOrgCode() ;
			String orderCode = indStock.getOrderCode() ;
			double dosageQty = indStock.getDosageQty() ;
			String batchNo = indStock.getBatchNo() ;
			for(int j = i+1;j < list.size();j++){       
				SpcIndStock stock = (SpcIndStock)list.get(j);
				String orgCodeNew = stock.getOrgCode() ;
				String orderCodeNew = stock.getOrderCode() ;
				double dosageQtyNew = stock.getDosageQty() ;
				String batchNoNew = stock.getBatchNo() ;
				//原版是同科室同药品合并   现在我加上了批号
					   if(orgCode.equals(orgCodeNew) && orderCode.equals(orderCodeNew)){
						   //fux modify 批号不为空时即为皮试药品 进行批次扣库
						   if(batchNo!=null&&batchNoNew!=null){
							   if(batchNo.equals(batchNoNew)){  
						   		dosageQty += dosageQtyNew ;
						   		indStock.setDosageQty(dosageQty);
							    list.remove(j); 
							    j--; 
							   }
						   }else{
							    dosageQty += dosageQtyNew ;
						   		indStock.setDosageQty(dosageQty);
							    list.remove(j); 
							    j--; 
						   }
				}
			   
			   
			}
		} 
		    
		TParm mParm = parm.getParm("DSPN_M") ; 
		int count = mParm.getCount("CASE_NO");
        TParm parmToINDCheck = new TParm();
        for (int i = 0; i < list.size(); i++) {
        	SpcIndStock indStock = (SpcIndStock)list.get(i);
        
        	TParm resultParm = getOrderCodeByHisOrderCode(regionCode, indStock.getOrderCode());
        	String orderCode = resultParm.getValue("ORDER_CODE");
        	
        	double dosageQty = indStock.getDosageQty();
        
        	for (int j = 0; j < count; j++) {
				TParm mrowParm = mParm.getRow(j);
				String takemedOrg = mrowParm.getValue("TAKEMED_ORG");
				String morderCode = mrowParm.getValue("ORDER_CODE");
				double mdosageQty = mrowParm.getDouble("DOSAGE_QTY");
				if(takemedOrg == null ){
					takemedOrg = "" ;
				}
				if(morderCode == null ){
					morderCode = "";
				}
				if(orderCode.equals(morderCode) && takemedOrg.equals("1")){
					TParm searchParm = new TParm();
					searchParm.setData("ORDER_CODE",orderCode);
					
					TParm result =  SPCIndCabdspnTool.getInstance().onQueryIsDrug(searchParm);
					  
					if(result.getCount() >  0 ){
						dosageQty = dosageQty - mdosageQty ;
					}else {
				 		String isDrugOutUdd = getIsDrugOutUdd() ;
				 		
						if(isDrugOutUdd.equals("N")){
							dosageQty = dosageQty - mdosageQty ;
						}
					}
					
				}
			}
        	
        	String orderDesc = resultParm.getValue("ORDER_DESC");
        	
        	
        	String spec = resultParm.getValue("SPECIFICATION");
        	
        	LogUtil.writerLog("扣库组装原始数据为:ORG_CODE=:"+indStock.getOrgCode()+" SENDATCT_FLG=:"+indStock.getSendatcFlg()+"orderDesc:"+orderDesc
        			+"BATCH_NO=:"+indStock.getBatchNo()	);   
        	String orgCode = getOrgCode(orderCode, indStock.getOrgCode(),indStock.getSendatcFlg());
        	
         
        	parmToINDCheck.setData("ORG_CODE",i,orgCode );
        	parmToINDCheck.setData("ORDER_CODE",i,orderCode);
        	parmToINDCheck.setData("DOSAGE_QTY",i,dosageQty);
        	//fux modify 20150507      
        	if(indStock.getBatchNo() !=null){
        		parmToINDCheck.setData("BATCH_NO",i,indStock.getBatchNo());
        	}
        	parmToINDCheck.setData("OPT_DATE",i, StringTool.getTimestamp(indStock.getOptDate(), "yyyy-MM-dd HH:mm:ss"));
        	parmToINDCheck.setData("OPT_USER",i,indStock.getOptUser());
        	parmToINDCheck.setData("OPT_TERM",i,indStock.getOptTerm());
               
           
        	parmToINDCheck.setData("SERVICE_LEVEL",i,indStock.getServiceLevel());
        	parmToINDCheck.setData("ORDER_DESC",i,orderDesc);
        	parmToINDCheck.setData("HIS_ORDER_CODE",i,indStock.getOrderCode());
        	parmToINDCheck.setData("SPECIFICATION",i,spec);
        }
        parm.setData("IND_STOCK_CHECK",parmToINDCheck.getData());
        
        return parm ;
	}

	  
	
	/**
	 * 住院药房调配组装数据
	 * @param odiDspnms
	 * @return
	 */
	private TParm builderDspnmdParm(SpcOdiDspnms odiDspnms) {
		
		
		String regionCode = "";
		
		/**
		 * 组装ODI_DSMNM表
		 */
		TParm mParm = new TParm() ;
		TParm dParm = new TParm() ;

		TParm parm = new TParm() ;
		
		double dosageQty = 0 ;
		double ownAmt = 0 ;
		double retailPrice = 0 ;
        List<SpcOdiDspnm> odidspnmList = odiDspnms.getSpcOdiDspnms() ;
        int j = 0;
        for(int i = 0 ; i < odidspnmList.size() ; i++ ){
        	SpcOdiDspnm odiDspnm = (SpcOdiDspnm)odidspnmList.get(i);
        	mParm.setData("CASE_NO",i,odiDspnm.getCaseNo());
        	mParm.setData("ORDER_NO",i,odiDspnm.getOrderNo());
        	mParm.setData("ORDER_SEQ",i,odiDspnm.getOrderSeq());
        	mParm.setData("START_DTTM",i,odiDspnm.getStartDttm());
        	mParm.setData("END_DTTM",i,odiDspnm.getEndDttm());
        	
        	String regionCode1 = odiDspnm.getRegionCode();
        	if(regionCode == null || regionCode.equals("") || regionCode.equals("null")){
        		if(regionCode1 != null && !regionCode1.equals("") && !regionCode1.equals("null")){
        			regionCode = regionCode1 ;
        		}
        	}
        	mParm.setData("REGION_CODE",i,regionCode1);
        	mParm.setData("STATION_CODE",i,odiDspnm.getStationCode());
        	mParm.setData("DEPT_CODE",i,odiDspnm.getDeptCode());
        	mParm.setData("VS_DR_CODE",i,odiDspnm.getVsDrCode());
        	mParm.setData("BED_NO",i,odiDspnm.getBedNo());
        	
        	mParm.setData("IPD_NO",i,odiDspnm.getIpdNo());
        	mParm.setData("MR_NO",i,odiDspnm.getMrNo());
        	mParm.setData("DSPN_KIND",i,odiDspnm.getDspnKind()); 
        	
        	String dspnDate = odiDspnm.getDspnDate() ;
        	if(dspnDate == null || dspnDate.equals("") ){
        		mParm.setData("DSPN_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("DSPN_DATE",i,StringTool.getTimestamp(dspnDate, "yyyy-MM-dd HH:mm:ss"));
        	}
        	mParm.setData("DSPN_USER",i,odiDspnm.getDspnUser());
        	
        	String effDate = odiDspnm.getDispenseEffDate() ;
        	if(effDate == null || effDate.equals("")){
        		mParm.setData("DISPENSE_EFF_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("DISPENSE_EFF_DATE",i,StringTool.getTimestamp(effDate, "yyyy-MM-dd HH:mm:ss"));
        	}
        	mParm.setData("RX_NO",i,odiDspnm.getRxNo());
        	mParm.setData("ORDER_CAT1_CODE",i,odiDspnm.getOrderCat1Code());
        	mParm.setData("CAT1_TYPE",i,odiDspnm.getCat1Type());
        	
        	String endDate = odiDspnm.getDispenseEndDate() ;
        	if(endDate == null || endDate.equals("")){
        		mParm.setData("DISPENSE_END_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("DISPENSE_END_DATE",i,StringTool.getTimestamp(endDate, "yyyy-MM-dd HH:mm:ss"));
        	}
        	
        	 
        	TParm searchParm = getOrderCodeByHisOrderCode(regionCode1, odiDspnm.getOrderCode());
        	  
        	String orderCode = searchParm.getValue("ORDER_CODE");
        	retailPrice = searchParm.getDouble("RETAIL_PRICE");
        	
        	String orgCode = getOrgCode(orderCode, odiDspnm.getExecDeptCode(),odiDspnm.getSendatcFlg());
        	mParm.setData("EXEC_DEPT_CODE",i,orgCode);
        	mParm.setData("DISPENSE_FLG",i,odiDspnm.getDispenseFlg());
        	mParm.setData("AGENCY_ORG_CODE",i,odiDspnm.getAgencyOrgCode());
        	mParm.setData("PRESCRIPT_NO",i,odiDspnm.getPrescriptNo()); 
        	mParm.setData("LINKMAIN_FLG",i,odiDspnm.getLinkmainFlg()); 
        	
        	mParm.setData("LINK_NO",i,odiDspnm.getLinkNo()); 
        	mParm.setData("ORDER_CODE",i,orderCode); 
        	mParm.setData("ORDER_DESC",i,odiDspnm.getOrderDesc()); 
        	mParm.setData("GOODS_DESC",i,odiDspnm.getGoodsDesc()); 
        	mParm.setData("SPECIFICATION",i,odiDspnm.getSpecification()); 
        	
        	mParm.setData("MEDI_QTY",i,odiDspnm.getMediQty()); 
        	mParm.setData("MEDI_UNIT",i,odiDspnm.getMediUnit()); 
        	mParm.setData("FREQ_CODE",i,odiDspnm.getFreqCode()); 
        	mParm.setData("ROUTE_CODE",i,odiDspnm.getRouteCode()); 
        	mParm.setData("TAKE_DAYS",i,odiDspnm.getTakeDays()); 
        	
        	dosageQty = odiDspnm.getDosageQty() ;
        	
        	mParm.setData("DOSAGE_QTY",i,dosageQty); 
        	mParm.setData("DOSAGE_UNIT",i,odiDspnm.getDosageUnit()); 
        	mParm.setData("DISPENSE_QTY",i,odiDspnm.getDispenseQty()); 
        	mParm.setData("DISPENSE_UNIT",i,odiDspnm.getDispenseUnit()); 
        	mParm.setData("GIVEBOX_FLG",i,odiDspnm.getGiveboxFlg()); 

         
        	mParm.setData("OWN_PRICE",i,retailPrice); 
        	mParm.setData("NHI_PRICE",i,odiDspnm.getNhiPrice()); 
        	mParm.setData("DISCOUNT_RATE",i,odiDspnm.getDiscountRate()); 

        	 
        	ownAmt = retailPrice * dosageQty ;
        	try {
        		ownAmt = StringTool.round(ownAmt, 2);
        	}catch (Exception e) {
				// TODO: handle exception
			}
        	mParm.setData("OWN_AMT",i,ownAmt); 
        	mParm.setData("TOT_AMT",i,odiDspnm.getTotAmt()); 
        	
        	String orderDate = odiDspnm.getOrderDate() ;
        	if(orderDate == null || orderDate.equalsIgnoreCase("")){
        		mParm.setData("ORDER_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("ORDER_DATE",i,StringTool.getTimestamp(orderDate, "yyyy-MM-dd HH:mm:ss")); 
        	}
        	mParm.setData("ORDER_DEPT_CODE",i,odiDspnm.getOrderDeptCode()); 
        	mParm.setData("ORDER_DR_CODE",i,odiDspnm.getOrderDrCode()); 
        	mParm.setData("DR_NOTE",i,odiDspnm.getDrNote()); 
        	mParm.setData("ATC_FLG",i,odiDspnm.getAtcFlg()); 
        	
        	mParm.setData("SENDATC_FLG",i,odiDspnm.getSendatcFlg()); 
        	
        	String sendatcDttm = odiDspnm.getSendatcDttm() ;
        	if(sendatcDttm == null || sendatcDttm.equals("")){
        		mParm.setData("SENDATC_DTTM",i,new TNull(Timestamp.class));
        	}else {
        		mParm.setData("SENDATC_DTTM",i,StringTool.getTimestamp(sendatcDttm,"yyyy-MM-dd HH:mm:ss")); 
			}
        	
        	mParm.setData("INJPRAC_GROUP",i,odiDspnm.getInjpracGroup()); 
        	
        	String dcDate = odiDspnm.getDcDate() ;
        	if(dcDate == null || dcDate.equalsIgnoreCase("")){
        		mParm.setData("DC_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("DC_DATE",i,StringTool.getTimestamp(dcDate, "yyyy-MM-dd HH:mm:ss")); 
        	}
        	 
        	mParm.setData("DC_TOT",i,odiDspnm.getDcTot()); 
        	
        	mParm.setData("RTN_NO",i,odiDspnm.getRtnNo()); 
        	mParm.setData("RTN_NO_SEQ",i,odiDspnm.getRtnNoSeq()); 
        	mParm.setData("RTN_DOSAGE_QTY",i,odiDspnm.getRtnDosageQty()); 
        	mParm.setData("RTN_DOSAGE_UNIT",i,odiDspnm.getDosageUnit()); 
        	mParm.setData("CANCEL_DOSAGE_QTY",i,odiDspnm.getCancelDosageQty()); 
        	
        	mParm.setData("CANCELRSN_CODE",i,odiDspnm.getCancelrsnCode()); 
        	mParm.setData("TRANSMIT_RSN_CODE",i,odiDspnm.getTransmitRsnCode()); 
        	mParm.setData("PHA_RETN_CODE",i,odiDspnm.getPhaRetnCode()); 
        	
        	String phaRetnDate = odiDspnm.getPhaRetnDate() ;
        	if(phaRetnDate == null || phaRetnDate.equals("")){
        		mParm.setData("PHA_RETN_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("PHA_RETN_DATE",i,StringTool.getTimestamp(phaRetnDate, "yyyy-MM-dd HH:mm:ss")); 
        	}
        	mParm.setData("PHA_CHECK_CODE",i,odiDspnm.getPhaCheckCode()); 
        	
        	String phaCheckDate = odiDspnm.getPhaCheckDate() ;
        	if(phaCheckDate == null || phaCheckDate.equals("")){
        		mParm.setData("PHA_CHECK_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("PHA_CHECK_DATE",i,StringTool.getTimestamp(phaCheckDate, "yyyy-MM-dd HH:mm:ss")); 
        	}
        	mParm.setData("PHA_DISPENSE_NO",i,odiDspnm.getPhaDispenseNo()); 
        	mParm.setData("PHA_DOSAGE_CODE",i,odiDspnm.getPhaDosageCode()); 
        	String phaDosageDate = odiDspnm.getPhaDosageDate() ;
        	if(phaDosageDate == null || phaDosageDate.equals("")){
        		mParm.setData("PHA_DOSAGE_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("PHA_DOSAGE_DATE",i,StringTool.getTimestamp(phaDosageDate, "yyyy-MM-dd HH:mm:ss")); 
        	}
        	mParm.setData("PHA_DISPENSE_CODE",i,odiDspnm.getPhaDispenseCode()); 
        	
        	String phaDispenseDate = odiDspnm.getPhaDispenseDate() ;
        	if(phaDispenseDate == null || phaDispenseDate.equals("")){
        		mParm.setData("PHA_DISPENSE_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("PHA_DISPENSE_DATE",i,StringTool.getTimestamp(phaDispenseDate, "yyyy-MM-dd HH:mm:ss")); 
        	}
        	mParm.setData("NS_EXEC_CODE",i,odiDspnm.getNsExecCode()); 
        	
        	String nsExecDate = odiDspnm.getNsExecDate() ;
        	if(nsExecDate == null || nsExecDate.equals("")){
        		mParm.setData("NS_EXEC_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("NS_EXEC_DATE",i,StringTool.getTimestamp(nsExecDate, "yyyy-MM-dd HH:mm:ss")); 
        	}
        	mParm.setData("NS_EXEC_DC_CODE",i,odiDspnm.getNsExecDcCode()); 
        	
        	String nsExecDcDate = odiDspnm.getNsExecDcDate() ;
        	if(nsExecDcDate == null || nsExecDcDate.equals("")){
        		mParm.setData("NS_EXEC_DC_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("NS_EXEC_DC_DATE",i,StringTool.getTimestamp(nsExecDcDate, "yyyy-MM-dd HH:mm:ss")); 
        	}
        	
        	mParm.setData("NS_USER",i,odiDspnm.getNsUser()); 
        	mParm.setData("CTRLDRUGCLASS_CODE",i,odiDspnm.getCtrldrugclassCode()); 
        	mParm.setData("ANTIBIOTIC_CODE",i,odiDspnm.getAntibioticCode()); 
        	mParm.setData("PHA_TYPE",i,odiDspnm.getPhaType()); 
        	mParm.setData("DOSE_TYPE",i,odiDspnm.getDoseType()); 
        	
        	mParm.setData("DCTAGENT_CODE",i,odiDspnm.getDctagentCode()); 
        	mParm.setData("DCTEXCEP_CODE",i,odiDspnm.getDctexcepCode()); 
        	mParm.setData("DCT_TAKE_QTY",i,odiDspnm.getDctTakeQty()); 
        	mParm.setData("PACKAGE_AMT",i,odiDspnm.getPackageAmt()); 
        	mParm.setData("DCTAGENT_FLG",i,odiDspnm.getDctagentFlg()); 
        	
        	mParm.setData("PRESRT_NO",i,odiDspnm.getPresrtNo()); 
        	mParm.setData("DECOCT_CODE",i,odiDspnm.getDecoctCode()); 
        	mParm.setData("URGENT_FLG",i,odiDspnm.getUrgentFlg()); 
        	mParm.setData("SETMAIN_FLG",i,odiDspnm.getSetmainFlg()); 
        	mParm.setData("ORDERSET_GROUP_NO",i,odiDspnm.getOrdersetGroupNo()); 
        	
        	mParm.setData("ORDERSET_CODE",i,odiDspnm.getOrdersetCode()); 
        	mParm.setData("RPTTYPE_CODE",i,odiDspnm.getRpttypeCode());
        	mParm.setData("OPTITEM_CODE",i,odiDspnm.getOptitemCode());
        	mParm.setData("HIDE_FLG",i,odiDspnm.getHideFlg()); 
        	mParm.setData("DEGREE_CODE",i,odiDspnm.getDegreeCode()); 
        	
        	mParm.setData("BILL_FLG",i,odiDspnm.getBillFlg()); 
        	mParm.setData("CASHIER_USER",i,odiDspnm.getCashierUser()); 
        	
        	String cashierDate = odiDspnm.getCashierDate() ;
        	
        	if(cashierDate == null || cashierDate.equals("")){
        		mParm.setData("CASHIER_DATE",i,SystemTool.getInstance().getDate());
        	}else{
        		mParm.setData("CASHIER_DATE",i,StringTool.getTimestamp(cashierDate, "yyyy-MM-dd HH:mm:ss")); 
        	}
        	mParm.setData("IBS_CASE_NO_SEQ",i,odiDspnm.getIbsCaseNoSeq()); 
        	mParm.setData("IBS_SEQ_NO",i,odiDspnm.getIbsSeqNo()); 

        	mParm.setData("OPT_USER",i,odiDspnm.getOptUser());
        	String optDate = odiDspnm.getOptDate() ;
        	if(optDate == null || optDate.equals("")){
        		mParm.setData("OPT_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("OPT_DATE",i,StringTool.getTimestamp(optDate, "yyyy-MM-dd HH:mm:ss"));
        	}
        	mParm.setData("OPT_TERM",i,odiDspnm.getOptTerm());
        	mParm.setData("ANTIBIOTIC_CODE",i,odiDspnm.getAntibioticCode());
        	mParm.setData("DC_DR_CODE",i,odiDspnm.getDcDrCode());
        	
        	mParm.setData("FINAL_TYPE",i,odiDspnm.getFinalType());
        	mParm.setData("DECOCT_REMARK",i,odiDspnm.getDecoctRemark());
        	mParm.setData("SEND_DCT_USER",i,odiDspnm.getSendDctUser());
        	String sendDctDate = odiDspnm.getSendDctDate() ;
        	if(sendDctDate == null || sendDctDate.equals("")){
        		mParm.setData("SEND_DCT_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("SEND_DCT_DATE",i,StringTool.getTimestamp(sendDctDate , "yyyy-MM-dd HH:mm:ss"));
        	}
        	mParm.setData("DECOCT_USER",i,odiDspnm.getDecoctUser());
        	
        	String decoctDate = odiDspnm.getDecoctDate() ;
        	if(decoctDate == null || decoctDate.equals("")){
        		mParm.setData("DECOCT_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("DECOCT_DATE",i,StringTool.getTimestamp(decoctDate , "yyyy-MM-dd HH:mm:ss"));
        	}
        	
        	mParm.setData("SEND_ORG_USER",i,odiDspnm.getSendOrgUser());
        	
        	String sendOrgDate = odiDspnm.getSendOrgDate() ;
        	if(sendOrgDate == null || sendOrgDate.equals("")){
        		mParm.setData("SEND_ORG_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("SEND_ORG_DATE",i,StringTool.getTimestamp(sendOrgDate , "yyyy-MM-dd HH:mm:ss"));
        	}
        	
        	mParm.setData("PARENT_CASE_NO",i,odiDspnm.getParentCaseNo());
        	mParm.setData("PARENT_ORDER_NO",i,odiDspnm.getParentOrderNo());
      
        	mParm.setData("PARENT_ORDER_SEQ",i,odiDspnm.getParentOrderSeq());
        	mParm.setData("PARENT_START_DTTM",i,odiDspnm.getParentStartDttm());
        	mParm.setData("BATCH_SEQ1",i,odiDspnm.getBatchSeq1());
        	mParm.setData("VERIFYIN_PRICE1",i,odiDspnm.getVerifyinPrice1());
        	mParm.setData("DISPENSE_QTY1",i,odiDspnm.getDispenseQty1());
        
        	mParm.setData("RETURN_QTY1",i,odiDspnm.getReturnQty1());
        	mParm.setData("BATCH_SEQ2",i,odiDspnm.getBatchSeq2());
        	mParm.setData("VERIFYIN_PRICE2",i,odiDspnm.getVerifyinPrice2());
        	mParm.setData("DISPENSE_QTY2",i,odiDspnm.getDispenseQty2());
        	mParm.setData("RETURN_QTY2",i,odiDspnm.getReturnQty2());
      
        	mParm.setData("BATCH_SEQ3",i,odiDspnm.getBatchSeq3());
        	mParm.setData("VERIFYIN_PRICE3",i,odiDspnm.getVerifyinPrice3());
        	mParm.setData("DISPENSE_QTY3",i,odiDspnm.getDispenseQty3());
        	mParm.setData("RETURN_QTY3",i,odiDspnm.getReturnQty3());
        	mParm.setData("BAR_CODE",i,odiDspnm.getBarCode());

        	String lisReDate = odiDspnm.getLisReDate() ;
        	if(lisReDate == null || lisReDate.equals("")){
        		mParm.setData("LIS_RE_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("LIS_RE_DATE",i, StringTool.getTimestamp(lisReDate , "yyyy-MM-dd HH:mm:ss"));
        	}
        	mParm.setData("LIS_RE_USER",i,odiDspnm.getLisReUser());
        	
        	String dcNsCheckDate = odiDspnm.getDcNsCheckDate() ;
        	if(dcNsCheckDate == null || dcNsCheckDate.equals("")){
        		mParm.setData("DC_NS_CHECK_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("DC_NS_CHECK_DATE",i,StringTool.getTimestamp(odiDspnm.getDcNsCheckDate() , "yyyy-MM-dd HH:mm:ss"));
        	}
        	mParm.setData("IS_INTG",i,odiDspnm.getIsIntg());
        	mParm.setData("INTGMED_NO",i,odiDspnm.getIntgmedNo());
        	
        	mParm.setData("TURN_ESL_ID",i,odiDspnm.getTurnEslId());
        	mParm.setData("BOX_ESL_ID",i,odiDspnm.getBoxEslId());
        	mParm.setData("TAKEMED_ORG",i,odiDspnm.getTakemedOrg());
        	mParm.setData("ACUM_OUTBOUND_QTY",i,odiDspnm.getAcumOutboundQty());
        	mParm.setData("TAKEMED_NO",i,odiDspnm.getTakemedNo());
        	//fux modify 20150508
        	mParm.setData("BATCH_NO",i,odiDspnm.getBatchNo()); 
        	List<SpcOdiDspnd> odisdpnds = odiDspnm.getSpcOdiDspnds() ;
        	
        	for(int k = 0 ; k < odisdpnds.size() ; k++ ){

        		SpcOdiDspnd dspnd = (SpcOdiDspnd)odisdpnds.get(k);
        		dParm.setData("CASE_NO",j,dspnd.getCaseNo());
        		dParm.setData("ORDER_NO",j,dspnd.getOrderNo());
        		dParm.setData("ORDER_SEQ",j,dspnd.getOrderSeq());
        		dParm.setData("ORDER_DATE",j,dspnd.getOrderDate());
        		dParm.setData("ORDER_DATETIME",j,dspnd.getOrderDatetime());
        		 
        		String batchCode = dspnd.getBatchCode() ;
        		if(batchCode == null || batchCode.equals("null")) {
        			batchCode = "" ;
        		}
        		dParm.setData("BATCH_CODE",j,batchCode);
        		dParm.setData("TREAT_START_TIME",j,dspnd.getTreatStartTime());
        		dParm.setData("TREAT_END_TIME",j,dspnd.getTreatEndTime());
        		dParm.setData("NURSE_DISPENSE_FLG",j,dspnd.getNurseDispenseFlg());
        		
        		String barCode = dspnd.getBarCode() ;
        		if(barCode == null || barCode.equals("null")){
        			barCode = "";
        		}
        		dParm.setData("BAR_CODE",j,barCode);
        		
        		 
        		dParm.setData("ORDER_CODE",j, dspnd.getOrderCode());
        		dParm.setData("MEDI_QTY",j,dspnd.getMediQty());
        		dParm.setData("MEDI_UNIT",j,dspnd.getMediUnit());
        		dParm.setData("DOSAGE_QTY",j,dspnd.getDosageQty());
        		dParm.setData("DOSAGE_UNIT",j,dspnd.getDosageUnit());
        		
        		dParm.setData("TOT_AMT",j,dspnd.getTotAmt());
            	String ddcDate = dspnd.getDcDate() ;
            	if(ddcDate == null || ddcDate.equalsIgnoreCase("")){
            		dParm.setData("DC_DATE",j,new TNull(Timestamp.class));
            	}else{
            		dParm.setData("DC_DATE",j,StringTool.getTimestamp(ddcDate, "yyyy-MM-dd HH:mm:ss")); 
            	}
        		dParm.setData("PHA_DISPENSE_NO",j,dspnd.getPhaDispenseNo());
        		dParm.setData("PHA_DOSAGE_CODE",j,dspnd.getPhaDosageCode());
        		
        		String dPhaDosageDate = dspnd.getPhaDosageDate() ;
        		if(dPhaDosageDate == null || dPhaDosageDate.equals("")){
        			dParm.setData("PHA_DOSAGE_DATE",j,new TNull(Timestamp.class));
        		}else{
        			dParm.setData("PHA_DOSAGE_DATE",j,StringTool.getTimestamp(dPhaDosageDate , "yyyy-MM-dd HH:mm:ss"));
        		}
        		
        		dParm.setData("PHA_DISPENSE_CODE",j,dspnd.getPhaDispenseCode());
        		String dDispenseDate = dspnd.getPhaDispenseDate() ;
        		if(dDispenseDate == null || dDispenseDate.equals("")){
        			dParm.setData("PHA_DISPENSE_DATE",j,new TNull(Timestamp.class));
        		}else{
        			dParm.setData("PHA_DISPENSE_DATE",j,StringTool.getTimestamp(dDispenseDate, "yyyy-MM-dd HH:mm:ss"));
        		}
        		dParm.setData("NS_EXEC_CODE",j,dspnd.getNsExecCode());
        		
        		String dNsExecDate = dspnd.getNsExecDate()  ;
        		if(dNsExecDate == null || dNsExecDate.equals("")){
        			dParm.setData("NS_EXEC_DATE",j,new TNull(Timestamp.class));
        		}else{
        			dParm.setData("NS_EXEC_DATE",j,StringTool.getTimestamp(dNsExecDate , "yyyy-MM-dd HH:mm:ss"));
        		}
        		dParm.setData("NS_EXEC_DC_CODE",j,dspnd.getNsExecDcCode());
        		
        		String dNsExecDcDate = dspnd.getNsExecDcDate() ;
        		if(dNsExecDcDate == null || dNsExecDcDate.equals("")){
        			dParm.setData("NS_EXEC_DC_DATE",j,new TNull(Timestamp.class));
        		}else{
        			dParm.setData("NS_EXEC_DC_DATE",j,StringTool.getTimestamp(dNsExecDcDate, "yyyy-MM-dd HH:mm:ss"));
        		}
        		dParm.setData("NS_USER",j,dspnd.getNsUser());
        		dParm.setData("EXEC_NOTE",j,dspnd.getExecNote());
        		dParm.setData("EXEC_DEPT_CODE",j,dspnd.getExecDeptCode());
        		dParm.setData("BILL_FLG",j,dspnd.getBillFlg());
        		
        		dParm.setData("CASHIER_CODE",j,dspnd.getCashierCode());
        		String dCashierDate = dspnd.getCashierDate() ;
        		if(dCashierDate == null || dCashierDate.equals("")){
        			dParm.setData("CASHIER_DATE",j,new TNull(Timestamp.class));
        		}else{
        			dParm.setData("CASHIER_DATE",j,StringTool.getTimestamp(dCashierDate, "yyyy-MM-dd HH:mm:ss"));
        		}
        		dParm.setData("PHA_RETN_CODE",j,dspnd.getPhaRetnCode());
        		String dPhaRetnDate = dspnd.getPhaRetnDate() ;
        		if(dPhaRetnDate == null || dPhaRetnDate.equals("")){
        			dParm.setData("PHA_RETN_DATE",j,new TNull(Timestamp.class));
        		}else{
        			dParm.setData("PHA_RETN_DATE",j,StringTool.getTimestamp(dPhaRetnDate, "yyyy-MM-dd HH:mm:ss"));
        		}
        		dParm.setData("TRANSMIT_RSN_CODE",j,dspnd.getTransmitRsnCode());
        		
        		dParm.setData("STOPCHECK_USER",j,dspnd.getStopcheckUser());
        		dParm.setData("STOPCHECK_DATE",j,dspnd.getStopcheckDate());
        		dParm.setData("IBS_CASE_NO",j,dspnd.getIbsCaseNo());
        		dParm.setData("IBS_CASE_NO_SEQ",j,dspnd.getIbsCaseNoSeq());
        		dParm.setData("OPT_USER",j,dspnd.getOptUser());
        		
        		String dOptDate = dspnd.getOptDate()  ;
        		if(dOptDate == null || dOptDate.equals("")){
        			dParm.setData("OPT_DATE",j,new TNull(Timestamp.class));
        		}else{
        			dParm.setData("OPT_DATE",j,StringTool.getTimestamp(dOptDate , "yyyy-MM-dd HH:mm:ss"));
        		}
        		dParm.setData("OPT_TERM",j,dspnd.getOptTerm());
        		
        		 
        		dParm.setData("INTGMED_NO",j,dspnd.getIntgmedNo());
        		dParm.setData("TAKEMED_ORG",j,dspnd.getTakemedOrg());
        		
        		
        		dParm.setData("TAKEMED_NO",j,dspnd.getTakemedNo());
        		
        		j++;
        	}
        }
        parm.setData("DSPN_M",mParm.getData());
        parm.setData("DSPN_D",dParm.getData());
		return parm;
	}
	
	private TParm builderDspnmdRtnParm(SpcOdiDspnms odiDspnms) {
	 
		String regionCode = "";
		
		TParm mParm = new TParm() ;
		TParm dParm = new TParm() ;

		TParm parm = new TParm() ;
		
        List<SpcOdiDspnm> odidspnmList = odiDspnms.getSpcOdiDspnms() ;
        int j = 0;
        String msg = "";
        for(int i = 0 ; i < odidspnmList.size() ; i++ ){
        	SpcOdiDspnm odiDspnm = (SpcOdiDspnm)odidspnmList.get(i);
        	mParm.setData("CASE_NO",i,odiDspnm.getCaseNo());
        	mParm.setData("ORDER_NO",i,odiDspnm.getOrderNo());
        	mParm.setData("ORDER_SEQ",i,odiDspnm.getOrderSeq());
        	mParm.setData("START_DTTM",i,odiDspnm.getStartDttm());
        	mParm.setData("END_DTTM",i,odiDspnm.getEndDttm());
        	
        	String regionCode1 = odiDspnm.getRegionCode();
        	if(regionCode == null || regionCode.equals("") || regionCode.equals("null")){
        		if(regionCode1 != null && !regionCode1.equals("") && !regionCode1.equals("null")){
        			regionCode = regionCode1 ;
        			break;
        		}
        	}
        	if(regionCode == null || regionCode.equals("")){
        		regionCode = "H01";
        	}

        	String orderCode =  odiDspnm.getOrderCode();
        	mParm.setData("ORDER_CODE",i,orderCode); 
        	mParm.setData("ORDER_DESC",i,odiDspnm.getOrderDesc()); 
        	mParm.setData("DC_TOT",i,odiDspnm.getDcTot()); 
        	mParm.setData("RTN_DOSAGE_QTY",i,odiDspnm.getRtnDosageQty()); 
        	mParm.setData("RTN_DOSAGE_UNIT",i,odiDspnm.getDosageUnit()); 
        	
        	mParm.setData("TRANSMIT_RSN_CODE",i,odiDspnm.getTransmitRsnCode()); 
        	mParm.setData("DSPN_USER",i,odiDspnm.getDspnUser());
        	
        	
        	
        	
        	String dspnDate = odiDspnm.getDspnDate() ;
        	if(dspnDate == null || dspnDate.equals("") ){
        		mParm.setData("DSPN_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("DSPN_DATE",i,StringTool.getTimestamp(dspnDate, "yyyy-MM-dd HH:mm:ss"));
        	}
        	
        	mParm.setData("OPT_USER",i,odiDspnm.getOptUser());
        	
        	
        	String optDate = odiDspnm.getOptDate() ;
        	if(optDate == null || optDate.equals("")){
        		mParm.setData("OPT_DATE",i,new TNull(Timestamp.class));
        	}else{
        		mParm.setData("OPT_DATE",i,StringTool.getTimestamp(optDate, "yyyy-MM-dd HH:mm:ss"));
        	}
        	
        	mParm.setData("OPT_TERM",i,odiDspnm.getOptTerm());
        	
        	//SQL语句里是RX_KIND
        	mParm.setData("RX_KIND",i,odiDspnm.getDspnKind()); 
        	mParm.setData("EXEC_DEPT_CODE",i,odiDspnm.getExecDeptCode());
        	mParm.setData("MR_NO",i,odiDspnm.getMrNo());
        	mParm.setData("STATION_CODE",i,odiDspnm.getStationCode());
        	
        	mParm.setData("BED_NO",i,odiDspnm.getBedNo());
         	mParm.setData("IPD_NO",i,odiDspnm.getIpdNo());
         	mParm.setData("DISPENSE_QTY",i,odiDspnm.getDispenseQty()); 
        	mParm.setData("DISPENSE_UNIT",i,odiDspnm.getDispenseUnit()); 
        	mParm.setData("DOSE_TYPE",i,odiDspnm.getDoseType()); 
        	
        	mParm.setData("GIVEBOX_FLG",i,odiDspnm.getGiveboxFlg()); 
        	mParm.setData("OWN_PRICE",i,odiDspnm.getOwnPrice()); 
        	mParm.setData("ROUTE_CODE",i,odiDspnm.getRouteCode());
        	mParm.setData("RTN_NO",i,odiDspnm.getRtnNo()); 
        	mParm.setData("RTN_NO_SEQ",i,odiDspnm.getRtnNoSeq()); 
        	
        	mParm.setData("ORDER_CAT1_CODE",i,odiDspnm.getOrderCat1Code());
        	mParm.setData("REGION_CODE",i,odiDspnm.getRegionCode());
        	mParm.setData("DEPT_CODE",i,odiDspnm.getDeptCode());
        	mParm.setData("CAT1_TYPE",i,odiDspnm.getCat1Type());
        	mParm.setData("VS_DR_CODE",i,odiDspnm.getVsDrCode());
        	mParm.setData("PHA_TYPE",i,odiDspnm.getPhaType());
        	
        	mParm.setData("CANCEL_DOSAGE_QTY",i,odiDspnm.getCancelDosageQty()); 
        	mParm.setData("SERVICE_LEVEL",i,odiDspnm.getServiceLevel());
        	
        	List<SpcOdiDspnd> odisdpnds = odiDspnm.getSpcOdiDspnds() ;
        	
        	for(int k = 0 ; k < odisdpnds.size() ; k++ ){
        		
        		SpcOdiDspnd dspnd = (SpcOdiDspnd)odisdpnds.get(k);
        		dParm.setData("CASE_NO",j,dspnd.getCaseNo());
        		dParm.setData("ORDER_NO",j,dspnd.getOrderNo());
        		dParm.setData("ORDER_SEQ",j,dspnd.getOrderSeq());
        		dParm.setData("ORDER_DATE",j,dspnd.getOrderDate());
        		dParm.setData("ORDER_DATETIME",j,dspnd.getOrderDatetime());
        		

        		dParm.setData("ORDER_CODE",j,dspnd.getOrderCode());
        		dParm.setData("RTN_DOSAGE_QTY",j,dspnd.getDosageQty());
        		dParm.setData("RTN_DOSAGE_UNIT",j,dspnd.getDosageUnit());
        		dParm.setData("EXEC_DEPT_CODE",j,dspnd.getExecDeptCode());
        		dParm.setData("TRANSMIT_RSN_CODE",j,dspnd.getTransmitRsnCode());
        		dParm.setData("OPT_USER",j,dspnd.getOptUser());
        		
        		String dOptDate = dspnd.getOptDate()  ;
        		if(dOptDate == null || dOptDate.equals("")){
        			dParm.setData("OPT_DATE",j,new TNull(Timestamp.class));
        		}else{
        			dParm.setData("OPT_DATE",j,StringTool.getTimestamp(dOptDate , "yyyy-MM-dd HH:mm:ss"));
        		}
        		dParm.setData("OPT_TERM",j,dspnd.getOptTerm());
        		
        		j++;
        	}
        }
        parm.setData("DSPN_M",mParm.getData());
        parm.setData("DSPN_D",dParm.getData());
		return parm;
	}
	
		
	private String getSpcOrderCodeByHisOrderCode(String regionCode,String hisOrderCode){
		TParm inParm = new TParm();
		inParm.setData("HIS_ORDER_CODE",hisOrderCode);
		inParm.setData("REGION_CODE",regionCode);
		TParm parm = SPCSysFeeTool.getInstance().querySysFeeSpc(inParm);
		return parm.getValue("ORDER_CODE",0);
	}

	private TParm getOrderCodeByHisOrderCode(String regionCode,String hisOrderCode){
		
		String sql = "SELECT B.ORDER_CODE,B.ORDER_DESC,B.RETAIL_PRICE,B.SPECIFICATION FROM PHA_BASE B  " +
	     "     WHERE B.ORDER_CODE='"+hisOrderCode+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode() < 0 ){
			LogUtil.writerLogErr("未查询到对应的物联网ORDER_CODE"+"REGION_CODE----:"+regionCode+" HIS_ORDER_CODE-----:"+hisOrderCode);
			return new TParm();
		}

		return result.getRow(0) ;
		
	}
	
	
	 
	  
	
	private String getOrgCode(String orderCode,String exeDept,String  sendatcFlg){
		
		if(sendatcFlg == null || sendatcFlg.equals("")){
			sendatcFlg = "N" ;
		}
		
		if( sendatcFlg != null && sendatcFlg.equals("N")){
			return exeDept;
		}
		
		String sql = " SELECT A.ATC_FLG_I FROM SYS_FEE A WHERE A.ORDER_CODE='"+orderCode+"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if(result.getErrCode() < 0 ){
			LogUtil.writerLog("查询不到SYS_FEE记录-------:"+orderCode) ;
			return exeDept ;
		}
		
	
		String actFlgI = result.getValue("ATC_FLG_I",0);
		
		LogUtil.writerLog("注记-------:"+actFlgI) ;
		
		if(actFlgI != null && actFlgI.equals("Y")){
			 sql = " SELECT A.ORG_CODE FROM IND_ORG A WHERE A.ATC_ORG_CODE='"+exeDept+"' " ;
			 result =  new TParm(TJDODBTool.getInstance().select(sql));
			 if(result.getErrCode() < 0 ){
				 return exeDept ;
			 }
			 return result.getValue("ORG_CODE",0);
		}
		return exeDept;
	}
	
	public static String getNowTime(String dateformat) {
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(dateformat);
		String hehe = dateFormat.format(now);
		return hehe;
	}
	
	public static String getObjectId(){
		String dateStr = getNowTime("yyyyMMddHHmmssSSS");
		return "10"+dateStr ;
	}
	
	
	
	 private String getApRegion( String orgCode) {
		TParm searchParm = new TParm () ;
		searchParm.setData("ORG_CODE",orgCode);
		TParm resulTParm = SPCGenDrugPutUpTool.getInstance().onQueryLabelByOrgCode(searchParm);
		String apRegion = "";
		if(resulTParm != null ){
			apRegion = resulTParm.getValue("AP_REGION");  
		}
		return apRegion;
	}
	 
		public boolean sendLabelDate(List<Map<String, Object>> list, String url) {

			List<Map<String, Object>> newList = new ArrayList<Map<String, Object>>();

			String objectId = getObjectId();
			for (int i = 0; i < list.size(); i++) {
				Map<String, Object> map = (Map<String, Object>) list.get(i);
				Map<String, Object> newMap = new LinkedHashMap<String, Object>();
				newMap.put("ObjectId", objectId);
				newMap.put("ObjectType", 1);
				newMap.put("ObjectName", "medBasket");

				newMap.put("TagNo", map.get("TagNo"));
				
	
				newMap.put("APRegion", map.get("APRegion"));
				newMap.put("ShelfNo", "10411515515");
		
				newMap.put("ProductName", map.get("ProductName"));
				
				String spec = (String) map.get("SPECIFICATION");
				if(spec != null && !spec.equals("") &&  (spec.length() > 12)){
					spec = spec.substring(0,12);
				}
				
				String num = (String)map.get("NUM");
				if(num == null || num.equals("null") || num.equals("")){
					num = "";
				}
				
				spec = spec + " "+num;
				 
			
				newMap.put("Spec", spec);

				newMap.put("Light", map.get("Light"));

				// map.put("LightTimes", 3);
				newMap.put("RSend", "0");
				newMap.put("RSendSum", 0);
				newMap.put("Enabled", false);
				newList.add(newMap);
			}
			LabelInf labelInf = new LabelImpl();
			Map<String, Object> returnMap = labelInf.labelData(newList, url);
			if (null != returnMap) {
				String status = (String) returnMap.get("Status");
				if (null != status && "10000".equals(status)) {// 更新电子标签状态成功
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}

		}
	
		private String  getIsDrugOutUdd(){
			String isDrugOutUdd = "Y" ;
			String sql = " SELECT IS_DRUG_OUT_UDD FROM IND_SYSPARM " ;
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			if(parm != null && parm.getCount() > 0 ){
				isDrugOutUdd = parm.getRow(0).getValue("IS_DRUG_OUT_UDD");
			}
			return isDrugOutUdd ;
		}
	

}
