#
# TBuilder Config File 
#
# Title:门特处方查询
#
# Company:BlueCore
#
# Author:wangl 2012.02.17
#
# version 1.0
#

<Type=TMenuBar>
UI.Item=File;Window
UI.button=close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=close

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

