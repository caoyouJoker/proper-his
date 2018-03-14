# 
#  Title:给号组别module
# 
#  Description:给号组别module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsQueGroup;getMaxQue;getVipFlg;getInfobyClinicType

//查询给号组别
queryTree.Type=TSQL
queryTree.SQL=SELECT QUEGROUP_CODE,QUEGROUP_DESC,PY1,PY2,SEQ,&
		     DESCRIPTION,MAX_QUE,VIP_FLG,OPT_USER,OPT_DATE,&
		     OPT_TERM &
		FROM REG_QUEGROUP &
	    ORDER BY QUEGROUP_CODE,SEQ
queryTree.Debug=N


//查询给号组别,号别说明,拼音码,注记码,顺序编号,备注,最大诊号,VIP注记,操作人员,操作日期,操作终端
selectdata.Type=TSQL
selectdata.SQL=SELECT QUEGROUP_CODE,QUEGROUP_DESC,PY1,PY2,SEQ,&
		      DESCRIPTION,MAX_QUE,VIP_FLG,OPT_USER,OPT_DATE,&
		      OPT_TERM &
		 FROM REG_QUEGROUP &
	     ORDER BY QUEGROUP_CODE,SEQ
selectdata.item=QUEGROUP_CODE
selectdata.QUEGROUP_CODE=QUEGROUP_CODE=<QUEGROUP_CODE>
selectdata.Debug=N

//删除给号组别,号别说明,拼音码,注记码,顺序编号,备注,最大诊号,VIP注记,操作人员,操作日期,操作终端
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_QUEGROUP &
		     WHERE QUEGROUP_CODE = <QUEGROUP_CODE>
deletedata.Debug=N

//新增给号组别,号别说明,拼音码,注记码,顺序编号,备注,最大诊号,VIP注记,操作人员,操作日期,操作终端
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_QUEGROUP &
			   (QUEGROUP_CODE,QUEGROUP_DESC,PY1,PY2,SEQ,&
			   DESCRIPTION,MAX_QUE,VIP_FLG,OPT_USER,OPT_DATE,&
			   OPT_TERM) &
		    VALUES (<QUEGROUP_CODE>,<QUEGROUP_DESC>,<PY1>,<PY2>,<SEQ>,&
			   <DESCRIPTION>,<MAX_QUE>,<VIP_FLG>,<OPT_USER>,SYSDATE,&
			   <OPT_TERM>)
insertdata.Debug=N

//更新给号组别,号别说明,拼音码,注记码,顺序编号,备注,最大诊号,VIP注记,操作人员,操作日期,操作终端
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_QUEGROUP &
		  SET QUEGROUP_CODE=<QUEGROUP_CODE>,QUEGROUP_DESC=<QUEGROUP_DESC>,PY1=<PY1>,&
		      PY2=<PY2>,SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,&
		      MAX_QUE=<MAX_QUE>,VIP_FLG=<VIP_FLG>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                WHERE QUEGROUP_CODE = <QUEGROUP_CODE>
updatedata.Debug=N

//是否存在给号组别
existsQueGroup.type=TSQL
existsQueGroup.SQL=SELECT COUNT(*) AS COUNT &
		     FROM REG_QUEGROUP &
		    WHERE QUEGROUP_CODE = <QUEGROUP_CODE>

//得到最大诊号
getMaxQue.type=TSQL
getMaxQue.SQL=SELECT MAX_QUE &
		FROM REG_QUEGROUP &
	       WHERE QUEGROUP_CODE = <QUEGROUP_CODE>

//得到VIP注记
getVipFlg.type=TSQL
getVipFlg.SQL=SELECT VIP_FLG &
		FROM REG_QUEGROUP &
	       WHERE QUEGROUP_CODE = <QUEGROUP_CODE>

//根据号别查询就诊序号，最大诊号VIP注记，临时诊
getInfobyClinicType.Type=TSQL
getInfobyClinicType.SQL=SELECT MAX_QUE,VIP_FLG &
			  FROM REG_QUEGROUP &
			 WHERE QUEGROUP_CODE=<QUEGROUP_CODE>
getInfobyClinicType.Debug=N

