<<<<<<< HEAD
package com.etek.controller.minaclient;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;


public class MessageDecoder extends CumulativeProtocolDecoder {

	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {

		

		if (in.remaining() < 1) {
			return false;
		}
		byte[] a = in.array();
		int i = 0;
		while (in.remaining() > 0) {
			in.mark();
			byte tag = in.get();
			a[i] = tag;
			i++;
		}
		
		out.write(a);
		return false;
	}
}
=======
package com.etek.controller.minaclient;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;


public class MessageDecoder extends CumulativeProtocolDecoder {

	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {

		

		if (in.remaining() < 1) {
			return false;
		}
		byte[] a = in.array();
		int i = 0;
		while (in.remaining() > 0) {
			in.mark();
			byte tag = in.get();
			a[i] = tag;
			i++;
		}
		
		out.write(a);
		return false;
	}
}
>>>>>>> 806c842... 雷管组网
