#
# <p>Title:医嘱模板替换基本档 </p>
#
# <p>Description: </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: </p>
#
# @author wangl 2011-05-27
# @version 1.0
#
Module.item=select;insert;update;insertComorderReplace;updateComorderReplace;deleteComorderReplace

//查询全字段
select.Type=TSQL
select.SQL=SELECT ORDER_CODE,MEDI_QTY,MEDI_UNIT,ROUTE_CODE,SEQ,&
         	  ORDER_DESC,REGION_CODE,ORDER_CODE_OLD,&
          	  ORDER_DESC_OLD,MEDI_QTY_OLD,MEDI_UNIT_OLD,ROUTE_CODE_OLD,OPT_USER,OPT_DATE,OPT_TERM,PACK_CODE &
	     FROM SYS_COMORDER_REPLACE 
select.item=ORDER_CODE;MEDI_QTY;MEDI_UNIT;ROUTE_CODE;REGION_CODE
select.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
select.MEDI_QTY=MEDI_QTY=<MEDI_QTY>
select.MEDI_UNIT=MEDI_UNIT=<MEDI_UNIT>
select.ROUTE_CODE=ROUTE_CODE=<ROUTE_CODE>
select.REGION_CODE=REGION_CODE=<REGION_CODE>
select.Debug=N
 
 
 
//病患入住
update.Type=TSQL
update.SQL=UPDATE  SYS_BED SET APPT_FLG=<APPT_FLG>,& 
	ALLO_FLG=<ALLO_FLG>,MR_NO=<MR_NO>,CASE_NO=<CASE_NO>,IPD_NO=<IPD_NO>,BED_STATUS=<BED_STATUS>,&
 	OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
 	WHERE BED_NO=<BED_NO> 
update.Debug=N


//插入主档
insert.Type=TSQL
insert.SQL=SELECT B.ROOM_CODE,A.BED_NO,B.YELLOW_SIGN,B.RED_SIGN,A.BED_NO_DESC &
 		      FROM SYS_BED A ,SYS_ROOM B &
 		     WHERE A.ROOM_CODE=B.ROOM_CODE &
 		       AND A.BED_NO=<BED_NO>
insert.Debug=N

//差看病患是否入住
checkBedStatus.Type=TSQL
checkBedStatus.SQL=SELECT BED_STATUS,BED_NO,BED_OCCU_FLG &
 		      FROM SYS_BED &
 		     WHERE CASE_NO=<CASE_NO>
checkBedStatus.Debug=N

//出科清床 （根据CASE_NO清空床位）
clearForAdm.Type=TSQL
clearForAdm.SQL=UPDATE SYS_BED SET APPT_FLG='N',BED_OCCU_FLG='N',& 
			ALLO_FLG='N',MR_NO='',CASE_NO='',IPD_NO='',BED_STATUS='0',&
			OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
			WHERE CASE_NO=<CASE_NO> 
clearForAdm.Debug=N

//取消包床
clearOCCUBed.Type=TSQL
clearOCCUBed.SQL=UPDATE SYS_BED SET APPT_FLG='N',BED_OCCU_FLG='N',& 
			ALLO_FLG='N',MR_NO='',CASE_NO='',IPD_NO='',BED_STATUS='0',&
			OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
			WHERE CASE_NO=<CASE_NO> AND BED_OCCU_FLG='Y'
clearOCCUBed.Debug=N

//添加操作===pangben 2014-8-27
insertComorderReplace.Type=TSQL
insertComorderReplace.SQL=INSERT INTO SYS_COMORDER_REPLACE(ORDER_CODE, SEQ, MEDI_QTY, &
				MEDI_UNIT, ROUTE_CODE, ORDER_DESC, &
				REGION_CODE, ORDER_CODE_OLD, ORDER_DESC_OLD, &
				MEDI_QTY_OLD, MEDI_UNIT_OLD, ROUTE_CODE_OLD, &
				UPDATE_FLG, OPT_USER, OPT_DATE, &
				OPT_TERM, PACK_CODE) VALUES(<ORDER_CODE>, <SEQ>, <MEDI_QTY>, &
				<MEDI_UNIT>, <ROUTE_CODE>, <ORDER_DESC>, &
				<REGION_CODE>, <ORDER_CODE_OLD>, <ORDER_DESC_OLD>, &
				<MEDI_QTY_OLD>, <MEDI_UNIT_OLD>, <ROUTE_CODE_OLD>, &
				'N', <OPT_USER>, SYSDATE, &
				<OPT_TERM>, <PACK_CODE>)
insertComorderReplace.Debug=N

//修改操作===pangben 2014-8-27
updateComorderReplace.Type=TSQL
updateComorderReplace.SQL=UPDATE SYS_COMORDER_REPLACE SET MEDI_QTY=<MEDI_QTY>, &
				MEDI_UNIT=<MEDI_UNIT>, ROUTE_CODE=<ROUTE_CODE>, ORDER_DESC=<ORDER_DESC>, &
				REGION_CODE=<REGION_CODE>, ORDER_CODE_OLD=<ORDER_CODE_OLD>, ORDER_DESC_OLD=<ORDER_DESC_OLD>, &
				MEDI_QTY_OLD=<MEDI_QTY_OLD>, MEDI_UNIT_OLD=<MEDI_UNIT_OLD>, ROUTE_CODE_OLD=<ROUTE_CODE_OLD>, &
				OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, &
				OPT_TERM=<OPT_TERM> WHERE ORDER_CODE=<ORDER_CODE> AND  SEQ=<SEQ>
updateComorderReplace.Debug=N

deleteComorderReplace.Type=TSQL
deleteComorderReplace.SQL=DELETE FROM SYS_COMORDER_REPLACE WHERE ORDER_CODE=<ORDER_CODE> AND  SEQ=<SEQ>
deleteComorderReplace.Debug=N