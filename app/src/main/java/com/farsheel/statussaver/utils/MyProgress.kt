package com.farsheel.statussaver.utils

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


import kotlinx.android.synthetic.main.layout_my_progress.view.*
import com.bumptech.glide.Glide
import com.farsheel.statussaver.R


/**
 * @Author farsheel
 * @Date 8/4/18.
 */
class MyProgress(private val context: Activity) {

    private lateinit var progressView: View
    private var parent: ViewGroup = context.window.decorView.rootView as ViewGroup


    fun showProgress(){

        progressView= LayoutInflater.from(context).inflate(R.layout.layout_my_progress,parent,false)
        Glide.with(context).load(R.drawable.progress_image).into(progressView.progressIv)

        parent.addView(progressView)
    }

    fun hideProgress(){

        parent.removeView(progressView)

    }

}