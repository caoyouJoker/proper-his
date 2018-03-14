package jdo.spc;

import javax.jws.WebService;

/**
 * <p>Title: 物联网与HIS OPD_ORDER表同步 werbservice接口</p>
 *
 * <p>Description: 物联网与HIS OPD_ORDER表同步 werbservice接口</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author liuzhen 2013-1-21
 * @version 4.0
 */
@WebService
public interface SPCOpdOrderSynchWsTool {
		
	
	
	/**更新 OPD_ORDER 接口*/
	public boolean updateOpdOrder(String case_no,String rx_no,String seq_no,
			String pha_check_code,String pha_check_date_str,
			String pha_dosage_code,String pha_dosage_date_str,
			String pha_dispense_code,String pha_dispense_date_str,
			String pha_retn_code,String pha_retn_date_str,
			String batchSeq1,String verifyinPrice1,
			String batchSeq2,String verifyinPrice2,
			String batchSeq3,String verifyinPrice3);

}
