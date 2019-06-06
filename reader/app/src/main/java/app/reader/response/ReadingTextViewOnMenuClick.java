package app.reader.response;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.PopupMenu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import app.reader.Data.ApplicationData;
import app.reader.R;
import app.reader.activity.ReadingActivity;
import app.reader.control.DefaultMark;
import app.reader.control.Interfaces.IMark;
import app.reader.control.ResultStructs.BtreeStruct;

/**
 * 菜单响应类
 */
public class ReadingTextViewOnMenuClick implements PopupMenu.OnMenuItemClickListener {
    private Context actionContext;
    private AlertDialog.Builder builder;
    DefaultMark mark = null;

    public ReadingTextViewOnMenuClick(Context actionContext) {
        this.actionContext = actionContext;
        String bookFileName = ((BtreeStruct) ((ReadingActivity) actionContext).getThisBtreeStruct()).getBookFileName();
        mark=new DefaultMark(bookFileName,1,1);
        DefaultMark.setDirPath(((ReadingActivity) actionContext).getBasePath()+"marks");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onMenuItemClick(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mark:
                try {
                    String bookFileName = ((BtreeStruct) ((ReadingActivity) actionContext).getThisBtreeStruct()).getBookFileName();
                    Integer thisChapter = ((ReadingActivity) actionContext).getThisChapter();
                    Integer thisPage = ((ReadingActivity) actionContext).getThisPage();
                    mark.setMarkPage(thisPage);
                    mark.setMarkChapter(thisChapter);
                    mark.setBookFileName(bookFileName);
                    mark.saveMark();
                } catch (Exception e) {
                    e.printStackTrace();
                    builder=new AlertDialog.Builder(actionContext);
                    builder.setTitle("错误");
                    builder.setMessage(e.getMessage()+"\n请重试。");
                    builder.setPositiveButton(R.string.dialogConfirm , null);
                    builder.show();
                }
                break;
            case R.id.marks:
                try {
                    SimpleDialogFragment easyDialogFragment = new SimpleDialogFragment();
                    builder = new AlertDialog.Builder(actionContext);
                    builder.setTitle("书签目录");
                    //得到书签目录
                    List<IMark> iMarks = mark.loadMarks();
                    List<IMark> thisBookMars=new ArrayList<IMark>();
                    //只要本书书签
                    boolean existed = false;
                    if (iMarks.size() != 0) {
                        Iterator<IMark> iterator = iMarks.iterator();
                        while (iterator.hasNext()) {
                            DefaultMark next = (DefaultMark) iterator.next();
                            if (next.getBookFileName().equals(mark.getBookFileName())) {
                                thisBookMars.add(next);
                                existed = true;
                            }
                        }
                    }
                    if (!existed) {
                        builder = new AlertDialog.Builder(actionContext);
                        builder.setTitle("提示");
                        builder.setMessage("这本书没有书签哦");
                        builder.setPositiveButton(R.string.dialogConfirm, null);
                        builder.show();
                        break;
                    } else {
                        //为ListView 绑定数据
                        View layout = (((ReadingActivity) actionContext).getLayoutInflater()).inflate(R.layout.view_readingindex, null);
                        View indexListView = layout.findViewById(R.id.indexListView);
                        ((ListView) indexListView).setAdapter(new MarkListViewAdatper(actionContext, thisBookMars, mark.getBookFileName(), easyDialogFragment));
                        // 为对话框绑定自定义视图
                        builder.setView(layout);
                        easyDialogFragment.setDialog(builder);
                        easyDialogFragment.show(((ReadingActivity) actionContext).getSupportFragmentManager(), "");
                        break;
                    }
                } catch(Exception e){
                    e.printStackTrace();
                    builder = new AlertDialog.Builder(actionContext);
                    builder.setTitle("错误");
                    builder.setMessage(e.getMessage() + "\n请重试。");
                    builder.setPositiveButton(R.string.dialogConfirm, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
            case R.id.callIndex:
                //自定义对话框管理容器
                SimpleDialogFragment easyDialogFragment = new SimpleDialogFragment();
                builder=new AlertDialog.Builder(actionContext);
                builder.setTitle("目录");
                //为ListView 绑定数据
                ApplicationData application = (ApplicationData)((ReadingActivity) actionContext).getApplication();
                List<String> indexList = application.getIndexList();
                View layout = (((ReadingActivity) actionContext).getLayoutInflater()).inflate(R.layout.view_readingindex, null);
                View indexListView = layout.findViewById(R.id.indexListView);
                ((ListView)indexListView).setAdapter(new IndexListViewAdapter(actionContext,indexList,easyDialogFragment));
                // 为对话框绑定自定义视图
                builder.setView(layout);
                easyDialogFragment.setDialog(builder);
                easyDialogFragment.show(((ReadingActivity) actionContext).getSupportFragmentManager(),"");
                break;
        }
        return false;
    }
}
