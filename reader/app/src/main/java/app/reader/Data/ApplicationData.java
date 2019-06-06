package app.reader.Data;

import android.app.Application;

import java.io.File;
import java.util.List;

/**
 * 保存全局变量的类
 */
public class ApplicationData extends Application {
    private File[] books;//书架中的书
    private List<String> indexList;//阅读的书的目录

    public File[] getBooks() {
        return books;
    }

    public void setBooks(File[] books) {
        this.books = books;
    }

    public List<String> getIndexList() {
        return indexList;
    }

    public void setIndexList(List<String> indexList) {
        this.indexList = indexList;
    }
}

