package com.wutong.trhdoodleview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * 懒得改用手势识别器了 我直接判断点击事件的时间 来设置好了
 * Created by jiuman on 2019/12/31.
 */

public class DoodleView extends RelativeLayout {
    private static final String TAG = "trh" + "DoodleView";

    int statue;

    public static final int Normal=0;
    public static final int Edite=1;
    public static final int Select=2;

    List<DrawDate> drawDateList = new ArrayList<>();

    /**
     * 用于区分是单点还是区域选择
     */
    long downTime;
    long timeLimit = 100;
    // 2g个StrokeWidth用于 边框的大小设定
    Paint currentPaint;
    float currentPaintStrokeWidth = 10f;

    Paint areaPaint;

    Paint framePaint;
    float framePaintStrokeWidth = 2f;

    boolean isEidtMode = false;


    AreaData areaData = new AreaData(0, 0, 0, 0);

    public DoodleView(@NonNull Context context) {
        this(context, null);
    }

    public DoodleView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public DoodleView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        areaPaint = new Paint();
        areaPaint.setColor(0x70FF0000);
        areaPaint.setStyle(Paint.Style.FILL);

        framePaint = new Paint();
        framePaint.setStyle(Paint.Style.STROKE);
        framePaint.setStrokeWidth(framePaintStrokeWidth);
        framePaint.setColor(Color.BLUE);


        currentPaint = new Paint();
        currentPaint.setStyle(Paint.Style.STROKE);
        currentPaint.setStrokeWidth(currentPaintStrokeWidth);
        currentPaint.setColor(Color.RED);
    }


    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        Log.i(TAG, "onDrawForeground: ");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.i(TAG, "onDraw: ");

        for (DrawDate date : drawDateList) {
            canvas.drawPath(date.path, currentPaint);
            if (date.isSelected) {
                canvas.drawRect(date.areaData.maxRectF, framePaint);
            }
        }

        if (isEidtMode && (System.currentTimeMillis() - downTime > timeLimit)) {
            canvas.drawRect(areaData.maxRectF, areaPaint);
        }

    }

    /**
     * 单纯删除 不刷新的哟
     *
     * @param index
     */
    public void delete(int index) {
        if (drawDateList.size() < index + 1)
            return;
        drawDateList.remove(index);
    }

    public void deleteSelected() {
        for (int i = 0; i < drawDateList.size(); i++) {
            if (drawDateList.get(i).isSelected) {
                delete(i);
                i--;
            }
        }
        invalidate();


    }

    public void upDraw() {
        invalidate();
    }

    public void setEidtMode(boolean eidtMode) {
        isEidtMode = eidtMode;
        if (!isEidtMode) {
            for (DrawDate a : drawDateList) {
                if (AreaData.isIntersect(areaData, a.areaData)) {
                    a.isSelected = false;
                }
            }
        }
    }


    //    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        return false;
//    }
    private boolean isIntercept = true;

    public void changeIntercept() {
        isIntercept = !isIntercept;
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //

        Log.i(TAG, "onTouchEvent: 进入");
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                Log.i(TAG, "onTouchEvent: 按下");
                if (isEidtMode) {
                    RectF areaRect = areaData.maxRectF;
                    //这里的top 和left并是不真正意义上的  而是初始值
                    areaRect.top = event.getY() - currentPaintStrokeWidth - framePaintStrokeWidth;
                    areaRect.left = event.getX() - currentPaintStrokeWidth - framePaintStrokeWidth;
                    break;
                }
                createNewDrawDate(event);
                break;
            case MotionEvent.ACTION_MOVE:
                Log.i(TAG, "onTouchEvent: 移动");
                if (isEidtMode) {
                    RectF areaRect = areaData.maxRectF;
                    areaRect.bottom = event.getY() + currentPaintStrokeWidth + framePaintStrokeWidth;
                    areaRect.right = event.getX() + currentPaintStrokeWidth + framePaintStrokeWidth;


                    //区域选择
                    if (System.currentTimeMillis() - downTime > timeLimit) {
                        for (DrawDate a : drawDateList) {
                            if (AreaData.isIntersect(areaData, a.areaData)) {
                                a.isSelected = true;
                            } else {
                                a.isSelected = false;
                            }
                        }

                    } else {
                        boolean flag = false; //单选的
                        for (int i = drawDateList.size() - 1; i >= 0; i--) {
                            DrawDate a = drawDateList.get(i);
                            if (AreaData.isIntersect(areaData, a.areaData)) {
                                if (flag)
                                    a.isSelected = false;
                                else
                                    flag = a.isSelected = true;
                            } else {
                                a.isSelected = false;
                            }
                        }
                    }

                    break;
                }

                updatePath(event);
                getMaxFrame(event);

                break;
            case MotionEvent.ACTION_UP:
                Log.i(TAG, "onTouchEvent: 放开");
                if (isEidtMode) {
                    areaData.maxRectF = new RectF(0, 0, 0, 0);
//                    for (DrawDate a : drawDateList) {
//                        a.isSelected = false;
//                    }
                    break;
                }
                updatePath(event);
                getMaxFrame(event);
                break;
        }

        invalidate();
        if (isIntercept) {
            return true;
        }
        return super.onTouchEvent(event);
    }


    private void getMaxFrame(MotionEvent event) {
        RectF maxRectF = drawDateList.get(drawDateList.size() - 1).areaData.maxRectF;
        maxRectF.left = Math.min(maxRectF.left, event.getX());
        maxRectF.right = Math.max(maxRectF.right, event.getX());
        maxRectF.top = Math.min(maxRectF.top, event.getY());
        maxRectF.bottom = Math.max(maxRectF.bottom, event.getY());

    }

    private void updatePath(MotionEvent event) {
        Path path = drawDateList.get(drawDateList.size() - 1).path;
        path.lineTo(event.getX(), event.getY());
    }

    private void createNewDrawDate(MotionEvent event) {
        DrawDate drawDate;
        drawDate = new DrawDate();
        drawDateList.add(drawDate);

        RectF rectF = new RectF();
        drawDate.areaData.maxRectF = rectF;
        rectF.top = event.getY();
        rectF.left = event.getX();
        rectF.bottom = event.getY();
        rectF.right = event.getX();
        drawDate.path = new Path();
        drawDate.path.moveTo(rectF.left, rectF.top);
    }
}
