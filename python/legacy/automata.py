import sys
import os

# Ruta del archivo error.txt
error_file = "error.txt"

# Verificar y eliminar el archivo error.txt si existe
if os.path.exists(error_file):
    os.remove(error_file)

content = ""
for i in range(0, len(sys.argv)):
    content += str(sys.argv[i])
    content += '\n'

palabrasReservadas = {
    "main": "PR", "then": "PR", "if": "PR", "else": "PR", "end": "PR", "do": "PR",
    "while": "PR", "repeat": "PR", "until": "PR", "cin": "PR", "cout": "PR",
    "real": "PR", "int": "PR", "boolean": "PR", "true": "PR", "false": "PR"
}  # Donde PR= Palabra Reservada

simbolos = {
    ")": "Parentesis_Cerradura", "(": "Parentesis_Apertura", "{": "Llave_Apertura",
    "}": "Llave_Cerradura", ";": "Punto_Coma", ",": "Coma"
}

operadoresAritmeticos = {
    "+": "SUMA", "-": "RESTA", "*": "MULTIPLICACION", "/": "DIVISION", "=": "IGUALACION"
}

operadoresRelacionales = {
    "==": "igualdad", "!=": "diferente", "<>": "diferente1",
    "<": "< ", ">": ">", "<=": "<=", ">=": ">= "
}

operadoresLogicos = {
    "&&": "AND", "||": "OR", "!": "NOT"
}

decrementoIncremento = {
    "--": "Decremento", "++": "Incremento"
}

tokens = []
linea = 1
col = 1

i = 0
while i < len(content):
    if content[i].isspace():
        if content[i] == "\n":
            linea += 1
            col = 1
        i += 1
        continue
    if content[i:i + 2] == "//":
        i = content.index("\n", i)
        continue
    if content[i:i + 2] == "/*":
        aux = content.find("*/", i)
        if aux != -1:
            aux += 2
            j = i
            while j < aux:
                if content[j] == '\n':
                    linea += 1
                j += 1
            i = aux
        else:
            with open(error_file, 'a') as file_object:
                file_object.write("error:'/*' sin cerrar (linea:" + str(linea) + ", columna: " + str(col) + ")\n")
            i += 2
            col += 2
        continue
    if content[i].isalpha():
        j = i + 1
        while j < len(content) and (content[j].isalnum() or content[j] == "_"):
            j += 1
            col += 1
        token = content[i:j]
        if token in palabrasReservadas:
            tokens.append("[" + token + ", " + palabrasReservadas[token] + "]")
        else:
            tokens.append("[" + token + ", ID]")  # De ID= Identificador
        i = j
        continue
    elif content[i].isdigit():
        j = i + 1
        while j < len(content) and content[j].isdigit():
            j += 1
            col += 1
        if j < len(content) and content[j] == ".":
            j += 1
            col += 1
            while j < len(content) and content[j].isdigit():
                j += 1
                col += 1
            tokens.append("[" + content[i:j] + ", Float]")
        else:
            tokens.append("[" + content[i:j] + ", Entero]")
        i = j
        continue
    if content[i] in simbolos:
        tokens.append("[" + content[i] + ", Caracter Especial]")
        i += 1
        col += 1
        continue
    if content[i:i + 2] in operadoresRelacionales:
        tokens.append("[" + operadoresRelacionales[content[i:i + 2]] + ", Operadores Relacionales]")
        i += 2
        col += 2
        continue
    elif content[i] in operadoresRelacionales:
        tokens.append("[" + operadoresRelacionales[content[i]] + ", Operador Relacional]")
        i += 1
        continue
    if content[i:i + 2] in decrementoIncremento:
        tokens.append("[" + content[i:i + 2] + ", Operador Incremento/Decremento]")
        i += 2
        col += 1
        continue
    elif content[i] in operadoresAritmeticos:
        tokens.append("[" + content[i] + ", Operador Aritmetico]")
        i += 1
        col += 1
        continue
    else:
        with open(error_file, 'a') as file_object:
            file_object.write("error:'" + content[i] + "'(linea:" + str(linea) + ", columna: " + str(col) + ")\n")
        i += 1
        col += 1
        continue

if len(tokens) > 0:
    for item in tokens:
        print(item)
else:
    print("")

with open(error_file, 'a+') as file_object:
    error_content = file_object.read()
    if not error_content:
        print("")

# Crear el archivo error.txt si no existe
if not os.path.exists(error_file):
    print("")
    with open(error_file, 'w') as file_object:
        file_object.write("")
