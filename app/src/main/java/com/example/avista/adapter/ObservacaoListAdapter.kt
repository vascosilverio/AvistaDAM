package com.example.avista.adapter;


import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.avista.R
import com.example.avista.model.Observacao

class ObservacaoListAdapter(val listaObservacoes: ArrayList<Observacao>, val onClickListener: OnClickListener) :
    RecyclerView.Adapter<ObservacaoListAdapter.ObservacaoViewHolder>() {

    class ObservacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.card_Data_Observacao)
        val image: ImageView = itemView.findViewById(R.id.card_thumbnail_Observacao)

    }

    class OnClickListener(val clickListener: (observacao: Observacao) -> Unit){
        fun onClick(observacao: Observacao) = clickListener(observacao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_observacao,parent,false)

        return ObservacaoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaObservacoes.size
    }

    override fun onBindViewHolder(holder: ObservacaoViewHolder, position: Int) {
        val observacao = listaObservacoes[position]
        holder.textView.setText(observacao.data)
        val imageBytes = Base64.decode(observacao.foto, Base64.DEFAULT)
        val decode = BitmapFactory.decodeByteArray(imageBytes,0,imageBytes.size)
        holder.image.setImageBitmap(decode)
        holder.itemView.setOnClickListener{
            onClickListener.onClick(observacao)
        }
    }
}
