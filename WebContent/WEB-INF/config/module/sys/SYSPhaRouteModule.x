Module.item=selectall;selectdata;deletedata;insertdata;updatedata;existsROUTECODE;selectallfromsysfee;selectfromsysfee;opdPhaRoute;selectdataForCombo

//查询给药途径代码按给药途径代码
selectdata.Type=TSQL
selectdata.SQL=SELECT   A.seq, A.route_code, A.route_chn_desc, A.py1, A.py2, A.wesmed_flg, A.chimed_flg, &
         		A.route_eng_desc, A.description, A.link_order_code, A.opt_user, A.opt_date, &
         		B.ORDER_DESC,A.IGNORE_FLG, A.PS_FLG, A.CLASSIFY_TYPE, A.IVA_FLG &
    			FROM sys_pharoute A, SYS_FEE B &
    			WHERE A.LINK_ORDER_CODE = B.ORDER_CODE(+) AND A.ROUTE_CODE = <ROUTE_CODE> &
			ORDER BY A.seq
selectdata.Debug=N


//删除给药途径代码全字段
deletedata.Type=TSQL
deletedata.SQL=DELETE SYS_PHAROUTE  WHERE ROUTE_CODE = <ROUTE_CODE> 
deletedata.Debug=N

//更新给药途径代码全字段按给药途径代码
updatedata.Type=TSQL
updatedata.SQL=UPDATE SYS_PHAROUTE SET SEQ=<SEQ>, ROUTE_CHN_DESC=<ROUTE_CHN_DESC>,PY1=<PY1>,PY2=<PY2>,WESMED_FLG=<WESMED_FLG>,CHIMED_FLG=<CHIMED_FLG>,ROUTE_ENG_DESC=<ROUTE_ENG_DESC>,DESCRIPTION=<DESCRIPTION>,OPT_USER=<OPT_USER> ,LINK_ORDER_CODE=<LINK_ORDER_CODE>,IGNORE_FLG=<IGNORE_FLG>,PS_FLG=<PS_FLG>,CLASSIFY_TYPE=<CLASSIFY_TYPE>,OPT_DATE=<OPT_DATE>,OPT_TERM=<OPT_TERM>,IVA_FLG=<IVA_FLG> WHERE ROUTE_CODE = <ROUTE_CODE> 
updatedata.Debug=N

//插入给药途径代码全字段
insertdata.Type=TSQL
insertdata.SQL=INSERT INTO SYS_PHAROUTE(ROUTE_CODE,ROUTE_CHN_DESC ,ROUTE_ENG_DESC,PY1,PY2,SEQ,DESCRIPTION,LINK_ORDER_CODE,WESMED_FLG,CHIMED_FLG,OPT_USER,OPT_DATE,OPT_TERM,CLASSIFY_TYPE,IGNORE_FLG,PS_FLG,IVA_FLG) VALUES(<ROUTE_CODE>,<ROUTE_CHN_DESC> ,<ROUTE_ENG_DESC>,<PY1>,<PY2>,<SEQ>,<DESCRIPTION>,<LINK_ORDER_CODE>,<WESMED_FLG>,<CHIMED_FLG>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>,<CLASSIFY_TYPE>,<IGNORE_FLG>,<PS_FLG>,<IVA_FLG>)
insertdata.Debug=N

//查询给药途径代码全字段
selectall.Type=TSQL
selectall.SQL=SELECT   A.seq, A.route_code, A.route_chn_desc, A.py1, A.py2, A.wesmed_flg, A.chimed_flg, &
         	       A.route_eng_desc, A.description, A.link_order_code,A.IGNORE_FLG,A.PS_FLG, A.opt_user, A.opt_date, B.ORDER_DESC,A.CLASSIFY_TYPE, A.IVA_FLG &
    		       FROM sys_pharoute A, SYS_FEE B &
    		 WHERE A.LINK_ORDER_CODE = B.ORDER_CODE(+) &
		       ORDER BY A.seq
selectall.Debug=N

//是否存在给药途径代码
existsROUTECODE.type=TSQL
existsROUTECODE.SQL=SELECT COUNT(ROUTE_CODE) AS COUNT FROM SYS_PHAROUTE WHERE ROUTE_CODE=<ROUTE_CODE>
existsROUTECODE.Debug=N

//Combo用数据
selectdataForCombo.type=TSQL
selectdataForCombo.SQL=SELECT ROUTE_CODE AS ID,ROUTE_CHN_DESC AS NAME ,ROUTE_ENG_DESC AS ENNAME,PY1 AS PY1, PY2 AS PY2 &
			 FROM SYS_PHAROUTE ORDER BY SEQ
selectdataForCombo.item=WESMED_FLG
selectdataForCombo.WESMED_FLG=WESMED_FLG=<WESMED_FLG>

//根据sys_fee.ORDER_CODE查询所有的sys_fee.ORDER_DESC,DESCRIPTION,GOODS_DESC,ALIAS_DESC
selectfromsysfee.type=TSQL
selectfromsysfee.SQL=SELECT B.ORDER_CODE,B.ORDER_DESC,B.DESCRIPTION,B.GOODS_DESC,B.ALIAS_DESC FROM SYS_PHAROUTE A,SYS_FEE B &
 WHERE B.ORDER_CODE=A.LINK_ORDER_CODE

//查询所有的sys_fee.ORDER_DESC,DESCRIPTION,GOODS_DESC,ALIAS_DESC
selectallfromsysfee.type=TSQL
selectallfromsysfee.SQL=SELECT B.ORDER_CODE,B.ORDER_DESC,B.DESCRIPTION,B.GOODS_DESC,B.ALIAS_DESC FROM SYS_FEE B

//查询pha_base 中相应order_code的route_code在sys_pharoute中的code和中文和py1 以及 sys_phafreq 中所有的code和中英文和py1
opdPhaRoute.type=TSQL
opdPhaRoute.SQL=SELECT TABLE1.ID AS ID,TABLE1.NAME AS NAME,TABLE1.PY1 AS PY1 FROM &
 	(SELECT A.ROUTE_CODE AS ID,A.ROUTE_CHN_DESC AS NAME,A.PY1 AS PY1 ,A.SEQ &
  	FROM SYS_PHAROUTE A, PHA_BASE B &
 	WHERE A.WESMED_FLG='Y' & 
	UNION ALL &
	SELECT A.ROUTE_CODE AS ID,A.ROUTE_CHN_DESC AS NAME,A.PY1 AS PY1 ,A.SEQ &
  	FROM SYS_PHAROUTE A, PHA_BASE B &
 	WHERE A.ROUTE_CODE = B.ROUTE_CODE AND B.ORDER_CODE = <ORDER_CODE>) TABLE1 ORDER BY TABLE1.SEQ 