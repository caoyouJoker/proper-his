#
  # Title: ת���ҽ�Ǽ�����/����
  #
  # Description:ת���ҽ�Ǽ�����/����
  #
  # Copyright: JavaHis (c) 2011
  #
  # @author pangben 2011-11-30
  # @version 2.0
<Type=TMenuBar>
UI.Item=File;Window
UI.button=save;|;admNClose;|;print;|;readCard;|;confirmQuery;|;revoke;|;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;|;admNClose;|;print;|;readCard;|;confirmQuery;|;revoke;|;clear;|;close

save.Type=TMenuItem
save.Text=ת���ҽ�Ǽ�����/����
save.Tip=ת���ҽ�Ǽ�����/����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=Commit.gif

revoke.Type=TMenuItem
revoke.Text=����
revoke.Tip=����
revoke.M=N
revoke.Action=onRevoke
revoke.pic=046.gif

readCard.Type=TMenuItem
readCard.Text=ˢ��
readCard.Tip=ˢ��
readCard.M=N
readCard.Action=onReadCard
readCard.pic=008.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

admNClose.Type=TMenuItem
admNClose.Text=סԺδ�᰸
admNClose.Tip=סԺδ�᰸
admNClose.Action=onAdmNClose
admNClose.pic=048.gif

confirmQuery.Type=TMenuItem
confirmQuery.Text=ת���ҽ�Ǽ���ʷ��¼��ѯ
confirmQuery.Tip=ת���ҽ�Ǽ���ʷ��¼��ѯ
confirmQuery.Action=onConfirmNo
confirmQuery.pic=043.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���(Ctrl+Z)
clear.M=C
clear.key=Ctrl+Z
clear.Action=onClear
clear.pic=clear.gif

print.Type=TMenuItem
print.Text=ת���ҽ�ǼǴ�ӡ
print.Tip=ת���ҽ�ǼǴ�ӡ
print.M=S
print.key=
print.Action=onPrint
print.pic=print.gif