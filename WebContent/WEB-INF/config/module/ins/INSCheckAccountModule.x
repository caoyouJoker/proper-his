# 
#  Title:医保门诊对账module
# 
#  Description:医保门诊对账 INS_CHECKACCOUNT 表module
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author pangben 2012-1-11
#  version 1.0
#

Module.item=insertInsCheckAccount;queryInsCheckAccount

//添加对账结算数据
insertInsCheckAccount.Type=TSQL
insertInsCheckAccount.SQL=INSERT INTO INS_CHECKACCOUNT&
				(CHECK_DATE, CHECK_TYPE, TOTAL_AMT, &
				NHI_AMT, OWN_AMT, ADDPAY_AMT, &
				OTOT_AMT, OTOT_AMT_B, ACCOUNT_PAY_AMT, &
				ACCOUNT_PAY_AMT_B, APPLY_AMT, APPLY_AMT_B, &
				FLG_AGENT_AMT, FLG_AGENT_AMT_B, SUM_PERTIME, &
				SUM_PERTIME_B, ARMY_AI_AMT, ARMY_AI_AMT_B, &
				SERVANT_AMT, SERVANT_AMT_B, MZ_AGENT_AMT, &
				MZ_AGENT_AMT_B, FY_AGENT_AMT, FY_AGENT_AMT_B, &
				FD_AGENT_AMT, FD_AGENT_AMT_B, UNREIM_AMT, &
				UNREIM_AMT_B, OPT_USER, OPT_DATE, OPT_TERM)&
				VALUES(<CHECK_DATE>, <CHECK_TYPE>, <TOTAL_AMT>, &
				<NHI_AMT>, <OWN_AMT>, <ADDPAY_AMT>, &
				<OTOT_AMT>, <OTOT_AMT_B>, <ACCOUNT_PAY_AMT>, &
				<ACCOUNT_PAY_AMT_B>, <APPLY_AMT>, <APPLY_AMT_B>, &
				<FLG_AGENT_AMT>, <FLG_AGENT_AMT_B>, <SUM_PERTIME>, &
				<SUM_PERTIME_B>, <ARMY_AI_AMT>, <ARMY_AI_AMT_B>, &
				<SERVANT_AMT>, <SERVANT_AMT_B>, <MZ_AGENT_AMT>, &
				<MZ_AGENT_AMT_B>, <FY_AGENT_AMT>, <FY_AGENT_AMT_B>, &
				<FD_AGENT_AMT>, <FD_AGENT_AMT_B>, <UNREIM_AMT>, &
				<UNREIM_AMT_B>, <OPT_USER>, SYSDATE, <OPT_TERM>)
insertInsCheckAccount.Debug=N

//查询是否存在数据 根据日期查询

queryInsCheckAccount.Type=TSQL
queryInsCheckAccount.SQL=SELECT CHECK_DATE FROM INS_CHECKACCOUNT WHERE CHECK_DATE=<CHECK_DATE>
queryInsCheckAccount.Debug=N