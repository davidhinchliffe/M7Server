package raspiworks.M7Device;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.GpioPin;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 */
public class RaspberryPi extends M7Device
{
    private Map<Pin,GpioPinDigitalOutput> devicePins;
    private final int DEVICE_MAX_CHANNELS=8;
    public RaspberryPi(int address){
        super.address=address;
        super.availableChannels=new ArrayList<>();
        devicePins=new HashMap<>();
    }
    @Override 
    public int getAddress(){
        return super.address;
    }
    @Override
     protected Pin assignFireGpio(int channel)
    {
        Pin pin=null;
        if (channel >7 )
             channel= channel % 8;
        switch (channel)
        {
            case 0:
                pin=RaspiPin.GPIO_07;
                break;
            case 1:
                pin=RaspiPin.GPIO_01;
                break;
            case 2:
                pin=RaspiPin.GPIO_02;
                break;
            case 3:
                pin=RaspiPin.GPIO_05;
                break;
            case 4:
                pin=RaspiPin.GPIO_21;
                break;
            case 5:
                pin=RaspiPin.GPIO_26;
                break;
            case 6:
                pin=RaspiPin.GPIO_23;
                break;
            case 7:
                pin=RaspiPin.GPIO_25;
                break;
            default:
                break;
        }
        
        return pin;
    }
    
    //assigns a gpio pin to an arming channel
     @Override
     protected Pin assignArmGpio(int channel)
    {
        Pin pin=null;
        if (channel >7 )
            channel= channel % 8;
        switch (channel)
        {
            case 0:
                pin=RaspiPin.GPIO_00;
                break;
            case 1:
                pin=RaspiPin.GPIO_04;
                break;
            case 2:
                pin=RaspiPin.GPIO_03;
                break;
            case 3:
                pin=RaspiPin.GPIO_06;
                break;
            case 4:
                pin=RaspiPin.GPIO_22;
                break;
            case 5:
                pin=RaspiPin.GPIO_27;
                break;
            case 6:
                pin=RaspiPin.GPIO_24;
                break;
            case 7:
                pin=RaspiPin.GPIO_28;
                break;
            default:
                break;
        }
        return pin;
    }
     
     @Override
    public void validateChannels()
    {
        for(int channel=0;channel<DEVICE_MAX_CHANNELS;++channel)
        {
            try{
                Pin firingPin=assignFireGpio(channel);
                Pin armingPin=assignArmGpio(channel);
                GpioPinDigitalOutput provisionedPin=M7Device.GPIO.provisionDigitalOutputPin(firingPin,PinState.LOW);
                GpioPinDigitalOutput provisionedPin2=M7Device.GPIO.provisionDigitalOutputPin(armingPin,PinState.LOW);
                super.availableChannels.add(channel);
                unProvisionPin(provisionedPin);
                unProvisionPin(provisionedPin2);
            }
          catch(Exception ignoreException){}
        }
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
                M7Device.GPIO.unprovisionPin(pins.get(i));
        }
    }
     @Override
    public boolean setPinHigh(Pin pin)
    {
        GpioPinDigitalOutput provisionedPin=M7Device.GPIO.provisionDigitalOutputPin(pin,PinState.HIGH);
        if(devicePins.containsKey(pin))
            devicePins.replace(pin,provisionedPin);
        else
            devicePins.put(pin, provisionedPin);
         return (M7Device.GPIO.getState(provisionedPin)==PinState.HIGH ? true : false);
    }
    @Override
    public boolean setPinLow(Pin pin){
       List<GpioPin> pins=(List<GpioPin>)M7Device.GPIO.getProvisionedPins();
       for (int i=0;i<pins.size();++i)
       {
           if(pins.get(i).getName().equals(pin.getName()))
           {  
               if(devicePins.containsKey(pin))
               {
                   devicePins.get(pin).setState(PinState.LOW);
                   devicePins.remove(pin);
               }
               M7Device.GPIO.unprovisionPin(pins.get(i));
               return true;
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
