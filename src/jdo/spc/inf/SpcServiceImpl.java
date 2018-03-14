package jdo.spc.inf;


import javax.jws.WebService;

import jdo.spc.inf.dto.SPCIndRequestm;
import jdo.spc.inf.dto.SpcCommonDto;
import jdo.spc.inf.dto.SpcCommonDtos;
import jdo.pha.inf.dto.SpcOpdOrderDtos;
import jdo.spc.inf.dto.SpcOpdOrderReturnDto;



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
public class SpcServiceImpl implements SpcService {

	/**
	 * HIS退药查询医嘱状态
	 * @param mrNo 病案号
	 * @param rxNo 处方号
	 * @param orderCode 药品编码
	 * @return
	 */
	public SpcOpdOrderReturnDto getPhaStateReturn(String rxNo,String caseNo, String seqNo) {
		// TODO Auto-generated method stub
		return SpcDaoImpl.getInstance().getPhaStateReturn(rxNo,caseNo,seqNo);
	}
	
	/**
	 * 住院药房请领
	 */
	@Override
	public String onSaveSpcRequest(SPCIndRequestm requestM) {
		// TODO Auto-generated method stub
		return SpcRequestDaoImpl.getInstance().onSaveSpcRequest(requestM);
	}

	/**
	 *  预发药接口-保存从HIS传过来的医嘱信息
	 */
	@Override
	public String onSavePhaOrderFromHis(SpcOpdOrderDtos dtos) {
		// TODO Auto-generated method stub
		return SpcServiceDaoImpl.getInstance().onSavePhaOrderFromHis(dtos);
	}




	
}