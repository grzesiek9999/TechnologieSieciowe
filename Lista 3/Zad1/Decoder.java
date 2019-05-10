import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Decoder {

    ArrayList<String> frames;
    String line;
    int crcSize = 33;

    public Decoder(){
        frames = new ArrayList<>();
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

    public void parse(String frameSequence){

        ArrayList<Integer> indices = new ArrayList<>();
        int i = -1;
        int startIndex,endIndex;

        while((i = line.indexOf(frameSequence, i + 1)) != -1) {
           indices.add(i);
        }

        for(i = 0; i < indices.size(); i++){
            startIndex = indices.get(i)+frameSequence.length();
            endIndex = indices.get(i+1);
            frames.add(line.substring(startIndex,endIndex));
            i++;
        }
    }

    public boolean decode(){
        String data, tmp,dataWithCrc;
        for(int i = 0; i < frames.size(); i++){
            frames.set(i,deStuffBits(frames.get(i)));
            tmp = frames.get(i);
            data = tmp.substring(0,tmp.length()-crcSize);
            dataWithCrc = addCrc(data);
            if(!tmp.equals(dataWithCrc)){
                return false;
            }else{
                frames.set(i,data);
            }
        }
        return true;
    }

    public String deStuffBits(String frame){
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
                frame = frame.substring(0, i+1) + frame.substring(i+2);
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

