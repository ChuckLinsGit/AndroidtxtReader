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
 * �����reader���ܵ�Activity
 *
 * ������Ҫ���¶���ṹ
 * ���뱣��ڵ���Ҫ�ҵ���Ӧ�鱾�ı���ڵ�-----��Memory��ʵ��
 *
 * ҳ������ͨ��������Ļ������������С�õ�
 * �½���ת��Ҫ��thisBtreeStruct���ҵ���Ӧ�½ڲ���content����chapterContent
 * ��һ�½�ֻ��thisChapterNode��nextNode��content����chapterContent
 * ��ҳ�����¶���Ҫ����ˢ��ReadingActivity�����������inited����־��ֹ�ظ�����ṹ
 *
 * �Ľ���ʹ���н���ģʽ�����ı�����������
 */
public class ReadingActivity extends FragmentActivity {
    public static final double specialTime = 1.45;//���������С
    private String basePath ;//Ĭ��·��
    private File thisBook=null;
    private BtreeStruct thisBtreeStruct;//��Ľṹ
    private String fileOpenCharset="GB2312";//�ı��򿪵ı���
    private BtreeLeftNode thisChapterNode=null;//��ǰ�½ڽڵ�
    private String chapterContent;//�½�����
    private String chapterContentCharset="UTF-8";//�����ı��ı���
    private float chapterTextSize=0;
    private Integer thisChapter=1;//��ǰ�½�
    private Integer thisPage=1;//��ǰҳ��
    private TextView textView;//�ı���
    private Integer pageSize;//ҳ������
    private GestureDetector gestureDetector;//����̽����

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

