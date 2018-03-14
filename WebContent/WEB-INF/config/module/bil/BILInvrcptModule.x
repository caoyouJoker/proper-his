# 
#  Title:票据明细档module
# 
#  Description:票据明细档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.05.07
#  version 1.0
#
Module.item=selectData;updataData;insertData;selectByInvNo;getOneInv;account;accountAll;getInvalidCount;getInvalidCountAll;getInvrcpCount;getInvrcpCountAll;getPrintNo;getBackPrintNo;getTearPrintNo

//查询 RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,AR_AMT,CANCEL_FLG,CANCEL_USER,CANCEL_DATE,OPT_USER,OPT_DATEOPT_TERMT 条件RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,CANCEL_FLG
selectData.Type=TSQL
selectData.SQL=SELECT RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,AR_AMT,CANCEL_FLG,&
               CANCEL_USER,CANCEL_DATE,OPT_USER,OPT_DATE,OPT_TERM,ADM_TYPE,STATUS &
               FROM BIL_INVRCP  &
               ORDER BY INV_NO
selectData.item=RECP_TYPE;INV_NO;RECEIPT_NO;CASHIER_CODE;CANCEL_FLG;STATUS
selectData.RECP_TYPE=RECP_TYPE=<RECP_TYPE>
selectData.INV_NO=INV_NO=<INV_NO>
selectData.RECEIPT_NO=RECEIPT_NO=<RECEIPT_NO>
selectData.CASHIER_CODE=CASHIER_CODE=<CASHIER_CODE>
selectData.CANCEL_FLG=CANCEL_FLG=<CANCEL_FLG>
selectData.STATUS=STATUS=<STATUS>
selectData.Debug=N


//打印票据插入打印细项
insertData.Type=TSQL
insertData.SQL=INSERT INTO BIL_INVRCP &
                           (RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,AR_AMT,&
                           CANCEL_FLG,OPT_USER,OPT_DATE,OPT_TERM,PRINT_USER,&
                           PRINT_DATE,ADM_TYPE,STATUS) &
                  VALUES   (<RECP_TYPE>,<INV_NO>,<RECEIPT_NO>,<CASHIER_CODE>,<AR_AMT>,&
                  	   <CANCEL_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>,<PRINT_USER>,SYSDATE,<ADM_TYPE>,<STATUS>)
insertData.Debug=N

//作废票据更新
updataData.Type=TSQL
updataData.SQL=UPDATE BIL_INVRCP &
		  SET CANCEL_FLG=<CANCEL_FLG>,CANCEL_USER=<CANCEL_USER>,CANCEL_DATE=SYSDATE,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		WHERE RECP_TYPE=<RECP_TYPE> &
		  AND INV_NO=<INV_NO> 
updataData.Debug=N

//查询 RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,AR_AMT,CANCEL_FLG,CANCEL_USER,CANCEL_DATE,OPT_USER,OPT_DATEOPT_TERMT 
//条件RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,CANCEL_FLG
//条件:票号，票据类型；操作员
selectByInvNo.Type=TSQL
selectByInvNo.SQL=SELECT RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,AR_AMT,CASE WHEN CANCEL_FLG = '1' THEN '1' WHEN CANCEL_FLG = '3' THEN '3' &
			 WHEN STATUS = '2' THEN '2' WHEN STATUS = '0' THEN '0' END AS CANCEL_FLG, &
			 CANCEL_USER,TO_CHAR (CANCEL_DATE, 'YYYY/MM/DD HH24:MI:SS') AS CANCEL_DATE,OPT_USER,&
			 OPT_DATE,OPT_TERM,TO_CHAR (PRINT_DATE, 'YYYY/MM/DD HH24:MI:SS') AS PRINT_DATE &
		    FROM BIL_INVRCP &
		   WHERE RECP_TYPE=<RECP_TYPE> &
		     AND INV_NO BETWEEN <START_INVNO> AND <END_INVNO> &
		ORDER BY INV_NO
selectByInvNo.item=CASHIER_CODE
selectByInvNo.CASHIER_CODE=CASHIER_CODE=<CASHIER_CODE>
selectByInvNo.Debug=N

//得到一张已经打印的票据
getOneInv.Type=TSQL
getOneInv.SQL=SELECT RECP_TYPE,INV_NO,RECEIPT_NO,CASHIER_CODE,AR_AMT,CANCEL_FLG, &
                     CANCEL_USER,CANCEL_DATE,STATUS,OPT_USER,OPT_DATE,OPT_TERM &
                FROM BIL_INVRCP &
               WHERE RECP_TYPE=<RECP_TYPE> &
                     AND INV_NO=<INV_NO>
