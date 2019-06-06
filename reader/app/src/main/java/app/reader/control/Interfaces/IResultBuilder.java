package app.reader.control.Interfaces;

import java.io.InputStream;

/**
 * 可能阅读器读入文件后自定义的结果集结构在后续开发中会发生改变，但产生的结果集的流程是相似
 * 由于一次阅读会话只需要一个IResultBuilder，实现子类最好使用单例模式
 * @author www25
 *
 */
public interface IResultBuilder {
	//根据IFileOpen接口实现类传入的InputStream对象来定义和产生结果集
	public IResult builder(InputStream readIn);
}
