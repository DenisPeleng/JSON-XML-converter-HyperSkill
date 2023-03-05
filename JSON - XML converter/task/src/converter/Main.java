package converter;

import converter.processing.*;
import converter.processing.JSONProcessing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        String path = "test.txt";
        Scanner scanner = new Scanner(new File(path));
        DataProcessing dataProcessingInput = null;
        DataProcessing dataProcessingOutput = null;
        StringBuilder inputStr = new StringBuilder();
        while (scanner.hasNextLine()) {
            inputStr.append(scanner.nextLine().trim());
        }
        String input = inputStr.toString();
        if (input.startsWith("<")) {
            dataProcessingInput = new XMLProcessing();
            dataProcessingOutput = new JSONProcessing();
        } else if (input.startsWith("{")) {
            dataProcessingInput = new JSONProcessing();
            dataProcessingOutput = new XMLProcessing();
        }else {
            System.out.println("Error input file");
            return;
        }
        List<DataElement> dataElement = dataProcessingInput.parseAllDataElement(input);
        for (DataElement tmpElement : dataElement
        ) {
            tmpElement.printDataElementDescription();
        }
    }
}
