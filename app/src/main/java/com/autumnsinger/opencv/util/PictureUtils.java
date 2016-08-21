package com.autumnsinger.opencv.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.autumnsinger.opencv.activity.MainActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by CarlZhang on 2016/6/6.
 */
public class PictureUtils {

    /**
     * 灰度化
     * @param picture
     * @param picture_result
     */
    public static void greyPic(ImageView picture,ImageView picture_result ){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat srcmat = new Mat();
        Utils.bitmapToMat(bitmap, srcmat);
        Mat grey = new Mat();
        Imgproc.cvtColor(srcmat, grey, Imgproc.COLOR_RGB2GRAY);

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(grey, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     * 二值化
     * @param picture
     * @param picture_result
     * @param minValue
     * @param maxValue
     */
    public static void binaryPic(ImageView picture, ImageView picture_result, SeekBar minValue,SeekBar maxValue){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmap, srcMat);
        Mat greyMat = new Mat();
        Imgproc.cvtColor(srcMat, greyMat, Imgproc.COLOR_RGB2GRAY);

        Mat resMat = new Mat();
        Imgproc.threshold(greyMat, resMat, minValue.getProgress(), maxValue.getProgress(), MainActivity.THRESHOLD_TYPE);
        Bitmap binaryImg = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(resMat, binaryImg);
        picture_result.setImageBitmap(binaryImg);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     * Otsu二值化
     * @param picture
     * @param picture_result
     */
    public static void binaryPicByOtsu(ImageView picture, ImageView picture_result){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat srcMat = new Mat();
        Utils.bitmapToMat(bitmap, srcMat);
        Mat greyMat = new Mat();
        Imgproc.cvtColor(srcMat, greyMat, Imgproc.COLOR_RGB2GRAY);

        Mat resMat = new Mat();
        Imgproc.threshold(greyMat, resMat, 1, 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        Bitmap binaryImg = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);

        Utils.matToBitmap(resMat, binaryImg);
        picture_result.setImageBitmap(binaryImg);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     * 图像加,减,乘,除操作
     * 具体原理可查看http://docs.opencv.org/2.4/doc/tutorials/core/adding_images/adding_images.html
     * @param picture
     * @param picture2
     * @param picture_result
     * @param opration 0加操作；1减操作；2乘法操作；3除法操作
     */
    public static void operateTwoPic(ImageView picture, ImageView picture2, ImageView picture_result, int opration ){
        picture.setDrawingCacheEnabled(true);
        picture2.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        picture2.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        Bitmap bitmap2 = ((BitmapDrawable) picture2.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat srcmat = new Mat();
        Utils.bitmapToMat(bitmap, srcmat);

        Mat srcmat2 = new Mat();
        Utils.bitmapToMat(bitmap2, srcmat2);

        Mat resultMat = srcmat.clone();
        try{
            switch (opration){
                case 0:
                    Core.addWeighted(srcmat, 0.5, srcmat2, 0.5, 0.0, resultMat);
                    break;
                case 1:
                    Core.subtract(srcmat, srcmat2, resultMat);
                    break;
                case 2:
                    Core.multiply(srcmat, srcmat2, resultMat);
                    break;
                case 3:
                    Core.divide(srcmat, srcmat2, resultMat);
                    break;
            }
        }catch (Exception e){
            Util.t(picture.getContext(), "请选择两张尺寸相同的图片");
        }


        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(resultMat, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
        picture2.setDrawingCacheEnabled(false);
    }

    /**
     * 形态学距离转换
     * 参考资料http://docs.opencv.org/3.0-rc1/d2/dbd/tutorial_distance_transform.html
     * @param picture
     * @param picture_result
     */
    public static void distanceTransform(ImageView picture,ImageView picture_result ){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);


        // Create binary image from source image
        Mat bw = new Mat();
        Imgproc.cvtColor(src, bw, Imgproc.COLOR_BGR2GRAY);
        Mat convertedTo8UC1 = new Mat();
        bw.convertTo(convertedTo8UC1, CvType.CV_8UC1);
        Imgproc.threshold(convertedTo8UC1, convertedTo8UC1, 40, 255,Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);

        // Perform the distance transform algorithm
        Mat dist = new Mat();
        Imgproc.distanceTransform(convertedTo8UC1, dist, Imgproc.CV_DIST_L2, 3);
        // Normalize the distance image for range = {0.0, 1.0}
        // so we can visualize and threshold it
        dist.convertTo(dist, CvType.CV_8UC1);
        Core.normalize(dist, dist, 0, 255, Core.NORM_MINMAX);
        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dist, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }


    /**
     * 图片平移
     * 参考资料http://docs.opencv.org/2.4/doc/tutorials/imgproc/imgtrans/warp_affine/warp_affine.html
     * @param picture
     * @param picture_result
     * @param x_distanceBar from 1 to 100
     * @param y_distanceBar from 1 to 100
     */
    public static void translate(ImageView picture,ImageView picture_result, SeekBar x_distanceBar, SeekBar y_distanceBar){
        double x_distance = 0.0 ;//from -1.0 to 1.0 x坐标平移比例
        double y_distance = 0.0;//from -1.0 to 1.0 y坐标平移比例

        x_distance = (x_distanceBar.getProgress() - 50)/50.0;
        y_distance = (y_distanceBar.getProgress() - 50)/50.0;

        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Mat warp_dst;
        Point [] srcTri = new Point[3];
        Point [] dstTri = new Point[3];
        Mat warpMat;

        warp_dst = src.clone();

        /// Set your 3 points to calculate the  Affine Transform
        srcTri[0] = new Point(0,0);
        srcTri[1] = new Point(src.cols() -1 , 0 );
        srcTri[2] = new Point(0, src.rows()- 1);

        double x = src.cols() * x_distance;
        double y = src.rows() * y_distance;

        dstTri[0] = new Point( x, y);
        dstTri[1] = new Point( src.cols() -1 + x, y);
        dstTri[2] = new Point(0 + x, src.rows() - 1 + y);

        MatOfPoint2f srcMO2 = new MatOfPoint2f(srcTri);
        MatOfPoint2f desMO2 = new MatOfPoint2f(dstTri);
        warpMat = Imgproc.getAffineTransform(srcMO2, desMO2);

        /// Get the Affine Transform
        Imgproc.warpAffine(src, warp_dst, warpMat, warp_dst.size());

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(warp_dst, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }


    /**
     * 缩放
     * @param picture
     * @param picture_result
     * @param scalingBar
     */
    public static void scaling(ImageView picture,ImageView picture_result, SeekBar scalingBar){
        double scalingRate = 0.0 ;//缩放比例

        scalingRate = (scalingBar.getProgress() - 50)/50.0;

        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Mat warp_dst;
        Point [] srcTri = new Point[3];
        Point [] dstTri = new Point[3];
        Mat warpMat;

        warp_dst = src.clone();

        /// Set your 3 points to calculate the  Affine Transform
        srcTri[0] = new Point(0,0);
        srcTri[1] = new Point(src.cols() -1 , 0 );
        srcTri[2] = new Point(0, src.rows()- 1);

        double x = src.cols() * scalingRate;
        double y = src.cols() * scalingRate;

        dstTri[0] = new Point( x, y);
        dstTri[1] = new Point( src.cols() -1 - x, y);
        dstTri[2] = new Point( x, src.rows() - 1 - y);

        MatOfPoint2f srcMO2 = new MatOfPoint2f(srcTri);
        MatOfPoint2f desMO2 = new MatOfPoint2f(dstTri);
        warpMat = Imgproc.getAffineTransform(srcMO2, desMO2);

        /// Get the Affine Transform
        Imgproc.warpAffine(src, warp_dst, warpMat, warp_dst.size());

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(warp_dst, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     *旋转
     * @param picture
     * @param picture_result
     * @param rotatingBar from 1 to 360
     */
   public static void rotating(ImageView picture,ImageView picture_result, SeekBar rotatingBar){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Point center = new Point(src.cols()/2,src.rows()/2);
        double angle = rotatingBar.getProgress();
        double scale = 1.0;

        Mat mapMatrix = Imgproc.getRotationMatrix2D(center, angle, scale);
        Mat dstMat = new Mat(src.size(), src.type());
        Imgproc.warpAffine(src, dstMat, mapMatrix, dstMat.size(), Imgproc.INTER_LINEAR);

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dstMat, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     * Roberts边缘检测
     * @param picture
     * @param picture_result
     */
    public static void edgeDetectationRoberts(ImageView picture, ImageView picture_result){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Mat dstgrey = new Mat();
        Imgproc.cvtColor(src, dstgrey, Imgproc.COLOR_RGB2GRAY);

        Mat dst = dstgrey.clone();

        double[] pixel = new double[4];
        //Roberts算法核心代码
        for(int x = 0 ; x < dstgrey.rows()-1; x ++){
            for(int y = 0; y < dstgrey.cols() -1; y ++){
                pixel[0] = (dstgrey.get(x, y))[0];
                pixel[1] = (dstgrey.get(x + 1, y))[0];
                pixel[2] = (dstgrey.get(x, y + 1))[0];
                pixel[3] = (dstgrey.get(x + 1, y + 1))[0];
                double tmp = Math.sqrt( (pixel[0] - pixel[3]) * (pixel[0] - pixel[3]) + (pixel[1] - pixel[2]) * (pixel[1] - pixel[2]));

                dst.put(x, y, new double[]{tmp});
            }
        }

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst , tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     * Prewitt边缘检测
     * @param picture
     * @param picture_result
     */
    public static void edgeDetectationPrewitt(ImageView picture, ImageView picture_result){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Mat dst = new Mat();
        Imgproc.cvtColor(src, dst, Imgproc.COLOR_RGB2GRAY);

        Mat kernel_x = new Mat(3,3, CvType.CV_32F){
                        {
                put(0,0,1);
                put(0,1,1);
                put(0,2,1);

                put(1,0,0);
                put(1,1,0);
                put(1,2,0);

                put(2,0,-1);
                put(2,1,-1);
                put(2,2,-1);
            }
        };
        Mat kernel_y = new MatOfDouble(3,3, CvType.CV_32F){
            {
                put(0,0,-1);
                put(0,1,0);
                put(0,2,1);

                put(1,0,-1);
                put(1,1,0);
                put(1,2,1);

                put(2,0,-1);
                put(2,1,0);
                put(2,2,1);
            }
        };

        Mat  dstMat_x = new Mat();
        Mat  dstMat_y = new Mat();

        Imgproc.filter2D(dst, dstMat_x, -1 , kernel_x);
        Imgproc.filter2D(dst, dstMat_y, -1, kernel_y);

        Core.convertScaleAbs(dstMat_x, dstMat_x);
        Core.convertScaleAbs(dstMat_y, dstMat_y);
        Core.addWeighted(dstMat_x, 0.5, dstMat_y, 0.5, 0, dst);

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst , tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     * Sobel边缘检测
     * 参考http://docs.opencv.org/2.4/doc/tutorials/imgproc/imgtrans/sobel_derivatives/sobel_derivatives.html
     * @param picture
     * @param picture_result
     */
    public static void edgeDetectationSobel(ImageView picture, ImageView picture_result){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Mat  src_gray = new Mat();
        Mat grad = new Mat();
        int scale = 1;
        int delta = 0;
        int ddepth = CvType.CV_16S;

        Imgproc.GaussianBlur( src, src, new Size(3,3), 0, 0, Core.BORDER_DEFAULT );
        /// Convert it to gray
        Imgproc.cvtColor( src, src_gray, Imgproc.COLOR_RGB2GRAY );

        /// Generate grad_x and grad_y
        Mat grad_x = new Mat(), grad_y = new Mat();
        Mat abs_grad_x = new Mat(), abs_grad_y = new Mat();

        /// Gradient X
        //Scharr( src_gray, grad_x, ddepth, 1, 0, scale, delta, BORDER_DEFAULT );
        Imgproc.Sobel( src_gray, grad_x, ddepth, 1, 0, 3, scale, delta, Core.BORDER_DEFAULT );
        Core.convertScaleAbs( grad_x, abs_grad_x );

        /// Gradient Y
        //Scharr( src_gray, grad_y, ddepth, 0, 1, scale, delta, BORDER_DEFAULT );
        Imgproc.Sobel( src_gray, grad_y, ddepth, 0, 1, 3, scale, delta, Core.BORDER_DEFAULT );
        Core.convertScaleAbs( grad_y, abs_grad_y );

        /// Total Gradient (approximate)
        Core.addWeighted( abs_grad_x, 0.5, abs_grad_y, 0.5, 0, grad );


        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(grad , tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     * Canny边缘检测
     * @param picture
     * @param picture_result
     */
    public static void edgeDetectationdCanny(ImageView picture, ImageView picture_result){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);

        Mat dst = new Mat();

        /// Global variables
        Mat src_gray = new Mat();
        Mat detected_edges = new Mat();
        double lowThreshold = 60.0;
        int ratio = 3;

        /// Create a matrix of the same type and size as src (for dst)
        dst.create( src.size(), src.type() );
        /// Convert the image to grayscale
        Imgproc.cvtColor( src, src_gray, Imgproc.COLOR_RGB2GRAY );
        /// Reduce noise with a kernel 3x3
        Imgproc.blur( src_gray, detected_edges, new Size(3,3) );

        /// Canny detector
        Imgproc.Canny( detected_edges, detected_edges, lowThreshold, lowThreshold*ratio );

        /// Using Canny's output as a mask, we display our result
        src.copyTo( dst, detected_edges);

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }


    /**
     * 均值，中值，高斯滤镜
     * 参考http://www.opencv.org.cn/opencvdoc/2.3.2/html/doc/tutorials/imgproc/gausian_median_blur_bilateral_filter/gausian_median_blur_bilateral_filter.html
     * @param picture
     * @param picture_result
     * @param blurType 0均值，1中值，2高斯滤镜
     */
    public static void toBlur(ImageView picture, ImageView picture_result, int blurType, EditText sigma_x, EditText sigma_y){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        int MAX_KERNEL_LENGTH = 15;//31
        Mat dst = new Mat();

        if(0 == blurType){
            //0均值
            for ( int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2 ){
                Imgproc.blur(src, dst, new Size(i,i),new Point(-1,-1));
            }
        }else if(1 == blurType){
            //1中值
            for ( int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2 ){
                Imgproc.medianBlur(src, dst,i);
            }
        }else if(2 == blurType){

            String x = sigma_x.getText().toString().trim();
            String y = sigma_y.getText().toString().trim();
            //默认为0
            Float s_x = "".equals(x) ? 0 :Float.valueOf(sigma_x.getText().toString());
            Float s_y = "".equals(y) ? 0 :Float.valueOf(sigma_y.getText().toString());
            //2高斯滤镜
            for ( int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2 ){
                Imgproc.GaussianBlur(src, dst, new Size(i,i), s_x, s_y);
            }
        }

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     * 膨胀或腐蚀
     * 参考自http://www.opencv.org.cn/opencvdoc/2.3.2/html/doc/tutorials/imgproc/erosion_dilatation/erosion_dilatation.html
     * 和http://www.opencv.org.cn/opencvdoc/2.3.2/html/doc/tutorials/imgproc/opening_closing_hats/opening_closing_hats.html#morphology-2
     * @param picture
     * @param picture_result
     * @param operationType 0膨胀，1腐蚀，2开操作，3比操作，4形态学梯度
     */
    public static void morphologyOperate(ImageView picture, ImageView picture_result, int operationType, int eleType, SeekBar morphBar ){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat dst = new Mat();

        int elementType = 0;
        double morph_size = morphBar.getProgress();

        switch (eleType){
            case 0:elementType = Imgproc.MORPH_RECT;break;
            case 1:elementType = Imgproc.MORPH_CROSS;break;
            case 2:elementType = Imgproc.MORPH_ELLIPSE;break;
        }

        Mat element = Imgproc.getStructuringElement( elementType,
               new Size( 2*morph_size  + 1, 2*morph_size +1 ),
               new Point( morph_size , morph_size  ) );

        if( operationType == 0){
            //膨胀操作
            Imgproc.dilate( src, dst, element );
        }else if(operationType == 1){
            // 腐蚀操作
            Imgproc.erode( src, dst, element );
        }else{
            //开操作,闭操作，形态学梯度
            Imgproc.morphologyEx( src, dst, operationType, element );
        }

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }


    /**
     * 骨架
     * 参考http://felix.abecassis.me/2011/09/opencv-morphological-skeleton/
     * @param picture
     * @param picture_result
     */
    public static void skeleton(ImageView picture, ImageView picture_result){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(src, src, 1, 255,Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        Mat dst = src.clone();

        int K = 0;//腐蚀至消失的次数
        Mat element =  Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3,3));
        Mat res = null;//骨架操作的结果
        do{
                Mat  dst2 = new Mat();
                Imgproc.morphologyEx(dst, dst2,  Imgproc.MORPH_OPEN, element);
                Mat tmp = new Mat();
                Core.subtract(dst, dst2, tmp);
                if(res == null){
                    res = tmp;
                }else {
                    Core.add(tmp, res, res);
                }
                K++;
                Imgproc.erode(src, dst, element, new Point(-1, -1), K);
        }while (Core.countNonZero(dst) > 0);

        ConstantMorph.MY_MAT = res;//操作结果
        ConstantMorph.MY_COUNT = K;

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(res, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }


    /**
     * 骨架重建
     * @param picture
     * @param picture_result
     */
    public static void reconstruction(ImageView picture, ImageView picture_result){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Imgproc.cvtColor(src, src, Imgproc.COLOR_BGR2GRAY);
        Imgproc.threshold(src, src, 1, 255,Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        Mat dst = src.clone();

        int K =  ConstantMorph.MY_COUNT;//腐蚀至消失的次数
        Mat element =  Imgproc.getStructuringElement(Imgproc.MORPH_CROSS, new Size(3,3));
        Imgproc.dilate(src, dst, element, new Point(-1, -1), K);

        for(int x = 0; x < src.cols(); x ++){
            for(int y = 0; y < src.rows(); y++){
                double v = src.get(y, x)[0];
                if(v == 0){
                    dst.put(y, x, 0);
                }
            }
        }

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     * 计算直方图
     * 参考
     * http://www.opencv.org.cn/opencvdoc/2.3.2/html/doc/tutorials/imgproc/histograms/histogram_calculation/histogram_calculation.html#histogram-calculation
     * and
     * http://stackoverflow.com/questions/22464503/how-to-use-opencv-to-calculate-hsv-histogram-in-java-platform
     * @param picture
     * @param picture_result
     */
    public static void calcHist(ImageView picture, ImageView picture_result){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat dst = src.clone();

        /// 分割成3个单通道图像 ( R, G 和 B )
        List<Mat> rgb_planes = new Vector<Mat>();
        Core.split(dst, rgb_planes);

        /// 设定取值范围 ( R,G,B) )
        boolean accumulate = false;

        Mat r_hist = new Mat(), g_hist = new Mat(), b_hist = new Mat();

        MatOfInt channels = new MatOfInt(3);
        MatOfInt histSize = new MatOfInt(256);
        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        /// 计算直方图:
        Imgproc.calcHist( rgb_planes,  new MatOfInt(1), new Mat(), r_hist, histSize, ranges, accumulate );
        Imgproc.calcHist( rgb_planes,  new MatOfInt(2), new Mat(), g_hist, histSize, ranges, accumulate );
        Imgproc.calcHist( rgb_planes,  new MatOfInt(3), new Mat(), b_hist, histSize, ranges, accumulate );


        // 创建直方图画布
        int hist_w =  src.rows();
        int hist_h = src.cols();
        Mat histImage = new Mat( hist_w, hist_h, CvType.CV_8UC3, new Scalar( 0,0,0) );

        /// 将直方图归一化到范围 [ 0, histImage.rows ]
        Core.normalize(r_hist, r_hist, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat() );
        Core.normalize(g_hist, g_hist, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat() );
        Core.normalize(b_hist, b_hist, 0, histImage.rows(), Core.NORM_MINMAX, -1, new Mat() );

        long bin_w = Math.round((double) hist_w / 256);
        /// 在直方图画布上画出直方图
        for( int i = 1; i < 256; i++ )
        {
            Point p1 = new Point(bin_w * (i - 1), hist_h - Math.round(r_hist.get(i - 1, 0)[0]));
            Point p2 = new Point(bin_w * (i), hist_h - Math.round(r_hist.get(i, 0)[0]));
            Imgproc.line(histImage, p1, p2, new Scalar( 0, 0, 255), 2, 8, 0);


            Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(g_hist.get(i - 1, 0)[0]))
                    , new Point(bin_w * (i), hist_h - Math.round(g_hist.get(i, 0)[0]))
                    , new Scalar( 0, 255, 0), 2, 8, 0);

            Imgproc.line(histImage, new Point(bin_w * (i - 1), hist_h - Math.round(b_hist.get(i - 1, 0)[0]))
                    , new Point(bin_w * (i), hist_h - Math.round(b_hist.get(i, 0)[0]))
                    , new Scalar( 255, 0, 0), 2, 8, 0);
        }

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(histImage, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }

    /**
     * 直方图均衡化
     * http://docs.opencv.org/2.4/doc/tutorials/imgproc/histograms/histogram_equalization/histogram_equalization.html#histogram-equalization
     * @param picture
     * @param picture_result
     */
    public static void equalHist(ImageView picture, ImageView picture_result){
        picture.setDrawingCacheEnabled(true);
        picture.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) picture.getDrawable()).getBitmap();
        System.loadLibrary("opencv_java3");
        Mat src = new Mat();
        Utils.bitmapToMat(bitmap, src);
        Mat dst = src.clone();
        // 转为灰度图
        Imgproc.cvtColor( src, src, Imgproc.COLOR_BGR2GRAY );
        /// 应用直方图均衡化
        Imgproc.equalizeHist( src, dst );

        Bitmap tmpbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(dst, tmpbitmap);
        picture_result.setImageBitmap(tmpbitmap);
        picture.setDrawingCacheEnabled(false);
    }
}
