/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infopoller.dataconverter;

import jaist.echonet.EOJ;

/**
 * Converter class that that handles the conversion of binary ECHONET Lite data
 * into strings, suitable for consumption by logstash.
 * @author Marios Sioutis
 */
public interface DataConverter {

    /**
     * Returns a string representation of the binary data that this converter 
     * handles. 
     * @param data
     * @return
     */
    String doConversion(byte[] data);

    /**
     * Returns the type of information handled by this converter as a String.
     * Examples: "HMDT", "TMPR", others.
     *
     * @return information type as a String.
     */
    String getInfoTypeString();
    byte getEPC();
    EOJ getEOJ();
}
