package app.reader.response;


import android.content.Context;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupWindow;

import app.reader.R;
import app.reader.activity.ReadingActivity;

public class ReadingTextViewGestureListener implements GestureDetector.OnGestureListener {
    private Context actionContext;
    private static boolean focused=false;
    public ReadingTextViewGestureListener(Context actionContext){
        this.actionContext=actionContext;
    }
    //手势翻页
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        //从右向左滑
        if (e1.getX()-e2.getX()>=120){
            ((ReadingActivity)actionContext).pageTurning(1);//翻页函数
        }
        //从左向右滑
        else if (e1.getX()-e2.getX()<=-120){
            ((ReadingActivity)actionContext).pageTurning(-1);
        }
        //从上往下滑
        else if(e1.getY()-e2.getY()<=-50){
            PopupMenu menu = new PopupMenu(actionContext, ((ReadingActivity)actionContext).findViewById(R.id.popupmenuPosition));//将菜单与activity绑定
            MenuInflater menuInflater = menu.getMenuInflater();
            menuInflater.inflate(R.menu.readingtextview_popupmenu,menu.getMenu());//设置菜单样式
            menu.setOnMenuItemClickListener(new ReadingTextViewOnMenuClick(actionContext));//设置菜单点击响应
            menu.show();
        }
        //从下往上滑
        else if (e1.getY()-e2.getY()>=50){

        }
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        if (focused){
            ((ReadingActivity)actionContext).hideSystemUI();
        }else {
            ((ReadingActivity)actionContext).showSystemUI();
        }
        focused=!focused;
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }
}
