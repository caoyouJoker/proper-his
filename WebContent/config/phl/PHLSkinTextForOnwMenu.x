#
# TBuilder Config File 
#
# Title:
#
# Company:JavaHis
#
# Author:yanjing 2014.08.18
#
# version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;query;|;ekt;|;skiResult;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;query;|;ekt;|;skiResult;|;clear;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=查询
query.Tip=查询(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

ekt.Type=TMenuItem
ekt.Text=医疗卡
ekt.Tip=医疗卡
ekt.M=S
ekt.key=
ekt.Action=onEkt
ekt.pic=042.gif

skiResult.Type=TMenuItem
skiResult.Text=皮试结果
skiResult.Tip=皮试结果
skiResult.M=P
skiResult.key=Ctrl+P
skiResult.Action=onSkiResult
skiResult.pic=032.gif


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
