#
# Title:静点区域床位
#
# Description:静点区域床位
#
# Copyright: JavaHis (c) 2009
#
# @author wangl 2009/10/271

Module.item=query;insert;update;delete;initCombo;updateBed;updatePatStatus


//根据条件查询静点区域床
//=========pangben modify 20110622 添加区域
query.Type=TSQL
query.SQL=SELECT REGION_CODE_ALL,REGION_CODE, BED_NO, BED_DESC, BED_STATUS, TYPE, &
                 MR_NO, CASE_NO, PAT_STATUS, REGISTER_DATE, OPT_USER, &
                 OPT_DATE, OPT_TERM &
	   FROM  PHL_BED 
query.ITEM=REGION_CODE;BED_NO;MR_NO;CASE_NO;REGION_CODE_ALL
query.REGION_CODE=REGION_CODE=<REGION_CODE>
query.REGION_CODE_ALL=REGION_CODE_ALL=<REGION_CODE_ALL>
query.BED_NO=BED_NO=<BED_NO>
query.MR_NO=MR_NO=<MR_NO>
query.CASE_NO=CASE_NO=<CASE_NO>
query.Debug=N

//添加静点区域床位
//=========pangben modify 20110622 添加区域
insert.Type=TSQL
insert.SQL = INSERT INTO PHL_BED( &
		REGION_CODE, BED_NO, BED_DESC, BED_STATUS, TYPE, &
	     	OPT_USER, OPT_DATE, OPT_TERM,REGION_CODE_ALL) &
	     VALUES(<REGION_CODE>, <BED_NO>, <BED_DESC>, <BED_STATUS>, <TYPE>,&
	     	<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<REGION_CODE_ALL>)
insert.Debug=N

//更新静点区域床位
update.Type=TSQL
update.SQL = UPDATE PHL_BED SET &
		BED_DESC=<BED_DESC> , BED_STATUS=<BED_STATUS> , TYPE=<TYPE>, OPT_USER=<OPT_USER> , OPT_DATE=<OPT_DATE> , OPT_TERM=<OPT_TERM> &
	     WHERE REGION_CODE=<REGION_CODE> AND BED_NO=<BED_NO>
update.Debug=N

//删除静点区域床位
//=========pangben modify 20110622 添加区域
delete.Type=TSQL
delete.SQL=DELETE FROM PHL_BED WHERE REGION_CODE=<REGION_CODE> AND BED_NO=<BED_NO> AND REGION_CODE_ALL=<REGION_CODE_ALL>
delete.Debug=N

//静点床位combo
//=========pangben modify 20110622 添加区域
initCombo.Type=TSQL
initCombo.SQL=SELECT BED_NO AS ID,BED_DESC AS NAME,ENNAME &
		FROM PHL_BED &
	    ORDER BY BED_NO
initCombo.ITEM=REGION_CODE;BED_STATUS;REGION_CODE_ALL
initCombo.REGION_CODE=REGION_CODE=<REGION_CODE>
initCombo.BED_STATUS=BED_STATUS=<BED_STATUS>
initCombo.REGION_CODE_ALL=REGION_CODE_ALL=<REGION_CODE_ALL>
initCombo.Debug=N

//病患报到更新静点床位
updateBed.Type=TSQL
updateBed.SQL = UPDATE PHL_BED SET &
		       BED_STATUS=<BED_STATUS> , MR_NO=<MR_NO> , CASE_NO=<CASE_NO>, PAT_STATUS=<PAT_STATUS> , &
		       REGISTER_DATE=<REGISTER_DATE>,  OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE> , OPT_TERM=<OPT_TERM> &
	         WHERE REGION_CODE=<REGION_CODE> AND BED_NO=<BED_NO>
updateBed.Debug=N


//更新病患状态
updatePatStatus.Type=TSQL
updatePatStatus.SQL = UPDATE PHL_BED SET &
		             PAT_STATUS=<PAT_STATUS> , OPT_USER=<OPT_USER>, OPT_DATE=<OPT_DATE> , OPT_TERM=<OPT_TERM> &
	               WHERE REGION_CODE=<REGION_CODE> AND BED_NO=<BED_NO>
updatePatStatus.Debug=N

