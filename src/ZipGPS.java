import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ZipGPS {

    double DpxDN = (double) (834) / (50 - 21);
    double DpxDW = (double) (1116) / (127 - 65);


    public ZipGPS() throws IOException {

        initGUI();

    }


    public double deltaX(double heightPX) {

        return (2.96268 * Math.pow(10, -8) * Math.pow(heightPX, 3) - 0.0000352381 * Math.pow(heightPX, 2) - 0.0533567 * heightPX + 3.22582);
    }

    public double deltaY(double widthPX) {

        double y = -1.49721 * Math.pow(10, -7) * Math.pow(widthPX, 3) - 0.000324907 * Math.pow(widthPX, 2) + 0.382076 * widthPX - 21.188;

        return y;
    }

    public Point convert(double latitude, double longitude) {

        double ycord = 56 + (DpxDN * (50 - latitude));
        double xcord = 48 + (DpxDW * (128.3 - longitude));

        double ycordFirst = ycord + deltaY(xcord);
        double xcordFirst = xcord + deltaX(ycordFirst);
        ycord += deltaY(xcordFirst);
        xcord += deltaX(ycord);

        return new Point((int) xcord, (int) ycord);
    }

    public void initGUI() throws IOException {

        JFrame frame = new JFrame("ZipGPS");

        File file = new File("src/utm-USA.jpg");


          final BufferedImage Image = ImageIO.read(file);


        JPanel panel = new JPanel(){

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(Image,0,0,null);
                g.setColor(Color.BLACK);

                int XCORD = (int) convert(35,120).getX();
                int YCORD = (int) convert(35,120).getY();
                g.fillOval(XCORD, YCORD, 5, 5);
            }
        };

        frame.add(panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
     //   frame.setMaximumSize(new Dimension(Image.getWidth(), Image.getHeight()));
        frame.setMinimumSize(new Dimension(Image.getWidth(), Image.getHeight()));
      //  frame.setResizable(false);

        frame.setVisible(true);
    }



    public static void main(String[] args) {

        try {
            ZipGPS zippy = new ZipGPS();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
