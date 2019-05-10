import java.io.*;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Encoder {

    ArrayList <String> frames;
    String line;

    public Encoder(){
        frames = new ArrayList<>();
    }

    public void parse(int frameSize){
        int i= 0;

        while (i < line.length()) {
            frames.add(line.substring(i, Math.min(i + frameSize,line.length())));
		// System.out.println("i="+i+", x="+line.substring(i, Math.min(i + frameSize,line.length())));
            i += frameSize;
        }
    }

    public void read(String filename){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            line = reader.readLine();

            if(line==null){
                System.out.println("Source file is empty");
                return;
            }else if (!line.matches("^[01]+$")) {
                System.out.println("Source file is corrupted");
                return;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Error reading from specified file");
        } catch (IOException e) {
            System.out.println("Cannot read line");
        }
    }

    public void encode(String frameSequence){
        for(int i = 0; i < frames.size(); i++){
            frames.set(i,addCrc(frames.get(i)));
            frames.set(i,stuffBits(frames.get(i)));
            frames.set(i,encloseFrames(frames.get(i),frameSequence));
        }
    }

    public String encloseFrames(String frame, String frameSequence){
        return frameSequence+frame+frameSequence;
    }

    public String stuffBits(String frame){
        int i = 0, count = 0;
        char c;
        while(i < frame.length()){
            c = frame.charAt(i);
            if(c == '1'){
                count++;
            }else{
                count = 0;
            }
            if(count == 5){
                count = 0;
                frame = frame.substring(0, i+1) + "0" + frame.substring(i+1, frame.length());
                i++;
            }
            i++;
        }
        return frame;
    }

    public String addCrc(String data){

        byte[] bytes = data.getBytes();
        Checksum checksum = new CRC32();
        checksum.update(bytes, 0, bytes.length);
        String crc = Long.toBinaryString(checksum.getValue());

        while(crc.length() != 32){
            crc = crc + "0";
        }
        crc = "0" + crc;

        return data+crc;
    }

    public void save(String filename){

        try {
            PrintWriter writer = new PrintWriter(filename);
            String line = String.join("", frames);
            writer.println(line);
            writer.flush();
            writer.close();

        } catch (FileNotFoundException e) {
            System.out.println("Error saving to file");
        }
    }


    public void printFrames(){
        for (String frame: frames) {
            System.out.println(frame);
        }
    }

}
