package app.reader.response;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import app.reader.R;
import app.reader.activity.ReadingActivity;

public class IndexListViewAdapter extends BaseAdapter {
    private Context actionContext;
    private List<String> indexList;
    private SimpleDialogFragment easyDialogFragment;
    public IndexListViewAdapter(Context actionContext, List<String> indexList, SimpleDialogFragment easyDialogFragment){
        this.actionContext=actionContext;
        this.indexList=indexList;
        this.easyDialogFragment=easyDialogFragment;
    }
    @Override
    public int getCount() {
        return indexList.size();
    }

    @Override
    public Object getItem(int position) {
        return indexList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        //��ȡ��ͼ
        if (convertView==null)
            convertView= LayoutInflater.from(actionContext).inflate(R.layout.view_readingindexlistviewitem, null);
        //�������
        TextView indexTV = convertView.findViewById(R.id.chapterIndex);
        indexTV.setText(String.valueOf(position)+" ");
        //���ñ���
        TextView titleTV = convertView.findViewById(R.id.chapterTitle);
        titleTV.setText(indexList.get(position));
        //������Ӧ
        titleTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ReadingActivity)actionContext).randomTurning(position,-1);//ListԪ�غ�Ŀ¼�ṹԪ���±궼��0��ʼ��position�����һ
                easyDialogFragment.dismiss();
            }
        });
        return convertView;
    }
}
