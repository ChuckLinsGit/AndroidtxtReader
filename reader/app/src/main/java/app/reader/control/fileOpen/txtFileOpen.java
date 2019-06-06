package app.reader.control.fileOpen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import app.reader.control.Interfaces.IFileOpen;


public class txtFileOpen implements IFileOpen {
	private String charset;
	private  static txtFileOpen thisFile;
	private txtFileOpen() {
		super();
	}
	//单例的thisFile
	public static txtFileOpen getOpenFile() {
		if(thisFile==null) {
			thisFile=new txtFileOpen();
		}
		return thisFile;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public Object open(File in) throws Exception {
		InputStreamReader fin= null;
		try {
			fin = new InputStreamReader(new FileInputStream(in),charset);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw  new Exception("找不到文件");
		}
//		finally {
//			try {
//				fin.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//				throw  new Exception("IO Error");
//			}
//		}
		return fin;
	}

}
