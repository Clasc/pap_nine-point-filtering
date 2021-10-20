import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.IIOException;

import javax.imageio.ImageIO;

public class TestImageFilter {
	private static MyLogger logger ;

	public static void main(String[] args) throws Exception {
		String outFile = getOutFileName(args);
		logger = new MyLogger(outFile);

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

		logger.log("Source image: " + srcFileName);
		logger.log("Image size is " + image.getWidth() + "x" + image.getHeight());

		FilterTestResult  seqResult = executeFilter(srcFileName, image);
		testParallelFilter(srcFileName, image, 2, seqResult);
		testParallelFilter(srcFileName, image, 4, seqResult);
		testParallelFilter(srcFileName, image, 8, seqResult);
		testParallelFilter(srcFileName, image, 16, seqResult);
		testParallelFilter(srcFileName, image, 32, seqResult);
		logger.writeLog();
	}

	private static String getOutFileName(String[] args) {
		String outFile = "out.txt";
		if (args.length < 2 || args[1] == null || "".equals(args[1])){
			System.out.println("no out filename provided, taking default: out.txt");
		} else {
			outFile = args[1];
		}
		return outFile;
	}

	private  static void testParallelFilter(String srcFileName, BufferedImage image, int threads, FilterTestResult seqResult){
		try {
			FilterTestResult parallelResult = executeFilter(srcFileName, image, threads);
			printComparison(parallelResult, seqResult, threads);
			logger.logLine();
		}catch (IOException e ){
			System.out.println(e.getStackTrace());
			System.out.println(e.getMessage());
		}
	}

	private static FilterTestResult executeFilter(String srcFileName, BufferedImage image) throws IOException {
		int w = image.getWidth();
		int h = image.getHeight();

		int[] src = image.getRGB(0, 0, w, h, null, 0, w);
		int[] dst = new int[src.length];

		logger.log("Starting sequential image filter.");

		long startTime = System.currentTimeMillis();
		ImageFilter filter = new ImageFilter(src, dst, w, h);
		filter.apply();
		long endTime = System.currentTimeMillis();

		long tSequential = endTime - startTime;
		logger.log("Sequential image filter took " + tSequential + " milliseconds.");

		BufferedImage dstImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		dstImage.setRGB(0, 0, w, h, dst, 0, w);

		String dstName = "Filtered" + srcFileName;
		File dstFile = new File(dstName);
		ImageIO.write(dstImage, "jpg", dstFile);

		logger.log("Output image: " + dstName);
		logger.logLine();
		return new FilterTestResult(dst, tSequential);
	}

	private static FilterTestResult executeFilter(String srcFileName, BufferedImage image, int threads) throws IOException {
		int w = image.getWidth();
		int h = image.getHeight();

		int[] src = image.getRGB(0, 0, w, h, null, 0, w);
		int[] dst = new int[src.length];

		logger.log("Starting parallel image filter with " + threads +" threads.");

		long startTime = System.currentTimeMillis();
		ParallelFJImageFilter filter = new ParallelFJImageFilter(src, dst, w, h);
		filter.apply(threads);
		long endTime = System.currentTimeMillis();

		long dt = endTime - startTime;
		logger.log("Parallel image filter using " + threads + " threads, took " + dt + " milliseconds.");

		BufferedImage dstImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		dstImage.setRGB(0, 0, w, h, dst, 0, w);

		String dstName = "Filtered_Parallel_"+threads+"threads_" + srcFileName;
		File dstFile = new File(dstName);
		ImageIO.write(dstImage, "jpg", dstFile);

		logger.log("Output image: " + dstName);
		return new FilterTestResult(dst, dt);
	}

	private static void printComparison(FilterTestResult out, FilterTestResult input, int threads){
		boolean areEqual = areEqual(out.dest, input.dest);
		logger.log("Result (threads: " + threads+ ") are the same as sequential filter result: " + areEqual);

		double speedUp = calcSpeedUp(input.time, out.time);
		logger.log("Relative Speedup: " + speedUp);
	}

	private static boolean areEqual(int [] img, int[] img2){
		logger.log("Comparing results....");

		if(img.length != img2.length){
			return false;
		}

		for (int i = 0; i < img.length ; i++) {
			if (img[i] != img2[i]) {
				return false;
			}
		}
		return true;
	}

	private static double calcSpeedUp(long original, long newTime){
		return ((double)(newTime - original)/(double) original) * -1;
	}
}
