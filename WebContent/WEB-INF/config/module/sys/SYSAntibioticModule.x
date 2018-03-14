# 
#  Title:抗生素等级module
# 
#  Description:抗生素等级module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.24
#  version 1.0
#
Module.item=initSYSAntibioticCode;selectdata

//抗生素等级combo
initSYSAntibioticCode.Type=TSQL
initSYSAntibioticCode.SQL=SELECT ANTIBIOTIC_CODE AS ID,ANTIBIOTIC_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 &
			    FROM SYS_ANTIBIOTIC &
			ORDER BY ANTIBIOTIC_CODE
initSYSAntibioticCode.Debug=N

//取得抗生素等级信息
selectdata.Type=TSQL
selectdata.SQL=SELECT ANTIBIOTIC_CODE, ANTIBIOTIC_DESC, PY1, PY2, DESCRIPTION, &
		      TAKE_DAYS, OPT_USER, MR_CODE, OPT_DATE, OPT_TERM &
		 FROM SYS_ANTIBIOTIC &
		WHERE ANTIBIOTIC_CODE=<ANTIBIOTIC_CODE> &
		  AND ANTIBIOTIC_DESC=<ANTIBIOTIC_DESC> &
		  ORDER BY ANTIBIOTIC_CODE 
selectdata.Debug=N