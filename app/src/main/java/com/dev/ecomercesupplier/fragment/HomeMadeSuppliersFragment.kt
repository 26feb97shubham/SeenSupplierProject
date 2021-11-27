package com.dev.ecomercesupplier.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.CategoryListAdapter
import com.dev.ecomercesupplier.adapter.NameListAdapter
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.CategoryName
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_brands.view.*
import org.json.JSONArray

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeMadeSuppliersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeMadeSuppliersFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView:View
    lateinit var nameAdapter: NameListAdapter
    var catNameList=ArrayList<CategoryName>()
    lateinit var categoryListAdapter: CategoryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_home_made_suppliers, container, false)
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

        setNameTab()

        mView.nameLayout.setOnClickListener {
            mView.nameLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setNameTab()
        }
        mView.categoryLayout.setOnClickListener {
            mView.categoryLayout.startAnimation(AlphaAnimation(1f, 0.5f))
            setCategoryTab()
        }

    }

    private fun setCategoryTab() {
        mView.nameView.isSelected=false
        mView.categoryView.isSelected=true
        categoryListAdapter= CategoryListAdapter(requireContext(), catNameList, object : ClickInterface.ClickArrayInterface{
            override fun clickArray(idArray: JSONArray) {
            }


        })
        mView.rvList.layoutManager= GridLayoutManager(requireContext(), 2)
        mView.rvList.adapter=categoryListAdapter

        setCatNameData()
    }

    private fun setNameTab() {
        mView.nameView.isSelected=true
        mView.categoryView.isSelected=false
        nameAdapter= NameListAdapter(requireContext(), catNameList)
        mView.rvList.layoutManager= LinearLayoutManager(requireContext())
        mView.rvList.adapter=nameAdapter

        setCatNameData()
    }

    private fun setCatNameData() {
        catNameList.clear()

        val c = CategoryName()
        c.id=0
        c.name="Supplier name"
        c.catName="Gadgets"
        c.address="Dubai, UAE"
        c.rating=4.5
        catNameList.add(c)

        val c2 = CategoryName()
        c2.id=0
        c2.name="Supplier name"
        c2.catName="Fashion"
        c2.address="Sharjah, UAE"
        c2.rating=1.5
        catNameList.add(c2)

        val c3 = CategoryName()
        c3.id=0
        c3.name="Supplier name"
        c3.catName="Gadgets"
        c3.address="Dubai, UAE"
        c3.rating=4.0
        catNameList.add(c3)

        val c4 = CategoryName()
        c4.id=0
        c4.name="Supplier name"
        c4.catName="Fashion"
        c4.address="Dubai, UAE"
        c4.rating=3.5
        catNameList.add(c4)

        val c5 = CategoryName()
        c5.id=0
        c5.name="Supplier name"
        c5.catName="Gadgets"
        c5.address="Sharjah, UAE"
        c5.rating=2.5
        catNameList.add(c5)

        val c6 = CategoryName()
        c6.id=0
        c6.name="Supplier name"
        c6.catName="Fashion"
        c6.address="Dubai, UAE"
        c6.rating=2.5
        catNameList.add(c6)

        nameAdapter.notifyDataSetChanged()



    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeMadeSuppliersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeMadeSuppliersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}