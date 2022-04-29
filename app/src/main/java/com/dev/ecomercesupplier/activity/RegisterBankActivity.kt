package com.dev.ecomercesupplier.activity

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.animation.AlphaAnimation
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.CustomSpinnerAdapter
import com.dev.ecomercesupplier.adapter.ServedCountriesAdapter
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.model.ModelForAccountType
import com.dev.ecomercesupplier.model.ModelForSpinner
import com.dev.ecomercesupplier.model.ServeCountries
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_register_2.*
import kotlinx.android.synthetic.main.activity_register_2.iv_back_register_2
import kotlinx.android.synthetic.main.activity_register_bank.*
import org.json.JSONArray

class RegisterBankActivity : AppCompatActivity() {
    private var businessName : String ?= null
    private var businessEmail : String ?= null
    private var businessContact : String ?= null
    private var password : String ?= null
    private var cnfrmPassword : String ?= null
    private var profilePath : String ?= null
    private var selectCountryCode : String ?= null
    private var accountHolderName : String ?= null
    private var bankAccountNumber : String ?= null
    private var ibanNumber : String ?= null
    private var countryList=ArrayList<ModelForSpinner>()
    private var accountList= java.util.ArrayList<ModelForAccountType>()
    var servedCountriesList=ArrayList<ServeCountries>()
    var is_back = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_bank)
        Utility.setLanguage(this, SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
    }

    private fun setUpViews() {
        is_back = false
        ivBackBankActivity.setOnClickListener {
            ivBackBankActivity.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, ivBackBankActivity)
            is_back = true
            onBackPressed()
        }

        if (intent!=null){
            if(intent.extras!=null){
                businessName = intent.getStringExtra("businessName")
                businessEmail = intent.getStringExtra("businessEmail")
                businessContact = intent.getStringExtra("businessContact")
                password = intent.getStringExtra("password")
                cnfrmPassword = intent.getStringExtra("cnfrmPassword")
                profilePath = intent.getStringExtra("profilePath")
                selectCountryCode = intent.getStringExtra("country_code")
                servedCountriesList = intent.getSerializableExtra("countryServedList") as ArrayList<ServeCountries>
                countryList = intent.getSerializableExtra("countryList") as ArrayList<ModelForSpinner>
                accountList = intent.getSerializableExtra("accountList") as ArrayList<ModelForAccountType>
            }
        }

        val gson = Gson()
        val json = gson.toJson(accountList)

        SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.accountList, json)

        btnNextRegisterBank.setOnClickListener {
            btnNextRegisterBank.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnNextRegisterBank)
            validateAndSignUp()
        }
    }

    private fun validateAndSignUp() {
        accountHolderName = etBankName.text.toString().trim()
        bankAccountNumber = etBankAccountNumber.text.toString().trim()
        ibanNumber = etIBAN.text.toString().trim()

        if (TextUtils.isEmpty(accountHolderName)){
            etBankName.requestFocus()
            etBankName.error = getString(R.string.please_enter_account_holder_name)
        }else if (TextUtils.isEmpty(bankAccountNumber)){
            etBankAccountNumber.requestFocus()
            etBankAccountNumber.error = getString(R.string.please_enter_account_number)
        }else if (bankAccountNumber!!.length<7 && bankAccountNumber!!.length>15){
            etBankAccountNumber.requestFocus()
            etBankAccountNumber.error = getString(R.string.please_enter_valid_account_number)
        }else if (TextUtils.isEmpty(ibanNumber)){
            etIBAN.requestFocus()
            etIBAN.error = getString(R.string.please_enter_iban_number)
        }else if (!ibanNumber!!.contains(bankAccountNumber!!, false)){
            etIBAN.requestFocus()
            etIBAN.error = getString(R.string.please_enter_valid_iban_number)
        }else{
            continueToNextPage()
        }
    }

    private fun continueToNextPage() {
        val bundle = Bundle()
        bundle.putString("businessName", businessName)
        bundle.putString("businessEmail", businessEmail)
        bundle.putString("country_code", selectCountryCode)
        bundle.putString("businessContact", businessContact)
        bundle.putString("password", password)
        bundle.putString("cnfrmPassword", cnfrmPassword)
        bundle.putString("profilePath", profilePath)
        bundle.putString("accountHolderName", accountHolderName)
        bundle.putString("bankAccountNumber", bankAccountNumber)
        bundle.putString("ibanNumber", ibanNumber)
        bundle.putSerializable("countryList", countryList)
        bundle.putSerializable("countryServedList", servedCountriesList)
        bundle.putSerializable("accountList", accountList)
        startActivity(Intent(this, RegisterActivity_2::class.java).putExtras(bundle))
    }
}