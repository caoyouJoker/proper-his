package jdo.opd.ws;

import java.util.List;

import javax.jws.WebService;

/**
*
* <p>
* Title: 医嘱接口
* </p>
*
* <p>
* Description:医生站医嘱调用webService 添加物联网数据
* </p>
*
* <p>
* Copyright: Copyright (c) 
* </p>
*
* <p>
* Company: ProperSoft
* </p>
*
* @author pangben 2013-5-20
* @version 1.0
*/
@WebService
public interface IOpdOrderService {
	/**
	 * 添加医嘱操作
	 * @return
	 */
	public String saveOpdOrder(OpdOrderList opdOrderList);
}
