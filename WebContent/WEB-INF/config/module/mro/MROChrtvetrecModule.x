#############################################
#  Title:病案审核module
# 
#  Description:病案审核module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.4.30
#  version 1.0
#############################################
Module.item=selectdata;insertdata;updatedata;deletedata;selectAllChr;selectChrData;updateEMail;updateBoard;selectSumScode;selectSumScode1

//查询审核信息
selectChrData.Type=TSQL
selectChrData.SQL=SELECT CASE_NO, EXAMINE_CODE, EXAMINE_DATE, IPD_NO, MR_NO, &
		         VS_CODE, DEDUCT_SCORE, DEDUCT_NOTE, URG_FLG, REPLY_DTTM, &
		         REPLY_DR_CODE, REPLY_REMK, OPT_USER, OPT_DATE, OPT_TERM &
		    FROM MRO_CHRTVETREC
selectChrData.item=CASE_NO;EXAMINE_CODE;EXAMINE_DATE
selectChrData.CASE_NO=CASE_NO=<CASE_NO>
selectChrData.EXAMINE_CODE=EXAMINE_CODE=<EXAMINE_CODE>
selectChrData.EXAMINE_DATE=EXAMINE_DATE=<EXAMINE_DATE>
selectChrData.Debug=N

//分组查询所有就诊患者的MR_NO,IPD_NO,CASE_NO
//==============pangben 20110830
selectSumScode.Type=TSQL
selectSumScode.SQL=SELECT CASE_NO, IPD_NO, MR_NO &
		     FROM MRO_CHRTVETREC &
		 GROUP BY CASE_NO,IPD_NO,MR_NO
selectSumScode.item=CASE_NO;IPD_NO;MR_NO
selectSumScode.CASE_NO=CASE_NO=<CASE_NO>
selectSumScode.IPD_NO=IPD_NO=<IPD_NO>
selectSumScode.MR_NO=MR_NO=<MR_NO>
selectSumScode.Debug=N

//分组查询就诊患者总分值:如果存在数据将显示分值不存在不显示
//==============modify by wanglong 20121107
selectSumScode1.Type=TSQL
selectSumScode1.SQL=WITH CATEGORY AS ( &
                                       SELECT CATEGORY_CODE, TO_NUMBER (DESCRIPTION) AS DESCRIPTION &
                                         FROM SYS_CATEGORY &
                                        WHERE RULE_TYPE = 'MRO_CHRTVETSTD' &
                                          AND LENGTH (CATEGORY_CODE) = 3 &
                                          AND (CATEGORY_CODE LIKE '21%' OR CATEGORY_CODE LIKE '22%') &
                                     ), &
                       CHRTVETREC AS ( &
		                       SELECT CASE_NO, MR_NO, IPD_NO, CATEGORY_CODE, SUM (DEDUCT_SCORE) AS SUB_DEDUCT_SCORE &
                                         FROM ( &
				                SELECT B.CASE_NO, B.MR_NO, B.IPD_NO, SUBSTR (A.EXAMINE_CODE, 0, 3) AS CATEGORY_CODE, &
                                                       A.DEDUCT_SCORE * A.DEDUCT_SCORECOUNT AS DEDUCT_SCORE &
                                                  FROM MRO_CHRTVETREC A, MRO_QLAYCONTROLM B &
                                                 WHERE A.CASE_NO = B.CASE_NO &
                                                   AND A.EXAMINE_CODE = B.EXAMINE_CODE &
                                                   AND B.QUERYSTATUS = '1' &
                                                   AND B.STATUS = '0' &
					       ) &
                                     GROUP BY CASE_NO, MR_NO, IPD_NO, CATEGORY_CODE &
				     ) &
                              SELECT B.CASE_NO, MR_NO, IPD_NO, 100 - SUM (LEAST (A.DESCRIPTION, B.SUB_DEDUCT_SCORE)) AS SCODE &
                                FROM CATEGORY A, CHRTVETREC B &
                               WHERE A.CATEGORY_CODE = B.CATEGORY_CODE &
                            GROUP BY B.CASE_NO, MR_NO, IPD_NO
selectSumScode1.item=CASE_NO;IPD_NO;MR_NO
selectSumScode1.CASE_NO=CASE_NO=<CASE_NO>
selectSumScode1.IPD_NO=IPD_NO=<IPD_NO>
selectSumScode1.MR_NO=MR_NO=<MR_NO>
selectSumScode1.Debug=N

//查询数据
selectdata.Type=TSQL
selectdata.SQL=SELECT A.IPD_NO,A.MR_NO,A.BED_NO,A.IN_DATE,A.DS_DATE, &
		      B.PAT_NAME,B.SEX,A.VS_DR_CODE &
		      //add by wanglong 20130819
		      ,A.ADM_DATE,A.DEPT_CODE,A.STATION_CODE,B.TYPERESULT &
		 FROM ADM_INP A,MRO_RECORD B &
		WHERE A.CASE_NO = B.CASE_NO
