 #
  # Title: 门诊收费人次查询
  #
  # Description:门诊收费人次查询
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author yanmm
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=query;print;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;query;print;|;clear;|;close

query.Type=TMenuItem
query.Text=查询
query.Tip=查询
query.M=N
query.key=F5
query.Action=onQuery
query.pic=query.gif


clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

print.Type=TMenuItem
print.Text=打印
print.Tip=打印
print.M=X
print.key=Alt+F4
print.Action=onPrint
print.pic=print.gif
