CREATE TABLE cliente (
	id VARCHAR(255) NOT NULL,
    nome VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE pedido (
	id VARCHAR(255) NOT NULL,
    numero_controle BIGINT NOT NULL,
    data_cadastro DATETIME DEFAULT CURRENT_TIMESTAMP,
    nome VARCHAR(255) NOT NULL,
    valor FLOAT NOT NULL,
    quantidade INT DEFAULT 1,
    codigo_cliente VARCHAR(255) NOT NULL,
    total FLOAT DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (codigo_cliente) REFERENCES cliente(id)
);

INSERT INTO pedidos.Cliente (id, nome) VALUES('1', 'cliente1');
INSERT INTO pedidos.Cliente (id, nome) VALUES('2', 'cliente2');
INSERT INTO pedidos.Cliente (id, nome) VALUES('3', 'cliente3');
INSERT INTO pedidos.Cliente (id, nome) VALUES('4', 'cliente4');
INSERT INTO pedidos.Cliente (id, nome) VALUES('5', 'cliente5');
INSERT INTO pedidos.Cliente (id, nome) VALUES('6', 'cliente6');
INSERT INTO pedidos.Cliente (id, nome) VALUES('7', 'cliente7');
INSERT INTO pedidos.Cliente (id, nome) VALUES('8', 'cliente8');
INSERT INTO pedidos.Cliente (id, nome) VALUES('9', 'cliente9');
INSERT INTO pedidos.Cliente (id, nome) VALUES('10', 'cliente10');


