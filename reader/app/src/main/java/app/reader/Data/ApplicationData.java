package app.reader.Data;

import android.app.Application;

import java.io.File;
import java.util.List;

/**
 * ����ȫ�ֱ�������
 */
public class ApplicationData extends Application {
    private File[] books;//����е���
    private List<String> indexList;//�Ķ������Ŀ¼

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

