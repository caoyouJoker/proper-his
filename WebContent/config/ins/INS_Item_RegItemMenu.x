#
  # Title: 诊疗项目备案新增上传管理
  #
  # Description:诊疗项目备案新增上传管理
  #
  # Copyright: ProperSoft (c) 2014
  #
  # @author zhangs
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=saveUp;|;save;|;clear;|;close;

Window.Type=TMenu
Window.Text=窗口
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=文件
File.M=F
File.Item=saveUp;|;save;|;clear;|;close

saveUp.Type=TMenuItem
saveUp.Text=保存并上传
saveUp.Tip=保存并上传
saveUp.M=Q
saveUp.key=Ctrl+F
saveUp.Action=onSaveAddUp
saveUp.pic=032.gif

clear.Type=TMenuItem
clear.Text=清空
clear.Tip=清空
clear.M=C
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

save.Type=TMenuItem
save.Text=保存
save.Tip=保存
save.M=E
save.key=F4
save.Action=onSave
save.pic=save.gif