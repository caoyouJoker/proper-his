#
# TBuilder Config File 
#
# Title:医保申报
#
# Company: ProperSoft
#
# Author:王亮 2012.02.10
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;readCard;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;readCard;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

readCard.Type=TMenuItem
readCard.Text=刷卡
readCard.Tip=刷卡
readCard.M=N
readCard.Action=onReadCard
readCard.pic=008.gif
