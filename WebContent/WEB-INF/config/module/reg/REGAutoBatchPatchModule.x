# 
#  Title:# ������ҹ������
#  Description:������ҹ������
# 
#  Copyright: Copyright (c) # bluecore
#  author caowl
#  version 1.0
#
Module.item=updMissCount;updBlackFlg;removeBlackFlg

//����ˬԼ����
updMissCount.Type=TSQL
updMissCount.SQL=UPDATE SYS_PATINFO SET MISS_COUNT = MISS_COUNT +1 WHERE MR_NO = <MR_NO>
selectdata.Debug=N



//���º��������
updBlackFlg.Type=TSQL
updBlackFlg.SQL=UPDATE SYS_PATINFO SET BLACK_FLG = 'Y',BLACK_DATE=SYSDATE WHERE MR_NO = <MR_NO>
updBlackFlg.Debug=N

//������������
removeBlackFlg.Type=TSQL
removeBlackFlg.SQL=UPDATE SYS_PATINFO SET BLACK_FLG = 'N',BLACK_DATE='',MISS_COUNT=0 WHERE MR_NO = <MR_NO>
removeBlackFlg.Debug=N