selectdata.item=CASE_NO
selectdata.CASE_NO=A.CASE_NO = <CASE_NO> 
selectdata.Debug=N

//查询一个患者的审核信息 modify by wanglong 20130909
selectAllChr.Type=TSQL
selectAllChr.SQL=SELECT '' FLG,A.EXAMINE_CODE,A.EXAMINE_DATE,A.VS_CODE,A.DEDUCT_SCORE,A.DEDUCT_NOTE, &
			A.URG_FLG,A.REPLY_DTTM,A.REPLY_DR_CODE,A.REPLY_REMK,A.OPT_USER, &
			A.OPT_DATE,A.OPT_TERM,B.TYPE_CODE,TO_DATE(A.EXAMINE_DATE,'YYYYMMDDHH24MISS') AS EXA_DATE &
		   FROM MRO_CHRTVETREC A,MRO_CHRTVETSTD B &
		  WHERE A.EXAMINE_CODE=B.EXAMINE_CODE &
	       ORDER BY EXAMINE_DATE
selectAllChr.ITEM=CASE_NO
selectAllChr.CASE_NO=A.CASE_NO=<CASE_NO> 
selectAllChr.Debug=N

//插入数据
//=================pangben modify 20110801 添加分数累计个数列 
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO MRO_CHRTVETREC ( &
                        CASE_NO,EXAMINE_CODE,EXAMINE_DATE,IPD_NO,MR_NO, &
                        VS_CODE,DEDUCT_SCORE,DEDUCT_NOTE,URG_FLG,REPLY_DTTM, &
                        REPLY_DR_CODE,REPLY_REMK,OPT_USER,OPT_DATE,OPT_TERM,DEDUCT_SCORECOUNT &
                        ) &
                 VALUES ( &
                        <CASE_NO>,<EXAMINE_CODE>,<EXAMINE_DATE>,<IPD_NO>,<MR_NO>, &
                        <VS_CODE>,<DEDUCT_SCORE>,<DEDUCT_NOTE>,<URG_FLG>,<REPLY_DTTM>, &
                        <REPLY_DR_CODE>,<REPLY_REMK>,<OPT_USER>,SYSDATE,<OPT_TERM>,<DEDUCT_SCORECOUNT> &
                        ) 
insertdata.Debug=N

//更新数据
//=================pangben modify 20110801 添加分数累计个数列 
updatedata.Type=TSQL
updatedata.SQL=UPDATE MRO_CHRTVETREC SET VS_CODE = <VS_CODE>, &
                                         DEDUCT_SCORE = <DEDUCT_SCORE>, &
                                         DEDUCT_NOTE = <DEDUCT_NOTE>, &
                                         URG_FLG = <URG_FLG>, &
					 EXAMINE_CODE = <EXAMINE_CODE>, &
			                 EXAMINE_DATE = <EXAMINE_DATE>, &
			                 REPLY_DTTM = <REPLY_DTTM>, &
			                 REPLY_DR_CODE = <REPLY_DR_CODE>, &
			                 REPLY_REMK = <REPLY_REMK>, &
					 DEDUCT_SCORECOUNT = <DEDUCT_SCORECOUNT>, &
			                 OPT_USER = <OPT_USER>, &
			                 OPT_DATE = SYSDATE, &
			                 OPT_TERM = <OPT_TERM> &
                WHERE CASE_NO = <CASE_NO> &
                  AND EXAMINE_DATE = <OLD_EXAMINE_DATE> &
                  AND EXAMINE_CODE = <OLD_EXAMINE_CODE>
updatedata.Debug=N

//删除数据
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM MRO_CHRTVETREC &
		WHERE CASE_NO = <CASE_NO> &
                  AND EXAMINE_DATE = <EXAMINE_DATE> &
                  AND EXAMINE_CODE = <EXAMINE_CODE>  
deletedata.Debug=N


//更新邮件状态
updateEMail.Type=TSQL
updateEMail.SQL=UPDATE MRO_CHRTVETREC SET EMAIL_STATUS = <EMAIL_STATUS>, &
			                  OPT_USER = <OPT_USER>, &
			                  OPT_DATE = SYSDATE, &
			                  OPT_TERM = <OPT_TERM> &
                 WHERE CASE_NO = <CASE_NO> &
                   AND EXAMINE_DATE = <EXAMINE_DATE> &
                   AND EXAMINE_CODE = <EXAMINE_CODE>
updateEMail.Debug=N


//更新公布栏状态
updateBoard.Type=TSQL
updateBoard.SQL=UPDATE MRO_CHRTVETREC SET BOARD_STATUS = <BOARD_STATUS>, &
			                  OPT_USER = <OPT_USER>, &
			                  OPT_DATE = SYSDATE, &
			                  OPT_TERM = <OPT_TERM> &
                 WHERE CASE_NO = <CASE_NO> &
                   AND EXAMINE_DATE = <EXAMINE_DATE> &
                   AND EXAMINE_CODE = <EXAMINE_CODE>
updateBoard.Debug=N