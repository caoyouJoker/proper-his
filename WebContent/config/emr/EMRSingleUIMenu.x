<Type=TMenuBar>
UI.Item=File;Window
UI.button=open;print;exit

Window.Type=TMenu
Window.Text=窗口
Window.zhText=窗口
Window.enText=Window
Window.M=W
Window.Item=open

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=exit

open.Type=TMenuItem
open.Text=打开
open.zhText=打开
open.enText=Open
open.Tip=打开
open.zhTip=打开
open.enTip=Open
open.M=O
open.key=Ctrl+S
open.Action=onOpen
open.pic=emr-2.gif

print.Type=TMenuItem
print.Text=病理条码打印
print.Tip=病理条码打印(Ctrl+P)
print.M=S
print.key=Ctrl+p
print.Action=onPrint
print.pic=print.gif

exit.Type=TMenuItem
exit.Text=关闭
exit.zhText=关闭
exit.enText=Close
exit.Tip=关闭
exit.zhTip=关闭
exit.enTip=Close
exit.M=X
exit.key=Alt+F4
exit.Action=onClose
exit.pic=close.gif