package org.jitsi.turnserver.listeners;

import org.ice4j.*;
import org.ice4j.attribute.*;
import org.ice4j.message.*;
import org.ice4j.stack.*;

import org.jitsi.turnserver.*;
import org.jitsi.turnserver.stack.*;

/**
 * Class to handle the incoming Send indications.
 * 
 * @author Aakash Garg
 * 
 */
public class SendIndicationListener extends IndicationListener {

    /**
     * parametrised constructor.
     * 
     * @param turnStack
     *            the turnStack to set for this class.
     */
    public SendIndicationListener(TurnStack turnStack) 
    {
	super(turnStack);
    }

    /**
     * Handles the incoming send indication.
     * 
     * @param ind
     *            the indication to handle.
     * @param alloc
     *            the allocation associated with message.
     */
    @Override
    public void handleIndication(Indication ind, Allocation alloc) {
	if(ind.getMessageType()==Message.SEND_INDICATION)
	{
	    byte[] tran = ind.getTransactionID();
	    XorPeerAddressAttribute xorPeerAddress
	    	= (XorPeerAddressAttribute) ind
	    		.getAttribute(Attribute.XOR_PEER_ADDRESS);
	    xorPeerAddress.setAddress(xorPeerAddress.getAddress(), tran);
	    DataAttribute data 
	    	= (DataAttribute) ind.getAttribute(Attribute.DATA);
	    TransportAddress peerAddr = xorPeerAddress.getAddress();
	    if(alloc!=null && alloc.isPermitted(peerAddr))
	    {
		RawMessage udpMessage = new RawMessage(data.getData(),
			data.getDataLength(), peerAddr, alloc.getRelayAddress());
		try {
		    this.getTurnStack().sendUdpMessage(udpMessage, peerAddr,
			    alloc.getRelayAddress());
		} catch (StunException e) {
		    System.err.println("Unable to send message.");
		}
	    }
	    // else silently ignore the indication.
	}
    }

}