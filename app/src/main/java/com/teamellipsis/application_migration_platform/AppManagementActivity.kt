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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_app_management.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import android.os.AsyncTask
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.teamellipsis.dynamic.DynamicApp
import dalvik.system.DexClassLoader
import java.io.*

class AppManagementActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var listItems: MutableList<String> = mutableListOf<String>()
    private var listFiles: MutableList<File> = mutableListOf<File>()
    lateinit var fileSystem: FileSystem
    lateinit var context: Context
    lateinit var obj : DynamicApp
    lateinit var st : ServerThred
    lateinit var appConfig: AppConfig
    var PERMISSIONS_WRITE_EXTERNAL_STORAGE = 0
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_management)

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
                    AppDialogOptions.Delete.name
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
        Open, Package, Send, Reset, Delete
    }

    fun openApp(filePath: File) {
        obj=deserialize(filePath.absolutePath)
       // getclasses()
        Toast.makeText(applicationContext,"Object loaded sucessfully", Toast.LENGTH_LONG).show()
        this.st = ServerThred("state data",obj)
        st!!.run()
//        val intent = Intent(applicationContext, AgentActivity::class.java).apply {
//            putExtra("WEB_VIEW_URL", webViewUrl)
//        }
        startActivity(intent)
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
        val myFile1 = File(path, "dm.dex")
        val getDirectoryPath = myFile1.getAbsolutePath()
        return DexClassLoader(getDirectoryPath, cacheDir.absolutePath, null, classLoader)
    }

    fun deserialize(path:String) : DynamicApp {
//        val folderPath = Environment.getExternalStorageDirectory()
        val myFile1 = File(path, "states.ser")
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
    fun getclasses() {
        val folder1 = Environment.getExternalStorageDirectory()
        val myFile1 = File(folder1, "/fyp/entry.tf")
        var text = ""
        if (myFile1 != null) {
            var br = BufferedReader(FileReader(myFile1))
            text = br.readLine()
            Log.i("App-Migratory-Platform", text)
        } else {
            Log.i("App-Migratory-Platform", "file_not found")
        }
    }

    fun load(dex: File, cls: String = "com.example.dynamicclassloader.Todo_App"): DynamicApp {
        println("dex path=  "+ dex.absolutePath)
        val clzLoader= DexClassLoader(dex.absolutePath, cacheDir.absolutePath, null, this.javaClass.classLoader)
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

        fun a() : String {

            return AppPath
        }
    }





}
