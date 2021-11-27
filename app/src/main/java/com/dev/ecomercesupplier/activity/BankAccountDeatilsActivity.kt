package com.dev.ecomercesupplier.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.MotionEvent
import android.view.View
import android.view.animation.AlphaAnimation
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_bank_account_deatils.*
import kotlinx.android.synthetic.main.fragment_bank_details.view.*

class BankAccountDeatilsActivity : AppCompatActivity() {
    var accountHolderName:String=""
    var accountNumber:String=""
    var iban_number:String=""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bank_account_deatils)
        setUpViews()
    }

    private fun setUpViews() {
        if(intent != null){
            accountHolderName=intent.getStringExtra("accountHolderName").toString()
            accountNumber=intent.getStringExtra("accountNumber").toString()
            iban_number=intent.getStringExtra("iban_number").toString()
            edtAccountHolderName.setText(accountHolderName)
            edtAccountNumber.setText(accountNumber)
            edtIban.setText(iban_number)
        }
        backImg.setOnClickListener {
            backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, backImg)
            onBackPressed()
        }
        btnSave.setOnClickListener {
           btnSave.startAnimation(AlphaAnimation(1f, .5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnSave)
            validateBankDetails()
        }
        scrollView.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
               edtAccountHolderName.clearFocus()
               edtAccountNumber.clearFocus()
               edtIban.clearFocus()
                return false
            }

        })
    }
    private fun validateBankDetails() {
        accountHolderName=edtAccountHolderName.text.toString()
        accountNumber=edtAccountNumber.text.toString()
        iban_number=edtIban.text.toString()
        when {
            TextUtils.isEmpty(accountHolderName) -> {
               edtAccountHolderName.requestFocus()
               edtAccountHolderName.error=getString(R.string.please_enter_account_holder_name)
            }
            TextUtils.isEmpty(accountNumber) -> {
               edtAccountNumber.requestFocus()
               edtAccountNumber.error=getString(R.string.please_enter_account_number)
            }
            accountNumber.length<9->{
               edtAccountNumber.requestFocus()
               edtAccountNumber.error=getString(R.string.please_enter_valid_account_number)
            }
            TextUtils.isEmpty(iban_number) -> {
               edtIban.requestFocus()
               edtIban.error=getString(R.string.please_enter_iban_number)
            }

            !SharedPreferenceUtility.getInstance().isIbanValid(iban_number, accountNumber)->{
               edtIban.requestFocus()
               edtIban.error=getString(R.string.please_enter_valid_iban_number)
            }
            else -> {
                val intent= Intent()
                intent.putExtra("accountHolderName", accountHolderName)
                intent.putExtra("accountNumber", accountNumber)
                intent.putExtra("iban_number", iban_number)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }
}