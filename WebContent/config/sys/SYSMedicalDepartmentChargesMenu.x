 #
  # Title: 门诊收费报表
  #
  # Description: 医技科室收费套餐报表
  #
  # Copyright: JavaHis (c) 2017
  #
  # @author liuyalin 2017.04.27
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=clear;|;export;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=clear;|;export;|;close


Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

export.Type=TMenuItem
export.Text=汇出
export.Tip=汇出
execrpt.M=E
export.Action=onExport
export.pic=export.gif