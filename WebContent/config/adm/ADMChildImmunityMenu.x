##############################################
# <p>Title:新生儿检验信息 </p>
#
# <p>Description:新生儿检验信息 </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company: Javahis</p>
#
# @author zhangk 2009-12-10
# @version 4.0
##############################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;close

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif