package com.example.ballball.onboarding.fragment

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import com.example.ballball.R
import com.example.ballball.databinding.FragmentOnBoardingBinding

class OnBoardingFragment : Fragment() {

    private var title: String? = null
    private var description: String? = null
    private var imageResource : Int = 0
    private lateinit var tvTitle: AppCompatTextView
    private lateinit var tvDescription: AppCompatTextView
    private lateinit var image: ImageView
    private lateinit var onBoardingBinding: FragmentOnBoardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(ARG_PARAM1)
            description = requireArguments().getString(ARG_PARAM2)
            imageResource = requireArguments().getInt(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        onBoardingBinding = FragmentOnBoardingBinding.inflate(inflater, container, false)

        tvTitle = onBoardingBinding.textOnboardingTitle
        tvDescription = onBoardingBinding.textOnboardingDescription
        image = onBoardingBinding.image
        tvTitle.text = title
        tvDescription.text = description
        image.setImageResource(imageResource)
        return onBoardingBinding.root
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"

        fun newInstance(
            title: String?,
            description: String?,
            imageResource: Int
        ): OnBoardingFragment {
            val fragment = OnBoardingFragment()
            val args = Bundle()
            args.putString(ARG_PARAM1, title)
            args.putString(ARG_PARAM2, description)
            args.putInt(ARG_PARAM3, imageResource)
            fragment.arguments = args
            return fragment
        }
    }
}