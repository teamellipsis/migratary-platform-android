package com.teamellipsis.application_migration_platform

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_app_management.*
import java.io.File
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import android.os.AsyncTask
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.teamellipsis.dynamic.DynamicApp
import dalvik.system.DexClassLoader
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*

class AppManagementActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var listItems: MutableList<String> = mutableListOf<String>()
    private var listFiles: MutableList<File> = mutableListOf<File>()
    lateinit var fileSystem: FileSystem
    lateinit var context: Context
    lateinit var obj : DynamicApp
    lateinit var st : ServerThred
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_management)

        listView.onItemClickListener = this

        fileSystem = FileSystem(applicationContext)
//        var appConfig = AppConfig(applicationContext)
        context = this

        var appsDir = File(Environment.getExternalStorageDirectory().absolutePath+ "/fyp")
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
                                val packagesDir = File(fileSystem.getPackagesDir())
                                packagesDir.mkdirs()
                                fileSystem.zipDir(appPath, File(packagesDir, appPath.name + ".zip"))
                            }
                            AppDialogOptions.Send.ordinal -> {
                                val file = File(fileSystem.getPackagesDir(), appPath.name + ".zip")
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
        obj=serialize()
        Toast.makeText(applicationContext,"Object loaded sucessfully", Toast.LENGTH_LONG).show()
        this.st = ServerThred("state data",obj)
        st!!.run()
        val intent = Intent(applicationContext, BrowserActivity::class.java)
        startActivity(intent)
//        Log.i("App-Migratory-Platform", filePath.absolutePath + "/server.js")
//        var projectThread = Thread(Runnable {
//            var nativeClient = NativeClient()
//            nativeClient.startNodeWithArgs("node", filePath.absolutePath + "/server.js")
//            Log.i("App-Migratory-Platform", filePath.absolutePath + "/server.js")
//        })
//        projectThread.start()
//        Log.i("App-Migratory-Platform", projectThread.name)
//        checkStatusCodeAndResend(null)
    }

    fun checkStatusCodeAndResend(statusCode: Long?) {
//        if (statusCode != 200.toLong()) {
//            HttpAsyncTask().execute()
//        } else {
//            val webViewUrl = "http://localhost:3000"
//            val intent = Intent(applicationContext, AgentActivity::class.java).apply {
//                putExtra("WEB_VIEW_URL", webViewUrl)
//            }
//            startActivity(intent)
//        }
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

    fun dex_loader(): DexClassLoader {
        val folder1 = Environment.getExternalStorageDirectory()
        val myFile1 = File(folder1, "/fyp/dx.dex")
        val getDirectoryPath = myFile1.getAbsolutePath()
        return DexClassLoader(getDirectoryPath, cacheDir.absolutePath, null, classLoader)
    }

    fun serialize() : DynamicApp {
        val folder1 = Environment.getExternalStorageDirectory()
        val myFile1 = File(folder1, "/fyp/states.ser")
        val fileIn = FileInputStream(myFile1)
        val obl = ObjectInputStreamWithLoader(fileIn, dex_loader())
        val e1 = obl.readObject() as DynamicApp
        obl.close()
        val arr6 = arrayOf("0", "test1", "test_task", "2001-2-21")
//        e1.execute(arr6)
//        e1.execute(arr6)
//        fileIn.close()
        return e1
    }
    fun saveobject(){
        val folder1 = Environment.getExternalStorageDirectory()
        val myFile1 = File(folder1, "/fyp/out/employee.ser")
        val fileOut = FileOutputStream(myFile1)
        val out = ObjectOutputStream(fileOut)
        out.writeObject(obj)
        out.close()
        fileOut.close()

    }

}
