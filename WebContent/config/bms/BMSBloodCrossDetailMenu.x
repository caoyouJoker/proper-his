 #
  # Title: 
  #
  # Description: 
  #
  # Copyright: JavaHis (c) 2009
  #
  # @author zhangy 2009.09.22
 # @version 1.0
<Type=TMenuBar>  
UI.Item=File;Window
UI.button=clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=clear;|;close

clear.Type=TMenuItem
clear.Text=��ѯ
clear.Tip=��ѯ(Ctrl+F)
clear.M=Q
clear.key=Ctrl+F
clear.Action=onApplyNoAction
clear.pic=clear.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif