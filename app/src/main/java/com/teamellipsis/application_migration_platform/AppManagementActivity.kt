package com.teamellipsis.application_migration_platform

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_app_management.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import android.os.AsyncTask
import android.os.Environment
import android.os.Handler
import android.support.design.widget.TextInputLayout
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import com.teamellipsis.dynamic.DynamicApp
import dalvik.system.DexClassLoader
import java.io.*
import java.net.Socket
import java.net.UnknownHostException

class AppManagementActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var listItems: MutableList<String> = mutableListOf<String>()
    private var listFiles: MutableList<File> = mutableListOf<File>()
    lateinit var fileSystem: FileSystem
    lateinit var context: Context
    lateinit var obj : DynamicApp
    lateinit var st : ServerThred
    lateinit var appConfig: AppConfig
    var PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0
    lateinit var messagefilelength: String
    lateinit var filename: String
    lateinit var output: PrintWriter
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_management)
        getPermission()
        appConfig = AppConfig(applicationContext)
        if (appConfig.get(AppConstant.KEY_WORKING_DIR).isEmpty()) {
            Log.i("App-Migratory-Platform", "first_time ")
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
//            appConfig.set(AppConstant.KEY_WORKING_DIR, "ttt")
        }else{
            Log.i("App-Migratory-Platform", appConfig.get(AppConstant.KEY_WORKING_DIR))

        }
        listView.onItemClickListener = this

        fileSystem = FileSystem(applicationContext)
