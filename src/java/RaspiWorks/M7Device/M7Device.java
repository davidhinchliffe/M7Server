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
package raspiworks.M7Device;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * Super class of the Raspberry Pi & MCP23017 Gpio pins. 
 * Acts as the receiver for the Command objects and invoker
 * 
 * Each device must extend this class to ensure and implement the abstract methods that are specific to the device. 
 * These methods are: 
 *      getAddress
 *      assignFireGpio
 *      assignArmGpio
 *      getPinState
 *      getNumberOfChannels
 *      setPinHigh
 *      setPinLow
 *      
 * It also contains a couple of hook methods that provide default behavior and can be overridden if need be:
 *      provisionFiringPin
 *      provisionArmingPin
 *      
 * Both of these are used to get the gpio pins assigned to each channel and passed as a parameter to the command objects 
 * 
 */
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import java.util.List;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
public abstract class M7Device
{
    protected List<Integer> availableChannels;
    public static final GpioController GPIO=GpioFactory.getInstance();
    protected int address;
    
    protected abstract int getAddress();
    protected abstract Pin assignFireGpio(int channel);
    protected abstract Pin assignArmGpio(int channel);
    public abstract String getPinState(Pin pin);

    public abstract boolean setPinHigh(Pin pin);
    public abstract boolean setPinLow(Pin pin);
    protected abstract void validateChannels();
   // public abstract void unProvisionPin(GpioPinDigitalOutput pin);
    
    public int getNumberOfChannels(){
        if(availableChannels.isEmpty())
            validateChannels();
        return availableChannels.size();
      
            
    }
    public List<Integer> getAvailableChannels(){
        if(availableChannels.isEmpty())
            validateChannels();       
        return availableChannels;
    }
    //Hook method that calls the assignFireGpio from the appropriate child class. 
    //passed as a parameter to the Fire command object
    public Pin provisionFiringPin(int channel){
        Pin pin=assignFireGpio(channel);
        return(pin);
    }
    //Hook method that calls the assignArmGpio from the appropriate child class
    //passed as a parameter to the Arm, Disarm, & Status command objects
    public Pin provisionArmingPin(int channel){
        Pin pin=assignArmGpio(channel);
        return(pin);
    }
}
