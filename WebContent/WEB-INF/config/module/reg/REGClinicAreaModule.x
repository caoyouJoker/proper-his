# 
#  Title:诊区module
# 
#  Description:诊区module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.10.08
#  version 1.0
#
Module.item=queryTree;selectdata;deletedata;insertdata;updatedata;existsClinicArea;getclinicAreaCombo

//查询诊区
queryTree.Type=TSQL
queryTree.SQL=SELECT CLINICAREA_CODE,CLINIC_DESC,PY1,PY2,SEQ,&
		     DESCRIPTION,REGION_CODE,OPT_USER,OPT_DATE,OPT_TERM &
		FROM REG_CLINICAREA &
	    ORDER BY CLINICAREA_CODE
queryTree.Debug=N

//查询诊区号,诊区号说明,拼音码,注记码,顺序编号,备注,院区代码,操作人员,操作日期,操作终端
selectdata.Type=TSQL
selectdata.SQL=SELECT CLINICAREA_CODE,CLINIC_DESC,PY1,PY2,SEQ,&
		      DESCRIPTION,REGION_CODE,OPT_USER,OPT_DATE,OPT_TERM &
		 FROM REG_CLINICAREA &
	     ORDER BY CLINICAREA_CODE
selectdata.item=CLINICAREA_CODE
selectdata.CLINICAREA_CODE=CLINICAREA_CODE=<CLINICAREA_CODE>
selectdata.Debug=N

//删除诊区号,诊区号说明,拼音码,注记码,顺序编号,备注,院区代码,操作人员,操作日期,操作终端
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_CLINICAREA &
		     WHERE REG_CLINICAREA=<REG_CLINICAREA>
deletedata.Debug=N

//新增诊区号,诊区号说明,拼音码,注记码,顺序编号,备注,院区代码,操作人员,操作日期,操作终端
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_CLINICAREA &
			   (CLINICAREA_CODE,CLINIC_DESC,PY1,PY2,SEQ,&
			   DESCRIPTION,REGION_CODE,OPT_USER,OPT_DATE,OPT_TERM) &
		    VALUES (<CLINICAREA_CODE>,<CLINIC_DESC>,<PY1>,<PY2>,<SEQ>,&
		    <DESCRIPTION>,<REGION_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertdata.Debug=N              

//更新诊区号,诊区号说明,拼音码,注记码,顺序编号,备注,院区代码,操作人员,操作日期,操作终端
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_CLINICAREA &
		  SET CLINICAREA_CODE=<CLINICAREA_CODE>,CLINIC_DESC=<CLINIC_DESC>,PY1=<PY1>,&
		      PY2=<PY2>,SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,&
		      REGION_CODE=<REGION_CODE>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,&
		      OPT_TERM=<OPT_TERM> &
		WHERE CLINICAREA_CODE=<CLINICAREA_CODE>							
updatedata.Debug=N

//是否存在诊区
existsClinicArea.type=TSQL
existsClinicArea.SQL=SELECT COUNT(*) AS COUNT &
		       FROM REG_CLINICAREA &
		      WHERE CLINICAREA_CODE=<CLINICAREA_CODE>

//得到诊区combo数据
getclinicAreaCombo.Type=TSQL
getclinicAreaCombo.SQL=SELECT CLINICAREA_CODE AS ID,CLINIC_DESC AS NAME ,ENG_DESC AS ENNAME,PY1,PY2 &
			 FROM REG_CLINICAREA &
		     ORDER BY CLINICAREA_CODE,SEQ
getclinicAreaCombo.item=REGION_CODE_ALL
getclinicAreaCombo.REGION_CODE_ALL=REGION_CODE=<REGION_CODE_ALL>
getclinicAreaCombo.Debug=N


