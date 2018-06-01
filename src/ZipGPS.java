import java.awt.*;

public class ZipGPS {

    double DpxDN = 903 / (50 - 21);
    double DpxDW = 1205 / (130 - 63);


    public ZipGPS() {
    }

    ;

    public double deltaX(int heightPX) {

        return (-0.0638279 * heightPX);
    }

    public double deltaY(int widthPX) {

        double y = -0.000324907 * Math.pow(widthPX, 2) + 0.382076 * widthPX;

        return y;
    }

    public Point convert(double latitude, double longitude) {

        double width = DpxDN * (latitude - 21) + deltaX((int) (DpxDN * (latitude - 21)));
        double height = DpxDW * (longitude - 67) + deltaX((int) (DpxDW * (longitude - 67)));

        return new Point((int) width, (int) height);
    }


    public static void main(String[] args) {

        ZipGPS zippy = new ZipGPS();

        System.out.println(zippy.convert(35, 102));
    }
}
