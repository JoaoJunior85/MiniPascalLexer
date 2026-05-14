package principal;

import lexer.AnalisadorLexico;
import model.Token;

import java.util.List;

public class Main {


    public static void main(String[] args) {
        String codigo = """
        program teste;

        var x : integer;

        begin

            x := 10;

            if x >= 5 then
                write(x);

        end.
        """;

        AnalisadorLexico analisador= new AnalisadorLexico();

        List<Token> tokens=analisador.analisar(codigo);

        for(Token t :tokens){
            System.out.println(
                    t.getTipo()
                    +"->"
                    +t.getLexema()
            );

        }

    }
}
