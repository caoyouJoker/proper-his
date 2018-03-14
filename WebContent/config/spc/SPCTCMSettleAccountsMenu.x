 #
  # Title: 非国药药品结算
  #
  # Description:非国药药品结算
  #
  # Copyright: JavaHis (c) 2017
  #
  # @author liuyl 2017.06.06
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;clear;|;export;|;print;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;clear;|;export;|;print;|;close


query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

export.Type=TMenuItem
export.Text=导出
export.Tip=导出
export.M=P
export.Action=onExport
export.pic=export.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=P
print.Action=onPrint
print.pic=print.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif



