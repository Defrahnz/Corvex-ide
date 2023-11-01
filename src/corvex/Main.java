package corvex;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Token> tokens = new ArrayList<>();
        tokens.add(new Token("main", "keyword"));
        tokens.add(new Token("{", "symbol"));
        tokens.add(new Token("int", "keyword"));
        tokens.add(new Token("x", "identifier"));
        tokens.add(new Token(";", "symbol"));
        tokens.add(new Token("a", "identifier"));
        tokens.add(new Token("=", "operator"));
        tokens.add(new Token("2", "integer"));
        tokens.add(new Token(";", "symbol"));
        tokens.add(new Token("}", "symbol"));
        
        SyntaxAnalyzer analyzer = new SyntaxAnalyzer(tokens);
        analyzer.parse();
        Ventana vent=new Ventana();
        vent.setResizable(true);
        vent.setVisible(true);
        vent.setLocationRelativeTo(null);
       
    }
}
