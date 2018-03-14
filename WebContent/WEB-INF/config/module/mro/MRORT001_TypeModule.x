###############################
# <p>Title:问题类型统计Tool </p>
#
# <p>Description:问题类型统计Tool </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:Javahis </p>
#
# @author zhangk  2009-9-13
# @version 4.0
#
###############################
Module.item=select

//查询报表信息 modify by wanlgong 20130909
select.Type=TSQL
select.SQL=SELECT C.REGION_CODE, C.DEPT_CODE, C.STATION_CODE, B.TYPE_CODE, A.EXAMINE_CODE, COUNT(A.EXAMINE_CODE) AS EXAMINE_COUNT &
             FROM MRO_CHRTVETREC A, MRO_CHRTVETSTD B, ADM_INP C, MRO_QLAYCONTROLM D &
            WHERE A.EXAMINE_CODE = B.EXAMINE_CODE &
              AND A.CASE_NO = C.CASE_NO &
              AND A.MR_NO = C.MR_NO &
              AND TO_DATE(A.EXAMINE_DATE, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE(<DATE_S>, 'YYYYMMDD') AND TO_DATE(<DATE_E> || '235959', 'YYYYMMDDHH24MISS') &
              //只查询检查不通过的记录 wanglong add 20150410
              AND A.CASE_NO = D.CASE_NO &
              AND A.EXAMINE_CODE = D.EXAMINE_CODE &
              AND D.QUERYSTATUS = '1' &
              AND D.STATUS = '0' &
         GROUP BY C.REGION_CODE, C.DEPT_CODE, C.STATION_CODE, B.TYPE_CODE, A.EXAMINE_CODE &
         ORDER BY C.REGION_CODE, C.DEPT_CODE, C.STATION_CODE
select.item=STATION;DEPT;REGION_CODE
select.STATION=C.STATION_CODE=<STATION>
//=========pangben modify 20110518 start
select.REGION_CODE=C.REGION_CODE=<REGION_CODE>
//=========pangben modify 20110518 stop
select.DEPT=C.DEPT_CODE=<DEPT>
select.Debug=N

