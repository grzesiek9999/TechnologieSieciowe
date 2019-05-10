import java.util.ArrayList;

public class Helper extends Thread {

    CommunicationChannel channel;
    ArrayList<Host> hosts;
    int delay;

    Helper(int delay){
        hosts = new ArrayList<>();
        this.delay = delay;
    }

    public void startSimulation(){
        for(int i = 0; i < hosts.size(); i++){
            hosts.get(i).observer = true;
            hosts.get(i).start();
        }
    }

    public double endSimulation(){
         double sum= 0;
        for (Host host : hosts) {
            try {
                host.join();
                System.out.println("Host name: "+host.name+" at position: "+host.position+" had "+host.sum+" collisions");
                sum = sum + host.sum;
            } catch (InterruptedException e) {
                System.out.println("Cannot join threads");
            }
        }
        System.out.println("There were on average: "+sum/hosts.size()+" collisions");
        return sum/hosts.size();
    }

    public void addHost(int name, int position,int iterations){
        hosts.add(new Host(name, position, channel,delay,iterations));
    }

    public void createChannel(int length){
        this.channel = new CommunicationChannel(length);
    }

    public double testChannel(int iter){
        double sum =0;
        int iterations = 10;
        for(int i = 0; i< iter; i++){
            System.out.println(i+"  "+sum);
            resetChannel();
            addHost(1,0,iterations);
            addHost(2,29,iterations);
            addHost(3,15,iterations);
            startSimulation();
            sum = sum + endSimulation();
            hosts.clear();
        }
        return sum/iter;
    }

    void resetChannel(){
        for(int i = 0; i < channel.size; i++){
            channel.array[i] = 0;
            channel.collision = false;
            channel.collisionNumber = 0;
        }
    }
}
