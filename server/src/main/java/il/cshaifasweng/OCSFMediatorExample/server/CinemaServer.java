package il.cshaifasweng.OCSFMediatorExample.server;

import il.cshaifasweng.OCSFMediatorExample.server.dataClasses.CheckInstances;

import java.io.IOException;


public class CinemaServer
{
	private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        server = new SimpleServer(3000);
        CheckInstances.main(args);
        System.out.println("server online");
        server.listen();
    }
}
