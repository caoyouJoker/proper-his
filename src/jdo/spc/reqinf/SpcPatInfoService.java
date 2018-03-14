package jdo.spc.reqinf;

import javax.jws.WebService;

import jdo.spc.reqinf.dto.SysPatinfo;


@WebService
public interface SpcPatInfoService {
	
	/**
	 * HIS请领作业
	 * @param requestM 数据传输对象
	 * @return
	 */
	public String onSaveSpcPatInfo(SysPatinfo patinfo);
}
