# 
#  Title:病案管理module
# 
#  Description:病案管理module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.05.06
#  version 1.0
#
Module.item=getMroType;getMroChrtvetstd;getMroLend

//审核项目COMBO
getMroType.Type=TSQL
getMroType.SQL=SELECT TYPE_CODE,TYPE_DESC FROM MRO_TYPE ORDER BY TYPE_CODE
getMroType.Debug=N
//病案审核标准
getMroChrtvetstd.Type=TSQL
getMroChrtvetstd.SQL=SELECT EXAMINE_CODE AS ID ,EXAMINE_DESC AS NAME ,SCORE AS TEXT,PY1,PY2 FROM MRO_CHRTVETSTD
getMroChrtvetstd.Item=TYPE_CODE;EXAMINE_CODE
getMroChrtvetstd.TYPE_CODE=TYPE_CODE=<TYPE_CODE>
//add by wanglong 20130909
getMroChrtvetstd.EXAMINE_CODE=EXAMINE_CODE=<EXAMINE_CODE>
getMroChrtvetstd.Debug=N
//病案借阅原因
getMroLend.Type=TSQL
getMroLend.SQL=SELECT LEND_CODE,LEND_DESC FROM MRO_LEND
getMroLend.Debug=N