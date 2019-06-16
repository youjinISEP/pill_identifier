#include <jni.h>
#include <string>
#include <opencv2/opencv.hpp>
#include <opencv2/core.hpp>
#include <opencv2/imgproc.hpp>

#include <iostream>
#include <cmath>

using namespace cv;
using namespace std;

extern "C"
JNIEXPORT jint JNICALL
Java_com_example_finalproject_pill_1Identifier_imageprocessing(JNIEnv *env, jobject instance,
                                                               jlong inputImage) {
    Mat &img_input = *(Mat *) inputImage; //input image
    Mat img_output; //output image
    Mat img_process,img_hsv,img_gray, img_binary, img_blur, img_canny, mask;
    int shape;

    //image 전처리 부분이다. HSV로 color space를 변환해준뒤, 흑백화 하고 이진화를 거쳐 이미지를 생성한다.
    cvtColor(img_input, img_process, COLOR_RGB2RGBA);
    cvtColor(img_process, img_hsv, COLOR_RGB2HSV);
    cvtColor(img_hsv, img_gray, COLOR_BGR2GRAY);
    GaussianBlur(img_gray,img_blur, Size(5,5),0);
    threshold(img_gray, img_binary, 0, 255, THRESH_BINARY_INV | THRESH_OTSU);


    //binary image의 [0.0.0]의 픽셀 값을 변수 r,g,b에 저장한다.
    int r = img_binary.at<Vec3b>(0,0)[0];
    int g = img_binary.at<Vec3b>(0,0)[1];
    int b = img_binary.at<Vec3b>(0,0)[2];

    //findContour()함수를 적용하기 전에, 배경이 흰색이면(r,g,b값이 255이면) 색 반전을 시켜준다.
    if((r==255)&&(g==255)&&(b==255)){
        img_binary = ~img_binary;
    }

    //이진화된 알약의 이미지에서 contour point들을 찾는다.
    vector<vector<Point>> contours;
    findContours(img_binary, contours, RETR_EXTERNAL, CHAIN_APPROX_SIMPLE, Point(0,0));

    //CannyEdge를 이용해 edge를 검출하고,
    //그 edge들이 그려진 이미지에서 houghline()함수를 이용하여 이미지 내부의 직선의 수를 찾는다.
    Canny(img_gray,img_canny,50,150,3);
    vector<Vec2f>lines;
    HoughLines(img_canny, lines , 3, CV_PI/30, 70,0,0);

    //approxPolyDP()를 이용하여, contour의 값들을 근사화하여 알약의 모양을 판별함.
    vector<Point> approx;
    shape = 0;
    for (int i = 0; i < contours.size(); i++) {
        approxPolyDP(Mat(contours[i]), approx, arcLength(Mat(contours[i]), true) * 0.01, true);

        unsigned long size = approx.size();
        line(img_binary, approx[0], approx[approx.size() - 1], Scalar(0, 255, 0), 3);

        for (int k = 0; k < size - 1; k++)
            line(img_binary, approx[k], approx[k + 1], Scalar(0, 255, 0), 3);

        if (isContourConvex(Mat(approx))) {
            switch (size) {
                case 3:
                    shape = 1;//삼각형
                    break;
                case 4:
                    shape = 2;//사각형, 마름모
                    break;
                case 5:
                    shape = 4;//오각형
                    break;
                case 6:
                    shape = 5;//육각형
                    break;
                case 8:
                    shape = 6;//팔각형
                    break;
                default:

                    double aspect_ratio = (double) boundingRect(Mat(contours[i])).width /
                                          boundingRect(Mat(contours[i])).height;

                    if (aspect_ratio >= 1.3)
                        if(lines.size()==2)
                            shape = 9;
                        else if(lines.size()==4)
                            shape = 2;
                        else
                            shape = 8;
                    else
                        shape = 7;

                    break;
            }

        }
    }


    //binary image에서 contour를 생성한 다음 result라는 mask생성하기.
    Mat result(img_binary.size(), CV_8U, Scalar(0));
    drawContours(img_binary, contours ,-1,Scalar(255,255,255),-1);
    //bitwise_and 연산을 이용하여, img_hsv(hsv colorspace로 변환한 원본이미지)에 mask를 씌운 img_output생성.
    bitwise_and(img_hsv,img_hsv, img_output, mask = img_binary);

    //Contour내부의 픽셀 값들의 평균 값구하여 무슨 색인 이 detect하기
    Scalar meanV = mean(img_hsv, mask = img_binary);
    double mean_h = meanV.val[0];
    double mean_s = meanV.val[1];
    double mean_v = meanV.val[2];

    //붉은 계열의 경우 180이 넘는 수가 나올수 있습니다.
    //이는 색을 판별할때에 혼돈을 초래하여 180을 뺀값으로 재 조정하였습니다.
    if(mean_h > 180)
        mean_h = 180-mean_h;


    //H값으로 색의 계열(붉,초,파)를 분류한후 S값으로 세부적으로 분류했습니다.
    int color=0;
    if ((0<=mean_h && mean_h<=15) || (170<= mean_h && mean_h <=180)){
        if(0<= mean_s && mean_s<31){
            if(mean_v<100)
                color = 13;
            else
                color = 14;
        }
        else if(30<mean_s && mean_s < 100){
            color = 1;
        }else{
            if(mean_v<100)
                color = 2;
            else
                color = 4;
        }
    }else if(15<mean_h && mean_h<38){
        if(mean_s <100)
            color = 5;
        else
            color = 6;
    }else if(37<mean_h && mean_h<83){
        if(mean_h < 59)
            color = 8;
        else
            color = 7;
    }else{
        if(mean_h<110)
            color = 10;
        else{
            if(mean_s<100)
                color = 9;
            else
                color = 11;
        }
    }

    //최종적으로 검출된 모양,색의 값을 pill_identifier.java로 넘겨줍니다.
    int value = color*10 + shape;
    return value;

}extern "C"
JNIEXPORT void JNICALL
Java_com_example_finalproject_pill_1Identifier_tessImage(JNIEnv *env, jobject instance,
                                                         jlong inputImage, jlong outputImage) {

    Mat &img_input = *(Mat *) inputImage; //input image
    Mat &img_output = *(Mat *) outputImage; //output image
    Mat img_process,img_hsv;

    cvtColor(img_input, img_process, COLOR_RGB2RGBA); //convert RGB to RGBA
    cvtColor(img_process, img_hsv, COLOR_RGB2HSV); //convert color to HSV

    vector<Mat> channels;
    split(img_hsv, channels); //split HSV image to h,s,v color channels

    img_output = channels[2]; //return v channels

}