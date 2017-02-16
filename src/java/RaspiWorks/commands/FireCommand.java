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

import raspiworks.receiver.RaspberryPiGpioPin;
/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 * 
 * Concrete class implementation of the command interface, which is part 
 * of the command design pattern. It defines the fire command, which when
 * executed sends out the firing signal to the correct channel
 *
 */
public class FireCommand implements M7ServerCommand
{
    private final int IGNITER_BURN_TIME=500; //in milliseconds
    private RaspberryPiGpioPin channel;
    public FireCommand(RaspberryPiGpioPin provisionedPin){
       channel=provisionedPin;
    }
    @Override
    public void execute(){ 
        //use this one if there is a logic shift between the relay and raspberry pi
        channel.setHigh();
        //only use this one if directly connecting to the relay board
        //channel.setLow();
        try
        {
            //TODO: adjust this timing to get the best burn on the igniter
        Thread.sleep(IGNITER_BURN_TIME);
        channel.setLow();
        }
        catch (Exception e)
                {}
    }
}
