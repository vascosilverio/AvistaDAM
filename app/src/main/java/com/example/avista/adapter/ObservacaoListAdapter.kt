package com.example.avista.adapter;


import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.avista.R
import com.example.avista.model.Observacao
import com.example.avista.model.RespostaAPI
import com.example.avista.retrofit.RetrofitInitializer
import com.example.avista.ui.MainActivity
import com.squareup.picasso.Picasso
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ObservacaoListAdapter(
    val listaObservacoes: ArrayList<Observacao>,
    val onClickListener: OnClickListener
) :
    RecyclerView.Adapter<ObservacaoListAdapter.ObservacaoViewHolder>() {

    class ObservacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.card_Data_Observacao)
        val txtDescricao: TextView = itemView.findViewById(R.id.txtDescricao)
        val txtEspecie: TextView = itemView.findViewById(R.id.txtEspecie)
        val image: ImageView = itemView.findViewById(R.id.card_thumbnail_Observacao)
        val btnDetalhes: Button = itemView.findViewById(R.id.btnAbreDialog)

    }

    class OnClickListener(val clickListener: (observacao: Observacao) -> Unit) {
        fun onClick(observacao: Observacao) = clickListener(observacao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_observacao, parent, false)

        return ObservacaoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return listaObservacoes.size
    }

    override fun onBindViewHolder(holder: ObservacaoViewHolder, position: Int) {
        val observacao = listaObservacoes[position]
        holder.textView.setText(observacao.data)
        holder.txtEspecie.setText(observacao.especie)
        holder.txtDescricao.setText(observacao.descricao)
        Picasso.get().load(observacao.foto).into(holder.image)

        holder.btnDetalhes.setOnClickListener {

            // Ao abrir os detalhes de uma observação, construir uma Dialog com recurso ao detalhes_observacao.xml
            val detalhes = Dialog(holder.itemView.context)
            detalhes.setContentView(R.layout.detalhes_observacao)
            detalhes.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            // colocar a dialog com 90% do tamanho horizontal
            val largura =
                (holder.itemView.context.resources.displayMetrics.widthPixels * 0.90).toInt()
            detalhes.window?.setLayout(largura, ViewGroup.LayoutParams.WRAP_CONTENT)
            detalhes.setCancelable(true)

            // construir o mapa
            val mapView = detalhes.findViewById<MapView>(R.id.mapa)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)

            val marcador = Marker(mapView)
            marcador.position = GeoPoint(observacao.lat!!.toDouble(), observacao.long!!.toDouble())
            mapView.overlays.add(marcador)
            mapView.controller.setZoom(12.0)
            mapView.controller.setCenter(
                GeoPoint(
                    observacao.lat!!.toDouble(),
                    observacao.long!!.toDouble()
                )
            )

            val txtdDescricao = detalhes.findViewById<TextView>(R.id.txtDescricao)
            val txtEspecie = detalhes.findViewById<TextView>(R.id.txtEspecie)
            val txtdData = detalhes.findViewById<TextView>(R.id.txtData)
            val aveImg = detalhes.findViewById<ImageView>(R.id.imgAve)
            val fechar = detalhes.findViewById<Button>(R.id.btnFecharDialog)
            val remover = detalhes.findViewById<Button>(R.id.btnRemoverObservacao)
            Picasso.get().load(observacao.foto).into(aveImg)
            txtEspecie.text = observacao.especie
            txtdDescricao.text = observacao.descricao
            txtdData.text = observacao.data
            detalhes.show()

            fechar.setOnClickListener {
                detalhes.dismiss()
            }

            remover.setOnClickListener {
                val dialogConfirmacao = AlertDialog.Builder(holder.itemView.context)
                dialogConfirmacao.setTitle("Remover observação")
                dialogConfirmacao.setMessage("Tem a certeza de que pretende remover esta observação?")
                dialogConfirmacao.setPositiveButton("Sim") { dialog, which ->
                    Log.d("ID DA OBSERVACAO: -> ", "${observacao.id}")

                    val call = RetrofitInitializer().servicoAPI()
                        .removerObservacao(observacao.id.toString())
                    call.enqueue(object : Callback<RespostaAPI> {
                        override fun onResponse(
                            call: Call<RespostaAPI>,
                            response: Response<RespostaAPI>
                        ) {
                            if (response.isSuccessful) {
                                Log.d("Estado: ", "removida")
                                detalhes.dismiss()
                                listaObservacoes.remove(observacao)

                                // construir novamente a MainActivity
                                val intent =
                                    Intent(holder.itemView.context, MainActivity::class.java)
                                (holder.itemView.context as? AppCompatActivity)?.finish()
                                intent.putExtra("utilizador", observacao.utilizador)
                                holder.itemView.context.startActivity(intent)

                            } else {
                                Log.e("Estado: ", "Falha ao remover observação")
                            }
                        }

                        override fun onFailure(call: Call<RespostaAPI>, t: Throwable) {
                            Log.e("ObservacaoListAdapter", "Erro ao remover observação", t)
                        }
                    })

                    ///////////
                }
                dialogConfirmacao.setNegativeButton("Não", null)
                dialogConfirmacao.show()
            }
        }
    }
}
