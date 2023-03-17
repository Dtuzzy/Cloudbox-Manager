package main.java.org.wuxian.machines;

import org.apache.commons.lang3.RandomStringUtils;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.ArrayList;

public class Recognize extends JFrame {

    JPanel total_pane, result_pane, video_pane;
    private DaemonThread myThread = null;
    int count = 0;
    BufferedImage img;
    String dir = null;
    String random = null;
    String filePath = null;

    VideoCapture webSource = null;
    Mat frame = new Mat();
    JTextArea console_pane;
    MatOfByte mem = new MatOfByte();
    CascadeClassifier faceDetector = new CascadeClassifier(CreateStudents.class.getResource("haar/haarcascade_frontalface_alt.xml").getPath().substring(1));
    MatOfRect faceDetections = new MatOfRect();

    JPanel result_panel;
    Container result_data;
    JLabel result_full_name, result_matric_number, result_department, result_student_id, result_student_phone, image_label, lecture, message;
    JButton model;
    Thread t;
    JComboBox classes;
    String [] dep = {"Csc 402", "Csc 404", " Csc 406","Csc 408","Csc 410","Csc 466"};
    ImageIcon imageIcon;
    public Recognize() {

        total_pane = new JPanel();
        total_pane.setLayout(new BorderLayout(5,5));
        result_pane = new JPanel(new GridLayout(1,1));
        result_pane.setBorder(BorderFactory.createEtchedBorder());

        console_pane = new JTextArea();
        console_pane.setFont(new Font("Lucida", Font.PLAIN, 20));
        console_pane.setBackground(Color.black);
        console_pane.setForeground(Color.white);
        JScrollPane jp = new JScrollPane(console_pane);
        jp.getVerticalScrollBar().addAdjustmentListener(e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum()));
       // result_pane.add(jp);


        result_pane = new JPanel(new FlowLayout(FlowLayout.LEFT, 50, 5));
        result_pane.setPreferredSize(new Dimension(600,400));
        result_student_id = new JLabel();
        result_student_id.setFont(new Font("Lucida", Font.PLAIN, 20));
        result_student_id.setPreferredSize(new Dimension(600,20));
        result_pane.add(result_student_id);

       result_full_name = new JLabel();
       result_full_name.setFont(new Font("Lucida", Font.PLAIN, 20));
       result_full_name.setPreferredSize(new Dimension(600,20));
       result_pane.add(result_full_name);

       result_matric_number = new JLabel();
       result_matric_number.setFont(new Font("Lucida", Font.PLAIN, 20));
       result_matric_number.setPreferredSize(new Dimension(600,20));
       result_pane.add(result_matric_number);

        result_student_phone = new JLabel();
        result_student_phone.setFont(new Font("Lucida", Font.PLAIN, 20));
        result_student_phone.setPreferredSize(new Dimension(600,20));
        result_pane.add(result_student_phone);

        result_department = new JLabel();
        result_department.setFont(new Font("Lucida", Font.PLAIN, 20));
        result_department.setPreferredSize(new Dimension(600,20));
        result_pane.add(result_department);


        image_label = new JLabel();
        result_pane.add(image_label);





