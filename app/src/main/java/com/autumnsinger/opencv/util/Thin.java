package com.autumnsinger.opencv.util;

import org.opencv.core.Mat;

/**
 * Created by CarlZhang on 2016/6/18.
 */
public class Thin {
    static {
        System.loadLibrary("Thin");
    }
    public native void  thinning(Mat src, Mat dst);
}
