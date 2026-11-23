package com.devsenior.sillon.pago;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "sillon.stripe")
public class PropiedadesStripe {

    private String claveSecreta;
    private String webhookSecreto;
    private String precioPro;
    private String precioCreditos;
    private String urlApi;

    public String getClaveSecreta() {
        return claveSecreta;
    }

    public void setClaveSecreta(String claveSecreta) {
        this.claveSecreta = claveSecreta;
    }

    public String getWebhookSecreto() {
        return webhookSecreto;
    }

    public void setWebhookSecreto(String webhookSecreto) {
        this.webhookSecreto = webhookSecreto;
    }

    public String getPrecioPro() {
        return precioPro;
    }

    public void setPrecioPro(String precioPro) {
        this.precioPro = precioPro;
    }

    public String getPrecioCreditos() {
        return precioCreditos;
    }

    public void setPrecioCreditos(String precioCreditos) {
        this.precioCreditos = precioCreditos;
    }

    public String getUrlApi() {
        return urlApi;
    }

    public void setUrlApi(String urlApi) {
        this.urlApi = urlApi;
    }
}
