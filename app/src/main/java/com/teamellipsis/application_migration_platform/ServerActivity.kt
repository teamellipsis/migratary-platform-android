package com.teamellipsis.application_migration_platform

import android.content.DialogInterface
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.TextView
import java.io.*
import java.net.*
import java.net.NetworkInterface.getNetworkInterfaces



class ServerActivity : AppCompatActivity() {
    private var ip: TextView? = null
    private var port: TextView? = null
    private var status: TextView? = null
    private var appPath: TextView? = null
    private var appName: TextView? = null
    private var currentFile : File? = null
    private var serverSocket: ServerSocket? = null
    private var alert11: Array<AlertDialog>? = null
    private var windowexit: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_server)
        val intent = this.intent
        val apppath = intent.getStringExtra("APP_PATH")

        ip = findViewById(R.id.ip)
        port = findViewById(R.id.port)
        status = findViewById(R.id.status)
        appPath = findViewById(R.id.app_path)
        appName= findViewById(R.id.folder_name)
        appPath!!.setText(apppath)

        currentFile = File(apppath)
        appName!!.setText(currentFile!!.name)
        createserver()
        windowexit = false

    }

    fun createserver() {

        val handler = Handler()
        val builder1 = AlertDialog.Builder(this)
        val alert11 = arrayOfNulls<AlertDialog>(1)

        val thread = Thread(Runnable {
            var socket: Socket? = null
                try {
                    serverSocket = ServerSocket(0)
                    val localPort = serverSocket!!.getLocalPort()
                    handler.post {

                        ip!!.setText(getipv4())
                        port!!.setText(Integer.toString(localPort))
                        status !!.setText("waiting for clients")
                    }

                    while (serverSocket!=null) {
                        println("waiting ...................")
                        socket = serverSocket!!.accept()
                        handler.post { status!!.setText("client is connected") }
                        println("accept client connection.")

                        val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                        val message1 = input.readLine()
                        println(message1)
                        val file1 = Environment.getExternalStorageDirectory()
//                        val file = File(file1, "/socket1.txt")
                        val file  = currentFile
                        val length = file!!.length()

                        if (message1 == "get file") {
                            handler.post { status!!.setText("sending file..") }
                            val bytes = ByteArray(length.toInt())
                            val fin = FileInputStream(file)
                            val bis1 = BufferedInputStream(fin)
                            bis1.read(bytes, 0, bytes.size)

                            val dOut = DataOutputStream(socket.getOutputStream())
                            dOut.writeInt(bytes.size) // write length of the message
                            dOut.write(bytes)
                            println("Done.")

                            socket.close()
                            serverSocket!!.close()
                            break
                        } else {
                            val output = PrintWriter(socket.getOutputStream())
                            output.println(Integer.toString(file.length().toInt()))
                            output.flush()
                            println("Done.")

                            val input11 = BufferedReader(InputStreamReader(socket.getInputStream()))
                            val message = input11.readLine()
                            println(message)

                            val filename = file.name
                            output.println(filename)
                            output.flush()
                            println("Done.")

                            socket.close()

                        }


                    }
                } catch (e: Throwable) {
                    e.printStackTrace()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()

                } finally {
                    handler.post {
                        builder1.setMessage("file is sent to the destination")
                        builder1.setCancelable(true)
                        builder1.setPositiveButton(
                            "Ok"
                        ) { dialog, id ->
                            finish()
                            dialog.cancel()
                        }
                        if (alert11[0] != null) {
                            alert11[0]!!.dismiss()
                        }
                        if (!windowexit) {
                            alert11[0] = builder1.create()
                            alert11[0]!!.show()
                        }

                    }
                }
            })
        thread.start()
    }

    fun getMobileIP(): String? {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement() as NetworkInterface
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress.toString()
                    }
                }
            }
        } catch (ex: SocketException) {
            Log.e("sa", "Exception in Get IP Address: " + ex.toString())
        }

        return null
    }

    fun getipv4(): String {
        var ip = ""
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback || !iface.isUp)
                    continue

                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()

                    // *EDIT*
                    if (addr is Inet6Address) continue

                    ip = addr.hostAddress
                    println(ip)
                }
            }
        } catch (e: SocketException) {
            throw RuntimeException(e)
        }

        return ip
    }

    override fun onBackPressed() {

        val builder1 = AlertDialog.Builder(this)
        builder1.setMessage("Do you want to cancel sharing..")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Yes"
        ) { dialog, id ->
            try {
                windowexit = true
                serverSocket!!.close()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
//                if (alert11!![0] != null) {
//                    alert11!![0].dismiss()
//                }
                finish()
                dialog.cancel()
            }
        }
        builder1.setNegativeButton(
            "No"
        ) { dialog, id -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }
}
