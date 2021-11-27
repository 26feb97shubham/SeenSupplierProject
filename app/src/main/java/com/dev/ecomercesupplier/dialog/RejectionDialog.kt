package com.dev.ecomercesupplier.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.DialogFragment
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.utils.LogUtils
import kotlinx.android.synthetic.main.dialog_rejection.view.*

class RejectionDialog() : DialogFragment() {
    private var completionCallback: RejectionInterface? = null
    internal lateinit var mView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mView = layoutInflater.inflate(R.layout.dialog_rejection, container, false)
        setUpViews()
        return mView
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val scale: Float = requireContext().resources.displayMetrics.density
            val dpAsPixels = (350 * scale + 0.5f).toInt()
            dialog.window!!.setLayout(dpAsPixels, ViewGroup.LayoutParams.WRAP_CONTENT)
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    fun setDataCompletionCallback(completionCallback: RejectionInterface?) {
        this.completionCallback = completionCallback
    }

    private fun setUpViews() {

        mView.btnSubmit.setOnClickListener {
            mView.btnSubmit.startAnimation(AlphaAnimation(1f, 0.5f))

            val reason=mView.edtReason.text.toString()
            if(TextUtils.isEmpty(reason)){
                LogUtils.shortToast(requireContext(), getString(R.string.please_enter_reason_for_rejection))
            }
            else{
                completionCallback!!.complete(reason)
                dismiss()
            }


        }
        mView.closeImg.setOnClickListener {
            mView.closeImg.startAnimation(AlphaAnimation(1f, 0.5f))
            dismiss()
        }

    }


    interface RejectionInterface {
        fun complete(reason: String)
    }
}