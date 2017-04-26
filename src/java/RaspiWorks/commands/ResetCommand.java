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
package raspiworks.commands;

import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import java.util.ArrayList;
import java.util.List;
import raspiworks.M7Device.M7Device;


/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class ResetCommand implements M7ServerCommand
{
    public ResetCommand(){}
    @Override
    public void execute(){
      
        List<GpioPin> pins=(List<GpioPin>)M7Device.GPIO.getProvisionedPins();
        while(pins.size()>0){
            //set pin state to low
           ((GpioPinDigitalOutput)pins.get(0)).setState(PinState.LOW);
            //index has to be the first element since each element is removed from the list as the pins 
            //are unprovisioned
            M7Device.GPIO.unprovisionPin(pins.get(0)); 
        }
    }
}
