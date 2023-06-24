from anaLex import tokens,analizador
from automata import tokens,error_content
from sys import stdin


#Prioridad
precedence=(('right','IGUAL'),('right','IGUALQUE'),('left','MAYOR','MENOR'),
            ('left','SUMA','RESTA'),('left','MULT','DIV','MOD'),('left','PARIZQ','PARDER'),('left','LLAVEIZQ','LLAVEDER'))

nombres={}
""" 
def p_init(t):
    'init : instrucciones'
    t[0]=t[1]
     """

