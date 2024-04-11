package com.example.demo.fragment

import android.content.Intent
import com.example.demo.activity.PlayerActivity
import com.example.demo.utils.Media
import com.example.demo.utils.MediaManager

class SelectVideoFragment : BaseSelectVideoFragment() {

    private var selectPosition = -1
    private var selectMedia: Media? = null

    override fun getTitle(): CharSequence {
        return "Select a video"
    }

    override fun clickRightMenu() {
        val selectMedias = getSelectedMedias()
        if (selectMedias.isEmpty()) {
            showToast("Please select a video")
        } else {
            MediaManager.setFrontMedias(selectMedias)
            activity?.apply {
                startActivity(Intent(this, PlayerActivity::class.java))
            }
        }
    }

    override fun onItemSelected(item: Media, position: Int) {
        if (position != selectPosition) {
            selectMedia?.isSelected = false
            if (selectPosition >= 0) adapter?.notifyItemChanged(selectPosition)

            selectPosition = position
            selectMedia = item
        }
    }
}