# 
#  Title:会诊申请module
# 
#  Description:会诊申请module
# 
#  Copyright: Copyright (c) Javahis 2013
# 
#  author caoyong 2013912
#  version 1.0
#


Module.item=selectall;selectdata;deletedata;insertdata;updatedata;inserBoard;inserPostRCV;selectdatacc;selectdatass;updateEMail;selectdMess;selectdCons

//查询
selectdata.Type=TSQL
selectdata.SQL=SELECT  URGENT_FLG,KIND_CODE,CONS_CODE,CONS_DATE,DR_DEPT,DR_CODE, &
                       ACCEPT_DEPT_CODE,ACCEPT_DR_CODE,RECIPIENT_DATE, &
		       REAL_DR_CODE,REPORT_DATE,REPORT_DR_CODE,CONCEL_CAUSE_CODE,ASSEPT_DEPT_CODE,ASSIGN_DR_CODE &
                       FROM &
		       INP_CONS &
		       WHERE & 
		       CONS_CODE = <CONS_CODE> 
selectdata.Debug=N


//查询被会科室医生
selectdatacc.Type=TSQL
selectdatacc.SQL=SELECT DR_CODE1,DR_CODE2,DR_CODE3,DR_CODE4,DR_CODE5,DR_CODE6,DEPT_CODE &
                       FROM INP_SCHDAY &
		       WHERE & 
		       DEPT_CODE = <DEPT_CODE> 
selectdatacc.Debug=N
//查询被指定会科室医生

selectdatass.Type=TSQL
selectdatass.SQL=SELECT ASSIGN_DR_CODE ,ASSEPT_DEPT_CODE &
                       FROM  &
		       INP_CONS &
		       WHERE & 
		       CONS_CODE = <CONS_CODE> 
selectdatass.Debug=N
//插入公告栏
inserBoard.Type=TSQL
inserBoard.SQL=INSERT INTO SYS_BOARD &
                          (MESSAGE_NO,POST_SUBJECT,URG_FLG,POST_INFOMATION,RESPONSE_NO, &
			   POST_ID,POST_TIME,OPT_USER,OPT_DATE,OPT_TERM ) &
                           VALUES &
			  (<MESSAGE_NO>,<POST_SUBJECT>,<URG_FLG>,<POST_INFOMATION>,<RESPONSE_NO>, &
			   <POST_ID>,<POST_TIME>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
inserBoard.Debug=N
//插入接收档
inserPostRCV.Type=TSQL
inserPostRCV.SQL=INSERT INTO &
                 SYS_POSTRCV &
                 (MESSAGE_NO,POST_TYPE,POST_GROUP,USER_ID,READ_FLG, &
		 OPT_USER,OPT_DATE,OPT_TERM) &
		 VALUES &
		 (<MESSAGE_NO>,<POST_TYPE>,<POST_GROUP>,<USER_ID>,<READ_FLG>, &
		 <OPT_USER>,<OPT_DATE>,<OPT_TERM>)
inserPostRCV.Debug=N
//删除
deletedata.Type=TSQL
deletedata.SQL=DELETE INP_CONS  WHERE CONS_CODE = <CONS_CODE> 
deletedata.Debug=N

//更新
updatedata.Type=TSQL
updatedata.SQL=UPDATE INP_CONS &
                      SET KIND_CODE=<KIND_CODE>,CONS_DATE=<CONS_DATE>,ACCEPT_DEPT_CODE=<ACCEPT_DEPT_CODE> , &
		      DR_DEPT=<DR_DEPT>,DR_CODE=<DR_CODE>,DR_TEL=<DR_TEL>, &
		      ACCEPT_DR_CODE=<ACCEPT_DR_CODE> ,ACCEPT_DR_TEL=<ACCEPT_DR_TEL>, &
		      ASSIGN_DR_CODE =<ASSIGN_DR_CODE>, ASSIGN_DR_TEL=<ASSIGN_DR_TEL> , &
		      URGENT_FLG=<URGENT_FLG>,ASSEPT_DEPT_CODE=<ASSEPT_DEPT_CODE>,ADM_TYPE=<ADM_TYPE>, &
		      OPT_USER =<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>  &
		      WHERE  &
		      CONS_CODE = <CONS_CODE> 
updatedata.Debug=N

//添加
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO INP_CONS ( &
                                      CONS_CODE,KIND_CODE,CONS_DATE,DR_DEPT,DR_CODE,DR_TEL,  &
                                      ACCEPT_DEPT_CODE,ACCEPT_DR_CODE,ACCEPT_DR_TEL,ASSIGN_DR_CODE,ASSIGN_DR_TEL, &
				      CASE_NO,REGION_CODE,URGENT_FLG,ASSEPT_DEPT_CODE,ADM_TYPE,OPT_DATE,OPT_TERM,OPT_USER) &
    	                              VALUES  &
				     (<CONS_CODE>,<KIND_CODE>,<CONS_DATE>,<DR_DEPT>,<DR_CODE>,<DR_TEL>, &
				      <ACCEPT_DEPT_CODE>,<ACCEPT_DR_CODE>,<ACCEPT_DR_TEL>,<ASSIGN_DR_CODE>,<ASSIGN_DR_TEL>, &
                                      <CASE_NO>,<REGION_CODE>,<URGENT_FLG>,<ASSEPT_DEPT_CODE>,<ADM_TYPE>,SYSDATE,<OPT_TERM>,<OPT_USER>)
insertdata.Debug=N

//查询所有
selectall.Type=TSQL
selectall.SQL=SELECT   URGENT_FLG,KIND_CODE,CONS_CODE,CONS_DATE,DR_DEPT,DR_CODE, &
                       ACCEPT_DEPT_CODE,ACCEPT_DR_CODE,RECIPIENT_DATE, &
		       REAL_DR_CODE,REPORT_DATE,REPORT_DR_CODE,CONCEL_CAUSE_CODE,ASSEPT_DEPT_CODE,ASSIGN_DR_CODE, &
		       DR_TEL,ACCEPT_DR_TEL,ASSIGN_DR_TEL &
                       FROM &
		       INP_CONS &
		       WHERE & 
		       CASE_NO = <CASE_NO> &
		       ORDER BY CONS_DATE DESC
selectall.Debug=N


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

//查询背会值班医生电话
selectdMess.Type=TSQL
selectdMess.SQL=SELECT DR_TEL1,DR_TEL2,DR_TEL3,DR_TEL4,DR_TEL5,DR_TEL6 & 
		FROM &
		INP_SCHDAY &
		WHERE & 
		DEPT_CODE = <DEPT_CODE> 
selectdMess.Debug=N

//查询会诊是否已完成
selectdCons.Type=TSQL
selectdCons.SQL=SELECT REPORT_FLG FROM INP_CONS &
                 WHERE &
		 CONS_CODE=<CONS_CODE>  AND KIND_CODE=<KIND_CODE> AND CASE_NO=<CASE_NO>
selectdCons.Debug=N