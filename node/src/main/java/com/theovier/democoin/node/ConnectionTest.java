package com.theovier.democoin.node;

import com.theovier.democoin.node.network.messages.Message;
import com.theovier.democoin.node.network.messages.Requests.AddressRequest;
import com.theovier.democoin.node.network.messages.Requests.Ping;
import com.theovier.democoin.node.network.messages.Responses.AddressResponse;
import org.apache.log4j.Logger;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionTest {

    private static final Logger LOG = Logger.getLogger(ConnectionTest.class);
    public static void main(String[] args) throws Exception {

        Socket socket = new Socket("192.168.1.48", 7777);
        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(new AddressRequest());

        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        Message msg = (Message) ois.readObject();

        LOG.info(msg);

        AddressResponse response = (AddressResponse) msg;
        for (InetSocketAddress inetSocketAddress : response.getAddresses()) {
            LOG.info(inetSocketAddress);
        }


        while (true) {

        }
    }
}
