#
# Title:临床路径EMR工具类
#
# Description:临床路径EMR工具类
#
# Copyright: JavaHis (c) 2011
# @author luhai 2011/05/04
Module.item=selectPatientInfo
selectPatientInfo.Type=TSQL
selectPatientInfo.SQL= SELECT A.CASE_NO,A.MR_NO,A.CLNCPATH_CODE,A.DEPT_CODE,A.STATION_CODE,A.BED_NO,A.VS_DR_CODE, &
											 S.PAT_NAME &
											 FROM ADM_INP A,SYS_PATINFO S WHERE A.MR_NO=<MR_NO> AND A.MR_NO=S.MR_NO(+) &
											 AND A.REGION_CODE=<REGION_CODE> 
selectPatientInfo.Debug=N


