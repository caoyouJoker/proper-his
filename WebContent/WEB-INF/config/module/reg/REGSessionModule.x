# 
#  Title:挂号时段module
# 
#  Description:挂号时段module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;deletedata;insertdata;updatedata;existsSession;initsessioncode;getdefSession;getSessionCode;getdefSessionNow;getDrSessionNow;getSessionInfoByCode

//查询门急住别,区域,时段代码,时段说明,拼音码,注记码,顺序编号,备注,看诊起时,看诊迄时,挂号起时,挂号迄时,加收时点,加收批价码,操作人员,操作日期,操作端末
//=============pangben modify 20110602 添加医嘱名称列
selectdata.Type=TSQL
selectdata.SQL=SELECT ADM_TYPE,REGION_CODE,SESSION_CODE,SESSION_DESC,PY1,&
		      PY2,SEQ,DESCRIPTION,START_CLINIC_TIME,END_CLINIC_TIME,&
		      START_REG_TIME,END_REG_TIME,VALUEADD_TIME,VALUEADD_CODE,OPT_USER,&
		      OPT_DATE,OPT_TERM,VALUEADD_DESC &
		 FROM REG_SESSION &
	     ORDER BY SESSION_CODE
selectdata.item=ADM_TYPE;REGION_CODE;SESSION_CODE
selectdata.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectdata.REGION_CODE=REGION_CODE=<REGION_CODE>
selectdata.SESSION_CODE=SESSION_CODE=<SESSION_CODE>
selectdata.Debug=N

//删除门急住别,区域,时段代码,时段说明,拼音码,注记码,顺序编号,备注,看诊起时,看诊迄时,挂号起时,挂号迄时,加收时点,加收批价码,操作人员,操作日期,操作端末
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_SESSION &
		WHERE ADM_TYPE= <ADM_TYPE> &
		  AND REGION_CODE= <REGION_CODE> &
		  AND SESSION_CODE= <SESSION_CODE>
deletedata.Debug=N

//新增门急住别,区域,时段代码,时段说明,拼音码,注记码,顺序编号,备注,看诊起时,看诊迄时,挂号起时,挂号迄时,加收时点,加收批价码,操作人员,操作日期,操作端末
//=============pangben modify 20110602 添加医嘱名称列
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_SESSION &
			   (ADM_TYPE,REGION_CODE,SESSION_CODE,SESSION_DESC,PY1,&
			   PY2,SEQ,DESCRIPTION,START_CLINIC_TIME,END_CLINIC_TIME,&
			   START_REG_TIME,END_REG_TIME,VALUEADD_TIME,VALUEADD_CODE,OPT_USER,&
			   OPT_DATE,OPT_TERM,VALUEADD_DESC) &
		    VALUES (<ADM_TYPE>,<REGION_CODE>,<SESSION_CODE>,<SESSION_DESC>,<PY1>,&
		    	   <PY2>,<SEQ>,<DESCRIPTION>,<START_CLINIC_TIME>,<END_CLINIC_TIME>,&
		    	   <START_REG_TIME>,<END_REG_TIME>,<VALUEADD_TIME>,<VALUEADD_CODE>,<OPT_USER>,&
		    	   SYSDATE,<OPT_TERM>,<VALUEADD_DESC>)
insertdata.Debug=N              

//更新门急住别,区域,时段代码,时段说明,拼音码,注记码,顺序编号,备注,看诊起时,看诊迄时,挂号起时,挂号迄时,加收时点,加收批价码,操作人员,操作日期,操作端末
//=============pangben modify 20110602 添加医嘱名称列
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_SESSION &
		  SET ADM_TYPE=<ADM_TYPE>,REGION_CODE=<REGION_CODE>,SESSION_CODE=<SESSION_CODE>,&
		      SESSION_DESC=<SESSION_DESC>,PY1=<PY1>,PY2=<PY2>,&
		      SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,START_CLINIC_TIME=<START_CLINIC_TIME>,&
		      END_CLINIC_TIME=<END_CLINIC_TIME>,START_REG_TIME=<START_REG_TIME>,END_REG_TIME=<END_REG_TIME>,&
		      VALUEADD_TIME=<VALUEADD_TIME>,VALUEADD_CODE=<VALUEADD_CODE>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,VALUEADD_DESC=<VALUEADD_DESC> &
		WHERE ADM_TYPE = <ADM_TYPE> &
		  AND SESSION_CODE=<SESSION_CODE>									
