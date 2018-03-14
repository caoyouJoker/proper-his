# 
#  Title:挂号方式module
# 
#  Description:挂号方式module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsQueMethod;getqueGropCombo;getqueGropNotVipCombo;updatQueNo;updateArriveTime;&
	    seldataForTable

//查询给号方式
queryTree.Type=TSQL
queryTree.SQL=SELECT QUEGROUP_CODE,QUE_NO,VISIT_CODE,APPT_CODE,REGMETHOD_CODE,&
		     START_TIME,OPT_USER,OPT_DATE,OPT_TERM &
		FROM REG_QUEMETHOD &
	       WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
	         AND QUE_NO=<QUE_NO> &
	    ORDER BY QUEGROUP_CODE,QUE_NO
queryTree.Debug=N


//查询给号组别,就诊号,初复诊,当诊预约,挂号方式,到院时间,操作人员,操作日期,操作终端
selectdata.Type=TSQL
selectdata.SQL=SELECT QUEGROUP_CODE,QUE_NO,VISIT_CODE,APPT_CODE,REGMETHOD_CODE,&
		      START_TIME,OPT_USER,OPT_DATE,OPT_TERM &
		 FROM REG_QUEMETHOD &
	     ORDER BY QUEGROUP_CODE,QUE_NO
selectdata.item=QUEGROUP_CODE;QUE_NO
selectdata.QUEGROUP_CODE=QUEGROUP_CODE=<QUEGROUP_CODE>
selectdata.QUE_NO=QUE_NO=<QUE_NO>
selectdata.Debug=N

//删除给号组别,就诊号,初复诊,当诊预约,挂号方式,到院时间,操作人员,操作日期,操作终端
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_QUEMETHOD &
		     WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
		       AND QUE_NO=<QUE_NO>
deletedata.Debug=N

//新增给号组别,就诊号,初复诊,当诊预约,挂号方式,到院时间,操作人员,操作日期,操作终端
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_QUEMETHOD &
			   (QUEGROUP_CODE,QUE_NO,VISIT_CODE,APPT_CODE,REGMETHOD_CODE,&
			   START_TIME,OPT_USER,OPT_DATE,OPT_TERM) &
		    VALUES (<QUEGROUP_CODE>,<QUE_NO>,<VISIT_CODE>,<APPT_CODE>,<REGMETHOD_CODE>,&
		           <START_TIME>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N

//更新给号组别,就诊号,初复诊,当诊预约,挂号方式,到院时间,操作人员,操作日期,操作终端
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_QUEMETHOD &
		  SET QUEGROUP_CODE=<QUEGROUP_CODE>,QUE_NO=<QUE_NO>,VISIT_CODE=<VISIT_CODE>,&
		      APPT_CODE=<APPT_CODE>,REGMETHOD_CODE=<REGMETHOD_CODE>,START_TIME=<START_TIME>,&
		      OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
                  AND QUE_NO=<QUE_NO>
updatedata.Debug=N

//是否存在给号组别
existsQueMethod.type=TSQL
existsQueMethod.SQL=SELECT COUNT(*) AS COUNT &
		      FROM REG_QUEMETHOD &
		     WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
		       AND QUE_NO=<QUE_NO>

//取得给号组别combo信息
getqueGropCombo.Type=TSQL
getqueGropCombo.SQL=SELECT QUEGROUP_CODE AS ID,QUEGROUP_DESC AS NAME,PY1,PY2 &
		      FROM REG_QUEGROUP &
		  ORDER BY QUEGROUP_CODE
getqueGropCombo.Debug=N

//取得给号组别 普通诊 combo信息
getqueGropNotVipCombo.Type=TSQL
getqueGropNotVipCombo.SQL=SELECT QUEGROUP_CODE AS ID,QUEGROUP_DESC AS NAME,PY1,PY2 &
			    FROM REG_QUEGROUP &
			   WHERE VIP_FLG='N' &
			ORDER BY QUEGROUP_CODE
getqueGropNotVipCombo.Debug=N

//更新就诊号
updatQueNo.Type=TSQL
updatQueNo.SQL=UPDATE REG_QUEMETHOD &
		  SET VISIT_CODE=<VISIT_CODE>,APPT_CODE=<APPT_CODE>,REGMETHOD_CODE=<REGMETHOD_CODE>,&
		      OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
                  AND QUE_NO=<QUE_NO>
updatQueNo.Debug=N

//更新到院时间
updateArriveTime.Type=TSQL
updateArriveTime.SQL=UPDATE REG_QUEMETHOD &
			SET START_TIME=<START_TIME>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,&
			    OPT_TERM=<OPT_TERM> &
		      WHERE QUEGROUP_CODE = <QUEGROUP_CODE> &
		      	AND QUE_NO=<QUE_NO>
updateArriveTime.Debug=N

//刷新table用查询
seldataForTable.Type=TSQL
seldataForTable.SQL=SELECT QUEGROUP_CODE,QUE_NO,VISIT_CODE,APPT_CODE,REGMETHOD_CODE,&
			   START_TIME,OPT_USER,OPT_DATE,OPT_TERM &
		      FROM REG_QUEMETHOD &
		  ORDER BY QUEGROUP_CODE,QUE_NO
seldataForTable.item=QUEGROUP_CODE
seldataForTable.QUEGROUP_CODE=QUEGROUP_CODE=<QUEGROUP_CODE>
seldataForTable.Debug=N
