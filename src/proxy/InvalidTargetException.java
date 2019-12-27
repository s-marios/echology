/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proxy;

/**
 *
 * @author haha
 */
public class InvalidTargetException extends Exception{

    private final String msg;
    public InvalidTargetException(String msg) {
        this.msg = msg;
    }
    
    @Override
    public String toString(){
        return "Invalid Target Exception: " + msg;
    }
}
