package app.reader.response;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;
import java.io.Serializable;

import app.reader.R;
import app.reader.activity.ReadingActivity;

/**
 * 设置书架ListView布局
 */
public class ShelfViewAdapter extends BaseAdapter {
    private File[] books;
    private Context myContext;

    public ShelfViewAdapter(File[] books, Context myContext) {
        this.books =books;
        this.myContext=myContext;
    }

    @Override
    public int getCount() {
        return books.length;
    }

    @Override
    public Object getItem(int position) {
        return books[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 图片和书名按钮都能响应跳往阅读页面
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            convertView= LayoutInflater.from(myContext).inflate(R.layout.view_bookshelflistviewitem, null);
        }
        ImageButton imageButton=convertView.findViewById(R.id.bookImg);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myContext.startActivity(
                        new Intent(myContext, ReadingActivity.class).
                                putExtra("book", books[position]));
            }
        });

        Button button=convertView.findViewById(R.id.bookName);
        button.setHint(books[position].getName());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myContext.startActivity(
                        new Intent(myContext, ReadingActivity.class).
                                putExtra("book", (Serializable) books[position]));
            }
        });
        return convertView;
    }
}

