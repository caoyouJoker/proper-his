##############################################
# <p>Title:查核类别字典</p>
#
# <p>Description:查核类别字典 </p>
#
# <p>Copyright: Copyright (c) 2011</p>
#
# <p>Company:Javahis </p>
#
# @author pangben 2011-05-02
# @version 1.0
##############################################

Module.item=insertClpchkType;updateClpchkType;deleteClpchkType;selectIsExist;selectAll
//添加数据
insertClpchkType.Type=TSQL
insertClpchkType.SQL=insert into CLP_CHKTYPE(REGION_CODE,CHKTYPE_CODE,ORDER_CAT1,PY1,CHKTYPE_CHN_DESC, &
SEQ,CHKTYPE_ENG_DESC,DESCRIPTION,PY2,OPT_USER,OPT_DATE,OPT_TERM) VALUES(<REGION_CODE>,<CHKTYPE_CODE>,<ORDER_CAT1>,<PY1>,<CHKTYPE_CHN_DESC>, &
<SEQ>,<CHKTYPE_ENG_DESC>,<DESCRIPTION>,<PY2>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertClpchkType.Debug=N
//修改数据
updateClpchkType.Type=TSQL
updateClpchkType.SQL=UPDATE CLP_CHKTYPE SET PY1=<PY1>,CHKTYPE_CHN_DESC=<CHKTYPE_CHN_DESC>,CHKTYPE_ENG_DESC=<CHKTYPE_ENG_DESC>, &
SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,PY2=<PY2>,OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE, &
OPT_TERM=<OPT_TERM> WHERE REGION_CODE=<REGION_CODE> &
AND CHKTYPE_CODE=<CHKTYPE_CODE> AND ORDER_CAT1=<ORDER_CAT1>
updateClpchkType.Debug=N
//删除数据
deleteClpchkType.Type=TSQL
deleteClpchkType.SQL=DELETE FROM CLP_CHKTYPE WHERE REGION_CODE=<REGION_CODE> AND CHKTYPE_CODE=<CHKTYPE_CODE> &
AND ORDER_CAT1=<ORDER_CAT1>
deleteClpchkType.Debug=N
//查找此信息是否存在，如果存在将执行修改命令，如果不存在添加命令
selectIsExist.Type=TSQL
selectIsExist.SQL=SELECT REGION_CODE FROM CLP_CHKTYPE WHERE REGION_CODE=<REGION_CODE> AND CHKTYPE_CODE=<CHKTYPE_CODE> &
AND ORDER_CAT1=<ORDER_CAT1>
selectIsExist.Debug=N

selectAll.Type=TSQL
selectAll.SQL=SELECT CHKTYPE_CODE FROM CLP_CHKTYPE WHERE REGION_CODE=<REGION_CODE> 	ORDER BY SEQ 
selectAll.Debug=N