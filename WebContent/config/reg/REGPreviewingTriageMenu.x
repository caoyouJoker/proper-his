# 
#  Title:急诊分诊
# 
#  Description:急诊分诊
# 
#  Copyright: Copyright (c) Javahis 2015
# 
#  author wangl 2015.9.22
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=new;save;|;Query;|;Wrist;|;firstECG;|;monitor;|;ERD_NURSE;|;clear;ERD_SAVE;|;FALL_AND_PAIN_ASSESSMENT;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=new;save;Refresh;clear;|;close

ERD_SAVE.Type=TMenuItem
ERD_SAVE.Text=急诊抢救
ERD_SAVE.Tip=急诊抢救
ERD_SAVE.M=
ERD_SAVE.key=
ERD_SAVE.Action=onErdSave
ERD_SAVE.pic=save.gif


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增
new.M=A
new.key=Ctrl+A
new.Action=onNew
new.pic=new.gif

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=E
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=关闭
close.Tip=关闭
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Wrist.Type=TMenuItem
Wrist.Text=条码
Wrist.Tip=条码
Wrist.M=W
Wrist.key=Ctrl+W
Wrist.Action=onWrist
Wrist.pic=print-1.gif

firstECG.Type=TMenuItem
firstECG.Text=首次心电
firstECG.Tip=首次心电
firstECG.M=W
firstECG.key=Ctrl+1
firstECG.Action=onFirstECG
firstECG.pic=query.gif

monitor.Type=TMenuItem
monitor.Text=监护仪
monitor.Tip=监护仪
monitor.M=
monitor.key=
monitor.Action=onMonitor
monitor.pic=query.gif

ERD_NURSE.Type=TMenuItem
ERD_NURSE.Text=胸痛急诊护士
ERD_NURSE.Tip=胸痛急诊护士
ERD_NURSE.M=
ERD_NURSE.key=
ERD_NURSE.Action=onErdNurse
ERD_NURSE.pic=query.gif

Query.Type=TMenuItem
Query.Text=查询
Query.Tip=查询
Query.M=X
Query.key=Alt+F4
Query.Action=onQuery
Query.pic=Query.gif

FALL_AND_PAIN_ASSESSMENT.Type=TMenuItem
FALL_AND_PAIN_ASSESSMENT.Text=跌倒、疼痛评估
FALL_AND_PAIN_ASSESSMENT.Tip=跌倒、疼痛评估
FALL_AND_PAIN_ASSESSMENT.M=
FALL_AND_PAIN_ASSESSMENT.key=
FALL_AND_PAIN_ASSESSMENT.Action=onFallAndPainAssessment
FALL_AND_PAIN_ASSESSMENT.pic=Query.gif