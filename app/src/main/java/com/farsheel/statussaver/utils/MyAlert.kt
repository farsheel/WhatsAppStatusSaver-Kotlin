package com.farsheel.statussaver.utils


import android.app.AlertDialog
import android.content.Context
import android.text.Spannable
import android.view.LayoutInflater
import android.view.View


import com.farsheel.statussaver.MyApplication.Companion.fontRegular
import com.farsheel.statussaver.R
import kotlinx.android.synthetic.main.layout_my_alert.view.*

/**
 * @Author farsheel
 * @Date 8/4/18.
 */
class MyAlert(private var context: Context) {

    private lateinit var alertBuilder:AlertDialog.Builder
    private lateinit var view: View
    private lateinit var alertDialog: AlertDialog

    init {
        initAlert()
    }

    private fun initAlert() {
        alertBuilder = AlertDialog.Builder(context)
        view = LayoutInflater.from(context).inflate(R.layout.layout_my_alert,null)
        alertBuilder.setView(view)
        initViews()
        alertDialog = alertBuilder.show()
    }

    private fun initViews() {

        Utils.setFonts(fontRegular,view.cancelBtn,view.okBtn,view.titleTv,view.messageTv)
    }

    fun setCancelable(isCancelable: Boolean):MyAlert{
        alertBuilder.setCancelable(isCancelable)
        return this
    }

    fun setMessage(message: Spannable):MyAlert{
        view.messageTv.visibility = View.VISIBLE
        view.messageTv.text = message
        return this
    }


    fun setMessage(message: String):MyAlert{
        view.messageTv.visibility = View.VISIBLE
        view.messageTv.text = message
        return this
    }

    fun setTitle(title: String):MyAlert{
        view.titleTv.visibility = View.VISIBLE
        view.titleTv.text = title
        return this
    }

    fun setOkButton(title: String,listener: View.OnClickListener?):MyAlert{
        view.okBtn.visibility = View.VISIBLE
        view.okBtn.text = title
        view.okBtn.setOnClickListener { view ->
            listener?.onClick(view)
            alertDialog.dismiss()
        }

        return this
    }

    fun setCancelButton(title: String, listener: View.OnClickListener?):MyAlert{
        view.cancelBtn.visibility = View.VISIBLE
        view.cancelBtn.text = title
        view.cancelBtn.setOnClickListener { view ->
            listener?.onClick(view)
            alertDialog.dismiss()
        }

        return this
    }


    companion object {
        fun create(context: Context): MyAlert {
            return  MyAlert(context)
        }
    }

}