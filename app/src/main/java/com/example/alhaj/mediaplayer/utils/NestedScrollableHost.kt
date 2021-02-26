package com.example.alhaj.mediaplayer.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.FrameLayout
import androidx.viewpager2.widget.ViewPager2


/**
 * Layout to wrap a scrollable component inside a ViewPager2. Provided as a solution to the problem
 * where pages of ViewPager2 have nested scrollable elements that scroll in the same direction as
 * ViewPager2. The scrollable element needs to be the immediate and only child of this host layout.
 *
 * This solution has limitations when using multiple levels of nested scrollable elements
 * (e.g. a horizontal RecyclerView in a vertical RecyclerView in a horizontal ViewPager2).
 */
class NestedScrollableHost : FrameLayout {
    private var parentViewPager: ViewPager2? = null
    private var touchSlop = 0
    private var initialX = 0f
    private var initialY = 0f

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context)
    }

    private fun init(context: Context) {
        touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                var v = parent as View
                while (v != null && v !is ViewPager2) {
                    v = v.parent as View
                }
                parentViewPager = v as ViewPager2
                viewTreeObserver.removeOnPreDrawListener(this)
                return false
            }
        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        handleInterceptTouchEvent(ev)
        return super.onInterceptTouchEvent(ev)
    }

    private fun canChildScroll(orientation: Int, delta: Float): Boolean {
        val direction = (-delta).toInt()
        val child = getChildAt(0)
        return if (orientation == 0) {
            child.canScrollHorizontally(direction)
        } else if (orientation == 1) {
            child.canScrollVertically(direction)
        } else {
            throw IllegalArgumentException()
        }
    }

    private fun handleInterceptTouchEvent(e: MotionEvent) {
        if (parentViewPager == null) return
        val orientation = parentViewPager!!.orientation

        // Early return if child can't scroll in same direction as parent
        if (!canChildScroll(orientation, -1f) && !canChildScroll(orientation, 1f)) {
            return
        }
        if (e.action == MotionEvent.ACTION_DOWN) {
            initialX = e.x
            initialY = e.y
            parent.requestDisallowInterceptTouchEvent(true)
        } else if (e.action == MotionEvent.ACTION_MOVE) {
            val dx = e.x - initialX
            val dy = e.y - initialY
            val isVpHorizontal = orientation == ViewPager2.ORIENTATION_HORIZONTAL

            // assuming ViewPager2 touch-slop is 2x touch-slop of child
            val scaledDx = Math.abs(dx) * if (isVpHorizontal) .5f else 1f
            val scaledDy = Math.abs(dy) * if (isVpHorizontal) 1f else .5f
            if (scaledDx > touchSlop || scaledDy > touchSlop) {
                if (isVpHorizontal == scaledDy > scaledDx) {
                    // Gesture is perpendicular, allow all parents to intercept
                    parent.requestDisallowInterceptTouchEvent(false)
                } else {
                    // Gesture is parallel, query child if movement in that direction is possible
                    if (canChildScroll(orientation, if (isVpHorizontal) dx else dy)) {
                        // Child can scroll, disallow all parents to intercept
                        parent.requestDisallowInterceptTouchEvent(true)
                    } else {
                        // Child cannot scroll, allow all parents to intercept
                        parent.requestDisallowInterceptTouchEvent(false)
                    }
                }
            }
        }
    }
}