# 
#  Title:医疗卡绿色通道module
# 
#  Description:医疗卡绿色通道module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author pangben 20111008
#  version 1.0
#
Module.item=selectdata;selectGreenPath;deletedata;insertdata;updatedata;existsPatMethod;selPatEktGreen

//查询绿色通道全部字段
selectdata.Type=TSQL
selectdata.SQL=SELECT CASE_NO,MR_NO,ADM_TYPE,&

		      TO_DATE(APPLY_DATE,'YYYYMMDDHH24MISS') AS APPLY_DATE,APPLY_AMT,APPLY_USER,APPLY_CAUSE,&
		      
		      APPROVE_DATE,APPROVE_USER, DESCRIPTION,APPROVE_AMT,&
		      
		      CANCLE_FLG,OPT_DATE,OPT_TERM &
		      
		      FROM EKT_GREEN_PATH &
		      
		      ORDER BY CASE_NO
selectdata.item=CASE_NO;MR_NO;ADM_TYPE
selectdata.CASE_NO=CASE_NO=<CASE_NO>
selectdata.MR_NO=MR_NO=<MR_NO>
selectdata.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectdata.Debug=N

//绿色通道查询
selectGreenPath.Type=TSQL
selectGreenPath.SQL=SELECT A.CASE_NO,A.MR_NO,B.PAT_NAME,&

		      A.ADM_TYPE,TO_DATE(APPLY_DATE,'YYYYMMDDHH24MISS') AS APPLY_DATE,A.APPLY_AMT,A.APPLY_USER,&
		      
		      A.APPLY_CAUSE,A.APPROVE_DATE,A.APPROVE_USER,A.DESCRIPTION,&
		      
		      A.APPROVE_AMT,A.CANCLE_FLG,A.OPT_USER,A.OPT_DATE,A.OPT_TERM &
		      
		      FROM EKT_GREEN_PATH A,SYS_PATINFO B &
		      
		      WHERE A.MR_NO=B.MR_NO
		      
		      ORDER BY A.CASE_NO
selectGreenPath.item=CASE_NO;MR_NO;ADM_TYPE
 selectGreenPath.CASE_NO=A.CASE_NO=<CASE_NO>
 selectGreenPath.MR_NO=A.MR_NO=<MR_NO>
 selectGreenPath.ADM_TYPE=A.ADM_TYPE=<ADM_TYPE>
selectGreenPath.Debug=N

//删除绿色通道
deletedata.Type=TSQL
deletedata.SQL=UPDATE EKT_GREEN_PATH SET CANCLE_FLG='Y' WHERE CASE_NO = <CASE_NO>
deletedata.item=APPLY_DATE
deletedata.APPLY_DATE=APPLY_DATE=<APPLY_DATE>
deletedata.Debug=N

//新增绿色通道
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO EKT_GREEN_PATH &

		    (CASE_NO,APPLY_DATE,MR_NO,&
		    
		    ADM_TYPE,APPLY_AMT,APPLY_USER,APPLY_CAUSE,&
		    
		    APPROVE_DATE,APPROVE_USER,DESCRIPTION,APPROVE_AMT,&
		    
		    CANCLE_FLG,OPT_USER,OPT_DATE,OPT_TERM) &
		    
		    VALUES&
		    
		    (<CASE_NO>,TO_CHAR(<APPLY_DATE>,'YYYYMMDDHH24MISS'),<MR_NO>,&
		    
		    <ADM_TYPE>,<APPLY_AMT>,<APPLY_USER>,<APPLY_CAUSE>,&
		    
		    <APPROVE_DATE>,<APPROVE_USER>,<DESCRIPTION>,<APPROVE_AMT>,&
		    
		    <CANCLE_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N

//更新绿色通道
updatedata.Type=TSQL
updatedata.SQL=UPDATE EKT_GREEN_PATH SET &

		APPLY_AMT=<APPLY_AMT>,APPLY_USER=<APPLY_USER>,APPLY_CAUSE=<APPLY_CAUSE>,&
		
		APPROVE_DATE=<APPROVE_DATE>,APPROVE_USER=<APPROVE_USER>,APPROVE_AMT=<APPROVE_AMT>,&
		
		DESCRIPTION=<DESCRIPTION>,CANCLE_FLG=<CANCLE_FLG>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		
		WHERE CASE_NO=<CASE_NO> &
		
		AND APPLY_DATE=TO_CHAR(<APPLY_DATE>,'YYYYMMDDHH24MISS')
updatedata.Debug=N

//查询是否存在相同病患
existsPatMethod.type=TSQL
existsPatMethod.SQL=SELECT COUNT(*) AS COUNT FROM EKT_GREEN_PATH WHERE CASE_NO=<CASE_NO> AND APPLY_DATE=TO_CHAR(<APPLY_DATE>,'YYYYMMDDHH24MISS')
existsPatMethod.Debug=N

//查询此就诊病患是否存在绿色通道
selPatEktGreen.type=TSQL
selPatEktGreen.SQL=SELECT COUNT(*) AS COUNT FROM EKT_GREEN_PATH WHERE CASE_NO=<CASE_NO> AND CANCLE_FLG='N'
selPatEktGreen.Debug=N