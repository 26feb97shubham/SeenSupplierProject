package com.dev.ecomercesupplier.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.core.text.HtmlCompat
import androidx.navigation.fragment.findNavController
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.about_us_more_info_frag_toolbar.*
import kotlinx.android.synthetic.main.about_us_more_info_frag_toolbar.view.*
import kotlinx.android.synthetic.main.activity_home.*
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
 * Use the [AboutUsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AboutUsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var mView : View?=null

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
        mView = inflater.inflate(R.layout.fragment_about_us, container, false)
        Utility.setLanguage(requireContext(), SharedPreferenceUtility.getInstance().get(SharedPreferenceUtility.SelectedLang, ""))
        setUpViews()
        getCmsContent()
        return mView
    }

    private fun getCmsContent() {
        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(
            arrayOf("lang"),
            arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""].toString())
        )
        val call: Call<ResponseBody?>? = apiInterface.getAboutUs(builder.build())


        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1) {
                            val data = jsonObject.getJSONObject("data")
                        } else {
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
            }
        })
    }

    private fun setUpViews() {
        requireActivity().about_us_fragment_toolbar.frag_about_us_backImg.setOnClickListener {
            requireActivity().about_us_fragment_toolbar.frag_about_us_backImg.startAnimation(
                AlphaAnimation(1f, 0.5f)
            )
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(
                requireContext(),
                requireActivity().about_us_fragment_toolbar.frag_about_us_backImg
            )
            findNavController().popBackStack()
        }

        /*requireActivity().about_us_fragment_toolbar.frag_about_us_menu.setOnClickListener {
            requireActivity().about_us_fragment_toolbar.frag_profile_notificationImg.startAnimation(
                AlphaAnimation(1f, 0.5f)
            )
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(
                requireContext(),
                requireActivity().about_us_fragment_toolbar.frag_profile_notificationImg
            )
            findNavController().navigate(R.id.notificationsFragment)
        }*/
    }

    override fun onResume() {
        super.onResume()
        requireActivity().toolbar.visibility = View.GONE
        requireActivity().other_frag_toolbar.visibility = View.GONE
        requireActivity().profile_fragment_toolbar.visibility = View.GONE
        requireActivity().about_us_fragment_toolbar.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().other_frag_toolbar.visibility = View.VISIBLE
        requireActivity().profile_fragment_toolbar.visibility = View.GONE
        requireActivity().about_us_fragment_toolbar.visibility = View.GONE
        requireActivity().toolbar.visibility = View.GONE
    }

    override fun onStop() {
        super.onStop()
        requireActivity().other_frag_toolbar.visibility = View.VISIBLE
        requireActivity().profile_fragment_toolbar.visibility = View.GONE
        requireActivity().other_frag_toolbar.setBackgroundResource(R.color.white)
        requireActivity().about_us_fragment_toolbar.visibility = View.GONE
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AboutUsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                AboutUsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}