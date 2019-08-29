package com.teamellipsis.application_migration_platform

import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.os.Environment
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.view.View
import android.widget.TextView

import kotlinx.android.synthetic.main.activity_app_unzip.*
import org.json.JSONObject
import java.io.File

class AppUnzipActivity : AppCompatActivity() {
    private lateinit var uri: Uri
    private lateinit var fileSystem: FileSystem
    private lateinit var appConfig: AppConfig
    private var extractPackageAsyncTask: ExtractPackageAsyncTask? = null
//    private lateinit var app_name: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_unzip)
        setSupportActionBar(toolbar)

        supportActionBar?.setTitle(R.string.title_activity_app_unzip)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        uri = if (intent.action == Intent.ACTION_VIEW) {
            intent.data
        } else {
            intent.getParcelableExtra(Intent.EXTRA_STREAM) as Uri
        }

        fileSystem = FileSystem(applicationContext)
        appConfig = AppConfig(applicationContext)

        app_name.text= uri.toString()
        println(uri)
        open_btn.visibility=View.GONE
    }

    fun extract(view: View) {
//        editTxtAppName.isEnabled = false
//        btnExtract.isEnabled = false
        extractPackageAsyncTask = ExtractPackageAsyncTask()
        extractPackageAsyncTask?.execute()
        open_btn.visibility=View.VISIBLE
    }

    fun openApp(view: View){
        val intent = Intent(applicationContext, AppManagementActivity::class.java)
        startActivity(intent)
        finish()
    }

//    private inner class CheckPackageJsonAsyncTask : AsyncTask<String, Int, JSONObject>() {
//        override fun doInBackground(vararg argv: String): JSONObject? {
//            return fileSystem.scanPackageJson(uri)
//        }
//
//        override fun onProgressUpdate(vararg values: Int?) {}
//
//        override fun onPostExecute(appPackageJson: JSONObject?) {
//            progressAppDetails.visibility = View.GONE
//            txtAppDetail.visibility = View.VISIBLE
//            if (appPackageJson != null) {
//                CheckPrerequisitesAsyncTask().execute()
//                cardExecutable.visibility = View.VISIBLE
//                packageJson = appPackageJson
//                setAppDetails()
//            } else {
//                txtAppDetail.text = resources.getString(R.string.incompatible_package_app_detail)
//            }
//        }
//    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        uri = if (intent?.action == Intent.ACTION_VIEW) {
            intent.data
        } else {
            intent?.getParcelableExtra(Intent.EXTRA_STREAM) as Uri
        }
//        init()
    }

    inner class ExtractPackageAsyncTask : AsyncTask<String, Int, Boolean>() {
        override fun onPreExecute() {
            super.onPreExecute()
//            progressBarExtract.max = zipEntries!!.size + zipEntriesNodeModules!!.size
//            progressBarExtract.visibility = View.VISIBLE
        }

        override fun doInBackground(vararg argv: String): Boolean {
            val targetDirectory = File(Environment.getExternalStorageDirectory().absolutePath, "fyp/Zip")

            return fileSystem.unzipByIntent(uri, targetDirectory, this)
        }

        fun publishProgressCallBack(vararg values: Int?) {
            this.publishProgress(*values)
        }

        override fun onProgressUpdate(vararg values: Int?) {
//            progressBarExtract.progress = values[0]!!
        }

        override fun onPostExecute(success: Boolean) {
//            progressBarExtract.visibility = View.GONE
            if (success) {
//              btnCancel.visibility = View.GONE
//              btnOpen.visibility = View.VISIBLE
//                open_btn.visibility= View.VISIBLE
            } else {
//                txtExecutable.visibility = View.VISIBLE
//                txtExecutable.text = resources.getString(R.string.extraction_failed_app_detail)
            }
        }

    }
}
