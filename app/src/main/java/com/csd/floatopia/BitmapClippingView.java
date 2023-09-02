package com.csd.floatopia;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;

import java.util.ArrayList;
import java.util.List;

public class BitmapClippingView extends View {

    public BitmapClippingView(Context context) {
        this(context,null);
    }

    public BitmapClippingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public BitmapClippingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float widthSize;//布局宽度
    private float heightSize;//布局高度

    private Bitmap bitmap;//要裁剪的位图
    private Bitmap mBitmap;
    private float bitmapWidth;//位图原始宽度
    private float bimapHeight;//位图原始高度
    private float proportionWidth;//比例：宽  如裁图比例3:4，此处传3
    private float proportionHeight;//比例：高  如裁图比例3:4，此处传4

    private Paint bitmapPaint;//图片画笔
    private Paint shadowPaint;//阴影画笔
    private Paint linePaint;//线条画笔

    private Paint tuyaPaint;//涂鸦画笔
    float scaleStep;//缩放比例

    private boolean initTag=true;//用于判断是不是首次绘制
    private float leftLine=-1;//选区左线
    private float topLine=-1;//选区上线
    private float rightLine=-1;//选区右线
    private float bottomLine=-1;//选区下线
    private float mindtopLine=-1;//上中选区

    private float mindleftLine=-1;//左中选区

    private String Clip_Mod="Clip";//模式

    private String MOD_CLIP ="Clip"; //剪裁模式

    private String MOD_PEN ="PEN";//画笔模式
    private String focus="NONE";//事件焦点
    private final String LEFT_TOP="LEFT_TOP";//LEFT_TOP:拖动左上角
    private final String LEFT_BOTTOM="LEFT_BOTTOM";//LEFT_BOTTOM:拖动左下角
    private final String MID_TOP="MID_TOP";//MID_TOP:拖动上中点
    private final String MID_LEFT="MID_LEFT";//MID_TOP:拖动左中点
    private final String MID_RIGHT="MID_RIGHT";//MMID_RIGHT:拖动右中点
    private final String MID_BOTTOM="MID_BOTTOM";//MID_BOTTOM:拖动下中点
    private final String BODY="BODY";//BODY：拖动整体
    private final String RIGHT_TOP="RIGHT_TOP";//RIGHT_TOP:拖动右上角
    private final String RIGHT_BOTTOM="RIGHT_BOTTOM";//RIGHT_BOTTOM:拖动右下角
    private final String NONE="NONE";//NONE:释放焦点
    private Boolean bitmap_cut_p=false;

    private float pen_x,pen_y,pen_r;
    private final  RectF wai_rect=null;

    //Text my_text; //返回的文本
    private   short text_p; //文本读取标志位  识别完成：1         识别失败:-1          识别中:0       空闲状态: 2
    private   short Recognizer_ing=0;
    private   short Recognizer_Failure=-1;
    private   short Recognizer_Success=1;
    private   short Recognizer_null=2;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        widthSize=MeasureSpec.getSize(widthMeasureSpec);
        heightSize=MeasureSpec.getSize(heightMeasureSpec);

        bitmapPaint=new Paint();
        bitmapPaint.setStrokeWidth(0);

        shadowPaint=new Paint();
        shadowPaint.setColor(Color.parseColor("#50DCDCDC"));
        shadowPaint.setStrokeWidth(4);
        shadowPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        linePaint=new Paint();
        linePaint.setColor(Color.parseColor("#FFBAB7"));
        linePaint.setStrokeWidth(4);

