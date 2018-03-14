package action.spc.services;

import javax.jws.WebService;
  
import action.spc.services.dto.SpcInwCheckDtos;
import action.spc.services.dto.SpcOdiDspnms;

@WebService           
public interface SpcOdiService {  

	public SpcInwCheckDtos inwCheck(SpcInwCheckDtos dtos) ;
	        

	public  String examine(SpcOdiDspnms odiDspnms);
	

	public String onUpdateRtnCfm(SpcOdiDspnms odiDspnms);
	

	public String sendElectronicTag(String caseNo,String patName,String  stationDesc,String bedNoDesc,String mrNo,String ip);
	
  
	public String onSaveIndCabdspn(SpcOdiDspnms odiDspnms,String status);

	public String onCheckStockQty(SpcOdiDspnms odiDspnms);

	public String onCheckStockQtyBatch(SpcOdiDspnms odiDspnms);
	
	public String onQueryBatch(SpcOdiDspnms odiDspnms);
	
	public String examineBatch(SpcOdiDspnms odiDspnms);
	
}


