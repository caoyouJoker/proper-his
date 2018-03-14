##############################################
# <p>Title:出院通知Menu </p>
#
# <p>Description:出院通知Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author zhangk  2010-2-25
# @version 1.0
##############################################
<Type=TMenuBar>
UI.Item=File
UI.button=save;|;clear;|;print;|;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;clear;|;print;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

print.Type=TMenuItem
print.Text=住院证打印
print.Tip=住院证打印
print.M=
print.key=
print.Action=onPrint
print.pic=print.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空(Ctrl+Z)
clear.M=Z
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

CPCOutcome.Type=TMenuItem
CPCOutcome.Text=胸痛转归
CPCOutcome.Tip=胸痛转归
CPCOutcome.M=X
CPCOutcome.key=Alt+F4
CPCOutcome.Action=onCPCOoutcome
CPCOutcome.pic=Button.gif