        tuyaPaint=new Paint();
        tuyaPaint.setColor(Color.parseColor("#FF232220"));
        tuyaPaint.setStrokeWidth(4);
    }

    float mid_line_Wlong=1;//上中线长度
    float mid_line_Hlong=1;//左中线长度

    float old_back_left=1;

    float old_left=1;
    float old_back_right=1;
    float old_back_top=1;
    float old_back_bottom=1;
    float back_left,back_right,back_top,back_bottom,back_widthSize,back_hightSize,back_prox,back_proy,back_bw,back_bh,old_back_bw,old_back_bh;
    short move_where=2;// 0 拖底  1 拖 高  2拖宽，对角   3 空闲
    @SuppressLint("SuspiciousIndentation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bitmap==null)return;

        scaleStep=widthSize/bitmapWidth;
        float backgroundImgHeight=bimapHeight*scaleStep;
        heightSize=backgroundImgHeight;//把有效图像高度设置作为布局高度计算

        if (initTag){
            text_p=Recognizer_null;//空闲
            pen_r=5;
            //绘制参考背景背景
            Rect rect=new Rect(0,0,(int)bitmapWidth,(int)bimapHeight);//裁剪图片中的部分(此处：全图)
            RectF rectF=new RectF(0,0,widthSize,backgroundImgHeight);//显示在屏幕中的什么位置
            canvas.drawBitmap(bitmap,rect,rectF,bitmapPaint);
            canvas.save();

            //绘制初始状态的选框（最大选框）
            if (bitmapWidth>bimapHeight){
                //宽大于高，取高
                float checkboxHeight=backgroundImgHeight;//选框的高
                float checkboxWidth=((checkboxHeight/proportionHeight)*proportionWidth);//选框的宽
                leftLine=(widthSize/2f)-(checkboxWidth/2f);
                topLine=(heightSize/2f)-(checkboxHeight/2f);
                rightLine=(widthSize/2f)+(checkboxWidth/2f);
                bottomLine=(heightSize/2f)+(checkboxHeight/2f);
            }else {
                //高大于宽 取宽
                float checkboxWidth=widthSize;//选框的宽
                float checkboxHeight=(widthSize/proportionWidth)*proportionHeight;//选框的高
                leftLine=(widthSize/2f)-(checkboxWidth/2f);
                topLine=(heightSize/2f)-(checkboxHeight/2f);
                rightLine=(widthSize/2f)+(checkboxWidth/2f);
                bottomLine=(heightSize/2f)+(checkboxHeight/2f);
            }
            mid_line_Wlong=(rightLine-leftLine)*0.05F;
            mid_line_Hlong=(bottomLine-topLine)*0.05F;
            old_left=leftLine;

            initTag=false;

        }

        if (Clip_Mod.equals(MOD_CLIP)){//剪裁模式

            if (move_where==5){

                //约束背景比例，使其跟随剪裁框放大而不变形
                back_prox=(old_back_right-old_back_left)/(rightLine-leftLine);
                back_proy=(old_back_bottom-old_back_top)/(bottomLine-topLine);

                old_back_bw=old_back_right-old_back_left;
                old_back_bh=old_back_bottom-old_back_top;
                back_widthSize=0;
                back_hightSize=0;


                back_left=leftLine-50;
                back_right=rightLine+100;
                //防止左右拖动时图片扭曲
                if (leftLine<50){

                    if(back_left<0){
                        back_right=back_right+(-back_left);
                        back_left=0;
                    }
                    if (back_right>widthSize){
                        // back_left=back_left-(back_right-widthSize);
                        back_right=widthSize;
                    }
                }else{
                    if (back_right>widthSize){
                        back_left=back_left-(back_right-widthSize);
                        back_right=widthSize;
                    }
                    if(back_left<0){
                        // back_right=back_right+(-back_left);
                        back_left=0;
                    }
                }

                back_widthSize=back_right-back_left;

                if (back_widthSize<0)back_widthSize=0;


                //topLine=bottomLine-(((rightLine-leftLine)/proportionWidth)*proportionHeight);

                back_hightSize=(4/3)*back_widthSize;
                back_top=topLine-50;
                back_bottom=back_hightSize+back_top;

                if(back_bottom>heightSize){
                    back_top=back_top-(back_bottom-heightSize);
                    back_bottom=heightSize;
                }

                if(back_top<0)back_top=0;

            } else if (move_where==1||move_where==2) {
                back_bottom=bottomLine;
                back_top=topLine-50;
                if (back_top<0)back_top=0;

                back_hightSize=back_bottom-back_top;

                back_widthSize=(3F/4F)*back_hightSize/2f;

                float mid_rl=(rightLine+leftLine)/2;

                back_left=mid_rl-back_widthSize;

                back_right=mid_rl+back_widthSize;

                //防止左右拖动时图片扭曲
                if (back_left-old_left<50){

                    if(back_left<0){
                        back_right=back_right+(-back_left);
                        back_left=0;
                    }
                    if (back_right>widthSize){
                        // back_left=back_left-(back_right-widthSize);
                        back_right=widthSize;
                    }
                }else{
                    if (back_right>widthSize){
                        back_left=back_left-(back_right-widthSize);
                        back_right=widthSize;
                    }
                    if(back_left<0){
                        // back_right=back_right+(-back_left);
                        back_left=0;
                    }
                }

                if (back_right>widthSize)back_right=widthSize;
                if (back_left<0)back_left=0;
            }

            old_left=leftLine;

            int startX= (int) (back_left/scaleStep);
            int startY= (int) (back_top/scaleStep);
            int cutWidth=(int) ((back_right/scaleStep)-(back_left/scaleStep));
            int cutHeight=(int) (back_bottom/scaleStep-back_top/scaleStep);


            Bitmap newBitmap=Bitmap.createBitmap(bitmap,startX,startY,cutWidth,cutHeight,null,false);

            mBitmap=Bitmap.createScaledBitmap(newBitmap,(int)widthSize,(int)heightSize,true);

            Rect rect=new Rect(0,0,(int)bitmapWidth,(int)bimapHeight);//裁剪图片中的部分(此处：全图)

            RectF rectF=new RectF(0,0,widthSize,backgroundImgHeight);//显示在屏幕中的什么位置

            canvas.drawBitmap(mBitmap,rect,rectF,bitmapPaint);
            canvas.save();
            bitmap_cut_p=false;
        }
        else {
            Rect rect=new Rect(0,0,(int)bitmapWidth,(int)bimapHeight);//裁剪图片中的部分(此处：全图)

            RectF rectF=new RectF(0,0,widthSize,backgroundImgHeight);//显示在屏幕中的什么位置
            if (mBitmap!=null){
                canvas.drawBitmap(mBitmap,rect,rectF,bitmapPaint);
                canvas.save();

            }else {

                canvas.drawBitmap(bitmap,rect,rectF,bitmapPaint);
                canvas.save();

            }

        }

        if (Clip_Mod.equals(MOD_PEN)){
            canvas.drawCircle(pen_x,pen_y,pen_r,tuyaPaint);

            canvas.save();
        }

        mindtopLine=(rightLine+leftLine)/2;
        mindleftLine=(topLine+bottomLine)/2;
        Log.d("MY_MID_LONG","H:        "+mid_line_Hlong+"");
        Log.d("MY_MID_LONG","W:        "+mid_line_Wlong+"");
        //绘制选择的区域
        //绘制周边阴影部分（分四个方块）
        linePaint.setColor(Color.parseColor("#A0A0A000"));
        linePaint.setStrokeWidth(4);
        canvas.drawRect(0,0,leftLine,heightSize,shadowPaint);//左
        canvas.drawRect(leftLine+4,0,rightLine-4,topLine,shadowPaint);//上
        canvas.drawRect(rightLine,0,widthSize,heightSize,shadowPaint);//右
        canvas.drawRect(leftLine+4,bottomLine,rightLine-4,heightSize,shadowPaint);//下

        //绘制选区边缘线
        canvas.drawLine(leftLine,topLine,rightLine,topLine,linePaint);
        canvas.drawLine(rightLine,topLine,rightLine,bottomLine,linePaint);
        canvas.drawLine(rightLine,bottomLine,leftLine,bottomLine,linePaint);
        canvas.drawLine(leftLine,bottomLine,leftLine,topLine,linePaint);

        //绘制上下中和左右中调节点
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(6);
        canvas.drawLine(mindtopLine-mid_line_Wlong,topLine,mindtopLine+mid_line_Wlong,topLine,linePaint);//上中
        canvas.drawLine(leftLine,mindleftLine-mid_line_Hlong,leftLine,mindleftLine+mid_line_Hlong,linePaint);//左中
        canvas.drawLine(mindtopLine-mid_line_Wlong,bottomLine,mindtopLine+mid_line_Wlong,bottomLine,linePaint);//下中
        canvas.drawLine(rightLine,mindleftLine-mid_line_Hlong,rightLine,mindleftLine+mid_line_Hlong,linePaint);//右中


        //绘制左上和右下调节点
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(6);
        canvas.drawLine(rightLine-4,bottomLine-4,rightLine-4,bottomLine-mid_line_Hlong-4,linePaint);
        canvas.drawLine(rightLine-4,bottomLine-4,rightLine-mid_line_Wlong-4,bottomLine-4,linePaint);
        canvas.drawLine(leftLine+4,topLine+4,leftLine+mid_line_Wlong+4,topLine+4,linePaint);
        canvas.drawLine(leftLine+4,topLine+4,leftLine+4,topLine+mid_line_Hlong+4,linePaint);

        //绘制右上和左下调节点
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(6);
        //右上
        canvas.drawLine(rightLine,topLine+mid_line_Hlong+4,rightLine,topLine,linePaint);
        canvas.drawLine(rightLine,topLine,rightLine-mid_line_Wlong,topLine,linePaint);
        //左下
        canvas.drawLine(leftLine,bottomLine,leftLine+mid_line_Wlong,bottomLine,linePaint);
        canvas.drawLine(leftLine,bottomLine,leftLine,bottomLine-mid_line_Hlong,linePaint);

    /*
        //绘制焦点圆
        linePaint.setStrokeWidth(2);
        linePaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(rightLine-4,bottomLine-4,80,linePaint);
        canvas.drawCircle(leftLine+4,topLine+4,80,linePaint);

        //绘制扇形
        linePaint.setColor(Color.parseColor("#57FF0000"));
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        RectF mRectF = new RectF(rightLine-4-40, bottomLine-4-40, rightLine-4+40, bottomLine-4+40);
        canvas.drawArc(mRectF, 270, 270, true, linePaint);

        linePaint.setColor(Color.parseColor("#57FF0000"));
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        RectF mRectF2 = new RectF(leftLine+4-40, topLine+4-40, leftLine+4+40, topLine+4+40);
        canvas.drawArc(mRectF2, 90, 270, true, linePaint);

     */

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (leftLine==-1)return false;
        if (topLine==-1)return false;
        if (rightLine==-1)return false;
        if (bottomLine==-1)return false;
        if (bitmap==null)return false;

        float touchX=event.getX();
        float touchY=event.getY();

        if (event.getAction()==MotionEvent.ACTION_DOWN){
            return actionDown(touchX,touchY);
        }

        if (event.getAction()==MotionEvent.ACTION_MOVE){
            return actionMove(touchX,touchY);
        }

        if (event.getAction()==MotionEvent.ACTION_UP){
            return actionUp(touchX,touchY);
        }

        return true;
    }

    //抬起
    private boolean actionUp(float touchX, float touchY) {
        Log.d("fxHou","抬起X="+touchX+"   touchY="+touchY);
        Log.d("fxHou","释放焦点");
        shadowPaint.setColor(Color.parseColor("#A0DCDCDC"));
        postInvalidate();
        bitmap_cut_p=true;//背景缩放开启
        focus=NONE;//释放焦点
       // move_where=3;
        return true;
    }

    //移动
    private boolean actionMove(float touchX, float touchY) {
        Log.d("fxHou","滑动X="+touchX+"   touchY="+touchY);

        if (Clip_Mod.equals(MOD_CLIP)){



        if (focus.equals(LEFT_TOP)){
            shadowPaint.setColor(Color.parseColor("#50DCDCDC"));
            move_where=2;

            //重绘制中线选区
            mid_line_Wlong=(rightLine-leftLine)*0.05F;
            mid_line_Hlong=(bottomLine-topLine)*0.05F;

            //移动边线
            leftLine=touchX;
            topLine=bottomLine-(((rightLine-leftLine)/proportionWidth)*proportionHeight);//约束比例

            //限制最小矩形 宽
            if (rightLine-leftLine<100){
                leftLine=rightLine-100;
                topLine=bottomLine-(((rightLine-leftLine)/proportionWidth)*proportionHeight);
                //重绘
                postInvalidate();
                return true;
            }

            //限制最小矩形 高
            if (bottomLine-topLine<100){
                topLine=bottomLine-100;
                leftLine=rightLine-((bottomLine-topLine)/proportionHeight)*proportionWidth;
                //重绘
                postInvalidate();
                return true;
            }

            //防止超出边界
            if (leftLine<0){
                leftLine=0;
                topLine=bottomLine-(((rightLine-leftLine)/proportionWidth)*proportionHeight);
            }

            //防止超出边界
            if (topLine<0){
                topLine=0;
                leftLine=rightLine-((bottomLine-topLine)/proportionHeight)*proportionWidth;
            }

            //重绘
            postInvalidate();
            return true;
        }else
            if (focus.equals(RIGHT_BOTTOM)){
            shadowPaint.setColor(Color.parseColor("#50DCDCDC"));
                move_where=2;
            //重绘制中线选区
            mid_line_Wlong=(rightLine-leftLine)*0.05F;
            mid_line_Hlong=(bottomLine-topLine)*0.05F;

            //移动边线
            rightLine=touchX;
            bottomLine=topLine+(((rightLine-leftLine)/proportionWidth)*proportionHeight);//约束比例

            //限制最小矩形 宽
            if (rightLine-leftLine<100){
                rightLine=leftLine+100;
                bottomLine=topLine+(((rightLine-leftLine)/proportionWidth)*proportionHeight);
                //重绘
                postInvalidate();
                return true;
            }

            //限制最小矩形 高
            if (bottomLine-topLine<100){
                bottomLine=topLine+100;
                rightLine=leftLine+(((bottomLine-topLine)/proportionHeight)*proportionWidth);
                //重绘
                postInvalidate();
                return true;
            }

            //防止超出边界
            if (rightLine>widthSize){
                rightLine=widthSize;
                bottomLine=topLine+(((rightLine-leftLine)/proportionWidth)*proportionHeight);
            }

            //防止超出边界
            if (bottomLine>heightSize){
                bottomLine=heightSize;
                rightLine=leftLine+(((bottomLine-topLine)/proportionHeight)*proportionWidth);
            }
            //重绘
            postInvalidate();
            return true;
        }
        else if (focus.equals(BODY)){
            shadowPaint.setColor(Color.parseColor("#50DCDCDC"));
                move_where=2;
            float moveX=touchX-downX;
            float moveY=touchY-downY;
            leftLine=downLeftLine+moveX;
            rightLine=downRightLine+moveX;
            topLine=downTopLine+moveY;
            bottomLine=downBottomLine+moveY;

            if (leftLine<0){
                rightLine=(rightLine-leftLine);
                leftLine=0;

                if (topLine<0){
                    bottomLine=bottomLine-topLine;
                    topLine=0;
                    //重绘
                    postInvalidate();
                    return true;
                }

                if (bottomLine>heightSize){
                    topLine=heightSize-(bottomLine-topLine);
                    bottomLine=heightSize;
                    //重绘
                    postInvalidate();
                    return true;
                }

                //重绘
                postInvalidate();
                return true;
            }

            if (rightLine>widthSize){
                leftLine=widthSize-(rightLine-leftLine);
                rightLine=widthSize;

                if (topLine<0){
                    bottomLine=bottomLine-topLine;
                    topLine=0;
                    //重绘
                    postInvalidate();
                    return true;
                }

                if (bottomLine>heightSize){
                    topLine=heightSize-(bottomLine-topLine);
                    bottomLine=heightSize;
                    //重绘
                    postInvalidate();
                    return true;
                }

                //重绘
                postInvalidate();
                return true;
            }

            if (topLine<0){
                bottomLine=bottomLine-topLine;
                topLine=0;
                //重绘
                postInvalidate();
                return true;
            }

            if (bottomLine>heightSize){
                topLine=heightSize-(bottomLine-topLine);
                bottomLine=heightSize;
                //重绘
                postInvalidate();
                return true;
            }
            //重绘
            postInvalidate();
            return true;
        }
        else if(focus.equals(MID_TOP)){
                move_where=1;
            shadowPaint.setColor(Color.parseColor("#50DCDCDC"));
            //移动边线
            topLine=touchY;

            //限制最小矩形 高
            if (bottomLine-topLine<100){
                topLine=bottomLine-100;
                //重绘
                postInvalidate();
                return true;
            }

            //防止超出边界
            if (topLine<0){
                topLine=0;
            }
            //重绘制中线选区
            mid_line_Wlong=(rightLine-leftLine)*0.05F;
            mid_line_Hlong=(bottomLine-topLine)*0.05F;

            //重计算比例 proportionHeight* (W/H)
            proportionHeight=1/((rightLine-leftLine)/(bottomLine-topLine))*(rightLine-leftLine);
            proportionWidth=((rightLine-leftLine)/(bottomLine-topLine))*(bottomLine-topLine);

            //重绘
            postInvalidate();
            return true;
        }
        else if(focus.equals(MID_LEFT)){
                move_where=2;

            shadowPaint.setColor(Color.parseColor("#50DCDCDC"));
            //移动边线
            leftLine=touchX;

            //限制最小矩形 宽
            if (rightLine-leftLine<100){
                leftLine=rightLine-100;
                topLine=bottomLine-(((rightLine-leftLine)/proportionWidth)*proportionHeight);
                //重绘
                postInvalidate();
                return true;
            }
            //防止超出边界
            if (leftLine<0){
                leftLine=0;
            }

            //重绘制中线选区
            mid_line_Wlong=(rightLine-leftLine)*0.05F;
            mid_line_Hlong=(bottomLine-topLine)*0.05F;

            //重计算比例
            proportionHeight=1/((rightLine-leftLine)/(bottomLine-topLine))*(rightLine-leftLine);
            proportionWidth=((rightLine-leftLine)/(bottomLine-topLine))*(bottomLine-topLine);

            //重绘
            postInvalidate();
            return true;
        }
        else if (focus.equals(RIGHT_TOP)){
            shadowPaint.setColor(Color.parseColor("#50DCDCDC"));
                move_where=2;

            //重绘制中线选区
            mid_line_Wlong=(rightLine-leftLine)*0.05F;
            mid_line_Hlong=(bottomLine-topLine)*0.05F;

            //移动边线
            rightLine=touchX;
            topLine=bottomLine-(((rightLine-leftLine)/proportionWidth)*proportionHeight);//约束比例

            //限制最小矩形 宽
            if (rightLine-leftLine<100){
                rightLine=leftLine+100;
                topLine=bottomLine-(((rightLine-leftLine)/proportionWidth)*proportionHeight);
                //重绘
                postInvalidate();
                return true;
            }

            //限制最小矩形 高
            if (bottomLine-topLine<100){
                topLine=bottomLine-100;
                rightLine=((bottomLine-topLine)/proportionHeight)*proportionWidth+leftLine;
                //重绘
                postInvalidate();
                return true;
            }

            //防止超出边界
            if (rightLine>widthSize){
                rightLine=widthSize;
                bottomLine=topLine+(((rightLine-leftLine)/proportionWidth)*proportionHeight);
            }

            //防止超出边界
            if (bottomLine>heightSize){
                bottomLine=heightSize;
                rightLine=leftLine+(((bottomLine-topLine)/proportionHeight)*proportionWidth);
            }
            //重绘
            postInvalidate();
            return true;
        }
        else if (focus.equals(LEFT_BOTTOM)){
            shadowPaint.setColor(Color.parseColor("#50DCDCDC"));
                move_where=2;

            //重绘制中线选区
            mid_line_Wlong=(rightLine-leftLine)*0.05F;
            mid_line_Hlong=(bottomLine-topLine)*0.05F;

            //移动边线
            leftLine=touchX;
            bottomLine=topLine+(((rightLine-leftLine)/proportionWidth)*proportionHeight);//约束比例

            //限制最小矩形 宽
            if (rightLine-leftLine<100){
                leftLine=rightLine-100;
                bottomLine=topLine+(((rightLine-leftLine)/proportionWidth)*proportionHeight);
                //重绘
                postInvalidate();
                return true;
            }

            //限制最小矩形 高
            if (bottomLine-topLine<100){
                bottomLine=topLine+100;
                leftLine=rightLine-(((bottomLine-topLine)/proportionHeight)*proportionWidth);
                //重绘
                postInvalidate();
                return true;
            }

            //防止超出边界
            if (leftLine<0){
                leftLine=0;
                bottomLine=topLine+(((rightLine-leftLine)/proportionWidth)*proportionHeight);
            }

            //防止超出边界
            if (bottomLine>heightSize){
                bottomLine=heightSize;
                leftLine=rightLine-(((bottomLine-topLine)/proportionHeight)*proportionWidth);
            }
            //重绘
            postInvalidate();
            return true;
        }
        else if (focus.equals(MID_BOTTOM)){
                move_where=0;
            shadowPaint.setColor(Color.parseColor("#50DCDCDC"));
            //移动边线
            bottomLine=touchY;

            //限制最小矩形 高
            if (bottomLine-topLine<100){
                bottomLine=topLine+100;
                //重绘
                postInvalidate();
                return true;
            }

            //防止超出边界
            if (bottomLine>heightSize){
                bottomLine=heightSize;
            }
            //重绘制中线选区
            mid_line_Wlong=(rightLine-leftLine)*0.05F;
            mid_line_Hlong=(bottomLine-topLine)*0.05F;

            //重计算比例
            proportionHeight=1/((rightLine-leftLine)/(bottomLine-topLine))*(rightLine-leftLine);
            proportionWidth=((rightLine-leftLine)/(bottomLine-topLine))*(bottomLine-topLine);

            //重绘
            postInvalidate();
            return true;
        }
        else if(focus.equals(MID_RIGHT)){
            shadowPaint.setColor(Color.parseColor("#50DCDCDC"));
            //移动边线
            rightLine=touchX;
                move_where=2;
            //限制最小矩形 宽
            if (rightLine-leftLine<100){
                rightLine=rightLine+100;
                topLine=bottomLine-(((rightLine-leftLine)/proportionWidth)*proportionHeight);
                //重绘
                postInvalidate();
                return true;
            }
            //防止超出边界
            if (rightLine>widthSize){
                rightLine=widthSize;
            }

            //重绘制中线选区
            mid_line_Wlong=(rightLine-leftLine)*0.05F;
            mid_line_Hlong=(bottomLine-topLine)*0.05F;

            //重计算比例
            proportionHeight=1/((rightLine-leftLine)/(bottomLine-topLine))*(rightLine-leftLine);
            proportionWidth=((rightLine-leftLine)/(bottomLine-topLine))*(bottomLine-topLine);

            //重绘
            postInvalidate();
            return true;
        }
        }

        if (Clip_Mod.equals(MOD_PEN)){
            pen_y=touchY;
            pen_x=touchX;
            postInvalidate();
            return true;
        }
        return true;
    }

    //按下
    private float downX,downY,downLeftLine,downTopLine,downRightLine,downBottomLine;
    private boolean actionDown(float touchX, float touchY) {
        downX=touchX;
        downY=touchY;
        downLeftLine=leftLine;
        downTopLine=topLine;
        downRightLine=rightLine;
        downBottomLine=bottomLine;
        Log.d("fxHou","按下X="+touchX+"   touchY="+touchY);
        boolean condition1=touchX>leftLine-40 && touchX<leftLine+40;
        boolean condition2=touchY>topLine-40 && touchY<topLine+40;
        if (condition1 && condition2){
            Log.d("fxHou","左上获得焦点");
            focus=LEFT_TOP;//左上获得焦点
            return true;
        }
        //右下
        boolean condition3=touchX>rightLine-40 && touchX<rightLine+40;
        boolean condition4=touchY>bottomLine-40 && touchY<bottomLine+40;
        if (condition3 && condition4){
            Log.d("fxHou","右下获得焦点");
            focus=RIGHT_BOTTOM;//右下获得焦点
            return true;
        }
        //右上
        condition3=touchX>rightLine-40 && touchX<rightLine+40;
        condition4=touchY>topLine-40 && touchY<topLine+40;
        if (condition3 && condition4){
            Log.d("fxHou","右上获得焦点");
            focus=RIGHT_TOP;//右上获得焦点
            return true;
        }

        //上下中接触点
        boolean condition7=touchX>mindtopLine-40 && touchX<mindtopLine+40;
        boolean condition8=touchY>topLine-40 && touchY<topLine+40;
        if (condition7 && condition8){
            Log.d("fxHou","上中点获得焦点");
            focus=MID_TOP;//上中获得焦点
            return true;
        }
        condition7=touchX>mindtopLine-40 && touchX<mindtopLine+40;
        condition8=touchY>bottomLine-40 && touchY<bottomLine+40;
        if (condition7 && condition8){
            Log.d("fxHou","下中点获得焦点");
            focus=MID_BOTTOM;//下中获得焦点
            return true;
        }

        //左中右中接触点
        boolean condition9=touchX>leftLine-40 && touchX<leftLine+40;
        boolean condition10=touchY>mindleftLine-40 && touchY<mindleftLine+40;
        if (condition9 && condition10){
            Log.d("fxHou","左中点获得焦点");
            focus=MID_LEFT;//左中获得焦点
            return true;
        }
        condition9=touchX>rightLine-40 && touchX<rightLine+40;
        condition10=touchY>mindleftLine-40 && touchY<mindleftLine+40;
        if (condition9 && condition10){
            Log.d("fxHou","右中点获得焦点");
            focus=MID_RIGHT;//右中获得焦点
            return true;
        }

        //上右接触点
        boolean condition11=touchX>rightLine-40 && touchX<rightLine+40;
        boolean condition12=touchY>topLine-40 && touchY<topLine+40;
        if (condition11 && condition12){
            Log.d("fxHou","上右点获得焦点");
            focus=RIGHT_TOP;//上右获得焦点
            return true;
        }
        //下左
        boolean condition13=touchX>leftLine-40 && touchX<leftLine+40;
        boolean condition14=touchY>bottomLine-40 && touchY<bottomLine+40;
        if (condition13 && condition14){
            Log.d("fxHou","下左点获得焦点");
            focus=LEFT_BOTTOM;//下左获得焦点
            return true;
        }

        boolean condition5=touchX>leftLine && touchX<rightLine;
        boolean condition6=touchY>topLine && touchY<bottomLine;
        if (condition5 && condition6){
            Log.d("fxHou","整体获得焦点");
            focus=BODY;//整体获得焦点
            return true;
        }
        return true;
    }

    /**
     * 设置要裁剪的位图
     * @param bitmap 要裁剪的位图
     * @param proportionWidth  比例：宽  如裁图比例3:4，此处传3
     * @param proportionHeight 比例：高  如裁图比例3:4，此处传4
     */
    public void setBitmap(Bitmap bitmap,int proportionWidth,int proportionHeight){
        this.bitmap=bitmap;
        mBitmap=bitmap;
        bitmapWidth=bitmap.getWidth();
        bimapHeight=bitmap.getHeight();
        this.proportionWidth=proportionWidth;
        this.proportionHeight=proportionHeight;
        initTag=true;
        postInvalidate();
    }

    /**
     * 获取裁剪后的位图
     * @param context
     * @param minPixelWidth 限制最小宽度（像素）
     * @param minPixelHeight 限制最小高度（像素）
     * @return 裁切后的位图
     */
    public Bitmap getBitmap(Context context,int minPixelWidth,int minPixelHeight){
        if (bitmap==null)return null;
        int startX= (int) (leftLine/scaleStep);
        int startY= (int) (topLine/scaleStep);
        int cutWidth=(int) ((rightLine/scaleStep)-(leftLine/scaleStep));
        int cutHeight=(int) (bottomLine/scaleStep-topLine/scaleStep);

        Bitmap newBitmap=Bitmap.createBitmap(mBitmap,startX,startY,cutWidth,cutHeight,null,false);

        if (newBitmap.getWidth()<minPixelWidth || newBitmap.getHeight()<minPixelHeight){
            Toast.makeText(context, "图片太模糊了", Toast.LENGTH_SHORT).show();
            return null;
        }

        return newBitmap;
    }
    static Text my_text=null;
    public void try_getText(Bitmap text_bitmap){

        TextRecognizer recognizer= TextRecognition.getClient(new ChineseTextRecognizerOptions.Builder().build());

        recognizer.process(InputImage.fromBitmap(text_bitmap,90))
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text visionText) {
                        text_p=Recognizer_Success;
                        my_text=visionText;
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                                text_p=Recognizer_Failure;
                            }
                        }
                );
        text_p=Recognizer_ing;
    }

public Text getText(){

    try_getText(mBitmap);

    int i=0;
    while (text_p==Recognizer_ing){
        i++;
        if(i>5000){
            text_p=Recognizer_null;
            return null;
        };

    }

    if (text_p==Recognizer_Success&&my_text!=null){
        text_p=Recognizer_null;
        return my_text;
    }

    if (text_p==Recognizer_Failure){
        text_p=Recognizer_null;
        return null;
    }

    text_p=Recognizer_null;
    return my_text;

}

}
