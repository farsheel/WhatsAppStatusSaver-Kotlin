package com.farsheel.statussaver.utils

import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.AppCompatRadioButton
import android.webkit.MimeTypeMap
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.TextView
import com.farsheel.statussaver.BuildConfig
import java.io.File


/**
 * @Author farsheel
 * @Date 31/7/18.
 */
class Utils {

    companion object {


         const val WHATSAPP_STATUSES_LOCATION = "/WhatsApp/Media/.Statuses"
         const val WHATSAPP_STATUSES_SAVED_LOCATION = "/statusSaver"


         fun isImageFile(context: Context,path: String): Boolean {
             val uri:Uri = Uri.parse(path)

             val mimeType: String?
             mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                 val cr = context.contentResolver
                 cr.getType(uri)
             } else {
                 val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                         .toString())
                 MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                         fileExtension.toLowerCase())
             }


            return mimeType != null && mimeType.startsWith("image")
        }

         fun isVideoFile(context: Context,path: String): Boolean {

             val uri:Uri = Uri.parse(path)

             val mimeType: String?
             mimeType = if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
                 val cr = context.contentResolver
                 cr.getType(uri)
             } else {
                 val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                         .toString())
                 MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                         fileExtension.toLowerCase())
             }

             return mimeType != null && mimeType.startsWith("video")
        }


        fun addToGallery(context: Context,f:File) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)

                val contentUri = Uri.fromFile(f)
                mediaScanIntent.data = contentUri
                context.sendBroadcast(mediaScanIntent)
            } else {
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())))
            }
        }


         fun shareFile(context: Context,f: File) {

            val intentShareFile = Intent(Intent.ACTION_SEND)

            if (f.exists()) {
                intentShareFile.type = "image/*"
                intentShareFile.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://${f.absolutePath}"))
                intentShareFile.putExtra(Intent.EXTRA_TEXT, "WhatsApp Status Downloaded Via https://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID} ")

                context.startActivity(Intent.createChooser(intentShareFile, f.name))
            }
        }


        fun setFonts(typeface: Typeface, vararg objects: Any) {
            for (obj in objects){
                when (obj){
                    is TextView -> obj.typeface = typeface
                    is EditText ->  obj.typeface = typeface
                    is TextInputEditText ->  obj.typeface = typeface
                    is TextInputLayout -> obj.setTypeface(typeface)
                    is Button -> obj.typeface = typeface
                    is RadioButton -> obj.typeface = typeface
                    is AppCompatEditText -> obj.typeface = typeface
                    is AppCompatButton -> obj.typeface = typeface
                    is AppCompatRadioButton -> obj.typeface = typeface
                    is AppCompatCheckBox -> obj.typeface = typeface
                }
            }
        }


    }
}