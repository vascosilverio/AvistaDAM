package com.projetodam.avista.adapter;


import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.projetodam.avista.R
import com.projetodam.avista.model.Observacao
import com.projetodam.avista.model.RespostaAPI
import com.projetodam.avista.retrofit.RetrofitInitializer
import com.projetodam.avista.ui.EditarObsActivity
import com.projetodam.avista.ui.FullscreenObservacaoActivity
import com.projetodam.avista.ui.MainActivity
import com.squareup.picasso.Picasso
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ObservacaoListAdapter(
    var listaObservacoes: ArrayList<Observacao>,
) :
    RecyclerView.Adapter<ObservacaoListAdapter.ObservacaoViewHolder>() {

    class ObservacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.card_Data_Observacao)
        val txtDescricao: TextView = itemView.findViewById(R.id.txtDescricao)
        val txtEspecie: TextView = itemView.findViewById(R.id.txtEspecie)
        val image: ImageView = itemView.findViewById(R.id.card_thumbnail_Observacao)
        val cartao: LinearLayout = itemView.findViewById(R.id.card_layout)
    }

    // ordenar a lista por data ou espécie
    public fun ordenarObs(asc: Boolean) {
        listaObservacoes.sortWith(compareBy { it.dataOrd() })
        if (!asc) {
            listaObservacoes.reverse()
        }
        notifyDataSetChanged()
    }

    public fun setObs(obsOri: ArrayList<Observacao>){
        listaObservacoes = obsOri
        notifyDataSetChanged()
    }


    // criar uma lista auxiliar para adicionar as observações que coincidem com o filtro
    public fun filtrarObs(especie: String) {
        val listaFiltrada = listaObservacoes.filter { it.filtro(especie) }
        listaObservacoes.clear()
        listaObservacoes.addAll(listaFiltrada)
        notifyDataSetChanged()
    }

    private fun Observacao.filtro(filtro: String): Boolean {
        return especie!!.contains(filtro, ignoreCase = true)
    }


    // extrair o ano, mês e dia, para a ordenação por data ser aaaa/mm/dd, em vez de dd/mm/aaaa
    private fun Observacao.dataOrd(): Long {
        val data = this.data!!.split("/")
        if (data.size == 3) {
            val ano = data[2].toInt()
            val mes = data[1].toInt()
            val dia = data[0].toInt()
            return ano * 10000.toLong() + mes * 100.toLong() + dia
        }
        return 0
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

        // construir um objeto onde vão estar todos os dados referentes à observação selecionada
        val observacao = listaObservacoes[position]

        holder.textView.setText(observacao.data)
        holder.txtEspecie.setText(observacao.especie)
        holder.txtDescricao.setText(observacao.descricao)
        Picasso.get().load(observacao.foto).into(holder.image)



        // evento no cartão selecionado para ficar à escuta de um clique
        holder.cartao.setOnClickListener{
            // Ao abrir os detalhes de uma observação, construir uma Dialog com recurso ao detalhes_observacao.xml
            val detalhes = Dialog(holder.itemView.context)
            detalhes.setContentView(R.layout.detalhes_observacao)
            detalhes.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

            // colocar a dialog com 90% do tamanho horizontal
            val largura = (holder.itemView.context.resources.displayMetrics.widthPixels * 0.90).toInt()
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
                GeoPoint(observacao.lat!!.toDouble(), observacao.long!!.toDouble())
            )


            val txtdDescricao = detalhes.findViewById<TextView>(R.id.txtDescricao)
            val txtEspecie = detalhes.findViewById<TextView>(R.id.txtEspecie)
            val txtdData = detalhes.findViewById<TextView>(R.id.txtData)
            val aveImg = detalhes.findViewById<ImageView>(R.id.imgAve)
            val fechar = detalhes.findViewById<Button>(R.id.btnFecharDialog)
            val editar = detalhes.findViewById<Button>(R.id.btnEditar)
            val remover = detalhes.findViewById<Button>(R.id.btnRemoverObservacao)
            Picasso.get().load(observacao.foto).into(aveImg)
            txtEspecie.text = observacao.especie
            txtdDescricao.text = observacao.descricao
            txtdData.text = observacao.data
            detalhes.show()



            aveImg.setOnClickListener {
                val intent = Intent(holder.itemView.context, FullscreenObservacaoActivity::class.java)
                intent.putExtra("imageUri", observacao.foto!!)
                holder.itemView.context.startActivity(intent)
            }

            editar.setOnClickListener {
                val intent =
                    Intent(holder.itemView.context, EditarObsActivity::class.java)
                (holder.itemView.context as? AppCompatActivity)?.finish()
                intent.putExtra("idObs", observacao.id)
                intent.putExtra("utilizador", observacao.utilizador)
                intent.putExtra("data", observacao.data)
                intent.putExtra("especie", observacao.especie)
                intent.putExtra("foto", observacao.foto)
                intent.putExtra("descricao", observacao.descricao)
                intent.putExtra("latitude", observacao.lat)
                intent.putExtra("longitude", observacao.long)
                intent.putExtra("listaObservacoes", listaObservacoes)
                intent.putExtra("option", "OPTION_3")
                detalhes.dismiss()
                holder.itemView.context.startActivity(intent)
            }

            fechar.setOnClickListener {
                detalhes.dismiss()
            }

            // botão para remover a observação que foi selecionada
            remover.setOnClickListener {
                // é preciso confirmar o dialog para certificar que o utilizador pretende mesmo remover a observação
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

                                // construir novamente a MainActivity para recarregar as observações sem a que foi removida
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

                }
                dialogConfirmacao.setNegativeButton("Não", null)
                dialogConfirmacao.show()
            }
        }
    }
}