        //���intentBook��Ϊ�գ�˵����Ҫ���ǳ�ʼ���鱾
        //���thisBookΪ�ձ�����δ�����鱾����Ҫ��ʼ��
        //���initedΪ����intentBook���ļ�����thisBook��ͬ����˵����Ҫ��������³�ʼ��
        if (intentBook!=null){
            if (this.thisBook ==null){
                this.thisBook = intentBook;
            }else if (intentBook.getName()== this.thisBook.getName()){
                this.thisBook =(File) intent.getParcelableExtra("thisBook");
            }
        }
        hideSystemUI();
        textView=findViewById(R.id.readingTextView);
        //һҳ�������
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // ��Ļ��ȣ����أ�
        int height = metric.heightPixels;   // ��Ļ�߶ȣ����أ�
        setChapterTextSize();//�õ��ı����������С
        pageSize=(int)(width*height/(chapterTextSize*specialTime));
        //�����ı�,��ʼ���ı��ṹ
        try {
            txtFileOpen openFile = (txtFileOpen) txtFileOpen.getOpenFile();
            openFile.setCharset(fileOpenCharset);
            thisBtreeStruct = new BtreeStruct((InputStreamReader) (openFile.open(this.thisBook)),intentBook.getName());
        } catch (Exception e) {
            //�����������
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("����");
            builder.setMessage(e.getMessage()+"\n�����ԡ�");
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
                //�����������
                e.printStackTrace();
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setTitle("����");
                builder.setMessage(e.getMessage()+"\n�����ԡ�");
                builder.setPositiveButton(R.string.dialogConfirm , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                    }
                });
                builder.show();
            }
        }
        //��Ŀ¼����Ϊȫ�ֱ���
        IIndexNode rootNode =thisBtreeStruct.getRootNode();//�õ�Ŀ¼�ṹ�׽ڵ�
        BtreeLeftNode firstLeftNode = (BtreeLeftNode)((BtreeIndexNode)((BtreeIndexNode) (rootNode)).getSubNodes().get(0)).getSubNodes().get(0);
        ApplicationData data = (ApplicationData) getApplication();
        List<String> indexList=new ArrayList<String>();
        while (firstLeftNode.getNextNode()!=null){
            indexList.add(firstLeftNode.getTitle());
            firstLeftNode=firstLeftNode.getNextNode();
        }
        data.setIndexList(indexList);

        //���뱣��㡣��Ҫ����ÿ����ı����
        loadSavePoint();
        //�������Ʒ�ҳ�ͳ��������˵�
        gestureDetector=new GestureDetector(this, new ReadingTextViewGestureListener(this));

        //�����β�ַ��±겻�����ַ�����±꣬����������±꣬����ȫ�����
        if (pageSize*(thisPage)+1<=chapterContent.length())
            textView.setText(chapterContent.substring(pageSize*(thisPage-1), pageSize*(thisPage)+1));
        else if(pageSize*(thisPage)+1>chapterContent.length())
            textView.setText(chapterContent.substring(pageSize*(thisPage-1)));
    }

    /**
     * ʵ�ֻ������Ʒ�ҳ������ʵ��onTouchEvent()����
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
            //�����������
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("����");
            builder.setMessage(e.getMessage()+"\nȷ��ǿ���˳�");
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
     * �����ı����������С
     */
    private void setChapterTextSize(){
        Paint paint = new Paint();
        String sampleStr="��";
        paint.setTextSize(textView.getTextSize());
        float strWidth = paint.measureText(sampleStr);
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
//        float height1 = fontMetrics.descent - fontMetrics.ascent;δ�����ı����ͽŵ�����
        float strHeight = fontMetrics.bottom - fontMetrics.top;
        this.chapterTextSize=strWidth*strHeight;
    }

    /**
     * ���뱣��ڵ�
     */
    private void loadSavePoint() {
        //Ѱ�ұ����
        DefaultMemory memory=new DefaultMemory();
        List<IMemory> loads=new ArrayList<>();
        try {
            DefaultMemory.setDirPath( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath()+getString(R.string.extendsFilePath)+"memories");
            loads = memory.load();
        } catch (Exception e) {
            //�����������
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("����");
            builder.setMessage(e.getMessage()+"\n�����ԡ�");
            builder.setPositiveButton(R.string.dialogConfirm ,  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                }
            });
            builder.show();
        }
        //�ҵ���Ӧ�鼮�ı���㣬����ӵ�һ�µ�һ��ҳ��ʼ
        if (loads.size()!=0){
            for (IMemory m:loads) {
                if(((DefaultMemory) m).getBookFileName().equals(thisBtreeStruct.getBookFileName()))
                    memory=(DefaultMemory)m;
            }
        }
        thisChapter=memory.getChapter();
        //���½�
        try {
            findChapter(thisChapter);
            setChapterContent(chapterContentCharset);
        } catch (Exception e) {
            //�����������
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("����");
            builder.setMessage(e.getMessage());
            builder.setPositiveButton(R.string.dialogConfirm ,  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                }
            });
            builder.show();
        }
        //��ҳ
        thisPage=memory.getPage();
    }


    /**
     * direction�Ƿ�ҳ����
     * ������������������������������������������������������������������* ��ǰ��ҳ��δʵ�֣��ѵ�����ת���ڵ㣬��Ҫ��Struct���֣��޸�Ŀ¼�ڵ�ṹ
     * @param direction
     */
    public void  pageTurning(Integer direction){
        thisPage+=direction;
        //����ҳ������
        //ҳ���1��ʼ��
        //��ͷ�ַ��±�ΪpageSize*��thisPage-1������β�±�ΪpageSize*thisPage+1(��һ����ΪsubString��ֹ��β�±�ǰһ���ַ�)
        // �磺��һҳΪ0-pageSize
        try {
            //��ͷ�ַ��±겻С���ַ�����±��������һ���½�,С��0��ص���һ�½����һҳ
            if(pageSize*(thisPage-1)>=chapterContent.length()){
                BtreeLeftNode nextNode = thisChapterNode.getNextNode();
                if (nextNode!=null){
                    thisChapterNode=nextNode;
                    setChapterContent(chapterContentCharset);
                    thisPage=1;
                }else {
                    //���һ���ڵ㣬ֱ�ӷ��أ����ı�TextView����
                    return;
                }
            }else if(pageSize*(thisPage-1)<0){
                BtreeLeftNode lastNode = thisChapterNode.getLastNode();
                if (lastNode!=null){
                    thisChapterNode=lastNode;
                    setChapterContent(chapterContentCharset);
                    int theLastPageSize=chapterContent.length()%pageSize;//���һҳ������
                    thisPage=(int)(chapterContent.length()/pageSize);
                    if (theLastPageSize!=0){
                        thisPage++;
                    }
                }else {
                    //��һ���ڵ㣬ֱ�ӷ��أ����ı�TextView����
                    return;
                }
            }
            thisChapter=thisChapterNode.getChapter();
        } catch (UnsupportedEncodingException e) {
            //�����������
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("����");
            builder.setMessage(e.getMessage());
            builder.setPositiveButton(R.string.dialogConfirm ,  new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    startActivity(new Intent(ReadingActivity.this,BookshelfActivity.class));
                }
            });
            builder.show();
        }

        //�����β�ַ��±겻�����ַ�����±꣬����������±꣬����ȫ�����
        if (pageSize*(thisPage)+1<=chapterContent.length())
            textView.setText(chapterContent.substring(pageSize*(thisPage-1), pageSize*(thisPage)+1));
        else if(pageSize*(thisPage)+1>chapterContent.length())
            textView.setText(chapterContent.substring(pageSize*(thisPage-1)));

        textView.invalidate();
    }

    /**
     * ������ת��ת
     * @param chapter �½�
     * @param page ҳ��,����-1ʱ��ʾ�ӵ�һҳ��ʼ
     */
    public void randomTurning(Integer chapter, Integer page){
        thisChapter=chapter;
        try {
            findChapter(thisChapter);
            setChapterContent(chapterContentCharset);
        } catch (Exception e) {
            //�����������
            e.printStackTrace();
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setTitle("����");
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
     * �ҵ���Ӧ�ڵ�
     * @param chapter
     * @throws ��Exception
     */
    private void findChapter(Integer chapter) throws Exception {
        thisChapterNode=thisBtreeStruct.findNode(chapter);
        if (thisChapterNode==null){
            throw new Exception("�Ҳ�����Ӧ�½�");
        }
    }

    private void setChapterContent(String charsetName) throws UnsupportedEncodingException {
        //�����ַ�������ֹ��������
        chapterContent=new String(thisChapterNode.getContent().getBytes(),charsetName);
    }

}
