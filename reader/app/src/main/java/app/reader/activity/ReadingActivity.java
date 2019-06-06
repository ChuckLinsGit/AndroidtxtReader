package app.reader.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import app.reader.Data.ApplicationData;
import app.reader.R;
import app.reader.control.DefaultMemory;
import app.reader.control.Interfaces.IIndexNode;
import app.reader.control.Interfaces.IMemory;
import app.reader.control.ResultStructs.BtreeIndexNode;
import app.reader.control.ResultStructs.BtreeLeftNode;
import app.reader.control.ResultStructs.BtreeStruct;
import app.reader.control.fileOpen.txtFileOpen;
import app.reader.response.ReadingTextViewGestureListener;

/**
 * 耦合了reader功能的Activity
 *
 * 换书需要重新读入结构
 * 读入保存节点需要找到对应书本的保存节点-----在Memory中实现
 *
 * 页中字数通过计算屏幕面积除以字体大小得到
 * 章节跳转需要在thisBtreeStruct中找到对应章节并将content赋予chapterContent
 * 下一章节只需thisChapterNode的nextNode的content赋予chapterContent
 * 翻页和跳章都需要重新刷新ReadingActivity，因此设置了inited做标志防止重复读入结构
 *
 * 改进：使用中介者模式分离文本操作方法。
 */
public class ReadingActivity extends FragmentActivity {
    public static final double specialTime = 1.45;//计算字体大小
    private String basePath ;//默认路径
    private File thisBook=null;
    private BtreeStruct thisBtreeStruct;//书的结构
    private String fileOpenCharset="GB2312";//文本打开的编码
    private BtreeLeftNode thisChapterNode=null;//当前章节节点
    private String chapterContent;//章节内容
    private String chapterContentCharset="UTF-8";//读出文本的编码
    private float chapterTextSize=0;
    private Integer thisChapter=1;//当前章节
    private Integer thisPage=1;//当前页数
    private TextView textView;//文本框
    private Integer pageSize;//页中字数
    private GestureDetector gestureDetector;//手势探测器

    public String getBasePath() {
        return basePath;
    }
    public BtreeStruct getThisBtreeStruct(){return this.thisBtreeStruct;}
    public Integer getThisChapter(){return thisChapter;}
    public Integer getThisPage(){return  thisPage;}
    public void setThisChapter(Integer thisChapter) {
        this.thisChapter = thisChapter;
    }
    public void setThisPage(Integer thisPage) {
        this.thisPage = thisPage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readingview);
        View decorView = getWindow().getDecorView();

