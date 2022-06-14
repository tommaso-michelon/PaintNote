package it.unipd.dei.esp2022.paintnote

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import it.unipd.dei.esp2022.paintnote.activity.NoteActivity.Companion.paintBrush
import it.unipd.dei.esp2022.paintnote.activity.NoteActivity.Companion.path

class PaintView: View {

    companion object {
        var currentBrush = Color.BLACK
        var isErase: Boolean = false
        var bitmap: Bitmap? = null
        var mCanvas: Canvas? = null
    }

    constructor(context: Context) : this(context, null) { init() }
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0) { init() }
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init() }

    private fun init() {
        paintBrush.isAntiAlias = true
        paintBrush.color = currentBrush
        paintBrush.style = Paint.Style.STROKE
        paintBrush.strokeJoin = Paint.Join.ROUND
        paintBrush.strokeWidth = 8f
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val x = event?.x
        val y = event?.y

        if(event != null) {
            when(event.action){
                MotionEvent.ACTION_DOWN -> {
                    if(x != null) {
                        if(y != null) {
                            path.moveTo(x, y)
                        }
                    }
                                        invalidate()
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    if(x != null) {
                        if(y != null) {
                            path.lineTo(x, y)
                        }
                    }
                    mCanvas?.drawPath(path, paintBrush) //eraser effects while you're drawing
                    invalidate()
                    return true
                }
            }
        }
        return false
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if(bitmap == null) {
            bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            bitmap!!.eraseColor(Color.WHITE)
        } else bitmap = bitmap!!.copy(Bitmap.Config.ARGB_8888, true)   //mutable bitmap (from db return an immutable bitmap)
        mCanvas = Canvas(bitmap!!)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawPath(path, paintBrush)
        canvas?.drawBitmap(bitmap!!, 0f, 0f, paintBrush)
    }

}