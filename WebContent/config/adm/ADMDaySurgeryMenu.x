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
UI.button=save;|;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
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