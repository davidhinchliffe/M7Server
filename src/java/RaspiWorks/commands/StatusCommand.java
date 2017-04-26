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

import com.pi4j.io.gpio.Pin;
import raspiworks.M7Device.M7Device;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class StatusCommand implements M7ServerCommand
{
    private String status=new String();
    private final M7Device device;
    private final Pin devicePin;
    public StatusCommand(M7Device device, Pin pin){
        //pin=outputPin;
        this.device=device;
        this.devicePin=pin;
    }
    
    @Override
    public void execute(){
       
        if (device.getPinState(devicePin).equals("high"))
            status="Armed";
        else
            status="Disarmed";
    }
    
    @Override
    public String toString(){
        return status;
        
    }
}
