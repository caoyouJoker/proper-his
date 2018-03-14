# 
#  Title:开关帐主档module
# 
#  Description:开关帐主档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.05.07
#  version 1.0
#
Module.item=selectAllData;updataData;insertData;CheckCounter;finishData
//;selectData;insertData;deleteData

//查询 收据类型，起始票号，结束票号，开账时间，交回状态，领用人员，操作人员，操作日期
//条件：票据类型，领用日期，开账日期，使用状态，关帐日期不存在
selectAllData.Type=TSQL
selectAllData.SQL=SELECT CASHIER_CODE,RECP_TYPE,START_INVNO, &
                         OPEN_DATE,END_INVNO,CLS_DATE,OPT_USER, &
                         OPT_DATE,OPT_TERM FROM BIL_COUNTER &
                         WHERE CASHIER_CODE=<CASHIER_CODE> &
                         AND RECP_TYPE=<RECP_TYPE> &
                         AND CLS_DATE IS NULL
selectAllData.Debug=N


//开账插入新数据<CASHIER_CODE>,<RECP_TYPE>,<OPEN_DATE>,<START_INVNO>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>
insertData.Type=TSQL
insertData.SQL=INSERT INTO BIL_COUNTER (CASHIER_CODE,RECP_TYPE, &
                      OPEN_DATE,START_INVNO,OPT_USER,OPT_DATE, OPT_TERM) &
                      VALUES (<CASHIER_CODE>,<RECP_TYPE>, &
                      <OPEN_DATE>,<START_INVNO>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertData.Debug=N

//关帐更新数据//插入操作人员，操作日期，操作终端，关帐时间，截止票号
updataData.Type=TSQL
updataData.SQL=UPDATE BIL_COUNTER SET OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, &
                      OPT_TERM=<OPT_TERM>,CLS_DATE=<CLS_DATE>,END_INVNO=<END_INVNO> &
                      WHERE RECP_TYPE=<RECP_TYPE> &
                      AND CASHIER_CODE=<CASHIER_CODE> &
                      AND CLS_DATE IS NULL
updataData.Debug=N


//检核开关账
CheckCounter.Type=TSQL
CheckCounter.SQL=SELECT CASHIER_CODE &
                        FROM BIL_COUNTER &
                        WHERE CASHIER_CODE=<CASHIER_CODE> &
                        AND RECP_TYPE=<RECP_TYPE> &
                        AND CLS_DATE IS NULL   
CheckCounter.Debug=N

//检核使用重的票据
finishData.Type=TSQL
finishData.SQL=SELECT CASHIER_CODE &
                        FROM BIL_COUNTER &
                        WHERE RECP_TYPE=<RECP_TYPE> &
                        AND OPT_TERM=<OPT_TERM> &
                        AND START_INVNO=<START_INVNO> &
                        AND CLS_DATE IS NULL   
finishData.Debug=N
