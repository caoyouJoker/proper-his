# 
#  Title:急诊抢救记录
# 
#  Description:急诊抢救记录
# 
#  Copyright: Copyright (c) Javahis 2015
# 
#  author WangQing 20170327
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=Query;|;save;|;Refresh;|;clear;|;print;|;sign;|;cancelSign;|;order;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=new;save;Refresh;clear;|;close

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onResets
Refresh.pic=Refresh.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增
new.M=A
new.key=Ctrl+A
new.Action=onNew
new.pic=new.gif

break.Type=TMenuItem
break.Text=刷新
break.Tip=刷新
break.M=A
break.Action=onBreak
break.pic=tempsave.gif

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


print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.Action=onPrint
print.pic=print.gif

Query.Type=TMenuItem
Query.Text=查询
Query.Tip=查询
Query.M=X
Query.key=Alt+F4
Query.Action=onQuery
Query.pic=Query.gif

sign.Type=TMenuItem
sign.Text=护士签名
sign.Tip=护士签名
sign.M=R
sign.key=
sign.Action=onSign
sign.pic=Refresh.gif

cancelSign.Type=TMenuItem
cancelSign.Text=取消签名
cancelSign.Tip=取消签名
cancelSign.M=
cancelSign.key=
cancelSign.Action=onCancelSign
cancelSign.pic=Refresh.gif

order.Type=TMenuItem
order.Text=口头医嘱
order.Tip=口头医嘱
order.M=
order.key=
order.Action=onOrder
order.pic=Refresh.gif


