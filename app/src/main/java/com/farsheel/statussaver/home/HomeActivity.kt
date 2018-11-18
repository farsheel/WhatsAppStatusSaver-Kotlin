package com.farsheel.statussaver.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.support.annotation.RequiresApi
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import com.farsheel.statussaver.BuildConfig
import com.farsheel.statussaver.R
import com.farsheel.statussaver.utils.MyAlert
import com.farsheel.statussaver.utils.MyProgress
import com.farsheel.statussaver.utils.Utils
import com.farsheel.statussaver.utils.Utils.Companion.WHATSAPP_STATUSES_LOCATION
import com.farsheel.statussaver.utils.Utils.Companion.WHATSAPP_STATUSES_SAVED_LOCATION
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_home.*
import org.apache.commons.io.comparator.LastModifiedFileComparator
import java.io.File
import java.lang.ref.WeakReference
import java.util.*
import kotlin.collections.ArrayList

/**
 * @Author farsheel
 * @Date 04/8/18.
 */


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnClickDownloadListener {



    override fun onClickDownload() {

        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }

    }

    private lateinit var progress:MyProgress

    private lateinit var mInterstitialAd: InterstitialAd


    private companion object {
        const val EXTERNAL_STORAGE_PERMISSION_CODE: Int = 343

        private const val TYPE_VIDEO = 12
        private const val TYPE_IMAGE = 13
        private const val TYPE_SAVED = 15

          class FetchFilesTask(activity: HomeActivity) : AsyncTask<Int, Int, ArrayList<File>>() {

              private val mRef: WeakReference<HomeActivity> = WeakReference(activity)

              override fun onPreExecute() {
                  super.onPreExecute()
                  mRef.get()?.progress?.showProgress()
              }

              override fun doInBackground(vararg p0: Int?): ArrayList<File>? {

                return mRef.get()?.fetchFiles(p0[0])

            }

              override fun onPostExecute(result: ArrayList<File>) {
                  super.onPostExecute(result)
                  mRef.get()?.statusAdapter?.addAll(result)

                  mRef.get()?.progress?.hideProgress()

                  if (result.size == 0){
                      mRef.get()?.showHelp()
                  }

              }
          }

    }


    private lateinit var statusAdapter:StatusAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(toolbar)

        MobileAds.initialize(this, getString(R.string.admob_app_id))

        //adView.adSize = AdSize.BANNER
        //adView.adUnitId = AD_UNIT_ID

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.admob_interstitial_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())




        progress = MyProgress(this)



        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)


        statusAdapter = StatusAdapter(this)
        statusRcV.layoutManager = GridLayoutManager(this,2)

        statusAdapter.setOnClickDownloadListener(this)

        statusRcV.adapter = statusAdapter




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission()
        }

        FetchFilesTask(this).execute(TYPE_IMAGE)

    }



    @RequiresApi(Build.VERSION_CODES.M)
    private fun askForPermission() {

        if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission
            requestPermissions( arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), EXTERNAL_STORAGE_PERMISSION_CODE)
        }
    }

     fun fetchFiles(type: Int?): ArrayList<File> {

        var parentDir = File(Environment.getExternalStorageDirectory().toString()+WHATSAPP_STATUSES_LOCATION)

         if (type == TYPE_SAVED){

             parentDir = File(Environment.getExternalStorageDirectory().toString()+ WHATSAPP_STATUSES_SAVED_LOCATION)
         }


        val files: Array<File>?
        files = parentDir.listFiles()

         val fetchedFiles: ArrayList<File> = ArrayList()

        if (files != null) {

            Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE)

            for (file in files) {

                if (Utils.isImageFile(this,file.path) && type == TYPE_IMAGE){

                    fetchedFiles.add(file)
                }

                if (Utils.isVideoFile(this,file.path) && type == TYPE_VIDEO){

                    fetchedFiles.add(file)
                }
                if (type == TYPE_SAVED){

                    fetchedFiles.add(file)
                }


            }
        }

         return fetchedFiles
    }



    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_share -> {
                shareApp()
            }
            R.id.nav_about -> {
                showAbout()
            }
            R.id.nav_rate -> {
                rateApp()
            }
            R.id.nav_help -> {
                showHelp()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)


        if(ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            //ask for permission


            MyAlert.create(this)
                    .setTitle(getString(R.string.no_permission))
                    .setMessage(getString(R.string.require_read_write_external_storage_permissin))
                    .setOkButton(getString(R.string.allow), View.OnClickListener {


                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                            // User checked "Never ask again"
                            val intent =  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                    Uri.fromParts("package", packageName, null))
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                        }else{
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                askForPermission()
                            }
                        }

                    }).setCancelButton(getString(R.string.exit),View.OnClickListener {
                        finish()
                    }).setCancelable(false)
            return

        }


        FetchFilesTask(this).execute(TYPE_IMAGE)
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            askForPermission()
        }
        when (item.itemId) {

            R.id.navigation_images -> {
                FetchFilesTask(this).execute(TYPE_IMAGE)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_video -> {
                FetchFilesTask(this).execute(TYPE_VIDEO)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_saved -> {
                FetchFilesTask(this).execute(TYPE_SAVED)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    private fun showHelp(){
        MyAlert.create(this).setTitle(getString(R.string.help))
                .setMessage(getString(R.string.help_message)).setOkButton("Close",null)

    }

    private fun showAbout(){
        val  about = "${getString(R.string.app_name)} \n ${getString(R.string.version, BuildConfig.VERSION_NAME)}"

        MyAlert.create(this).setTitle(getString(R.string.about))
                .setMessage(about).setOkButton("Close",null)

    }

    private fun shareApp(){

        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.share_app_text,"https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}"))
        sendIntent.type = "text/plain"
        startActivity(sendIntent)

    }

    @SuppressLint("InlinedApi")
    private fun rateApp(){


        val uri:Uri = Uri.parse("market://details?id=$packageName")

        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK

        try {
            startActivity(intent)

        }catch (e:ActivityNotFoundException){
            startActivity(Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=$packageName")))
        }
    }




}
