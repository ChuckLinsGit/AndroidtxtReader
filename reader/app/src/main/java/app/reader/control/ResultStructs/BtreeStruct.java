package app.reader.control.ResultStructs;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.reader.control.Interfaces.IIndexNode;
import app.reader.control.Interfaces.IResult;


/**
 * ������Ҫ���ڱ����ı��ṹ���ı�����
 * ���ı����½ڷָÿ���½���һ��B��Ҷ�ӽڵ㣬ÿ��ʮ��Ҷ�ӽڵ���һ����Ҷ�ӽڵ���ӽڵ�
 * root
 * |
 * ------------------------------------------------------------
 * |		 	|			|			|			|			|		������������
 * size		 2*size	 	  3*size	 4*size	 	  5*size	  6*size
 * |
 * -----------------
 * | | | | | | |������|
 * 1-2-3-4-5-6-7-����size
 *
 * @author www25
 */
public class BtreeStruct implements IResult {
    private InputStreamReader ir;
    private String bookFileName;
    private IIndexNode rootNode;
    private Integer subNodeListSize = 50;//ÿ���ڵ��ӽڵ������Ŀ
    private Integer nodeContentLength = 20;//��Ŀ¼����£�ÿ���ڵ������������

    public BtreeStruct(InputStreamReader ir,String bookFileName) {
        this.ir = ir;
        this.bookFileName=bookFileName;
        this.rootNode = new BtreeIndexNode();
    }

    public BtreeStruct(FileReader fr, Integer subNodeListSize) {
        this.ir = fr;
        this.subNodeListSize = subNodeListSize;
        this.rootNode = new BtreeIndexNode();
    }

    public String getBookFileName() {
        return bookFileName;
    }

    public Integer getSubNodeListSize() {
        return subNodeListSize;
    }

    public void setSubNodeListSize(Integer subNodeListSize) {
        this.subNodeListSize = subNodeListSize;
    }

    public Integer getNodeContentLength() {
        return nodeContentLength;
    }

    public void setNodeContentLength(Integer nodeContentLength) {
        this.nodeContentLength = nodeContentLength;
    }

    public IIndexNode getRootNode() {
        return rootNode;
    }

