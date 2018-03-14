#
# <p>Title:临床路径医嘱替换操作 </p>
#
# <p>Description: </p>
#
# <p>Copyright: Copyright (c) 2014</p>
#
# <p>Company: </p>
#
# @author pangb 2014-8-28
# @version 4.0
#
Module.item=selectClpPack;selectComorderReplace;selectClpPackSum;updateClpPack;updateClpPackVersion;&
            updateClpPackHistoryVersion;insertClpPackHistory;updateClpPackHistory;updateClpBscinfoVersion

//查询替换医嘱表
selectComorderReplace.Type=TSQL
selectComorderReplace.SQL=SELECT A.ORDER_CODE,A.MEDI_QTY,A.MEDI_UNIT,A.ROUTE_CODE,A.SEQ,&
         	          A.ORDER_DESC,A.REGION_CODE,A.ORDER_CODE_OLD,B.OWN_PRICE,&
          	          A.ORDER_DESC_OLD,A.MEDI_QTY_OLD,A.MEDI_UNIT_OLD,A.ROUTE_CODE_OLD,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.PACK_CODE &
	                FROM SYS_COMORDER_REPLACE A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE
selectComorderReplace.item=ORDER_CODE;PACK_CODE;ORDER_CODE_OLD
selectComorderReplace.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
selectComorderReplace.PACK_CODE=PACK_CODE=<PACK_CODE>
selectComorderReplace.ORDER_CODE_OLD=ORDER_CODE_OLD=<ORDER_CODE_OLD>
selectComorderReplace.Debug=N

//查询临床路径医嘱字典表
selectClpPack.Type=TSQL
selectClpPack.SQL=SELECT A.CLNCPATH_CODE,A. SCHD_CODE,A. ORDER_TYPE,&
			A.ORDER_CODE,A. CHKTYPE_CODE,A. ORDER_SEQ_NO,&
			A.VERSION,A. REGION_CODE,A. DOSE,&
			A.DOSE_UNIT,A. FREQ_CODE,A. ROUT_CODE,&
			A.DOSE_DAYS,A. NOTE,A. ORDER_FLG,&
			A.SEQ,A. RBORDER_DEPT_CODE,A. URGENT_FLG,&
			A.CHKUSER_CODE,A. EXEC_FLG,A. ORDTYPE_CODE,&
			A.STANDARD,A. START_DAY,A. OWN_PRICE,&
			B.CLP_STATUS,A.VERSION &
	   	  FROM CLP_PACK A,CLP_BSCINFO B WHERE A.CLNCPATH_CODE=B.CLNCPATH_CODE  ORDER BY A.CLNCPATH_CODE, A.SCHD_CODE
selectClpPack.item=ORDER_CODE;CLNCPATH_CODE;SCHD_CODE
selectClpPack.ORDER_CODE=A.ORDER_CODE=<ORDER_CODE>
selectClpPack.CLNCPATH_CODE=A.CLNCPATH_CODE=<CLNCPATH_CODE>
selectClpPack.SCHD_CODE=A.SCHD_CODE=<SCHD_CODE>
selectClpPack.Debug=N
 
//查询临床路径项目所有的医嘱
selectClpPackSum.Type=TSQL
selectClpPackSum.SQL=SELECT CLNCPATH_CODE, SCHD_CODE, ORDER_TYPE, &
			ORDER_CODE, CHKTYPE_CODE, ORDER_SEQ_NO, &
			VERSION, REGION_CODE, DOSE, &
			DOSE_UNIT, FREQ_CODE, ROUT_CODE, &
			DOSE_DAYS, NOTE, ORDER_FLG, &
			SEQ, RBORDER_DEPT_CODE, URGENT_FLG, &
			CHKUSER_CODE, EXEC_FLG, ORDTYPE_CODE, &
			STANDARD, OPT_USER, OPT_DATE, &
			OPT_TERM, START_DAY, OWN_PRICE &
		     FROM CLP_PACK
selectClpPackSum.item=ORDER_CODE;CLNCPATH_CODE;SCHD_CODE	
selectClpPackSum.ORDER_CODE=ORDER_CODE=<ORDER_CODE>
selectClpPackSum.CLNCPATH_CODE=CLNCPATH_CODE=<CLNCPATH_CODE>
selectClpPackSum.SCHD_CODE=SCHD_CODE=<SCHD_CODE>
selectClpPackSum.Debug=N   

