<<<<<<< HEAD
package com.etek.controller.minaclient;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;


public class MessageCodecFactory implements ProtocolCodecFactory {
	
	private final MessageDecoder decoder;
	private final MessageEncoder encoder;

	public MessageCodecFactory() {
		this.decoder = new MessageDecoder();
		this.encoder = new MessageEncoder();
	}

	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return this.decoder;
	}

	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return this.encoder;
	}
}
=======
package com.etek.controller.minaclient;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;


public class MessageCodecFactory implements ProtocolCodecFactory {
	
	private final MessageDecoder decoder;
	private final MessageEncoder encoder;

	public MessageCodecFactory() {
		this.decoder = new MessageDecoder();
		this.encoder = new MessageEncoder();
	}

	public ProtocolDecoder getDecoder(IoSession arg0) throws Exception {
		return this.decoder;
	}

	public ProtocolEncoder getEncoder(IoSession arg0) throws Exception {
		return this.encoder;
	}
}
>>>>>>> 806c842... 雷管组网
