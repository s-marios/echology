package infopoller.dataconverter;

import jaist.echonet.EOJ;

/**
 * Converter class that that handles the conversion of binary ECHONET Lite data
 * into strings, suitable for consumption by logstash.
 *
 * Before you create new data converters, make sure that none of the generic
 * ones suit your needs, e.g. {@link DataConverterShort DataConverterShort} etc.
 *
 * Register your converter at the static initialization of the
 * {@link infopoller.Converters Converters} class
 *
 * @author Marios Sioutis
 */
public interface DataConverter {

    /**
     * Returns a string representation of the binary data that this converter
     * handles.
     *
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
