###############################################
# <p>Title:床位检索Menu </p>
#
# <p>Description:床位检索Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY
# @version 1.0
###############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.enTip=save
save.enText=save
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif
