package jdo.inv;

import javax.jws.WebService;
/** 
 * <p>
 * Title: 手术对接手术包 werbservice接口
 * </p>
 * 
 * <p>
 * Description: 手术对接手术包 werbservice接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 *   
 * @author fux 2014-5-4
 * @version 4.0
 */
@WebService 
public interface OPEPackageWsTool {  
	//从HIS传过来 得到OPE_REQUEST信息
    //OPERATION_ICD ; OPT_CHN_DESC ; PACK_CODE ; QTY ; PACK_DESC ; SEQ_FLG  ---物联网维护手术对应手术包界面
	//OPBOOK_SEQ ; OP_CODE  ; SUPTYPE_CODE; OP_DATE ; STATE
	//手术排程信息(用来获取package的信息)
	//预约状态 0 申请， 1 排程完毕 ，2手术完成   
	//public String onOpeSeqQuery(String opbookSeq,String opCode,String packCode,String qty,String packDesc,String supTypeCode, String opDate); 
	//只需要全部保存进OPE_PACKAGE
	//flg 全部为0 未排程为1 已排程为2
	public String onSaveOpePackage(String opCode,String supTypeCode, String opDateS, String opDateE,String state,String optUser ,String optTerm);
}
