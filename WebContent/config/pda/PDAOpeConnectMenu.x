<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;fromuser;|;touser;|;PrintShow;|;exit

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.zhText=�ļ�
File.enText=File
File.M=F
File.Item=exit

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

fromuser.Type=TMenuItem
fromuser.Text=������ǩ��
fromuser.Tip=������ǩ��
fromuser.M=S
fromuser.key=Ctrl+S
fromuser.Action=onFromuser
fromuser.pic=bedcard.gif

touser.Type=TMenuItem
touser.Text=�Ӱ���ǩ��
touser.Tip=�Ӱ���ǩ��
touser.M=S
touser.key=Ctrl+S
touser.Action=onTouser
touser.pic=bedcard.gif

PrintShow.type=TMenuItem
PrintShow.Text=��ӡ
PrintShow.zhText=��ӡ
PrintShow.enText=Print
PrintShow.Tip=��ӡ
PrintShow.zhTip=��ӡ
PrintShow.enTip=Print
PrintShow.M=P
PrintShow.key=
PrintShow.Action=onPrint
PrintShow.pic=print.gif

exit.Type=TMenuItem
exit.Text=�ر�
exit.zhText=�ر�
exit.enText=Quit
exit.Tip=�ر�
exit.zhTip=�ر�
exit.enTip=Quit
exit.M=C
exit.key=Alt+F4
exit.Action=onClose
exit.pic=close.gif
