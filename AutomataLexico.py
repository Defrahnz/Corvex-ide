# -*- coding: utf-8 -*-

# Crear una instancia de OptionParser con la versión del programa
from optparse import OptionParser

parser = OptionParser(version="%prog 2.8.9")
parser.add_option(
    "-f",
    "--*file",
    action="store",
    dest="file",
    default="test.txt",
    type="string",
    help="specify a file to load. todos los derechos reservados",
)
# Analizar los argumentos de línea de comandos
options, args = parser.parse_args()

# Abrir el archivo especificado en modo de lectura
file = open(options.file, "r")
codigofuente = file.read()
# Diccionarios de tokens
palabras_reservadas = {
    "main": "palabra reservada",
    "then": "palabra reservada",
    "if": "palabra reservada",
    "else": "palabra reservada",
    "end": "palabra reservada",
    "do": "palabra reservada",
    "while": "palabra reservada",
    "repeat": "palabra reservada",
    "until": "palabra reservada",
    "cin": "palabra reservada",
    "cout": "palabra reservada",
    "real": "palabra reservada",
    "int": "palabra reservada",
    "boolean": "palabra reservada",
    "true": "palabra reservada",
    "false": "palabra reservada",
    "float":"palabra reservada",
}
simbolos_especiales = {
    "(": "PAR_IZQ",
    ")": "PAR_DER",
    "{": "LLAVE_IZQ",
    "}": "LLAVE_DER",
    ";": "PUNTO_COMA",
    ",": "COMA",
}
operadores_aritmeticos = {
    "+": "SUMA",
    "-": "RESTA",
    "*": "MULTIPLICACION",
    "/": "DIVISION",
    "=": "IGUALACION",
}
operadores_relacionales = {
    "==": "IGUALDAD",
    "!=": "DIFERENTE",
    "<>": "DIFERENTE2",
    "<": "MENOR_QUE",
    ">": "MAYOR_QUE",
    "<=": "MENOR_IGUAL_QUE",
    ">=": "MAYOR_IGUAL_QUE",
}
operadores_logicos = {"&&": "AND", "||": "OR", "!": "NOT"}
operadores_dobles = {"++": "INCREMENTO", "--": "DECREMENTO"}
tokens = [] # Lista para almacenar los tokens encontrados
errors = [] # Lista para almacenar los errores encontrados
linea = 1 # Variable para rastrear el número de línea actual
col = 1 # Variable para rastrear la columna actual

i = 0
while i < len(codigofuente):
    # Obvia espacios en blanco
    if codigofuente[i].isspace():
        if codigofuente[i] == "\n":
            linea += 1
            col = 1
        else:
            col += 1
        i += 1
        continue
    # Identificar comentarios de una línea
    if codigofuente[i : i + 2] == "//":
        i = codigofuente.index("\n", i)
        continue
    # Identificar comentarios multilinea
    if codigofuente[i : i + 2] == "/*":
        aux = codigofuente.index("*/", i) + 2
        j = i
        while j < aux:
            if codigofuente[j] == "\n":
                linea += 1
            j += 1
        i = aux
        continue
    # Identificar palabras reservadas, identificadores y números
    if codigofuente[i].isalpha():
        j = i + 1
        while j < len(codigofuente) and (
            codigofuente[j].isalnum() or codigofuente[j] == "_"
        ):
            j += 1
        token = codigofuente[i:j]
        if token in palabras_reservadas:
            tokens.append(" " + token + "  <---> " + palabras_reservadas[token] + " <---> ")
        else:
            tokens.append(" " + token + "  <---> identificador <--->")
        col += j - i
        i = j
        continue
    elif codigofuente[i].isdigit():
        j = i + 1
        while j < len(codigofuente) and codigofuente[j].isdigit():
            j += 1
        if j < len(codigofuente) and codigofuente[j] == ".":
            j += 1
            while j < len(codigofuente) and codigofuente[j].isdigit():
                j += 1
            tokens.append(" " + codigofuente[i:j] + " <---> flotante <---> ")
        else:
            tokens.append(" " + codigofuente[i:j] + " <---> entero <---> ")
        col += j - i
        i = j
        continue
    # Identificar símbolos especiales
    if codigofuente[i] in simbolos_especiales:
        tokens.append(" " + codigofuente[i] + "  <---> simbolo especial <--->")
        i += 1
        col += 1
        continue
    # Identificar operadores aritméticos y relacionales
    if codigofuente[i : i + 2] in operadores_relacionales:
        tokens.append(" " + codigofuente[i : i + 2] + " <---> operador relacional <--->")
        i += 2
        col += 2
        continue
    elif codigofuente[i] in operadores_relacionales:
        tokens.append(" " + codigofuente[i] + " <---> operador relacional <--->")
        i += 1
        continue
    if codigofuente[i : i + 2] in operadores_dobles:
        tokens.append(" " + codigofuente[i : i + 2] + " <---> operador aritmetico <--->")
        i += 2
        col += 1
        continue
    elif codigofuente[i] in operadores_aritmeticos:
        tokens.append(" " + codigofuente[i] + "  <---> operador aritmetico <--->")
        i += 1
        col += 1
        continue
    else:
        errors.append(
            "error:'"
            + codigofuente[i]
            + "'(linea:"
            + str(linea)
            + ", columna: "
            + str(col)
            + ")"
        )
        i += 1
        col += 1
        continue
# Ingresa lo tokens a txt
f = open("tokens_lexico.txt", "w")
for item in tokens:
    print(item)
    f.write(item + "\n")
f.close()
# Ingresa los errores a un txt
f = open("error_lexico.txt", "w")
for error in errors:
    print(error)
    f.write(error + "\n")
f.close()
