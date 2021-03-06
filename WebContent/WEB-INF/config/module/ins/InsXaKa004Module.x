Module.item=query;update;insert;delete;query_sys_ins;importOrder;onQueryMaxXaka004

//查询三目ins_ka_004基本档数据
query.Type=TSQL
query.SQL=SELECT AKA100, AKA101, AKA102, AKA063, AKA065, BKA246, BKA247, BKA260, BKA001, AKA103, AKA066, AKA068, AKA069, AAE011, TO_CHAR(AAE036, 'YYYY-mm-dd hh24:MI:SS')AAE036, BAA001, AKA104, AAE100, HOSP_ORDER_CODE, FEE_TYPE, LIMIT_PRICE FROM ORAL.INS_XA_KA004 ORDER BY AKA100
query.item=HOSP_ORDER_CODE;FEE_TYPE;AKA100
query.HOSP_ORDER_CODE=HOSP_ORDER_CODE=<HOSP_ORDER_CODE>
query.FEE_TYPE=FEE_TYPE=<FEE_TYPE>
query.AKA100=AKA100=<AKA100>
query.Debug=N

//修改医保药品
update.Type=TSQL
update.SQL=update INS_XA_KA004 set AKA101=<AKA101>, AKA102=<AKA102>, AKA063=<AKA063>, AKA065=<AKA065>, BKA246=<BKA246>, BKA247=<BKA247>, BKA260=<BKA260>, BKA001=<BKA001>, AKA103=<AKA103>, AKA066=<AKA066>, AKA068=<AKA068>, AKA069=<AKA069>, AAE011=<AAE011>, AAE036=TO_CHAR(<AAE036>, 'YYYY-mm-dd hh24:MI:SS'), BAA001=<BAA001>, AKA104=<AKA104>, AAE100=<AAE100>, HOSP_ORDER_CODE=<HOSP_ORDER_CODE>, FEE_TYPE=<FEE_TYPE>, LIMIT_PRICE=<LIMIT_PRICE> where AKA100=<AKA100>
update.item=AKA101;AKA102;AKA063;AKA065;BKA246;BKA247;BKA260;BKA001;AKA103;AKA066;AKA068;AKA069;AAE011;AAE036;BAA001;AKA104;AAE100;HOSP_ORDER_CODE;FEE_TYPE;LIMIT_PRICE
update.Debug=N

/删除数据
delete.Type=TSQL
delete.SQL=DELETE FROM INS_XA_KA004 WHERE AKA100=<AKA100>
delete.Debug=N

//新增
insert.Type=TSQL
insert.SQL=INSERT INTO INS_XA_KA004&
            (AKA100, AKA101, AKA102, AKA063, AKA065,&
             BKA246, BKA247, BKA260, BKA001, AKA103, AKA066, AKA068, AKA069,&
             AAE011,AAE036, BAA001, AKA104, AAE100, HOSP_ORDER_CODE, FEE_TYPE, LIMIT_PRICE)&
             VALUES&
 (<AKA100>,<AKA101>,<AKA102>,<AKA063>,<AKA065>,&
 <BKA246>,<BKA247>,<BKA260>,<BKA001>,<AKA103>,<AKA066>,<AKA068>,<AKA069>,&
 <AAE011>,SYSDATE,<BAA001>,<AKA104>,<AAE100>,<HOSP_ORDER_CODE>,<FEE_TYPE>,<LIMIT_PRICE>)
insert.Debug=N



//查询sys_fee中存在，医保不存在的药品
query_sys_ins.Type=TSQL
query_sys_ins.SQL=  SELECT   DISTINCT A.ORDER_CODE, B.HOSP_ORDER_CODE,A.ORDER_DESC,A.TRADE_ENG_DESC,&

		A.CHARGE_HOSP_CODE,A.ORDER_CAT1,A.ORDER_TYPE,A.UNIT_CODE,&
		
		A.OWN_PRICE,A.DESCRIPTION &
		
		FROM SYS_FEE A, INS_XA_KA004 B &
		
  		WHERE  A.HOSP_AREA = 'HIS' &
  		 
            	AND A.ORDER_CAT1 NOT IN('CRT', 'ECT', 'END', 'LAB','PAT',  'RAD', 'ULT', 'ZJL','PHA') &
           	
          	AND B.HOSP_ORDER_CODE(+) = A.ORDER_CODE &
           	
           	AND A.ACTIVE_FLG='Y' &
           	
		ORDER BY   A.ORDER_CODE
query_sys_ins.Debug=N


//汇入
importOrder.Type=TSQL
importOrder.SQL=INSERT INTO INS_XA_KA004 &

            ( AKA100,LIMIT_PRICE, AKA101, AKA102, AKA063,&
            
            AKA065, BKA246, BKA247, BKA260, &
            
            BKA001, AKA103, AKA066, AKA068,&
            
            AKA069, AAE011, AAE036, BAA001,&
            
            AKA104, AAE100,HOSP_ORDER_CODE, FEE_TYPE ) &
             
             VALUES &
             
 	(<AKA100>,0.0,101,<AKA102>,<AKA063>,&
 	
 	<AKA065>,<BKA246>,'','',&
 	
 	'','','',<AKA068>,&
 	
 	0.0,<AAE011>,SYSDATE,'',&
 	
 	0.0,'Y',<AKA100>,'')
 	
importOrder.Debug=N

onQueryMaxXaka004.Type=TSQL
onQueryMaxXaka004.SQL=SELECT AKA100, AKA101, AKA102, AKA063, AKA065, &

			BKA246, BKA247, BKA260, BKA001, AKA103, &
			
			AKA066, AKA068, AKA069, AAE011, TO_CHAR(AAE036, 'YYYY-mm-dd hh24:MI:SS'), &
			
			BAA001, AKA104, AAE100, HOSP_ORDER_CODE, FEE_TYPE, LIMIT_PRICE &

			FROM INS_XA_KA004  &
			
			WHERE AAE036=(SELECT MAX(AAE036) FROM INS_XA_KA004  ) &
			
			ORDER BY AKA100
onQueryMaxXaka004.Debug=N