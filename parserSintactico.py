# -*- coding: utf-8 -*-

# Definición de la clase Token
class Token:
    def __init__(self, token_type, value):
        self.token_type = token_type
        self.value = value

# Definición de la clase Node
class Node:
    def __init__(self, value, children=None):
        self.value = value
        self.children = children or []

    def add_child(self, node):
        self.children.append(node)

# Definición de la clase Parser
class Parser:
    def __init__(self, tokens):
        self.tokens = tokens
        self.current_token = None
        self.token_index = -1
        self.errors = []
        self.advance()

    def advance(self):
        self.token_index += 1
        if self.token_index < len(self.tokens):
            self.current_token = self.tokens[self.token_index]
        else:
            self.current_token = None
    # Verifica si el token actual coincide con el tipo esperado
    def match(self, token_type):
        if self.current_token and self.current_token.token_type == token_type:
            self.advance()
        else:
             # Registra el error si no coincide el tipo esperado
            expected_token = token_type if token_type else "fin de entrada"
            found_token = (
                self.current_token.token_type
                if self.current_token
                else "fin de entrada"
            )
            self.errors.append(
                f"Se preveía {expected_token}, se obtuvo {found_token}"
            )
            self.advance()
    # Analiza el programa principal
    def program(self):
        root = Node("Programa")
        self.match("main")
        self.match("{")
        root.add_child(self.stmts())
        self.match("}")
        return root
    # Analiza una lista de sentencias
    def stmts(self):
        root = Node("Sentencias")
        if self.current_token and self.current_token.token_type in [ "int", "float", "id", "if",  "while", "{", "cin", "cout",]:
            root.add_child(self.stmt())
        while self.current_token and self.current_token.token_type != "}" and self.current_token.token_type != "end":
            if self.current_token.token_type == "do":
                root.add_child(self.do_while_stmt())
            elif  self.current_token.value == "end" or self.current_token.token_type == "else":
                return root
            else:
                root.add_child(self.stmt())
        return root
    # Analiza una lista de sentencias
    def do_while_stmt(self):
        root = Node("SentenciaDo")
        self.match("do")
        root.add_child(self.stmt())  # Insertar la inicialización inicial dentro del bucle do-while
        while self.current_token and self.current_token.token_type != "until":
            root.add_child(self.stmt())  # Insertar más expresiones dentro del bucle do-while
        self.match("until")
        self.match("(")
        root.add_child(self.expr())
        self.match(")")
        self.match(";")
        return root
    # Analiza una sentencia
    def stmt(self):
        # Si el token actual es "int", se analiza una declaración de tipo entero
        if self.current_token and self.current_token.token_type == "int":
            root = Node("DeclaraciónInt")
            self.match("int")
            root.add_child(self.idList())
            self.match(";")
        # Si el token actual es "do", se analiza una sentencia do-while
        elif self.current_token and self.current_token.token_type == "do":
            root = self.do_while_stmt()
        # Si el token actual es "float", se analiza una declaración de tipo flotante
        elif self.current_token and self.current_token.token_type == "float":
            root = Node("DeclaraciónFloat")
            self.match("float")
            root.add_child(self.idList())
            self.match(";")
        # Si el token actual es un identificador, se analiza una asignación o una expresión relacional
        elif self.current_token and self.current_token.token_type == "id":
            root = Node("Asignación")
            id_node = Node(self.current_token.value)
            self.match("id")
            root.add_child(id_node)
            # Si el token actual es "++" o "--", se agrega un nodo de operador de incremento o decremento
            if self.current_token and self.current_token.token_type in ["++", "--"]:
                op_node = Node(self.current_token.value)
                self.match(self.current_token.token_type)
                root.add_child(op_node)
            # Si el token actual es un operador relacional, se agrega un nodo de operador y un nodo de operando
            elif self.current_token and self.current_token.token_type in [ "<", ">", "<=", ">=", "==", "!=", ]:
                op_node = Node(self.current_token.value)
                self.match(self.current_token.token_type)
                if self.current_token and self.current_token.token_type in [
                    "id",
                    "num",
                ]:
                    operand_node = Node(self.current_token.value)
                    root.add_child(operand_node)
                    self.match(self.current_token.token_type)
            # Si no se cumple ninguna condición anterior, se analiza una asignación normal
            else:
                self.match("=")
                expr_node = self.expr()
                root.add_child(expr_node)
            self.match(";")
        # Si el token actual es "if", se analiza una sentencia if
        elif self.current_token and self.current_token.token_type == "if":
            root = Node("SentenciaIf")
            self.match("if")
            # Se analizan la condicion dentro del if
            if self.current_token and self.current_token.token_type == "(":
                self.match("(")
                expr_node = self.expr()
                root.add_child(expr_node)
                self.match(")")

            stmt_node = self.stmts()
            # Se analizan las sentencias dentro del if
            if self.current_token and self.current_token.token_type == "{":
                self.match("{")
                root.add_child(stmt_node)
                self.match("}")
            else:
                root.add_child(stmt_node)

            if self.current_token and self.current_token.token_type == "else":
                self.match("else")
                else_stmt_node = self.stmts()
                root.add_child(else_stmt_node)

            self.match("end")
        # Si el token actual es "while", se analiza una sentencia while
        elif self.current_token and self.current_token.token_type == "while":
            root = Node("SentenciaWhile")
            self.match("while")
            self.match("(")
            expr_node = self.expr()
            root.add_child(expr_node)
            self.match(")")
            stmt_node = self.stmt()
            root.add_child(stmt_node)
        # Si el token actual es "{", se analiza un bloque de sentencias
        elif self.current_token and self.current_token.token_type == "{":
            root = Node("Bloque")
            self.match("{")
            root.add_child(self.stmts())
            self.match("}")
        # Si el token actual es "cin", se analiza una sentencia de entrada
        elif self.current_token and self.current_token.token_type == "cin":
            root = Node("SentenciaInput")
            
            self.match("cin")
            root.add_child(self.idList())
            self.match(";")
        # Si el token actual es "cout", se analiza una sentencia de salida
        elif self.current_token and self.current_token.token_type == "cout":
            root = Node("SentenciaOutput")
            self.match("cout")
            root.add_child(self.expr())
            self.match(";")
        # Si no se cumple ninguna condición anterior, se trata de un error
        else:
            root = Node("Error")
            error_token = self.current_token.value if self.current_token else None
            if error_token:
                self.errors.append(f"Sentencia inválida: {error_token}")
            self.advance()
        return root
    # Analiza una lista de identificadores
    def idList(self):
        root = Node("IdList")
        id_node = Node(self.current_token.value)
        root.add_child(id_node)
        self.match("id")
        # Si hay una coma, agrega otro identificador a la lista
        while self.current_token and  self.current_token.value ==","  and  self.current_token.value !=";":
            self.match(",")
            id_node = Node(self.current_token.value)
            root.add_child(id_node)
            self.match("id")
            
        return root
    # Analiza una expresión
    def expr(self):
        root = self.term()
        # Si hay un operador de suma o resta, se añade a la expresión
        while self.current_token and self.current_token.token_type in ["+", "-"]:
            op_node = Node(self.current_token.value)
            self.match(self.current_token.token_type)
            op_node.add_child(root)
            root = op_node
            root.add_child(self.term())
        
        return root
    # Analiza un término
    def term(self):
        root = self.factor()
        # Si hay un operador de multiplicación o división, se añade al término
        while self.current_token and self.current_token.token_type in ["*", "/"]:
            op_node = Node(self.current_token.value)
            self.match(self.current_token.token_type)
            op_node.add_child(root)
            root = op_node
            root.add_child(self.factor())
        
        return root
    # Analiza un factor
    def factor(self):
        root = self.primary()
        # Analiza un factor
        while self.current_token and self.current_token.token_type in [ "<",  ">", "<=", ">=",  "==", "!=",]:
            op_node = Node(self.current_token.value)
            self.match(self.current_token.token_type)
            op_node.add_child(root)
            root = op_node
            root.add_child(self.primary())
        
        return root
    # Analiza un elemento primario
    def primary(self):
        # Si el token actual es un paréntesis de apertura, se analiza la expresión contenida
        if self.current_token and self.current_token.token_type == "(":
            self.match("(")
            root = self.expr()
            self.match(")")
        elif self.current_token and self.current_token.token_type in ["id", "num"]:
            root = Node(self.current_token.value)
            self.match(self.current_token.token_type)
        # Si no se cumple ninguna condición anterior, se trata de un error
        else:
            root = Node("Error")
            error_token = self.current_token.value if self.current_token else None
            self.errors.append(f"Factor inválido: {error_token}")
            self.advance()
        
        return root
    
    # Analiza una expresión relacional
    def relational_expr(self):
        root = self.term()
        # Si hay un operador relacional, se añade a la expresión
        if self.current_token and self.current_token.token_type in [ "<", ">", "<=", ">=", "==", "!=", ]:
            op_node = Node(self.current_token.value)
            self.match(self.current_token.token_type)
            root.add_child(op_node)
            # Si hay un identificador o un número después del operador, se añade como operando
            if self.current_token and self.current_token.token_type in ["id", "num"]:
                operand_node = Node(self.current_token.value)
                root.add_child(operand_node)
                self.match(self.current_token.token_type)
        return root

    # Realiza el análisis sintáctico completo del programa
    def parse(self):
        ast = self.program()

        if self.errors:
            print("Se encontraron errores de sintaxis. La compilación ha fallado.")
        else:
            print("La sintaxis es correcta. La compilación ha sido exitosa.")

        return ast


