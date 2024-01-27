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

/*
 * Classe que contém a classe ViewHolder com uma RecyclerView com a lista de observações
 * Cada célula da RecyclerView implementa uma CardView com os detalhes de observações
 * Cada CardView com os detalhes implementam as funcionalidades de editar ou remover cada observação
 * Dá acesso à funcionalidade de adicionar observação
 * Implementa a ordenação e filtragem da lista de observações da RecyclerView
 *
 */
class ObservacaoListAdapter(
    var listaObservacoes: ArrayList<Observacao>,
) :
    RecyclerView.Adapter<ObservacaoListAdapter.ObservacaoViewHolder>() {

        /*
        * Classe que contém o ViewHolder de observações que extende a RecyclerView
         */
    class ObservacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.card_Data_Observacao)
        val txtDescricao: TextView = itemView.findViewById(R.id.txtDescricao)
        val txtEspecie: TextView = itemView.findViewById(R.id.txtEspecie)
        val image: ImageView = itemView.findViewById(R.id.card_thumbnail_Observacao)
        val cartao: LinearLayout = itemView.findViewById(R.id.card_layout)
    }

    /*
     * Ordena a lista de observações por data de inserção ou por nome de espécie
     */
    public fun ordenarObs(asc: Boolean) {
        listaObservacoes.sortWith(compareBy { it.dataOrd() })
        if (!asc) {
            listaObservacoes.reverse()
        }
        notifyDataSetChanged()
    }

    /*
     * Cria uma lista auxiliar para adicionar as observações que coincidem com o filtro
     */
    public fun filtrarObs(especie: String) {
        val listaFiltrada = listaObservacoes.filter { it.filtro(especie) }
        listaObservacoes.clear()
        listaObservacoes.addAll(listaFiltrada)
        notifyDataSetChanged()
    }

    /*
     * Devolve true se o nome da espécie corresponder ao filtro
     */
    private fun Observacao.filtro(filtro: String): Boolean {
        return especie!!.contains(filtro, ignoreCase = true)
    }

    /*
    * Extrai o ano, mês e dia, para a ordenação por data ser aaaa/mm/dd, em vez de dd/mm/aaaa
    */
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

    /*
    * Associa o ViewHolder ao layout da RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ObservacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_observacao, parent, false)

        return ObservacaoViewHolder(view)
    }

    /*
    * Contagem de items na lista de observacoes
     */
    override fun getItemCount(): Int {
        return listaObservacoes.size
    }

    /*
    * Associa os eventos de ver a ScrollView de detalhes da observação, editar, remover, adicionar observação
    * fechar a ScrollView e abrir a imagem da observação para fullscreen
     */
    override fun onBindViewHolder(holder: ObservacaoViewHolder, position: Int) {

        // constrói um objeto onde vão estar todos os dados referentes à observação selecionada
        val observacao = listaObservacoes[position]

        holder.textView.setText(observacao.data)
        holder.txtEspecie.setText(observacao.especie)
        holder.txtDescricao.setText(observacao.descricao)
        Picasso.get().load(observacao.foto).into(holder.image)



        // adiciona evento onclick na célula de observação da RecyclerView selecionada para ficar à escuta de um clique
        holder.cartao.setOnClickListener{
            // abre os detalhes de uma observação, construói uma Dialog com recurso ao layout detalhes_observacao.xml
            val detalhes = Dialog(holder.itemView.context)
            detalhes.setContentView(R.layout.detalhes_observacao)
            detalhes.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))

            // coloca a dialog com 90% do tamanho horizontal
            val largura = (holder.itemView.context.resources.displayMetrics.widthPixels * 0.90).toInt()
            detalhes.window?.setLayout(largura, ViewGroup.LayoutParams.WRAP_CONTENT)
            detalhes.setCancelable(true)

            // constrói o mapa
            val mapView = detalhes.findViewById<MapView>(R.id.mapa)
            mapView.setTileSource(TileSourceFactory.MAPNIK)
            mapView.setMultiTouchControls(true)

            // adiciona um marker no mapa e centra o mapa na localização atual do utilizador
            val marcador = Marker(mapView)
            marcador.position = GeoPoint(observacao.lat!!.toDouble(), observacao.long!!.toDouble())
            mapView.overlays.add(marcador)
            mapView.controller.setZoom(12.0)
            mapView.controller.setCenter(
                GeoPoint(observacao.lat!!.toDouble(), observacao.long!!.toDouble())
            )

            //preenche os dados da observacao escolhida em variáveis
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


            //abre a imagem da observação escolhida na RecyclerView em fullscreen
            aveImg.setOnClickListener {
                val intent = Intent(holder.itemView.context, FullscreenObservacaoActivity::class.java)
                intent.putExtra("imageUri", observacao.foto!!)
                holder.itemView.context.startActivity(intent)
            }

            //abre a atividade de editar a observação escolhida na RecyclerView
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

            //fecha a janela de detalhes da observação escolhida na RecyclerView
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
