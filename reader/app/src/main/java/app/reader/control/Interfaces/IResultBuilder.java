package app.reader.control.Interfaces;

import java.io.InputStream;

/**
 * �����Ķ��������ļ����Զ���Ľ�����ṹ�ں��������лᷢ���ı䣬�������Ľ����������������
 * ����һ���Ķ��Ựֻ��Ҫһ��IResultBuilder��ʵ���������ʹ�õ���ģʽ
 * @author www25
 *
 */
public interface IResultBuilder {
	//����IFileOpen�ӿ�ʵ���ഫ���InputStream����������Ͳ��������
	public IResult builder(InputStream readIn);
}
