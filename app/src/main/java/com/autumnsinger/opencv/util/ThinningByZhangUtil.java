package com.autumnsinger.opencv.util;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

/**
 * Created by CarlZhang on 2016/6/18.
 * 参考https://github.com/bsdnoobz/zhang-suen-thinning/blob/master/thinning.cpp#L1
 * 论文A fast parallel algorithm for thinning digital patterns” by T.Y. Zhang and C.Y. Suen.以及Parallel thinning with two sub-iteration algorithms” by Zicheng Guo and Richard Hall.。
 *
 */
public class ThinningByZhangUtil {
    /**
     * Perform one thinning iteration.
     * Normally you wouldn't call this function directly from your code.
     *
     * Parameters:
     * 		im    Binary image with range = [0,1]
     * 		iter  0=even, 1=odd
     */
   public static void thinningIteration(Mat img, int iter)
    {
//        Assert(img.channels() == 1) ;
//        Assert(img.depth() != sizeof(uchar));
//        Assert(img.rows() > 3 && img.cols() > 3);

       Mat marker = Mat.zeros(img.size(), CvType.CV_8UC1);

        double nw, no, ne;    // north (pAbove)
        double we, me, ea;
        double sw, so, se;    // south (pBelow)

        //下面的操作前提是，此图是单通道的图
        for (int y = 1; y < img.rows()-1; ++y) {
            for (int x = 1; x < img.cols()-1; ++x) {
                // shift col pointers left by one (scan left to right)
                nw = img.get(y-1, x-1)[0];
                no = img.get(y-1, x)[0];
                ne = img.get(y-1, x+1)[0];
                we = img.get(y, x-1)[0];
                me = img.get(y, x)[0];
                ea = img.get(y, x+1)[0];
                sw = img.get(y+1, x-1)[0];
                so = img.get(y+1, x)[0];
                se = img.get(y+1, x+1)[0];

                double A  =  bool2Int(no == 0 && ne == 1) + bool2Int(ne == 0 && ea == 1) +
                        bool2Int (ea == 0 && se == 1) + bool2Int(se == 0 && so == 1) +
                        bool2Int (so == 0 && sw == 1) + bool2Int(sw == 0 && we == 1) +
                        bool2Int(we == 0 && nw == 1) + bool2Int(nw == 0 && no == 1);
                double B  = no + ne + ea + se + so + sw + we + nw;
                double m1 = iter == 0 ? (no * ea * so) : (no * ea * we);
                double m2 = iter == 0 ? (ea * so * we) : (no * so * we);

                if (A == 1 && (B >= 2 && B <= 6) && m1 == 0 && m2 == 0){
                    double[] d = {0};
                    marker.put(y, x, d);
                }
            }
        }

        img = marker;
    }

    private static int bool2Int(boolean b){
        if(b == true){
            return 1;
        }else {
            return 0;
        }
    }

    /**
     * Function for thinning the given binary image
     *
     * Parameters:
     * 		src  The source image, binary with range = [0,255]
     * 		dst  The destination image
     */
    public static void thinning(Mat src, Mat dst)
    {
        dst = src.clone();
//        Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2GRAY);        // convert to binary image

//        Imgproc.threshold(dst, dst, 255/2, 255, 0);

        for(int y = 0; y < dst.rows(); y++){
            for(int x = 0; x < dst.cols(); x++ ){
                double[] v = {dst.get(y, x)[0] / 255};
                dst.put(y, x, v);
            }
        }

        Mat prev = Mat.zeros(dst.size(), CvType.CV_8UC1);
        Mat diff = new Mat();

        do {
            thinningIteration(dst, 0);
            thinningIteration(dst, 1);
            Core.absdiff(dst, prev, diff);
            dst.copyTo(prev);
        }
        while (Core.countNonZero(diff) > 0);

        //dst *= 255;

        for(int y = 0; y < dst.rows(); y++){
            for(int x = 0; x < dst.cols(); x++ ){
                double[] v = {dst.get(y, x)[0] * 255};
                dst.put(y, x, v);
            }
        }
    }
}
