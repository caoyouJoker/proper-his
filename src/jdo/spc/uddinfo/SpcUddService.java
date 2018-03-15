package jdo.spc.uddinfo;

import java.util.List;

import javax.jws.WebService;

import jdo.spc.inf.dto.SPCIndRequestm;
import jdo.pha.inf.dto.SpcOpdOrderDtos;
import jdo.spc.inf.dto.SpcOpdOrderReturnDto;


/**
 * <p>Title: 物联网-护士单词执行- werbservice接口</p>
 *
 * <p>Description: 物联网-护士单词执行- werbservice接口</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 *  @author  liyh
 * @version 1.0
 */

@WebService
public interface SpcUddService {

	/**
	 * 护士单次执行-根据麻精条码查询发药信息主键
	 * @param caseNo
	 * @param barCode 麻精条码
	 * @return
	 */
	public List<OdiDspndPkVo> getOdiDspndInfo(String caseNo,String barCode);
	

}
