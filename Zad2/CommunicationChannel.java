public class CommunicationChannel {

    public int [] array;
    int size;
    volatile boolean collision;
    int collisionNumber;


    public CommunicationChannel(int length){
        this.size = length;
        collision = false;
        collisionNumber = 0;
        array = new int [length];
        for(int i = 0; i < length; i++){
            array[i] = 0;
        }
    }

    public int getValue(int position){
        return array[position];
    }

    public  int getSize(){
        return size;
    }

    public synchronized void printArray(){
       // System.out.println();
        for(int i = 0; i < size; i++){
            System.out.print(array[i]+" ");
        }
        System.out.println();
    }

}
