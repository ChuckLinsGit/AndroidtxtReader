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
	 * ���뱣���
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
	 * ��һҳ
	 * @return
	 */
	@Override
	public String toNextPage() {
		thisPage++;
		return seekPage();
	}

	/**
	 * ǰһҳ
	 * @return
	 */
	@Override
	public String toLastPage() {
		thisPage--;
		return seekPage();
	}

	/**
	 * ���ظ��ڵ��Դ���Ŀ¼�ṹ
	 * @return
	 */
	@Override
	public IIndexNode openIndex() {
		return ((BtreeStruct)result).getRootNode();
	}

	/**
	 * ������Ӧ�½�
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
	 * ������ǩ
	 * @throws ��Exception
	 */
	@Override
	public void saveMark() throws Exception {
		DefaultMark mark=new DefaultMark(((BtreeStruct)result).getBookFileName(), ((BtreeLeftNode)thisChapter).getChapter(), thisPage);
		mark.saveMark();
	}

	/**
	 * ����������ǩ�������Ҫ�÷���������ʱӦ�ø�������ɸѡ����Ӧ�鼮����ǩ
	 * @return
	 * @throws ��Exception
	 */
	@Override
	public List<IMark> loadMarkList() throws Exception {
		return DefaultMark.loadMarks();
	}

	/**
	 * ������Ӧ����ǩ��
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
	 * ����������
	 * @throws ��Exception
	 */
	@Override
	public void save() throws Exception {
		DefaultMemory defaultMemory = new DefaultMemory(((BtreeStruct)result).getBookFileName(),((BtreeLeftNode)thisChapter).getChapter(),thisPage);
		defaultMemory.save();
	}

	/**
	 * �ҵ���Ӧҳ�������
	 * @return
	 */
	private String seekPage() {
		String thisContent=((BtreeLeftNode)thisChapter).getContent();
		return thisContent.substring(thisPage*pageSize,(thisPage+1)*pageSize+1);
	}

	/**
	 * �ҵ���Ӧҳ����½�
	 */
	private void seekChapter() {
		this.thisChapter=((BtreeStruct)result).findNode(((BtreeLeftNode)thisChapter).getChapter());
	}

	/**
	 * ������ģʽ----��������չ�ķ���
	 * @param visitor
	 */
	@Override
	public void visit(IVisit visitor) {
	}
}

