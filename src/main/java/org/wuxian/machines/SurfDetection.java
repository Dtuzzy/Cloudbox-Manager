package org.wuxian.machines;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.SQLException;

class SURFDetection {

    double[] descriptor;

    public void runTrainingData(String filename, String matric_number) throws SQLException, IOException {
        Mat src = Imgcodecs.imread(filename, Imgcodecs.IMREAD_GRAYSCALE);
        if (src.empty()) {
            System.err.println("Cannot read image: " + filename);
            System.exit(0);
        }
        //-- Step 1: Detect the keypoints using SURF Detector
        Mat descriptors = new Mat();
        ORB detector = ORB.create();
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        // detector.detect(src,keypoints);
        detector.detectAndCompute(src, new Mat(), keypoints, descriptors);

        // Print out the ORB descriptors
        for (int i = 0; i < descriptors.rows(); i++) {
            for (int j = 0; j < descriptors.cols(); j++) {
                descriptor = descriptors.get(i, j);
                //  System.out.print(descriptor[0]);
                for (int k = 1; k < descriptor.length; k++) {
                    //   System.out.print(", " + descriptor[k]);
                }
                // System.out.println();
            }
        }
        //-- Draw key-points
        //Features2d.drawKeypoints(src, keypoints, src);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(descriptor);
        oos.close();
        byte[] bytes = baos.toByteArray();
        //System.out.println(src);

       /* List<KeyPoint> first_100 = new ArrayList(keypoints.toList().subList(0,keypoints.toArray().length));
        System.out.println(first_100.size());
        //System.out.println(first_100);
        for (int i = 0; i < first_100.size(); i++){
            System.out.println(first_100.get(i));
            String pointX = String.valueOf(first_100.get(i).pt.x);
            String pointY = String.valueOf(first_100.get(i).pt.y);
            String pointSize = String.valueOf(first_100.get(i).size);
            String pointAngle = String.valueOf(first_100.get(i).angle);
            String pointOctave = String.valueOf(first_100.get(i).octave);
            String pointResponse = String.valueOf(first_100.get(i).response);
            String pointClass = String.valueOf(first_100.get(i).class_id);
            new Portal().insertKeyPoints(matric_number,pointX,pointY,pointSize,pointAngle,pointOctave,pointResponse,pointClass);
        }
        //-- Show detected (drawn) keypoints
       // HighGui.imshow("SURF Keypoints", src);
       // HighGui.waitKey(0);
        //System.exit(0);*/

        //new Portal().insertKeyPoints(matric_number,filename,src,bytes);
    }


    public void runTestingData(String filename, String matric_number) throws SQLException, IOException {
        Mat src = Imgcodecs.imread(filename, Imgcodecs.IMREAD_GRAYSCALE);
        if (src.empty()) {
            System.err.println("Cannot read image: " + filename);
            System.exit(0);
        }
        //-- Step 1: Detect the keypoints using SURF Detector
        Mat descriptors = new Mat();
        ORB detector = ORB.create();
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        // detector.detect(src,keypoints);
        detector.detectAndCompute(src, new Mat(), keypoints, descriptors);

        // Print out the ORB descriptors
        for (int i = 0; i < descriptors.rows(); i++) {
            for (int j = 0; j < descriptors.cols(); j++) {
                descriptor = descriptors.get(i, j);
                //  System.out.print(descriptor[0]);
                for (int k = 1; k < descriptor.length; k++) {
                    //   System.out.print(", " + descriptor[k]);
                }
                // System.out.println();
            }
        }
        //-- Draw key-points
        //Features2d.drawKeypoints(src, keypoints, src);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(descriptor);
        oos.close();
        byte[] bytes = baos.toByteArray();
       // for(int i=0; i< bytes.length ; i++) {
       //     System.out.print(bytes[i] +" ");
       // }
       // System.out.println(src);

       /* List<KeyPoint> first_100 = new ArrayList(keypoints.toList().subList(0,keypoints.toArray().length));
        System.out.println(first_100.size());
        //System.out.println(first_100);
        for (int i = 0; i < first_100.size(); i++){
            System.out.println(first_100.get(i));
            String pointX = String.valueOf(first_100.get(i).pt.x);
            String pointY = String.valueOf(first_100.get(i).pt.y);
            String pointSize = String.valueOf(first_100.get(i).size);
            String pointAngle = String.valueOf(first_100.get(i).angle);
            String pointOctave = String.valueOf(first_100.get(i).octave);
            String pointResponse = String.valueOf(first_100.get(i).response);
            String pointClass = String.valueOf(first_100.get(i).class_id);
            new Portal().insertKeyPoints(matric_number,pointX,pointY,pointSize,pointAngle,pointOctave,pointResponse,pointClass);
        }
        //-- Show detected (drawn) keypoints
       // HighGui.imshow("SURF Keypoints", src);
       // HighGui.waitKey(0);
        //System.exit(0);*/

       // new Portal().insertTrainedKeyPoints(matric_number,filename,src,bytes);
    }

    public void runSample(String filename, String file2) throws SQLException {
        Mat src = Imgcodecs.imread(filename, Imgcodecs.IMREAD_GRAYSCALE);
        if (src.empty()) {
            System.err.println("Cannot read image: " + filename);
            System.exit(0);
        }
        Mat dst = new Mat();
        Mat src2 = Imgcodecs.imread(file2, Imgcodecs.IMREAD_GRAYSCALE);
        //-- Step 1: Detect the keypoints using SURF Detector
        int hessianThreshold = 400;
        int nOctaves = 4, nOctaveLayers = 3;
        boolean extended = false, upright = false;
        ORB detector = ORB.create();
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        MatOfKeyPoint keypoints2 = new MatOfKeyPoint();
        detector.detect(src, keypoints);
        detector.detect(src2, keypoints2);

        MatOfDMatch matOf1to2 = new MatOfDMatch();
        Features2d.drawMatches(src, keypoints, src2, keypoints2, matOf1to2, dst);
        HighGui.imshow("Feature Matching", dst);
        HighGui.waitKey();

    }


    public static void main(String[] args) throws SQLException, IOException {
        // Load the native OpenCV library
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        // new SURFDetection().runSample("C:\\Users\\HP\\Documents\\SimpleAccess\\trainedImages\\GXHVHGL7.png","C:\\Users\\HP\\Documents\\SimpleAccess\\trainedImages\\09AYGNVI.png");
        //new SURFDetection().runTrainingData("C:\\Users\\HP\\Documents\\SimpleAccess\\kay.jpeg", "ARGGFA");
        new SURFDetection().runTestingData("C:\\Users\\HP\\Documents\\SimpleAccess\\kay_02.png", "ARGGTF");


    }
}
