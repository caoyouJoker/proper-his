#
# Title:静点执行医嘱
#
# Description:静点执行医嘱
#
# Copyright: JavaHis (c) 2009
#
# @author wangl 2009/10/271

Module.item=query;insertOrderDetail;updateOrderDetail


//根据条件查询静点执行医嘱
query.Type=TSQL
query.SQL=SELECT REGION_CODE, BED_NO, BED_DESC, BED_STATUS, TYPE, &
                 MR_NO, CASE_NO, PAT_STATUS, REGISTER_DATE, OPT_USER, &
                 OPT_DATE, OPT_TERM &
	   FROM  PHL_ORDER 
query.ITEM=REGION_CODE;BED_NO
query.REGION_CODE=REGION_CODE=<REGION_CODE>
query.BED_NO=BED_NO=<BED_NO>
query.Debug=N


//添加静点执行医嘱
insertOrderDetail.Type=TSQL
insertOrderDetail.SQL = INSERT INTO PHL_ORDER( &
		         START_DATE, ADM_TYPE, CASE_NO, ORDER_NO, SEQ_NO, ORDER_CODE, &
			 MR_NO, DR_CODE, ORDER_DTTM, LINK_MAIN_FLG, LINK_NO, &
			 ROUTE_CODE, FREQ_CODE, TAKE_DAYS, BAR_CODE, BAR_CODE_PRINT_FLG, &
			 EXEC_STATUS, DR_NOTE, NS_NOTE, &
			 OPT_USER, OPT_DATE, OPT_TERM) &
		  VALUES(<START_DATE>, <ADM_TYPE>, <CASE_NO>, <ORDER_NO>, <SEQ_NO>, <ORDER_CODE>,&
		         <MR_NO>, <DR_CODE>, <ORDER_DTTM>, <LINK_MAIN_FLG>, <LINK_NO>,&
		         <ROUTE_CODE>, <FREQ_CODE>, <TAKE_DAYS>, <BAR_CODE>, <BAR_CODE_PRINT_FLG>,&
		         <EXEC_STATUS>, <DR_NOTE>, <NS_NOTE>,&
			 <OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertOrderDetail.Debug=N


//更新静点执行医嘱
updateOrderDetail.Type=TSQL
updateOrderDetail.SQL = UPDATE PHL_ORDER SET &
		               EXEC_STATUS=<EXEC_STATUS> , EXEC_USER=<EXEC_USER> , EXEC_DATE=<EXEC_DATE>,  NS_NOTE=<NS_NOTE>, &
		               OPT_USER=<OPT_USER> , OPT_DATE=<OPT_DATE> , OPT_TERM=<OPT_TERM> & 
	                 WHERE ADM_TYPE=<ADM_TYPE> &
	                       AND CASE_NO=<CASE_NO> &
	                       AND ORDER_NO=<ORDER_NO> &
	                       AND SEQ_NO=<SEQ_NO> &
	                       AND ORDER_CODE=<ORDER_CODE> &
	                       AND START_DATE=<START_DATE>
updateOrderDetail.Debug=N



