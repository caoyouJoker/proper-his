<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;clear;|;returnFee;|;print;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;clear;returnFee;|;Refresh;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif


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

returnFee.Type=TMenuItem
returnFee.Text=退费
returnFee.Tip=退费
returnFee.M=P
returnFee.Action=onReturnFee
returnFee.pic=030.gif

bedcard.Type=TMenuItem
bedcard.Text=结算
bedcard.Tip=结算
bedcard.M=B
bedcard.Action=onBedCard
bedcard.pic=014.gif

print.Type=TMenuItem
print.Text=补印
print.Tip=补印
print.M=P
print.key=
print.Action=onPrint
print.pic=print.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

