package com.example.sketchify

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path

class DrawingView(context:Context, attrbs:AttributeSet):View(context, attrbs) {
    private var mDrawPath:CustomPath?=null
    private var mCanvasBitmap:Bitmap?=null
    private var mDrawPaint:Paint?=null
    private var mCanvasPaint:Paint?=null
    private var mBrushSize:Float=0.toFloat()
    private var color=Color.BLACK
    private var canvas:Canvas?=null
    private val mPaths=ArrayList<CustomPath>()
    private val mUndoPaths=ArrayList<CustomPath>()
    private val mRedoPaths=ArrayList<CustomPath>()
    init {
        setUpDrawing()
    }

    fun onClickUndo(){
        if(mPaths.size>0){
            mUndoPaths.add(mPaths.removeAt(mPaths.size-1))
            invalidate()
        }
    }

     fun onClickRedo(){
        if(mUndoPaths.isNotEmpty()){
            mPaths.add(mUndoPaths.removeAt(mUndoPaths.size-1))
            invalidate()

        }
    }

    private fun setUpDrawing() {
        mDrawPaint= Paint()
        mDrawPath=CustomPath(color, mBrushSize)
        mDrawPaint!!.color=color
        mDrawPaint!!.style=Paint.Style.STROKE
        mDrawPaint!!.strokeJoin=Paint.Join.ROUND
        mDrawPaint!!.strokeCap=Paint.Cap.ROUND
        mCanvasPaint= Paint(Paint.DITHER_FLAG)
       // mBrushSize=20.toFloat()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap= Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)
        canvas=Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)
        for (path in mPaths){
            mDrawPaint!!.strokeWidth=path.brushThickness
            mDrawPaint!!.color=path.color
            canvas.drawPath(path,mDrawPaint!!)
        }
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth=mDrawPath!!.brushThickness
            mDrawPaint!!.color=mDrawPath!!.color //how thick the paint should be
            canvas.drawPath(mDrawPath!!,mDrawPaint!!)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchx=event?.x
        val touchy=event?.y
        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                mDrawPath!!.color=color
                mDrawPath!!.brushThickness=mBrushSize //how thick the path is
                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchx!!,touchy!!)
            }
            MotionEvent.ACTION_MOVE->{
                mDrawPath!!.lineTo(touchx!!,touchy!!)
            }
            MotionEvent.ACTION_UP->{
                mPaths.add(mDrawPath!!)
                mDrawPath=CustomPath(color,mBrushSize)
            }
            else->return false
        }
        invalidate()
        return true
    }

    fun setSizeForBrush(newsize:Float){
        mBrushSize=TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newsize,resources.displayMetrics)
        mDrawPaint!!.strokeWidth=mBrushSize
    }

    fun setColor(newColor:String){
        color=Color.parseColor(newColor)
        mDrawPaint!!.color=color
    }

    internal inner class CustomPath(var color:Int, var brushThickness:Float):android.graphics.Path() {

    }
}