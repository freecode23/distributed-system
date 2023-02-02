import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;
public class ClientDefault implements Client {

    @Override
    public void startClient() {
        // TODO Auto-generated method stub
        
    }

    public ArrayList<String> parseTextFile() {
        ArrayList<String> arrL = new ArrayList<String>();

        try {
            File file = new File("../file.txt");
            Scanner scan = new Scanner(file);

            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                // String[] command = line.split(" ");
                line.toLowerCase();
                arrL.add(line);
            }
            scan.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + e.getMessage());
        }
        return arrL;
    }

}
    


