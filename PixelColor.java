public class PixelColor {
    public float rt = 0;
    public float gt = 0;
    public float bt = 0;

    public int convertToSingleValue(){
        return (0xff000000) | (((int) rt / 9) << 16) | (((int) gt / 9) << 8) | (((int) bt / 9));
    }

    public void apply(int pixel) {
        rt += (float) ((pixel & 0x00ff0000) >> 16);
        gt += (float) ((pixel & 0x0000ff00) >> 8);
        bt += (float) ((pixel & 0x000000ff));
    }
}
