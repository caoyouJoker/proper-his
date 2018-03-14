 #
  # Title: HRM阳性检查结果
  #
  # Description:HRM阳性检查结果
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=Window
UI.button=print;execle;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=print;execle;clear;|;close

print.Type=TMenuItem
print.Text=补印
print.Tip=补印
print.M=C
print.Action=onPrint
print.pic=print.gif

execle.Type=TMenuItem
execle.Text=导出
execle.Tip=导出
execle.M=I
execle.Action=onExecl
execle.pic=export.gif

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
