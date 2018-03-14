Module.item=needExamine;needExamineD;dispEqualSend

//判断门急诊西药是否需要审核后配药（Y-是/N-不是）
needExamine.Type=TSQL
needExamine.SQL=SELECT EXAMINEOE_FLG FROM PHA_SYSPARM
needExamine.Debug=N

//判断门急诊中药是否需要审核后配药（Y-是/N-不是）
needExamineD.Type=TSQL
needExamineD.SQL=SELECT EXAMINEOED_FLG FROM PHA_SYSPARM
needExamineD.Debug=N

//判断门急诊是否是配药即发药（Y-是/N-不是）
dispEqualSend.Type=TSQL
dispEqualSend.SQL=SELECT DGTSENDI_FLG FROM PHA_SYSPARM
dispEqualSend.Debug=N

