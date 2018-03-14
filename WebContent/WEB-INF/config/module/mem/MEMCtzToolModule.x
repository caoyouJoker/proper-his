###############################
# <p>Title:会员身份管理 </p>
#
# <p>Description:会员身份管理  </p>
#
# <p>Copyright: Copyright (c) 2009</p>
#
# <p>Company:javahis </p>
#
# @author duzhw 2013.12.30
# @version 4.5
#
###############################
Module.item=checkCtzCode;onSaveMemCtzInfoData;updateMemCtzInfoData;queryCtzDetail;queryCtzFee;delCtzCode;updateDiscountRate;delCtzDetailCode


//校验身份代码是否可用
checkCtzCode.Type=TSQL
checkCtzCode.SQL=SELECT CTZ_CODE FROM SYS_CTZ WHERE CTZ_CODE=<CTZ_CODE>
checkCtzCode.Debug=N

//保存-新增身份设置 
onSaveMemCtzInfoData.Type=TSQL
onSaveMemCtzInfoData.SQL=INSERT INTO SYS_CTZ( &
								CTZ_CODE,CTZ_DESC,PY1,PY2,SEQ,DESCRIPT, &
								NHI_COMPANY_CODE,MAIN_CTZ_FLG,NHI_CTZ_FLG, &
								MRCTZ_UPD_FLG,OPT_USER,OPT_DATE,OPT_TERM, &
								DEF_CTZ_FLG,ENG_DESC,MRO_CTZ,STA2_CODE, &
								STA1_CODE,MEM_TYPE,MEM_CODE,MEM_DESC, &
								DEPT_CODE,DISCOUNT_RATE,USE_FLG,START_DATE, &
								END_DATE,CTZ_DEPT_FLG,OPD_FIT_FLG,EMG_FIT_FLG, &
								IPD_FIT_FLG,HRM_FIT_FLG,OVERDRAFT,NHI_NO,SPECIAL_FLG) &
							VALUES ( &
								<CTZ_CODE>,<CTZ_DESC>,<PY1>,'',<SEQ>,<DESCRIPT>, &
								'',<MAIN_CTZ_FLG>,<NHI_CTZ_FLG>, &
								<MRCTZ_UPD_FLG>,<OPT_USER>,SYSDATE,<OPT_TERM>, &
								'','',<MRO_CTZ>,'', &
								'',<MEM_TYPE>,<MEM_CODE>,'', &
								<DEPT_CODE>,<DISCOUNT_RATE>,<USE_FLG>,TO_DATE(<START_DATE>,'YYYY/MM/DD'), &
								TO_DATE(<END_DATE>,'YYYY/MM/DD'),<CTZ_DEPT_FLG>,<OPD_FIT_FLG>,<EMG_FIT_FLG>, &
								<IPD_FIT_FLG>,<HRM_FIT_FLG>,<OVERDRAFT>,<NHI_NO>,<SPECIAL_FLG> )
onSaveMemCtzInfoData.Debug=N

//保存-修改身份设置
updateMemCtzInfoData.Type=TSQL
updateMemCtzInfoData.SQL=UPDATE SYS_CTZ SET &
					CTZ_DESC=<CTZ_DESC>, &
					MEM_TYPE=<MEM_TYPE>, &
					MEM_CODE=<MEM_CODE>, &
					DEPT_CODE=<DEPT_CODE>, &
					DISCOUNT_RATE=<DISCOUNT_RATE>, &
					PY1=<PY1>, &
					DESCRIPT=<DESCRIPT>, &
					USE_FLG=<USE_FLG>, &
					START_DATE=TO_DATE(<START_DATE>,'YYYY/MM/DD'), &
					END_DATE=TO_DATE(<END_DATE>,'YYYY/MM/DD'), &
					CTZ_DEPT_FLG=<CTZ_DEPT_FLG>, &
					MAIN_CTZ_FLG=<MAIN_CTZ_FLG>, &
					NHI_CTZ_FLG=<NHI_CTZ_FLG>, &
					MRCTZ_UPD_FLG=<MRCTZ_UPD_FLG>, &
					OPD_FIT_FLG=<OPD_FIT_FLG>, &
					EMG_FIT_FLG=<EMG_FIT_FLG>, &
					IPD_FIT_FLG=<IPD_FIT_FLG>, &
					HRM_FIT_FLG=<HRM_FIT_FLG>, &
					OVERDRAFT=<OVERDRAFT>, &
					MRO_CTZ=<MRO_CTZ>, &
					NHI_NO=<NHI_NO>, &
					SPECIAL_FLG=<SPECIAL_FLG>, &
					SEQ=<SEQ> &
				WHERE CTZ_CODE=<CTZ_CODE>
updateMemCtzInfoData.Debug=N

//校验身份表是否可以删除-项目分类折扣（SYS_CHARGE_DETAIL）
queryCtzDetail.Type=TSQL
queryCtzDetail.SQL=SELECT B.DEPT_CODE,B.CTZ_CODE,B.CTZ_DESC,A.CTZ_CODE, &
					A.CHARGE_HOSP_CODE,A.DISCOUNT_RATE,A.OPT_USER, &
					A.OPT_DATE,A.OPT_TERM &
					FROM SYS_CHARGE_DETAIL A,sys_ctz B &
					WHERE A.CTZ_CODE = B.CTZ_CODE AND A.CTZ_CODE = <CTZ_CODE>
queryCtzDetail.Debug=N

//校验身份表是否可以删除-项目明细（SYS_CTZ_FEE）
queryCtzFee.Type=TSQL
queryCtzFee.SQL=SELECT B.DEPT_CODE,B.CTZ_CODE,B.CTZ_DESC,A.CTZ_CODE, &
				A.ORDER_CODE,A.ORDER_DESC,A.DISCOUNT_RATE, &
				A.OPT_USER,A.OPT_DATE,A.OPT_TERM &
				FROM SYS_CTZ_FEE A,SYS_CTZ B &
				WHERE A.CTZ_CODE = B.CTZ_CODE  AND A.CTZ_CODE = <CTZ_CODE>
queryCtzFee.Debug=N

//删除身份表数据
delCtzCode.Type=TSQL
delCtzCode.SQL=DELETE FROM SYS_CTZ WHERE CTZ_CODE=<CTZ_CODE>
delCtzCode.Debug=N
   
//删除身份表明细数据
delCtzDetailCode.Type=TSQL
delCtzDetailCode.SQL=DELETE FROM SYS_CTZ_ORDER_DETAIL
delCtzDetailCode.ITEM=ORDER_CODE;CTZ_CODE  
delCtzDetailCode.ORDER_CODE=ORDER_CODE=<ORDER_CODE>   
delCtzDetailCode.CTZ_CODE=CTZ_CODE=<CTZ_CODE>
delCtzDetailCode.Debug=N  


//修改折扣率
updateDiscountRate.Type=TSQL
updateDiscountRate.SQL=UPDATE SYS_CHARGE_DETAIL SET DISCOUNT_RATE = <DISCOUNT_RATE> WHERE CTZ_CODE = <CTZ_CODE>
updateDiscountRate.Debug=N