    /**
     * �����ļ������ı��ṹ
     * @return
     * @throws 'Exception'
     */
    public IIndexNode product() throws Exception {
        BtreeLeftNode thisNode=new BtreeLeftNode();
        BufferedReader br = new BufferedReader(ir);
        boolean matched = false;
        try {
            String line = br.readLine();
            String content = "";
            while (line != null && !(matched = line.matches("^��[[0-9]|[һ�����������߰˾�ʮ��ǧ��]]+[\\s|\\S]+$"))) {
                content += line+"\n";//��Ҫ��������
                line = br.readLine();
            }
            if (matched) {
                List<IIndexNode> indexNodeList = new ArrayList<IIndexNode>();
                //����һ�½�֮ǰ�����ݵ�����һ���ڵ������
                thisNode.setTitle("ǰ��");
                thisNode.setContent(content);
                thisNode.setChapter(0);
                indexNodeList.add(thisNode);
                content = "";
                //����ÿ���½ڵ�����
                int wrapCount = 1, chapterCount = 1;
                while (line != null) {
                    //��װһ���½�
                    BtreeLeftNode newNode = new BtreeLeftNode();
                    newNode.setChapter(chapterCount);
                    if(line!=null)
                        newNode.setTitle(line);
                    do {
                        content +=line+"\n";
                        line = br.readLine();
                    } while (line!=null&&!line.matches("^��[[0-9]|[һ�����������߰˾�ʮ��ǧ��]]+[\\s|\\S]+$"));
                    newNode.setContent(content);
                    //Ҷ�ӽڵ�������
                    thisNode.setNextNode(newNode);
                    newNode.setLastNode(thisNode);
                    thisNode=newNode;
                    indexNodeList.add(newNode);
                    chapterCount++;
                    //ÿsubNodeListSize���ڵ��װһ�λ��ߵ����һ���ڵ㼴ʹ����subNodeListSizeҲ���װ
                    if (++wrapCount == subNodeListSize||line==null) {
                        BtreeIndexNode btreeIndexNode = new BtreeIndexNode(chapterCount);
                        btreeIndexNode.setSubNodes(indexNodeList);
                        ((ArrayList<IIndexNode>) (((BtreeIndexNode) rootNode).getSubNodes())).add(btreeIndexNode);
                        indexNodeList=new ArrayList<IIndexNode>();
                        wrapCount = 0;
                    }
                    content = "";
                }
                //��Ŀ¼�ṹ
            } else {
                List<IIndexNode> indexNodeList = new ArrayList<IIndexNode>();
                //����ÿ���½ڵ�����
                thisNode.setChapter(0);
                thisNode.setContent(content.substring(0, nodeContentLength));
                indexNodeList.add(thisNode);
                int wrapCount = 1, chapterCount = 1;
                boolean EOF=false;
                //��ͷ�ַ�λ����content��ĩβ֮ǰ���ý����װ
                while (chapterCount*nodeContentLength<=content.length()) {
                    //��װһ���½�
                    BtreeLeftNode newNode = new BtreeLeftNode();
                    newNode.setChapter(chapterCount);

                    //�����װ�ַ�����ĩβ��content��ĩβҪ�����ֹ��ĩβ���ýڵ�Ϊ���һ���ڵ�
                    if(nodeContentLength*(chapterCount+1)>content.length()) {
                        newNode.setContent(content.substring(nodeContentLength*chapterCount,content.length()));
                        EOF=true;
                    }else {
                        newNode.setContent(content.substring(nodeContentLength*chapterCount,nodeContentLength*(chapterCount+1)));
                    }
                    //Ҷ�ӽڵ�������
                    thisNode.setNextNode(newNode);
                    thisNode=newNode;
                    indexNodeList.add(newNode);
                    chapterCount++;
                    //ÿsubNodeListSize���ڵ��װһ�λ��ߵ����һ���ڵ㼴ʹ����subNodeListSizeҲ���װ
                    if (++wrapCount == subNodeListSize||EOF) {
                        BtreeIndexNode btreeIndexNode = new BtreeIndexNode(chapterCount);
                        btreeIndexNode.setSubNodes(indexNodeList);
                        ((ArrayList<IIndexNode>) (((BtreeIndexNode) rootNode).getSubNodes())).add(btreeIndexNode);
                        indexNodeList=new ArrayList<IIndexNode>();
                        wrapCount = 0;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("�����ļ�����");
        } finally {
            br.close();
            ir.close();
        }
        return rootNode;
    }

    /**
     * ����B��������Ӧ�½�
     * @param chapter
     * @return
     */
    public BtreeLeftNode findNode(Integer chapter) {
        BtreeLeftNode lefNode = null;
        Iterator<IIndexNode> iterator = ((BtreeIndexNode) this.rootNode).getSubNodes().iterator();
        while (iterator.hasNext()) {
            BtreeIndexNode node = (BtreeIndexNode) iterator.next();
            //���ݽṹ��Ŀ¼�ڵ�chapter-Ŀ��chapter<һ��Ŀ¼�ڵ����ܰ����Ľڵ���˵��Ŀ��chapter�ڸ�Ŀ¼�ڵ���
            if (node.getChapter()-chapter <= this.subNodeListSize) {
                Iterator<IIndexNode> it= node.getSubNodes().iterator();
                while (it.hasNext()){
                    BtreeLeftNode childNode=(BtreeLeftNode)it.next();
                    if (childNode.getChapter().intValue()==chapter.intValue()) {
                        lefNode=childNode;
                        break;
                    }
                }
            }
        }
        return lefNode;
    }
}