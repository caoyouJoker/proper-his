 #
  # Title: ÁÙ´²Â·¾¶Ì×²Í×Öµä
  #
  # Description: ÁÙ´²Â·¾¶Ì×²Í×Öµä
  #
  # Copyright: bluecore (c) 2015
  #
  # @author pangb 20150810
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;delete;query;clear;|;close

Window.Type=TMenu
Window.Text=´°¿Ú
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=ÎÄ¼þ
File.M=F
File.Item=save;delete;query;clear;|;close

save.Type=TMenuItem
save.Text=±£´æ
save.Tip=±£´æ(Ctrl+S)
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

delete.Type=TMenuItem
delete.Text=É¾³ý
delete.Tip=É¾³ý(Delete)
delete.M=N
delete.key=Delete
delete.Action=onDelete
delete.pic=delete.gif

query.Type=TMenuItem
query.Text=²éÑ¯
query.Tip=²éÑ¯(Ctrl+F)
query.M=Q
query.key=Ctrl+F
query.Action=onQuery
query.pic=query.gif

clear.Type=TMenuItem
clear.Text=Çå¿Õ
clear.Tip=Çå¿Õ(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

preview.Type=TMenuItem
preview.Text=ÁÙ´²Â·¾¶Ä£°å
preview.Tip=ÁÙ´²Â·¾¶Ä£°å(Ctrl+P)
preview.M=P
preview.key=Ctrl+P
preview.Action=onPreview
preview.pic=025.gif

close.Type=TMenuItem
close.Text=ÍË³ö
close.Tip=ÍË³ö(Alt+F4)
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Refresh.Type=TMenuItem
Refresh.Text=Ë¢ÐÂ
Refresh.Tip=Ë¢ÐÂ
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif