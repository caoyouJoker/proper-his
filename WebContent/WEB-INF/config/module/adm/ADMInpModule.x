###############################################
# <p>Title:住院登记 </p>
#
# <p>Description:住院登记 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk
# @version 1.0   
###############################################
Module.item=insertForInp;update;selectall;insertAll;checkAdmInp;queryInStation;updateForWait;updateForWaitPat;updateForOutDept;outAdmInp;queryBed;SelectLastDsDate;&
 	     queryCaseNo;updateYellowRed;updateForBillPay;updateForCancel;updateForAdmInp;upDateWeightHigh;updateNewDaily;inHosptal;clearDS_DATE;updateBedNoForReturn;&
	     updateNURSING_CLASS;updatePATIENT_STATUS;updateMEDDISCH_DATE;updateGREENPATH_VALUE;updateMRO_CHAT_FLG;clearCHARGE_DATE;selectForXML;selectInHosp;&
	     updateLAST_DS_DATE;updateINPConfirmNo;queryAdmNClose;queryAdmNCloseInsBalance;selectBedNo;insertMRODiag;updatePatPro
 
//查询全字段
selectall.Type=TSQL
selectall.SQL=SELECT CASE_NO, IPD_NO, MR_NO, DEPT_CODE, STATION_CODE,&
		BED_NO,CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, ADM_SOURCE,&
 		IN_DATE, IN_DEPT_CODE, IN_STATION_CODE,DS_DATE, DS_DEPT_CODE,&
 		DS_STATION_CODE, DRG_CODE, DISCH_CODE, TRANS_HOSP,INVISIBLE_FLG,&
 		BORN_FLG, ACCIDENT_FLG, DEAD48_FLG, NOTIFY_FLG, CANCEL_FLG,&
 		SEPA_FLG,CLINICAL_FLG, NEW_BORN_FLG, M_CASE_NO,MERGE_O_CASE_NO,&
 		MERGE_I_CASE_NO, OPD_DR_CODE, VS_DR_CODE, ATTEND_DR_CODE,DIRECTOR_DR_CODE,&
 		ADM_DAYS, VS_NURSE_CODE, PRESS_DATE,ADM_DATE,ADM_CLERK, &
 		ADM_REMK,REGION_CODE,MAINDIAG,MEDDISCH_DATE,BILL_DATE,&
 		CHARGE_DATE,RED_SIGN,YELLOW_SIGN,STOP_BILL_FLG,CONFIRM_NO,&
 		PATIENT_STATUS,NURSING_CLASS,PATIENT_CONDITION,DIE_CONDITION,CARE_NUM,TOTAL_AMT,TOTAL_BILPAY,&
 		CUR_AMT,GREENPATH_VALUE,WEIGHT,HEIGHT,CLNCPATH_CODE,&
 		OPT_USER,OPT_DATE,OPT_TERM,IN_COUNT,PAY_INS,BILL_STATUS, &
		IO_MEASURE,ISOLATION,ALLERGY,SERVICE_LEVEL,TOILET,LAST_DS_DATE ,&
		//加入过敏说明
		PATLOGY_PRO_CODE, PATLOGY_DEPT_CODE, PATLOGY_DOC_CODE, PATLOGY_PRO_DATE, PATLOGY_PRO_REMARK, ALLERGIC_MARK,DAY_OPE_FLG, INFECT_SCR_RESULT &
 		FROM ADM_INP 
selectall.item=IPD_NO;MR_NO;CASE_NO;BED_NO;CANCEL_FLG;REGION_CODE;DS_DATE
selectall.IPD_NO=IPD_NO=<IPD_NO>
selectall.MR_NO=MR_NO=<MR_NO>
selectall.CASE_NO=CASE_NO=<CASE_NO>
selectall.BED_NO=BED_NO=<BED_NO>
selectall.CANCEL_FLG=CANCEL_FLG=<CANCEL_FLG>
//===========pangben modify 20110516 start
selectall.REGION_CODE=REGION_CODE=<REGION_CODE>
selectall.DS_DATE=DS_DATE IS NULL
//===========pangben modify 20110516 stop
selectall.Debug=N
 
//=================alter by wukai 20160510
queryall.Type=TSQL
queryall.SQL= SELECT * &
		FROM ( SELECT CASE_NO, IPD_NO, MR_NO, DEPT_CODE, STATION_CODE,&
		BED_NO,CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, ADM_SOURCE,&
 		IN_DATE, IN_DEPT_CODE, IN_STATION_CODE,DS_DATE, DS_DEPT_CODE,&
 		DS_STATION_CODE, DRG_CODE, DISCH_CODE, TRANS_HOSP,INVISIBLE_FLG,&
 		BORN_FLG, ACCIDENT_FLG, DEAD48_FLG, NOTIFY_FLG, CANCEL_FLG,&
 		SEPA_FLG,CLINICAL_FLG, NEW_BORN_FLG, M_CASE_NO,MERGE_O_CASE_NO,&
 		MERGE_I_CASE_NO, OPD_DR_CODE, VS_DR_CODE, ATTEND_DR_CODE,DIRECTOR_DR_CODE,&
 		ADM_DAYS, VS_NURSE_CODE, PRESS_DATE,ADM_DATE,ADM_CLERK, &
 		ADM_REMK,REGION_CODE,MAINDIAG,MEDDISCH_DATE,BILL_DATE,&
 		CHARGE_DATE,RED_SIGN,YELLOW_SIGN,STOP_BILL_FLG,CONFIRM_NO,&
 		PATIENT_STATUS,NURSING_CLASS,PATIENT_CONDITION,DIE_CONDITION,CARE_NUM,TOTAL_AMT,TOTAL_BILPAY,&
 		CUR_AMT,GREENPATH_VALUE,WEIGHT,HEIGHT,CLNCPATH_CODE,&
 		OPT_USER,OPT_DATE,OPT_TERM,IN_COUNT,PAY_INS,BILL_STATUS, &
		IO_MEASURE,ISOLATION,ALLERGY,SERVICE_LEVEL,TOILET,LAST_DS_DATE &
 		FROM ADM_INP & 
		
		UNION &
		
		SELECT SELECT CASE_NO, IPD_NO, MR_NO, DEPT_CODE, STATION_CODE,&
		BED_NO,CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, ADM_SOURCE,&
 		IN_DATE, IN_DEPT_CODE, IN_STATION_CODE,DS_DATE, DS_DEPT_CODE,&
 		DS_STATION_CODE, DRG_CODE, DISCH_CODE, TRANS_HOSP,INVISIBLE_FLG,&
 		BORN_FLG, ACCIDENT_FLG, DEAD48_FLG, NOTIFY_FLG, CANCEL_FLG,&
 		SEPA_FLG,CLINICAL_FLG, NEW_BORN_FLG, M_CASE_NO,MERGE_O_CASE_NO,&
 		MERGE_I_CASE_NO, OPD_DR_CODE, VS_DR_CODE, ATTEND_DR_CODE,DIRECTOR_DR_CODE,&
 		ADM_DAYS, VS_NURSE_CODE, PRESS_DATE,ADM_DATE,ADM_CLERK, &
 		ADM_REMK,REGION_CODE,MAINDIAG,MEDDISCH_DATE,BILL_DATE,&
 		CHARGE_DATE,RED_SIGN,YELLOW_SIGN,STOP_BILL_FLG,CONFIRM_NO,&
 		PATIENT_STATUS,NURSING_CLASS,PATIENT_CONDITION,DIE_CONDITION,CARE_NUM,TOTAL_AMT,TOTAL_BILPAY,&
 		CUR_AMT,GREENPATH_VALUE,WEIGHT,HEIGHT,CLNCPATH_CODE,&
 		OPT_USER,OPT_DATE,OPT_TERM,IN_COUNT,PAY_INS,BILL_STATUS, &
		IO_MEASURE,ISOLATION,ALLERGY,SERVICE_LEVEL,TOILET,LAST_DS_DATE &
 		FROM ADM_INP )   

queryall.item=IPD_NO;MR_NO;CASE_NO;BED_NO;CANCEL_FLG;REGION_CODE;DS_DATE
queryall.IPD_NO=IPD_NO=<IPD_NO>
queryall.MR_NO=MR_NO=<MR_NO>
queryall.CASE_NO=CASE_NO=<CASE_NO>
queryall.BED_NO=BED_NO=<BED_NO>
queryall.CANCEL_FLG=CANCEL_FLG=<CANCEL_FLG>
queryall.REGION_CODE=REGION_CODE=<REGION_CODE>
queryall.DS_DATE=DS_DATE IS NULL 
queryall.Debug=N
 
 
//==================chenxi add 201302287
//查询预约床号
selectBedNo.Type=TSQL
selectBedNo.SQL=SELECT B.BED_NO_DESC FROM ADM_INP A,SYS_BED B  &
                 WHERE A.BED_NO =B.BED_NO                 &
                AND  B.APPT_FLG='Y'  AND A.CASE_NO = <CASE_NO>  &
               AND A.MR_NO=<MR_NO>                                
             
selectBedNo.Debug=N


//查询上次住院时间
SelectLastDsDate.Type=TSQL
SelectLastDsDate.SQL=SELECT DS_DATE FROM ADM_INP WHERE MR_NO = <MR_NO> &
                    AND CANCEL_FLG = <CANCEL_FLG>  AND DS_DATE IS NOT NULL ORDER BY DS_DATE DESC
SelectLastDsDate.Debug=N
 
 //插入预约住院全字段信息
