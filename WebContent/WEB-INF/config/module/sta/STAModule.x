# 
#  Title:STA对外公用module
# 
#  Description:STA对外公用module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author zhangk 2009.06.12
#  version 1.0
#
Module.item=getIBSChargeName
//获取费用代码对照的chang
getIBSChargeName.Type=TSQL
getIBSChargeName.SQL=SELECT CHARGE01, CHARGE02, CHARGE03, CHARGE04, CHARGE05,&
                           CHARGE06, CHARGE07, CHARGE08, CHARGE09, CHARGE10,&
                           CHARGE11, CHARGE12, CHARGE13, CHARGE14, CHARGE15,&
                           CHARGE16, CHARGE17, CHARGE18, CHARGE19, CHARGE20,&
                           CHARGE21, CHARGE22, CHARGE23, CHARGE24, CHARGE25,&
                           CHARGE26, CHARGE27, CHARGE28, CHARGE29, CHARGE30 &
                           FROM BIL_RECPPARM  WHERE RECP_TYPE = 'IBS' AND ADM_TYPE='I'
getIBSChargeName.Debug=N