# 
#  Title:工作报表module
# 
#  Description:工作报表module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.05.21
#  version 1.0
#
Module.item=selectdataDay;insertData;selectDEPT;checkNum;deleteSTA_DAILY_02;selectStation;selectOPD;selectSubmitDept;selectIPD_DEPT;selectDEPT_4;&
selectDeptList;selectSTA_DAILY_02;checkIPDDept;selectStationFirstDay;selectStationLastDay;updateSTA_DAILY_02

//查询相关数据 工作报表查询(日报)
selectdataDay.Type=TSQL
selectdataDay.SQL=SELECT B.OUTP_NUM,B.ERD_NUM,B.ERD_DIED_NUM,B.OBS_NUM,B.OBS_DIED_NUM, &
                         C.OUYCHK_OI_NUM,C.OUYCHK_RAPA_NUM,C.OUYCHK_INOUT,C.OUYCHK_OPBFAF,C.HEAL_LV_I_CASE,&
                         C.HEAL_LV_BAD,C.GET_TIMES,C.SUCCESS_TIMES,C.BED_RETUEN,C.BED_WORK_DAY,&
                         C.BED_USE_RATE,A.DATA_01, A.DATA_02, A.DATA_03, A.DATA_04, &
                         A.DATA_05, A.DATA_06, A.DATA_06_1, A.DATA_07, A.DATA_08, &
                         A.DATA_08_1, A.DATA_09, A.DATA_10, A.DATA_11, A.DATA_12, &
                         A.DATA_13, A.DATA_14, A.DATA_15, A.DATA_15_1, A.DATA_16, &
                         A.DATA_17, A.DATA_18, A.DATA_19, A.DATA_20, A.DATA_21, &
                         A.DATA_22, A.DEPT_CODE ,A.STATION_CODE &
                    FROM STA_DAILY_01 A,STA_OPD_DAILY B,STA_STATION_DAILY C &
                   WHERE A.DEPT_CODE=B.DEPT_CODE &
                     AND A.STA_DATE=B.STA_DATE &
                     AND A.DEPT_CODE=C.DEPT_CODE &
                     AND A.STA_DATE=C.STA_DATE
selectdataDay.item=STA_DATE;DEPT_CODE
selectdataDay.STA_DATE=A.STA_DATE=<STA_DATE>
selectdataDay.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectdataDay.Debug=N

//查询住院相关数据(日报)
selectStation.Type=TSQL
selectStation.SQL=SELECT SUM(C.OUYCHK_OI_NUM) AS OUYCHK_OI_NUM,SUM(C.OUYCHK_RAPA_NUM) AS OUYCHK_RAPA_NUM,SUM(C.OUYCHK_INOUT) AS OUYCHK_INOUT,&
                         SUM(C.OUYCHK_OPBFAF) AS OUYCHK_OPBFAF,SUM(C.HEAL_LV_I_CASE) AS HEAL_LV_I_CASE, &
                         SUM(C.HEAL_LV_BAD) AS ,SUM(C.GET_TIMES) AS GET_TIMES,SUM(C.SUCCESS_TIMES) AS SUCCESS_TIMES,SUM(A.DATA_01) AS DATA_01,&
                         SUM(A.DATA_02) AS DATA_02,SUM(A.DATA_03) AS DATA_03,SUM(A.DATA_04) AS DATA_04,&
                         SUM(A.DATA_05) AS DATA_05,SUM(A.DATA_06) AS DATA_06,SUM(A.DATA_06_1) AS DATA_06_1,SUM(A.DATA_07) AS DATA_07,SUM(A.DATA_08) AS DATA_08, &
                         SUM(A.DATA_08_1) AS DATA_08_1,SUM(A.DATA_09) AS DATA_09,SUM(A.DATA_10) AS DATA_10,SUM(A.DATA_11) AS DATA_11,SUM(A.DATA_12) AS DATA_12, &
                         SUM(A.DATA_13) AS DATA_13,SUM(A.DATA_14) AS DATA_14,SUM(A.DATA_15) AS DATA_15,SUM(A.DATA_15_1) AS DATA_15_1,SUM(A.DATA_16) AS DATA_16, &
                         SUM(A.DATA_19) AS DATA_19,SUM(A.DATA_20) AS DATA_20,SUM(A.DATA_21) AS DATA_21, &
                         SUM(A.DATA_22) AS DATA_22,A.DEPT_CODE,A.CONFIRM_FLG,A.DATA_17,A.DATA_18 &
                   FROM STA_DAILY_01 A,STA_STATION_DAILY C &
                  WHERE A.DEPT_CODE=C.DEPT_CODE AND A.STATION_CODE=B.STATION_CODE &
		            AND A.STA_DATE=C.STA_DATE  &
		            AND C.REGION_CODE=<REGION_CODE> &
               GROUP BY A.DEPT_CODE,A.CONFIRM_FLG,A.DATA_17,A.DATA_18
selectStation.item=STA_DATE;DEPT_CODE
selectStation.STA_DATE=A.STA_DATE=<STA_DATE>
selectStation.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
selectStation.Debug=N

//查询门急诊相关数据(日报)
selectOPD.Type=TSQL
selectOPD.SQL=SELECT STA_DATE, DEPT_CODE, OUTP_NUM, &
                     ERD_NUM, HRM_NUM, OTHER_NUM, &
                     GET_TIMES, PROF_DR, COMM_DR, &
                     DR_HOURS, SUCCESS_TIMES, OBS_NUM, &
                     ERD_DIED_NUM, OBS_DIED_NUM, OPE_NUM, &
                     FIRST_NUM, FURTHER_NUM, OPT_USER, &
                     OPT_DATE, OPT_TERM &
                FROM STA_OPD_DAILY
selectOPD.item=STA_DATE;DEPT_CODE;REGION_CODE
selectOPD.STA_DATE=STA_DATE=<STA_DATE>
//============pangben modify 20110519 start
selectOPD.REGION_CODE=REGION_CODE=<REGION_CODE>
//============pangben modify 20110519 stop
selectOPD.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
selectOPD.Debug=N

//插入STA_DAILY_02表
//=============pangben modify 20110519 添加区域列
insertData.Type=TSQL
insertData.SQL=Insert into STA_DAILY_02 &
		   (STA_DATE, DEPT_CODE, STATION_CODE, DATA_01, DATA_02, &
		    DATA_03, DATA_04, DATA_05, DATA_06, DATA_07, &
		    DATA_08, DATA_08_1, DATA_09, DATA_10, DATA_11, &
		    DATA_12, DATA_13, DATA_14, DATA_15, DATA_15_1, &
		    DATA_16, DATA_17, DATA_18, DATA_19, DATA_20, &
		    DATA_21, DATA_22, DATA_23, DATA_24, DATA_25, &
		    DATA_26, DATA_27, DATA_28, DATA_29, DATA_30, &
		    DATA_31, DATA_32, DATA_33, DATA_34, DATA_35, &
		    DATA_36, DATA_37, DATA_38, DATA_39, DATA_40, &
		    DATA_41, DATA_41_1, DATA_41_2, CONFIRM_FLG, CONFIRM_USER, &
		    CONFIRM_DATE, OPT_USER, OPT_DATE, OPT_TERM,SUBMIT_FLG,REGION_CODE) &
		 Values &
		   (<STA_DATE>, <DEPT_CODE>, <STATION_CODE>, <DATA_01>, <DATA_02>, &
		    <DATA_03>, <DATA_04>, <DATA_05>, <DATA_06>, <DATA_07>, &
		    <DATA_08>, <DATA_08_1>, <DATA_09>, <DATA_10>, <DATA_11>, &
		    <DATA_12>, <DATA_13>, <DATA_14>, <DATA_15>, <DATA_15_1>, &
		    <DATA_16>, <DATA_17>, <DATA_18>, <DATA_19>, <DATA_20>, &
		    <DATA_21>, <DATA_22>, <DATA_23>, <DATA_24>, <DATA_25>, &
		    <DATA_26>, <DATA_27>, <DATA_28>, <DATA_29>, <DATA_30>, &
		    <DATA_31>, <DATA_32>, <DATA_33>, <DATA_34>, <DATA_35>, &
		    <DATA_36>, <DATA_37>, <DATA_38>, <DATA_39>, <DATA_40>, &
		    <DATA_41>, <DATA_41_1>, <DATA_41_2>, <CONFIRM_FLG>, <CONFIRM_USER>, &
		    SYSDATE, <OPT_USER>, SYSDATE, <OPT_TERM>,<SUBMIT_FLG>,<REGION_CODE>)
insertData.Debug=N

//更新STA_DAILY_02表
updateSTA_DAILY_02.Type=TSQL
updateSTA_DAILY_02.SQL=UPDATE STA_DAILY_02 SET DATA_01=<DATA_01>,DATA_02=<DATA_02>,DATA_03=<DATA_03>,&
                              DATA_04=<DATA_08_1>, DATA_09=<DATA_09>, DATA_10=<DATA_10>, DATA_11=<DATA_11>,DATA_12=<DATA_12>,&
                              DATA_08_1=<DATA_04>, DATA_05=<DATA_05>, DATA_06=<DATA_06>, DATA_07=<DATA_07>,DATA_08=<DATA_08>,&
                              DATA_13=<DATA_13>, DATA_14=<DATA_14>, DATA_15=<DATA_15>, DATA_15_1=<DATA_15_1>,DATA_16=<DATA_16>,&
                              DATA_17=<DATA_17>, DATA_18=<DATA_18>, DATA_19=<DATA_19>, DATA_20=<DATA_20>,DATA_21=<DATA_21>,&
                              DATA_22=<DATA_22>, DATA_23=<DATA_23>, DATA_24=<DATA_24>, DATA_25=<DATA_25>,DATA_26=<DATA_26>,&
                              DATA_27=<DATA_27>, DATA_28=<DATA_28>, DATA_29=<DATA_29>, DATA_30=<DATA_30>,DATA_31=<DATA_31>,&
                              DATA_32=<DATA_32>, DATA_33=<DATA_33>, DATA_34=<DATA_34>, DATA_35=<DATA_35>,DATA_36=<DATA_36>,&
                              DATA_37=<DATA_37>, DATA_38=<DATA_38>, DATA_39=<DATA_39>, DATA_40=<DATA_40>,DATA_41=<DATA_41>,&
                              DATA_41_1=<DATA_41_1>, DATA_41_2=<DATA_41_2>,&
                              CONFIRM_FLG=<CONFIRM_FLG>, CONFIRM_USER=<CONFIRM_USER>,CONFIRM_DATE=SYSDATE,&
                              OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM>, SUBMIT_FLG=<SUBMIT_FLG> &
                        WHERE STA_DATE=<STA_DATE> &
                          AND DEPT_CODE=<DEPT_CODE> &
                          AND REGION_CODE=<REGION_CODE>
updateSTA_DAILY_02.Debug=N


//判断STA_DAILY_02表是否存在指定日期的数据
checkNum.Type=TSQL
checkNum.SQL=SELECT A.STA_DATE,A.CONFIRM_FLG &
               FROM STA_DAILY_02 A, STA_OEI_DEPT_LIST B &
              WHERE A.DEPT_CODE = B.DEPT_CODE &
                AND A.STA_DATE = <STA_DATE>
checkNum.item=REGION_CODE
checkNum.REGION_CODE=B.REGION_CODE=<REGION_CODE>
checkNum.Debug=N

//删除STA_DAILY_02表指定日期的数据
deleteSTA_DAILY_02.Type=TSQL
deleteSTA_DAILY_02.SQL=DELETE FROM STA_DAILY_02 WHERE STA_DATE=<STA_DATE> AND REGION_CODE=<REGION_CODE>
deleteSTA_DAILY_02.Debug=N

//中间表科室 查询(三级科室)
selectDEPT.Type=TSQL
selectDEPT.SQL=SELECT DEPT_CODE, DEPT_DESC, OE_DEPT_CODE, IPD_DEPT_CODE &
                 FROM STA_OEI_DEPT_LIST &
                WHERE DEPT_LEVEL='3'
//===========pangben modify 20110520 start
selectDEPT.item=REGION_CODE
selectDEPT.REGION_CODE=REGION_CODE=<REGION_CODE>
//===========pangben modify 20110520 stop
selectDEPT.Debug=N

//中间表科室 查询(四级科室)
selectDEPT_4.Type=TSQL
selectDEPT_4.SQL=SELECT DISTINCT DEPT_CODE, DEPT_DESC, OE_DEPT_CODE, IPD_DEPT_CODE &
                   FROM STA_OEI_DEPT_LIST &
                  WHERE DEPT_LEVEL='4' 
//===========pangben modify 20110520 start
selectDEPT_4.item=REGION_CODE
selectDEPT_4.REGION_CODE=REGION_CODE=<REGION_CODE>
//===========pangben modify 20110520 stop
selectDEPT_4.Debug=N

//在中间科室表中查询属于住院科室的CODE
selectIPD_DEPT.Type=TSQL
selectIPD_DEPT.SQL=SELECT DISTINCT DEPT_CODE, DEPT_DESC, OE_DEPT_CODE, IPD_DEPT_CODE &
                     FROM STA_OEI_DEPT_LIST &
                    WHERE DEPT_LEVEL='4' &
                      AND IPD_DEPT_CODE IS NOT NULL &
                 ORDER BY DEPT_CODE
//=============pangben modify 20110520 start
selectIPD_DEPT.item=REGION_CODE
selectIPD_DEPT.REGION_CODE=REGION_CODE=<REGION_CODE>
//=============pangben modify 20110520 stop
selectIPD_DEPT.Debug=N

//查询STA_DAILY_01表中数据的已经提交的部门CODE
selectSubmitDept.Type=TSQL
selectSubmitDept.SQL=SELECT STA_DATE, DEPT_CODE, STATION_CODE, &
                            CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE &
                       FROM STA_DAILY_01 &
                      WHERE CONFIRM_FLG='Y' &
                        AND STA_DATE=<STA_DATE>
//=============pangben modify 20110520 start
selectSubmitDept.item=REGION_CODE
selectSubmitDept.REGION_CODE=REGION_CODE=<REGION_CODE>
//=============pangben modify 20110520 stop
selectSubmitDept.Debug=N


//查询 1，2，3级部门
selectDeptList.Type=TSQL
selectDeptList.SQL=SELECT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,OE_DEPT_CODE,IPD_DEPT_CODE &
                     FROM STA_OEI_DEPT_LIST &
                    WHERE DEPT_LEVEL <> '4' &
                 ORDER BY DEPT_CODE,SEQ
selectDeptList.Debug=N

