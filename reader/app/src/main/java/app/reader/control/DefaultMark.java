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

import app.reader.control.Interfaces.IMark;

public class DefaultMark implements IMark, Serializable {
	private static final long serialVersionUID = 1L;
	private String bookFileName;
	private Integer markChapter;
	private Integer markPage;
	private static String dirPath="";
	private long time;

	public DefaultMark(String bookFileName, Integer markChapter, Integer markPage) {
		super();
		this.bookFileName = bookFileName;
		this.markChapter = markChapter;
		this.markPage = markPage;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public Integer getMarkChapter() {
		return markChapter;
	}
	public void setMarkChapter(Integer markChapter) {
		this.markChapter = markChapter;
	}
	public Integer getMarkPage() {
		return markPage;
	}
	public void setMarkPage(Integer markPage) {
		this.markPage = markPage;
	}

	public static void setDirPath(String dirPath){DefaultMark.dirPath=dirPath;}

	public static String getDirPath(){return  DefaultMark.dirPath;}

	public String getBookFileName() {
		return bookFileName;
	}
	public void setBookFileName(String bookFileName) {
		this.bookFileName = bookFileName;
	}

	/**
	 * ��ǩ���������������½ڡ�ҳ����ʱ��
	 * @throws Exception
	 */
	public void saveMark() throws Exception {
		ObjectOutputStream oi=null;
		try {
			this.time=System.currentTimeMillis();
			File file = new File(dirPath+"/"+this.time);
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
			throw new Exception("д�����");
		}finally {
			if(oi!=null)
				try {
					oi.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new Exception("д�����");
				}
		}
	}
	/**
	 /* ��ȡĿ¼�е�������ǩ
	 / * @return
	 / * @throws Exception
	 */
	public static List<IMark> loadMarks() throws Exception {
		ObjectInputStream oo=null;
		ArrayList<IMark> markList=new ArrayList<IMark>();
		try {
			File dir = new File(dirPath);
			File[] listFiles = dir.listFiles();
			if (listFiles!=null){
				for(int i=0;i<listFiles.length;i++) {
					oo=new ObjectInputStream(new FileInputStream(listFiles[i]));
					markList.add((IMark) oo.readObject());
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new Exception("file not found!");
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("�������");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new Exception("�������");
		}finally {
			if(oo!=null)
				try {
					oo.close();
				} catch (IOException e) {
					e.printStackTrace();
					throw new Exception("�������");
				}
		}
		return markList;
	}

}
