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

UI.button=Query;|;save;|;close;


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

Submit.Type=TMenuItem
Submit.Text=提交
Submit.Tip=提交
Submit.M=W
Submit.Action=onWrist
Submit.pic=015.gif

Wrist.Type=TMenuItem
Wrist.Text=条码
Wrist.Tip=条码
Wrist.M=W
Wrist.key=Ctrl+W
Wrist.Action=onWrist
Wrist.pic=print-1.gif




Query.Type=TMenuItem
Query.Text=查询
Query.Tip=查询
Query.M=X
Query.key=Alt+F4
Query.Action=onQuery
Query.pic=Query.gif