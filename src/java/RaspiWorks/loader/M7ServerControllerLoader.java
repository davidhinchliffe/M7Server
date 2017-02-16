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
package raspiworks.loader;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import raspiworks.commands.ArmCommand;
import raspiworks.commands.DisarmCommand;
import raspiworks.commands.FireCommand;
import raspiworks.commands.ResetCommand;
import raspiworks.commands.ShutdownCommand;
import raspiworks.commands.StatusCommand;
import raspiworks.invoker.M7ServerController;
import raspiworks.receiver.ProvisionGpio;
import raspiworks.receiver.RaspberryPiGpioPin;
import raspiworks.commands.M7ServerCommand;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class M7ServerControllerLoader
{
    //can be final since only one reference to the pins is allowed. 
    private final GpioController gpioController;
    private final int MAX_CHANNELS=8;
    private M7ServerController controller;
    private int provisionedChannels;
    public M7ServerControllerLoader(){
        provisionedChannels=0;
        gpioController=GpioFactory.getInstance();
    }
     //assigns a Gpio pin to a firing channel
    private Pin assignChannelToFireGpio(int channel)
    {
        Pin pin=null;
        switch (channel)
        {
            case 0:
                pin=RaspiPin.GPIO_00;
                break;
            case 1:
                pin=RaspiPin.GPIO_01;
                break;
            case 2:
                pin=RaspiPin.GPIO_02;
                break;
            case 3:
                pin=RaspiPin.GPIO_03;
                break;
            case 4:
                pin=RaspiPin.GPIO_04;
                break;
            case 5:
                pin=RaspiPin.GPIO_05;
                break;
            case 6:
                pin=RaspiPin.GPIO_06;
                break;
            case 7:
                pin=RaspiPin.GPIO_07;
                break;
            default:
                break;
        }
        
        return pin;
    }
    
    //assigns a gpio pin to a arming channel
    private Pin assignChannelToArmGpio(int channel)
    {
        Pin pin=null;
        switch (channel)
        {
            case 0:
                pin=RaspiPin.GPIO_21;
                break;
            case 1:
                pin=RaspiPin.GPIO_22;
                break;
            case 2:
                pin=RaspiPin.GPIO_23;
                break;
            case 3:
                pin=RaspiPin.GPIO_24;
                break;
            case 4:
                pin=RaspiPin.GPIO_25;
                break;
            case 5:
                pin=RaspiPin.GPIO_26;
                break;
            case 6:
                pin=RaspiPin.GPIO_27;
                break;
            case 7:
                pin=RaspiPin.GPIO_28;
                break;
            default:
                break;
        }
        return pin;
    }
        public String getNumberOfProvisionedChannels(){
        return Integer.toString(provisionedChannels);
    }

    //It sets up the controller by pairing up the channels with the commands and 
    //it provisions all of the gpio pins.  It returns the number of pins that were provisioned
    
    public M7ServerController initializeController()
    {

           provisionedChannels=0;
           ProvisionGpio provision=new ProvisionGpio(gpioController);
           M7ServerCommand fireCommand;
           M7ServerCommand armCommand;
           M7ServerCommand disarmCommand;
           M7ServerCommand statusCommand;
           M7ServerCommand resetCommand=new ResetCommand(gpioController);
           M7ServerCommand shutdownCommand=new ShutdownCommand(gpioController);
           
           RaspberryPiGpioPin theGpioPin;
           Pin pin;
           
           controller=new M7ServerController(MAX_CHANNELS);
           controller.setRaspberryPiCommands(resetCommand,shutdownCommand);
           controller.resetGpio();
           
           for(int channel=0;channel<MAX_CHANNELS;++channel)
           {
               //setup the firing pin for the channel 
               pin=assignChannelToFireGpio(channel);
               //this is the line we need to provison to pin low with a transistor                 
                GpioPinDigitalOutput provisionedPin=provision.ProvisionOutputPin(pin,"Channel " + channel,PinState.LOW);
                                  
                provisionedPin.setShutdownOptions(true,PinState.LOW,PinPullResistance.OFF);
                theGpioPin=new RaspberryPiGpioPin(provisionedPin);
                fireCommand=new FireCommand(theGpioPin);
                
                //setup the arming/disarming pin for the channel
                pin=assignChannelToArmGpio(channel);
                provisionedPin=provision.ProvisionOutputPin(pin,"Channel " + channel+"b", PinState.LOW);
                provisionedPin.setShutdownOptions(true,PinState.LOW,PinPullResistance.OFF);
                theGpioPin=new RaspberryPiGpioPin(provisionedPin);        
                armCommand=new ArmCommand(theGpioPin);
                disarmCommand=new DisarmCommand(theGpioPin);
                statusCommand=new StatusCommand(theGpioPin);
                
                ++provisionedChannels;
                //setup controller
                controller.setGpioCommands(channel, fireCommand,armCommand,disarmCommand,statusCommand);        
           }
           return controller;
    }
}