insertAll.Type=TSQL
insertAll.SQL=INSERT INTO ADM_INP (CASE_NO, IPD_NO, MR_NO, DEPT_CODE, STATION_CODE,&
 		BED_NO,CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, ADM_SOURCE,&
 		IN_DATE, IN_DEPT_CODE, IN_STATION_CODE,DRG_CODE, TRANS_HOSP,&
 		INVISIBLE_FLG,BORN_FLG,ACCIDENT_FLG,DEAD48_FLG,NOTIFY_FLG,&
 		CANCEL_FLG,SEPA_FLG,CLINICAL_FLG,NEW_BORN_FLG,M_CASE_NO,&
 		OPD_DR_CODE,VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE,ADM_DAYS,& 		
 		PRESS_DATE,ADM_DATE,ADM_CLERK,ADM_REMK,REGION_CODE,&
 		MAINDIAG,CHARGE_DATE,MEDDISCH_DATE,BILL_DATE,RED_SIGN,&
 		YELLOW_SIGN,STOP_BILL_FLG,CONFIRM_NO,PATIENT_STATUS,NURSING_CLASS,&		
 		PATIENT_CONDITION,TOTAL_AMT,TOTAL_BILPAY,GREENPATH_VALUE,WEIGHT,&
 		HEIGHT,CLNCPATH_CODE,OPT_USER,OPT_DATE,OPT_TERM ) &
		VALUES (<CASE_NO>,<IPD_NO>,<MR_NO>,<DEPT_CODE>,<STATION_CODE>,&
		<BED_NO>,<CTZ1_CODE>,<CTZ2_CODE>,<CTZ3_CODE>,<ADM_SOURCE>,&
		<IN_DATE>,<IN_DEPT_CODE>,<IN_STATION_CODE>,<DRG_CODE>,<TRANS_HOSP>,&
		<INVISIBLE_FLG>,<BORN_FLG>,<ACCIDENT_FLG>,<DEAD48_FLG>,<NOTIFY_FLG>,&
		<CANCEL_FLG>,<SEPA_FLG>,<CLINICAL_FLG>,<NEW_BORN_FLG>,<M_CASE_NO>, &
		<OPD_DR_CODE>,<VS_DR_CODE>,<ADM_CLERK>,<DIRECTOR_DR_CODE>,<ADM_DAYS>, &	
		<PRESS_DATE>,<ADM_DATE>,<ATTEND_DR_CODE>,<ADM_REMK>,<REGION_CODE>, &
		<MAINDIAG>,<CHARGE_DATE>,<MEDDISCH_DATE>,<BILL_DATE>,<RED_SIGN>, &
		<YELLOW_SIGN>,<STOP_BILL_FLG>,<CONFIRM_NO>,<PATIENT_STATUS>,<NURSING_CLASS>, &
		<PATIENT_CONDITION>,<TOTAL_AMT>,<TOTAL_BILPAY>,<GREENPATH_VALUE>,<WEIGHT>, &
		<HEIGHT>,<CLNCPATH_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>) 
insertAll.Debug=N

 //插入住院
 //就诊序号,住院号,病案号,住院科别,住院病区,
 //床位号码,身份一,身份二,身份三，来源别
 //入院日期，入院科别，入院病区，入院状态，区域代码
insertForInp.Type=TSQL
insertForInp.SQL=INSERT INTO ADM_INP (CASE_NO, IPD_NO, MR_NO,DEPT_CODE,STATION_CODE,&
		BED_NO,CTZ1_CODE, CTZ2_CODE, CTZ3_CODE,ADM_SOURCE,&
		VS_DR_CODE,IN_DATE, IN_DEPT_CODE, IN_STATION_CODE,NEW_BORN_FLG,&		
		M_CASE_NO,YELLOW_SIGN,RED_SIGN,PATIENT_STATUS,REGION_CODE,&
		CANCEL_FLG,OPT_USER,OPT_DATE,OPT_TERM,SERVICE_LEVEL ) &		
		VALUES (<CASE_NO>,<IPD_NO>,<MR_NO>,<DEPT_CODE>,<STATION_CODE>,&
		<BED_NO>,<CTZ1_CODE>,<CTZ2_CODE>,<CTZ3_CODE>,<ADM_SOURCE>,&		
		<VS_DR_CODE>,<IN_DATE>,<IN_DEPT_CODE>,<STATION_CODE>,<NEW_BORN_FLG>,&
		<M_CASE_NO>,<YELLOW_SIGN>,<RED_SIGN>,<PATIENT_STATUS>,<REGION_CODE>,&
		'N',<OPT_USER>,SYSDATE,<OPT_TERM>,<SERVICE_LEVEL>)
insertForInp.Debug=N

//入院登记 插入信息
inHosptal.Type=TSQL
inHosptal.SQL=INSERT INTO ADM_INP( &
		CASE_NO,IPD_NO,MR_NO,DEPT_CODE,STATION_CODE,&
		BED_NO,CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,ADM_SOURCE,&
		VS_DR_CODE,IN_DATE,IN_DEPT_CODE,IN_STATION_CODE,NEW_BORN_FLG,&
		M_CASE_NO,YELLOW_SIGN,RED_SIGN,PATIENT_CONDITION,REGION_CODE,&
		CANCEL_FLG,OPT_USER,OPT_DATE,OPT_TERM,ADM_DATE,&
		ADM_CLERK,ADM_DAYS,IN_COUNT,OPD_DR_CODE,SERVICE_LEVEL,BILL_STATUS,DAY_OPE_FLG,  &
		ALLERGY) VALUES ( &
		<CASE_NO>,<IPD_NO>,<MR_NO>,<DEPT_CODE>,<STATION_CODE>,&
		<BED_NO>,<CTZ1_CODE>,<CTZ2_CODE>,<CTZ3_CODE>,<ADM_SOURCE>,&
		<VS_DR_CODE>,<IN_DATE>,<IN_DEPT_CODE>,<IN_STATION_CODE>,<NEW_BORN_FLG>,&
		<M_CASE_NO>,<YELLOW_SIGN>,<RED_SIGN>,<PATIENT_CONDITION>,<REGION_CODE>,&
		'N',<OPT_USER>,SYSDATE,<OPT_TERM>,SYSDATE,&
		<ADM_CLERK>,'1',<IN_COUNT>,<OPD_DR_CODE>,<SERVICE_LEVEL>,'0',<DAY_OPE_FLG>, &
		<ALLERGY>)
