package jdo.spc.uddinfo;


import java.util.List;

import javax.jws.WebService;




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
public class SpcUddServiceImpl implements SpcUddService {

	/**
	 * 护士单次执行-根据麻精条码查询发药信息主键
	 * @param caseNo
	 * @param barCode 麻精条码
	 * @return
	 */
	@Override
	public List<OdiDspndPkVo> getOdiDspndInfo(String caseNo, String barCode) {
		// TODO Auto-generated method stu
		return SpcUddServiceDaoImpl.getInstance().getOdiDspndInfo(caseNo, barCode); 
	}
	




	
}