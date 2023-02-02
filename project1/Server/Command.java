import java.util.HashMap;
import java.util.Map;
public class Command {

    
    public String go(String command, Map<Integer, Integer> keyVal) {
        String[] commandArr = command.split(" ");
        String response = "";
        
        switch (commandArr[0].toLowerCase()) {
            case "put":
 
                response = this.put(keyVal, 
                Integer.parseInt(commandArr[1]), 
                Integer.parseInt(commandArr[2]));
                break;

            case "get":
   
                response = this.get(keyVal, Integer.parseInt(commandArr[1]));
                break;
            
            case "delete":

                response = this.delete(keyVal, Integer.parseInt(commandArr[1]));
                break;
            
            default:

                response = "INVALID_REQUEST";
                break;
        }
        return response;
    }
    
    private String put(Map<Integer, Integer> keyVal, int key, int val) {
        System.out.print("before put>>>>>");
        System.out.println(keyVal);
        keyVal.put(key, val);
        System.out.print("after put>>>>>");
        System.out.println(keyVal);
        return "OK";
    }

    private String get(Map<Integer, Integer> keyVal, int key) {
        System.out.print("before get>>>>>");
        System.out.println(keyVal);

        if (!keyVal.containsKey(key)) {
            return "NOT FOUND";
        } else {
            int val = keyVal.get(key);
            System.out.print("after gett>>>>>");
            System.out.println(keyVal);
            return "OK";
        }
        

  
    }

    private String delete(Map<Integer, Integer> keyVal, int key) {
        System.out.print("before del>>>>>");
        System.out.println(keyVal);
        keyVal.remove(key);

        System.out.print("after del>>>>>");
        System.out.println(keyVal);
        return "OK";
    }

}
