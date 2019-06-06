package app.reader.control.ResultStructs;

import java.util.ArrayList;
import java.util.List;

import app.reader.control.Interfaces.IIndexNode;
/**
 * BtreeIndexNode使用了组合模式
 * @author www25
 *
 */
public  class BtreeIndexNode extends BtreeLeftNode{
	private List<IIndexNode> subNodes;//子节点集合
	
	public BtreeIndexNode(int chapter) {
		super(chapter);
	}
	
	public BtreeIndexNode() {
		super();
		this.subNodes=new ArrayList<IIndexNode>();
}

	public List<IIndexNode> getSubNodes() {
		return subNodes;
	}

	public void setSubNodes(List<IIndexNode> subNodes) {
		this.subNodes = subNodes;
	}
}
