 #
  # Title: 人头及病种付费月清算表信息下载
  #
  # Description: 人头及病种付费月清算表信息下载
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author lim 2016.12.05
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;|;print;|;sinprint;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=query;|;print;|;sinprint;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=Q
query.Action=onQuery
query.pic=query.gif

print.Type=TMenuItem
print.Text=病种清算表
print.Tip=病种清算表
print.M=P
print.key=Ctrl+P
print.Action=DataDown_zjkd_N
print.pic=print.gif

sinprint.Type=TMenuItem
sinprint.Text=病种汇总表
sinprint.Tip=病种汇总表
sinprint.M=P
sinprint.key=Ctrl+S
sinprint.Action=onSinPrint
sinprint.pic=print.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
