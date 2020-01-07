package com.wutong.trhdoodleview;

import android.graphics.RectF;

/**
 * 判断正方形重叠流程：  横竖相交 不成立则 判断是否有一个小正方形的顶点在另一个大正方形内
 * 注意你获取正方形的四个坐标不一定对啊朋友  所以一定要用线段长度来判断  来判断是否在正方形内
 * Created by jiuman on 2019/12/31.
 */

public class AreaData {
    public RectF maxRectF;


    /**
     * \
     *
     * @param maxLeft
     * @param maxTop
     * @param maxRight
     * @param maxBottom
     */
    public AreaData(float maxLeft, float maxTop, float maxRight, float maxBottom) {
        maxRectF = new RectF(maxLeft, maxTop, maxRight, maxBottom);
    }

    /**
     * 对比正方形 区域是否重叠   线段相交拯救世界
     *
     * @param areaA
     * @param areaB
     * @return
     */
    public static boolean isIntersect(AreaData areaA, AreaData areaB) {


        int countA = 0;
        countA += isBetween(areaA.maxRectF.left, areaA.maxRectF.right, areaB.maxRectF.right) ? 1 : 0;
        countA += isBetween(areaA.maxRectF.left, areaA.maxRectF.right, areaB.maxRectF.left) ? 2 : 0;
        countA += isBetween(areaA.maxRectF.top, areaA.maxRectF.bottom, areaB.maxRectF.top) ? 4 : 0;
        countA += isBetween(areaA.maxRectF.top, areaA.maxRectF.bottom, areaB.maxRectF.bottom) ? 8 : 0;

        AreaData areaT;
        areaT = areaA;
        areaA = areaB;
        areaB = areaT;
        int countB = 0;
        countB += isBetween(areaA.maxRectF.left, areaA.maxRectF.right, areaB.maxRectF.right) ? 1 : 0;
        countB += isBetween(areaA.maxRectF.left, areaA.maxRectF.right, areaB.maxRectF.left) ? 2 : 0;
        countB += isBetween(areaA.maxRectF.top, areaA.maxRectF.bottom, areaB.maxRectF.top) ? 4 : 0;
        countB += isBetween(areaA.maxRectF.top, areaA.maxRectF.bottom, areaB.maxRectF.bottom) ? 8 : 0;

        if ((countA & 1) == 1) {
            if ((countB & 8) == 8 || (countB & 4) == 4)
                return true;
        }
        if ((countA & 2) == 2) {
            if ((countB & 8) == 8 || (countB & 4) == 4)
                return true;
        }

        if ((countA & 4) == 4) {
            if ((countB & 1) == 1 || (countB & 2) == 2)
                return true;
        }
        if ((countA & 8) == 8) {
            if ((countB & 1) == 1 || (countB & 2) == 2)
                return true;
        }

/**
 *判断是否在内部
 */
        if (Math.abs(areaA.maxRectF.right - areaA.maxRectF.left) > Math.abs(areaB.maxRectF.right - areaB.maxRectF.left)) {
            if (isBetween(areaA.maxRectF.left, areaA.maxRectF.right, areaB.maxRectF.right)) {
                if (isBetween(areaA.maxRectF.top, areaA.maxRectF.bottom, areaB.maxRectF.top))
                    return true;
            }
        } else {
            if (isBetween(areaB.maxRectF.left, areaB.maxRectF.right, areaA.maxRectF.right)) {
                if (isBetween(areaB.maxRectF.top, areaB.maxRectF.bottom, areaA.maxRectF.top))
                    return true;
            }
        }

        return false;

    }

    private static boolean isInside(float leftA, float rightA, float rightB) {
        float temp = leftA;
        leftA = Math.min(temp, rightA);
        rightA = Math.max(temp, rightA);
        if (leftA < rightB && rightA > rightB)
            return true;
        return false;
    }

    /**
     * 判断C点是否在AB之间
     *
     * @param pointA   3
     * @param pointB   10
     * @param pointC  5
     * @return
     */
    public static boolean isBetween(float pointA, float pointB, float pointC) {

        if (Math.abs(pointA - pointB) >= Math.abs(pointA - pointC) && (Math.abs(pointA - pointB) >= Math.abs(pointB - pointC)))
            return true;

        return false;
    }


}
