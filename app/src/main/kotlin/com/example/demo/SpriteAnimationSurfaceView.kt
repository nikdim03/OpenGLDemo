package com.example.demo

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.Rect
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.DrawableRes

class SpriteAnimationSurfaceView(context: Context) : SurfaceView(context), SurfaceHolder.Callback {
    private val paint = Paint()
    private val frameChangeDelay: Long = 30 // time between frames in ms
    private var spriteSheet: Bitmap? = null
    private var srcRect: Rect? = null
    private var destRect: Rect? = null
    private var frameIndex = 0
    private var framesCount: Int = 0
    private var spriteWidth: Int = 0
    private var spriteHeight: Int = 0
    private var frameCols: Int = 0
    private var frameRows: Int = 0
    private var animationThread: Thread? = null

    init {
        holder.addCallback(this)
        holder.setFormat(PixelFormat.TRANSPARENT)
    }

    fun loadSprite(@DrawableRes resourceId: Int, frameCols: Int, frameRows: Int) {
        this.frameCols = frameCols
        this.frameRows = frameRows
        spriteSheet = BitmapFactory.decodeResource(resources, resourceId)
        spriteWidth = spriteSheet?.width?.div(frameCols) ?: 0
        spriteHeight = spriteSheet?.height?.div(frameRows) ?: 0
        srcRect = Rect(0, 0, spriteWidth, spriteHeight)
        destRect = Rect(0, 0, spriteWidth, spriteHeight)
        framesCount = frameCols * frameRows
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        destRect?.set(0, 0, width, height)
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        animationThread = Thread {
            while (true) {
                if (!holder.surface.isValid) {
                    continue
                }

                val canvas = holder.lockCanvas()
                if (canvas != null) {
                    synchronized(holder) {
                        updateFrame()
                        drawSprite(canvas)
                    }
                    holder.unlockCanvasAndPost(canvas)
                }

                // Sleep to control frame rate
                try {
                    Thread.sleep(frameChangeDelay)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
        animationThread?.start()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        var retry = true
        animationThread?.interrupt()
        while (retry) {
            try {
                animationThread?.join()
                retry = false
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
    }

    private fun updateFrame() {
        frameIndex = (++frameIndex % framesCount)
        val column = frameIndex % frameCols
        val row = frameIndex / frameCols
        Log.d(TAG, "updateFrame: $frameIndex row: $row column: $column")
        srcRect?.left = column * spriteWidth
        srcRect?.top = row * spriteHeight
        srcRect?.right = srcRect?.left!! + spriteWidth
        srcRect?.bottom = srcRect?.top!! + spriteHeight
    }

    private fun drawSprite(canvas: Canvas) {
        clearCanvas(canvas)
        canvas.drawBitmap(spriteSheet!!, srcRect!!, destRect!!, paint)
    }

    private fun clearCanvas(canvas: Canvas) {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
    }

    private companion object {
        private const val TAG = "SpriteAnimationSurfaceView"
    }
}