inHosptal.Debug=N

//检查病患是否住院中
checkAdmInp.Type = TSQL
checkAdmInp.SQL = SELECT IPD_NO,CASE_NO &
                  FROM ADM_INP &
		  WHERE MR_NO=<MR_NO> &
		  AND IN_DATE IS NOT null &
		  AND DS_DATE IS  NULL &
		  AND CANCEL_FLG <> 'Y'
checkAdmInp.Debug=N



//在院病患，床位查询
queryInStation.Type=TSQL
queryInStation.SQL=SELECT A.BED_STATUS, A.BED_NO,A.BED_NO_DESC,A.IPD_NO,A.MR_NO,C.PAT_NAME,ROUND(TO_NUMBER(B.IN_DATE-C.BIRTH_DATE)/365) AS AGE,C.BIRTH_DATE,& 
 			B.IN_DATE,B.CLNCPATH_CODE,B.DEPT_CODE,B.STATION_CODE,B.CTZ1_CODE,&
 			C.SEX_CODE,B.HEIGHT,B.WEIGHT,B.YELLOW_SIGN,B.GREENPATH_VALUE,&
 			B.VS_DR_CODE,B.ATTEND_DR_CODE,B.DIRECTOR_DR_CODE,B.VS_NURSE_CODE,B.TOTAL_BILPAY,&
 			B.CUR_AMT,A.CASE_NO,B.DS_DATE,B.PATIENT_CONDITION,&
 			//B.NURSING_CLASS,A.BED_OCCU_FLG,A.APPT_FLG &
 			B.NURSING_CLASS,A.BED_OCCU_FLG,A.APPT_FLG,B.DISE_CODE, &
 			A.PRE_MRNO,A.PRE_PATNAME,A.PRE_SEX AS PRE_SEX_CODE,A.PRETREAT_DATE,D.ROOM_DESC,A.PRE_FLG, A.PRETREAT_OUT_NO,A.ROOM_CODE,A.PRETREAT_NO,A.PRETREAT_TYPE &
			//modify by wanglong 20121115
 			FROM SYS_BED A,  ADM_INP B,  SYS_PATINFO C,SYS_ROOM D &
 			WHERE    B.BED_NO(+) = A.BED_NO &
			         AND B.CASE_NO(+) = A.CASE_NO &
			         AND B.MR_NO(+) = A.MR_NO &
			         AND C.MR_NO(+) = A.MR_NO &
			         AND B.DS_DATE IS  NULL &
			         AND B.STATION_CODE(+)=A.STATION_CODE &
				 AND A.ACTIVE_FLG='Y'   &
				 AND D.ROOM_CODE=A.ROOM_CODE &
			ORDER BY BED_NO
queryInStation.item=STATION_CODE;BED_NO;DEPT_CODE;REGION_CODE;ALLO_FLG;DR_APPROVE_FLG
queryInStation.STATION_CODE=A.STATION_CODE=<STATION_CODE>
//===========pangben modify 20110516 start
queryInStation.REGION_CODE=A.REGION_CODE=<REGION_CODE>
//===========pangben modify 20110516 stop
queryInStation.BED_NO=A.BED_NO=<BED_NO>
queryInStation.DEPT_CODE=B.DEPT_CODE=<DEPT_CODE>
queryInStation.ALLO_FLG=A.ALLO_FLG=<ALLO_FLG>
queryInStation.DR_APPROVE_FLG=A.DR_APPROVE_FLG=<DR_APPROVE_FLG>
queryInStation.Debug=N

//更新床位号
updateForWait.Type=TSQL
updateForWait.SQL=UPDATE ADM_INP SET BED_NO=<BED_NO> ,STATION_CODE=<STATION_CODE>,DEPT_CODE=<DEPT_CODE>,RED_SIGN=<RED_SIGN>,YELLOW_SIGN=<YELLOW_SIGN> , &
 			OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> & 
 			WHERE CASE_NO=<CASE_NO>
updateForWait.Debug=N
 
//在院信息更新
updateForWaitPat.Type=TSQL
updateForWaitPat.SQL=UPDATE ADM_INP SET VS_DR_CODE=<VS_DR_CODE> ,ATTEND_DR_CODE=<ATTEND_DR_CODE>,DIRECTOR_DR_CODE=<DIRECTOR_DR_CODE> , &
 			VS_NURSE_CODE=<VS_NURSE_CODE> ,PATIENT_CONDITION=<PATIENT_CONDITION> ,NURSING_CLASS=<NURSING_CLASS>,&
  			PATIENT_STATUS=<PATIENT_STATUS>,DIE_CONDITION=<DIE_CONDITION>,CARE_NUM=<CARE_NUM>, &
  			//fux modify 20161011 增加过敏说明
			  IO_MEASURE=<IO_MEASURE>,ISOLATION=<ISOLATION>,ALLERGY=<ALLERGY>,TOILET=<TOILET>, &
  			OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
  			WHERE CASE_NO=<CASE_NO>
updateForWaitPat.Debug=N
 
//转科	
updateForOutDept.Type=TSQL
updateForOutDept.SQL=UPDATE ADM_INP SET STATION_CODE=<STATION_CODE>,DEPT_CODE=<DEPT_CODE> , &
 			 OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> & 
  			WHERE CASE_NO=<CASE_NO>
updateForOutDept.Debug=N
 
//出院	
outAdmInp.Type=TSQL
outAdmInp.SQL=UPDATE ADM_INP SET DS_DEPT_CODE=<DS_DEPT_CODE>,DS_STATION_CODE=<DS_STATION_CODE> , &
  			 DS_DATE=<DS_DATE>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> & 
   			WHERE CASE_NO=<CASE_NO>
outAdmInp.Debug=N
 
//床位检索  chenxi modify 20130228
queryBed.Type=TSQL
queryBed.SQL=SELECT A.BED_NO,A.BED_NO_DESC,B.PAT_NAME,A.ROOM_CODE,A.STATION_CODE,&
		A.DEPT_CODE,A.BED_CLASS_CODE,A.BED_TYPE_CODE,A.APPT_FLG,A.ALLO_FLG,A.BED_OCCU_FLG,&
 	B.SEX_CODE,A.BABY_BED_FLG,A.BED_STATUS,A.MR_NO, &
     TO_CHAR(C.IN_DATE,'YYYY/MM/DD HH:MM:SS') AS IN_DATE, & 
     TO_CHAR(C.MEDDISCH_DATE,'YYYY/MM/DD HH:MM:SS') AS MEDDISCH_DATE ,A.DR_APPROVE_FLG &
 	FROM   SYS_BED A LEFT JOIN  SYS_PATINFO B ON B.MR_NO=A.MR_NO &
                LEFT JOIN ADM_INP C ON C.CASE_NO=A.CASE_NO &
 		WHERE   A.ACTIVE_FLG = 'Y'  &
 		ORDER BY A.BED_NO
queryBed.item=STATION_CODE;BED_NO;BED_STATUS;BED_CLASS_CODE;ROOM_CODE;ALLO_FLG;DR_APPROVE_FLG
queryBed.STATION_CODE=A.STATION_CODE=<STATION_CODE>
queryBed.BED_NO=A.BED_NO=<BED_NO>
queryBed.DEPT_CODE=A.DEPT_CODE=<DEPT_CODE>
queryBed.BED_STATUS=A.BED_STATUS=<BED_STATUS>
queryBed.BED_CLASS_CODE=A.BED_CLASS_CODE=<BED_CLASS_CODE>
queryBed.ROOM_CODE=A.ROOM_CODE=<ROOM_CODE>
queryBed.ALLO_FLG=A.ALLO_FLG=<ALLO_FLG>
queryBed.DR_APPROVE_FLG=A.DR_APPROVE_FLG=<DR_APPROVE_FLG>
queryBed.Debug=N
 
//查询在院病患的基本信息
//就诊序号,住院号,病案号,科室,病区/护士站
//病床,身份一,来源别（O：门诊；E：急诊；I：住院）,入院日期,门急诊医生,
//经治医生,主治医生,科主任,目前住院天数,红色警戒,黄色警戒,预交金,新生儿注记,入院次数
queryCaseNo.Type=TSQL
queryCaseNo.SQL=SELECT CASE_NO,IPD_NO,MR_NO,DEPT_CODE,STATION_CODE,&
 	BED_NO,CTZ1_CODE,CTZ2_CODE,CTZ3_CODE,ADM_SOURCE,IN_DATE,OPD_DR_CODE,&
 	VS_DR_CODE,ATTEND_DR_CODE,DIRECTOR_DR_CODE,ADM_DAYS,RED_SIGN,YELLOW_SIGN,TOTAL_BILPAY ,&
 	CUR_AMT,NEW_BORN_FLG,IN_COUNT,M_CASE_NO,SERVICE_LEVEL,DAY_OPE_FLG  &
 	FROM ADM_INP &
 	WHERE  DS_DATE IS NULL &
 	AND CANCEL_FLG <> 'Y'
queryCaseNo.item=MR_NO;IPD_NO;CASE_NO
queryCaseNo.MR_NO=MR_NO=<MR_NO>
queryCaseNo.IPD_NO=IPD_NO=<IPD_NO>
queryCaseNo.CASE_NO=CASE_NO=<CASE_NO>
queryCaseNo.Debug=N
 
//修改黄色警戒，红色警戒,入院日期，住院次数,床位号
updateForAdmInp.Type=TSQL
updateForAdmInp.SQL=UPDATE ADM_INP SET IN_DATE=<IN_DATE>,YELLOW_SIGN=<YELLOW_SIGN>,RED_SIGN=<RED_SIGN>,IN_COUNT=<IN_COUNT>,&
		 BED_NO=<BED_NO>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,ALLERGY=<ALLERGY> &
		 WHERE CASE_NO=<CASE_NO>
updateForAdmInp.Debug=N
 
//修改黄色警戒，红色警戒
updateYellowRed.Type=TSQL
updateYellowRed.SQL=UPDATE ADM_INP SET YELLOW_SIGN=<YELLOW_SIGN>,RED_SIGN=<RED_SIGN>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		  WHERE CASE_NO=<CASE_NO>
updateYellowRed.Debug=N
 
