package app.reader.control;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import app.reader.control.Interfaces.IMemory;


public class DefaultMemory implements IMemory, Serializable {
	private static final long serialVersionUID = 1L;
	private static String dirPath = null;
	private String bookFileName;
	private Integer chapter=0;
	private Integer page=1;
	private long time;

	public DefaultMemory(String bookFileName,Integer chapter, Integer page) {
		this.bookFileName=bookFileName;
		this.chapter = chapter;
		this.page = page;
	}

	public static String getDirPath() {
		return dirPath;
	}

	public static void setDirPath(String dirPath) {
		DefaultMemory.dirPath = dirPath;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}



	public DefaultMemory(){
		super();
	}

	public String getBookFileName() {
		return bookFileName;
	}

	public void setBookFileName(String bookFileName) {
		this.bookFileName = bookFileName;
	}

	public Integer getChapter() {
		return chapter;
	}

	public void setChapter(Integer chapter) {
		this.chapter = chapter;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	@Override
	public void save() throws Exception {
		ObjectOutputStream oi=null;
		try {
			this.time=System.currentTimeMillis();
			File file = new File(dirPath+"/"+bookFileName);
			if(!file.exists())
				file.createNewFile();
			oi= new ObjectOutputStream(new FileOutputStream(file));
			oi.writeObject(this);
			oi.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception("file not found!!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("写入出错！");
		}finally {
			if(oi!=null)
				try {
					oi.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new Exception("写入出错！");
				}
		}
	}

	public static List<IMemory> load() throws Exception {
		File dir = new File(dirPath);
		File[] listFiles = dir.listFiles();
		ObjectInputStream oo=null;
		List<IMemory> memoryList=new ArrayList<IMemory>();
		try {
			for(int i=0;i<listFiles.length;i++) {
				oo=new ObjectInputStream(new FileInputStream(listFiles[i]));
				DefaultMemory memory = (DefaultMemory) oo.readObject();
				memoryList.add(memory);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception("file not found!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("读入出错！");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new Exception("读入出错！");
		}finally {
			if(oo!=null)
				try {
					oo.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new Exception("读入出错！");
				}
		}
		return memoryList;
	}
}
