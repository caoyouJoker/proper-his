 #
  # Title: HRM�ܼ�
  #
  # Description:HRM�ܼ�
  #
  # Copyright: JavaHis (c) 2008
  #
  # @author ehui
 # @version 1.0
<Type=TMenuBar>
UI.Item=File;Edit;Window;Report
UI.button=save;query;unfold;|;crawl;|;InsertLCSJ;InsertPY;deleteRow;|;print;|;PrintClear;|;ClearMenu;|;ModifyFontCombo;ModifyFontSizeCombo;AlignmentLeft;AlignmentCenter;AlignmentRight;FontBMenu;FontIMenu;clear;|;close

Window.Type=TMenu
Window.Text=����
Window.M=W
Window.Item=Refresh;EditWord;|;Preview

Edit.Type=TMenu
Edit.Text=�༭
Edit.M=E
Edit.Item=InsertTableBase;DelTable;|;InsertPanel;deleteRow;|;TableProperty;TRProperty;UniteTD;|;ParagraphEdit;DIVProperty;InsertPic;DeletePic;PicDIVProperty;openFixedProperty;RemoveFixed

File.Type=TMenu
File.Text=�ļ�
File.M=F
File.Item=save;query;unfold;|;crawl;crawls;|;insertResult;|;InsertLCSJ;InsertPY;|;PrintClear;|;print;|;ClearMenu;|;clear;|;close

Report.Type=TMenu
Report.Text=����/���
Report.zhText=����/���
Report.enText=Report
Report.M=R
Report.Item=labReport;imageReport;eccReport

save.Type=TMenuItem
save.Text=����
save.Tip=����
save.M=S
save.key=Ctrl+S
save.Action=onSave
save.pic=save.gif

query.Type=TMenuItem
query.Text=��ѯ
query.Tip=��ѯ
query.M=N
query.key=F5
query.Action=onQuery
query.pic=query.gif

unfold.Type=TMenuItem
unfold.Text=�ܼ�չ��
unfold.Tip=�ܼ�չ��
unfold.M=S
unfold.key=Ctrl+U
unfold.Action=onUnfold
unfold.pic=048.gif

crawl.Type=TMenuItem
crawl.Text=�ܼ�ץȡ
crawl.Tip=�ܼ�ץȡ
crawl.M=S
crawl.key=Ctrl+W
crawl.Action=onCrawl
crawl.pic=008.gif

crawls.Type=TMenuItem
crawls.Text=�ܼ첿��ץȡ
crawls.Tip=�ܼ첿��ץȡ
crawls.M=S
crawls.key=Ctrl+W
crawls.Action=onCrawls
crawls.pic=convert.gif

InsertLCSJ.Type=TMenuItem
InsertLCSJ.Text=�ٴ�����
InsertLCSJ.Tip=�ٴ�����
InsertLCSJ.M=S
InsertLCSJ.key=Ctrl+J
InsertLCSJ.Action=onInsertLCSJ
InsertLCSJ.pic=053.gif

InsertPY.Type=TMenuItem
InsertPY.Text=Ƭ��
InsertPY.Tip=Ƭ��
InsertPY.M=S
InsertPY.key=Ctrl+Y
InsertPY.Action=onInsertPY
InsertPY.pic=Line.gif

print.Type=TMenuItem
print.Text=��ӡ
print.Tip=��ӡ
print.M=C
print.Action=onPrint
print.pic=print.gif

ClearMenu.Type=TMenuItem
ClearMenu.Text=��ռ�����
ClearMenu.Tip=��ռ�����
ClearMenu.M=v
ClearMenu.Action=onClearMenu
ClearMenu.Key=
ClearMenu.pic=001.gif

clear.Type=TMenuItem
clear.Text=���
clear.Tip=���
clear.M=C
clear.Action=onClear
clear.pic=clear.gif

close.Type=TMenuItem
close.Text=�˳�
close.Tip=�˳�
close.M=X
close.key=Alt+F4
close.Action=onClose
close.pic=close.gif

deleteRow.type=TMenuItem
deleteRow.Text=ɾ����
deleteRow.Tip=ɾ����
deleteRow.M=I
deleteRow.key=
deleteRow.Action=onDelTableRow
deleteRow.pic=cancle.gif

DelTable.type=TMenuItem

DelTable.Text=ɾ�����

DelTable.Tip=ɾ�����

DelTable.M=I

DelTable.key=
DelTable.Action=onDelTable
DelTable.pic=delete.gif

insertResult.Type=TMenuItem
insertResult.Text=������������
insertResult.Tip=������������
insertResult.M=
insertResult.key=
insertResult.Action=onInsertLisRisResult
insertResult.pic=LIS.gif

exportword.Type=TMenuItem
exportword.Text=��ģ��
exportword.Tip=��ģ��
exportword.M=Z
exportword.key=Ctrl+Z
exportword.Action=onSubTemplate
exportword.pic=exportword.gif

