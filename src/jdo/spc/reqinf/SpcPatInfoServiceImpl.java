package jdo.spc.reqinf;

import javax.jws.WebService;

import jdo.spc.reqinf.dto.SysPatinfo;

@WebService
public class SpcPatInfoServiceImpl implements SpcPatInfoService {

	@Override
	public String onSaveSpcPatInfo(SysPatinfo patinfo) {
		// log
		return  SpcPatInfoDaoImpl.getInstance().onSaveSpcPatInfo(patinfo);
	}
   
}
   