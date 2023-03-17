package main.java.org.wuxian.machines;


import main.java.org.wuxian.database.Portal;
import org.opencv.core.Point;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import org.apache.commons.lang3.RandomStringUtils;

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
import java.io.IOException;
import java.sql.SQLException;

public class CreateStudents extends JFrame {

    JPanel total_pane, photo_panel;
    JLabel lbl_mat_number, lbl_full_name, lbl_department, lbl_phone, lbl_upload;
    JTextField mat_number, full_name, department, phone;
    JButton photo, create, back_btn;
    JLabel photo_tip;
    String dir = null;
    String random = generateRandomNumbers();;
    String num  = generateNumbers();
    private DaemonThread myThread = null;
    int count = 0;
    BufferedImage img;
    String filePath = null;
    Thread t;
    JComboBox departments;
    String [] dep = {"Computer Science", "Physics", "Biochemistry","Agricultural Engineering"};
    int capture = 1;

    VideoCapture webSource = null;
    Mat frame = new Mat();
    MatOfByte mem = new MatOfByte();
    CascadeClassifier faceDetector = new CascadeClassifier(CreateStudents.class.getResource("haar/haarcascade_frontalface_alt.xml").getPath().substring(1));
    MatOfRect faceDetections = new MatOfRect();

    JProgressBar jProgressBar;