with open("tokens_lexico.txt", "r") as file:
    lines = file.readlines()

# genera la secuencia  de objetos Token
token_list = []
for line in lines:
    line = line.strip()
    if line:
        token_parts = line.split("<--->")
        if token_parts[1].strip() == "identificador":
            token_type = "id"
            value = token_parts[0].strip()
        elif token_parts[1].strip() == "flotante":
            token_type = "num"
            value = token_parts[0].strip()
        elif token_parts[1].strip() == "entero":
            token_type = "num"
            value = token_parts[0].strip()
        else:
            token_type = token_parts[0].strip()
            value = token_parts[0].strip()
        token = Token(token_type, value)
        token_list.append(token)


parser = Parser(token_list)
ast = parser.parse()


# Imprimir errores
f = open("errore_sintactico.txt", "w", encoding="utf-8")
if parser.errors:
    print("Errores de sintaxis:")
    for error in parser.errors:
        f.write(error+"\n")
        print(error)
f.close()

# Imprimir arbol sintactico
f = open("arbol_sintactico.txt", "w", encoding="utf-8")
print("Arbol de Sintactico:")
f.write("Arbol Sintactico")
def print_ast(node, level=0, is_last_child=False):
    indent = " | " * level
    print(f"{indent}{node.value}")
    f.write(f"\n{indent}{node.value}")
    for i, child in enumerate(node.children):
        is_last = i == len(node.children) - 1
        print_ast(child, level + 1, is_last)

print_ast(ast)
f.close()
