package com.farsheel.statussaver.home

import android.app.Activity
import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Toast
import com.bumptech.glide.Glide
import com.farsheel.statussaver.MyApplication.Companion.fontRegular
import com.farsheel.statussaver.R
import com.farsheel.statussaver.image.ImageViewActivity
import com.farsheel.statussaver.utils.Utils
import com.farsheel.statussaver.video.VideoActivity
import kotlinx.android.synthetic.main.layout_status_item.view.*
import org.apache.commons.io.FileUtils
import java.io.File


/**
 * @Author farsheel
 * @Date 31/7/18.
 */
class StatusAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    var dataList: ArrayList<File> = ArrayList()

    private lateinit var onClickDownloadListener: OnClickDownloadListener

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {


        val view = LayoutInflater.from(context).inflate(R.layout.layout_status_item, p0, false)

        return StatusHolder(view, context)


    }

    override fun getItemCount(): Int {

        return dataList.size

    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        (holder as StatusHolder).bindData(dataList[position])

    }

    private fun clear() {

        dataList.clear()
    }

    fun addAll(result: ArrayList<File>) {
        clear()
        dataList.addAll(result)
        notifyDataSetChanged()
    }

    fun setOnClickDownloadListener(onClickDownloadListener: OnClickDownloadListener) {

        this.onClickDownloadListener = onClickDownloadListener

    }


    internal inner class StatusHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        override fun onClick(view: View?) {

            val file = dataList[adapterPosition]

            if (view!!.id == R.id.downloadTv) {
                val destFile = File("${Environment.getExternalStorageDirectory()}${Utils.WHATSAPP_STATUSES_SAVED_LOCATION}/${file.name}")
                FileUtils.copyFile(file, destFile)
                Utils.addToGallery(context, destFile)

                Toast.makeText(context, context.getString(R.string.status_saved_to_gallery), Toast.LENGTH_SHORT).show()

                onClickDownloadListener.onClickDownload()
            }

            if (view.id == R.id.shareTv) {

                Utils.shareFile(context, file)

            }

            if (view.id == R.id.thumbnailIv && Utils.isVideoFile(context, file.path)) {

                val intent = Intent(context, VideoActivity::class.java)
                intent.putExtra("path", file.absolutePath)

                val activityOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions.makeSceneTransitionAnimation(
                            context as Activity, itemView.thumbnailIv, "image")

                } else {
                    context.startActivity(intent)
                    return
                }

                context.startActivity(intent, activityOptions.toBundle())

            }else if (view.id == R.id.thumbnailIv){

                val intent = Intent(context, ImageViewActivity::class.java)
                intent.putExtra("image", file.absolutePath)

                val activityOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions.makeSceneTransitionAnimation(
                            context as Activity, itemView.thumbnailIv, "image")

                } else {
                    context.startActivity(intent)
                    return
                }

                context.startActivity(intent, activityOptions.toBundle())

            }


        }


        init {


            Utils.setFonts(fontRegular, itemView.downloadTv, itemView.shareTv)

            itemView.downloadTv.setOnClickListener(this)
            itemView.shareTv.setOnClickListener(this)
            itemView.thumbnailIv.setOnClickListener(this)
        }


        private fun setFadeAnimation(view: View) {
            val anim = AlphaAnimation(0.0f, 1.0f)
            anim.duration = 200
            view.startAnimation(anim)
        }


        fun bindData(file: File) {

            setFadeAnimation(itemView)

            if (Utils.isVideoFile(context, file.path)) {

                itemView.playIv.visibility = View.VISIBLE

                Glide.with(context)
                        .load(Uri.fromFile(File(file.path)))
                        .thumbnail(0.1f)
                        .into(itemView.thumbnailIv)


            } else {

                itemView.playIv.visibility = View.GONE
                Glide.with(context)
                        .load(file)
                        .into(itemView.thumbnailIv)
            }

            if (file.absolutePath.contains(Utils.WHATSAPP_STATUSES_SAVED_LOCATION)) {
                itemView.downloadTv.visibility = View.GONE
            } else {
                itemView.downloadTv.visibility = View.VISIBLE
            }
        }

    }
}