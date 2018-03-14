 #
  # Title: 科室费用查询
  #
  # Description: 科室费用查询
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.04.22
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=query;|;clear;|;print;|;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;print;|;export;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印(Ctrl+P)
print.M=C
print.key=Ctrl+P
print.Action=onPrint
print.pic=print.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出(Ctrl+E)
export.M=C
export.key=Ctrl+E
export.Action=onExport
export.pic=export.gif


close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


