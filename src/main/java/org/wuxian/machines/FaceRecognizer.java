package main.java.org.wuxian.machines;

import main.java.org.wuxian.database.Portal;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.opencv_face.LBPHFaceRecognizer;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.IntBuffer;
import java.sql.SQLException;
import java.util.ArrayList;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_face.createLBPHFaceRecognizer;
import static org.bytedeco.javacpp.opencv_imgcodecs.CV_LOAD_IMAGE_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;

public class FaceRecognizer {

	LBPHFaceRecognizer faceRecognizer;
 
	public File root;
	MatVector images;
	Mat labels;

	public void init() {
		// mention the directory the faces has been saved
		String trainingDir = "C:\\Users\\HP\\Documents\\SimpleAccess\\images";

		root = new File(trainingDir);

		FilenameFilter imgFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				name = name.toLowerCase();
				return name.endsWith(".jpg") || name.endsWith(".pgm") || name.endsWith(".png");
			}
		};

		File[] imageFiles = root.listFiles(imgFilter);

		this.images = new MatVector(imageFiles.length);

		this.labels = new Mat(imageFiles.length, 1, CV_32SC1);
		IntBuffer labelsBuf = labels.createBuffer();

		int counter = 0;
		// reading face images from the folder

		for (File image : imageFiles) {
			Mat img = imread(image.getAbsolutePath(), CV_LOAD_IMAGE_GRAYSCALE);

			// extracting unique face code from the face image names
			/*
			this unique face will be used to fetch all other information from
			I dont put face data on database.
			I just store face indexes on database.

			For example:
			When you train a new face to the system suppose person named ABC.
			Now this person named ABC has 10(can be more or less)  face image which
			will be saved in the project folder named "/Faces" using a naming convention such as
			1_ABC1.jpg
			1_ABC2.jpg
			1_ABC3.jpg
			.......
			1_ABC10.jpg
		
			The initial value of the file name is the index key in the database table of that person.
			the key 1 will be used to fetch data from database.
 
			*/
			System.out.println(image.getName().split("\\-")[0]);
			int label = Integer.parseInt(image.getName().split("\\-")[0]);

			images.put(counter, img);

			labelsBuf.put(counter, label);

			counter++;
		}

		// face training
		//this.faceRecognizer = createLBPHFaceRecognizer();
		faceRecognizer = createLBPHFaceRecognizer();
		
		
		faceRecognizer.train(images, labels);
		File trainingFile  = new File("trained_data.xml");
		if (trainingFile.exists()){
			trainingFile.delete();
			faceRecognizer.save("trained_data.xml");
		}else{
			faceRecognizer.save("trained_data.xml");
		}

	}

	public ArrayList<String> recognize(IplImage faceData) throws SQLException {
		Mat faces = cvarrToMat(faceData);
		System.out.println(faces);
		cvtColor(faces, faces, CV_BGR2GRAY);

		IntPointer label = new IntPointer(1);

		DoublePointer confidence = new DoublePointer(0);
		
 		LBPHFaceRecognizer faceRecognize = createLBPHFaceRecognizer();;
		 faceRecognize.load("trained_data.xml");
		faceRecognize.predict(faces, label, confidence);
		
		 
		int predictedLabel = label.get(0);
		System.out.println(predictedLabel);
		 
		//System.out.println(confidence.get(0));
		
 
	
		//Confidence value less than 60 means face is known 
		//Confidence value greater than 60 means face is unknown 
		 if(confidence.get(0) > 60)
		 {
			 System.out.println("-1");
			// return "-1";
		 }

		String filter = filterFiles(String.valueOf(predictedLabel));
		ArrayList<String> student_details = new Portal().getStudentDetails(filter);
		//System.out.println(student_details);
		return student_details;
	}


	public String filterFiles(String predicted){
		String filename = null;
		File files =  new File("C:\\Users\\HP\\Documents\\SimpleAccess\\images");
		for(File file : files.listFiles()){
			if(predicted.contentEquals(file.getName().substring(0,2))){
				filename  = file.getName().substring(0,6);
			}
		}
		return filename;
	}

	public static IplImage toIplImage(BufferedImage bufImage) {

		OpenCVFrameConverter.ToIplImage iplConverter = new OpenCVFrameConverter.ToIplImage();
		Java2DFrameConverter java2dConverter = new Java2DFrameConverter();
		IplImage iplImage = iplConverter.convert(java2dConverter.convert(bufImage));
		return iplImage;
	}

//	public static void main(String[] args) throws IOException, SQLException {
		//new FaceRecognizer().init();
		//System.out.println(new FaceRecognizer().filterFiles("49"));
		//Image image = ImageIO.read(new File("C:\\Users\\HP\\Documents\\SimpleAccess\\trainedImages\\37-CWH.png"));
		//BufferedImage buffered = (BufferedImage) image;
		//System.out.println(new FaceRecognizer().recognize(toIplImage(buffered)));
//	}
}
