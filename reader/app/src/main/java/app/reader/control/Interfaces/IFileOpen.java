package app.reader.control.Interfaces;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * ��ͬ���ļ��򿪷���������ͬ�������Ҫʹ�ò���ģʽ
 * ����һ���Ķ��Ựֻ��Ҫһ��IFileOpen��ʵ���������ʹ�õ���ģʽ
 * @author www25
 *
 */
public interface IFileOpen {
	//��ȡ�ļ�������һ����������������ʹ��
	public Object open(File in) throws Exception;
}
