import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

/**
 * This class implements to put, get, and delete method
 * that the TCP server need to do its operation on its hashmap
 * It will use the helper function go() and depending on the command * string it will execute one of the 3 commands
 */
public class Command {
    

    public String[] splitIdString(String idString) {
        String[] parts = idString.split(" ", 2);
        return parts;
    }

    public String go(String clientInput, Map<Integer, Integer> keyVal) {


        // 1. split client input and extract id, command
        String[] idString = splitIdString(clientInput);
        String reqId = idString[0];
        String command = idString[1];

        // 2. split the command
        String[] commandArr = command.split(" ");

        // 3. init response object
        Map<String, Object> responseObj = new HashMap<>();

        switch (commandArr[0].toLowerCase()) {
            case "put":
 
                responseObj = this.put(keyVal, 
                Integer.parseInt(commandArr[1]), 
                Integer.parseInt(commandArr[2]));
                break;

            case "get":
                responseObj = this.get(keyVal, Integer.parseInt(commandArr[1]));
                break;
            
            case "delete":
                
                responseObj = this.delete(keyVal, Integer.parseInt(commandArr[1]));
                break;
            
            default:
                String errorMsg="Invalid command only put, get, delete are allowed";
                responseObj.put("msg", errorMsg);
                
                break;
        }

        responseObj.put("reqId", reqId);

        /**
         * uncomment this to test for unrequested response
         */
        // responseObj.put("reqId", "123");
        
        // convert
        Gson gson = new Gson();
        String responseString = gson.toJson(responseObj);
        return responseString;
        }
    
    private Map<String, Object> put(Map<Integer, Integer> keyVal, int key, int val) {
        
        System.out.print("before put>>>>>");
        System.out.println(keyVal);

        // 1. add to hashmap stored by ServerObj
        keyVal.put(key, val);
        System.out.print("after put>>>>>");
        System.out.println(keyVal);

        // 2. init return obj to send back to clinet
        Map<String, Object> retObj = new HashMap<>();
        retObj.put("msg", "OK");

        return retObj;
    }

    private Map<String, Object> get(Map<Integer, Integer> keyVal, int key) {
        System.out.print("before get>>>>>");
        System.out.println(keyVal);
        
        // 1. init return obj
        Map<String, Object> retObj = new HashMap<>();
        if (!keyVal.containsKey(key)) {

            // - add message
            retObj.put("msg", "KEY NOT FOUND");

        } else {

            // - get value
            String val = String.valueOf(keyVal.get(key));
            
            // - add msg and value to return object
            retObj.put("msg", "OK");
            retObj.put("val", val);
            
            System.out.print("after gett>>>>>");
            System.out.println(keyVal);
        }
        return retObj;
    }

    private Map<String, Object> delete(Map<Integer, Integer> keyVal, int key) {

        System.out.print("before del>>>>>");
        System.out.println(keyVal);
        
        Map<String, Object> retObj = new HashMap<>();

        if (!keyVal.containsKey(key)) {
            // add error mesg
            retObj.put("msg", "KEY NOT FOUND");

        } else {
            
            // - remove
            keyVal.remove(key);
            System.out.print("after del>>>>>");
            System.out.println(keyVal);

            // add error msg
            retObj.put("msg", "OK");
            
        }
        return retObj;
    }

}
