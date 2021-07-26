package com.solinari.MHSGene

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.solinari.MHSGene.databinding.ItemGeneBinding

class GeneAdapter(var context: Context) : RecyclerView.Adapter<GeneAdapter.ViewHolder>() {

    var geneList: ArrayList<Gene> = ArrayList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var listener: GeneClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemGeneBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val gene = geneList[position]
        holder.binding.name.text = gene.name
        holder.binding.icon.setImageResource(gene.getGeneIcon())
    }

    override fun getItemCount(): Int {
        return geneList.size
    }


    fun addGeneToFirst(gene: Gene) {
        geneList.add(0, gene)
        notifyItemInserted(0)
    }

    inner class ViewHolder(var binding: ItemGeneBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener?.geneClick(geneList[adapterPosition])
            }
        }
    }

    interface GeneClickListener {
        fun geneClick(gene: Gene)
    }
}