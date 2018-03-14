#################################################
# <p>Title:新生儿登记Menu </p>
#
# <p>Description:新生儿登记Menu </p>
#
# <p>Copyright: Copyright (c) 2008</p>
#
# <p>Company:Javahis </p>
#
# @author JiaoY 2009.04.30
# @version 1.0
#################################################
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;patinfo;|;clear;|;close

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=save;|;patinfo;|;close

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

patinfo.Type=TMenuItem
patinfo.Text=病患查询
patinfo.Tip=病患查询
patinfo.M=
patinfo.key=
patinfo.Action=onPatInfo
patinfo.pic=search-1.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=X
clear.key=
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=退出
close.Tip=退出
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif