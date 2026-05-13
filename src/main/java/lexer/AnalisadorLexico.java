package lexer;

import model.TipoToken;
import model.Token;

import java.util.ArrayList;
import java.util.List;

public class AnalisadorLexico {


/* este metodo vai ser responsavel por receber o codigo fonte e
 devolver a respectiva lista de tokens

 */

    public List<Token> analisar (String codigo){

        List<Token> tokens =new ArrayList<>();
        int linha=1;

        for (int i=0;i<codigo.length();i++){

            char c =codigo.charAt(i);


//             contagem de linha
            if (c =='\n'){
                linha++;
                continue;
            }
//            ignorando espacaos em branco
            if (Character.isWhitespace(c)){
                continue;
            }

//            IDENTIFICADORES
            if (Character.isLetter(c)) {
                String lexema = "";

                while (i < codigo.length() &&
                        Character.isLetterOrDigit(codigo.charAt(i))) {
                    lexema += codigo.charAt(i);
                    i++;

                }
                i--;

                tokens.add(new Token(lexema, TipoToken.IDENTIFICADOR, linha));
            }

            }

        return tokens;
    }

    /*
    O lexer ainda NÃO reconhece:

❌ números
❌ operadores
❌ delimitadores
❌ reservadas
     */
}
