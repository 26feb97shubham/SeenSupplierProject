package com.dev.ecomercesupplier.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.GlobalItemListAdapter
import com.dev.ecomercesupplier.model.CategoryName
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_global_market_details.view.*


class GlobalMarketDetailsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var ref_key: String=""

    lateinit var mView:View
    lateinit var globalItemListAdapter:GlobalItemListAdapter
    var catNameList=ArrayList<CategoryName>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            ref_key = it.getString("ref_key").toString()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_global_market_details, container, false)
        setUpViews()
        return mView
    }

    private fun setUpViews() {
        if(ref_key=="1"){
            mView.txtName.text=getString(R.string.united_arab_emirates)
            mView.imgLogo.setImageResource(R.drawable.uae)
        }
        else if(ref_key=="2"){
            mView.txtName.text=getString(R.string.saudi_arabia)
            mView.imgLogo.setImageResource(R.drawable.saudi_arabia)
        }
        else if(ref_key=="3"){
            mView.txtName.text=getString(R.string.oman)
            mView.imgLogo.setImageResource(R.drawable.oman)
        }
        else if(ref_key=="4"){
            mView.txtName.text=getString(R.string.kuwait)
            mView.imgLogo.setImageResource(R.drawable.kuwait)
        }
        else if(ref_key=="5"){
            mView.txtName.text=getString(R.string.bahrain)
            mView.imgLogo.setImageResource(R.drawable.bahrain)
        }
        requireActivity().other_frag_backImg.visibility= View.VISIBLE
        requireActivity().other_frag_backImg.setOnClickListener {
             requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }
        globalItemListAdapter= GlobalItemListAdapter(requireContext(), catNameList)
        mView.rvList.layoutManager=LinearLayoutManager(requireContext())
        mView.rvList.adapter=globalItemListAdapter

        setCatNameData()

    }
    private fun setCatNameData() {
        catNameList.clear()

        val c = CategoryName()
        c.id=0
        c.name="Supplier name"
        c.catName="Gadgets"
        c.address="Dubai, UAE"
        c.rating=3.5
        catNameList.add(c)

        val c2 = CategoryName()
        c2.id=0
        c2.name="Blogger name"
        c2.catName="Fashion"
        c2.address="Sharjah, UAE"
        c2.rating=2.5
        catNameList.add(c2)

        val c3 = CategoryName()
        c3.id=0
        c3.name="Brand name"
        c3.catName="Gadgets"
        c3.address="Dubai, UAE"
        c3.rating=3.5
        catNameList.add(c3)

        val c4 = CategoryName()
        c4.id=0
        c4.name="Supplier name"
        c4.catName="Fashion"
        c4.address="Dubai, UAE"
        c4.rating=4.5
        catNameList.add(c4)

        val c5 = CategoryName()
        c5.id=0
        c5.name="Blogger name"
        c5.catName="Gadgets"
        c5.address="Sharjah, UAE"
        c5.rating=5.0
        catNameList.add(c5)

        val c6 = CategoryName()
        c6.id=0
        c6.name="Brand name"
        c6.catName="Fashion"
        c6.address="Dubai, UAE"
        c6.rating=1.0
        catNameList.add(c6)

        globalItemListAdapter.notifyDataSetChanged()



    }

}