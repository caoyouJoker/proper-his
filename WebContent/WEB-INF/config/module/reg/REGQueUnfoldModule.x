# 
#  Title:VIP增加就诊号module
# 
#  Description:VIP增加就诊号module
# 
#  Copyright: Copyright (c) Javahis 2010
# 
#  author huangtt 20160601
#  version 1.0
#
Module.item=addRegQue


//VIP增加就诊号

addRegQue.Type=TSQL
addRegQue.SQL=INSERT INTO REG_CLINICQUE ( &
		ADM_TYPE,ADM_DATE,SESSION_CODE,CLINICROOM_NO,QUE_NO,&
		QUE_STATUS,VISIT_CODE,APPT_CODE,REGMETHOD_CODE,START_TIME,&
		OPT_USER,OPT_DATE,OPT_TERM,ADD_FLG &
		)VALUES(&
		<ADM_TYPE>,<ADM_DATE>,<SESSION_CODE>,<CLINICROOM_NO>,<QUE_NO>,&
		<QUE_STATUS>,<VISIT_CODE>,<APPT_CODE>,<REGMETHOD_CODE>,&
		<START_TIME>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<ADD_FLG>)
addRegQue.Debug=N

