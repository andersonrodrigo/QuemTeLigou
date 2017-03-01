package com.andersonsilva.quemteligou.entity;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.InputStream;
import java.util.List;

public class Sms {
    private String _id;
    private String _address;
    private String _msg;
    private String _readState; //"0" for have not read sms and "1" for have read sms
    private String _time;
    private String _folderName;


    private String numeroTeLigou;
    private String dataLigacao;
    private String horaLigacao;

    private String loja;
    private String dataCompra;
    private String valor;
    private String valorReal;
    private String finalCartao;
    private String banco;

    private Bitmap imagemContato;

    private String nomeContato;

    private String dataInicial;
    private String dataFinal;

    private List<String> ocorrencias;

    public String getId(){
        return _id;
    }
    public String getAddress(){
        return _address;
    }
    public String getMsg(){
        return _msg;
    }
    public String getReadState(){
        return _readState;
    }
    public String getTime(){
        return _time;
    }
    public String getFolderName(){
        return _folderName;
    }


    public void setId(String id){
        _id = id;
    }
    public void setAddress(String address){
        _address = address;
    }
    public void setMsg(String msg){
        _msg = msg;
    }
    public void setReadState(String readState){
        _readState = readState;
    }
    public void setTime(String time){
        _time = time;
    }
    public void setFolderName(String folderName){
        _folderName = folderName;
    }


    /** BRADESCO CARTOES:
     * COMPRA APROVADA NO CARTAO FINAL 9900
     * EM 12/02/2017 07:40 VALOR DE $ 10,28 NO(A)
     * PANIFICADORA F E C GUI BELO HORIZON.
     * **/

    public String getLoja() {
        return loja;
    }

    public void setLoja(String loja) {
        this.loja = loja;
    }

    public String getDataCompra() {
        return dataCompra;
    }

    public void setDataCompra(String dataCompra) {
        this.dataCompra = dataCompra;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(String dataInicial) {
        this.dataInicial = dataInicial;
    }

    public String getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(String dataFinal) {
        this.dataFinal = dataFinal;
    }

    public String getValorReal() {
        return valorReal;
    }

    public void setValorReal(String valorReal) {
        this.valorReal = valorReal;
    }

    public String getFinalCartao() {
        return finalCartao;
    }

    public void setFinalCartao(String finalCartao) {
        this.finalCartao = finalCartao;
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getNumeroTeLigou() {
        return numeroTeLigou;
    }

    public void setNumeroTeLigou(String numeroTeLigou) {
        this.numeroTeLigou = numeroTeLigou;
    }

    public String getHoraLigacao() {
        return horaLigacao;
    }

    public void setHoraLigacao(String horaLigacao) {
        this.horaLigacao = horaLigacao;
    }

    public String getDataLigacao() {
        return dataLigacao;
    }

    public void setDataLigacao(String dataLigacao) {
        this.dataLigacao = dataLigacao;
    }

    public List<String> getOcorrencias() {
        return ocorrencias;
    }

    public void setOcorrencias(List<String> ocorrencias) {
        this.ocorrencias = ocorrencias;
    }

    public String getNomeContato() {
        return nomeContato;
    }

    public void setNomeContato(String nomeContato) {
        this.nomeContato = nomeContato;
    }

    public Bitmap getImagemContato() {
        return imagemContato;
    }

    public void setImagemContato(Bitmap imagemContato) {
        this.imagemContato = imagemContato;
    }
}