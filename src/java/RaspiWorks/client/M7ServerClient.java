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
package raspiworks.client;

import raspiworks.loader.M7ServerControllerLoader;
import raspiworks.exceptions.InvalidCommandException;
import raspiworks.invoker.M7ServerController;
import raspiworks.exceptions.InvalidChannelException;
import raspiworks.exceptions.ChannelNotReadyException;


/**
 *
 * @author David Hinchliffe <belgoi@gmail.com>
 
 The M7ServerClient is the object responsible for the main interaction between the UI and the rest of 
 the objects in the command pattern.  It is responsible for calling the constructor of the Loader to ensure the M7ServerController is setup properly. 
 The main responsibility though is to get the commands issued by the UI and call the appropriate commands on the M7ServerController and passing
 any responses back to the UI to be displayed for the user.
 
 The command pattern is setup as follows:
  Client->M7ServerClient
  Loader->M7ServerControllerLoader
  Invoker-> M7ServerController
  Command <<Interface>> M7ServerCommand
  Concrete Commands -> ArmCommand,DisarmCommand, &  FireCommand
  Receiver->RaspberryPiGpioPin, and the GpioController class from Pi4j
 
 to maintain consistency in naming..a channel consists of two pins, a firing pin and an arming pin
 the arming pin must be set before the firing pin can be fired.  
 * 
 * 
 */
public class M7ServerClient
{
    private final int MAX_CHANNELS=8;
    private  M7ServerController controller;
    private final M7ServerControllerLoader loadController;
    public M7ServerClient()
    {
        //TODO: implement the passing of data betweend the html portion and the launcher
        // and implement the commands
        this.loadController = new M7ServerControllerLoader();
        this.controller=loadController.initializeController();
    }
    /**
     * 
     * Execute Command is the main interaction between the UI and the M7ServerClient. 
     * The UI calls execute with valid commands and the channel to execute it on
     * It is overloaded so that an optional value parameter can be issued. This is necessary to
     * handle the case when resetController or shutdown is requested where a channel 
     * isn't required. The other is for the commands where a channel value is required.
     * The valid commands are:
     *      reset->                 Unprovisions the gpio pins so that they can be reused
     *      shutdown->          Properly shuts down the gpio pins and unprovisions them
     *      initialize->             Initialize the controller as needed. All available channels are initialized
     *      fire->                    Issues the fire command but only if the channel has been armed first
     *      arm->                  Arms channel if it has been setup.
     *      disarm->              Disarms channel if it has been setup.
     *      status->              Returns the status of the channel (is channel armed or disarmed)
     *      maxchannels->     Returns the number of maximum number of channels available
     *      test->                  Returns connected successfully if a remote client is connected to the server
     */
     
    public String executeClientCommand(String command) throws InvalidCommandException
    {
        String message="failed";
        switch (command.toLowerCase())
        {
            case "initialize":
                this.controller=loadController.initializeController();
                message=loadController.getNumberOfProvisionedChannels();
                break;
            case "reset":
                controller.resetGpio();
                message="reset successful";
                break;
            case "shutdown":
                controller.shutdownGpio();
                message="shutdown successful";
                break;
            case "maxchannels":
                message=Integer.toString(MAX_CHANNELS);
                break;
            case "test":
                message="connected successfully";
                break;
            default:
                throw new InvalidCommandException();
        }
        
        return message;
    }
    
    //Overloads ExecuteCommand to take the value parameter 
    //required for some commands
    public String executeClientCommand(String command,int value) throws InvalidChannelException,ChannelNotReadyException,InvalidCommandException
    {
        String message="success";
        try{
            switch (command.toLowerCase())
            {
                case "fire":
                    controller.fire(value);
                    break;
                case "arm":
                    controller.arm(value);
                    message=controller.getStatus(value);
                    break;
                case "status":
                    message=controller.getStatus(value);
                    break;
                case "disarm":
                    controller.disarm(value);
                    message=controller.getStatus(value);
                    break;
                default:
                    throw new InvalidCommandException();
            }
            
            return message;
        }
        catch(InvalidChannelException | ChannelNotReadyException|NullPointerException e ){
            //want UI to send error message back to client
            throw e;
        }
    }
}
