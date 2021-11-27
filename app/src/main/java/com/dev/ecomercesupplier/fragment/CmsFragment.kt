package com.dev.ecomercesupplier.fragment

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.webkit.WebView
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.ecomercesupplier.MyWebViewClient
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_cms.view.*
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
 * Use the [CmsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CmsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    var title:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title", "")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_cms, container, false)
        setUpViews()
        getCmsContent()
        return mView
    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility= View.VISIBLE
        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }
//        mView.header.text=title


        mView.webView.webViewClient = MyWebViewClient()
        mView.webView.settings.javaScriptEnabled = true
        mView.webView.settings.setSupportZoom(true)
        mView.webView.getSettings().setBuiltInZoomControls(true)
        //Enable Multitouch if supported by ROM
        mView.webView.getSettings().setUseWideViewPort(true)
        mView.webView.getSettings().setLoadWithOverviewMode(false)
        mView.webView.setBackgroundColor(Color.TRANSPARENT)
        mView.webView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null);
    }
    private fun getCmsContent() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.UsergetClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

 /*       val call: Call<ResponseBody?>? = when(title){
            getString(R.string.terms_amp_conditions) -> apiInterface.getTermsConditions(builder.build())
            getString(R.string.privacy_and_policy) -> apiInterface.getPrivacyPolicy(builder.build())
            else -> apiInterface.getAboutUs(builder.build())
        }

*/

        val call: Call<ResponseBody?>? = when(title){
            getString(R.string.terms_amp_conditions) -> {
                requireActivity().main_View.setBackgroundResource(R.drawable.terms_background)
                requireActivity().about_us_fragment_toolbar.visibility=View.GONE
                mView.webView.visibility = View.VISIBLE
                apiInterface.getTermsConditions(builder.build())
            }
            getString(R.string.privacy_and_policy) -> {
                requireActivity().main_View.setBackgroundResource(R.drawable.data_policy_bg)
                requireActivity().about_us_fragment_toolbar.visibility=View.GONE
                mView.webView.visibility = View.VISIBLE
                apiInterface.getPrivacyPolicy(builder.build())
            }
            else -> {
                requireActivity().main_View.setBackgroundResource(R.drawable.about_us_bg)
                requireActivity().about_us_fragment_toolbar.visibility=View.VISIBLE
                mView.webView.visibility = View.GONE
                apiInterface.getAboutUs(builder.build())
            }
        }

        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1) {
                            val data = jsonObject.getJSONObject("data")
                            Log.e("url", data.getString("url"))
                            mView.webView.loadUrl(data.getString("url"))
                            //mView.txt.text = HtmlCompat.fromHtml(data.getString("content"), 0)
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
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })

    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().main_View.setBackgroundColor(Color.WHITE)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().main_View.setBackgroundColor(Color.WHITE)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment CmsFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                CmsFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}