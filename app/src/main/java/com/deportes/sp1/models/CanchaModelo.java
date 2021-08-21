package com.deportes.sp1.models;

public class CanchaModelo {
    String barrio, ciudad, comuna, direccion, dominio, estado, estado_cancha, iluminacion, imagen, material, pais, usuario;

    public CanchaModelo() {
    }

    public CanchaModelo(String barrio, String ciudad, String comuna, String direccion, String dominio, String estado,
                        String estado_cancha, String iluminacion, String imagen, String material, String pais) {
        this.barrio = barrio;
        this.ciudad = ciudad;
        this.comuna = comuna;
        this.direccion = direccion;
        this.dominio = dominio;
        this.estado = estado;
        this.estado_cancha = estado_cancha;
        this.iluminacion = iluminacion;
        this.imagen = imagen;
        this.material = material;
        this.pais = pais;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public CanchaModelo(String barrio, String ciudad, String comuna, String direccion,
                        String dominio, String estado, String estado_cancha, String iluminacion, String imagen, String material, String pais, String usuario) {
        this.barrio = barrio;
        this.ciudad = ciudad;
        this.comuna = comuna;
        this.direccion = direccion;
        this.dominio = dominio;
        this.estado = estado;
        this.estado_cancha = estado_cancha;
        this.iluminacion = iluminacion;
        this.imagen = imagen;
        this.material = material;
        this.pais = pais;
        this.usuario = usuario;
    }

    public String getBarrio() {
        return barrio;
    }

    public void setBarrio(String barrio) {
        this.barrio = barrio;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getDominio() {
        return dominio;
    }

    public void setDominio(String dominio) {
        this.dominio = dominio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEstado_cancha() {
        return estado_cancha;
    }

    public void setEstado_cancha(String estado_cancha) {
        this.estado_cancha = estado_cancha;
    }

    public String getIluminacion() {
        return iluminacion;
    }

    public void setIluminacion(String iluminacion) {
        this.iluminacion = iluminacion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }
}
