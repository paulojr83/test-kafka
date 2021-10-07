# Estudando kafka

* Criando um topico no kafka

```
~$ kafka-topics --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic LOJA_NOVO_PEDIDO
```
  
* Listar os topico

```
~$ kafka-topics --list --bootstrap-server localhost:9092
```

* Criando um producer no console do kafka

```
~$ kafka-console-producer --broker-list localhost:9092 --topic LOJA_NOVO_PEDIDO
```

* Consumir uma mensagem do kafka
```
~$ kafka-console-consumer --bootstrap-server localhost:9092 --topic LOJA_NOVO_PEDIDO
  
~$ kafka-console-consumer --bootstrap-server localhost:9092 --topic LOJA_NOVO_PEDIDO --from-beginning
```


* Verificar as informações dobre o topicos
```
~$ kafka-topics --bootstrap-server localhost:9092 --describe
```


* Alter a quantidade de partições de um topico ( executar no zookeeper )
```
~$ kafka-topics --alter --zookeeper localhost:2181 --topic ECOMMERCE_NEW_ORDER --partitions 3
~$ kafka-topics --alter --zookeeper localhost:2181 --topic ECOMMERCE_ORDER_REJECTED --partitions 3
~$ kafka-topics --alter --zookeeper localhost:2181 --topic ECOMMERCE_SEND_EMAIL --partitions 3
~$ kafka-topics --alter --zookeeper localhost:2181 --topic ECOMMERCE_ORDER_APPROVED --partitions 3
```

* Verificar todos os grupos
```
~$ kafka-consumer-groups --all-groups --bootstrap-server localhost:9092  --describe
```


* [kafka transaction](https://itnext.io/kafka-transaction-56f022af1b0c)
