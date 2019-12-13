package com.givers.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class NettyWebServerFactorySslCustomizer {
//  implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {
//	@Value("")
//	private String port
// 
//    @Override
//    public void customize(NettyReactiveWebServerFactory serverFactory) {
////        Ssl ssl = new Ssl();
////        ssl.setEnabled(true);
////        ssl.setKeyStore("/Users/i340033/Documents/givers2.jks");
////        ssl.setKeyAlias("givers2");
////        ssl.setKeyPassword("simeon92");
////        ssl.setKeyStorePassword("simeon92");
////        Http2 http2 = new Http2();
////        http2.setEnabled(false);
////        serverFactory.addServerCustomizers(new SslServerCustomizer(ssl, http2, null));
//        serverFactory.setPort(8443);
//    }
//}
}