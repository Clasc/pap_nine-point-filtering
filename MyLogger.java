import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyLogger {
    private List<String> logBuffer;
    private String outFile;
    MyLogger(String filename){
        logBuffer = new ArrayList<String>();
        outFile = filename;
    }

    public List<String> getLog(){
        return logBuffer;
    }

    public void log(String line){
        logBuffer.add(line + "\n");
    }

    public void logLine(){
        logBuffer.add("\n");
    }

    public void writeLog(){
        try {
            new File(outFile).createNewFile();
        } catch (IOException e) {
            System.out.println("An error occurred creating an output file.");
            e.printStackTrace();
        }

        try {
            FileWriter writer = new FileWriter(outFile);
            for (String line : logBuffer) {
                writer.write(line);
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("An error occurred when writing the log to file: " + outFile);
            e.printStackTrace();
        }
    }
}
