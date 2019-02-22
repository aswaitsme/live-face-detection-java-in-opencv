import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc; 
import org.opencv.objdetect.CascadeClassifier;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;


public class FaceDetect extends JPanel
{ 
	static BufferedImage image;
	static Robot robot;
    Rectangle rectArea;
    BufferedImage img;
    JLabel label;
    JFrame frame;
    CascadeClassifier faceDetector;
    int width=500,height=400;

    public FaceDetect() throws AWTException {

        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        faceDetector = new CascadeClassifier();
        faceDetector.load("C:\\others\\opencv\\sources\\data\\haarcascades\\haarcascade_frontalface_alt.xml");
        robot = new Robot();
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                        ex.printStackTrace();
                    }

                     rectArea = new Rectangle(10, 50, width, height);
                    //rectArea =  new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                     img = robot.createScreenCapture(rectArea);

                     label = new JLabel(new ImageIcon(img));

                     frame = new JFrame("Testing");


                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.add(label);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setVisible(true);



    }
	 @Override
	    protected void paintComponent(Graphics g) {
	        super.paintComponent(g);
	        g.drawImage(image, 0, 0, this);
	    }

	    private void takeScreenshot() throws IOException, InterruptedException {
            for(;;) {

                img = robot.createScreenCapture(rectArea);
                img=getFaceBoxes(img);

                frame.remove(label);
                label = new JLabel(new ImageIcon(img));

                frame.add(label);
                frame.revalidate();
                frame.repaint();

            }
        }
    public static Mat BufferedImagetoMat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "jpg", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }

    public static BufferedImage MattoBufferedImage(Mat matrix)throws IOException {
        MatOfByte mob=new MatOfByte();
        Imgcodecs.imencode(".jpg", matrix, mob);
        return ImageIO.read(new ByteArrayInputStream(mob.toArray()));
    }

    public static BufferedImage rotImage(BufferedImage image) throws IOException {

    	 Mat src = BufferedImagetoMat(image);
         Mat dst = new Mat();
         Mat rotationMatrix = Imgproc.getRotationMatrix2D(new Point(300, 200), 30, 1);
         Imgproc.warpAffine(src, dst,rotationMatrix, new Size(src.cols(), src.cols()));

         return MattoBufferedImage(dst);
    }

    private BufferedImage getFaceBoxes(BufferedImage img) throws IOException {

        Mat image = BufferedImagetoMat(img);
        MatOfRect faceDetections = new MatOfRect();

        faceDetector.detectMultiScale(image, faceDetections);

        for (Rect rect : faceDetections.toArray())
        {
            Imgproc.rectangle(image, new Point(rect.x, rect.y),
                    new Point(rect.x + rect.width, rect.y + rect.height),
                    new Scalar(0, 255, 0));
        }

        return MattoBufferedImage(image);
    }

	public static void main(String[] args) throws AWTException, IOException, InterruptedException {
		FaceDetect fd = new FaceDetect();
		fd.takeScreenshot();
	} 
} 
