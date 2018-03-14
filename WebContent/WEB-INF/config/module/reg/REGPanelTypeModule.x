# 
#  Title:号别module
# 
#  Description:号别module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsClinicType;initclinictype;getDescByCode;selProfFlg

//查询号别
queryTree.Type=TSQL
queryTree.SQL=SELECT ADM_TYPE,CLINICTYPE_CODE,CLINICTYPE_DESC,PY1,PY2,&
		     SEQ,DESCRIPTION,PROF_FLG,OPT_USER,OPT_DATE,&
		     OPT_TERM &
		FROM REG_CLINICTYPE &
	    ORDER BY CLINICTYPE_CODE,SEQ
queryTree.Debug=N


//查询门急诊,号别,号别说明,拼音码,注记码,顺序编号,备注,专家诊注记,操作人员,操作日期,操作终端
selectdata.Type=TSQL
selectdata.SQL=SELECT ADM_TYPE,CLINICTYPE_CODE,CLINICTYPE_DESC,PY1,PY2,&
		      SEQ,DESCRIPTION,PROF_FLG,OPT_USER,OPT_DATE,&
		      OPT_TERM &
		 FROM REG_CLINICTYPE &
	     ORDER BY SEQ
selectdata.item=ADM_TYPE;CLINICTYPE_CODE
selectdata.ADM_TYPE=ADM_TYPE=<ADM_TYPE>
selectdata.CLINICTYPE_CODE=CLINICTYPE_CODE=<CLINICTYPE_CODE>
selectdata.Debug=N

//删除门急诊,号别,号别说明,拼音码,注记码,顺序编号,备注,专家诊注记,操作人员,操作日期,操作终端
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_CLINICTYPE &
		WHERE ADM_TYPE = <ADM_TYPE> &
		  AND CLINICTYPE_CODE = <CLINICTYPE_CODE>
deletedata.Debug=N

//新增门急诊,号别,号别说明,拼音码,注记码,顺序编号,备注,专家诊注记,操作人员,操作日期,操作终端
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_CLINICTYPE &
			   (ADM_TYPE,CLINICTYPE_CODE,CLINICTYPE_DESC, PY1,PY2,&
			   SEQ,DESCRIPTION,PROF_FLG,OPT_USER,OPT_DATE,&
			   OPT_TERM) &
		    VALUES (<ADM_TYPE>,<CLINICTYPE_CODE>,<CLINICTYPE_DESC>,<PY1>,<PY2>,&
		    	   <SEQ>,<DESCRIPTION>,<PROF_FLG>,<OPT_USER>,SYSDATE,&
		    	   <OPT_TERM>)
insertdata.Debug=N

//更新门急诊,号别,号别说明,拼音码,注记码,顺序编号,备注,专家诊注记,操作人员,操作日期,操作终端
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_CLINICTYPE &
		  SET ADM_TYPE=<ADM_TYPE>,CLINICTYPE_CODE=<CLINICTYPE_CODE>,CLINICTYPE_DESC=<CLINICTYPE_DESC>,&
		      PY1=<PY1>,PY2=<PY2>,SEQ=<SEQ>,&
		      DESCRIPTION=<DESCRIPTION>,PROF_FLG=<PROF_FLG>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
                WHERE ADM_TYPE = <ADM_TYPE> &
                  AND CLINICTYPE_CODE = <CLINICTYPE_CODE>
updatedata.Debug=N

//是否存在号别
existsClinicType.type=TSQL
existsClinicType.SQL=SELECT COUNT(*) AS COUNT &
		       FROM REG_CLINICTYPE &
		      WHERE ADM_TYPE = <ADM_TYPE> &
		        AND CLINICTYPE_CODE = <CLINICTYPE_CODE>

//得到号别名称
initclinictype.Type=TSQL
initclinictype.SQL=SELECT CLINICTYPE_CODE AS ID,CLINICTYPE_DESC AS NAME ,ENNAME,PY1,PY2 &
		     FROM REG_CLINICTYPE &
		 ORDER BY CLINICTYPE_CODE,SEQ
initclinictype.Debug=N

//根据号别CODE取得号别DESC
getDescByCode.Type=TSQL
getDescByCode.SQL=SELECT CLINICTYPE_DESC &
		    FROM REG_CLINICTYPE &
		   WHERE CLINICTYPE_CODE=<CLINICTYPE_CODE>
getDescByCode.Debug=N

//根据号别CODE取得号别DESC
selProfFlg.Type=TSQL
selProfFlg.SQL=SELECT PROF_FLG &
		    FROM REG_CLINICTYPE &
		   WHERE CLINICTYPE_CODE=<CLINICTYPE_CODE>
selProfFlg.Debug=N