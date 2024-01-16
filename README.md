# Pedidos

ENDPOINTS

 POST http://localhost:8180/pedido

PARAM: "file" -> test.xml

    <pedidos>
      <pedido>
        <numero_controle>20</numero_controle>
        <data_cadastro></data_cadastro>
        <nome>Joao</nome>
        <valor>1</valor>
        <quantidade>6</quantidade>
        <codigo_cliente>1</codigo_cliente>
      </pedido>......

PARAM: "file" -> test.json


    {
    "numero_controle": "31202",
    "data_cadastro": "",
    "nome": "Joao1",
    "valor": "1",
    "quantidade": "6",
    "codigo_cliente": "1"
    },.....

 GET http://localhost:8180/pedido/search?nome=Joao


