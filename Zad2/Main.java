public class Main {

    public static void main(String[] args) {
        int propagationDelay = 10;
        int iterations = 2;
        Helper helper = new Helper(propagationDelay);
        helper.createChannel(50);

        helper.addHost(1,0,iterations);
        helper.addHost(2,20,iterations);
        helper.addHost(3,40,iterations);


        helper.startSimulation();
        helper.endSimulation();

    }
}