//        var appConfig = AppConfig(applicationContext)
        context = this
        AppManagementActivity.AppPath=appConfig.get(AppConstant.KEY_WORKING_DIR)
        val appsDir = File(appConfig.get(AppConstant.KEY_WORKING_DIR))
        if (appsDir.exists()) {
            Log.i("App-Migratory-Platform", appsDir.listFiles().size.toString())

            for (file in appsDir.listFiles()) {
                listItems.add(file.name)
                listFiles.add(file)
            }

            if (listItems.isNotEmpty()) {
        listView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems)
//                listView.adapter = ArrayAdapter<String>(this, R.layout.app_list_item, R.id.listItemText, listItems)
            }
        }



    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_appmanagement, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_changedir -> {
                val intent = Intent(applicationContext, MainActivity::class.java)
                startActivity(intent)
//                val intent = Intent(applicationContext, AppManagementActivity::class.java)
//                startActivity(intent)
//                finish()
                true
            }
            R.id.getapp -> {
//                finish()
                showAlertWithTextInputLayout()
                true
            }
            R.id.wifidirect -> {
                finish()

                true
            }
            R.id.exit -> {
                finish()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("App-Migratory-Platform", appConfig.get(AppConstant.KEY_WORKING_DIR))
        AppManagementActivity.AppPath=appConfig.get(AppConstant.KEY_WORKING_DIR)
        appConfig = AppConfig(applicationContext)
        if (appConfig.get(AppConstant.KEY_WORKING_DIR).isEmpty()) {
            Log.i("App-Migratory-Platform", "first_time ")
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
//            appConfig.set(AppConstant.KEY_WORKING_DIR, "ttt")
        }else{
            Log.i("App-Migratory-Platform", appConfig.get(AppConstant.KEY_WORKING_DIR))

        }
        listView.onItemClickListener = this

        fileSystem = FileSystem(applicationContext)
//        var appConfig = AppConfig(applicationContext)
        context = this

        listItems.clear()
        listFiles.clear()
        val appsDir = File(appConfig.get(AppConstant.KEY_WORKING_DIR))
        if (appsDir.exists()) {
            Log.i("App-Migratory-Platform", appsDir.listFiles().size.toString())

            for (file in appsDir.listFiles()) {
                listItems.add(file.name)
                listFiles.add(file)
            }

            if (listItems.isNotEmpty()) {
                listView.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems)
//                listView.adapter = ArrayAdapter<String>(this, R.layout.app_list_item, R.id.listItemText, listItems)
            }
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//        openApp(listFiles[position])

        openDialog(listFiles[position])
    }

    fun openDialog(appPath: File) {
        val alertDialog: AlertDialog? = this?.let {
            val builder = AlertDialog.Builder(it)
            builder.apply {
                setTitle(appPath.name)
                setItems(arrayOf(
                    AppDialogOptions.Open.name,
                    AppDialogOptions.Package.name,
                    AppDialogOptions.Send.name,
                    AppDialogOptions.Reset.name,
                    AppDialogOptions.Delete.name,
                    AppDialogOptions.Direct_send.name
                ),
                    DialogInterface.OnClickListener { dialog, which ->
                        Log.i("App-Migratory-Platform", which.toString())
                        when (which) {
                            AppDialogOptions.Open.ordinal -> {
                                openApp(appPath)
                            }
                            AppDialogOptions.Package.ordinal -> {
                                val packagesDir = File(appPath.parent)
                                packagesDir.mkdirs()
                                fileSystem.zipDir(appPath, File(packagesDir, "toBeSent/"+appPath.name + ".zip"))
                            }
                            AppDialogOptions.Send.ordinal -> {
                                val file = File(appPath.parent, appPath.name + ".zip")
                                val fileUri = FileProvider.getUriForFile(
                                    context,
                                    applicationContext.packageName + ".provider",
                                    file
                                )
//
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                    putExtra(Intent.EXTRA_STREAM, fileUri)
                                    type = "application/zip"
                                }

                                if (sendIntent.resolveActivity(packageManager) != null) {
                                    startActivity(sendIntent)
                                }
                            }
                            AppDialogOptions.Reset.ordinal -> {

                            }
                            AppDialogOptions.Delete.ordinal -> {

                            }
                            AppDialogOptions.Direct_send.ordinal -> {
                                val intent = Intent(applicationContext, ServerActivity::class.java).apply {
                                    putExtra("APP_PATH",appPath.absolutePath )
                                }
                                startActivity(intent)
                            }
                            else -> { // Note the block
                                print("x is neither 1 nor 2")
                            }
                        }
                    })
//                setPositiveButton("ok",
//                    DialogInterface.OnClickListener { dialog, id ->
//                        // User clicked OK button
//                    })
//                setNegativeButton("cancel",
//                    DialogInterface.OnClickListener { dialog, id ->
//                        // User cancelled the dialog
//                    })
            }
            builder.create()
        }
        alertDialog?.show()
    }

    enum class AppDialogOptions {
        Open, Package, Send, Reset, Delete ,Direct_send
    }

    fun openApp(filePath: File) {
        var files = filePath.listFiles()
        var avalablestateobject=false
        var clsfilepath=""
        var dexpath=""
        for (f in files) {
            val fullPath = f.absolutePath
            val dot = fullPath.lastIndexOf(".")
            val ext = fullPath.substring(dot + 1)

            Log.i("extension", ext)
            if (ext.equals("ser")) {
                avalablestateobject=true
            }
            else if(ext.equals("tf")){
                clsfilepath=fullPath
            }
            else if(ext.equals("dex")){
                dexpath=fullPath
            }
        }
        Log.i("extension", avalablestateobject.toString())
        if(avalablestateobject){
            obj=deserialize(filePath.absolutePath)
            Log.i("foundclasses", getclasses(clsfilepath))
        }else{
            obj=  loadobject(dexpath,getclasses(clsfilepath))
        }

       // getclasses()

        Toast.makeText(applicationContext,"Object loaded sucessfully", Toast.LENGTH_LONG).show()
        AppManagementActivity.serverthread = ServerThred("state data",obj)

        AppManagementActivity.serverthread!!.run()

//        val intent = Intent(applicationContext, AgentActivity::class.java).apply {
//            putExtra("WEB_VIEW_URL", webViewUrl)
//        }
//        startActivity(intent)
        val intent = Intent(applicationContext, WebviewActivity::class.java).apply { putExtra("WEB_VIEW_URL_FILE", filePath.absolutePath) }
        startActivity(intent)

    }

    fun checkStatusCodeAndResend(statusCode: Long?) {

    }
    fun changeDir(view: View) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        startActivity(intent)
    }

    fun isAppUp(view: View) {
        val url = URL("http://localhost:3000/__ping")
        HttpAsyncTask().execute(url)

//        val url = URL("http://localhost:3000")
//        val urlConnection = url.openConnection() as HttpURLConnection
//        try {
//            val bufferedInputStream = BufferedInputStream(urlConnection.getInputStream())
//            val BUFFER_SIZE = 2048
//            val data = ByteArray(BUFFER_SIZE)
//            var count: Int
//            while ({ count = bufferedInputStream.read(data, 0, BUFFER_SIZE);count }() != -1) {
//                Log.i("App-Migratory-Platform", data.toString())
//            }
//            bufferedInputStream.close()
////            readStream(bufferedInputStream)
//        } catch (e: Exception) {
//            Log.i("App-Migratory-Platform", e.message)
//            e.printStackTrace()
//        } finally {
//            urlConnection.disconnect()
//        }
    }

