package com.example.demo.shapes

import android.content.Context
import com.example.demo.R

class PerspectiveShape(context: Context, isOES: Boolean = true) : BasePerspectiveShape(context, isOES) {

    override var fragmentResId: Int = R.raw.perspective_texture_shader
}