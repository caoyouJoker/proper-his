package com.javahis.ui.adm;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.File;

import javax.swing.ImageIcon;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.ole.win32.OleAutomation;
import org.eclipse.swt.ole.win32.OleClientSite;
import org.eclipse.swt.ole.win32.OleFrame;
import org.eclipse.swt.ole.win32.Variant;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.MessageBox;

import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.util.FileTool;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class SWTshell extends Shell {

	OleFrame olef = new OleFrame(getShell(), SWT.NONE);
	OleClientSite site = new OleClientSite(olef, SWT.NONE,
			"{090457CB-DF21-41EB-84BB-39AAFC9E271A}");
	OleAutomation auto = new OleAutomation(site);
	Combo combofbl = new Combo(this, SWT.DROP_DOWN);// �ֱ���
	Combo combosmcc = new Combo(this, SWT.DROP_DOWN);// ɨ��ߴ�
	Combo combojd = new Combo(this, SWT.DROP_DOWN);// ��Ƶ��ת�Ƕ�
	Button btnRadioButton = new Button(this, SWT.RADIO);// ���֤����
	Button btnRadioButton_1 = new Button(this, SWT.RADIO);// ���֤����
	String sLocalPath = "";
	String sMrNo = "";
	boolean status = false;
	Display display;

	/**
	 * Launch the application.
	 * 
	 * @param args
	 */
	public static void main(String args[]) {
		try {
			Display display = Display.getDefault();
			SWTshell shell = new SWTshell(display, "C:\\JavaHis\\Temp\\",
					"333333333333");
			shell.open();
			shell.layout();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the shell.
	 * 
	 * @param display
	 */
	public SWTshell(Display dis, String LocalPath, String MrNo) {

		super(dis, SWT.SHELL_TRIM);
		display = dis;
		sLocalPath = LocalPath;
		sMrNo = MrNo;
		jbInit();
		// ��ʾ����
		createContents();

	}

	private void jbInit() {
		setBackground(SWTResourceManager.getColor(161, 220, 230));// ���ñ�����ɫ
		// ���豸
		Button button = new Button(this, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onOpenDevice();

			}
		});
		button.setBounds(27, 535, 95, 21);
		button.setText("\u6253\u5F00\u8BBE\u5907");
		// ��ʼԤ��
		Button btnNewButton = new Button(this, SWT.NONE);
		btnNewButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onStartPerView();
			}
		});
		btnNewButton.setBounds(128, 535, 96, 21);
		btnNewButton.setText("\u5F00\u59CB\u9884\u89C8");
		// ֹͣԤ��
		Button btnNewButton_1 = new Button(this, SWT.NONE);
		btnNewButton_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onStopPreView();
			}
		});
		btnNewButton_1.setBounds(230, 535, 99, 21);
		btnNewButton_1.setText("\u505C\u6B62\u9884\u89C8");
		// ����
		Button btnNewButton_2 = new Button(this, SWT.NONE);
		btnNewButton_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onTakePic();
			}
		});
		btnNewButton_2.setBounds(335, 535, 96, 21);
		btnNewButton_2.setText("\u62CD\u7167");
		// �ر�
		Button btnNewButton_4 = new Button(this, SWT.NONE);
		btnNewButton_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				onClose();
			}
		});
		btnNewButton_4.setBounds(437, 535, 96, 21);
		btnNewButton_4.setText("\u5173\u95ED");
		combofbl.setEnabled(false);

		combofbl.setBounds(77, 496, 96, 21);// �ֱ���
		// �ֱ���comboѡ���¼�
		combofbl.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				oncombofbl();
			}
		});
		combosmcc.setEnabled(false);
		combosmcc.setBounds(252, 496, 108, 21);// ɨ��ߴ�
		// ɨ��ߴ�comboѡ���¼�
		combosmcc.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				oncombosmcc();
			}
		});
		combojd.setBounds(437, 497, 96, 20);// ��Ƶ��ת�Ƕ�
		// ��Ƶ��ת�Ƕ�comboѡ���¼�
		combojd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				oncombojd();
			}
		});
		Label lblNewLabel = new Label(this, SWT.NONE);
		lblNewLabel.setFont(SWTResourceManager.getFont("����", 9, SWT.NORMAL));
		lblNewLabel.setAlignment(SWT.CENTER);
		lblNewLabel.setBounds(20, 501, 51, 14);
		lblNewLabel.setBackground(SWTResourceManager.getColor(161, 220, 230));
		lblNewLabel.setText("\u5206\u8FA8\u7387");

		Label lblNewLabel_1 = new Label(this, SWT.NONE);
		lblNewLabel_1.setBounds(195, 501, 51, 14);
		lblNewLabel_1.setBackground(SWTResourceManager.getColor(161, 220, 230));
		lblNewLabel_1.setText("\u626B\u63CF\u5C3A\u5BF8");

		Label lblNewLabel_2 = new Label(this, SWT.NONE);
		lblNewLabel_2.setBounds(380, 501, 51, 14);
		lblNewLabel_2.setBackground(SWTResourceManager.getColor(161, 220, 230));
		lblNewLabel_2.setText("\u65CB\u8F6C\u89D2\u5EA6");
		btnRadioButton.setSelection(true);

		// ���֤����
		btnRadioButton.setBounds(542, 499, 51, 17);
		btnRadioButton
				.setBackground(SWTResourceManager.getColor(161, 220, 230));
		btnRadioButton.setText("\u6B63\u9762");
		// ���֤����
		btnRadioButton_1.setBounds(599, 500, 51, 16);
		btnRadioButton_1.setBackground(SWTResourceManager.getColor(161, 220,
				230));
		btnRadioButton_1.setText("\u53CD\u9762");

	}

	/**
	 * ���豸
	 */
	public void onOpenDevice() {
		int[] ids = auto.getIDsOfNames(new String[] { "StartPreviewEx" });
		auto.invoke(ids[0]);
		// ��ȡ����ʹ�õķֱ��ʸ���
		int[] idscount = auto
				.getIDsOfNames(new String[] { "GetResolutionCount" });
		Variant count = auto.invoke(idscount[0]);
		// System.out.println("count============="+count);
		for (int i = 0; i < count.getInt(); i++) {
			// ��ȡ��i���ֱ��ʵĿ�
			int[] ids1 = auto
					.getIDsOfNames(new String[] { "GetResolutionWidth" });
			int dispIdMember = ids1[0];
			Variant[] rgvarg = new Variant[1];
			rgvarg[0] = new Variant(i);
			Variant width = auto.invoke(dispIdMember, rgvarg);
			// ��ȡ��i���ֱ��ʵĸ�
			int[] ids2 = auto
					.getIDsOfNames(new String[] { "GetResolutionHeight" });
			int dispIdMember1 = ids2[0];
			Variant[] rgvarg1 = new Variant[1];
			rgvarg1[0] = new Variant(i);
			Variant height = auto.invoke(dispIdMember1, rgvarg1);
			String str = width.getString() + "x" + height.getString();
			// �ֱ���combo��ֵ
			combofbl.add(str, i);
		}
		// �ֱ���Ĭ��ֵ
		combofbl.select(0);
		// ���÷ֱ���(Ĭ����߷ֱ���)
		int[] Resolution = auto.getIDsOfNames(new String[] { "SetResolution" });
		int dispIdMember2 = Resolution[0];
		Variant[] rgvarg2 = new Variant[1];
		rgvarg2[0] = new Variant(0);// Ĭ����߷ֱ���
		auto.invoke(dispIdMember2, rgvarg2);
		// ��ȡ����ʹ�õ�ɨ��ߴ����
		int[] idssize = auto.getIDsOfNames(new String[] { "GetScanSizeCount" });
		Variant size = auto.invoke(idssize[0]);
		if (size.getInt() == 8) {
			// ɨ��ߴ�combo��ֵ
			combosmcc.add("All");
			combosmcc.add("A3");
			combosmcc.add("A4");
			combosmcc.add("A5");
			combosmcc.add("A6");
			combosmcc.add("A7");
			combosmcc.add("��Ƭ");
			combosmcc.add("���֤");
			combosmcc.add("�Զ���");
			combosmcc.select(7);
			// ����ɨ��ߴ磨Ĭ�����֤��
			int[] maxsize = auto.getIDsOfNames(new String[] { "SetScanSize" });
			int dispIdMember3 = maxsize[0];
			Variant[] rgvarg3 = new Variant[1];
			rgvarg3[0] = new Variant(7);// Ĭ�����֤
			auto.invoke(dispIdMember3, rgvarg3);
		} else {
			// ɨ��ߴ�combo��ֵ
			combosmcc.add("All");
			combosmcc.add("A4");
			combosmcc.add("A5");
			combosmcc.add("A6");
			combosmcc.add("A7");
			combosmcc.add("��Ƭ");
			combosmcc.add("���֤");
			combosmcc.add("�Զ���");
			combosmcc.select(6);
			// ����ɨ��ߴ磨Ĭ�����֤��
			int[] maxsize = auto.getIDsOfNames(new String[] { "SetScanSize" });
			int dispIdMember3 = maxsize[0];
			Variant[] rgvarg3 = new Variant[1];
			rgvarg3[0] = new Variant(6);// Ĭ�����֤
			auto.invoke(dispIdMember3, rgvarg3);
		}
		// ��Ƶ��ת�Ƕ�combo��ֵ
		combojd.add("0��");
		combojd.add("90��");
		combojd.add("270��");
		combojd.add("180��");
		combojd.select(0);
		// ������Ƶ��ת�Ƕ�(Ĭ�ϽǶ�=0��)
		int[] Rotate = auto.getIDsOfNames(new String[] { "SetVideoRotate" });
		int dispIdMember4 = Rotate[0];
		Variant[] rgvarg4 = new Variant[1];
		rgvarg4[0] = new Variant(0);// Ĭ�ϽǶ�=0��
		auto.invoke(dispIdMember4, rgvarg4);
	}

	/**
	 * ��ʼԤ��
	 */
	public void onStartPerView() {

		int[] ids = auto.getIDsOfNames(new String[] { "StartPreviewEx" });
		auto.invoke(ids[0]);

	}

	/**
	 * ֹͣԤ��
	 */
	public void onStopPreView() {
		int[] ids = auto.getIDsOfNames(new String[] { "StopPreviewEx" });
		auto.invoke(ids[0]);

	}

	/**
	 * ����
	 */
	public void onTakePic() {
		status = false;
		int jpgval = 36;// ����JPGͼƬ�ļ���Ч��,ȡֵ��ΧΪ1��99
		int[] ids = auto.getIDsOfNames(new String[] { "SetJpegQuality" });// ����JPGͼƬ�ļ���Ч��
		int dispIdMember = ids[0];
		Variant[] rgvarg = new Variant[1];
		rgvarg[0] = new Variant(jpgval);
		auto.invoke(dispIdMember, rgvarg);
		String path = "";
		File filepath = new File(sLocalPath);
		if (!filepath.exists())
			filepath.mkdirs();
		if (btnRadioButton.getSelection())
			path = sLocalPath + sMrNo + "_IDFront.jpg";// pathΪͼƬ�ı���·�����ļ���
		if (btnRadioButton_1.getSelection())
			path = sLocalPath + sMrNo + "_IDBack.jpg";// pathΪͼƬ�ı���·�����ļ���
		int[] ids1 = auto.getIDsOfNames(new String[] { "QuickScan" });// ����ɨ��ͼƬ
		int dispIdMember1 = ids1[0];
		Variant[] rgvarg1 = new Variant[1];
		rgvarg1[0] = new Variant(path);
		Variant result = auto.invoke(dispIdMember1, rgvarg1);
		// System.out.println("result============="+result);
		// if (result.getBoolean()) {
		// MessageDialog.openInformation(null, "��Ϣ", "���ճɹ�");
		// } else {
		// MessageDialog.openInformation(null, "��Ϣ", "����ʧ��");
		// }
		// ����ʧ��,ֹͣ���з���false
		if (result.getBoolean()) {
			
			// ���ˮӡ�����ֺ�����ˮӡ���ֳ��ֵ�����
			// ��ˮӡʧ��,ֹͣ���з���false
			if (this.createMark(path, "TEDAICH", Color.red, 1)) {
				if (btnRadioButton.getSelection())
					status = sendIdPic("_IDFront.jpg");
				if (btnRadioButton_1.getSelection())
					status = sendIdPic("_IDBack.jpg");
			}
		} else {
			MessageBox("����ʧ��");
		}
		// int[] ids2 = auto.getIDsOfNames(new String[] { "StartPreview" });
		// auto.invoke(ids2[0]);
		onStartPerView();

	}

	/**
	 * �ر�
	 */
	public void onClose() {
		int[] ids = auto.getIDsOfNames(new String[] { "CloseDevice" });
		auto.invoke(ids[0]);
		display.close();
	}

	/**
	 * �ֱ���comboѡ���¼�
	 */
	public void oncombofbl() {
		int x = combofbl.getSelectionIndex();// ��÷ֱ���index
		int[] resolution = auto.getIDsOfNames(new String[] { "SetResolution" });
		int dispIdMember = resolution[0];
		Variant[] rgvarg = new Variant[1];
		rgvarg[0] = new Variant(x);
		auto.invoke(dispIdMember, rgvarg);
	}

	/**
	 * ɨ��ߴ�comboѡ���¼�
	 */
	public void oncombosmcc() {
		int x = combosmcc.getSelectionIndex();// ���ɨ��ߴ�index
		int[] size = auto.getIDsOfNames(new String[] { "SetScanSize" });
		int dispIdMember = size[0];
		Variant[] rgvarg = new Variant[1];
		rgvarg[0] = new Variant(x);
		auto.invoke(dispIdMember, rgvarg);
	}

	/**
	 * ��Ƶ��ת�Ƕ�comboѡ���¼�
	 */
	public void oncombojd() {
		int x = combojd.getSelectionIndex();// �����Ƶ��ת�Ƕ�index
		int[] rotate = auto.getIDsOfNames(new String[] { "SetVideoRotate" });
		int dispIdMember = rotate[0];
		Variant[] rgvarg = new Variant[1];
		rgvarg[0] = new Variant(x);
		auto.invoke(dispIdMember, rgvarg);
	}

	/**
	 * ���ˮӡ�����ֺ�����ˮӡ���ֳ��ֵ�����
	 */
	public boolean createMark(String filePath, String markContent,
			Color markContentColor, float qualNum) {
		ImageIcon imgIcon = new ImageIcon(filePath);
		Image theImg = imgIcon.getImage();
		int width = theImg.getWidth(null);
		int height = theImg.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bimage.createGraphics();
		g.setColor(markContentColor);
		g.setBackground(Color.white);
		g.drawImage(theImg, 0, 0, null);
		g.setFont(new Font("����", Font.BOLD, 30));
		g.drawString(markContent, (width / 5) * 3 + 15, (height / 3) * 2); // ���ˮӡ�����ֺ�����ˮӡ���ֳ��ֵ�����
		g.dispose();

		try {
			FileOutputStream out = new FileOutputStream(filePath);
			JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
			@SuppressWarnings("restriction")
			JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(bimage);
			param.setQuality(qualNum, true);
			encoder.encode(bimage, param);
			out.close();
		} catch (Exception e) {
			return false;
		}

		return true;

	}

	/**
	 * Create contents of the shell.
	 */
	protected void createContents() {
		setText("SWT Application");
//		setSize(800, 600);
		this.setBounds(100, 60, 800, 600);
		olef.setLocation(10, 10);
		olef.setSize(640, 480);
		site.doVerb(org.eclipse.swt.ole.win32.OLE.OLEIVERB_SHOW);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	protected void MessageBox(String str) {
		MessageBox messagebox = new MessageBox(this, SWT.ICON_WORKING | SWT.OK);
		messagebox.setMessage(str);
		messagebox.open();
	}

	// protected boolean getStatus() {
	// return status;
	// }
	//
	// protected String getMrNo() {
	// return sMrNo;
	// }

	/**
	 * ������Ƭ
	 * 
	 * @param image
	 *            Image
	 */
	public boolean sendIdPic(String localFileName) {
		String mrNo = this.sMrNo;
		// String photoNameFront = mrNo + "_IDFront.jpg";
		// String photoNameBack = mrNo + "_IDBack.jpg";
		String photoName = mrNo + localFileName;
		String dir = this.sLocalPath;
		try {
			// byte[] data = FileTool.getByte(dir + photoName);
			// new File(dir + photoName).delete();
			File file = new File(dir + photoName);
			byte[] data = FileTool.getByte(dir + photoName);
			if (file.exists()) {
				new File(dir + photoName).delete();
			}

			String root = TIOM_FileServer.getRoot();
			dir = TIOM_FileServer.getPath("PatInfPIC.ServerPath");
			dir = root + dir + mrNo.substring(0, 3) + "\\"
					+ mrNo.substring(3, 6) + "\\" + mrNo.substring(6, 9) + "\\";
			if (!TIOM_FileServer.writeFile(TIOM_FileServer.getSocket(), dir
					+ photoName, data))
				return false;
		} catch (Exception e) {
			return false;
		}
		// this.viewPhoto(pat.getMrNo());
		return true;
	}
}