updatedata.Debug=N

//是否存在挂号时段
existsSession.type=TSQL
existsSession.SQL=SELECT COUNT(*) AS COUNT &
		    FROM REG_SESSION &
		   WHERE ADM_TYPE=<ADM_TYPE> &
		     AND REGION_CODE=<REGION_CODE> &
		     AND SESSION_CODE = <SESSION_CODE>

//取得时段combo信息
initsessioncode.Type=TSQL
initsessioncode.SQL=SELECT SESSION_CODE AS ID,SESSION_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 &
		      FROM REG_SESSION &
		  ORDER BY SEQ
initsessioncode.item=ADM_TYPE;REGION_CODE;REGION_CODE_ALL
initsessioncode.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
initsessioncode.REGION_CODE=REGION_CODE=<REGION_CODE>
initsessioncode.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
initsessioncode.Debug=N

//根据门急住别,挂号起始时间查询挂号时段
getdefSessionNow.Type=TSQL
getdefSessionNow.SQL=SELECT SESSION_CODE &
		       FROM REG_SESSION &
		      WHERE ADM_TYPE=<ADM_TYPE> &
			AND (       TO_CHAR (SYSDATE, 'HH24:MI') BETWEEN START_REG_TIME &
								     AND END_REG_TIME &
				AND (START_REG_TIME < END_REG_TIME) &
				OR (    (   TO_CHAR (SYSDATE, 'HH24:MI') BETWEEN '00:00' AND END_REG_TIME &
					OR TO_CHAR (SYSDATE, 'HH24:MI') BETWEEN START_REG_TIME &
									    AND '24:00') &
				AND (START_REG_TIME > END_REG_TIME) ) )
								     
getdefSessionNow.Debug=N

//根据门急住别,挂号起始时间查询挂号时段(For OPD)
getDrSessionNow.Type=TSQL
getDrSessionNow.SQL=SELECT SESSION_CODE &
		      FROM REG_SESSION &
		     WHERE ADM_TYPE=<ADM_TYPE> &
		       AND TO_CHAR(SYSDATE,'HH24:MI') BETWEEN START_CLINIC_TIME &
		       AND END_CLINIC_TIME
getDrSessionNow.item=REGION_CODE
getDrSessionNow.REGION_CODE=REGION_CODE=<REGION_CODE>
getDrSessionNow.Debug=N

//得到时段编号
getSessionCode.Type=TSQL
getSessionCode.SQL=SELECT SESSION_CODE &
		     FROM REG_SESSION &
		    WHERE ADM_TYPE=<ADM_TYPE>
getSessionCode.item=REGION_CODE
getSessionCode.REGION_CODE=REGION_CODE=<REGION_CODE>
getSessionCode.Debug=N

getSessionInfoByCode.Type=TSQL
getSessionInfoByCode.SQL=SELECT  &
				ADM_TYPE, SESSION_CODE, SESSION_DESC,  &
				PY1, PY2, SEQ,  &
				DESCRIPTION, REGION_CODE, START_CLINIC_TIME,  &
				END_CLINIC_TIME, START_REG_TIME, END_REG_TIME,  &
				VALUEADD_TIME, VALUEADD_CODE, OPT_USER,  &
				OPT_DATE, OPT_TERM, ENG_DESC,  &
				ENG_NAME &
				FROM REG_SESSION &
				WHERE ADM_TYPE=<ADM_TYPE> &
				AND SESSION_CODE=<SESSION_CODE>
getSessionInfoByCode.item=REGION_CODE
getSessionInfoByCode.REGION_CODE=REGION_CODE=<REGION_CODE>
getSessionInfoByCode.Debug=N