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
UI.button=new;Query;tempsave;Wrist;clear;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=Refresh;clear;|;close

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

new.Type=TMenuItem
new.Text=新增
new.Tip=新增(Ctrl+N)
new.M=N
new.key=Ctrl+N
new.Action=onNew
new.pic=New.gif

Query.Type=TMenuItem
Query.Text=查询
Query.Tip=查询(Ctrl+Q)
Query.M=Q
Query.key=Ctrl+Q
Query.Action=onQuery
Query.pic=Query.gif

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

tempsave.Type=TMenuItem
tempsave.Text=检伤修改
tempsave.zhText=检伤修改
tempsave.enText=Pending
tempsave.Tip=检伤修改
tempsave.zhTip=检伤修改
tempsave.enTip=Pending
tempsave.M=T
tempsave.key=Ctrl+T
tempsave.Action=onUpdate
tempsave.pic=tempsave.gif
