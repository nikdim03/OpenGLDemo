package com.example.demo.shapes

import android.content.Context
import com.example.demo.R

class CurvedShape(context: Context, isOES: Boolean = false) : DefaultShape(context, isOES) {

    override var fragmentResId: Int = R.raw.curved_texture_shader
    override var vertexResId: Int = R.raw.curved_vertex_shader
}