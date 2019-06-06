package app.reader.response;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import app.reader.Data.ApplicationData;
import app.reader.R;
import app.reader.activity.ReadingActivity;
import app.reader.control.DefaultMark;
import app.reader.control.Interfaces.IMark;

public class MarkListViewAdatper extends BaseAdapter {
    private Context actionContext;
    private List<IMark> marksList;
    private String bookFileName;
    private SimpleDialogFragment easyDialogFragment;

    public MarkListViewAdatper(Context actionContext,List<IMark> marksList,String bookFileName,SimpleDialogFragment easyDialogFragment){
        this.actionContext=actionContext;
        this.marksList=marksList;
        this.bookFileName=bookFileName;
        this.easyDialogFragment=easyDialogFragment;
    }
    @Override
    public int getCount() {
        return marksList.size();
    }

    @Override
    public Object getItem(int position) {
        return marksList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //获取目录ListView视图
        if (convertView==null)
            convertView= LayoutInflater.from(actionContext).inflate(R.layout.view_readingindexlistviewitem, null);

        final DefaultMark m = (DefaultMark) getItem(position);
        ApplicationData application =(ApplicationData) ((ReadingActivity) actionContext).getApplication();
        List<String> indexList = application.getIndexList();
        //序号
        TextView indexTV = convertView.findViewById(R.id.chapterIndex);
        indexTV.setText(Integer.valueOf(position)+" ");
        //设置标题
        TextView titleTV = convertView.findViewById(R.id.chapterTitle);
        titleTV.setText(" 第"+m.getMarkChapter()+"章 "+indexList.get(m.getMarkChapter()));
        //设置响应
        titleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReadingActivity)actionContext).randomTurning(m.getMarkChapter(),m.getMarkPage());
                easyDialogFragment.dismiss();
            }
        });
        return convertView;
    }
}
