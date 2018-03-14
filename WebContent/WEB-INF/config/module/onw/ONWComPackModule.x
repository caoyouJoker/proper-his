######################################################
# <p>Title:急诊抢救之口头医嘱套餐Module </p>
#
# <p>Description:急诊抢救之口头医嘱套餐Module </p>
#
# <p>Copyright: Copyright (c) 2017</p>
#
# <p>Company:javahis </p>
#
# @author wangqing 20170901
#
# @version 1.0
#

Module.item=insertOnwPackMain;deleteOnwPackMain;updateOnwPackMain;selectOnwPackMain; &
            insertOnwPackOrder;deleteOnwPackOrder;updateOnwPackOrder;selectOnwPackOrder; &
            insertOnwOrder;deleteOnwOrder;selectOnwOrder;updateOnwOrder1;updateOnwOrder2;updateOnwOrder3;updateOnwOrder4; &
            updateAmiErdVtsRecord;updateAmiErdVtsRecord1;updateAmiErdVtsRecord2

// add by wangqing 20170901 ONW_PACK_MAIN表新增一条套餐
insertOnwPackMain.Type=TSQL
insertOnwPackMain.SQL=INSERT INTO ONW_PACK_MAIN (PACK_CODE, PACK_DESC, RX_TYPE, DEPT_CODE, OPT_USER, OPT_DATE, OPT_TERM) &
                      VALUES(<PACK_CODE>,<PACK_DESC>,<RX_TYPE>,<DEPT_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertOnwPackMain.Debug=Y

// add by wangqing 20170901 ONW_PACK_MAIN表删除一条套餐
deleteOnwPackMain.Type=TSQL
deleteOnwPackMain.SQL=DELETE FROM ONW_PACK_MAIN WHERE PACK_CODE=<PACK_CODE>
deleteOnwPackMain.Debug=Y

// add by wangqing 20170901 ONW_PACK_MAIN表更新一条套餐
updateOnwPackMain.Type=TSQL
updateOnwPackMain.SQL=UPDATE ONW_PACK_MAIN SET PACK_DESC=<PACK_DESC>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
                      WHERE PACK_CODE=<PACK_CODE>
updateOnwPackMain.Debug=Y

// add by wangqing 20170904 ONW_PACK_MAIN表查询套餐
selectOnwPackMain.Type=TSQL
selectOnwPackMain.SQL=SELECT PACK_CODE, PACK_DESC, RX_TYPE, DEPT_CODE, OPT_USER, OPT_DATE, OPT_TERM FROM ONW_PACK_MAIN WHERE RX_TYPE=<RX_TYPE>
selectOnwPackMain.item=DEPT_CODE
selectOnwPackMain.DEPT_CODE=DEPT_CODE=<DEPT_CODE>
selectOnwPackMain.Debug=Y

// add by wangqing 20170901 ONW_PACK_ORDER表新增一条医嘱
insertOnwPackOrder.Type=TSQL
insertOnwPackOrder.SQL=INSERT INTO ONW_PACK_ORDER &
                      (PACK_CODE, SEQ_NO, LINKMAIN_FLG, LINK_NO, ORDER_CODE, ORDER_DESC, &
                      MEDI_QTY, MEDI_UNIT, ROUTE_CODE, FREQ_CODE, TAKE_DAYS, &
                      CAT1_TYPE, ORDER_CAT1_CODE, OPT_USER, OPT_DATE, OPT_TERM) &
                      VALUES(<PACK_CODE>,<SEQ_NO>,<LINKMAIN_FLG>,<LINK_NO>,<ORDER_CODE>,<ORDER_DESC>, &
                      <MEDI_QTY>,<MEDI_UNIT>,<ROUTE_CODE>,<FREQ_CODE>,<TAKE_DAYS>, &
                      <CAT1_TYPE>,<ORDER_CAT1_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>)
insertOnwPackOrder.Debug=Y

// add by wangqing 20170901 ONW_PACK_ORDER表删除医嘱
deleteOnwPackOrder.Type=TSQL
deleteOnwPackOrder.SQL=DELETE FROM ONW_PACK_ORDER 
deleteOnwPackOrder.item=PACK_CODE;SEQ_NO
deleteOnwPackOrder.PACK_CODE=PACK_CODE=<PACK_CODE>
deleteOnwPackOrder.SEQ_NO=SEQ_NO=<SEQ_NO>
deleteOnwPackOrder.Debug=Y

// add by wangqing 20170901 ONW_PACK_ORDER表更新医嘱
updateOnwPackOrder.Type=TSQL
updateOnwPackOrder.SQL=UPDATE ONW_PACK_ORDER SET LINKMAIN_FLG=<LINKMAIN_FLG>, LINK_NO=<LINK_NO>, &
                      MEDI_QTY=<MEDI_QTY>, MEDI_UNIT=<MEDI_UNIT>, ROUTE_CODE=<ROUTE_CODE>, FREQ_CODE=<FREQ_CODE>, &
                      TAKE_DAYS=<TAKE_DAYS>, OPT_USER=<OPT_USER>, OPT_DATE=SYSDATE, OPT_TERM=<OPT_TERM> &
                      WHERE PACK_CODE=<PACK_CODE> AND SEQ_NO=<SEQ_NO>
updateOnwPackOrder.Debug=Y

selectOnwPackOrder.Type=TSQL
selectOnwPackOrder.SQL=SELECT PACK_CODE, SEQ_NO, LINKMAIN_FLG, LINK_NO, ORDER_CODE, ORDER_DESC, MEDI_QTY, MEDI_UNIT, &
                       ROUTE_CODE, FREQ_CODE, TAKE_DAYS, CAT1_TYPE, ORDER_CAT1_CODE, OPT_USER, OPT_DATE, OPT_TERM FROM ONW_PACK_ORDER WHERE &
                       PACK_CODE=<PACK_CODE> ORDER BY SEQ_NO
selectOnwPackOrder.Debug=Y

// add by wangqing 20170901 ONW_ORDER表新增一条医嘱
insertOnwOrder.Type=TSQL
insertOnwOrder.SQL=INSERT INTO ONW_ORDER &
                      (TRIAGE_NO, SEQ_NO, LINKMAIN_FLG, LINK_NO, ORDER_CODE, ORDER_DESC, &
                      MEDI_QTY, MEDI_UNIT, ROUTE_CODE, FREQ_CODE, TAKE_DAYS, &
                      CAT1_TYPE, ORDER_CAT1_CODE, OPT_USER, OPT_DATE, OPT_TERM, NOTE_DATE, SIGN_DR, SIGN_NS, EXE_FLG) &
                      VALUES(<TRIAGE_NO>,<SEQ_NO>,<LINKMAIN_FLG>,<LINK_NO>,<ORDER_CODE>,<ORDER_DESC>, &
                      <MEDI_QTY>,<MEDI_UNIT>,<ROUTE_CODE>,<FREQ_CODE>,<TAKE_DAYS>, &
                      <CAT1_TYPE>,<ORDER_CAT1_CODE>,<OPT_USER>,SYSDATE,<OPT_TERM>,TO_DATE(<NOTE_DATE>,'yyyy/mm/dd hh24:mi:ss'),<SIGN_DR>,<SIGN_NS>,<EXE_FLG>)
insertOnwOrder.Debug=Y

// add by wangqing 20170911 ONW_ORDER删除医嘱
deleteOnwOrder.Type=TSQL
deleteOnwOrder.SQL=DELETE FROM ONW_ORDER WHERE TRIAGE_NO=<TRIAGE_NO> & 
                   AND (SIGN_DR IS NUll OR LENGTH(SIGN_DR)=0) AND (SIGN_NS IS NUll OR LENGTH(SIGN_NS)=0)
deleteOnwOrder.item=SEQ_NO
deleteOnwOrder.SEQ_NO=SEQ_NO=<SEQ_NO>
deleteOnwOrder.Debug=Y

// add by wangqing 20171024 ONW_ORDER更新医嘱或护士签名
updateOnwOrder1.Type=TSQL
updateOnwOrder1.SQL=UPDATE ONW_ORDER SET SEQ_NO=<SEQ_NO>, LINKMAIN_FLG=<LINKMAIN_FLG>, LINK_NO=<LINK_NO>,ORDER_CODE= <ORDER_CODE>, &
                   ORDER_DESC=<ORDER_DESC>,MEDI_QTY=<MEDI_QTY> ,ROUTE_CODE=<ROUTE_CODE>, FREQ_CODE=<FREQ_CODE>, TAKE_DAYS=<TAKE_DAYS>, &
                   CAT1_TYPE=<CAT1_TYPE>, ORDER_CAT1_CODE=<ORDER_CAT1_CODE>, OPT_USER=<OPT_USER> , OPT_DATE= SYSDATE, OPT_TERM= <OPT_TERM>, &
                   NOTE_DATE=TO_DATE(<NOTE_DATE>,'yyyy/mm/dd hh24:mi:ss'), SIGN_DR=<SIGN_DR>, SIGN_NS=<SIGN_NS>, EXE_FLG=<EXE_FLG> &
                   WHERE TRIAGE_NO=<TRIAGE_NO> &
                   AND (SIGN_DR IS NUll OR LENGTH(SIGN_DR)=0) AND (SIGN_NS IS NUll OR LENGTH(SIGN_NS)=0)
updateOnwOrder1.item=SEQ_NO
updateOnwOrder1.SEQ_NO=SEQ_NO=<SEQ_NO>
updateOnwOrder1.Debug=Y

// add by wangqing 20170919 ONW_ORDER取消护士签名
updateOnwOrder2.Type=TSQL
updateOnwOrder2.SQL=UPDATE ONW_ORDER SET SEQ_NO=<SEQ_NO>, LINKMAIN_FLG=<LINKMAIN_FLG>, LINK_NO=<LINK_NO>,ORDER_CODE= <ORDER_CODE>, &
                   ORDER_DESC=<ORDER_DESC>,MEDI_QTY=<MEDI_QTY> ,ROUTE_CODE=<ROUTE_CODE>, FREQ_CODE=<FREQ_CODE>, TAKE_DAYS=<TAKE_DAYS>, &
                   CAT1_TYPE=<CAT1_TYPE>, ORDER_CAT1_CODE=<ORDER_CAT1_CODE>, OPT_USER=<OPT_USER> , OPT_DATE= SYSDATE, OPT_TERM= <OPT_TERM>, &
                   NOTE_DATE=TO_DATE(<NOTE_DATE>,'yyyy/mm/dd hh24:mi:ss'), SIGN_DR=<SIGN_DR>, SIGN_NS=<SIGN_NS>, EXE_FLG=<EXE_FLG> &
                   WHERE TRIAGE_NO=<TRIAGE_NO> &
                   AND (SIGN_DR IS NUll OR LENGTH(SIGN_DR)=0) AND (SIGN_NS IS NOT NUll AND LENGTH(SIGN_NS)>0)
updateOnwOrder2.item=SEQ_NO
updateOnwOrder2.SEQ_NO=SEQ_NO=<SEQ_NO>
updateOnwOrder2.Debug=Y

// add by wangqing 20170919 ONW_ORDER医生签名
updateOnwOrder3.Type=TSQL
updateOnwOrder3.SQL=UPDATE ONW_ORDER SET SEQ_NO=<SEQ_NO>, LINKMAIN_FLG=<LINKMAIN_FLG>, LINK_NO=<LINK_NO>,ORDER_CODE= <ORDER_CODE>, &
                   ORDER_DESC=<ORDER_DESC>,MEDI_QTY=<MEDI_QTY> ,ROUTE_CODE=<ROUTE_CODE>, FREQ_CODE=<FREQ_CODE>, TAKE_DAYS=<TAKE_DAYS>, &
                   CAT1_TYPE=<CAT1_TYPE>, ORDER_CAT1_CODE=<ORDER_CAT1_CODE>, OPT_USER=<OPT_USER> , OPT_DATE= SYSDATE, OPT_TERM= <OPT_TERM>, &
                   NOTE_DATE=TO_DATE(<NOTE_DATE>,'yyyy/mm/dd hh24:mi:ss'), SIGN_DR=<SIGN_DR>, SIGN_NS=<SIGN_NS>, EXE_FLG=<EXE_FLG> &
                   WHERE TRIAGE_NO=<TRIAGE_NO> &
                   AND (SIGN_DR IS NUll OR LENGTH(SIGN_DR)=0) AND (SIGN_NS IS NOT NUll AND LENGTH(SIGN_NS)>0)
updateOnwOrder3.item=SEQ_NO
updateOnwOrder3.SEQ_NO=SEQ_NO=<SEQ_NO>
updateOnwOrder3.Debug=Y

// add by wangqing 20170919 ONW_ORDER取消医生签名
updateOnwOrder4.Type=TSQL
updateOnwOrder4.SQL=UPDATE ONW_ORDER SET SEQ_NO=<SEQ_NO>, LINKMAIN_FLG=<LINKMAIN_FLG>, LINK_NO=<LINK_NO>,ORDER_CODE= <ORDER_CODE>, &
                   ORDER_DESC=<ORDER_DESC>,MEDI_QTY=<MEDI_QTY> ,ROUTE_CODE=<ROUTE_CODE>, FREQ_CODE=<FREQ_CODE>, TAKE_DAYS=<TAKE_DAYS>, &
                   CAT1_TYPE=<CAT1_TYPE>, ORDER_CAT1_CODE=<ORDER_CAT1_CODE>, OPT_USER=<OPT_USER> , OPT_DATE= SYSDATE, OPT_TERM= <OPT_TERM>, &
                   NOTE_DATE=TO_DATE(<NOTE_DATE>,'yyyy/mm/dd hh24:mi:ss'), SIGN_DR=<SIGN_DR>, SIGN_NS=<SIGN_NS>, EXE_FLG=<EXE_FLG> &
                   WHERE TRIAGE_NO=<TRIAGE_NO> &
                   AND (SIGN_DR IS NOT NUll OR LENGTH(SIGN_DR)>0) AND (SIGN_NS IS NOT NUll AND LENGTH(SIGN_NS)>0)
updateOnwOrder4.item=SEQ_NO
updateOnwOrder4.SEQ_NO=SEQ_NO=<SEQ_NO>
updateOnwOrder4.Debug=Y

// add by wangqing 20170912 ONW_ORDER查询医嘱
selectOnwOrder.Type=TSQL
selectOnwOrder.SQL=SELECT TRIAGE_NO, SEQ_NO, LINKMAIN_FLG, LINK_NO, ORDER_CODE, ORDER_DESC, &
                   MEDI_QTY, MEDI_UNIT, ROUTE_CODE, FREQ_CODE, TAKE_DAYS, &
                   CAT1_TYPE, ORDER_CAT1_CODE, OPT_USER, OPT_DATE, OPT_TERM, &
                   NOTE_DATE, SIGN_DR, SIGN_NS, EXE_FLG &
                   FROM ONW_ORDER WHERE TRIAGE_NO=<TRIAGE_NO> &
                   ORDER BY SEQ_NO
selectOnwOrder.item=CHECK_FLG;EXE_FLG
selectOnwOrder.CHECK_FLG=CHECK_FLG=<CHECK_FLG>
selectOnwOrder.EXE_FLG=EXE_FLG=<EXE_FLG>
selectOnwOrder.Debug=Y

// add by wangqing 20171015 AMI_ERD_VTS_RECORD更新数据
updateAmiErdVtsRecord.Type=TSQL
updateAmiErdVtsRecord.SQL=UPDATE AMI_ERD_VTS_RECORD SET CONDITION=<CONDITION>, PAIN=<PAIN>, OXY_SUPPLY_TYPE=<OXY_SUPPLY_TYPE>, &
                          OXY_SUPPLY_RATE=<OXY_SUPPLY_RATE>, TEMPERATURE=<TEMPERATURE>, CARDIOTACH=<CARDIOTACH>, &
                          RESPIRATORY_RATE=<RESPIRATORY_RATE>, NBPS=<NBPS>, NBPD=<NBPD>, SPO2=<SPO2> &
                          WHERE TRIAGE_NO=<TRIAGE_NO> AND VS_TIME=<VS_TIME>
updateAmiErdVtsRecord.Debug=Y

// add by wangqing 20171025 护士签名
updateAmiErdVtsRecord1.Type=TSQL
updateAmiErdVtsRecord1.SQL=UPDATE AMI_ERD_VTS_RECORD SET SIGN=<SIGN> &
                          WHERE TRIAGE_NO=<TRIAGE_NO> AND VS_TIME=<VS_TIME> AND (SIGN IS NULL OR LENGTH(SIGN)=0)
updateAmiErdVtsRecord1.Debug=Y

// add by wangqing 20171025 取消护士签名
updateAmiErdVtsRecord2.Type=TSQL
updateAmiErdVtsRecord2.SQL=UPDATE AMI_ERD_VTS_RECORD SET SIGN=<SIGN> &
                          WHERE TRIAGE_NO=<TRIAGE_NO> AND VS_TIME=<VS_TIME> AND SIGN IS NOT NULL
updateAmiErdVtsRecord2.Debug=Y









