#
  # Title: ������Ŀ���������ϴ�����
  #
  # Description:������Ŀ���������ϴ�����
  #
  # Copyright: ProperSoft (c) 2014
  #
  # @author zhangs
  # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=saveUp;|;save;|;clear;|;close;

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=saveUp;|;save;|;clear;|;close

saveUp.Type=TMenuItem
saveUp.Text=���沢�ϴ�
saveUp.Tip=���沢�ϴ�
saveUp.M=Q
saveUp.key=Ctrl+F
saveUp.Action=onSaveAddUp
saveUp.pic=032.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=E
save.key=F4
save.Action=onSave
save.pic=save.gif