//更新adm_inp中STOP_BILL_FLG(停止计价注记),TOTAL_BILPAY(预交金),CUR_AMT(目前余额)字段
updateForBillPay.Type=TSQL
updateForBillPay.SQL=UPDATE ADM_INP &
			SET CUR_AMT = <CUR_AMT>,TOTAL_BILPAY=<TOTAL_BILPAY> ,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
 			WHERE CASE_NO=<CASE_NO>
 updateForBillPay.Debug=N
 
//取消住院
updateForCancel.Type=TSQL
updateForCancel.SQL=UPDATE ADM_INP &
		SET CANCEL_FLG = <CANCEL_FLG>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		WHERE CASE_NO=<CASE_NO>
updateForCancel.Debug=N
 
//修改病患身高体重
upDateWeightHigh.Type=TSQL
upDateWeightHigh.SQL=UPDATE ADM_INP &
 			SET WEIGHT = <WEIGHT>,HEIGHT=<HEIGHT>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
 			WHERE CASE_NO=<CASE_NO>
upDateWeightHigh.Debug=N

//修改最新诊断
updateNewDaily.Type=TSQL
updateNewDaily.SQL=UPDATE ADM_INP &
			SET MAINDIAG=<MAINDIAG>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
			WHERE CASE_NO=<CASE_NO>
updateNewDaily.Debug=N

//出院召回 清空出院日期
clearDS_DATE.Type=TSQL
clearDS_DATE.SQL=UPDATE ADM_INP SET &
		DS_DATE='' &
		WHERE CASE_NO=<CASE_NO>
clearDS_DATE.Debug=N

//账务召回  清空账务出院日期
clearCHARGE_DATE.Type=TSQL
clearCHARGE_DATE.SQL=UPDATE ADM_INP SET &
		     CHARGE_DATE='' &
		     WHERE CASE_NO=<CASE_NO>
clearCHARGE_DATE.Debug=N

//出院召回 修改召回后的床位
updateBedNoForReturn.Type=TSQL
updateBedNoForReturn.SQL=UPDATE ADM_INP SET &
				BED_NO=<BED_NO> &
				WHERE CASE_NO=<CASE_NO>
updateBedNoForReturn.Debug=N

//护士执行更新三级检诊  更新“护理等级”字段(住院护士站使用)
updateNURSING_CLASS.Type=TSQL
updateNURSING_CLASS.SQL=UPDATE ADM_INP SET NURSING_CLASS=<NURSING_CLASS> WHERE CASE_NO=<CASE_NO>
updateNURSING_CLASS.Debug=N

//护士执行更新三级检诊  更新“病情状态”字段(住院护士站使用)
updatePATIENT_STATUS.Type=TSQL
updatePATIENT_STATUS.SQL=UPDATE ADM_INP SET PATIENT_STATUS=<PATIENT_STATUS> WHERE CASE_NO=<CASE_NO>
updatePATIENT_STATUS.Debug=N

//出院通知 使用
updateMEDDISCH_DATE.Type=TSQL
updateMEDDISCH_DATE.SQL=UPDATE ADM_INP SET MEDDISCH_DATE=<MEDDISCH_DATE>,DISCH_CODE=<DISCH_CODE> WHERE CASE_NO=<CASE_NO>
updateMEDDISCH_DATE.Debug=N

//修改绿色通道值
updateGREENPATH_VALUE.Type=TSQL
updateGREENPATH_VALUE.SQL=UPDATE ADM_INP SET GREENPATH_VALUE=<GREENPATH_VALUE> WHERE CASE_NO=<CASE_NO>
updateGREENPATH_VALUE.Debug=N

//病案审核状态修改
updateMRO_CHAT_FLG.Type=TSQL
updateMRO_CHAT_FLG.SQL=UPDATE ADM_INP SET MRO_CHAT_FLG=<MRO_CHAT_FLG> WHERE CASE_NO=<CASE_NO>
updateMRO_CHAT_FLG.Debug=N


//查询生成信息看板XML的信息
selectForXML.Type=TSQL
selectForXML.SQL=SELECT A.DEPT_CODE,A.STATION_CODE,A.BED_NO,B.ROOM_CODE,A.MR_NO,A.IPD_NO,C.PAT_NAME, &
			D.CHN_DESC AS SEX,C.BIRTH_DATE,A.IN_DATE,E.USER_NAME AS DIRECTOR_DR_CODE,F.USER_NAME AS VS_NURSE_CODE, &
			G.USER_NAME AS ATTEND_DR_CODE,A.NURSING_CLASS,A.DIE_CONDITION,A.CTZ1_CODE,A.PATIENT_STATUS,A.CARE_NUM, &
			A.IO_MEASURE,A.ISOLATION,A.ALLERGY,A.DS_DATE,H.CTZ_DESC,A.TOILET, &
			CASE WHEN B.OCCU_RATE_FLG='Y' THEN '' ELSE '加床' END AS ISADD &
			//增加取消住院标记 wanglong 20140731
            ,A.CANCEL_FLG,C.SEX_CODE,A.HEIGHT,A.WEIGHT,A.DIRECTOR_DR_CODE DIRECTOR_DR,A.VS_DR_CODE,A.VS_NURSE_CODE NURSE_CODE &
			FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DICTIONARY D,SYS_OPERATOR E, &
			SYS_OPERATOR F,SYS_OPERATOR G,SYS_CTZ H &
			WHERE A.BED_NO=B.BED_NO(+) &
			AND A.MR_NO=C.MR_NO &
			AND D.GROUP_ID='SYS_SEX' &
			AND C.SEX_CODE=D.ID &
			AND A.DIRECTOR_DR_CODE = E.USER_ID(+) &
			AND A.VS_NURSE_CODE=F.USER_ID(+) &
