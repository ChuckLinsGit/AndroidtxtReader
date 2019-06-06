package app.reader.control.ResultStructs;


import app.reader.control.Interfaces.IIndexNode;

public class BtreeLeftNode implements IIndexNode {
	private Integer chapter;
	private String title;
	private String content;
	private BtreeLeftNode nextNode;
	private BtreeLeftNode lastNode;
	
	public BtreeLeftNode(int chapter) {
		this.chapter=chapter;
	}
	
	public BtreeLeftNode() {
		super();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getChapter() {
		return chapter;
	}

	public void setChapter(Integer chapter) {
		this.chapter = chapter;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setNextNode(BtreeLeftNode nextNode){this.nextNode=nextNode;}

	public BtreeLeftNode getLastNode() {
		return lastNode;
	}

	public void setLastNode(BtreeLeftNode lastNode) {
		this.lastNode = lastNode;
	}

	public BtreeLeftNode getNextNode(){return this.nextNode;}
}
