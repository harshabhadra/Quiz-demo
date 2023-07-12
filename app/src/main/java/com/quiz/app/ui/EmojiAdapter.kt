package com.quiz.app.ui

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.OvershootInterpolator
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.quiz.app.R

class EmojiAdapter(private val images: List<Int>) :
    RecyclerView.Adapter<EmojiAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.image_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageRes = images[position]
        holder.bind(imageRes)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.emo_iv)

        init {
            itemView.setOnTouchListener { view, motionEvent ->
                if (motionEvent.action == MotionEvent.ACTION_MOVE || motionEvent.action == MotionEvent.ACTION_DOWN) {
                    animatePopUp()
                }
                view.performClick()
            }
        }

        private fun animatePopUp() {
            val animation = AnimationUtils.loadAnimation(itemView.context, R.anim.pop_up_anim)
            animation.interpolator = OvershootInterpolator()
            imageView.startAnimation(animation)
        }

        fun bind(imageRes: Int) {
            imageView.setImageResource(imageRes)
        }
    }
}
