package com.example.avista.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class Observacao(
    @SerializedName("id")
    val id: String?,
    @SerializedName("utilizador")
    val utilizador: String?,
    @SerializedName("lat")
    val lat: String?,
    @SerializedName("long")
    val long: String?,
    @SerializedName("foto")
    val foto: String?,
    @SerializedName("descricao")
    val descricao: String?,
    @SerializedName("data")
    val data: String?,
    @SerializedName("especie")
    val especie: String?
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(utilizador)
        parcel.writeString(lat)
        parcel.writeString(long)
        parcel.writeString(foto)
        parcel.writeString(descricao)
        parcel.writeString(data)
        parcel.writeString(especie)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Observacao> {
        override fun createFromParcel(parcel: Parcel): Observacao {
            return Observacao(parcel)
        }

        override fun newArray(size: Int): Array<Observacao?> {
            return arrayOfNulls(size)
        }
    }
}
