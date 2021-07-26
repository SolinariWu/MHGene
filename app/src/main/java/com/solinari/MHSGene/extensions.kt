package com.solinari.MHSGene

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import io.github.hyuwah.draggableviewlib.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

interface listenere {
    fun dragEnd(v: View)
}

fun View.setupGeneDraggable(
    stickyAxis: DraggableView.Mode = DraggableView.Mode.NON_STICKY,
    animated: Boolean = true,
    draggableListener: DraggableListener? = null,
    listener: listenere? = null
) {
    var widgetInitialX = 0f
    var widgetDX = 0f
    var widgetInitialY = 0f
    var widgetDY = 0f

    val marginStart = marginStart()
    val marginTop = marginTop()
    val marginEnd = marginEnd()
    val marginBottom = marginBottom()

    setOnTouchListener { v, event ->
        val viewParent = v.parent as View
        val parentHeight = viewParent.height
        val parentWidth = viewParent.width
        val xMax = parentWidth - v.width - marginEnd
        val xMiddle = parentWidth / 2
        val yMax = parentHeight - v.height - marginBottom
        val yMiddle = parentHeight / 2

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                widgetDX = v.x - event.rawX
                widgetDY = v.y - event.rawY
                widgetInitialX = v.x
                widgetInitialY = v.y
            }
            MotionEvent.ACTION_MOVE -> {
                var newX = event.rawX + widgetDX
                newX = max(marginStart, newX)
                newX = min(xMax, newX)
                v.x = newX

                var newY = event.rawY + widgetDY
                newY = max(marginTop, newY)
                newY = min(yMax, newY)
                v.y = newY

                draggableListener?.onPositionChanged(v)
//                minimizeBtnListener.onPositionChanged(v, StickyRestSide.HIDE)
            }
            MotionEvent.ACTION_UP -> {
                when (stickyAxis) {
                    DraggableView.Mode.STICKY_X -> {
                        if (event.rawX >= xMiddle) {
                            if (animated)
                                v.animate().x(xMax)
                                    .setDuration(Draggable.DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
//                                        minimizeBtnListener.onPositionChanged(v, StickyRestSide.RIGHT)
                                    }
                                    .setListener(object : AnimatorListenerAdapter() {
                                        override fun onAnimationEnd(animation: Animator?) {
                                            super.onAnimationEnd(animation)
                                            Log.d("drg", "Animate END Sticky X RIGHT")
                                        }
                                    })
                                    .start()
                            else {
                                v.x = xMax
//                                minimizeBtnListener.onPositionChanged(v, StickyRestSide.RIGHT)
                            }
                        }
                        else {
                            if (animated)
                                v.animate().x(marginStart).setDuration(Draggable.DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
//                                        minimizeBtnListener.onPositionChanged(v, StickyRestSide.LEFT)
                                    }
                                    .start()
                            else {
                                v.x = marginStart
//                                minimizeBtnListener.onPositionChanged(v, StickyRestSide.LEFT)
                            }
                        }
                    }
                    DraggableView.Mode.STICKY_Y -> {
                        if (event.rawY >= yMiddle) {
                            if (animated)
                                v.animate().y(yMax)
                                    .setDuration(Draggable.DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
//                                        minimizeBtnListener.onPositionChanged(v, StickyRestSide.BOTTOM)
                                    }
                                    .start()
                            else
                                v.y = yMax
                        }
                        else {
                            if (animated)
                                v.animate().y(marginTop)
                                    .setDuration(Draggable.DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
                                    }
                                    .start()
                            else
                                v.y = marginTop
                        }
                    }
                    DraggableView.Mode.STICKY_XY -> {
                        if (event.rawX >= xMiddle) {
                            if (animated)
                                v.animate().x(xMax)
                                    .setDuration(Draggable.DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
                                    }
                                    .start()
                            else
                                v.x = xMax
                        }
                        else {
                            if (animated)
                                v.animate().x(marginStart).setDuration(Draggable.DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
                                    }
                                    .start()
                            v.x = marginStart
                        }

                        if (event.rawY >= yMiddle) {
                            if (animated)
                                v.animate().y(yMax)
                                    .setDuration(Draggable.DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
                                    }
                                    .start()
                            else
                                v.y = yMax
                        }
                        else {
                            if (animated)
                                v.animate().y(marginTop).setDuration(Draggable.DURATION_MILLIS)
                                    .setUpdateListener {
                                        draggableListener?.onPositionChanged(v)
                                    }
                                    .start()
                            else
                                v.y = marginTop
                        }
                    }
                    else -> {
                    }
                }

                if (abs(v.x - widgetInitialX) <= Draggable.DRAG_TOLERANCE && abs(v.y - widgetInitialY) <= Draggable.DRAG_TOLERANCE) {
                    performClick()
                }
                listener?.dragEnd(v)
            }
            else -> return@setOnTouchListener false
        }
        true
    }
}

internal fun View.marginStart(): Float {
    return ((layoutParams as? ViewGroup.MarginLayoutParams)?.marginStart ?: 0).toFloat()
}

internal fun View.marginEnd(): Float {
    return ((layoutParams as? ViewGroup.MarginLayoutParams)?.marginEnd ?: 0).toFloat()
}

internal fun View.marginTop(): Float {
    return ((layoutParams as? ViewGroup.MarginLayoutParams)?.topMargin ?: 0).toFloat()
}

internal fun View.marginBottom(): Float {
    return ((layoutParams as? ViewGroup.MarginLayoutParams)?.bottomMargin ?: 0).toFloat()
}

fun Gene.getGeneIcon(): Int {
    return when (geneClass) {
        GeneClass.NonEle -> R.drawable.non_elem
        GeneClass.NonPower -> R.drawable.non_power
        GeneClass.NonSpeed -> R.drawable.non_speed
        GeneClass.NonTechnical -> R.drawable.non_technical
        GeneClass.FireEle -> R.drawable.fire_elem
        GeneClass.FirePower -> R.drawable.fire_power
        GeneClass.FireSpeed -> R.drawable.fire_speed
        GeneClass.FireTechnical -> R.drawable.fire_technical
        GeneClass.WaterEle -> R.drawable.water_elem
        GeneClass.WaterPower -> R.drawable.water_power
        GeneClass.WaterSpeed -> R.drawable.water_speed
        GeneClass.WaterTechnical -> R.drawable.water_technical
        GeneClass.ThunderEle -> R.drawable.thunder_elem
        GeneClass.ThunderPower -> R.drawable.thunder_power
        GeneClass.ThunderSpeed -> R.drawable.thunder_speed
        GeneClass.ThunderTechnical -> R.drawable.thunder_technical
        GeneClass.IceEle -> R.drawable.ice_elem
        GeneClass.IcePower -> R.drawable.ice_power
        GeneClass.IceSpeed -> R.drawable.ice_speed
        GeneClass.IceTechnical -> R.drawable.ice_technical
        GeneClass.DragonEle -> R.drawable.dragon_elem
        GeneClass.DragonPower -> R.drawable.dragon_power
        GeneClass.DragonSpeed -> R.drawable.dragon_speed
        GeneClass.DragonTechnical -> R.drawable.dragon_technical
        else -> R.drawable.rainbow
    }
}
