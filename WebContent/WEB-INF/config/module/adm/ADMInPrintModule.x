#################################################
# <p>Title:入院统计报表 </p>
#
# <p>Description:入院统计报表 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk 2009-10-29
# @version 4.0
#################################################
Module.item=selectInHosp

//查询入院病患信息
selectInHosp.Type=TSQL
selectInHosp.SQL=SELECT I.REGION_CHN_ABN,A.MR_NO,A.IPD_NO,B.DEPT_CHN_DESC,H.STATION_DESC,C.CTZ_DESC,D.PAT_NAME, &
			E.CHN_DESC,D.ADDRESS,D.CONTACTS_NAME,D.CELL_PHONE,A.IN_DATE, &
			F.USER_NAME AS ADM_CLERK,G.USER_NAME AS OPD_DR_CODE &
			FROM ADM_INP A,SYS_DEPT B,SYS_CTZ C,SYS_PATINFO D,SYS_DICTIONARY E,SYS_OPERATOR F,SYS_OPERATOR G,SYS_STATION H,SYS_REGION I &
			WHERE A.IN_DEPT_CODE = B.DEPT_CODE &
			AND A.CTZ1_CODE=C.CTZ_CODE &
			AND A.REGION_CODE=I.REGION_CODE(+) &
			AND A.MR_NO = D.MR_NO &
			AND D.SEX_CODE = E.ID(+) &
			AND A.ADM_CLERK=F.USER_ID(+) &
			AND A.OPD_DR_CODE=G.USER_ID(+) &
			AND E.GROUP_ID='SYS_SEX'  &     
			AND A.IN_STATION_CODE = H.STATION_CODE &
			AND A.IN_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS')   
selectInHosp.item=CTZ1_CODE;REGION_CODE
selectInHosp.CTZ1_CODE=A.CTZ1_CODE=<CTZ1_CODE>
selectInHosp.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectInHosp.Debug=N