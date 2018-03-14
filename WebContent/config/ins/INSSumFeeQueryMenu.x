#############################################
# <p>Title:每日费用清单 </p>
#
# <p>Description:每日费用清单 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2010.09.28
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;printview;|;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=query;|;printview;|;print;|;clear;|;close


query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

printview.Type=TMenuItem
printview.Text=打印预览
printview.Tip=打印预览(Ctrl+P)
printview.M=P
printview.key=Ctrl+P
printview.Action=onPrintView
printview.pic=print.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=
print.key=
print.Action=onPrint
print.pic=print-1.gif


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.zhText=刷新
Refresh.enText=Refresh
Refresh.Tip=刷新(F5)
Refresh.zhTip=刷新
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Empty
clear.Tip=清空(Ctrl+Z)
clear.zhTip=清空
clear.enTip=Empty
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Quit
close.Tip=退出(Alt+F4)
close.zhTip=退出
close.enTip=Quit
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

.