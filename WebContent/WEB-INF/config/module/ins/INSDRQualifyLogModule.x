# 
#  Title:职业医师证书号管理
# 
#  Description:职业医师证书号管理
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author xueyf 2012.2.02
#  version 1.0
#
Module.item=selectdata;insertdata;updateOperatordata;selectOperatordata;selectdatabyuser


//查询职业医师证书号管理信息列表
selectdatabyuser.Type=TSQL
selectdatabyuser.SQL=select I.USERID,S.USER_NAME,S.ID_NO,I.DR_QUALIFY_CODE_NEW,I.DR_QUALIFY_CODE_OLD from SYS_OPERATOR s, INS_DR_QUALIFY_LOG i where I.USERID=S.USER_ID and S.USER_ID=<USERID> &
	     ORDER BY I.OPT_DATE DESC
selectdatabyuser.Debug=N

//查询职业医师证书号管理信息列表
selectdata.Type=TSQL
selectdata.SQL=select I.USERID,S.USER_NAME,S.ID_NO,I.DR_QUALIFY_CODE_NEW,I.DR_QUALIFY_CODE_OLD from SYS_OPERATOR s, INS_DR_QUALIFY_LOG i WHERE I.USERID=S.USER_ID  &
	     ORDER BY I.OPT_DATE DESC
selectdata.Debug=N


//查询人员信息表
selectOperatordata.Type=TSQL
selectOperatordata.SQL=select s.USER_ID USERID,S.USER_NAME,S.ID_NO,DR_QUALIFY_CODE from SYS_OPERATOR s where S.USER_ID=<USERID> 
	 
selectOperatordata.Debug=N


//修改人员信息表
updateOperatordata.Type=TSQL
updateOperatordata.SQL=UPDATE SYS_OPERATOR SET DR_QUALIFY_CODE=<DR_QUALIFY_CODE_NEW> WHERE USER_ID=<USERID>
	 
updateOperatordata.Debug=N


//新增职业医师证书号管理信息
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO INS_DR_QUALIFY_LOG &
			   (REGION_CODE,USERID,OPT_DATE,DR_QUALIFY_CODE_OLD,DR_QUALIFY_CODE_NEW,OPT_USER,OPT_TERM) &
		    VALUES (<REGION_CODE>,<USERID>,<OPT_DATE>,<DR_QUALIFY_CODE_OLD>,<DR_QUALIFY_CODE_NEW>,&
		    	   <OPT_USER>,<OPT_TERM>)
insertdata.Debug=N

