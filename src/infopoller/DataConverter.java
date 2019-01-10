/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package infopoller;

/**
 *
 * @author haha
 */
public interface DataConverter {
    void setType(String type);
    String doConversion(byte[] data);
    String getType();
    byte getEPC();
}
