########################################################
# <p>Title:门急医师办理入院统计报表 </p>
#
# <p>Description:门急医师办理入院统计报表 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:JavaHis </p>
#
# @author zhangk 2009-11-19
# @version 4.0
########################################################
Module.item=selectOpdDrCount

//查询入院病患信息
selectOpdDrCount.Type=TSQL
selectOpdDrCount.SQL=SELECT A.REGION_CODE,B.DEPT_CHN_DESC,C.USER_NAME,COUNT(CASE_NO) AS NUM,D.REGION_CHN_ABN &
			FROM ADM_INP A,SYS_DEPT B,SYS_OPERATOR C,SYS_REGION D &
			WHERE A.IN_DEPT_CODE = B.DEPT_CODE &
			AND A.OPD_DR_CODE = C.USER_ID &
			AND A.REGION_CODE = D.REGION_CODE(+) &
			AND A.IN_DATE BETWEEN TO_DATE(<DATE_S>,'YYYYMMDDHH24MISS') AND TO_DATE(<DATE_E>,'YYYYMMDDHH24MISS')  &
			GROUP BY A.IN_DEPT_CODE,A.OPD_DR_CODE,B.DEPT_CHN_DESC,C.USER_NAME,A.REGION_CODE,D.REGION_CHN_ABN
selectOpdDrCount.item=IN_DEPT_CODE;OPD_DR_CODE;REGION_CODE
selectOpdDrCount.IN_DEPT_CODE=A.IN_DEPT_CODE=<IN_DEPT_CODE>
selectOpdDrCount.OPD_DR_CODE=A.OPD_DR_CODE=<OPD_DR_CODE>
selectOpdDrCount.REGION_CODE=A.REGION_CODE=<REGION_CODE>
selectOpdDrCount.Debug=N