        model = new JButton("Activate Model");
        model.setPreferredSize(new Dimension(getWidth(),80));
        model.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    activateModel();
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });


        video_pane = new JPanel();
        video_pane.setBackground(Color.decode("#FFCCCB"));
        video_pane.setPreferredSize(new Dimension(800,500));
        video_pane.setBorder(BorderFactory.createEtchedBorder());
        video_pane.setLayout(null);

        total_pane.add(video_pane,BorderLayout.WEST);
        total_pane.add(jp);
        total_pane.add(model,BorderLayout.PAGE_END);

        getContentPane().add(total_pane);
        setSize(1200, 600);
        setVisible(true);
        setLocationRelativeTo(null);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                System.out.println("Closed");
                t.stop();
                myThread.runnable = false;
                webSource.release();
                e.getWindow().dispose();
            }
        });

          webSource = new VideoCapture(0); // video capture from default cam
          myThread = new DaemonThread(); //create object of threat class
            t = new Thread(myThread);
          t.setDaemon(true);
          myThread.runnable = true;
           t.start();
    }


   // public static void main(String[] args) {
  //      System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
   //     new Recognize();
  //  }


    private void activateModel() throws Exception {
        dir = System.getProperty("user.dir");
        random = generateRandomNumbers();
        String num = generateNumbers();
        filePath = "C:\\Users\\HP\\Documents\\SimpleAccess\\trainedImages\\" +num+"-"+random + ".png";
        // System.out.println(filePath);
        faceDetector.detectMultiScale(frame, faceDetections);
        for (Rect rect : faceDetections.toArray()) {
            Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
            Mat faceROI = new Mat(frame, rect);
            Mat dst = new Mat();
            Imgproc.resize(faceROI, dst, new Size(150, 150), 0.1, 0.1,
                    Imgproc.INTER_AREA);
            Imgcodecs.imwrite(filePath, dst);
        }
        File file = new File(filePath);
        if (file.exists()) {
            console_pane.append("Photo Captured");
            Image image = ImageIO.read(new File(filePath));
            BufferedImage buffered = (BufferedImage) image;
            System.out.println(new FaceRecognizer().recognize(toIplImage(buffered)));

            ArrayList result = new FaceRecognizer().recognize(toIplImage(buffered));
            System.out.println(result);
            console_pane.append("Student Name : " + result.get(1) + "\n");
            console_pane.append("Student Matric-No : " + result.get(0) + "\n");
            console_pane.append("Department : " + result.get(2) + "\n");
            console_pane.append("Student Phone : " + result.get(3) + "\n");
            console_pane.append("Student Image : " + result.get(4) + "\n");
            result_student_id.setText("Student Matric-No : " + result.get(0));
            result_full_name.setText("Student Name : " + result.get(1));
            result_department.setText("Department : " + result.get(2));
            result_student_phone.setText("Student Phone: " + result.get(3));
            imageIcon = new ImageIcon(result.get(4).toString());
            image_label.setIcon(imageIcon);
            JOptionPane.showConfirmDialog(null,result_pane,"Information",JOptionPane.YES_NO_OPTION);


        } else {
            console_pane.append("Photo Capture Error");
        }

    }

    public String generateRandomNumbers() {
        String letter = RandomStringUtils.randomAlphanumeric(3).toUpperCase();
        return letter;
    }

    public String generateNumbers() {
        String letter = RandomStringUtils.randomNumeric(2);
        return letter;
    }

    class DaemonThread implements Runnable {

        protected volatile boolean runnable = false;

        @Override
        public void run() {
            synchronized (this) {
                while (runnable) {
                    if (webSource.grab()) {
                        try {

                            webSource.retrieve(frame);
                            Graphics g = video_pane.getGraphics();
                            // g.drawImage(getImage(frame), 0, 0, null);
                            faceDetector.detectMultiScale(frame, faceDetections);
                            if (faceDetections.toArray().length > 1) {
                                console_pane.append("Multiple Images Detected  \n");
                            } else if (faceDetections.toArray().length == 1) {
                                console_pane.append("Image Found And Ready For Processing  \n");
                                // captureImage();
                                //  webSource.release();

                            } else if (faceDetections.toArray().length == 0) {
                                console_pane.append("No Image Detected \n");
                            }
                            for (Rect rect : faceDetections.toArray()) {
                                // System.out.println("ttt");
                                Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                                        new Scalar(0, 255, 0));
                                // Mat faceROI = new Mat(frame,rect);
                                // Imgcodecs.imwrite("dayo.jpeg", faceROI);

                            }
                            Imgcodecs.imencode(".bmp", frame, mem);
                            Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
                            BufferedImage buff = (BufferedImage) im;
                            if (g.drawImage(buff, 0, 0, 800, 500, 0, 0, buff.getWidth(), buff.getHeight(), null)) {
                                if (runnable == false) {
                                    System.out.println("Paused ..... ");
                                    this.wait();
                                }
                            }
                        } catch (Exception ex) {
                            System.out.println("Error!!");
                            ex.printStackTrace();
                        }
                    }
                }
            }
        }
    }


    public void getSpace(Mat mat) {
        int type = 0;
        if (mat.channels() == 1) {
            type = BufferedImage.TYPE_BYTE_GRAY;
        } else if (mat.channels() == 3) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        frame = mat;
        int w = mat.cols();
        int h = mat.rows();
        if (img == null || img.getWidth() != w || img.getHeight() != h || img.getType() != type)
            img = new BufferedImage(w, h, type);
    }


    BufferedImage getImage(Mat mat) {
        getSpace(mat);
        WritableRaster raster = img.getRaster();
        DataBufferByte dataBuffer = (DataBufferByte) raster.getDataBuffer();
        byte[] data = dataBuffer.getData();
        mat.get(0, 0, data);
        return img;
    }

    public opencv_core.IplImage toIplImage(BufferedImage bufImage) {

        OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
        Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
        opencv_core.IplImage iplImage = iplConverter.convert(java2dConverter.convert(bufImage));
        return iplImage;
    }


    public void resultContainer(){
        JFrame res_frame = new JFrame("Results");
        Container container = new Container();


        res_frame.getContentPane().add(container);
        res_frame.setSize(400,400);
        res_frame.setVisible(true);
        res_frame.setLocationRelativeTo(null);
        res_frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                e.getWindow().dispose();
            }
        });
    }
}
