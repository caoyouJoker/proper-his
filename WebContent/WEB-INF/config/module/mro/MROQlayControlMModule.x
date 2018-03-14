  #
   # Title:质控记录主档
   #
   # Description:质控记录主档
   #
   # Copyright: JavaHis (c) 2009
   #
   # @author zhangy 2011/05/05
Module.item=query;insert;update;queryQlayControlScore;queryQlayControlSUM;delete

//查询
query.Type=TSQL
query.SQL=SELECT CASE_NO,EXAMINE_CODE,EXAMINE_DATE,MR_NO,IPD_NO, &
		STATUS,CHECK_RANGE,CHECK_USER,CHECK_DATE, &
	     	OPT_USER,OPT_DATE,OPT_TERM &
	     	FROM MRO_QLAYCONTROLM
query.item=CASE_NO;EXAMINE_CODE
query.CASE_NO=CASE_NO=<CASE_NO>
query.EXAMINE_CODE=EXAMINE_CODE=<EXAMINE_CODE>
query.Debug=N

//新增
//=================pangben modify 20110801 添加状态列：已查\未查
insert.Type=TSQL
insert.SQL = INSERT INTO MRO_QLAYCONTROLM( &
		CASE_NO,EXAMINE_CODE,EXAMINE_DATE,MR_NO,IPD_NO, &
		STATUS,CHECK_RANGE,CHECK_USER,CHECK_DATE, &
	     	OPT_USER,OPT_DATE,OPT_TERM,QUERYSTATUS) &
	     VALUES(<CASE_NO>,<EXAMINE_CODE>,<EXAMINE_DATE>,<MR_NO>,<IPD_NO>,&
	     	<STATUS>,<CHECK_RANGE>,<CHECK_USER>,SYSDATE,&
	     	<OPT_USER>,SYSDATE,<OPT_TERM>,<QUERYSTATUS>)
insert.Debug=N
//更新
//=================pangben modify 20110801 添加状态列：已查\未查
update.Type=TSQL
update.SQL = UPDATE MRO_QLAYCONTROLM SET &
		STATUS=<STATUS> , CHECK_USER=<CHECK_USER> , CHECK_DATE=SYSDATE , QUERYSTATUS=<QUERYSTATUS>, &
		OPT_USER=<OPT_USER> , OPT_DATE=SYSDATE , OPT_TERM=<OPT_TERM> &
	     WHERE CASE_NO=<CASE_NO> AND EXAMINE_CODE=<EXAMINE_CODE>
update.Debug=N

//删除 add by wanglong 20130819
delete.Type=TSQL
delete.SQL=DELETE FROM MRO_QLAYCONTROLM WHERE CASE_NO = <CASE_NO> AND EXAMINE_CODE = <EXAMINE_CODE>
delete.Debug=N

//查询
queryQlayControlScore.Type=TSQL
//modify by wanglong 20121127
queryQlayControlScore.SQL=SELECT DISTINCT 'N' AS FLG, C.DEPT_CODE, C.STATION_CODE, C.VS_DR_CODE, B.PAT_NAME, A.MR_NO, A.CASE_NO,&
                                          A.IPD_NO, B.SEX_CODE, B.BIRTH_DATE, C.IN_DATE, C.DS_DATE,&
                                          '' AS STATUSTYPE, A.TYPERESULT, A.SUMSCODE,C.ADM_DATE &
                            FROM MRO_RECORD A, SYS_PATINFO B, ADM_INP C &
                           WHERE A.MR_NO = B.MR_NO(+) AND A.CASE_NO = C.CASE_NO &
                        ORDER BY C.DEPT_CODE, C.STATION_CODE, A.CASE_NO
queryQlayControlScore.item=REGION_CODE;DEPT_CODE;STATION_CODE;VS_DR_CODE;MR_NO;IPD_NO;TYPE_IN;TYPE_OUT;TYPERESULT
queryQlayControlScore.REGION_CODE=C.REGION_CODE=<REGION_CODE>
queryQlayControlScore.DEPT_CODE=C.DEPT_CODE=<DEPT_CODE>
queryQlayControlScore.STATION_CODE=C.STATION_CODE=<STATION_CODE>
queryQlayControlScore.VS_DR_CODE=C.VS_DR_CODE=<VS_DR_CODE>
queryQlayControlScore.MR_NO=A.MR_NO=<MR_NO>
queryQlayControlScore.IPD_NO=A.IPD_NO=<IPD_NO>
queryQlayControlScore.TYPE_IN=C.IN_DATE IS NOT NULL AND (C.DS_DATE IS NULL OR C.DS_DATE > SYSDATE)
queryQlayControlScore.TYPE_OUT=C.DS_DATE IS NOT NULL AND C.DS_DATE BETWEEN <START_DATE> AND <END_DATE>
queryQlayControlScore.TYPERESULT=A.TYPERESULT=<TYPERESULT>
queryQlayControlScore.Debug=N

//查询2------根据条件显示所有在院出院的病患信息
queryQlayControlSUM.Type=TSQL
queryQlayControlSUM.SQL=SELECT  A.MR_NO, A.CASE_NO, A.IPD_NO, &
         		      A.TYPERESULT,A.SUMSCODE &
    			      FROM MRO_RECORD A,ADM_INP B & 
    			      WHERE A.MR_NO = B.MR_NO &
			     ORDER BY A.CASE_NO
queryQlayControlSUM.item=REGION_CODE;DEPT_CODE;STATION_CODE;USER_ID;MR_NO;IPD_NO;TYPE_IN;TYPE_OUT;CASE_NO
queryQlayControlSUM.REGION_CODE=A.REGION_CODE=<REGION_CODE>
queryQlayControlSUM.DEPT_CODE=B.DEPT_CODE=<DEPT_CODE>
queryQlayControlSUM.STATION_CODE=B.STATION_CODE=<STATION_CODE>
queryQlayControlSUM.USER_ID=B.VS_DR_CODE=<USER_ID>
queryQlayControlSUM.MR_NO=A.MR_NO=<MR_NO>
queryQlayControlSUM.CASE_NO=A.CASE_NO=<CASE_NO>
queryQlayControlSUM.IPD_NO=A.IPD_NO=<IPD_NO>
queryQlayControlSUM.TYPE_IN=B.IN_DATE IS NOT NULL AND (B.DS_DATE IS NULL OR B.DS_DATE > SYSDATE)
queryQlayControlSUM.TYPE_OUT=B.DS_DATE IS NOT NULL AND B.DS_DATE BETWEEN TO_DATE(<START_DATE>, 'YYYYMMDDHH24MISS') AND TO_DATE(<END_DATE>, 'YYYYMMDDHH24MISS')
queryQlayControlSUM.Debug=N