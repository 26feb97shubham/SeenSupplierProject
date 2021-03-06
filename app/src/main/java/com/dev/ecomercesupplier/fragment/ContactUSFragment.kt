package com.dev.ecomercesupplier.fragment

import android.os.Bundle
import android.text.TextUtils
import android.view.*
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_contact_u_s.view.*
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
 * Use the [ContactUSFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactUSFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    var name: String = ""
    var email: String = ""
    var message: String = ""

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
        mView = inflater.inflate(R.layout.fragment_contact_u_s, container, false)
        Utility.setLanguage(requireContext(), SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        return mView
    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility= View.VISIBLE

        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        mView.btnSend.setOnClickListener {
            mView.btnSend.startAnimation(AlphaAnimation(1f, 0.5f))
            validateAndContactUS()
        }
    }
    private fun validateAndContactUS() {
        name = mView.edtName.text.toString()
        email= mView.edtEmail.text.toString()
        message= mView.edtMessage.text.toString()

        if (TextUtils.isEmpty(name)) {
            mView.edtName.requestFocus()
            mView.edtName.error=getString(R.string.please_enter_your_full_name)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_mob_number))

        }
        else if (!SharedPreferenceUtility.getInstance().isCharacterAllowed(name)) {
            mView.edtName.requestFocus()
            mView.edtName.error=getString(R.string.emojis_are_not_allowed)
//              LogUtils.shortToast(this, getString(R.string.emojis_are_not_allowed))
        }

        else if (TextUtils.isEmpty(email)) {
            mView.edtEmail.requestFocus()
            mView.edtEmail.error=getString(R.string.please_enter_your_email)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else if (!SharedPreferenceUtility.getInstance().isEmailValid(email)) {
            mView.edtEmail.requestFocus()
            mView.edtEmail.error=getString(R.string.please_enter_valid_email)
//            LogUtils.shortToast(requireContext(), getString(R.string.password_length_valid))
        }
        else if (TextUtils.isEmpty(message)) {
            mView.edtMessage.requestFocus()
            mView.edtMessage.error=getString(R.string.please_enter_message)
//            LogUtils.shortToast(requireContext(), getString(R.string.please_enter_your_password))
        }
        else {
            contactUs()
        }
    }

    private fun contactUs() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("name", "email", "message", "lang"),
                arrayOf(name, email.trim({ it <= ' ' }), message, SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))


        val call = apiInterface.contactUs(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                            findNavController().navigate(R.id.homeFragment)
                        }
                        else {
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
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
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AboutUSFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ContactUSFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}