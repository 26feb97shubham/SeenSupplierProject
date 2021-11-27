package com.dev.ecomercesupplier.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_package_details.*
import kotlinx.android.synthetic.main.activity_package_details.backImg
import kotlinx.android.synthetic.main.activity_package_details.progressBar
import kotlinx.android.synthetic.main.activity_sign_up.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PackageDetailsActivity : AppCompatActivity() {
    var amount:String=""
    var packagePlan:String=""
    var detail:String=""
    var cardNumber:String=""
    var cvv:String=""
    var exMonthYear:String=""
    var pack_id:Int=0
    var isSave:Int=0
    var user_id: String=""
    private val EMPTY_STRING = ""
    private val WHITE_SPACE = " "
    private var lastSource = EMPTY_STRING
    val SPACING_CHAR=' '
    private val TOTAL_SYMBOLS = 19 // size of pattern 0000-0000-0000-0000

    private val TOTAL_DIGITS = 16 // max numbers of digits in pattern: 0000 x 4

    private val DIVIDER_MODULO = 5 // means divider position is every 5th symbol beginning with 1

    private val DIVIDER_POSITION = DIVIDER_MODULO - 1 // means divider position is every 4th symbol beginning with 0

    private val DIVIDER = ' '
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_details)
        setUpViews()
    }

    private fun setUpViews() {
        if(intent.extras != null){
            amount=intent.getStringExtra("amount").toString()
            packagePlan=intent.getStringExtra("packagePlan").toString()
            detail=intent.getStringExtra("detail").toString()
            user_id=intent.getStringExtra("user_id").toString()
            pack_id=intent.getIntExtra("pack_id", 0)


        }

//        txtPlanName.text=packagePlan
        txtDuration.text = packagePlan
        txtPrice.text=amount
        packDesc.text=detail

        btnContinue.setOnClickListener {
            btnContinue.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, btnContinue)
            validatePackagePlan()
        }
        backImg.setOnClickListener {
            backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(this, backImg)
            onBackPressed()
        }

        switchSave.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                isSave = if(isChecked){
                    1
                } else{
                    0
                }
            }

        })

        /*edtCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
                when{
                        start == 3 && start+added == 4  -> {
                            edtCardNumber.setText(p0.toString()+" ")
                            edtCardNumber.setSelection(5)
                        }
                        start == 5 && start-removed == 4  -> {
                            edtCardNumber.setText(p0.toString().replace(" ", ""))
                            edtCardNumber.setSelection(4)
                        }
                    start == 8 && start+added == 9  -> {
                        edtCardNumber.setText(p0.toString()+" ")
                        edtCardNumber.setSelection(10)
                    }
                    start == 10 && start-removed == 9  -> {
                        edtCardNumber.setText(p0.toString().replace(" ", ""))
                        edtCardNumber.setSelection(9)
                    }
                    start == 15 && start+added == 16  -> {
                        edtCardNumber.setText(p0.toString()+" ")
                        edtCardNumber.setSelection(17)
                    }
                    start == 17 && start-removed == 16  -> {
                        edtCardNumber.setText(p0.toString().replace(" ", ""))
                        edtCardNumber.setSelection(16)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })*/

        edtExpiry.doOnTextChanged { p0, start, removed, added ->
            when {
                p0.toString().length == 1 && p0.toString() >= "2" -> {
                    edtExpiry.setText("0$p0/")
                    edtExpiry.setSelection(3)
                }
                p0.toString().length == 2 && p0.toString()=="00" -> {
                    edtExpiry.setText("0")
                    edtExpiry.setSelection(1)
                }
                p0.toString().length == 2 && p0.toString() > "12" -> {
                    edtExpiry.setText("1")
                    edtExpiry.setSelection(1)
                }
                start == 1 && start+added == 2 && p0?.contains('/') == false  -> {
                    edtExpiry.setText("$p0/")
                    edtExpiry.setSelection(3)
                }

                start == 3 && start-removed == 2 && p0?.contains('/') == true -> {
                    edtExpiry.setText(p0.toString().replace("/", ""))
                    edtExpiry.setSelection(2)
                }
                start == 2 && start+added == 3 && p0?.contains('/') == false -> {
                    edtExpiry.setText(p0.toString().replace(p0[2].toString(), "/"+p0[2]))
                    edtExpiry.setSelection(4)
                }
            }
        }

    }
    private fun isInputCorrect(s: Editable, totalSymbols: Int, dividerModulo: Int, divider: Char): Boolean {
        var isCorrect = s.length <= totalSymbols // check size of entered string
        for (i in 0 until s.length) { // check that every element is right
            isCorrect = if (i > 0 && (i + 1) % dividerModulo == 0) {
                isCorrect and (divider == s[i])
            } else {
                isCorrect and Character.isDigit(s[i])
            }
        }
        return isCorrect
    }

    private fun buildCorrectString(digits: CharArray, dividerPosition: Int, divider: Char): String? {
        val formatted = StringBuilder()
        for (i in digits.indices) {
            if (digits[i].toInt() != 0) {
                formatted.append(digits[i])
                if (i > 0 && i < digits.size - 1 && (i + 1) % dividerPosition == 0) {
                    formatted.append(divider)
                }
            }
        }
        return formatted.toString()
    }
    private fun getDigitArray(s: Editable?, size: Int): CharArray {
        val digits = CharArray(size)
        var index = 0
        var i = 0
        while (i < s!!.length && index < size) {
            val current = s[i]
            if (Character.isDigit(current)) {
                digits[index] = current
                index++
            }
            i++
        }
        return digits
    }

    private fun validatePackagePlan() {
        cardNumber = edtCardNumber.text.toString()
        cvv= edtCVV.text.toString()
        exMonthYear= edtExpiry.text.toString()


        if (TextUtils.isEmpty(cardNumber)) {
            edtCardNumber.requestFocus()
            edtCardNumber.error=getString(R.string.please_enter_your_card_number)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_mob_number))

        }
        else if ((cardNumber.length != 16)) {
            edtCardNumber.requestFocus()
            edtCardNumber.error=getString(R.string.please_enter_valid_card_number)
//            LogUtils.shortToast(requireContext(), getString(R.string.mob_num_length_valid))
        }


        else if (TextUtils.isEmpty(exMonthYear)) {
            edtExpiry.requestFocus()
            edtExpiry.error=getString(R.string.please_enter_expiry_month_and_year)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_length_valid))
        }
        else if (TextUtils.isEmpty(cvv)) {
            edtCVV.requestFocus()
            edtCVV.error=getString(R.string.please_enter_cvv)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else if (cvv.length<3) {
            edtCVV.requestFocus()
            edtCVV.error=getString(R.string.please_enter_valid_cvv)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else{
            packagePlanPayment()
        }

      /*  else {
            if(remembered){
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsRemembered, true)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Phone, phone)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Password, password)
            }
            else{
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.IsRemembered, false)
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Phone, "")
                SharedPreferenceUtility.getInstance().save(SharedPreferenceUtility.Password, "")
            }
            getLogin()
        }*/
    }

    private fun packagePlanPayment() {

        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "package_id", "card_number", "expiry", "cvv", "save_card", "lang"),
                arrayOf(user_id, pack_id.toString(), cardNumber, exMonthYear, cvv
                        , isSave.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.packagePlanPayment(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        LogUtils.shortToast(this@PackageDetailsActivity, jsonObject.getString("message"))
                        if (jsonObject.getInt("response") == 1){
                            startActivity(Intent(this@PackageDetailsActivity, ChooseCategoriesActivity::class.java).putExtra("user_id", user_id))

                        }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<ResponseBody?>, throwable: Throwable) {
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(this@PackageDetailsActivity, getString(R.string.check_internet))
                progressBar.visibility= View.GONE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
}