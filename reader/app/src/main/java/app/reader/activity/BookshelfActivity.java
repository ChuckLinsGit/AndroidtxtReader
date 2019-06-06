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
        ApplicationData data = (ApplicationData) getApplication();//全局变量books

        if (data.getBooks()==null){
            Intent reqIntent=getIntent();
            data.setBooks((File[]) reqIntent.getSerializableExtra("files"));
        }
        books=data.getBooks();

        //绑定ListView数据源
        //注意：由于旧版本GridLayout无法使用android:layout_columnWeight="1"属性，还需要兼容早前的版本
        //layout-v24-bookShelfListViewItem针对新版本
//！！！！layout-bookShelfListViewItem针对早前版本，还未实现！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！
        ShelfViewAdapter adapter=new ShelfViewAdapter(books, this);
        ((ListView)findViewById(R.id.bookShelfListView)).setAdapter(adapter);
    }
}
