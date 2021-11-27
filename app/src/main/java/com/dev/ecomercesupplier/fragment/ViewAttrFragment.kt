package com.dev.ecomercesupplier.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.adapter.ViewAttrAdapter
import com.dev.ecomercesupplier.interfaces.ClickInterface
import com.dev.ecomercesupplier.model.Attributes
import com.dev.ecomercesupplier.model.Categories
import kotlinx.android.synthetic.main.fragment_view_attr.view.*
import org.json.JSONArray

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewAttrFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewAttrFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View
    lateinit var viewAttrAdapter: ViewAttrAdapter
    var header:String=""
    var att_count:Int=0
  /*  var attributeArray=JSONArray()*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            header = it.getString("header").toString()
            att_count = it.getInt("att_count", 0)
          /*  val attrString = it.getString("attributeArray").toString()
            if(!TextUtils.isEmpty(attrString)){
                attributeArray= JSONArray(attrString)
            }*/
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_view_attr, container, false)
        setUpViews()
       /* setAttributesData()*/
        return mView
    }

    private fun setUpViews() {

        if(!TextUtils.isEmpty(header)){
            mView.title.text=header
        }
        mView.rvList.layoutManager=LinearLayoutManager(requireContext())
        viewAttrAdapter= ViewAttrAdapter(requireContext(), AddItemFragment.attributeArray, att_count, object :ClickInterface.ClickPosInterface{
            override fun clickPostion(pos: Int) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.delete))
                builder.setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_product_attribute))
                builder.setPositiveButton(getString(R.string.ok)) { dialog, which ->
                    AddItemFragment.attributeArray.remove(pos)
                    viewAttrAdapter.notifyDataSetChanged()

                    if(AddItemFragment.attributeArray.length()==0){
                        mView.txtNoAttributes.visibility=View.VISIBLE
                        mView.rvList.visibility=View.GONE
                    }
                    else{
                        mView.txtNoAttributes.visibility=View.GONE
                        mView.rvList.visibility=View.VISIBLE
                    }

                }
                builder.setNegativeButton(getString(R.string.cancel), null)

                val dialog = builder.create()
                dialog.show()

            }

        })
        mView.rvList.adapter=viewAttrAdapter




    }

/*    private fun setAttributesData() {
        for(i in 0 until attributeArray.length()){
            val obj= attributeArray.getJSONObject(i)

            val a =Attributes()
            a.id=obj.getInt("id")
            a.name=obj.getString("price")
            a.type=obj.getString("quantity")
            a.value=obj.getJSONArray("data")
            list.add(a)
            viewAttrAdapter.notifyDataSetChanged()
        }
    }*/

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewAttrFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                ViewAttrFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}