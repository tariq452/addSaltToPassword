package org.example;

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSignature;

import javax.xml.soap.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.security.*;
import java.util.Properties;

public class SOAPMessageSigner {

    public static void main(String[] args) throws Exception {
        // Load keystore
        KeyStore keyStore = loadKeyStore("keystore.jks", "keystore-password");

        // Sign SOAP message
        SOAPMessage soapMessage = createSampleSOAPMessage();
        SOAPMessage signedMessage = signSOAPMessage(soapMessage, keyStore, "alias", "key-password");

        // Print signed SOAP message
        signedMessage.writeTo(System.out);
    }

    private static KeyStore loadKeyStore(String keystorePath, String keystorePassword) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("jks");
        try (FileInputStream fis = new FileInputStream(keystorePath)) {
            keyStore.load(fis, keystorePassword.toCharArray());
        }
        return keyStore;
    }

    private static SOAPMessage createSampleSOAPMessage() throws SOAPException {
        MessageFactory messageFactory = MessageFactory.newInstance();
        SOAPMessage soapMessage = messageFactory.createMessage();

        // Create SOAP envelope and body
        SOAPEnvelope envelope = soapMessage.getSOAPPart().getEnvelope();
        SOAPBody body = envelope.getBody();

        // Add a sample body element
        body.addBodyElement(envelope.createName("TestElement", "ns", "http://example.com/"));

        return soapMessage;
    }

    private static SOAPMessage signSOAPMessage(SOAPMessage soapMessage, KeyStore keyStore, String alias, String keyPassword) throws WSSecurityException, SOAPException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
        // Get private key and certificate
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, keyPassword.toCharArray());
        java.security.cert.Certificate certificate = keyStore.getCertificate(alias);

        // Create WSS4J Crypto properties
        Properties properties = CryptoFactory.getProperties("org.apache.wss4j.common.crypto.Merlin", null);
        properties.put("org.apache.wss4j.crypto.merlin.keystore.type", "jks");
        properties.put("org.apache.wss4j.crypto.merlin.keystore.password", keyPassword);
        Crypto crypto = CryptoFactory.getInstance(properties);

        // Create WSS4J security header
        WSSecHeader secHeader = new WSSecHeader();
        secHeader.insertSecurityHeader();

        // Create WSS4J signature
        WSSecSignature signature = new WSSecSignature();
        signature.setUserInfo(alias, keyPassword);
        signature.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
        signature.setSignatureAlgorithm(WSConstants.RSA_SHA1);
        signature.setSignatureKey(privateKey);
        signature.setUseSingleCertificate(true);
        signature.prepare(soapMessage.getSOAPPart(), crypto, secHeader);

        // Add binary security token
        signature.prependBSTElementToHeader(secHeader);

        // Sign SOAP message
        signature.computeSignature(soapMessage.getSOAPPart(), null, secHeader.getSecurityHeader());

        return soapMessage;
    }
}