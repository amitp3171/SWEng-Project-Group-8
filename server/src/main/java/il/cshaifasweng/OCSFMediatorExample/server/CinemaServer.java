package il.cshaifasweng.OCSFMediatorExample.server;

import java.io.IOException;


public class CinemaServer
{
	private static SimpleServer server;
    public static void main( String[] args ) throws IOException
    {
        server = new SimpleServer(3000);
        System.out.println("server online");
        server.listen();
    }
}
