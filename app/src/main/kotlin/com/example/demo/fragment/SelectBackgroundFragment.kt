package com.example.demo.fragment

import com.example.demo.R
import com.example.demo.utils.MediaManager

class SelectBackgroundFragment : BaseSelectVideoFragment() {

    override fun getTitle(): CharSequence {
        return "Select background video"
    }

    override fun clickRightMenu() {
        val selectMedias = getSelectedMedias()
        if (selectMedias.isEmpty()) {
            showToast("Please select a background video")
        } else {
            MediaManager.setBackGroundMedias(selectMedias)
            activity?.supportFragmentManager?.also {
                it.beginTransaction()
                    .replace(R.id.ll_container, SelectVideoFragment())
                    .commitAllowingStateLoss()
            }
        }
    }
}