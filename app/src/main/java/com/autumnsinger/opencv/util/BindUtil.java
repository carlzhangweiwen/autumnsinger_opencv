package com.autumnsinger.opencv.util;

import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.autumnsinger.opencv.activity.MainActivity;

/**
 * Created by CarlZhang on 2016/6/8.
 */
public class BindUtil {
    /**
     * 二值化
     * @param picture
     * @param picture_result
     * @param minValue
     * @param maxValue
     * @param minValueText
     * @param maxValueText
     */
    public static void binaryBarBind(final ImageView picture,final ImageView  picture_result,final SeekBar minValue,final SeekBar maxValue, final TextView minValueText, final TextView maxValueText){
        minValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                minValueText.setText( "MinValue:" + progress);
                //二值化
                PictureUtils.binaryPic(picture, picture_result, minValue, maxValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        maxValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                maxValueText.setText("MaxValue:" + progress);
                //二值化
                PictureUtils.binaryPic(picture, picture_result, minValue, maxValue);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 平移
     * @param picture
     * @param picture_result
     * @param x_distanceBar
     * @param y_distanceBar
     */
    public static void xy_distanceBarBind(final ImageView picture,final ImageView  picture_result,final SeekBar x_distanceBar,final SeekBar y_distanceBar){
        x_distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //图片平移
                PictureUtils.translate(picture, picture_result , x_distanceBar, y_distanceBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        y_distanceBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //图片平移
                PictureUtils.translate(picture, picture_result , x_distanceBar, y_distanceBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    /**
     * 图片缩放
     * @param picture
     * @param picture_result
     * @param scalingBar
     */
    public static void scalingBarBind(final ImageView picture,final ImageView  picture_result,final SeekBar scalingBar){
        scalingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //图片缩放
                PictureUtils.scaling(picture, picture_result , scalingBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    /**
     * 图片旋转
     * @param picture
     * @param picture_result
     * @param rotatingBar
     */
    public static void rotatingBarBind(final ImageView picture,final ImageView  picture_result,final SeekBar rotatingBar, final TextView ratatingText){
        rotatingBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ratatingText.setText("Rotating Angle:" + progress);
                //图片缩放
                PictureUtils.rotating(picture, picture_result , rotatingBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    /**
     * 形态学
     * @param mainActivity
     * @param picture
     * @param picture_result
     * @param morph_type_group
     * @param morph_ele_type_group
     * @param morphText
     * @param morphBar
     */
    public static void bindMorphOperate(final MainActivity mainActivity, final ImageView picture, final ImageView picture_result, RadioGroup morph_type_group, RadioGroup morph_ele_type_group, final TextView morphText,final SeekBar morphBar) {
        morph_type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton)mainActivity.findViewById(radioButtonId);
                switch (rb.getText().toString()){
                    case "Dilation": ConstantMorph.MORPH_OPERATION_TYPE = 0;break;
                    case "Erosion": ConstantMorph.MORPH_OPERATION_TYPE = 1;break;
                    case "Opening": ConstantMorph.MORPH_OPERATION_TYPE = 2;break;
                    case "Closing": ConstantMorph.MORPH_OPERATION_TYPE = 3;break;
                    case "Morphological Gradient": ConstantMorph.MORPH_OPERATION_TYPE = 4;break;
                }

                //Morphology Operation
                PictureUtils.morphologyOperate(picture, picture_result, ConstantMorph.MORPH_OPERATION_TYPE, ConstantMorph.MORPH_ELEMENT, morphBar);
            }
        });

        morph_ele_type_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                //获取变更后的选中项的ID
                int radioButtonId = arg0.getCheckedRadioButtonId();
                //根据ID获取RadioButton的实例
                RadioButton rb = (RadioButton)mainActivity.findViewById(radioButtonId);
                switch (rb.getText().toString()){
                    case "Rect": ConstantMorph.MORPH_ELEMENT = 0;break;
                    case "Cross": ConstantMorph.MORPH_ELEMENT = 1;break;
                    case "Ellipse": ConstantMorph.MORPH_ELEMENT = 2;break;

                }

                //Morphology Operation
                PictureUtils.morphologyOperate(picture, picture_result, ConstantMorph.MORPH_OPERATION_TYPE, ConstantMorph.MORPH_ELEMENT, morphBar);
            }
        });

        morphBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                morphText.setText("Element Size:" + progress);
                //Morphology Operation
                PictureUtils.morphologyOperate(picture, picture_result, ConstantMorph.MORPH_OPERATION_TYPE, ConstantMorph.MORPH_ELEMENT, seekBar);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }
}
