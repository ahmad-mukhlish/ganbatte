package com.programmerbaper.skripsi.model.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaksi implements Parcelable {

    @SerializedName("id_transaksi")
    @Expose
    private int idTransaksi;
    @SerializedName("id_pembeli")
    @Expose
    private int idPembeli;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("foto")
    @Expose
    private String foto;
    @SerializedName("no_telp")
    @Expose
    private String noTelp;
    @SerializedName("catatan")
    @Expose
    private String catatan;
    @SerializedName("pre_order_status")
    @Expose
    private int preOrderStatus;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
    @SerializedName("harga")
    @Expose
    private int harga;
    @SerializedName("item")
    @Expose
    private int item;

    public int getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(int idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public int getIdPembeli() {
        return idPembeli;
    }

    public void setIdPembeli(int idPembeli) {
        this.idPembeli = idPembeli;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public int getPreOrderStatus() {
        return preOrderStatus;
    }

    public void setPreOrderStatus(int preOrderStatus) {
        this.preOrderStatus = preOrderStatus;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public int getItem() {
        return item;
    }

    public void setItem(int item) {
        this.item = item;
    }


    protected Transaksi(Parcel in) {
        idTransaksi = in.readInt();
        idPembeli = in.readInt();
        nama = in.readString();
        foto = in.readString();
        noTelp = in.readString();
        catatan = in.readString();
        preOrderStatus = in.readInt();
        alamat = in.readString();
        area = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        harga = in.readInt();
        item = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(idTransaksi);
        dest.writeInt(idPembeli);
        dest.writeString(nama);
        dest.writeString(foto);
        dest.writeString(noTelp);
        dest.writeString(catatan);
        dest.writeInt(preOrderStatus);
        dest.writeString(alamat);
        dest.writeString(area);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(harga);
        dest.writeInt(item);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Transaksi> CREATOR = new Parcelable.Creator<Transaksi>() {
        @Override
        public Transaksi createFromParcel(Parcel in) {
            return new Transaksi(in);
        }

        @Override
        public Transaksi[] newArray(int size) {
            return new Transaksi[size];
        }
    };
}