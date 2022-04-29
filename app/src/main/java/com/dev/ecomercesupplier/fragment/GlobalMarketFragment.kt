package com.dev.ecomercesupplier.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.custom.Utility
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_global_market.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GlobalMarketFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GlobalMarketFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var mView: View

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
         mView = inflater.inflate(R.layout.fragment_global_market, container, false)
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

        mView.llUAE.setOnClickListener {
            mView.llUAE.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "1")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
        mView.llSA.setOnClickListener {
            mView.llSA.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "2")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
        mView.llOman.setOnClickListener {
            mView.llOman.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "3")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
        mView.llKuwait.setOnClickListener {
            mView.llKuwait.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "4")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }
        mView.llBahrain.setOnClickListener {
            mView.llBahrain.startAnimation(AlphaAnimation(1f, 0.5f))
            val bundle=Bundle()
            bundle.putString("ref_key", "5")
            findNavController().navigate(R.id.action_globalMarketFragment_to_globalMarketDetailsFragment, bundle)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment GlobalMarketFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            GlobalMarketFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}