//修改医嘱
updateClpPack.Type=TSQL
updateClpPack.SQL=UPDATE CLP_PACK SET ORDER_CODE=<ORDER_CODE>,DOSE=<MEDI_QTY>,&
                                  DOSE_UNIT=<UNIT_CODE>,ROUT_CODE=<ROUTE_CODE> &
                       WHERE CLNCPATH_CODE=<CLNCPATH_CODE> AND SCHD_CODE=<SCHD_CODE> &
                         AND ORDER_CODE=<ORDER_CODE_OLD> AND ORDER_TYPE=<ORDER_TYPE> &
                         AND CHKTYPE_CODE=<CHKTYPE_CODE> AND ORDER_SEQ_NO=<ORDER_SEQ_NO>
updateClpPack.Debug=N

//修改历史数据医嘱
updateClpPackHistory.Type=TSQL
updateClpPackHistory.SQL=UPDATE CLP_PACK_HISTORY SET END_DATE=<END_DATE>,VERSION=<VERSION_NEW> &
                       WHERE CLNCPATH_CODE=<CLNCPATH_CODE> AND SCHD_CODE=<SCHD_CODE> &
                         AND ORDER_CODE=<ORDER_CODE_OLD> AND ORDER_TYPE=<ORDER_TYPE> &
                         AND CHKTYPE_CODE=<CHKTYPE_CODE> AND ORDER_SEQ_NO=<ORDER_SEQ_NO> AND VERSION=<VERSION>
updateClpPackHistory.Debug=N

//修改历史版本号码
updateClpPackHistoryVersion.Type=TSQL
updateClpPackHistoryVersion.SQL=UPDATE CLP_PACK_HISTORY SET VERSION=<VERSION>,OPT_USER=<OPT_USER>,&
                                  OPT_TERM=<OPT_TERM>,OPT_DATE=SYSDATE &
                       WHERE CLNCPATH_CODE=<CLNCPATH_CODE> AND VERSION=<VERSION_NEW>
updateClpPackHistoryVersion.Debug=N

//添加历史医嘱代码
insertClpPackHistory.Type=TSQL
insertClpPackHistory.SQL=INSERT INTO CLP_PACK_HISTORY(CLNCPATH_CODE, SCHD_CODE, ORDER_TYPE, &
			ORDER_CODE, CHKTYPE_CODE, ORDER_SEQ_NO, &
			VERSION, REGION_CODE, DOSE, &
			DOSE_UNIT, FREQ_CODE, ROUT_CODE, &
			DOSE_DAYS, NOTE, ORDER_FLG, &
			SEQ, RBORDER_DEPT_CODE, URGENT_FLG, &
			CHKUSER_CODE, EXEC_FLG, ORDTYPE_CODE, &
			STANDARD, OPT_USER, OPT_DATE, &
			OPT_TERM, START_DAY, OWN_PRICE, &
			START_DATE, END_DATE)VALUES(<CLNCPATH_CODE>, <SCHD_CODE>, <ORDER_TYPE>,& 
			<ORDER_CODE>, <CHKTYPE_CODE>, <ORDER_SEQ_NO>, &
			<VERSION>, <REGION_CODE>, <DOSE>, &
			<DOSE_UNIT>, <FREQ_CODE>, <ROUT_CODE>, &
			<DOSE_DAYS>, <NOTE>, <ORDER_FLG>, &
			<SEQ>, <RBORDER_DEPT_CODE>, <URGENT_FLG>, &
			<CHKUSER_CODE>, <EXEC_FLG>, <ORDTYPE_CODE>, &
			<STANDARD>, <OPT_USER>, SYSDATE, &
			<OPT_TERM>, <START_DAY>, <OWN_PRICE>, &
			<START_DATE>, <END_DATE>)
insertClpPackHistory.Debug=N

//修改版本号码
updateClpPackVersion.Type=TSQL
updateClpPackVersion.SQL=UPDATE CLP_PACK SET VERSION=<VERSION>,OPT_USER=<OPT_USER>,&
                                  OPT_TERM=<OPT_TERM>,OPT_DATE=SYSDATE &
                       WHERE CLNCPATH_CODE=<CLNCPATH_CODE> 
updateClpPackVersion.Debug=N

//修改临床路径版本号码
updateClpBscinfoVersion.Type=TSQL
updateClpBscinfoVersion.SQL=UPDATE CLP_BSCINFO SET VERSION=<VERSION>,OPT_USER=<OPT_USER>,&
                                  OPT_TERM=<OPT_TERM>,OPT_DATE=SYSDATE &
                       WHERE CLNCPATH_CODE=<CLNCPATH_CODE> 
updateClpBscinfoVersion.Debug=N