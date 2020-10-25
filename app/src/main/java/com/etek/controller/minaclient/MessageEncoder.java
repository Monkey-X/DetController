package com.etek.controller.minaclient;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class MessageEncoder extends ProtocolEncoderAdapter {

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {

		IoBuffer buf = IoBuffer.allocate(500).setAutoExpand(true);

		buf.put((byte[]) message);
		buf.flip();
		out.write(buf);

	}
}
