# 
#  Title:挂号主档
# 
#  Description:挂号主档
# 
#  Copyright: Copyright (c) Javahis 2008
# 
#  author wangl 2008.11.03
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=unreg;arrive;;clear;print;|;Wrist;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=unreg;arrive;Refresh;clear;print;|;close

unreg.Type=TMenuItem
unreg.Text=退挂
unreg.Tip=退挂
unreg.M=U
unreg.key=
unreg.Action=onUnReg
unreg.pic=030.gif

arrive.Type=TMenuItem
arrive.Text=报到
arrive.Tip=报到
arrive.M=A
arrive.key=
arrive.Action=onArrive
arrive.pic=017.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

print.Type=TMenuItem
print.Text=补印
print.Tip=补印
print.M=P
print.key=
print.Action=onPrint
print.pic=print.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
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
Wrist.Text=打印腕带
Wrist.Tip=打印腕带
Wrist.M=
Wrist.key=
Wrist.Action=onWrist
Wrist.pic=print-1.gif