        basePath=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).
                                                     getPath()+ getString(R.string.extendsFilePath);
        Intent intent = getIntent();
        File intentBook = (File) intent.getSerializableExtra("book");

        //如果intentBook不为空，说明需要考虑初始化书本
        //如果thisBook为空表明还未读入书本，需要初始化
        //如果inited为真且intentBook的文件名与thisBook不同，则说明需要换书后重新初始化
        if (intentBook!=null){
            if (this.thisBook ==null){
                this.thisBook = intentBook;
            }else if (intentBook.getName()== this.thisBook.getName()){
                this.thisBook =(File) intent.getParcelableExtra("thisBook");
            }
        }
        hideSystemUI();
        textView=findViewById(R.id.readingTextView);
        //一页最大字数
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        setChapterTextSize();//得到文本字体面积大小
        pageSize=(int)(width*height/(chapterTextSize*specialTime));
        //读进文本,初始化文本结构
        try {
            txtFileOpen openFile = (txtFileOpen) txtFileOpen.getOpenFile();
            openFile.setCharset(fileOpenCharset);
            thisBtreeStruct = new BtreeStruct((InputStreamReader) (openFile.open(this.thisBook)),intentBook.getName());
        } catch (Exception e) {
            //出错，跳回书架
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("错误");
            builder.setMessage(e.getMessage()+"\n请重试。");
            builder.setPositiveButton(R.string.dialogConfirm , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                }
            });
            builder.show();
        }
        if (thisBtreeStruct!=null) {
            try {
                thisBtreeStruct.product();
                System.out.println("producted");
            } catch (Exception e) {
                //出错，跳回书架
                e.printStackTrace();
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("错误");
                builder.setMessage(e.getMessage()+"\n请重试。");
                builder.setPositiveButton(R.string.dialogConfirm , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                    }
                });
                builder.show();
            }
        }
        //将目录保存为全局变量
        IIndexNode rootNode =thisBtreeStruct.getRootNode();//得到目录结构首节点
        BtreeLeftNode firstLeftNode = (BtreeLeftNode)((BtreeIndexNode)((BtreeIndexNode) (rootNode)).getSubNodes().get(0)).getSubNodes().get(0);
        ApplicationData data = (ApplicationData) getApplication();
        List<String> indexList=new ArrayList<String>();
        while (firstLeftNode.getNextNode()!=null){
            indexList.add(firstLeftNode.getTitle());
            firstLeftNode=firstLeftNode.getNextNode();
        }
        data.setIndexList(indexList);

        //读入保存点。还要考虑每本书的保存点
        loadSavePoint();
        //滑动手势翻页和长按呼出菜单
        gestureDetector=new GestureDetector(this, new ReadingTextViewGestureListener(this));

        //如果结尾字符下标不超过字符最大下标，则输出到该下标，否则全部输出
        if (pageSize*(thisPage)+1<=chapterContent.length())
            textView.setText(chapterContent.substring(pageSize*(thisPage-1), pageSize*(thisPage)+1));
        else if(pageSize*(thisPage)+1>chapterContent.length())
            textView.setText(chapterContent.substring(pageSize*(thisPage-1)));
    }

    /**
     * 实现滑动手势翻页，必须实现onTouchEvent()方法
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return this.gestureDetector.onTouchEvent(event);
    }

    @Override
    protected void onDestroy() {
        DefaultMemory saveMemory = new DefaultMemory(thisBtreeStruct.getBookFileName(), thisChapter, thisPage);
        try {
            saveMemory.save();
        } catch (Exception e) {
            //出错，跳回书架
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("错误");
            builder.setMessage(e.getMessage()+"\n确认强制退出");
            builder.setPositiveButton(R.string.dialogConfirm ,  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                }
            });
            builder.show();
            System.exit(0);
        }
        super.onDestroy();
    }

    public void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    public void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * 计算文本字体面积大小
     */
    private void setChapterTextSize(){
        Paint paint = new Paint();
        String sampleStr="宝";
        paint.setTextSize(textView.getTextSize());
        float strWidth = paint.measureText(sampleStr);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//        float height1 = fontMetrics.descent - fontMetrics.ascent;未计算文本顶和脚的留白
        float strHeight = fontMetrics.bottom - fontMetrics.top;
        this.chapterTextSize=strWidth*strHeight;
    }

    /**
     * 载入保存节点
     */
    private void loadSavePoint() {
        //寻找保存点
        DefaultMemory memory=new DefaultMemory();
        List<IMemory> loads=new ArrayList<>();
        try {
            DefaultMemory.setDirPath( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+getString(R.string.extendsFilePath)+"memories");
            loads = memory.load();
        } catch (Exception e) {
            //出错，跳回书架
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("错误");
            builder.setMessage(e.getMessage()+"\n请重试。");
            builder.setPositiveButton(R.string.dialogConfirm ,  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                }
            });
            builder.show();
        }
        //找到对应书籍的保存点，否则从第一章第一个页开始
        if (loads.size()!=0){
            for (IMemory m:loads) {
                if(((DefaultMemory) m).getBookFileName().equals(thisBtreeStruct.getBookFileName()))
                    memory=(DefaultMemory)m;
            }
        }
        thisChapter=memory.getChapter();
        //找章节
        try {
            findChapter(thisChapter);
            setChapterContent(chapterContentCharset);
        } catch (Exception e) {
            //出错，跳回书架
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("错误");
            builder.setMessage(e.getMessage());
            builder.setPositiveButton(R.string.dialogConfirm ,  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                }
            });
            builder.show();
        }
        //找页
        thisPage=memory.getPage();
    }


    /**
     * direction是翻页方向
     * ！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！！* 往前翻页还未实现，难点在于转换节点，需要从Struct入手，修改目录节点结构
     * @param direction
     */
    public void  pageTurning(Integer direction){
        thisPage+=direction;
        //设置页面文字
        //页面从1开始数
        //开头字符下标为pageSize*（thisPage-1），结尾下标为pageSize*thisPage+1(加一是因为subString截止结尾下标前一个字符)
        // 如：第一页为0-pageSize
        try {
            //开头字符下标不小于字符最大下标则进入下一个章节,小于0则回到上一章节最后一页
            if(pageSize*(thisPage-1)>=chapterContent.length()){
                BtreeLeftNode nextNode = thisChapterNode.getNextNode();
                if (nextNode!=null){
                    thisChapterNode=nextNode;
                    setChapterContent(chapterContentCharset);
                    thisPage=1;
                }else {
                    //最后一个节点，直接返回，不改变TextView内容
                    return;
                }
            }else if(pageSize*(thisPage-1)<0){
                BtreeLeftNode lastNode = thisChapterNode.getLastNode();
                if (lastNode!=null){
                    thisChapterNode=lastNode;
                    setChapterContent(chapterContentCharset);
                    int theLastPageSize=chapterContent.length()%pageSize;//最后一页的字数
                    thisPage=(int)(chapterContent.length()/pageSize);
                    if (theLastPageSize!=0){
                        thisPage++;
                    }
                }else {
                    //第一个节点，直接返回，不改变TextView内容
                    return;
                }
            }
            thisChapter=thisChapterNode.getChapter();
        } catch (UnsupportedEncodingException e) {
            //出错，跳回书架
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("错误");
            builder.setMessage(e.getMessage());
            builder.setPositiveButton(R.string.dialogConfirm ,  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                }
            });
            builder.show();
        }

        //如果结尾字符下标不超过字符最大下标，则输出到该下标，否则全部输出
        if (pageSize*(thisPage)+1<=chapterContent.length())
            textView.setText(chapterContent.substring(pageSize*(thisPage-1), pageSize*(thisPage)+1));
        else if(pageSize*(thisPage)+1>chapterContent.length())
            textView.setText(chapterContent.substring(pageSize*(thisPage-1)));

        textView.invalidate();
    }

    /**
     * 任意跳转跳转
     * @param chapter 章节
     * @param page 页数,等于-1时表示从第一页开始
     */
    public void randomTurning(Integer chapter, Integer page){
        thisChapter=chapter;
        try {
            findChapter(thisChapter);
            setChapterContent(chapterContentCharset);
        } catch (Exception e) {
            //出错，跳回书架
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("错误");
            builder.setMessage(e.getMessage());
            builder.setPositiveButton(R.string.dialogConfirm ,  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                }
            });
            builder.show();
        }
        if (page==-1){
            thisPage=1;
        }else {
            thisPage=page;
        }
        pageTurning(0);
    }

    /**
     * 找到相应节点
     * @param chapter
     * @throws ‘Exception
     */
    private void findChapter(Integer chapter) throws Exception {
        thisChapterNode=thisBtreeStruct.findNode(chapter);
        if (thisChapterNode==null){
            throw new Exception("找不到对应章节");
        }
    }

    private void setChapterContent(String charsetName) throws UnsupportedEncodingException {
        //更换字符集，防止中文乱码
        chapterContent=new String(thisChapterNode.getContent().getBytes(),charsetName);
    }

}
