package com.example.customviews;

import org.jetbrains.annotations.NotNull;

public class MathUtil {

    /**
     * 判断当前点到圆心的点是否小于半径
     * 如果是，则在圆内
     * 否在在圆外
     */
    public static boolean checkInRound(float sx, float sy, float r, float x, float y) {
        return Math.sqrt((sx - x) * (sx - x) + (sy - y) * (sy - y)) < r;
    }

    public static double distance(double startX, double startY, double endX, double endY) {
        return Math.sqrt((endX-startX)*(endX-startX) + (endY-startY)*(endY-startY));
    }
}
