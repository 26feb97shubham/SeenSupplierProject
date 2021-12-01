package com.dev.ecomercesupplier.activity

import android.content.Intent
import android.os.Bundle
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.AccountTypeAdapter
import com.dev.ecomercesupplier.custom.Utility.Companion.checkedPosition
import com.dev.ecomercesupplier.model.ModelForAccountType
import com.dev.ecomercesupplier.model.ModelForSpinner
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_choose_category.*
import kotlinx.android.synthetic.main.activity_register_1.*
import kotlinx.android.synthetic.main.activity_register_2.*
import java.util.ArrayList

class ChooseCategoryActivity : AppCompatActivity() {
    private var accountList= ArrayList<ModelForAccountType>()
    private var accountTypeAdapter : AccountTypeAdapter?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_category)
        if (intent!=null){
            if (intent.extras!=null){
                accountList = intent.getSerializableExtra("accountList") as ArrayList<ModelForAccountType>
            }
        }
        setUpViews()
    }

    private fun setUpViews() {
        iv_back_choose_account_type.setOnClickListener {
            iv_back_choose_account_type.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, iv_back_choose_account_type)
            onBackPressed()
        }
        rv_select_your_account_type.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        accountTypeAdapter = AccountTypeAdapter(this,accountList)
        rv_select_your_account_type.adapter = accountTypeAdapter
        accountTypeAdapter!!.notifyDataSetChanged()

        btnSubmit_account_type.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("accountList", accountList)
            startActivity(Intent(this, AgreementActivity::class.java).putExtras(bundle))
        }
    }
}