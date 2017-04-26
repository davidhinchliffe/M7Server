/**
 * MIT License
 *
 * Copyright (c) 2016 David Hinchliffe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package raspiworks.invoker;
import java.util.List;
import raspiworks.M7Device.M7Device;
import raspiworks.exceptions.ChannelNotReadyException;
import raspiworks.exceptions.InvalidChannelException;
import raspiworks.commands.M7ServerCommand;
/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * 
 * This class is the invoker in the command pattern.  It is the main class used by the client
 * to invoke the methods of the command objects.  As a result, this class is the only way to 
 * issue a command.  
 */
public class M7ServerController{
    private M7ServerCommand fireCommand[];
    private M7ServerCommand armCommand[];
    private M7ServerCommand disarmCommand[];
    private M7ServerCommand statusCommand[];
    private M7ServerCommand resetCommand;
    private M7ServerCommand shutdownCommand;
    private int maxChannels=0;
    public M7ServerController(int numberOfChannels){
        fireCommand=new M7ServerCommand[numberOfChannels];
        armCommand=new M7ServerCommand[numberOfChannels];
        disarmCommand=new M7ServerCommand[numberOfChannels];
        statusCommand = new M7ServerCommand[numberOfChannels];
        this.maxChannels=numberOfChannels;
    }
    //Commands that act on the Raspberry pi as a whole.  They take the GpioController as their receiver
    public void setRaspberryPiCommands(M7ServerCommand resetCommand,M7ServerCommand shutdownCommand){
        this.resetCommand=resetCommand;
        this.shutdownCommand=shutdownCommand;
    }
   
    //Commands that act on individual pins.  They take Gpiopins as their receiver
    public void setGpioCommands(int channelNumber,M7ServerCommand fireCommand,M7ServerCommand armCommand,M7ServerCommand disarmCommand,M7ServerCommand statusCommand){
        this.fireCommand[channelNumber]=fireCommand;
        this.armCommand[channelNumber]=armCommand;
        this.disarmCommand[channelNumber]=disarmCommand;
        this.statusCommand[channelNumber]=statusCommand;
    }
    public void fire(int channelNumber)throws ChannelNotReadyException,InvalidChannelException{
       try{
            if (getStatus(channelNumber).equals("Armed")){
                fireCommand[channelNumber].execute();
                disarm(channelNumber);
            }else
                 //checked exception so it must be declared 
                throw new ChannelNotReadyException("Error firing channel: Channel " + channelNumber + " isn't armed");
        }
       catch (InvalidChannelException e){
            //checked exception so it must be declared 
            throw new InvalidChannelException("Error firing channel: Invalid channel");
       }

    }
    public void arm(int channelNumber) throws InvalidChannelException{
        try{
            if (getStatus(channelNumber).equals("Disarmed"))
                armCommand[channelNumber].execute();
        }
        catch(InvalidChannelException e){
              //checked exception so it must be declared 
            throw new InvalidChannelException("Error arming channel: Invalid channel");
            }
    }
    public void disarm(int channelNumber) throws InvalidChannelException{
        try{
             if (getStatus(channelNumber).equals("Armed")){
                disarmCommand[channelNumber].execute();
            }
        }catch (InvalidChannelException e){
            //checked exception so it must be declared 
            throw new InvalidChannelException("Error disarming channel: Invalid channel");
        }

    }
   
    public String getStatus(int channelNumber) throws InvalidChannelException{
        if (channelNumber >= 0 && channelNumber < maxChannels){
             statusCommand[channelNumber].execute();
            return statusCommand[channelNumber].toString();
        }
        else 
            //checked exception so it must be declared 
            throw new InvalidChannelException("Channel " + channelNumber + " isn't available");

    }
    public int getMaxChannels(){
        return maxChannels;
    }
    public void resetGpio(){
        resetCommand.execute();
    }
    public void shutdownGpio(){
        shutdownCommand.execute();
    }
    
}
