package ru.skillbranch.devintensive.ui.custom


import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.skillbranch.devintensive.R


class ChatItemDecoration(val context: Context) : RecyclerView.ItemDecoration() {
    private val ATTRS = intArrayOf(android.R.attr.listDivider)
    private var divider: Drawable? = null
    private var space = 0

    init {
        val typedArray = context.obtainStyledAttributes(ATTRS)
        divider = typedArray.getDrawable(0)
        typedArray.recycle()
        space = context.resources.getDimensionPixelSize(R.dimen.space_decorator)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft + space
        val right = parent.width - parent.paddingRight
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            if (i == childCount - 1) return
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top = child.bottom + params.bottomMargin
            val bottom = top + divider!!.intrinsicHeight
            divider!!.setBounds(left, top, right, bottom)
            divider!!.draw(c)
        }
    }

}