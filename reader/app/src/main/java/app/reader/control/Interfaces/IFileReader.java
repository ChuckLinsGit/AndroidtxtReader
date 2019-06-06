package app.reader.control.Interfaces;

import java.util.List;

/**
 * 即便打开不同格式的文件，阅读器的使用方法和步骤是相同的，因此使用策略模式
 * @author www25
 *
 */
public interface IFileReader {
	//子类必须要有的成员域
	//private IResult result;//打开文件后的结果结构集合，而IResult实际上不应有任何成员，只是一个标志接口；
	//private IndexNode thisChapter;
	//private Integer thisPage;

	public String toLastSavePos();//上次阅读的位置，如果是第一次打开，则是文件开头。
	
	public String toNextPage();//到下一页
	
	public String toLastPage();//到上一页
	
	public IIndexNode openIndex();//打开目录
	
	public String jumpToChapter(Integer aimChapter);//跳转章节
	
	public void saveMark() throws Exception;//保存书签
	
	public List<IMark> loadMarkList() throws Exception;//读取书签列表
	
	public String jumpToMark(IMark mark);//跳转到书签位置
	
	public void save() throws Exception;//保存进度
	
	//访问者模式：提供程扩展的方法。
	public void visit(IVisit visitor);
}
