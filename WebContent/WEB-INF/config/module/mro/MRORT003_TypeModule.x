###############################
# <p>Title:医师问题类型统计表 </p>
#
# <p>Description:医师问题类型统计表 </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:Javahis </p>
#
# @author zhangk  2009-9-13
# @version 4.0
#
###############################
Module.item=selectOUT;selectIN;selectOUTDetail;selectINDetail

//出院患者统计
selectOUT.Type=TSQL
selectOUT.SQL=SELECT C.REGION_CODE,C.DEPT_CODE,C.STATION_CODE,A.VS_CODE DR_CODE,B.TYPE_CODE,A.EXAMINE_CODE,COUNT(A.EXAMINE_CODE) AS EXAMINE_COUNT &
                FROM MRO_CHRTVETREC A, MRO_CHRTVETSTD B, ADM_INP C, MRO_QLAYCONTROLM D &
               WHERE A.EXAMINE_CODE = B.EXAMINE_CODE &
                 AND A.CASE_NO = C.CASE_NO &
                 AND A.MR_NO = C.MR_NO &
                 AND C.DS_DATE IS NOT NULL &
                 AND A.CASE_NO = D.CASE_NO &
                 AND A.EXAMINE_CODE = D.EXAMINE_CODE &
                 AND D.QUERYSTATUS = '1' &
                 AND D.STATUS = '0' &
                 AND TO_DATE( A.EXAMINE_DATE, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
            GROUP BY C.REGION_CODE, C.DEPT_CODE, C.STATION_CODE, A.VS_CODE, B.TYPE_CODE, A.EXAMINE_CODE &
            ORDER BY C.REGION_CODE, C.DEPT_CODE, C.STATION_CODE, A.VS_CODE, A.EXAMINE_CODE
selectOUT.item=STATION;VS_CODE;DEPT;REGION_CODE
selectOUT.STATION=C.STATION_CODE=<STATION>
//========pangben modify 20110518 start
selectOUT.REGION_CODE=C.REGION_CODE=<REGION_CODE>
//========pangben modify 20110518 stop
selectOUT.VS_CODE=A.VS_CODE=<VS_CODE>
selectOUT.DEPT=C.DEPT_CODE=<DEPT>
selectOUT.Debug=N

//在院统计
selectIN.Type=TSQL
selectIN.SQL=SELECT C.REGION_CODE,C.DEPT_CODE,C.STATION_CODE,A.VS_CODE DR_CODE,B.TYPE_CODE,A.EXAMINE_CODE,COUNT(A.EXAMINE_CODE) AS EXAMINE_COUNT &
               FROM MRO_CHRTVETREC A, MRO_CHRTVETSTD B, ADM_INP C, MRO_QLAYCONTROLM D &
              WHERE A.EXAMINE_CODE = B.EXAMINE_CODE &
                AND A.CASE_NO = C.CASE_NO &
                AND A.MR_NO = C.MR_NO &
                AND C.DS_DATE IS NULL &
                AND A.CASE_NO = D.CASE_NO &
                AND A.EXAMINE_CODE = D.EXAMINE_CODE &
                AND D.QUERYSTATUS = '1' &
                AND D.STATUS = '0' &
                AND TO_DATE( A.EXAMINE_DATE, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
           GROUP BY C.REGION_CODE, C.DEPT_CODE, C.STATION_CODE, A.VS_CODE, B.TYPE_CODE, A.EXAMINE_CODE &
           ORDER BY C.REGION_CODE, C.DEPT_CODE, C.STATION_CODE, A.VS_CODE, A.EXAMINE_CODE
selectIN.item=STATION;VS_CODE;DEPT;REGION_CODE
selectIN.STATION=C.STATION_CODE=<STATION>
//========pangben modify 20110518 start
selectIN.REGION_CODE=C.REGION_CODE=<REGION_CODE>
//========pangben modify 20110518 stop
selectIN.VS_CODE=A.VS_CODE=<VS_CODE>
selectIN.DEPT=C.DEPT_CODE=<DEPT>
selectIN.Debug=N

//出院患者统计明细 add by wanglong 20130909
selectOUTDetail.Type=TSQL
selectOUTDetail.SQL=SELECT C.REGION_CODE,C.DEPT_CODE,C.STATION_CODE,A.VS_CODE DR_CODE,A.MR_NO,D.PAT_NAME,B.TYPE_CODE,A.EXAMINE_CODE &
                      FROM MRO_CHRTVETREC A, MRO_CHRTVETSTD B, ADM_INP C, SYS_PATINFO D, MRO_QLAYCONTROLM E &
                     WHERE A.EXAMINE_CODE = B.EXAMINE_CODE &
                       AND A.CASE_NO = C.CASE_NO &
                       AND A.MR_NO = C.MR_NO &
                       AND A.MR_NO = D.MR_NO &
                       AND C.DS_DATE IS NOT NULL &
                       AND A.CASE_NO = E.CASE_NO &
                       AND A.EXAMINE_CODE = E.EXAMINE_CODE &
                       AND E.QUERYSTATUS = '1' &
                       AND E.STATUS = '0' &
                       AND TO_DATE(A.EXAMINE_DATE, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
                  GROUP BY C.REGION_CODE,C.DEPT_CODE,C.STATION_CODE,A.VS_CODE,A.MR_NO,D.PAT_NAME,B.TYPE_CODE,A.EXAMINE_CODE &
                  ORDER BY C.REGION_CODE,C.DEPT_CODE,C.STATION_CODE,A.VS_CODE,A.MR_NO,A.EXAMINE_CODE
selectOUTDetail.item=STATION;VS_CODE;DEPT;REGION_CODE
selectOUTDetail.STATION=C.STATION_CODE=<STATION>
//========pangben modify 20110518 start
selectOUTDetail.REGION_CODE=C.REGION_CODE=<REGION_CODE>
//========pangben modify 20110518 stop
selectOUTDetail.VS_CODE=A.VS_CODE=<VS_CODE>
selectOUTDetail.DEPT=C.DEPT_CODE=<DEPT>
selectOUTDetail.Debug=N

//在院统计明细 add by wanglong 20130909
selectINDetail.Type=TSQL
selectINDetail.SQL=SELECT C.REGION_CODE,C.DEPT_CODE,C.STATION_CODE,A.VS_CODE DR_CODE,A.MR_NO,D.PAT_NAME,B.TYPE_CODE,A.EXAMINE_CODE &
                      FROM MRO_CHRTVETREC A, MRO_CHRTVETSTD B, ADM_INP C, SYS_PATINFO D, MRO_QLAYCONTROLM E &
                     WHERE A.EXAMINE_CODE = B.EXAMINE_CODE &
                       AND A.CASE_NO = C.CASE_NO &
                       AND A.MR_NO = C.MR_NO &
                       AND A.MR_NO = D.MR_NO &
                       AND C.DS_DATE IS NULL &
                       AND A.CASE_NO = E.CASE_NO &
                       AND A.EXAMINE_CODE = E.EXAMINE_CODE &
                       AND E.QUERYSTATUS = '1' &
                       AND E.STATUS = '0' &
                       AND TO_DATE(A.EXAMINE_DATE, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') AND TO_DATE(<DATE_E>||'235959','YYYYMMDDHH24MISS') &
                  GROUP BY C.REGION_CODE,C.DEPT_CODE,C.STATION_CODE,A.VS_CODE,A.MR_NO,D.PAT_NAME,B.TYPE_CODE,A.EXAMINE_CODE &
                  ORDER BY C.REGION_CODE,C.DEPT_CODE,C.STATION_CODE,A.VS_CODE,A.MR_NO,A.EXAMINE_CODE
selectINDetail.item=STATION;VS_CODE;DEPT;REGION_CODE
selectINDetail.STATION=C.STATION_CODE=<STATION>
//========pangben modify 20110518 start
selectINDetail.REGION_CODE=C.REGION_CODE=<REGION_CODE>
//========pangben modify 20110518 stop
selectINDetail.VS_CODE=A.VS_CODE=<VS_CODE>
selectINDetail.DEPT=C.DEPT_CODE=<DEPT>
selectINDetail.Debug=N