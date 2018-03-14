package jdo.spc;

import javax.jws.WebService;


/**
 * <p>Title: 物联网 werbservice接口</p>
 *
 * <p>Description: 物联网 werbservice接口</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: Javahis</p>
 *
 *  @author  Yuanxm
 * @version 1.0
 */

@WebService
public class SpcImpl implements SpcInf {

	/**
	 * 门急诊药房审核（HIS执行门急诊药房审核后、将处方信息传送至物联网）
	 * @param list 
	 * @return
	 */
	public String phaExamine(SpcOpdOrderDtos dtos){
		if(null != dtos){
			
		}
		return "";
	}

	/**
	 * 门急诊药房发药(于HIS门急诊发药界面点中处方时、相应药筐需闪烁提示)
	 * @param SpcCommonDto  数据传输对象
	 * @return
	 */
	public String phaSend(SpcCommonDtos dtos) {
		return null;
	}

	/**
	 * 门急诊药房发药门急诊药房发药(于HIS执行门急诊药房发药后、将发药信息传送至物联网)
	 * @return
	 */
	public String phaSendClicked(SpcCommonDto dto) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
}