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

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import raspiworks.commands.ArmCommand;
import raspiworks.commands.DisarmCommand;
import raspiworks.commands.FireCommand;
import raspiworks.commands.ResetCommand;
import raspiworks.commands.ShutdownCommand;
import raspiworks.commands.StatusCommand;
import raspiworks.invoker.M7ServerController;
import raspiworks.commands.M7ServerCommand;
import raspiworks.M7Device.M7Device;
import raspiworks.M7Device.MCP23017;
import raspiworks.M7Device.RaspberryPi;

/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class M7ServerControllerLoader
{
    private int MAX_CHANNELS;
    private M7ServerController controller;
    public M7ServerControllerLoader(){
    }

        private List<Integer> getExpansionAddress(){
        //since max i2c address space is 7 bit, there are a possible 127 addresses 
        //that can be assigned, 1-127
            
            //ordered list in order by device address
            List<Integer> expansionAddresses=new ArrayList<>();
            try {
                final I2CBus bus=I2CFactory.getInstance(I2CBus.BUS_1);
                //addresses range is 1-127 or 7 bit
                for (int i=1;i<128; i++)
                {
                    try{
                        I2CDevice device=bus.getDevice(i);
                        //if write is successful then device exists
                        device.write((byte)0);
                        //add device to List
                        expansionAddresses.add(i);
                    }
                    //exception is ignored and the next device is tested
                    catch (Exception ignoreException){}
                }
            }
             catch(UnsupportedBusNumberException | IOException e){}
            return expansionAddresses;
        }
        public List<M7Device> initializeDevices(){
            List<M7Device> devices=new ArrayList<>();
            
            //the first one is the Raspberry Pi since it will always be present. It is assigned 0 since the MCP23017 address space is 1-127
            M7Device raspberryPi=new RaspberryPi(0);
            int maxChannels=raspberryPi.getNumberOfChannels();
            devices.add(raspberryPi);
            //get the MCP23017 addresses that are attached to the Raspberry Pi
            for (Integer address:getExpansionAddress())
            {
                M7Device mcp23017 = new MCP23017(address);
                devices.add(mcp23017);
                maxChannels +=mcp23017.getNumberOfChannels();
            }
            MAX_CHANNELS=maxChannels;
            return devices;
        }
  public M7ServerController initializeController()
    {
           M7ServerCommand fireCommand;
           M7ServerCommand armCommand;
           M7ServerCommand disarmCommand;
           M7ServerCommand statusCommand;
           M7ServerCommand resetCommand=new ResetCommand();
           M7ServerCommand shutdownCommand=new ShutdownCommand();
           
           List<M7Device> PiDevices;
           PiDevices=initializeDevices();
           controller=new M7ServerController(MAX_CHANNELS);
           controller.setRaspberryPiCommands(resetCommand,shutdownCommand);
           //controller.resetGpio();
           
           int offset=0;
           //iterate through the device list to assign channels to the commands
           for (M7Device device:PiDevices){
               List<Integer> availableChannels=device.getAvailableChannels();
               for(Integer channel: availableChannels)
               //for(int channel=0;channel<8;++channel)
               {
                   Pin firingPin=device.provisionFiringPin(channel+offset);
                   Pin armingPin=device.provisionArmingPin(channel+offset);
                   fireCommand=new FireCommand(device,firingPin);
                   armCommand=new ArmCommand(device,armingPin);                 
                   disarmCommand=new DisarmCommand(device,armingPin);
                   statusCommand=new StatusCommand(device,armingPin);
                   controller.setGpioCommands(channel+offset, fireCommand,armCommand,disarmCommand,statusCommand);
               }
               //channel offset based on the number of channels available on each device
               offset+=device.getNumberOfChannels();
           }
               
           return controller;
    }    
}
