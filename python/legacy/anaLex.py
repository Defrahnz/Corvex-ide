import ply.lex as lex


palabrasReservadas={'if':'IF','while':'WHILE','cout':'COUT','cin':'CIN',
                    'main':'MAIN','then':'THEN','else':'ELSE','end':'END',
                    'do':'DO','repeat':'REPEAT','until':'UNTIL','real':'REAL','int':'INT',
                    'boolean':'BOOLEAN','true':'TRUE','false':'FALSE'}

tokens=['PTCOMA','LLAVEIZQ','LLAVEDER','PARIZQ','PARDER','DOBPUNT','IGUAL','MAYORIGUAL',
        'MENOR','MENORIGUAL','IGUALQUE','SUMA','RESTA','MULT','DIV','MOD','ID','ENTERO',
        'MASMAS','MENOSMENOS','DIFERENTE']+list(palabrasReservadas.values())

#Tokens

t_PTCOMA=r';'
t_LLAVEIZQ=r'{'
t_LLAVEDER=r'}'
t_PARIZQ=r'\('
t_PARDER=r'\)'
t_IGUAL=r'='
t_SUMA=r'\+'
t_RESTA=r'\-'
t_MULT=r'\*'
t_DIV=r'/'
t_MOD=r'\%'
t_MENOR=r'<'
t_MAYOR=r'>'
t_MENORIGUAL=r'<='
t_MAYORIGUAL=r'>='
t_DIFERENTE=r'!='
t_DOBPUNTO=r':'
t_MASMAS=r'++'
t_MENOSMENOS=r'--'
t_CIN=r'cin'
t_COUT=r'cout'
t_MAIN=r'main'
t_IF=r'if'
t_WHILE=r'while'
t_THEN=r'then'
t_DO=r'do'
t_REPEAT=r'repeat'
t_ELSE=r'else'
t_END=r'end'
t_UNTIL=r'until'
t_REAL=r'real'
t_INT=r'int'
t_BOOLEAN=r'boolean'
t_TRUE=r'true'
t_FALSE=r'false'

# Ignoramos estos tokens
t_ignore="\t\n"

#Detectamos si hay saltos de linea
def t_newline(t):
    r'\n+'
    t.lexer.newlino+= len(t.value)

def t_COMENTARIO(t):
    r'//.*'
    pass

#Detecta un identificador para retornarlo
def t_ID(t):
    r'[a-zA-Z_][a-zA-Z0-9_]*'
    #Mayus
    t.type=palabrasReservadas.get(t.value.lower(),'ID')
    return t
#Detecta un numero para retornarlo
def t_ENTERO(t):
    r'\d+'
    try:
        t.value=int(t.value)
    except ValueError:
        print("Integer value to large %d",t.value)
        t.value=0
    return t
#Detecta un numero decimal para retornarlo
def t_DECIMAL(t):
    r'(\d*\.\d+)|(\d+\.\d*))'
    try:
        t.value=float(t.value)
    except ValueError:
        print("Float value to large %d",t.value)
        t.value=0
    return t

#Detecta cadenas
def t_CADENA(t):
    r'\".*?\"'
    t.value=t.value[1:-1]#Quita comillas
    return t

#Detecta los errores
def t_error(t):
    print("Error lexico %s",t.value[0])
    t.lexer.skip(1)
    return t

analizador=lex.lex()
        




