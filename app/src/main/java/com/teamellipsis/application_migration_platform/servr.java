package com.teamellipsis.application_migration_platform;

import android.os.Environment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.teamellipsis.dynamic.DynamicApp;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.sql.SQLOutput;
import java.util.*;


public class servr extends WebSocketServer {

    private static int TCP_PORT = 4444;
    private static String serverstate;
    private static Object[]  newserverstate;
    private String send_data;
    private static DynamicApp app;
    private Set<WebSocket> conns;
    private static boolean serveropen;

    public servr(DynamicApp app) {
        super(new InetSocketAddress(TCP_PORT));
        conns = new HashSet<>();
        serverstate="not_set";
        this.app=app;
        newserverstate=app.saveState();

    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        conns.add(conn);
        System.out.println("New connection from " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
        Gson gson=new Gson();
        String json = gson.toJson(app.saveState());
        conn.send(json);


//        if(serverstate.equals("not_set")){
//            System.out.println("not_set state");
//            System.out.println("state is setted");
//            conn.send("not_set state");
//            Gson gson=new Gson();
//            String json=gson.toJson(app.saveState());
//            conn.send(json);
//        }else{
//            System.out.println("state is setted");
//            Gson gson=new Gson();
//            String json=gson.toJson(app.saveState());
//            conn.send(json);
//        }
        this.serveropen=true;
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        conns.remove(conn);
//        conns.clear();
        System.out.println("Closed connection to " + conn.getRemoteSocketAddress().getAddress().getHostAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        System.out.println("Message from client: " + message);
        Gson gson=new Gson();
        Method method;
        Type type= new TypeToken<HashMap<String,String>>(){}.getType();
        HashMap<String, String> args= gson.fromJson(message,type);
//        System.out.println(args.get("operation"));
//        if(args.get("operation").equals("getdata")){
//            String json=gson.toJson(app.saveState());
//            conn.send(json);
//        }
//        else if(args.get("operation").equals("")){
//            String json=gson.toJson(app.saveState());
//            conn.send(json);
//        }
//
//        if(args.get("operation").equals("saveobject")){
//            try {
//                saveobject();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        else {
            try {
                if(args.get("method").equals("saveobject")){
                    System.out.println("save object");
                    saveobject();
                    conns.remove(conn);

                }else {
                    method = this.app.getClass().getMethod(Objects.requireNonNull(args.get("method")), HashMap.class);
                    method.invoke(this.app, args);
                    String json = gson.toJson(app.saveState());
                    conn.send(json);
                }

            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }

//        operations operation= gson.fromJson(message, operations.class);
//        String[] arr= new String[]{};
//        if(operation.operation.equals("addtodo")){
//            System.out.println(operation.title);
//            arr= new String[]{"0", operation.title, operation.description, operation.date};
//            this.app.execute(arr);
//            String json=gson.toJson(app.saveState());
//            conn.send(json);
//        }
//        if(operation.operation.equals("getdata")){
//            String json=gson.toJson(app.saveState());
//            conn.send(json);
//        }
//        if(operation.operation.equals("update_title")){
//            System.out.println(operation.title);
//            arr= new String[]{"4", operation.id, operation.title};
//            this.app.execute(arr);
//            String json=gson.toJson(app.saveState());
//            conn.send(json);
        }


//        TypeToken<ArrayList<Task>> token = new TypeToken<ArrayList<Task>>() {};
//        ArrayList<Task> animals = gson.fromJson(message, token.getType());
//        String json=gson.toJson(animals);
//        System.out.println(json);

//
//    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        //ex.printStackTrace();
        if (conn != null) {
            conns.remove(conn);
            // do some thing if required
        }

    }

    @Override
    public void onStart() {

    }

    public void saveobject() throws IOException {
        File folder1 = Environment.getExternalStorageDirectory();
        File myFile1 = new File(AppManagementActivity.AppPath+"/testobj.ser");
        System.out.println("saved app ..............................................");
        FileOutputStream fileOut = new FileOutputStream(myFile1);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(app);
        out.close();
        fileOut.close();
//        conns.clear();

    }
    public static boolean getserverstate(){
        return serveropen;
    }
    public static void appsave() throws IOException{
        File myFile1 = new File(AppManagementActivity.AppPath+"/app.ser");
        System.out.println("saved app ..............................................");
        FileOutputStream fileOut = new FileOutputStream(myFile1);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(app);
        out.close();
        fileOut.close();

    }
}