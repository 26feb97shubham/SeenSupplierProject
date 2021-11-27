package com.dev.ecomercesupplier.fragment

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.dev.ecomercesupplier.R
import com.dev.ecomercesupplier.utils.SharedPreferenceUtility
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.fragment_player_view.view.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PlayerViewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PlayerViewFragment : Fragment() {
    var thumbnail:String=""
    var files:String=""
    lateinit var mView: View
    var simpleExoPlayer: SimpleExoPlayer?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            thumbnail = it.getString("thumbnail", "")
            files = it.getString("files", "")

        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_player_view, container, false)
        setUpViews()
        return mView
    }
    private fun setUpViews() {
        requireActivity().other_frag_backImg.visibility=View.VISIBLE
        requireActivity().menuImg.visibility=View.GONE
        requireActivity().notificationImg.visibility=View.GONE
        requireActivity().other_frag_backImg.setOnClickListener {
            requireActivity().other_frag_backImg.startAnimation(AlphaAnimation(1f, 0.5f))
            SharedPreferenceUtility.getInstance().hideSoftKeyBoard(requireContext(), requireActivity().other_frag_backImg)
            findNavController().popBackStack()
        }
        if(files != thumbnail){
            mView.ep_video_view.visibility=View.VISIBLE
            mView.image.visibility=View.GONE
            initPlayer()
        }
        else{
            mView.ep_video_view.visibility=View.GONE
            mView.image.visibility=View.VISIBLE
            Glide.with(requireContext()).load(thumbnail).placeholder(R.drawable.default_icon).into(mView.image)
        }

    }
    private fun initPlayer() {
        simpleExoPlayer= SimpleExoPlayer.Builder(requireContext()).build()
        mView.ep_video_view.player=simpleExoPlayer

        val uri= Uri.parse(files)
        val mediaItem= MediaItem.fromUri(uri)
        simpleExoPlayer!!.setMediaItem(mediaItem)

        playVideo()
    }

    private fun playVideo() {
        simpleExoPlayer!!.prepare()
        simpleExoPlayer!!.play()
    }



    override  fun onDestroy() {
        super.onDestroy()
        stopMedia()
    }

    override  fun onPause() {
        super.onPause()
        if(simpleExoPlayer != null){
            if(simpleExoPlayer!!.isPlaying){
                simpleExoPlayer!!.pause()
            }
        }

    }

    private fun stopMedia() {
        if(simpleExoPlayer != null){
            simpleExoPlayer!!.stop()
            simpleExoPlayer!!.release()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment PlayerViewFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
                PlayerViewFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }
}