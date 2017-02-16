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

package raspiworks.receiver;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * 
 * This is the receiver object in the command design pattern.  It gives the commands
 * an object to act upon.  In other words, the commands can either set a gpio pin high 
 * or low. or get its state
 */
public class RaspberryPiGpioPin
{
    private GpioPinDigitalOutput gpioPin;
    
    public RaspberryPiGpioPin(GpioPinDigitalOutput provisionedPin)
    {
        gpioPin=provisionedPin;
    }
    public void setHigh()
    {
        //Launcher.gpio.setState(PinState.HIGH,pin);
        gpioPin.setState(PinState.HIGH);
        //this.gpioPin=LauncherClient.gpio.provisionDigitalOutputPin(highPin,PinState.HIGH);
    }
    public void setLow()
    {
        //this.gpioPin=LauncherClient.gpio.provisionDigitalOutputPin(lowPin,PinState.LOW);
        gpioPin.setState(PinState.LOW);
        
    }
    public String getPinState()
    {
        if (gpioPin.isHigh())
            return "high";
        else
            return "low";
    }
    
}
