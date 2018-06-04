import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;

public class ZipGPS {

    int currentZip;
    JTextField field;
    JLabel error;

    public ZipGPS() throws IOException {

        initGUI();

    }


    public double WtoPx(double degrees) {

        //linear fit of border Degrees West
        //ex. give a degree -->
        //the x coordinate of that degree on the border of photo
        return 2222.9 - degrees / 0.06;
    }

    public double NtoPx(double degrees) {

        //linear fit of y coordinate of Degrees North
        return -27.72 * degrees + 1450.33;
    }

    public double deltaX(double heightPX, double degree) {

        //cubic fit of x regression around 102 W
        //linear multiplier due to regression from 102 W
        return (2.96268 * Math.pow(10, -8) * Math.pow(heightPX, 3) - 0.0000352381 * Math.pow(heightPX, 2) - 0.0533567 * heightPX + 3.22582) * (1 + 0.14 * (degree - 102));
    }

    public double deltaY(double widthPX, double degree) {

        //initial regression from curve fit around 35 N
        double y = -14.14338 + 0.3289523 * widthPX - 0.0002647707 * Math.pow(widthPX, 2);

        //linear multiplier due to regression from 35 N line
        return y * (1 + 0.012 * (degree - 35));
    }

    public Point convert(double latitude, double longitude) {

        //initial linear approximation
        double yCord = NtoPx(latitude);
        double xCord = WtoPx(longitude);

        //add changes due to regression
        double yCordFirst = yCord + deltaY(xCord, latitude);
        double xCordFirst = xCord + deltaX(yCordFirst, longitude);

        //create change due to secondary guess and add to primary
        yCord += deltaY(xCordFirst, latitude);
        xCord += deltaX(yCordFirst, longitude);

        //return as Point
        return new Point((int) xCord, (int) yCord);
    }

    public double getLatitude(int zipCode) {

        File file = new File("src/ZipGPS.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");

                if (Integer.valueOf(parts[0]) == (zipCode)) {
                    return Double.valueOf(parts[1]);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    public double getLongitude(int zipCode) {

        File file = new File("src/ZipGPS.txt");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {

                String[] parts = line.split(",");

                if (Integer.valueOf(parts[0]) == zipCode) {
                    return Math.abs(Double.valueOf(parts[2].substring(1)));
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        error.setText("Entered Zip not in Database");
        return 0;
    }

    public void initGUI() throws IOException {

        JFrame frame = new JFrame("ZipGPS");
        frame.setBackground(Color.WHITE);

        File file = new File("src/utm-USA.jpg");


        final BufferedImage Image = ImageIO.read(file);


        JPanel panel = new JPanel() {

            @Override
            public void paint(Graphics g) {
                super.paint(g);
                g.drawImage(Image, 0, 0, null);

                if(currentZip != 0) {
                    g.setColor(Color.RED);

                    Point p = convert(getLatitude(currentZip), getLongitude(currentZip));
                    System.out.println(p);
                    g.fillOval((int) p.getX(), (int) p.getY(), 10, 10);
                }
            }
        };

        JPanel labelPanel = new JPanel();
        field = new JTextField(10);

        error = new JLabel();
        field.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                currentZip = Integer.valueOf(field.getText());
                error.setText("");
                panel.repaint();
            }
        });
        labelPanel.add(field);
        labelPanel.add(error);

        //set preferred size of panel so whole image is shown
        panel.setPreferredSize(new Dimension(Image.getWidth(), Image.getHeight()));

        frame.add(panel, BorderLayout.CENTER);
        frame.add(labelPanel, BorderLayout.NORTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //pack frame around preferred sizes
        frame.pack();
        frame.setResizable(false);

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
