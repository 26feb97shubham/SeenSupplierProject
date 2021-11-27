package com.dev.ecomercesupplier.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.arasthel.spannedgridlayoutmanager.SpanSize
import com.arasthel.spannedgridlayoutmanager.SpannedGridLayoutManager
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.GalleryListAdapter
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Categories
import com.dev.ecomercesupplier.model.Galleries
import com.dev.ecomercesupplier.rest.ApiClient
import com.dev.ecomercesupplier.rest.ApiInterface
import com.dev.ecomercesupplier.utils.LogUtils
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_gallery.view.*
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
 * Use the [GalleryFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GalleryFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    lateinit var galleryListAdapter: GalleryListAdapter
    var galleryList=ArrayList<Galleries>()
    var accessValue:Int=0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_gallery, container, false)
        setUpViews()
        getHomes(false)
        return mView
    }

    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility= View.VISIBLE
        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }

        mView.btnUploadPhotosVideos.setOnClickListener {
            mView.btnUploadPhotosVideos.startAnimation(AlphaAnimation(1f, 0.5f))
            findNavController().navigate(R.id.action_galleryFragment_to_uploadImageVideoFragment)
        }

        mView.swipeRefresh.setOnRefreshListener {
            getHomes(true)
        }

        /*mView.rvList.layoutManager = GridLayoutManager(activity, 3).also {
            it.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int {

                    return if(position==0){
                        3
                    } else if (position == 4 || position==accessValue){
                        accessValue=position+6
                        2
                    } else{
                        1
                    }

                }
            }
        }*/
        val spannedGridLayoutManager = SpannedGridLayoutManager(
            orientation = SpannedGridLayoutManager.Orientation.VERTICAL,
            spans = 3)
        spannedGridLayoutManager.spanSizeLookup = SpannedGridLayoutManager.SpanSizeLookup { position ->
            when {
                position == 0 -> {
                    /**
                     * 150f is now static
                     * should calculate programmatically in runtime
                     * for to manage header hight for different resolution devices
                     */
                    SpanSize(3, 2)
                }
                position == 4 || position==accessValue ->{
                    accessValue=position+6
                    SpanSize(2, 2)
                }

                else ->
                    SpanSize(1, 1)
            }
        }
        mView.rvList.layoutManager =spannedGridLayoutManager

                /*mView.rvList.layoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)*/
        galleryListAdapter= GalleryListAdapter(requireContext(), galleryList, object : ClickInterface.ClickPosInterface {
            override fun clickPostion(pos: Int) {
                val bundle=Bundle()
                bundle.putString("thumbnail", galleryList[pos].thumbnail)
                bundle.putString("files", galleryList[pos].files)
                findNavController().navigate(R.id.action_galleryFragment_to_playerViewFragment, bundle)
            }

        })

//        mView.rvList.adapter=galleryListAdapter


//        setCategoryData()
    }
    private fun getHomes(isRefresh: Boolean) {
      /*  mView.rvList.recycledViewPool.clear()
        mView.rvList.setItemViewCacheSize(0)
        galleryList.clear()
        galleryListAdapter.notifyDataSetChanged()*/
        requireActivity().window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        if(!isRefresh) {
            mView.progressBar.visibility = View.VISIBLE
        }

        val apiInterface = ApiClient.getClient()!!.create(ApiInterface::class.java)

        val builder = ApiClient.createBuilder(arrayOf("user_id","lang"),
                arrayOf(SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.UserId, 0].toString(), SharedPreferenceUtility.getInstance()[SharedPreferenceUtility.SelectedLang, ""]))

        val call = apiInterface.getHomes(builder.build())
        call!!.enqueue(object : Callback<ResponseBody?> {
            override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                try {
                    if (response.body() != null) {
                        val jsonObject = JSONObject(response.body()!!.string())

                        val gallery = jsonObject.getJSONArray("gallery")
                        galleryList.clear()
                        for (i in 0 until gallery.length()) {
                            val jsonObj = gallery.getJSONObject(i)
                            val g = Galleries()
                            g.id=jsonObj.getInt("id")
                            g.files=jsonObj.getString("files")
                            g.thumbnail=jsonObj.getString("thumbnail")
                            galleryList.add(g)
                        }
                        if(galleryList.size==0){
                            mView.txtNoDataFound.visibility=View.VISIBLE
                            mView.rvList.visibility=View.GONE
                        }
                        else{
                            mView.txtNoDataFound.visibility=View.GONE
                            mView.rvList.visibility=View.VISIBLE
                        }
                        mView.rvList.adapter=galleryListAdapter
//                        galleryListAdapter.notifyDataSetChanged()


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
                if(mView.swipeRefresh.isRefreshing){
                    mView.swipeRefresh.isRefreshing=false
                }
                LogUtils.e("msg", throwable.message)
                LogUtils.shortToast(requireContext(), getString(R.string.check_internet))
                mView.progressBar.visibility = View.GONE
                requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        })
    }
    /*private fun setCategoryData() {
        galleryList.clear()

        val g = Galleries()
        g.id=0
        g.name="Groceries"
        g.icon=R.drawable.one
        galleryList.add(g)

        val c2 = Galleries()
        c2.id=1
        c2.name="Sports"
        c2.icon=R.drawable.four
        galleryList.add(c2)

        val c3 = Galleries()
        c3.id=2
        c3.name="Medicals"
        c3.icon=R.drawable.two
        galleryList.add(c3)

        val c4 = Galleries()
        c4.id=3
        c4.name="Books"
        c4.icon=R.drawable.three
        galleryList.add(c4)

        val c5 = Galleries()
        c5.id=4
        c5.name="Groccery"
        c5.icon=R.drawable.two
        galleryList.add(c5)

        val c6 = Galleries()
        c6.id=5
        c6.name="Clothes"
        c6.icon=R.drawable.three
        galleryList.add(c6)

        val c7 = Galleries()
        c7.id=6
        c7.name="Shoes"
        c7.icon=R.drawable.four
        galleryList.add(c7)

        val c8 = Galleries()
        c8.id=7
        c8.name="Clothes"
        c8.icon=R.drawable.one
        galleryList.add(c8)

        val c9 = Galleries()
        c9.id=8
        c9.name="Shoes"
        c9.icon=R.drawable.four
        galleryList.add(c9)

        val c10 = Galleries()
        c10.id=9
        c10.name="Clothes"
        c10.icon=R.drawable.three
        galleryList.add(c10)

        val c11 = Galleries()
        c11.id=10
        c11.name="Grocerry"
        c11.icon=R.drawable.two
        galleryList.add(c11)

        val c12 = Galleries()
        c12.id=11
        c12.name="Clothes"
        c12.icon=R.drawable.three
        galleryList.add(c12)

        val c13 = Galleries()
        c13.id=12
        c13.name="Shoes"
        c13.icon=R.drawable.four
        galleryList.add(c13)

        galleryListAdapter.notifyDataSetChanged()

    }
*/
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GalleryFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                GalleryFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}