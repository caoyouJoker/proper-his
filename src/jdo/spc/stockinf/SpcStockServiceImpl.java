package jdo.spc.stockinf;

import java.util.List;

import javax.jws.WebService;

import jdo.spc.stockinf.dto.IndAgent;
import jdo.spc.stockinf.dto.IndStock;

/**
 * <p>
 * Title: 物联网 werbservice接口
 * </p>
 * 
 * <p>
 * Description: 物联网 werbservice接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author fuwj
 * @version 1.0
 */
@WebService
public class SpcStockServiceImpl implements SpcStockService {
	
	/**
	 * 查询物联网麻精药品库存
	 */
	@Override
	public List<IndStock> onSearchIndStock() {
		return SpcStockDaoImpl.getInstance().onSearchIndStock();
	}
	
	/**
	 * 同步HIS端IND_AGNET数据
	 */
	@Override
	public String onSaveIndAgent(IndAgent indAgent) {
		// TODO Auto-generated method stub
		return SpcStockDaoImpl.getInstance().onSaveIndAgent(indAgent);
		
	}

}