//查询
selectSTA_DAILY_02.Type=TSQL
selectSTA_DAILY_02.SQL=SELECT STA_DATE, DEPT_CODE, STATION_CODE, &
                              TO_CHAR(DATA_01) AS DATA_01, TO_CHAR(DATA_02) AS DATA_02, TO_CHAR(DATA_03) AS DATA_03, &
                              TO_CHAR(DATA_04) AS DATA_04, TO_CHAR(DATA_05) AS DATA_05, TO_CHAR(DATA_06) AS DATA_06, &
                              TO_CHAR(DATA_07) AS DATA_07, TO_CHAR(DATA_08) AS DATA_08, TO_CHAR(DATA_08_1) AS DATA_08_1, &
                              TO_CHAR(DATA_09) AS DATA_09, TO_CHAR(DATA_10) AS DATA_10, TO_CHAR(DATA_11) AS DATA_11, &
                              TO_CHAR(DATA_12) AS DATA_12, TO_CHAR(DATA_13) AS DATA_13, TO_CHAR(DATA_14) AS DATA_14, &
                              TO_CHAR(DATA_15) AS DATA_15, TO_CHAR(DATA_15_1) AS DATA_15_1, TO_CHAR(DATA_16) AS DATA_16, &
                              TO_CHAR(DATA_17) AS DATA_17, TO_CHAR(DATA_18) AS DATA_18, TO_CHAR(DATA_19) AS DATA_19, &
                              TO_CHAR(DATA_20) AS DATA_20, TO_CHAR(DATA_21) AS DATA_21, TO_CHAR(DATA_22) AS DATA_22, &
                              TO_CHAR(DATA_23) AS DATA_23, TO_CHAR(DATA_24) AS DATA_24, TO_CHAR(DATA_25) AS DATA_25, &
                              TO_CHAR(DATA_26) AS DATA_26, TO_CHAR(DATA_27) AS DATA_27, TO_CHAR(DATA_28) AS DATA_28, &
                              TO_CHAR(DATA_29) AS DATA_29, TO_CHAR(DATA_30) AS DATA_30, TO_CHAR(DATA_31) AS DATA_31, &
                              TO_CHAR(DATA_32) AS DATA_32, TO_CHAR(DATA_33) AS DATA_33, TO_CHAR(DATA_34) AS DATA_34, &
                              TO_CHAR(DATA_35) AS DATA_35, TO_CHAR(DATA_36) AS DATA_36, TO_CHAR(DATA_37) AS DATA_37, &
                              TO_CHAR(DATA_38) AS DATA_38, TO_CHAR(DATA_39) AS DATA_39, TO_CHAR(DATA_40) AS DATA_40, &
                              TO_CHAR(DATA_41) AS DATA_41, TO_CHAR(DATA_41_1) AS DATA_41_1, TO_CHAR(DATA_41_2) AS DATA_41_2 &
                         FROM STA_DAILY_02 
selectSTA_DAILY_02.item=STA_DATE;REGION_CODE
selectSTA_DAILY_02.STA_DATE=STA_DATE=<STA_DATE>
//=======pangben modify 20110519 start
selectSTA_DAILY_02.REGION_CODE=REGION_CODE=<REGION_CODE>
//=======pangben modify 20110519 stop
selectSTA_DAILY_02.Debug=N

//检查四级科室是不是住院科室
checkIPDDept.Type=TSQL
checkIPDDept.SQL=SELECT IPD_DEPT_CODE  &
                   FROM STA_OEI_DEPT_LIST  &
                  WHERE DEPT_CODE=<DEPT_CODE> &
                    AND DEPT_LEVEL='4'
checkIPDDept.Debug=N

//查询月报表期初值（月第一天值）  查询期初实有病人数
selectStationFirstDay.Type=TSQL
selectStationFirstDay.SQL=SELECT DEPT_CODE,SUM(DATA_07) AS DATA_07 &
                            FROM STA_DAILY_01 &
                           WHERE STA_DATE=<STA_DATE> &
                        GROUP BY DEPT_CODE
//=======pangben modify 20110519 start
selectStationFirstDay.item=REGION_CODE
selectStationFirstDay.REGION_CODE=REGION_CODE=<REGION_CODE>
//=======pangben modify 20110519 stop
selectStationFirstDay.Debug=N

//查询月报表期末值（月最后一天值） 查询 实有病人数 期末实有病床
selectStationLastDay.Type=TSQL
selectStationLastDay.SQL=SELECT DEPT_CODE,SUM(DATA_16) AS DATA_16,DATA_17 FROM STA_DAILY_01 &
                          WHERE STA_DATE = ( &
                                            SELECT MAX(STA_DATE) FROM STA_DAILY_02 &
                                             WHERE LENGTH(STA_DATE)=8 &
                                               AND TO_DATE(STA_DATE,'YYYYMMDD') BETWEEN TO_DATE(<DATE_S>,'YYYYMMDD') &
                                                                                    AND TO_DATE(<DATE_E>,'YYYYMMDD')) &
                       GROUP BY DEPT_CODE,DATA_17
//=======pangben modify 20110519 start
selectStationLastDay.item=REGION_CODE
selectStationLastDay.REGION_CODE=REGION_CODE=<REGION_CODE>
//=======pangben modify 20110519 stop
selectStationLastDay.Debug=N