//			AND A.ATTEND_DR_CODE=G.USER_ID(+) &
			//改为使用经治医生 wanglong 20140731
			AND A.VS_DR_CODE=G.USER_ID(+) &
			AND A.CTZ1_CODE=H.CTZ_CODE
selectForXML.item=CASE_NO
selectForXML.CASE_NO=A.CASE_NO=<CASE_NO>
selectForXML.Debug=N

//查询在院病患信息
selectInHosp.Type=TSQL
selectInHosp.SQL=SELECT CASE_NO, IPD_NO, MR_NO, DEPT_CODE, STATION_CODE,&
		BED_NO,CTZ1_CODE, CTZ2_CODE, CTZ3_CODE, ADM_SOURCE,&
 		IN_DATE, IN_DEPT_CODE, IN_STATION_CODE,DS_DATE, DS_DEPT_CODE,&
 		DS_STATION_CODE, DRG_CODE, DISCH_CODE, TRANS_HOSP,INVISIBLE_FLG,&
 		BORN_FLG, ACCIDENT_FLG, DEAD48_FLG, NOTIFY_FLG, CANCEL_FLG,&
 		SEPA_FLG,CLINICAL_FLG, NEW_BORN_FLG, M_CASE_NO,MERGE_O_CASE_NO,&
 		MERGE_I_CASE_NO, OPD_DR_CODE, VS_DR_CODE, ATTEND_DR_CODE,DIRECTOR_DR_CODE,&
 		ADM_DAYS, VS_NURSE_CODE, PRESS_DATE,ADM_DATE,ADM_CLERK, &
 		ADM_REMK,REGION_CODE,MAINDIAG,MEDDISCH_DATE,BILL_DATE,&
 		CHARGE_DATE,RED_SIGN,YELLOW_SIGN,STOP_BILL_FLG,CONFIRM_NO,&
 		PATIENT_STATUS,NURSING_CLASS,PATIENT_CONDITION,DIE_CONDITION,CARE_NUM,TOTAL_AMT,TOTAL_BILPAY,&
 		CUR_AMT,GREENPATH_VALUE,WEIGHT,HEIGHT,CLNCPATH_CODE,&
 		OPT_USER,OPT_DATE,OPT_TERM,IN_COUNT,PAY_INS,BILL_STATUS, &
		IO_MEASURE,ISOLATION,ALLERGY,SERVICE_LEVEL,TOILET &
 		FROM ADM_INP &
		WHERE DS_DATE IS NULL &
		AND (CANCEL_FLG <> 'Y' OR CANCEL_FLG IS NULL) 
selectInHosp.item=IPD_NO;MR_NO;CASE_NO;BED_NO
selectInHosp.IPD_NO=IPD_NO=<IPD_NO>
selectInHosp.MR_NO=MR_NO=<MR_NO>
selectInHosp.CASE_NO=CASE_NO=<CASE_NO>
selectInHosp.BED_NO=BED_NO=<BED_NO>
selectInHosp.Debug=N

//记录上次出院日期
updateLAST_DS_DATE.Type=TSQL
updateLAST_DS_DATE.SQL=UPDATE ADM_INP SET LAST_DS_DATE=DS_DATE WHERE CASE_NO=<CASE_NO>
updateLAST_DS_DATE.Debug=N

//修改资格确认书编号
updateINPConfirmNo.Type=TSQL
updateINPConfirmNo.SQL=UPDATE  ADM_INP SET &
	CONFIRM_NO=<CONFIRM_NO>,CTZ1_CODE=<CTZ1_CODE> &
	WHERE CASE_NO=<CASE_NO>
updateINPConfirmNo.Debug=N

//查询ADM_INP数据(医保资格确认书下载开立操作)
//预约单号/病案号/姓名/预约日期/预约住院日/身分证/科别/主要诊断
queryAdmNClose.Type=TSQL
queryAdmNClose.SQL= SELECT F.REGION_CODE,A.RESV_NO,A.MR_NO,B.PAT_NAME,A.APP_DATE,A.RESV_DATE,&
                      A.URG_FLG,A.CAN_DATE,F.IN_DATE,F.ADM_SOURCE,B.IDNO,& 
                      A.BED_CLASS_CODE,A.BED_NO,F.TRANS_HOSP,F.PATIENT_CONDITION,&
                      A.DEPT_CODE,&
                      A.DR_CODE,A.ADM_DAYS,&
                      A.DIAG_CODE,D.ICD_CHN_DESC,&
                      A.OPER_DATE,A.OPER_DESC,A.TEL,&
                      A.TEL_NO1,A.CAN_REASON_CODE,A.CAN_CLERK,A.IN_CASE_NO AS CASE_NO,B.BIRTH_DATE, &
                      A.CTZ1_CODE &
                      FROM ADM_RESV A,SYS_PATINFO B, SYS_DIAGNOSIS D,ADM_INP F &
                      WHERE B.MR_NO=A.MR_NO AND &
                      F.IN_DATE BETWEEN TO_DATE(<START_DATE>, 'YYYYMMDDHH24MISS') &
		      	AND TO_DATE(<END_DATE>, 'YYYYMMDDHH24MISS') AND &
                     F.CASE_NO=A.IN_CASE_NO &
                      AND F.DS_DATE IS NULL &
                      AND F.CANCEL_FLG = 'N'  AND & 
                      A.DIAG_CODE=D.ICD_CODE(+) &
                      ORDER BY A.URG_FLG,A.RESV_NO 
