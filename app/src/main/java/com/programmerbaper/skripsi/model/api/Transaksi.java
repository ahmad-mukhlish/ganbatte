package com.programmerbaper.skripsi.model.api;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Transaksi implements Parcelable {

    @SerializedName("id_transaksi")
    @Expose
    private Integer idTransaksi;
    @SerializedName("id_pedagang")
    @Expose
    private Integer idPedagang;
    @SerializedName("id_pembeli")
    @Expose
    private Integer idPembeli;
    @SerializedName("catatan")
    @Expose
    private String catatan;
    @SerializedName("tanggal")
    @Expose
    private String tanggal;
    @SerializedName("pre_order_status")
    @Expose
    private Integer preOrderStatus;
    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("cuaca")
    @Expose
    private String cuaca;
    @SerializedName("alamat")
    @Expose
    private String alamat;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("rating")
    @Expose
    private Integer rating;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("nama")
    @Expose
    private String nama;
    @SerializedName("no_telp")
    @Expose
    private String noTelp;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("foto")
    @Expose
    private String foto;
    @SerializedName("fcm_token")
    @Expose
    private String fcmToken;
    @SerializedName("harga")
    @Expose
    private Integer harga;
    @SerializedName("item")
    @Expose
    private Integer item;

    public Integer getIdTransaksi() {
        return idTransaksi;
    }

    public void setIdTransaksi(Integer idTransaksi) {
        this.idTransaksi = idTransaksi;
    }

    public Integer getIdPedagang() {
        return idPedagang;
    }

    public void setIdPedagang(Integer idPedagang) {
        this.idPedagang = idPedagang;
    }

    public Integer getIdPembeli() {
        return idPembeli;
    }

    public void setIdPembeli(Integer idPembeli) {
        this.idPembeli = idPembeli;
    }

    public String getCatatan() {
        return catatan;
    }

    public void setCatatan(String catatan) {
        this.catatan = catatan;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public Integer getPreOrderStatus() {
        return preOrderStatus;
    }

    public void setPreOrderStatus(Integer preOrderStatus) {
        this.preOrderStatus = preOrderStatus;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCuaca() {
        return cuaca;
    }

    public void setCuaca(String cuaca) {
        this.cuaca = cuaca;
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

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Integer getHarga() {
        return harga;
    }

    public void setHarga(Integer harga) {
        this.harga = harga;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }


    protected Transaksi(Parcel in) {
        idTransaksi = in.readByte() == 0x00 ? null : in.readInt();
        idPedagang = in.readByte() == 0x00 ? null : in.readInt();
        idPembeli = in.readByte() == 0x00 ? null : in.readInt();
        catatan = in.readString();
        tanggal = in.readString();
        preOrderStatus = in.readByte() == 0x00 ? null : in.readInt();
        status = in.readByte() == 0x00 ? null : in.readInt();
        cuaca = in.readString();
        alamat = in.readString();
        area = in.readString();
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        rating = in.readByte() == 0x00 ? null : in.readInt();
        username = in.readString();
        password = in.readString();
        nama = in.readString();
        noTelp = in.readString();
        email = in.readString();
        foto = in.readString();
        fcmToken = in.readString();
        harga = in.readByte() == 0x00 ? null : in.readInt();
        item = in.readByte() == 0x00 ? null : in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (idTransaksi == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(idTransaksi);
        }
        if (idPedagang == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(idPedagang);
        }
        if (idPembeli == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(idPembeli);
        }
        dest.writeString(catatan);
        dest.writeString(tanggal);
        if (preOrderStatus == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(preOrderStatus);
        }
        if (status == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(status);
        }
        dest.writeString(cuaca);
        dest.writeString(alamat);
        dest.writeString(area);
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitude);
        }
        if (rating == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(rating);
        }
        dest.writeString(username);
        dest.writeString(password);
        dest.writeString(nama);
        dest.writeString(noTelp);
        dest.writeString(email);
        dest.writeString(foto);
        dest.writeString(fcmToken);
        if (harga == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(harga);
        }
        if (item == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(item);
        }
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