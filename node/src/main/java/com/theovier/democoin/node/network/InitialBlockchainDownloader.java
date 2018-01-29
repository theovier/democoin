package com.theovier.democoin.node.network;

import com.theovier.democoin.common.Blockchain;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class InitialBlockchainDownloader {

    private static final Logger LOG = Logger.getLogger(InitialBlockchainDownloader.class);

    /**
     * This DOES modify the blockchain, if a longer blockchain is obtained.
     * 1) ask all peersToDownloadFrom for their blockchain height
     * 2) sort peersToDownloadFrom by height
     * 3) ask the one with the greatest height for its chain
     * 4) iterate over the peersToDownloadFrom until we receive a valid chain
     */
    public static void downloadLongestBlockchain(final Blockchain currentBlockchain, final List<Peer> peersToDownloadFrom) {
        Map<Peer, Long> sortedPeers = getPeersSortedByHeightDesc(currentBlockchain, peersToDownloadFrom);
        for (Peer peer : sortedPeers.keySet()) {
            try {
                Blockchain remoteBlockchain = peer.requestBlockchain();
                if (currentBlockchain.substitute(remoteBlockchain)) {
                    currentBlockchain.saveToDisc();
                    break;
                }
            } catch (IOException | InterruptedException e) {
                LOG.error(e);
                peer.disconnect();
            }
        }
    }

    private static Map<Peer, Long> getPeersSortedByHeightDesc(final Blockchain currentBlockchain, final List<Peer> peers) {
        Map<Peer, Long> heightByPeer = getRelevantHeightByPeer(currentBlockchain, peers);
        return sortByValueDesc(heightByPeer);
    }

    private static Map<Peer, Long> getRelevantHeightByPeer(final Blockchain currentBlockchain, final List<Peer> peers) {
        Map<Peer, Long> heightByPeer = new HashMap<>();
        for (Peer peer : peers) {
            long receivedHeight = peer.requestBlockchainHeight(10, TimeUnit.SECONDS);
            if (currentBlockchain.getHeight() < receivedHeight) {
                heightByPeer.put(peer, receivedHeight);
            }
        }
        return heightByPeer;
    }

    /** sorts the given map, so the peer with the longest chain is first. */
    private static Map<Peer, Long> sortByValueDesc(final Map<Peer, Long> unsorted) {
        return unsorted.entrySet().stream()
                .sorted(Map.Entry.<Peer, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

}
