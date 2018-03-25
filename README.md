# Democoin

This is a small example cryptocurrency which is based on _Bitcoin_.
It was written for my bachelor thesis _Implementierung einer eigenen Kryptowährung in Java nach dem Vorbild Bitcoin_ 
which roughly translates to _implementation of an own java based cryptocurrency in the manner of Bitcoin_.
It features the blockchain technology and a simple input-output-system. Please refer to the Wiki pages if you are interested in a more detailed explanation.

## Getting Started

### Docker

**Democoin** has an own docker image.

```
docker run -d \
--publish=7777:7777 \
-v /Users/theo/desktop/democoin/data:/data \
-v /Users/theo/desktop/democoin/logs:/logs \
theovier/democoin \
-m "my message" \
-a "1AQPpmmMrUBCCdEJBt7usSrXG7ZTuBs9tu"
```

This command will start a stand-alone full-node which will automatically try to connect with other nodes who are expected to be online 24/7.
This full-node will manage an own blockchain, mine new blocks and listen for incoming connections.
The docker independent parameters ``-m <message>`` and ``-a <address>`` are optional. They allow you to specifiy an address to which the mining reward is sent. 
Additionally you may define a message for the _coinbase transaction_. To enable verbose logging, add the parameter ``-v``.
The IPs of those nodes are for the moment hardcoded in the ``NetworkParams.java``.

### Generate new addresses

Democoin uses the same base-58 addresses as _Bitcoin_. 
To generate new addresses with corresponding private keys either use the provided jar directly or compile the client module yourself.
```
java -jar address_generator.jar
```
Generated addresses will always end in .key, for example: ``1AVuQjcnquXEaXgggJx7TsyMBjbatiBtNB.key``. 
They can be opened with regular text editors and contain the corresponding private key which was used to generated them. 
**Keep this file private or else someone else can spent your coins.**


### Broadcast transactions

To create and broadcast transactions, use the ``spend_tx.jar``.
You will need to provide inputs, outputs, an optional message and an IP to which the transaction is sent.
Keep in mind that transactions can only be validated by full-nodes, which means you can create invalid transactions but those are going to be rejected by honest full-node.

```
java -jar spend_tx.jar \
-in 6b62ca894aecb4474a34828ba2b7f690acae6cb2755c3beb238287eee117f23e 0 ./1AVuQjcnquXEaXgggJx7TsyMBjbatiBtNB.key \
-out 13bxQW7poaa5pC3h7NttviQ1kUBwJa7qvY 50 \
-msg "my transaction" \
-host 192.168.0.144
```

To reference an unspent transaction output (UTXO) from a previous transaction as an input, you need to provide the following
``
-in <transactionID> <outputIndex> <path to the corresponding private key file>
``. To specify an output, you'll need to define a target address and the amount you want to spent 
``
-out <targetAddress> <amount>
``. You may include as many inputs and outputs as you like; there is no limit. 

## Compile the source yourself

If you don't want to wait for new releases you can compile the source code yourself.
As this is a maven project you need to make sure to have the java build management tool _maven3_ installed.
To check your maven version type

```
mvn -v
```

it should display something along this lines

```
Apache Maven 3.5.0 (ff8f5e7444045639af65f6095c62210b5713f426; 2017-04-03T21:39:06+02:00)
```

If you installed maven just clone the repository or download the .zip and navigate to the pom.xml.
Then simply run 

```
mvn package
```

and check your /target folder for the newly generated jar file.


## Author

Theo Harkenbusch [@theovier](https://github.com/Theovier)