getOneInv.Debug=N


//日结
account.Type=TSQL
account.SQL=UPDATE BIL_INVRCP SET &
		   ACCOUNT_SEQ=<ACCOUNT_SEQ>,ACCOUNT_USER=<ACCOUNT_USER>,ACCOUNT_DATE=TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS'),ACCOUNT_FLG='Y' &
	     WHERE RECP_TYPE=<RECP_TYPE> &
	       AND CASHIER_CODE=<CASHIER_CODE> &
	       AND ADM_TYPE = <ADM_TYPE> &
	       AND PRINT_DATE<TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS') &
	       AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL)
account.Debug=N

//日结o e h
accountAll.Type=TSQL
accountAll.SQL=UPDATE BIL_INVRCP SET &
		   ACCOUNT_SEQ=<ACCOUNT_SEQ>,ACCOUNT_USER=<ACCOUNT_USER>,ACCOUNT_DATE=TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS'),ACCOUNT_FLG='Y' &
	     WHERE RECP_TYPE=<RECP_TYPE> &
	       AND CASHIER_CODE=<CASHIER_CODE> &
	       AND PRINT_DATE<TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS') &
	       AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL)
accountAll.Debug=N

//得到日结人员作废张数
getInvalidCount.Type=TSQL
getInvalidCount.SQL=SELECT COUNT(INV_NO) AS COUNT FROM BIL_INVRCP &
		     WHERE RECP_TYPE=<RECP_TYPE> &
                           AND CASHIER_CODE=<CASHIER_CODE> &
                           AND ADM_TYPE = <ADM_TYPE> &
                           AND PRINT_DATE<TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS') &
                           AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) &
                           AND (CANCEL_FLG ='3' OR STATUS = '2')
getInvalidCount.Debug=N

//得到日结人员作废张数(O,E,H)
getInvalidCountAll.Type=TSQL
getInvalidCountAll.SQL=SELECT COUNT(INV_NO) AS COUNT FROM BIL_INVRCP &
		     WHERE RECP_TYPE=<RECP_TYPE> &
                           AND CASHIER_CODE=<CASHIER_CODE> &
                           AND ADM_TYPE IN ('O','E','H') &
                           AND PRINT_DATE<TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS') &
                           AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) &
                           AND (CANCEL_FLG ='3' OR STATUS = '2')
getInvalidCountAll.Debug=N


//得到日结票据张数
getInvrcpCount.Type=TSQL
getInvrcpCount.SQL=SELECT COUNT(INV_NO) COUNT FROM BIL_INVRCP &
		     WHERE RECP_TYPE=<RECP_TYPE> &
		     			   AND ADM_TYPE = <ADM_TYPE> &
                           AND CASHIER_CODE=<CASHIER_CODE> &
                           AND PRINT_DATE<TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS') &
                           AND (ACCOUNT_FLG IS NULL OR ACCOUNT_FLG = 'N')
getInvrcpCount.Debug=N

//得到日结票据张数(O,E,H)
getInvrcpCountAll.Type=TSQL
getInvrcpCountAll.SQL=SELECT COUNT(INV_NO) COUNT FROM BIL_INVRCP &
		     WHERE RECP_TYPE=<RECP_TYPE> &
		     			   AND ADM_TYPE IN ('O','E','H') &
                           AND CASHIER_CODE=<CASHIER_CODE> &
                           AND PRINT_DATE<TO_DATE(<PRINT_DATE>,'YYYYMMDDHH24MISS') &
                           AND (ACCOUNT_FLG IS NULL OR ACCOUNT_FLG = 'N')
getInvrcpCountAll.Debug=N


//得到日结票据号
getPrintNo.Type=TSQL
getPrintNo.SQL=SELECT INV_NO COUNT FROM BIL_INVRCP &
		     WHERE ACCOUNT_SEQ IN(<ACCOUNT_SEQ>)
getPrintNo.Debug=N

//得到日结作废票号
getBackPrintNo.Type=TSQL
getBackPrintNo.SQL=SELECT INV_NO,AR_AMT FROM BIL_INVRCP &
		     WHERE ACCOUNT_SEQ IN(<ACCOUNT_SEQ>) &
		           AND STATUS = '2'
getBackPrintNo.Debug=N

//得到日结退费票号
getTearPrintNo.Type=TSQL
getTearPrintNo.SQL=SELECT INV_NO,AR_AMT FROM BIL_INVRCP &
		     WHERE ACCOUNT_SEQ IN(<ACCOUNT_SEQ>) &
		           AND CANCEL_FLG IN('1')
getTearPrintNo.Debug=N