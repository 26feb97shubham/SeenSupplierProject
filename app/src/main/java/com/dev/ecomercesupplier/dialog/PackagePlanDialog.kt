package com.dev.ecomercesupplier.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.DialogFragment
import com.dev.ecomercesupplier.R
import kotlinx.android.synthetic.main.dialog_package_plan.view.*

class PackagePlanDialog(val amt: String, val packPlan: String, val detail: String) : DialogFragment() {
    private var completionCallback: PackagePlanInterface? = null
    internal lateinit var mView: View

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        return dialog
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        dialog!!.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mView = layoutInflater.inflate(R.layout.dialog_package_plan, container, false)
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

    fun setDataCompletionCallback(completionCallback: PackagePlanInterface?) {
        this.completionCallback = completionCallback
    }

    private fun setUpViews() {

        mView.txtPrice.text = amt
        mView.txtDuration.text = packPlan
        mView.packDesc.text = detail

        mView.btnSubscribe.setOnClickListener {
            mView.btnSubscribe.startAnimation(AlphaAnimation(1f, 0.5f))
            completionCallback!!.complete()
            dismiss()
        }
//        mView.btnBack.setOnClickListener {
//            mView.btnBack.startAnimation(AlphaAnimation(1f, 0.5f))
//            dismiss()
//        }

    }


    interface PackagePlanInterface {
        fun complete()
    }
}