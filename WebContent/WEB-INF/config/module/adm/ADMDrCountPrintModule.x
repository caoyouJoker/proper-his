###################################################
# <p>Title:住院医师统计报表 </p>
#
# <p>Description:住院医师统计报表 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk 2009-11-19
# @version 4.0
####################################################
Module.item=selectDrCount

//查询入院病患信息
selectDrCount.Type=TSQL
selectDrCount.SQL=SELECT B.USER_NAME,A.MR_NO,A.IPD_NO,D.PAT_NAME,C.CTZ_DESC,E.REGION_CHN_ABN &
			FROM ADM_INP A,SYS_OPERATOR B,SYS_CTZ C,SYS_PATINFO D,SYS_REGION E &
			WHERE A.OPD_DR_CODE=B.USER_ID &
			AND A.CTZ1_CODE=C.CTZ_CODE &
			AND A.MR_NO=D.MR_NO &
                        AND B.REGION_CODE = E.REGION_CODE &
			AND A.IN_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS')
selectDrCount.item=IN_DEPT_CODE
selectDrCount.IN_DEPT_CODE=A.IN_DEPT_CODE=<IN_DEPT_CODE>
selectDrCount.Debug=N