package app.reader.control.Interfaces;

import java.util.List;

/**
 * ����򿪲�ͬ��ʽ���ļ����Ķ�����ʹ�÷����Ͳ�������ͬ�ģ����ʹ�ò���ģʽ
 * @author www25
 *
 */
public interface IFileReader {
	//�������Ҫ�еĳ�Ա��
	//private IResult result;//���ļ���Ľ���ṹ���ϣ���IResultʵ���ϲ�Ӧ���κγ�Ա��ֻ��һ����־�ӿڣ�
	//private IndexNode thisChapter;
	//private Integer thisPage;

	public String toLastSavePos();//�ϴ��Ķ���λ�ã�����ǵ�һ�δ򿪣������ļ���ͷ��
	
	public String toNextPage();//����һҳ
	
	public String toLastPage();//����һҳ
	
	public IIndexNode openIndex();//��Ŀ¼
	
	public String jumpToChapter(Integer aimChapter);//��ת�½�
	
	public void saveMark() throws Exception;//������ǩ
	
	public List<IMark> loadMarkList() throws Exception;//��ȡ��ǩ�б�
	
	public String jumpToMark(IMark mark);//��ת����ǩλ��
	
	public void save() throws Exception;//�������
	
	//������ģʽ���ṩ����չ�ķ�����
	public void visit(IVisit visitor);
}
