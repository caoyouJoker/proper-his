package jdo.spc.inf;

import javax.jws.WebService;

import jdo.spc.inf.dto.SPCIndRequestm;
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
public interface SpcService {

	/**
	 * HIS退药查询医嘱状态
	 * @param dto 
	 * @return
	 */
	public SpcOpdOrderReturnDto getPhaStateReturn(String rxNo,String caseNo, String seqNo);
	
	
	/**
	 * HIS请领作业
	 * @param requestM 数据传输对象
	 * @return
	 */
	public String onSaveSpcRequest (SPCIndRequestm requestM);

	/**
	 * 预发药接口-保存从HIS传过来的医嘱信息
	 * @return
	 */
	public String onSavePhaOrderFromHis(SpcOpdOrderDtos dtos);	
}
