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
 * 本类主要用于保存文本结构和文本名字
 * 将文本按章节分割，每个章节是一个B树叶子节点，每二十个叶子节点是一个非叶子节点的子节点
 * root
 * |
 * ------------------------------------------------------------
 * |		 	|			|			|			|			|		······
 * size		 2*size	 	  3*size	 4*size	 	  5*size	  6*size
 * |
 * -----------------
 * | | | | | | |···|
 * 1-2-3-4-5-6-7-··size
 *
 * @author www25
 */
public class BtreeStruct implements IResult {
    private InputStreamReader ir;
    private String bookFileName;
    private IIndexNode rootNode;
    private Integer subNodeListSize = 50;//每个节点子节点最大数目
    private Integer nodeContentLength = 20;//无目录情况下，每个节点内容最大字数

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
     * 根据文件生成文本结构
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
            while (line != null && !(matched = line.matches("^第[[0-9]|[一二三四五六七八九十百千万]]+[\\s|\\S]+$"))) {
                content += line+"\n";//需要保留换行
                line = br.readLine();
            }
            if (matched) {
                List<IIndexNode> indexNodeList = new ArrayList<IIndexNode>();
                //将第一章节之前的内容当作第一个节点的内容
                thisNode.setTitle("前言");
                thisNode.setContent(content);
                thisNode.setChapter(0);
                indexNodeList.add(thisNode);
                content = "";
                //保存每个章节的内容
                int wrapCount = 1, chapterCount = 1;
                while (line != null) {
                    //包装一个章节
                    BtreeLeftNode newNode = new BtreeLeftNode();
                    newNode.setChapter(chapterCount);
                    if(line!=null)
                        newNode.setTitle(line);
                    do {
                        content +=line+"\n";
                        line = br.readLine();
                    } while (line!=null&&!line.matches("^第[[0-9]|[一二三四五六七八九十百千万]]+[\\s|\\S]+$"));
                    newNode.setContent(content);
                    //叶子节点相连接
                    thisNode.setNextNode(newNode);
                    newNode.setLastNode(thisNode);
                    thisNode=newNode;
                    indexNodeList.add(newNode);
                    chapterCount++;
                    //每subNodeListSize个节点包装一次或者到最后一个节点即使不足subNodeListSize也会封装
                    if (++wrapCount == subNodeListSize||line==null) {
                        BtreeIndexNode btreeIndexNode = new BtreeIndexNode(chapterCount);
                        btreeIndexNode.setSubNodes(indexNodeList);
                        ((ArrayList<IIndexNode>) (((BtreeIndexNode) rootNode).getSubNodes())).add(btreeIndexNode);
                        indexNodeList=new ArrayList<IIndexNode>();
                        wrapCount = 0;
                    }
                    content = "";
                }
                //无目录结构
            } else {
                List<IIndexNode> indexNodeList = new ArrayList<IIndexNode>();
                //保存每个章节的内容
                thisNode.setChapter(0);
                thisNode.setContent(content.substring(0, nodeContentLength));
                indexNodeList.add(thisNode);
                int wrapCount = 1, chapterCount = 1;
                boolean EOF=false;
                //开头字符位置在content的末尾之前都得进入包装
                while (chapterCount*nodeContentLength<=content.length()) {
                    //包装一个章节
                    BtreeLeftNode newNode = new BtreeLeftNode();
                    newNode.setChapter(chapterCount);

                    //如果包装字符串的末尾比content的末尾要大，则截止该末尾，该节点为最后一个节点
                    if(nodeContentLength*(chapterCount+1)>content.length()) {
                        newNode.setContent(content.substring(nodeContentLength*chapterCount,content.length()));
                        EOF=true;
                    }else {
                        newNode.setContent(content.substring(nodeContentLength*chapterCount,nodeContentLength*(chapterCount+1)));
                    }
                    //叶子节点相连接
                    thisNode.setNextNode(newNode);
                    thisNode=newNode;
                    indexNodeList.add(newNode);
                    chapterCount++;
                    //每subNodeListSize个节点包装一次或者到最后一个节点即使不足subNodeListSize也会封装
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
            throw new Exception("载入文件出错！");
        } finally {
            br.close();
            ir.close();
        }
        return rootNode;
    }

    /**
     * 根据B树查找相应章节
     * @param chapter
     * @return
     */
    public BtreeLeftNode findNode(Integer chapter) {
        BtreeLeftNode lefNode = null;
        Iterator<IIndexNode> iterator = ((BtreeIndexNode) this.rootNode).getSubNodes().iterator();
        while (iterator.hasNext()) {
            BtreeIndexNode node = (BtreeIndexNode) iterator.next();
            //根据结构，目录节点chapter-目标chapter<一个目录节点所能包含的节点数说明目标chapter在该目录节点中
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