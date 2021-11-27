package com.dev.ecomercesupplier.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.CompoundButton
import androidx.navigation.fragment.findNavController
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.activity.ChooseCategoriesActivity
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_payment_details.view.*
import okhttp3.ResponseBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PaymentDetailsFragment.newInstance] factory method to
 * create an instance of requreContext() fragment.
 */
class PaymentDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    lateinit var mView: View
    private var param1: String? = null
    private var param2: String? = null
    private var amount: String=""
    private var packagePlan: String=""
    private var detail: String=""
    private var pack_id: Int=0
    var cardNumber:String=""
    var cvv:String=""
    var exMonthYear:String=""
    var isSave:Int=0
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            amount = it.getString("amount").toString()
            packagePlan = it.getString("packagePlan").toString()
            detail = it.getString("detail").toString()
            pack_id = it.getInt("pack_id", 0)
           
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for requreContext() fragment
        mView = inflater.inflate(R.layout.fragment_payment_details, container, false)
        setUpViews()
        return mView
    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility= View.VISIBLE


        mView.txtPlanName.text=packagePlan
        mView.txtPrice.text=amount
        mView.packDesc.text=detail

        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }
        mView.btnContinue.setOnClickListener {
            mView.btnContinue.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView.btnContinue)
            validatePackagePlan()
           
        }

        mView.switchSave.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                isSave = if(isChecked){
                    1
                } else{
                    0
                }
            }

        })

        /*mView.edtCardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
                when{
                    start == 3 && start+added == 4  -> {
                        mView.edtCardNumber.setText(p0.toString()+" ")
                        mView.edtCardNumber.setSelection(5)
                    }
                    start == 5 && start-removed == 4  -> {
                        mView.edtCardNumber.setText(p0.toString().replace(" ", ""))
                        mView.edtCardNumber.setSelection(4)
                    }
                    start == 8 && start+added == 9  -> {
                        mView.edtCardNumber.setText(p0.toString()+" ")
                        mView.edtCardNumber.setSelection(10)
                    }
                    start == 10 && start-removed == 9  -> {
                        mView.edtCardNumber.setText(p0.toString().replace(" ", ""))
                        mView.edtCardNumber.setSelection(9)
                    }
                    start == 15 && start+added == 16  -> {
                        mView.edtCardNumber.setText(p0.toString()+" ")
                        mView.edtCardNumber.setSelection(17)
                    }
                    start == 17 && start-removed == 16  -> {
                        mView.edtCardNumber.setText(p0.toString().replace(" ", ""))
                        mView.edtCardNumber.setSelection(16)
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })*/

         mView.edtExpiry.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, start: Int, removed: Int, added: Int) {
                when {
                    p0.toString().length == 1 && p0.toString() >= "2" -> {
                         mView.edtExpiry.setText("0$p0/")
                         mView.edtExpiry.setSelection(3)
                    }
                    p0.toString().length == 2 && p0.toString()=="00" -> {
                         mView.edtExpiry.setText("0")
                         mView.edtExpiry.setSelection(1)
                    }
                    p0.toString().length == 2 && p0.toString() > "12" -> {
                         mView.edtExpiry.setText("1")
                         mView.edtExpiry.setSelection(1)
                    }
                    start == 1 && start+added == 2 && p0?.contains('/') == false  -> {
                         mView.edtExpiry.setText("$p0/")
                         mView.edtExpiry.setSelection(3)
                    }

                    start == 3 && start-removed == 2 && p0?.contains('/') == true -> {
                         mView.edtExpiry.setText(p0.toString().replace("/", ""))
                         mView.edtExpiry.setSelection(2)
                    }
                    start == 2 && start+added == 3 && p0?.contains('/') == false -> {
                         mView.edtExpiry.setText(p0.toString().replace(p0[2].toString(), "/"+p0[2]))
                         mView.edtExpiry.setSelection(4)
                    }
                    /* start == 3 && start+added == 4 && p0!![3].toString() != "2" -> {
                          mView.edtExpiry.setText(p0.toString().replace(p0[3].toString(), ""))
                          mView.edtExpiry.setSelection(3)
                     }
                     start == 4 && start+added == 5 && p0!![4].toString() != "0" -> {
                          mView.edtExpiry.setText(p0.toString().replace(p0[4].toString(), ""))
                          mView.edtExpiry.setSelection(4)
                     }
                     start == 5 && start+added == 6 && (p0!![5].toString() == "0" || p0[5].toString() == "1") -> {
                          mView.edtExpiry.setText(p0.toString().replace(p0[5].toString(), ""))
                          mView.edtExpiry.setSelection(5)
                     }
                     start == 6 && start+added == 7 && p0!![6].toString() == "0" -> {
                          mView.edtExpiry.setText(p0.toString().replace(p0[6].toString(), ""))
                          mView.edtExpiry.setSelection(6)
                     }
                     p0.toString().length == 4 && p0!![3].toString() != "2" -> {
                          mView.edtExpiry.setText(p0.toString().replace(p0[3].toString(), ""))
                          mView.edtExpiry.setSelection(3)
                     }
                     p0.toString().length == 5 && p0!![4].toString() != "0" -> {
                          mView.edtExpiry.setText(p0.toString().replace(p0[4].toString(), ""))
                          mView.edtExpiry.setSelection(4)
                     }*/
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }
    private fun validatePackagePlan() {
        cardNumber = mView.edtCardNumber.text.toString()
        cvv=  mView.edtCVV.text.toString()
        exMonthYear=  mView.edtExpiry.text.toString()


        if (TextUtils.isEmpty(cardNumber)) {
            mView.edtCardNumber.requestFocus()
            mView.edtCardNumber.error=getString(R.string.please_enter_your_card_number)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_mob_number))

        }
        else if ((cardNumber.length != 16)) {
            mView.edtCardNumber.requestFocus()
            mView.edtCardNumber.error=getString(R.string.please_enter_valid_card_number)
//            LogUtils.shortToast(requireContext(), getString(R.string.mob_num_length_valid))
        }


        else if (TextUtils.isEmpty(exMonthYear)) {
             mView.edtExpiry.requestFocus()
             mView.edtExpiry.error=getString(R.string.please_enter_expiry_month_and_year)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_length_valid))
        }
        else if (TextUtils.isEmpty(cvv)) {
             mView.edtCVV.requestFocus()
             mView.edtCVV.error=getString(R.string.please_enter_cvv)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else if (cvv.length<3) {
             mView.edtCVV.requestFocus()
             mView.edtCVV.error=getString(R.string.please_enter_valid_cvv)
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

        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("user_id", "package_id", "card_number", "expiry", "cvv", "save_card", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), pack_id.toString(), cardNumber, exMonthYear, cvv
                , isSave.toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.packagePlanPayment(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        if (jsonObject.getInt("response") == 1){
                            findNavController().navigate(R.id.homeFragment)

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
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }
    companion object {
        /**
         * Use requreContext() factory method to create a new instance of
         * requreContext() fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PaymentDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PaymentDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}