#############################################
# <p>Title:科常用手术码选择界面Menu </p>
#
# <p>Description:科常用手术码选择界面Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author ZhangK 2009.09.26
# @version 4.0
#############################################
<Type=TMenuBar>
UI.Item=File
UI.button=back;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.zhText=文件
File.enText=File
File.M=F
File.Item=save;|;close

back.Type=TMenuItem
back.Text=回传选中值
back.zhText=回传选中值
back.enText=Fetch
back.Tip=回传选中值
back.zhTip=回传选中值
back.enTip=Fetch
back.Action=onBack
back.pic=Undo.gif

close.Type=TMenuItem
close.Text=退出
close.zhText=退出
close.enText=Close
close.Tip=退出(Alt+F4)
close.zhTip=退出
close.enTip=Close
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
