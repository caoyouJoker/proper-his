package jdo.med;

import org.xvolks.jnative.JNative;
import org.xvolks.jnative.Type;
import org.xvolks.jnative.exceptions.NativeException;
import org.xvolks.jnative.pointers.Pointer;
import org.xvolks.jnative.pointers.memory.HeapMemoryBlock;
/**
*
* <p>Title: ͼ�ο��������ɽӿ�</p>
*
* <p>Description: </p>
*
* <p>Copyright: Copyright (c) 2015</p>
*
* <p>Company: bluecore </p>
*
* @author wanglong 2015.04.10
* @version 1.0
*/
public class MedFntHEX {

    static {
        System.loadLibrary("Fnthex32"); // ����dll
    }

    /**
     * ���ɺ��ֶ�Ӧͼ�ο�����
     * @param chineseText �����ı�
     * @return
     */
    public static String getCHNControlCode(String chineseText) {
        return getCHNControlCode(chineseText, "����", "0", "22", "0", "1", "0");
    }

    /**
     * ���ɺ��ֶ�Ӧͼ�ο�����
     * @param chineseText ���� �ı�
     * @param font ����
     * @param degree  ��ת�Ƕ�0,90,180,270
     * @param fontHeight ����߶�
     * @param fontWeight �����ȣ�ͨ����0
     * @param bold 1��֣�0����
     * @param italic 1б�壬0����
     * @return
     */
    public static String getCHNControlCode(String chineseText, String font, String degree,
                                           String fontHeight, String fontWeight, String bold,
                                           String italic) {
        try {
            JNative n = new JNative("Fnthex32", "GETFONTHEX");
            n.setRetVal(Type.INT);
            n.setParameter(0, Type.STRING, chineseText); // ����
            n.setParameter(1, Type.STRING, font);// ��������
            n.setParameter(2, Type.INT, degree);// ��ת�Ƕ�0,90,180,270��ͨ����0
            n.setParameter(3, Type.INT, fontHeight);// ����߶ȣ�ͨ����14
            n.setParameter(4, Type.INT, fontWeight);// �����ȣ�ͨ����0
            n.setParameter(5, Type.INT, fontWeight);// 1��֣�0����
            n.setParameter(6, Type.INT, italic);// 1б�壬0����
            Pointer p2 = new Pointer(new HeapMemoryBlock(22 * 1024));
            p2.setMemory(new byte[21 * 1024]);
            n.setParameter(7, p2);// ���ص�ͼƬ�ַ�
            n.invoke();
            // System.out.println(p2.getAsString());// ���ص�ͼƬ�ַ�
            return p2.getAsString() + "^XGOUTSTR01,1,1^FS";
            // ���ص��ַ���������ʾ��
            // ~DGOUTSTR01,00224,016,3180398063H018J06H063H018H0C301981E3H0C3H018J06H0H3H018H0C
            // 3H01803HF19FE018I0IFC0303IF7HFEHF80DEH363H018J0C01HFH03I0C3H0780763063H018I01HFH
            // 0FH07FC0C3H0D81E7F0IF018I038301BH0E0CJ019803631C0C03CI07830H301E0C3HFC3D807EH3C0
            // C03CI0DHF07B037FC030C7F81DHF0IF03CI01830HFH060C030CDF803B60C0C0H6I01831BFH060C03
            // 0C19807B60D8C0H60701HF0H3H07FC030C198HDB60IC0C30701830H31860C0H38198C1E78C0C1818
            // 301830H31860C03H018FC7C38C3C7H0HE018F031F863C03H0
            // Ȼ��ʹ��^XG�����Ѵ洢ͼ���Դ�豸
            // ע�⣺��ͬ��ͼ���ò�ͬ�����֡�������ͼ������ͬһ���͵���ӡ��������ͼ�񽫱����һ��ͼ�������档
            // ^XG ����ͼ��
            // ��ʽ ^XGd:o.x,mx,my
            // d ---�Ѵ洢ͼ���Դ�豸
            // mx = x ��ķŴ�ϵ�����ܵ�ֵ��1 �� 10 Ĭ��ֵ��1
            // my = y ��ķŴ�ϵ�����ܵ�ֵ��1 �� 10 Ĭ��ֵ��1
            // ~DG �������������� ASCII ʮ�����Ʊ�ʾ��ͼ��ͼ��
            // ��ʽ ~DGd:o.x,t,w,data
            // d = �洢ͼ����豸
            // t = ͼ���е����ֽ���
            // w = ÿ�е��ֽ�
            // data = ����ͼ��� ASCII ʮ�������ַ���
        }
        catch (NativeException e) {
            e.printStackTrace();
        }
        catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return "";
    }
}
