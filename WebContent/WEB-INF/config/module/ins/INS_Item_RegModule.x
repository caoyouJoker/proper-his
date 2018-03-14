# 
#  Title:备案管理module
# 
#  Description:备案管理module
# 
#  Copyright: Copyright (c) Javahis 2014
# 
#  author zhangs 2014.08.21
#  version 1.0
#
Module.item=upInsItemReg;downUpInsItemReg;insertInsItemReg;updateInsItemReg
//备案信息上传更新数据
upInsItemReg.Type=TSQL
upInsItemReg.SQL=UPDATE INS_ITEM_REG SET &
			       APROVE_TYPE=<APROVE_TYPE>, &
                           OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		    WHERE INS_TYPE=<INS_TYPE> AND NHI_CODE=<NHI_CODE> AND START_DATE=<START_DATE> 
upInsItemReg.Debug=Y

//备案信息下载更新数据
downUpInsItemReg.Type=TSQL
downUpInsItemReg.SQL=UPDATE INS_ITEM_REG SET &
			   NHI_ORDER_DESC=<NHI_ORDER_DESC>,APPROVE_TYPE=<APPROVE_TYPE>,END_DATE=<END_DATE>, &
                           OPT_USER=<OPT_USER>,OPT_DATE=SYSDATE,OPT_TERM=<OPT_TERM> &
		    WHERE INS_TYPE=<INS_TYPE> AND NHI_CODE=<NHI_CODE> AND START_DATE=<START_DATE>
downUpInsItemReg.Debug=Y


//诊疗项目备案信息新增
insertInsItemReg.Type=TSQL
insertInsItemReg.SQL=INSERT INTO INS_ITEM_REG &
        (INS_TYPE,ORDER_CODE,NHI_CODE,START_DATE, & 
        ORDER_DESC,REG_TYPE,PRICE, &
        OPT_USER,OPT_TERM,OPT_DATE,APPROVE_TYPE, & 
        NHI_ORDER_DESC,ORDER_TYPE,OWN_PRICE,TOT_QTY,REG_CLASS,DISEASE_CODE) &
    VALUES ( <INS_TYPE>,<ORDER_CODE>,<NHI_CODE>,<START_DATE>, & 
             <ORDER_DESC>,<REG_TYPE>,<PRICE>, & 
             <OPT_USER>,<OPT_TERM>,SYSDATE,<APPROVE_TYPE>, & 
             <NHI_ORDER_DESC>,<ORDER_TYPE>,<OWN_PRICE>,<TOT_QTY>,<REG_CLASS>,<DISEASE_CODE>)
insertInsItemReg.Debug=Y

//备案信息更新数据
updateInsItemReg.Type=TSQL
updateInsItemReg.SQL=UPDATE INS_ITEM_REG SET  &
    INS_TYPE       = <INS_TYPE>,  & 
    ORDER_CODE     = <ORDER_CODE>,  &
    NHI_CODE       = <NHI_CODE>,  &
    START_DATE     = <START_DATE>,  &
    CHANGE_DATE    = <CHANGE_DATE>,  &
    ORDER_DESC     = <ORDER_DESC>,  &
    REG_TYPE       = <REG_TYPE>,  &
    PRICE          = <PRICE>,   &
    OPT_USER       = <OPT_USER>,  &
    OPT_TERM       = <OPT_TERM>,  &
    OPT_DATE       = SYSDATE,  &
    NHI_ORDER_DESC = <NHI_ORDER_DESC>,  &
    ORDER_TYPE     = <ORDER_TYPE>,   &
    OWN_PRICE      = <OWN_PRICE>,   &
    TOT_QTY        = <TOT_QTY>,   &
    REG_CLASS      =<REG_CLASS>,  &
    DISEASE_CODE   =<DISEASE_CODE>
WHERE                     &
    INS_TYPE       = <INS_TYPE> &        
    and ORDER_CODE = <ORDER_CODE> &
    and NHI_CODE   = <NHI_CODE> &
    and START_DATE = <START_DATE>
updateInsItemReg.Debug=Y

