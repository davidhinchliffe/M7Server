package raspiworks.M7Device;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.gpio.extension.mcp.MCP23017Pin;
import com.pi4j.gpio.extension.mcp.MCP23008GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP23017GpioProvider;
import com.pi4j.io.gpio.GpioPin;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class MCP23017 extends M7Device
{
    private Map<Pin,GpioPinDigitalOutput> devicePins;
    private final int DEVICE_MAX_CHANNELS=8;
    public MCP23017(int address){
        super.address=address;
        super.availableChannels=new ArrayList<>();
        devicePins=new HashMap<>();
    }
    
    public int getAddress(){
        return super.address;
    }
   
    @Override
    protected Pin assignFireGpio(int channel){
        Pin pin=null;
        if (channel >7 )
             channel= channel % 8;
        switch (channel){
            case 0:
               pin=MCP23017Pin.GPIO_A0;
               break;
            case 1:
                pin=MCP23017Pin.GPIO_A2;
                break;
            case 2:
                pin=MCP23017Pin.GPIO_A4;
                break;
            case 3:
                pin=MCP23017Pin.GPIO_A6;
                break;
            case 4:
                pin=MCP23017Pin.GPIO_B7;
                break;
            case 5:
                pin=MCP23017Pin.GPIO_B5;
                break;
            case 6:
                pin=MCP23017Pin.GPIO_B3;
                break;
            case 7:
                pin=MCP23017Pin.GPIO_B1;
                break;
            default:
                break;
           }
        return pin;
    }
   @Override
    protected Pin assignArmGpio(int channel){
         Pin pin=null;
         if (channel >7 )
             channel=channel % 8;
        switch (channel){
            case 0:
               pin=MCP23017Pin.GPIO_A1;
               break;
            case 1:
                //swap1 & 7
                 pin=MCP23017Pin.GPIO_A3;

                break;
            case 2:
                pin=MCP23017Pin.GPIO_A5;

                break;
            case 3:
                pin=MCP23017Pin.GPIO_A7;

                break;
            case 4:
                pin=MCP23017Pin.GPIO_B6;

                break;
            case 5:
                pin=MCP23017Pin.GPIO_B4;

                break;
            case 6:
                pin=MCP23017Pin.GPIO_B2;

                break;
            case 7:
                pin=MCP23017Pin.GPIO_B0;
                break;
            default:
                break;
           }
        return pin;
    }
    @Override
    protected void validateChannels()
    {
        try{ 
               final MCP23017GpioProvider provider=new MCP23017GpioProvider(I2CBus.BUS_1,super.address);
                for (int channel=0;channel<DEVICE_MAX_CHANNELS;++channel)
                {
                    try{
                        Pin firingPin=assignFireGpio(channel);
                        Pin armingPin=assignArmGpio(channel);
                        GpioPinDigitalOutput provisionedPin=M7Device.GPIO.provisionDigitalOutputPin(provider,firingPin);
                        GpioPinDigitalOutput provisionedPin2=M7Device.GPIO.provisionDigitalOutputPin(provider,armingPin);
                        super.availableChannels.add(channel);
                        unProvisionPin(provisionedPin);
                        unProvisionPin(provisionedPin2);
                    }
                    //catch any exceptions thrown by provisioning pin
                    catch(Exception ignoreException){}      
            }
        }
        catch (UnsupportedBusNumberException | IOException e){}        
    }
    @Override
    public void resetDevice(){
         for (Integer channel: availableChannels)
        {
            Pin armingPin=assignArmGpio(channel);
            if (devicePins.containsKey(armingPin))
                unProvisionPin(devicePins.get(armingPin));
        }       
    }
    private void unProvisionPin(GpioPinDigitalOutput pin){
         List<GpioPin> pins=(List<GpioPin>)M7Device.GPIO.getProvisionedPins();
         for(int i=0;i<pins.size();++i)
        {
            if(pins.get(i).getName().equals(pin.getName()))
            {
                pin.setState(PinState.LOW);
                M7Device.GPIO.unprovisionPin(pins.get(i));
                break;
            }
        }
    }
    //Due to possibly a bug in Pi4J or wiringPi or quite possibly the in the MCP23017 itself, when a pin is set high using setState(PinState.High) then
    //set low with setState(PinState.LOW) other pins in the same bank are sometimes set to low as well.  For instance arm=8 arm=9 fire=8 will
    //fire channel 8 but will also set the arm pin on channel 9 to low, but, arm=8 arm=9 fire=9 will work like it is supposed to.  
    //as a work around, the pin has to be provisioned with an initial state of either high or low.  
    @Override
    public boolean setPinHigh(Pin pin)
    {
          try{ 
              final MCP23017GpioProvider provider=new MCP23017GpioProvider(I2CBus.BUS_1,super.address); 
             // final MCP23017GpioProvider provider=new MCP23017GpioProvider(I2CBus.BUS_1,super.address);
                GpioPinDigitalOutput provisionedPin=M7Device.GPIO.provisionDigitalOutputPin(provider,pin,PinState.HIGH);
               
               if(devicePins.containsKey(pin))
                   devicePins.replace(pin,provisionedPin);
               else
                   devicePins.put(pin, provisionedPin);
               return (M7Device.GPIO.getState(provisionedPin)==PinState.HIGH ? true : false);
            }
            catch (UnsupportedBusNumberException | IOException e){}
            
            return false;       
    }
    //Due to possibly a bug in Pi4J or wiringPi or quite possibly the in the MCP23017 itself, when a pin is set high using setState(PinState.High) then
    //set low with setState(PinState.LOW) other pins in the same bank are sometimes set to low as well.  For instance arm=8 arm=9 fire=8 will
    //fire channel 8 but will also set the arm pin on channel 9 to low, but, arm=8 arm=9 fire=9 will work like it is supposed to.  
    //as a work around, the pin has to be provisioned with an initial state of either high or low.  
    @Override
    public boolean setPinLow(Pin pin)
    {
        List<GpioPin> pins=(List<GpioPin>)M7Device.GPIO.getProvisionedPins();
        for (int i=0;i<pins.size();++i)
       {
           if(pins.get(i).getName().equals(pin.getName()))
           {
               if(devicePins.containsKey(pin))
              { 
                   devicePins.remove(pin);
              }
                M7Device.GPIO.unprovisionPin(pins.get(i));
                try{ 
                    final MCP23017GpioProvider provider=new MCP23017GpioProvider(I2CBus.BUS_1,super.address);
                    GpioPinDigitalOutput provisionedPin=M7Device.GPIO.provisionDigitalOutputPin(provider,pin,PinState.LOW);
                    unProvisionPin(provisionedPin);
                    return true;
                }
                catch (UnsupportedBusNumberException | IOException e){}
           }
        }
        return false;
    }

    @Override
    public String getPinState(Pin pin){
        if (devicePins.containsKey(pin))
            return(devicePins.get(pin).isHigh()?"high" : "low");
        else
            return "low";
        }
}
