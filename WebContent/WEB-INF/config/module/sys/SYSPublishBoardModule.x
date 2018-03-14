# 
#  Title:公告栏程序
# 
#  Description:公告栏程序
# 
#  Copyright: Copyright (c) Javahis 2011
# 
#  author li.xiang790130@gmail.com 2011.04.21
#
#  version 1.0
#
Module.item=queryByUserID;queryByDept;queryByRole;queryAll;inserPostRCV;inserBoard;updateBoardByMessageNo;deletePostRCV;deleteBoard;deleteRCVByUserIDMessNo;getBoardByMessageNo;updateResposeNo;updateReadFlag
//通过用户ID,获得接收用户
queryByUserID.Type=TSQL
queryByUserID.SQL=SELECT USER_ID,ROLE_ID,E_MAIL FROM SYS_OPERATOR WHERE USER_ID=<RECIPIENTS> and REGION_CODE=<REGION> and ACTIVE_DATE<=to_date(<CURRENT_DATE>,'YYYYMMDD') and END_DATE>=to_date(<CURRENT_DATE>,'YYYYMMDD')
queryByUserID.Debug=N

//通过部门，获得接收用户
queryByDept.Type=TSQL
queryByDept.SQL=SELECT a.USER_ID,a.ROLE_ID,E_MAIL FROM SYS_OPERATOR a LEFT JOIN SYS_OPERATOR_DEPT b on a.USER_ID=b.USER_ID WHERE DEPT_CODE=<RECIPIENTS> and REGION_CODE=<REGION> and ACTIVE_DATE<=to_date(<CURRENT_DATE>,'YYYYMMDD') and END_DATE>=to_date(<CURRENT_DATE>,'YYYYMMDD')
queryByDept.Debug=N

//通过角色，获得接收用户
queryByRole.Type=TSQL
queryByRole.SQL=SELECT USER_ID,ROLE_ID,E_MAIL FROM SYS_OPERATOR WHERE ROLE_ID=<RECIPIENTS> and REGION_CODE=<REGION> and ACTIVE_DATE<=to_date(<CURRENT_DATE>,'YYYYMMDD') and END_DATE>=to_date(<CURRENT_DATE>,'YYYYMMDD')
queryByRole.Debug=N

//获得所有人员
queryAll.Type=TSQL
queryAll.SQL=SELECT USER_ID,ROLE_ID,E_MAIL FROM SYS_OPERATOR WHERE REGION_CODE=<REGION> and ACTIVE_DATE<=to_date(<CURRENT_DATE>,'YYYYMMDD') and END_DATE>=to_date(<CURRENT_DATE>,'YYYYMMDD')
queryAll.Debug=N

//插入接收档
inserPostRCV.Type=TSQL
inserPostRCV.SQL=INSERT INTO SYS_POSTRCV(MESSAGE_NO,POST_TYPE,POST_GROUP,USER_ID,READ_FLG,OPT_USER,OPT_DATE,OPT_TERM) VALUES(<MESSAGE_NO>,<POST_TYPE>,<POST_GROUP>,<USER_ID>,<READ_FLG>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
inserPostRCV.Debug=N

//插入公告栏
inserBoard.Type=TSQL
inserBoard.SQL=INSERT INTO SYS_BOARD(MESSAGE_NO,POST_SUBJECT,URG_FLG,POST_INFOMATION,RESPONSE_NO,POST_ID,POST_TIME,OPT_USER,OPT_DATE,OPT_TERM) VALUES(<MESSAGE_NO>,<POST_SUBJECT>,<URG_FLG>,<POST_INFOMATION>,<RESPONSE_NO>,<POST_ID>,<OPT_DATE>,<OPT_USER>,<OPT_DATE>,<OPT_TERM>)
inserBoard.Debug=N

//更新公告
updateBoardByMessageNo.Type=TSQL
updateBoardByMessageNo.SQL=UPDATE SYS_BOARD SET POST_SUBJECT=<POST_SUBJECT>,URG_FLG=<URG_FLG>,POST_INFOMATION=<POST_INFOMATION> WHERE MESSAGE_NO=<MESSAGE_NO>
updateBoardByMessageNo.Debug=N


//删除接收档
deletePostRCV.Type=TSQL
deletePostRCV.SQL=DELETE FROM SYS_POSTRCV WHERE MESSAGE_NO=<MESSAGE_NO>
deletePostRCV.Debug=N

//删除公告栏
deleteBoard.Type=TSQL
deleteBoard.SQL=DELETE FROM SYS_BOARD WHERE MESSAGE_NO=<MESSAGE_NO>
deleteBoard.Debug=N

//通过用户ID和消息号删除接收档记录
deleteRCVByUserIDMessNo.Type=TSQL
deleteRCVByUserIDMessNo.SQL=DELETE FROM SYS_POSTRCV WHERE MESSAGE_NO=<MESSAGE_NO> and USER_ID=<USER_ID>
deleteRCVByUserIDMessNo.Debug=N

//取详细公告；
getBoardByMessageNo.Type=TSQL
getBoardByMessageNo.SQL=SELECT POST_SUBJECT,POST_INFOMATION,RESPONSE_NO FROM SYS_BOARD WHERE MESSAGE_NO=<MESSAGE_NO>
getBoardByMessageNo.Debug=N

//更新响应数
updateResposeNo.Type=TSQL
updateResposeNo.SQL=UPDATE SYS_BOARD SET RESPONSE_NO=<RESPONSE_NO> WHERE MESSAGE_NO=<MESSAGE_NO>
updateResposeNo.Debug=N

//更新已读标记
updateReadFlag.Type=TSQL
updateReadFlag.SQL=UPDATE SYS_POSTRCV SET READ_FLG='Y' WHERE MESSAGE_NO=<MESSAGE_NO> AND USER_ID=<USER_ID>
updateReadFlag.Debug=N