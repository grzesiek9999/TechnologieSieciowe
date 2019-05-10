import java.util.Random;
import static java.lang.Math.pow;

public class Host extends Thread {

    int position;
    int name;
    Thread sender;
    CommunicationChannel channel;
    boolean observer;
    boolean sentSuccesfully;
    int propagationDelay;
    int propagationTime;
    int collisionValue;
    int iterations;
    int sum;
    Thread collisionThread;

    public Host(int name, int position, CommunicationChannel channel,int propagationDelay,int iterations){
        this.name = name;
        this.position = position;
        this.channel = channel;
        sender = null;
        observer = false;
        sentSuccesfully = true;
        this.propagationDelay = propagationDelay;
        propagationTime = channel.size*propagationDelay *2;
        collisionValue = 9;
        this.iterations = iterations;
        sum = 0;
    }

   public void run(){
        while(iterations>0){
            if(channel.collisionNumber <= 16) {
                chooseAction();
            }else{
                System.out.println("Too many collisions");
                return;
            }
        }
       try {
            if(collisionThread!=null && collisionThread.isAlive()){
                collisionThread.join();
            }
            if(sender != null && sender.isAlive()) {
                sender.join();
            }
       } catch (InterruptedException e) {
           e.printStackTrace();
       }
       //System.out.println();
        //System.out.println("Closing host with name "+name);
   }

   public void chooseAction(){
      int value = channel.getValue(position);
      if(value == 0 && !channel.collision){
          if(sender == null || !sender.isAlive()){
              send();
          }
      }else if(value == collisionValue){
          if(!sentSuccesfully) {
              System.out.println();
              System.out.println("Host at position: "+position + " detected a collision");
              resolveCollision();
              channel.collision = false;
          }
      }
   }

    public void send(){
        iterations--;
        sender = new Thread() {

           public void run() {
               Random random = new Random();
               int min = 1, max = 5;
               int rand = random.nextInt(max + 1 - min) + min;

               try {
                   Thread.currentThread().sleep(rand * 1000);
               } catch (InterruptedException e) {
                   //System.out.println("Sending thread cannot wait");
                   return;
               }
               int value = channel.getValue(position);
               if(value != 0 || channel.collision){
                   sentSuccesfully = true;
                   return;
               }
               System.out.println();
               System.out.println(iterations+" Sending from host at position: "+position);

               if(observer){
                   sentSuccesfully = sendSignal(position,name);
               }else {
                   sentSuccesfully = sendSignal(position,name);
               }
               if(sentSuccesfully){
                   channel.collision = false;
                   channel.collisionNumber = 0;
               }else{
                   sum++;
               }
           }
       };

       sender.start();


    }

    void resolveCollision(){
        Random random = new Random();
        int min = 1,max;
        int collisions = channel.collisionNumber;

        if(collisions <= 10){
            max = (int)pow(2,collisions);
        }else{
            max = 1024;
        }

        int rand = random.nextInt(max + 1 - min) + min;
        System.out.println();
        System.out.println("Host at position: "+position+" resolving collision, waiting "+rand+ " * "+ propagationTime);
        try {
            sleep(rand * propagationTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        channel.collision = false;
        if(iterations > 0) {
            send();
        }
    }

    boolean sendSignal(int position,int name){
        int k,j;
        int min = 0;
        int max = channel.getSize()-1;
        channel.printArray();
        channel.array[position] = name;
        channel.printArray();

        for(int x =0; x < 2; x++) {
            j = position+1;
            k = position-1;

            while (j <= max || k >= min) {
                try {
                    sleep(propagationDelay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (j <= max) {
                    if (channel.array[j] == 0 || channel.array[j] == name) {
                        channel.array[j] = name;
                        j++;
                    }else{
                        if(!channel.collision) {
                            channel.collision = true;
                            channel.collisionNumber++;
                            //System.out.println();
                            //System.out.println("Signal: "+name+" sees a collision");
                            channel.printArray();

                            class Collision implements Runnable {
                                int j;
                                Collision(int j) { this.j = j; }
                                public void run() {
                                    propagateCollisionSignal(j,collisionValue);
                                }
                            }
                            collisionThread = new Thread(new Collision(j));
                            collisionThread.start();

                        }
                            return false;

                    }
                }
                if (k >= min) {
                    if (channel.array[k] == 0 || channel.array[k] == name) {
                        channel.array[k] = name;
                        k--;
                    }else{
                        if(!channel.collision) {
                            channel.collision = true;
                            channel.collisionNumber++;
                            //System.out.println();
                            //System.out.println("Signal: "+name+" sees a collision");
                            channel.printArray();
                            class Collision implements Runnable {
                                int k;
                                Collision(int k) { this.k = k; }
                                public void run() {
                                    propagateCollisionSignal(k,collisionValue);
                                }
                            }
                            collisionThread = new Thread(new Collision(k));
                            collisionThread.start();

                        }
                       return false;
                    }
                }
                channel.printArray();
            }
        }

       removeSignal(position);

        return true;
    }
    public void propagateCollisionSignal(int position,int value){
        int j = position+1;
        int k = position-1;
        int min = 0;
        int max = channel.getSize()-1;
        channel.array[position] = value;
        channel.printArray();
        while (j <= max || k >= min) {
            try {
                sleep(propagationDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (j <= max) {
                channel.array[j] = value;
                j++;
            }
            if (k >= min) {
                channel.array[k] = value;
                k--;
            }
            channel.printArray();
        }
        removeSignal(position);
    }

    void removeSignal(int position){
        int j = position+1;
        int k = position-1;
        int min = 0;
        int max = channel.getSize()-1;
        int name = channel.array[position];
        channel.array[position] = 0;
        channel.printArray();
        while (j <= max || k >= min) {
            if (j <= max) {
                if (channel.array[j] == name) {
                    channel.array[j] = 0;
                    j++;
                }
            }
            if (k >= min) {
                if (channel.array[k] == name) {
                    channel.array[k] = 0;
                    k--;
                }
            }
            channel.printArray();
        }
    }


}
