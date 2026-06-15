package com.example.raithvartha

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class TipCardAdapter : RecyclerView.Adapter<TipCardAdapter.CardViewHolder>() {

    private var tips: List<Tip> = emptyList()
    private var showKannada: Boolean = true

    fun setTips(newTips: List<Tip>) {
        tips = newTips
        notifyDataSetChanged()
    }

    fun setLanguage(kannada: Boolean) {
        showKannada = kannada
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tip_card, parent, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        holder.bind(tips[position], showKannada)
    }

    override fun getItemCount(): Int = tips.size

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvInstruction: TextView = itemView.findViewById(R.id.tvInstruction)
        private val tvCategory: TextView = itemView.findViewById(R.id.tvCategory)
        private val tvSuccessStory: TextView = itemView.findViewById(R.id.tvSuccessStory)
        private val ivCrop: ImageView = itemView.findViewById(R.id.ivCrop)

        fun bind(tip: Tip, showKannada: Boolean) {
            tvTitle.text = if (showKannada) tip.titleKannada else tip.titleEnglish
            tvInstruction.text = if (showKannada) tip.instructionKannada else tip.instructionEnglish
            tvCategory.text = tip.cropCategory
            tvSuccessStory.text = tip.successStory

            Glide.with(itemView.context)
                .load(tip.imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_gallery)
                .centerCrop()
                .into(ivCrop)

            // ← ADD THIS: fixes the Ask Expert button
            itemView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnExpertAsk)
                .setOnClickListener {
                    val intent = android.content.Intent(itemView.context, ExpertAskActivity::class.java)
                    itemView.context.startActivity(intent)
                }
        }
    }
}