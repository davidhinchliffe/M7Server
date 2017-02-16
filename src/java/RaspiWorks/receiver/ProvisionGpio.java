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

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiPin;
import java.util.List;


/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * 
 * This is a helper class to provision the gpio pins upon startup.
 * It also sets the initial state of the pins to low.  The input pin
 * has its pull down resistor enabled to ensure the pin is in a predictable state.  
 * the output pins have an external pull down resistor.  If no resistor is present in the
 * circuit, then the raspberry pi's pull down resistor must be enabled
 */
public class ProvisionGpio
{
    
    private final GpioController gpio;
    public ProvisionGpio(GpioController gpio)
    {
        this.gpio=gpio;
    }
    public GpioPinDigitalOutput ProvisionOutputPin(Pin pin, String name,PinState pinState)
    {
        return (gpio.provisionDigitalOutputPin(pin,name,pinState));
    }
    public GpioPinDigitalInput ProvisionInputPin(Pin pin,String name)
    {
        //provision input pin and turn on pull down resistor to ensure that 
        //the pin is in a fixed low state at startup. 
        return(gpio.provisionDigitalInputPin(pin,name,PinPullResistance.PULL_DOWN));
    }
    public boolean isProvisioned(GpioPin pin)
    {
        List<GpioPin> provisionedPins;
        provisionedPins=(List<GpioPin>)gpio.getProvisionedPins();
        
        return provisionedPins.contains(pin)?true:false;                   
    }
    
    
    
    
}
