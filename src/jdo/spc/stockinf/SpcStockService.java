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
public interface SpcStockService {
	/**
	 * 查询IND_STOCK麻精药品
	 * @param requestM 数据传输对象
	 * @return
	 */
	public List<IndStock> onSearchIndStock();
	
	
	/**
	 * 同步HIS端IND_AGENT
	 * @param indAgent
	 * @return
	 */
	public String onSaveIndAgent(IndAgent indAgent);
	
	
	}
