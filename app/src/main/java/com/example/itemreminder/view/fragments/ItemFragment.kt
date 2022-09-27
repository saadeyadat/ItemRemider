package com.example.itemreminder.view.fragments

import android.content.Context
import android.net.Uri
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.itemreminder.model.Item
import com.example.itemreminder.R
import kotlinx.android.synthetic.main.fruit_fragment.*

class ItemFragment(private val item: Item, context: Context): Fragment(R.layout.fruit_fragment) {

    private val itemFragmentInfo = ItemFragmentInfo(item, context)
    override fun onResume() {
        super.onResume()

        val name = requireArguments().getString("fruitName")
        val image = requireArguments().getString("fruitImage")

        fruit_title?.text = name.toString()
        fruit_image?.setImageURI(Uri.parse(image))
        if (image!!.contains("https://"))
            Glide.with(requireContext()).load(image).into(fruit_image)
        else
            fruit_image?.setImageURI(Uri.parse(image))

        fruit_info?.setOnClickListener {
            childFragmentManager.beginTransaction().replace(R.id.fragment_view_info, itemFragmentInfo).commit()
        }

        exit_button?.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
        }
    }
}