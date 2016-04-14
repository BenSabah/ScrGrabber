import java.awt.AWTException;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ScrGrabber {
	static String PATH;
	private static final String USAGE_MSG = "USAGE - type the output dir as an argument.";

	public static void main(String[] args) throws AWTException, IOException, InterruptedException {
		try {
			PATH = args[0];
		} catch (Exception e) {
			System.out.println(USAGE_MSG);
			System.exit(1);
		}

		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = gd.getDisplayMode().getWidth();
		int height = gd.getDisplayMode().getHeight();
		Rectangle rect = new Rectangle(0, 0, width, height);
		Robot robot = new Robot();

		BufferedImage screenshot;
		PointerInfo pointer;
		long sumSize = 0;
		while (sumSize <= 1073741824) {
			pointer = MouseInfo.getPointerInfo();
			int x = (int) pointer.getLocation().getX();
			int y = (int) pointer.getLocation().getY();
			screenshot = robot.createScreenCapture(rect);
			markCursor(screenshot, x, y, 4, 20);
			File f = new File(PATH, "screenshot_" + System.currentTimeMillis() + ".PNG");
			ImageIO.write(screenshot, "PNG", f);
			sumSize += f.length();
			System.out.println(sumSize + " - " + f);
			Thread.sleep(2500);
		}
	}

	static void markCursor(BufferedImage screenshot, int x, int y, int thickness, int length) {
		int xy = ((x & 0xfff) << 12) | (y & 0xfff);
		screenshot.setRGB(0, 0, xy);

		int pixel;
		for (int j = y - length; j < y + length; j++) {
			for (int i = x - thickness; i < x + thickness; i++) {
				try {
					pixel = screenshot.getRGB(i, j);
					pixel = reverseColor(pixel);
					screenshot.setRGB(i, j, pixel);

				} catch (Exception e) {
				}
			}
		}

		for (int j = y - thickness; j < y + thickness; j++) {
			for (int i = x - length; i < x + length; i++) {
				try {
					pixel = screenshot.getRGB(i, j);
					pixel = reverseColor(pixel);
					screenshot.setRGB(i, j, pixel);

				} catch (Exception e) {
				}
			}
		}
	}

	private static int reverseColor(int pixel) {
		int a = 255;
		int r = 255 - ((pixel >> 16) & 0xff);
		int g = 255 - ((pixel >> 8) & 0xff);
		int b = 255 - (pixel & 0xff);

		return (a << 24) | (r << 16) | (g << 8) | b;
	}
}