ModifyFontCombo.type=TFontCombo
ModifyFontSizeCombo.type=TFontSizeCombo

AlignmentLeft.type=TMenuItem
AlignmentLeft.text=����
AlignmentLeft.tip=����
AlignmentLeft.Action=onAlignmentLeft
AlignmentLeft.pic=Left.gif

AlignmentCenter.type=TMenuItem
AlignmentCenter.text=����
AlignmentCenter.tip=����
AlignmentCenter.Action=onAlignmentCenter
AlignmentCenter.pic=Center.gif

AlignmentRight.type=TMenuItem
AlignmentRight.text=����
AlignmentRight.tip=����
AlignmentRight.Action=onAlignmentRight
AlignmentRight.pic=Right.gif

FontBMenu.type=TMenuItem
FontBMenu.text=����
FontBMenu.tip=����
FontBMenu.pic=B.gif

FontIMenu.type=TMenuItem
FontIMenu.text=б��
FontIMenu.tip=б��
FontIMenu.pic=I.gif

EditWord.Type=TMenuItem
EditWord.Text=�༭
EditWord.M=T
EditWord.Action=onEditWord

Preview.Type=TMenuItem
Preview.Text=Ԥ��
Preview.M=T
Preview.Action=onPreviewWord

InsertTableBase.Type=TMenuItem
InsertTableBase.Text=������
InsertTableBase.M=T
InsertTableBase.Action=onInsertTableBase

TableProperty.Type=TMenuItem
TableProperty.Text=�������
TableProperty.M=T
TableProperty.Action=onTableProperty

TRProperty.Type=TMenuItem
TRProperty.Text=������
TRProperty.M=T
TRProperty.Action=onTRProperty

UniteTD.Type=TMenuItem
UniteTD.Text=�ϲ���Ԫ��
UniteTD.M=T
UniteTD.Action=onUniteTD

InsertPanel.Type=TMenuItem
InsertPanel.Text=���ǰ�������
InsertPanel.M=T
InsertPanel.Action=onTableInsertPanel

ParagraphEdit.Type=TMenuItem
ParagraphEdit.Text=����
ParagraphEdit.M=X
ParagraphEdit.Action=onParagraphEdit

DIVProperty.Type=TMenuItem
DIVProperty.Text=ͼ�����
DIVProperty.M=T
DIVProperty.Action=onDIVProperty

InsertPic.Type=TMenuItem
InsertPic.Text=����ͼ��
InsertPic.M=X
InsertPic.Action=onInsertPic

DeletePic.Type=TMenuItem
DeletePic.Text=ɾ��ͼ��
DeletePic.M=X
DeletePic.Action=onDeletePic

PicDIVProperty.Type=TMenuItem
PicDIVProperty.Text=ͼ��ͼ�����
PicDIVProperty.M=T
PicDIVProperty.Action=onPicDIVProperty

openFixedProperty.Type=TMenuItem
openFixedProperty.Text=����
openFixedProperty.M=R
openFixedProperty.Action=onOpenFixedProperty

RemoveFixed.Type=TMenuItem
RemoveFixed.Text=ɾ��Ԫ��
RemoveFixed.M=R
RemoveFixed.Action=onRemoveFixed

labReport.Type=TMenuItem
labReport.Text=���鱨��
labReport.zhText=���鱨��
labReport.enText=labReport
labReport.Tip=���鱨��
labReport.zhTip=���鱨��
labReport.enTip=labReport
labReport.M=L
labReport.Action=onLis
labReport.pic=LIS.gif

imageReport.Type=TMenuItem
imageReport.Text=��鱨��
imageReport.zhText=��鱨��
imageReport.enText=imageReport
imageReport.Tip=��鱨��
imageReport.zhTip=��鱨��
imageReport.enTip=imageReport
imageReport.M=I
imageReport.Action=onRis
imageReport.pic=RIS.gif

eccReport.Type=TMenuItem
eccReport.Text=�ĵ籨��
eccReport.zhText=�ĵ籨��
eccReport.enText=eccReport
eccReport.Tip=�ĵ籨��
eccReport.zhTip=�ĵ籨��
eccReport.enTip=eccReport
eccReport.M=C
eccReport.Action=getPdfReport
eccReport.pic=PicData01.jpg

PrintClear.Type=TMenuItem
PrintClear.Text=����Ԥ��
PrintClear.zhText=����Ԥ��
PrintClear.enText=preview
PrintClear.Tip=����Ԥ��(Ctrl+Q)
PrintClear.zhTip=����Ԥ��(Ctrl+Q)
PrintClear.enTip=preview
PrintClear.M=N
PrintClear.key=Ctrl+Q
PrintClear.Action=onPrintClear
PrintClear.pic=Retrieve.gif