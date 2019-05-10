public class Main {

    public static void main(String[] args) {

        int frameSize = 4;
        String frameSequence = "01111110";

        Encoder encoder = new Encoder();
        encoder.read("Z.txt");
        encoder.parse(frameSize);
        encoder.encode(frameSequence);
        encoder.save("W.txt");

        Decoder decoder = new Decoder();
        decoder.read("W.txt");
        decoder.parse(frameSequence);
        if(decoder.decode()) {
            decoder.save("Z_Decoded.txt");
	    System.out.println("Done)");
        }else{
            System.out.println("File was damaged");
        }

    }
}
