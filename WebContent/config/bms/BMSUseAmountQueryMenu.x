 #
  # Title: 用血量统计
  #
  # Description: 用血量统计
  #
  # Copyright: JavaHis (c) 2016
  #
  # @author wangb 2016.04.11
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=query;|;clear;|;print;|;export;|;close

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
File.Item=query;|;clear;|;print;|;export;|;close


query.Type=TMenuItem
query.Text=查询
query.zhText=查询
query.enText=Query
query.Tip=查询(Ctrl+F)
query.zhTip=查询(Ctrl+F)
query.enTip=Query(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.zhText=刷新
Refresh.enText=Refresh
Refresh.Tip=刷新
Refresh.zhTip=刷新
Refresh.enTip=Refresh
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.zhText=清空
clear.enText=Clear
clear.Tip=清空(Ctrl+Z)
clear.zhTip=清空(Ctrl+Z)
clear.enTip=Clear(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印(Ctrl+P)
print.M=P
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

export.Type=TMenuItem
export.Text=导出Excel
export.Tip=导出Excel(Ctrl+E)
export.M=E
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Close
close.Tip=退出(Alt+F4)
close.zhTip=退出(Alt+F4)
close.enTip=Close(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
