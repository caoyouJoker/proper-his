#############################################
# <p>Title:急诊抢救报表打印预览Menu </p>
#
# <p>Description:急诊抢救报表打印预览Menu </p>
#
# <p>Copyright: Copyright (c) 2017</p>
#
# <p>Company: Javahis</p>
#
# @author wangqing 20170922
# @version 5.0
#############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=print;|;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=print;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

Refresh.Type=TMenuItem
Refresh.Text=刷新
Refresh.Tip=刷新
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

print.Type=TMenuItem
print.Text=打印上传
print.Tip=打印上传
print.M=
print.key=
print.Action=onPrint
print.pic=save.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
