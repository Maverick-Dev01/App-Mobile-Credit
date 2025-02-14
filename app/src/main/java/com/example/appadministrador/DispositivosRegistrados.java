package com.example.appadministrador;

public class DispositivosRegistrados {
    //Datos del telefono
    private String dispositivoId, userId, IMEI, marcaTelefono, Precio, nombreCliente, domicilio, edad;
    private String fechaInicio, fechaFin, pagoSemanal, montoTotal, estado;
    private String porcentajeInteres;
    private int totalSemanas;

    public DispositivosRegistrados() {
    }

    public DispositivosRegistrados(String dispositivoId, String userId, String IMEI, String marcaTelefono, String precio, String nombreCliente, String domicilio, String edad, String fechaInicio, String fechaFin, String pagoSemanal, String montoTotal, String estado, String porcentajeInteres, int totalSemanas) {
        this.dispositivoId = dispositivoId;
        this.userId = userId;
        this.IMEI = IMEI;
        this.marcaTelefono = marcaTelefono;
        Precio = precio;
        this.nombreCliente = nombreCliente;
        this.domicilio = domicilio;
        this.edad = edad;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.pagoSemanal = pagoSemanal;
        this.montoTotal = montoTotal;
        this.estado = estado;
        this.porcentajeInteres = porcentajeInteres;
        this.totalSemanas = totalSemanas;
    }

    public String getDispositivoId() {
        return dispositivoId;
    }

    public String getUserId() {
        return userId;
    }

    public String getIMEI() {
        return IMEI;
    }

    public String getMarcaTelefono() {
        return marcaTelefono;
    }

    public String getPrecio() {
        return Precio;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getDomicilio() {
        return domicilio;
    }

    public String getEdad() {
        return edad;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public String getPagoSemanal() {
        return pagoSemanal;
    }

    public String getMontoTotal() {
        return montoTotal;
    }

    public String getEstado() {
        return estado;
    }

    public int getTotalSemanas() {
        return totalSemanas;
    }

    public void setDispositivoId(String dispositivoId) {
        this.dispositivoId = dispositivoId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setIMEI(String IMEI) {
        this.IMEI = IMEI;
    }

    public void setMarcaTelefono(String marcaTelefono) {
        this.marcaTelefono = marcaTelefono;
    }

    public void setPrecio(String precio) {
        Precio = precio;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public void setDomicilio(String domicilio) {
        this.domicilio = domicilio;
    }

    public void setEdad(String edad) {
        this.edad = edad;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setPagoSemanal(String pagoSemanal) {
        this.pagoSemanal = pagoSemanal;
    }

    public void setMontoTotal(String montoTotal) {
        this.montoTotal = montoTotal;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setTotalSemanas(int totalSemanas) {
        this.totalSemanas = totalSemanas;
    }

    public String getPorcentajeInteres() {
        return porcentajeInteres;
    }

    public void setPorcentajeInteres(String porcentajeInteres) {
        this.porcentajeInteres = porcentajeInteres;
    }
}