    public CreateStudents() {

        total_pane = new JPanel();
        total_pane.setLayout(new GridLayout());
        total_pane.setBackground(Color.WHITE);

        Container form_pane = new Container();
        JLabel title = new JLabel("Create New Student");
        title.setFont(new Font("Lucida", Font.BOLD, 30));
        title.setBounds(400, 5, 500, 50);
        form_pane.add(title);


        lbl_mat_number = new JLabel("Enter Matric Number");
        lbl_mat_number.setBounds(30, 80, 250, 50);
        lbl_mat_number.setFont(new Font("Lucida", Font.PLAIN, 20));
        form_pane.add(lbl_mat_number);

        mat_number = new JTextField();
        mat_number.setBounds(225, 80, 250, 50);
        form_pane.add(mat_number);


        lbl_full_name = new JLabel("Enter Full Name");
        lbl_full_name.setBounds(30, 160, 250, 50);
        lbl_full_name.setFont(new Font("Lucida", Font.PLAIN, 20));
        form_pane.add(lbl_full_name);

        full_name = new JTextField();
        full_name.setBounds(225, 160, 250, 50);
        form_pane.add(full_name);

        lbl_department = new JLabel("Enter Department");
        lbl_department.setBounds(30, 240, 250, 50);
        lbl_department.setFont(new Font("Lucida", Font.PLAIN, 20));
        form_pane.add(lbl_department);

        departments = new JComboBox(dep);
        departments.setBounds(225, 240, 250, 50);
        form_pane.add(departments);

        lbl_phone = new JLabel("Enter Phone");
        lbl_phone.setBounds(30, 320, 250, 50);
        lbl_phone.setFont(new Font("Lucida", Font.PLAIN, 20));
        form_pane.add(lbl_phone);

        phone = new JTextField();
        phone.setBounds(225, 320, 250, 50);
        form_pane.add(phone);


        back_btn = new JButton("Home");
        back_btn.setBounds(5, 5, 100, 40);
        back_btn.setFont(new Font("Lucida", Font.PLAIN, 20));

        form_pane.add(back_btn);


        create = new JButton("Capture and Create Student");
        create.setBounds(30, 480, 450, 50);
        create.setFont(new Font("Lucida", Font.PLAIN, 20));
        create.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String matric = mat_number.getText();
                String name = full_name.getText();
                String dept = departments.getSelectedItem().toString();
                String phn = phone.getText();
                if (matric == "" || name == "" || dept == "" || phn == "") {
                    JOptionPane.showMessageDialog(null, "Empty Fields Detected");
                } else {
                    try {
                        createStudents(matric, name, dept, phn);
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, "Error creating student " + ex);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }

            }
        });
        form_pane.add(create);


        photo_panel = new JPanel();
        photo_panel.setBorder(BorderFactory.createBevelBorder(1));
        photo_panel.setBounds(550, 100, 350, 350);
        form_pane.add(photo_panel);

        photo_tip = new JLabel();
        photo_tip.setBounds(550, 450, 350, 16);
        photo_tip.setFont(new Font("Lucida", Font.ITALIC, 16));
        photo_tip.setText("No Image Detected");
        photo_tip.setForeground(Color.RED);
        form_pane.add(photo_tip);

        jProgressBar = new JProgressBar();
        jProgressBar.setBounds(550, 485, 350, 40);
        jProgressBar.setFont(new Font("Lucida", Font.ITALIC, 20));
        jProgressBar.setForeground(Color.RED);
        jProgressBar.setStringPainted(true);
        jProgressBar.setValue(0);
        form_pane.add(jProgressBar);


        total_pane.add(form_pane);

        getContentPane().add(total_pane);
        setSize(1000, 600);
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


    class DaemonThread implements Runnable {

        protected volatile boolean runnable = false;

        @Override
        public void run() {
            synchronized (this) {
                while (runnable) {
                    if (webSource.grab()) {
                        try {

                            webSource.retrieve(frame);
                            Graphics g = photo_panel.getGraphics();
                            // g.drawImage(getImage(frame), 0, 0, null);
                            faceDetector.detectMultiScale(frame, faceDetections);
                            if (faceDetections.toArray().length > 1){
                                photo_tip.setText("Multiple Images Detected");
                                photo_tip.setForeground(Color.RED);
                            }else if (faceDetections.toArray().length == 1){
                                photo_tip.setText("Image Ready For Capture");
                                photo_tip.setForeground(Color.BLUE);
                                //captureImage();
                            }else if(faceDetections.toArray().length == 0){
                                photo_tip.setText("No Image Detected");
                                photo_tip.setForeground(Color.RED);
                            }
                            for (Rect rect : faceDetections.toArray()) {
                                 System.out.println(rect);
                                if(rect.empty()){
                                    photo_tip.setText("No Image Detected");
                                    photo_tip.setForeground(Color.RED);
                                }
                                Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                                        new Scalar(0, 255, 0));


                            }
                            Imgcodecs.imencode(".bmp", frame, mem);
                            Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
                            BufferedImage buff = (BufferedImage) im;
                            if (g.drawImage(buff, 0, 0, getWidth() - 650, getHeight() - 250, 0, 0, buff.getWidth(), buff.getHeight(), null)) {
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


    private void createStudents(String matric, String name, String dept, String phn) throws SQLException, IOException {
        filePath ="C:\\Users\\HP\\Documents\\SimpleAccess\\images\\" + num +"-"+random+".png";
        int status = new Portal().insertStudents(num+"-"+random, matric, name, dept, phn, filePath);
        if (status > 0) {
            mat_number.setText("");
            full_name.setText("");
            phone.setText("");

                try {
                    captureImage(random,num, filePath);
                        JOptionPane.showMessageDialog(null, "Student Created Successfully");
                        new FaceRecognizer().init();

                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Error Creating Student"+ex);
                    throw new RuntimeException(ex);
                }
        } else {
            JOptionPane.showMessageDialog(null, "Error Creating Student");
        }
    }

    private void captureImage(String rand,String num, String filePath) throws SQLException {
        dir = System.getProperty("user.dir");
        // System.out.println(filePath);
        faceDetector.detectMultiScale(frame, faceDetections);
        for (Rect rect : faceDetections.toArray()) {
            if (rect.empty()) {
                JOptionPane.showMessageDialog(null, "No Face Detected");
            } else if (faceDetections.toArray().length > 1) {
                JOptionPane.showMessageDialog(null, "Too many faces");
            } else {


                Imgproc.rectangle(frame, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 255, 0));
                Mat faceROI = new Mat(frame, rect);
                Mat dst = new Mat();
                Imgproc.resize(faceROI, dst, new Size(150, 150), 0.1, 0.1,
                        Imgproc.INTER_AREA);
                Imgcodecs.imwrite(filePath, dst);
               // jProgressBar.setValue(capture);

            }
            File file = new File(filePath);
            if (file.exists()) {
                new Portal().insertImages(random,filePath);
                jProgressBar.setValue(100);
               // JOptionPane.showMessageDialog(null, "Photo Captured");
            } else {
               // JOptionPane.showMessageDialog(null, "Photo Capture Error");
            }

        }


    }

    public String generateNumbers() {
        String letter = RandomStringUtils.randomNumeric(2);
        return letter;
    }

    public String generateRandomNumbers() {
        String letter = RandomStringUtils.randomAlphanumeric(3).toUpperCase();
        return letter;
    }

   public BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) throws IOException {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

   // public static void main(String[] args) {

   //     new CreateStudents();
   // }
}