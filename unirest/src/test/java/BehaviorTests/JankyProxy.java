/**
 * The MIT License
 *
 * Copyright for portions of unirest-java are held by Kong Inc (c) 2013.
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package BehaviorTests;

import java.io.*;
import java.net.*;
/**
 *
 * @author jcgonzalez.com
 *
 */
public class JankyProxy {
    public static ThreadProxy thread;
    public static void runServer(String host, int remoteport, int localport) {
        try {
            // and the local port that we listen for connections on
            // Print a start-up message
            System.out.println("Starting proxy for " + host + ":" + remoteport
                    + " on port " + localport);
            ServerSocket server = new ServerSocket(localport);
            thread = new ThreadProxy(server, host, remoteport);
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Usage: java ProxyMultiThread "
                    + "<host> <remoteport> <localport>");
        }
    }

    public static boolean wasUsed(){
        return thread.wasUsedForClientToServer;
    }

    public static void shutdown(){
        if(thread != null){
            try {
                thread.shutdown();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
/**
 * Handles a socket connection to the proxy server from the client and uses 2
 * threads to proxy between server and client
 *
 * @author jcgonzalez.com
 *
 */
class ThreadProxy extends Thread {
    private Socket sClient;
    private ServerSocket socket;
    private final String SERVER_URL;
    private final int SERVER_PORT;
    public boolean wasUsedForClientToServer;

    ThreadProxy(ServerSocket socket, String ServerUrl, int ServerPort) {
        this.SERVER_URL = ServerUrl;
        this.SERVER_PORT = ServerPort;
        this.socket = socket;
        this.start();
    }
    @Override
    public void run() {
        try {
            sClient = socket.accept();
            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final InputStream inFromClient = sClient.getInputStream();
            final OutputStream outToClient = sClient.getOutputStream();
            Socket client = null, server = null;
            // connects a socket to the server
            try {
                server = new Socket(SERVER_URL, SERVER_PORT);
            } catch (IOException e) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(outToClient));
                out.flush();
                System.out.println("SERVER_URL = " + SERVER_URL + " : " + SERVER_PORT);
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            final InputStream inFromServer = server.getInputStream();
            final OutputStream outToServer = server.getOutputStream();
            // a new thread for uploading to the server
            new Thread(() -> {
                int bytes_read;
                try {
                    while ((bytes_read = inFromClient.read(request)) != -1) {
                        outToServer.write(request, 0, bytes_read);
                        outToServer.flush();
                        wasUsedForClientToServer = true;
                    }
                } catch (IOException e) {
                }
                try {
                    outToServer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
            // current thread manages streams from server to client (DOWNLOAD)
            int bytes_read;
            try {
                while ((bytes_read = inFromServer.read(reply)) != -1) {
                    outToClient.write(reply, 0, bytes_read);
                    outToClient.flush();
                    //TODO CREATE YOUR LOGIC HERE
                }
            } catch (IOException e) {
               // e.printStackTrace();
            } finally {
                try {
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outToClient.close();
            sClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        interrupt();
    }
}