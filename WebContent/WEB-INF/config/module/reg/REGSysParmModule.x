# 
#  Title:挂号参数module
# 
#  Description:挂号参数module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;updatedata;selPayWay;selVisitCode;insertdata;selPayWay;selVisitCode;selEffectDays;selOthHospRegFlg;selTriageFlg

//查询报到注记，退挂不占号，跨院区挂号，急诊需检伤,当诊给连续号,默认的支付方式,默认初复诊,挂号收据有效天数
selectdata.Type=TSQL
selectdata.SQL=SELECT CHECKIN_FLG,QUEREUSE_FLG,OTHHOSP_REG_FLG,TRIAGE_FLG,APPTCONTI_FLG,&
		      EFFECT_DAYS,DEFAULT_PAY_WAY,DEFAULT_VISIT_CODE, &
		      Q_O_EFFECT_DAYS,Q_E_EFFECT_DAYS,MT_CLINIC_FEE_CODE &
		 FROM REG_SYSPARM
selectdata.Debug=N



//更新报到注记，退挂不占号，跨院区挂号，急诊需检伤,当诊给连续号,默认的支付方式,默认初复诊,挂号收据有效天数,操作人员，操作日期，操作终端
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_SYSPARM &
		  SET CHECKIN_FLG=<CHECKIN_FLG>,QUEREUSE_FLG=<QUEREUSE_FLG>,OTHHOSP_REG_FLG=<OTHHOSP_REG_FLG>,&
		      TRIAGE_FLG=<TRIAGE_FLG>,APPTCONTI_FLG=<APPTCONTI_FLG>,EFFECT_DAYS=<EFFECT_DAYS>,&
		      DEFAULT_PAY_WAY=<DEFAULT_PAY_WAY>,DEFAULT_VISIT_CODE=<DEFAULT_VISIT_CODE>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,Q_E_EFFECT_DAYS=<Q_E_EFFECT_DAYS>,Q_O_EFFECT_DAYS=<Q_O_EFFECT_DAYS>,&
		      MT_CLINIC_FEE_CODE=<MT_CLINIC_FEE_CODE>
updatedata.Debug=N

//新增报到注记，退挂不占号，跨院区挂号，急诊需检伤,当诊给连续号,默认的支付方式,默认初复诊,挂号收据有效天数,操作人员，操作日期，操作终端
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_SYSPARM &
		           (CHECKIN_FLG,QUEREUSE_FLG,OTHHOSP_REG_FLG,TRIAGE_FLG,APPTCONTI_FLG,&
		           EFFECT_DAYS,DEFAULT_PAY_WAY,DEFAULT_VISIT_CODE,OPT_USER,OPT_DATE,&
		           OPT_TERM,Q_O_EFFECT_DAYS,Q_E_EFFECT_DAYS,MT_CLINIC_FEE_CODE) &
		    VALUES (<CHECKIN_FLG>,<QUEREUSE_FLG>,<OTHHOSP_REG_FLG>,<TRIAGE_FLG>,<APPTCONTI_FLG>,&
		    	   <EFFECT_DAYS>,<DEFAULT_PAY_WAY>,<DEFAULT_VISIT_CODE>,<OPT_USER>,SYSDATE,&
		    	   <OPT_TERM>,<Q_O_EFFECT_DAYS>,<Q_E_EFFECT_DAYS>,<MT_CLINIC_FEE_CODE>)
insertdata.Debug=N


//初始化支付方式
selPayWay.Type=TSQL
selPayWay.SQL=SELECT DEFAULT_PAY_WAY &
		FROM REG_SYSPARM
selPayWay.Debug=N

//初始化初复诊
selVisitCode.Type=TSQL
selVisitCode.SQL=SELECT DEFAULT_VISIT_CODE &
		   FROM REG_SYSPARM
selVisitCode.Debug=N

//查询挂号有效天数
selEffectDays.Type=TSQL
selEffectDays.SQL=SELECT EFFECT_DAYS &
		    FROM REG_SYSPARM
selEffectDays.Debug=N

//查询是否可以跨院区挂号
selOthHospRegFlg.Type=TSQL
selOthHospRegFlg.SQL=SELECT OTHHOSP_REG_FLG &
		       FROM REG_SYSPARM
selOthHospRegFlg.Debug=N

//查询急诊检伤标记
selTriageFlg.Type=TSQL
selTriageFlg.SQL=SELECT TRIAGE_FLG &
		       FROM REG_SYSPARM
selTriageFlg.Debug=N