queryAdmNClose.item=MR_NO;DEPT_CODE;REGION_CODE;STATION_CODE
queryAdmNClose.MR_NO=F.MR_NO=<MR_NO>
queryAdmNClose.DEPT_CODE=F.DEPT_CODE=<DEPT_CODE>
queryAdmNClose.STATION_CODE=F.STATION_CODE=<STATION_CODE>
queryAdmNClose.REGION_CODE=F.REGION_CODE=<REGION_CODE>
queryAdmNClose.Debug=N

//费用分割查询病患信息确定唯一数据
//预约单号/病案号/姓名/预约日期/预约住院日/身分证/科别/主要诊断
//========pangben 2012-6-18
queryAdmNCloseInsBalance.Type=TSQL
queryAdmNCloseInsBalance.SQL= SELECT F.REGION_CODE,A.RESV_NO,A.MR_NO,B.PAT_NAME,A.APP_DATE,A.RESV_DATE,&
                      A.URG_FLG,A.CAN_DATE,F.IN_DATE,F.ADM_SOURCE,B.IDNO,& 
                      A.BED_CLASS_CODE,A.BED_NO,F.TRANS_HOSP,F.PATIENT_CONDITION,&
                      A.DEPT_CODE,&
                      A.DR_CODE,A.ADM_DAYS,&
                      A.DIAG_CODE,D.ICD_CHN_DESC,&
                      A.OPER_DATE,A.OPER_DESC,A.TEL,&
                      A.TEL_NO1,A.CAN_REASON_CODE,A.CAN_CLERK,A.IN_CASE_NO AS CASE_NO,B.BIRTH_DATE, &
                      A.CTZ1_CODE &
                      FROM ADM_RESV A,SYS_PATINFO B, SYS_DIAGNOSIS D,ADM_INP F &
                      WHERE B.MR_NO=A.MR_NO AND &
                     F.CASE_NO=A.IN_CASE_NO &
//                      AND F.DS_DATE IS NOT NULL &
                      AND F.CANCEL_FLG = 'N'  AND & 
                      A.DIAG_CODE=D.ICD_CODE(+) &
                      ORDER BY A.URG_FLG,A.RESV_NO 
queryAdmNCloseInsBalance.item=MR_NO;DEPT_CODE;REGION_CODE;STATION_CODE
queryAdmNCloseInsBalance.MR_NO=F.MR_NO=<MR_NO>
queryAdmNCloseInsBalance.DEPT_CODE=F.DEPT_CODE=<DEPT_CODE>
queryAdmNCloseInsBalance.STATION_CODE=F.STATION_CODE=<STATION_CODE>
queryAdmNCloseInsBalance.REGION_CODE=F.REGION_CODE=<REGION_CODE>
queryAdmNCloseInsBalance.Debug=N

//插入诊断表
insertMRODiag.Type=TSQL
insertMRODiag.SQL=INSERT INTO MRO_RECORD_DIAG (CASE_NO,MR_NO,IPD_NO, &
                      IO_TYPE,ICD_KIND, MAIN_FLG,ICD_CODE,ICD_DESC,SEQ_NO,ICD_REMARK,ICD_STATUS,ADDITIONAL_CODE,ADDITIONAL_DESC,IN_PAT_CONDITION, OPT_USER, &
                      OPT_DATE, OPT_TERM &
                     ) &
              VALUES (<CASE_NO>, <MR_NO>, <IPD_NO>, & 
                      <IO_TYPE>,<ICD_KIND>, <MAIN_FLG>,<ICD_CODE>,<ICD_DESC>,<SEQ_NO>,<ICD_REMARK>,<ICD_STATUS>,<ADDITIONAL_CODE>,<ADDITIONAL_DESC>,<IN_PAT_CONDITION>,<OPT_USER>, &
                      SYSDATE, <OPT_TERM> &
                     )
insertMRODiag.Debug=N

//更新入组项目信息
updatePatPro.Type=TSQL
updatePatPro.SQL=UPDATE ADM_INP SET PATLOGY_PRO_CODE=<PATLOGY_PRO_CODE>,PATLOGY_DEPT_CODE=<PATLOGY_DEPT_CODE>, PATLOGY_DOC_CODE=<PATLOGY_DOC_CODE>,PATLOGY_PRO_DATE=<PATLOGY_PRO_DATE>, PATLOGY_PRO_REMARK=<PATLOGY_PRO_REMARK>  &
		  WHERE MR_NO=<MR_NO> AND DEPT_CODE=<DEPT_CODE>  AND STATION_CODE=<STATION_CODE>
updatePatPro.Debug=Y