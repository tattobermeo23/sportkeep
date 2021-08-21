package com.deportes.sp1.models;

public class UserModelo {
    String  nombre, avatar, ip, habilitado;

    public UserModelo() {
    }

    public UserModelo(String nombre, String avatar, String ip, String habilitado) {
        this.nombre = nombre;
        this.avatar = avatar;
        this.ip = ip;
        this.habilitado = habilitado;
    }

    public String getHabilitado() {
        return habilitado;
    }

    public void setHabilitado(String habilitado) {
        this.habilitado = habilitado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}
