# 
#  Title:�����ֵ�module
# 
#  Description:�����ֵ�
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author pangben 2011-05-03
#  version 1.0
#
//�ٴ�·�����
Module.item=selectIsExist;insertClpVarMoncat;updateClpVarMoncat;updateClpVariance;insertClpVariance
//��ѯ��������ݱ����ֵ��������Ƿ����
selectIsExist.Type=TSQL
selectIsExist.SQL=SELECT REGION_CODE FROM CLP_VARMONCAT WHERE REGION_CODE=<REGION_CODE> AND MONCAT_CODE=<MONCAT_CODE>
selectIsExist.Debug=N
//�������������ݷ���
insertClpVarMoncat.Type=TSQL
insertClpVarMoncat.SQL=INSERT INTO CLP_VARMONCAT(REGION_CODE,MONCAT_CODE,PY1,MONCAT_CHN_DESC, &
SEQ,DESCRIPTION,PY2,MONCAT_ENG_DESC,OPT_USER,OPT_DATE,OPT_TERM) VALUES (<REGION_CODE>,<MONCAT_CODE>,<PY1>, &
<MONCAT_CHN_DESC>,<SEQ>,<DESCRIPTION>,<PY2>,<MONCAT_ENG_DESC>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertClpVarMoncat.Debug=N
//�޸����������ݷ���
updateClpVarMoncat.Type=TSQL
updateClpVarMoncat.SQL=UPDATE CLP_VARMONCAT SET PY1=<PY1>,MONCAT_CHN_DESC=<MONCAT_CHN_DESC>,&
SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,PY2=<PY2>,MONCAT_ENG_DESC=<MONCAT_ENG_DESC>,OPT_USER=<OPT_USER>,&
OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> WHERE REGION_CODE=<REGION_CODE> AND MONCAT_CODE=<MONCAT_CODE>
updateClpVarMoncat.Debug=N
//�޸��ӱ����ݷ���
updateClpVariance.Type=TSQL
updateClpVariance.SQL=UPDATE CLP_VARIANCE SET SEQ=<SEQ>,PY2=<PY2>,VARIANCE_CHN_DESC=<VARIANCE_CHN_DESC>, &
PY1=<PY1>,VARIANCE_ENG_DESC=<VARIANCE_ENG_DESC>,DESCRIPTION=<DESCRIPTION>,CLNCPATH_CODE=<CLNCPATH_CODE>,&
OPT_USER=<OPT_USER>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM> WHERE REGION_CODE=<REGION_CODE> AND MONCAT_CODE=<MONCAT_CODE> AND VARIANCE_CODE=<VARIANCE_CODE>
updateClpVariance.Debug=N
//�����ӱ����ݷ���
insertClpVariance.Type=TSQL
insertClpVariance.SQL=INSERT INTO CLP_VARIANCE(REGION_CODE,MONCAT_CODE,VARIANCE_CODE,SEQ,PY2,VARIANCE_CHN_DESC,PY1,VARIANCE_ENG_DESC,DESCRIPTION,CLNCPATH_CODE,&
OPT_USER,OPT_DATE,OPT_TERM) VALUES (<REGION_CODE>,<MONCAT_CODE>,<VARIANCE_CODE>,<SEQ>,<PY2>,<VARIANCE_CHN_DESC>,<PY1>,<VARIANCE_ENG_DESC>,<DESCRIPTION>,<CLNCPATH_CODE>, &
<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
insertClpVariance.Debug=N