//    external fun startNodeWithArguments(vararg argv: String): Integer
//
//    companion object {
//        init {
//            try {
//                System.loadLibrary("native-lib")
//                System.loadLibrary("node")
//            } catch (error: UnsatisfiedLinkError) {
//                error.printStackTrace()
//            }
//        }
//    }

    private inner class HttpAsyncTask : AsyncTask<URL, Int, Long>() {
        override fun doInBackground(vararg urls: URL): Long? {
            var code: Int? = null
            val url = URL("http://localhost:3000/__ping")
            val urlConnection = url.openConnection() as HttpURLConnection
//            urlConnection.connectTimeout = 5000
            try {
//                Log.i("App-Migratory-Platform", "doInBackground")

                code = urlConnection.responseCode
//                Log.i("App-Migratory-Platform", code.toString())

//                val bufferedInputStream = BufferedInputStream(urlConnection.inputStream)
//                val BUFFER_SIZE = 2048
//                val data = ByteArray(BUFFER_SIZE)
//                var count: Int
//                while ({ count = bufferedInputStream.read(data, 0, BUFFER_SIZE);count }() != -1) {
//                    Log.i("App-Migratory-Platform", data.toString())
//                }
//                bufferedInputStream.close()
//            readStream(bufferedInputStream)
            } catch (e: Exception) {
//                Log.i("App-Migratory-Platform", e.message)
                e.printStackTrace()
            } finally {
                urlConnection.disconnect()
            }

            return code?.toLong()
        }

        override fun onProgressUpdate(vararg values: Int?) {
//            setProgressPercent(progress[0])
        }

        override fun onPostExecute(result: Long?) {
            Log.i("App-Migratory-Platform", "finished: " + result)
            checkStatusCodeAndResend(result)
//            showDialog("Downloaded $result bytes".toInt())
        }
    }

    fun dex_loader(path:String): DexClassLoader {
//        val folder1 = Environment.getExternalStorageDirectory()
        val myFile1 = File(path, "Todo.dex")
        val getDirectoryPath = myFile1.getAbsolutePath()
        return DexClassLoader(getDirectoryPath, cacheDir.absolutePath, null, classLoader)
    }

    fun deserialize(path:String) : DynamicApp {
//        val folderPath = Environment.getExternalStorageDirectory()
        val myFile1 = File(path, "todo.ser")
        val fileIn = FileInputStream(myFile1)
        val obl = ObjectInputStreamWithLoader(fileIn, dex_loader(path))
        val e1 = obl.readObject() as DynamicApp
        obl.close()
        return e1
    }
    fun saveobject(path:String){
//        val folder1 = Environment.getExternalStorageDirectory()
        val myFile1 = File(path, "out/employee.ser")
        val fileOut = FileOutputStream(myFile1)
        val out = ObjectOutputStream(fileOut)
        out.writeObject(obj)
        out.close()
        fileOut.close()

    }
    override fun onBackPressed() {
//        super.onBackPressed()
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle("Alert")
        alertDialog.setMessage("Do you want to exit?")
        alertDialog.setButton(
            AlertDialog.BUTTON_POSITIVE, "No",
            DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }
        )
        alertDialog.setButton(
            AlertDialog.BUTTON_NEGATIVE, "Yes",
            DialogInterface.OnClickListener { dialog, which -> super.onBackPressed() }
        )
        alertDialog.show()
    }
    fun getclasses(path:String):String {
        val folder1 = Environment.getExternalStorageDirectory()
        val myFile1 = File(path)
        var text = ""
        if (myFile1 != null) {
            var br = BufferedReader(FileReader(myFile1))
            text = br.readLine()
            Log.i("App-Migratory-Platform", text)
        } else {
            Log.i("App-Migratory-Platform", "file_not found")
        }
        return text
    }

    fun loadobject (dexpath: String, cls: String): DynamicApp {
//        println("dex path=  "+ dex.absolutePath)
        val clzLoader= DexClassLoader(dexpath, cacheDir.absolutePath, null, classLoader)
        val moduleClass= clzLoader.loadClass(cls)
        return moduleClass.newInstance() as DynamicApp

    }

    private fun getPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                PERMISSIONS_WRITE_EXTERNAL_STORAGE
            )
        } else {
//            dot1.setImageResource(R.drawable.ic_tick_mark_dark)
//            printLog("LOG: (getPermission) PERMISSION_GRANTED")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == PERMISSIONS_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                dot1.setImageResource(R.drawable.ic_tick_mark_dark)
//                printLog("LOG: (onRequestPermissionsResult) PERMISSION_GRANTED")
            } else {
//                dot1.setImageResource(R.drawable.ic_cancel_dark)
//                printLog("LOG: (onRequestPermissionsResult) PERMISSION_DENIED")
            }
        }
    }

    companion object {
        lateinit var AppPath: String
        lateinit var serverthread: ServerThred
        fun closeserver() {
            AppManagementActivity.serverthread.closeserver()
            return
        }
    }
    fun showAlertWithTextInputLayout() {
        var layout = LinearLayout(context)
        layout.setOrientation(LinearLayout.VERTICAL)

        val textInputLayout = TextInputLayout(context)
        val textInputLayout1 = TextInputLayout(context)
        val ip = EditText(context)
        ip.inputType= InputType.TYPE_CLASS_NUMBER
        ip.keyListener= DigitsKeyListener.getInstance("0123456789.")
        textInputLayout.hint = "Ip Address"
        textInputLayout.addView(ip)
        layout.addView(textInputLayout)

        val port = EditText(context)
        port.inputType= InputType.TYPE_CLASS_NUMBER
        textInputLayout1.hint = "Port"

        textInputLayout1.addView(port)
        layout.addView(textInputLayout1)

        val alert = AlertDialog.Builder(context)
            .setTitle("Connect to remote device")
            .setView(layout)
            .setMessage("Please Enter Ip & port")
            .setPositiveButton("Submit") { dialog, _ ->
//                createDir(input.text.toString())
                println(ip.text.toString())
                println(port.text.toString().toInt())
                connecttoserver(ip.text.toString(),port.text.toString().toInt())
                dialog.cancel()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.create()

        alert.show()
    }

    fun connecttoserver(ip:String, port:Int) {

        val handler = Handler()
        val thread = Thread(Runnable {
            var sock: Socket? = null
            try {
//                val sock = Socket("192.168.43.13", 9002)
                sock = Socket(ip, port)
                println("Connecting...")
                val input = BufferedReader(InputStreamReader(sock.getInputStream()))
                output = PrintWriter(sock.getOutputStream())
                output.println("send me file")
                output.flush()

                messagefilelength = input.readLine()

                output = PrintWriter(sock.getOutputStream())
                output.println("send me file")
                output.flush()
                filename = input.readLine()


            } catch (e: UnknownHostException) {
                e.printStackTrace()
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()

            }finally {

                handler.post {
                    val builder1 = AlertDialog.Builder(context)
                    builder1.setMessage(filename + messagefilelength)
                    builder1.setCancelable(true)
                        .setPositiveButton("Submit") { dialog, _ ->
                            //                createDir(input.text.toString())
//                            println(ip.text.toString())
//                            println(port.text.toString().toInt())
                            val thread1 = Thread(Runnable {
                                val sock: Socket? = null
                                try {
                                    getfile(ip, port)
                                } catch (e: UnknownHostException) {
                                    e.printStackTrace()
                                } catch (e: FileNotFoundException) {
                                    e.printStackTrace()
                                } catch (e: IOException) {
                                    e.printStackTrace()

                                }finally {
                                    handler.post {
                                        val alert = AlertDialog.Builder(context)
                                            .setMessage("file is saved on app path")
                                            .setCancelable(true)
                                            .setPositiveButton("Ok") { dialog, _ ->
                                                //                createDir(input.text.toString())
//                                                println(ip.text.toString())
//                                                println(port.text.toString().toInt())
                                                dialog.cancel()
                                            }.create()
                                        alert.show()

                                    }
                                    if (sock != null) {
                                        try {
                                            sock.close()
                                        } catch (e: IOException) {
                                            e.printStackTrace()
                                        }

                                    }
                                }
                            })
                            thread1.start()
                            dialog.cancel()
                        }
                        .setNegativeButton("Cancel") { dialog, _ ->
                            dialog.cancel()
                        }.create()
                    builder1.show()
                }

                if (sock != null) {
                    try {
                        sock.close()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                }

            }
        })
        thread.start()
    }


    fun getfile(ip:String, port:Int) {
        val sock = Socket(ip, port)
        println("Connecting...")
        val output = PrintWriter(sock.getOutputStream())
        output.println("get file")
        output.flush()

        val dIn = DataInputStream(sock.getInputStream())   // read length of incoming message
        val length = dIn.readInt()
        if (length > 0) {
            val message = ByteArray(length)
            val message1 = ByteArray(length)
            dIn.readFully(message1, 0, message1.size)
            println(message1)
            writeByte(message1)


        }
    }

    fun writeByte(bytes: ByteArray) {
        val file = Environment.getExternalStorageDirectory()
        val save = File(file, "/"+filename)

        try {
            val out = FileOutputStream(save.absolutePath)
            out.write(bytes)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
}
