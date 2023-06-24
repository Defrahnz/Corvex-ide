""" import jpype
import jpype.imports
# Iniciar la máquina virtual de Java
jpype.startJVM(jpype.getDefaultJVMPath())
import java
import javax
from javax.swing import JFrame, JTree, JScrollPane
from javax.swing.tree import DefaultMutableTreeNode



# Crear el árbol de nodos
root = DefaultMutableTreeNode("Root")
node1 = DefaultMutableTreeNode("Node 1")
node2 = DefaultMutableTreeNode("Node 2")
node3 = DefaultMutableTreeNode("Node 3")

root.add(node1)
root.add(node2)
root.add(node3)

# Crear el componente JTree
tree = JTree(root)

# Crear la ventana
frame = JFrame("JTree Example")
frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
frame.setSize(300, 300)

# Agregar el árbol al panel de desplazamiento y luego al marco
scrollPane = JScrollPane(tree)
frame.getContentPane().add(scrollPane)

# Mostrar la ventana
frame.setVisible(True)

# Detener la máquina virtual de Java al cerrar la ventana
jpype.shutdownJVM() """

import ply.lex as lex
import ply.yacc as yacc

# Lista de tokens
tokens = [
    'ID',
    'NUMBER',
    'PRINT',
    'EQUALS',
    'PLUS',
    'MINUS',
    'TIMES',
    'DIVIDE',
    'LPAREN',
    'RPAREN'
]

# Expresiones regulares para tokens simples
t_EQUALS = r'='
t_PLUS = r'\+'
t_MINUS = r'-'
t_TIMES = r'\*'
t_DIVIDE = r'/'
t_LPAREN = r'\('
t_RPAREN = r'\)'

# Definición de tokens complejos
def t_ID(t):
    r'[a-zA-Z_][a-zA-Z_0-9]*'
    t.type = 'ID'
    return t

def t_NUMBER(t):
    r'\d+'
    t.value = int(t.value)
    return t

# Ignorar espacios y saltos de línea
t_ignore = ' \n'

# Manejo de errores
def t_error(t):
    print("Carácter ilegal: %s" % t.value[0])
    t.lexer.skip(1)

# Construcción del lexer
lexer = lex.lex()

# Reglas de la gramática
def p_program(p):
    'program : statements'

def p_statements(p):
    '''statements : statements statement
                  | statement'''

def p_statement(p):
    '''statement : assignment
                 | print_statement'''

def p_assignment(p):
    'assignment : ID EQUALS expression'

def p_expression(p):
    '''expression : term
                  | expression PLUS term
                  | expression MINUS term'''

def p_term(p):
    '''term : factor
            | term TIMES factor
            | term DIVIDE factor'''

def p_factor(p):
    '''factor : ID
              | NUMBER
              | LPAREN expression RPAREN'''

def p_print_statement(p):
    'print_statement : PRINT LPAREN expression RPAREN'

def p_error(p):
    print("Error de sintaxis")

# Construcción del parser
parser = yacc.yacc()

# Entrada de ejemplo
data = '''
    x = 10
    print(x + 5)
'''

# Análisis léxico y sintáctico
lexer.input(data)
while True:
    tok = lexer.token()
    if not tok:
        break
    print(tok)

result = parser.parse(data)