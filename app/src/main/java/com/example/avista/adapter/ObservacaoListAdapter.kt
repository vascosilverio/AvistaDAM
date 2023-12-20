package com.example.avista.adapter;


import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.avista.R
import com.example.avista.model.Observacao
import com.squareup.picasso.Picasso

class ObservacaoListAdapter(val listaObservacoes: ArrayList<Observacao>, val onClickListener: OnClickListener) :
    RecyclerView.Adapter<ObservacaoListAdapter.ObservacaoViewHolder>() {

    class ObservacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.card_Data_Observacao)
        val image: ImageView = itemView.findViewById(R.id.card_thumbnail_Observacao)
        val btnDetalhes: Button = itemView.findViewById(R.id.btnAbreDialog)

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
        Picasso.get().load(observacao.foto).into(holder.image)

        /*holder.itemView.setOnClickListener{
            Log.d("Entrou","yeah")
            onClickListener.onClick(observacao)
        }*/

        holder.btnDetalhes.setOnClickListener{
            Log.d("Clique",observacao.especie.toString())
        }
    }
}
