# 
#  Title:�������
# 
#  Description:�������
# 
#  Copyright: Copyright (c) Javahis 2015
# 
#  author wangl 2015.9.22
#  version 1.0
#
<Type=TMenuBar>
UI.Item=File;Window
UI.button=new;save;|;Query;|;Wrist;|;firstECG;|;monitor;|;ERD_NURSE;|;clear;ERD_SAVE;|;FALL_AND_PAIN_ASSESSMENT;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=new;save;Refresh;clear;|;close

ERD_SAVE.Type=TMenuItem
ERD_SAVE.Text=��������
ERD_SAVE.Tip=��������
ERD_SAVE.M=
ERD_SAVE.key=
ERD_SAVE.Action=onErdSave
ERD_SAVE.pic=save.gif


Refresh.Type=TMenuItem
Refresh.Text=ˢ��
Refresh.Tip=ˢ��
Refresh.M=R
Refresh.key=F5
Refresh.Action=onReset
Refresh.pic=Refresh.gif

new.Type=TMenuItem
new.Text=����
new.Tip=����
new.M=A
new.key=Ctrl+A
new.Action=onNew
new.pic=new.gif

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=E
clear.key=Ctrl+E
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�ر�
close.Tip=�ر�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

Wrist.Type=TMenuItem
Wrist.Text=����
Wrist.Tip=����
Wrist.M=W
Wrist.key=Ctrl+W
Wrist.Action=onWrist
Wrist.pic=print-1.gif

firstECG.Type=TMenuItem
firstECG.Text=�״��ĵ�
firstECG.Tip=�״��ĵ�
firstECG.M=W
firstECG.key=Ctrl+1
firstECG.Action=onFirstECG
firstECG.pic=query.gif

monitor.Type=TMenuItem
monitor.Text=�໤��
monitor.Tip=�໤��
monitor.M=
monitor.key=
monitor.Action=onMonitor
monitor.pic=query.gif

ERD_NURSE.Type=TMenuItem
ERD_NURSE.Text=��ʹ���ﻤʿ
ERD_NURSE.Tip=��ʹ���ﻤʿ
ERD_NURSE.M=
ERD_NURSE.key=
ERD_NURSE.Action=onErdNurse
ERD_NURSE.pic=query.gif

Query.Type=TMenuItem
Query.Text=��ѯ
Query.Tip=��ѯ
Query.M=X
Query.key=Alt+F4
Query.Action=onQuery
Query.pic=Query.gif

FALL_AND_PAIN_ASSESSMENT.Type=TMenuItem
FALL_AND_PAIN_ASSESSMENT.Text=��������ʹ����
FALL_AND_PAIN_ASSESSMENT.Tip=��������ʹ����
FALL_AND_PAIN_ASSESSMENT.M=
FALL_AND_PAIN_ASSESSMENT.key=
FALL_AND_PAIN_ASSESSMENT.Action=onFallAndPainAssessment
FALL_AND_PAIN_ASSESSMENT.pic=Query.gif