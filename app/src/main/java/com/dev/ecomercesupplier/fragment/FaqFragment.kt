package com.dev.ecomercesupplier.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.FaqAdapter
import com.dev.ecomercesupplier.model.Faq
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_faq.view.*
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
 * Use the [FaqFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FaqFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    lateinit var faqAdapter: FaqAdapter
    var list=ArrayList<Faq>()
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
        mView = inflater.inflate(R.layout.fragment_faq, container, false)
        setUpViews()
        getFaq()
        return mView
    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility= View.VISIBLE
        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }
    }
    private fun getFaq() {
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        mView.progressBar.visibility= View.VISIBLE

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)
        val builder = ApiClient.createBuilder(arrayOf("lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getFaq(builder.build())

        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                mView.progressBar.visibility= View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())
                        if (jsonObject.getInt("response") == 1){
                            mView.txtNoDataFound.visibility = View.GONE
                            mView.rvList.visibility = View.VISIBLE
                            val data = jsonObject.getJSONArray("data")
                            list.clear()
                            for(i in 0 until data.length()){
                                val obj = data.getJSONObject(i)
                                val f = Faq()
                                f.id = obj.getInt("id")
                                f.question = obj.getString("question")
                                f.question_arabic = obj.getString("question_arabic")
                                f.answer = obj.getString("answer")
                                f.answer_arabic = obj.getString("answer_arabic")
                                list.add(f)
                            }

                            mView.rvList.layoutManager= LinearLayoutManager(requireContext())
                            faqAdapter= FaqAdapter(requireContext(), list)
                            mView.rvList.adapter=faqAdapter

                        }
                        else {
                            mView.txtNoDataFound.visibility = View.VISIBLE
                            mView.rvList.visibility = View.GONE
                            LogUtils.shortToast(requireContext(), jsonObject.getString("message"))
                        }

                      /*  if(list.size==0){
                            mView.txtNoDataFound.visibility = View.VISIBLE
                            mView.rvList.visibility = View.GONE
                        }
                        else{
                            mView.txtNoDataFound.visibility = View.GONE
                            mView.rvList.visibility = View.VISIBLE
                        }
                        faqAdapter.notifyDataSetChanged()*/
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

    override fun onResume() {
        super.onResume()
        requireActivity().main_View.setBackgroundResource(R.drawable.faq_bg)
    }

    override fun onStop() {
        super.onStop()
        requireActivity().main_View.setBackgroundColor(Color.WHITE)
    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().main_View.setBackgroundColor(Color.WHITE)
    }

}