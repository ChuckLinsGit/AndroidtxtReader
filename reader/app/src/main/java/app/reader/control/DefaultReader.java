package app.reader.control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.reader.control.Interfaces.IFileReader;
import app.reader.control.Interfaces.IIndexNode;
import app.reader.control.Interfaces.IMark;
import app.reader.control.Interfaces.IResult;
import app.reader.control.Interfaces.IVisit;
import app.reader.control.ResultStructs.BtreeIndexNode;
import app.reader.control.ResultStructs.BtreeLeftNode;
import app.reader.control.ResultStructs.BtreeStruct;

public class DefaultReader implements IFileReader {
	private IResult result;
	private IIndexNode thisChapter;
	private Integer thisPage;
	private Integer pageSize;
	private DefaultMemory memory;

	public DefaultReader(IResult result,DefaultMemory memory) {
		this.result = result;
		this.memory=memory;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * 载入保存点
	 * @return
	 */
	@Override
	public String toLastSavePos() {
		((BtreeLeftNode)this.thisChapter).setChapter(memory.getChapter());
		this.thisPage=memory.getPage();
		seekChapter();
		return seekPage();
	}

	/**
	 * 后一页
	 * @return
	 */
	@Override
	public String toNextPage() {
		thisPage++;
		return seekPage();
	}

	/**
	 * 前一页
	 * @return
	 */
	@Override
	public String toLastPage() {
		thisPage--;
		return seekPage();
	}

	/**
	 * 返回根节点以传递目录结构
	 * @return
	 */
	@Override
	public IIndexNode openIndex() {
		return ((BtreeStruct)result).getRootNode();
	}

	/**
	 * 跳到相应章节
	 * @param aimChapter
	 * @return
	 */
	@Override
	public String jumpToChapter(Integer aimChapter) {
		((BtreeLeftNode)thisChapter).setChapter(aimChapter);
		thisPage=0;
		seekChapter();
		return seekPage();
	}

	/**
	 * 保存书签
	 * @throws ‘Exception
	 */
	@Override
	public void saveMark() throws Exception {
		DefaultMark mark=new DefaultMark(((BtreeStruct)result).getBookFileName(), ((BtreeLeftNode)thisChapter).getChapter(), thisPage);
		mark.saveMark();
	}

	/**
	 * 载入所有书签，因此需要该方法被调用时应该根据书名筛选出对应书籍的书签
	 * @return
	 * @throws ’Exception
	 */
	@Override
	public List<IMark> loadMarkList() throws Exception {
		return DefaultMark.loadMarks();
	}

	/**
	 * 跳到相应的书签点
	 * @param mark
	 * @return
	 */
	@Override
	public String jumpToMark(IMark mark) {
		DefaultMark m=(DefaultMark)mark;
		((BtreeLeftNode)thisChapter).setChapter(m.getMarkChapter());
		thisPage=m.getMarkPage();
		seekChapter();
		return seekPage();
	}

	/**
	 * 保存读书进度
	 * @throws ‘Exception
	 */
	@Override
	public void save() throws Exception {
		DefaultMemory defaultMemory = new DefaultMemory(((BtreeStruct)result).getBookFileName(),((BtreeLeftNode)thisChapter).getChapter(),thisPage);
		defaultMemory.save();
	}

	/**
	 * 找到对应页面的文字
	 * @return
	 */
	private String seekPage() {
		String thisContent=((BtreeLeftNode)thisChapter).getContent();
		return thisContent.substring(thisPage*pageSize,(thisPage+1)*pageSize+1);
	}

	/**
	 * 找到对应页面的章节
	 */
	private void seekChapter() {
		this.thisChapter=((BtreeStruct)result).findNode(((BtreeLeftNode)thisChapter).getChapter());
	}

	/**
	 * 访问者模式----留下做扩展的方法
	 * @param visitor
	 */
	@Override
	public void visit(IVisit visitor) {
	}
}

