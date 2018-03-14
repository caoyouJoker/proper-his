# 
#  Title:料位档module
# 
#  Description:料位档module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2009.04.29
#  version 1.0
#
Module.item=initMaterialLocCode

//料位共用combo
initMaterialLocCode.Type=TSQL
initMaterialLocCode.SQL=SELECT MATERIAL_LOC_CODE AS ID,MATERIAL_CHN_DESC AS NAME,ENNAME,PY1,PY2 FROM IND_MATERIALLOC ORDER BY MATERIAL_LOC_CODE,SEQ
initMaterialLocCode.item=ORG_CODE
initMaterialLocCode.ORG_CODE=ORG_CODE=<ORG_CODE>
initMaterialLocCode.Debug=N

