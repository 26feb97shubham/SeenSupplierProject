package com.dev.ecomercesupplier.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.AlphaAnimation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.model.Categories
import com.dev.ecomercesupplier.model.Products
import com.dev.ecomercesupplier.model.ServeCountries
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_bank_details.view.*
import kotlinx.android.synthetic.main.fragment_bank_details.view.progressBar
import kotlinx.android.synthetic.main.fragment_profile.view.*
import kotlinx.android.synthetic.main.side_top_view.*
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
 * Use the [BankDetailsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BankDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var mView:View?=null
    var accountHolderName:String=""
    var accountNumber:String=""
    var iban_number:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_bank_details, container, false)
        setUpViews()
        getProfile()
        return mView
    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility = View.VISIBLE


        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        mView!!.btnSave.setOnClickListener {
            mView!!.btnSave.startAnimation(AlphaAnimation(1f, .5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), mView!!.btnSave)
            validateAndEditBankDetails()
        }

        mView!!.scrollView.setOnTouchListener { v, event ->
            mView!!.edtAccountHolderName.clearFocus()
            mView!!.edtAccountNumber.clearFocus()
            mView!!.edtIban.clearFocus()
            false
        }
    }

    private fun getProfile() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            mView!!.progressBar.visibility = View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.getProfile(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        if(jsonObject.getInt("response")==1){
                            val data= jsonObject.getJSONObject("data")
                            mView!!.edtAccountHolderName.setText(data.getString("account_holder_name"))
                            mView!!.edtAccountNumber.setText(data.getString("account_number"))
                            mView!!.edtIban.setText(data.getString("iban"))

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
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    private fun validateAndEditBankDetails() {
        accountHolderName=mView!!.edtAccountHolderName.text.toString()
        accountNumber=mView!!.edtAccountNumber.text.toString()
        iban_number=mView!!.edtIban.text.toString()
        when {
            TextUtils.isEmpty(accountHolderName) -> {
                mView!!.edtAccountHolderName.requestFocus()
                mView!!.edtAccountHolderName.error=getString(R.string.please_enter_account_holder_name)
            }
            TextUtils.isEmpty(accountNumber) -> {
                mView!!.edtAccountNumber.requestFocus()
                mView!!.edtAccountNumber.error=getString(R.string.please_enter_account_number)
            }
            accountNumber.length<9->{
                mView!!.edtAccountNumber.requestFocus()
                mView!!.edtAccountNumber.error=getString(R.string.please_enter_valid_account_number)
            }
            TextUtils.isEmpty(iban_number) -> {
                mView!!.edtIban.requestFocus()
                mView!!.edtIban.error=getString(R.string.please_enter_iban_number)
            }

           !SharedPreferenceUtility.getInstance().isIbanValid(iban_number, accountNumber)->{
               mView!!.edtIban.requestFocus()
               mView!!.edtIban.error=getString(R.string.please_enter_valid_iban_number)
           }
            else -> {
                editBankDetail()
            }
        }
    }

    private fun editBankDetail() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView!!.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createMultipartBodyBuilder(arrayOf("user_id", "account_holder_name", "account_number", "iban", "lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), accountHolderName, accountNumber, iban_number
                , SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.editBankDetail(builder!!.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        when {
                            jsonObject.getInt("response") == 1 -> {
                                LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                                findNavController().popBackStack()
                            }
                            else -> {
                                LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            }
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
                mView!!.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BankDetailsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                BankDetailsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}