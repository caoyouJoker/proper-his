# 
#  Title:挂号方式module
# 
#  Description:挂号方式module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
Module.item=selectdata;deletedata;insertdata;updatedata;existsRegMethod;getregmethodCombo;selComboFlg;selPrintFlg

//查询挂号方式，方式说明，拼音编码，注记符，顺序编号,备注,预约周数,计入爽约,手动选择,需读卡，操作人员，操作日期，操作终端
//======pangb 2012-2-20 添加打票注记PRINT_FLG
selectdata.Type=TSQL
selectdata.SQL=SELECT REGMETHOD_CODE,REGMETHOD_DESC,PY1,PY2,SEQ,&
		      DESCRIPTION,APPT_WEEK,MISSVST_FLG,COMBO_FLG,READIC_FLG,ORDER_CODE,&
		      OPT_USER,OPT_DATE,OPT_TERM,PRINT_FLG &
		 FROM REG_REGMETHOD &
		WHERE REGMETHOD_CODE LIKE <REGMETHOD_CODE> &
	     ORDER BY SEQ
selectdata.Debug=N

//删除挂号方式，方式说明，拼音编码，注记符，顺序编号,备注,预约周数,计入爽约,手动选择,需读卡，操作人员，操作日期，操作终端
deletedata.Type=TSQL
deletedata.SQL=DELETE FROM REG_REGMETHOD &
		     WHERE REGMETHOD_CODE = <REGMETHOD_CODE>
deletedata.Debug=N

//新增挂号方式，方式说明，拼音编码，注记符，顺序编号,备注,预约周数,计入爽约,手动选择,需读卡，操作人员，操作日期，操作终端
//======pangb 2012-2-20 添加打票注记PRINT_FLG
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO REG_REGMETHOD &
			   (REGMETHOD_CODE,REGMETHOD_DESC,PY1,PY2,SEQ,&
			   DESCRIPTION,APPT_WEEK,MISSVST_FLG,COMBO_FLG,READIC_FLG,&
			   ORDER_CODE,OPT_USER,OPT_DATE,OPT_TERM,PRINT_FLG) &
		    VALUES (<REGMETHOD_CODE>,<REGMETHOD_DESC>,<PY1>,<PY2>,<SEQ>,&
		           <DESCRIPTION>,<APPT_WEEK>,<MISSVST_FLG>,<COMBO_FLG>,<READIC_FLG>,&
		           <ORDER_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>,<PRINT_FLG>)
insertdata.Debug=N

//更新挂号方式，方式说明，拼音编码，注记符，顺序编号,备注,预约周数,计入爽约,手动选择,需读卡，操作人员，操作日期，操作终端
//======pangb 2012-2-20 添加打票注记PRINT_FLG
updatedata.Type=TSQL
updatedata.SQL=UPDATE REG_REGMETHOD &
		  SET REGMETHOD_CODE=<REGMETHOD_CODE>,REGMETHOD_DESC=<REGMETHOD_DESC>,PY1=<PY1>,&
		      PY2=<PY2>,SEQ=<SEQ>,DESCRIPTION=<DESCRIPTION>,&
		      APPT_WEEK=<APPT_WEEK>,MISSVST_FLG=<MISSVST_FLG>,COMBO_FLG=<COMBO_FLG>,&
		      READIC_FLG=<READIC_FLG>,ORDER_CODE=<ORDER_CODE>,OPT_USER=<OPT_USER>,&
		      OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM>,PRINT_FLG=<PRINT_FLG> &
		WHERE REGMETHOD_CODE=<REGMETHOD_CODE>
updatedata.Debug=N

//是否存在挂号方式
existsRegMethod.type=TSQL
existsRegMethod.SQL=SELECT COUNT(*) AS COUNT &
		      FROM REG_REGMETHOD &
		     WHERE REGMETHOD_CODE=<REGMETHOD_CODE>

//取得挂号方式combo信息
getregmethodCombo.Type=TSQL
getregmethodCombo.SQL=SELECT REGMETHOD_CODE AS ID,REGMETHOD_DESC AS NAME,ENNAME,PY1,PY2 &
			FROM REG_REGMETHOD &
		    ORDER BY SEQ
getregmethodCombo.item=COMBO_FLG
getregmethodCombo.COMBO_FLG=COMBO_FLG=<COMBO_FLG>
getregmethodCombo.Debug=N

//取得挂号方式combo信息
selComboFlg.Type=TSQL
selComboFlg.SQL=SELECT COMBO_FLG &
		  FROM REG_REGMETHOD &
		 WHERE REGMETHOD_CODE = <REGMETHOD_CODE>
selComboFlg.Debug=N

//得到打票注记
selPrintFlg.Type=TSQL
selPrintFlg.SQL=SELECT PRINT_FLG &
		  FROM REG_REGMETHOD &
		 WHERE REGMETHOD_CODE = <REGMETHOD_CODE>
selPrintFlg.Debug=N
