import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.IIOException;

import javax.imageio.ImageIO;

public class TestImageFilter {

	public static void main(String[] args) throws Exception {
		
		BufferedImage image = null;
		String srcFileName = null;
		try {
			srcFileName = args[0];
			File srcFile = new File(srcFileName);
			image = ImageIO.read(srcFile);
		}
		catch (ArrayIndexOutOfBoundsException e) {
			System.out.println("Usage: java TestAll <image-file>");
			System.exit(1);
		}
		catch (IIOException e) {
			System.out.println("Error reading image file " + srcFileName + " !");
			System.exit(1);
		}

		System.out.println("Source image: " + srcFileName);

		startSequentialFilter(srcFileName, image);
		startParallelFilter(srcFileName, image);
	}

	private static void startSequentialFilter(String srcFileName, BufferedImage image) throws IOException {
		int w = image.getWidth();
		int h = image.getHeight();
		System.out.println("Image size is " + w + "x" + h);
		System.out.println();

		int[] src = image.getRGB(0, 0, w, h, null, 0, w);
		int[] dst = new int[src.length];

		System.out.println("Starting sequential image filter.");

		long startTime = System.currentTimeMillis();
		ImageFilter filter = new ImageFilter(src, dst, w, h);
		filter.apply();
		long endTime = System.currentTimeMillis();

		long tSequential = endTime - startTime;
		System.out.println("Sequential image filter took " + tSequential + " milliseconds.");

		BufferedImage dstImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		dstImage.setRGB(0, 0, w, h, dst, 0, w);

		String dstName = "Filtered" + srcFileName;
		File dstFile = new File(dstName);
		ImageIO.write(dstImage, "jpg", dstFile);

		System.out.println("Output image: " + dstName);
	}

	private static void startParallelFilter(String srcFileName, BufferedImage image) throws IOException {
		int w = image.getWidth();
		int h = image.getHeight();
		System.out.println("Image size is " + w + "x" + h);
		System.out.println();

		int[] src = image.getRGB(0, 0, w, h, null, 0, w);
		int[] dst = new int[src.length];

		System.out.println("Starting parallel image filter.");

		long startTime = System.currentTimeMillis();
		ParallelFJImageFilter filter = new ParallelFJImageFilter(src, dst, w, h);
		filter.apply(2);
		long endTime = System.currentTimeMillis();

		long dt = endTime - startTime;
		System.out.println("Parallel image filter took " + dt + " milliseconds.");

		BufferedImage dstImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		dstImage.setRGB(0, 0, w, h, dst, 0, w);

		String dstName = "Filtered_Parallel" + srcFileName;
		File dstFile = new File(dstName);
		ImageIO.write(dstImage, "jpg", dstFile);

		System.out.println("Output image: " + dstName);
	}


}
