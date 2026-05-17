package gui;

/** Códigos de exemplo carregáveis pelo menu "Exemplos". */
public final class Exemplos {
    private Exemplos() {}

    public static final String PADRAO = """
            program exemplo;

            var
                x    : integer;
                nome : string;

            begin
                x    := 10;
                nome := 'Olá, Pascal';

                write(nome);
                writeln(x);
            end.
            """;

    public static final String HELLO_WORLD = """
            program hello;
            begin
                writeln('Hello, World!');
            end.
            """;

    public static final String FATORIAL = """
            program fatorial;
            var
                n, i, fat : integer;
            begin
                n := 5;
                fat := 1;
                for i := 1 to n do
                    fat := fat * i;
                writeln(fat);
            end.
            """;

    public static final String COM_ERROS = """
            program comErros;
            var
                x @ : integer;
            begin
                x := 10 $ 5;
                writeln(x);
            end.
            """;

    public static String porId(int n) {
        return switch (n) {
            case 1 -> HELLO_WORLD;
            case 2 -> FATORIAL;
            case 3 -> COM_ERROS;
            default -> PADRAO;
        };
    }
}
