package app.reader.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.io.File;

import app.reader.Data.ApplicationData;
import app.reader.R;
import app.reader.response.ShelfViewAdapter;


public class BookshelfActivity extends Activity {
    private File[] books;
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookshelf);
        ApplicationData data = (ApplicationData) getApplication();//ȫ�ֱ���books

        if (data.getBooks()==null){
            Intent reqIntent=getIntent();
            data.setBooks((File[]) reqIntent.getSerializableExtra("files"));
        }
        books=data.getBooks();

        //��ListView����Դ
        //ע�⣺���ھɰ汾GridLayout�޷�ʹ��android:layout_columnWeight="1"���ԣ�����Ҫ������ǰ�İ汾
        //layout-v24-bookShelfListViewItem����°汾
//��������layout-bookShelfListViewItem�����ǰ�汾����δʵ�֣���������������������������������������������������������������������������������������������������������
        ShelfViewAdapter adapter=new ShelfViewAdapter(books, this);
        ((ListView)findViewById(R.id.bookShelfListView)).setAdapter(adapter);
    }
}
