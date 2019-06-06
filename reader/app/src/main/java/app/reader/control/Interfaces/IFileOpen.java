package app.reader.control.Interfaces;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * 不同的文件打开方法不尽相同，因此需要使用策略模式
 * 由于一次阅读会话只需要一个IFileOpen，实现子类最好使用单例模式
 * @author www25
 *
 */
public interface IFileOpen {
	//读取文件，返回一个读入流给调用者使用
	public Object open(File in) throws Exception;
}
