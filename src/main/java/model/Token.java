package model;

public class Token {

//     classe para criacao e classificacao dos tokens

//     os tokens que vao ser encontrados em cada linha

    private  String lexema;
    private TipoToken tipo;
    private int linha;



    public Token (String lexema, TipoToken tipo, int linha){
        this.lexema=lexema;
        this.tipo=tipo;
        this.linha=linha;

    }


    public String getLexema(){
        return  lexema;
    }

    public  TipoToken getTipo(){
        return tipo;
    }

    public int getLinha(){
        return linha;
    }






}
