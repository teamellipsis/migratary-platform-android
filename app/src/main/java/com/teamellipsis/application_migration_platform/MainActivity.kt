package com.teamellipsis.application_migration_platform

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
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
import android.support.design.widget.TextInputLayout
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.widget.*
import com.teamellipsis.dynamic.DynamicApp
import dalvik.system.DexClassLoader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*
import java.io.File.separator



class MainActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    private var listItems: MutableList<String> = mutableListOf<String>()
    private var listFiles: MutableList<File> = mutableListOf<File>()
    lateinit var fileSystem: FileSystem
    lateinit var context: Context
    lateinit var obj : DynamicApp
    lateinit var st : ServerThred
    lateinit var appConfig: AppConfig
    lateinit var currentdir: File
    lateinit var listView: ListView
    lateinit var txview: TextView
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        appConfig = AppConfig(applicationContext)
        listView = findViewById<ListView>(R.id.listView)
        listView.onItemClickListener = this
        fileSystem = FileSystem(applicationContext)
        context = this
        val appsDir = File(Environment.getExternalStorageDirectory().absolutePath)
        currentdir = File(Environment.getExternalStorageDirectory().absolutePath)
        txview = findViewById<TextView>(R.id.txtView)
        txview.setText(currentdir.absolutePath)
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
        currentdir=listFiles[position]
        openDialog1(currentdir)

    }
    fun openDialog1(appPath: File) {
        val appsDir = appPath
        listItems.clear()
        listFiles.clear()
        if (appsDir.exists()) {
            Log.i("App-Migratory-Platform", appsDir.listFiles().size.toString())
            for (file in appsDir.listFiles()) {
                listItems.add(file.name)
                listFiles.add(file)
            }
            if (listItems.isNotEmpty()) {
                var arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems)
                listView.adapter = arrayAdapter
                arrayAdapter.setNotifyOnChange(true)
                txview.setText(currentdir.absolutePath)
//                listView.adapter = ArrayAdapter<String>(this, R.layout.app_list_item, R.id.listItemText, listItems)
            }else{
                var arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listItems)
                listView.adapter = arrayAdapter
                arrayAdapter.setNotifyOnChange(true)
                txview.setText(currentdir.absolutePath)
            }
        }
    }
    override fun onBackPressed() {
//        super.onBackPressed()
        if(currentdir!=Environment.getExternalStorageDirectory()){
            var newcurrent=File(currentdir.parent)
            openDialog1( File(currentdir.parent))
            currentdir=newcurrent
            txview.setText(currentdir.absolutePath)
        }else{
                val alertDialog = AlertDialog.Builder(this).create()
            if (appConfig.get(AppConstant.KEY_WORKING_DIR).isEmpty()) {
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Please select a working directory first")
                alertDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, "Ok",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }
                )
            }else{
                alertDialog.setTitle("Alert")
                alertDialog.setMessage("Do you want to exit?")
                alertDialog.setButton(
                    AlertDialog.BUTTON_POSITIVE, "No",
                    DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() }
                )
                alertDialog.setButton(
                    AlertDialog.BUTTON_NEGATIVE, "Yes",
                    DialogInterface.OnClickListener { dialog, which ->
                        super.onBackPressed()
                    }
                )
            }

                alertDialog.show()
        }

    }

    fun selectFolder(view: View) {
        appConfig.set(AppConstant.KEY_WORKING_DIR, currentdir.absolutePath)
        appConfig.set(AppConstant.KEY_APPS_DIR, currentdir.absolutePath+"/Migration/Apps")
        appConfig.set(AppConstant.KEY_SENTITM_DIR, currentdir.absolutePath+"/Migration/SentItems")
        createFolder(currentdir.absolutePath)
        val intent = Intent(applicationContext, AppManagementActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun checkStatusCodeAndResend(statusCode: Long?) {

    }



    fun createFolder(path:String){
        val folder1 = File(path + "/Migration/Apps")
        val folder2 = File(path + "/Migration/SentItems")

        var success = true

        if (!folder2.exists()) {
            success = folder2.mkdirs()
        }
        if (!folder1.exists()) {
            success = folder1.mkdirs()
        }

    }
    fun createDir(name:String){
        if(name!=null){
            val folder = File(currentdir.absolutePath+File.separator +name )
            var success = true

            if (!folder.exists()) {
                success = folder.mkdirs()
            }
            if(success){
                openDialog1(currentdir)
            }
        }
    }
fun showAlertWithTextInputLayout(view: View) {

        val textInputLayout = TextInputLayout(context)

        val input = EditText(context)
        textInputLayout.hint = "Name"
        textInputLayout.addView(input)

        val alert = AlertDialog.Builder(context)
            .setTitle("Create Folder")
            .setView(textInputLayout)
            .setMessage("Please Enter Folder Name")
            .setPositiveButton("Submit") { dialog, _ ->
                createDir(input.text.toString())
                dialog.cancel()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.cancel()
            }.create()

        alert.show()
    }


}
